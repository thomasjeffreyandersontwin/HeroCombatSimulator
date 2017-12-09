/*
 * effectDamageReduction.java
 *
 * Created on April 22, 2001, 7:40 PM
 */
package champions.powers;

import champions.Ability;
import champions.BattleEvent;
import champions.DefenseList;
import champions.LinkedEffect;
import champions.Target;
import champions.battleMessage.DRSummaryMessage;
import champions.enums.DefenseType;
import champions.exception.BattleEventException;


/**
 *
 * @author  twalker
 * @version
 */
public class effectDamageReduction extends LinkedEffect{
    
    
    effectDamageReduction(Ability ability, String name, Integer percent, String type, boolean resistant) {
        super(name, "LINKED", false);
        add("Effect.DAMAGETYPE", type);
        add("Effect.RESISTANT", (resistant) ? "TRUE" : "FALSE");
        add("Effect.PERCENT", percent);
        setAbility(ability);
    }

    /*  public void postdefense(BattleEvent be, Effect effect)
    throws BattleEventException {
    int i, count;
    String type, defense,special;
    Double value, newValue;
    Object versus;
    
    String damageType = this.getStringValue("Effect.DAMAGETYPE");
    Integer percent = this.getIntegerValue("Effect.PERCENT");
    boolean resistant = this.getBooleanValue("Effect.RESISTANT");
    
    count = effect.getIndexedSize("Subeffect");
    for ( i=0;i<count;i++) {
    if (
    (type = effect.getIndexedStringValue( i, "Subeffect", "EFFECTTYPE" )) != null
    && (value = effect.getIndexedDoubleValue( i, "Subeffect", "VALUE" )) != null
    && (defense = effect.getIndexedStringValue( i, "Subeffect", "DEFTYPE" )) != null
    && (special = effect.getIndexedStringValue( i, "Subeffect", "DEFSPECIAL" )) != null
    && (versus = effect.getIndexedValue( i, "Subeffect", "VERSUS" )) != null
    ) {
    if ( value.intValue() == 0 ) continue;
    if ( defense.equals(damageType) == false ) continue;
    if ( special.equals("NORMAL")
    || special.equals("NND")
    || special.equals("AVDL")
    ||  ( special.equals("KILLING") && resistant == true )
    ){
    newValue = new Double( Math.round( value.doubleValue() * (100 - percent.doubleValue()) / 100.0 ));
    if (! value.equals( newValue ) ) {
    effect.addIndexed( i, "Subeffect","VALUE",newValue, true);
    be.addMessage( this.getName() + " reduced " + versus.toString() + " effect from " + value.toString()
    + " to " + newValue.toString(), BattleEvent.MSG_ABILITY );
    }
    }
    }
    }
    
    } */
    public boolean addEffect(BattleEvent be, Target target)
            throws BattleEventException {
        if (super.addEffect(be, target)) {
            String damageType = this.getStringValue("Effect.DAMAGETYPE");
            Integer percent = this.getIntegerValue("Effect.PERCENT");
            boolean resistant = this.getBooleanValue("Effect.RESISTANT");

            double pd = damageType.equals("PD") ? percent / 100.0 : 0;
            double rpd = resistant && damageType.equals("PD") ? percent / 100.0 : 0;

            double ed = damageType.equals("ED") ? percent / 100.0 : 0;
            double red = resistant && damageType.equals("ED") ? percent / 100.0 : 0;

            be.addBattleMessage(new DRSummaryMessage(target, true, pd, rpd, ed, red));

            return true;
        }
        return false;
    }

    public void removeEffect(BattleEvent be, Target target)
            throws BattleEventException {
        super.removeEffect(be, target);

        String damageType = this.getStringValue("Effect.DAMAGETYPE");
        Integer percent = this.getIntegerValue("Effect.PERCENT");
        boolean resistant = this.getBooleanValue("Effect.RESISTANT");

        double pd = damageType.equals("PD") ? percent / 100.0 : 0;
        double rpd = resistant && damageType.equals("PD") ? percent / 100.0 : 0;

        double ed = damageType.equals("ED") ? percent / 100.0 : 0;
        double red = resistant && damageType.equals("ED") ? percent / 100.0 : 0;

        be.addBattleMessage(new DRSummaryMessage(target, false, pd, rpd, ed, red));
    }

    public String getDescription() {
        String damageType = this.getStringValue("Effect.DAMAGETYPE");
        Integer percent = this.getIntegerValue("Effect.PERCENT");
        boolean resistant = this.getBooleanValue("Effect.RESISTANT");

        String resString = (resistant) ? ", resistant" : "";
        return percent.toString() + "% " + "Damage Reduction (" + damageType + ")" + resString;
    }

    public void addDefenseModifiers(DefenseList dl, String defense) {
        String damageType = this.getStringValue("Effect.DAMAGETYPE");
        Integer percent = this.getIntegerValue("Effect.PERCENT");
        boolean resistant = this.getBooleanValue("Effect.RESISTANT");

        if (defense.equals(damageType) || (resistant && defense.equals("r" + damageType))) {
            DefenseType dt = DefenseType.valueOf(defense);
            dl.addDefenseMultiplier(dt, getName(), (1 - (percent.intValue() / 100.0)));
        }
    }

} // End of effectDamageReduction
