/*
 * EffectmessageMessage.java
 *
 * Created on February 18, 2008, 2:38 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.battleMessage;

import champions.ChampionsUtilities;
import champions.Target;
import javax.swing.UIManager;

/**
 *
 * @author twalker
 */
public class CVMultiplerMessage extends SimpleBattleMessage implements SummaryMessage {
    
    private double multiplier;
    private boolean added;
    private String cvType;
    
    /**
     * Creates a new instance of EffectmessageMessage
     */
    public CVMultiplerMessage(Target target, boolean added, String cvType, double multiplier) {
        super(target, null);
        
        this.cvType = cvType;
        this.multiplier = multiplier;
        this.added = added;
        
        setMessageIcon( UIManager.getIcon("AttackTree.summaryIcon"));
    }

    public String getMessage() {
        return getTarget().getName() + " " + getSummary() + ".";
    }


    public String getSummary() {
        
        
        if ( added ) {
            return "is at " + ChampionsUtilities.toStringWithFractions(multiplier) + " " + cvType;
        }
        else {
            return "is no longer at " + ChampionsUtilities.toStringWithFractions(multiplier) + " " + cvType;
        }
    }

    public boolean isRelevant(Target relevantTarget) {
        return getTarget() == relevantTarget;
    }
    


    public SummaryMessage merge(SummaryMessage message2) {
        if ( message2 instanceof CVMultiplerMessage) {
            CVMultiplerMessage msg = (CVMultiplerMessage)message2;
            
            if ( getTarget() == msg.getTarget() && added == msg.added && cvType.equals(msg.cvType) ) {
                double value = multiplier * msg.multiplier;
                if ( value == 0 ) {
                    return NullSummaryBattleMessage.nullSummaryBattleMessage;
                }
                else {
                    return new CVMultiplerMessage(getTarget(), added, cvType, value);
                }
            }
        }
        
        return null;
    }


    
}
