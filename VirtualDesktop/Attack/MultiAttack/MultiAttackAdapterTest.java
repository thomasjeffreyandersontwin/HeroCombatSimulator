package VirtualDesktop.Attack.MultiAttack;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import VirtualDesktop.Attack.AttackAdapter;
import VirtualDesktop.Attack.AttackResultAdapter;
import VirtualDesktop.Attack.BaseAttackAdapterTest;
import VirtualDesktop.Attack.PhysicalObjectAdapter;
import VirtualDesktop.Attack.AreaEffect.AreaEffectAttackAdapter;
import VirtualDesktop.Attack.AreaEffect.AreaEffectAttackResultAdapter;
import VirtualDesktop.Attack.Autofire.AutofireAttackAdapter;
import VirtualDesktop.Character.CharacterAdaptor;
import champions.Battle;
import champions.BattleEvent;
import champions.Roster;
import champions.SweepBattleEvent;

public abstract class MultiAttackAdapterTest extends BaseAttackAdapterTest {

	public CharacterAdaptor attacker;
	protected CharacterAdaptor defender1;
	protected CharacterAdaptor defender2;
	protected CharacterAdaptor defender3;
	protected Roster roster;
	AttackAdapter attack;

	PhysicalObjectAdapter door;
	PhysicalObjectAdapter lock ;	
	PhysicalObjectAdapter pot ;
	PhysicalObjectAdapter plock ;
	
	@BeforeEach
	void RosterHasFourCharactersLoaded() {
		roster = Battle.currentBattle.findRoster("Unnamed");	
		
		roster.removeAll();
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
		roster.add(createAttacker().target);
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

	protected MultiAttackAdapter targetAllDefendersWithAttack()
	{
		MultiAttackAdapter attack = activateAndSetupAttack();
		return targetDefenders(attack);
	}
	protected abstract CharacterAdaptor createAttacker();
	protected abstract MultiAttackAdapter activateAndSetupAttack() ;
	protected MultiAttackAdapter targetDefenders(MultiAttackAdapter attack) {
		attack.targetDefender(defender1);
		attack.targetDefender(defender2);
		attack.targetDefender(defender3);
		
		attack.Process();
		
		for(int i=0 ; i< attack.getCountOfIndividualAttackTarget();i++)
		{
				AttackAdapter attackTarget = attack.getIndividualAttack(i);
	//			attackTarget.ForceHit();
		}
		return attack;
	}
	
	@Test
	void changingAnExistingAttackTargetToADiffferentDefender_ThenTheCorrectDefenderIsReturnedFromTheAttack()
	{
		//act
		MultiAttackAdapter attack = targetAllDefendersWithAttack();
		attack.changeAttackTargetAt(0,defender2);
		attack.changeAttackTargetAt(1,defender3);
		attack.changeAttackTargetAt(2,defender1);
		
		assertEquals(defender2.target, attack.getIndividualAttack(0).getTarget());
		assertEquals(defender3.target, attack.getIndividualAttack(1).getTarget());
		assertEquals(defender1.target, attack.getIndividualAttack(2).getTarget());
		attack.completeAttack();
	}
	
	@Test
	void attackHitsThreeDefendersAndHits_AllDefenderSufferStunLossEqualToDamageDone() {
		
		//act
		MultiAttackAdapter attack = targetAllDefendersWithAttack();
		MultiAttackResultAdapter result = (MultiAttackResultAdapter) attack.completeAttack();
		
		try{Thread.sleep(500);}catch(Exception e) {}
		
		ArrayList<AttackResultAdapter> individualResults = result.getAffectedTargetResults();
		for(int i=0 ; i< individualResults.size();i++)
		{
			AttackResultAdapter targetedResult = individualResults.get(i);
			AttackAdapter attackTarget = attack.getIndividualAttack(i);
			CharacterAdaptor defender = attackTarget.getDefender();
			assertAttackHit(attackTarget, targetedResult);
			assertAttackDamage(targetedResult, defender);
		}
	}
	
	@Test
	void attackHitsThreeDefendersAndAdifferentObstructionIsInbetweenEachDefender_AllObstructionSoakAttackDamage()
	{
		//arrange
		MultiAttackAdapter attack = targetAllDefendersWithAttack();
		
		//act
		attack.getIndividualAttack(0).addObstruction(door);
		attack.getIndividualAttack(1).addObstruction(lock);
		attack.getIndividualAttack(2).addObstruction(pot);
		
		
		MultiAttackResultAdapter result = (MultiAttackResultAdapter) attack.completeAttack();
		
		//assert
		assertObstructionAbsorbsAttackAndIsDestroyed(result.getAffectedTargetResults().get(0), door.getName());
		assertObstructionAbsorbsAttackAndIsDestroyed(result.getAffectedTargetResults().get(1), lock.getName());
		assertObstructionAbsorbsAttackAndIsDestroyed(result.getAffectedTargetResults().get(2), pot.getName());
				
	}
	
	@Test
	public void attackerAttacksThreeDefendersInFrontOfObjectAndAttackDoesEnoughKnockbackToHitObject_DefendersTakesFullKnockbackDamageAndAllObjectsTakesFullKnockbackDamage()
	{
		//arrange
		MultiAttackAdapter attack = targetAllDefendersWithAttack();
		
		//attack.Process();
		//act
		attack.getIndividualAttack(0).placeObjectDirectlyBehindDefender(door, 3);
		attack.getIndividualAttack(1).placeObjectDirectlyBehindDefender(lock, 3);
		attack.getIndividualAttack(2).placeObjectDirectlyBehindDefender(plock, 4);
		
		MultiAttackResultAdapter r = (MultiAttackResultAdapter) attack.completeAttack();
				
		assertObjectWasCollidedIntoAndAndObjectTookCorrectDamageAndObjectWasDestroyed(door, r.getAffectedTargetResults().get(0));
		assertObjectWasCollidedIntoAndAndObjectTookCorrectDamageAndObjectWasDestroyed(lock, r.getAffectedTargetResults().get(1));
		assertObjectWasCollidedIntoAndAndObjectTookCorrectDamageAndObjectWasDestroyed(plock, r.getAffectedTargetResults().get(2));
		
		
	}
	
	@Test
	public void ToHItModifiersAreSetForAllAttackTargets_ToHitRollForAttackisModifiedCorrectlyForEachAttack() 
	{
		//arrange
		MultiAttackAdapter attack = targetAllDefendersWithAttack();
		
		for(int i=0; i< attack.getCountOfIndividualAttackTarget(); i++)
		{
			AttackAdapter individualAttack = attack.getIndividualAttack(i);
			changeModifersAndAssertCorrectModifiersHaveChanged(individualAttack);
		}
		attack.completeAttack();
	}
	
	@Test
	public void attackerAttacksSpecificHitLocationForEachDefender_AttackDamageAndAttackToHitAreModifiedDifferentlyForEachDefender() 
	{
		//arrange
		MultiAttackAdapter attack = targetAllDefendersWithAttack();
		
		//act
		for(int i=0; i< attack.getCountOfIndividualAttackTarget(); i++)
		{
			AttackAdapter at = attack.getIndividualAttack(i);
			at.targetHitLocation(VirtualDesktop.Attack.AttackAdapter.HitLocation.HEAD);
		}
		MultiAttackResultAdapter r = (MultiAttackResultAdapter) attack.completeAttack();
		
		//assert
		for(int i=0; i< r.getAffectedTargetResults().size();i++)
		{
			AttackResultAdapter ra = r.getAffectedTargetResults().get(i);
			VirtualDesktop.Attack.AttackAdapter.HitLocation hitLocationUsed = ra.getLocationHit();
			assertEquals(VirtualDesktop.Attack.AttackAdapter.HitLocation.HEAD, hitLocationUsed);
		}
	}
	
	@Test
	public void attackerAttacksThreeDefendersAndDefendersAreInFrontOfTwoObstacleEachAndAttackDoesEnoughKnockbackToDestroyFirstObstacle_EachDefenderKnocksIntoBothObstacles()
	{
		//arrange
		MultiAttackAdapter attack = targetAllDefendersWithAttack();
		PhysicalObjectAdapter collidingWith = door;
		PhysicalObjectAdapter secondCollidingWith = lock;
		
		PhysicalObjectAdapter collidingWith2 = PhysicalObjectAdapter.newObjectFromPresests("Barrel");
		PhysicalObjectAdapter secondCollidingWith2 = PhysicalObjectAdapter.newObjectFromPresests("Bicycle");
		roster.add(collidingWith2.target);
		roster.add(secondCollidingWith2.target);
		
		PhysicalObjectAdapter collidingWith3 = PhysicalObjectAdapter.newObjectFromPresests("Furniture, glass, reinforced");
		PhysicalObjectAdapter secondCollidingWith3 = PhysicalObjectAdapter.newObjectFromPresests("Cart, small");
		roster.add(collidingWith3.target);
		roster.add(secondCollidingWith3.target);
		
		try{Thread.sleep(500);}catch(Exception e){};
		//act
		attack.getIndividualAttack(0).placeObjectDirectlyBehindDefender(collidingWith, 2);
		attack.getIndividualAttack(0).placeObjectDirectlyBehindDefender(secondCollidingWith, 2);
		
		attack.getIndividualAttack(1).placeObjectDirectlyBehindDefender(collidingWith2, 2);
		attack.getIndividualAttack(1).placeObjectDirectlyBehindDefender(secondCollidingWith2, 2);
	
		attack.getIndividualAttack(2).placeObjectDirectlyBehindDefender(collidingWith3, 5);
		attack.getIndividualAttack(2).placeObjectDirectlyBehindDefender(secondCollidingWith3, 8);
		
		
		
		try{Thread.sleep(500);}catch(Exception e) {}
		
		MultiAttackResultAdapter r = (MultiAttackResultAdapter) attack.completeAttack();
		
		try{Thread.sleep(500);}catch(Exception e) {}
		
		//assert
		ArrayList<AttackResultAdapter>  list = r.getAffectedTargetResults();
		
		assertCollisionForBothObjects(collidingWith, secondCollidingWith,  list.get(0), door.getName(), lock.getName());
		
		
		assertCollisionForBothObjects(collidingWith2, secondCollidingWith2, list.get(1), "Barrel", "Bicycle");
		
		
		assertCollisionForBothObjects(collidingWith3, secondCollidingWith3, list.get(2),  "Furniture, glass, reinforced","Cart, small");

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
		MultiAttackAdapter attack = (MultiAttackAdapter) attacker.getActiveAbilty();
		try{Thread.sleep(1000);}catch(Exception e) {}
		MultiAttackResultAdapter result = (MultiAttackResultAdapter) attack.getLastResult();
		
		
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
	
	
	protected abstract JSONObject buildBaseAttackJSON();
	protected JSONObject buildAttackJSON() {
		JSONObject attackJSON = buildBaseAttackJSON();
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
}
