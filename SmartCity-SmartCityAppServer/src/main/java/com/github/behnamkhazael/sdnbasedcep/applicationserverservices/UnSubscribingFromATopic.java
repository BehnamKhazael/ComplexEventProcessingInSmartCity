package com.github.behnamkhazael.sdnbasedcep.applicationserverservices;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

/**
 * Created by Behnam Khazael on 2/5/2021.
 * A HTTP server to let Apps un-subscribe from a topic.
 * @author Behnam Khazael
 * @version 0.1
 */
public class UnSubscribingFromATopic implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

    }
}
