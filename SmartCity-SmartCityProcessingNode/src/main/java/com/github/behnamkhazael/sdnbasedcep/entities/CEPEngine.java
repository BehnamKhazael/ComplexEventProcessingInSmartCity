package com.github.behnamkhazael.sdnbasedcep.entities;

import trexengine.TRexEngine;

/**
 * Created by Behnam Khazael on 2/14/2021.
 * Represents a CEP engine.
 * @author Behnam Khazael
 * @version 0.1
 */
public class CEPEngine {
    private int topic;
    private TRexEngine engine;

    public int getTopic() {
        return topic;
    }

    public void setTopic(int topic) {
        this.topic = topic;
    }

    public TRexEngine getEngine() {
        return engine;
    }

    public void setEngine(TRexEngine engine) {
        this.engine = engine;
    }
}
