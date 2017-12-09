/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package champions;

import champions.attackTree.AttackTreeModel;
import champions.attackTree.AttackTreeNode;
import champions.attackTree.ConfigureFrameworkRootNode;
import champions.exception.BattleEventException;
import champions.interfaces.Framework;

/**
 *
 * @author twalker
 */
public class ConfigureFrameworkBattleEvent extends RunnableBattleEvent {

    static final long serialVersionUID = -5433613071596122413L;

    private Target source;
    private Framework framework;

    private SkillRollInfo configurationSkillRoll;

    public ConfigureFrameworkBattleEvent(Target source, Framework framework) {
        this.source = source;
        this.framework = framework;
    }

    @Override
    public void processEvent() throws BattleEventException {
        AttackTreeNode node = new ConfigureFrameworkRootNode("Allocate Framework Root", this);
        AttackTreeModel atm = new AttackTreeModel(node);
        atm.processAttackTree();
    }

    /**
     * @return the framework
     */
    public Framework getFramework() {
        return framework;
    }

    /**
     * @param framework the framework to set
     */
    public void setFramework(Framework framework) {
        this.framework = framework;
    }

    /**
     * @return the target
     */
    @Override
    public Target getSource() {
        return source;
    }

    /**
     * @param target the target to set
     */
    @Override
    public void setSource(Target source) {
        this.source = source;
    }

    @Override
    public String getActivationTime() {
        return framework.getConfigurationTime().name();
    }

    public boolean isReconfigurationSuccessful() {
        boolean result = true;

        if ( configurationSkillRoll != null ) {
            result = configurationSkillRoll.isSuccessful();
        }

        return result;
    }

    public void setConfigurationSkillRoll(SkillRollInfo sri) {
        this.configurationSkillRoll = sri;
    }

}
