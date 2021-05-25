package com.github.sdnwiselab.sdnwise.cooja;

import com.github.sdnwiselab.sdnwise.flowtable.FlowTableEntry;
import com.github.sdnwiselab.sdnwise.flowtable.ForwardUnicastAction;
import com.github.sdnwiselab.sdnwise.flowtable.Window;
import com.github.sdnwiselab.sdnwise.packet.DataPacket;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.apache.logging.log4j.Logger;
import org.contikios.cooja.MoteType;
import org.contikios.cooja.Simulation;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;

import static com.github.sdnwiselab.sdnwise.cooja.LoadConfig.*;
import static com.github.sdnwiselab.sdnwise.flowtable.FlowTableInterface.SDN_WISE_CONST;
import static com.github.sdnwiselab.sdnwise.flowtable.FlowTableInterface.SDN_WISE_PACKET;
import static com.github.sdnwiselab.sdnwise.flowtable.Window.SDN_WISE_EQUAL;
import static com.github.sdnwiselab.sdnwise.flowtable.Window.SDN_WISE_SIZE_2;
import static com.github.sdnwiselab.sdnwise.packet.NetworkPacket.SDN_WISE_DST_H;
import static org.apache.logging.log4j.LogManager.getLogger;

/**
 * Created by Behnam Khazael on 2/21/2021.
 * Represents a simulated sensor.
 * @author Behnam Khazael
 * @version 0.1
 */
public class SmokeSensorType1 extends Mote implements SensorInterface {
    private static final Logger logger = getLogger(SmokeSensorType1.class);
    private static final String HEADER_ALLOW = "Allow";
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final int STATUS_OK = 200;
    private static final int STATUS_NOK = 201;
    private static final int STATUS_METHOD_NOT_ALLOWED = 405;
    private static final int NO_RESPONSE_LENGTH = -1;
    private static final String METHOD_POST = "POST";
    private static final String METHOD_OPTIONS = "OPTIONS";
    private static final String ALLOWED_METHODS = METHOD_POST + "," + METHOD_OPTIONS;
    private final Object lock = new Object();
    private volatile boolean isOn = false;
    private int sampleRate = Integer.parseInt(getSmokeSensorType1SampleRate());
    private int sampleError = 2;
    private int id;
    private String eventArea;
    private int registrationStartTime = Integer.parseInt(LoadConfig.getRegistrationStartTime());
    private int registrationStartTimeRandomDelay = Integer.parseInt(LoadConfig.getRegistrationStartTimeRandomDelay());
    private String HOSTNAME = "0.0.0.0";
    private int PORT = 6000; //read from config
    private int BACKLOG = 0;

    public SmokeSensorType1() {
        super();
        startDispatchingMessages();
        try {
            startSimulationListeners();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SmokeSensorType1(MoteType moteType, Simulation simulation) {
        super(moteType, simulation);
        startDispatchingMessages();
        try {
            startSimulationListeners();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Request getRequestParameters(InputStream inputStream) {
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
        return new Gson().fromJson(sb.toString(), Request.class);
    }

    private void startDispatchingMessages() {
        try {
            Thread th = new Thread(new PublishMessage());
            Thread register = new Thread(new RegisterMessage());
            th.start();
            register.start();
        } catch (Exception ex) {
            log(ex.getLocalizedMessage() + "\n" + addr + "\n");
        }

    }

    private void startSimulationListeners() throws IOException {

        Thread listener = new Thread(new Listener());
        listener.start();

        Timer timer = new Timer();


        timer.scheduleAtFixedRate(new TimerTask() {
            int logNumber = 0;

            public void run() {
                if (logNumber == 15)
                    System.exit(0);
                JsonObject obj = new JsonObject();
                obj.addProperty("Solution", getSolution());
                obj.addProperty("NumberOfNodes", getNumberOfMotes());
                obj.addProperty("Run", getRun());
                obj.addProperty("Action", "PUBLISHER_STATUS");
                obj.addProperty("addr", addr.toString());
                obj.addProperty("id", id);
                obj.addProperty("battery", battery.getBatteryLevel());
                obj.addProperty("battery_Percentage", battery.getBatteryPercent() / 2.55);
                obj.addProperty("ConsumedEnergyOverRX", ((Battery) battery).getConsumedEnergyOverRX());
                obj.addProperty("ConsumedEnergyOverTX", ((Battery) battery).getConsumedEnergyOverTX());
                obj.addProperty("flow_table_free_pos", flow_table_free_pos);
                obj.addProperty("sentBytes", sentBytes);
                obj.addProperty("receivedBytes", receivedBytes);
                obj.addProperty("sentDataBytes", sentDataBytes);
                obj.addProperty("receivedDataBytes", receivedDataBytes);
                obj.addProperty("sentDataPackets", sentDataPackets);
                obj.addProperty("numberOfSentDataPackets", numberOfSentDataPackets);
                obj.addProperty("numberOfReceivedDataPackets", numberOfReceivedDataPackets);
                obj.addProperty("receivedDataPackets", receivedDataPackets);
                obj.addProperty("logNumber", logNumber++);
                logger.info(obj);
            }
        }, 60 * 1000, 60 * 1000);
    }

    @Override
    public boolean simulator_turnOn() {
        synchronized (lock) {

            isOn = true;
            lock.notify();
            return true;
        }
    }

    @Override
    public boolean simulator_turnOff() {
        synchronized (lock) {

            isOn = false;
            lock.notify();
            return true;
        }

    }

    @Override
    public void simulator_sendLocation() {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("node", getID());
        jsonObject.addProperty("sensor", getClass().getName());
        jsonObject.addProperty("x_coordinate", getInterfaces().getPosition().getXCoordinate());
        jsonObject.addProperty("y_coordinate", getInterfaces().getPosition().getYCoordinate());
        jsonObject.addProperty("url", HOSTNAME);
        jsonObject.addProperty("port", PORT);


        try {
            URL url = new URL("http://" + URLEncoder.encode(getEventSimulatorUrl(), StandardCharsets.UTF_8.toString())
                    + ":"
                    + URLEncoder.encode(getEventSimulatorPort(), StandardCharsets.UTF_8.toString())
                    + "/"
                    + URLEncoder.encode(getEventSimulatorResource(), StandardCharsets.UTF_8.toString())
            );

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.setDoOutput(true);


            DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());

            wr.write(jsonObject.toString().getBytes());

            int responseCode = httpURLConnection.getResponseCode();

            if (responseCode == 200) {
                logger.debug("{\"status\":\"ok\"}");
            } else {
                logger.debug("{\"status\":\"nok\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Long time = System.currentTimeMillis();
        JsonObject obj = new JsonObject();
        final Headers headers = httpExchange.getResponseHeaders();
        final String requestMethod = httpExchange.getRequestMethod().toUpperCase();
        obj.addProperty("Solution", getSolution());
        obj.addProperty("NumberOfNodes", getNumberOfMotes());
        obj.addProperty("Run", getRun());
        obj.addProperty("Action", "CHANGE_THE_STATUS_OF_SENSOR");
        obj.addProperty("PublisherAddress", id);
        obj.addProperty("PublisherRegisterTime", time);
        obj.addProperty("Status", isOn);

        switch (requestMethod) {
            case METHOD_POST:
                boolean check = false;
                final Request request = getRequestParameters(httpExchange.getRequestBody());
                eventArea = request.getArea();
                System.out.println("--------->" + eventArea);
                log("--------->" + eventArea);
                switch (request.getCommand()) {
                    case "ON":
                        check = simulator_turnOn();
                        obj.addProperty("Status", isOn);
                        logger.info(obj);
                        break;
                    case "OFF":
                        check = simulator_turnOff();
                        obj.addProperty("Status", isOn);
                        logger.info(obj);
                        break;
                    default:
                        check = false;
                        break;
                }
                logger.info(obj);
                if (check) {
                    final String responseBody = "{\"message\":\"Status Successfully change to" + isOn + "\"}";
                    headers.set(HEADER_CONTENT_TYPE, String.format("application/json; charset=%s", CHARSET));
                    final byte[] rawResponseBody = responseBody.getBytes(CHARSET);
                    httpExchange.sendResponseHeaders(STATUS_OK, rawResponseBody.length);
                    httpExchange.getResponseBody().write(rawResponseBody);
                    httpExchange.close();
                    break;
                } else {
                    final String responseBody = " {\"message\":\"can not change the status\"}";
                    headers.set(HEADER_CONTENT_TYPE, String.format("application/json; charset=%s", CHARSET));
                    final byte[] rawResponseBody = responseBody.getBytes(CHARSET);
                    httpExchange.sendResponseHeaders(STATUS_NOK, rawResponseBody.length);
                    httpExchange.getResponseBody().write(rawResponseBody);
                    httpExchange.close();
                    break;
                }
            case METHOD_OPTIONS:
                headers.set(HEADER_ALLOW, ALLOWED_METHODS);
                httpExchange.sendResponseHeaders(STATUS_NOK, NO_RESPONSE_LENGTH);
                httpExchange.getResponseBody().write("{\"status\":\"nok\"}".getBytes());
                httpExchange.close();
                break;
            default:
                headers.set(HEADER_ALLOW, ALLOWED_METHODS);
                httpExchange.sendResponseHeaders(STATUS_METHOD_NOT_ALLOWED, NO_RESPONSE_LENGTH);
                httpExchange.getResponseBody().write("{\"status\":\"nok\"}".getBytes());
                httpExchange.close();
                break;
        }
    }

    private class PublishMessage implements Runnable {
        @Override
        public void run() {
            // We wait for the network to start
            while (battery.getBatteryLevel() > 0) {
                synchronized (lock) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                while (isOn) {
                    Random rand = new Random();
                    int rn = rand.nextInt(1000 - 0 + 1) + 0;
                    try {
                        Thread.sleep(rn);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    Random r = new Random();
                    int low = 80;
                    int high = 100;
                    int result = r.nextInt(high - low) + low;
                    Random e = new Random();
                    int eLow = -sampleError;
                    int eHigh = sampleError;
                    int eResult = e.nextInt(eHigh - eLow) + eLow;
                    long eventTime = System.currentTimeMillis();
                    //log("Try to send pub, smoke type 1 = " + subscribersTable.size());
                    //int size = subscribersTable.get("smoke").size();
                    //String sSize = String.valueOf(size);
//                    if (!subscribersTable.isEmpty()) {
//                        log("Size: " + sSize);
//                        try {
//                            for (NodeAddress n :
//                                    subscribersTable.get("smoke")) {
                    try {
                        if (getSolution().compareTo("SDN") == 0) {
                            log("In SDN Solution");
                            if (sampleError > 5) {
                                log("SDN Error Continue: " + sampleError);
                                try {
                                    Thread.sleep(sampleRate - rn);
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                                continue;
                            }
                        }
                        System.out.println("SENDING PUB: " + sampleError);
                        //System.out.println(n.toString());
                        log("SENDING PUB: " + sampleError);
                            DataPacket dataPacket = new DataPacket(net_id, addr, getActualSinkAddress());
                            String uid = String.valueOf(id);
                            //uid = uid.concat(String.valueOf(eventTime));
                            JsonObject message = new JsonObject();
                            JsonObject messageInner = new JsonObject();
                            message.addProperty("type", "pub");
                            messageInner.addProperty("topic", "smoke");
                            messageInner.addProperty("value", (result + eResult));
                            messageInner.addProperty("time", eventTime);
                            messageInner.addProperty("uid", uid);
                            messageInner.addProperty("QoS", sampleError);
                            messageInner.addProperty("Area", eventArea);
                            message.add("data", messageInner);

                            dataPacket.setPayload((message.toString()).getBytes(Charset.forName("UTF-8")));


                            JsonObject obj = new JsonObject();
                            obj.addProperty("Solution", getSolution());
                            obj.addProperty("NumberOfNodes", getNumberOfMotes());
                            obj.addProperty("Run", getRun());
                            obj.addProperty("Action", "PUBLISHER_MESSAGE");
                            obj.addProperty("add", id);
                            obj.add("message", message);

                            log(message.toString());
                            logger.info(obj);
//                                try {
//                                    dataPacket.setNxhop(getNextHopVsSink());
//                                }catch (Exception ex){
//                                    ex.printStackTrace();
//                                    reInitial();
//                                }
                            dataPacket.setNxhop(getNextHopVsSink());
                            numberOfSentDataPackets++;
                            radioTX(dataPacket);



                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
//                    }
                    try {
                        Thread.sleep(sampleRate - rn);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    private void reInitial() {
        FlowTableEntry toSink = new FlowTableEntry();
        toSink.addWindow(new Window()
                .setOperator(SDN_WISE_EQUAL)
                .setSize(SDN_WISE_SIZE_2)
                .setLhsLocation(SDN_WISE_PACKET)
                .setLhs(SDN_WISE_DST_H)
                .setRhsLocation(SDN_WISE_CONST)
                .setRhs(this.addr.intValue()));
        toSink.addWindow(Window.fromString("P.TYPE > 127"));
        toSink.addAction(new ForwardUnicastAction()
                .setNextHop(addr));
        toSink.getStats().setPermanent();
        flowTable.add(0, toSink);
    }

    private class RegisterMessage implements Runnable {
        @Override
        public void run() {
            while (!registered) {
                Random rand = new Random();
                int n = rand.nextInt(registrationStartTimeRandomDelay);
                // We wait for the network to start
                try {
                    Thread.sleep(registrationStartTime + n);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                long eventTime = System.currentTimeMillis();
                DataPacket dataPacket = new DataPacket(net_id, addr, getActualSinkAddress());

                JsonObject message = new JsonObject();
                JsonObject messageInner = new JsonObject();
                message.addProperty("type", "reg");
                messageInner.addProperty("topic", "smoke");
                messageInner.addProperty("id", addr.toString());
                messageInner.addProperty("QoS", sampleError);
                messageInner.addProperty("time", System.currentTimeMillis());
                message.add("data", messageInner);

                dataPacket.setPayload((message.toString()).getBytes(Charset.forName("UTF-8")));

                JsonObject obj = new JsonObject();
                obj.addProperty("Solution", getSolution());
                obj.addProperty("NumberOfNodes", getNumberOfMotes());
                obj.addProperty("Run", getRun());
                obj.addProperty("Action", "PUBLISHER_SEND_REGISTRATION");
                obj.addProperty("PublisherRegisterTime", eventTime);
                obj.addProperty("id", id);
                obj.add("message", message);
                logger.info(obj);
                dataPacket.setNxhop(getNextHopVsSink());
                numberOfSentDataPackets++;
                radioTX(dataPacket);
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }

    }


    private class Listener implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(Long.parseLong(getTheStartTimeToListenToTheSimulator()));
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            PORT = PORT + getID();
            id = getID();

            try {
                final HttpServer server = HttpServer.create(new InetSocketAddress(HOSTNAME, PORT), BACKLOG);

                server.createContext("/sensor/change-status", httpExchange -> {
                    Long time = System.currentTimeMillis();
                    JsonObject obj = new JsonObject();
                    final Headers headers = httpExchange.getResponseHeaders();
                    final String requestMethod = httpExchange.getRequestMethod().toUpperCase();
                    obj.addProperty("Solution", getSolution());
                    obj.addProperty("NumberOfNodes", getNumberOfMotes());
                    obj.addProperty("Run", getRun());
                    obj.addProperty("Action", "CHANGE_THE_STATUS_OF_SENSOR");
                    obj.addProperty("PublisherAddress", id);
                    obj.addProperty("PublisherRegisterTime", time);
                    obj.addProperty("Status", isOn);
                    switch (requestMethod) {
                        case METHOD_POST:
                            boolean check = false;
                            final Request request = getRequestParameters(httpExchange.getRequestBody());
                            eventArea = request.getArea();
                            switch (request.getCommand()) {
                                case "ON":
                                    check = simulator_turnOn();
                                    obj.addProperty("Status", isOn);
                                    logger.info(obj);
                                    break;
                                case "OFF":
                                    check = simulator_turnOff();
                                    obj.addProperty("Status", isOn);
                                    logger.info(obj);
                                    break;
                                default:
                                    check = false;
                                    break;
                            }
                            logger.info(obj);
                            if (check) {
                                final String responseBody = "{\"message\":\"Status Successfully change to" + isOn + "\"}";
                                headers.set(HEADER_CONTENT_TYPE, String.format("application/json; charset=%s", CHARSET));
                                final byte[] rawResponseBody = responseBody.getBytes(CHARSET);
                                httpExchange.sendResponseHeaders(STATUS_OK, rawResponseBody.length);
                                httpExchange.getResponseBody().write(rawResponseBody);
                                httpExchange.close();
                                break;
                            } else {
                                final String responseBody = " {\"message\":\"can not change the status\"}";
                                headers.set(HEADER_CONTENT_TYPE, String.format("application/json; charset=%s", CHARSET));
                                final byte[] rawResponseBody = responseBody.getBytes(CHARSET);
                                httpExchange.sendResponseHeaders(STATUS_NOK, rawResponseBody.length);
                                httpExchange.getResponseBody().write(rawResponseBody);
                                httpExchange.close();
                                break;
                            }
                        case METHOD_OPTIONS:
                            headers.set(HEADER_ALLOW, ALLOWED_METHODS);
                            httpExchange.sendResponseHeaders(STATUS_NOK, NO_RESPONSE_LENGTH);
                            httpExchange.getResponseBody().write("{\"status\":\"nok\"}".getBytes());
                            httpExchange.close();
                            break;
                        default:
                            headers.set(HEADER_ALLOW, ALLOWED_METHODS);
                            httpExchange.sendResponseHeaders(STATUS_METHOD_NOT_ALLOWED, NO_RESPONSE_LENGTH);
                            httpExchange.getResponseBody().write("{\"status\":\"nok\"}".getBytes());
                            httpExchange.close();
                            break;
                    }
                });

                server.setExecutor(Executors.newCachedThreadPool());
                server.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            simulator_sendLocation();
        }
    }

    private class Request {
        private String command;
        private String area;

        public String getCommand() {
            return command;
        }

        public void setCommand(String command) {
            this.command = command;
        }

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }
    }
}
