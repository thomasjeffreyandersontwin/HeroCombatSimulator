package VirtualDesktop.Attack.AreaEffect;

import java.util.ArrayList;

import VirtualDesktop.Attack.AttackAdapter;
import VirtualDesktop.Attack.AttackResultAdapter;
import VirtualDesktop.Attack.AttackTarget;
import VirtualDesktop.Attack.BasicTargetAdapter;
import VirtualDesktop.Attack.PhysicalObjectAdapter;
import VirtualDesktop.Character.CharacterAdaptor;
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

public class AreaEffectAttackAdapter extends AttackAdapter{

	public AreaEffectAttackAdapter(String name, CharacterAdaptor character) {
		super(name, character);
	}

	public void targetCenter(BasicTargetAdapter center) {
		AECenterTargetNode aeNode = (AECenterTargetNode) AEAffectedTargetsNode.Node;
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
		
	
	public AttackTarget getAttackTarget(int i)
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

	public int getAttackTargetCount() {
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
		super.completeAttack();
		return new AreaEffectAttackResultAdapter(battleEvent);
	}
	


}
