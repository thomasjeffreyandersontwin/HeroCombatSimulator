package VirtualDesktop.Character;

import org.json.simple.JSONObject;

import VirtualDesktop.Ability.AbilityWrapper;
import VirtualDesktop.Ability.MovementAdapter;
import champions.Ability;
import champions.Battle;
import champions.BattleEvent;
import champions.Target;
import champions.TargetList;

public class CharacterAdaptor {
	public Target UnderlyingCharacter;
	public AbilityWrapper ActiveAbility;
	public String ActiveAbilityName;
	public String ActivateAbilityName;
	public CharacterAdaptor() {}
	public CharacterAdaptor(Target character) {
		this.UnderlyingCharacter = character;
	}
	
	public CharacterAdaptor(String characterName) {
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

	public static CharacterAdaptor GetActiveCharacter() {
		Battle battle = Battle.currentBattle;
		champions.Target active = battle.getActiveTarget();
		return new CharacterAdaptor(active);
		
	}

	public void ActivateAbilityByName(String abilityName) throws Exception {
		
		AbilityWrapper ability= AbilityWrapper.CreateAbility(abilityName, this);
		ability.ActivateAbility();
		this.ActiveAbility = ability;
	}
	public void MoveByName(String movementName, int distance) throws Exception {
		ActivateAbilityByName( movementName);
		((MovementAdapter)this.ActiveAbility).setDistance(distance);
		this.ActiveAbility.ConfirmAttack();
		this.ActiveAbility.ConfirmAttack();
	}
	
	public JSONObject toJSON() {
		return new CharacterJSONExporter().toJSON(this.UnderlyingCharacter);
	}
	

}
