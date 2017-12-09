/*
 * AbstractFrameworkAbility.java
 *
 * Created on June 11, 2004, 1:32 PM
 */

package champions;


import champions.interfaces.AbilityList;
import champions.interfaces.Advantage;
import champions.interfaces.Framework;
import champions.interfaces.Limitation;
import champions.interfaces.PAD;
import champions.parameters.ParameterList;

/**
 *
 * @author  1425
 */
public class FrameworkAbility extends Ability {
    static final long serialVersionUID = -5432931536350725953L;
    
    /** Creates a new instance of AbstractFrameworkAbility */
    public FrameworkAbility() {
    }
    
    /** Override setName to keep framework ability name and sublist name in sync. 
     *
     */
    public void setName(String name) {
        super.setName(name);
        if ( getAbilityList() != null ) {
            getAbilityList().setName(name);
        }
    }
    /** Override addPAD to restrict the types of things that can be added.
     *
     * Elemental Control does not allow advantages applied to the base power.
     */
    public boolean addPAD(PAD pad, ParameterList pl) {        
        boolean result = super.addPAD(pad, pl);
        
        if ( pad instanceof Advantage ) {
            ((Advantage)pad).getParameterList().setVisible("Private", true);
        }
        else if ( pad instanceof Limitation ) {
            ((Limitation)pad).getParameterList().setVisible("Private", true);
        }
        
        AbstractFramework fm = (AbstractFramework)getFramework();
        if ( fm != null && this.isBaseInstance()) {
            int count = fm.getFrameworkAbilityInstanceGroupCount();
            for(int index = 0; index < count; index++) {
                AbilityInstanceGroup aig = fm.getFrameworkAbilityInstanceGroup(index);
                Ability child = aig.getBaseInstance();
                fm.configureAbilityModifiers(child);
            }
        }
        
        return result;
    }
    
    public void setAbilityList(AbilityList abilityList) {
        super.setAbilityList(abilityList);
        if ( abilityList != null ) {
            abilityList.setName(getName());
        }
    }
    
    /** Forces a reconfiguration of the abilities power.
     *
     * Override this method in Frameworks to reconfigure all member abilities.
     */
    public void reconfigurePower(Power p, ParameterList parameterList) {
        super.reconfigurePower(p, parameterList);
        
        Framework fm = getFramework();
        if ( fm != null && this.isBaseInstance()) {
            int count = fm.getFrameworkAbilityInstanceGroupCount();
            for(int index = 0; index < count; index++) {
                AbilityInstanceGroup aig = fm.getFrameworkAbilityInstanceGroup(index);
                Ability child = aig.getBaseInstance();
                child.reconfigurePower();
                child.calculateCPCost();
            }
        }
    }
    
    /** Override so framework does try to change it's own value.
     */
    
   public int calculateCPCost() {
        int base = 0;
        double adv = 0;
        double lim = 0;
        int cost = 0;
        int apcost = 0;
        
        int oldCP = cpCost;
        
        if ( isFixedCPEnabled() ) {
            base = getFixedCPCost();
        }
        else {
            Power p = getPower();
            base = (p==null) ? 0 : p.calculateCPCost(this);
        }
        // Store the power cost for reference.
        setPowerCost(base);
        
        
        adv = getAdvCost();
        
        lim = getLimCost();

        cost = ChampionsUtilities.roundValue( base * ( 1 + adv ) / ( 1 - lim ), false); 
        
        apcost = ChampionsUtilities.roundValue( base * ( 1 + adv ), false);
        
        setAPCost(apcost);
        setCPCost(cost);
        setRealCost(cost);
        
        if (  oldCP != cost ) {
            calculateENDCost();
        }
        
        return cost;
    } 
    
    /** Override to make sure we always have 0 END on the frameworkAbility.
     *
     */
    public int calculateENDCost() {
        return 0;
    }
    
    /** Overrides to make sure activating frameworks is never delayed.
     * 
     * @return  
     */
    public boolean isDelayed() {
        return false;
    }
    
    /** Override to force FrameworkAbility's to override this themselves.
     *
     * This utility method should create a plain, unconfigured Ability object
     * of the same class as the original.  This object will then be configured
     * by clone or createChildInstance to create a new Ability.
     */
    protected Ability createAbilityObject(boolean createInstanceGroup) {
        throw new NullPointerException("Child classes should override this method.");
    }
    
    
//    public String getDescription() {
//        String desc = "This ability provides a way to enable or disable all abilities contained in a framework.  Normally, the framework " +
//                "ability is activate.  However, this ability can be deactivated in order to deactivate all other abilities in the " +
//                "framework.";
//        
//        return desc;
//    }
    
    public String getHTMLDescription() {
        return ChampionsUtilities.createWrappedHTMLString(getDescription(), 80);
    }
    
    
        
}
