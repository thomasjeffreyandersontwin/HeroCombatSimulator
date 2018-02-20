package VirtualDesktop.Attack;

import java.io.File;
import java.util.ArrayList;

import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.sun.corba.se.spi.oa.ObjectAdapter;

import VirtualDesktop.Ability.AbilityAdapter;
import VirtualDesktop.Attack.AttackAdapter.CombatRole;
import VirtualDesktop.Attack.MultiAttack.MultiAttackAdapter;
import VirtualDesktop.Attack.Sweep.SweepAttackAdapter;
import VirtualDesktop.Character.CharacterAdaptor;
import VirtualDesktop.Character.CharacterSensesAdapter;
import VirtualDesktop.Character.SenseAdapter;
import champions.ActivationInfo;
import champions.ActiveTargetPanel;
import champions.Battle;
import champions.BattleEngine;
import champions.BattleEvent;
import champions.CVList;
import champions.DefenseList;
import champions.Dice;
import champions.ObstructionList;
import champions.SelectedTargetModel;
import champions.Target;
import champions.TargetList;
import champions.attackTree.AttackParametersNode;
import champions.attackTree.AttackParametersPanel;
import champions.attackTree.AttackTreeModel;
import champions.attackTree.AttackTreeNode;
import champions.attackTree.AttackTreePanel;
import champions.attackTree.AttackTreeSelectionModel;
import champions.attackTree.DefaultAttackTreeNode;
import champions.attackTree.HitLocationNode;
import champions.attackTree.HitLocationPanel;
import champions.attackTree.KnockbackEffectNode;
import champions.attackTree.KnockbackTargetNode;
import champions.attackTree.ObstructionNode;
import champions.attackTree.ProcessActivateRootNode;
import champions.attackTree.SelectTargetPanel;
import champions.attackTree.SingleTargetNode;
import champions.attackTree.SummaryNode;
import champions.attackTree.SweepActivateRootNode;
import champions.attackTree.ToHitNode;
import champions.exception.BadDiceException;
import champions.exception.BattleEventException;
import champions.interfaces.IndexIterator;
import javafx.scene.control.SelectionModel;
import champions.attackTree.KnockbackEffectPanel;
public class AttackAdapter extends AbilityAdapter {
	
	public MultiAttackAdapter parentAttack;
	protected AttackResultAdapter Result;
	public AttackAdapter(String name, CharacterAdaptor character) {
		super(name, character);
		
		battleEvent = UnderlyingAbility.getActivateAbilityBattleEvent(UnderlyingAbility, null, character.target);
	
	}
	
	
	public AttackAdapter(CharacterAdaptor defender, int tindex, BattleEvent event, MultiAttackAdapter parent) {
		super(defender,tindex, event);
		parentAttack =parent;
		
	}

	public void setBattleEvent(BattleEvent be) {
		//attackTarget = new AttackTarget(targetIndex, be);
		//BattleEngine.setupAttackParameters(battleEvent);
		//attackTarget.battleEvent = be;
	}

	
	public AttackResultAdapter activateAbility() {
		AttackTreeModel.treeModel.battleEvent = this.battleEvent;
        battle.addEvent( battleEvent );
        while(AttackTreePanel.Panel.isShowing()==false)
        {
        	try {Thread.sleep(150);}catch(Exception e) {}
        }
        DefaultAttackTreeNode node = DefaultAttackTreeNode.Node;
        node.setBattleEvent(battleEvent);
        
        
        if(getActivationInfo()!=null && getDefender()!=null) {
        	AttackResultAdapter result = new AttackResultAdapter(battleEvent, targetIndex);
        	Result = result;
        	return result;
        }
        return null;
	}
	
	public void pushWithStr(int push) {
			
			/*battleEvent.add("Normal.STR", new Integer(battleEvent.getSource().getCurrentStat("STR")), true);
			battleEvent.add( "Pushed.STR",push,true );
			battleEvent.add("Bonus.PUSHED_USED", push);
			battleEvent.add("Pushed.STR_USED", push);
			battleEvent.add("Bonus.PUSHED.STR", push);
			battleEvent.add("Bonus.PUSHED.STR",push);
			*/
			
			AttackParametersNode aNode = AttackParametersNode.Node;
			aNode.activateNode(true);	
			
			AttackParametersPanel.ad.pushedStrPAD.setValue(push);
			AttackTreePanel.Panel.model.advanceAndActivate(aNode,null);
		
		}
	public AttackResultAdapter completeAttack() {	
		
		AttackResultAdapter result=null;
		if(AttackTreePanel.Panel.isShowing()) {
			if(AttackTreePanel.Panel.model.battleEvent==null)
			{
				AttackTreePanel.Panel.model.battleEvent = battleEvent;
			}
			AttackTreePanel.Panel.advanceNode();
			
			result = new AttackResultAdapter(battleEvent, targetIndex);
			Result = result;
			try{
				
				Thread.sleep(700);
				AttackTreePanel.Panel.advanceNode();
				while(AttackTreePanel.Panel.isShowing()) {
					Thread.sleep(50);
					break;
				}
			}
			catch (Exception e) {e.printStackTrace();}
		}
	    	return result;
	}
	public void CancelAttack() {
		AttackTreePanel attackPanel = AttackTreePanel.defaultAttackTreePanel;
		attackPanel.cancelAttack();
		//targetNum=0;
		
	}
	
	protected BasicTargetAdapter getTargetByNameFromCurrentBattle(String defenderName) {
		BasicTargetAdapter defender = new CharacterAdaptor(defenderName);
		if(defender.target==null)
		{
			defender = new PhysicalObjectAdapter(defenderName);
		}
		return defender;
	}
	
	public void ForceHit() {
		enterToHitModifiers();
		battleEvent.getActivationInfo().addIndexed(targetIndex, "Target", "HITMODE","FORCEHIT",true);
		AttackTreePanel.Panel.model.advanceAndActivate(ToHitNode.Node,ToHitNode.Node);
		
	}
	
	public ToHitModifiers enterToHitModifiers() {
		ToHitModifiers = new ToHitModifiers(battleEvent, targetIndex, this);
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
		//if(ToHitModifiers==null) {
			enterToHitModifiers();
	//	}	
		return ToHitModifiers;
	}
	
	public int getDefenderDCV() {
		return (int) battleEvent.getAbility().getDamageDie();
		
	}
	
	
	ToHitModifiers ToHitModifiers;

	protected void setPushedAmount(JSONObject attackJSON) {
		int push =0;
		if(attackJSON.get("PushedStr")!=null) {
			if( attackJSON.get("PushedStr") instanceof Long) {
				push = ((Long)attackJSON.get("PushedStr")).intValue();
			}
			else
				push=(int) attackJSON.get("PushedStr") ;
			pushWithStr(push);
		}
		
	}

	public enum CombatRole{ Attacker, Defender}
	public void Process() {
		AttackTreePanel.Panel.advanceNode();
	}
	public AttackResultAdapter getLastResult() {
		// TODO Auto-generated method stub
		return Result;
	}
	
	
	public CharacterSensesAdapter getAttackerSenses() {
		CharacterSensesAdapter senses = new CharacterSensesAdapter(battleEvent,targetIndex,CombatRole.Attacker, this);
		return senses;
	}
	public boolean isAttackActivated() 
	{
		// TODO Auto-generated method stub
		return AttackTreePanel.Panel.isShowing() && battleEvent!=null&&battleEvent.getAbility().getName().equals(this.UnderlyingAbility.getName());
	}
	public int getDamageClass() {
		 return (int) battleEvent.getAbility().getDamageDie();
	}

	public CharacterAdaptor getDefender() {
		ActivationInfo ai = battleEvent.getActivationInfo();
		Target t = ai.getTarget(targetIndex);
		CharacterAdaptor defender = new CharacterAdaptor(t);
		return defender;
	}
	public SingleTargetNode SingleTargetNode;
	public void targetDefender(BasicTargetAdapter defender)
	{		
		targetDefenderNoActivate(defender);
		if(getActivationInfo().getSource()==null)
		{
			getActivationInfo().setSource(Character.target);
		}
		
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
	    if(battleEvent!=null) {
	    	SingleTargetNode.battleEvent = battleEvent;
	    }
	    
	    SingleTargetNode.activateNode(true);
	    SingleTargetNode.setTarget(target);
	}

	ArrayList<PhysicalObjectAdapter> obstructions = new ArrayList<PhysicalObjectAdapter>();
	public void addObstruction(PhysicalObjectAdapter obstruction) {
		ObstructionNode node = (ObstructionNode) activateSubNodeOfTarget(ObstructionNode.class); 
		
		ObstructionList ol = getActivationInfo().getObstructionList(targetIndex);
		if(ol!=null) {
			if(obstruction.target != null){
				ol.addObstruction(obstruction.target);
			}
		}
		if(node!=null) {
			node.activateNode(true);
			AttackTreePanel.Panel.advanceNode();
		}
		
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
	public void targetHitLocation(HitLocation head) {
		HitLocationNode node = (HitLocationNode) activateSubNodeOfTarget(HitLocationNode.class);	
		String hitLocation = head.toString();
		HitLocationPanel panel = HitLocationPanel.Panel;
		if(panel == null)
		{
			panel = new HitLocationPanel();
		}
		panel.setHitLocation(hitLocation);
		AttackTreePanel.Panel.model.advanceAndActivate(null,null);
	}

	public void placeObjectDirectlyBehindDefender(BasicTargetAdapter obj, int distance) {
		KnockbackTargetNode node = getKnockbackNodeForTarget();
		if(node!=null) {
			placeObjectDirectlyBehindDefenderUsingKnockbackNode(obj, distance, node);	
		}
	}
	protected void placeObjectDirectlyBehindDefenderUsingKnockbackNode(BasicTargetAdapter obj, int distance,KnockbackTargetNode node) {
		KnockbackEffectNode n = (KnockbackEffectNode) node.getChildAt(1);
		
		
		if(n==null) {
			n = KnockbackEffectNode.Node;
		}
	
		int knockbackDistance = getKnockbackDistance();
		if(knockbackDistance!=0 && knockbackDistance  > distance) {
			SingleTargetNode snode = (SingleTargetNode) n.getChildAt(n.getChildCount()-1);
			try {Thread.sleep(500);} catch (InterruptedException e) {}
			//dont mess with this mode of selecting knockback target!
			AttackTreeModel.treeModel.activateNode(snode, snode);
			SelectTargetPanel.ad.selectTarget(obj.target);
		}
		
		try{Thread.sleep(500);} catch (InterruptedException e) {}
	}
	protected KnockbackTargetNode getKnockbackNodeForTarget() {
		KnockbackTargetNode knode=null;
		if(AttackTreeModel.treeModel.getRoot() instanceof SweepActivateRootNode)
		{
			//change
			((SweepAttackAdapter)parentAttack).indexSweepNumber = this.currentIndexInParent;
			try
			{
			
				knode = (KnockbackTargetNode) parentAttack.getSelectTargetingNodeForTarget(getTarget()).getParent().getChildAt(5);
			}
			catch(java.lang.ClassCastException ce)
			{
				//we disabled knockback so this failed , return null
				
				return null;
			}
		}
		else
		{
			ProcessActivateRootNode pNode = ProcessActivateRootNode.PNode;
			for(int i = 0; i < pNode.getChildCount();i++)
			{
				DefaultAttackTreeNode node = (DefaultAttackTreeNode) pNode.getChildAt(i);
				if(node instanceof KnockbackTargetNode) {
					knode = (KnockbackTargetNode)node;
					Target t = knode.getTarget();
					if(getTarget() == t && i== currentIndexInParent) {
						break;
					}
				}
				knode=null;
			}
			
			
			
		}
		if(knode!=null) {
			while(knode.getTarget()!=getTarget())
			{
				int order = knode.getParent().getIndex(knode);
				knode = (KnockbackTargetNode) knode.getParent().getChildAt(order+1);
			}
		}
		return knode;
	}
	public int getKnockbackDistance() {
		int kbIndex = getKBIndexOfTarget();
		return battleEvent.getKnockbackDistance(kbIndex);
	}
	protected int getKBIndexOfTarget() {
		return battleEvent.getKnockbackIndex(getTarget(), "KB");
	}
	
	public JSONObject processJSON(JSONObject attackJSON) {
		String token = (String)attackJSON.get("Token");
		super.processJSON(attackJSON);
		setPushedAmount(attackJSON);
		String defName = (String) attackJSON.get("Defender");
		CharacterAdaptor defender = new CharacterAdaptor(defName);
		targetDefender(defender);
		processObstructionsInJSON(attackJSON);
		processToHitModifiersInJSON(attackJSON);
		if(attackJSON.get("Targeting Sense")!=null) {
			SenseAdapter sense = getAttackerSenses().getSense((String) attackJSON.get("Targeting Sense"));
			sense.Activate();
		}
		processPotentialCollisionsInJSON(attackJSON);
		AttackResultAdapter r = new AttackResultAdapter(battleEvent, targetIndex);
		Result = r;
		r.setToken(token);
		return r.exportToJSON();
	}
	
	public void processObstructionsInJSON(JSONObject attackJSON) {
		JSONArray physicalObjects = (JSONArray) attackJSON.get("Obstructions");
		if(physicalObjects!=null)
		{
			for(int i = 0; i < physicalObjects.size();i++) {
				addObstruction(new PhysicalObjectAdapter((String) physicalObjects.get(i)));
			}
		}
		
		
	}



	public void processToHitModifiersInJSON(JSONObject attackJSON) {
		JSONObject modifiersJSON = (JSONObject) attackJSON.get("To Hit Modifiers");
		if(modifiersJSON!=null)
		{
			ToHitModifiers modifiers = enterToHitModifiers();
			modifiers.processJSON(modifiersJSON);
		}
	}
	public void processPotentialCollisionsInJSON(JSONObject attackJSON) {
		
		JSONArray potentialCollisions = (JSONArray) attackJSON.get("Potential Knockback Collisions");
		if(potentialCollisions!=null) {
			for(int i = 0; i < potentialCollisions.size();i++) {
				JSONObject pcoJSON = (JSONObject)potentialCollisions.get(i);
				String co = (String) pcoJSON.get("Collision Object");
				int distance =0;
				try {
					 distance  = ((Long) pcoJSON.get("Collision Distance")).intValue();
				}
				catch (Exception e)
				{
					distance = (int) pcoJSON.get("Collision Distance");
				}
				BasicTargetAdapter ta;
				ta = getTargetByNameFromCurrentBattle(co);
				placeObjectDirectlyBehindDefender(ta, distance);
			}
		}
	}
	
	public TreeNode activateSubNodeOfTarget(Class nodeClass) {
		DefaultAttackTreeNode node=null;
		try
		{
			if(parentAttack!=null) 
			{
				return parentAttack.activateSubNodeOfSpecificTarget(nodeClass, getTarget());
			}
			else {
				node= (DefaultAttackTreeNode) nodeClass.getField("Node").get(null);
				node.activateNode(true);
			}	
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return node;
	}

	public int currentIndexInParent;
	private boolean knockbackDisabled=false;
	public void disableKnockback() {
		//go to parent node first because this panel sometimes shares info
		getKnockbackNodeForTarget().activateNode(true);

		//disable knockback
		KnockbackEffectNode  node =(KnockbackEffectNode) getKnockbackNodeForTarget().getChildAt(1);
		node.activateNode(true);
		KnockbackEffectPanel  panel = KnockbackEffectPanel.panel;
		panel.noEffectButton.setSelected(true);
		panel.noEffectButtonActionPerformed(null);
		
		getKnockbackNodeForTarget().activateNode(true);
		//IMPORTANT process the sction
		AttackTreePanel.Panel.model.advanceAndActivate(null,null);
		
		
		this.knockbackDisabled =true;
		
	}


	public boolean getHit() {
	    return getActivationInfo().getHasHitTargets();
	}


	public boolean getKnockbackDisabled() {
		
		KnockbackTargetNode knode = getKnockbackNodeForTarget();
		if(knode==null)
			return true;
		KnockbackEffectNode  node =(KnockbackEffectNode) getKnockbackNodeForTarget().getChildAt(1);
		if(node==null)
			return true;
		return knockbackDisabled;
	}
}
