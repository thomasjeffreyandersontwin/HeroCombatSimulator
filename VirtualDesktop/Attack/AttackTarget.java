package VirtualDesktop.Attack;

import java.util.ArrayList;

import javax.swing.tree.TreeNode;


import VirtualDesktop.Ability.AbstractBattleClassAdapter;
import VirtualDesktop.Character.CharacterAdaptor;
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
import champions.exception.BattleEventException;

public class AttackTarget extends AbstractBattleClassAdapter{
	private BasicTargetAdapter defender;
	
	public AttackTarget(BasicTargetAdapter ta, int targetIndex, BattleEvent battleEvent) {
		this.battleEvent = battleEvent;
		this.defender = ta;
		this.targetIndex = targetIndex;
	}
	
	
	public AttackTarget(int targetIndex, BattleEvent battleEvent) {
		this.battleEvent = battleEvent;
		this.targetIndex = targetIndex;
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


	private TreeNode activateSubNodeOfTarget(Class nodeClass)
	{
		DefaultAttackTreeNode node=null;
		if(getAEATargetsNode()!=null) {
			for (int i=0; i< getAEATargetsNode().getChildCount();i++)
			{
				if(  getAEATargetsNode().getChildAt(i).getClass() == nodeClass)
					node = (DefaultAttackTreeNode) getAEATargetsNode().getChildAt(i);
			}
			if(node == null)
			{
				try {
					node = (DefaultAttackTreeNode) nodeClass.newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			try {
				node.activateNode(true);
			} catch (BattleEventException e) {
				e.printStackTrace();
			}
		}
		else
		{
			try {
				node= (DefaultAttackTreeNode) nodeClass.getField("Node").get(null);
			} catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		return node;
	}


	private TreeNode getAEATargetsNode() {
		if(AEAffectedTargetsNode.AENode!=null) {
			return AEAffectedTargetsNode.AENode.getChildAt(targetIndex - 1);
		}
		return null;
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
	public void TargetHitLocation(HitLocation location) {
		HitLocationNode node = HitLocationNode.Node;
		node.activateNode(true);
		
		String hitLocation = location.toString();
		HitLocationPanel panel = HitLocationPanel.Panel;
		
		panel.setHitLocation(hitLocation);
		AttackTreePanel.Panel.model.advanceAndActivate(null,null);
	}

	public void placeObjectDirectlyBehindDefender(PhysicalObjectAdapter obj, int distance) {
		KnockbackTargetNode node = getKnockbackNodeForTarget();
		//node = (KnockbackTargetNode) KnockbackTargetNode.Node;
		
		KnockbackEffectNode n = (KnockbackEffectNode) node.getChildAt(1);
		n = KnockbackEffectNode.Node;

		
		int knockbackDistance = getKnockbackDistance();
		
		if(knockbackDistance!=0 && knockbackDistance  > distance) {
			SingleTargetNode snode = (SingleTargetNode) n.getChildAt(n.getChildCount()-1);
			AttackTreeModel.treeModel.activateNode(snode, snode);
//			snode.activateNode(true);
			SelectTargetPanel panel = snode.stp;
			panel.selectTarget(obj.target);
			
			
			
		}
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}
			
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


	

}
