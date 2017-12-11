package VirtualDesktop.Attack.Sweep;

import javax.swing.tree.TreeNode;

import VirtualDesktop.CombatSimulatorCharacter;
import VirtualDesktop.SingleAttack.SimulatorSingleAttack;
import champions.Ability;
import champions.Battle;
import champions.attackTree.AttackTreeModel;
import champions.attackTree.AttackTreeNode;
import champions.attackTree.AttackTreePanel;
import champions.attackTree.KnockbackEffectNode;
import champions.attackTree.KnockbackNode;
import champions.attackTree.SelectTargetPanel;
import champions.attackTree.SingleAttackNode;
import champions.attackTree.SingleTargetNode;
import champions.attackTree.SweepActivateAbilityNode;
import champions.attackTree.SweepActivateRootNode;
import champions.attackTree.SweepExecuteNode;
import champions.attackTree.SweepSetupPanel;

public class SimulatorSweepAttack extends VirtualDesktop.SingleAttack.SimulatorSingleAttack {
	private SingleTargetNode targetNode = null;
	

	public SimulatorSweepAttack(String name, CombatSimulatorCharacter character) {
		super(name, character);
		// TODO Auto-generated constructor stub
	}

	public void AddAbility(String abilityName) {
		Ability ability = this.Character.UnderlyingCharacter.getAbility(abilityName);
		if(ability==null) {
			ability = Battle.currentBattle.getDefaultAbilities().getAbility(abilityName, true);	
		}
		SweepSetupPanel.ad.battleEvent.addLinkedAbility(ability, false);
		targetNum++;
		
	}

	public void SelectTargetForSpecificAttack(String targetName, int i) {
		if(i==0) {
			champions.Target t = new CombatSimulatorCharacter(targetName).UnderlyingCharacter;
			SelectTargetPanel.ad.selectTarget(t);
		}
		else {
			SingleTargetNode execNode = GetTargetNode(i);

			champions.Target t = new CombatSimulatorCharacter(targetName).UnderlyingCharacter;

			AttackTreeModel.treeModel.advanceAndActivate(execNode, execNode);
			execNode.setTarget(t);
			AttackTreePanel.Panel.advanceNode();			
			
		}
	}

	private SingleTargetNode GetTargetNode(int i) {
		AttackTreeModel model = AttackTreeModel.treeModel;
		SweepActivateRootNode node = ((SweepActivateRootNode)model.getRoot());
		TreeNode n = node.getChildAt(i+1);
		n = n.getChildAt(3);
		SingleTargetNode execNode = (SingleTargetNode) n;		
		return execNode;
	}
	
	private SingleTargetNode GetKnockbackNode(int i) {
		AttackTreeModel model = AttackTreeModel.treeModel;
		TreeNode node = ((TreeNode)model.getRoot());
		TreeNode n = node.getChildAt(i+1);
		SingleTargetNode sNode=null;
		try {
			sNode = (SingleTargetNode) n.getChildAt(5).getChildAt(1).getChildAt(0);		
		}catch(NullPointerException e) {
			sNode=null;
		}
		return sNode;
	}
	
	

	public void EnterKnockbackForSpecificTarget(String knockbackTargetName, int i) {
		AttackTreeModel model = AttackTreeModel.treeModel;
		SingleTargetNode knockBackTargetNode = GetKnockbackNode(i);
		if(knockBackTargetNode!=null) {
			AttackTreeModel.treeModel.advanceAndActivate(knockBackTargetNode , knockBackTargetNode);		
			SetTargetByName(knockbackTargetName);
		}
	}
	
	
	

}
