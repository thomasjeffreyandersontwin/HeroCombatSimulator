/*
 * effectUnconscious.java
 *
 * Created on October 2, 2000, 4:06 PM
 */

package champions.powers;

import champions.interfaces.*;
import champions.*;
import champions.event.*;
import champions.exception.*;
/**
 *
 * @author  unknown
 * @version
 */
public class effectSkipoverSprayfire extends LinkedEffect{

    /** Creates new effectUnconscious */
    public effectSkipoverSprayfire(String effectName) {
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
            be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " is now " + getName(), BattleEvent.MSG_NOTICE )); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " is now " + getName(), BattleEvent.MSG_NOTICE )); // .addMessage(target.getName() + " is now " + getName(), BattleEvent.MSG_NOTICE );
            return true;
        }
        return false;
    }
    
           public void removeEffect(BattleEvent be, Target target)
    throws BattleEventException {
        super.removeEffect(be,target );
            be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " is no longer " + getName(), BattleEvent.MSG_NOTICE )); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " is no longer " + getName(), BattleEvent.MSG_NOTICE )); // .addMessage(target.getName() + " is no longer " + getName(), BattleEvent.MSG_NOTICE );
          
    }

    public Ability getAbility() {
        return null;
    }

}