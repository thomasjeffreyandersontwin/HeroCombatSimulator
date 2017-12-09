/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package champions.battleMessage;

import champions.Target;
import java.io.Serializable;
import java.util.List;
import javax.swing.Icon;

/**
 *
 * @author twalker
 */
public interface BattleMessage extends Serializable {
    

    
    /** Indicates that this node is relevant to the indicated target.
     * 
     * @return true if message is relevant.
     */
    public boolean isRelevant(Target relevantTarget);


    
    /** Returns this messages text message.
     * 
     * @return
     */
    public String getMessage();
    
    public Icon getMessageIcon();

}
