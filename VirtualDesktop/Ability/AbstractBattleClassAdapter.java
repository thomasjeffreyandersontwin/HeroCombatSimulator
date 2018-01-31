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
import champions.attackTree.SweepActivateRootNode;
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
}
