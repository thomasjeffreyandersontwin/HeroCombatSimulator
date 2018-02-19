/*
 * advCombatModifier.java
 *
 * Created on October 2, 2000, 10:05 PM
 */

package champions.powers;

import champions.*;
import champions.interfaces.*;
import champions.parameters.ParameterList;

/**
 *
 * @author  unknown
 * @version
 */
public class advantageDoesKnockback extends AdvantageAdapter implements ChampionsConstants{
    static final long serialVersionUID = -6399128355213532419L;
    /** Creates new advCombatModifier */
    
    private static boolean unique = true; // Indicates whether multiple copies can be added to ability
    private String advantageName="Does Knockback";
    static private Object[][] parameterArray = {};
    
    private static Object[][] patterns = {};
    
    public advantageDoesKnockback() {
    }
    
    public String getName() {
        return advantageName;
    }
    
    public Object[][] getParameterArray() {
        return parameterArray;
    }
    
    public boolean isUnique() {
        return unique;
    }
    
    public boolean configurePAD(Ability ability, ParameterList parameterList) {
        // Fail immediately if ability is null
        if ( ability == null ) return false;
        int index = ability.addAdvantageInfo(this, advantageName, parameterList);
        this.setAffectsDC(affectsDC);
        setDescription( getConfigSummary() );
        ability.setDoesKnockback(true);
        return true;
        
    }
    
    
    public String getConfigSummary() {
        return advantageName;
    }
     
    
    public double calculateMultiplier() {
        return .25;
    }
    
    public Object[][] getImportPatterns() {
        return patterns;
    }
    
}