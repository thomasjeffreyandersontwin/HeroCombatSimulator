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
public class PartiallyDestroyedSummaryMessage extends AbstractBattleMessageGroup implements SummaryMessage{
    
    protected Target target;
    protected boolean added;
    /**
     * Creates a new instance of EffectSummaryMessage
     */
    public PartiallyDestroyedSummaryMessage(Target target, boolean added) {
        this.target = target;
        this.added = added;
        
        setMessageIcon( UIManager.getIcon("AttackTree.appyEffectsIcon"));
    }

    public String getMessage() {
        return target.getName() + " " + getSummary() + ".";
    }

    public String getSummary() {
        if ( added ) {
            return "is partially destroyed";
        }
        else {
            return "is no longer partially destroyed";
        }
    }


    public SummaryMessage merge(SummaryMessage message2) {
        if ( message2 instanceof PartiallyDestroyedSummaryMessage ) {
            PartiallyDestroyedSummaryMessage esm = (PartiallyDestroyedSummaryMessage)message2;
            if ( target == esm.getTarget() && added == esm.added) {
                return this;
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

    public void closeGroup() {
    }


}
