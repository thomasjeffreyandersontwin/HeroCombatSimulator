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
public class KnockbackBodyMessage extends SimpleBattleMessage implements SummaryMessage {
    
    private int body;
    private boolean added;
    
    /**
     * Creates a new instance of EffectmessageMessage
     */
    public KnockbackBodyMessage(Target target, boolean added, int body) {
        super(target, null);
        
        this.body = body;
        this.added = added;
        
        setMessageIcon( UIManager.getIcon("AttackTree.summaryIcon"));
    }

    public String getMessage() {
        return getTarget().getName() + " " + getSummary() + ".";
    }


    public String getSummary() {
        if ( added ) {
            return "has " + ChampionsUtilities.toSignedString(body) + " BODY added to knockback calculations";
        }
        else {
            return "no longer has " + ChampionsUtilities.toSignedString(body) + " BODY added to knockback calculations";
        }
    }

    public boolean isRelevant(Target relevantTarget) {
        return getTarget() == relevantTarget;
    }
    


    public SummaryMessage merge(SummaryMessage message2) {
        if ( message2 instanceof KnockbackResistanceMessage) {
            KnockbackBodyMessage msg = (KnockbackBodyMessage)message2;
            
            if ( getTarget() == msg.getTarget() && added == msg.added ) {
                int value = body + msg.body;
                if ( value == 0 ) {
                    return NullSummaryBattleMessage.nullSummaryBattleMessage;
                }
                else {
                    return new KnockbackBodyMessage(getTarget(), added, value);
                }
            }
        }
        
        return null;
    }


    
}
