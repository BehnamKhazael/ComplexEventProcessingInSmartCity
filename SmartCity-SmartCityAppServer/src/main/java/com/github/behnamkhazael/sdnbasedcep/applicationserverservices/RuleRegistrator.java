package com.github.behnamkhazael.sdnbasedcep.applicationserverservices;

import com.github.behnamkhazael.sdnbasedcep.entities.Broker;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Hashtable;
import java.util.List;

import static com.github.behnamkhazael.sdnbasedcep.config.LoadConfig.getNumberOfMotes;
import static com.github.behnamkhazael.sdnbasedcep.config.LoadConfig.getRun;
import static com.github.behnamkhazael.sdnbasedcep.config.LoadConfig.getSolution;
import static org.apache.logging.log4j.LogManager.getLogger;

/**
 * Created by Behnam Khazael on 2/5/2021.
 * A HTTP simple server to capture Apps requests.
 * @author Behnam Khazael
 * @version 0.1
 */
public class RuleRegistrator implements HttpHandler {

    private static final Logger logger = getLogger(RuleRegistrator.class);


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

    private static final Hashtable<String, Broker> brokerHashTable = new Hashtable<String, Broker>();

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        Long time = System.currentTimeMillis();
        JsonObject obj = new JsonObject();
        loadBrokers();
        final Headers headers = httpExchange.getResponseHeaders();
        final String requestMethod = httpExchange.getRequestMethod().toUpperCase();

        obj.addProperty("Solution", getSolution());
        obj.addProperty("NumberOfNodes", getNumberOfMotes());
        obj.addProperty("Run", getRun());
        obj.addProperty("Time", time);
        obj.addProperty("Action", "Add_Rule_FROM_APP");
        logger.info(obj);
        switch (requestMethod) {
            case METHOD_POST:
                try {
                    final Request request = getRequestParameters(httpExchange.getRequestBody());
                    String rule = request.getRule();
                    String uuid = request.getUuid();
                    String callBackURL = request.getCallBackURL();
                    String topic = request.getTopic();
                    logger.info(request.toString());

                Boolean check;
                check = checkRule(rule, uuid, topic, callBackURL);

                if (check) {
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
                }catch (Exception e){
                    e.printStackTrace();
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

    private static void loadBrokers() {
        Gson gson = new Gson();
        try {
            Reader reader = Files.newBufferedReader(Paths.get("Brokers.json"));
            List<Broker> list = gson.fromJson(reader, new TypeToken<List<Broker>>() {
            }.getType());
            list.forEach(broker -> brokerHashTable.put(broker.getName(), broker));
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

    private static boolean checkRule(String rule, String uuid, String topic, String callBackURL) {
        for (Broker broker :
                brokerHashTable.values()) {
            if (broker.checkRule(rule, uuid, topic, callBackURL)) {
                return true;
            }
        }
        return false;
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

        @Override
        public String toString(){
            JsonObject object = new JsonObject();
            object.addProperty("uuid", uuid);
            object.addProperty("callBackURL", callBackURL);
            object.addProperty("topic", topic);
            object.addProperty("rule", rule);
            return object.toString();
        }
    }
}
