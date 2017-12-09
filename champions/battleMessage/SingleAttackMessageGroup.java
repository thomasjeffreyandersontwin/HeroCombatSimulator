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
import javax.swing.UIManager;

/**
 *
 * @author twalker
 */
public class SingleAttackMessageGroup extends AbstractBattleMessageGroup implements AttackMessage {
    
    protected Target source;
    protected AttackMessage attackMessage;
    
    /** Creates a new instance of ActivateAbilityMessageGroup */
    public SingleAttackMessageGroup(Target source) {
        this.source = source;
        
        setMessageIcon( UIManager.getIcon("AttackTree.toHitIcon"));
    }

    public void closeGroup() {
        
    }

    public String getMessage() {
        String summary = getHitOrMissSummary();
        String space = "";
        if ( summary.startsWith("'s") == false) {
            space = " ";
        }
        return source.getName() + space + getHitOrMissSummary() + ".";
    }

    public String getHitOrMissSummary() {
        if ( attackMessage != null ) {
            return attackMessage.getHitOrMissSummary();
        }
        else {
            return "{SingleAttachMessageGroup: you shouldn't see this}";
        }
    }

    public void addMessage(BattleMessage message) {
        if ( attackMessage == null && message instanceof AttackMessage ) {
            attackMessage = (AttackMessage)message;
        }
        
        super.addMessage(message);
        
    }

    public void removeMessage(BattleMessage message) {
        if ( attackMessage == message ) {
            attackMessage = null;
        }
        
        super.removeMessage(message);
    }

    public Target getTarget() {
        if ( attackMessage != null ) {
            return attackMessage.getTarget();
        }
        else {
            return null;
        }
    }

    public boolean isTargetHit() {
        if ( attackMessage != null ) {
            return attackMessage.isTargetHit();
        }
        else {
            return false;
        }
    }
    
    public boolean isRelevant(Target relevantTarget) {
        return attackMessage != null && attackMessage.getTarget() == relevantTarget;
    }


    
}
