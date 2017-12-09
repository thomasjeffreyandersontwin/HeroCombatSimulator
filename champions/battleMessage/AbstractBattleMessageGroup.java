/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package champions.battleMessage;

import champions.Target;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;

/**
 *
 * @author twalker
 */
public abstract class AbstractBattleMessageGroup implements BattleMessageGroup, Cloneable {
    
    protected List<BattleMessage> children;
    protected Icon icon;
    
    public int getChildCount() {
        if ( children == null ) return 0;
        else return children.size();
    }
    
    public BattleMessage getChild(int index) {
        if ( children == null ) throw new IndexOutOfBoundsException();
        return children.get(index);
    }
    
    public List<BattleMessage> getSummaryChildren(Target relevantTarget) {
        List<BattleMessage> list = new ArrayList<BattleMessage>();
        
        if ( isRelevant(relevantTarget) ) {
            BattleMessageGroup newBattleMessageGroup = createMessageGroup();
            
            list.add(newBattleMessageGroup);
            
            for(int i = 0; i < getChildCount(); i++) {
                BattleMessage childBattleMessage = getChild(i);
                if ( childBattleMessage instanceof BattleMessageGroup ) {
                    List<BattleMessage> childList = ((BattleMessageGroup)childBattleMessage).getSummaryChildren(relevantTarget);
                    if ( childList != null ) {
                        for(BattleMessage bm : childList) {
                            newBattleMessageGroup.addMessage(bm);
                        }
                    }
                } else if ( childBattleMessage.isRelevant(relevantTarget) ) {
                    newBattleMessageGroup.addMessage(childBattleMessage);
                }
            }
        } else {
            // We aren't relevant, so don't include us, but there might be children that are...
            
            for(int i = 0; i < getChildCount(); i++) {
                BattleMessage childBattleMessage = getChild(i);
                if ( childBattleMessage instanceof BattleMessageGroup ) {
                    List<BattleMessage> childList = ((BattleMessageGroup)childBattleMessage).getSummaryChildren(relevantTarget);
                    if ( childList != null ) {
                        for(BattleMessage bm : childList) {
                            list.add(bm);
                        }
                    }
                } else if ( childBattleMessage.isRelevant(relevantTarget) ) {
                    list.add(childBattleMessage);
                }
            }
        }
        return list;
    }
    
    public boolean isRelevant(Target relevantTarget) {
        return false;
    }
    
    public List<SummaryMessage> getSummaryOfEffects() {
        List<SummaryMessage> list = null;
        if ( getChildCount() > 0 ) {
            list = new ArrayList<SummaryMessage>();
            
            for(int i = 0; i < getChildCount(); i++) {
                BattleMessage bm = getChild(i);
                if ( bm instanceof SummaryMessage ) {
                    list.add( (SummaryMessage)bm);
                } else if ( bm instanceof BattleMessageGroup ) {
                    List<SummaryMessage> childList = ((BattleMessageGroup)bm).getSummaryOfEffects();
                    if ( childList != null ) {
                        list.addAll(childList);
                    }
                }
            }
            
            mergeSummaryList(list);
        }
        return list;
    }
    
    protected void mergeSummaryList(List<SummaryMessage> list) {
        for(int i = 0; i < list.size(); i++) {
            for(int j = i+1; j < list.size(); j++) {
                SummaryMessage message1 = list.get(i);
                SummaryMessage message2 = list.get(j);
                
                SummaryMessage newMessage = message1.merge(message2);
                if ( newMessage != null ) {
                    if ( newMessage instanceof NullSummaryBattleMessage ) {
                        // they cancel each other out, so remove both of them...
                        list.remove(i);
                        list.remove(j-1);
                        
                        i--;
                        break; // break out of the inner loop, since we no longer need to compare to ith.
                    } else {
                        // It is a new message, so we need to replace the ith and remove the jth
                        list.set(i,newMessage);
                        list.remove(j);
                        
                        i--;
                        break; // break out of the inner loop...this will rerun the merge with the new element
                    }
                }
            }
        }
    }
    
    public String getEffectDescription() {
        return null;
    }
    
    public void addMessage(BattleMessage message) {
        if ( children == null ) children = new ArrayList<BattleMessage>();
        children.add(message);
    }
    
    public void removeMessage(BattleMessage message) {
        if ( children != null ) {
            children.remove(message);
        }
    }
    
    public Icon getMessageIcon() {
        return icon;
    }
    
    public void setMessageIcon(Icon icon) {
        this.icon = icon;
    }
    
    protected BattleMessageGroup createMessageGroup() {
        try {
            AbstractBattleMessageGroup bmg = (AbstractBattleMessageGroup)clone();
            bmg.children = null;
            return bmg;
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }
    
    
    
    public boolean isExpandedByDefault() {
        return true;
    }
    
}
