/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package champions.battleMessage;

import champions.Target;
import java.util.List;
import javax.swing.Icon;

/**
 *
 * @author twalker
 */
public abstract class AbstractBattleMessage implements BattleMessage {
    
    protected Icon icon;
    
    public boolean isRelevant(Target relevantTarget) {
        return false;
    }
    
    public String toString() {
        return getClass().getCanonicalName() + " [\"" + getMessage() + "\"]";
    }
    
    public Icon getMessageIcon() {
        return icon;
    }
    
    public void setMessageIcon(Icon icon) {
        this.icon = icon;
    }
}
