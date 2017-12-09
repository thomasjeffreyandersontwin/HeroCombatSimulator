/*
 * AggregateEffectSummaryMessage.java
 *
 * Created on February 18, 2008, 2:56 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.battleMessage;

import champions.ChampionsUtilities;
import champions.Effect;
import champions.Target;
import java.util.ArrayList;
import java.util.List;
import javax.swing.UIManager;

/**
 *
 * @author twalker
 */
public class AggregateGainedEffectSummaryMessage extends AbstractBattleMessage implements SummaryMessage, Cloneable {
    
    private Target target;
    protected List<Effect> addedEffects = new ArrayList<Effect>();
    protected List<Effect> removedEffects = new ArrayList<Effect>();
    
    /** Creates a new instance of AggregateEffectSummaryMessage */
    public AggregateGainedEffectSummaryMessage(Target target) {
        this.setTarget(target);
        
        setMessageIcon( UIManager.getIcon("AttackTree.appyEffectsIcon"));
    }
    
    public void addEffect(Effect effect) {
        if ( effect.isUnique() ) {
            if ( removedEffects.contains(effect) ) {
                removedEffects.remove(effect);
            }
            else {
                addedEffects.add(effect);
            }
        }
        else {
            addedEffects.add(effect);
        }
    }
    
    public void removeEffect(Effect effect) {
        if ( effect.isUnique() ) {
            if ( addedEffects.contains(effect) ) {
                addedEffects.remove(effect);
            }
            else {
                removedEffects.add(effect);
            }
        }
        else {
            removedEffects.add(effect);
        }
    }

    public String getMessage() {
        return getTarget().getName() + " " + getSummary() + ".";
    }
    
    protected String getEffectName(Effect effect) {
        String html = ChampionsUtilities.getHTMLColorString( effect.getName().toLowerCase(), effect.getEffectColor());
        if ( effect.isCritical() ) {
            html = "<B>" + html + "</B>";
        }
        return html;
    }

    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        boolean join = false;
        if ( addedEffects.size() > 0 ) {
            sb.append("gained ");
            int count = addedEffects.size();
            for(int i = 0; i < count; i++) {
                Effect me = addedEffects.get(i);
                
                if ( count > 2 && i >= 1 ) {
                    sb.append(", ");
                    if ( i == count - 1 ) {
                        sb.append("& ");
                    }
                }
                else if ( count > 1 && i == count - 1) {
                    sb.append(" & ");
                }
                
                sb.append(getEffectName(me));
            }
            join = true;
        }
        
        
        
        if ( removedEffects.size() > 0 ) {
            if ( join ) {
                sb.append(" & ");
            }
            sb.append("lost ");
            int count = removedEffects.size();
            for(int i = 0; i < count; i++) {
                Effect me = removedEffects.get(i);
                
                if ( count > 2 && i >= 1 ) {
                    sb.append(", ");
                    if ( i == count - 1 ) {
                        sb.append("or ");
                    }
                }
                else if ( count > 1 && i == count - 1) {
                    sb.append(" or ");
                }
                
                sb.append(getEffectName(me));
            }
        }
        
        return sb.toString();
    }
    
    public AggregateGainedEffectSummaryMessage clone() {
        try {
            return (AggregateGainedEffectSummaryMessage)super.clone();
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }
    
    protected boolean isEmpty() {
        return addedEffects.size() == 0 && removedEffects.size() == 0;
    }

    public SummaryMessage merge(SummaryMessage message2) {
        if ( message2 instanceof EffectSummaryMessage ) {
            EffectSummaryMessage esm = (EffectSummaryMessage)message2;
            if ( getTarget() != esm.getTarget() ) return null;
            AggregateGainedEffectSummaryMessage newMessage = clone();
            if ( esm.isAdded() ) {
                newMessage.addEffect( esm.getEffect() );
            }
            else {
                newMessage.removeEffect(esm.getEffect() );
            }
            
            if ( newMessage.isEmpty() ) {
                return NullSummaryBattleMessage.nullSummaryBattleMessage;
            }
            else {
                return newMessage;
            }
            
        }
        else if ( message2 instanceof AggregateGainedEffectSummaryMessage ) {
            AggregateGainedEffectSummaryMessage esm = (AggregateGainedEffectSummaryMessage)message2;
            if ( getTarget() != esm.getTarget() ) return null;
            AggregateGainedEffectSummaryMessage newMessage = clone();
            for(Effect effect : esm.addedEffects) {
                newMessage.addEffect(effect);
            }
            
            for(Effect effect : esm.removedEffects) {
                newMessage.removeEffect(effect);
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
