/*
 * EffectSummaryMessage.java
 *
 * Created on February 18, 2008, 2:38 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.battleMessage;

import champions.Target;
import champions.interfaces.ENDSource;
import javax.swing.UIManager;

/**
 *
 * @author twalker
 */
public class ChargesSummaryMessage extends ENDSummaryMessage implements SummaryMessage{
    

    /**
     * Creates a new instance of EffectSummaryMessage
     */
    public ChargesSummaryMessage(Target target, ENDSource endSource, int endAmount, int remaining, String reason) {
        super(target,endSource,endAmount,remaining,reason);
        
        setMessageIcon( UIManager.getIcon("AttackTree.appyEffectsIcon"));
    }

    public String getMessage() {
        String s1 = "charge";
        if ( endAmount > 1 ) {
            s1 = "charges";
        }
        
        String s2 = "charges remain";
        if ( endAmount == 1 ) {
            s2 = "charge remains";
        }
        
        
        return target.getName() + " used " + endAmount + " " + s1 + " using " + reason + ". " + remaining + " " + s2 + ".";
    }

    public String getSummary() {
        
        String s1 = "charge";
        if ( endAmount > 1 ) {
            s1 = "charges";
        }
        
        String s2 = "charges remain";
        if ( endAmount == 1 ) {
            s2 = "charge remains";
        }
        
        return "used " + endAmount + " " + s1 + " (" + remaining + " " + s2 + ")";
    }


    public SummaryMessage merge(SummaryMessage message2) {
        if ( message2 instanceof ENDSummaryMessage ) {
            ENDSummaryMessage esm = (ENDSummaryMessage)message2;
            if ( target == esm.getTarget() && endSource == esm.endSource) {
                int used = endAmount + esm.endAmount;
                ENDSummaryMessage newMessage = new ENDSummaryMessage(target, endSource, used, esm.remaining, null);
                return newMessage;
            }
        }
            
        return null;
    }

    public boolean isRelevant(Target relevantTarget) {
        return relevantTarget == target;
    }
    


    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        this.target = target;
    }


}
