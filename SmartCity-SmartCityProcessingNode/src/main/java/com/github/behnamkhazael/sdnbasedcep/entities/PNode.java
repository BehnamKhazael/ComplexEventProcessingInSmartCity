package com.github.behnamkhazael.sdnbasedcep.entities;

import com.google.gson.JsonObject;

/**
 * Created by Behnam Khazael on 2/26/2021.
 * Represents a processing node.
 * @author Behnam Khazael
 * @version 0.1
 */
public class PNode {
    private String HOSTNAME;
    private int PORT; //read from config
    private int BACKLOG;
    private String directConnectedSinkAdd;
    private String sinkXCoordinate;
    private String sinkYCoordinate;
    private String x;
    private String y;
    private String width;
    private String height;


    public String getHOSTNAME() {
        return HOSTNAME;
    }

    public void setHOSTNAME(String HOSTNAME) {
        this.HOSTNAME = HOSTNAME;
    }

    public int getPORT() {
        return PORT;
    }

    public void setPORT(int PORT) {
        this.PORT = PORT;
    }

    public int getBACKLOG() {
        return BACKLOG;
    }

    public void setBACKLOG(int BACKLOG) {
        this.BACKLOG = BACKLOG;
    }

    public String getDirectConnectedSinkAdd() {
        return directConnectedSinkAdd;
    }

    public void setDirectConnectedSinkAdd(String directConnectedSinkAdd) {
        this.directConnectedSinkAdd = directConnectedSinkAdd;
    }

    public String getSinkXCoordinate() {
        return sinkXCoordinate;
    }

    public void setSinkXCoordinate(String sinkXCoordinate) {
        this.sinkXCoordinate = sinkXCoordinate;
    }

    public String getSinkYCoordinate() {
        return sinkYCoordinate;
    }

    public void setSinkYCoordinate(String sinkYCoordinate) {
        this.sinkYCoordinate = sinkYCoordinate;
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
        JsonObject obj = new JsonObject();
        obj.addProperty("HOSTNAME", this.getHOSTNAME());
        obj.addProperty("PORT", this.getPORT());
        obj.addProperty("BACKLOG", this.getBACKLOG());
        obj.addProperty("NodeAddress", this.getDirectConnectedSinkAdd().toString());
        obj.addProperty("sinkXCoordinate", this.getSinkXCoordinate());
        obj.addProperty("sinkYCoordinate", this.getSinkYCoordinate());
        obj.addProperty("x", this.getX());
        obj.addProperty("y", this.getY());
        obj.addProperty("width", this.getWidth());
        obj.addProperty("height", this.getHeight());
        return obj.toString();
    }
}
