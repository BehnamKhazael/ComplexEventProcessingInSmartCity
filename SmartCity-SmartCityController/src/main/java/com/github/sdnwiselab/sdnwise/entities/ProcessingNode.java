package com.github.sdnwiselab.sdnwise.entities;

import com.github.sdnwiselab.sdnwise.util.NodeAddress;
import com.google.gson.JsonObject;

/**
 * Created by Behnam Khazael on 2/28/2021.
 * Represents a processing node
 * @author Behnam Khazael
 * @version 0.1
 */
public class ProcessingNode {
    private NodeAddress address;
    private String url;
    private String port;
    private String x;
    private String y;
    private String width;
    private String height;
    private String resource;

    public ProcessingNode(NodeAddress sink){
        this.setAddress(sink);
    }
    public ProcessingNode(){
    }

    public NodeAddress getAddress() {
        return address;
    }

    public void setAddress(NodeAddress address) {
        this.address = address;
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

    @Override
    public String toString(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("address", address.toString());
        jsonObject.addProperty("url", url);
        jsonObject.addProperty("port", port);
        jsonObject.addProperty("x", x);
        jsonObject.addProperty("y", y);
        jsonObject.addProperty("width", width);
        jsonObject.addProperty("height", height);
        return jsonObject.toString();
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }
}
