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
import javax.swing.UIManager;

/**
 *
 * @author twalker
 */
public class GenericSummaryMessage extends AbstractBattleMessageGroup implements SummaryMessage{
    
    protected Target target;
    protected String summary;
    /**
     * Creates a new instance of EffectSummaryMessage
     */
    public GenericSummaryMessage(Target target, String summary) {
        this.target = target;
        this.summary = summary;
        
        setMessageIcon( UIManager.getIcon("AttackTree.appyEffectsIcon"));
    }

    public String getMessage() {
            return target.getName() + " " + getSummary() + ".";
    }

    public String getSummary() {
        return summary;
    }


    public SummaryMessage merge(SummaryMessage message2) {
        if ( message2 instanceof GenericSummaryMessage ) {
            GenericSummaryMessage esm = (GenericSummaryMessage)message2;
            if ( target == esm.getTarget() && summary.equals(esm.summary)) {
                
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
