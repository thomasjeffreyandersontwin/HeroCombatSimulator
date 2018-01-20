package VirtualDesktop.Attack.AreaEffect;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import VirtualDesktop.Attack.AttackAdapter;
import VirtualDesktop.Attack.AttackResultAdapter;
import VirtualDesktop.Attack.AttackTarget;
import VirtualDesktop.Attack.BasicTargetAdapter;
import VirtualDesktop.Attack.PhysicalObjectAdapter;
import VirtualDesktop.Character.CharacterAdaptor;
import VirtualDesktop.MultiAttack.MultiAttackAdapter;
import champions.BattleEvent;
import champions.Target;
import champions.attackTree.AEAffectedTargetsNode;
import champions.attackTree.AECenterTargetNode;
import champions.attackTree.AreaEffectAttackNode;
import champions.attackTree.AttackTreeModel;
import champions.attackTree.AttackTreePanel;
import champions.attackTree.KnockbackEffectNode;
import champions.attackTree.KnockbackNode;
import champions.attackTree.SingleTargetNode;
import champions.exception.BattleEventException;

public class AreaEffectAttackAdapter extends MultiAttackAdapter{

	public AreaEffectAttackAdapter(String name, CharacterAdaptor character) {
		super(name, character);
	}

	public void targetCenter(BasicTargetAdapter center) {
		boolean success = false;
		AECenterTargetNode aeNode=null;
		while( success==false){
			ClassCastException cee=null;
			try {
				aeNode = (AECenterTargetNode) AEAffectedTargetsNode.Node;
				success = true;
			}
			catch(ClassCastException ce) {
				if(AttackTreePanel.Panel.isShowing()==true) {
					AttackTreePanel.Panel.cancelAttack();
					this.Activate();
				}
			}
		}
		aeNode.setBattleEvent(battleEvent);
		aeNode.activateNode(true);	
	    
		Target target= center.target;
		aeNode.setTarget(center.target);
       
		AttackTreePanel.Panel.advanceNode();	
	}
	
	@Override
	public void targetDefender(BasicTargetAdapter defender)
	{
		SingleTargetNode node = SingleTargetNode.Node;
		if(node.getTarget()!=null) 
		{
			AEAffectedTargetsNode aeNode = (AEAffectedTargetsNode) AEAffectedTargetsNode.Node;
			aeNode.buildNextChild(null);
		}
		attackTarget.SingleTargetNode =null;
		super.targetDefender(defender);
		
	}
	
	@Override
	public void Process() {
		//hack to get collisions on last node with knock back working
		if(!getIndividualAttackTarget(getCountOfIndividualAttackTarget()-1).getDefender().getName().equals("Hex"))
		{
			targetDefender(new PhysicalObjectAdapter("Hex"));
		}
		AttackTreePanel.Panel.advanceNode();
	}
	
	public AttackTarget getIndividualAttackTarget(int i)
	{
		AEAffectedTargetsNode aeNode = (AEAffectedTargetsNode) AEAffectedTargetsNode.AENode;
		Target t = ((SingleTargetNode) aeNode.getChildAt(i)).getTarget();
		CharacterAdaptor defender =  new CharacterAdaptor(t);
		int tindex = getActivationInfo().getTargetIndex(t);
		AttackTarget at = new AttackTarget(defender,tindex, battleEvent);
		return at;
	}
	
	public void changeAttackTargetAt(int i, BasicTargetAdapter ta)
	{
		AEAffectedTargetsNode aeNode = (AEAffectedTargetsNode) AEAffectedTargetsNode.AENode;
		AttackTarget at = new AttackTarget(i, battleEvent);
		at.SingleTargetNode =(SingleTargetNode) aeNode.getChildAt(i);
		
		at.targetDefenderNoActivate(ta);
		
		

	}
	
	public void removeAttackTarget(int i) 
	{
		AEAffectedTargetsNode aeNode = (AEAffectedTargetsNode) AEAffectedTargetsNode.Node;
		aeNode.removeChild((SingleTargetNode) aeNode.getChildAt(i));
		aeNode.activateNode(false);
	}

	public int getCountOfIndividualAttackTarget() {
		AEAffectedTargetsNode aeNode = (AEAffectedTargetsNode) AEAffectedTargetsNode.AENode;
		int c;
		for( c=0;c < aeNode.getChildCount();c++)
		{ 
			SingleTargetNode n = (SingleTargetNode) aeNode.getChildAt(c);
			if(n.getTarget()==null) {
				return c;
			}
		}
		return c;
	}

	
	public AreaEffectAttackResultAdapter completeAttack() {
		//hack to get collisions on last node with knock back working
		if(!getIndividualAttackTarget(getCountOfIndividualAttackTarget()-1).getDefender().getName().equals("Hex"))
		{
			targetDefender(new PhysicalObjectAdapter("Hex"));
		}
		super.completeAttack();
		
		Result = new AreaEffectAttackResultAdapter(battleEvent);
		return (AreaEffectAttackResultAdapter)Result;
	}
	
	public JSONObject processJSON(JSONObject attackJSON)
	{
		super.Activate();
		processAttackerJSON(attackJSON);
		BasicTargetAdapter aoeCenter = new PhysicalObjectAdapter((String)attackJSON.get("AOE Center"));
		if(aoeCenter.target==null) {
			aoeCenter = new CharacterAdaptor((String)attackJSON.get("AOE Center"));
		}
		try {Thread.sleep(500);}catch(Exception e) {}
		targetCenter(aoeCenter);
		
		JSONArray attackTargetsJSON = (JSONArray) attackJSON.get("Attack Targets");
		for(int i=0;i< attackTargetsJSON.size();i++)
		{
			JSONObject attackTargetJSON = (JSONObject) attackTargetsJSON.get(i);
			AttackTarget attackTarget = new AttackTarget(battleEvent);
			attackTarget.processDefenderObstructionsAndModifiersInJSON(attackTargetJSON);
		}
		Process();
		for(int i=0;i< attackTargetsJSON.size();i++)
		{
			JSONObject attackTargetJSON = (JSONObject) attackTargetsJSON.get(i);
			AttackTarget attackTarget = getIndividualAttackTarget(i);
			attackTarget.processPotentialCollisionsInJSON(attackTargetJSON);
		}
		Result = completeAttack();
		return Result.exportToJSON();
	}


}
