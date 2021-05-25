package com.github.sdnwiselab.sdnwise.function;

import com.github.sdnwiselab.sdnwise.controller.ControllerDijkstra;
import com.github.sdnwiselab.sdnwise.entities.ProcessingNode;
import com.github.sdnwiselab.sdnwise.entities.Publisher;
import com.github.sdnwiselab.sdnwise.packet.DataPacket;
import com.github.sdnwiselab.sdnwise.util.NodeAddress;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import static com.github.sdnwiselab.sdnwise.configuration.LoadConfig.getProcessingNodeEngineOnResource;
import static com.github.sdnwiselab.sdnwise.loader.SdnWise.NODE_ADDRESS_PROCESSING_NODE_HASHTABLE;
import static com.github.sdnwiselab.sdnwise.loader.SdnWise.staticRefrence;
import static org.apache.logging.log4j.LogManager.getLogger;

/**
 * Created by Behnam Khazael on 2/28/2021.
 * Sending subscription request on behalf of the selected processing node.
 * @author Behnam Khazael
 * @version 0.1
 */
public class SubscriptionEnabler {
    private static ArrayBlockingQueue<Publisher> list = new ArrayBlockingQueue<Publisher>(1000);
    private static final List<Publisher> solution = new ArrayList<>();
    private static final ArrayBlockingQueue<Publisher> list2 = new ArrayBlockingQueue<Publisher>(1000);
    private static final ArrayBlockingQueue<String> list3 = new ArrayBlockingQueue<String>(1000);

    private static ProcessingNode selectedHead;
    private static final org.apache.logging.log4j.Logger logger = getLogger(SubscriptionEnabler.class);

    private SubscriptionEnabler() {
    }

    static void enableSubscriptions(JsonArray publishersList, ProcessingNode head, String rule, String requester) {
        selectedHead = head;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("callBackUrl", requester);
        jsonObject.addProperty("rule", rule);

        //Sending Engine start request to the processing node!

        for (ProcessingNode p :NODE_ADDRESS_PROCESSING_NODE_HASHTABLE) {

            try {
                //System.out.println("HEAD URL: " + p.getUrl() + ", Head Port: " + p.getPort() + ", Head Resource: " + getProcessingNodeEngineOnResource());
                URL url = new URL("http://" + URLEncoder.encode(p.getUrl(), StandardCharsets.UTF_8.toString())
                        + ":"
                        + URLEncoder.encode(p.getPort(), StandardCharsets.UTF_8.toString())
                        + "/"
                        + URLEncoder.encode(getProcessingNodeEngineOnResource(), StandardCharsets.UTF_8.toString())

                );

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json");
                httpURLConnection.setRequestProperty("Accept", "application/json");
                httpURLConnection.setDoOutput(true);


                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());

                wr.write(jsonObject.toString().getBytes());

                int responseCode = httpURLConnection.getResponseCode();

                if (responseCode == 200) {
                    logger.debug("{\"message\":\"ok\"}");
                } else {
                    logger.debug("{\"message\":\"nok\"}");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }


        Gson gson = new Gson();
        Publisher[] publishers = gson.fromJson(publishersList, Publisher[].class);
        Collections.addAll(list, publishers);
        solution.addAll(list);
        Collections.shuffle(solution);
        list2.addAll(solution);



            Thread register = new Thread(new subscribeTillReceiveAck());
            register.start();

    }


    //TODO write this part with for (int i = 0 ; i < list.size; i++) synchoronize list ...
    private static class subscribeTillReceiveAck implements Runnable {
        @Override
        public void run() {
            while (list2.size() > 0) {
                System.out.println("Sub list Size: " + list2.size());
                for (Publisher pub :
                        list2) {
                    System.out.println("Sending Sub request to: " + pub.getAddress());
                    NodeAddress pubAddr = new NodeAddress(pub.getAddress());
                    int netId = pubAddr.getHigh();
                    NodeAddress src = selectedHead.getAddress();
                    if (netId == 0)
                        src = new NodeAddress("0.1");
                    if (netId == 1)
                        src = new NodeAddress("1.1");
                    if (netId == 2)
                        src = new NodeAddress("2.1");
                    if (netId == 3)
                        src = new NodeAddress("3.1");
                    System.out.println("Selected Head Address is: " + src.toString());
                    //Sending subscriber request to the publishers on behalf of head!
                    JsonObject subMessage = new JsonObject();
                    JsonObject subMessageInner = new JsonObject();
                    subMessage.addProperty("type", "sub");
                    subMessageInner.addProperty("topic", pub.getTopic());
                    subMessageInner.addProperty("requester", selectedHead.getAddress().toString());
                    subMessageInner.addProperty("time", System.currentTimeMillis());
                    subMessage.add("data", subMessageInner);
                    DataPacket subscribe = new DataPacket(netId, src, pubAddr);
                    subscribe.setPayload(subMessage.toString().getBytes());
                    subscribe.setNxhop(src);
                    //System.out.println("Net Id: " + netId + ", " + " src: " + src + ", pubAddr: " + pubAddr);
                    try {
                        ((ControllerDijkstra) staticRefrence).manageRoutingRequest(subscribe);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    staticRefrence.sendNetworkPacket(subscribe);
                    logger.info(subMessage);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                clean();
            }


            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private static void clean() {

        for (Publisher p :
                list2) {
            for (String p2 :
                    list3) {
                NodeAddress pN = new NodeAddress(p.getAddress());
                NodeAddress n = new NodeAddress(p2);

                if (pN.compareTo(n) == 0) {
                    boolean check = list2.remove(p);
                    System.out.println("Successfully Removed! ->" + check);
                }
            }

        }
    }

    public static boolean removeFromSubscriptionList(String nodeAddress) {
        return list3.add(nodeAddress);

    }

}
