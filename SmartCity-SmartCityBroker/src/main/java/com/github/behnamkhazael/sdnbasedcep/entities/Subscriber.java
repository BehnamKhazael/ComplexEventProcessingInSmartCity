package com.github.behnamkhazael.sdnbasedcep.entities;

/**
 * Created by Behnam Khazael on 2/6/2021.
 * Represents a subscriber
 * @author Behnam Khazael
 * @version 0.1
 */
public class Subscriber {
    private String topic;
    private String address;

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
}
