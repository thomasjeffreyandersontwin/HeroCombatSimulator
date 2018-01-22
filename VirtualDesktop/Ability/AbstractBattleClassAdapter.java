package VirtualDesktop.Ability;

import javax.swing.tree.TreeNode;

import champions.ActivationInfo;
import champions.Battle;
import champions.BattleEvent;
import champions.Target;
import champions.attackTree.AEAffectedTargetsNode;
import champions.attackTree.AFShotNode;
import champions.attackTree.AutofireAttackNode;
import champions.attackTree.DefaultAttackTreeNode;
import champions.attackTree.SingleTargetNode;
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
	
	protected DefaultAttackTreeNode getAEATargetsNode() {
		if(AEAffectedTargetsNode.AENode!=null) {
			return (DefaultAttackTreeNode) AEAffectedTargetsNode.AENode.getChildAt(targetIndex - 1);
		}
		return null;
	}
	
	protected DefaultAttackTreeNode getAFTargetsNode() {

		if( AutofireAttackNode.AFNode!=null) {
			return  (DefaultAttackTreeNode) AutofireAttackNode.AFNode.getChildAt(targetIndex).getChildAt(0);
		}
		return null;
	}
	

	//protected SingleTargetNode getSelectTargetingNode(int i) {
		//DefaultAttackTreeNode rootNode =  getRootAttackNode();
		 //AFShotNode afsn= (AFShotNode) rootNode.getChildAt(i);
         //r//eturn (SingleTargetNode) afsn.getChildAt(0);
//	}
	
	protected TreeNode activateSubNodeOfTarget(Class nodeClass) {
		DefaultAttackTreeNode node=null;
		DefaultAttackTreeNode rootNode=null;
		try
		{
			if(getAEATargetsNode()!=null) {
				rootNode = getAEATargetsNode();
			}
			else if(getAFTargetsNode()!=null)
			{
				rootNode = getAFTargetsNode();
			}
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
			if(node == null)
			{
				
				node = (DefaultAttackTreeNode) nodeClass.newInstance();
				node.activateNode(true);
				
			}
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return node;
	}

}
