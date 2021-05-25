package com.github.behnamkhazael.sdnbasedcep.loader;

import com.github.behnamkhazael.sdnbasedcep.brokerserverservices.*;
import com.sun.net.httpserver.HttpServer;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import static org.apache.logging.log4j.LogManager.getLogger;

/**
 * Created by Behnam Khazael on 2/6/2021.
 * Loads brokers from the config and starts their services.
 * @author Behnam Khazael
 * @version 0.1
 */
public class BrokerServerLoader {
    private static final Logger logger = getLogger(BrokerServerLoader.class);
    private static final String HOSTNAME = "0.0.0.0";
    private static final int PORT = 8001; //read from config
    private static final int BACKLOG = 0;
    public static void main(String args[]) throws IOException {

        HttpServer server = HttpServer.create(new InetSocketAddress(HOSTNAME, PORT), BACKLOG);
        server.createContext("/cep/check-rule", new RuleChecker());
        server.createContext("/cep/remove-rule", new RuleRemover());
        server.createContext("/cep/register", new PublisherRegisterATopic());
        server.createContext("/cep/un-register", new PublisherUnRegisterFromATopic());
        server.createContext("/cep/subscribe", new SubscribingToATopic());
        server.createContext("/cep/un-subscribe", new UnSubscribingFromATopic());
        server.createContext("/cep/publish-call-back", new PublishListener());
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        System.out.println("Smart City Broker is running....");
    }
}
