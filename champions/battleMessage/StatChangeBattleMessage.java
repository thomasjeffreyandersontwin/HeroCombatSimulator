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
public class StatChangeBattleMessage extends AbstractBattleMessage implements SummaryMessage, Iterable<Entry<String,Integer>> {
    
    private Target target;
    private Map<String, Integer> changeMap = new LinkedHashMap<String,Integer>();
    protected StatChangeType changeType;
    
    /**
     * Creates a new instance of StatChangeBattleMessage
     */
    public StatChangeBattleMessage(Target target, StatChangeType changeType) {
            this.changeType = changeType;
        this.target = target;
        
        setMessageIcon( UIManager.getIcon("AttackTree.appyEffectsIcon"));
        
        }
    public StatChangeBattleMessage(Target target, StatChangeType changeType, int stun, int body) {
        this(target,changeType);
        if ( stun > 0 ) {
            setStatChangeAmount("STUN", stun);
        }
        if ( body > 0 ) {
            setStatChangeAmount("BODY", body);
        }
    }
    
    public StatChangeBattleMessage(Target target, StatChangeType damageType, String stat, int amount) {
        this(target,damageType);
        setStatChangeAmount(stat,amount);
    }
        
        public void setStatChangeAmount(String stat, int amount) {
            changeMap.put(stat,amount);
        }
        
        public int getStatChangeAmount(String stat) {
            if ( changeMap.containsKey(stat) ) {
                return changeMap.get(stat);
            }
            else {
                return 0;
            }
        }
    
    public String getMessage() {
        return target.getName() + " " + getSummary() + ".";
    }
    
    public String getSummary() {
        if ( changeMap.size() == 0 ) {
            return changeType.getVerb() + " no damage";
        }
        else {
            StringBuilder sb = new StringBuilder();
            sb.append(changeType.getVerb()).append(" ");
            int count = 0;
            String connector = changeType.getConnector();
            for(Iterator<Entry<String,Integer>> it = changeMap.entrySet().iterator(); it.hasNext(); ) {
                Entry<String,Integer> e = it.next();
                if ( count > 0 ) {
                    sb.append(", ");
                }
                
                sb.append(e.getValue());
                sb.append(connector);
                sb.append(e.getKey());
                count++;
            }
            return sb.toString();
        }
    }
    
    public Iterator<Entry<String,Integer>> iterator() {
        return changeMap.entrySet().iterator();
    }
    
    public SummaryMessage merge(SummaryMessage message2) {
        if ( message2 instanceof StatChangeBattleMessage) {
            StatChangeBattleMessage bm = (StatChangeBattleMessage)message2;
            if ( target == bm.getTarget() && changeType == bm.changeType) {
                StatChangeBattleMessage newMessage = new StatChangeBattleMessage(target, changeType);
                
                for(Entry<String,Integer> e : this ) {
                    newMessage.setStatChangeAmount(e.getKey(), e.getValue());
                }
                
                for(Entry<String,Integer> e : bm ) {
                    newMessage.setStatChangeAmount(e.getKey(), this.getStatChangeAmount(e.getKey()) + e.getValue());
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
    
}
