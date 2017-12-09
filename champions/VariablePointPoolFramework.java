/*
 * VariablePointPoolFramework.java
 *
 * Created on June 11, 2004, 1:20 PM
 */
package champions;

import champions.enums.ActivationTime;
import champions.interfaces.Framework.ReconfigurationMode;
import champions.interfaces.Limitation;
import champions.parameters.ParameterList;
import champions.powers.advantageCosmic;
import champions.powers.advantageNoSkillRollRequired;
import champions.powers.advantagePowersCanBeChangedAsAHalfPhaseAction;
import champions.powers.advantagePowersCanBeChangedAsAZeroPhaseAction;
import champions.powers.limitationCharges;
import champions.powers.limitationCharges.limitationFrameworkCharges;

/**
 *
 * @author  1425
 */
public class VariablePointPoolFramework extends AbstractFramework {

    static final long serialVersionUID = -5234071531530722003L;

    /** Creates a new instance of Variable Point PoolFramework */
    public VariablePointPoolFramework(FrameworkAbility frameworkAbility) {
        super(frameworkAbility);
    }

    public int getFrameworkConfiguredPoints() {
        ReconfigurationMode mode = getFrameworkMode();

        if (mode == ReconfigurationMode.WARNING_ONLY || mode == ReconfigurationMode.IMPLICIT_RECONFIG) {
            // Count the activated abilities
            int activePoints = 0;
            int count = getFrameworkAbilityInstanceGroupCount();
            for (int i = 0; i < count; i++) {
                Ability a = getFrameworkAbilityInstanceGroup(i).getCurrentInstance();
                if (a.isActivated(null)) {
                    activePoints += a.getCPCost();
                }
            }
            return activePoints;
        } else {
            // Explicit...
            return 0;
        }
    }

    public void reconfigureFramework() {
    }

    protected void configureAbilityModifiers(Ability ability) {
        // Add all limitations to children...
        boolean hasCharges = false;
        FrameworkAbility afa = getFrameworkAbility();
        int count = afa.getLimitationCount();
        for (int i = 0; i < count; i++) {
            Limitation lim = afa.getLimitation(i);
            if (lim.isPrivate()) {
                continue;
            }

            if (lim instanceof limitationCharges) {
                // Create a limitationFrameworkCharges for the children...
                hasCharges = true;
                lim = new limitationFrameworkCharges();
            }

            int index = ability.findLimitation(lim);
            if (index == -1) {
                Limitation newLim = lim.clone();
                newLim.setAddedByFramework(true);

                // If it isn't on there, add it...
                ParameterList pl = lim.getParameterList(afa, i);
                ParameterList pl2 = new ParameterList(pl);
                ability.addPAD(newLim, pl2);
            }
        }

        // Now remove any excess FRAMEWORKLIMITATIONS on the ability.
        count = ability.getLimitationCount();
        for (int i = 0; i < count; i++) {
            //if ( ability.getIndexedBooleanValue(i, "Limitation", "ADDEDBYFRAMEWORK") ) {
            Limitation lim = ability.getLimitation(i);
            if (lim.isAddedByFramework()) {
                //if ( afa.findExactIndexed("Limitation", "LIMITATION", lim) == -1 ) {
                if (afa.findLimitation(lim) == -1) {
                    if (hasCharges == false || !(lim instanceof limitationFrameworkCharges)) {
                        ability.removeLimitation(i);
                        count--;
                    }
                }
            }
        }

        count = afa.getSpecialEffectCount();
        for (int i = 0; i < count; i++) {
            SpecialEffect sp = afa.getSpecialEffect(i);
            int index = ability.findExactIndexed("SpecialEffect", "SPECIALEFFECT", sp);
            if (index == -1) {
                // If it isn't on there, add it...
                ability.addSpecialEffect(sp);
            //ability.reconfigureLimitation(lim, pl2, index);
            }

            index = ability.findExactIndexed("SpecialEffect", "SPECIALEFFECT", sp);
            if (index != -1) {
                // This should alway occur...
                ability.addIndexed(index, "SpecialEffect", "ADDEDBYFRAMEWORK", "TRUE", true, false);
            }
        }

        // Now remove any excess FRAMEWORKLIMITATIONS on the ability.
        count = ability.getSpecialEffectCount();
        for (int i = 0; i < count; i++) {
            if (ability.getIndexedBooleanValue(i, "SpecialEffect", "ADDEDBYFRAMEWORK")) {
                SpecialEffect sp = ability.getSpecialEffect(i);
                ability.removeSpecialEffect(sp);
                count--;
            }
        }
    }

    protected void unconfigureAbilityModifiers(Ability ability) {

        // remove any excess FRAMEWORKLIMITATIONS on the ability.
        int count = ability.getLimitationCount();
        for (int i = 0; i < count; i++) {
            //if ( ability.getIndexedBooleanValue(i, "Limitation", "FRAMEWORKLIMITATION") ) {
            Limitation lim = ability.getLimitation(i);
            if (lim.isAddedByFramework()) {
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
        if (ability instanceof FrameworkAbility) {
            return true;
        }
        ReconfigurationMode mode = getFrameworkMode();
        if (mode == ReconfigurationMode.WARNING_ONLY || mode == ReconfigurationMode.IMPLICIT_RECONFIG) {
            // Count the activated abilities
            int activePoints = 0;
            int count = getFrameworkAbilityInstanceGroupCount();
            for (int i = 0; i < count; i++) {
                Ability a = getFrameworkAbilityInstanceGroup(i).getCurrentInstance();
                if (a.isActivated(target)) {
                    activePoints += a.getCPCost();
                }
            }

            int needed = 0;
            if (ability.isActivated(target) == false) {
                needed = ability.getCPCost();
            }

            if (getFrameworkPoolSize() < activePoints) {
                String s = "<b>Framework Warning:</b> Variable Point Pool Framework is over-configured. " + activePoints + " active points are configured out of a pool of " + getFrameworkPoolSize() + ".";
                if (needed > 0) {
                    s += "A Pool of " + (activePoints + needed) + " is needed to activate " + ability.getNameWithInstance() + ".";
                }
                ability.setEnableMessage(s);
                if (mode == ReconfigurationMode.WARNING_ONLY) {
                    ability.setEnableColor(Ability.getEnableErrorColor());
                    return true;
                } else { // implicit mode...
                    ability.setEnableColor(Ability.getEnableErrorColor());
                    return false;
                }
            } else if (getFrameworkPoolSize() < activePoints + needed) {
                String s = "<b>Framework Warning:</b> Variable Point Pool already has " + activePoints + " active points configured out of a pool of " + getFrameworkPoolSize() + ".";
                if (needed > 0) {
                    s += " A Pool of " + (activePoints + needed) + " is needed to activate " + ability.getNameWithInstance() + ".";
                }
                ability.setEnableMessage(s);
                if (mode == ReconfigurationMode.WARNING_ONLY) {
                    ability.setEnableColor(Ability.getEnableWarningColor());
                    return true;
                } else { // implicit mode...
                    ability.setEnableColor(Ability.getEnableErrorColor());
                    return false;
                }
            }
        } else if (mode == ReconfigurationMode.EXPLICIT_RECONFIG) {
            if (isAbilityConfigured(ability) == false) {
                // This needs to be much smarter

                String s = ability.getNameWithInstance() + " is not currently configured in the framework. (Framework Mode is Explicit Reconfiguration.)";
                ability.setEnableMessage(s);
                ability.setEnableColor(Ability.getEnableErrorColor());
                return false;
            }
        }
        return true;
    }

    public int calculateCost(Ability ability, int baseCost, double advantages, double limitations) {
        return 0;
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

        ReconfigurationMode mode = getFrameworkMode();

        if (mode == ReconfigurationMode.WARNING_ONLY || mode == ReconfigurationMode.IMPLICIT_RECONFIG) {
            return true;
        } else {
            VariablePointPoolAbilityConfiguration.VPPConfigurationStatus status = getConfiguration().getAbilityStatus(ability);
            return (status == VariablePointPoolAbilityConfiguration.VPPConfigurationStatus.ENABLED || status == VariablePointPoolAbilityConfiguration.VPPConfigurationStatus.PARENT_ENABLED);

        }
    }

    public Ability getConfigurationSkill() {
        //Ability frameworkAbility = getFrameworkAbility();


        if (frameworkAbility.hasAdvantage(advantageCosmic.advantageName) || frameworkAbility.hasAdvantage(advantageNoSkillRollRequired.advantageName)) {
            return null;
        } else {
            ParameterList pl = frameworkAbility.getPowerParameterList();
            Ability skill = null;

            Object o = pl.getParameterValue("Skill");
            if (o instanceof AbilityAlias) {
                skill = ((AbilityAlias) o).getAliasReferent();
            } else if (o instanceof Ability) {
                skill = (Ability) o;
            }

            return skill;
        }
    }

    public ActivationTime getConfigurationTime() {
        if (frameworkAbility.hasAdvantage(advantagePowersCanBeChangedAsAHalfPhaseAction.advantageName)) {
            return ActivationTime.HALFMOVE;
        } else if (frameworkAbility.hasAdvantage(advantagePowersCanBeChangedAsAZeroPhaseAction.advantageName) ||
                frameworkAbility.hasAdvantage(advantageCosmic.advantageName)) {
            return ActivationTime.INSTANT;
        } else {
            return ActivationTime.FULLMOVE;
        }
    }
}
