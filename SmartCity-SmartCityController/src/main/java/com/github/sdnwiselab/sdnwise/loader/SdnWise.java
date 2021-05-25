/* 
 * Copyright (C) 2015 SDN-WISE
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.sdnwiselab.sdnwise.loader;

import com.github.sdnwiselab.sdnwise.configuration.Configurator;
import com.github.sdnwiselab.sdnwise.controller.Controller;
import com.github.sdnwiselab.sdnwise.controller.ControllerFactory;
import com.github.sdnwiselab.sdnwise.entities.ProcessingNode;
import com.github.sdnwiselab.sdnwise.function.AddToTheListOfProcessingNodes;
import com.github.sdnwiselab.sdnwise.function.PublisherHeadSelection;
import com.github.sdnwiselab.sdnwise.function.SubscriptionAckListener;
import com.github.sdnwiselab.sdnwise.function.UpdateTheListOfProcessingNodes;
import com.sun.net.httpserver.HttpServer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.apache.logging.log4j.LogManager.getLogger;

/**
 * SdnWise class of the SDN-WISE project. It loads the configuration file and
 * starts the the Controller.
 *
 * @author Sebastiano Milardo
 * @author Behnam Khazael
 * @version 0.2
 */
public class SdnWise {

    /**
     * Starts the components of the SDN-WISE Controller.
     *
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        new SdnWise().startExample();
    }
    private static final org.apache.logging.log4j.Logger logger = getLogger(SdnWise.class);


    private static final String HOSTNAME = "0.0.0.0";
    private static final int PORT = 8002; //read from config
    private static final int BACKLOG = 0;
    private Controller controller;
    public static Controller staticRefrence;
    public static final ArrayList<ProcessingNode> NODE_ADDRESS_PROCESSING_NODE_HASHTABLE = new ArrayList<ProcessingNode>();


    /**
     * Starts the Controller layer of the SDN-WISE network. The path to the
     * configurations are specified in the configFilePath String. The options to
     * be specified in this file are: a "lower" Adapter, in order to communicate
     * with the flowVisor (See the Adapter javadoc for more info), an
     * "algorithm" for calculating the shortest path in the network. The only
     * supported at the moment is "DIJKSTRA". A "map" which contains
     * informations regarding the "TIMEOUT" in order to remove a non responding
     * node from the topology, a "RSSI_RESOLUTION" value that triggers an event
     * when a link rssi value changes more than the set threshold.
     *
     * @param configFilePath a String that specifies the path to the
     * configuration file.
     * @return the Controller layer of the current SDN-WISE network.
     */
    public Controller startController(String configFilePath) {
        InputStream configFileURI = null;
        if (configFilePath == null || configFilePath.isEmpty()) {
            configFileURI = this.getClass().getResourceAsStream("/config.ini");
        } else {
            try {
                configFileURI = new FileInputStream(configFilePath);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(SdnWise.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Configurator conf = Configurator.load(configFileURI);
        controller = new ControllerFactory().getController(conf.getController());
        new Thread(controller).start();
        return controller;
    }

    /**
     * Starts the servers for the services that needed for smart city complex event processing
     * @throws IOException
     */
    public void startExample() throws IOException {
        controller = startController("");
        staticRefrence  = controller;
        HttpServer server = HttpServer.create(new InetSocketAddress(HOSTNAME, PORT), BACKLOG);
        server.createContext("/controller/head-selection", new PublisherHeadSelection());
        server.createContext("/controller/add-processing-node", new AddToTheListOfProcessingNodes());
        server.createContext("/controller/update-processing-node", new UpdateTheListOfProcessingNodes());
        server.createContext("/controller/subscriptionAckListener", new SubscriptionAckListener());
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        System.out.println("SDN-WISE Controller running....");

    }
}
