/*
 * effectFlash.java
 *
 * Created on April 22, 2001, 7:53 PM
 */

package champions.powers;

import champions.*;
import champions.exception.BattleEventException;


/**
 *
 * @author  twalker
 * @version
 */
public class effectDarkness extends LinkedEffect{
    
    public effectDarkness( Ability ability ) {
        super("Darknessed", "LINKED" );
        setAbility(ability);
    }
    
    public boolean addEffect(BattleEvent be, Target t) throws BattleEventException {
        int index,count;
        Effect effect;
    
        be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( t.getName() + " is in Darkness.", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( t.getName() + " is in Darkness.", BattleEvent.MSG_NOTICE)); // .addMessage( t.getName() + " is in Darkness.", BattleEvent.MSG_NOTICE);

        //  t.flashSense(this,getSense());
        
        Ability ability = getAbility();
        if ( getIndexedSize("SensePenalty") == 0 ) {
            // Generate and apply on for each sense
            count = ability.getIndexedSize("Sense");
            for(int i = 0; i < count; i++) {
                Sense s = (Sense)ability.getIndexedValue(i, "Sense", "SENSE");
                SensePenaltyModifier spm = new SensePenaltyModifier("Flashed", s.getSenseName());
                spm.setFunctioningPenalty(true);
                createIndexed("SensePenalty", "MODIFIER",spm, false);
                be.addUndoableEvent(t.addSensePenalty(spm));
            }
        }
        else {
            count = getIndexedSize("SensePenalty");
            for(int i = 0; i < count; i++) {
                SensePenaltyModifier spm = (SensePenaltyModifier)getIndexedValue(i, "SensePenalty", "MODIFIER");
                be.addUndoableEvent(t.addSensePenalty(spm));
            }
        }
        
        return super.addEffect(be,t);
    }
    
    public void removeEffect(BattleEvent be, Target target)
    throws BattleEventException {
        int count = getIndexedSize("SensePenalty");
        for(int i = 0; i < count; i++) {
            SensePenaltyModifier spm = (SensePenaltyModifier)getIndexedValue(i, "SensePenalty", "MODIFIER");
            be.addUndoableEvent(target.removeSensePenalty(spm));
        }
        super.removeEffect(be,target);
    }

    public String getDescription() {
        
        
        String s = "It is really dark.";
        return s;
    }

    
}
