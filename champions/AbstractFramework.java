/*
 * AbstractFramework.java
 *
 * Created on June 9, 2004, 7:20 PM
 */

package champions;

import champions.interfaces.Framework;

/**
 *
 * @author  1425
 */
public abstract class AbstractFramework extends DetailList
implements Framework {
    static final long serialVersionUID = -5433010026351725953L;
    
    protected FrameworkAbility frameworkAbility = null;
    protected int frameworkPoolSize = 0;
    protected ReconfigurationMode mode = ReconfigurationMode.DEFAULT_MODE;
    
    VariablePointPoolAbilityConfiguration configuration;
    
    /** Creates a new instance of AbstractFramework */
    public AbstractFramework(FrameworkAbility frameworkAbility) {
        setFireChangeByDefault(false);
        setFrameworkAbility(frameworkAbility);

        createConfiguration();
    }

    protected void createConfiguration() {
       configuration  = new VariablePointPoolAbilityConfiguration(this);
    }
    
    /** Add the ability to the framework.
     *
     * This method should be called by the Ability when it is added to the 
     * framework.  If the ability cannot be added to the framework for some
     * reason, this method will return null.
     *
     */
    public boolean addFrameworkAbilityInstanceGroup(AbilityInstanceGroup abilityInstanceGroup) {
        if ( abilityInstanceGroup == null ) return false;
        if ( abilityInstanceGroup == getFrameworkAbility().getInstanceGroup()) return false;
        int index = findExactIndexed("FrameworkAbilityInstanceGroup", "AIG", abilityInstanceGroup);
        if ( index == -1 ) {
            index = createIndexed("FrameworkAbilityInstanceGroup", "AIG", abilityInstanceGroup, false);
            addIndexed(index, "FrameworkAbilityInstanceGroup", "CONFIGURED", "TRUE", true);
            
            // The Advantages/Limitations belonging to the Framework
            // should be passed onto the ability
            configureAbilityModifiers(abilityInstanceGroup.getBaseInstance());
            
            reconfigureFramework();
        }
        return true;
    }   
    
    /** Removes the Ability from the framework.
     *
     * This method should be called by the Ability when it is no longer a 
     * member of the framework.  
     *
     * This should only be called by the baseAbility of the Ability instance group.  
     * Other abiliies should use the base ability.
     */
    public void removeFrameworkAbilityInstanceGroup(AbilityInstanceGroup abilityInstanceGroup) {
        int index = findExactIndexed("FrameworkAbilityInstanceGroup", "AIG", abilityInstanceGroup);
        if ( index != -1 ) {
            removeAllIndexed(index, "FrameworkAbilityInstanceGroup");
            
            if ( configuration != null ) configuration.removeAbilityInstanceGroup(abilityInstanceGroup);
            
            unconfigureAbilityModifiers(abilityInstanceGroup.getBaseInstance());
            
            reconfigureFramework();
        }
    }
    
    /** Configures the appropriate advantages/limitations to the child abilites. 
     *
     * This method is called by addFrameworkAbility and whenever an advantage
     * or limitation is added/removed to/from the framework.  It should check that the
     * indicated ability has the all the necessary advantages and limitations,
     * as appropraite for the framework, adding or removing them as necessary.
     *
     * Each type of framework overrides this method approparitely.
     */
    protected abstract void configureAbilityModifiers(Ability ability);
    
    /** Unconfigures the appropriate advantages/limitations to the child abilites. 
     *
     * This method is called by removeFrameworkAbility. It should remove any 
     * advantages or limitations that were added by the framework due previously.
     *
     * Each type of framework overrides this method approparitely.
     */
    protected abstract void unconfigureAbilityModifiers(Ability ability);
    
    public int findFrameworkAbilityInstanceGroup(AbilityInstanceGroup aig) {
        return findExactIndexed("FrameworkAbilityInstanceGroup", "AIG", aig);
    }
    
    public int findFrameworkAbilityInstanceGroup(Ability ability) {
        return findExactIndexed("FrameworkAbilityInstanceGroup", "AIG", ability.getInstanceGroup());
    }
    
    public AbilityInstanceGroup getFrameworkAbilityInstanceGroup(int index) {
        return (AbilityInstanceGroup) getIndexedValue(index, "FrameworkAbilityInstanceGroup", "AIG");
    }
    
    public int getFrameworkAbilityInstanceGroupCount() {
        return getIndexedSize("FrameworkAbilityInstanceGroup");
    }
    
    public boolean isEnabled(Ability ability, Target target) {
        return isAbilityConfigured(ability);
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
    public abstract boolean isAbilityConfigured(Ability ability);
    
    /** Tells the framework that a changed in the framework has occurred.
     *
     * The framework should reconfigured the framework appropriately after
     * a framework change occurs.
     */
    public abstract void reconfigureFramework();
    
    /** Returns the Mode for the Framework.
     *
     */
    public ReconfigurationMode getFrameworkMode() {
//        int mode = 0;
//        Integer i = getIntegerValue("Framework.MODE");
//        if ( i == null  ) {
//            return ReconfigurationMode.DEFAULT_MODE;
//        }
//        else  {
//            mode = i.intValue();
//        
//        }
        ReconfigurationMode m = mode;
        if ( m == ReconfigurationMode.DEFAULT_MODE ) m = getDefaultFrameworkMode();
        return m;
    }
    
    public void setFrameworkMode(ReconfigurationMode mode){
//        if ( mode == ReconfigurationMode.DEFAULT_MODE ) {
//            //remove("Framework.MODE");
//        }
//        else {
//            //add("Framework.MODE", new Integer(mode), true);
//        }
        this.mode = mode;
    }
    
    protected ReconfigurationMode getDefaultFrameworkMode() {
        ReconfigurationMode mode = ReconfigurationMode.WARNING_ONLY;
        String s = (String)Preferences.getPreferenceList().getParameterValue("FrameworkMode");
        if ( s.equals("Warning Only")) {
            mode = ReconfigurationMode.WARNING_ONLY;
        }
        else if ( s.equals("Implicit Reconfiguration")) {
            mode = ReconfigurationMode.IMPLICIT_RECONFIG;
        }
        else if ( s.equals("Explicit Reconfiguration")) {
            mode = ReconfigurationMode.EXPLICIT_RECONFIG;
        }
        
        return mode;
        
    }
    
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
    abstract public int getFrameworkConfiguredPoints();
    
    /** Returns the size of the framework in points.
     *
     * The size of the framework is the maximum number of points that can be
     * configured in the framework at one time.  
     */
    public int getFrameworkPoolSize() {
//        Integer i = getIntegerValue("Framework.POOLSIZE");
//        return i == null ? 0 : i.intValue();
        return frameworkPoolSize;
    }
    
    /** Sets the maximum points that can be configured in the framework.
     *
     */
    public void setFrameworkPoolSize(int frameworkPoolSize) {
//        add("Framework.POOLSIZE", new Integer(points), true);
        this.frameworkPoolSize = frameworkPoolSize;
    }
    
    /** Overrides the setFramework of Ability to throw an error.
     *
     */
    public void setFramework(Framework framework) {
        if ( framework != this ) 
            throw new IllegalArgumentException("Framework Abilities can not exist "
                + "inside of another framework.");
    }
    
    /**
     * Getter for property frameworkAbility.
     * @return Value of property frameworkAbility.
     */
    public FrameworkAbility getFrameworkAbility() {
        //return (FrameworkAbility)getValue("Framework.FRAMEWORKABILITY");
        return frameworkAbility;
    }
    
    /**
     * Setter for property frameworkAbility.
     * @param frameworkAbility New value of property frameworkAbility.
     */
    public void setFrameworkAbility(FrameworkAbility frameworkAbility) {
        //add("Framework.FRAMEWORKABILITY", frameworkAbility, true, false);
        this.frameworkAbility = frameworkAbility;
    }

    public VariablePointPoolAbilityConfiguration getConfiguration() {
        return configuration;
    }
    
}
