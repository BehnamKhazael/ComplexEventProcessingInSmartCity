package com.github.behnamkhazael.sdnbasedcep.loader;


import com.github.behnamkhazael.sdnbasedcep.applicationserverservices.RuleRegistrator;
import com.github.behnamkhazael.sdnbasedcep.applicationserverservices.RuleRemover;
import com.github.behnamkhazael.sdnbasedcep.applicationserverservices.SubscribingToATopic;
import com.github.behnamkhazael.sdnbasedcep.applicationserverservices.UnSubscribingFromATopic;
import com.sun.net.httpserver.HttpServer;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import static org.apache.logging.log4j.LogManager.getLogger;

/**
 * Created by Behnam Khazael on 2/2/2021.
 * Starts the server and listen for incoming requests.
 * @author Behnam Khazael
 * @version 0.1
 */
public class ApplicationServerLoader {

    /*
    This is the curl command that can be used for calling this
curl -X POST -H "Content-Type: application/json" -d "{"uuid":"0000","callBackURL":"localhost","topic":"13","rule":"Assign 1=>O3,2=>NO2,3=>airQuality Define airQuality(area:Geometry,measuredO3:Int,measuredNO2:Int) From O3(area=>$a) and each NO2([Geometry]areaINTERSECT$a,value>45) within 300000 from O3 Where area:=O3.areaINTERSECTNO2.area,measuredO3:=O3.value,measuredNO2:=NO2.value"}" http://localhost:8000/cep/register


curl -X POST -H "Content-Type: application/json" -d "{"uuid": "0000","callBackURL": "localhost","topic": "13","rule": "Assign 2000=>Smoke,2001=>Temp,2100=>Fire Define Fire(area:String,mTemp:Float) From Smoke(area=>$a)and Each Temp([String]area=$a,value>45)within 5from Smoke Where area:=Smoke.area,mTemp:=Temp.value;"}" http://localhost:8000/cep/register


{
	"uuid": "0000",
	"callBackURL": "localhost",
	"topic": "13",
	"rule": "Assign 2000=>Smoke,2001=>Temp,2100=>Fire Define Fire(area:String,mTemp:Float) From Smoke(area=>$a)and Each Temp([String]area=$a,value>45)within 5from Smoke Where area:=Smoke.area,mTemp:=Temp.value;"
}
     */



    private static final Logger logger = getLogger(ApplicationServerLoader.class);
    private static final String HOSTNAME = "0.0.0.0";
    private static final int PORT = 8000;
    private static final int BACKLOG = 0;

    public static void main(String args[]) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(HOSTNAME, PORT), BACKLOG);
        server.createContext("/cep/register", new RuleRegistrator());
        server.createContext("/cep/remove", new RuleRemover());
        server.createContext("/cep/subscribe", new SubscribingToATopic());
        server.createContext("/cep/un-subscribe", new UnSubscribingFromATopic());
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        System.out.println("Application Server is running...");
    }
}
