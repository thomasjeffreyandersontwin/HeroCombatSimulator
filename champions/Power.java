/*
 * Power.java
 *
 * Created on September 23, 2000, 11:27 AM
 */

package champions;

import champions.adjustmentPowers.Adjustable;
import champions.adjustmentPowers.AdjustmentClass;
import champions.attackTree.AttackTreeNode;
import champions.exception.BattleEventException;
import champions.interfaces.AbilityIterator;
import champions.interfaces.AbilityList;
import champions.interfaces.ChampionsConstants;
import tjava.Filter;
import champions.interfaces.PAD;
import champions.parameters.ParameterList;
import champions.powers.advantageCumulative;
import champions.powers.limitationLockout;

import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.Icon;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;

/**
 *
 * @author  unknown
 * @version
 */
public abstract class Power implements PAD, ChampionsConstants, AdjustmentClass, Serializable {
    static final long serialVersionUID = -3106731423206158461L;
    
    // Default Import Patterns Definitions
    private static Object[][] defaultImportPatterns = {
        { "Range: [0-9]*", null},
        { "Range \\([0-9]*\"\\).*", null},
        { "Multipower: .*",null},
        { "Active Points: .*", null},
        { "LEVELS: 0",null}
    };
    
    public static Icon genericPowerIcon = null;
    public Effect getEffect(Ability ability) {
    	return null;
    }
    /**
     * Creates new Power
     */
    public Power() {
        if ( genericPowerIcon == null ) genericPowerIcon = UIManager.getIcon("Power.DefaultIcon");
    }
    
    /** Configures the ability according to the parameters in parameterList.
     * The parameterList should be stored with the ability for configuration
     * later on. If an existing parameterList alread exists, it should be
     * replaced with this one.
     *
     * All value/pairs should be copied into the ability for direct access.
     */
    public boolean configurePAD(Ability ability,ParameterList pl) {
        return false;
    }
    
    public boolean configurePAD(Ability ability, DetailList detailList, int not_used) {
                
        ParameterList pl = createParameterList(detailList, not_used);
        
        return configurePAD(ability, pl);
    }
    
    /** Get the parameterList necessary to configure the PAD for the ability.
     *
     * This parameterList is stored with the ability from this point forward, so
     * additional calls to getParameters will always return the same parameter list.
     */
    public ParameterList getParameterList(Ability ability, int not_used) {
        //ParameterList pl = (ParameterList)ability.getValue("Power.PARAMETERLIST");
        ParameterList pl = ability.getPowerParameterList();
        if ( pl == null ) {
            pl = createParameterList(ability, not_used);
            setParameterList(ability, pl);
        }
        return pl;
    }
    
    /** Power Specific Format for getParameterList
     */
    public ParameterList getParameterList(Ability ability) {
        return getParameterList(ability,-1);
    }
    
    /** Creates a new parameterList for PAD based on values in DetailList
     *
     */
    public ParameterList createParameterList(DetailList detailList, int not_used) {
        ParameterList pl;
        if ( getParameterArray() == null ) {
            pl = new ParameterList();
        }
        else {
            pl = new ParameterList(getParameterArray(), detailList);
        }
        return pl;
    }
    
    public void setParameterList(Ability ability, ParameterList pl) {
        //ability.add("Power.PARAMETERLIST", pl, true, false);
        ability.setPowerParameterList(pl);
    }
    
    /** Executed when the power has been trigger.
     * TriggerPower should generate any effects it want to apply to the target (except damage effect being
     * generated automatically by BattleEngine).
     *
     * TriggerPower is called once for every single target which is going to be affected by the power.
     *
     * @param be BattleEvent of event currently being processed.
     * @param effectList List of Effect already being applied.
     * @param target Target of the effects.
     * @throws BattleEventException
     */
    public void triggerPower(BattleEvent be, Ability ability, DetailList effectList, Target target, int referenceNumber, String targetGroup) throws BattleEventException {
    	
        if(ability!=null)
        {
        	int index = ability.findLimitation(new limitationLockout().getName());
        	if(index>-1)
        	{
        		limitationLockout lockout = (limitationLockout) ability.getLimitation(index);
        		lockout.postTrigger( be,ability, target);
        	}
        	
        	
        }
    
    }
    
    public boolean postTrigger(BattleEvent be,Target source) throws BattleEventException {
        Ability ability = be.getAbility();
        if(ability!=null)
        {
        	int index = ability.findLimitation(new limitationLockout().getName());
        	if(index>-1)
        	{
        		limitationLockout lockout = (limitationLockout) ability.getLimitation(index);
        		lockout.postTrigger( be,ability, source);
        	}
        	index = ability.findAdvantage(new advantageCumulative().getName());
        	if(index>-1)
        	{
        		advantageCumulative adv = (advantageCumulative) ability.getAdvantage(index);
        		adv.postTrigger( be,ability, source);
        	}
  	
        }
    	return true;
    }
    
    /** Indicates power is shutting down.
     *
     * shutdownPower is called during the deactivation of any power that was previously
     * activated, including situations where an activation might have failed.  Power should
     * be careful to check the battle event state to make sure that cleanup is actually necessary.
     *
     * Note:  This method is called from BattleEngine.processDeactivating method.  As such it down
     * not have access to the inline view and the attack pane structure.  shutdownPower should not
     * do anything which would result in the utilization of the inline pane (which in most cases
     * will create a deadlock and lock the GUI).  This includes deactivating other power via
     * a processAndEmbed type call.
     *
     * @param be BattleEvent of event currently being processed.
     * @param source Target which was the original power source.
     * @throws BattleEventException Indicate something went to hell.
     */
    public void shutdownPower(BattleEvent be,Target source) throws BattleEventException {
    	return ;
    }
    
    public int calculateENDCost(Ability ability) {
        return ability.getPowerCost();
    }
    
    /** Returns whether this ability is currently enabled given the current state of the BattleEngine.
     * Only Power specific conditions should be checked.
     * @param ability Ability to be check.
     * @return True if ability is currently usable.
     */    
    public boolean isEnabled(Ability ability, Target source) {
    	return true;
    	
    }
    
    
    
    public String getName() {
        return getClass().getName();
    }
    
    public ParameterList getParameters(Ability ability, int index) {
        return new ParameterList();
    }
    
    public String getConfigSummary(Ability ability, int index) {
        return "No configuration available";
    }
    
    public boolean checkParameter(Ability ability, int not_used, String key, Object value, Object oldValue) {
        return true;
    }
    
    public AttackTreeNode preactivate(BattleEvent be,int index,Ability ability){
        return null;
    }
    
    public AttackTreeNode preactivate(BattleEvent be) {
        return null;
    }
    
    public AttackTreeNode predelay(BattleEvent be){
        return null;
    }
    
    public void adjustDice(BattleEvent be, String targetGroup) throws BattleEventException {
        Ability ability = be.getAbility();
        // Target source = be.getSource();
        Double dc;
        if ( ( dc = ability.getDoubleValue("Base.DC" ) ) != null ) {
            be.add("Base.DC", dc, true);
        }
    }
    
    public String getMovementType(Ability ability) {
        return ability.getStringValue("Ability.MOVETYPE" );
    }
    
    public int getMovementDistance(Ability ability) {
        Integer i;
        if ( ( i = ability.getIntegerValue("Ability.MOVEDISTANCE")) == null ) i = new Integer(0);
        return i.intValue();
    }
    
    public void addECVAttackModifiers(BattleEvent be, CVList cvList, Ability attack ) {
        
    }
    
    public void addECVDefenseModifiers(BattleEvent be, CVList cvList, Ability attack ) {
        
    }
    
    public void addOCVAttackModifiers(BattleEvent be,  CVList cvList, Ability attack ) {
        
    }
    
    public void addDCVDefenseModifiers(BattleEvent be,  CVList cvList, Ability attack ) {
        
    }
    
    public String getDamagePrefix(BattleEvent be) throws BattleEventException {
        return getDamagePrefix(be,null);
    }
    
     public String getDamagePrefix(BattleEvent be,Ability maneuver) throws BattleEventException{
         Ability ability = be.getAbility();
         Target source = be.getSource();
        String die = "";
        if ( ability != null ) {
            if ( this.isGenerateDefaultEffects(ability) == true ||
            ( maneuver != null && maneuver.getPower().isGenerateDefaultEffects(maneuver) == true)) {
                be.getActivationInfo().addTargetGroup("ATTACK");
                BattleEngine.calculateDamage(be, be.getSource(), "ATTACK");
                int dindex = be.getDiceIndex( "DamageDie", "ATTACK" );
                die = be.getDiceSize(dindex);
                
                if ( be.isKillingAttack() ) {
                    die = die + "k";
                }
            }
            else {
                // This is a special attack, so just look up the first die if it exists.
                
                int count = ability.getIndexedSize("Die");
                if ( count >= 1 ) {
                    // String diename = ability.getIndexedStringValue(0, "Die","NAME");
                    // Just grab the first die size
                    die = ability.getIndexedStringValue(0, "Die" , "SIZE");
                }
            }
        }
        return die == null ? "" : die;
    }
    
    public boolean invokeMenu( JPopupMenu popup , Ability ability) {
        return false;
    }
    
    public void addActions(java.util.Vector v,Ability ability) {
    }
    
    public boolean isGenerateDefaultEffects(Ability ability) throws BattleEventException {
//        if ( ability.contains( "Ability.GENERATEDEFAULTEFFECTS" ) == false ) {
//            return false;
//        }
//        else {
//            return ability.getBooleanValue("Ability.GENERATEDEFAULTEFFECTS" );
//        }
        return ability.getGenerateDefaultEffects();
    }
    
    /** Called when a new source has been set for the ability.
     * Used to notify the power that a new source was set and any appropriate actions should be taken.
     * @param source New source of the Ability.
     */
    public void abilitySourceSet(Ability ability, Target oldSource,Target newSource) {
        
    }
    
    /** Called when the ability's name is changed.
     * Used to notify the PAD that the ability's name was changed and that appropriate actions should
     * be taken.
     * @param oldName String containing the old name
     * @param newName String containing the new name
     */
    public void abilityNameSet(Ability ability, String oldName, String newName) {
        
    }
    
    public boolean matchImport(Ability ability,AbilityImport ai) {
        // Do the most rudimentry match here
        // If IMPORT.NAME == getName() consider it a match
        String importName = ability.getStringValue("Ability.IMPORTNAME");
        String aiName = ai.getImportLine(0);
        return ( importName != null && importName.equals(aiName) );
    }
    
    /** Attempt to identify power
     * This method is called when an unknown AbilityImport exists and the CharacterImport is trying to
     * determine the correct power to assign to it.
     */
    public int identifyPower(Ability template, AbilityImport ai) {
        String name = ai.getPowerName();
        
        if ( template != null ) {
            // If this is based on a power Template, determine the power via the template name.
            if ( name != null && name.equals( template.getName() ) ) {
                return 10;
            }
        }
        else {
            // If this is not based on a power Template, determine the power via the power name.
            if ( name != null && name.equals( getName() ) ) {
                return 10;
            }
        }
        return 0;
    }
    
    public void importPower(Ability ability, AbilityImport ai) {
        int index, count;
        Object[][] patterns;
        String pattern;
        Object[] parameters;
        
        ParameterList parameterList = this.getParameterList(ability);
        
        patterns = defaultImportPatterns;
        count = patterns.length;
        for (index=0;index<count;index++) {
            pattern = (String)patterns[index][0];
            parameters = (Object[])patterns[index][1];
            
            ai.searchForParameters( pattern, parameters, ability, this, parameterList);
        }
        
        patterns = getImportPatterns();
        if ( patterns != null ) {
            count = patterns.length;
            for (index=0;index<count;index++) {
                pattern = (String)patterns[index][0];
                parameters = (Object[])patterns[index][1];
                
                ai.searchForParameters( pattern, parameters, ability, this, parameterList);
            }
        }
    }
    
    public Icon getIcon() {
        
        return genericPowerIcon;
    }
    
    /** Executed for all PADs just prior to an ability being saved.
     * All clean up should be done at this point.
     */
    public void prepareToSave(Ability ability, int index) {
        
    }
    
    public Object clone() {
        Object o = null;
        
        try {
            o = this.getClass().newInstance();
        }
        catch ( Exception e) {
        }
        return o;
    }
    
    /** equals method for power.
     *
     * @return true if they are the same power class, false otherwise.
     */
    public boolean equals(Object that) {
        if (that != null) {
            return  this.getClass() == that.getClass();
        }
        return false;
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
        return null;
    }
    
    /**
     * Returns the CP Cost of the Power as configured in ability.
     * 
     * Power uses the information contained in the getCostArray() to determine the cost of
     * the Power.  Children can override this method, but it is recommend for reconfigurability
     * that the costArray be used whereever possible.
     */
    public int calculateCPCost(Ability ability) {
        Object[][] costArray = getCostArray(ability);
        ParameterList parameterList = getParameterList(ability);
        
        if ( costArray == null || parameterList == null ) return 0;
        
        double cost = 0;
        Integer totalMultiplier = new Integer(1);
        
        String parameter;
        String type;
        
        int index;
        for ( index = 0; index < costArray.length; index++ ) {
            parameter = (String)costArray[index][0];
            type = (String)costArray[index][1];
            
            if ( parameter == null ) throw new NullPointerException("ParameterName is null in costArray.");
            if ( type == null ) throw new NullPointerException("Type is null in costArray.");
            
            if ( type.equals( NORMAL_DICE_COST ) ) {
                Integer cpPerDC = (Integer)costArray[index][4];
                Integer base = (Integer)costArray[index][5];
                
                String dice = (String)parameterList.getParameterValue(parameter);
                
                double dcLevel = ChampionsUtilities.StringToNormalDC(dice);
                
                cost += ChampionsUtilities.calculateNormalDiceCost((int)dcLevel, cpPerDC.intValue(), base.intValue() );
            }
            else if ( type.equals( KILLING_DICE_COST ) ) {
                Integer cpPerDC = (Integer)costArray[index][4];
                Integer base = (Integer)costArray[index][5];
                
                String dice = (String)parameterList.getParameterValue(parameter);
                
                double dcLevel = ChampionsUtilities.StringToKillingDC(dice);
                
                cost += ChampionsUtilities.calculateKillingDiceCost((int)dcLevel, cpPerDC.intValue(), base.intValue() );
                
            }
            else if ( type.equals( GEOMETRIC_COST ) ) {
                Integer X = (Integer)costArray[index][4];
                Integer Y = (Integer)costArray[index][5];
                Integer base = (Integer)costArray[index][6];
                
                Integer level=null;
                try {
                	level = (Integer)parameterList.getParameterValue(parameter);
                }
                catch(Exception e)
                {
                	if(parameter.equals("DistanceFromCollision"))
                	{
                		level = (Integer)parameterList.getParameterValue("Distance");
                	}
                	else 
                	{
                		throw e;
                	}
                }
                cost += ChampionsUtilities.calculateGeometricCost(level.intValue(), X.intValue(), Y.intValue(), base.intValue());
            }
            else if ( type.equals( LOGRITHMIC_COST ) ) {
                Integer cpPerMutliple = (Integer)costArray[index][4];
                Integer multiplier = (Integer)costArray[index][5];
                Integer base = (Integer)costArray[index][6];
                
                Integer level = (Integer)parameterList.getParameterValue(parameter);
                
                cost += ChampionsUtilities.calculateLogrithmicCost(level.intValue(), cpPerMutliple.intValue(), multiplier.intValue(), base.intValue());
            }
            else if ( type.equals( LIST_COST ) ) {
                Integer cpPerOption = (Integer)costArray[index][4];
                Integer base = (Integer)costArray[index][5];
                
                // Parse the parameter key in the form of "XXXXX*.YYYYY"
                String key = parameterList.getParameter(parameter).getKey();
                String keyName = key.substring(0, key.indexOf("*"));
                
                int optionCount = ability.getIndexedSize( keyName );
                
                cost += ChampionsUtilities.calculateListCost(optionCount, cpPerOption.intValue(), base.intValue());
            }
            else if ( type.equals( BOOLEAN_COST ) ) {
                Integer cpForOption = (Integer)costArray[index][4];
                
                boolean option = (Boolean)parameterList.getParameterValue(parameter);
                
                cost += ChampionsUtilities.calculateBooleanCost( option, cpForOption.intValue());
            }
            else if ( type.equals( COMBO_COST ) ) {
                Integer[] cpCostsArray = (Integer[])costArray[index][4];
                String[] optionNames = (String[])costArray[index][5];
                
                String selectedOption = (String)parameterList.getParameterValue(parameter);
                
                cost += ChampionsUtilities.calculateComboCost(selectedOption, cpCostsArray, optionNames);
            }
            else if ( type.equals( BASE_COST ) ) {
                Integer baseCost = (Integer)costArray[index][4];
                
                cost += baseCost.intValue();
            }
            else if ( type.equals( TOTALMULTIPLIER_COST ) ) {
                totalMultiplier = (Integer)parameterList.getParameterValue(parameter);
                if (totalMultiplier == null) {
                    totalMultiplier = new Integer(1);
                }
                
            }
            else {
                throw new RuntimeException("Unrecognized Type when processing costArray");
            }
        }
        
        return (int)ChampionsUtilities.roundValue( cost, false )* totalMultiplier.intValue();
    }
    
    /**
     * Reconfigures the Power dynamically so that the Power CP cost is less then or equals to the Adjusted Power Cost.
     * 
     * Powers should only override this method if they have very speciallized methods of reconfiguration.  Generally,
     * a power should depend on the Power version of this method.
     */
    public void reconfigurePower(Ability ability) {
        // Make sure this is not the Base ability.  Base ability should never be reconfigured dynamically.
        if ( ability == null || ability.isBaseInstance() ) return;
        
        Ability baseInstance = ability.getParentAbility();
        if ( baseInstance == null ) baseInstance = ability.getInstanceGroup().getBaseInstance();
        
        ability.calculateCPCost();
        
        // Base the Delta off the baseInstance configuration...
        int basePowerCost = baseInstance.getPowerCost();
        int currentPowerCost = ability.getPowerCost();
        double adjustedPowerCost = ability.getMaximumPowerAllocation();
        
        // If the currentPowerCost == adjustedPowerCost, just return.
        // There is nothing to see here. Move along. Move along.
        if ( currentPowerCost == Math.round(adjustedPowerCost) ) return;
        
        // Else Calculate the delta...
        double cpDelta = adjustedPowerCost - currentPowerCost;
        
        // Determine how to split the delta between the different dynamic parameter.
        // 1) Grab the cost array.
        // 2) Add up the Proportion for each parameter in the cost array, traking the largest proportion.
        // 3) Each parameter get delta * proportion / proportionTotal.
        
        // Grab array and check for null.
        Object[][] costArray = getCostArray(ability);
        if ( costArray == null ) return;
        
        // Grab the parameter list of the base Instance in case there are any PROPORTIONAL_RECONFIG parameters.
        ParameterList parameterList = getParameterList(ability);
        ParameterList baseParameterList = getParameterList(baseInstance);
        
        String parameter, type, dynamic;
        Integer proportion;
        int proportionTotal = 0;
        int index;
        int biggestIndex = -1;
        int biggestProportion = 0;
        double parameterDelta;
        
        for(index=0;index<costArray.length;index++) {
            parameter = (String)costArray[index][0];
            // type = (String)costArray[index][1];
            dynamic = (String)costArray[index][2];
            proportion = (Integer)costArray[index][3];
            
            if ( dynamic.equals( DYNAMIC_RECONFIG ) ) {
                if ( proportion.equals(PROPORTIONAL_RECONFIG) ) {
                    Object pv = baseParameterList.getParameterValue(parameter);
                    if ( pv instanceof Integer ) {
                        proportion = (Integer)pv;
                    }
                    else {
                        proportion = 1;
                        System.out.println( this.getClass().getCanonicalName() + " cost array incorrectly written.  PROPORTIONAL_RECONFIG only supported for integer parameter types.");
                    }
                }
                
                proportionTotal += proportion.intValue();
                
                if ( proportion.intValue() > biggestProportion ) {
                    biggestIndex = index;
                    biggestProportion = proportion.intValue();
                }
            }
        }
        
        // Now that proportions are known, distribute and reconfigure each parameter
        for(index=0;index<costArray.length;index++) {
            parameter = (String)costArray[index][0];
            type = (String)costArray[index][1];
            dynamic = (String)costArray[index][2];
            proportion = (Integer)costArray[index][3];
            
            if ( dynamic.equals( DYNAMIC_RECONFIG ) ) {
                if ( proportion.equals(PROPORTIONAL_RECONFIG) ) {
                    Object pv = baseParameterList.getParameterValue(parameter);
                    if ( pv instanceof Integer ) {
                        proportion = (Integer)pv;
                    }
                    else {
                        proportion = 1;
                    }
                }
                
                // Here is the magic.
                // Split the delta
                parameterDelta = cpDelta * proportion / proportionTotal;
               // if ( parameterDelta == 0 ) continue; // Just because there equal,
                // doesn't mean you can quit.
                // Reconfigure the parameter based on type.
                if ( type.equals( NORMAL_DICE_COST ) ) {
                    Integer cpPerDC = (Integer)costArray[index][4];
                    Integer base = (Integer)costArray[index][5];
                    Integer minimum = (Integer)costArray[index][6];
                    
                    String dice = (String)parameterList.getParameterValue(parameter);
                    
                    double dcLevel = ChampionsUtilities.StringToNormalDC(dice);
                    
                    int newLevel = ChampionsUtilities.calculateAdjustedNormalDiceCost(parameterDelta, (int)dcLevel, cpPerDC.intValue(), base.intValue(), minimum.intValue() );
                    
                    parameterList.setParameterValue(parameter, ChampionsUtilities.DCToNormalString(newLevel) );
                }
                else if ( type.equals( KILLING_DICE_COST ) ) {
                    Integer cpPerDC = (Integer)costArray[index][4];
                    Integer base = (Integer)costArray[index][5];
                    Integer minimum = (Integer)costArray[index][6];
                    
                    String dice = (String)parameterList.getParameterValue(parameter);
                    
                    double dcLevel = ChampionsUtilities.StringToKillingDC(dice);
                    
                    int newLevel = ChampionsUtilities.calculateAdjustedKillingDiceCost(parameterDelta, (int)dcLevel, cpPerDC.intValue(), base.intValue(), minimum.intValue() );
                    parameterList.setParameterValue(parameter, ChampionsUtilities.DCToKillingString(newLevel) );
                }
                else if ( type.equals( GEOMETRIC_COST ) ) {
                    Integer X = (Integer)costArray[index][4];
                    Integer Y = (Integer)costArray[index][5];
                    Integer base = (Integer)costArray[index][6];
                    Integer minimum = (Integer)costArray[index][7];
                    
                    Integer level = (Integer)parameterList.getParameterValue(parameter);
                    
                    int newLevel = ChampionsUtilities.calculateAdjustedGeometricCost(parameterDelta, level.intValue(), X.intValue(), Y.intValue(), base.intValue(), minimum.intValue());
                    parameterList.setParameterValue(parameter, new Integer(newLevel) );
                }
                else if ( type.equals( LOGRITHMIC_COST ) ) {
                    Integer cpPerMutliple = (Integer)costArray[index][4];
                    Integer multiplier = (Integer)costArray[index][5];
                    Integer base = (Integer)costArray[index][6];
                    Integer minimum = (Integer)costArray[index][7];
                    
                    Integer level = (Integer)parameterList.getParameterValue(parameter);
                    
                    int newLevel = ChampionsUtilities.calculateAdjustedLogrithmicCost(parameterDelta, level.intValue(), cpPerMutliple.intValue(), multiplier.intValue(), base.intValue(), minimum.intValue());
                    parameterList.setParameterValue(parameter, new Integer(newLevel) );
                }
                else if ( type.equals( LIST_COST ) ) {
                    // Lists can't be adjusted dynmically
                }
                else if ( type.equals( BOOLEAN_COST ) ) {
                    Integer cpForOption = (Integer)costArray[index][4];
                    
                    boolean option = (Boolean)parameterList.getParameterValue(parameter);
                    
                    boolean newLevel = ChampionsUtilities.calculateAdjustedBooleanCost(parameterDelta, option, cpForOption.intValue());
                    parameterList.setParameterValue(parameter, newLevel );
                }
                else if ( type.equals( COMBO_COST ) ) {
                    Integer[] cpCostsArray = (Integer[])costArray[index][4];
                    String[] optionNames = (String[])costArray[index][5];
                    
                    String selectedOption = (String)parameterList.getParameterValue(parameter);
                    
                    String newOption = ChampionsUtilities.calculateAdjustedComboCost(parameterDelta, selectedOption, cpCostsArray, optionNames);
                    parameterList.setParameterValue(parameter, newOption );
                }
                else {
                    throw new RuntimeException("Unrecognized Type when processing costArray");
                }
            }
        }
        
        /* Don't take the extra delta for now.  It will cause large items not to be changed, which small item
         * get all the extra.
         *
        // Now all the levels are adjusted.  Recalculate the power cost...
        calculateCPCost(ability);
         
        // Now figure out the delta again
        currentPowerCost = ability.getPowerCost();
         
        cpDelta = adjustedPowerCost - currentPowerCost;
         
        // If the delta isn't zero, try to put the extra in the larges proportioned index
        if ( cpDelta != 0 ) {
            parameter = (String)costArray[biggestIndex][0];
            type = (String)costArray[biggestIndex][1];
            dynamic = (String)costArray[biggestIndex][2];
            proportion = (Integer)costArray[biggestIndex][3];
         
            // Here is the magic.
            // Give all the remained to the power
            parameterDelta = cpDelta;
         
            // Reconfigure the parameter based on type.
            if ( type.equals( NORMAL_DICE_COST ) ) {
                Integer cpPerDC = (Integer)costArray[index][4];
                Integer base = (Integer)costArray[index][5];
                Integer minimum = (Integer)costArray[index][6];
         
                String dice = (String)parameterList.getParameterValue(parameter);
         
                double dcLevel = ChampionsUtilities.StringToNormalDC(dice);
         
                int newLevel = ChampionsUtilities.calculateAdjustedNormalDiceCost(parameterDelta, (int)dcLevel, cpPerDC.intValue(), base.intValue(), minimum.intValue() );
         
                parameterList.setParameterValue(parameter, new Integer(newLevel) );
            }
            else if ( type.equals( KILLING_DICE_COST ) ) {
                Integer cpPerDC = (Integer)costArray[index][4];
                Integer base = (Integer)costArray[index][5];
                Integer minimum = (Integer)costArray[index][6];
         
                String dice = (String)parameterList.getParameterValue(parameter);
         
                double dcLevel = ChampionsUtilities.StringToKillingDC(dice);
         
                int newLevel = ChampionsUtilities.calculateAdjustedKillingDiceCost(parameterDelta, (int)dcLevel, cpPerDC.intValue(), base.intValue(), minimum.intValue() );
                parameterList.setParameterValue(parameter, new Integer(newLevel) );
            }
            else if ( type.equals( GEOMETRIC_COST ) ) {
                Integer X = (Integer)costArray[index][4];
                Integer Y = (Integer)costArray[index][5];
                Integer base = (Integer)costArray[index][6];
                Integer minimum = (Integer)costArray[index][7];
         
                Integer level = (Integer)parameterList.getParameterValue(parameter);
         
                int newLevel = ChampionsUtilities.calculateAdjustedGeometricCost(parameterDelta, level.intValue(), X.intValue(), Y.intValue(), base.intValue(), minimum.intValue());
                parameterList.setParameterValue(parameter, new Integer(newLevel) );
            }
            else if ( type.equals( LOGRITHMIC_COST ) ) {
                Integer cpPerMutliple = (Integer)costArray[index][4];
                Integer multiplier = (Integer)costArray[index][5];
                Integer base = (Integer)costArray[index][6];
                Integer minimum = (Integer)costArray[index][7];
         
                Integer level = (Integer)parameterList.getParameterValue(parameter);
         
                int newLevel = ChampionsUtilities.calculateAdjustedLogrithmicCost(parameterDelta, level.intValue(), cpPerMutliple.intValue(), multiplier.intValue(), base.intValue(), minimum.intValue());
                parameterList.setParameterValue(parameter, new Integer(newLevel) );
            }
            else if ( type.equals( LIST_COST ) ) {
                // Lists can't be adjusted dynmically
            }
            else if ( type.equals( BOOLEAN_COST ) ) {
                Integer cpForOption = (Integer)costArray[index][4];
         
                String option = (String)parameterList.getParameterValue(parameter);
         
                boolean newLevel = ChampionsUtilities.calculateAdjustedBooleanCost(parameterDelta, option.equals("TRUE") ? true : false, cpForOption.intValue());
                parameterList.setParameterValue(parameter, newLevel ? "TRUE" : "FALSE" );
            }
            else if ( type.equals( COMBO_COST ) ) {
                Integer[] cpCostsArray = (Integer[])costArray[index][4];
                String[] optionNames = (String[])costArray[index][5];
         
                String selectedOption = (String)parameterList.getParameterValue(parameter);
         
                String newOption = ChampionsUtilities.calculateAdjustedComboCost(parameterDelta, selectedOption, cpCostsArray, optionNames);
                parameterList.setParameterValue(parameter, newOption );
            }
            else {
                throw new RuntimeException("Unrecognized Type when processing costArray");
            }
         
        } */
        
        configurePAD(ability, parameterList);
        ability.calculateCPCost();
    }
    
    /** Returns Power Cost array for this Power.
     *
     * The Power cost array is an Object[] array, which contains information detailing how to
     * calculate the cost of a power and reconfigure a power when the CP for an ability is adjusted.
     *
     * It is in the follow format:
     * Object[][] costArray = {
     * { Parameter, Type, Dynamic, ReconfigPercent, Type Options ... },
     * ...
     * }
     *
     * Where:
     * Parameter -> String representing the parameterName.  Must be parameter from getParameterArray() array.
     * Type -> Type of Cost Calculation: NORMAL_DICE_COST, KILLING_DICE_COST, GEOMETRIC_COST, LOGRITHMIC_COST,
     *     LIST_COST, BOOLEAN_COST, COMBO_COST.
     * Dynamic -> Indicater of Dynamic or Static reconfigurability: DYNAMIC_RECONFIG or STATIC_RECONFIG.
     * ReconfigPercent -> Integer indicate what percent of reconfigured CP should be allocated to this parameter
     *     by default.  Can be 0 to 100 or PROPORTIONAL_RECONFIG.  PROPORTIONAL_RECONFIG will base the proportion
     *     on the configuration of the base power.
     * Type Options -> Custom options depending on the specified type, as follows:
     *     NORMAL_DICE_COST -> PtsPerDC:Integer, Base:Integer, Minimum:Integer.
     *     KILLING_DICE_COST -> PtsPerDC:Integer, Base:Integer, Minimum:Integer.
     *     GEOMETRIC_COST -> PtsPerXlevels:Integer, Xlevels:Integer, Base:Integer, Minimum:Integer.
     *     LOGRITHMIC_COST -> PtsPerMultiple:Integer, Multiple:Integer, Base:Integer, Minimum:Integer.
     *     LIST_COST -> PtsPerItem:Integer, Base:Integer.
     *     BOOLEAN_COST -> PtsForOption:Integer.
     *     COMBO_COST -> OptionCostArray:Integer[], OptionNames:String[]
     *     BASE_COST -> Fixed base cost for power:Integer
     *
     */
    public Object[][] getCostArray(Ability ability) {
        return null;
    }
    
    /** Returns a Description of the Power
     */
    public String getDescription() {
        return "No Description Available.";
    }
    
    /** Returns a list of Caveats for Power
     */
    public String getCaveats() {
        String[] caveats = getCaveatArray();
        if ( caveats == null ) {
            return "No caveat information available.";
        }
        else if ( caveats.length == 0 ) {
            return "No known existing caveats.";
        }
        else {
            
            
            StringBuffer sb = new StringBuffer();
            sb.append("<HTML>");
            
            String beginPlainText = (String)UIManager.get("HTML.beginPlainText");
            if ( beginPlainText != null ) sb.append(beginPlainText);
            
            sb.append("<UL>");
            int index;
            for(index=0;index<caveats.length;index++) {
                sb.append("<LI>");
                sb.append( caveats[index] );
            }
            sb.append("</UL>");
            
            String endPlainText = (String)UIManager.get("HTML.endPlainText");
            if ( endPlainText != null ) sb.append(endPlainText);
            
            sb.append("</HTML>");
            return sb.toString();
        }
    }
    
    /** Returns whether power can be dynamcially reconfigured.
     */
    public boolean isDynamic() {
        return false;
    }
    
    /**
     * Returns a String[] of Caveats about the Power
     * Power uses this method to automatically build the getCaveats()
     * String.  The Strings returns by getCaveatArray() will be assembled into
     * list form and returned via getCaveats().
     * 
     * Return an empty array if there are no known caveats for this power.
     */
    public String[] getCaveatArray() {
        return null;
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
        return null;
    }
    
    /** Returns an AttackTreeNode which is placed immediately after the target effects for each target.
     *
     * getPostTriggerPowerNode allows the power a chance to create a node, which can be used
     * to perform actions after the effects of the power have been applied.  
     * 
     * For each Target with is hit by the power, getPostTriggerPowerNode will be called once.  If a non-null
     * value is returned, the node will be added to the AttackTree under the effect node for the relevant 
     * attack.  Only hit targets will cause getPostTriggerPowerNode to be called.
     *
     * If a manuever is in use, it will also have an oppertunity to generate an AttackTreeNode, which will
     * be added to the AttackTree.
     */
    public AttackTreeNode getPostTriggerPowerNode(BattleEvent be, Target target, String TargetGroup, int refNumber) {
        return null;
    }
    
    /** Executed when the power has been trigger.
     * TriggerPower should generate any effects it want to apply to the target (except damage effect being
     * generated automatically by BattleEngine).
     *
     * TriggerPower is called once for every single target which is going to be affected by the power.
     *
     * @param be BattleEvent of event currently being processed.
     * @param effectList List of Effect already being applied.
     * @param target Target of the effects.
     * @throws BattleEventException
     */
    public void triggerPower(BattleEvent be, DetailList effectList, Target target, int referenceNumber, String targetGroup) throws BattleEventException {
    }
    
    /** Returns an AttackTreeNode used to gather necessary information configure a Power.
     *
     * getSetupPowerNode allows the power a chance to create a setPower node, which can be used
     * to gather additional information necessary to apply the effect.
     *
     * getSetupPowerNode is called for both the ability and the maneuver of a power and can be used to display
     * panels necessary to gather information for the ability/maneuver.
     *
     * If the node provided by getSetupPowerNode is non-null, it will be inserted as children of the
     * AttackParameters panel.
     */
    public AttackTreeNode getSetupPowerNode(BattleEvent be, Target source) {
        return null;
    }
    
    public String toString() {
        return "Power: " + getName();
    }
    
    /** Initializes the Power when it is first loaded by the Simulator.
     *
     * The <code>initialize</code> method is gauranteed to be called at least once prior to
     * any use of the Power.  However, it is possible that the method would be called
     * multiple times.  If the initialization code can only be run one, the Power should
     * track whether it has been initialized already.
     */
    public void initialize() {
    }
    
    /** Returns an Array of Objects, representing custom/special advantages, limitations, special parameters usable with the power.
     * The Array should be in the format of class type (limitation, advantage, special) followed by the class name, repeated for
     * each additional custom added.  For example:
     *  array[] = { Limitation.class, "limitationLimitedSpecialFX", Advantage.class, "advantageVariableEffect" };
     */
    public Object[] getCustomAddersArray() {
        return null;
    }
    
    public boolean finalizeImport(Ability ability, AbilityImport ai) {
        return false;
    }
    
    /** Executed after triggerPower and before apply defenses.
     *
     * This power method is called at the same time that the prepower methods are called
     * for advantages and limitations.
     *
     */
    public void postpower(BattleEvent be, Ability ability, Effect effect, Target target, int targetReferenceNumber, String targetGroup, String hitLocationForDamage, boolean isFinalTarget, int priority) {
    }
    
    /** Executed after triggerPower and before apply defenses.
     *
     * This power method is called at the same time that the prepower methods are called
     * for advantages and limitations.
     *
     */
    public void prepower(BattleEvent be, Ability ability, Effect effect, Target target, int targetReferenceNumber, String targetGroup, String hitLocationForDamage, boolean isFinalTarget, int priority) {
    }
    
    /** Creates an Activate BattleEvent for the indicated Ability. 
     *
     * This method is run when the user clicks on the ability to activate the ability.
     * It should create the default action ability.  For most powers, this 
     * will be an activate battle event.
     *
     * If the user wishes to use a maneuver with this ability, the maneuver's 
     * getActivateAbilityBattleEvent will first be called.  If it returns null, this 
     * method will be called.  If it returns non-null, this method will be skipped
     * and it will be assumed that the manuever is taking care of the 
     * activation.  
     *
     * Generally maneuvers will allow the power to build the activation event,
     * but either may be required to perform the action.
     *
     * This method will not be called if the ability is already activated.
     */
    public BattleEvent getActivateAbilityBattleEvent(Ability ability, Ability maneuver, Target source) {
        BattleEvent be = new BattleEvent(ability);
        if ( maneuver != null ) be.setManeuver(maneuver);
        
        return be;
    }
        
     /** Creates an Activate BattleEvent for the indicated Maneuver. 
     *
     * This method is run when the user clicks on the ability to activate the ability
     * with a maneuver.  It should create the default action maneuver.  
     * For most powers, this will be an activate battle event.
     *
     * If the user wishes to use a maneuver with this ability, the maneuver's 
     * getActivateManeuverBattleEvent will first be called.  If it returns null, this 
     * method will be called.  If it returns non-null, this method will be skipped
     * and it will be assumed that the manuever is taking care of the 
     * activation.  
     *
     * Generally maneuvers will allow the power to build the activation event,
     * but either may be required to perform the action.
     *
     * This method will not be called if the ability is already activated.
     */
    public BattleEvent getActivateManeuverBattleEvent(Ability ability, Ability maneuver, Target source) {
        return null;
    }
    
    /** Creates a Deactivate BattleEvent for the indicated ActivationInfo. 
     *
     * This method is called to generate the deactivate battleEvent for the
     * indicated ActivationInfo.  This is always called for the primary
     * ability, not the maneuver.
     *
     * This method should NOT post the event.  Posting will be taken care of
     * by the caller.
     */
    public BattleEvent getDeactivateAbilityBattleEvent(ActivationInfo activationInfo) {
        return new BattleEvent(  BattleEvent.DEACTIVATE, activationInfo);
    }
    
    /** Compares the configuration to two Abilities. 
     * 
     * This method should return false if either ability doesn't have
     * the correct power.
     *
     * @return true if Abilities are configure the same, false otherwise.
     */
    public boolean compareConfiguration(Ability thisAbility, Ability thatAbility) {
        if ( this.equals(thisAbility.getPower()) == false ) return false;
        if ( this.equals(thatAbility.getPower()) == false ) return false;
        return getParameterList(thisAbility).compareConfiguration( getParameterList(thatAbility));
    }
 
    /** Returns the amount of Advantage configured in the power.
     *
     * Some Powers have inherent advantages or limitation built into them.  
     * Sometimes, it is easier to configure those with the power GUI.  This
     * method allows you to reflect those advantage/limitation multiplier costs
     * directly from the power.
     *
     * This should only return multiplier costs built into the ability.  External
     * advantages/limitation will be counted seperately.
     */
    public double getAdvantageMultiplier(Ability ability) {
        return 0;
    }
    
    /** Returns the amount of Limitation configured in the power.
     *
     * Some Powers have inherent advantages or limitation built into them.  
     * Sometimes, it is easier to configure those with the power GUI.  This
     * method allows you to reflect those advantage/limitation multiplier costs
     * directly from the power.
     *
     * This should only return multiplier costs built into the ability.  External
     * advantages/limitation will be counted seperately.
     */
    public double getLimitationMultiplier(Ability ability) {
        return 0;
    }
    
    /** 
     * Allows power to adjust the target set for an attack.
     *
     * PrimaryTargetNumber indicates if this particular target is considered 
     * a primary target for this attack.  If it is, primaryTargetNumber should
     * be one or greater.
     */
    public void adjustTarget(BattleEvent be, Target source, int referenceNumber, String targetGroup, int primaryTargetNumber) {

    }
    
    public ArrayList<Adjustable> getAdjustablesForTarget(Target target, ArrayList<Adjustable> list) {
        AbilityIterator ai = target.getAbilities();
        while ( ai.hasNext() ) {
            Ability targetAbility = ai.nextAbility();
            
            if ( targetAbility.getPower().getClass().equals( getClass() ) ) {
                // Should we break here?
                // Can there be multiple ones?
                if ( list == null ) list = new ArrayList<Adjustable>();
                list.add(targetAbility);
            }
        }
        return list;
    }
    
    /** Returns a Filter that accepts only eligible targets.
     *
     * Returns null if all targets are acceptible.
     */
    public Filter<Target> getTargetFilter(Ability ability) {
        return null;
    }

    
}