/*
 * effectUnconscious.java
 *
 * Created on October 2, 2000, 4:06 PM
 */

package champions.powers;

import champions.Ability;
import champions.BattleEvent;
import champions.ChampionsUtilities;
import champions.LinkedEffect;
import champions.Target;
import champions.battleMessage.DCSummaryMessage;
import champions.exception.BattleEventException;
import champions.battleMessage.GenericSummaryMessage;


/**
 *
 * @author  unknown
 * @version
 */
public class effectMartialDC extends LinkedEffect{
    static final long serialVersionUID = -5345632949586802371L;
    
    
    /** Creates new effectUnconscious */
    public effectMartialDC(String name,Ability ability) {
        super ( name, "LINKED", true );
        setAbility(ability);
        setHidden(true);
    }
    
    public String getDescription() {
        return ChampionsUtilities.toSignedString( getLevels() ) + " Martial Damage Classes";
    }
    
    /** Getter for property sense.
     * @return Value of property sense.
     */
    public Integer getLevels() {
        return getAbility().getIntegerValue("Power.LEVEL");
    }
    /** Setter for property sense.
     * @param sense New value of property sense.
     */

    
    public boolean addEffect(BattleEvent be, Target target)
            throws BattleEventException {
        if (super.addEffect(be, target)) {
            //be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " is now " + getName(), BattleEvent.MSG_NOTICE )); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " is now " + getName(), BattleEvent.MSG_NOTICE )); // .addMessage(target.getName() + " is now " + getName(), BattleEvent.MSG_NOTICE );
            be.addBattleMessage(new DCSummaryMessage(target, "Martial", getLevels()));
            return true;
        }
        return false;
    }

    public void removeEffect(BattleEvent be, Target target)
            throws BattleEventException {
        super.removeEffect(be, target);
        //be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " is no longer " + getName(), BattleEvent.MSG_NOTICE )); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " is no longer " + getName(), BattleEvent.MSG_NOTICE )); // .addMessage(target.getName() + " is no longer " + getName(), BattleEvent.MSG_NOTICE );
        be.addBattleMessage(new DCSummaryMessage(target, "Martial", - getLevels()));
    }
    
    public void adjustDice(BattleEvent be, String targetGroup) {
        Ability ability = be.getAbility();
        Ability effectAbility = getAbility();
        Ability maneuver = be.getManeuver();
        
        int index,count;
        Ability a;
        //  boolean activate = false;
        
        // Check for Abilities which may not be exactly equal
       /* if (activate == false) {
            count = effectAbility.getIndexedSize( "CanUseDC" );
            for ( index=0;index<count;index++) {
                a = (Ability)effectAbility.getIndexedValue( index, "CanUseDC", "ABILITY" );
                if ( ability.equals(a) ) {
                    activate = true;
                    break;
                }
                if ( maneuver != null && maneuver.equals(a) ) {
                    activate = true;
                    break;
                }
            } */
        if ( ability.is("MARTIALMANEUVER") || ( maneuver != null && maneuver.is("MARTIALMANEUVER"))) {
            
            Double dc = be.getDoubleValue("Martial.DC" );
            if ( dc == null ) {
                dc = new Double ( (double)getLevels().intValue() );
            }
            else {
                dc = new Double ( dc.doubleValue() + getLevels().intValue() );
            }
            be.addBattleMessage( new GenericSummaryMessage(be.getSource(), " has " +
                    getName() + " adding " + ChampionsUtilities.toSignedString( getLevels() ) + " Martial DC(s)."));
            
            be.add("Martial.DC",  dc, true);
        }
    }
}