/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package champions.battleMessage;

import java.awt.Color;

/**
 *
 * @author twalker
 */
public class LegacyBattleMessage extends AbstractBattleMessage {

    String message;
    private int messageType;

    public LegacyBattleMessage(String message, int messageType) {
        this.message = message;
        this.setMessageType(messageType);
    }
    
    public String getMessage() {
        return message;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    

}
