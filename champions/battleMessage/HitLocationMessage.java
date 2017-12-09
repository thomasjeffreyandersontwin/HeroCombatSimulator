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
public class HitLocationMessage extends SimpleBattleMessage {
    
    /**
     * Creates a new instance of EffectmessageMessage
     */
    public HitLocationMessage(Target target, String message) {
        super(target, message);
        
        setMessageIcon( UIManager.getIcon("AttackTree.hitLocationIcon"));
    }


    
}
