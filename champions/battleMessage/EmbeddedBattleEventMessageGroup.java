/*
 * ActivateAbilityMessageGroup.java
 *
 * Created on February 18, 2008, 12:25 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.battleMessage;

import champions.BattleEvent;
import java.awt.Color;

/**
 *
 * @author twalker
 */
public class EmbeddedBattleEventMessageGroup extends AbstractBattleMessageGroup {
    
    public BattleEvent battleEvent;
    
    /** Creates a new instance of ActivateAbilityMessageGroup */
    public EmbeddedBattleEventMessageGroup(BattleEvent battleEvent) {
        this.battleEvent = battleEvent;
    }

    public void closeGroup() {
        
    }

    public String getMessage() {
        return battleEvent.getPrimaryBattleMessageGroup().getMessage();
    }
    
    public int getChildCount() {
        return battleEvent.getPrimaryBattleMessageGroup().getChildCount();
    }

    public BattleMessage getChild(int index) {
        return battleEvent.getPrimaryBattleMessageGroup().getChild(index);
    }
    

    
}
