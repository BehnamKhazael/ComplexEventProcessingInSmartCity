package com.github.behnamkhazael.sdnbasedcep.loader;

import com.github.behnamkhazael.sdnbasedcep.ProcessingNodeServices.*;
import com.github.behnamkhazael.sdnbasedcep.config.RuleR1;
import com.github.behnamkhazael.sdnbasedcep.entities.PNode;
import com.github.sdnwiselab.sdnwise.util.NodeAddress;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpServer;
import org.apache.logging.log4j.Logger;
import trexengine.ResultListener;
import trexengine.TRexEngine;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

import static com.github.behnamkhazael.sdnbasedcep.config.LoadConfig.*;
import static org.apache.logging.log4j.LogManager.getLogger;

/**
 * Created by Behnam Khazael on 2/22/2021.
 * Loads and starts processing nodes.
 * @author Behnam Khazael
 * @version 0.1
 */
public class ProcessingNode {
    private static final Logger logger = getLogger(ProcessingNode.class);
    //private final TRexEngine engine = new TRexEngine(1);


    private String HOSTNAME;
    private int PORT; //read from config
    private int BACKLOG;
    private NodeAddress directConnectedSinkAdd;
    private String sinkXCoordinate;
    private String sinkYCoordinate;
    private String x;
    private String y;
    private String width;
    private String height;
    private String resource;


    public ProcessingNode(PNode pNode) {
        HttpServer server = null;
        HOSTNAME = pNode.getHOSTNAME();
        PORT = pNode.getPORT();
        BACKLOG = pNode.getBACKLOG();
        directConnectedSinkAdd = new NodeAddress(pNode.getDirectConnectedSinkAdd());
        sinkXCoordinate = pNode.getSinkXCoordinate();
        sinkYCoordinate = pNode.getSinkYCoordinate();
        x = pNode.getX();
        y = pNode.getY();
        width = pNode.getWidth();
        height = pNode.getHeight();


        TRexEngine engine = new TRexEngine(12);

        //Creating a Rule to submitted to the engine this could potentially happen via the network by marshalling.
        RuleR1 testRule = new RuleR1();

        //Adding the rule to the engine.
        engine.processRulePkt(testRule.buildRule());
        //Create a thread to handle incoming events.
        engine.finalizing();

        //Adding result listener to inform us about the detected complex event (Subscribing)
        ResultListener listener = new ResultListener("http://0.0.0.0:7998/event", String.valueOf(PORT));
        //Addd the listener to the engine.
        engine.addResultListener(listener);

        setResource("/processingnode/publish-call-back");

        try {
            server = HttpServer.create(new InetSocketAddress(HOSTNAME, PORT), BACKLOG);
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.createContext("/processingnode/provisionrule", new ProvisionRule(PORT));
        server.createContext("/processingnode/remove-rule", new RuleRemover());
        server.createContext("/processingnode/publish-call-back", new ReceiveAMessage(PORT, engine));
        server.createContext("/processingnode/subscribe", new SubscribingToATopic());
        server.createContext("/processingnode/un-subscribe", new UnSubscribingFromATopic());
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();

        Runnable RegisterMessage = () -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("url", this.getHOSTNAME());
            jsonObject.addProperty("port", this.getPORT());
            jsonObject.addProperty("x", this.getX());
            jsonObject.addProperty("y", this.getY());
            jsonObject.addProperty("width", this.getWidth());
            jsonObject.addProperty("height", this.getHeight());
            jsonObject.addProperty("resource", this.getResource());

            try {
                URL url = new URL("http://" + URLEncoder.encode(getControllerUrl(), StandardCharsets.UTF_8.toString())
                        + ":"
                        + URLEncoder.encode(getControllerPort(), StandardCharsets.UTF_8.toString())
                        + "/"
                        + URLEncoder.encode(getControllerAddProcessingNodeResource(), StandardCharsets.UTF_8.toString())
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
                    logger.debug("{\"message\":\"ok\"}");
                } else {
                    logger.debug("{\"message\":\"nok\"}");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        new Thread(RegisterMessage).start();
    }


    public String getHOSTNAME() {
        return HOSTNAME;
    }

    public int getPORT() {
        return PORT;
    }

    public int getBACKLOG() {
        return BACKLOG;
    }

    public NodeAddress getDirectConnectedSinkAdd() {
        return directConnectedSinkAdd;
    }

    public void setDirectConnectedSinkAdd(NodeAddress directConnectedSinkAdd) {
        this.directConnectedSinkAdd = directConnectedSinkAdd;
    }

    public String getSinkXCoordinate() {
        return sinkXCoordinate;
    }

    public void setSinkXCoordinate(String sinkXCoordinate) {
        this.sinkXCoordinate = sinkXCoordinate;
    }

    public String getSinkYCoordinate() {
        return sinkYCoordinate;
    }

    public void setSinkYCoordinate(String sinkYCoordinate) {
        this.sinkYCoordinate = sinkYCoordinate;
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

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }
}
