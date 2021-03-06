package VirtualDesktop.Attack.Sweep;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.sun.org.apache.xpath.internal.FoundIndex;

import VirtualDesktop.Ability.AbilityAdapter;
import VirtualDesktop.Attack.AttackAdapter;
import VirtualDesktop.Attack.BasicTargetAdapter;
import VirtualDesktop.Attack.Autofire.AutofireAttackResultAdapter;
import VirtualDesktop.Attack.MultiAttack.MultiAttackAdapter;
import VirtualDesktop.Attack.MultiAttack.MultiAttackResultAdapter;
import VirtualDesktop.Character.CharacterAdaptor;
import champions.Ability;
import champions.BattleEvent;
import champions.SweepBattleEvent;
import champions.Target;
import champions.attackTree.AttackTreeModel;
import champions.attackTree.AttackTreePanel;
import champions.attackTree.DefaultAttackTreeNode;
import champions.attackTree.KnockbackTargetNode;
import champions.attackTree.ProcessActivateRootNode;
import champions.attackTree.SelectTargetPanel;
import champions.attackTree.SingleTargetNode;
import champions.attackTree.SummaryNode;
import champions.attackTree.SweepActivateAbilityNode;
import champions.attackTree.SweepActivateRootNode;
import champions.attackTree.SweepSetupNode;
import champions.attackTree.sweepTree.SWTAbilityNode;
import champions.exception.BattleEventException;

public class SweepAttackAdapter extends MultiAttackAdapter {
	
	private ArrayList<Integer> targetIndexes = new ArrayList<Integer>();
	public SweepAttackAdapter( CharacterAdaptor character) {
		super("Sweep", character);
	}
	
	public SweepAttackAdapter( CharacterAdaptor character, String ability) {
		super(ability, character);
	}
	
	@Override
	public void Process() {
	}
	public MultiAttackResultAdapter completeAttack() 
	{
		return super.completeAttack();
	}
	public JSONObject processJSON(JSONObject attackJSON)
	{
		Activate();
		BattleEvent event= battleEvent;
		preProcessJSON(attackJSON);
		JSONArray attackTargetsJSON = (JSONArray) attackJSON.get("Attack Targets");	
		
		
		processAllAttacksInSweep(attackJSON, attackTargetsJSON);
		
		Process();
		battleEvent = event;
		
		ArrayList<AttackAdapter> attacks = iterateThroughIndividualAttacksAndEnableLongestKnockbackOnly(attackTargetsJSON);
		
		processPotentialCollisionsForAllAttacksInSweep(attackTargetsJSON, attacks);
		
		Result = buildAndSaveAttackResult();
		JSONObject resultjson =  Result.exportToJSON();
		passTokenFromRequestToResponseForEachIndividualAttack(attackTargetsJSON, resultjson);
		resultjson.put("Token", attackJSON.get("Token"));
		
		
		return resultjson;
		
	}

	private void passTokenFromRequestToResponseForEachIndividualAttack(JSONArray attackTargetsJSON,
			JSONObject resultjson) {
		JSONArray responsesJson = (JSONArray)resultjson.get("Affected Targets");
		for(int i = 0; i < responsesJson.size();i++) 
		{
			JSONObject attackJson = (JSONObject) attackTargetsJSON.get(i);
			String token = (String) attackJson.get("Token");
			JSONObject responseJson = (JSONObject) responsesJson.get(i);
			responseJson.put("Token", token);
		}
	}

	private void processPotentialCollisionsForAllAttacksInSweep(JSONArray attackTargetsJSON, ArrayList<AttackAdapter> attacks) {
		for(int i=0;i< attackTargetsJSON.size();i++)
		{
			JSONObject attackTargetJSON = (JSONObject) attackTargetsJSON.get(i);
		    
			AttackAdapter attack = attacks.get(i);;
			if(attack.getKnockbackDisabled()==false)
			{
				attack.processPotentialCollisionsInJSON(attackTargetJSON);
			}
			else
			{
				attack.disableKnockback();
			}
			
		}
	}

	private ArrayList<AttackAdapter> iterateThroughIndividualAttacksAndEnableLongestKnockbackOnly(
			JSONArray attackTargetsJSON) {
		ArrayList<AttackAdapter> attacks = new ArrayList<AttackAdapter>();
		HashMap<Target, AttackAdapter> knockbackresults = new HashMap<Target, AttackAdapter>(); 
		for(int i=0;i< attackTargetsJSON.size();i++)
		{
			AttackAdapter attack = getIndividualAttack(i);
			CharacterAdaptor c = attack.getDefender();
			if(attack.getHit()==true)
			{
				if(knockbackresults.containsKey(c.target))
				{ 
					int lastKnockbackDistance = knockbackresults.get(c.target).getKnockbackDistance();
					int thisKnockbackDistance = attack.getKnockbackDistance();
					if( lastKnockbackDistance > thisKnockbackDistance)
					{
						attack.disableKnockback();
					}
					else {
						knockbackresults.get(c.target).disableKnockback();
						knockbackresults.put(c.target, attack);
					}
				}else {
					knockbackresults.put(c.target, attack);
				}
			}
			 attacks.add(attack);  	   
		}
		return attacks;
	}

	private void processAllAttacksInSweep(JSONObject attackJSON, JSONArray attackTargetsJSON) {
		for(int i=0;i< attackTargetsJSON.size();i++)
		{
			JSONObject attackTargetJSON = (JSONObject) attackTargetsJSON.get(i);
			
			setPushedAmount(attackJSON);
			targetDefender(new CharacterAdaptor((String)attackTargetJSON.get("Defender")));
			String attackName = (String) attackTargetJSON.get("Ability");
			AttackAdapter adapter = getIndividualAttack(i);
			adapter.processObstructionsInJSON(attackTargetJSON);
			adapter.processToHitModifiersInJSON(attackTargetJSON);
		}
	}
	
	
	@Override
	protected void preProcessJSON(JSONObject attackJSON) {
	
		JSONArray attacks = (JSONArray) attackJSON.get("Attack Targets");
		for(int i=0;i < attacks.size();i++)
		{
			JSONObject attackJson= (JSONObject) attacks.get(i);
			String attackname = (String) attackJson.get("Ability");
			AttackAdapter attack = (AttackAdapter) AbilityAdapter.CreateAbility(attackname, Character);
			addAttackAbility(attack);		
		}
		finishAddingAttacks();

	}

	@Override
	protected DefaultAttackTreeNode getRootAttackNode() {
		return SweepActivateRootNode.SARNode;
	}
	@Override
	protected SingleTargetNode getSelectTargetingNode(int i) {
		SweepActivateRootNode rootNode = (SweepActivateRootNode) getRootAttackNode();
		SingleTargetNode node = (SingleTargetNode) rootNode.getChildAt(i+1).getChildAt(3);
		return node;
	}
	
	public int indexSweepNumber=0;
	public DefaultAttackTreeNode getSelectTargetingNodeForTarget(Target target) {
		if( SweepActivateRootNode.SARNode!=null) {
			for(int i=0; i < SweepActivateRootNode.SARNode.getChildCount();i++) {
				SingleTargetNode node = (SingleTargetNode) SweepActivateRootNode.SARNode.getChildAt(i+1).getChildAt(3);
				if(node.getTarget() == target && i== indexSweepNumber) {
					return  (DefaultAttackTreeNode) node;
				}
			}
		}
		return null;
	}
	@Override
	protected KnockbackTargetNode getKnockbackNodeForTarget() {
		SweepActivateRootNode rootNode = (SweepActivateRootNode) getRootAttackNode();
		ProcessActivateRootNode pNode = ProcessActivateRootNode.PNode;
		for(int i = 0; i < pNode.getChildCount();i++)
		{
			DefaultAttackTreeNode node = (DefaultAttackTreeNode) pNode.getChildAt(i);
			if(node instanceof KnockbackTargetNode) {
				KnockbackTargetNode knode = (KnockbackTargetNode)node;
				Target t = knode.getTarget();
				if(getTarget() == t && i== currentIndexInParent) {
					return knode;
				}
			}
			
		}
		return null;
	}
	
	@Override
	protected MultiAttackResultAdapter BuildNewMultiAttackResult(BattleEvent battleEvent) {
		return  new SweepAttackResultAdapter(battleEvent, -1);
	}
	
	@Override
	public void targetDefender(BasicTargetAdapter defender)
	{
		SingleTargetNode snode = getSelectTargetingNode(targetIndexes.size());
	    AttackTreeModel.treeModel.advanceAndActivate(snode, snode);
	    SelectTargetPanel.ad.SelectTarget(defender.target, false);
        targetIndex = snode.battleEvent.getActivationInfo().getTargetIndex(defender.target);
        targetIndexes.add(targetIndex);
	}
	
	@Override
	public AttackAdapter getIndividualAttack(int i)
	{
		BattleEvent event =  ((SweepBattleEvent)battleEvent).getLinkedBattleEvent(i);
		AttackAdapter a =   getIndividualAttackTargetForBattleEvent(i, event);
		
		return a;		
	}
	@Override
	public void changeAttackTargetAt(int i, BasicTargetAdapter ta)
	{
		BattleEvent event =  ((SweepBattleEvent)battleEvent).getLinkedBattleEvent(i);
		changeAttackTargetInBattleEventAt(i, ta, event);
		int tindex = event.getActivationInfo().getTargetIndex(ta.target);
		targetIndexes.remove(i);
		targetIndexes.add(i,tindex);
	}
	
	public void addAttackAbility(AttackAdapter attack) {
		SweepBattleEvent sbe = (SweepBattleEvent)battleEvent;
		sbe.addLinkedAbility(attack.UnderlyingAbility, false);
		BattleEvent event = sbe.getLinkedBattleEvent(sbe.getLinkedAbilityCount()-1);
		attack.setBattleEvent(event);	
	}
	public void removeAttackAbility(AttackAdapter attack) {
		int pos = ((SweepBattleEvent)battleEvent).getLinkedAbilityIndex(attack.UnderlyingAbility.getName());
		((SweepBattleEvent)battleEvent).removeLinkedAbility(pos);
	}
	public void finishAddingAttacks() {
		try{Thread.sleep(500);}catch (Exception e) {}
		AttackTreePanel.Panel.okayButtonActionPerformed(null);
	}

	
	

}
