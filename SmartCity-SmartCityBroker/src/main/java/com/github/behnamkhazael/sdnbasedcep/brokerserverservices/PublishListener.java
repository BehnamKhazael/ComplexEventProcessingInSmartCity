package com.github.behnamkhazael.sdnbasedcep.brokerserverservices;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static com.github.behnamkhazael.sdnbasedcep.config.LoadConfig.*;
import static org.apache.logging.log4j.LogManager.getLogger;

/**
 * Created by Behnam Khazael on 2/22/2021.
 * @author Behnam Khazael
 * @version 0.1
 */
public class PublishListener implements HttpHandler {

    private static final Logger logger = getLogger(PublishListener.class);


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
                final Request request = getRequestParameters(httpExchange.getRequestBody());
                long receivedTime = System.currentTimeMillis();

                JsonObject obj = new JsonObject();
                obj.addProperty("Solution", getSolution());
                obj.addProperty("NumberOfNodes", getNumberOfMotes());
                obj.addProperty("Run", getRun());
                obj.addProperty("Action", "COMPLEX_EVENT_PUBLISHED");
                obj.addProperty("EventTemplate", request.data);
                obj.addProperty("ReceivedTime", receivedTime);
                logger.info(obj);

                final String responseBody = "{\"message\":\"Rule Successfully Registered\"}";
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
        private String type;
        private String data;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getData() {
            return data;
        }

        public void setRule(String data) {
            this.data = data;
        }

    }
}
