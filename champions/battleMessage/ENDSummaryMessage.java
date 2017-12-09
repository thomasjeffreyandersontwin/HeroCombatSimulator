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
public class ENDSummaryMessage extends AbstractBattleMessage implements SummaryMessage{
    
    protected Target target;
    protected int endAmount;
    protected ENDSource endSource;
    protected int remaining;
    protected String reason;
    /**
     * Creates a new instance of EffectSummaryMessage
     */
    public ENDSummaryMessage(Target target, ENDSource endSource, int endAmount, int remaining, String reason) {
        this.target = target;
        this.endSource = endSource;
        this.endAmount = endAmount;
        this.remaining = remaining;
        this.reason = reason;
        
        setMessageIcon( UIManager.getIcon("AttackTree.appyEffectsIcon"));
    }

    public String getMessage() {
        return target.getName() + " used " + endAmount + " END using " + reason + ". " + remaining + " END Remains.";
    }

    public String getSummary() {
        
        return "used " + endAmount + " END (" + remaining + " END remains)";
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
