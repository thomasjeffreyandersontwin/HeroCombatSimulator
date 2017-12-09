/*
 * EffectmessageMessage.java
 *
 * Created on February 18, 2008, 2:38 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.battleMessage;

import champions.Target;
import java.util.ArrayList;
import java.util.List;
import javax.swing.UIManager;

/**
 *
 * @author twalker
 */
public class SimpleBattleMessage extends AbstractBattleMessage {
    
    private Target target;
    private String message;
    /**
     * Creates a new instance of EffectmessageMessage
     */
    public SimpleBattleMessage(Target target, String message) {
        this.target = target;
        this.message = message;
        
        setMessageIcon( UIManager.getIcon("AttackTree.appyEffectsIcon"));
    }

    public String getMessage() {
        return message;
    }
    

    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    public boolean isRelevant(Target relevantTarget) {
        return target == relevantTarget;
    }


    
}
