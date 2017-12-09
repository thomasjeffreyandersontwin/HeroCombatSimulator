package VirtualDesktop;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.json.simple.JSONObject;

import com.sun.xml.internal.bind.v2.runtime.Name;

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
		JSONObject activeTargetOutput = new JSONObject();
		activeTargetOutput.put("Name", character.getName());
		
		ExportStates(character, activeTargetOutput);
		ExportEffects(character, activeTargetOutput);
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
	public void ExportCharacter(Target character) {
		if(character==null) {
			return ;
		}
		exportingCharacter = character;
		try {
			JSONObject activeTargetOutput = new JSONObject();
			activeTargetOutput.put("Name", character.getName());
			
			ExportStates(character, activeTargetOutput);
					
	
			
			
			
			JSONObject stats = new JSONObject();
			activeTargetOutput.put("Stats", stats);
			
			AddStatToJSON(stats,"STR");
			AddStatToJSON(stats,"DEX");
			AddStatToJSON(stats,"CON");
			AddStatToJSON(stats,"COM");
			AddStatToJSON(stats,"BODY");
			AddStatToJSON(stats,"PRE");
			AddStatToJSON(stats,"EGO");
			AddStatToJSON(stats,"INT");
			AddStatToJSON(stats,"PD");
			AddStatToJSON(stats,"ED");
			AddStatToJSON(stats,"rPD");
			AddStatToJSON(stats,"rED");
			AddStatToJSON(stats,"MD");
			AddStatToJSON(stats,"SPD");
			AddStatToJSON(stats,"REC");
			AddStatToJSON(stats,"STUN");
			AddStatToJSON(stats,"END");
			AddCVToJSON(stats,"OCV");
			AddCVToJSON(stats,"DCV");
			AddCVToJSON(stats,"ECV");
			
			
			
			AbilityList list = exportingCharacter.getAbilityList();
			
			AddAbilities(activeTargetOutput, list,"Powers", 0); 
			AddAbilities(activeTargetOutput, list,"Skills", 1); 
			AddAbilities(activeTargetOutput, list,"Disadvantages", 2); 
			AddAbilities(activeTargetOutput, list,"Talents", 3);
			AddAbilities(activeTargetOutput, list,"Perks", 4);
			AddAbilities(activeTargetOutput, list,"Equipment", 5);
			
			AbilityList defaults = Battle.getCurrentBattle().getDefaultAbilities();
			AddAbilittiesFromList(activeTargetOutput, "Defaults",  list);
			
			ExportEffects(character, activeTargetOutput);
			
			JSONObject defenses = new JSONObject();
			activeTargetOutput.put("Defenses", defenses);
			defenses.put("PD",character.getDefense(DefenseType.PD));
			defenses.put("rPD",character.getDefense(DefenseType.rPD));
			defenses.put("ED",character.getDefense(DefenseType.ED));
			defenses.put("rED",character.getDefense(DefenseType.rED));
	          
			
	        FileWriter writer;
		
			writer = new FileWriter(VirtualDesktop.GLOBALS.EXPORT_PATH + "ActiveCharacter.info");
			writer.write(activeTargetOutput.toJSONString());
	        writer.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
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
			
			AddAbilittiesFromList(activeTargetOutput, category, l);
		}

		public void AddAbilittiesFromList(JSONObject activeTargetOutput, String category,AbilityList list) {
			Ability ability=null;
			JSONObject powers = new JSONObject();
			activeTargetOutput.put(category, powers);
			if (list != null) { 
				for (AbilityIterator a = list.getAbilities(true); a.hasNext(); ability =a.next()) {	
					if(ability!=null) {  
						AddAbilityToJSON(powers, ability);
					}
				}
			}
		}

		private void AddAbilityToJSON(JSONObject powers, Ability ability) {
			if(ability!=null) {
			JSONObject abilityJSON = new JSONObject();
			abilityJSON.put("Description", ability.getDescription());
			abilityJSON.put("Is Enabled", ability.isEnabled(ability.getSource()));
			powers.put(ability.getName(), abilityJSON);
			}
		}

		private void AddStatToJSON(JSONObject stats, String stat) {
			JSONObject STUN = new JSONObject();
			int value = exportingCharacter.getCurrentStat(stat);
			int base = exportingCharacter.getBaseStat(stat);
			
			STUN.put("Current", value);
			STUN.put("Base", value);
			stats.put(stat, STUN);
		}
		
		private void AddCVToJSON(JSONObject stats, String stat) {
			JSONObject STUN = new JSONObject();
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
			STUN.put("Calculated", value);
			STUN.put("Base", value);
			stats.put(stat, STUN);
		}

	
}
