/*
 * StatChangeBattleMessage.java
 *
 * Created on February 18, 2008, 1:18 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.battleMessage;

import champions.Ability;
import champions.Target;
import champions.battleMessage.StatCPChangeBattleMessage.StatChangeEntry;
import java.util.Iterator;
import java.util.Map.Entry;
import javax.swing.UIManager;

/** Represents a change in the stats of a character.
 *
 * Although it is called damage, it can be used for aids, drains, etc.
 *
 * @author twalker
 */
public class AbilityCPChangeBattleMessage extends AbstractBattleMessage implements SummaryMessage {
    
    protected Target target;
    protected Ability ability;
    protected double cpAmount;
    protected StatChangeType damageType;

    
    
    public AbilityCPChangeBattleMessage(Target target, StatChangeType damageType, Ability ability, double cpAmount) {
        this.target = target;
        this.damageType = damageType;
        this.ability = ability;
        this.cpAmount = cpAmount;
        
        setMessageIcon( UIManager.getIcon("AttackTree.appyEffectsIcon"));
    }
    
    
    public String getMessage() {
        return target.getName() + " " + getSummary() + ".";
    }
    
    public String getSummary() {

            return  damageType.getVerb() + " " + cpAmount + " character point(s)" + damageType.getConnector() + ability.getName();
    }
    

    
    public SummaryMessage merge(SummaryMessage message2) {
        if ( message2 instanceof AbilityCPChangeBattleMessage) {
            AbilityCPChangeBattleMessage bm = (AbilityCPChangeBattleMessage)message2;
            if ( target == bm.getTarget() && damageType == bm.damageType && ability == bm.ability) {
                
                double value = cpAmount + bm.cpAmount;
                
                AbilityCPChangeBattleMessage newMessage = new AbilityCPChangeBattleMessage(target, damageType, ability, value);
                
                return newMessage;
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
    
    public static class StatChangeEntry {
        double cpAmount;
        int newStatValue;
        
        StatChangeEntry(double cpAmount, int newStatValue) {
            this.cpAmount = cpAmount;
            this.newStatValue = newStatValue;
        }
    }
    
}
