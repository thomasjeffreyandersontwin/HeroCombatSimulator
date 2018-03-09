/*
 * powerHandToHandAttack.java
 *
 * Created on September 24, 2000, 5:06 PM
 */

package champions.powers;

import champions.*;
import champions.interfaces.*;
import champions.exception.*;
import champions.attackTree.*;
import champions.parameters.ParameterList;
import champions.battleMessage.GenericSummaryMessage;

/**
 *
 * @author  unknown
 * @version
 *
 * To Convert from old format powers, to new format powers:
 *
 * 1) Add implements ChampionsConstants to class definition.<P>
 * 2) Copy and Fill in Power Definition Variables. <P>
 * 3) Move Parameter Information to parameterArray. <P>
 * 4) Delete getParameters method (unless special parameter handling is necessary.<P>
 * 5) Change configurePAD(Ability,DetailList) method to configurePAD(Ability ability, ParameterList parameterList).<P>
 * 6) Edit configurePAD method to use format specified below.<P>
 * 7) Change checkParameter method to checkParameter(Ability ability, <i>int padIndex</i>, 
 * String key, Object value, Object oldValue);
 * 8) Edit getConfigSummary method to use parameterList instead of parseParameter methods.<P>
 * 9) Change all instances of parseParameter to getParameterValue.<P>
 * 10) Add getParameterArray method.<P>
 * 11) Edit getName method to return powerName variable.
 * 12) Change serialVersionUID by some amount.
 * 13) Add patterns array and define import patterns.<P>
 * 14) Add getImportPatterns() method.<P>
 */
public class maneuverGrab extends Power
implements ChampionsConstants {
    static final long serialVersionUID =5295848683348705403L;

    static private Object[][] parameterArray = {
     //   {"DC","Maneuver.DC", Double.class, new Double(0), "Extra Damage Classes", DOUBLE_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED },
        {"Strength","Power.STRENGTH", Integer.class, new Integer(0), "Additional Strength", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "MINIMUM", new Integer(0)},
      //  {"OCVModifier","Ability.OCVBONUS", Integer.class, new Integer(-1), "Maneuver OCV Modifier", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
      //  {"DCVModifier","Ability.DCVBONUS", Integer.class, new Integer(-2), "Maneuver DCV Modifier", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
      //  {"MartialManeuver","Ability.ISMARTIALMANEUVER", String.class, "FALSE", "Martial Arts Skill", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED}        
    };
    
    // Power Definition Variables
    private static String powerName = "Grab"; // The Name of the Power
    private static String targetType = "SINGLE"; // The Type of Attack: Either "SINGLE" or "SELF"
    private static String persistenceType = "INSTANT"; // The Length of Time power stays active: "INSTANT","CONSTANT","PERSISTENT"
    private static String activationTime = "ATTACK"; // Time required to activate Ability: "INSTANT", "ATTACK", "FULL", "HALF"
    private static double pointsPerDC = 5; // The Point cost of a single Damage Class of this power.
    private static String attackType = "MELEE"; // Type of Attack, either "RANGED", "MELEE", or null if it is not an attack.
    private static String damageType = "NORMAL"; // Type of Damage done by attack.  Either "NORMAL","KILLING", or "SPECIAL"
    private static boolean generateDefaultDamage = false; // Indicates this is a standard damage generating attack
    private static int endMultiplier = 0; // Indicates the END Multiplier for this power

    // Import Patterns Definitions
    private static Object[][] patterns = {
      //  { ".*\\(OCV ([-+][0-9]*), DCV ([-+][0-9]*), STR ([0-9]*)\\)", new Object[] {"OCVModifier", Integer.class, "DCVModifier", Integer.class}}
    };
    
    /** Creates new powerHandToHandAttack */
    public maneuverGrab()  {
    }

    /* Returns an array which can be used to create the parameterList.
     */
    public Object[][] getParameterArray() {
        return parameterArray;
    }
    
    /** Get the English name of the PAD.
     * @return name of PAD
     */
    public String getName() {
        return powerName;
    }
    
 /** Configures the ability according to the parameters in parameterList.
  * The parameterList should be stored with the ability for configuration
  * later on. If an existing parameterList alread exists, it should be
  * replaced with this one.
  *
  * All value/pairs should be copied into the ability for direct access.
  */
    public boolean configurePAD(Ability ability, ParameterList parameterList) {
        // Fail immediately if ability is null
        if ( ability == null ) return false;
        
        // Always Set the ParameterList to the parameterList
        setParameterList(ability,parameterList);
        
        // Read in any parameters that will be needed to configure the power or
        // Determine the validity of the power configuration.  Read the parameters
        // from the parameterList, instead of directly from the ability, since the
        // Ability isn't configured yet.
        Integer str = (Integer)parameterList.getParameterValue("Strength");
        
        // Check for the validity of the parameters that will be set.  If the parameters
        // Fail for any reason, return false from the method immediately, indicating a
        // failure to configure
        
        // No Validation Necessary
        
        // Always copy the configuration parameters directly into the ability.  This will
        // take the parameters stored in the parameter list and copy them into the
        // ability using the appropriate keys and values.
        parameterList.copyValues(ability);
        
        // Start to Actually Configure the Power.
        // The Add Power Info should always be executed to add information to the ability.
        // All of this information should be set in the Power Definition Variables at the
        // top of this file
        ability.addPowerInfo( this, powerName, targetType, persistenceType, activationTime);
        if ( attackType != null ) {
            ability.addAttackInfo( attackType,damageType );
            ability.add("Ability.PPDC", new Double(pointsPerDC), true);
        }
        ability.setGenerateDefaultEffects(generateDefaultDamage);
        if ( endMultiplier != 1 ) ability.setENDMultiplier(endMultiplier);
        
        // Add any dice information which is necessary to use this power.
        //ability.addDiceInfo( "DamageDie", die, "Hand-to-Hand Attack Damage");
        
        // Add A Damage Class Info
        //ability.add("Base.DC",  new Double(ChampionsUtilities.StringToNormalDC(die)), true);
        
        // Add Extra Value/Pairs used by the Power/BattleEngine
        ability.setIs("MELEEMANEUVER",true);
        
        // Update the Ability Description based on the new configuration
        ability.setPowerDescription( getConfigSummary(ability, -1));
        
        // Return true to indicate success
        return true;
    }

    public String getConfigSummary(Ability ability, int not_used) {
        ParameterList parameterList = getParameterList(ability);
        Integer str = (Integer)parameterList.getParameterValue("Strength");

        if ( str.intValue() != 0 ) {
            return "Grab (" + ChampionsUtilities.toSignedString( str.intValue() ) + " STR)";
        }
        else {
            return "Grab";
        }
    }
    
    /** Returns an AttackTreeNode used to gather necessary information to generate effects of Power.
     *
     * getTriggerPowerNode allows the power a chance to create a triggerPower node, which can be used
     * to gather additional information necessary to apply the effect.
     *
     * For each Target with is hit by the power, getTriggerPowerNode will be called once.  If a non-null
     * value is returned, the node will be added to the AttackTree under the effect node for the relevant
     * attack.  Only hit targets will cause getTriggerPowerNode to be called.
     *
     * If a manuever is in use, it will also have an oppertunity to generate an AttackTreeNode, which will
     * be added to the AttackTree.
     */
    public AttackTreeNode getTriggerPowerNode(BattleEvent be, Target target, String TargetGroup, int refNumber) {
        GrabTriggerNode gtn =  new GrabTriggerNode("GrabTriggerNode");
        gtn.setTargetReferenceNumber(refNumber);
        return gtn;
    }
    
    public AttackTreeNode getPostTriggerPowerNode(BattleEvent be, Target target, String TargetGroup, int refNumber) {
        GrabActionNode gtn =  new GrabActionNode("GrabActionNode");
        gtn.setTargetReferenceNumber(refNumber);
        return gtn;
    }
    
    public void triggerPower(BattleEvent be, Ability ability,DetailList effectList, Target target, int refNumber, String targetGroup) throws BattleEventException {
        Ability maneuver;

        // Check to see if the target escaped with Casual Strength...
        int sindex = be.findSkillChallenge("Casual Strength", "Strength", target, be.getSource());
        if ( sindex != -1 && be.getSkillChallengeWinner(sindex) == target ) {
            be.addBattleMessage( new GenericSummaryMessage(target, " escaped immediately using casual strength"));
            int index = be.findIndexed("GrabbedEffect", "TARGET", target);
            if ( index == -1 ) index = be.createIndexed( "GrabbedEffect", "TARGET", target);
            be.addIndexed(index, "GrabbedEffect", "EFFECT", null, true);
            be.addIndexed(index, "GrabbedEffect", "GRABBED", "FALSE", true);
        }
        else {
            Ability realAbility = be.getAbility();
            maneuver = be.getManeuver();

            effectGrabbed grabbedEffect = new effectGrabbed(realAbility, maneuver,  be.getSource());
            grabbedEffect.setGrabStrength( (int)Math.round( be.getDC() * 5 ) );

            // store the grabbed effect so you can check on it later
            
            int index = be.findIndexed("GrabbedEffect", "TARGET", target);
            if ( index == -1 ) index = be.createIndexed( "GrabbedEffect", "TARGET", target);
            be.addIndexed(index, "GrabbedEffect", "EFFECT", grabbedEffect, true);
            be.addIndexed(index, "GrabbedEffect", "GRABBED", "TRUE", true);
            
            //be.addUndoableEvent( new CreateIndexedUndoable(be, index,"GrabbedEffect", "TARGET", target));

            effectList.createIndexed( "Effect","EFFECT",grabbedEffect);
        }
    }

//  /*  public boolean postTrigger(BattleEvent be,Target target) throws BattleEventException {
//        Effect grabbedEffect =  null ;
//        int index;
//
//        if ( (index = be.findIndexed("GrabbedEffect","TARGET",target) ) != -1 ) {
//            grabbedEffect = (Effect) be.getIndexedValue( index, "GrabbedEffect", "EFFECT");
//        }
//
//        if ( grabbedEffect != null && target.findIndexed( "Effect","EFFECT", grabbedEffect ) != -1 ) {
//            // He didn't escape...
//            JOptionPane jop = new JOptionPane();
//            int result = jop.showOptionDialog(null, be.getSource().getName() + " has grabbed " + target.getName() + ".  What would (s)he do?",
//            target.getName() + " Grabbed", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null,
//        new String[]{ "Squeeze", "Throw",  "Hold", "Cancel" },
//            "Squeeze");
//
//            Ability ability = be.getAbility();
//            Ability maneuver = be.getManeuver();
//
//            if ( result == 3 ) {
//                throw new BattleEventException( "Grab Cancelled",false);
//            }
//            else if ( result == 2 ) {
//                // Nothing
//            }
//            else if ( result == 1 ) {
//                // Throw
//                Ability throwAbility;
//                throwAbility = new Ability("Throw Grabbed");
//                throwAbility.addPAD( new maneuverThrowFromGrab(), null);
//
//                BattleEvent newBE;
//                if ( maneuver == null ) {
//                    throwAbility.createIndexed( "Target","TARGET", target);
//                    newBE = new BattleEvent(throwAbility);
//                }
//                else {
//                    newBE = new BattleEvent(ability);
//                    newBE.setManeuver( throwAbility );
//                }
//                newBE.setSource( be.getSource() );
//                Battle.currentBattle.getBattleEngine().processAttack(newBE,be.getSource());
//                be.embedBattleEvent(newBE);
//            }
//            else if ( result == 0 ) {
//                // Squeeze
//                Ability squeezeAbility = new Ability("Squeeze Grabbed");
//                squeezeAbility.addPAD( new maneuverSqueezeFromGrab(), null);
//
//                BattleEvent newBE;
//
//                if ( maneuver == null ) {
//                    squeezeAbility.createIndexed( "Target", "TARGET", target );
//                    newBE = new BattleEvent(squeezeAbility);
//                }
//                else {
//                    newBE = new BattleEvent(ability);
//                    newBE.setManeuver(squeezeAbility);
//                }
//                newBE.setSource( be.getSource() );
//                Battle.currentBattle.getBattleEngine().processAttack(newBE,be.getSource());
//                be.embedBattleEvent(newBE);
//            }
//
//        }
//
//        return true;
//    } */
    
    public void adjustDice(BattleEvent be, String targetGroup) {
        Integer str;
        ParameterList parameterList;
        
        Ability a = be.getAbility();
        Ability m = be.getManeuver();
        if ( a != null && a.getPower() == this ) {
            parameterList = getParameterList(a);
            str = (Integer)parameterList.getParameterValue("Strength");
        }
        else if ( m != null && m.getPower() == this )  {
            parameterList = getParameterList(m);
            str = (Integer)parameterList.getParameterValue("Strength");
        }
        else {
            str = new Integer(0);
        }
        
        be.add("Maneuver.DC", new Double( ChampionsUtilities.strToDCs(str.intValue())), true);
    } 

    /** This is a challenge of the grab.
     * It is from the perspective of the grabber.
     * challengeGrab requires the original ability/maneuver combination from the
     * grab attack in order to calculate the grabbers resistence value.
     * @param be BattleEvent that is being processed to cause break.
     * @param ability Ability from original grab attack.
     * @param maneuver Maneuver from original grab attack ( either null or a maneuver grab )
     * @param source The grabber
     * @param target The grabbed
     * @param targetStr The amount of strength the target is using to break free.
     * @param modifier Any source modifiers to the break out.
     * @throws BattleEventException
     * @return <PRE>SOURCE_WON if grabber won the challenge.
     * TARGET_WON if grab character broke free.</PRE>
     */
//  /*  static public int challengeGrab(BattleEvent be, Ability ability, Ability maneuver, Target source, Target target, int targetStr, int modifier )
//    throws BattleEventException {
//        BattleEvent challengeBE = new BattleEvent(ability);
//        if ( maneuver != null ) challengeBE.setManeuver(maneuver);
//       
//        Power p;
//        if ( (p = ability.getPower() ) != null ) {
//            p.adjustDice(challengeBE, "");
//        }
//
//        if ( maneuver != null ) {
//            Power m = maneuver.getPower();
//            m.adjustDice(challengeBE, "");
//        }
//
//        // Run through the source Effects to let them adjust the dice
//        // First build list, then run through list
//        int i,count;
//        count = source.getIndexedSize("Effect");
//        Effect[] sourceEffects = new Effect[count];
//        for (i=0;i<count;i++) {
//            sourceEffects[i] = (Effect)source.getIndexedValue( i,"Effect","EFFECT" );
//        }
//
//        for (i=0;i<count;i++) {
//            if ( source.findIndexed( "Effect","EFFECT",sourceEffects[i]) != -1 ) {
//                sourceEffects[i].adjustDice(challengeBE, "");
//            }
//        }
//        
//        challengeBE.add("Normal.STR",  new Integer( source.getCurrentStat( "STR" ) ), true);
//        
//        ChampionsUtilities.calculateDCs(challengeBE);
//        
//        //System.out.println( challengeBE);
//
//        int result = VersusDialog.getVersusDialog().showVersusDialog(source, target, target.getName() + " is attempting to break " + source.getName() + "'s grab",
//        ChampionsUtilities.DCToNormalDice( challengeBE.getDC().doubleValue() ).toString(),
//        ChampionsUtilities.DCToNormalDice( ChampionsUtilities.strToDCs( targetStr ) ).toString(), modifier );
//
//        if ( result == JOptionPane.CANCEL_OPTION ) {
//            throw new BattleEventException ( "Challenge Cancelled",false );
//        }
//
//        be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( source.getName() + " rolled " + VersusDialog.getVersusDialog().getSourceResult().getBody().toString() + " for Strength. "
//        + target.getName() + " rolled " + VersusDialog.getVersusDialog().getTargetResult().getBody().toString() + " for Strength.", BattleEvent.MSG_NOTICE);
//
//        return result;
//    } */

    static public Effect findGrabbedEffect(Target source, Target target) {
        int index,count;
        Effect effect;
        count = target.getEffectCount();
        for(index=0;index<count;index++) {
            effect = target.getEffect(index);
            if ( effect instanceof effectGrabbed && ((effectGrabbed)effect).getGrabber() == source ) {
                return effect;
            }
        }
        return null;
    }
    
    /** Returns the patterns necessary to import the Power from CW.
 * The Object[][] returned should be in the following format:
 * patterns = Object[][] {
 *  { "PATTERN" , new Object[] { "PARAMETER1", parameter1.class, "PARAMETER2", parameter2.class, ... },
 *  ...
 *  }
 *
 * PATTERN should be a regular expression pattern.  For every PARAMETER* where should be one
 * parathesis group in the expression. The PARAMETERS sub-array can be null, if the line has no parameter
 * and is just informational.
 *
 * By default, the importPower will check each line of the getImportPatterns() array and if a match is
 * found, the specified parameters will be set in the powers parameter list.  It is assumed that each 
 * PATTERN will only occur once.  If the pattern can occur multiple times in a valid import, a custom
 * importPower method will have to be used.
 */
    public Object[][] getImportPatterns() {
      return patterns;
    }
    
    public void importPower(Ability ability, AbilityImport ai) {
        int index, count;
        String line;
        
        String matchPattern = ".*\\(OCV ([-+][0-9]*), DCV ([-+][0-9]*), STR ([0-9]*)\\)";
        
        count = ai.getImportLineCount();
        for(index=0;index<count;index++) {
            if ( ai.isLineUsed(index) == true ) continue;
            
            line = ai.getImportLine(index);
            
            if ( ChampionsMatcher.matches(matchPattern, line) ) {
                // Set OCV/DCV modifiers
                
                int ocv = ChampionsMatcher.getIntMatchedGroup(1);
                int dcv = ChampionsMatcher.getIntMatchedGroup(2);
                
                ability.setOCVModifier(ocv);
                ability.setDCVModifier(dcv);
                
                ai.setLineUsed(index, this);
                break;
            }
        }
        
        super.importPower(ability, ai);
    }

	
}