package com.github.behnamkhazael.sdnbasedcep.brokerserverservices;

import com.github.behnamkhazael.sdnbasedcep.entities.Publisher;
import com.github.behnamkhazael.sdnbasedcep.entities.SingletonPublishersTable;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.logging.log4j.Logger;
import trex.common.Attribute;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;

import static com.github.behnamkhazael.sdnbasedcep.config.LoadConfig.*;
import static org.apache.logging.log4j.LogManager.getLogger;
import static trex.common.Consts.ValType.INT;

/**
 * Created by Behnam Khazael on 2/6/2021.
 * This simple server to the publishers register themselves in the Brokers.
 * @author Behnam Khazael
 * @version 0.1
 */
public class PublisherRegisterATopic implements HttpHandler {


    private static final Logger logger = getLogger(PublisherRegisterATopic.class);
    private static final HashSet<Double> set = new HashSet<Double>();

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
        try {
            final Headers headers = httpExchange.getResponseHeaders();
            final String requestMethod = httpExchange.getRequestMethod().toUpperCase();
            switch (requestMethod) {
                case METHOD_POST:
                    long time = System.currentTimeMillis();
                    JsonObject obj = new JsonObject();
                    final Request request = getRequestParameters(httpExchange.getRequestBody());
                    Publisher p = new Publisher();
                    ArrayList<Attribute> attributes = new ArrayList<Attribute>();
                    Attribute attr = new Attribute();
                    if (set.contains(Double.parseDouble(request.getData().getId()))) {
                        final String responseBody = "{\"message\":\"Publisher Already Registered\"}";
                        headers.set(HEADER_CONTENT_TYPE, String.format("application/json; charset=%s", CHARSET));
                        final byte[] rawResponseBody = responseBody.getBytes(CHARSET);
                        httpExchange.sendResponseHeaders(STATUS_OK, rawResponseBody.length);
                        httpExchange.getResponseBody().write(rawResponseBody);
                        httpExchange.close();
                        break;
                    }
                    set.add(Double.parseDouble(request.getData().getId()));
                    //TODO: Generalize events and data quality. At the moment it just support T-Rex...
                    switch (request.getData().getTopic()) {
                        case "smoke":
                            p.setTopic(request.getData().getTopic());
                            p.setAddress(request.getData().getId());
                            p.setQoS(request.getData().getQoS());
                            p.setEventType(10);
//                        attr.setName("accuracy");
//                        attr.setValType(INT);
//                        attr.setIntVal(Integer.parseInt(request.getData().getQoS()));
                            attributes.add(attr);
                            p.setAttributes(attributes);
                            break;
                        case "temp":
                            p.setTopic(request.getData().getTopic());
                            p.setAddress(request.getData().getId());
                            p.setQoS(request.getData().getQoS());
                            p.setEventType(11);
                            attr.setName("accuracy");
                            attr.setValType(INT);
                            attr.setIntVal(Integer.parseInt(request.getData().getQoS()));
                            attributes.add(attr);
                            p.setAttributes(attributes);
                            break;
                        default:
                            p.setTopic(request.getData().getTopic());
                            p.setAddress(request.getData().getId());
                            p.setQoS(request.getData().getQoS());
                            attr.setName("accuracy");
                            attr.setValType(INT);
                            attr.setIntVal(Integer.parseInt(request.getData().getQoS()));
                            attributes.add(attr);
                            p.setAttributes(attributes);
                            break;
                    }

                    SingletonPublishersTable.addPublisher(p);

                    obj.addProperty("Solution", getSolution());
                    obj.addProperty("NumberOfNodes", getNumberOfMotes());
                    obj.addProperty("Run", getRun());
                    obj.addProperty("Action", "PUBLISHER_REGISTRATION");
                    obj.addProperty("PublisherAddress", p.getAddress());
                    obj.addProperty("PublisherRegisterTime", time);
                    obj.addProperty("PublisherRegisterMessageTime", request.getData().getTime());
                    obj.addProperty("PublisherRegistrationDelay", time - Long.parseLong(request.getData().getTime()));

                    logger.info(obj);
                    final String responseBody = "{\"message\":\"Publisher Successfully Registered\"}";
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
        }catch (Exception e){
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


    private class Request {
        private String type;
        private Data data;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

        private class Data {
            private String topic;
            private String QoS;
            private String id;
            private String time;

            public String getTopic() {
                return topic;
            }

            public void setTopic(String topic) {
                this.topic = topic;
            }

            public String getQoS() {
                return QoS;
            }

            public void setQoS(String qoS) {
                QoS = qoS;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }
        }


    }
}
