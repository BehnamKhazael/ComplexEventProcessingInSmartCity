package com.github.behnamkhazael.sdnbasedcep.config;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * Created by Behnam Khazael on 2/2/2021.
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
            String path = "./config.properties";

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

    public static String getLoggerDBPassword(){
        loadFile();
        String loggerDBPassword;
        loggerDBPassword = mainProperties.getProperty("DB_PASSWORD");
        return loggerDBPassword;
    }

    public static String getLoggerDBIp(){
        loadFile();
        String loggerDBIp;
        loggerDBIp = mainProperties.getProperty("DB_IP");
        return loggerDBIp;
    }

    public static String getBrokerUrl(){
        loadFile();
        String loggerBrokerIp;
        loggerBrokerIp = mainProperties.getProperty("BROKER_IP");
        return loggerBrokerIp;
    }

    public static String getBrokerPort(){
        loadFile();
        String loggerBrokerPort;
        loggerBrokerPort = mainProperties.getProperty("BROKER_PORT");
        return loggerBrokerPort;
    }

    public static String getBrokerCheckRuleResource(){
        loadFile();
        String loggerBrokerCheckRuleUri;
        loggerBrokerCheckRuleUri = mainProperties.getProperty("BROKER_CHECK_RULE_RESOURCE");
        return loggerBrokerCheckRuleUri;
    }


    public static String getBrokerSubscribeResource(){
        loadFile();
        String loggerBrokerSubscribeUri;
        loggerBrokerSubscribeUri = mainProperties.getProperty("BROKER_SUBSCRIBE_RESOURCE");
        return loggerBrokerSubscribeUri;
    }

    public static String getBrokerAddRuleResource(){
        loadFile();
        String loggerBrokerAddRule;
        loggerBrokerAddRule = mainProperties.getProperty("BROKER_ADD_RULE_RESOURCE");
        return loggerBrokerAddRule;
    }

    public static String getSolution(){
        loadFile();
        String loggerSolution;
        loggerSolution = mainProperties.getProperty("SOLUTION");
        return loggerSolution;
    }

    public static String getNumberOfMotes(){
        loadFile();
        String loggerNumberOfMotes;
        loggerNumberOfMotes = mainProperties.getProperty("NUMBER_OF_MOTES");
        return loggerNumberOfMotes;
    }

    public static String getRun(){
        loadFile();
        String loggerRun;
        loggerRun = mainProperties.getProperty("RUN");
        return loggerRun;
    }
}
