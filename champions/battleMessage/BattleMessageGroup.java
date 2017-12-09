/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package champions.battleMessage;

import champions.Target;
import java.util.List;

/**
 *
 * @author twalker
 */
public interface BattleMessageGroup extends BattleMessage {
    
    /** Adds the <code>message</code> to the group.
     *
     */
    public void addMessage(BattleMessage message);
    
    /** Removes the <code>message</code> from the group.
     *
     * Only the exact message should be removed.  This is used by the undo 
     * mechanism to remove extra messages.
     */
    public void removeMessage(BattleMessage message);
    
    /** Tells the group that it is closed and can post-process anything it wants.
     * 
     */
    public void closeGroup();
    
     /** Return the number of children.
      *
      */
    public int getChildCount();
    
    /** Returns child message at <code>index</code>.
     * 
     * @return List of children, null if no children exist.
     */
    public BattleMessage getChild(int index);
    
    /** Return a list of the children of this message that are relevant to relevantTarget.
     * 
     * @param relevantTarget
     * @return List of relevant children, null if no children are relevant.
     */
    public List<BattleMessage> getSummaryChildren(Target relevantTarget);
    
    /** Returns whether this Battle message should be expanded automatically.
     *
     */
    public boolean isExpandedByDefault();
    
    /** Return a summarized list of effects produced by this node and its children.
     * 
     * This method relies on merge to generate a consise list.
     * 
     * @return null if no effects are added.
     */
    public List<SummaryMessage> getSummaryOfEffects();
}
