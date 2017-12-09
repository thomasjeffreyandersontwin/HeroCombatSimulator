/*
 * AttackMessage.java
 *
 * Created on February 18, 2008, 11:09 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.battleMessage;

import champions.Target;

/** Message which contains information about hit or misses that occurred during an attack.
 *
 * @author twalker
 */
public interface AttackMessage extends BattleMessage {
    /** Produce a summary of the targets hit or miss.
     * 
     * @return String describing a hit or miss.
     */
    public String getHitOrMissSummary();
    
    /** Returns the target of this attack message.
     *
     */
    public Target getTarget();
    
    /** Returns whether the target was hit by this attack.
     *
     */
    public boolean isTargetHit();
}
