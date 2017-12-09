/*
 * NullSummaryBattleMessage.java
 *
 * Created on February 18, 2008, 1:12 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.battleMessage;

import champions.Target;

/**
 *
 * @author twalker
 */
public class NullSummaryBattleMessage implements SummaryMessage {
    
    static final public NullSummaryBattleMessage nullSummaryBattleMessage = new NullSummaryBattleMessage();
    /**
     * Creates a new instance of NullSummaryBattleMessage
     */
    private NullSummaryBattleMessage() {
    }

    public String getSummary() {
        return "{Null Summary Battle Message}";
    }

    public SummaryMessage merge(SummaryMessage message2) {
        return message2;
    }
    
    public Target getTarget() {
        return null;
    }
    
}
