package VirtualDesktop.Character;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import VirtualDesktop.Ability.AbilityAdapter;
import VirtualDesktop.Ability.MovementAdapter;
import VirtualDesktop.Attack.AttackAdapter;
import VirtualDesktop.Attack.BasicTargetAdapter;
import champions.Ability;
import champions.Battle;
import champions.BattleEvent;
import champions.CVList;
import champions.DetailList;
import champions.Target;
import champions.TargetList;
import champions.enums.DefenseType;
import jdk.nashorn.api.scripting.JSObject;

public class CharacterAdaptor extends BasicTargetAdapter {
	public AbilityAdapter ActiveAbility;
	public String ActiveAbilityName;
	public String ActivateAbilityName;
	
	public CharacterAdaptor() {}
	
	public CharacterAdaptor(Target character) {
		this.target = character;
	}
	public CharacterAdaptor(String characterName) {
		this();
		Battle battle = Battle.currentBattle;
		//battle.startBattle();
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
		this.target =target;
	}
	public static CharacterAdaptor GetActiveCharacter() {
		Battle battle = Battle.currentBattle;
		champions.Target active = battle.getActiveTarget();
		return new CharacterAdaptor(active);
		
	}

	public void MoveByName(String movementName, int distance) throws Exception {
		ActivateAbilityByName( movementName);
		((MovementAdapter)this.ActiveAbility).setDistance(distance);
		this.ActiveAbility.ConfirmAttack();
		this.ActiveAbility.ConfirmAttack();
	}
	
	public JSONObject toJSON() {
		return new CharacterJSONExporter().toJSON(target);
	}
	
	public void ActivateAbilityByName(String abilityName) throws Exception {
		
		 LoadAbilityByName( abilityName);
		 if(this.ActiveAbility!=null) {
			 this.ActiveAbility.activateAbility();
		 }
		
	}
	public void LoadAbilityByName(String abilityName) throws Exception {
		AbilityAdapter ability = getAbility(abilityName);	
		this.ActiveAbility = ability;
	}
	public AbilityAdapter getAbilityWrapper(String abilityName) 
	{
		return AbilityAdapter.CreateAbilityWrapper(abilityName, this);
	}
	public AbilityAdapter getAbility(String abilityName) 
	{
		return AbilityAdapter.CreateAbility(abilityName, this);
	}
	public void Perform(String abilityName) {}
	public void AddAbility(AbilityAdapter ability) {}
	public void RemoveAbility(AbilityAdapter ability) {}
	public Dictionary<String, AbilityAdapter> getAllowedAbilities(){
		return null;
	}
	public void cancelAbilityInProgress() {}
	public AbilityAdapter getActiveAbilty(){
		return ActiveAbility;
	}
	public void setActiveAbilty(AbilityAdapter ability){	
	}
	
	public boolean IsPerformingSet() { return false;}
    public boolean IsBlocking() { return false;}
    public boolean IsRolling() { return false;};
    public boolean IsAborting(){ return false;};
    public boolean IsHaymakering() { return false;}
	
	private Target loadCharacterFromFile(String characterName) {
		return loadFromFile(characterName,".hcs"); 
	}
	
    public String getName() {
		
		return target.getName();
	}

	public int getDefense(DefenseType def) {
		return target.getDefense(def);
	}

	public JSONObject processJSON(JSONObject abilityJSON) {
		
		AbilityAdapter a = getAbilityWrapper((String) abilityJSON.get("Ability"));
		ActiveAbility = a;
		if(a==null) {
			Exception e = new Exception("Character "+ getName() + 
					" does not have ability " + (String) abilityJSON.get("Ability") +"!!!");
			
			e.printStackTrace();
			return null;
		}
		JSONObject r = a.processJSON(abilityJSON);
		if(a instanceof AttackAdapter) 
			a.WriteJSON(r);
		return r;
		
		
	}

	public JSONObject exportToJSON() {
		JSONObject ex = new JSONObject();
		ex.put("Name", getName());
		
		JSONObject bodyJSO = getCharasteristic("BODY").exportToJSON();
		ex.put("BODY",bodyJSO );
		JSONObject stunJSO = getCharasteristic("STUN").exportToJSON();
		ex.put("STUN",stunJSO );
		
		JSONArray damageEffects = getCharacterEffect().exportToJSON();
		ex.put("Effects", damageEffects  );
		
		return ex;
	}

}
