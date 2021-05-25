package com.github.behnamkhazael.sdnbasedcep.brokerserverservices;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

/**
 * Created by Behnam Khazael on 2/6/2021.
 * A simple server to let subscribers un-subscribe from a topic.
 * @author Behnam Khazael
 * @version 0.1
 */
public class UnSubscribingFromATopic implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

    }
}
