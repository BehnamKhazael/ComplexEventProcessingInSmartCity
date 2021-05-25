package com.github.sdnwiselab.sdnwise.function;

import com.github.sdnwiselab.sdnwise.entities.ProcessingNode;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static com.github.sdnwiselab.sdnwise.configuration.LoadConfig.*;
import static org.apache.logging.log4j.LogManager.getLogger;


/**
 * Created by Behnam Khazael on 2/28/2021.
 * A simple server to accept rules along with list of publishers from the broker.
 * it will send it to the headselector
 * @author Behnam Khazael
 * @version 0.1
 */
public class PublisherHeadSelection implements HttpHandler {

    private static final org.apache.logging.log4j.Logger logger = getLogger(PublisherHeadSelection.class);


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
                try {
                    final Request request = getRequestParameters(httpExchange.getRequestBody());

                    long headSelectionStartTime = System.currentTimeMillis();
                    JsonArray jsonElements = new Gson().toJsonTree(request.getPublishersList()).getAsJsonArray();
                    ProcessingNode head = HeadSelector.returnHead(jsonElements, request.getRule(), request.getCallBackURL());
                    long headSelectionEndTime = System.currentTimeMillis();
                    obj.addProperty("Solution", getSolution());
                    obj.addProperty("NumberOfNodes", getNumberOfMotes());
                    obj.addProperty("Run", getRun());
                    obj.addProperty("Action", "HEAD_SELECTION");
                    obj.addProperty("headSelectionStartTime", headSelectionStartTime);
                    obj.addProperty("headSelectionEndTime", headSelectionEndTime);
                    obj.addProperty("headSelectionProcessTime", headSelectionEndTime - headSelectionStartTime);
                    logger.info(obj);
                    if (head != null) {
                        final String responseBody = "{\"head\":\"" + head.getAddress().toString() + "\"}";
                        headers.set(HEADER_CONTENT_TYPE, String.format("application/json; charset=%s", CHARSET));
                        final byte[] rawResponseBody = responseBody.getBytes(CHARSET);
                        httpExchange.sendResponseHeaders(STATUS_OK, rawResponseBody.length);
                        httpExchange.getResponseBody().write(rawResponseBody);
                        httpExchange.close();
                        break;
                    } else {
                        final String responseBody = "{\"message\":\"" + "Failed to select a head" + "\"}";
                        headers.set(HEADER_CONTENT_TYPE, String.format("application/json; charset=%s", CHARSET));
                        final byte[] rawResponseBody = responseBody.getBytes(CHARSET);
                        httpExchange.sendResponseHeaders(STATUS_NOK, rawResponseBody.length);
                        httpExchange.getResponseBody().write(rawResponseBody);
                        httpExchange.close();
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
        private String requestId;
        private String uuid;
        private String callBackURL;
        private String rule;
        private JsonObject[] publishersList;

        public String getRule() {
            return rule;
        }

        public void setRule(String rule) {
            this.rule = rule;
        }

        public JsonObject[] getPublishersList() {
            return publishersList;
        }

        public void setPublishersList(String PublishersList) {
            this.publishersList = publishersList;
        }

        public String getRequestId() {
            return requestId;
        }

        public void setRequestId(String requestId) {
            this.requestId = requestId;
        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getCallBackURL() {
            return callBackURL;
        }

        public void setCallBackURL(String callBackURL) {
            this.callBackURL = callBackURL;
        }

    }
}
