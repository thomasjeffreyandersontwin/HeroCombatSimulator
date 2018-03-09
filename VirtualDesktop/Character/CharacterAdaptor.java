package VirtualDesktop.Character;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Iterator;

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
import champions.Power;
import champions.Target;
import champions.TargetList;
import champions.enums.DefenseType;
import champions.interfaces.PAD;
import champions.parameters.ParameterList;
import champions.powers.powerCombatLevels;
import champions.powers.skillSkillLevels;
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
		try {Thread.sleep(200);}catch(Exception e) {}
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
	public void LoadAbilityByName(String abilityName) {
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
	
    public enum CombatStance {OFFENSIVE, BALANCED, DEFENSIVE}
    public CombatStance CombatStance;
    public void ChangeCombatStance(CombatStance stance) {
    	
    	for (Iterator iterator = target.getAbilities(); iterator.hasNext();) 
    	{
			Ability a  = (Ability) iterator.next();
			Power pc = null;
			
			boolean cont=false;
			if(a.getPower() instanceof powerCombatLevels) {
				a = a.getSource().getAbility(a.getName()).getInstanceGroup().getCurrentInstance();
				pc = (powerCombatLevels) a.getPower() ;
				cont =true;
			}
			else if(a.getPower() instanceof skillSkillLevels && a.getPower() instanceof skillSkillLevels&& a.getPower().getParameterList(a).getParameterValue("LevelType").equals("Overall Level")) 
			{
				a = a.getSource().getAbility(a.getName()).getInstanceGroup().getCurrentInstance();
				pc = (skillSkillLevels) a.getPower() ;
			
				cont =true;
			}
			if(cont)
			{
				ParameterList pl = pc.getParameterList(a);
				int  lvl = (int) pl.getParameterValue("Level");
			
				if(stance==CombatStance.OFFENSIVE)
				{
					pl.setParameterValue("OCVLevel", lvl);
					pl.setParameterValue("DCVLevel", 0);
				}
				if(stance==CombatStance.DEFENSIVE)
				{
					pl.setParameterValue("OCVLevel", 0);
					pl.setParameterValue("DCVLevel", lvl);
				}
			
				if(stance==CombatStance.BALANCED)
				{
					pl.setParameterValue("OCVLevel", lvl/2);
					pl.setParameterValue("DCVLevel", lvl - lvl/2);
				}
				pc.configurePAD(a, pl);
			}

		}
    	CombatStance = stance;
    }
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

	public boolean hasEffect(String string) {
		return this.target.getEffect(string)!=null;
	}

}
