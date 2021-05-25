import java.io.FileInputStream;
import java.util.Properties;

/**
 * Created by Behnam Khazael on 2/22/2021.
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

    public static String getNumberOfComplexEvents() {
        loadFile();
        String numberOfComplexEvents;
        numberOfComplexEvents = mainProperties.getProperty("NUMBER_OF_COMPLEX_EVENTS");
        return numberOfComplexEvents;
    }

    public static String getSimulationSpaceWidth() {
        loadFile();
        String simulationSpaceWidth;
        simulationSpaceWidth = mainProperties.getProperty("SIMULATION_SPACE_WIDTH");
        return simulationSpaceWidth;
    }

    public static String getSimulationSpaceHeight() {
        loadFile();
        String simulationSpaceHeight;
        simulationSpaceHeight = mainProperties.getProperty("SIMULATION_SPACE_HEIGHT");
        return simulationSpaceHeight;
    }


    public static String getcomplexEventRadius() {
        loadFile();
        String complexEventRadius;
        complexEventRadius = mainProperties.getProperty("COMPLEX_EVENT_RADIUS");
        return complexEventRadius;
    }

    public static String getSensorsRadius() {
        loadFile();
        String sensorsRadius;
        sensorsRadius = mainProperties.getProperty("SENSOR_RADIUS");
        return sensorsRadius;
    }

    public static String getSimulationStartTime() {
        loadFile();
        String simulationStartTime;
        simulationStartTime = mainProperties.getProperty("SIMULATION_START_TIME");
        return simulationStartTime;
    }

    public static String getComplexEventActiveTime() {
        loadFile();
        String complexEventActiveTime;
        complexEventActiveTime = mainProperties.getProperty("COMPLEX_EVENT_ACTIVE_TIME");
        return complexEventActiveTime;
    }

    public static String getTimeToStartVisualizingTheWorkingSpace() {
        loadFile();
        String timeToStartVisualizingTheWorkingSpace;
        timeToStartVisualizingTheWorkingSpace = mainProperties.getProperty("TIME_TO_START_VISUALIZING_THE_WORKING_SPACE");
        return timeToStartVisualizingTheWorkingSpace;
    }

    public static String getSimulationStartSubmitRuleTime() {
        loadFile();
        String simulationStartSubmitRuleTime;
        simulationStartSubmitRuleTime = mainProperties.getProperty("SIMULATION_START_SUBMIT_RULE_TIME");
        return simulationStartSubmitRuleTime;
    }

    public static String getSmartCityAppServerURL() {
        loadFile();
        String smartCityAppServerURL;
        smartCityAppServerURL = mainProperties.getProperty("SMART_CITY_APP_SERVER_URL");
        return smartCityAppServerURL;
    }

    public static String getSmartCityAppServerPort() {
        loadFile();
        String smartCityAppServerPort;
        smartCityAppServerPort = mainProperties.getProperty("SMART_CITY_APP_SERVER_PORT");
        return smartCityAppServerPort;
    }

    public static String getBrokerRuleProvisioningResource() {
        loadFile();
        String brokerRuleProvisioningResource;
        brokerRuleProvisioningResource = mainProperties.getProperty("BROKER_RULE_PROVISIONING_RESOURCE");
        return brokerRuleProvisioningResource;
    }

    public static String getSimulationConcurrentEvent() {
        loadFile();
        String simulationConcurrentEvent;
        simulationConcurrentEvent = mainProperties.getProperty("SIMULATION_CONCURRENT_EVENT");
        return simulationConcurrentEvent;
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

}
