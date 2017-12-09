/*
 * ProcessActivateRoot.java
 *
 * Created on April 25, 2002, 2:17 PM
 */

package champions.attackTree;

import champions.Ability;
import champions.ActivationInfo;
import champions.BattleEvent;
import champions.ConfigureFrameworkBattleEvent;
import champions.VariablePointPoolAbilityConfiguration.ConfigurationEntry;
import champions.exception.BattleEventException;
import java.util.Iterator;

/**
 *
 * @author  Trevor Walker
 * @version
 */
public class ConfigureFrameworkApplyNode extends DefaultAttackTreeNode {

    protected Ability ability;
    protected ConfigurationEntry entry;

    boolean shutdownAbility = false;

    /** Creates new ProcessActivateRoot */
    public ConfigureFrameworkApplyNode(String name, Ability ability, ConfigurationEntry entry) {
        super();
        this.name = name;
        this.ability = ability;
        this.entry = entry;
        
        setVisible(false);
    }
    
    @Override
    public boolean activateNode(boolean manualOverride) {
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");

        return false;
    }

    @Override
    public boolean processAdvance() throws BattleEventException {
        
        if ( getBattleEvent().isReconfigurationSuccessful() ) {
            shutdownAbility = getBattleEvent().getFramework().getConfiguration().applyActionsForAbility(ability, battleEvent);
        }
        else {
            shutdownAbility = getBattleEvent().getFramework().getConfiguration().failActionsForAbility(ability, battleEvent);
        }

        return true;
    }

    @Override
    public void checkNodes() {
        if ( shutdownAbility == false ) {
            for(int i = getChildCount() - 1; i >= 0; i--) {
                removeChild((AttackTreeNode)getChildAt(i));
            }
        }
        else {
            if ( getChildCount() == 0 ) {
                // We have to create shutdown nodes for each of the activations of this ability...
                Iterator<ActivationInfo> it = ability.getActivations();
                while(it.hasNext()) {
                    ActivationInfo ai = it.next();

                    BattleEvent newBE = new BattleEvent(BattleEvent.DEACTIVATE, ai);
                    ProcessEmbeddedEventNode node = new ProcessEmbeddedEventNode("Deactivating " + ability.getName(), newBE);
                    addChild(node);
                }
            }
        }
    }

    @Override
    public ConfigureFrameworkBattleEvent getBattleEvent() {
        return (ConfigureFrameworkBattleEvent)battleEvent;
    }
}
