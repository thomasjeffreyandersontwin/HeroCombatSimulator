/*
 * ATSingleCombatLevelsListTree.java
 *
 * Created on June 8, 2004, 10:34 PM
 */

package champions.abilityTree2;

import champions.CombatLevelList;
import champions.Target;

/**
 *
 * @author  1425
 */
public class ATCombatLevelsTree extends ATTree {
    
    /** Holds the CombatLevelList of this tree. */
    private CombatLevelList combatLevelsList;
//    
//    /** Holds the Sublist name to show. */
//    private String sublistName;
    
    /**
     * Creates a new instance of ATSingleCombatLevelsListTree
     */
    public ATCombatLevelsTree() {
       setUpdateBlockedWhileProcessing(false);
    }
    
    protected void setupModel() {
        ATNode root = new ATCombatLevelsNode(null, new ATSingleTargetNodeFactory(), getNodeFilter(), true);
        ATModel model = new ATCombatLevelsModel(root, getTitle());
        setTreeTableModel(model);
        root.setTree(this);
    }

    public boolean isManagingFocus() {
        return true;
    }

    public boolean isFocusCycleRoot() {
        return true;
    }
    
    /**
     * Getter for property combatLevelsList.
     * @return Value of property combatLevelsList.
     */
    public CombatLevelList getCombatLevelsList() {
        return combatLevelsList;
    }



     
//    /**
//     * Setter for property combatLevelsList.
//     * @param combatLevelsList New value of property combatLevelsList.
//     */
//    protected void setCombatLevelsList(CombatLevelList combatLevelsList) {
//        if ( this.combatLevelsList != combatLevelsList  ) {
//
//            this.combatLevelsList = combatLevelsList;
//
//            Object root = getProxyTreeTableModel().getRoot();
//            if ( root instanceof ATCombatLevelsNode ) {
//                ((ATCombatLevelsNode)root).setCombatLevelsList(combatLevelsList);
//            }
//        }
//    }

    public void setup(CombatLevelList combatLevelsList) {
        if ( this.combatLevelsList != combatLevelsList ) {
            this.combatLevelsList = combatLevelsList;

            Object root = getProxyTreeTableModel().getRoot();
            if ( root instanceof ATCombatLevelsNode ) {
                ((ATCombatLevelsNode)root).setup(combatLevelsList);
            }
        }
    }

    
}
