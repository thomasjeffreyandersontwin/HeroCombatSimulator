package VirtualDesktop.Attack;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import VirtualDesktop.Ability.AbilityAdapter;
import VirtualDesktop.Attack.AttackTarget.HitLocation;
import VirtualDesktop.Attack.DamageAmount;
import VirtualDesktop.Attack.KnockbackResult;
import VirtualDesktop.Attack.ToHitModifiers.Concealment;
import VirtualDesktop.Character.CharacterAdaptor;
import VirtualDesktop.Character.CharacterSensesAdapter;
import VirtualDesktop.Character.SenseAdapter;
import champions.Ability;
import champions.Battle;
import champions.CombatSimulator;
import champions.Roster;
import champions.Target;
import champions.attackTree.AttackParametersNode;
import champions.attackTree.AttackTreeModel;
import champions.attackTree.EffectNode;
import champions.enums.DefenseType;
import champions.event.AbilityAddedEvent;
import junit.framework.Assert;

public class AttackAdapterTest extends BaseAttackAdapterTest{

	
	
	
	@BeforeEach
	void twoValidCharactersAreInARoster() {
		
		attacker = new CharacterAdaptor(System.getProperty("user.dir") + "\\eventinfo\\testdata\\Pre-Emptive Strike\\Pre-Emptive Strike");
		defender = new CharacterAdaptor(System.getProperty("user.dir") + "\\eventinfo\\testdata\\Ogun\\Ogun");
				
		roster = Battle.currentBattle.findRoster("Unnamed");
		roster.removeAll();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		roster.add(attacker.target);
		roster.add(defender.target);
			
		try {
			Thread.sleep(500);
			Battle.currentBattle.startBattle();	
			Thread.sleep(500);	
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Battle.currentBattle.startBattle();
		
	}
	
	@Test
	public void attackerAttacksDefender_DefenderIsHitAndLosesStunEqualToAttackStunDamage() {
		//arrange
		AttackAdapter _attack = (AttackAdapter) attacker.getAbilityWrapper("Strike");
		
		//act
		_attack.activateAbility();
		_attack.targetDefender(defender);
		_attack.ForceHit();
		AttackResultAdapter result = _attack.completeAttack();
		
		//assert
		assertAttackHit(_attack.getAttackTarget(), result);
		assertAttackDamage(result, defender);
	}

		
	@Test
	public void attackerPushesWithSTR_AttackDamageClassesIncrease() {
		//arrange
		int pushedAmount = 25;
		attack = (AttackAdapter) attacker.getAbilityWrapper("Strike");
		Target d = defender.target;
		int damageClass = attack.getDamageClass();
				
		//act
		attack.activateAbility();
		attack.targetDefender(defender);
		attack.pushWithStr(pushedAmount);
	
		int pushesDamageClass = (int) attack.UnderlyingAbility.getDamageDieForBattleEvent(attack.battleEvent,null);
		attack.completeAttack();
		
		//assert
		assertEquals(damageClass+pushedAmount/5, pushesDamageClass);
	}
	
	@Test
	public void ToHItModifiersAreSetForAttack_ToHitRollForAttackisModified() {
		//arrange
				
		attack = (AttackAdapter) attacker.getAbilityWrapper("Strike");
		Target d = defender.target;
		
		attack.activateAbility();
		attack.targetDefender(defender);
		
		changeModifersAndAssertToHitRollHasChanged(attack.attackTarget);
		attack.completeAttack();
	}

	
	@Test
	public void targetSenseChangedForAttacker_ToHitRollForAttackisModified() {
		//arrange
		attack = (AttackAdapter) attacker.getAbilityWrapper("Strike");
		attack.activateAbility();
		attack.targetDefender(defender);
		ToHitModifiers modifiers = attack.getToHitModifiers();
		int OCV = attack.getAttackerOCV();
		
		//act
		CharacterSensesAdapter senses =  attack.getAttackerSenses();
		SenseAdapter sense = senses.getSense("Normal Hearing");
		sense.Activate();
		
		//assert
		int modifiedOCV = OCV/2;		
		attack.completeAttack();	
		assertEquals( modifiedOCV,attack.getAttackerOCV());
	}

	@Test
	public void attackerHasOthertargetingSenseAndTargetSenseChangedForAttacker_ToHitRollForAttackisNotModified()
	{
		//arrange 
		CharacterAdaptor attackerWithFlash  = new CharacterAdaptor(System.getProperty("user.dir") + "\\eventinfo\\testdata\\Saviour\\Saviour");
		CharacterAdaptor attackerWithDangerSense = new CharacterAdaptor(System.getProperty("user.dir") + "\\eventinfo\\testdata\\Spyder\\Spyder");
		roster.add(attackerWithDangerSense.target);
		roster.add(attackerWithFlash.target);
		
		//arrange blind Spyder
		attack = (AttackAdapter) attackerWithFlash.getAbilityWrapper("Flash");
		attack.activateAbility();
		attack.targetDefender(attackerWithFlash);
		attack.ForceHit();
		attack.completeAttack();
		
		
		attack = (AttackAdapter) attackerWithDangerSense.getAbilityWrapper("Strike");
		attack.activateAbility();
		attack.targetDefender(defender);
		ToHitModifiers modifiers = attack.getToHitModifiers();
		int OCV = attack.getAttackerOCV();
		
		//act
		CharacterSensesAdapter senses =  attack.getAttackerSenses();
		SenseAdapter sense = senses.getSense("Normal Sight");
		sense.Activate();
		
		//assert
		int modifiedOCV = OCV;		
		assertEquals( modifiedOCV,attack.getAttackerOCV());
		
		attack.completeAttack();	
		roster.remove(attackerWithFlash.target);
		roster.remove(attackerWithDangerSense.target);
		
	}
	
	@Test
	public void attackerAttackDefenderAndDefenderHasObstructionInTheWay_ObstructionSoaksAttackDamage()
	{
		
		
		//arrange
		PhysicalObjectAdapter door = PhysicalObjectAdapter.newObjectFromPresests("Interior wood door");
		attack = (AttackAdapter) attacker.getAbilityWrapper("Strike");
		attack.activateAbility();
		attack.targetDefender(defender);
		
		
		//act
		attack.addObstruction(door);
		attack.ForceHit();
		AttackResultAdapter result = attack.completeAttack();
		
		//assert
		assertObstructionAbsorbsAttackAndIsDestroyed(result, door.getName() );
		
	}

	

	@Test
	public void attackerAttacksSpecificHitLocationOfDefender_AttackIsAimedAtHead()
	{
		//arrange
		attack = (AttackAdapter) attacker.getAbilityWrapper("Strike");
		attack.activateAbility();
		attack.targetDefender(defender);
		

		//act
		attack.targetHitLocation(HitLocation.HEAD);
		AttackResultAdapter r = attack.completeAttack();
		
		//assert
		AttackTarget.HitLocation hitLocationUsed = r.getLocationHit();
		
		assertEquals(HitLocation.HEAD, hitLocationUsed);
	}
	
	@Test
	public void attackerAttacksDefenderAndDefenderIsInFrontOfObstacleAndAttackDoesEnoughKnockbackToHitObstacle_DefenderTakesFullKnockbackDamageAndObstacleTakesFullKnockbackDamage()
	{
		//arrange
		PhysicalObjectAdapter collidingWith = PhysicalObjectAdapter.newObjectFromPresests("Interior wood door");
		
		attack = (AttackAdapter) attacker.getAbilityWrapper("Strike");
		attack.activateAbility();
		attack.targetDefender(defender);
		
		
		//act
		attack.placeObjectDirectlyBehindDefender(collidingWith,2);
		AttackResultAdapter r = attack.completeAttack();
		
		assertObjectWasCollidedIntoAndAndObjectTookCorrectDamageAndObjectWasDestroyed(collidingWith, r);
	}

		@Test
	public void attackerAttacksDefenderAndDefenderIsInFrontOfTwoObstacleAndAttackDoesEnoughKnockbackToDestroyFirstObstacle_DefenderKnocksIntoBothObstacles()
	{
		//arrange
		PhysicalObjectAdapter collidingWith = PhysicalObjectAdapter.newObjectFromPresests("Interior wood door");
		PhysicalObjectAdapter secondCollidingWith = PhysicalObjectAdapter.newObjectFromPresests("Magnetic lock");
		
		attack = (AttackAdapter) attacker.getAbilityWrapper("Strike");
		attack.activateAbility();
		
		attack.targetDefender(defender);
		//act
		attack.placeObjectDirectlyBehindDefender(collidingWith,2);
		attack.placeObjectDirectlyBehindDefender(secondCollidingWith,2);
		AttackResultAdapter r = attack.completeAttack();		

		assertCollisionForBothObjects(collidingWith, secondCollidingWith, r, "Interior wood door", "Magnetic lock");
				
		
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
		AttackAdapter _attack = (AttackAdapter) attacker.getActiveAbilty();
		AttackResultAdapter result = _attack.getLastResult();
		
		assertJSONResultEqualsResult(resultJSON, result);
	}

	private JSONObject buildAttackJSON() {
		JSONObject attackJSON = new JSONObject();
		attackJSON.put("Ability", "Strike");
		attackJSON.put("PushedStr", 45);
		buildAttackTargetJSON(attackJSON, "Ogun", "Interior wood door","Magnetic lock", 2);
		
		return attackJSON;
	}

	
	@AfterEach
	public void closeAllRosters() {
		super.closeAllRosters();
	}
}
