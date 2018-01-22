package VirtualDesktop.Attack.AreaEffect;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import VirtualDesktop.Attack.AttackResultAdapter;
import VirtualDesktop.Attack.PhysicalObjectAdapter;
import VirtualDesktop.Attack.MultiAttack.MultiAttackResultAdapter;
import champions.BattleEvent;
import champions.DetailList;
import champions.Target;
import champions.attackTree.AEAffectedTargetsNode;
import champions.attackTree.AttackTreePanel;
import champions.attackTree.DefaultAttackTreeNode;
import champions.attackTree.SingleTargetNode;

public class AreaEffectAttackResultAdapter extends MultiAttackResultAdapter{

	public AreaEffectAttackResultAdapter(BattleEvent battleEvent) 
	{
		super(battleEvent, -1);
		// TODO Auto-generated constructor stub
	}

	
	public boolean attackHitCenter() 
	{
		return  battleEvent.getActivationInfo().getTargetHit(0);
		
	}
	
	protected DefaultAttackTreeNode getRootAttackNode()
	{
		return AEAffectedTargetsNode.AENode;
		
	}
	protected SingleTargetNode getSelectTargetingNode(int i) {
		DefaultAttackTreeNode rootNode =  getRootAttackNode();
		return (SingleTargetNode) rootNode.getChildAt(i);

	}
	
	


}
