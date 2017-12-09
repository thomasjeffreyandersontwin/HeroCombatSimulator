/*
 * EffectSummaryMessage.java
 *
 * Created on February 18, 2008, 2:38 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.battleMessage;

import champions.ChampionsUtilities;
import champions.Effect;
import champions.Target;
import javax.swing.UIManager;

/**
 *
 * @author twalker
 */
public class CombatSkillLevelMessage extends AbstractBattleMessage implements SummaryMessage{
    
    private Target target;
    private Effect effect;
    private boolean added;
    /**
     * Creates a new instance of EffectSummaryMessage
     */
    public CombatSkillLevelMessage(Target target, Effect effect, boolean added) {
        this.target = target;
        this.effect = effect;
        this.added = added;
        
        setMessageIcon( UIManager.getIcon("AttackTree.appyEffectsIcon"));
    }

    public String getMessage() {
        return target.getName() + " " + getSummary()  + ".";
    }

    public String getSummary() {
        if ( added ) {
            return "gained combat skill levels";
        }
        else {
            return "lost combat skill levels";
        }
    }
    
    


    public SummaryMessage merge(SummaryMessage message2) {
        if ( message2 instanceof CombatSkillLevelMessage ) {
            CombatSkillLevelMessage esm = (CombatSkillLevelMessage)message2;
            if ( target != esm.getTarget() ) return null;
            if ( added != esm.added ) return null;
            
            // If the target and added match, just return one of the two CSL messages.
            return this;
        }
        else if ( message2 instanceof AggregateEffectSummaryMessage ) {
            return ((AggregateEffectSummaryMessage)message2).merge(this);
        }
        else {
            return null;
        }
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

    public Effect getEffect() {
        return effect;
    }

    public void setEffect(Effect effect) {
        this.effect = effect;
    }

    public boolean isAdded() {
        return added;
    }

    public void setAdded(boolean added) {
        this.added = added;
    }
    
}
