package VirtualDesktop.Ability;

import javax.swing.tree.TreeNode;

import champions.ActivationInfo;
import champions.Battle;
import champions.BattleEvent;
import champions.Target;
import champions.attackTree.AEAffectedTargetsNode;
import champions.attackTree.DefaultAttackTreeNode;
import champions.exception.BattleEventException;

public abstract class AbstractBattleClassAdapter{
	
	public int targetIndex=0;
	public BattleEvent battleEvent;
	
	public Target getTarget()
	{
		return battleEvent.getActivationInfo().getTarget(targetIndex);
	}
	
	public int getTargetReferenceNumber()
	{
		return battleEvent.getActivationInfo().getTargetReferenceNumber(targetIndex);
	}

	
	public void setTarget(Target target) {
		battleEvent.getActivationInfo().setTarget(targetIndex, target);
		
	}
	
	public ActivationInfo getActivationInfo()
	{
		return battleEvent.getActivationInfo();
	}
	
	protected TreeNode getAEATargetsNode() {
		if(AEAffectedTargetsNode.AENode!=null) {
			return AEAffectedTargetsNode.AENode.getChildAt(targetIndex - 1);
		}
		return null;
	}

	protected TreeNode activateSubNodeOfTarget(Class nodeClass) {
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
				try {
					node.activateNode(true);
				} catch (BattleEventException e) {
					e.printStackTrace();
				}
			} catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		return node;
	}
}
