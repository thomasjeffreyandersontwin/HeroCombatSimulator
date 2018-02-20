package VirtualDesktop.Character;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.lang3.ClassUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.runners.Parameterized.Parameter;

import com.sun.xml.internal.bind.v2.runtime.Name;

import VirtualDesktop.Controller.GLOBALS;
import champions.Ability;
import champions.Battle;
import champions.DetailList;
import champions.Effect;
import champions.Target;
import champions.enums.DefenseType;
import champions.interfaces.AbilityIterator;
import champions.interfaces.AbilityList;
import champions.interfaces.Advantage;
import champions.interfaces.Limitation;
import champions.interfaces.SpecialParameter;
import champions.parameters.ParameterList;
import champions.powers.advantageAreaEffect;
import champions.powers.advantageAutofire;
import champions.powers.advantageExplosion;
import sun.security.util.Length;



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
		
		AddAbilities(activeTargetOutput, list,"Powers"); 
		AddAbilities(activeTargetOutput, list,"Skills"); 
		AddAbilities(activeTargetOutput, list,"Disadvantages"); 
		AddAbilities(activeTargetOutput, list,"Talents");
		AddAbilities(activeTargetOutput, list,"Perks");
		AddAbilities(activeTargetOutput, list,"Equipment");
		
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
			if(ef.get(i).getDescription()!=null) {
				effect.put("Description", ef.get(i).getDescription());
			}
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
		
	private void AddAbilities(JSONObject activeTargetOutput, AbilityList list,String category) {
			
			for (int j = 0; j <  list.getSublists().length; j++) {
				AbilityList l = list.getSublist(j);
				if(l.getName().equals(category)) {
					AddAbilitiesFromList(activeTargetOutput, category, l);
					return;
				}
			}
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
			ParameterList parameters = ability.getPowerParameterList();
			for (int j=0; j<parameters.getParameterCount(); j++) {
				champions.parameters.Parameter p = parameters.getParameter(j);
				String key  = p.getName();
				Object val = parameters.getParameterValue(key);
				if(val instanceof ArrayList<?>) {
					JSONArray array = new JSONArray();
					for(int i=0;i < ((ArrayList<?>)val).size();i++)
					{
						Object arrayVal = ((ArrayList<?>)val).get(i);
						if(arrayVal instanceof Ability)
						{
							array.add(((Ability)arrayVal).getName());
						}
						else {
							if(arrayVal!=null && ClassUtils.isPrimitiveOrWrapper(arrayVal.getClass())){
								array.add(val);
							}
							else {
								array.add(val.toString());
							}
						}
					}
					abilityJSON.put(key, array);
					
				}
				else if(val instanceof Ability && !((Ability)val).getName().equals(ability.getName())) {
					AddAbility(abilityJSON, (Ability)val);
					
				}
				else {
					if(val!=null && ClassUtils.isPrimitiveOrWrapper(val.getClass())){
						abilityJSON.put(key, val);
					}
					else {
						abilityJSON.put(key, "");
					}
				}
			}
			
			JSONObject detailsJSON = new JSONObject();
			abilityJSON.put("Details", detailsJSON);
			int k =0;
			Object key = ability.getKey(k);
			while(key!=null) {
				Object val = ability.getValue(k);
				if(val instanceof ParameterList) {
					JSONObject parameterJSON = ExportParameterList(val);
					val = parameterJSON;	
				}
				if(val instanceof SpecialParameter) {
					int index = ability.findIndexed("SpecialParameter", "SPECIALPARAMETER", val);
					ParameterList spl = ((SpecialParameter)val).getParameterList(ability, index);
					JSONObject spljson = ExportParameterList(spl);
					detailsJSON.put(key, spljson);
				}
				else if(val instanceof Ability && !((Ability)val).getName().equals(ability.getName())) {
					AddAbility(abilityJSON, (Ability)val);
					
				}
				else if(val ==null) 
				{
					detailsJSON.put(key, "");
				}
				else {
					if(val instanceof Ability) {
						detailsJSON.put(key, ((Ability)val).getName());	
					}
					else {
						if(ClassUtils.isPrimitiveOrWrapper(val.getClass())){
							detailsJSON.put(key, val);
						}
						else {
							detailsJSON.put(key, "");
						}
					}
				}

				k++;
				key = ability.getKey(k);
			}
			
			abilityJSON.put("IsTalent", ability.isTalent());
			abilityJSON.put("IsSkill", ability.isSkill());
			abilityJSON.put("IsDisadvantage", ability.isDisadvantage());
			abilityJSON.put("IsPerk", ability.isPerk());
			abilityJSON.put("IsPower", ability.isPower());
			
			abilityJSON.put("IsAlwaysOn", ability.isAlwaysOn());
			abilityJSON.put("IsConstant", ability.isConstant());
			abilityJSON.put("IsInherent", ability.isInherent());
			
			abilityJSON.put("IsAttack", ability.isAttack());
			abilityJSON.put("IsDefense", ability.isDefense());
			abilityJSON.put("IsNND", ability.isNND());
			abilityJSON.put("IsNormal", ability.isNormalAttack());
			abilityJSON.put("IsKilling", ability.isKillingAttack());
			
			abilityJSON.put("IsRanged", ability.isRangedAttack());
			abilityJSON.put("IsMeleeAttack", ability.isMeleeAttack());
			abilityJSON.put("IsMovement", ability.isMovementPower());
			abilityJSON.put("IsThrow", ability.isThrow());
			abilityJSON.put("IsGrab", ability.isGrab());
			abilityJSON.put("IsEgoBased", ability.isEgoBased());
			
			try {
			abilityJSON.put("DamageDice", ability.getDamageDie());
			}
			catch(Exception e){
				e.printStackTrace();
				abilityJSON.put("DamageDice", ability.getDamageDie());
			}
			abilityJSON.put("Damage", ability.getDamageString());
			abilityJSON.put("DoesKnockback", ability.getDoesKnockback());
			
			abilityJSON.put("END Cost", ability.getENDCost());
			
			abilityJSON.put("IsEnabled", ability.isEnabled(null));
			abilityJSON.put("IsActivated", ability.isActivated(null));
			abilityJSON.put("IsDelayed", ability.isDelayed());
			abilityJSON.put("IsDelayedInActivating", ability.isDelayActivating(null));
			
			abilityJSON.put("RealCost", ability.getRealCost());
			
			String spread = (String) ability.getValue("Ability.CANSPREAD");
			if(spread!=null)
			{
				if(spread=="TRUE")
				{
					abilityJSON.put("CanSpread",true);
				}
				else
				{
					abilityJSON.put("CanSpread",false);
				}
			}
			else 
			{
				abilityJSON.put("CanSpread",false);
			}
			
			
			
			
			powers.put(ability.getName(), abilityJSON);
			ExportAdvantages(ability, abilityJSON);
			ExportLimitations(ability, abilityJSON);
			
		}
	}
	private JSONObject ExportParameterList(Object val) {
		JSONObject parameterJSON = new JSONObject();
		ParameterList pl = (ParameterList)val;
		for (int j=0; j< pl.getParameterCount(); j++) {
			champions.parameters.Parameter p = pl.getParameter(j); 
			String pKey  = p.getName();
			Object pVal = pl.getParameterValue(pKey);
			
			if(pVal instanceof ParameterList) {
				JSONObject chilParameterJSON = ExportParameterList(pVal);
				pVal = chilParameterJSON;
			}
			if(ClassUtils.isPrimitiveOrWrapper(val.getClass())){
				parameterJSON.put(pKey, pVal);
			}
			else {
				parameterJSON.put(pKey, "");
			};
		}
		return parameterJSON;
	}
	private void ExportAdvantages(Ability ability, JSONObject abilityJSON) {
		if(ability.getAdvantageCount()>0) {
			JSONObject advantages = new JSONObject();
			abilityJSON.put("Advantages", advantages);
			
			for (int i=0; i< ability.getAdvantageCount();i++) {
				Advantage a = ability.getAdvantage(i);
				JSONObject advantageJSON = new JSONObject();
				advantages.put(a.getName(),advantageJSON);
				advantageJSON.put("Description", a.getDescription());
								
				ParameterList parameters =  a.getParameterList();
				for (int j=0; j<parameters.getParameterCount(); j++) {
					
					champions.parameters.Parameter p = parameters.getParameter(j);
					String key  = p.getName();
					if(!key.equals("Private")) {
						Object val = parameters.getParameterValue(key);
						
						if(ClassUtils.isPrimitiveOrWrapper(val.getClass())){
							advantageJSON.put(key, val);
						}
						else {
							advantageJSON.put(key, "");
						};
					}
				}
				CalculateAndExportAreaEffectRange(ability, a, advantageJSON, parameters);
			}
		}
	}
	
	private void ExportLimitations(Ability ability, JSONObject abilityJSON) {
		if(ability.getLimitationCount()>0) {
			JSONObject limitations = new JSONObject();
			abilityJSON.put("Limitations", limitations);
			
			for (int i=0; i< ability.getLimitationCount();i++) {
				Limitation l = ability.getLimitation(i);
				JSONObject limitationJSON = new JSONObject();
				limitations.put(l.getName(),limitationJSON);
				limitationJSON.put("Description", l.getDescription());
								
				ParameterList parameters =  l.getParameterList();
				for (int j=0; j<parameters.getParameterCount(); j++) {
					
					champions.parameters.Parameter p = parameters.getParameter(j);
					String key  = p.getName();
					if(!key.equals("Private")) {
						Object val = parameters.getParameterValue(key);
						if(!key.equals("Private")) {
							if(val!=null) {
								if(ClassUtils.isPrimitiveOrWrapper(val.getClass())){
									limitationJSON.put(key, val);
								}
								else {
									limitationJSON.put(key, "");
								};
							}
						}
					}
				}
			}
		}
	}
	private void CalculateAndExportAreaEffectRange(Ability ability, Advantage a, JSONObject advantageJSON,
			ParameterList parameters) {
		if(a.getName().equals("Area Effect") || a.getName().equals("Explosion"))
		{
			Ability ability2 =(Ability) ability.clone();
			
			int i = ability2.getAdvantageIndex(advantageAreaEffect.advantageName);
			advantageAreaEffect areaEffect2 = (advantageAreaEffect) ability2.getAdvantage(i);
			ability2.removeAdvantage(areaEffect2);
			
			int distance=0;
			int cost = ability2.getAPCost();
			if (parameters.getParameterStringValue("Shape").equals("Radius")){
				
				distance = cost/10;
			}
			if (parameters.getParameterStringValue("Shape").equals("One-hex")){
				distance = 1;
			}
			if (parameters.getParameterStringValue("Shape").equals("Cone")){
				distance = cost/5;
			}
			if (parameters.getParameterStringValue("Shape").equals("Line")){
				distance = cost/5;
			}
			if (parameters.getParameterStringValue("Shape").equals("Any Area")){
				distance = cost/10;
			}
			int increasedArea = (int) advantageJSON.get("IncreasedAreaLevel");
			distance = distance *(increasedArea+1);
			advantageJSON.put("Range", distance);
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
