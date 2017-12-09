/*
 * Framework.java
 *
 * Created on June 9, 2004, 6:30 PM
 */
package champions.interfaces;

import champions.Ability;
import champions.AbilityInstanceGroup;
import champions.FrameworkAbility;
import champions.Skill;
import champions.Target;
import champions.VariablePointPoolAbilityConfiguration;
import champions.enums.ActivationTime;

/**
 *
 * @author  1425
 */
public interface Framework {

    public enum ReconfigurationMode {

        /** Indicates the default preference mode should be used. */
        DEFAULT_MODE,
        /** Warning Only Mode.  All abilities are always active and can be used
         *  at any time.  The will be displayed with a GUI warning if the framework
         *  is overutilized.
         */
        WARNING_ONLY,
        /** Implicit Reconfiguration.  All abilities are active up until the point
         *  cap for the framework has been reached by the currently activated 
         *  abilities.  After the point cap is reach, no more abilities can be 
         *  activated.
         */
        IMPLICIT_RECONFIG,
        /** Explicit Reconfiguration mode.  Abilities are disabled until they 
         *  are explicitly configured as enabled in the framework.  Reconfiguration
         *  typically can only be done at the start of a phase and may require
         *  additional time per framework rules.
         */
        EXPLICIT_RECONFIG;
    }

    /** Add the ability to the framework.
     *
     * This method should be called by the Ability when it is added to the 
     * framework.  If the ability cannot be added to the framework for some
     * reason, this method will return null.
     *
     * This should only be called by the baseAbility of an Ability instance
     * group.  Other abilities should use the base ability.
     */
    public boolean addFrameworkAbilityInstanceGroup(AbilityInstanceGroup abilityInstanceGroup);

    /** Removes the Ability from the framework.
     *
     * This method should be called by the Ability when it is no longer a 
     * member of the framework.  
     *
     * This should only be called by the baseAbility of the Ability instance group.  
     * Other abiliies should use the base ability.
     */
    public void removeFrameworkAbilityInstanceGroup(AbilityInstanceGroup abilityInstanceGroup);

    /** Returns the CP Cost of the Ability.
     *
     * This method is used to determine the cost of the Ability belonging to a list.  In general,
     * the standard formula of Base * ( 1 + adv ) / (1 + lim ) should be used.  However, in some 
     * cases, the cost is overriden due to special calculations, such as Multipower and Variable Point pool.
     *
     * This method is used to calculate the cost of both the CP Cost and the Adjusted CP Cost, so the ability
     * cost should be based on only the powerCost, advantage, and limitation parameters provided.
     * 
     * The baseCost is obviously the base cost of the ability.
     *
     * The advantages is the total advantage modifier.  This may include framework advantages,
     * so the Framework may need to retotal the advantages skipping specific ones.
     *
     * Limitations is the total limitation modifier.  This may include framework limitations,
     * so the Framework may need to retotal the limitations skipping specific ones.
     *
     * @return The final real cost of the ability.  For base instances, this 
     * should be the amount the ability cost the character.  For other instances
     * it is often the active or real cost of the ability (depending on the framework).
     */
    public int calculateCost(Ability ability, int baseCost, double advantages, double limitations);

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
    public boolean isFrameworkAbilityEnabled(Ability ability, Target target);

    /** Returns the index of the indicated AbilityInstanceGroup.
     *
     */
    public int findFrameworkAbilityInstanceGroup(AbilityInstanceGroup aig);

    /** Returns the index of the instance group indicated ability belongs to.
     *
     */
    public int findFrameworkAbilityInstanceGroup(Ability ability);

    /** Returns the member AbilityInstanceGroup at the indicated instance.
     *
     */
    public AbilityInstanceGroup getFrameworkAbilityInstanceGroup(int index);

    /** Returns the number of member AbilityInstanceGroups.
     */
    public int getFrameworkAbilityInstanceGroupCount();

    /** Returns the FrameworkAbility associated with this framework.
     *
     */
    public FrameworkAbility getFrameworkAbility();

    /** Returns the current mode of the framework.
     *
     */
    public ReconfigurationMode getFrameworkMode();

    /** Sets the current mode of the framework.
     *
     */
    public void setFrameworkMode(ReconfigurationMode newMode);

    /** Returns the number of Points currently configured in the framework.
     *
     * This method returns the number of points configured in the framework.
     * This value will depend on both the framework and the mode.
     *
     * The framework will determine if the activePoints or realPoints will be
     * counted.
     *
     * The mode will determine if only configured powers or all active powers
     * will be counted.
     */
    public int getFrameworkConfiguredPoints();

    /** Returns the size of the framework in points.
     *
     * The size of the framework is the maximum number of points that can be
     * configured in the framework at one time.  
     */
    public int getFrameworkPoolSize();

    /** Sets the maximum points that can be configured in the framework.
     *
     */
    public void setFrameworkPoolSize(int poolSize);//    /** Configures the ability for use with the frame.
    
    /** Returns the configuration information for this framework.
     * 
     */
    public VariablePointPoolAbilityConfiguration getConfiguration();

    /** Return the skill ability necessary to reconfigure the framework during combat.
     *
     * If the framework does not require a skill roll to configure, this will be
     * null.
     */
    public Ability getConfigurationSkill();

    /** Returns the amount of time necessary to reconfigure the framework during combat.
     *
     */
    public ActivationTime getConfigurationTime();
     
//     *
//     */
//    public void configureAbilityModifiers(Ability ability);
//    
//    /** Unconfigures the ability for use with the frame.
//     *
//     */
//    public void unconfigureAbilityModifiers(Ability ability);
}
