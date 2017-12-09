/*
 * effectUnconscious.java
 *
 * Created on October 2, 2000, 4:06 PM
 */

package champions.powers;



import champions.Ability;
import champions.AbilityAction;
import champions.BattleEvent;
import champions.CVList;
import champions.Effect;
import champions.Target;
import champions.battleMessage.EffectSummaryMessage;
import champions.exception.BattleEventException;
import java.util.Vector;
import java.awt.Color;
/**
 *
 * @author  unknown
 * @version
 */
public class effectKnockedDown extends Effect {
    
    /** Creates new effectUnconscious */
    static private Ability unknockdown = null;
    
    public effectKnockedDown() {
        super("Knocked Down", "PERSISTENT", true);
        setUnique(true);
        setCritical(true);
        setEffectColor(new Color(153,0,153));
    }
    
    public boolean addEffect(BattleEvent be, Target t)  throws BattleEventException {
        
        boolean added = super.addEffect(be,t);
        if ( added ) {
            //be.addBattleMessage( new champions.battleMessage.SimpleBattleMessage(t.getName() + " is knocked down!", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.SimpleBattleMessage(t.getName() + " is knocked down!", BattleEvent.MSG_NOTICE)); // .addMessage(t.getName() + " is knocked down!", BattleEvent.MSG_NOTICE);
            
            be.addBattleMessage( new EffectSummaryMessage(t, this, true));
        }
        return added;
    }
    
    public void removeEffect(BattleEvent be, Target t)  throws BattleEventException {
        super.removeEffect(be,t);
        //be.addBattleMessage( new champions.battleMessage.SimpleBattleMessage(t.getName() + " is no longer knocked down!", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.SimpleBattleMessage(t.getName() + " is no longer knocked down!", BattleEvent.MSG_NOTICE)); // .addMessage(t.getName() + " is no longer knocked down!", BattleEvent.MSG_NOTICE);
        
        be.addBattleMessage( new EffectSummaryMessage(t, this, false));
    }
    
    
    public void addDCVDefenseModifiers( CVList cvList , Ability attack) {
        cvList.addTargetCVMultiplier( "Knocked Down", 0.5);
    }
    
    public void addActions(Vector actions) {
        if ( unknockdown == null ) {
            unknockdown = new Ability("Stand Up");
            unknockdown.addPAD( new powerUnknockdown(), null );
        }
        
        AbilityAction action = new AbilityAction( "Stand Up", unknockdown );
        action.putValue( "TOOLTIPTEXT","Stand Up - Remove Knocked Down");
        action.setType(  BattleEvent.ACTIVATE );
        action.setSource( this.getTarget() );
        
        actions.add(action);
    }
}