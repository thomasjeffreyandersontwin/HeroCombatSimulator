package VirtualDesktop.Attack.Sweep;

import java.util.ArrayList;

import VirtualDesktop.Attack.AttackResultAdapter;
import VirtualDesktop.Attack.MultiAttack.MultiAttackResultAdapter;
import champions.BattleEvent;
import champions.SweepBattleEvent;
import champions.attackTree.DefaultAttackTreeNode;
import champions.attackTree.SingleTargetNode;
import champions.attackTree.SweepActivateAbilityNode;
import champions.attackTree.SweepActivateRootNode;
import champions.attackTree.SweepExecuteNode;

public class SweepAttackResultAdapter extends MultiAttackResultAdapter {

	public SweepAttackResultAdapter(BattleEvent battleEvent, int tindex) {
		super(battleEvent, tindex);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected DefaultAttackTreeNode getRootAttackNode() {
		return SweepExecuteNode.SENode;
	}

	@Override
	protected SingleTargetNode getSelectTargetingNode(int i) {
		SweepExecuteNode rootNode = (SweepExecuteNode) getRootAttackNode();
		return (SingleTargetNode) rootNode.getChildAt(i).getChildAt(3);
	}
	@Override
	public String getAbilityName() {
		return "Sweep";
	}
	

	@Override
	public ArrayList<AttackResultAdapter> getAffectedTargetResults()
	{
		ArrayList<AttackResultAdapter> results = new ArrayList<AttackResultAdapter>();
		DefaultAttackTreeNode rootNode = getRootAttackNode();
		BattleEvent event = ((SweepBattleEvent)battleEvent);
		for(int i=0; i <= rootNode.getChildCount();i++)
		{
			DefaultAttackTreeNode child = (DefaultAttackTreeNode) rootNode.getChildAt(i);
			if(child instanceof SweepActivateAbilityNode)
			{
				BattleEvent levent = ((SweepBattleEvent)battleEvent).getLinkedBattleEvent(i);
				buildAffectedTargetResultFromTargetAndBattleEvent(levent, results, i);
			}
		}
		return results;
	}

}
