package VirtualDesktop.Character;

import java.io.File;
import java.io.IOException;
import java.util.Dictionary;

import org.json.simple.JSONObject;

import VirtualDesktop.Ability.AbilityWrapper;
import VirtualDesktop.Ability.MovementAdapter;
import champions.Ability;
import champions.Battle;
import champions.BattleEvent;
import champions.DetailList;
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
		if(target==null) 
		{
			target = loadCharacterFromFile(characterName);
		}
		this.UnderlyingCharacter = target;
	}
	private Target loadCharacterFromFile(String characterName) {
		File file = new File(characterName +".hcs");
		DetailList d = new DetailList();
		Target target=null;
		try {
			target = (Target) d.open(file);
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		return target;
	}

	public static CharacterAdaptor GetActiveCharacter() {
		Battle battle = Battle.currentBattle;
		champions.Target active = battle.getActiveTarget();
		return new CharacterAdaptor(active);
		
	}

	public void ActivateAbilityByName(String abilityName) throws Exception {
		
		 LoadAbilityByName( abilityName);
		 if(this.ActiveAbility!=null) {
			 this.ActiveAbility.ActivateAbility();
		 }
		
	}
	
	public void LoadAbilityByName(String abilityName) throws Exception {
		AbilityWrapper ability= AbilityWrapper.CreateAbility(abilityName, this);
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
	
	public AbilityWrapper getAbilityWrapper(String abilityName) 
	{
		return AbilityWrapper.CreateAbilityWrapper(abilityName, this);
	}
	
	public AbilityWrapper getAbility(String abilityName) 
	{
		return AbilityWrapper.CreateAbility(abilityName, this);
	}
	
	public void Perform(String abilityName) {}

	public void AddAbility(AbilityWrapper ability) {}

	public void RemoveAbility(AbilityWrapper ability) {}
	
	public Dictionary<String, AbilityWrapper> getAllowedAbilities(){
		return null;
	}
	
	public void CancelAbilityInProgress() {}
	
	public AbilityWrapper getActiveAbilty(){
		return null;
	}
	
	public void setActiveAbilty(AbilityWrapper ability){
		
	}
	
	public boolean IsPerformingSet() { return false;}
    public boolean IsBlocking() { return false;}
    public boolean IsRolling() { return false;};
    public boolean IsAborting(){ return false;};
    public boolean IsHaymakering() { return false;}
	public Object getName() {
		// TODO Auto-generated method stub
		return UnderlyingCharacter.getName();
	};
	

}
