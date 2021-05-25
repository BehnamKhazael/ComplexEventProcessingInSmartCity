package com.github.sdnwiselab.sdnwise.entities;

/**
 * Created by Behnam Khazael on 2/28/2021.
 * Represents a Publisher (Edge node).
 * @author Behnam Khazael
 * @version 0.1
 */
public class Publisher {
    private String topic;
    private int eventType;
    private String address;
    private String location;
    private String QoS;
    //private Collection<Attribute> attributes = new ArrayList<>();

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

    //public Collection<Attribute> getAttributes() {
//        return attributes;
//    }

    //public void setAttributes(Collection<Attribute> attributes) {
//        this.attributes = attributes;
//    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }
}
