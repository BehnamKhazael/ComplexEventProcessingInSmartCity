package com.github.behnamkhazael.sdnbasedcep.brokerserverservices;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

/**
 * Created by Behnam Khazael on 2/6/2021.
 * This simple server to the publishers un-register themselves in the Brokers.
 * @author Behnam Khazael
 * @version 0.1
 */
public class PublisherUnRegisterFromATopic implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

    }
}
