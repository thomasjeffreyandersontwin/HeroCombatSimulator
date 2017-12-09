/*
 * MultipowerFramework.java
 *
 * Created on June 11, 2004, 1:20 PM
 */

package champions;

import champions.enums.ActivationTime;
import champions.interfaces.Advantage;
import champions.interfaces.Framework.ReconfigurationMode;
import champions.interfaces.Limitation;
import champions.interfaces.SpecialParameter;
import champions.parameters.ParameterList;
import champions.powers.SpecialParameterMultipowerSlot;
import champions.powers.limitationCharges;
import champions.powers.limitationCharges.limitationFrameworkCharges;

/**
 *
 * @author  1425
 */
public class MultipowerFramework extends AbstractFramework {
    static final long serialVersionUID = -5234071536350722003L;
    
    /** Creates a new instance of MultipowerFramework */
    public MultipowerFramework(FrameworkAbility frameworkAbility) {
        super(frameworkAbility);
    }
    
    public int getFrameworkConfiguredPoints() {
        ReconfigurationMode mode = getFrameworkMode();
        if ( mode == ReconfigurationMode.DEFAULT_MODE ) mode = getDefaultFrameworkMode();
        if ( mode == ReconfigurationMode.WARNING_ONLY || mode == ReconfigurationMode.IMPLICIT_RECONFIG ) {
            // Count the activated abilities
            int activePoints = 0;
            int count = getFrameworkAbilityInstanceGroupCount();
            for(int i = 0; i < count; i++ ) {
                AbilityInstanceGroup aig = getFrameworkAbilityInstanceGroup(i);
                Ability a = aig.getCurrentInstance();
                if ( a.isActivated(null) ) {
                    activePoints += getCostToConfigure(a);
                }
            }
            return activePoints;
        }
        else {
            // Explicit...
            return 0;
        }
    }
    
    protected int getCostToConfigure(Ability ability) {
        if ( ability.getBooleanValue("Multipower.FIXEDSLOT") ) {
            // Use the base instance if this is a fixed slot
            return ability.getInstanceGroup().getBaseInstance().getAPCost();
        }
        else {
            // Use the current instance if this is a flexible slot
            return ability.getAPCost();
        }
    }
    
    public void reconfigureFramework() {
    }
    
    protected void configureAbilityModifiers(Ability ability) { 
        // Check that it has the MultipleSlot special parameter...
        if ( ability.hasSpecialParameter("Multipower Slot Type") == false && ! (ability.getAbilityList() instanceof CombinedAbility) ) {
            ability.addSpecialParameter( new SpecialParameterMultipowerSlot() );
        }
        
        // Add all limitations to children...
        boolean hasCharges = false;
        FrameworkAbility afa = getFrameworkAbility();
        int count = afa.getLimitationCount();
        for(int i = 0; i < count; i++) {
            Limitation lim = afa.getLimitation(i);
            if ( lim.isPrivate() ) continue;
            
            if ( lim instanceof limitationCharges ) {
                // Create a limitationFrameworkCharges for the children...
                hasCharges = true;
                lim = new limitationFrameworkCharges();
            }
            
            int index = ability.findLimitation(lim);
            if ( index == -1 ) {
                Limitation newLim = lim.clone();
                newLim.setAddedByFramework(true);
                
                // If it isn't on there, add it...
                ParameterList pl = lim.getParameterList();
                ParameterList pl2 = new ParameterList(pl);
                ability.addPAD(newLim, pl2);
            }
        }
        
        // Now remove any excess FRAMEWORKLIMITATIONS on the ability.
        count = ability.getLimitationCount();
        for(int i = 0; i < count; i++) {
            //if ( ability.getIndexedBooleanValue(i, "Limitation", "ADDEDBYFRAMEWORK") ) {
            Limitation lim = ability.getLimitation(i);
            if ( lim.isAddedByFramework() ) {
                //if ( afa.findExactIndexed("Limitation", "LIMITATION", lim) == -1 ) {
                if ( afa.findLimitation(lim) == -1 ) {
                    if ( hasCharges == false || ! (lim instanceof limitationFrameworkCharges)) {
                        ability.removeLimitation(i);
                        count --;
                    }
                }
            }
        }
        
        // Add all limitations to children...
        hasCharges = false;
        count = afa.getAdvantageCount();
        for(int i = 0; i < count; i++) {
            Advantage adv = afa.getAdvantage(i);
            if ( adv.isPrivate() ) continue;
            
            int index = ability.findAdvantage(adv);
            if ( index == -1 ) {
                Advantage newAdv = adv.clone();
                newAdv.setAddedByFramework(true);
                
                // If it isn't on there, add it...
                ParameterList pl = adv.getParameterList();
                ParameterList pl2 = new ParameterList(pl);
                newAdv.setZeroCost(true);
                ability.addPAD(newAdv, pl2);
            }
        }
        
        // Now remove any excess FRAMEWORKLIMITATIONS on the ability.
        count = ability.getAdvantageCount();
        for(int i = 0; i < count; i++) {
            //if ( ability.getIndexedBooleanValue(i, "Advantage", "ADDEDBYFRAMEWORK") ) {
            Advantage adv = ability.getAdvantage(i);
            if ( adv.isAddedByFramework() ) {
                //if ( afa.findExactIndexed("Advantage", "LIMITATION", lim) == -1 ) {
                if ( afa.findAdvantage(adv) == -1 ) {
                    ability.removeAdvantage(i);
                    count --;
                }
            }
        }
        
        count = afa.getSpecialEffectCount();
        for(int i = 0; i < count; i++) {
            SpecialEffect sp = afa.getSpecialEffect(i);
            int index = ability.findExactIndexed("SpecialEffect", "SPECIALEFFECT", sp);
            if ( index == -1 ) {
                // If it isn't on there, add it...
                ability.addSpecialEffect(sp);
                //ability.reconfigureLimitation(lim, pl2, index);
            }
            
            ability.setAddedByFramework(sp,true);
        }
        
        // Now remove any excess on the ability.
        count = ability.getSpecialEffectCount();
        for(int i = 0; i < count; i++) {
            if ( ability.getIndexedBooleanValue(i, "SpecialEffect", "ADDEDBYFRAMEWORK") ) {
                SpecialEffect sp = ability.getSpecialEffect(i);
                ability.removeSpecialEffect(sp);
                count --;
            }
        }
        
        
    }
    
    protected void unconfigureAbilityModifiers(Ability ability) {
        // Check that it has the MultipleSlot special parameter...
        SpecialParameter sp = ability.findSpecialParameter("Multipower Slot Type");
        if ( sp != null ) {
            ability.removeSpecialParameter(sp);
        }

        // remove any excess FRAMEWORKLIMITATIONS on the ability.
        int count = ability.getLimitationCount();
        for(int i = 0; i < count; i++) {
            //if ( ability.getIndexedBooleanValue(i, "Limitation", "FRAMEWORKLIMITATION") ) {
            Limitation lim = ability.getLimitation(i);
            if ( lim.isAddedByFramework() ) {
                ability.removeLimitation(i);
                count --;
            }
        }
    }
    
    /** Returns whether a given ability is current enabled.
     *
     * This method is called by Ability.isEnabled to determine if the ability is currently
     * enabled.  This can be used by sublists to control abilities for special list types such
     * as Multipower and Variable Point Pool.<P>
     *
     * If the Framework does not care to modify whether an ability is enabled,
     * it should return true. 
     *
     * The ability that is passed to this method should be the actual ability
     * that will be activated (the template/instance) if possible, as framework
     * my allow some templates while denying others.
     */
    public boolean isFrameworkAbilityEnabled(Ability ability, Target target) {
        FrameworkAbility afa = getFrameworkAbility();
        if ( ability instanceof FrameworkAbility ) return true;
        
        if ( afa != null && afa.isActivated(target) == false ) {
            String s = "Framework ability is not currently activated.  Activate the framework ability to enable framework powers.";
                ability.setEnableMessage( s);
            return false;
        }
        
        ReconfigurationMode mode = getFrameworkMode();
        if ( mode == ReconfigurationMode.DEFAULT_MODE ) mode = getDefaultFrameworkMode();
        if ( mode == ReconfigurationMode.WARNING_ONLY || mode == ReconfigurationMode.IMPLICIT_RECONFIG ) {
            // Count the activated abilities
            int activePoints = 0;
            int count = getFrameworkAbilityInstanceGroupCount();
            for(int i = 0; i < count; i++ ) {
                AbilityInstanceGroup aig = getFrameworkAbilityInstanceGroup(i);
                Ability a = aig.getCurrentInstance();
                if ( a.isActivated(target) ) {
                    activePoints += getCostToConfigure(a);
                }
            }
            
            int needed = 0;
            if ( ability.isActivated(target) == false ) needed = getCostToConfigure(ability);
            
            if ( getFrameworkPoolSize() < activePoints  ) {
                String s = "Multipower Framework is over-configured. " + activePoints + " active points are configured out of a pool of " + getFrameworkPoolSize() + ".";
                if ( needed > 0 ) s += "A Pool of " + (activePoints + needed) + " is needed to activate " + ability.getNameWithInstance() + ".";
                ability.setEnableMessage( s);
                if ( mode == ReconfigurationMode.WARNING_ONLY ) {
                    ability.setEnableColor( Ability.getEnableErrorColor() );
                    return true;
                }
                else { // implicit mode...
                    ability.setEnableColor( Ability.getEnableErrorColor() );
                    return false;
                }
            }
            else if ( getFrameworkPoolSize() < activePoints + needed ) {
                String s = "Multipower already has " + activePoints + " active points configured out of a pool of " + getFrameworkPoolSize() + ".";
                if ( needed > 0 ) s += " A Pool of " + (activePoints + needed) + " is needed to activate " + ability.getNameWithInstance() + ".";
                ability.setEnableMessage( s);
                if ( mode == ReconfigurationMode.WARNING_ONLY ) {
                    ability.setEnableColor( Ability.getEnableWarningColor() );
                    return true;
                }
                else { // implicit mode...
                    ability.setEnableColor( Ability.getEnableErrorColor() );
                    return false;
                }
            }
        }
        else if ( mode == ReconfigurationMode.EXPLICIT_RECONFIG ) {
            if ( isAbilityConfigured(ability) == false ) {
                // This needs to be much smarter
            
                String s = ability.getNameWithInstance() + " is not currently configured in the framework. (Framework Mode is Explicit Reconfiguration.)";
                ability.setEnableMessage( s);
                ability.setEnableColor( Ability.getEnableErrorColor() );
                return false;
            }
        }
        return true;
    }
    
    /** Calculates the Real cost of the power.
     *
     */
     public int calculateCost(Ability ability, int baseCost, double advantages, double limitations) {
        boolean fixed = false;
        
        if ( ability.getAbilityList() instanceof CombinedAbility ) {
            fixed = ((Ability)ability.getAbilityList()).getBooleanValue("Multipower.FIXEDSLOT");
        }
        else {
            fixed = ability.getBooleanValue("Multipower.FIXEDSLOT");
        }
        int activePoints = (int)Math.round( baseCost * ( 1 + advantages));
        double realCost = activePoints / (fixed?10.0:5.0);
        realCost =  realCost / (1-limitations);
        return (int)ChampionsUtilities.roundValue(realCost, false);
    }
     
     /** Returns whether the indicated ability is current active.
     *
     * Active abilities are abilities in the framework that are currently 
     * allowed to be used within the framework.  If an ability is not currently
     * active, it can not be used.
     *
     * The active state of an ability is stored indexed on the baseInstance of
     * an ability.
     */ 
    public boolean isAbilityConfigured(Ability ability) {
        return true;
    }

        public Ability getConfigurationSkill() {
        return null;
    }

    public ActivationTime getConfigurationTime() {
        return ActivationTime.INSTANT;
    }
}
