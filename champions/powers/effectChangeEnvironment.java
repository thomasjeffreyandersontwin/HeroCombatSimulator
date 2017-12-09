/*
 * effectFlash.java
 *
 * Created on April 22, 2001, 7:53 PM
 */

package champions.powers;

import champions.Ability;
import champions.BattleEvent;
import champions.CVList;
import champions.Effect;
import champions.LinkedEffect;
import champions.Target;
import champions.exception.BattleEventException;




/**
 *
 * @author  twalker
 * @version
 */

public class effectChangeEnvironment extends LinkedEffect {
    
    
    
    
    public effectChangeEnvironment(Ability ability ) {
        super(ability.getName(), "LINKED");
        setAbility(ability);
        //this.ability = ability;
        //Integer ocvbonus = (Integer)ability.parseParameter( parameterArray, "OCVBonus" );
        
        
        //setSense(sense);
        //this.add("effect.DURATION",duration );
        //this.add("effect.TARGETING",targeting );
    }
    
    public boolean addEffect(BattleEvent be, Target t) throws BattleEventException {
        //Integer ocvbonus = (Integer)ability.parseParameter( parameterArray, "OCVBonus" );
        Effect effect;
        Ability effectAbility = getAbility();
        Integer ocvmodifier = effectAbility.getIntegerValue("Power.OCVMODIFIER" );
        
        be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( t.getName() + " has entered " + effectAbility.getName() + ".  OCV will be modified by " +  ocvmodifier + ".", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( t.getName() + " has entered " + effectAbility.getName() + ".  OCV will be modified by " +  ocvmodifier + ".", BattleEvent.MSG_NOTICE)); // .addMessage( t.getName() + " has entered " + effectAbility.getName() + ".  OCV will be modified by " +  ocvmodifier + ".", BattleEvent.MSG_NOTICE);
        return super.addEffect(be,t);
    }
    

    public void addOCVAttackModifiers( CVList cvList , Ability attack) {
        Ability effectAbility = getAbility();
        Integer ocvmodifier = effectAbility.getIntegerValue("Power.OCVMODIFIER" );
 
        if ( ocvmodifier.intValue() != 0 ) {
            cvList.addSourceCVModifier( this.getName(), ocvmodifier.intValue());
        }
    }

    public String getDescription() {
        boolean targeting = getBooleanValue("effect.TARGETING");
        Ability effectAbility = getAbility();
        Integer ocvmodifier = effectAbility.getIntegerValue("Power.OCVMODIFIER" );
        
        return effectAbility.getName() + "\n OCV modifier: " + ocvmodifier;
        
    }
            /** Getter for property sense.
             * @return Value of property sense.
             */
    public String getSense() {
        return getStringValue( "effect.SENSE");
    }
    /** Setter for property sense.
     * @param sense New value of property sense.
     */
    public void setSense(String sense) {
        add( "effect.SENSE",sense,true);
    }

    
}
