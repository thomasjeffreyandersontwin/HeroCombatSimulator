/*
 * ActivateAbilityMessageGroup.java
 *
 * Created on February 18, 2008, 12:25 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.battleMessage;

import champions.Ability;
import champions.Target;
import javax.swing.Icon;
import javax.swing.UIManager;

/**
 *
 * @author twalker
 */
public class ActivateAbilityMessageGroup extends AbstractBattleMessageGroup {
    
    public Ability ability;
    protected Ability maneuver;
    protected Target source;
    
    public ActivateAbilityMessage activateAbilityMessage;
    
    /** Creates a new instance of ActivateAbilityMessageGroup */
    public ActivateAbilityMessageGroup(Target source, Ability ability) {
        this.ability = ability;
        this.source = source;
        
        Icon icon = ability.getPower().getIcon();
        if ( icon == null ) {
            icon = UIManager.getIcon("AttackTree.powerIcon");
        }
        setMessageIcon( icon );
    }
    
    /** Creates a new instance of ActivateAbilityMessageGroup */
    public ActivateAbilityMessageGroup(Target source, Ability ability, Ability manuever) {
        this.ability = ability;
        this.maneuver = manuever;
        this.source = source;
        
        setMessageIcon( ability.getPower().getIcon() );
    }

    public void closeGroup() {
        
    }

    public String getMessage() {
        
        
        if ( activateAbilityMessage != null ) {
            return activateAbilityMessage.getMessage();
        }
        else {
        String maneuverString = "";
            if ( maneuver != null ) {
                maneuverString = "with " + maneuver.getInstanceName();
            }

            return source.getName() + " activates " + ability.getInstanceName() + maneuverString + ".";
        }
    }

    public void addMessage(BattleMessage message) {
        if ( activateAbilityMessage == null && message instanceof ActivateAbilityMessage) {
            activateAbilityMessage = (ActivateAbilityMessage)message;
        }
        else { 
            super.addMessage(message);
        }
    }

    public void removeMessage(BattleMessage message) {
        if ( message == activateAbilityMessage ) {
            activateAbilityMessage = null;
        }
        
        super.removeMessage(message);
    }

    public boolean isRelevant(Target relevantTarget) {
        return source == relevantTarget;
    }
    

    


    
}
