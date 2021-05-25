package com.github.behnamkhazael.sdnbasedcep.entities;

import com.google.gson.JsonObject;
import trex.common.Attribute;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Behnam Khazael on 2/6/2021.
 * Represents a publisher
 * @author Behnam Khazael
 * @version 0.1
 */
public class Publisher {
    private String topic;
    private int eventType;
    private String address;
    private String location;
    private String QoS;
    private Collection<Attribute> attributes = new ArrayList<>();

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getQoS() {
        return QoS;
    }

    public void setQoS(String qoS) {
        QoS = qoS;
    }

    public Collection<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Collection<Attribute> attributes) {
        this.attributes = attributes;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    @Override
    public String toString(){
        JsonObject object = new JsonObject();
        object.addProperty("topic", topic);
        object.addProperty("eventType", eventType);
        object.addProperty("address", address);
        object.addProperty("location", location);
        object.addProperty("QoS", QoS);
        return object.toString();
    }
}
