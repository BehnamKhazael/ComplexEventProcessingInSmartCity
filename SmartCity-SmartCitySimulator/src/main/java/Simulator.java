import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpServer;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Executors;

import static org.apache.logging.log4j.LogManager.getLogger;

/**
 * Created by Behnam Khazael on 2/22/2021.
 * Starts the simulation and configure simulation parameters as specified in the config file.
 * @author Behnam Khazael
 * @version 0.1
 */
public class Simulator {
    private static final Logger logger = getLogger(Simulator.class);
    private static final String HOSTNAME = "0.0.0.0";
    private static final int PORT = 7999;
    private static final int BACKLOG = 0;

    private static final String HEADER_ALLOW = "Allow";
    private static final String HEADER_CONTENT_TYPE = "Content-Type";

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    private static final int STATUS_OK = 200;
    private static final int STATUS_METHOD_NOT_ALLOWED = 405;

    private static final int NO_RESPONSE_LENGTH = -1;

    private static final String METHOD_POST = "POST";
    private static final String METHOD_OPTIONS = "OPTIONS";
    private static final String ALLOWED_METHODS = METHOD_POST + "," + METHOD_OPTIONS;


    public static void main(String[] args) {
        int numberOfComplex = Integer.parseInt(LoadConfig.getNumberOfComplexEvents());
        double simulationSpaceWidth = Double.parseDouble(LoadConfig.getSimulationSpaceWidth()); //m
        double simulationSpaceHeight = Double.parseDouble(LoadConfig.getSimulationSpaceHeight()); //m
        int complexEventRadius = Integer.parseInt(LoadConfig.getcomplexEventRadius()); //m
        int sensorsRadius = Integer.parseInt(LoadConfig.getSensorsRadius()); //m
        int simulationStartTime = Integer.parseInt(LoadConfig.getSimulationStartTime());
        int complexEventActiveTime = Integer.parseInt(LoadConfig.getComplexEventActiveTime());
        int timeToStartVisualizingTheWorkingSpace = Integer.parseInt(LoadConfig.getTimeToStartVisualizingTheWorkingSpace());
        int simulationStartSubmitRuleTime = Integer.parseInt(LoadConfig.getSimulationStartSubmitRuleTime());
        int concurrentEvent = Integer.parseInt(LoadConfig.getSimulationConcurrentEvent());
        HttpServer server = null;
        try {
            server = HttpServer.create(new InetSocketAddress(HOSTNAME, PORT), BACKLOG);
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.createContext("/simulator/register", new MoteRegistrator());
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();

        Stack<ComplexEvent> complexEventsList = new Stack<ComplexEvent>();


        try {
//the file to be opened for reading
            FileInputStream fis = new FileInputStream("eventArea.txt");
            Scanner sc = new Scanner(fis);    //file to be scanned
//returns true if there is another line to read
            int i = 1;
            WKTReader rdr = new WKTReader();
            while (sc.hasNextLine()) {
                i++;
                ComplexEvent complexEvent = new ComplexEvent();
                complexEvent.setName("ce" + (i));
                complexEvent.setTime(System.currentTimeMillis());
                try {
                    complexEvent.setTargetArea(rdr.read(sc.nextLine()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                complexEventsList.push(complexEvent);
            }
            sc.close();     //closes the scanner
        } catch (IOException e) {
            e.printStackTrace();
        }



        //Set up the sensors in the working space
        DrawUtility drawChit = new DrawUtility();

        // Lambda Runnable
        Runnable visualizeSensors = () -> {
            try {
                Thread.sleep(timeToStartVisualizingTheWorkingSpace);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (Mote m :
                    MoteRegistrator.motes.values()) {
                Geometry point = SpatialUtility.createRandomPoint(Double.parseDouble(m.getX_coordinate()), Double.parseDouble(m.getY_coordinate()));
                Geometry circle = SpatialUtility.createCircle((Point) point, sensorsRadius);
                if (m.getSensor().contains("Temp")) {
                    drawChit.addShape(point, Color.RED);
                    drawChit.addShape(circle, Color.RED);
                } else if (m.getSensor().contains("Smoke")) {
                    drawChit.addShape(point, Color.GRAY);
                    drawChit.addShape(circle, Color.GRAY);
                }
                logger.info(m.toString());
            }
        };
        // start the thread
        new Thread(visualizeSensors).start();
        DrawUtility.showGui(drawChit);


        // Lambda Runnable
        Runnable complexEventRuleThread = () -> {
            try {
                Thread.sleep(simulationStartSubmitRuleTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("rule", "Assign 2000=>Smoke,2001=>Temp,2100=>Fire Define Fire(area:String,mTemp:Float) From Smoke(area=>$a)and Each Temp([String]area=$a,value>45)within 5from Smoke Where area:=Smoke.area,mTemp:=Temp.value;");
            jsonObject.addProperty("uuid", "0000");
            jsonObject.addProperty("topic", "13");
            jsonObject.addProperty("callBackURL", "http://0.0.0.0:7998/event");

            try {
                URL url = new URL("http://" + URLEncoder.encode(LoadConfig.getSmartCityAppServerURL(), StandardCharsets.UTF_8.toString())
                        + ":"
                        + URLEncoder.encode(LoadConfig.getSmartCityAppServerPort(), StandardCharsets.UTF_8.toString())
                        + "/"
                        + URLEncoder.encode(LoadConfig.getBrokerRuleProvisioningResource(), StandardCharsets.UTF_8.toString())
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
            try {

                final HttpServer listener = HttpServer.create(new InetSocketAddress(HOSTNAME, 7998), BACKLOG);

                listener.createContext("/event", he -> {
                    try {
                        final DataMessage request = getRequestParameters(he.getRequestBody());
                        final Headers headers = he.getResponseHeaders();
                        final String requestMethod = he.getRequestMethod().toUpperCase();
                        System.out.println("-----------------------------");
                        System.out.println(he.getRequestURI().getPort());
                        System.out.println("-----------------------------");
                        String eventTemplate = request.getData().get("value").getAsString();
                        String[] templateArray = eventTemplate.split(",", 9);
                        request.getData().addProperty("area", (templateArray[7].split(":", 2))[1]);
                        request.getData().addProperty("Action", "COMPLEX_EVENT_REPORT");
                        request.getData().addProperty("Delay", request.getData().get("time").getAsLong() - request.getData().get("eventTime").getAsLong());
                        request.getData().addProperty("Solution", LoadConfig.getSolution());
                        request.getData().addProperty("NumberOfNodes", LoadConfig.getNumberOfMotes());
                        request.getData().addProperty("Run", LoadConfig.getRun());
                        switch (requestMethod) {
                            case METHOD_POST:
                                logger.info(request.toString());
                                final String responseBody = "['Ok']";
                                headers.set(HEADER_CONTENT_TYPE, String.format("application/json; charset=%s", CHARSET));
                                final byte[] rawResponseBody = responseBody.getBytes(CHARSET);
                                he.sendResponseHeaders(STATUS_OK, rawResponseBody.length);
                                he.getResponseBody().write(rawResponseBody);
                                he.close();
                                break;
                            case METHOD_OPTIONS:
                                headers.set(HEADER_ALLOW, ALLOWED_METHODS);
                                he.sendResponseHeaders(STATUS_OK, NO_RESPONSE_LENGTH);
                                he.getResponseBody().write("ok".getBytes());
                                he.close();
                                break;
                            default:
                                headers.set(HEADER_ALLOW, ALLOWED_METHODS);
                                he.sendResponseHeaders(STATUS_METHOD_NOT_ALLOWED, NO_RESPONSE_LENGTH);
                                he.getResponseBody().write("ok".getBytes());
                                he.close();
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        he.close();
                    }
                });

                listener.setExecutor(Executors.newCachedThreadPool());
                listener.start();

            } catch (IOException e) {
                e.printStackTrace();
            }

        };
        // start the thread
        new Thread(complexEventRuleThread).start();



        // Lambda Runnable
        Runnable complexEventThreadParallel = () -> {
            try {
                Thread.sleep(simulationStartTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (!complexEventsList.isEmpty()) {
                ArrayList<ComplexEvent> temp = new ArrayList<ComplexEvent>();
                if (complexEventsList.size() - concurrentEvent >= 0) {
                    for (int i = 0; i < concurrentEvent; i++) {
                        temp.add(complexEventsList.pop());
                    }
                } else {
                    for (int i = 0; i < complexEventsList.size(); i++) {
                        temp.add(complexEventsList.pop());
                    }
                }

                HashMap<ComplexEvent, ArrayList<Mote>> map = new HashMap<ComplexEvent, ArrayList<Mote>>();

                for (ComplexEvent e :
                        temp) {
                    ArrayList<Mote> list = new ArrayList<Mote>();
                    for (Mote m :
                            MoteRegistrator.motes.values()) {
                        if (check(m, e))
                            list.add(m);
                    }
                    map.put(e, list);
                }

                for (Map.Entry<ComplexEvent, ArrayList<Mote>> entry : map.entrySet()) {
                    for (Mote m :
                            entry.getValue()) {
                        changeStatus(m, "ON", entry.getKey());
                    }
                }

                try {
                    Thread.sleep(complexEventActiveTime);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }

                for (Map.Entry<ComplexEvent, ArrayList<Mote>> entry : map.entrySet()) {
                    for (Mote m :
                            entry.getValue()) {
                        changeStatus(m, "OFF", entry.getKey());
                    }
                }
            }
        };
        // start the thread
        new Thread(complexEventThreadParallel).start();


    }


    private static boolean check(Mote m, ComplexEvent e) {
        GeometryFactory geometryFactory = new GeometryFactory();
        Point point = geometryFactory.createPoint(new Coordinate(Double.parseDouble(m.getX_coordinate()), Double.parseDouble(m.getY_coordinate())));
        if (e.getTargetArea().covers(point) || e.getTargetArea().intersects(point))
            return true;
        return false;

    }

    private static boolean changeStatus(Mote m, String status, ComplexEvent event) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("command", status);
        jsonObject.addProperty("area", event.getName());

        try {
            URL url = new URL("http://" + URLEncoder.encode(m.getUrl(), StandardCharsets.UTF_8.toString())
                    + ":"
                    + URLEncoder.encode(m.getPort(), StandardCharsets.UTF_8.toString())
                    + "/"
                    + URLEncoder.encode("sensor/change-status", StandardCharsets.UTF_8.toString())
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
                return true;
            } else {
                logger.debug("{\"message\":\"nok\"}");
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private class DataMessage {
        private String type;
        private JsonObject data;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public JsonObject getData() {
            return data;
        }

        public void setData(JsonObject data) {
            this.data = data;
        }

        @Override
        public String toString() {
            JsonObject object = new JsonObject();
            object.addProperty("type", type);
            object.add("data", data);
            return object.toString();
        }
    }

    private static DataMessage getRequestParameters(InputStream inputStream) {
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
        return new Gson().fromJson(sb.toString(), DataMessage.class);
    }


}
