package VirtualDesktop.Attack.AreaEffect;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import VirtualDesktop.Ability.AbstractBattleClassAdapter;
import VirtualDesktop.Attack.*;
import VirtualDesktop.Attack.AttackTarget.HitLocation;
import VirtualDesktop.Attack.MultiAttack.MultiAttackAdapter;
import VirtualDesktop.Attack.MultiAttack.MultiAttackAdapterTest;
import VirtualDesktop.Character.CharacterAdaptor;
import VirtualDesktop.Character.CharacterSensesAdapter;
import VirtualDesktop.Character.SenseAdapter;
import champions.Battle;
import champions.CombatSimulator;
import champions.Roster;

class AreaEffectAttackAdapterTest extends MultiAttackAdapterTest{
	
	
	
	
	
	
	@Test
	void areaAttackHitsThreeDefendersAndHits_AttackHitsCenter() {
		
		//act
		AreaEffectAttackAdapter attack = (AreaEffectAttackAdapter) targetAllDefendersWithAttack();
		AreaEffectAttackResultAdapter result = (AreaEffectAttackResultAdapter) attack.completeAttack();
		
		assertEquals(true, result.attackHitCenter());
	}

	@Override
	protected CharacterAdaptor createAttacker() {
		attacker = new CharacterAdaptor(System.getProperty("user.dir") + "\\eventinfo\\testdata\\Pre-Emptive Strike\\Pre-Emptive Strike");
		return attacker;
	}
	@Override
	protected AreaEffectAttackAdapter activateAndSetupAttack() {
		AreaEffectAttackAdapter attack = (AreaEffectAttackAdapter) attacker.getAbilityWrapper("Air - Strike");
		
		attack.activateAbility();
		attack.targetCenter(new PhysicalObjectAdapter("Hex"));
		return attack;
	}
	
	@Test
	public void jSONWithFullAttackParametersPassedToCharacter_CharacterInvokesAttackAndResponseIsCreatedCorrectly()
	{
		//arrange
		JSONObject attackJSON = buildAttackJSON();
		
		//act
		JSONObject resultJSON =  attacker.processJSON(attackJSON);
		try {Thread.sleep(500);		} catch (InterruptedException e) {	e.printStackTrace();}
		
		//assert
		AreaEffectAttackAdapter attack = (AreaEffectAttackAdapter) attacker.getActiveAbilty();
		AreaEffectAttackResultAdapter result = (AreaEffectAttackResultAdapter) attack.getLastResult();
		
		
		JSONArray rJSONs = (JSONArray) resultJSON.get("Affected Targets");
		
		AttackResultAdapter r = result.getAffectedTargetResults().get(0);
		JSONObject rJSON = (JSONObject) rJSONs.get(0);
		assertJSONResultEqualsResult(rJSON, r);
		
		r = result.getAffectedTargetResults().get(1);
		rJSON = (JSONObject) rJSONs.get(1);
		assertJSONResultEqualsResult(rJSON, r);
		
		r = result.getAffectedTargetResults().get(2);
		rJSON = (JSONObject) rJSONs.get(2);
		assertJSONResultEqualsResult(rJSON, r);
		
		


	}
	
	private JSONObject buildAttackJSON() {
		JSONObject attackJSON = new JSONObject();
		attackJSON.put("Ability", "Air - Strike");
		attackJSON.put("AOE Center", "Hex");
		JSONArray attackTargets = new JSONArray();
		attackJSON.put("Attack Targets",attackTargets);
		
		JSONObject attackTarget = new JSONObject();
		buildAttackTargetJSON(attackTarget, "Ogun", "Interior wood door","Magnetic lock", 2);
		attackTargets.add(attackTarget);
		
		attackTarget = new JSONObject();
		buildAttackTargetJSON(attackTarget, "Spyder", "Barrel","Bicycle", 3);
		attackTargets.add(attackTarget);
		
		attackTarget = new JSONObject();
		buildAttackTargetJSON(attackTarget, "Saviour", "Chamber pot","Padlock", 4);
		attackTargets.add(attackTarget);
		return attackJSON;
	}

	@AfterEach
	public void clearRoster()
	{
		roster.removeAll();
		while(roster.getSize()>0)
		{
			try {Thread.sleep(100);}catch (Exception e) {}
			
		}
		Battle.currentBattle.setStopped(true);
		
		
		
		
	}
	
	

}
