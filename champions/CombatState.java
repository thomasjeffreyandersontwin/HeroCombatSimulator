/*
 * CombatState.java
 *
 * Created on September 21, 2007, 9:41 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions;

/**
 *
 * @author twalker
 */
public enum CombatState {
    STATE_ACTIVE, STATE_FIN, STATE_HALFFIN, STATE_HELD, STATE_HALFHELD, 
    STATE_ABORTING, STATE_ABORTED, STATE_DELAYED,
    STATE_INACTIVE;
            
//    public boolean equals(Object that) {
//        if ( this == that ) return true;
//        if ( that instanceof String ) throw new IllegalArgumentException();
//        return false;
//    }
}
