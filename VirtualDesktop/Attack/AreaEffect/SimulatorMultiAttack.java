package VirtualDesktop.Attack.AreaEffect;

import java.util.HashMap;
import java.util.Map;

import VirtualDesktop.Attack.SingleAttack.SimulatorSingleAttack;
import VirtualDesktop.Character.CharacterAdaptor;
import champions.attackTree.AttackTreeModel;
import champions.attackTree.KnockbackEffectNode;
import champions.attackTree.KnockbackNode;
import champions.attackTree.SingleTargetNode;

public class SimulatorMultiAttack extends SimulatorSingleAttack {

	public SimulatorMultiAttack(String name, CharacterAdaptor character) {
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
	
	
	

	private Map<String, CharacterAdaptor> Targets = new HashMap<String, CharacterAdaptor>();
	public void AddTargetByName(String targetName) {
		Targets.put(targetName, SetTargetByName(targetName));
	}
	
}
