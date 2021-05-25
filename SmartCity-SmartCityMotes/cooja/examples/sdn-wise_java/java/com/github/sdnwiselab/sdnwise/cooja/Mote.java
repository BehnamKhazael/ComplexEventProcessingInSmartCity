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
package com.github.sdnwiselab.sdnwise.cooja;

import com.github.sdnwiselab.sdnwise.flowtable.FlowTableEntry;
import com.github.sdnwiselab.sdnwise.flowtable.ForwardUnicastAction;
import com.github.sdnwiselab.sdnwise.flowtable.Window;
import com.github.sdnwiselab.sdnwise.packet.BeaconPacket;
import com.github.sdnwiselab.sdnwise.packet.ConfigPacket;
import com.github.sdnwiselab.sdnwise.packet.DataPacket;
import com.github.sdnwiselab.sdnwise.packet.NetworkPacket;
import com.github.sdnwiselab.sdnwise.util.NodeAddress;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.logging.log4j.Logger;
import org.contikios.cooja.MoteType;
import org.contikios.cooja.Simulation;

import java.nio.charset.Charset;
import java.util.Hashtable;
import java.util.concurrent.ArrayBlockingQueue;

import static com.github.sdnwiselab.sdnwise.cooja.Constants.SDN_WISE_DFLT_CNT_SLEEP_MAX;
import static com.github.sdnwiselab.sdnwise.cooja.LoadConfig.getNumberOfMotes;
import static com.github.sdnwiselab.sdnwise.cooja.LoadConfig.getRun;
import static com.github.sdnwiselab.sdnwise.cooja.LoadConfig.getSolution;
import static com.github.sdnwiselab.sdnwise.flowtable.Window.*;
import static com.github.sdnwiselab.sdnwise.packet.NetworkPacket.SDN_WISE_DST_H;
import static org.apache.logging.log4j.LogManager.getLogger;

/**
 * Represents a mote within Cooja simulator.
 * @author Sebastiano Milardo
 * @author Behnam Khazael
 * @version 0.2
 */
public class Mote extends AbstractMote {

    private static final Logger logger = getLogger(Mote.class);
    protected volatile boolean registered = false;
    ArrayBlockingQueue<NodeAddress> nodeAddresses = new ArrayBlockingQueue<NodeAddress>(100);
    Hashtable<String, ArrayBlockingQueue<NodeAddress>> subscribersTable = new Hashtable<String, ArrayBlockingQueue<NodeAddress>>();


    public Mote() {
        super();
    }

    public Mote(MoteType moteType, Simulation simulation) {
        super(moteType, simulation);
    }

    @Override
    public final void initSdnWise() {
        super.initSdnWise();
        commonConstructor();
        setDistanceFromSink(ttl_max + 1);
        setRssiSink(0);
        setSemaphore(0);
    }

    @Override
    public void SDN_WISE_Callback(DataPacket packet) {
        numberOfReceivedDataPackets++;
        battery.receiveRadio(packet.getLen());
        try {
            Gson gson = new Gson();
            DataMessage dataMessage = gson.fromJson(new String(packet.getPayload(), Charset.forName("UTF-8")), DataMessage.class);
            switch (dataMessage.getType()) {
                case "reg":
                    runFlowMatch(packet);
                    break;
                case "sub":
                    log("Subscription request received!");
                    String subscriber = dataMessage.getData().get("requester").toString().replaceAll("\"", "");
                    Long submittedTime = dataMessage.getData().get("time").getAsLong();
                    Long time = System.currentTimeMillis();
                    String topic = dataMessage.getData().get("topic").toString().replaceAll("\"", "");
                    if (!nodeAddresses.contains(new NodeAddress(subscriber)))
                        nodeAddresses.add(new NodeAddress(subscriber));
                    subscribersTable.putIfAbsent(topic, nodeAddresses);
                    log("topic: " + topic + ", subscriber address: " + subscriber);
                    log("****** Subscription request Registered! ******");
                    //Ack to subscribe message
                    DataPacket dataPacket = new DataPacket(net_id, addr, packet.getSrc());
                    JsonObject message = new JsonObject();
                    JsonObject messageInner = new JsonObject();
                    message.addProperty("type", "subResponse");
                    messageInner.addProperty("nodeAddress", addr.toString());
                    message.add("data", messageInner);
                    dataPacket.setPayload((message.toString()).getBytes(Charset.forName("UTF-8")));
                    runFlowMatch(dataPacket);
                    radioTX(dataPacket);
                    JsonObject object = new JsonObject();
                    object.addProperty("Solution", getSolution());
                    object.addProperty("NumberOfNodes", getNumberOfMotes());
                    object.addProperty("Run", getRun());
                    object.addProperty("subscriptionDelay", time - submittedTime);
                    object.addProperty("pubAddress", addr.toString());
                    object.addProperty("request", dataMessage.toString());
                    object.addProperty("time", time);
                    object.addProperty("Action", "SUBSCRIPTION_DELAY");
                    logger.info(object.toString());
                    break;
                case "response":
                    registered = true;
                    log("Registered turned into: " + getID() + " " + String.valueOf(registered));
                    break;
                default:
                    log("can not interpret received data packet: " + dataMessage.toString());
            }
        } catch (Exception e) {
            System.out.println("****************************************----********");
            e.printStackTrace();
        }
        if (this.functions.get(1) == null) {
            log(new String(packet.getPayload(), Charset.forName("UTF-8")));
            packet.setSrc(addr)
                    .setDst(getActualSinkAddress())
                    .setTtl((byte) ttl_max);
            runFlowMatch(packet);
        } else {
            this.functions.get(1).function(adcRegister,
                    flowTable,
                    neighborTable,
                    statusRegister,
                    acceptedId,
                    flowTableQueue,
                    txQueue,
                    0,
                    0,
                    0,
                    packet);
        }

    }

    @Override
    public void rxBeacon(BeaconPacket bp, int rssi) {
        if (rssi > rssi_min) {
            if (bp.getDist() < this.getDistanceFromSink()
                    && (rssi > getRssiSink())) {
                this.setSemaphore(1);
                FlowTableEntry toSink = new FlowTableEntry();
                toSink.addWindow(new Window()
                        .setOperator(SDN_WISE_EQUAL)
                        .setSize(SDN_WISE_SIZE_2)
                        .setLhsLocation(SDN_WISE_PACKET)
                        .setLhs(SDN_WISE_DST_H)
                        .setRhsLocation(SDN_WISE_CONST)
                        .setRhs(bp.getSinkAddress().intValue()));
                toSink.addWindow(Window.fromString("P.TYPE > 127"));
                toSink.addAction(new ForwardUnicastAction()
                        .setNextHop(bp.getSrc()));
                flowTable.set(0, toSink);

                setDistanceFromSink(bp.getDist() + 1);
                setRssiSink(rssi);
            } else if ((bp.getDist() + 1) == this.getDistanceFromSink()
                    && getNextHopVsSink().equals(bp.getSrc())) {
                flowTable.get(0).getStats().restoreTtl();
                flowTable.get(0).getWindows().get(0)
                        .setRhs(bp.getSinkAddress().intValue());
            }
            super.rxBeacon(bp, rssi);
        }
    }

    @Override
    public final void controllerTX(NetworkPacket pck) {
        pck.setNxhop(getNextHopVsSink());
        radioTX(pck);
    }

    @Override
    public void rxConfig(ConfigPacket packet) {
        NodeAddress dest = packet.getDst();
        if (!dest.equals(addr)) {
            runFlowMatch(packet);
        } else {
            if (this.marshalPacket(packet) != 0) {
                packet.setSrc(addr);
                packet.setDst(getActualSinkAddress());
                packet.setTtl((byte) ttl_max);
                runFlowMatch(packet);
            }
        }
    }

    private void commonConstructor() {
        cnt_sleep_max = SDN_WISE_DFLT_CNT_SLEEP_MAX;
    }

    @Override
    final void resetSemaphore() {
        setSemaphore(0);
        setDistanceFromSink(255);
    }


    private class DataMessage {
        private String type;
        private JsonObject data;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public JsonObject getData() {
            return data;
        }

        public void setData(JsonObject data) {
            this.data = data;
        }

        @Override
        public String toString() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("type", this.getType());
            jsonObject.add("data", this.getData());
            return jsonObject.toString();
        }
    }


}
