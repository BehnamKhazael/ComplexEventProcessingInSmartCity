import com.google.gson.JsonObject;

/**
 * Created by Behnam Khazael on 2/22/2021.
 * Represents a Mote.
 * @author Behnam Khazael
 * @version 0.1
 */
public class Mote {
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
        JsonObject object = new JsonObject();
        object.addProperty("node", node);
        object.addProperty("sensor", sensor);
        object.addProperty("x_coordinate", x_coordinate);
        object.addProperty("y_coordinate", y_coordinate);
        object.addProperty("url", url);
        object.addProperty("port", port);
        return object.toString();
    }

}
