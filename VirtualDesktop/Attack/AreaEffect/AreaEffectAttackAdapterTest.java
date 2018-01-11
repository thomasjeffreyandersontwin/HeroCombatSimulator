package VirtualDesktop.Attack.AreaEffect;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import VirtualDesktop.Attack.*;
import VirtualDesktop.Character.CharacterAdaptor;
import champions.Battle;
import champions.CombatSimulator;
import champions.Roster;

class AreaEffectAttackAdapterTest extends AttackAdapterTest{
	
	CharacterAdaptor attacker;
	CharacterAdaptor defender1;
	CharacterAdaptor defender2;
	CharacterAdaptor defender3;
	Roster roster;
	AttackAdapter attack;

	
	
	PhysicalObjectAdapter door;
	PhysicalObjectAdapter lock ;	
	PhysicalObjectAdapter pot ;
	
	@BeforeEach
	void RosterHasFourCharactersLoaded() {
		
		attacker = new CharacterAdaptor(System.getProperty("user.dir") + "\\eventinfo\\testdata\\Pre-Emptive Strike\\Pre-Emptive Strike");
		defender1 = new CharacterAdaptor(System.getProperty("user.dir") + "\\eventinfo\\testdata\\Spyder\\Spyder");
		defender2 = new CharacterAdaptor(System.getProperty("user.dir") + "\\eventinfo\\testdata\\Saviour\\Saviour");
		defender3 = new CharacterAdaptor(System.getProperty("user.dir") + "\\eventinfo\\testdata\\Ogun\\Ogun");
		
		//arrange
		
		door = PhysicalObjectAdapter.newObjectFromPresests("Interior wood door");	
		lock = PhysicalObjectAdapter.newObjectFromPresests("Magnetic lock");	
		pot = PhysicalObjectAdapter.newObjectFromPresests("Chamber pot");	
		
		roster = Battle.currentBattle.findRoster("Unnamed");	
		
		
		
		
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
		
		for(int i=0 ; i< attack.getCountOfIndividualAttackTarget();i++)
		{
				AttackTarget attackTarget = attack.getIndividualAttackTarget(i);
				attackTarget.ForceHit();
		}
		return attack;
	}
	
	@Test
	void changingAnExistingAttackTargetToADiffferentDefener_ThenTheCorrectDefenderIsReturnedFromTheAttack()
	{
		//act
		AreaEffectAttackAdapter attack = targetAllDefendersWithAreaAttack();
		attack.changeAttackTargetAt(0,defender2);
		attack.changeAttackTargetAt(1,defender3);
		attack.changeAttackTargetAt(2,defender1);
		
		assertEquals(defender2.target, attack.getIndividualAttackTarget(0).getTarget());
		assertEquals(defender3.target, attack.getIndividualAttackTarget(1).getTarget());
		assertEquals(defender1.target, attack.getIndividualAttackTarget(2).getTarget());
		
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
		attack.getIndividualAttackTarget(2).placeObjectDirectlyBehindDefender(pot, 4);
		
		AreaEffectAttackResultAdapter r = attack.completeAttack();
				
		assertObjectWasCollidedIntoAndAndObjectTookCorrectDamageAndObjectWasDestroyed(door, r.getAffectedTargetResults().get(0));
		assertObjectWasCollidedIntoAndAndObjectTookCorrectDamageAndObjectWasDestroyed(lock, r.getAffectedTargetResults().get(1));
		assertObjectWasCollidedIntoAndAndObjectTookCorrectDamageAndObjectWasDestroyed(pot, r.getAffectedTargetResults().get(2));
		
		
	}
	
	@Test
	public void ToHItModifiersAreSetForAllAttackTargets_ToHitRollForAttackisModifiedCorrectlyForEachAttack() 
	{
		//arrange
		AreaEffectAttackAdapter attack = targetAllDefendersWithAreaAttack();
		
		for(int i=0; i< attack.getCountOfIndividualAttackTarget(); i++)
		{
			int toHitRoll=attack.getToHitRoll();
			int OCV = attack.getAttackerOCV();
			AttackTarget individualAttack = attack.getIndividualAttackTarget(i);
			changeModifersAndAssertToHitRollHasChanged(individualAttack);
			AttackTarget attackTarget = attack.getIndividualAttackTarget(i);
		}
			
		
		
	}
	
	public void targetSenseChangedForAttacker_ToHitRollForAttackisModified() {}
	
	public void attackerAttacksSpecificHitLocationForEachDefender_AttackDamageAndAttackToHitAreModifiedDifferentlyForEachDefender() {}
	
	public void attackerAttacksThreeDefendersAndDefendersAreInFrontOfTwoObstacleAndAttackDoesEnoughKnockbackToDestroyFirstObstacle_EachDefenderKnocksIntoBothObstacles()
	{
	}
	
	public void jSONWithFullAttackParametersPassedToCharacter_CharacterInvokesAttackAndResponseIsCreatedCorrectly()
	{
	}

	@AfterEach
	public void clearRoster()
	{
		
		Battle.currentBattle.setStopped(true);
		roster.remove(attacker.target);
		roster.remove(defender1.target);
		roster.remove(defender2.target);
		roster.remove(defender3.target);
		roster.remove(door.target);
		roster.remove(lock.target);
		roster.remove(pot.target);
		
		
		
	}
}
