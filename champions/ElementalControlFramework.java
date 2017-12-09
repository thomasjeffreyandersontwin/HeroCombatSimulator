/*
 * ElementalControlFramework.java
 *
 * Created on June 11, 2004, 1:20 PM
 */

package champions;

import champions.enums.ActivationTime;
import champions.interfaces.Limitation;
import champions.parameters.ParameterList;
import champions.powers.limitationCharges;
import champions.powers.limitationCharges.limitationFrameworkCharges;

/**
 *
 * @author  1425
 */
public class ElementalControlFramework extends AbstractFramework {
    static final long serialVersionUID = -5436071532230725953L;
    /** Creates a new instance of ElementalControlFramework */
    public ElementalControlFramework(FrameworkAbility frameworkAbility) {
        super(frameworkAbility);
    }
    
    public int getFrameworkConfiguredPoints() {
        return -1;
    }
    
    public void reconfigureFramework() {
    }
    
    protected void configureAbilityModifiers(Ability ability) {        
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
        
        count = afa.getSpecialEffectCount();
        for(int i = 0; i < count; i++) {
            SpecialEffect sp = afa.getSpecialEffect(i);
            int index = ability.findExactIndexed("SpecialEffect", "SPECIALEFFECT", sp);
            if ( index == -1 ) {
                // If it isn't on there, add it...
                ability.addSpecialEffect(sp);
                //ability.reconfigureLimitation(lim, pl2, index);
            }
            
            index = ability.findExactIndexed("SpecialEffect", "SPECIALEFFECT", sp);
            if ( index != -1 ) {
                // This should alway occur...
                ability.addIndexed(index, "SpecialEffect", "ADDEDBYFRAMEWORK", "TRUE", true, false); 
            }
        }
        
        // Now remove any excess FRAMEWORKLIMITATIONS on the ability.
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

        // remove any excess FRAMEWORKLIMITATIONS on the ability.
        int count = ability.getLimitationCount();
        for(int i = 0; i < count; i++) {
            //if ( ability.getIndexedBooleanValue(i, "Limitation", "FRAMEWORKLIMITATION") ) {
            Limitation lim = ability.getLimitation(i);
            if ( lim.isAddedByFramework() ) {
                ability.removeLimitation(i);
                count--;
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
        if ( ability.getInstanceGroup() == afa.getInstanceGroup() ) return true;
        if ( afa.isActivated(target) == false ) {
            ability.setEnableMessage( "Elemental Control " + this.getName() + " is not currently activated.");
            return false;
        }
        return true;
    }
    
     public int calculateCost(Ability ability, int baseCost, double advantages, double limitations) {
        int activePoints = (int)Math.round( baseCost * ( 1 + advantages));
        int realCost = (int)Math.round( (activePoints - getFrameworkPoolSize()) / (1-limitations));
        return realCost;
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
