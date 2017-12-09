/*
 * endSourceCharges.java
 *
 * Created on September 27, 2001, 12:01 PM
 */

package champions.powers;

import champions.BattleEvent;
import champions.DetailList;
import champions.battleMessage.ChargesSummaryMessage;
import champions.exception.BattleEventException;
import champions.interfaces.ENDSource;
import champions.interfaces.Undoable;
import java.io.Serializable;


/**
 *
 * @author  twalker
 * @version
 */
public class endSourceCharges extends DetailList
implements ENDSource {
    static final long serialVersionUID = 7964641679741790344L;
    /** Creates new endSourceCharges */
    public endSourceCharges() {
    }
    
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
    public int checkEND(BattleEvent be, int amount, boolean burnStun) {
        return getRemainingUses();
    }
    
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
    public void chargeEND(BattleEvent be, int amount, int count, boolean burnStun, String reason) throws BattleEventException {
        if ( amount == 0 ) return;
        if ( getRemainingUses() < count ) {
            // Don't have enough charges left.  Throw Exception.
            throw new BattleEventException( "Out of Charges for " + reason );
        }
        
        Undoable u = setRemainingUses( getRemainingUses() - count );
        be.addUndoableEvent(u);

        be.addBattleMessage( new ChargesSummaryMessage(be.getSource(), this, count, getRemainingUses(), reason));
    }
    
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
    public int checkEND(int amount, boolean burnStun) {
        return getRemainingUses();
    }
    
    /** Getter for property maximumUses.
     * @return Value of property maximumUses.
     */
    public int getTotalUses() {
        Integer i = getIntegerValue("Charges.TOTALUSES");
        return i != null ? i.intValue() : 0;
    }
    
    /** Setter for property maximumUses.
     * @param maximumUses New value of property maximumUses.
     */
    public void setTotalUses(int maximumUses) {
        add("Charges.TOTALUSES", new Integer(maximumUses), true);
        setRemainingUses(maximumUses);
    }
    
    /** Getter for property totalClips.
     * @return Value of property totalClips.
     */
    public int getTotalClips() {
        Integer i = getIntegerValue("Charges.TOTALCLIPS");
        return i != null ? i.intValue() : 0;
    }
    
    /** Setter for property totalClips.
     * @param totalClips New value of property totalClips.
     */
    public void setTotalClips(int totalClips) {
        add("Charges.TOTALCLIPS", new Integer(totalClips), true);
        setRemainingClips(totalClips - 1);
    }
    
    /** Getter for property remainingUses.
     * @return Value of property remainingUses.
     */
    public int getRemainingUses() {
        Integer i = getIntegerValue("Charges.REMAININGUSES");
        return i != null ? i.intValue() : 0;
    }
    
    /** Setter for property remainingUses.
     * @param remainingUses New value of property remainingUses.
     */
    public Undoable setRemainingUses(int remainingUses) {
        
        int starting = getRemainingUses();
        add("Charges.REMAININGUSES", new Integer(remainingUses), true);
        
        return new RemainingUsesUndoable(starting, remainingUses);
    }
    
    /** Getter for property remainingClips.
     * @return Value of property remainingClips.
     */
    public int getRemainingClips() {
        Integer i = getIntegerValue("Charges.REMAININGCLIPS");
        return i != null ? i.intValue() : 0;
    }
    
    /** Setter for property remainingClips.
     * @param remainingClips New value of property remainingClips.
     */
    public void setRemainingClips(int remainingClips) {
        add("Charges.REMAININGCLIPS", new Integer(remainingClips), true);
    }
    
    public String getENDString(int endCost) {
        return  getRemainingUses() + "/" + getRemainingClips();
    }
    
    public boolean canBurnStun() {
        return false;
    }
    
    public String getENDTooltip(int endCost) {
        StringBuffer sb = new StringBuffer();
        sb.append("<HTML>");
        sb.append(" <B>").append(getRemainingUses()).append("</B> of ").append(getTotalUses()).append(" remaining charges in clip.<BR>" );
        sb.append(" <B>").append(getRemainingClips()).append("</B> of ").append(getTotalClips()).append(" remaining clips.<BR>" );
        sb.append("</HTML>");
        
        return sb.toString();
    }
    
    public class RemainingUsesUndoable implements Undoable, Serializable {
        private int startingCharges;
        private int endingCharges;
        
        public RemainingUsesUndoable(int startingCharges, int endingCharges) {
            this.startingCharges = startingCharges;
            this.endingCharges = endingCharges;
        }
        
        public void redo() {
            add("Charges.REMAININGUSES", new Integer(endingCharges), true);
        }
        
        public void undo() {
            add("Charges.REMAININGUSES", new Integer(startingCharges), true);
        }
        
    }
    
}
