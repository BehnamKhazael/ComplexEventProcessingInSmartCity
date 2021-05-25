package com.github.behnamkhazael.sdnbasedcep.utilities;

import com.github.behnamkhazael.sdnbasedcep.entities.Data;
import com.github.behnamkhazael.sdnbasedcep.entities.SingletonEngineTable;
import org.apache.logging.log4j.Logger;
import trex.common.Attribute;
import trex.packets.PubPkt;
import trexengine.TRexEngine;

import static org.apache.logging.log4j.LogManager.getLogger;
import static trex.common.Consts.ValType.INT;
import static trex.common.Consts.ValType.STRING;
import static trex.examples.RuleR0.EVENT_SMOKE;
import static trex.examples.RuleR0.EVENT_TEMP;

/**
 * Created by Behnam Khazael on 2/20/2021.
 * Process received notifications.
 * Its also a wrapper for T-Rex, could be updated as a general utility.
 * @author Behnam Khazael
 * @version 0.1
 */
public class ProcessMessage {
    private static final Logger logger = getLogger(ProcessMessage.class);
    private ProcessMessage(){}
    private static PubPkt processMessage(Data message){
        PubPkt pubPkt;
        switch (message.getTopic()){
            case "temp":

                // Temp event
                Attribute tempAttr[] = new Attribute[3];
                tempAttr[0] = new Attribute();
                tempAttr[1] = new Attribute();
                tempAttr[2] = new Attribute();
                // Value attribute
                tempAttr[0].setName("value");
                tempAttr[0].setValType(INT);
                tempAttr[0].setIntVal(Integer.parseInt(message.getValue()));
                // Area attribute
                tempAttr[1].setName("area");
                tempAttr[1].setValType(STRING);
                tempAttr[1].setStringVal(message.getArea());
                // QoS attribute
                tempAttr[2].setName("accuracy");
                tempAttr[2].setValType(INT);
                tempAttr[2].setIntVal(Integer.parseInt(message.getQoS()));
                pubPkt= new PubPkt(EVENT_TEMP, tempAttr, 3);
                pubPkt.setTimeStamp(Long.parseLong(message.getTime()));
                System.out.println("Temp: " + pubPkt.toString());
                return pubPkt;
            case "smoke":
                // Smoke event
                // Area attribute
                Attribute smokeAttr[] = new Attribute[1];
                smokeAttr[0] = new Attribute();
                smokeAttr[0].setName("area");
                smokeAttr[0].setValType(STRING);
                smokeAttr[0].setStringVal(message.getArea());
                pubPkt= new PubPkt(EVENT_SMOKE, smokeAttr, 1);
                pubPkt.setTimeStamp(Long.parseLong(message.getTime()));
                System.out.println("Smoke: " + pubPkt.toString());
                return pubPkt;
            default:
                pubPkt = new PubPkt();
                return pubPkt;
        }

    }

    public static synchronized void processPublicMessage(Data message, String topic, TRexEngine engine1){
        PubPkt pubPkt = processMessage(message);


        try {
            if (!SingletonEngineTable.getEngineTable().isEmpty()) {
                //SingletonEngineTable.getEngineTable().get(EVENT_FIRE).getEngine().processPubPkt(pubPkt);
                engine1.processPubPkt(pubPkt);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
