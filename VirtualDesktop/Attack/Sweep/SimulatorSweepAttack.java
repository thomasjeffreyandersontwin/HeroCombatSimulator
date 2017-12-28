package VirtualDesktop.Attack.Sweep;

import javax.swing.tree.TreeNode;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import VirtualDesktop.Attack.SingleAttackAdapter;
import VirtualDesktop.Character.CharacterAdaptor;
import VirtualDesktop.Roster.SingleAttackResults;
import champions.Ability;
import champions.Battle;
import champions.BattleEvent;
import champions.Target;
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
import champions.battleMessage.ActivateAbilityMessageGroup;
import champions.battleMessage.EmbeddedBattleEventMessageGroup;
import champions.battleMessage.SweepMessageGroup;
import champions.interfaces.IndexIterator;

public class SimulatorSweepAttack extends VirtualDesktop.Attack.SingleAttackAdapter {
	private SingleTargetNode targetNode = null;
	

	public SimulatorSweepAttack(String name, CharacterAdaptor character) {
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
			champions.Target t = new CharacterAdaptor(targetName).UnderlyingCharacter;
			SelectTargetPanel.ad.selectTarget(t);
		}
		else {
			SingleTargetNode execNode = GetTargetNode(i);

			champions.Target t = new CharacterAdaptor(targetName).UnderlyingCharacter;

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
	
	public JSONObject ExportBasedOnBattleEvent(String token, BattleEvent sweepEvent) {
		String type= "SweepTargetResult";
		JSONObject sweepAttackJSON = new JSONObject();
		sweepAttackJSON.put("Type", type);
		
		JSONArray attacks = new JSONArray(); 
		sweepAttackJSON.put("Attacks", attacks );
		int sweeps = sweepEvent.getIndexedSize("LinkedAbility");
		for(int i = 0;i< sweeps; i++)
		{
			BattleEvent event = (BattleEvent) sweepEvent.getIndexedValue(i, "LinkedAbility", "BATTLEEVENT");
			IndexIterator index =event.getActivationInfo().getTargetGroupIterator(".ATTACK");
			while(index.hasNext()) {
				int  j =  index.nextIndex();
				Target t  = event.getActivationInfo().getTarget(j);
				if(t!=null) {
					int tindex = event.getActivationInfo().getTargetIndex(t, ".ATTACK") ;
					JSONObject targetJSON = new JSONObject();
					ExportSingleAttackResults(targetJSON, event,tindex);
					ExportSingleKnockback( targetJSON, event,tindex);
					
					attacks.add(targetJSON);
					targetJSON.put("Ability", event.getAbility().getName());
				}
				
			}
		}
		
		/*
		SweepMessageGroup group = (SweepMessageGroup) sweepEvent.getPrimaryBattleMessageGroup(); 

		for(int i=0; i< group.getChildCount();i++) {
			if(group.getChild(i) instanceof EmbeddedBattleEventMessageGroup) {
				EmbeddedBattleEventMessageGroup emb = (EmbeddedBattleEventMessageGroup)group.getChild(i);
				BattleEvent be = emb.battleEvent;
				ActivateAbilityMessageGroup embeddedGroup = (ActivateAbilityMessageGroup) be.getPrimaryBattleMessageGroup();
				
				type = AbilityExporter.DetermineAttackType(embeddedGroup);
				SingleAttackResults abilityResult = AbilityResultFactory.GetAbilityForType(type);
				JSONObject resultJSON =  abilityResult.buildFrom(embeddedGroup);
				attacks.add(resultJSON);
			}
		}
		*/
		sweepAttackJSON.put("Token", token);
		return sweepAttackJSON;
	}
	

}
