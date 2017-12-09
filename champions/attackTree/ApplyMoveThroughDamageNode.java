/*
 * MoveThroughEffectNode.java
 *
 * Created on January 20, 2002, 12:51 PM
 */

package champions.attackTree;

import champions.BattleEngine;
import champions.BattleEvent;
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
public class ApplyMoveThroughDamageNode extends DefaultAttackTreeNode {
    
    private int startMessageCount;
    private int endMessageCount;
    
    /** Creates new DefensesNode */
    public ApplyMoveThroughDamageNode(String name) {
        this.name = name;
        icon = UIManager.getIcon("AttackTree.summaryIcon");
    }
    
    public boolean activateNode(boolean manualOverride) {
        boolean activateNode = false;
        
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        
        startMessageCount = battleEvent.getMessageCount();
        
        int kbDistance = battleEvent.getKnockbackDistance(0);
        Target source = battleEvent.getSource();
        
        if ( kbDistance > 0 ) {
            battleEvent.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(source.getName() + "'s move-through knocked the target back.", BattleEvent.MSG_ABILITY)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(source.getName() + "'s move-through knocked the target back.", BattleEvent.MSG_ABILITY)); // .addMessage(source.getName() + "'s move-through knocked the target back.", BattleEvent.MSG_ABILITY);
            battleEvent.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(source.getName() + " will take half of the damage of the attack.", BattleEvent.MSG_ABILITY)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(source.getName() + " will take half of the damage of the attack.", BattleEvent.MSG_ABILITY)); // .addMessage(source.getName() + " will take half of the damage of the attack.", BattleEvent.MSG_ABILITY);
        }
        else {
            battleEvent.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(source.getName() + "'s move-through did not knock the target back.", BattleEvent.MSG_ABILITY)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(source.getName() + "'s move-through did not knock the target back.", BattleEvent.MSG_ABILITY)); // .addMessage(source.getName() + "'s move-through did not knock the target back.", BattleEvent.MSG_ABILITY);
            battleEvent.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(source.getName() + " will take the full damage of the attack.", BattleEvent.MSG_ABILITY)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(source.getName() + " will take the full damage of the attack.", BattleEvent.MSG_ABILITY)); // .addMessage(source.getName() + " will take the full damage of the attack.", BattleEvent.MSG_ABILITY);
        }
        
        // Generate Knockback Damage Effect
        Dice d = battleEvent.getDiceRoll("DamageDie", getTargetGroup());
        String defense = battleEvent.getAbility().getDefense();
        
        Effect effect = BattleEngine.createNormalEffects(d, defense);
        
        if ( kbDistance > 0 ) {
            for(int i = 0; i < effect.getSubeffectCount(); i++ ) {
                double amount = effect.getSubeffectValue(i);
                effect.setSubeffectValue(i, Math.round(amount/2) );
            }
        }
        
        int stun = effect.getTotalStunDamage();
        int body = effect.getTotalBodyDamage();
        battleEvent.addBattleMessage( new champions.battleMessage.LegacyBattleMessage("Pre-defense damage to " + source.getName() + " is " + stun + " stun, " + body + " body.", BattleEvent.MSG_DICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage("Pre-defense damage to " + source.getName() + " is " + stun + " stun, " + body + " body.", BattleEvent.MSG_DICE)); // .addMessage("Pre-defense damage to " + source.getName() + " is " + stun + " stun, " + body + " body.", BattleEvent.MSG_DICE);

        
        try {
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
