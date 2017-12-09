/*
 * AutofireAttackNode.java
 *
 * Created on December 16, 2001, 2:00 PM
 */

package champions.attackTree;


import champions.ActivationInfo;
import champions.BattleEvent;
import champions.CVList;
import champions.Effect;
import champions.SweepBattleEvent;
import champions.Target;
import champions.exception.BattleEventException;
import champions.interfaces.CombatLevelProvider;

/**
 *
 * @author  twalker
 * @version
 */
public class SweepActivatingNode extends DefaultAttackTreeNode {
    
    /** Creates new AutofireAttackNode */
    public SweepActivatingNode(String name) {
        this.name = name;
        setVisible(false);
    }

    @Override
    public boolean activateNode(boolean manualOverride) throws BattleEventException {
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");

        callAbilityIsActivating();
        
        // The root node should never accept active node status, since
        // It isn't really a real node.
        return false;
    }
    
    protected void callAbilityIsActivating() throws BattleEventException {
        Target source = battleEvent.getSource();

        if ( source != null ) {
            for (Effect effect : source.getEffects()) {
                effect.abilityIsActivating(battleEvent, getBattleEvent().getSweepAbility());

                if ( effect instanceof CombatLevelProvider && ((CombatLevelProvider)effect).applies(getBattleEvent().getSweepAbility())) {
                    ((CombatLevelProvider)effect).activateCombatLevels(battleEvent, true);
                }
            }
        }

    }

    @Override
    public SweepBattleEvent getBattleEvent() {
        return (SweepBattleEvent)battleEvent;
    }

}
