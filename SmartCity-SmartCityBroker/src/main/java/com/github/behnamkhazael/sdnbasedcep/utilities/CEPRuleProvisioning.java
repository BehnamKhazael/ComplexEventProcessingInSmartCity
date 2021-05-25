package com.github.behnamkhazael.sdnbasedcep.utilities;

import com.github.behnamkhazael.sdnbasedcep.entities.SingletonRulesTable;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.Logger;
import trex.packets.RulePkt;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static com.github.behnamkhazael.sdnbasedcep.config.LoadConfig.*;
import static org.apache.logging.log4j.LogManager.getLogger;


/**
 * Created by Behnam Khazael on 2/19/2021.
 * Sending rule provisioning request to the controller.
 * @author Behnam Khazael
 * @version 0.1
 */
public class CEPRuleProvisioning {
    private static final Logger logger = getLogger(CEPRuleProvisioning.class);

    private CEPRuleProvisioning() {
    }

    public static Boolean provisionRequest(RulePkt rule, String uuid, String topic, String callBackURL) {

        JsonArray jsonElements = new Gson().toJsonTree(SingletonRulesTable.getRulesTable().get(rule)).getAsJsonArray();


        JsonObject obj = new JsonObject();
        obj.addProperty("Solution", getSolution());
        obj.addProperty("NumberOfNodes", getNumberOfMotes());
        obj.addProperty("Run", getRun());
        obj.addProperty("Action", "PUBLISHERS_LIST");
        obj.add("PublishersList", jsonElements);
        logger.info(obj);


        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("rule", rule.toString());
        jsonObject.addProperty("uuid", uuid);
        jsonObject.addProperty("callBackURL", callBackURL);
        jsonObject.add("publishersList", jsonElements);
        long requestId = System.currentTimeMillis();
        jsonObject.addProperty("requestId", requestId);


        try {
            URL url = new URL("http://" + URLEncoder.encode(getControllerUrl(), StandardCharsets.UTF_8.toString())
                    + ":"
                    + URLEncoder.encode(getControllerPort(), StandardCharsets.UTF_8.toString())
                    + "/"
                    + URLEncoder.encode(getControllerProvisioningResource(), StandardCharsets.UTF_8.toString())
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
                return true;
            } else {
                logger.debug("{\"message\":\"nok\"}");
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
