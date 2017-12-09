/*
 * DefaultAttackMessage.java
 *
 * Created on February 18, 2008, 11:37 AM
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
public class DefaultAttackMessage extends AbstractBattleMessage implements AttackMessage {
    
    protected Target source;
    protected Target target;
    protected boolean targetHit;
    protected String message;
    
    /** Creates a new instance of DefaultAttackMessage */
    public DefaultAttackMessage(Target source, Target target, boolean targetHit, String message) {
        this.source = source;
        this.target = target;
        this.targetHit = targetHit;
        this.message = message;
        
        setMessageIcon( UIManager.getIcon("AttackTree.toHitIcon"));
    }

    public String getMessage() {
        return message;
    }

    public String getHitOrMissSummary() {
        return (targetHit ? "hit " : "missed ") + target.getName();
    }
    
    public Target getTarget() {
        return target;
    }

    public boolean isTargetHit() {
        return targetHit;
    }
    
}
