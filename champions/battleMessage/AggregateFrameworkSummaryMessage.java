/*
 * AggregateAbilitySummaryMessage.java
 *
 * Created on February 18, 2008, 2:56 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.battleMessage;

import champions.Ability;
import champions.Target;
import java.util.ArrayList;
import java.util.List;
import javax.swing.UIManager;

/**
 *
 * @author twalker
 */
public class AggregateFrameworkSummaryMessage extends AbstractBattleMessage implements SummaryMessage, Cloneable {
    
    private Target target;
    protected List<Ability> enabledAbilities = new ArrayList<Ability>();
    protected List<Ability> disabledAbilities = new ArrayList<Ability>();
    
    /** Creates a new instance of AggregateAbilitySummaryMessage */
    public AggregateFrameworkSummaryMessage(Target target) {
        this.setTarget(target);
        
        setMessageIcon( UIManager.getIcon("AttackTree.appyAbilitysIcon"));
    }
    
    public void addEnabledAbility(Ability ability) {
           enabledAbilities.add(ability);
    }
    
    public void addDisabledAbility(Ability ability) {

            disabledAbilities.add(ability);
    }

    public String getMessage() {
        return getTarget().getName() + " " + getSummary() + ".";
    }
    
    protected String getAbilityName(Ability ability) {
        return ability.getName();
    }

    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        boolean join = false;
        if ( enabledAbilities.size() > 0 ) {
            sb.append("enabled ");
            int count = enabledAbilities.size();
            for(int i = 0; i < count; i++) {
                Ability me = enabledAbilities.get(i);
                
                if ( count > 2 && i >= 1 ) {
                    sb.append(", ");
                    if ( i == count - 1 ) {
                        sb.append("& ");
                    }
                }
                else if ( count > 1 && i == count - 1) {
                    sb.append(" & ");
                }
                
                sb.append(getAbilityName(me));
            }
            join = true;
        }
        
        
        
        if ( disabledAbilities.size() > 0 ) {
            if ( join ) {
                sb.append(" & ");
            }
            sb.append("disabled ");
            int count = disabledAbilities.size();
            for(int i = 0; i < count; i++) {
                Ability me = disabledAbilities.get(i);
                
                if ( count > 2 && i >= 1 ) {
                    sb.append(", ");
                    if ( i == count - 1 ) {
                        sb.append("& ");
                    }
                }
                else if ( count > 1 && i == count - 1) {
                    sb.append(" & ");
                }
                
                sb.append(getAbilityName(me));
            }
        }
        
        return sb.toString();
    }
    
    public AggregateFrameworkSummaryMessage clone() {
        try {
            return (AggregateFrameworkSummaryMessage)super.clone();
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }
    
    protected boolean isEmpty() {
        return enabledAbilities.size() == 0 && disabledAbilities.size() == 0;
    }

    public SummaryMessage merge(SummaryMessage message2) {
        if ( message2 instanceof FrameworkSummaryMessage ) {
            FrameworkSummaryMessage esm = (FrameworkSummaryMessage)message2;
            if ( getTarget() != esm.getTarget() ) return null;
            AggregateFrameworkSummaryMessage newMessage = clone();
            if ( esm.isEnabled() ) {
                newMessage.addEnabledAbility( esm.getAbility() );
            }
            else {
                newMessage.addDisabledAbility(esm.getAbility() );
            }
            
            if ( newMessage.isEmpty() ) {
                return NullSummaryBattleMessage.nullSummaryBattleMessage;
            }
            else {
                return newMessage;
            }
            
        }
        else if ( message2 instanceof AggregateFrameworkSummaryMessage ) {
            AggregateFrameworkSummaryMessage esm = (AggregateFrameworkSummaryMessage)message2;
            if ( getTarget() != esm.getTarget() ) return null;
            AggregateFrameworkSummaryMessage newMessage = clone();
            for(Ability ability : esm.enabledAbilities) {
                newMessage.addEnabledAbility(ability);
            }
            
            for(Ability ability : esm.disabledAbilities) {
                newMessage.addDisabledAbility(ability);
            }
            
            if ( newMessage.isEmpty() ) {
                return NullSummaryBattleMessage.nullSummaryBattleMessage;
            }
            else {
                return newMessage;
            }
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
    
}
