package VirtualDesktop.Attack;

import java.util.ArrayList;

import javax.swing.tree.TreeNode;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import VirtualDesktop.Ability.AbstractBattleClassAdapter;
import VirtualDesktop.Attack.AttackAdapter.CombatRole;
import VirtualDesktop.Character.CharacterAdaptor;
import VirtualDesktop.Character.CharacterSensesAdapter;
import VirtualDesktop.Character.SenseAdapter;
import champions.ActivationInfo;
import champions.BattleEvent;
import champions.CVList;
import champions.ObstructionList;
import champions.Target;
import champions.attackTree.AEAffectedTargetsNode;
import champions.attackTree.AttackTreeModel;
import champions.attackTree.AttackTreePanel;
import champions.attackTree.DefaultAttackTreeNode;
import champions.attackTree.HitLocationNode;
import champions.attackTree.HitLocationPanel;
import champions.attackTree.KnockbackEffectNode;
import champions.attackTree.KnockbackSecondaryTargetNode;
import champions.attackTree.KnockbackTargetNode;
import champions.attackTree.ObstructionNode;
import champions.attackTree.ProcessActivateRootNode;
import champions.attackTree.SelectTargetPanel;
import champions.attackTree.SingleAttackNode;
import champions.attackTree.SingleTargetNode;
import champions.attackTree.SummaryNode;

public class AttackTarget extends AbstractBattleClassAdapter{
	private BasicTargetAdapter defender;
	
	public AttackTarget(BasicTargetAdapter defender, int targetIndex, BattleEvent battleEvent) {
		this.battleEvent = battleEvent;
		this.defender = defender;
		this.targetIndex = targetIndex;
	}
	
	public AttackTarget(int targetIndex, BattleEvent battleEvent) {
		this.battleEvent = battleEvent;
		this.targetIndex = targetIndex;
	}

	public AttackTarget(BattleEvent battleEvent) {
		this.battleEvent = battleEvent;
	}

	ToHitModifiers ToHitModifiers;
	public ToHitModifiers enterToHitModifiers() {
		ToHitModifiers = new ToHitModifiers(battleEvent, targetIndex);
		return ToHitModifiers;
	}
	public int getToHitRoll() {
		getToHitModifiers();
		CVList cvl = (CVList) battleEvent.getActivationInfo().getIndexedValue(targetIndex, "Target", "CVLIST");
		int OCV=  cvl.getSourceCV();
		int DCV= cvl.getTargetCV();
		return 11 + OCV - DCV;
	}
	public int getAttackerOCV() {
		getToHitModifiers();
		CVList cvl = (CVList) battleEvent.getActivationInfo().getIndexedValue(targetIndex, "Target", "CVLIST");
		return  cvl.getSourceCV();
	}
	public ToHitModifiers getToHitModifiers() {
		if(ToHitModifiers==null) {
			enterToHitModifiers();
		}	
		return ToHitModifiers;
	}
	public CharacterAdaptor getDefender() {
		ActivationInfo ai = battleEvent.getActivationInfo();
		Target t = ai.getTarget(targetIndex);
		CharacterAdaptor defender = new CharacterAdaptor(t);
		return defender;
	}
	public int getDefenderDCV() {
		getToHitModifiers();
		CVList cvl = (CVList) battleEvent.getActivationInfo().getIndexedValue(targetIndex, "Target", "CVLIST");
		return  cvl.getTargetCV();
	}
	ArrayList<PhysicalObjectAdapter> obstructions = new ArrayList<PhysicalObjectAdapter>();
	public SingleTargetNode SingleTargetNode;
	public AttackResultAdapter Result;
	
	public void addObstruction(PhysicalObjectAdapter obstruction) {
		ObstructionNode node = (ObstructionNode) activateSubNodeOfTarget(ObstructionNode.class); 
		
		ObstructionList ol = getActivationInfo().getObstructionList(targetIndex);
		if(ol!=null) {
			if(obstruction.target != null){
				ol.addObstruction(obstruction.target);
			}
		}
		node.activateNode(true);
		AttackTreePanel.Panel.model.advanceAndActivate(node,node);
	}
	public void removeObstruction(PhysicalObjectAdapter obstruction) {
		ObstructionList ol = getActivationInfo().getObstructionList(targetIndex);
		if(ol!=null) {
			if(obstruction.target != null){
				ol.remove(obstruction.getName());
			}
		}
	}
	public PhysicalObjectAdapter getObstruction(int i) {
		ObstructionList ol = getActivationInfo().getObstructionList(targetIndex);
		if(ol!=null) {
			return  new PhysicalObjectAdapter(ol.getObstruction(i));
		}
		return null;
	}
	public int getObstructionCount() {
		ObstructionList ol = getActivationInfo().getObstructionList(targetIndex);
		if(ol!=null) {
			return getActivationInfo().getObstructionList(targetIndex).size();
		}
		return 0;
	}
	
	public enum HitLocation{ HEAD, SHOULDERS, ARMS, HANDS, CHEST, STOMACH, VITALS, THIGHS, LEGS, FEET}
	public void targetHitLocation(HitLocation location) {
		HitLocationNode node = (HitLocationNode) activateSubNodeOfTarget(HitLocationNode.class);	
		String hitLocation = location.toString();
		HitLocationPanel panel = HitLocationPanel.Panel;
		if(panel == null)
		{
			panel = new HitLocationPanel();
		}
		panel.setHitLocation(hitLocation);
		AttackTreePanel.Panel.model.advanceAndActivate(null,null);
	}

	public void placeObjectDirectlyBehindDefender(PhysicalObjectAdapter obj, int distance) {
		KnockbackTargetNode node = getKnockbackNodeForTarget();
		
		KnockbackEffectNode n = (KnockbackEffectNode) node.getChildAt(1);
		if(n==null) 
		{	n = KnockbackEffectNode.Node;
		
		}
	
		int knockbackDistance = getKnockbackDistance();
		
		
		if(knockbackDistance!=0 && knockbackDistance  > distance) {
			SingleTargetNode snode = (SingleTargetNode) n.getChildAt(n.getChildCount()-1);
			//snode.targetGroup = ".KB." + getDefender().getName();
			AttackTreeModel.treeModel.activateNode(snode, snode);

			SelectTargetPanel panel = snode.stp;
			snode.setTarget(obj.target);
			//panel.selectTarget(obj.target);	
			//snode.activateNode(true);
			//AttackTreeModel.treeModel.activateNode(snode, snode);
			AttackTreePanel.Panel.advanceNode();
		}
		
		try{Thread.sleep(500);} catch (InterruptedException e) {}	
	}
	
	private KnockbackTargetNode getKnockbackNodeForTarget() {
		ProcessActivateRootNode pNode = ProcessActivateRootNode.PNode;
		for(int i = 0; i < pNode.getChildCount();i++)
		{
			DefaultAttackTreeNode node = (DefaultAttackTreeNode) pNode.getChildAt(i);
			if(node instanceof KnockbackTargetNode) {
				KnockbackTargetNode knode = (KnockbackTargetNode)node;
				Target t = knode.getTarget();
				if(getTarget() == t) {
					return knode;
				}
			}
			
		}
		return null;
	}

	public int getKnockbackDistance() {
		return battleEvent.getKnockbackDistance(targetIndex);
	}

	public void ForceHit() {
		battleEvent.getActivationInfo().addIndexed(targetIndex, "Target", "HITMODE",true,true);
		
	}
	
	public void targetDefender(BasicTargetAdapter defender)
	{
        /*ActivationInfo ai = battleEvent.getActivationInfo();
        if ( defender != null ) {
        	 ai.removeTarget(defender.UnderlyingCharacter, ".ATTACK");
        }
        ai.addTarget(defender.UnderlyingCharacter, ".ATTACK");
        int _targetIndex = ai.getTargetIndex(defender.UnderlyingCharacter, ".ATTACK");
        
        DefenseList dl = new DefenseList();
        BattleEngine.buildDefenseList(dl,defender.UnderlyingCharacter);
        ai.setDefenseList(_targetIndex, dl);
        
        int tgindex = ai.addTargetGroup(".ATTACK");
        ai.setKnockbackGroup(tgindex, "KB");
        */
		
		targetDefenderNoActivate(defender);
        AttackTreePanel.Panel.advanceNode();
        targetIndex = getActivationInfo().getTargetIndex(defender.target);
	}
	public void targetDefenderNoActivate(BasicTargetAdapter ta) {
		if(SingleTargetNode==null)
			SingleTargetNode=SingleTargetNode.Node;
		
		if(SingleTargetNode==null) {
			SingleTargetNode= new SingleTargetNode("");
		}
		
	    Target target= ta.target;
	    SingleTargetNode.battleEvent = battleEvent;
	    SingleTargetNode.activateNode(false);
	    SingleTargetNode.setTarget(target);
	}

	public JSONObject processJSON(JSONObject attackJSON) {
		processDefenderObstructionsAndModifiersInJSON(attackJSON);
		if(attackJSON.get("Targeting Sense")!=null) {
			SenseAdapter sense = getAttackerSenses().getSense((String) attackJSON.get("Targeting Sense"));
			sense.Activate();
		}
		processPotentialCollisionsInJSON(attackJSON);
		AttackResultAdapter r = new AttackResultAdapter(battleEvent, targetIndex);
		Result = r;
		return r.exportToJSON();
	}
	public CharacterSensesAdapter getAttackerSenses() {
		CharacterSensesAdapter senses = new CharacterSensesAdapter(battleEvent,targetIndex,CombatRole.Attacker);
		return senses;
	}

	public void processPotentialCollisionsInJSON(JSONObject attackJSON) {
		JSONArray potentialCollisions = (JSONArray) attackJSON.get("Potential Knockback Collisions");
		for(int i = 0; i < potentialCollisions.size();i++) {
			JSONObject pcoJSON = (JSONObject)potentialCollisions.get(i);
			String co = (String) pcoJSON.get("Collision Object");
			int distance  = (int) pcoJSON.get("Collision Distance");
			placeObjectDirectlyBehindDefender(new PhysicalObjectAdapter(co), distance);
		}
	}

	public void processDefenderObstructionsAndModifiersInJSON(JSONObject attackJSON) {
		targetDefender(new CharacterAdaptor((String)attackJSON.get("Defender")));
		
		JSONArray physicalObjects = (JSONArray) attackJSON.get("Obstructions");
		for(int i = 0; i < physicalObjects.size();i++) {
			addObstruction(new PhysicalObjectAdapter((String) physicalObjects.get(i)));
		}
		
		JSONObject modifiersJSON = (JSONObject) attackJSON.get("To Hit Modifiers");
		ToHitModifiers modifiers = enterToHitModifiers();
		modifiers.processJSON(modifiersJSON);
	}



	



	

}
