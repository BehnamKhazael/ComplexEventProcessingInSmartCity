package com.github.behnamkhazael.sdnbasedcep.loader;

import com.github.behnamkhazael.sdnbasedcep.entities.PNode;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by sony on 2/13/2021.
 */
public class ProcessingNodeLoader {
    private static final Hashtable<String, ProcessingNode> processingHashTable = new Hashtable<String, ProcessingNode>();

    public static void main(String args[]) throws IOException {

        Gson gson = new Gson();

        BufferedReader reader = new BufferedReader(
                new FileReader("ProcessingNodes.json"));

        PNode[] config = gson.fromJson(reader, PNode[].class);

        List<PNode> list = Arrays.asList(config);

        //list.forEach(pNode -> System.out.println(pNode.toString()));

        list.forEach(ProcessingNodeLoader::loadProcessingNode);



    }
    private static void loadProcessingNode(PNode pNode) {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        processingHashTable.put(pNode.getDirectConnectedSinkAdd(), new ProcessingNode(pNode));
    }
}
