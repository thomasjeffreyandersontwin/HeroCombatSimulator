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
import java.util.ArrayList;
import java.util.List;
import javax.swing.UIManager;

/**
 *
 * @author twalker
 */
public class KnockbackSummaryMessage extends AbstractBattleMessage implements SummaryMessage{
    
    private Target target;
    public String summary;
    /**
     * Creates a new instance of EffectSummaryMessage
     */
    public KnockbackSummaryMessage(Target target, String summary) {
        this.target = target;
        this.summary = summary;
        
        setMessageIcon( UIManager.getIcon("AttackTree.knockbackEffectIcon"));
    }

    public String getMessage() {
        return target.getName() + " " + getSummary()  + ".";
    }

    public String getSummary() {
        return summary;
    }

    


    public SummaryMessage merge(SummaryMessage message2) {
        this.summary= this.summary +" and " +message2.getSummary();
        return this;
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
