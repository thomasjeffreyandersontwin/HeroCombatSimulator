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
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author twalker
 */
public class AreaEffectAttackMessageGroup extends AbstractBattleMessageGroup implements AttackMessage {
    
    protected Target source;
    protected AttackMessage attackMessage;
    protected List<AttackMessage> affectedTargets = new LinkedList<AttackMessage>();
    
    /** Creates a new instance of ActivateAbilityMessageGroup */
    public AreaEffectAttackMessageGroup(Target source) {
        this.source = source;
        
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
            return "'s center of area effect " + attackMessage.getHitOrMissSummary() + ", " + getAffectedTargets();
        }
        else {
            return "{AreaEffectAttackMessageGroup: you shouldn't see this}";
        }
    }
    
    protected String getAffectedTargets() {
        if ( affectedTargets.size() == 0 ) {
            return "affecting no targets";
        }
        else {
            StringBuilder sb = new StringBuilder();
            sb.append("affecting ");
            int affectedTargetsCount = 0;
            for(AttackMessage am : affectedTargets) {
                if ( am.isTargetHit() ) {
                    if ( affectedTargetsCount > 0 ) {
                        sb.append(", ");
                    }
                    sb.append(am.getTarget().getName());
                    affectedTargetsCount++;
                }
            }
            
            if ( affectedTargetsCount == 0 ) {
                sb.append("no targets");
            }
            
            return sb.toString();
        }
    }

    public void addMessage(BattleMessage message) {
        // Area attack need to grab all of the targets..
        if ( message instanceof AttackMessage ) {
            if ( attackMessage == null ) {
                // This is the first, so it is the center of the attack...
                attackMessage = (AttackMessage)message;
            }
            else{
                AttackMessage am = (AttackMessage)message;
                affectedTargets.add(am);
            }
        }
        super.addMessage(message);
        
    }

    public void removeMessage(BattleMessage message) {
        if ( message instanceof AttackMessage ) {
            if ( attackMessage == message ) {
                attackMessage = null;
            }
            else {
                affectedTargets.remove(message);
            }
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
    
    public boolean isRelevant(Target target) {
        if ( attackMessage != null && attackMessage.getTarget() == target ) return true;
        for(AttackMessage am : affectedTargets) {
            if ( am.getTarget() == target) return true;
        }    
        
        return false;
    }

    
}
