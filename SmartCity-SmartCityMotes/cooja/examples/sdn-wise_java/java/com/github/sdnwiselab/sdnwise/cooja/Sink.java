/*
 * Copyright (C) 2015 SDN-WISE
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.sdnwiselab.sdnwise.cooja;

import com.github.sdnwiselab.sdnwise.flowtable.FlowTableEntry;
import com.github.sdnwiselab.sdnwise.flowtable.ForwardUnicastAction;
import com.github.sdnwiselab.sdnwise.flowtable.Window;
import com.github.sdnwiselab.sdnwise.packet.ConfigPacket;
import com.github.sdnwiselab.sdnwise.packet.DataPacket;
import com.github.sdnwiselab.sdnwise.packet.NetworkPacket;
import com.github.sdnwiselab.sdnwise.packet.RegProxyPacket;
import com.github.sdnwiselab.sdnwise.util.NodeAddress;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.util.GeometricShapeFactory;
import org.apache.logging.log4j.Logger;
import org.contikios.cooja.MoteType;
import org.contikios.cooja.Simulation;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

import static com.github.sdnwiselab.sdnwise.cooja.LoadConfig.*;
import static com.github.sdnwiselab.sdnwise.flowtable.Window.*;
import static com.github.sdnwiselab.sdnwise.packet.NetworkPacket.SDN_WISE_DST_H;
import static org.apache.logging.log4j.LogManager.getLogger;

/**
 * Represents a sink node which is the bridge between controllers layer and edge nodes.
 * @author Sebastiano Milardo
 * @author Behnam Khazael
 * @version 0.2
 */
public class Sink extends AbstractMote {

    private static final Logger logger = getLogger(Sink.class);

    private final String addrController;
    private final int portController;
    private Socket tcpSocket;
    private DataOutputStream inviaOBJ;
    private DataInputStream riceviOBJ;
    private ArrayList<ProcessingNode> processingNodes = new ArrayList<ProcessingNode>();


    public Sink() {
        super();
        addrController = "localhost";
        portController = 9991;
    }

    public Sink(MoteType moteType, Simulation simulation) {
        super(moteType, simulation);
        addrController = "localhost";
        portController = 9991;
    }

    @Override
    public final void initSdnWise() {
        super.initSdnWise();
        commonConstructor();
        setDistanceFromSink(0);
        setRssiSink(255);
        this.setSemaphore(1);
    }

    private String[] getControllerIpPort() {
        String s = (String) JOptionPane.showInputDialog(null,
                "Please insert the IP address and TCP port of the controller:",
                "SDN-WISE Sink",
                JOptionPane.QUESTION_MESSAGE, null, null, "localhost:9991");

        String[] tmp = s.split(":");

        if (tmp.length != 2) {
            return getControllerIpPort();
        } else {
            return tmp;
        }
    }

    @Override
    public final void controllerTX(NetworkPacket pck) {
        try {
            if (tcpSocket != null) {
                inviaOBJ = new DataOutputStream(tcpSocket.getOutputStream());
                inviaOBJ.write(pck.toByteArray());
                log("C-TX " + pck);
            }
        } catch (IOException ex) {
            log(ex.getLocalizedMessage());
        }
    }

    @Override
    public void SDN_WISE_Callback(DataPacket packet) {
        controllerTX(packet);
        try {
            Gson gson = new Gson();
            DataMessage dataMessage = gson.fromJson(new String(packet.getPayload(), Charset.forName("UTF-8")), DataMessage.class);

            logger.info(dataMessage.toString());
            switch (dataMessage.getType()) {
                case "reg":
                    log("In Sink register case");
                    try {
                        URL url = new URL("http://" + URLEncoder.encode(getBrokerUrl(), StandardCharsets.UTF_8.toString())
                                + ":"
                                + URLEncoder.encode(getBrokerPort(), StandardCharsets.UTF_8.toString())
                                + "/"
                                + URLEncoder.encode(getBrokerPublisherRegisterResource(), StandardCharsets.UTF_8.toString())
                        );

                        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setRequestProperty("Content-Type", "application/json");
                        httpURLConnection.setRequestProperty("Accept", "application/json");
                        httpURLConnection.setDoOutput(true);


                        DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());

                        wr.write(packet.getPayload());

                        int responseCode = httpURLConnection.getResponseCode();

                        if (responseCode == 200) {
                            logger.debug("{\"status\":\"ok\"}");
                        } else {
                            logger.debug("{\"status\":\"nok\"}");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    DataPacket dataPacket = new DataPacket(net_id, addr, packet.getSrc());
                    JsonObject message = new JsonObject();
                    JsonObject messageInner = new JsonObject();
                    message.addProperty("type", "response");
                    messageInner.addProperty("message", "ok");
                    message.add("data", messageInner);

                    dataPacket.setPayload((message.toString()).getBytes(Charset.forName("UTF-8")));
                    runFlowMatch(dataPacket);
                    radioTX(dataPacket);
                    logger.info(dataPacket.toString());

                    break;
                case "pub":
                    log("In Sink publication case");
                    ProcessingNode processingNode = getProcessingNode(dataMessage.getData().get("uid").toString().replaceAll("\"",""));
                    System.out.println("URL: " + processingNode.getUrl() + ", Port: " + processingNode.getPort() + ", Resource: " + processingNode.getResource());
                    try {
                        URL url = new URL("http://" + URLEncoder.encode(processingNode.getUrl(), StandardCharsets.UTF_8.toString())
                                + ":"
                                + URLEncoder.encode(processingNode.getPort(), StandardCharsets.UTF_8.toString())
                                + "/"
                                + URLEncoder.encode(processingNode.getResource().replaceFirst("/",""), StandardCharsets.UTF_8.toString())
                        );

                        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setRequestProperty("Content-Type", "application/json");
                        httpURLConnection.setRequestProperty("Accept", "application/json");
                        httpURLConnection.setDoOutput(true);


                        DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());

                        wr.write(packet.getPayload());

                        int responseCode = httpURLConnection.getResponseCode();

                        if (responseCode == 200) {
                            logger.debug("{\"status\":\"ok\"}");
                        } else {
                            logger.debug("{\"status\":\"nok\"}");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "subResponse":
                    log("In Sink subscription ack case");
                    try {
                        URL url = new URL("http://" + URLEncoder.encode(getControllerUrl(), StandardCharsets.UTF_8.toString())
                                + ":"
                                + URLEncoder.encode(getControllerPort(), StandardCharsets.UTF_8.toString())
                                + "/"
                                + URLEncoder.encode(getControllerSubscriptionAck(), StandardCharsets.UTF_8.toString())
                        );

                        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setRequestProperty("Content-Type", "application/json");
                        httpURLConnection.setRequestProperty("Accept", "application/json");
                        httpURLConnection.setDoOutput(true);


                        DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());

                        wr.write(packet.getPayload());

                        int responseCode = httpURLConnection.getResponseCode();

                        if (responseCode == 200) {
                            logger.debug("{\"status\":\"ok\"}");
                        } else {
                            logger.debug("{\"status\":\"nok\"}");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void rxConfig(ConfigPacket packet) {
        NodeAddress dest = packet.getDst();
        NodeAddress src = packet.getSrc();

        if (!dest.equals(addr)) {
            runFlowMatch(packet);
        } else {
            if (!src.equals(addr)) {
                controllerTX(packet);
            } else {
                if (marshalPacket(packet) != 0) {
                    controllerTX(packet);
                }
            }
        }
    }

    @Override
    public NodeAddress getActualSinkAddress() {
        return addr;
    }

    private void commonConstructor() {
        this.battery = new SinkBattery();

        FlowTableEntry toSink = new FlowTableEntry();
        toSink.addWindow(new Window()
                .setOperator(SDN_WISE_EQUAL)
                .setSize(SDN_WISE_SIZE_2)
                .setLhsLocation(SDN_WISE_PACKET)
                .setLhs(SDN_WISE_DST_H)
                .setRhsLocation(SDN_WISE_CONST)
                .setRhs(this.addr.intValue()));
        toSink.addWindow(Window.fromString("P.TYPE > 127"));
        toSink.addAction(new ForwardUnicastAction().setNextHop(addr));
        toSink.getStats().setPermanent();
        flowTable.set(0, toSink);
        startListening();

        InetSocketAddress iAddr;
        iAddr = new InetSocketAddress(addrController, port);
        //RegProxyPacket rpp = new RegProxyPacket(1, addr, "mah", "00:00:00:00:00:00", 1, iAddr);
        RegProxyPacket rpp = new RegProxyPacket(net_id, addr, "mah", "00:00:00:00:00:00", 1, iAddr);
        controllerTX(rpp);

        Runnable RegisterMessage = () -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("add", this.addr.toString());
            jsonObject.addProperty("x", this.getInterfaces().getPosition().getXCoordinate());
            jsonObject.addProperty("y", this.getInterfaces().getPosition().getYCoordinate());

            logger.info(jsonObject);

            try {
                URL url = new URL("http://" + URLEncoder.encode(getControllerUrl(), StandardCharsets.UTF_8.toString())
                        + ":"
                        + URLEncoder.encode(getControllerPort(), StandardCharsets.UTF_8.toString())
                        + "/"
                        + URLEncoder.encode(getControllerUpdateProcessingNodeResource(), StandardCharsets.UTF_8.toString())
                );

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setRequestProperty("Accept", "application/json");
                httpURLConnection.setDoOutput(true);


                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());

                wr.write(jsonObject.toString().getBytes());

                int responseCode = httpURLConnection.getResponseCode();
                InputStream inputStream = httpURLConnection.getInputStream();
                if (responseCode == 200) {
                    //logger.info(httpURLConnection.getResponseMessage());
                    StringBuilder sb = new StringBuilder();
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                    try {
                        String read;
                        while ((read = br.readLine()) != null) {
                            sb.append(read);
                        }
                        br.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Response response = new Gson().fromJson(sb.toString(), Response.class);

                    JsonArray jsonElements = new Gson().toJsonTree(response.getProcessingNodesList()).getAsJsonArray();

                    Gson gson = new Gson();
                    ProcessingNode[] processingNodes = gson.fromJson(jsonElements, ProcessingNode[].class);
                    this.processingNodes = new ArrayList<ProcessingNode>(Arrays.asList(processingNodes));
                    logger.debug("{\"message\":\"ok\"}");
                    return;
                } else {
                    logger.debug("{\"message\":\"nok\"}");
                    return;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        };

        new Thread(RegisterMessage).start();


    }


    private void startListening() {
        try {
            tcpSocket = new Socket(addrController, portController);
            Thread th = new Thread(new TcpListener());
            th.start();
        } catch (IOException ex) {
            log(ex.getLocalizedMessage() + "\n" + addrController + "\n" + portController);
        }
    }

    private class TcpListener implements Runnable {
        @Override
        public void run() {
            try {
                riceviOBJ = new DataInputStream(tcpSocket.getInputStream());
                while (true) {
                    int len = riceviOBJ.read();
                    if (len > 0) {
                        byte[] packet = new byte[len];
                        packet[0] = (byte) len;
                        riceviOBJ.read(packet, 1, len - 1);
                        NetworkPacket np = new NetworkPacket(packet);
                        //Check if its on my net id
                        int npNetId = np.getNetId();
                        if (npNetId == net_id) {
                            log("C-RX " + np);
                            flowTableQueue.put(np);
                        }
                    }
                }
            } catch (IOException | InterruptedException ex) {
                java.util.logging.Logger.getLogger(Sink.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }


    private class DataMessage {
        private String type;
        private JsonObject data;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public JsonObject getData() {
            return data;
        }

        public void setData(JsonObject data) {
            this.data = data;
        }

        @Override
        public String toString() {
            JsonObject object = new JsonObject();
            object.addProperty("type", type);
            object.add("data", data);
            return object.toString();
        }
    }

    private class Response{
        private JsonObject[] processingNodesList;

        public JsonObject[] getProcessingNodesList() {
            return processingNodesList;
        }

        public void setProcessingNodesList(JsonObject[] processingNodesList) {
            this.processingNodesList = processingNodesList;
        }
    }

    public class ProcessingNode {
        private NodeAddress address;
        private String url;
        private String port;
        private String x;
        private String y;
        private String width;
        private String height;
        private String resource;

        public ProcessingNode() {
        }

        public NodeAddress getAddress() {
            return address;
        }

        public void setAddress(NodeAddress address) {
            this.address = address;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getPort() {
            return port;
        }

        public void setPort(String port) {
            this.port = port;
        }

        public String getX() {
            return x;
        }

        public void setX(String x) {
            this.x = x;
        }

        public String getY() {
            return y;
        }

        public void setY(String y) {
            this.y = y;
        }

        public String getWidth() {
            return width;
        }

        public void setWidth(String width) {
            this.width = width;
        }

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }

        @Override
        public String toString() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("address", address.toString());
            jsonObject.addProperty("url", url);
            jsonObject.addProperty("port", port);
            jsonObject.addProperty("x", x);
            jsonObject.addProperty("y", y);
            jsonObject.addProperty("width", width);
            jsonObject.addProperty("height", height);
            return jsonObject.toString();
        }

        public String getResource() {
            return resource;
        }

        public void setResource(String resource) {
            this.resource = resource;
        }
    }

    private ProcessingNode getProcessingNode(String id) {
        for (ProcessingNode p :
                processingNodes) {
            GeometricShapeFactory shapeFactory = new GeometricShapeFactory();
            shapeFactory.setNumPoints(4);
            shapeFactory.setCentre(new Coordinate(Double.parseDouble(p.getX()), Double.parseDouble(p.getY())));
            shapeFactory.setWidth(Double.parseDouble(p.getWidth()));
            shapeFactory.setHeight(Double.parseDouble(p.getHeight()));
            Polygon processingNodeCoverageArea = shapeFactory.createRectangle();
            GeometryFactory geometryFactory = new GeometryFactory();
            Point point = geometryFactory.createPoint(new Coordinate(simulation.getMote(Integer.parseInt(id)).getInterfaces().getPosition().getXCoordinate(), simulation.getMote(Integer.parseInt(id)).getInterfaces().getPosition().getYCoordinate()));
            if (processingNodeCoverageArea.covers(point) | processingNodeCoverageArea.intersects(point)) {
                return p;
            }
        }
        return null;
    }
}
