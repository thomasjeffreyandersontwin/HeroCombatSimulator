package VirtualDesktop.Attack.MoveThrough;

import VirtualDesktop.CombatSimulatorCharacter;
import VirtualDesktop.Ability.SimulatorMovement;
import VirtualDesktop.Attack.SimulatorSingleAttack;
import champions.attackTree.AttackTreeModel;
import champions.attackTree.DefaultAttackTreeNode;
import champions.attackTree.EffectNode;
import champions.attackTree.MoveByEffectNode;
import champions.attackTree.MoveThroughEffectNode;
import champions.attackTree.MovementManeuverSetupPanel;

public class SimulatorMoveThrough extends SimulatorSingleAttack {
	
	int _distance=0;
	int _attackDamage;
	MovementManeuverSetupPanel _panel  = MovementManeuverSetupPanel.defaultPanel;
	SimulatorMovement _movement=null;
	SimulatorSingleAttack _attack = null;
	Integer _damageDice;
	
	public SimulatorMoveThrough(String name, CombatSimulatorCharacter character) {
		super(name, character);
		// TODO Auto-generated constructor stub
	}
	


	public void setDistance(int distance)
	{
		this._distance= distance;
		_panel.setDistance(distance);
		if(_attack!=null) {
			updateDamageDice();
		}
	}
	
	private void updateDamageDice() {
		UnderlyingAbility = _panel.getBattleEvent().getAbility();
		int distanceMod = new Integer(_distance/3);
		
		Integer damage = ((Double)_attack.UnderlyingAbility.getDamageDie()).intValue();
		if(_attack.UnderlyingAbility.isKillingAttack())
		{
			Integer base  = ((Double)_attack.UnderlyingAbility.getValue("Base.DC")).intValue();
			if(damage+distanceMod > base *2)
			{
				damage = base * 2;
			}
			else {
				damage+=distanceMod;
			}
			damage = damage / 3;
		}
		else {
			damage+=distanceMod;
		}
		UnderlyingAbility.DamageDiceOverride = (int) damage ;
		if(_attack.UnderlyingAbility.isKillingAttack()) {
			UnderlyingAbility.remove("Ability.KTYPE");
			UnderlyingAbility.add("Ability.KTYPE","KILLING");
		}
		else
		{
			UnderlyingAbility.remove("Ability.KTYPE");
			UnderlyingAbility.add("Ability.KTYPE","NORMAL");
		}
		_damageDice = damage;
	}



	public void setMovementAbility(String movementName)
	{
		_movement = new SimulatorMovement(movementName, Character);
		_panel.setMovementAbility(_movement.UnderlyingAbility);

		
	}



	public void setAttack(String attackName) {
		_attack = (SimulatorSingleAttack) SimulatorSingleAttack.CreateAbility(attackName, Character);
		_attackDamage = ((Double)_attack.UnderlyingAbility.getDamageDie()).intValue();
		updateDamageDice();
	}



	public void UpdateAttackWithAdjustedDamageDice() {
		DefaultAttackTreeNode node = null;
		if(Name.equals("Move Through")){
			node =MoveThroughEffectNode.Node;
		}
		else if (Name.equals("MoveBy")){
			node =MoveByEffectNode.Node;
		}
		
		int dindex = node.getBattleEvent().getDiceIndex("DamageDie", node.getTargetGroup());
        node.getBattleEvent().setDiceSize(dindex,_damageDice.toString());
        node.getBattleEvent().getDiceSize(dindex);
        if(_attack.UnderlyingAbility.isKillingAttack()) {
        	node.getBattleEvent().setKillingAttack(true);
        }
        
        
        EffectNode node2 = EffectNode.Node;
		AttackTreeModel.treeModel.advanceAndActivate(node2, node2);
		UnderlyingAbility.DamageDiceOverride = 0;
		
	}
	

}
