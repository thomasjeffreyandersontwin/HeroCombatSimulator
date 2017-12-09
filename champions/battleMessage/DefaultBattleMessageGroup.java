/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package champions.battleMessage;

import java.awt.Color;
import javax.swing.Icon;

/**
 *
 * @author twalker
 */
public class DefaultBattleMessageGroup extends AbstractBattleMessageGroup {

    private String message;
    private Color color = Color.BLACK;
    
    public DefaultBattleMessageGroup(String message) {
        this.message = message;
    }

    public DefaultBattleMessageGroup(String message, Icon icon) {
        this.message = message;
        setMessageIcon(icon);
    }
    
    public void closeGroup() {
        
    }

    public String getMessage() {
        return message;
    }

    
}
