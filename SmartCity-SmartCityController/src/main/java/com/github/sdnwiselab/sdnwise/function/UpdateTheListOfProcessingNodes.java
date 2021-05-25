package com.github.sdnwiselab.sdnwise.function;

import com.github.sdnwiselab.sdnwise.entities.ProcessingNode;
import com.github.sdnwiselab.sdnwise.util.NodeAddress;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.util.GeometricShapeFactory;

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
 * update the list of processing nodes based on their location.
 * @author Behnam Khazael
 * @version 0.1
 */
public class UpdateTheListOfProcessingNodes implements HttpHandler {
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
                boolean check = false;
                try {
                    final Request sink = getRequestParameters(httpExchange.getRequestBody());
                    logger.info(sink);
                    JsonArray jsonElements = new Gson().toJsonTree(NODE_ADDRESS_PROCESSING_NODE_HASHTABLE).getAsJsonArray();
                    compareLocation(sink);

                    JsonObject responseBody = new JsonObject();
                    responseBody.add("processingNodesList", jsonElements);
                    headers.set(HEADER_CONTENT_TYPE, String.format("application/json; charset=%s", CHARSET));
                        final byte[] rawResponseBody = responseBody.toString().getBytes(CHARSET);
                        httpExchange.sendResponseHeaders(STATUS_OK, rawResponseBody.length);
                        httpExchange.getResponseBody().write(rawResponseBody);
                        httpExchange.close();
                        break;
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
        private String add;
        private String x;
        private String y;


        public String getAddr() {
            return add;
        }

        public void setAddr(String PublishersList) {
            this.add = add;
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

        @Override
        public String toString() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("add", this.add);
            jsonObject.addProperty("x", this.x);
            jsonObject.addProperty("y", this.y);
            return jsonObject.toString();
        }
    }

    private static ProcessingNode compareLocations(Request sink) {

        for (ProcessingNode p :
                NODE_ADDRESS_PROCESSING_NODE_HASHTABLE) {
            if (p.getAddress().compareTo(new NodeAddress("0.99")) != 0)
                continue;
            GeometricShapeFactory shapeFactory = new GeometricShapeFactory();
            shapeFactory.setNumPoints(4);
            shapeFactory.setCentre(new Coordinate(Double.parseDouble(p.getX()), Double.parseDouble(p.getY())));
            shapeFactory.setWidth(Double.parseDouble(p.getWidth()));
            shapeFactory.setHeight(Double.parseDouble(p.getHeight()));
            Polygon processingNodeCoverageArea = shapeFactory.createRectangle();
            GeometryFactory geometryFactory = new GeometryFactory();
            Point point = geometryFactory.createPoint(new Coordinate(Double.parseDouble(sink.getX()), Double.parseDouble(sink.getY())));
            if (processingNodeCoverageArea.covers(point) | processingNodeCoverageArea.intersects(point)) {
                p.setAddress(new NodeAddress(sink.add));
                return p;
            }

        }
        return null;
    }

    private static void compareLocation(Request sink) {

        for (ProcessingNode p :
                NODE_ADDRESS_PROCESSING_NODE_HASHTABLE) {
            if (p.getAddress().compareTo(new NodeAddress("0.99")) != 0)
                continue;
                p.setAddress(new NodeAddress(sink.add));
        }
    }
}
