/*
 * ReprocessBattleEvent.java
 *
 * Created on February 29, 2008, 9:40 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions;

/**
 *
 * @author twalker
 */
public class ReprocessBattleEvent extends BattleEvent {
    
    private BattleEvent originalEvent;
    
    /** Creates a new instance of ReprocessBattleEvent */
    public ReprocessBattleEvent(BattleEvent originalEvent) {
        super(REPROCESS_EVENT);
        this.setOriginalEvent(originalEvent);
    }

    public BattleEvent getOriginalEvent() {
        return originalEvent;
    }

    public void setOriginalEvent(BattleEvent originalEvent) {
        this.originalEvent = originalEvent;
    }
    
}
