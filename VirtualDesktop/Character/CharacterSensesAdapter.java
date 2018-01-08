package VirtualDesktop.Character;

import VirtualDesktop.Ability.AbstractBattleClassAdapter;
import VirtualDesktop.Attack.AttackAdapter;
import VirtualDesktop.Attack.AttackAdapter.CombatRole;
import champions.BattleEvent;
import champions.attackTree.DefaultAttackTreeNode;
import champions.attackTree.SourcePerceptionsNode;
import champions.attackTree.TargetPerceptionsNode;
import champions.attackTree.ToHitNode;
import champions.exception.BattleEventException;

public class CharacterSensesAdapter extends AbstractBattleClassAdapter {
	CombatRole CombatRole;
	public CharacterSensesAdapter(BattleEvent battleEvent, int targetIndex,CombatRole  role) {
		this.battleEvent = battleEvent;
		this.targetIndex = targetIndex;
		DefaultAttackTreeNode node;
		if(role==CombatRole.Defender) {
			node = TargetPerceptionsNode.Node;
			if(node==null)
			{
				node = new TargetPerceptionsNode("target");
			}
		}
		else 
		{
			node = SourcePerceptionsNode.Node;	
			if(node==null)
			{
				node = new SourcePerceptionsNode("source");
			}
		}
		try {
			node.activateNode(true);
		} catch (BattleEventException e) {
			e.printStackTrace();
		}
	}
	public SenseAdapter getSense(String name) {
			
		return new SenseAdapter(battleEvent, targetIndex, name);
	}
}
