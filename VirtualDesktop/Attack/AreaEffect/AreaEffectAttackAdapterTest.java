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
import VirtualDesktop.Character.CharacterAdaptor;
import VirtualDesktop.Character.CharacterSensesAdapter;
import VirtualDesktop.Character.SenseAdapter;
import champions.Battle;
import champions.CombatSimulator;
import champions.Roster;

class AreaEffectAttackAdapterTest extends BaseAttackAdapterTest{
	
	CharacterAdaptor attacker;
	CharacterAdaptor defender1;
	CharacterAdaptor defender2;
	CharacterAdaptor defender3;
	Roster roster;
	AttackAdapter attack;

	
	
	PhysicalObjectAdapter door;
	PhysicalObjectAdapter lock ;	
	PhysicalObjectAdapter pot ;
	PhysicalObjectAdapter plock ;
	
	@BeforeEach
	void RosterHasFourCharactersLoaded() {
		roster = Battle.currentBattle.findRoster("Unnamed");	
		
		roster.removeAll();
		attacker = new CharacterAdaptor(System.getProperty("user.dir") + "\\eventinfo\\testdata\\Pre-Emptive Strike\\Pre-Emptive Strike");
		defender1 = new CharacterAdaptor(System.getProperty("user.dir") + "\\eventinfo\\testdata\\Spyder\\Spyder");
		defender3 = new CharacterAdaptor(System.getProperty("user.dir") + "\\eventinfo\\testdata\\Saviour\\Saviour");
		defender2 = new CharacterAdaptor(System.getProperty("user.dir") + "\\eventinfo\\testdata\\Ogun\\Ogun");
		
		//arrange
		
		door = PhysicalObjectAdapter.newObjectFromPresests("Interior wood door");	
		lock = PhysicalObjectAdapter.newObjectFromPresests("Magnetic lock");	
		pot = PhysicalObjectAdapter.newObjectFromPresests("Chamber pot");
		plock = PhysicalObjectAdapter.newObjectFromPresests("Padlock");
		
		
		
		
		
		try {
			Battle.currentBattle.setStopped(true);
			Thread.sleep(500);
			roster.removeAll();
			Thread.sleep(500);
		}
		catch (InterruptedException e){}
		roster.add(attacker.target);
		roster.add(defender1.target);
		roster.add(defender2.target);
		roster.add(door.target);
		roster.add(lock.target);
		roster.add(pot.target);
		roster.add(defender3.target);
			
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
	void areaAttackHitsThreeDefendersAndHits_AllDefenderSufferStunLossEqualToDamageDone() {
		
		//act
		AreaEffectAttackAdapter attack = targetAllDefendersWithAreaAttack();
		AreaEffectAttackResultAdapter result = (AreaEffectAttackResultAdapter) attack.completeAttack();
		
		assertEquals(true, result.attackHitCenter());
		ArrayList<AttackResultAdapter> individualResults = result.getAffectedTargetResults();
		
		for(int i=0 ; i< individualResults.size();i++)
		{
			AttackResultAdapter targetedResult = individualResults.get(i);
			AttackTarget targetedAttack = attack.getIndividualAttackTarget(i);
			CharacterAdaptor defender = targetedAttack.getDefender();
			assertAttackHit(targetedAttack, targetedResult);
			assertAttackDamage(targetedResult, defender);
		}
	}

	private AreaEffectAttackAdapter targetAllDefendersWithAreaAttack() {
		AreaEffectAttackAdapter attack = (AreaEffectAttackAdapter) attacker.getAbilityWrapper("Air - Strike");
		
		attack.activateAbility();
		attack.targetCenter(new PhysicalObjectAdapter("Hex"));
		attack.targetDefender(defender1);
		attack.targetDefender(defender2);
		attack.targetDefender(defender3);
		
		attack.Process();
		
		for(int i=0 ; i< attack.getCountOfIndividualAttackTarget();i++)
		{
				AttackTarget attackTarget = attack.getIndividualAttackTarget(i);
				attackTarget.ForceHit();
		}
		
		
		return attack;
	}
	
	@Test
	void changingAnExistingAttackTargetToADiffferentDefender_ThenTheCorrectDefenderIsReturnedFromTheAttack()
	{
		//act
		AreaEffectAttackAdapter attack = targetAllDefendersWithAreaAttack();
		attack.changeAttackTargetAt(0,defender2);
		attack.changeAttackTargetAt(1,defender3);
		attack.changeAttackTargetAt(2,defender1);
		
		assertEquals(defender2.target, attack.getIndividualAttackTarget(0).getTarget());
		assertEquals(defender3.target, attack.getIndividualAttackTarget(1).getTarget());
		assertEquals(defender1.target, attack.getIndividualAttackTarget(2).getTarget());
		attack.completeAttack();
	}
	
	@Test
	void areaEffectAttackHitsThreeDefendersAndAdifferentObstructionIsInbetweenEachDefender_AllObstructionSoakAttackDamage()
	{
		//arrange
		AreaEffectAttackAdapter attack = targetAllDefendersWithAreaAttack();
		
		//act
		attack.getIndividualAttackTarget(0).addObstruction(door);
		attack.getIndividualAttackTarget(1).addObstruction(lock);
		attack.getIndividualAttackTarget(2).addObstruction(pot);
		
		AreaEffectAttackResultAdapter result = attack.completeAttack();
		
		//assert
		assertObstructionAbsorbsAttackAndIsDestroyed(result.getAffectedTargetResults().get(0), door.getName());
		assertObstructionAbsorbsAttackAndIsDestroyed(result.getAffectedTargetResults().get(1), lock.getName());
		assertObstructionAbsorbsAttackAndIsDestroyed(result.getAffectedTargetResults().get(2), pot.getName());
				
	}
	
	@Test
	public void attackerAttacksThreeDefendersInFrontOfObjectAndAttackDoesEnoughKnockbackToHitObject_DefendersTakesFullKnockbackDamageAndAllObjectsTakesFullKnockbackDamage()
	{
		//arrange
		AreaEffectAttackAdapter attack = targetAllDefendersWithAreaAttack();
		
		//attack.Process();
		//act
		attack.getIndividualAttackTarget(0).placeObjectDirectlyBehindDefender(door, 3);
		attack.getIndividualAttackTarget(1).placeObjectDirectlyBehindDefender(lock, 3);
		attack.getIndividualAttackTarget(2).placeObjectDirectlyBehindDefender(plock, 4);
		
		AreaEffectAttackResultAdapter r = attack.completeAttack();
				
		assertObjectWasCollidedIntoAndAndObjectTookCorrectDamageAndObjectWasDestroyed(door, r.getAffectedTargetResults().get(0));
		assertObjectWasCollidedIntoAndAndObjectTookCorrectDamageAndObjectWasDestroyed(lock, r.getAffectedTargetResults().get(1));
		assertObjectWasCollidedIntoAndAndObjectTookCorrectDamageAndObjectWasDestroyed(plock, r.getAffectedTargetResults().get(2));
		
		
	}
	
	@Test
	public void ToHItModifiersAreSetForAllAttackTargets_ToHitRollForAttackisModifiedCorrectlyForEachAttack() 
	{
		//arrange
		AreaEffectAttackAdapter attack = targetAllDefendersWithAreaAttack();
		
		for(int i=0; i< attack.getCountOfIndividualAttackTarget(); i++)
		{

			AttackTarget individualAttack = attack.getIndividualAttackTarget(i);
			changeModifersAndAssertToHitRollHasChanged(individualAttack);
			
		}
	}
	
	
	@Test
	public void attackerAttacksSpecificHitLocationForEachDefender_AttackDamageAndAttackToHitAreModifiedDifferentlyForEachDefender() 
	{
		//arrange
		AreaEffectAttackAdapter attack = targetAllDefendersWithAreaAttack();
		
		//act
		for(int i=0; i< attack.getCountOfIndividualAttackTarget(); i++)
		{
			AttackTarget at = attack.getIndividualAttackTarget(i);
			at.targetHitLocation(HitLocation.HEAD);
		}
		AreaEffectAttackResultAdapter r = attack.completeAttack();
		//assert
		for(int i=0; i< r.getAffectedTargetResults().size();i++)
		{
			AttackResultAdapter ra = r.getAffectedTargetResults().get(i);
			HitLocation hitLocationUsed = ra.getLocationHit();
			assertEquals(HitLocation.HEAD, hitLocationUsed);
		}
	}
	
	@Test
	public void attackerAttacksThreeDefendersAndDefendersAreInFrontOfTwoObstacleAndAttackDoesEnoughKnockbackToDestroyFirstObstacle_EachDefenderKnocksIntoBothObstacles()
	{
		//arrange
		AreaEffectAttackAdapter attack = targetAllDefendersWithAreaAttack();
		PhysicalObjectAdapter collidingWith = door;
		PhysicalObjectAdapter secondCollidingWith = lock;
		
		PhysicalObjectAdapter collidingWith2 = PhysicalObjectAdapter.newObjectFromPresests("Barrel");
		PhysicalObjectAdapter secondCollidingWith2 = PhysicalObjectAdapter.newObjectFromPresests("Bicycle");
		
		PhysicalObjectAdapter collidingWith3 = PhysicalObjectAdapter.newObjectFromPresests("Furniture, glass, reinforced");
		PhysicalObjectAdapter secondCollidingWith3 = PhysicalObjectAdapter.newObjectFromPresests("Cart, small");
		
		//act
		attack.getIndividualAttackTarget(0).placeObjectDirectlyBehindDefender(collidingWith, 2);
		attack.getIndividualAttackTarget(0).placeObjectDirectlyBehindDefender(secondCollidingWith, 2);
		
		attack.getIndividualAttackTarget(1).placeObjectDirectlyBehindDefender(collidingWith2, 2);
		attack.getIndividualAttackTarget(1).placeObjectDirectlyBehindDefender(secondCollidingWith2, 2);
	
		//attack.getIndividualAttackTarget(2).placeObjectDirectlyBehindDefender(secondCollidingWith3, 8);
		//attack.getIndividualAttackTarget(2).placeObjectDirectlyBehindDefender(collidingWith3, 11);
		
		
		AreaEffectAttackResultAdapter r = attack.completeAttack();		

		assertCollisionForBothObjects(collidingWith, secondCollidingWith,  r.getAffectedTargetResults().get(0), door.getName(), lock.getName());
		assertCollisionForBothObjects(collidingWith2, secondCollidingWith2, r.getAffectedTargetResults().get(1), "Barrel", "Bicycle");
	//	assertCollisionForBothObjects(collidingWith3, secondCollidingWith3, r.getAffectedTargetResults().get(2), "Furniture, glass, reinforced", "Cart, small");

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
