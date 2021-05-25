package com.github.sdnwiselab.sdnwise.cooja;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * Created by Behnam Khazael on 2/21/2021.
 * Utility Class for loading configs.
 * @author Behnam Khazael
 * @version 0.1
 */
public class LoadConfig {

    //to load application's properties, we use this class
    private static Properties mainProperties = new Properties();

    private static void loadFile() {
        try {
            FileInputStream file;
            //the base folder is ./, the root of the main.properties file
            String path = "/home/behnamkhazael/Desktop/upload/config.properties";

            //load the file handle for main.properties
            file = new FileInputStream(path);


            //load all the properties from this file
            mainProperties.load(file);

            //we have loaded the properties, so close the file handle
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getLoggerDBUser() {
        loadFile();
        String loggerDBUser;
        loggerDBUser = mainProperties.getProperty("DB_USER");
        return loggerDBUser;
    }

    public static String getLoggerDBPassword() {
        loadFile();
        String loggerDBPassword;
        loggerDBPassword = mainProperties.getProperty("DB_PASSWORD");
        return loggerDBPassword;
    }

    public static String getLoggerDBIp() {
        loadFile();
        String loggerDBIp;
        loggerDBIp = mainProperties.getProperty("DB_IP");
        return loggerDBIp;
    }

    public static String getEventSimulatorUrl() {
        loadFile();
        String loggerDBIp;
        loggerDBIp = mainProperties.getProperty("EventSimulator_IP");
        return loggerDBIp;
    }

    public static String getEventSimulatorPort() {
        loadFile();
        String loggerSimulatorIp;
        loggerSimulatorIp = mainProperties.getProperty("EventSimulator_PORT");
        return loggerSimulatorIp;
    }

    public static String getEventSimulatorResource() {
        loadFile();
        String loggerSimulatorUri;
        loggerSimulatorUri = mainProperties.getProperty("EventSimulator_MOTE_LOCATION");
        return loggerSimulatorUri;
    }

    public static String getBrokerUrl() {
        loadFile();
        String loggerBrokerIp;
        loggerBrokerIp = mainProperties.getProperty("BROKER_IP");
        return loggerBrokerIp;
    }

    public static String getBrokerPort() {
        loadFile();
        String loggerBrokerPort;
        loggerBrokerPort = mainProperties.getProperty("BROKER_PORT");
        return loggerBrokerPort;
    }

    public static String getBrokerPublisherRegisterResource() {
        loadFile();
        String loggerBrokerPublisherRegisterUri;
        loggerBrokerPublisherRegisterUri = mainProperties.getProperty("BROKER_PUBLISHER_REGISTER_RESOURCE");
        return loggerBrokerPublisherRegisterUri;
    }

    public static String getControllerUrl() {
        loadFile();
        String loggerControllerUrl;
        loggerControllerUrl = mainProperties.getProperty("CONTROLLER_IP");
        return loggerControllerUrl;
    }

    public static String getControllerPort() {
        loadFile();
        String loggerControllerPort;
        loggerControllerPort = mainProperties.getProperty("CONTROLLER_PORT");
        return loggerControllerPort;
    }

    public static String getControllerUpdateProcessingNodeResource() {
        loadFile();
        String loggerControllerIpdateProcessingNodeUri;
        loggerControllerIpdateProcessingNodeUri = mainProperties.getProperty("CONTROLLER_UPDATE_PROCESSING_NODE_RESOURCE");
        return loggerControllerIpdateProcessingNodeUri;
    }

    public static String getSolution() {
        loadFile();
        String loggerDBIp;
        loggerDBIp = mainProperties.getProperty("SOLUTION");
        return loggerDBIp;
    }

    public static String getNumberOfMotes() {
        loadFile();
        String loggerDBIp;
        loggerDBIp = mainProperties.getProperty("NUMBER_OF_MOTES");
        return loggerDBIp;
    }

    public static String getRun() {
        loadFile();
        String loggerDBIp;
        loggerDBIp = mainProperties.getProperty("RUN");
        return loggerDBIp;
    }

    public static String getBatteryMaxLevel() {
        loadFile();
        String batteryMaxLevel;
        batteryMaxLevel = mainProperties.getProperty("BATTERY_MAX_LEVEL");
        return batteryMaxLevel;
    }

    public static String getRegistrationStartTime() {
        loadFile();
        String registrationStartTime;
        registrationStartTime = mainProperties.getProperty("REGISTRATION_START_TIME");
        return registrationStartTime;
    }

    public static String getRegistrationStartTimeRandomDelay() {
        loadFile();
        String registrationStartTimeRandomDelay;
        registrationStartTimeRandomDelay = mainProperties.getProperty("REGISTRATION_START_TIME_RANDOM_DELAY");
        return registrationStartTimeRandomDelay;
    }


    public static String getTheStartTimeToListenToTheSimulator() {
        loadFile();
        String timeToSendInfoToSimulator;
        timeToSendInfoToSimulator = mainProperties.getProperty("THE_START_TIME_TO_LISTEN_TO_THE_SIMULATOR");
        return timeToSendInfoToSimulator;
    }

    public static String getSmokeSensorType1SampleRate() {
        loadFile();
        String smokeSensorType1SampleRate;
        smokeSensorType1SampleRate = mainProperties.getProperty("SMOKE_SENSOR_TYPE_1_SAMPLE_RATE");
        return smokeSensorType1SampleRate;
    }

    public static String getSmokeSensorType2SampleRate() {
        loadFile();
        String smokeSensorType2SampleRate;
        smokeSensorType2SampleRate = mainProperties.getProperty("SMOKE_SENSOR_TYPE_2_SAMPLE_RATE");
        return smokeSensorType2SampleRate;
    }

    public static String getTempSensorType1SampleRate() {
        loadFile();
        String tempSensorType1SampleRate;
        tempSensorType1SampleRate = mainProperties.getProperty("TEMP_SENSOR_TYPE_1_SAMPLE_RATE");
        return tempSensorType1SampleRate;
    }

    public static String getTempSensorType2SampleRate() {
        loadFile();
        String tempSensorType2SampleRate;
        tempSensorType2SampleRate = mainProperties.getProperty("TEMP_SENSOR_TYPE_2_SAMPLE_RATE");
        return tempSensorType2SampleRate;
    }

    public static String getTempSensorType1Error() {
        loadFile();
        String tempSensorType1Error;
        tempSensorType1Error = mainProperties.getProperty("TEMP_SENSOR_TYPE_1_ERROR_RATE");
        return tempSensorType1Error;
    }

    public static String getTempSensorType1RegisterError() {
        loadFile();
        String tempSensorType1Error;
        tempSensorType1Error = mainProperties.getProperty("TEMP_SENSOR_TYPE_1_REGISTER_RATE");
        return tempSensorType1Error;
    }

    public static String getTempSensorType2Error() {
        loadFile();
        String tempSensorType2Error;
        tempSensorType2Error = mainProperties.getProperty("TEMP_SENSOR_TYPE_2_ERROR_RATE");
        return tempSensorType2Error;
    }

    public static String getTempSensorType2RegisterError() {
        loadFile();
        String tempSensorType2RegisterError;
        tempSensorType2RegisterError = mainProperties.getProperty("TEMP_SENSOR_TYPE_2_REGISTER_RATE");
        return tempSensorType2RegisterError;
    }

    public static String getControllerSubscriptionAck(){
        loadFile();
        String ackResource;
        ackResource = mainProperties.getProperty("CONTROLLER_UPDATE_SUBSCRIPTION_NODE_ACK");
        return ackResource;
    }


}
