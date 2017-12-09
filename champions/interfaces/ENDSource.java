/*
 * ENDSource.java
 *
 * Created on May 1, 2001, 9:20 PM
 */

package champions.interfaces;

import champions.exception.*;
import champions.*;
/** Interface used to handle tracking of END.
 *
 * @author twalker
 * @version
 */
public interface ENDSource {
    /** Returns the number of times an amount of END could be used.
     * Amount should be the amount of end needed by the ability/action.  checkEnd() will then calculate
     * the number of times which that ability/action can be used, given the current end available, and
     * return it.
     *
     * If burnStun is true, the number of times allowed is calculated with stun burn accounted for.
     * @param amount END Cost of Ability/Action being checked.
     * @param burnStun True if STUN should be burned as END, False if not.
     * @return Number of times ability/action could be performed given currently available END.
     */
    public int checkEND(int amount, boolean burnStun);
    
     /** Returns the number of times an amount of END could be used.
      * Amount should be the amount of end needed by the ability/action.  checkEnd() will then calculate
      * the number of times which that ability/action can be used, given the current end available, and
      * return it.
      *
      * If burnStun is true, the number of times allowed is calculated with stun burn accounted for.
      * @param be BattleEvent which is being processed.
      * @param amount END Cost of Ability/Action being checked.
      * @param burnStun True if STUN should be burned as END, False if not.
      * @return Number of times ability/action could be performed given currently
      * available END.
      */
    public int checkEND(BattleEvent be, int amount, boolean burnStun);
    
    /** Charges for the use of END.
     * chargeEND will actually remove the end from the end source.  If the endSource doesn't have enough
     * end, a BattleEventException will be thrown.
     *
     * @param burnStun True if STUN should be burned as END, False if not.
     * @param be BattleEvent which is being processed.
     * @param amount END Cost of Ability/Action being used.
     * @param count Number of times ability/action is being used, due to autofire or
     * other reasons.
     * @param reason String describing use of END.  Added to BattleEvent messages.
     * @throws BattleEventException A BattleEventException will be thrown if insignificant END exists.
     */
    public void chargeEND(BattleEvent be,int amount, int count, boolean burnStun, String reason) throws BattleEventException;   

    /** Returns a brief string describing the END state.
     *
     * This string returns a short text string which indicates how much
     * the indicated power costs and possibly how much END is remaining.
     */
    public String getENDString(int endCost);
    
    /** Returns the text to place in a tooltip when the mouse is held over the discription.
     *
     *  This can include html text if desired.
     */
    public String getENDTooltip(int endCost);
    
    /** Returns whether Stun can be burned in the absence of enough END.
     *
     */
    public boolean canBurnStun();
}

