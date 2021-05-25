package com.github.sdnwiselab.sdnwise.function;

import com.github.sdnwiselab.sdnwise.controller.ControllerDijkstra;
import com.github.sdnwiselab.sdnwise.entities.ProcessingNode;
import com.github.sdnwiselab.sdnwise.entities.Publisher;
import com.github.sdnwiselab.sdnwise.util.NodeAddress;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import java.util.HashMap;

import static com.github.sdnwiselab.sdnwise.loader.SdnWise.NODE_ADDRESS_PROCESSING_NODE_HASHTABLE;
import static com.github.sdnwiselab.sdnwise.loader.SdnWise.staticRefrence;

/**
 * Created by Behnam Khazael on 2/28/2021.
 * Select a Processing node based on the list of publishers for a rule that submitted to the controller.
 * Closest processing node will selected to run the rule and be a subscriber for all eligible publishers
 * @author Behnam Khazael
 * @version 0.1
 */
public class HeadSelector {
    private HeadSelector(){}

    static ProcessingNode returnHead(JsonArray publishersList, String rule, String requester) {
        try {
            HashMap<ProcessingNode, Integer> matrix = new HashMap<ProcessingNode, Integer>();
            Gson gson = new Gson();
            Publisher[] publishers = gson.fromJson(publishersList, Publisher[].class);
            for (ProcessingNode pr :
                    NODE_ADDRESS_PROCESSING_NODE_HASHTABLE) {
            }
            for (ProcessingNode p :
                    NODE_ADDRESS_PROCESSING_NODE_HASHTABLE) {
                //Default address 0.99 should generalize.
                if (p.getAddress().compareTo(new NodeAddress("0.99")) == 0)
                    continue;
                int aggrigatedPathLength = 0;
                for (Publisher pub :
                        publishers) {
                    NodeAddress pubAddr = new NodeAddress(pub.getAddress());
                    if (pubAddr.getHigh() != p.getAddress().getHigh())
                        continue;
                    int pathLength = 0;
                    try {
                        pathLength = ((ControllerDijkstra) staticRefrence).getShortestPathTo(new NodeAddress(pub.getAddress()), p.getAddress(), p.getAddress().getHigh()).size();
                    }catch (Exception e)
                    {e.printStackTrace();}
                    aggrigatedPathLength = aggrigatedPathLength + pathLength;
                }
                matrix.put(p, aggrigatedPathLength);
                //System.out.println("Processing Node: " + p.getAddress().toString() +" Score: " + aggrigatedPathLength);
            }
            ProcessingNode head = matrix.entrySet().stream().min((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getKey();
            //System.out.println("Selected Processing node is: " + head.getAddress().toString());
            // Lambda Runnable
            Runnable subscriptionEnablingThread = () -> {
                SubscriptionEnabler.enableSubscriptions(publishersList, head, rule, requester);
            };
            // start the thread
            new Thread(subscriptionEnablingThread).start();
            return head;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
