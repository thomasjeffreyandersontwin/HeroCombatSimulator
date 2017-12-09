/*
 * effectUnconscious.java
 *
 * Created on October 2, 2000, 4:06 PM
 */

package champions.powers;

import champions.Ability;
import champions.BattleEvent;
import champions.LinkedEffect;
import champions.Target;
import champions.battleMessage.GenericSummaryMessage;
import champions.exception.BattleEventException;



/**
 *
 * @author  unknown
 * @version
 */
public class effectTwoWeaponFightingHTH extends LinkedEffect{

    /** Creates new effectUnconscious */
    public effectTwoWeaponFightingHTH(String effectName) {
        super ( effectName, "LINKED", true );
    }
    
    public void setDescription( String description ) {
        this.add("Effect.DESCRIPTION", description) ;
    }
    
    public String getDescription() {
        return this.getStringValue("Effect.DESCRIPTION");
    }
    
       public boolean addEffect(BattleEvent be, Target target)
    throws BattleEventException {
        if ( super.addEffect(be,target ) ){
            be.addBattleMessage( new GenericSummaryMessage(target, "can perform " + getName())); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " can perform " + getName(), BattleEvent.MSG_NOTICE )); // .addMessage(target.getName() + " can perform " + getName(), BattleEvent.MSG_NOTICE );
            return true;
        }
        return false;
    }
    
           public void removeEffect(BattleEvent be, Target target)
    throws BattleEventException {
        super.removeEffect(be,target );
            be.addBattleMessage( new GenericSummaryMessage(target, "can no longer perform " + getName())); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " can no longer perform " + getName(), BattleEvent.MSG_NOTICE )); // .addMessage(target.getName() + " can no longer perform " + getName(), BattleEvent.MSG_NOTICE );
          
    }

    public Ability getAbility() {
        return null;
    }

}