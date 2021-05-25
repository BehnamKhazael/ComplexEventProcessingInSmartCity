import com.google.gson.Gson;
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
import java.util.Hashtable;

import static org.apache.logging.log4j.LogManager.getLogger;

/**
 * Created by Behnam Khazael on 2/22/2021.
 * A listener to receive a Mote data from Cooja simulator motes.
 * @author Behnam Khazael
 * @version 0.1
 */
public class MoteRegistrator implements HttpHandler {

    private static final Logger logger = getLogger(MoteRegistrator.class);

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

    public static final Hashtable<String, Mote> motes = new Hashtable<String, Mote>();


    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        final Headers headers = httpExchange.getResponseHeaders();
        final String requestMethod = httpExchange.getRequestMethod().toUpperCase();

        switch (requestMethod) {
            case METHOD_POST:
                final Request request = getRequestParameters(httpExchange.getRequestBody());

                Gson gson = new Gson();
                Mote mote = gson.fromJson(request.toString(), Mote.class);
                motes.put(request.getNode(), mote);
                final String responseBody = "{\"message\":\"Sensor Successfully Registered\"}";
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
        private String node;
        private String sensor;
        private String x_coordinate;
        private String y_coordinate;
        private String url;
        private String port;

        public String getNode() {
            return node;
        }

        public void setNode(String node) {
            this.node = node;
        }

        public String getSensor() {
            return sensor;
        }

        public void setSensor(String sensor) {
            this.sensor = sensor;
        }

        public String getX_coordinate() {
            return x_coordinate;
        }

        public void setX_coordinate(String x_coordinate) {
            this.x_coordinate = x_coordinate;
        }

        public String getY_coordinate() {
            return y_coordinate;
        }

        public void setY_coordinate(String y_coordinate) {
            this.y_coordinate = y_coordinate;
        }

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

        @Override
        public String toString() {
            return "{\"node\":\"" + this.getNode() + "\", \"sensor\":\"" + this.getSensor() + "\", \"x_coordinate\":" + this.getX_coordinate() + ", \"y_coordinate\":" + this.getY_coordinate() + " , \"url\":\"" + this.getUrl() + "\", \"port\":\"" + this.getPort() + "\"}";
        }
    }
}
