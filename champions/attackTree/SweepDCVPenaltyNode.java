/*
 * AutofireAttackNode.java
 *
 * Created on December 16, 2001, 2:00 PM
 */

package champions.attackTree;

import champions.*;
import champions.battleMessage.CVModiferMessage;
import champions.battleMessage.CVMultiplerMessage;
import champions.exception.BattleEventException;
import champions.powers.effectCombatModifier;
import champions.powers.effectCombatMultiplier;

/**
 *
 * @author  twalker
 * @version
 */
public class SweepDCVPenaltyNode extends DefaultAttackTreeNode {
    
    /** Creates new AutofireAttackNode */
    public SweepDCVPenaltyNode(String name) {
        this.name = name;
        setVisible(false);
    }
    
    public boolean activateNode(boolean manualOverride) {
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        
        try {
            if ( ((SweepBattleEvent)getBattleEvent()).hasTwoWeaponFighting() ) {
                // We will throw the DCV modifier in here just because I don't have anywhere
                // else to put it....
                Effect dcvMod = new effectCombatModifier("Sweep/Rapid Fire DCV Penalty", 0, -2,0,true);
                dcvMod.addEffect( battleEvent, battleEvent.getSource());
                
                //battleEvent.addBattleMessage( new CVModiferMessage(battleEvent.getSource(), true, "DCV", -2));// + " is now at -2 DCV.", BattleEvent.MSG_ABILITY)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( battleEvent.getSource() + " is now at -2 DCV.", BattleEvent.MSG_ABILITY)); // .addMessage( battleEvent.getSource() + " is now at -2 DCV.", BattleEvent.MSG_ABILITY);
            } else {
                // We will throw the DCV modifier in here just because I don't have anywhere
                // else to put it....
                Effect dcvMod = new effectCombatMultiplier("Sweep/Rapid Fire DCV Penalty", 1, .5);
                dcvMod.addEffect( battleEvent, battleEvent.getSource());
                
                //battleEvent.addBattleMessage( new CVMultiplerMessage( battleEvent.getSource(), true,  + " is now at half DCV.", BattleEvent.MSG_ABILITY)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( battleEvent.getSource() + " is now at half DCV.", BattleEvent.MSG_ABILITY)); // .addMessage( battleEvent.getSource() + " is now at half DCV.", BattleEvent.MSG_ABILITY);
            }
        } catch ( BattleEventException bee ) {
            battleEvent.setError( bee.getMessage() );
        }
        
        // The root node should never accept active node status, since
        // It isn't really a real node.
        return true;
    }
    
    
    public AttackTreeNode buildNextChild(AttackTreeNode activeChild) {
        
        AttackTreeNode nextNode = null;
        
        return nextNode;
    }
    
    public String nextNodeName(String previousNodeName) {
        String nextNodeName = null;
        
        
        return nextNodeName;
    }
}
