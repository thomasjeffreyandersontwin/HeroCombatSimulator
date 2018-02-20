package VirtualDesktop.Attack.Sweep;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import VirtualDesktop.Attack.AttackAdapter;
import VirtualDesktop.Attack.AttackResultAdapter;
import VirtualDesktop.Attack.MultiAttack.MultiAttackAdapter;
import VirtualDesktop.Attack.MultiAttack.MultiAttackResultAdapter;
import VirtualDesktop.Character.CharacterAdaptor;
import champions.BattleEvent;
import champions.SweepBattleEvent;
import champions.Target;
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
	
	
	@Override 
	public JSONObject exportToJSON()
	{
		JSONObject result = super.exportToJSON();
		
		HashMap<String, JSONObject> knockbackresults = new HashMap<String, JSONObject>();
		JSONArray attacks = (JSONArray) result.get("Affected Targets");
		for(int i=0;i< attacks.size();i++)
		{
			JSONObject attack = (JSONObject) attacks.get(i);
			JSONObject defender = (JSONObject) attack.get("Defender");
			String name = (String) defender.get("Name");
			boolean hit = (boolean)attack.get("Hit");
			if(hit==true)
			{
				if(knockbackresults.containsKey(name))
				{ 
					int lastKnockbackDistance = (int) ((JSONObject)knockbackresults.get(name).get("Knockback Result")).get("Distance");
					int thisKnockbackDistance = (int) ((JSONObject)attack.get("Knockback Result")).get("Distance");
					if( lastKnockbackDistance > thisKnockbackDistance)
					{
						attack.remove("Knockback Result");
					}
					else {
						((JSONObject)knockbackresults.get(name)).remove("Knockback Result");
						knockbackresults.put(name, attack);
						
					}
				}else {
					knockbackresults.put(name, attack);
				}
			} 	   
		}
		return result;
	}
}
