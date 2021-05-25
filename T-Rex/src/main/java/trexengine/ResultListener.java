package trexengine;


import trex.common.Attribute;
import trex.common.Constraint;
import trex.packets.PubPkt;
import trex.packets.SubPkt;


import java.nio.ByteBuffer;
import java.util.*;



/**
 * Created by sony on 2/3/2020.
 */

public class ResultListener {


    private HashSet<SubPkt> subs= new HashSet<>();

    public ResultListener(SubPkt subPkt){
        subs.add(subPkt);
    }

    void handleResult(Set<PubPkt> genPkts, double procTime){
        for (PubPkt b :
             genPkts) {
            for (SubPkt s :
                    subs) {
                if (match(s,b) != 0){
                    final Long time = System.nanoTime();
                    final Long time2 = System.currentTimeMillis();
                }
            }
        }
    }
    public int match(SubPkt subPkt, PubPkt pkt) {
        //first I must match event type
        if (subPkt.getEventType() != pkt.getEventType()) return 0;
        //Then constraints
        Collection<Attribute> attrs = pkt.getAttributes();
        //Here comes a list of switches that handle all the value types and the operators.
        //This is just like the one found in the TRexServer project, in the TRexUtils.cpp file
        for (Constraint constr : subPkt.getConstraints()) {
            for (Attribute at : attrs) {
                if (constr.getName().equals(at.getName())) {
                    switch (constr.getValType()) {
                        case INT:
                            switch (constr.getOp()) {
                                case EQ:
                                    if (constr.getIntVal() != at.getIntVal()) return 0;

                                case NE:
                                    if (constr.getIntVal() == at.getIntVal()) return 0;

                                case GT:
                                    if (constr.getIntVal() <= at.getIntVal()) return 0;

                                case LT:
                                    if (constr.getIntVal() >= at.getIntVal()) return 0;

                                case LE:
                                    if (constr.getIntVal() > at.getIntVal()) return 0;

                                case GE:
                                    if (constr.getIntVal() < at.getIntVal()) return 0;
                                default:
                                    break;
                            }
                            break;

                        case FLOAT:
                            switch (constr.getOp()) {
                                case EQ:
                                    if (constr.getFloatVal() != at.getFloatVal()) return 0;

                                case NE:
                                    if (constr.getFloatVal() == at.getFloatVal()) return 0;

                                case GT:
                                    if (constr.getFloatVal() <= at.getFloatVal()) return 0;

                                case LT:
                                    if (constr.getFloatVal() >= at.getFloatVal()) return 0;

                                case LE:
                                    if (constr.getFloatVal() > at.getFloatVal()) return 0;

                                case GE:
                                    if (constr.getFloatVal() < at.getFloatVal()) return 0;

                                default:
                                    break;
                            }
                            break;

                        case BOOL:
                            switch (constr.getOp()) {
                                case EQ:
                                    if (constr.getBoolVal() != at.getBoolVal()) return 0;

                                case NE:
                                    if (constr.getBoolVal() == at.getBoolVal()) return 0;

                                default:
                                    break;
                            }
                            break;

                        case STRING:
                            switch (constr.getOp()) {
                                case EQ:
                                    if (!constr.getStringVal().equals(at.getStringVal())) return 0;

                                case NE:
                                    if (constr.getStringVal().equals(at.getStringVal())) return 0;

                                    //Not defined for strings
                                case GT:
                                    return 0;

                                //Not defined for strings
                                case LT:
                                    return 0;

                                case IN:
                                    //FROM SERVER CODE, TRexUtils.cpp
                                    // The constraint's value should be a substring of the attribute's value:
                                    // it is a filter specified for published events' attributes
                                    if (at.getStringVal().indexOf(constr.getStringVal()) < 0) return 0;

                                default:
                                    break;
                            }
                            break;

                        default:
                            break;
                    }
                }
            }
        }
        //And finally the custom matcher
        if (subPkt.match(pkt) == 1) return 1;
        else return -1;
    }

}
