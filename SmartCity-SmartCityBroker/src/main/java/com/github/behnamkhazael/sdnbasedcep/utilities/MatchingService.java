package com.github.behnamkhazael.sdnbasedcep.utilities;

import com.github.behnamkhazael.sdnbasedcep.entities.Publisher;
import com.github.behnamkhazael.sdnbasedcep.entities.Event;
import trex.common.Attribute;
import trex.common.Constraint;

import java.util.Collection;

/**
 * Created by Behnam Khazael on 2/6/2021.
 * Matches simple sevents that extracted from a rule by the publishers that already registered in the publishers table.
 * @author Behnam Khazael
 * @version 0.1
 */
public class MatchingService {
    private MatchingService(){}


    public static int match(Event event, Publisher publisher) {
        //first I must match event type
        if (event.getEventType() != publisher.getEventType()) return 0;
        //Then constraints
        Collection<Attribute> attrs = publisher.getAttributes();
        //Here comes a list of switches that handle all the value types and the operators.
        //This is just like the one found in the TRexServer project, in the TRexUtils.cpp file
        //TODO: Generalize QoS...
        for (Constraint constr : event.getConstraints()) {
            for (Attribute at : attrs) {
                if ("accuracy".equals(at.getName())){
                    if (constr.getName().equals(at.getName())) {
                        switch (constr.getValType()) {
                            case INT:
                                switch (constr.getOp()) {
                                    case EQ:
                                        //return -1;
                                        if (constr.getName().equals("accuracy")) {
                                            if (constr.getIntVal() == at.getIntVal()) {
                                                break;
                                            } else return 0;
                                        }
                                        break;

                                    case NE:
                                        //return -1;
                                        break;

                                    case GT:
                                        //return -1;
                                        if (constr.getName().equals("accuracy")) {
                                            if (constr.getIntVal() < at.getIntVal()) {
                                                break;
                                            } else return 0;
                                        }
                                        break;

                                    case LT:
                                        //return -1;
                                        if (constr.getName().equals("accuracy")) {
                                            if (constr.getIntVal() > at.getIntVal()) {
                                                break;
                                            } else return 0;
                                        }
                                        break;

                                    case LE:
                                        //return -1;
                                        if (constr.getName().equals("accuracy")) {
                                            if (constr.getIntVal() >= at.getIntVal()) {
                                                break;
                                            } else return 0;
                                        }
                                        break;

                                    case GE:
                                        //return -1;
                                        if (constr.getName().equals("accuracy")) {
                                            if (constr.getIntVal() <= at.getIntVal()) {
                                                break;
                                            } else return 0;
                                        }
                                        break;


                                    default:
                                        break;
                                }
                                break;

                            case FLOAT:
                                switch (constr.getOp()) {
                                    case EQ:
                                        //return -1;
                                        break;

                                    case NE:
                                        //return -1;
                                        break;

                                    case GT:
                                        //return -1;
                                        break;

                                    case LT:
                                        //return -1;
                                        break;

                                    case LE:
                                        //return -1;
                                        break;

                                    case GE:
                                        //return -1;
                                        break;

                                    default:
                                        break;
                                }
                                break;

                            case BOOL:
                                switch (constr.getOp()) {
                                    case EQ:
                                        //
                                        // break;return -1;

                                    case NE:
                                        //return -1;
                                        break;

                                    default:
                                        break;
                                }
                                break;

                            case STRING:
                                switch (constr.getOp()) {
                                    case EQ:
                                        if (constr.getName().equals("accuracy")) {
                                            if (Integer.parseInt(constr.getStringVal()) != Integer.parseInt(at.getStringVal())) {
                                                break;
                                            } else return 0;
                                        }
                                        if (!constr.getStringVal().equals(at.getStringVal())) return 0;
                                        break;

                                    case NE:
//                                    if(constr.getName().equals("accuracy")) {
//                                        if (Integer.parseInt(constr.getStringVal()) != Integer.parseInt(at.getStringVal()))
//                                            return 0;
//                                    }
                                        if (constr.getStringVal().equals(at.getStringVal())) return 0;
                                        break;

                                    //Not defined for strings
                                    case GT:
                                        if (constr.getName().equals("accuracy")) {
                                            if (Integer.parseInt(constr.getStringVal()) <= Integer.parseInt(at.getStringVal())) {
                                                break;
                                            } else return 0;
                                        }
                                        return 0;

                                    //Not defined for strings
                                    case LT:
                                        if (constr.getName().equals("accuracy")) {
                                            if (Integer.parseInt(constr.getStringVal()) >= Integer.parseInt(at.getStringVal())) {
                                                break;
                                            } else return 0;
                                        }
                                        return 0;

                                    case IN:
                                        //FROM SERVER CODE, TRexUtils.cpp
                                        // The constraint's value should be a substring of the attribute's value:
                                        // it is a filter specified for published events' attributes
                                        if (at.getStringVal().indexOf(constr.getStringVal()) < 0) return 0;
                                        break;

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
        }
        //And finally the custom matcher
//        if (subPkt.match(pkt) == 1) return 1;
//        else
        return -1;
    }
}
