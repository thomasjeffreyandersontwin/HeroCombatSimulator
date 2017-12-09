/*
 * PADFolderNode.java
 *
 * Created on January 14, 2003, 2:49 PM
 */

package champions.abilityTree.PADTree;

import champions.Ability;
import champions.Battle;
import champions.ElementalControlAbility;
import champions.MultipowerAbility;
import champions.PADFolder;
import champions.PADRoster;
import champions.SpecialEffect;
import champions.VariablePointPoolAbility;
import champions.interfaces.AbilityList;
import champions.interfaces.Advantage;
import champions.interfaces.ChampionsConstants;
import champions.interfaces.Limitation;
import champions.interfaces.SpecialParameter;
import java.util.Iterator;


/**
 *
 * @author  Trevor Walker
 */
public class PADRootNode extends PADTreeTableNode 
implements ChampionsConstants {
    
    /** Holds value of property folderName. */
    private int mask;
    
    /** Creates a new instance of PADFolderNode */
    public PADRootNode(int mask) {
        this.mask = mask;
        buildChildren();
    }
    
    private void buildChildren() {
        PADFolder folder = PADRoster.getFolder();
        
        Iterator i;
        
        // Add the odd ones, like presets...
        if ( (mask & GENERIC_PRESETS_MASK) != 0 ) {
            AbilityList al = PADRoster.getGenericPresets();
            if ( al != null ) {
                PADAbilityListNode node = new PADAbilityListNode(al, "Presets");
                
                add(node);
            }
        }
        
        if ( (mask & GENERIC_RECENT_MASK) != 0 ) {
            AbilityList al = Battle.currentBattle.getRecentGenericAbilityList();
            if ( al != null ) {
                PADAbilityListNode node = new PADAbilityListNode(al, "Recent");
                node.setDeleteEnabled(true);
                add(node);
            }
        }
        
        i = folder.getFolderIterator();
        while ( i.hasNext() ) {
            PADFolder f = (PADFolder)i.next();
            String name = f.getName();
            if ( (name.equals("Powers") && (POWER_MASK & mask) != 0) || 
                 (name.equals("Skills") && (SKILL_MASK & mask) != 0) ||
                 (name.equals("Disadvantages") && (DISADVANTAGE_MASK & mask) != 0) ||
                 (name.equals("Talents") && (TALENT_MASK & mask) != 0) ||
                 (name.equals("Perks") && (PERK_MASK & mask) != 0) ||
                 (name.equals("Advantages") && (ADVANTAGE_MASK & mask) != 0) ||
                 (name.equals("Limitations") && (LIMITATION_MASK & mask) != 0) ||
                 (name.equals("Special Parameters") && (SPECIAL_PARAMETERS_MASK & mask) != 0) ||
                 (name.equals("Special Effects") && (SPECIAL_EFFECTS_MASK & mask) != 0) ) {
                     
                PADFolderNode node = new PADFolderNode(name);
                add(node);
            }
        }
        
        i = folder.getPADIterator();
        while ( i.hasNext() ) {
            String name = (String)i.next();
            PADTreeTableNode node = null;
            Class c = PADRoster.getPADClass(name);
            if ( c != null ) {
                if ( c.equals(Ability.class) && (POWER_MASK & mask) != 0) {
                    node = new PADPowerNode(name);
                }
                else if ( Advantage.class.isAssignableFrom( c ) && (ADVANTAGE_MASK & mask) != 0) {
                    node = new PADAdvantageNode(name);
                }
                else if ( Limitation.class.isAssignableFrom( c ) && (LIMITATION_MASK & mask) != 0) {
                    node = new PADLimitationNode(name);
                }
                else if ( SpecialParameter.class.isAssignableFrom( c ) && (SPECIAL_PARAMETERS_MASK & mask) != 0) {
                    node = new PADSpecialParameterNode(name);
                }
                else if ( SpecialEffect.class.equals(c) && (SPECIAL_EFFECTS_MASK & mask) != 0) {
                    node = new PADSpecialEffectNode(name);
                }
                add(node);
            }
        }
        
        if ( (mask & FRAMEWORK_MASK) != 0) {
            PADTreeTableNode frameworkFolder = new PADTreeTableNode();
            frameworkFolder.setUserObject("Frameworks");
            
            PADFrameworkNode node;
            
            node = new PADFrameworkNode("Elemental Control", ElementalControlAbility.class);
            frameworkFolder.add(node);
            
            node = new PADFrameworkNode("Multipower", MultipowerAbility.class);
            frameworkFolder.add(node);
            
            node = new PADFrameworkNode("Variable Point Pool", VariablePointPoolAbility.class);
            frameworkFolder.add(node);
            
            add(frameworkFolder);
        }
        
        if ( (mask & POWER_MASK) != 0 ) {
            PADTreeTableNode node = new PADCombinedAbilityNode();
            add(node);
        }
    }
    
}
