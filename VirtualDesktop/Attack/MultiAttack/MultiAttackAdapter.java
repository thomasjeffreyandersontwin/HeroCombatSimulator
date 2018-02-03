package VirtualDesktop.Attack.MultiAttack;
import javax.swing.tree.TreeNode;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import VirtualDesktop.Ability.AbilityAdapter;
import VirtualDesktop.Attack.AttackAdapter;
import VirtualDesktop.Attack.AttackResultAdapter;
import VirtualDesktop.Attack.BasicTargetAdapter;

import VirtualDesktop.Attack.PhysicalObjectAdapter;
import VirtualDesktop.Attack.AreaEffect.AreaEffectAttackResultAdapter;
import VirtualDesktop.Character.CharacterAdaptor;
import champions.BattleEvent;
import champions.SweepBattleEvent;
import champions.Target;
import champions.attackTree.AEAffectedTargetsNode;
import champions.attackTree.DefaultAttackTreeNode;
import champions.attackTree.SingleTargetNode;
import champions.exception.BattleEventException;

public abstract class MultiAttackAdapter extends AttackAdapter{

	public MultiAttackAdapter(String name, CharacterAdaptor character) {
		super(name, character);
		// TODO Auto-generated constructor stub
	}
	
	public int getCountOfIndividualAttackTarget() {
		DefaultAttackTreeNode rootNode = getRootAttackNode();
		
		int c;
		for( c=0;c < rootNode.getChildCount();c++)
		{ 
			SingleTargetNode n = getSelectTargetingNode(c);
			if(n==null || n.getTarget()==null) {
				return c;
			}
		}
		return c;
	}
	public AttackAdapter getIndividualAttack(int i)
	{
		BattleEvent event = battleEvent;
		return getIndividualAttackTargetForBattleEvent(i, event);
	}	
	protected AttackAdapter getIndividualAttackTargetForBattleEvent(int i, BattleEvent event) {
		Target t = getSelectTargetingNode(i).getTarget();
		AttackAdapter at=null;
		if(t!=null) 
		{	
			CharacterAdaptor defender =  new CharacterAdaptor(t);
			int tindex = event.getActivationInfo().getTargetIndex(t);
		
			//jeff think about passing in the i index to constructor as targetIndex doesnt always match up
			at = new AttackAdapter(defender,tindex, event, this);
		}
		else
			at = new AttackAdapter(null,0, event, this);
		return at;
	}	
	public void changeAttackTargetAt(int i, BasicTargetAdapter ta)
	{
		changeAttackTargetInBattleEventAt(i, ta, battleEvent);
	}
	protected void changeAttackTargetInBattleEventAt(int i, BasicTargetAdapter ta, BattleEvent event) {
		AttackAdapter at = new AttackAdapter(null,i, event,this);
		at.SingleTargetNode =getSelectTargetingNode(i);
		at.targetDefender(ta);
	}
	public void removeAttackTarget(int i) 
	{
		DefaultAttackTreeNode rootNode =  getRootAttackNode();
		rootNode.removeChild(getSelectTargetingNode(i));
		try {
			rootNode.activateNode(false);
		} catch (BattleEventException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void targetDefender(BasicTargetAdapter defender)
	{
		SingleTargetNode node = SingleTargetNode.Node;
		if(node.getTarget()!=null) 
		{
			DefaultAttackTreeNode rootNode =getRootAttackNode();
			rootNode.buildNextChild(null);
		}
		SingleTargetNode =null;
		super.targetDefender(defender);
		
	}
	public MultiAttackResultAdapter completeAttack() {
		
		completeMultiAttack();
		return (MultiAttackResultAdapter) Result;
	}
	public abstract void Process();
	public MultiAttackResultAdapter completeMultiAttack()
	{
		super.completeAttack();
		
		Result = BuildNewMultiAttackResult(battleEvent);
		return  (MultiAttackResultAdapter) Result;
	}
	protected abstract MultiAttackResultAdapter BuildNewMultiAttackResult(BattleEvent battleEvent) ;

	public JSONObject processJSON(JSONObject attackJSON)
	{
		super.Activate();
		setPushedAmount(attackJSON);
		preProcessJSON(attackJSON);
		
		JSONArray attackTargetsJSON = (JSONArray) attackJSON.get("Attack Targets");
		for(int i=0;i< attackTargetsJSON.size();i++)
		{
			JSONObject attackTargetJSON = (JSONObject) attackTargetsJSON.get(i);	
			String defenderName = (String)attackTargetJSON.get("Defender");
			CharacterAdaptor defender = new CharacterAdaptor(defenderName);
			targetDefender(defender);
			processObstructionsInJSON(attackTargetJSON);
			processToHitModifiersInJSON(attackTargetJSON);
		}
		Process();
		for(int i=0;i< attackTargetsJSON.size();i++)
		{
			JSONObject attackTargetJSON = (JSONObject) attackTargetsJSON.get(i);
			AttackAdapter attackTarget = getIndividualAttack(i);
			attackTarget.processPotentialCollisionsInJSON(attackTargetJSON);
		}
		Result = completeAttack();
		return Result.exportToJSON();
	}
	protected abstract void preProcessJSON(JSONObject attackJSON) ;

	protected abstract DefaultAttackTreeNode getRootAttackNode();
	protected abstract SingleTargetNode getSelectTargetingNode(int i);
	public abstract DefaultAttackTreeNode getSelectTargetingNodeForTarget(Target target);
	public TreeNode activateSubNodeOfSpecificTarget(Class nodeClass, Target target) 
	{
		DefaultAttackTreeNode node=null;
		DefaultAttackTreeNode rootNode=null;
		try
		{
			rootNode= getSelectTargetingNodeForTarget(target);
			if(rootNode!=null) {
				for (int i=0; i< rootNode.getChildCount();i++)
				{
					if(  rootNode.getChildAt(i).getClass() == nodeClass)
					{
						node = (DefaultAttackTreeNode) rootNode.getChildAt(i);
						node.activateNode(true);
						return node;
					}
					
				}
			}
			node= (DefaultAttackTreeNode) nodeClass.getField("Node").get(null);
			node.activateNode(true);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return node;
	}

}
