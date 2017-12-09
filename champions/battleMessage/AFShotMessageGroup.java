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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author twalker
 */
public class AFShotMessageGroup extends AbstractBattleMessageGroup implements AttackMessage {
    
    public AttackMessage attackMessage;
    private Target source;
    private int shot;
    /** Creates a new instance of ActivateAbilityMessageGroup */
    public AFShotMessageGroup(Target source, int shot) {
        this.source = source;
        this.shot = shot;
        
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
            String summary = attackMessage.getHitOrMissSummary();
            String space = "";
            if ( summary.startsWith("'s") == false) {
                space = " ";
            }
            return "'s autofire shot " + shot + space + summary;
        } else {
            return "{AFShotMessageGroup: you shouldn't see this}";
        }
    }
    
    public void addMessage(BattleMessage message) {
        if ( attackMessage == null && message instanceof AttackMessage ) {
            attackMessage = (AttackMessage)message;
        } else {
            throw new IllegalArgumentException("AFShotMessageGroup expected only a single child, but got additional child: " + message);
        }
    }
    
    public void removeMessage(BattleMessage message) {
        if ( attackMessage == message ) {
            attackMessage = null;
        }
    }

    public int getChildCount() {
        if ( attackMessage instanceof BattleMessageGroup ) {
            return ((BattleMessageGroup)attackMessage).getChildCount();
        }
        else { 
            return 0;
        }
    }

    public BattleMessage getChild(int index) {
        if ( attackMessage instanceof BattleMessageGroup ) {
            return ((BattleMessageGroup)attackMessage).getChild(index);
        }
        else { 
            throw new IndexOutOfBoundsException();
        }
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
    

public List<BattleMessage> getSummaryChildren(Target relevantTarget) {
        List<BattleMessage> list = new ArrayList<BattleMessage>();
        
        if ( isRelevant(relevantTarget) ) {
                BattleMessageGroup newBattleMessageGroup = createMessageGroup();
                
                list.add(newBattleMessageGroup);
                
                if ( attackMessage != null && attackMessage instanceof BattleMessageGroup ) {
                    List<BattleMessage> childList = ((BattleMessageGroup)attackMessage).getSummaryChildren(relevantTarget);
                    if ( childList != null ) {
                        for(BattleMessage bm : childList) {
                            newBattleMessageGroup.addMessage(bm);
                        }
                    }
                }
        }
        else {
            // We aren't relevant, so don't include us, but there might be children that are...
            if ( attackMessage != null ) {
                if ( attackMessage instanceof BattleMessageGroup ) {
                    List<BattleMessage> childList = ((BattleMessageGroup)attackMessage).getSummaryChildren(relevantTarget);
                    if ( childList != null ) {
                        for(BattleMessage bm : childList) {
                            list.add(bm);
                        }
                    }
                }
                else if ( attackMessage.isRelevant(relevantTarget) ) {
                    // This should never be true since our isRelevant is based on this...
                    list.add(attackMessage);
                }
            }
        }
        return list;
    }

    public boolean isRelevant(Target relevantTarget) {
        return ( attackMessage != null && attackMessage.isRelevant(relevantTarget));
    }

    protected BattleMessageGroup createMessageGroup() {
        try {
            AFShotMessageGroup mg = (AFShotMessageGroup)clone();
            mg.attackMessage = null;
            return mg;
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }
    

    
}
