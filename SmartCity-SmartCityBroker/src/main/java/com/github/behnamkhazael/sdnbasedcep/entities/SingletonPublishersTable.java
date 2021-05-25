package com.github.behnamkhazael.sdnbasedcep.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Behnam Khazael on 2/6/2021.
 * Table of the publishers
 * @author Behnam Khazael
 * @version 0.1
 */
public class SingletonPublishersTable {

    private interface Singleton {
        SingletonPublishersTable INSTANCE = new SingletonPublishersTable();
    }

    private SingletonPublishersTable(){}


    private final List<Publisher> publisherArrayList =  Collections.synchronizedList(new ArrayList<Publisher>());

    public static void addPublisher(Publisher p){
        synchronized (Singleton.INSTANCE.publisherArrayList) {
            Singleton.INSTANCE.publisherArrayList.add(p);
        }
    }

    public static void removePublisher(Publisher p){
        synchronized (Singleton.INSTANCE.publisherArrayList) {
            Singleton.INSTANCE.publisherArrayList.remove(p);
        }
    }

    public static Publisher getPublisher(Publisher p){
        synchronized (Singleton.INSTANCE.publisherArrayList) {
            if (Singleton.INSTANCE.publisherArrayList.contains(p))
                return Singleton.INSTANCE.publisherArrayList.get(Singleton.INSTANCE.publisherArrayList.indexOf(p));
            else return null;
        }
    }

    public static ArrayList<Publisher> getPublisherTable(){
        return new ArrayList<Publisher>(Singleton.INSTANCE.publisherArrayList);
    }


}
