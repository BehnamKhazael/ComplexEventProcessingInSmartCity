package com.github.behnamkhazael.sdnbasedcep.brokerserverservices;

import com.github.behnamkhazael.sdnbasedcep.utilities.CEPRulePreProcessing;
import com.github.behnamkhazael.sdnbasedcep.utilities.CEPRuleProvisioning;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.logging.log4j.Logger;
import trex.examples.RuleR1;
import trex.packets.RulePkt;
import trex.ruleparser.TRexRuleParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static com.github.behnamkhazael.sdnbasedcep.config.LoadConfig.*;
import static org.apache.logging.log4j.LogManager.getLogger;


/**
 * Created by Behnam Khazael on 2/6/2021.
 * This simple server to check if a rule can be satisfied in the network.
 * @author Behnam Khazael
 * @version 0.1
 */
public class RuleChecker implements HttpHandler {

    private static final Logger logger = getLogger(RuleChecker.class);


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
        final Headers headers = httpExchange.getResponseHeaders();
        final String requestMethod = httpExchange.getRequestMethod().toUpperCase();
        switch (requestMethod) {
            case METHOD_POST:
                JsonObject obj = new JsonObject();
                final Request request = getRequestParameters(httpExchange.getRequestBody());
                RulePkt rule = TRexRuleParser.parse(request.getRule(), 1);
                String uuid = request.getUuid();
                String callBackURL = request.getCallBackURL();
                String topic = request.getTopic();
                Boolean checkRule;
                Boolean checkProvisioning;
                long preProcessingStartTime = System.currentTimeMillis();
                rule = new RuleR1().buildRule();
                checkRule = CEPRulePreProcessing.checkRule(rule, uuid, topic, callBackURL);
                long preProcessingEndTime = System.currentTimeMillis();
                long provisioningStartTime = System.currentTimeMillis();
                checkProvisioning = CEPRuleProvisioning.provisionRequest(rule, uuid, topic, callBackURL);
                long provisioningEndTime = System.currentTimeMillis();
                obj.addProperty("Solution", getSolution());
                obj.addProperty("NumberOfNodes", getNumberOfMotes());
                obj.addProperty("Run", getRun());
                obj.addProperty("Action", "RULE_PRE_PROCESSING");
                obj.addProperty("PreProcessStartTime", preProcessingStartTime);
                obj.addProperty("PreProcessEndTime", preProcessingEndTime);
                obj.addProperty("ProvisioningStartTime", provisioningStartTime);
                obj.addProperty("ProvisioningEndTime", provisioningEndTime);

                logger.info(obj);
                if (checkRule & checkProvisioning) {
                    final String responseBody = "{\"message\":\"Rule Successfully Registered\"}";
                    headers.set(HEADER_CONTENT_TYPE, String.format("application/json; charset=%s", CHARSET));
                    final byte[] rawResponseBody = responseBody.getBytes(CHARSET);
                    httpExchange.sendResponseHeaders(STATUS_OK, rawResponseBody.length);
                    httpExchange.getResponseBody().write(rawResponseBody);
                    httpExchange.close();
                    break;
                } else {
                    final String responseBody = "{\"message\":\"Rule can not satisfy in this network\"}";
                    headers.set(HEADER_CONTENT_TYPE, String.format("application/json; charset=%s", CHARSET));
                    final byte[] rawResponseBody = responseBody.getBytes(CHARSET);
                    httpExchange.sendResponseHeaders(STATUS_NOK, rawResponseBody.length);
                    httpExchange.getResponseBody().write(rawResponseBody);
                    httpExchange.close();
                    break;
                }
            case METHOD_OPTIONS:
                headers.set(HEADER_ALLOW, ALLOWED_METHODS);
                httpExchange.sendResponseHeaders(STATUS_OK, NO_RESPONSE_LENGTH);
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
        private String uuid;
        private String callBackURL;
        private String topic;
        private String rule;

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getRule() {
            return rule;
        }

        public void setRule(String rule) {
            this.rule = rule;
        }

        public String getCallBackURL() {
            return callBackURL;
        }

        public void setCallBackURL(String callBackURL) {
            this.callBackURL = callBackURL;
        }

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }
    }
}
