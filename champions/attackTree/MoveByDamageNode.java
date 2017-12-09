/*
 * MoveByDamageNode.java
 *
 * Created on January 21, 2002, 10:58 AM
 */

package champions.attackTree;

import champions.BattleEngine;
import champions.Dice;
import champions.Effect;
import champions.Target;
import champions.exception.BattleEventException;
import champions.exceptionWizard.ExceptionWizard;
import javax.swing.UIManager;

/**
 *
 * @author  twalker
 * @version 
 */
public class MoveByDamageNode extends DefaultAttackTreeNode {

    private int startMessageCount;
    private int endMessageCount;
    
    /** Creates new DefensesNode */
    public MoveByDamageNode(String name) {
        this.name = name;
        icon = UIManager.getIcon("AttackTree.summaryIcon");
    }
    
    public boolean activateNode(boolean manualOverride) {
        boolean activateNode = false;
        
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        
        startMessageCount = battleEvent.getMessageCount();
        
        // Generate Knockback Damage Effect
        Dice d = battleEvent.getDiceRoll("DamageDie", getTargetGroup());
        String defense = battleEvent.getAbility().getDefense();
        
        Effect effect = BattleEngine.createNormalEffects(d, defense);
        
        try {
            //String knockbackGroup = battleEvent.getActivationInfo().getKnockbackGroup( getTargetGroup() );
            //BattleEngine.proccessEffectsForTarget(getBattleEvent(), getTargetGroup(), targetReferenceNumber);
            BattleEngine.applyEffectToTarget(battleEvent, battleEvent.getSource(), effect, null, true);
       }
       catch (BattleEventException bee) {
            ExceptionWizard.postException(bee);
       }
        
        endMessageCount = battleEvent.getMessageCount();
        
        if ( startMessageCount == endMessageCount ) {
            battleEvent.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( battleEvent.getSource().getName() + " was not affected.", MSG_COMBAT)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( battleEvent.getSource().getName() + " was not affected.", MSG_COMBAT)); // .addMessage( battleEvent.getSource().getName() + " was not affected.", MSG_COMBAT);
            endMessageCount = battleEvent.getMessageCount();
        }
        
        if ( nodeRequiresInput() || manualOverride == true ) {
            MessagePanel mp = MessagePanel.getDefaultPanel(battleEvent, battleEvent.getSource(), startMessageCount, endMessageCount);
            attackTreePanel.showInputPanel(this,mp);
            attackTreePanel.setInstructions("Hit Okay to Continue...");
            
            activateNode = true;
        }
        
        return activateNode;
    }
    
    private boolean nodeRequiresInput(){
        boolean requiresInput = false;
        
        if ( startMessageCount < endMessageCount ) {
            requiresInput = true;
        }
        
        return requiresInput && getAutoBypassTarget().getBooleanProfileOption(getAutoBypassOption());
    }
    
    
    public AttackTreeNode buildNextChild(AttackTreeNode activeChild) {
        // This node build everything upfront, when it's target is first set.
        return null;
    }
    
    /**
     * Causes node to process an advance and perform any work that it needs to do.
     *
     * This is a method introduced by DefaultAttackTreeNode.  DefaultAttackTreeNode
     * delegates to this method if advanceNode node is called and this is the active
     * node.  This method should do all the work of advanceNode whenever it can, since
     * the next node processing and buildNextChild methods depend on the existing
     * DefaultAttackTreeNode advanceNode method.
     *
     * Returns true if it is okay to leave the node, false if the node should
     * be reactivated to gather more information.
     */
    public boolean processAdvance() {
        return true;
    }
        
    public String getAutoBypassOption() {
        return "SHOW_APPLY_EFFECTS_PANEL";
    }
    
    public Target getAutoBypassTarget() {
        return battleEvent.getSource();
    }

}
