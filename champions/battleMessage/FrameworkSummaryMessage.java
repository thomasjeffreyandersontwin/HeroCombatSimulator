/*
 * EffectSummaryMessage.java
 *
 * Created on February 18, 2008, 2:38 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.battleMessage;

import champions.Ability;
import champions.Target;
import champions.interfaces.Framework;
import javax.swing.UIManager;

/**
 *
 * @author twalker
 */
public class FrameworkSummaryMessage extends AbstractBattleMessage implements SummaryMessage{

    private Framework framework;
    private Target target;
    private Ability ability;
    private boolean enabled;

    public FrameworkSummaryMessage(Framework framework, Target target, Ability ability, boolean endabled) {
        this.framework = framework;
        this.target = target;
        this.ability = ability;
        this.enabled = endabled;

        setMessageIcon( UIManager.getIcon("Framework.DefaultIcon"));
    }

    public String getMessage() {
        return target.getName() + " " + getSummary()  + ".";
    }

    public String getSummary() {
        if ( enabled ) {
            return "enabled " + getAbilityName() + " in " + framework.getFrameworkAbility().getName();
        }
        else {
            return "disabled " + getAbilityName() + " in " + framework.getFrameworkAbility().getName();
        }
    }
    
    protected String getAbilityName() {
        return getAbility().getName();
    }
    


    public SummaryMessage merge(SummaryMessage message2) {
        if ( message2 instanceof FrameworkSummaryMessage ) {
            FrameworkSummaryMessage esm = (FrameworkSummaryMessage)message2;
            if ( target != esm.target ) return null;
            AggregateFrameworkSummaryMessage newMessage = new AggregateFrameworkSummaryMessage(target);
            if ( enabled ) {
                newMessage.addEnabledAbility(getAbility());
            }
            else {
                newMessage.addDisabledAbility(getAbility());
            }
            
            if ( esm.enabled ) {
                newMessage.addEnabledAbility(esm.getAbility());
            }
            else {
                newMessage.addDisabledAbility(esm.getAbility());
            }
            
            if ( newMessage.isEmpty() ) {
                return NullSummaryBattleMessage.nullSummaryBattleMessage;
            }
            else {
                return newMessage;
            }
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


    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean added) {
        this.enabled = added;
    }

    /**
     * @return the ability
     */
    public Ability getAbility() {
        return ability;
    }

    /**
     * @param ability the ability to set
     */
    public void setAbility(Ability ability) {
        this.ability = ability;
    }
    
}
