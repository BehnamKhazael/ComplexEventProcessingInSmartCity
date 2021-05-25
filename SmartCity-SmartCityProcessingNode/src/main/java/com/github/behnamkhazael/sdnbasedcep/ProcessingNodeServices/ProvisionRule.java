package com.github.behnamkhazael.sdnbasedcep.ProcessingNodeServices;

import com.github.behnamkhazael.sdnbasedcep.entities.CEPEngine;
import com.github.behnamkhazael.sdnbasedcep.entities.SingletonEngineTable;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.logging.log4j.Logger;
import trex.examples.RuleR1;
import trex.packets.RulePkt;
import trexengine.ResultListener;
import trexengine.TRexEngine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static com.github.behnamkhazael.sdnbasedcep.config.LoadConfig.*;
import static org.apache.logging.log4j.LogManager.getLogger;

/**
 * Created by Behnam Khazael on 2/13/2021.
 *A simple server that captures rules.
 * @author Behnam Khazael
 * @version 0.1
 */
public class ProvisionRule implements HttpHandler {

    private static final Logger logger = getLogger(ProvisionRule.class);
    private final int p;
    public ProvisionRule(int port){
        System.out.println("Processing Node: " + port);
        p = port;
    }

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


    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Long time = System.currentTimeMillis();
        JsonObject obj = new JsonObject();
        final Headers headers = httpExchange.getResponseHeaders();
        final String requestMethod = httpExchange.getRequestMethod().toUpperCase();
        switch (requestMethod) {
            case METHOD_POST:
                long preProcessingStartTime = System.currentTimeMillis();
                final Request request = getRequestParameters(httpExchange.getRequestBody());
                //TODO: Generalize rules.
                //RulePkt rule = TRexRuleParser.parse(request.getRule(), 1);

                //TRexEngine engine1 = new TRexEngine(1);

                //Creating a Rule to submitted to the engine this could potentially happen via the network by marshalling.
//                RuleR1 testRule = new RuleR1();
//
//                //Adding the rule to the engine.
//                engine1.processRulePkt(testRule.buildRule());
//                //Create a thread to handle incoming events.
//                engine1.finalizing();
//
//                //Adding result listener to inform us about the detected complex event (Subscribing)
//                ResultListener listener1 = new ResultListener(request.callBackUrl);
//                //Addd the listener to the engine.
//                engine1.addResultListener(listener1);


                //For testing purposes.
                RulePkt rule = new RuleR1().buildRule();
                TRexEngine engine = new TRexEngine(1);
                engine.processRulePkt(new RuleR1().buildRule());
                engine.finalizing();
                ResultListener listener = new ResultListener(request.callBackUrl);
                engine.addResultListener(listener);
                long preProcessingEndTime = System.currentTimeMillis();
                CEPEngine cepEngine = new CEPEngine();
                cepEngine.setEngine(engine);
                cepEngine.setTopic(rule.getCompositeEventTemplate().getEventType());


                SingletonEngineTable.addEngine(rule.getCompositeEventTemplate().getEventType(), cepEngine);


                obj.addProperty("Solution", getSolution());
                obj.addProperty("NumberOfNodes", getNumberOfMotes());
                obj.addProperty("Run", getRun());
                obj.addProperty("Time", time);
                obj.addProperty("Action", "PROVISION_RULE_ON_PROCESSING_NODE");
                obj.addProperty("PreProcessStartTime", preProcessingStartTime);
                obj.addProperty("PreProcessStartTime", preProcessingEndTime);
                logger.info(obj);

                final String responseBody = "{\"message\":\"Rule Successfully Provisioned\"}";
                headers.set(HEADER_CONTENT_TYPE, String.format("application/json; charset=%s", CHARSET));
                final byte[] rawResponseBody = responseBody.getBytes(CHARSET);
                httpExchange.sendResponseHeaders(STATUS_OK, rawResponseBody.length);
                httpExchange.getResponseBody().write(rawResponseBody);
                httpExchange.close();
                break;
            case METHOD_OPTIONS:
                headers.set(HEADER_ALLOW, ALLOWED_METHODS);
                httpExchange.sendResponseHeaders(STATUS_NOK, NO_RESPONSE_LENGTH);
                httpExchange.getResponseBody().write("{\"message\":\"nok\"}".getBytes());
                httpExchange.close();
                break;
            default:
                headers.set(HEADER_ALLOW, ALLOWED_METHODS);
                httpExchange.sendResponseHeaders(STATUS_METHOD_NOT_ALLOWED, NO_RESPONSE_LENGTH);
                httpExchange.getResponseBody().write("{\"message\":\"nok\"}".getBytes());
                httpExchange.close();
                break;
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


    private class Request {
        private String callBackUrl;
        private String rule;

        public String getCallBackUrl() {
            return callBackUrl;
        }

        public void setCallBackUrl(String callBackUrl) {
            this.callBackUrl = callBackUrl;
        }

        public String getRule() {
            return rule;
        }

        public void setRule(String rule) {
            this.rule = rule;
        }
    }
}
