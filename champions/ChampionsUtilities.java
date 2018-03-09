/*
 * ChampionsUtilities.java
 *
 * Created on November 10, 2000, 6:16 PM
 */

package champions;

import champions.attackTree.MovementManeuverSetupPanel;
import champions.exception.BadDiceException;
import champions.interfaces.ChampionsConstants;
import champions.powers.SpecialParameterIsMartialManeuver;
import champions.powers.powerHandToHandAttack;
import java.awt.Color;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.util.Hashtable;

import com.sun.org.glassfish.external.statistics.impl.AverageRangeStatisticImpl;
/**
 *
 * @author  unknown
 * @version
 */
public class ChampionsUtilities extends Object
implements ChampionsConstants {
    
    static private Hashtable statRoundDirection;
    
    static {
        //System.out.println( "Loading Hashtables" );
        statRoundDirection = new Hashtable();
        statRoundDirection.put( "STR", new Integer(1) );
        statRoundDirection.put( "DEX", new Integer(1) );
        statRoundDirection.put( "CON", new Integer(1) );
        statRoundDirection.put( "BODY",new Integer(1) );
        statRoundDirection.put( "INT", new Integer(1) );
        statRoundDirection.put( "EGO", new Integer(1) );
        statRoundDirection.put( "PRE", new Integer(1) );
        statRoundDirection.put( "COM", new Integer(1) );
        statRoundDirection.put( "PD", new Integer(1) );
        statRoundDirection.put( "ED", new Integer(1) );
        statRoundDirection.put( "SPD", new Integer(1) );
        statRoundDirection.put( "REC", new Integer(1) );
        statRoundDirection.put( "END", new Integer(1) );
        statRoundDirection.put( "STUN", new Integer(1) );
        //1 line added by PR
        statRoundDirection.put( "MD", new Integer(1) );
        statRoundDirection.put( "DCV", new Integer(1) );
        statRoundDirection.put( "OCV", new Integer(1) );
        statRoundDirection.put( "ECV", new Integer(1) );
    }
    
    /** Creates new ChampionsUtilities */
    public ChampionsUtilities() {
    }
    
    static public Dice strToDamage(int activeCost, Dice base, int Str, boolean unlimitedDC) {
        double dc = Str / 5.0;
        
        if ( Math.floor( dc ) < Math.round( dc ) ) dc = Math.floor(dc) + 0.5;
        else dc = Math.floor(dc);
        
        Dice dice = dcToDamage(activeCost, base, dc, unlimitedDC);
        
        return dice;
    }
    
    static public int strToEnd(int activeCost, Dice base, int Str, boolean unlimitedDC) {
        double dc = Str / 5.0;
        double maxDC;
        
        if ( Math.floor( dc ) < Math.round( dc ) ) dc = Math.floor(dc) + 0.5;
        else dc = Math.floor(dc);
        
        if ( unlimitedDC == false ) {
            maxDC = maxDCforAttack(activeCost, base);
            
            if ( dc > maxDC ) dc = maxDC;
        }
        
        return (int)Math.round( dc / 2 );
    }
    
    static public Dice dcToDamage(int activeCost, Dice base, double dc, boolean unlimitedDC) {
        Dice realDice;
        double dicePerDC, baseD6, newD6, realD6, realDC, baseDC;
        
        try {
            baseD6 = base.getD6() + ( base.isOnehalf() ? 0.5 : 0.0 );
            if ( activeCost == 0 ) {
                dicePerDC = 1;
            }
            else {
                dicePerDC = baseD6 * 5 / activeCost;
            }
            
            baseDC = baseD6 / dicePerDC;
            
            
            if (unlimitedDC == false && dc > baseDC ) dc = baseDC;
            
            newD6 = dc * dicePerDC;
            
            if ( Math.floor(newD6) < Math.round(newD6) ) realD6 = Math.floor(newD6) + 0.5;
            else realD6 = Math.floor(newD6);
            
            realDC = Math.round(realD6 / dicePerDC);
            
            int plus = 0;
            if ( Math.round(dc) > realDC ) plus = (int)(Math.round(dc) - realDC);
            
            realDice = new Dice( Double.toString(realD6) +"d6" ,false );
            realDice.setPlus( plus );
        }
        catch ( BadDiceException bde ) {
            realDice = new Dice(0);
        }
        return realDice;
    }
    
    static public double maxDCforAttack(int activeCost, Dice base) {
        
        double dicePerDC, baseD6, baseDC;
        
        baseD6 = base.getD6() + ( base.isOnehalf() ? 0.5 : 0.0 );
        if ( activeCost == 0 ) {
            dicePerDC = 1;
        }
        else {
            dicePerDC = baseD6 * 5 / activeCost;
        }
        
        baseDC = baseD6 / dicePerDC;
        
        if ( Math.floor( baseDC ) < Math.round(baseDC) ) baseDC = Math.round(baseDC) + 0.5;
        else baseDC = Math.round(baseDC);
        
        return baseDC;
        
    }
    
    static public int strToEnd(int str) {
        if ( isHeroic() ) {
            return str/5;
        }
        else {
            return str/10;
        }
    }
    
    static public String toSignedString(Integer i) {
        if ( i == null ) return "0";
        return ( i.intValue() >= 0 )? "+" + i.toString() : i.toString();
        //return i.toString();
    }
    
    static public String toSignedString(int i) {
        return ( i>= 0 )? "+" + Integer.toString(i) : Integer.toString(i);
        //return i.toString();
    }
    
    /** Round <code>value</code> properly based upon <code>stat</code>.
     *
     * This method will round the input value, using normal rounding rules.  However, if
     * the value is a .5 increment exactly, the value will be round in the direction which is
     * beneficial to the character, based upon the indicated stat.
     *
     * Speed is rounded using the special speed rounding rules.
     *
     * @param stat Stat indicating direction to round.
     * @param value Value to be rounded.
     * @return Properly rounded value based upon <CODE>stat</CODE>
     */
    static public double roundStat(String stat, double value) {
        if ( stat.equals("SPD") ){
            value = Math.floor( value * 10 ) / 10;
        }
        else if ( value == Math.floor(value) + 0.5 ) {
            if ( statRoundDirection.get(stat) != null && ((Integer)statRoundDirection.get(stat)).intValue() == 1 ) {
                value = Math.ceil(value);
            }
            else {
                value = Math.floor(value);
            }
        }
        else {
            value = Math.round(value);
        }
        return value;
    }
    
    /** Round <code>value</code> properly based upon direction <code>up</code>.
     *
     * This method will round the input value, using normal rounding rules, in
     * most cases.<P>
     *
     * However, if the value is a .5 increment exactly, the value will be round in the direction
     * indicated by up.  It will be rounded to the next highest integer if up is true
     * and down if up is false.
     * @param value Value to be rounded.
     * @param up Direction to round if <CODE>value</CODE> is .5 increment.
     * @return Properly rounded value, based upon <CODE>up</CODE>.
     */
    static public int roundValue(double value, boolean up) {
        if ( value == Math.floor(value) + 0.5 ) {
            if ( up ) {
                value = Math.ceil(value);
            }
            else {
                value = Math.floor(value);
            }
        }
        else {
            value = Math.round(value);
        }
        return (int)value;
    }
    
    static public Dice DCToKillingDice( int dc ) {
        Dice dice = new Dice();
        dice.setD6(dc / 3);
        if ( dc % 3 == 1 ) {
            dice.setPlus(1);
        }
        if ( dc % 3 == 2 ) {
            dice.setOnehalf(true);
        }
        return dice;
    }
    
    static public Dice DCToNormalDice( double dc ) {
        Dice dice;
        try {
            dice = new Dice( Double.toString(dc) + "d6",false);
            return dice;
        }
        catch ( BadDiceException bde ) {
            return new Dice(0);
        }
        
    }
    
    static public String DCToKillingString( int dc ) {
        String s;
        s = Integer.toString(dc / 3);
        if ( dc % 3 == 2 ) {
            s = s + ".5";
        }
        s = s + "d6";
        if ( dc % 3 == 1 ) {
            s = s + "+1";
        }
        
        return s;
    }
    
    static public String DCToNormalString( double dc ) {
        if ( dc + .5 == Math.floor( dc ) ) return Double.toString(dc) + "d6";
        return Integer.toString((int)dc) + "d6";
    }
    
    static public double StringToNormalDC( String die ) {
        try {
            Dice dice = new Dice(die);
            return dice.getD6() + ( ( dice.isOnehalf() ) ? 0.5 : 0 ) ;
        }
        catch ( BadDiceException bde ) {
        }
        return 0;
    }
    
    static public double StringToKillingDC( String die ) {
        try {
            Dice dice = new Dice(die);
            double dc = 0;
            dc += dice.getD6() * 3;
            dc += ( ( dice.isOnehalf() ) ? 2 : 0 ) ;
            dc += ( ( dice.getPlus() >= 1 ) ? 1 : 0 );
            dc += ( ( dice.getPlus() <= -1 ) ? -1 : 0 );
            return dc;
        }
        catch ( BadDiceException bde ) {
        }
        return 0;
    }
    
    static public double strToDCs(int Str) {
        double dc = Str / 5.0;
        
        if ( Math.floor( dc ) < Math.round( dc ) ) dc = Math.floor(dc) + 0.5;
        else dc = Math.floor(dc);
        
        return dc;
    }
    
    /** Round <code>in</code> to Nearest .5 increment.
     *
     * This method rounds the input parameter to the nearest 0.5 increment.  It uses
     * normal rounding rules, applied at .5 increment, instead of integer increments.
     */
    static public double roundHalf( double in ) {
        return Math.round( in * 2.0 ) / 2.0;
    }
    
    static public void calculateDCs(BattleEvent be) {
        calculateDCs(be, null);
    }
    
    static public void calculateDCs(BattleEvent be, String targetGroup) {
        Ability ability = be.getAbility();
        Ability maneuver = be.getManeuver();
        boolean isHeroic;
        String h;
        int dindex;
        
        //dindex = be.findIndexed("Die","NAME", "DamageDie");
        dindex = be.getDiceIndex("DamageDie", targetGroup);
        
        if ( (h = (String)Preferences.getPreferenceList().getParameterValue("RuleSet" ) ) != null
        && h.equals( "Heroic" ) ) {
            isHeroic = true;
        }
        else {
            isHeroic = false;
        }
        
        Double baseAbilityDC, maneuverDC, martialDC, combatDC, tempdb, movementDC;
        Double baseAbilityPointsPerDC;
        double baseAbilityPointsPerDCWithAdv,realStrMultiplier, otherDCModifiers;
        Double baseAbilityAdvCost,originalStrMultiple;
        int index, count;
        
        double totalDC;
        
        Integer normalStr, pushedStr, minimumStr;
        int pushedStrUsed = 0;
        
        
        // Read all the variable in
        baseAbilityDC = be.getDoubleValue("Base.DC" );
        maneuverDC = be.getDoubleValue("Maneuver.DC" );
        martialDC = be.getDoubleValue("Martial.DC" );
        combatDC = be.getDoubleValue("Combat.DC" );
        movementDC = be.getDoubleValue("Movement.DC");
        
        normalStr = be.getIntegerValue("Normal.STR" );
        pushedStr = be.getIntegerValue("Pushed.STR" );
        minimumStr = be.getIntegerValue("Minimum.STR" );
        originalStrMultiple = be.getDoubleValue("Multiplier.STR");
        
        baseAbilityPointsPerDC = ability.getDoubleValue("Ability.PPDC" );
        
        boolean isWeapon = ability.isWeapon();
        boolean isKilling = ability.isKillingAttack();
        boolean isAbilityMartial = ability.hasSpecialParameter(SpecialParameterIsMartialManeuver.specialParameterName);
        
        boolean isStrengthApplicable = be.strengthAddsToDC();
        
        baseAbilityAdvCost = ability.getAdvDCCost() *ability.reduction;;
        if ( baseAbilityAdvCost == null ) baseAbilityAdvCost = new Double(0);
        
//        maneuverAdvCost = null;
//        if ( maneuver != null ) {
//            maneuverAdvCost = maneuver.getAdvDCCost();
//            baseAbilityPointsPerDC = maneuver.getDoubleValue("Ability.PPDC" );
//            if ( ability.findAdvantage( "No Normal Defense" ) != -1 && maneuver.findAdvantage("No Normal Defense" ) != -1 ) {
//                maneuverAdvCost = new Double(maneuverAdvCost.doubleValue() - 0.5);
//            }
//        }
        
        // Load the Final modifiers to Damage Dice
        otherDCModifiers = 0;
        count = be.getIndexedSize( "DCModifier" );
        for(index = 0; index < count; index ++ ) {
            if ( ( tempdb = be.getIndexedDoubleValue(index, "DCModifier","MODIFIER" ) ) != null ) {
                otherDCModifiers += tempdb.doubleValue();
            }
        }
        
        if ( targetGroup != null ) {
            ActivationInfo ai = be.getActivationInfo();
            int tgindex = ai.getTargetGroupIndex(targetGroup);
            otherDCModifiers += ai.getTargetGroupDCModifier(tgindex);
        }
        
//        if ( maneuverAdvCost == null ) maneuverAdvCost = new Double(0);
        
        if ( baseAbilityDC == null ) baseAbilityDC = new Double(0);
        if ( baseAbilityPointsPerDC == null ) baseAbilityPointsPerDC = new Double(5);
        
        if ( martialDC == null ) martialDC = new Double(0);
        if ( combatDC == null ) combatDC = new Double(0);
        if ( maneuverDC == null ) maneuverDC = new Double(0);
        if ( movementDC == null ) movementDC = new Double(0);
        
        if ( normalStr == null ) normalStr = new Integer(0);
        if ( pushedStr == null ) pushedStr = new Integer(0);
        if ( minimumStr == null ) minimumStr = new Integer(0);
        
        
        if ( isStrengthApplicable == false ) {
            normalStr = 0;
            pushedStr = 0;
        }
        
        // Calculate the Real Pts Per DC
        //baseAbilityPointsPerDCWithAdv = baseAbilityPointsPerDC.doubleValue() * ( 1 + baseAbilityAdvCost.doubleValue() + maneuverAdvCost.doubleValue() );
        baseAbilityPointsPerDCWithAdv = baseAbilityPointsPerDC.doubleValue() * ( 1 + baseAbilityAdvCost.doubleValue());
        
        boolean isAdvantageBound = baseAbilityAdvCost > 0;
        int advantageBoundPoints = (int)Math.round(baseAbilityDC * baseAbilityPointsPerDC);
        
        // This is the maximum amount of str applied total
        int maximumStrength = Integer.MAX_VALUE;
        double normalStrUsed = 0;
        
        if ( isAdvantageBound && isHeroic == false && isKilling == false ) {
            // We can only use STR limited to unadvantaged (base) power cost
            maximumStrength = advantageBoundPoints;
        }
        
        be.add("Maximum.STR", maximumStrength, true);
        
        realStrMultiplier = originalStrMultiple == null ? 1 : originalStrMultiple.doubleValue();
        
        if ( movementDC > 0 && isWeapon ) {
            realStrMultiplier = 0.5;
        }
        
        be.add("PPDC.STR", baseAbilityPointsPerDC, true);
        be.add("RealMultipler.STR", realStrMultiplier, true);
        
        double adjustedBaseDC = 0;
        
        if ( ability.isManeuver() == false ) {
            // For ability, determined by ability DCs   - Source: Base.DC
            // For weapon, determined by weapon damage  - Source: Base.DC
            adjustedBaseDC = baseAbilityDC;
            
            be.add("AdjustedBase.BASE_DC", baseAbilityDC, true);
            
            if ( ability.getPower() instanceof powerHandToHandAttack ) {
            	
                // This isn't a killing attack, BTW
                // Special STR rule for HA power...
                double availableStr = Math.min(maximumStrength, Math.max(0, normalStr - minimumStr) ); 
                availableStr = normalStr;
                double strDC;

                // You can have 1/2 DCs, so you have to round by 0.5
                strDC = Math.floor(2 * availableStr * realStrMultiplier / baseAbilityPointsPerDC) / 2;
                strDC = Math.floor(2 * availableStr * realStrMultiplier / baseAbilityPointsPerDC) / 2;
                double usedStr = strDC / realStrMultiplier * baseAbilityPointsPerDC;
                
                adjustedBaseDC += strDC;
                normalStrUsed = usedStr + minimumStr;
                minimumStr = 0;
                
                be.add("AdjustedBase.STR_USED", normalStrUsed, true);
                be.add("AdjustedBase.STR_DC", strDC, true);
            }
            
            if ( isAbilityMartial ) {
                // Martial abilities get their Martial DC as base
                if ( isKilling ) {
                    double martialDCUsed = Math.floor(martialDC/2);
                    adjustedBaseDC += martialDCUsed;
                    be.add("AdjustedBase.MARTIAL_DC", martialDCUsed, true);
                    martialDC = 0.0; // zero this out so it won't be used later
                }
                else {
                    adjustedBaseDC += martialDC;
                    be.add("AdjustedBase.MARTIAL_DC", martialDC, true);
                    martialDC = 0.0; // zero this out so it won't be used later
                }
            }
        }
        else {
            if ( isKilling ) {
                // determined by Maneuver.DC + Martial DC
                double martialDCUsed = Math.floor(martialDC/2);
                adjustedBaseDC = maneuverDC + martialDCUsed;
                be.add("AdjustedBase.BASE_DC", 0, true);
                be.add("AdjustedBase.MANEUVER_DC", maneuverDC, true);
                be.add("AdjustedBase.MARTIAL_DC", martialDCUsed, true);
                maneuverDC = 0.0; // Zero this out so it won't be used later...
                martialDC = 0.0; // Zero this out so it won't be used later...
            }
            else {
                // determined by STR + Extra Martial DC
                double availableStr = Math.min(maximumStrength, Math.max(0, normalStr - minimumStr) ); 
                // You can have 1/2 DCs, so round by 0.5
                double strDC = Math.floor(2 * availableStr * realStrMultiplier / baseAbilityPointsPerDC)/2;
                double usedStr = strDC / realStrMultiplier * baseAbilityPointsPerDC;
                normalStrUsed = usedStr + minimumStr;
                minimumStr = 0;
                
                adjustedBaseDC = martialDC + strDC;

                be.add("AdjustedBase.BASE_DC", 0, true);
                be.add("AdjustedBase.MARTIAL_DC", martialDC, true);
                be.add("AdjustedBase.STR_USED", normalStrUsed, true);
                be.add("AdjustedBase.STR_DC", strDC, true);

                martialDC = 0.0; // Zero this out so it won't be used later...
            }
        }
        
        be.add("AdjustedBase.DC", adjustedBaseDC, true);
        
        double maximumDC;
        
        if ( isHeroic || isKilling ) {
            // In all combinations of killing and heroic, 
            // maximum DC is 2 x AdjustedBase.  However,
            // movement powers can exceed this limit.
            maximumDC = adjustedBaseDC * 2;
        }
        else {
            // In the case of a superheroic normal attack, 
            // there is no maximum.  
            maximumDC = Double.MAX_VALUE;
        }
        
        be.add("Maximum.DC", maximumDC, true);
        
        totalDC = adjustedBaseDC;
        
        if ( otherDCModifiers > 0 ) {
            // We don't know what this come from, so we will just add to total, limited by max
            double otherDCModifiersUsed = Math.max(0, Math.min(maximumDC - totalDC, otherDCModifiers));
            be.add("Bonus.MISC_DC", otherDCModifiersUsed, true);
            totalDC += otherDCModifiers;
        }
        else if ( otherDCModifiers < 0 ) {
            // We don't know what this come from, so we are just going to penalize the total dc
            be.add("Bonus.MISC_DC", otherDCModifiers, true);
            totalDC += otherDCModifiers;
        }
        
        if ( combatDC > 0 ) {
            if ( isHeroic ) {
                // Heroic 2 CSL = 1 DC
                double combatDCUsed = Math.max(0, Math.min(maximumDC - totalDC, combatDC));
                be.add("Bonus.COMBAT_DC", combatDCUsed, true);
                totalDC += combatDCUsed;
            }
            else {
                // For superheroic, there is a damage bonus of 
                // either +1 Body for KA, or +3 STUN for normal
                be.add("Bonus.COMBAT_DC", 0.0, true);
                if ( isKilling ) {
                    be.add("Bonus.BODY", new Integer( (int) combatDC.doubleValue() ), true);
                }
                else {
                    be.add("Bonus.STUN", new Integer( (int) combatDC.doubleValue() * 3 ), true);
                }
            }
        }
        
        if ( maneuverDC > 0 ) {
            if ( isKilling ) {
                double maneuverDCUsed = Math.max(0, Math.min(maximumDC - totalDC, Math.floor(maneuverDC / 2)));
                be.add("Bonus.MANEUVER_DC", maneuverDCUsed, true);
                totalDC += maneuverDCUsed;
            }
            else {
                double maneuverDCUsed = Math.max(0, Math.min(maximumDC - totalDC, Math.floor(maneuverDC)));
                be.add("Bonus.MANEUVER_DC", maneuverDCUsed, true);
                totalDC += maneuverDCUsed;
            }
        }
        
        if ( martialDC > 0 ) {
            if ( isKilling ) {
                double dcUsed = Math.max(0, Math.min(maximumDC - totalDC, Math.floor(martialDC / 2)));
                be.add("Bonus.MARTIAL_DC", dcUsed, true);
                totalDC += dcUsed;
            }
            else {
                double dcUsed = Math.max(0, Math.min(maximumDC - totalDC, martialDC));
                be.add("Bonus.MARTIAL_DC", dcUsed, true);
                totalDC += dcUsed;
            }
        }
        
        if ( movementDC > 0 && isKilling ) {
            // Killing +1 DC per movement DC, can not exceed max
            // Add this prior to strength...no need to waste end on str when you
            // already have the DCs from movement...
            double dcUsed = Math.max(0, Math.min(maximumDC - totalDC, Math.floor(movementDC * 5 / baseAbilityPointsPerDCWithAdv)));
            be.add("Bonus.MOVEMENT_DC", dcUsed, true);
            totalDC += dcUsed;
        }
        
        if ( normalStr > 0 || pushedStr > 0 ) {
            double maximumStrenthToUse = normalStr + pushedStr - minimumStr;  // Total strength that could be used for damage
            if ( isAdvantageBound ) {
                maximumStrenthToUse = Math.min(maximumStrenthToUse, advantageBoundPoints);
            }
            
            double strengthStillAvailable = Math.max(0, maximumStrenthToUse - normalStrUsed);  // Strength left for use
            
            if ( strengthStillAvailable > 0 ) {
                int strDCAvailable = (int)Math.floor(strengthStillAvailable * realStrMultiplier / baseAbilityPointsPerDC);
                double dcUsed = Math.max(0, Math.min(maximumDC - totalDC, strDCAvailable));
                int totalStrUsed = (int)(normalStrUsed + minimumStr + dcUsed / realStrMultiplier * baseAbilityPointsPerDC);

                int uncountedStrUsed = totalStrUsed - (int)normalStrUsed;
                int normalUsedHere = (int)Math.min(uncountedStrUsed,  normalStr - normalStrUsed);
                pushedStrUsed = totalStrUsed - (normalUsedHere + (int)normalStrUsed);
                
                normalStrUsed += normalUsedHere;

               double bonusStDCUsed = uncountedStrUsed * realStrMultiplier / baseAbilityPointsPerDC;
                //double pushedDCUsed = pushedStrUsed * realStrMultiplier / baseAbilityPointsPerDC;
                //double normalDCUsed = dcUsed - pushedDCUsed;
                
                totalDC += dcUsed;
                
                be.add("Bonus.STR_DC", bonusStDCUsed, true);
                //be.add("Bonus.PUSHED_DC", pushedDCUsed, true);
                be.add("Bonus.STR_USED", normalUsedHere, true);
                be.add("Bonus.PUSHED_USED", pushedStrUsed, true);
                
            }
        }
        
        if ( movementDC > 0 && isKilling == false ) {
            // Normal +1 DC per movement DC, can exceed max
            // When it is a normal attack, add movement bonuses after strength, since
            // strength only applies pre-limit but movement can exceed the limit
            be.add("Bonus.MOVEMENT_DC", Math.floor(movementDC * 5 / baseAbilityPointsPerDCWithAdv), true);
            totalDC += movementDC;
        }
        
        if ( isKilling ) {
            if ( dindex != -1 ) 
            	be.addIndexed(dindex, "Die","SIZE",  DCToKillingString((int)totalDC), true );
         //   else
            //	be.addIndexed(0, "Die","SIZE",  DCToKillingString((int)totalDC), true );
        }
        else {
            if ( dindex != -1 ) 
            	be.addIndexed(dindex, "Die","SIZE",  DCToNormalString((int)totalDC), true );
          //  else
          //  	be.addIndexed(0, "Die","SIZE",  DCToNormalString((int)totalDC), true );
        }
        
        // Add the final DC information to be
        //be.add("Base.DC", new Double( realdc ) , true );
        Integer distance=0;
        if(ability.usingMoveThrough) {
        	distance = be.getIntegerValue("MovementManeuver.MOVEDISTANCE");
            distance = ( distance == null ? 0 : distance.intValue());  
            distance = (distance/3);
            totalDC+=distance;
        }
       
        be.add("Total.DC", totalDC , true );
        be.add("Normal.STR_USED", (int)normalStrUsed , true );
        be.add("Pushed.STR_USED", pushedStrUsed , true );
    }
    
    static public boolean isHeroic() {
        String h;
        if ( (h = (String)Preferences.getPreferenceList().getParameterValue("RuleSet" ) ) != null
        && h.equals( "Heroic" ) ) {
            return true;
        }
        else {
            return  false;
        }
    }
    
    /** Returns the Cost of a Normal Dice Power Option.
     * The total Character Point cost of the option will be calculated and returned.
     */
    static public double calculateNormalDiceCost(int dcLevel, int cpPerLevel, int base) {
        return (double) ( cpPerLevel * ( dcLevel - base ) );
    }
    
    /** Returns the Maximum level which can still be used given a adjustment in available CP points.
     * The maximum level of Normal Dice will be calculated based on the current level,
     * and the amount of cp change that must occur.
     *
     * cpDelta is the amount of cp which must be removed or added to the option.  For example, if the power
     * was adjusted from 10 cp to 20 cp, the delta for this option might be +10cp.  However if two options
     * are sharing those adjusted points, the delta might be +5cp for this option and +5 cp for the other
     * option.
     *
     * minimumLevel can be used to specify whether buy-back options are appropriate.  If the option can be bought
     * back, specify some negative minimumLevel.  Otherwise, specify 0.
     */
    static public int calculateAdjustedNormalDiceCost(double cpDelta, int currentLevel, int cpPerLevel, int base, int minimumLevel) {
        int newLevel = (int)Math.floor( currentLevel + ( cpDelta / cpPerLevel ) );
        if ( newLevel < minimumLevel ) newLevel = minimumLevel;
        return newLevel;
    }
    
    /** Returns the Cost of a Killing Dice Power Option.
     * The total Character Point cost of the option will be calculated and returned.
     *
     * Note: The dcLevel is in Killing Damage Classes, not Killing dice, and the
     * cpPerLevel is based on DamageClasses also.  So, for a killing power which cost
     * 15pts per 1d6, the dcLevel for 1d6 is 3, and the cpPerLevel is 5 (15/3).
     *
     */
    static public double calculateKillingDiceCost(int dcLevel, int cpPerLevel, int base) {
        return (double) ( cpPerLevel * ( dcLevel - base ) );
    }
    
    /** Returns the Maximum level which can still be used given a adjustment in available CP points.
     * The maximum level of Killing Dice will be calculated based on the current level,
     * and the amount of cp change that must occur.
     *
     * Note: The currentLevel is in Killing Damage Classes, not Killing dice, and the
     * cpPerLevel is based on DamageClasses also.  So, for a killing power which cost
     * 15pts per 1d6, the dcLevel for 1d6 is 3, and the cpPerLevel is 5 (15/3).
     *
     * cpDelta is the amount of cp which must be removed or added to the option.  For example, if the power
     * was adjusted from 10 cp to 20 cp, the delta for this option might be +10cp.  However if two options
     * are sharing those adjusted points, the delta might be +5cp for this option and +5 cp for the other
     * option.
     *
     * minimumLevel can be used to specify whether buy-back options are appropriate.  If the option can be bought
     * back, specify some negative minimumLevel.  Otherwise, specify 0.
     */
    static public int calculateAdjustedKillingDiceCost(double cpDelta, int currentLevel, int cpPerLevel, int base, int minimumLevel) {
        int newLevel = (int)Math.floor( currentLevel + ( cpDelta / cpPerLevel ) );
        if ( newLevel < minimumLevel ) newLevel = minimumLevel;
        return newLevel;
    }
    
    /** Returns the Cost of a Geometric Power Option.
     * The total Character Point cost of the option will be calculated and returned.
     *
     * For powers which cost costPerXlevels cps for Xlevel points of power.  So, for Armor, it cost
     * 3 cps for 2 points of Armor.  Thus, costPerXlevels = 3, Xlevel = 2.  The base can be used to adjust
     * the starting point of the cost.  For example, if you get 10 levels of something
     * for free, the base will be 10.
     */
    static public double calculateGeometricCost(int level, int costPerXlevels, int Xlevel, int base) {
        return (double)costPerXlevels / (double)Xlevel * (level - base);
    }
    
    /** Returns the Maximum level which can still be used given a adjustment in available CP points.
     * The maximum level for Geometric option will be calculated based on the current level,
     * and the amount of cp change that must occur.
     *
     * cpDelta is the amount of cp which must be removed or added to the option.  For example, if the power
     * was adjusted from 10 cp to 20 cp, the delta for this option might be +10cp.  However if two options
     * are sharing those adjusted points, the delta might be +5cp for this option and +5 cp for the other
     * option.
     *
     * minimumLevel can be used to specify whether buy-back options are appropriate.  If the option can be bought
     * back, specify some negative minimumLevel.  Otherwise, specify 0.
     */
    static public int calculateAdjustedGeometricCost(double cpDelta, int currentLevel, int costPerXlevels, int Xlevel, int base, int minimumLevel) {
        int newLevel = (int)Math.floor( (double)currentLevel + ( cpDelta * Xlevel / costPerXlevels ) );
        if ( newLevel < minimumLevel ) newLevel = minimumLevel;
        return newLevel;
    }
    
    /** Returns the Cost of a LOGRITHMIC Power Option.
     * The total Character Point cost of the option will be calculated and returned.
     *
     * Logrithmic Power Options are options in which doubling the amount of levels you
     * received cost some fixed amount.  For example, non-combat running costs 5 points
     * for each 2x multiplier to the speed.  Also, 2x non-combat running is free.  In this
     * example, the level would be the total non-combat running multiplier (i.e. x16),
     * cpPerMultiple would be 5, multiplier would be 2 (since it cost 5 for every x2),
     * and the base would be 2 (since you get x2 free).
     *
     */
    static public double calculateLogrithmicCost(int level, int cpPerMultiple, int multiplier, int base) {
        return (double)cpPerMultiple * Math.ceil( Math.log( (double) level / base ) / Math.log(multiplier) );
    }
    
    /** Returns the Maximum level which can still be used given a adjustment in available CP points.
     * The maximum level for Logrithmic option will be calculated based on the current level,
     * and the amount of cp change that must occur.
     *
     * With Logrithmic options, some round will occur.  Since options can only be purchased in whole amounts
     * the level will be rounded to the closest acceptable amount.  For example, if a character wants to have
     * exact x15 Non-Combat running, it will cost him 15 pts (5 * 3).  He could have up to x16 NC-running for
     * the same cost.  When the calculation is reversed, the calculation will be based off x16 value, not
     * x15, since the character did pay for up to x16.
     *
     * cpDelta is the amount of cp which must be removed or added to the option.  For example, if the power
     * was adjusted from 10 cp to 20 cp, the delta for this option might be +10cp.  However if two options
     * are sharing those adjusted points, the delta might be +5cp for this option and +5 cp for the other
     * option.
     *
     * minimumLevel can be used to specify whether buy-back options are appropriate.  If the option can be bought
     * back, specify some negative minimumLevel.  Otherwise, specify 0.
     */
    static public int calculateAdjustedLogrithmicCost(double cpDelta, int currentLevel, int cpPerMultiple, int multiplier, int base, int minimumLevel) {
        int newLevel = (int)Math.pow( multiplier, Math.floor( Math.ceil( Math.log( (double)currentLevel/base ) / Math.log(multiplier) ) + cpDelta / cpPerMultiple ) ) * base;
        if ( newLevel < minimumLevel ) newLevel = minimumLevel;
        return newLevel;
    }
    
    /** Returns the Cost of a List Power Option.
     * The total Character Point cost of the option will be calculated and returned.
     *
     * List Power options are options where each additional item selected from a list costs
     * X points.  For example, Flash cost 5 points for each additional sense, but the first
     * sense is free.
     *
     * Note: List powers are not currently dynmic since there is not method to specify which
     * options can be choosen from a list.
     */
    static public double calculateListCost(int optionsSelected, int cpPerOption, int base) {
        return (double) ( optionsSelected - base )* cpPerOption ;
    }
    
    
    /** Returns the Cost of a Boolean Power Option.
     * The total Character Point cost of the option will be calculated and returned.
     *
     * Boolean Power options are options where it cost X points to have an option
     * on.  For example, Clairsentience cost +20 points to see into other dimensions.
     */
    static public double calculateBooleanCost(boolean isSelected, int cpForOption) {
        return (double)(isSelected ? cpForOption : 0);
    }
    
    /** Returns the Maximum level which can still be used given a adjustment in available CP points.
     * The maximum level for Boolean option will be calculated based on the current level,
     * and the amount of cp change that must occur.
     *
     * For a Boolean option, dynamic reconfiguration doesn't work well and really shouldn't be used.
     *
     * cpDelta is the amount of cp which must be removed or added to the option.  For example, if the power
     * was adjusted from 10 cp to 20 cp, the delta for this option might be +10cp.  However if two options
     * are sharing those adjusted points, the delta might be +5cp for this option and +5 cp for the other
     * option.
     *
     */
    static public boolean calculateAdjustedBooleanCost(double cpDelta, boolean isSelected, int cpForOption) {
        return ( cpDelta >= cpForOption * ( 1 - (isSelected ? 1 : 0 ) ) );
    }
    
    /** Returns the Cost of a Combo Power Option.
     * The total Character Point cost of the option will be calculated and returned.
     *
     * Combo Power options are options where there are several selections from a list
     * and each selection cost a fixed CP cost.  For example, for Damage Reduction,
     * if costs 10pts, 20pts, and 40pts for %25, %50, %75 damage reduction, respectively.
     */
    static public double calculateComboCost(String optionSelected, Integer[] cpCostsArray, String[] optionNames) {
        int index;
        boolean found = false;
        for(index=0;index<optionNames.length;index++) {
            if ( optionNames[index].equals( optionSelected ) ) {
                found = true;
                break;
            }
        }
        return (double)( found ? cpCostsArray[index].doubleValue() : 0 );
    }
    
    /** Returns the Maximum level which can still be used given a adjustment in available CP points.
     * The maximum level for Combo option will be calculated based on the current level,
     * and the amount of cp change that must occur.
     *
     * For a List option, the returned level will be the best available option given the points
     * available.  If options in a list are necessarily better or worst, mark the option as
     * STATIC_OPTION to prevent automatic reconfiguration.  For example, the type of focus could
     * be build via a combo box, but more expensive options are not necessarily better, just different.
     *
     * cpDelta is the amount of cp which must be removed or added to the option.  For example, if the power
     * was adjusted from 10 cp to 20 cp, the delta for this option might be +10cp.  However if two options
     * are sharing those adjusted points, the delta might be +5cp for this option and +5 cp for the other
     * option.
     */
    static public String calculateAdjustedComboCost(double cpDelta, String currentOptionSelected, Integer[] cpCostsArray, String[] optionNames) {
        double currentCost = calculateComboCost( currentOptionSelected,cpCostsArray,optionNames);
        double newCost = currentCost + cpDelta;
        
        int index, bestIndex;
        double difference,bestDifference;
        
        bestIndex = -1;
        bestDifference = Integer.MAX_VALUE;
        
        
        for (index=0;index<cpCostsArray.length;index++) {
            if ( newCost <= cpCostsArray[index].intValue() ) {
                difference = newCost - cpCostsArray[index].intValue();
                if ( difference < bestDifference ) {
                    bestIndex = index;
                    bestDifference = difference;
                }
            }
        }
        
        return ( bestIndex != -1 ) ? optionNames[bestIndex] : null;
    }
    
    static public String createWrappedHTMLString(String string, int width) {
        if ( string == null || string.equals("") ) {
            return "<html></html>";
        }
        
        string = string.replaceAll("<HTML>", "");
        string = string.replaceAll("</HTML>","");
        string = string.replaceAll("<html>", "");
        string = string.replaceAll("</html>","");
       
        StringBuffer sb = new StringBuffer(string);
        
        int lastBreak, index, lineCount;
        
        lastBreak = -1;
        lineCount = 0;
        index = 0;
        char c;
        
        boolean inTag = false;
        
        while ( index < sb.length() ) {
            if ( inTag ) {
                if ( sb.charAt(index) == '>' ) {
                    inTag = false;
                }
                index++;
            }
            else if ( sb.charAt(index) == '<' && sb.substring(index, Math.min(index+4, sb.length()) ).startsWith("<br>") || sb.substring(index, Math.min(index+4, sb.length()) ).startsWith("<BR>")
                || sb.substring(index, Math.min(index+3, sb.length()) ).startsWith("<p>") || sb.substring(index, Math.min(index+3, sb.length()) ).startsWith("<P>")) {

                index += 3;
                lineCount = 0;
                lastBreak = -1;
            }
            else if ( sb.charAt(index) == '<' ) {
                inTag = true;
                index++;
            }
            else {
                c = sb.charAt(index);
                if ( java.lang.Character.isSpaceChar(c) ) {
                    lastBreak = index;
                }
                
                if ( lineCount > width && lastBreak != -1 ) {
                    // If the character is a space, replace the space with <P>
                    if ( java.lang.Character.isSpaceChar(sb.charAt(lastBreak)) ) {
                        sb.replace(lastBreak, lastBreak, "<br>");
                        if ( index == lastBreak ) {
                            index += 3;
                        }
                        else {
                            index += 2;
                        }
                    }
                    else {
                        // Isn't a space, so insert before breakable character
                        sb.insert(lastBreak, "<br>");
                        index += 3;
                    }
                    
                    if ( width < lineCount ) width = lineCount;
                    
                    lastBreak = -1;
                    lineCount = 0;
                }
                else {
                    lineCount ++;
                    index ++;
                }
            }
        }
        
        sb.insert(0,"<html><font SIZE=\"-1\" FACE=\"Arial,'Times New Roman',System\">");
        sb.append("</font></html>");
        
        return sb.toString();
    }
    
    static public String createHTMLString(String string) {
        StringBuffer sb = new StringBuffer();
        
        sb.append("<html><font SIZE=\"-1\" FACE=\"Arial,'Times New Roman',System\">");
        sb.append(string);
        sb.append("</font></html>");
        
        return sb.toString();
    }
    
    public static String getHTMLColorString(String stringToWrap, Color color) {
        StringBuilder sb = new StringBuilder();
        sb.append("<Font Color=#").append( getHTMLColorHexValue(color)).append(">");
        sb.append(stringToWrap);
        sb.append("</Font>");
        
        return sb.toString();
    }
    
    public static String getHTMLColorHexValue(Color color) {
        
        StringBuilder sb = new StringBuilder();
        
        int colorValue;
        
        colorValue = color.getRed();
        if ( colorValue < 16 ) {
           sb.append("0").append(Integer.toString(colorValue, 16));
        } else {
           sb.append(Integer.toString(colorValue, 16));
        }
        
        colorValue = color.getGreen();
        if ( colorValue < 16 ) {
           sb.append("0").append(Integer.toString(colorValue, 16));
        } else {
           sb.append(Integer.toString(colorValue, 16));
        }
        
        colorValue = color.getBlue();
        if ( colorValue < 16 ) {
           sb.append("0").append(Integer.toString(colorValue, 16));
        } else {
           sb.append(Integer.toString(colorValue, 16));
        }
        
        return sb.toString();
    }
    
    static public double getDoubleValue(String expression) {
        double value = 0;
        if ( ChampionsMatcher.matches( "\\s*([-+]?)\\s*([0-9]*)\\s*([\u00BC\u00BD\u00BE]?)\\s*", expression ) ) {
            String minus = ChampionsMatcher.getMatchedGroup(1);
            int whole = ChampionsMatcher.getIntMatchedGroup(2);
            String fraction = ChampionsMatcher.getMatchedGroup(3);
            
            // Convert the fraction
            if ( fraction != null && fraction.equals("") == false ) {
                if ( fraction.equals("\u00BC") ) {
                    value += .25;
                }
                else if ( fraction.equals("\u00BD") ) {
                    value += .50;
                }
                else if ( fraction.equals("\u00BE") ) {
                    value += .75;
                }
            }
            
            value += whole;
            
            if ( minus != null && minus.equals("-") ) {
                value *= -1;
            }
        }
        return value;
    }
    
    static public String toStringWithFractions(double value) {
        //
        if ( Math.floor(value) != value && Math.floor(value / 0.25) == value / 0.25 ) {
            // This is a multiple, so we can use a fraction...
            int integer = (int)Math.floor(Math.abs(value));
            double fraction = Math.abs(value) - integer;
            String intString = null;
            String fracString = null;
            
            if ( integer == 0 ) {
                if ( value < 0 ) {
                    intString = "-";
                }
                else {
                    intString = "";
                }
                
            }
            else {
                intString = Integer.toString(integer);
            }
            
            if ( fraction == 0.25 ) {
                fracString = "\u00BC";
            }
            else if ( fraction == 0.5 ) {
                fracString = "\u00BD";
            }
            else if ( fraction == 0.75 ) {
                fracString = "\u00BE";
            }
            
            return intString + fracString;
        }
        else {
            return Integer.toString((int)value);
        }
    }
    
    static public String toSignedStringWithFractions(double value) {
        String fracString = toStringWithFractions(value);
        if ( value > 0 ) {
            return "+" + fracString;
        }
        else {
            return fracString;
        }
    }
    
    /** Calculates the number of seconds in a time period.
     *
     * The time parameter is based upon the Time chart located in ChampionsConstants.
     * The count is the number of time units to count.
     *
     * If the time constant of TIME_ONE_PHASE is used and target is specified, then the
     * actual targets phases are counted and changed into seconds from the current battle
     * time.  If the target is null or not specified then TIME_ONE_PHASE is treated as
     * a single segment.
     */
    static public long calculateSeconds(int timeStepConstant, int stepCount) {
        return calculateSeconds(timeStepConstant, stepCount, null);
    }
    
    /** Calculates the number of seconds in a time period.
     *
     * The time parameter is based upon the Time chart located in ChampionsConstants.
     * The count is the number of time units to count.
     *
     * If the time constant of TIME_ONE_PHASE is used and target is specified, then the
     * actual targets phases are counted and changed into seconds from the current battle
     * time.  If the target is null or not specified then TIME_ONE_PHASE is treated as
     * a single segment.
     */
    static public long calculateSeconds(int timeStepConstant, int stepCount, Target target) {
        long seconds = 0;
        
        switch ( timeStepConstant ) {
            case TIME_ONE_SEGMENT:
                seconds = stepCount;
                break;
            case TIME_ONE_PHASE:
                // This isn't really right.  It should figure out the next phase based on the current battle
                // state and the target...
                if ( target == null ) {
                    // Target is null, so treat this as segments
                    seconds = stepCount;
                }
                else {
                    Chronometer realTime = Battle.currentBattle.getTime();
                    Chronometer newTime = (Chronometer)realTime.clone();
                    
                    for(int i = 0; i < stepCount; i++) {
                        int nextSegment = Chronometer.nextActiveSegment( target.getEffectiveSpeed( Battle.currentBattle.getTime() ), newTime);
                        int difference = nextSegment - newTime.getSegment();
                        if ( difference < 0 ) difference += 12;
                        seconds += difference;
                        newTime.setTime( newTime.getTime() + difference );
                    }
                }
                break;
            case TIME_ONE_TURN:
                seconds = 12 * stepCount;
                break;
            case TIME_ONE_MINUTE:
                seconds = 60 * stepCount;
                break;
            case TIME_FIVE_MINUTES:
                seconds = 300 * stepCount;
                break;
            case TIME_TWENTY_MINUTES:
                seconds = 1200 * stepCount;
                break;
            case TIME_ONE_HOUR:
                seconds = 3600 * stepCount;
                break;
            case TIME_SIX_HOURS:
                seconds = 21600 * stepCount;
                break;
            case TIME_1_DAY:
                seconds = 86400 * stepCount;
                break;
            case TIME_1_WEEK:
                seconds = 604800 * stepCount;
                break;
            case TIME_1_MONTH:
                seconds = 2629744 * stepCount;
                break;
            case TIME_1_SEASON:
                seconds = 7889231 * stepCount;
                break;
            case TIME_1_YEAR:
                seconds =  31556926 * stepCount;
                break;
            case TIME_5_YEARS:
                seconds = 157784630 * stepCount;
                break;
            case TIME_25_YEARS:
                seconds = 788923149 * stepCount;
                break;
                //            case TIME_1_CENTURY:
                //                seconds = 3153600000 * count;
                //                break;
                
                
        }
        
        return seconds;
    }
    
    
    static public int calculateTimeChartDifference(long starttime, long currenttime) {
       
        long timedifference = currenttime - starttime;
        //25 Year
        if (timedifference >= 788923149) {
            return TIME_25_YEARS;
        }
        //5 Year
        else if (timedifference >= 157784630) {
            return TIME_5_YEARS;
        }
        //1 Year
        else if (timedifference >= 31556926) {
            return TIME_1_YEAR;
        }
        //1 season (3 months)
        else if (timedifference >= 7889231) {
            return TIME_1_SEASON;
        }
        //1 month
        else if (timedifference >= 2629744) {
            return TIME_1_MONTH;
        }
        // 1 week
        else if (timedifference >= 604800) {
            return TIME_1_MONTH;
        }
        //1 day
        else if (timedifference >= 86400) {
            return TIME_1_DAY;
        }
        //5 hour
        else if (timedifference >= 21600) {
            return TIME_SIX_HOURS;
        }
        //1 hour
        else if (timedifference >= 3600) {
            return TIME_ONE_HOUR;
        }
        //20 minutes
        else if (timedifference >= 1200) {
            return TIME_TWENTY_MINUTES;
        }
        //5 minutes
        else if (timedifference >= 300) {
            return TIME_FIVE_MINUTES;
        }
        //1 minute
        else if (timedifference >= 60) {
            return TIME_ONE_MINUTE;
        }
        //1 turn
        else if (timedifference >= 12) {
            return TIME_ONE_TURN;
        }
        return TIME_ONE_SEGMENT;
    }
    
    static public long getSecondsInTimestep(int timeStep) {
        return TIME_CHART_SECONDS[timeStep];
    }
    
    public static String getTimeString(int timeStepConstant) {
        return TIME_CHART[timeStepConstant];
    }
    
    public static int getTimeStep(String timeString) {
        for(int i = 0; i < TIME_CHART.length; i++) {
            if ( TIME_CHART[i].equals( timeString ) ) return i;
        }
        return -1;
    }
    
    public static void centerWindow(Window frame) {
        Dimension d = Toolkit.getDefaultToolkit ().getScreenSize ();
        Dimension m = frame.getSize();
        d.width -= m.width;
        d.height -= m.height;
        d.width /= 2;
        d.height /= 2;

        if ( d.width < 0 ) d.width = 0;
        if ( d.height < 0 ) d.height = 0;
        frame.setLocation (d.width,d.height);
    }
    
    static String[] activationTimes = { "HOLD", "INSTANT", "HALFMOVE",
                                            "FULLMOVE",  "ATTACK" };
    
    public static String getLongerActivationTime(String timeString1, String timeString2) {
        int time1, time2;
        
        for(time1 = 0; time1 < activationTimes.length; time1++ ) {
            if (activationTimes[time1].equals(timeString1) ) break;
        }
        
        for(time2 = 0; time2 < activationTimes.length; time2++ ) {
            if (activationTimes[time2].equals(timeString2) ) break;
        }
        
        return (time1 > time2 ? activationTimes[time1] : activationTimes[time2]);
    }
    
}