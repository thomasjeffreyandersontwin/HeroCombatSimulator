/*
 * StatChangeBattleMessage.java
 *
 * Created on February 18, 2008, 1:18 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.battleMessage;

import champions.Target;
import champions.battleMessage.StatCPChangeBattleMessage.StatChangeEntry;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.UIManager;

/** Represents a change in the stats of a character.
 *
 * Although it is called damage, it can be used for aids, drains, etc.
 *
 * @author twalker
 */
public class StatCPChangeBattleMessage extends AbstractBattleMessage implements SummaryMessage, Iterable<Entry<String,StatChangeEntry>> {
    
    private Target target;
    private Map<String, StatChangeEntry> statMap = new LinkedHashMap<String,StatChangeEntry>();
    protected StatChangeType changeType;
    
    /**
     * Creates a new instance of StatChangeBattleMessage
     */
    public StatCPChangeBattleMessage(Target target, StatChangeType changeType) {
        this.changeType = changeType;
        this.target = target;
        
        setMessageIcon( UIManager.getIcon("AttackTree.appyEffectsIcon"));
        
    }
    
    
    public StatCPChangeBattleMessage(Target target, StatChangeType changeType, String stat, double cpAmount, int newStatValue) {
        this(target,changeType);
        setStatChange(stat,cpAmount,newStatValue);
    }
    
    public void setStatChange(String stat, double amount, int newValue) {
        statMap.put(stat, new StatChangeEntry(amount,newValue));
    }
    
    public double getStatCPChange(String stat) {
        if ( statMap.containsKey(stat) ) {
            return statMap.get(stat).cpAmount;
        } else {
            return 0;
        }
    }
    
    public double getNewStatValue(String stat) {
        if ( statMap.containsKey(stat) ) {
            return statMap.get(stat).newStatValue;
        } else {
            return 0;
        }
    } 
    
    public String getMessage() {
        return target.getName() + " " + getSummary() + ".";
    }
    
    public String getSummary() {
        if ( statMap.size() == 0 ) {
            return changeType.getVerb() + " no damage";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(changeType.getVerb()).append(" ");
            int count = 0;
            String connector = changeType.getConnector();
            for(Iterator<Entry<String,StatChangeEntry>> it = statMap.entrySet().iterator(); it.hasNext(); ) {
                Entry<String,StatChangeEntry> e = it.next();
                if ( count > 0 ) {
                    sb.append(", ");
                }
                
                sb.append(e.getValue().cpAmount);
                sb.append(" character point(s)");
                sb.append(connector);
                sb.append(e.getKey());
                sb.append(" (now at ").append(e.getValue().newStatValue).append(" ").append(e.getKey()).append(")");
                count++;
            }
            return sb.toString();
        }
    }
    
    public Iterator<Entry<String,StatChangeEntry>> iterator() {
        return statMap.entrySet().iterator();
    }
    
    public SummaryMessage merge(SummaryMessage message2) {
        if ( message2 instanceof StatCPChangeBattleMessage) {
            StatCPChangeBattleMessage bm = (StatCPChangeBattleMessage)message2;
            if ( target == bm.getTarget() && changeType == bm.changeType) {
                StatCPChangeBattleMessage newMessage = new StatCPChangeBattleMessage(target, changeType);
                
                for(Entry<String,StatChangeEntry> e : this ) {
                    newMessage.setStatChange(e.getKey(), e.getValue().cpAmount, e.getValue().newStatValue);
                }
                
                for(Entry<String,StatChangeEntry> e : bm ) {
                    newMessage.setStatChange(e.getKey(), this.getStatCPChange(e.getKey()) + e.getValue().cpAmount, e.getValue().newStatValue);
                }
                
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
    
    public static class StatChangeEntry implements Serializable{
        double cpAmount;
        int newStatValue;
        
        StatChangeEntry(double cpAmount, int newStatValue) {
            this.cpAmount = cpAmount;
            this.newStatValue = newStatValue;
        }
    }
    
}
