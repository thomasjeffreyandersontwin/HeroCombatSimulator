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
import java.awt.Color;
import javax.swing.UIManager;

/**
 *
 * @author twalker
 */
public class ActivateAbilityMessage extends AbstractBattleMessage {
    
    public Ability ability;
    public Ability maneuver;
    public Target source;
    
    /** Creates a new instance of ActivateAbilityMessageGroup */
    public ActivateAbilityMessage(Target source, Ability ability) {
        this.ability = ability;
        this.source = source;
        
        setMessageIcon( ability.getPower().getIcon() );
    }
    
    /** Creates a new instance of ActivateAbilityMessageGroup */
    public ActivateAbilityMessage(Target source, Ability ability, Ability manuever) {
        this.ability = ability;
        this.maneuver = maneuver;
        this.source = source;
    }
    public String getMessage() {
        String maneuverString = "";
        if ( maneuver != null ) {
            maneuverString = "with " + maneuver.getInstanceName();
        }
        
        return source.getName() + " activates " + ability.getInstanceName() + maneuverString + ".";
        
    }
    


    
}
