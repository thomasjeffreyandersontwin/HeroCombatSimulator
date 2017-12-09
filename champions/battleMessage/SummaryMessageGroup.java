/*
 * ActivateAbilityMessageGroup.java
 *
 * Created on February 18, 2008, 12:25 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.battleMessage;

import champions.Target;
import java.util.List;
import javax.swing.UIManager;

/**
 *
 * @author twalker
 */
public class SummaryMessageGroup extends AbstractBattleMessageGroup {
    
    protected Target target;
    protected String cachedMessage = null;
    
    /** Creates a new instance of ActivateAbilityMessageGroup */
    public SummaryMessageGroup(Target target) {
        this.target = target;
        
        setMessageIcon( UIManager.getIcon("AttackTree.appyEffectsIcon"));
    }

    public void closeGroup() {
        cachedMessage = null;
    }
    
    public String getMessage() {
        if ( cachedMessage == null ) {
            cachedMessage = buildMessage();
        }
        
        return cachedMessage;
    }

    public String buildMessage() {
        List<SummaryMessage> list = getSummaryOfEffects();
        
        int count = 0;
        if ( list != null && list.size() > 0 ) {
            for(int i = 0; i < list.size(); i++) {
                SummaryMessage me = list.get(i);
                if ( me.getTarget() == target ) {
                    count++;
                }
            }
        }
        
        if ( count == 0 ) {
            return target.getName() + " was not affected.";
        }
        else {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getName());
            sb.append(" ");
            
            int seen = 0;
            for(int i = 0; i < list.size(); i++) {
                SummaryMessage me = list.get(i);
                if ( me.getTarget() == target ) {
                 
                    if ( count > 2 && seen >= 1 ) {
                        sb.append(", ");
                        if ( seen == count - 1 ) {
                            sb.append("and ");
                        }
                    }
                    else if ( count > 1 && seen == count - 1) {
                        sb.append(" and ");
                    }

                    sb.append(me.getSummary());
                    seen++;
                }
            }
            
            sb.append(".");
            
            return sb.toString();
        }
    }

    
    public String toString() {
        return getClass().getCanonicalName() + "[ Target: " + target + " ]";
    }

    public void addMessage(BattleMessage message) {
        cachedMessage = null;
        super.addMessage(message);
    }

    public void removeMessage(BattleMessage message) {
        cachedMessage = null;
        super.removeMessage(message);
    }
    
    public boolean isExpandedByDefault() {
        return false;
    }
    
}
