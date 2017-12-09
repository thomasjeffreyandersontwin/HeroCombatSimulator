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
public class effectEND extends Effect {

    /** Creates new effectUnconscious */
    public effectEND(String abilityName, int end) {
        super ( "END", "INSTANT", true );
       // addSubeffectInfo( "END", "END", "NONE", "NONE", "STAT", "END", new Integer(end));
        add("Ability.NAME",  abilityName) ;
    }

    protected void applySubEffects(BattleEvent be, Target target)
    throws BattleEventException {
        int count = getIndexedSize("Subeffect");

        int i;
        String type, versustype, name,stat;
        Object versus;
        int currentstat,basestat;
        int value;

        for (i=0;i<count;i++) {
            if (
            (type = getIndexedStringValue( i, "Subeffect", "EFFECTTYPE" )) == null
            || getIndexedDoubleValue( i, "Subeffect", "VALUE" ) == null
            || (versustype = getIndexedStringValue( i, "Subeffect", "VERSUSTYPE" )) == null
            || (versus = getIndexedValue( i, "Subeffect", "VERSUS" )) == null
            || (name = getStringValue("Ability.NAME" )) == null
            ) {
                continue;
            }

            value = ((Double)getIndexedValue( i, "Subeffect", "VALUE")).intValue();
            
            if ( versustype.equals("STAT" ) ){
                // It is versus a STAT.  Find the STAT name and current value of Target
                stat = (String)versus;
                
                if ( ! target.hasStat( stat ) ) {
                    continue;
                }
                else {
                    currentstat = target.getCurrentStat( stat) ;
                    basestat = target.getBaseStat( stat);
                }
                addIndexed(i,  "Subeffect", "UNDOADD", new Integer(currentstat), true );

                if ( type.equals("END" ) ){
                    // Apply Damage to Target
                    int con;
                    int newstat = currentstat - value;

                    if ( newstat < 0 ) throw new BattleEventException( "Not Enough Endurance.  Needed " + Integer.toString(value) +" END for " + name +".");

                    //target.add(   stat, "CURRENTSTAT", new Integer (newstat), true);
                    target.setCurrentStat( stat, newstat );

                    if ( value > 0 ) {
                        be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( target.getName() + " lost " + Integer.toString(value) + " END using " + name + ". " +
                        target.getName() + "'s END is currently at " + Integer.toString(newstat) + ".",
                        BattleEvent.MSG_END));
                    }
                }
            }
        }
    }
}