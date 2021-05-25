package com.github.sdnwiselab.sdnwise.function;

import com.github.sdnwiselab.sdnwise.entities.ProcessingNode;
import com.github.sdnwiselab.sdnwise.util.NodeAddress;
import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static com.github.sdnwiselab.sdnwise.loader.SdnWise.NODE_ADDRESS_PROCESSING_NODE_HASHTABLE;
import static org.apache.logging.log4j.LogManager.getLogger;

/**
 * Created by Behnam Khazael on 2/28/2021.
 * A simple server to get processing node information.
 * Whenever a processing node starts it sends its information to the controller.
 * @author Behnam Khazael
 * @version 0.1
 */
public class AddToTheListOfProcessingNodes implements HttpHandler {

    private static final org.apache.logging.log4j.Logger logger = getLogger(AddToTheListOfProcessingNodes.class);


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
                boolean check;
                final Request request = getRequestParameters(httpExchange.getRequestBody());
                ProcessingNode processingNode = new ProcessingNode();
                processingNode.setUrl(request.getUrl());
                processingNode.setPort(request.getPort());
                processingNode.setX(request.getX());
                processingNode.setY(request.getY());
                processingNode.setWidth(request.getWidth());
                processingNode.setHeight(request.getHeight());
                processingNode.setAddress(new NodeAddress("0.99"));
                processingNode.setResource(request.getResource());
                check = NODE_ADDRESS_PROCESSING_NODE_HASHTABLE.add(processingNode);
                System.out.println(processingNode.toString());
                if (check) {
                    final String responseBody = "{\"message\":\"" + processingNode.getPort() + " successfully added.\"}";
                    headers.set(HEADER_CONTENT_TYPE, String.format("application/json; charset=%s", CHARSET));
                    final byte[] rawResponseBody = responseBody.getBytes(CHARSET);
                    httpExchange.sendResponseHeaders(STATUS_OK, rawResponseBody.length);
                    httpExchange.getResponseBody().write(rawResponseBody);
                    httpExchange.close();
                    break;
                } else {
                    final String responseBody = "{\"message\":\"" + "Failed to add processing node" + "\"}";
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

        private String url;
        private String port;
        private String x;
        private String y;
        private String width;
        private String height;
        private String resource;

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

        public String getResource() {
            return resource;
        }

        public void setResource(String resource) {
            this.resource = resource;
        }
    }
}
