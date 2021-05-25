package com.github.behnamkhazael.sdnbasedcep.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Behnam Khazael on 2/6/2021.
 * Table of the subscribers
 * @author Behnam Khazael
 * @version 0.1
 */
public class SingletonSubscribersTable {
    private interface Singleton {
        SingletonSubscribersTable INSTANCE = new SingletonSubscribersTable();
    }
    private SingletonSubscribersTable(){}
    private final List<Subscriber> subscriberArrayList =  Collections.synchronizedList(new ArrayList<Subscriber>());

    public static boolean addSubscriber(Subscriber s){
        synchronized (Singleton.INSTANCE.subscriberArrayList) {
            return Singleton.INSTANCE.subscriberArrayList.add(s);
        }
    }


    public static void removeSubscriber(Subscriber s){
        synchronized (Singleton.INSTANCE.subscriberArrayList) {
            Singleton.INSTANCE.subscriberArrayList.remove(s);
        }
    }

    public static Subscriber getSubscriber(Subscriber s){
        synchronized (Singleton.INSTANCE.subscriberArrayList) {
            if (Singleton.INSTANCE.subscriberArrayList.contains(s))
                return Singleton.INSTANCE.subscriberArrayList.get(Singleton.INSTANCE.subscriberArrayList.indexOf(s));
            else return null;
        }
    }

    public static ArrayList<Subscriber> getSubscriberTable(){
        return new ArrayList<Subscriber>(Singleton.INSTANCE.subscriberArrayList);
    }

}
