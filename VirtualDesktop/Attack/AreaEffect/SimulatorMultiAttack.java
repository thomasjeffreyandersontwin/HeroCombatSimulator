package VirtualDesktop.Attack.AreaEffect;

import java.util.HashMap;
import java.util.Map;

import VirtualDesktop.CombatSimulatorCharacter;
import VirtualDesktop.SingleAttack.SimulatorSingleAttack;
import champions.attackTree.AttackTreeModel;
import champions.attackTree.KnockbackEffectNode;
import champions.attackTree.KnockbackNode;
import champions.attackTree.SingleTargetNode;

public class SimulatorMultiAttack extends SimulatorSingleAttack {

	public SimulatorMultiAttack(String name, CombatSimulatorCharacter character) {
		super(name, character);
		// TODO Auto-generated constructor stub
	}
	
	private KnockbackNode knockBackNode = null;
	public void EnterKnockbackForSpecificTarget(String knockbackTargetName, int i) {
		AttackTreeModel model = AttackTreeModel.treeModel;
		if(knockBackNode==null) {
			knockBackNode= (KnockbackNode) KnockbackNode.Node;
		}
		SingleTargetNode knockBackTargetNode = (SingleTargetNode) knockBackNode.getChildAt(i).getChildAt(1).getChildAt(0);
		KnockbackEffectNode effectNode = (KnockbackEffectNode) knockBackNode.getChildAt(i).getChildAt(1);
		model.advanceAndActivate(effectNode , knockBackTargetNode);		
		SetTargetByName(knockbackTargetName);
	}
	
	
	

	private Map<String, CombatSimulatorCharacter> Targets = new HashMap<String, CombatSimulatorCharacter>();
	public void AddTargetByName(String targetName) {
		Targets.put(targetName, SetTargetByName(targetName));
	}
	
}
