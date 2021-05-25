package com.github.behnamkhazael.sdnbasedcep.entities;

import com.google.gson.JsonObject;

/**
 * Created by Behnam Khazael on 2/24/2021.
 * Represents a notification that received on the processing node.
 * @author Behnam Khazael
 * @version 0.1
 */
public class Data {
    private String topic;
    private String value;
    private String time;
    private String uid;
    private String QoS;
    private String Area;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getQoS() {
        return QoS;
    }

    public void setQoS(String qoS) {
        QoS = qoS;
    }

    public String getArea() {
        return Area;
    }

    public void setArea(String area) {
        Area = area;
    }

    @Override
    public String toString(){
        JsonObject object = new JsonObject();
        object.addProperty("topic", this.getTopic());
        object.addProperty("value", this.getValue());
        object.addProperty("time", this.getTime());
        object.addProperty("uid", this.getUid());
        object.addProperty("QoS", this.getQoS());
        object.addProperty("Area", this.getArea());
        return object.toString();
    }
}
