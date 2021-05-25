package com.github.behnamkhazael.sdnbasedcep.entities;

import trex.common.Constraint;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Behnam Khazael on 2/6/2021.
 * Represents an event.
 * @author Behnam Khazael
 * @version 0.1
 */
public class Event {

    private int eventType;
    private Collection<Constraint> constraints = new ArrayList<>();

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }


    public Collection<Constraint> getConstraints() {
        return constraints;
    }

    public void setAttributes(Collection<Constraint> attributes) {
        this.constraints = attributes;
    }




}
