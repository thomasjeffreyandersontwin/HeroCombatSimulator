package VirtualDesktop.Character;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.json.simple.JSONObject;

import com.sun.xml.internal.bind.v2.runtime.Name;

import VirtualDesktop.Controller.GLOBALS;
import champions.Ability;
import champions.Battle;
import champions.Effect;
import champions.Target;
import champions.enums.DefenseType;
import champions.interfaces.AbilityIterator;
import champions.interfaces.AbilityList;

public class CharacterJSONExporter {
	private Target exportingCharacter;
	
	public void ExportCharacterStates(Target character) {
		JSONObject activeTargetOutput = statesToJSON(character);
		 FileWriter writer;
			
		try {
			writer = new FileWriter("C:\\Champions\\hcs\\EventInfo\\CharacterStates\\" + character.getName() +".info");
			writer.write(activeTargetOutput.toJSONString());
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
		
	}
	private JSONObject statesToJSON(Target character) {
		JSONObject activeTargetOutput = new JSONObject();
		activeTargetOutput.put("Name", character.getName());
		
		ExportStates(character, activeTargetOutput);
		ExportEffects(character, activeTargetOutput);
		return activeTargetOutput;
	}
	
	public void ExportCharacter(Target character) {
		if(character==null) {
			return ;
		}
		exportingCharacter = character;
		try {
			JSONObject activeTargetOutput = toJSON(character);

			ExportDefenses(character, activeTargetOutput);
	          
	        FileWriter writer;
			writer = new FileWriter(GLOBALS.EXPORT_PATH + "ActiveCharacter.info");
			writer.write(activeTargetOutput.toJSONString());
	        writer.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
}
	public JSONObject toJSON(Target character) {
		JSONObject activeTargetOutput = new JSONObject();
		activeTargetOutput.put("Name", character.getName());
		
		ExportStates(character, activeTargetOutput);				
		ExportStats(activeTargetOutput);
		ExportAbilities(activeTargetOutput);		
		ExportEffects(character, activeTargetOutput);
		return activeTargetOutput;
	}
	
	private void ExportDefenses(Target character, JSONObject activeTargetOutput) {
		JSONObject defenses = new JSONObject();
		activeTargetOutput.put("Defenses", defenses);
		defenses.put("PD",character.getDefense(DefenseType.PD));
		defenses.put("rPD",character.getDefense(DefenseType.rPD));
		defenses.put("ED",character.getDefense(DefenseType.ED));
		defenses.put("rED",character.getDefense(DefenseType.rED));
	}
	private void ExportAbilities(JSONObject activeTargetOutput) {
		AbilityList list = exportingCharacter.getAbilityList();
		
		AddAbilities(activeTargetOutput, list,"Powers", 0); 
		AddAbilities(activeTargetOutput, list,"Skills", 1); 
		AddAbilities(activeTargetOutput, list,"Disadvantages", 2); 
		AddAbilities(activeTargetOutput, list,"Talents", 3);
		AddAbilities(activeTargetOutput, list,"Perks", 4);
		AddAbilities(activeTargetOutput, list,"Equipment", 5);
		
		AbilityList defaults = Battle.getCurrentBattle().getDefaultAbilities();
		AddAbilitiesFromList(activeTargetOutput, "Defaults",  list);
	}
	private void ExportStats(JSONObject activeTargetOutput) {
		JSONObject stats = new JSONObject();
		activeTargetOutput.put("Stats", stats);
		
		AddStat(stats,"STR");
		AddStat(stats,"DEX");
		AddStat(stats,"CON");
		AddStat(stats,"COM");
		AddStat(stats,"BODY");
		AddStat(stats,"PRE");
		AddStat(stats,"EGO");
		AddStat(stats,"INT");
		AddStat(stats,"PD");
		AddStat(stats,"ED");
		AddStat(stats,"rPD");
		AddStat(stats,"rED");
		AddStat(stats,"MD");
		AddStat(stats,"SPD");
		AddStat(stats,"REC");
		AddStat(stats,"STUN");
		AddStat(stats,"END");
		AddCV(stats,"OCV");
		AddCV(stats,"DCV");
		AddCV(stats,"ECV");
	}
	public void ExportEffects(Target character, JSONObject activeTargetOutput) {
		JSONObject effects = new JSONObject();
		activeTargetOutput.put("Effects", effects);
		List<Effect> ef = character.getEffects();
		for (int i = 0; i < ef.size(); i++) {
			JSONObject effect = new JSONObject();
			effect.put("Description", ef.get(i).getDescription());
			effects.put(ef.get(i).getName(), effect);
		}
	}
	public void ExportStates(Target character, JSONObject activeTargetOutput) {
		JSONObject states = new JSONObject();
		activeTargetOutput.put("States", states);
		states.put("Is Abortable", character.isAbortable());
		states.put("Is Dead", character.isDead());
		states.put("Is Dying", character.isDying());
		states.put("Is Stunned", character.isAbortable());
		states.put("Is Alive", character.isAbortable());
		states.put("Is Holding For Dex", character.isHoldingForDex());
		states.put("Is Unconsious", character.isUnconscious());
	}
		
	private void AddAbilities(JSONObject activeTargetOutput, AbilityList list,String category, int i) {
			
			
			AbilityList l = list.getSublist(i);
			
			AddAbilitiesFromList(activeTargetOutput, category, l);
	}
	public void AddAbilitiesFromList(JSONObject activeTargetOutput, String category,AbilityList list) {
			Ability ability=null;
			JSONObject powers = new JSONObject();
			activeTargetOutput.put(category, powers);
			if (list != null) { 
				for (AbilityIterator a = list.getAbilities(true); a.hasNext(); ability =a.next()) {	
					if(ability!=null) {  
						AddAbility(powers, ability);
					}
				}
			}
		}
	private void AddAbility(JSONObject powers, Ability ability) {
		if(ability!=null) {
		JSONObject abilityJSON = new JSONObject();
		abilityJSON.put("Description", ability.getDescription());
		abilityJSON.put("Is Enabled", ability.isEnabled(ability.getSource()));
		powers.put(ability.getName(), abilityJSON);
		}
	}
	private void AddStat(JSONObject stats, String stat) {
		JSONObject STUN = new JSONObject();
		int value = exportingCharacter.getCurrentStat(stat);
		int base = exportingCharacter.getBaseStat(stat);
		
		STUN.put("Current", value);
		STUN.put("Base", value);
		stats.put(stat, STUN);
	}
	private void AddCV(JSONObject stats, String stat) {
		JSONObject obj = new JSONObject();
		int value=0;
		int base=0;
		if(stat == "DCV") {
		 value = exportingCharacter.getCalculatedDCV();
		 base = exportingCharacter.getBaseDCV();
		}
		if(stat == "OCV") {
			 value = exportingCharacter.getCalculatedOCV();
			 base = exportingCharacter.getBaseOCV();
		}
		if(stat == "ECV") {
			 value = exportingCharacter.getCalculatedECV();
			 base = exportingCharacter.getBaseECV();
		}
		obj.put("Calculated", value);
		obj.put("Base", value);
		stats.put(stat, obj);
	}

	
}
