package trex.examples;

import trex.common.Consts;
import trex.common.EventTemplate;
import trex.marshalling.BufferedPacketUnmarshaller;
import trex.marshalling.Marshaller;
import trex.marshalling.Unmarshaller;
import trex.packets.RulePkt;
import trex.packets.TRexPkt;
import trex.ruleparser.TRexRuleParser;
import trex.utils.MutableInt;
import com.sun.javafx.event.CompositeEventTargetImpl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;


import static trex.packets.TRexPkt.PacketType.RULE_PACKET;

/**
 * Created by sony on 1/28/2020.
 */
public class RuleTester {
    static String teslaRule;
    private BufferedPacketUnmarshaller unmarshaller;

    public static void main(String[] args) throws IOException {
        teslaRule = "Assign 2000=>Smoke," +
                "2001=>Temp," +
                "2100=>Fire " +
                "Define Fire(area:string,measuredTemp:float)" +
                "From Smoke(area=>$a)" +
                "and each Temp( [ string ] area=$a,value>45)within" +
                " 300000from Smoke" +
                " Where area := Smoke.area,measuredTemp := Temp.value;";
//        teslaRule = "Assign 2000=>Smoke," +
//                "" +
//                "2100=>Fire " +
//                "Define Fire(area:string)" +
//                "From Smoke(area=\"$a\")" +
//                "" +
//                "" +
//                " Where area:=\"$a\";";
        RulePkt rule = null;
        try {
            rule = TRexRuleParser.parse(teslaRule, 0);
        }catch (Exception e){
            e.printStackTrace();
        }
        byte[] bytes;
        bytes = Marshaller.marshalRule(rule, Consts.EngineType.CPU);
        RulePkt unMarshalled = (RulePkt) Unmarshaller.unmarshal(bytes, new MutableInt(0));
        System.out.println(unMarshalled.toString());
//        unMarshalled.getPredicates().values().stream()
//                .forEach(System.out::println);
        //System.out.println(Arrays.asList(unMarshalled.getPredicates()));
    }


}
