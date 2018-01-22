package VirtualDesktop.Attack.AreaEffect;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import VirtualDesktop.Attack.AttackTarget;
import VirtualDesktop.Attack.BasicTargetAdapter;
import VirtualDesktop.Attack.PhysicalObjectAdapter;
import VirtualDesktop.Attack.MultiAttack.MultiAttackAdapter;
import VirtualDesktop.Attack.MultiAttack.MultiAttackResultAdapter;
import VirtualDesktop.Character.CharacterAdaptor;
import champions.BattleEvent;
import champions.Target;
import champions.attackTree.AEAffectedTargetsNode;
import champions.attackTree.AECenterTargetNode;
import champions.attackTree.AttackTreePanel;
import champions.attackTree.DefaultAttackTreeNode;
import champions.attackTree.SingleTargetNode;

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
	public void Process() {
		//hack to get collisions on last node with knock back working
		if(!getIndividualAttackTarget(getCountOfIndividualAttackTarget()-1).getDefender().getName().equals("Hex"))
		{
			targetDefender(new PhysicalObjectAdapter("Hex"));
		}
		AttackTreePanel.Panel.advanceNode();
	}
	protected MultiAttackResultAdapter BuildNewMultiAttackResult(BattleEvent battleEvent) {
		return new AreaEffectAttackResultAdapter(battleEvent);
	}

	public AreaEffectAttackResultAdapter completeAttack() {
		//hack to get collisions on last node with knock back working
		if(!getIndividualAttackTarget(getCountOfIndividualAttackTarget()-1).getDefender().getName().equals("Hex"))
		{
			targetDefender(new PhysicalObjectAdapter("Hex"));
		}
		completeMultiAttack();
		return (AreaEffectAttackResultAdapter) Result;
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

	protected DefaultAttackTreeNode getRootAttackNode()
	{
		return AEAffectedTargetsNode.AENode;
		
	}
	protected SingleTargetNode getSelectTargetingNode(int i) {
		DefaultAttackTreeNode rootNode =  getRootAttackNode();
		return (SingleTargetNode) rootNode.getChildAt(i);

	}

	
	
	}
