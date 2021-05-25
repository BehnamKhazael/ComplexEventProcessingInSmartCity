package com.github.behnamkhazael.sdnbasedcep.utilities;

import com.github.behnamkhazael.sdnbasedcep.entities.Event;
import com.github.behnamkhazael.sdnbasedcep.entities.Publisher;
import com.github.behnamkhazael.sdnbasedcep.entities.SingletonPublishersTable;
import com.github.behnamkhazael.sdnbasedcep.entities.SingletonRulesTable;
import trex.common.*;
import trex.packets.RulePkt;

import java.util.*;

/**
 * Created by Behnam Khazael on 2/6/2021.
 * Decompose a submitted rule (Based on T-Rex EPL) and extract simple events with their defined criteria.
 * @author Behnam Khazael
 * @version 0.1
 */
public class CEPRulePreProcessing {
    private CEPRulePreProcessing() {
    }

    public static Boolean checkRule(RulePkt rule, String uuid, String topic, String callBackURL) {

        //At first we extract all events from the rule.
        ArrayList<Event> events = new ArrayList<Event>();
        ArrayList<Publisher> publisherArrayList = new ArrayList<Publisher>();

        int numPreds = rule.getPredicatesNum();
        // Iterating on rule predicates (states)
        for (int state = 0; state < numPreds; state++) {
            EventPredicate pred = rule.getPredicates(state);
            Event e = new Event();
            e.setEventType(pred.getEventType());
            for (Constraint i : pred.getConstraints()
                    ) {
                e.getConstraints().add(i);
            }
            events.add(e);
        }
        // Iterating on rule aggregates
        int aggregatesNum = rule.getAggregatesNum();
        for (int aggId = 0; aggId < aggregatesNum; aggId++) {
            TAggregate agg = rule.getAggregate(aggId);
            // We may add this to TAggregate!!
            Event e = new Event();
            e.setEventType(agg.getEventType());
            for (Constraint i : agg.getConstraints()
                    ) {
                e.getConstraints().add(i);
            }
            events.add(e);
        }
        // Iterating on rule negations
        int negationsNum = rule.getNegationsNum();
        for (int negId = 0; negId < negationsNum; negId++) {
            Negation neg = rule.getNegation(negId);
            Event e = new Event();
            e.setEventType(neg.getEventType());
            for (Constraint i : neg.getConstraints()
                    ) {
                e.getConstraints().add(i);
            }
            events.add(e);
        }

        Hashtable<Event, LinkedList<Publisher>> eventLinkedListHashtable = new Hashtable<Event, LinkedList<Publisher>>();

        for (Event e :
                events) {
            for (Publisher p :
                    SingletonPublishersTable.getPublisherTable()) {
                if (MatchingService.match(e, p) != 0) {
                    eventLinkedListHashtable.putIfAbsent(e, new LinkedList<>());
                    eventLinkedListHashtable.get(e).add(p);
                    publisherArrayList.add(p);
                }
            }
        }

        for (Event e :
                events) {
            if (!eventLinkedListHashtable.containsKey(e))
                return false;
        }
        SingletonRulesTable.addRule(rule, publisherArrayList);
        Publisher complexPublisher = new Publisher();
        complexPublisher.setEventType(rule.getEventTemplate().getEventType());
        complexPublisher.setAddress(callBackURL);
        complexPublisher.setTopic(topic);
        SingletonPublishersTable.addPublisher(complexPublisher);
        return true;
    }


}
