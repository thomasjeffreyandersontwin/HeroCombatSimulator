package VirtualDesktop;

import VirtualDesktop.Ability.SimulatorAbility;
import VirtualDesktop.Ability.SimulatorMovement;
import champions.Ability;
import champions.Battle;
import champions.BattleEvent;
import champions.Target;
import champions.TargetList;

public class CombatSimulatorCharacter {
	public Target UnderlyingCharacter;
	public SimulatorAbility ActiveAbility;
	public String ActiveAbilityName;
	public String ActivateAbilityName;
	public CombatSimulatorCharacter() {}
	public CombatSimulatorCharacter(Target character) {
		this.UnderlyingCharacter = character;
	}
	
	public CombatSimulatorCharacter(String characterName) {
		this();
		Battle battle = Battle.currentBattle;
		TargetList targets = battle.getTargetList(true);
		champions.Target target = targets.getTarget(characterName, true);
		if(target==null) {
			targets = battle.getSpecialTargetList();
			target = targets.getTarget(characterName, true);
		}
		this.UnderlyingCharacter = target;
	}

	public static CombatSimulatorCharacter GetActiveCharacter() {
		Battle battle = Battle.currentBattle;
		champions.Target active = battle.getActiveTarget();
		return new CombatSimulatorCharacter(active);
		
	}

	public void ActivateAbilityByName(String abilityName) throws Exception {
		
		SimulatorAbility ability= SimulatorAbility.CreateAbility(abilityName, this);
		ability.ActivateAbility();
		this.ActiveAbility = ability;
	}
	public void MoveByName(String movementName, int distance) throws Exception {
		ActivateAbilityByName( movementName);
		((SimulatorMovement)this.ActiveAbility).setDistance(distance);
		this.ActiveAbility.ConfirmAttack();
		this.ActiveAbility.ConfirmAttack();
	}
	

}
