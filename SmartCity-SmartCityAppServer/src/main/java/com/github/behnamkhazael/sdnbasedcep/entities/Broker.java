package com.github.behnamkhazael.sdnbasedcep.entities;

import com.google.gson.JsonObject;
import org.apache.logging.log4j.Logger;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static com.github.behnamkhazael.sdnbasedcep.config.LoadConfig.*;
import static org.apache.logging.log4j.LogManager.getLogger;

/**
 * Created by Behnam Khazael on 2/5/2021.
 * This Class represents the broker module of the SDN-BASED-CEP. for AppServer.
 */
public class Broker {

    private static final Logger logger = getLogger(Broker.class);
    private String name;
    private String mac;
    private String address;
    private String port;

    public boolean checkRule(String rule, String uuid, String topic, String callBackURL) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("rule", rule);
        jsonObject.addProperty("uuid", uuid);
        jsonObject.addProperty("topic", topic);
        jsonObject.addProperty("callBackURL", callBackURL);

        try {
            URL url = new URL("http://" + URLEncoder.encode(this.getAddress(), StandardCharsets.UTF_8.toString())
                    + ":"
                    + URLEncoder.encode(this.getPort(), StandardCharsets.UTF_8.toString())
                    + "/"
                    + URLEncoder.encode(getBrokerCheckRuleResource(), StandardCharsets.UTF_8.toString())
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

    public boolean addRule(String rule) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("brokerName", this.getName());
        jsonObject.addProperty("rule", rule);
        long requestId = System.currentTimeMillis();
        jsonObject.addProperty("requestId", requestId);


        try {
            URL url = new URL("http://" + URLEncoder.encode(getBrokerUrl(), StandardCharsets.UTF_8.toString())
                    + ":"
                    + URLEncoder.encode(getBrokerPort(), StandardCharsets.UTF_8.toString())
                    + "/"
                    + URLEncoder.encode(getBrokerAddRuleResource(), StandardCharsets.UTF_8.toString())
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

    public boolean removeRule(String rule) {
        return false;
    }

    public boolean subscribe(String uuid, String topic, String callBackURL) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("brokerName", this.getName());
        jsonObject.addProperty("uuid", uuid);
        jsonObject.addProperty("topic", topic);
        jsonObject.addProperty("callBackURL", callBackURL);
        long requestId = System.currentTimeMillis();
        jsonObject.addProperty("requestId", requestId);


        try {
            URL url = new URL("http://" + URLEncoder.encode(getBrokerUrl(), StandardCharsets.UTF_8.toString())
                    + ":"
                    + URLEncoder.encode(getBrokerPort(), StandardCharsets.UTF_8.toString())
                    + "/"
                    + URLEncoder.encode(getBrokerSubscribeResource(), StandardCharsets.UTF_8.toString())
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

    public boolean unSubscribe(String uuid, String topic) {
        return false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
