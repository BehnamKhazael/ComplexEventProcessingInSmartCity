package com.github.behnamkhazael.sdnbasedcep.config;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * Created by Behnam Khazael on 2/6/2021.
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

    public static String getControllerUrl(){
        loadFile();
        String controllerUrl;
        controllerUrl = mainProperties.getProperty("CONTROLLER_IP");
        return controllerUrl;
    }

    public static String getControllerPort(){
        loadFile();
        String controllerPort;
        controllerPort = mainProperties.getProperty("CONTROLLER_PORT");
        return controllerPort;
    }

    public static String getControllerProvisioningResource(){
        loadFile();
        String controllerProvisioningResource;
        controllerProvisioningResource = mainProperties.getProperty("CONTROLLER_RULE_PROVISIONING_RESOURCE");
        return controllerProvisioningResource;
    }

    public static String getSolution(){
        loadFile();
        String solution;
        solution = mainProperties.getProperty("SOLUTION");
        return solution;
    }

    public static String getNumberOfMotes(){
        loadFile();
        String numberOfMotes;
        numberOfMotes = mainProperties.getProperty("NUMBER_OF_MOTES");
        return numberOfMotes;
    }

    public static String getRun(){
        loadFile();
        String run;
        run = mainProperties.getProperty("RUN");
        return run;
    }
}
