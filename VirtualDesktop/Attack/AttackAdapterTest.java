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

public class AttackAdapterTest {

	CharacterAdaptor attacker;
	CharacterAdaptor _defender;
	Roster roster;
	AttackAdapter _attack;
	
	@BeforeAll
	public static void HeroCombatSimulatiorIsRunning() {
		CombatSimulator.main(null);
	}
	
	@BeforeEach
	void twoValidCharactersAreInARoster() {
		
		attacker = new CharacterAdaptor(System.getProperty("user.dir") + "\\eventinfo\\testdata\\Pre-Emptive Strike\\Pre-Emptive Strike");
		_defender = new CharacterAdaptor(System.getProperty("user.dir") + "\\eventinfo\\testdata\\Ogun\\Ogun");
				
		roster = Battle.currentBattle.findRoster("Unnamed");
		roster.removeAll();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		roster.add(attacker.target);
		roster.add(_defender.target);
			
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
		_attack.targetDefender(_defender);
		_attack.ForceHit();
		AttackResultAdapter result = _attack.completeAttack();
		
		//assert
		assertAttackHit(_attack.getAttackTarget(), result);
		assertAttackDamage(result, _defender);
	}

	protected void assertAttackHit(AttackTarget _attack, AttackResultAdapter result) {
		assertEquals(true, result.getAttackHit());
		assertEquals(result.getDefender().getName(), _attack.getDefender().getName());
		assertEquals(true, result.getAttackHit());
	}

	public void assertAttackDamage(AttackResultAdapter result, CharacterAdaptor defender) {
		int now  = defender.getCharasteristic("STUN").getCurrentVaue();
		DamageAmount knockbackDamageAmount = result.getKnockbackResult().getKnockbackDamage();
		int kbstun = knockbackDamageAmount.getSTUN(); 
		double starting =defender.getCharasteristic("STUN").getStartingVaue();
		int stunDamage = result.getDamageResults().getSTUN(); 
		
		assertEquals( starting - now-kbstun, stunDamage);
	}
	
	@Test
	public void attackerPushesWithSTR_AttackDamageClassesIncrease() {
		//arrange
		int pushedAmount = 25;
		_attack = (AttackAdapter) attacker.getAbilityWrapper("Strike");
		Target d = _defender.target;
		int damageClass = _attack.getDamageClass();
				
		//act
		_attack.activateAbility();
		_attack.targetDefender(_defender);
		_attack.pushWithStr(pushedAmount);
	
		int pushesDamageClass = (int) _attack.UnderlyingAbility.getDamageDieForBattleEvent(_attack.battleEvent,null);
		_attack.completeAttack();
		
		//assert
		assertEquals(damageClass+pushedAmount/5, pushesDamageClass);
	}
	
	@Test
	public void ToHItModifiersAreSetForAttack_ToHitRollForAttackisModified() {
		//arrange
				
		_attack = (AttackAdapter) attacker.getAbilityWrapper("Strike");
		Target d = _defender.target;
		
		_attack.activateAbility();
		
		changeModifersAndAssertToHitRollHasChanged(_attack.attackTarget);
		_attack.completeAttack();
	}

	protected void changeModifersAndAssertToHitRollHasChanged(AttackTarget attackTarget ) {
		//arrange
		int toHitRoll=attackTarget.getToHitRoll();
		int OCV = attackTarget.getAttackerOCV();
		int DCV = attackTarget.getDefenderDCV();
		
		attackTarget.targetDefender(_defender);
		ToHitModifiers modifiers = attackTarget.getToHitModifiers();
		modifiers.setAttackerPerception(false);
		modifiers.setDefenderPerception(false);
		
		//act
		modifiers.setGenericAttacker(3);
		modifiers.setOffHand(true);
		modifiers.setSurpriseMove(-2);
		modifiers.setEncumbrance(-3);
		modifiers.setTargetConcealment(Concealment.HalfHidden);
		modifiers.setGenericDefender(+2);
		modifiers.setAttackFromBehind(false);
		modifiers.setDefenderSurprised(false);
		modifiers.setAttackerPerception(false);
		modifiers.setDefenderPerception(false);
		
		//assert
		int expectedToHitRoll = 11+ OCV-DCV+ modifiers.getTotalModiferAmount() ;
		int actualToHitRoll = toHitRoll;	
		assertEquals(expectedToHitRoll, actualToHitRoll); //fix when i add perception	
	}
	
	@Test
	public void targetSenseChangedForAttacker_ToHitRollForAttackisModified() {
		//arrange
		_attack = (AttackAdapter) attacker.getAbilityWrapper("Strike");
		_attack.activateAbility();
		_attack.targetDefender(_defender);
		ToHitModifiers modifiers = _attack.getToHitModifiers();
		int OCV = _attack.getAttackerOCV();
		
		//act
		CharacterSensesAdapter senses =  _attack.getAttackerSenses();
		SenseAdapter sense = senses.getSense("Normal Hearing");
		sense.Activate();
		
		//assert
		int modifiedOCV = OCV/2;		
		_attack.completeAttack();	
		assertEquals( modifiedOCV,_attack.getAttackerOCV());
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
		_attack = (AttackAdapter) attackerWithFlash.getAbilityWrapper("Flash");
		_attack.activateAbility();
		_attack.targetDefender(attackerWithFlash);
		_attack.ForceHit();
		_attack.completeAttack();
		
		
		_attack = (AttackAdapter) attackerWithDangerSense.getAbilityWrapper("Strike");
		_attack.activateAbility();
		_attack.targetDefender(_defender);
		ToHitModifiers modifiers = _attack.getToHitModifiers();
		int OCV = _attack.getAttackerOCV();
		
		//act
		CharacterSensesAdapter senses =  _attack.getAttackerSenses();
		SenseAdapter sense = senses.getSense("Normal Sight");
		sense.Activate();
		
		//assert
		int modifiedOCV = OCV;		
		assertEquals( modifiedOCV,_attack.getAttackerOCV());
		
		_attack.completeAttack();	
		roster.remove(attackerWithFlash.target);
		roster.remove(attackerWithDangerSense.target);
		
	}
	
	@Test
	public void attackerAttackDefenderAndDefenderHasObstructionInTheWay_ObstructionSoaksAttackDamage()
	{
		
		
		//arrange
		PhysicalObjectAdapter door = PhysicalObjectAdapter.newObjectFromPresests("Interior wood door");
		_attack = (AttackAdapter) attacker.getAbilityWrapper("Strike");
		_attack.activateAbility();
		_attack.targetDefender(_defender);
		
		
		//act
		_attack.addObstruction(door);
		_attack.ForceHit();
		AttackResultAdapter result = _attack.completeAttack();
		
		//assert
		assertObstructionAbsorbsAttackAndIsDestroyed(result, door.getName() );
		
	}

	public void assertObstructionAbsorbsAttackAndIsDestroyed(AttackResultAdapter result, String objectName) {
		ArrayList<AttackResultAdapter> obtacleDamageresults = result.getObstructionDamageResults();
		assertEquals(1, obtacleDamageresults.size());
		
		AttackResultAdapter doorResult = obtacleDamageresults.get(0);
		assertEquals(objectName, doorResult.getDefender().getName());
		ArrayList<String> effects = doorResult.getDefenderEffects().getEffects();
		
		assertEquals("Partially Destroyed", effects.get(0));
		assert(doorResult.getDamageResults().getBODY() > 0 );
	}

	@Test
	public void attackerAttacksSpecificHitLocationOfDefender_AttackDamageAndAttackToHitAreModified()
	{
		//arrange
		_attack = (AttackAdapter) attacker.getAbilityWrapper("Strike");
		_attack.activateAbility();
		_attack.targetDefender(_defender);
		
		int damageDice = _attack.getDamageClass();
		//act
		_attack.TargetHitLocation(HitLocation.HEAD);
		AttackResultAdapter r = _attack.completeAttack();
		
		//assert
		AttackTarget.HitLocation hitLocationUsed = r.getLocationHit();
		
		assertEquals(HitLocation.HEAD, hitLocationUsed);
	}
	
	@Test
	public void attackerAttacksDefenderAndDefenderIsInFrontOfObstacleAndAttackDoesEnoughKnockbackToHitObstacle_DefenderTakesFullKnockbackDamageAndObstacleTakesFullKnockbackDamage()
	{
		//arrange
		PhysicalObjectAdapter collidingWith = PhysicalObjectAdapter.newObjectFromPresests("Interior wood door");
		
		_attack = (AttackAdapter) attacker.getAbilityWrapper("Strike");
		_attack.activateAbility();
		_attack.targetDefender(_defender);
		
		
		//act
		_attack.placeObjectDirectlyBehindDefender(collidingWith,2);
		AttackResultAdapter r = _attack.completeAttack();
		
		assertObjectWasCollidedIntoAndAndObjectTookCorrectDamageAndObjectWasDestroyed(collidingWith, r);
	}

	protected void assertObjectWasCollidedIntoAndAndObjectTookCorrectDamageAndObjectWasDestroyed
	(PhysicalObjectAdapter collidingWith,AttackResultAdapter r) {
		int startingObjectBOD = (int) collidingWith.getCharasteristic("BODY").getStartingVaue();
		//assert - one object was collided with
		KnockbackResult knr = r.getKnockbackResult();
		ArrayList<CollisionResultAdapter>  collided = knr.getObstaclesCollidedWith();
		
		assertEquals(collidingWith.target, collided.get(0).getObjectCollidingWith().target);
		assertEquals(1, collided.size());
		
		//assert - object was destroyed
		CollisionResultAdapter collisionResult = collided.get(0);
		BasicTargetAdapter collidedWith = collisionResult.getObjectCollidingWith();
		
		assertEquals(collidingWith.getName(), collidedWith.getName());
		assert( collidedWith.getCharacterEffect().getEffects().contains("Partially Destroyed") ||  collidedWith.getCharacterEffect().getEffects().contains("Destroyed"));
		
		//assert - object took correct damage
		int collisonObjectBOD = collidedWith.getCharasteristic("BODY").getCurrentVaue();
		DamageAmount damageToObject = collisionResult.getDamageResults();
		int collisonBodyDamage = damageToObject.getBODY();
		assertEquals( (startingObjectBOD - collisonObjectBOD), collisonBodyDamage);
		
		//assert - target of _attack took correct amount of damage
		DamageAmount kdamage = knr.getKnockbackDamage();
		double now  =r.getDefender().getCharasteristic("STUN").getCurrentVaue();
		int kbstun = kdamage.getSTUN(); 
		
		
		double starting =r.getDefender().getCharasteristic("STUN").getStartingVaue();
		int PD = r.getDefender().getDefense(DefenseType.PD);
		
		
		int stunDamage = r.getDamageResults().getSTUN(); 
	
		
		assertEquals( (starting - now)- stunDamage, kbstun);
	}
	
	@Test
	public void attackerAttacksDefenderAndDefenderIsInFrontOfTwoObstacleAndAttackDoesEnoughKnockbackToDestroyFirstObstacle_DefenderKnocksIntoBothObstacles()
	{
		//arrange
		PhysicalObjectAdapter collidingWith = PhysicalObjectAdapter.newObjectFromPresests("Interior wood door");
		PhysicalObjectAdapter secondCollidingWith = PhysicalObjectAdapter.newObjectFromPresests("Magnetic lock");
		
		roster.add(collidingWith.target);
		roster.add(secondCollidingWith.target);
		
		
		
		int startingObjectBOD = collidingWith.getCharasteristic("BODY").getCurrentVaue();
		int secondStartingObjectBOD = secondCollidingWith.getCharasteristic("BODY").getCurrentVaue();
		_attack = (AttackAdapter) attacker.getAbilityWrapper("Strike");
		_attack.activateAbility();
		_attack.targetDefender(_defender);
		
		//act
		_attack.placeObjectDirectlyBehindDefender(collidingWith,2);
		_attack.placeObjectDirectlyBehindDefender(secondCollidingWith,2);
		AttackResultAdapter r = _attack.completeAttack();
		

		//assert - 2 collisions
		KnockbackResult knr = r.getKnockbackResult();
		ArrayList<CollisionResultAdapter>  collided = knr.getObstaclesCollidedWith();
		assertEquals(2, collided.size());
		
		
		//assert first collision
		CollisionResultAdapter collisionResult = collided.get(0);
		BasicTargetAdapter collidedWith = collisionResult.getObjectCollidingWith();
		assertEquals("Interior wood door", collidedWith.getName());
		assert( collidedWith.getCharacterEffect().getEffects().contains("Partially Destroyed") ||  collidedWith.getCharacterEffect().getEffects().contains("Destroyed"));

		int collisonObjectBOD = collidedWith.getCharasteristic("BODY").getCurrentVaue();
		DamageAmount damageToObject = collisionResult.getDamageResults();
		int collisonBodyDamage = damageToObject.getBODY();
		assertEquals( (startingObjectBOD - collisonObjectBOD), collisonBodyDamage);
		
		//second
		collisionResult = collided.get(1);
		collidedWith = collisionResult.getObjectCollidingWith();
		assertEquals("Magnetic lock", collidedWith.getName());
		assert( collidedWith.getCharacterEffect().getEffects().contains("Partially Destroyed") ||  collidedWith.getCharacterEffect().getEffects().contains("Destroyed"));

		//assert - object took correct damage
		collisonObjectBOD = collidedWith.getCharasteristic("BODY").getCurrentVaue();
		damageToObject = collisionResult.getDamageResults();
		collisonBodyDamage = damageToObject.getBODY();
		assertEquals( (startingObjectBOD - collisonObjectBOD), collisonBodyDamage);
		
		roster.remove(collidingWith.target);
		roster.remove(secondCollidingWith.target);
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
		
		assertEquals( result.getDefender().getName(), ((JSONObject) resultJSON.get("Defender")).get("Name"));
		
		AttackResultAdapter obs = result.getObstructionDamageResults().get(0);
		JSONArray obj = (JSONArray) resultJSON.get("Obstruction Damage Results");
		JSONObject objc = (JSONObject) ((JSONObject) obj.get(0)).get("Defender");
		assertEquals(obs.getDefender().getName(), objc.get("Name"));
		assert(obs.getDefender().getCharacterEffect().getEffects().contains("Partially Destroyed"));
		
		KnockbackResult r = result.getKnockbackResult();
		JSONObject kr = (JSONObject) resultJSON.get("Knockback Result");
		JSONObject kro = (JSONObject) ((JSONArray) kr.get("Collisions")).get(0);
		assertEquals(r.getObstaclesCollidedWith().get(0).getObjectCollidingWith().getName(), ((JSONObject) kro.get("Object Collided With")).get("Name"));
		assertEquals(r.getDistance(), kr.get("Distance"));
	}
		
	
	private JSONObject buildAttackJSON() {
		JSONObject attackJSON = new JSONObject();
		attackJSON.put("Ability", "Strike");
		attackJSON.put("Defender", "Ogun");
		attackJSON.put("PushedStr", 45);
		
		JSONArray physicalObjects = new JSONArray();
		attackJSON.put("Obstructions", physicalObjects);
		physicalObjects.add("Interior wood door");
		
		JSONObject toHitModifiers = new JSONObject();
		attackJSON.put("To Hit Modifiers", toHitModifiers);
		
		toHitModifiers.put("Surprise Move", -3);		
		toHitModifiers.put("From Behind", true);
		toHitModifiers.put("Defender Entangled", true);
		
		attackJSON.put("Targeting Sense", "Normal Hearing");
		
		JSONArray potentialKnocbackCollisions = new JSONArray();
		JSONObject knockbackCollision = new JSONObject();
		knockbackCollision.put("Collision Object", "Magnetic lock");

		knockbackCollision.put("Collision Distance", 2);
		potentialKnocbackCollisions.add(knockbackCollision);
		attackJSON.put("Potential Knockback Collisions", potentialKnocbackCollisions);
		
		return attackJSON;
	}

	@AfterEach
	protected void closeAllRosters() {
		Battle.currentBattle.setStopped(true);
		roster.removeAll();
		
		
	}
	
	
}
