package com.github.behnamkhazael.sdnbasedcep.entities;

import java.util.*;

/**
 * Created by Behnam Khazael on 2/14/2021.
 * Represents a table of rule and engines.
 * @author Behnam Khazael
 * @version 0.1
 */
public class SingletonEngineTable {
    private interface Singleton {
        SingletonEngineTable INSTANCE = new SingletonEngineTable();
    }

    private SingletonEngineTable(){}


    private final Hashtable<Integer, CEPEngine> enginesTable = new Hashtable<Integer, CEPEngine>();

    public static void addEngine(Integer topic, CEPEngine engine){
        synchronized (Singleton.INSTANCE.enginesTable) {
            Singleton.INSTANCE.enginesTable.put(topic, engine);
        }
    }

    public static void removeEngine(Integer topic){
        synchronized (Singleton.INSTANCE.enginesTable) {
            Singleton.INSTANCE.enginesTable.remove(topic);
        }
    }

    public static CEPEngine getEngine(Integer topic){
        synchronized (Singleton.INSTANCE.enginesTable) {
            if (Singleton.INSTANCE.enginesTable.contains(topic))
                return Singleton.INSTANCE.enginesTable.get(topic);
            else return null;
        }
    }


    public static Hashtable<Integer, CEPEngine> getEngineTable(){
        return new Hashtable<>(Singleton.INSTANCE.enginesTable);
    }

}
