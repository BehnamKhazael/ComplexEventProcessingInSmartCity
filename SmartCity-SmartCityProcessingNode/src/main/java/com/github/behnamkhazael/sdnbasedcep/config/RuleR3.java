package com.github.behnamkhazael.sdnbasedcep.config;

import trex.common.Constraint;
import trex.common.EventTemplate;
import trex.common.OpTree;
import trex.common.RulePktValueReference;
import trex.packets.RulePkt;

import static trex.common.Consts.CompKind.EACH_WITHIN;
import static trex.common.Consts.ConstraintOp.GT;
import static trex.common.Consts.ConstraintOp.IN;
import static trex.common.Consts.ConstraintOp.LT;
import static trex.common.Consts.StateType.STATE;
import static trex.common.Consts.ValType.INT;
import static trex.common.Consts.ValType.STRING;
import static trex.examples.RuleR0.EVENT_FIRE;
import static trex.examples.RuleR0.EVENT_SMOKE;
import static trex.examples.RuleR0.EVENT_TEMP;

/**
 * Created by Behnam Khazael on 4/6/2021.
 * Represents a rule.
 */
public class RuleR3 {
    public RulePkt buildRule(){
        RulePkt rule= new RulePkt(false);

        int indexPredSmoke= 0;
        int indexPredTemp= 1;

        Long fiveMin = 1000L*60L*5L;

        // Smoke root predicate
        // Fake constraint as a temporary workaround to an engine's bug
        // FIXME remove workaround when bug fixed
        Constraint fakeConstr[] = new Constraint[1];
        fakeConstr[0] = new Constraint();
//        fakeConstr[1] = new Constraint();
        fakeConstr[0].setName("area");
        fakeConstr[0].setValType(STRING);
        fakeConstr[0].setOp(IN);
        fakeConstr[0].setStringVal("");
        rule.addRootPredicate(EVENT_SMOKE, fakeConstr, 1);


        // Temp predicate
        // Constraint: Temp.value > 45
        Constraint tempConstr[] = new Constraint[2];
        tempConstr[0] = new Constraint();
        tempConstr[0].setName("value");
        tempConstr[0].setValType(INT);
        tempConstr[0].setOp(GT);
        tempConstr[0].setIntVal(20);
        tempConstr[1] = new Constraint();
        tempConstr[1].setName("accuracy");
        tempConstr[1].setValType(INT);
        tempConstr[1].setOp(LT);
        tempConstr[1].setIntVal(5);
        rule.addPredicate(EVENT_TEMP, tempConstr, 2, indexPredSmoke, fiveMin, EACH_WITHIN);

        // Parameter: Smoke.area=Temp.area
        rule.addParameterBetweenStates(indexPredSmoke, "area", indexPredTemp, "area");

        // Fire template
        EventTemplate fireTemplate= new EventTemplate(EVENT_FIRE);
        // Area attribute in template
        OpTree areaOpTree= new OpTree(new RulePktValueReference(indexPredSmoke, STATE, "area"), STRING);
        fireTemplate.addAttribute("area", areaOpTree);
        // MeasuredTemp attribute in template
        OpTree measuredTempOpTree= new OpTree(new RulePktValueReference(indexPredTemp, STATE, "value"), INT);
        fireTemplate.addAttribute("measuredtemp", measuredTempOpTree);

        rule.setEventTemplate(fireTemplate);

        return rule;
    }
}
