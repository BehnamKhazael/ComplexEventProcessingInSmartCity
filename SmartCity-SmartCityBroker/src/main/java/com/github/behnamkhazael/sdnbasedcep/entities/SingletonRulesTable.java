package com.github.behnamkhazael.sdnbasedcep.entities;

import trex.packets.RulePkt;

import java.util.*;

/**
 * Created by Behnam Khazael on 2/6/2021.
 * Table of the rules.
 * @author Behnam Khazael
 * @version 0.1
 */
public class SingletonRulesTable {
    private interface Singleton {
        SingletonRulesTable INSTANCE = new SingletonRulesTable();
    }

    private SingletonRulesTable(){}


    private final Hashtable<RulePkt, ArrayList<Publisher>> rulePktsHashTable =  new Hashtable<RulePkt, ArrayList<Publisher>>();

    public static void addRule(RulePkt rulePkt, ArrayList<Publisher> publisherLinkedList){
        synchronized (Singleton.INSTANCE.rulePktsHashTable) {
            Singleton.INSTANCE.rulePktsHashTable.put(rulePkt, publisherLinkedList);
        }
    }

    public static void removeRule(RulePkt rulePkt){
        synchronized (Singleton.INSTANCE.rulePktsHashTable) {
            Singleton.INSTANCE.rulePktsHashTable.remove(rulePkt);
        }
    }

    public static boolean hasRule(RulePkt rulePkt){
        synchronized (Singleton.INSTANCE.rulePktsHashTable) {
            if (Singleton.INSTANCE.rulePktsHashTable.contains(rulePkt))
                return true;
            else return false;
        }
    }

    public static Hashtable<RulePkt, ArrayList<Publisher>> getRulesTable(){
        return new Hashtable<RulePkt, ArrayList<Publisher>>(Singleton.INSTANCE.rulePktsHashTable);
    }
}
