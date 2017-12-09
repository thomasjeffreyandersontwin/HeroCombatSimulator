/*
 * SummaryMessage.java
 *
 * Created on February 18, 2008, 1:05 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.battleMessage;

import champions.Target;
import java.util.List;

/**
 *
 * @author twalker
 */
public interface SummaryMessage {
    
    /** Return a text description of the effects of this node.
     *
     * The effect summary should be of a form such that it can be used like:
     *  Bob <effect summary>, <effect summary>, and <effect summary>.
     *
     *  Thus, it should start with a verb followed by a short description.  Generally,
     *  it should be phrased in the past tense, unless it is a persistent change, 
     *  such as an effect addition.
     *
     *  For example:  
     *  Bob {took 3 Stun, 8 Body}, {is stunned & dying}, and {was knocked back 3"}.
     * 
     * @return Effect summary.
     */
    public String getSummary();
    
    /** Attempt to merge two battleMessages to produce a summary of the effects.
     * 
     * Attempt to merge the information of two battle messages, producing a 
     * summary of those nodes.
     * 
     * @param message2 Message to merge with.
     * @return new BattleMessage if merge was successful, null if messages are
     * not mergible, NullEffectBattleMessage if the messages cancel each other.
     */
    public SummaryMessage merge(SummaryMessage message2);
    
    /** Returns the target this message is relevant for.
     *
     */
    public Target getTarget();
    

}
