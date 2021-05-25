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
package com.github.sdnwiselab.sdnwise.controller;

import com.github.sdnwiselab.sdnwise.adapter.Adapter;
import com.github.sdnwiselab.sdnwise.packet.NetworkPacket;
import com.github.sdnwiselab.sdnwise.topology.NetworkGraph;
import com.github.sdnwiselab.sdnwise.util.NodeAddress;
import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.Node;

import java.util.LinkedList;

/**
 * This class implements the Controller class using the Dijkstra routing
 * algorithm in order to find the shortest path between nodes. When a request
 * from the network is sent, this class sends a SDN_WISE_OPEN_PATH message with
 * the shortest path. No action is taken if the topology of the network changes.
 *
 * @author Sebastiano Milardo
 * @version 0.1
 */
public class ControllerDijkstra extends Controller {

    private final Dijkstra dijkstra;
    private String lastSource = "";
    private long lastModification = -1;
    private final Dijkstra dijkstraPath;
    private String lastSourcePath = "";
    private long lastModifiedPath = -1;

    /*
     * Constructor method fo ControllerDijkstra.
     * 
     * @param id ControllerId object.
     * @param lower Lower Adpater object.
     * @param networkGraph NetworkGraph object.
     */
    public ControllerDijkstra(Adapter lower, NetworkGraph networkGraph) {
        super(lower, networkGraph);
        this.dijkstra = new Dijkstra(Dijkstra.Element.EDGE, null, "length");
        this.dijkstraPath = new Dijkstra(Dijkstra.Element.EDGE, null, "length");
    }

    @Override
    public final void graphUpdate() {

    }

    @Override
    public final void manageRoutingRequest(NetworkPacket data) {

        String destination = data.getNetId() + "." + data.getDst();
        String source = data.getNetId() + "." + data.getSrc();

        if (!source.equals(destination)) {

            Node sourceNode = networkGraph.getNode(source);
            Node destinationNode = networkGraph.getNode(destination);
            LinkedList<NodeAddress> path = null;

            if (sourceNode != null && destinationNode != null) {

                if (!lastSource.equals(source) || lastModification != networkGraph.getLastModification()) {
                    results.clear();
                    dijkstra.init(networkGraph.getGraph());
                    dijkstra.setSource(networkGraph.getNode(source));
                    dijkstra.compute();
                    lastSource = source;
                    lastModification = networkGraph.getLastModification();
                } else {
                    path = results.get(data.getDst());
                }
                if (path == null) {
                    path = new LinkedList<>();
                    for (Node node : dijkstra.getPathNodes(networkGraph.getNode(destination))) {
                        path.push((NodeAddress) node.getAttribute("nodeAddress"));
                    }
                    System.out.println("[CTRL]: " + path);
                    results.put(data.getDst(), path);
                }
                if (path.size() > 1) {
                    sendPath((byte) data.getNetId(), path.getFirst(), path);

                    data.unsetRequestFlag();
                    data.setSrc(getSinkAddress(data.getNetId()));
                    sendNetworkPacket(data);

                } else {
                    // TODO send a rule in order to say "wait I dont have a path"
                    //sendMessage(data.getNetId(), data.getDst(),(byte) 4, new byte[10]);
                }
            }
        }
    }

    @Override
    public void setupNetwork() {

    }



    public final LinkedList<NodeAddress> getShortestPathTo(NodeAddress source, NodeAddress destination, int net){

        String des = destination.getHigh() + "." + destination;
        String src = source.getHigh() + "." + source;

        if (!src.equals(des)) {

            Node sourceNode = networkGraphPath.getNode(src);
            Node destinationNode = networkGraphPath.getNode(des);
            LinkedList<NodeAddress> path = null;

            if (sourceNode != null && destinationNode != null) {

                if (!lastSourcePath.equals(src) || lastModifiedPath != networkGraphPath.getLastModification()) {
                    paths.clear();
                    dijkstraPath.init(networkGraphPath.getGraph());
                    dijkstraPath.setSource(networkGraphPath.getNode(src));
                    dijkstraPath.compute();
                    lastSourcePath = src;
                    lastModifiedPath = networkGraphPath.getLastModification();
                } else {
                    path = paths.get(destination);
                }
                if (path == null) {
                    path = new LinkedList<>();
                    for (Node node : dijkstraPath.getPathNodes(networkGraphPath.getNode(des))) {
                        path.push((NodeAddress) node.getAttribute("nodeAddress"));
                    }
                    System.out.println("[PATH]: " + path);
                    paths.put(destination, path);
                }
                if (path.size() > 1) {
                    return path;

                } else {
                    // TODO send a rule in order to say "wait I dont have a path"
                    //sendMessage(data.getNetId(), data.getDst(),(byte) 4, new byte[10]);
                }
            }
        }
        return new LinkedList<>();
    }

}
