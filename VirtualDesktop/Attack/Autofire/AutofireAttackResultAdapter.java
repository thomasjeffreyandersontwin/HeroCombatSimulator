package VirtualDesktop.Attack.Autofire;

import VirtualDesktop.Attack.MultiAttack.MultiAttackAdapter;
import VirtualDesktop.Attack.MultiAttack.MultiAttackResultAdapter;
import VirtualDesktop.Character.CharacterAdaptor;
import champions.BattleEvent;
import champions.attackTree.AFShotNode;
import champions.attackTree.AutofireAttackNode;
import champions.attackTree.AutofireSprayAttackNode;
import champions.attackTree.DefaultAttackTreeNode;
import champions.attackTree.SingleTargetNode;

public class AutofireAttackResultAdapter extends MultiAttackResultAdapter {

	public AutofireAttackResultAdapter(BattleEvent battleEvent) 
	{
		super(battleEvent, -1);
		// TODO Auto-generated constructor stub
	}


	@Override
	protected DefaultAttackTreeNode getRootAttackNode() {
		   return AutofireAttackNode.AFNode;
          
	}
	@Override
	protected SingleTargetNode getSelectTargetingNode(int i) {
		DefaultAttackTreeNode rootNode =  getRootAttackNode();
		 AFShotNode afsn= (AFShotNode) rootNode.getChildAt(i);
         return (SingleTargetNode) afsn.getChildAt(0);
	}

}
