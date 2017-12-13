package VirtualDesktop.Attack.SingleAttack;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import VirtualDesktop.Controller.GLOBALS;
import VirtualDesktop.Roster.SimulatorRoster;
import champions.Battle;
import champions.BattleEvent;
import champions.CombatSimulator;
import champions.ConfigureBattleBattleEvent;
import junit.framework.Assert;

public class SingleAttackTest {
	
	@BeforeClass
	void LoadHCS() {
		
	}
	
	@BeforeEach
	void CreateNewBattleAndLoadRoster() {
		try {
			NewBattle();
			StartBattle();
			AddRoster();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@AfterEach
	void ClearAttackAndBattle() {
		try {
			ConfirmAttack();
			ConfirmAttack();
			NewBattle();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
		
	}

	private void AddRoster() throws InterruptedException {
		String content = "{\"Type\": \"RosterSynchronization\",	\"Rosters\": [ \"Heroes\"]}";
		WriteAbilityActivatedEventFile(content);
		Thread.sleep(3000);
	}

	protected void WriteAbilityActivatedEventFile(String content) {
		String fileName = GLOBALS.EXPORT_PATH + "AbilityActivatedFromDesktop.event";
		File file = new File(fileName);
		try {
			FileWriter fw = new FileWriter(file);
			fw.write(content);
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	void testSingleAttackRequestIsCreatedAgainstTarget_SingleAttackResponseIsCreatedWithTargetEffects() {
	
		try {
			//act
			InvokeAbility("Pass Turn");			
			InvokeAttack("Spider Strike", "Blind Justice",0,null);			
			JSONObject result  = LoadAttackResult();
			
			//assert			
			assertEquals("Spider Strike", (String)result.get("Ability"));
			assertEquals("AttackSingleTargetResult", result.get("Type"));
			
			String actualTarget = (String) ((JSONObject)result.get("Target")).get("Name");
			assertEquals("Blind Justice", actualTarget);
			
			Boolean actualHit = (Boolean) ((JSONObject)result.get("Results")).get("Hit");
			assertEquals(actualHit, true);
			
		
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
	}
	
	@Test
	void testSingleAttackRequestIsCreatedAgainstTargetWithPushedSTR_AttackDoesExtraDamage() {
		try {
			//act
		
			InvokeAbility("Pass Turn");			
			InvokeAttack("Strike", "Blind Justice",90,null);			
			JSONObject result  = LoadAttackResult();
			
			JSONArray effects = (JSONArray)((JSONObject)result.get("Results")).get("Effects");
			 assertTrue(effects.contains("Stunned"));
			
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	
	}
	
	@Test
	void testSingleAttackRequestIsCreatedWithAlotofToHitBonuses_AttackWillHit() {
		try {
			//act
		
			InvokeAbility("Pass Turn");			
			String toHitParameters = "\"Surprised\":true,\"Surprise Move\": 3,\"From Behind\": true,\"Entangled\": true,\"Recovering\": true";
			
			InvokeAttack("Spider Web", "Off-Road",0, toHitParameters);			
			JSONObject result  = LoadAttackResult();
			
			Boolean hit= (Boolean)((JSONObject)result.get("Results")).get("Hit");
			assertEquals(true, hit);
			
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	
	}
	
	@Test
	void testSingleAttackRequestIsCreatedWithNonTargetingSenseAndRangeAttack_AttackWillLikelyMiss() {
		try {
			//act
		
			InvokeAbility("Pass Turn");

			String toHitParameters = "\"Targeting Sense\":\"Normal Hearing\", \"Generic\":-3";
			
			InvokeAttack("Spider Web", "Pre-Emptive Strike",0, toHitParameters);			
			JSONObject result  = LoadAttackResult();
			
			Boolean hit= (Boolean)((JSONObject)result.get("Results")).get("Hit");
			assertEquals(false, hit);
			
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	
	}
	
	@Test
	void testSingleAttackRequestIsCreatedThatHitsTargetInTheHead__TargetWillLikelyBeUnconsious() {
		try {
			//act
		
			InvokeAbility("Pass Turn");

			String toHitParameters = "\"Hit Location\":\"HEAD\", \"Generic\":20";
			
			InvokeAttack("Spider Strike", "Ogun",0, toHitParameters);			
			JSONObject result  = LoadAttackResult();
			
			JSONArray effects = (JSONArray)((JSONObject)result.get("Results")).get("Effects");
			assertTrue(effects.contains("Unconscious"));
			
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	
	}
	
	@Test
	void testSingleAttackRequestIsCreatedWithObstruction_ObstructionTakesTheDamage() {
		try {
			//act
		
			InvokeAbility("Pass Turn");

			String toHitParameters = "\"Obstruction\": \"test\"";
			
			InvokeAttack("Spider Strike", "Ogun",0, toHitParameters);			
			JSONObject result  = LoadAttackResult();
			
			JSONArray effects = (JSONArray)((JSONObject)result.get("Obstruction Result")).get("Effects");
			assertTrue(effects.contains("Partially Destroyed"));
			
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	
	}
	
	@Test
	void testSingleAttackRequestIsCreatedWithAlotofToHitPenalties_AttackWillMiss() {
		try {
			//act
		
			InvokeAbility("Pass Turn");			
			String toHitParameters = "\"Generic\": -6,\"Off Hand\": true, \"Unfamiliar Weapon\": true,\"Encumbrance\": -3, \"Concealement\": -3,\"Range\": -3";
			
			InvokeAttack("Spider Web", "Ogun",0, toHitParameters);			
			JSONObject result  = LoadAttackResult();
			
			Boolean hit= (Boolean)((JSONObject)result.get("Results")).get("Hit");
			assertEquals(false, hit);
			
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	
	}
	private void SetKnockBackCollision(String object) throws InterruptedException {
		String request = "{\r\n" + 
				"	\"Type\": \"KnockbackCollisionSingleTarget\",\r\n" + 
				"	\"Knockback Target\":\""+object+ "\"" + "}\r\n" + 
				"";
		WriteAbilityActivatedEventFile(request);
		Thread.sleep(3000);
		
	}
	@Test
	void testKnockBackCollisionRequestIsCreatedForSingleAttack_BothObjectANdTargetTakeDamage() {
		try {
			InvokeAbility("Pass Turn");			
			String toHitParameters = "";
			
			InvokeAttack("Spider Strike", "Ogun",45, null);
			SetKnockBackCollision("test" );
			
			JSONObject result  = LoadAttackResult();
			
			JSONObject kc = (JSONObject) ((JSONObject) ((JSONObject)result).get("Results")).get("Knockback");
			JSONObject kco= (JSONObject) kc.get("Obstacle Collision");
			assertEquals("test", kco.get("Name"));
		
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	protected JSONObject LoadAttackResult() {
		FileReader reader;
		try {
			JSONParser parser = new JSONParser();
			reader = new  FileReader(GLOBALS.EXPORT_PATH + "\\AbilityAttackresult.event");
			JSONObject message = (JSONObject)parser.parse(reader);
			reader.close();
			return message;
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
		
		
	}

	private void ConfirmAttack() throws InterruptedException {
		String request = "{\"Type\": \"ConfirmAttack\",	\"Status\": \"Confirm\"}";

		WriteAbilityActivatedEventFile(request);
		Thread.sleep(500);
	}

	protected void InvokeAttack(String attack, String defender, int pushedStr, String toHitParameters) throws InterruptedException {
		String request = getBaseAttackString(attack,defender);
		if(pushedStr!=0) {
			request+= ", \"PushedStr\":"+ pushedStr;
		}
		if(toHitParameters!=null && toHitParameters!="") {
			request+= ","+toHitParameters;
		}
		request+= "}";
		WriteAbilityActivatedEventFile(request);
		Thread.sleep(3000);
	}

	private String getBaseAttackString(String attack, String defender) {
		// TODO Auto-generated method stub
		return "{\"Type\": \"AttackSingleTarget\",	\"Ability\": \""+ attack+ "\",\"Target\": \""+defender + "\"";
	}

	private void NewBattle() throws InterruptedException {
		String request = "{\"Type\": \"CombatAction\",	\"Action\": \"New Battle\"}";
		WriteAbilityActivatedEventFile(request); 
		Thread.sleep(1000);
		
	}

	public void InvokeAbility(String ability) throws InterruptedException {
		String request = "{\"Type\": \"SimpleAbility\",	\"Ability\": \""+ ability+ "\"}";
		WriteAbilityActivatedEventFile(request);
		Thread.sleep(2000);
	}

	private void StartBattle() throws InterruptedException {
		String request = "{\"Type\": \"CombatAction\",	\"Action\": \"Start\"}";
		WriteAbilityActivatedEventFile(request); 
		Thread.sleep(1000);
	
		
	}

	
}
