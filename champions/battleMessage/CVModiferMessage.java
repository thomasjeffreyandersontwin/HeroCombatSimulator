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
public class CVModiferMessage extends SimpleBattleMessage implements SummaryMessage {
    
    private int modifier;
    private boolean added;
    private String cvType;
    
    /**
     * Creates a new instance of EffectmessageMessage
     */
    public CVModiferMessage(Target target, boolean added, String cvType, int modifier) {
        super(target, null);
        
        this.cvType = cvType;
        this.modifier = modifier;
        this.added = added;
        
        setMessageIcon( UIManager.getIcon("AttackTree.summaryIcon"));
    }

    public String getMessage() {
        return getTarget().getName() + " " + getSummary() + ".";
    }


    public String getSummary() {
        
        String pOrB = "";
        if ( modifier < 0 ) {
            pOrB = "penalty";
        }
        else {
            pOrB = "bonus"; 
        }
        
        if ( added ) {
                return "has a " + ChampionsUtilities.toSignedString(modifier) + " " + cvType + " " + pOrB;
        }
        else {
            return "no longer has a " + ChampionsUtilities.toSignedString(modifier) + " " + cvType + " " + pOrB;
        }
    }

    public boolean isRelevant(Target relevantTarget) {
        return getTarget() == relevantTarget;
    }
    


    public SummaryMessage merge(SummaryMessage message2) {
        if ( message2 instanceof CVModiferMessage) {
            CVModiferMessage msg = (CVModiferMessage)message2;
            
            if ( getTarget() == msg.getTarget() && added == msg.added && cvType.equals(msg.cvType) ) {
                int value = modifier + msg.modifier;
                if ( value == 0 ) {
                    return NullSummaryBattleMessage.nullSummaryBattleMessage;
                }
                else {
                    return new CVModiferMessage(getTarget(), added, cvType, value);
                }
            }
        }
        
        return null;
    }

}
