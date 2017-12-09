/*
 * EffectmessageMessage.java
 *
 * Created on February 18, 2008, 2:38 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.battleMessage;

import champions.Target;
import javax.swing.UIManager;

/**
 *
 * @author twalker
 */
public class KnockbackResistanceMessage extends SimpleBattleMessage implements SummaryMessage {
    
    private int kbResistance;
    private boolean added;
    
    /**
     * Creates a new instance of EffectmessageMessage
     */
    public KnockbackResistanceMessage(Target target, boolean added, int kbResistance) {
        super(target, null);
        
        this.kbResistance = kbResistance;
        this.added = added;
        
        setMessageIcon( UIManager.getIcon("AttackTree.summaryIcon"));
    }

    public String getMessage() {
        return getTarget().getName() + " " + getSummary() + ".";
    }


    public String getSummary() {
        if ( added ) {
            return "has " + kbResistance + "\" of knockback resistance";
        }
        else {
            return "no longer has " + kbResistance + "\" of knockback resistance";
        }
    }

    public boolean isRelevant(Target relevantTarget) {
        return getTarget() == relevantTarget;
    }
    


    public SummaryMessage merge(SummaryMessage message2) {
        if ( message2 instanceof KnockbackResistanceMessage) {
            KnockbackResistanceMessage krm = (KnockbackResistanceMessage)message2;
            
            if ( getTarget() == krm.getTarget() && added == krm.added ) {
                int resist = kbResistance + krm.kbResistance;
                if ( resist == 0 ) {
                    return NullSummaryBattleMessage.nullSummaryBattleMessage;
                }
                else {
                    return new KnockbackResistanceMessage(getTarget(), added, resist);
                }
            }
        }
        
        return null;
    }


    
}
