package VirtualDesktop.Attack;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import VirtualDesktop.Attack.ToHitModifiers.Concealment;
import VirtualDesktop.Character.CharacterAdaptor;
import champions.Battle;
import champions.CombatSimulator;
import champions.Roster;
import champions.attackTree.AttackTreePanel;
import champions.enums.DefenseType;

public abstract class BaseAttackAdapterTest  {
	protected CharacterAdaptor attacker;
	protected CharacterAdaptor defender;
	protected static Roster roster;
	protected  AttackAdapter attack;

	@BeforeAll
	public static void HeroCombatSimulatiorIsRunning() {
		if(CombatSimulator.getFrame()==null )
		{
			CombatSimulator.main(null);
		}
	}
	
	protected void assertObjectWasCollidedIntoAndAndObjectTookCorrectDamageAndObjectWasDestroyed
	(PhysicalObjectAdapter collidingWith,AttackResultAdapter r) {
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
		int startingObjectBOD = (int) collidingWith.getCharasteristic("BODY").getStartingValue();
		int collisonObjectBOD = collidedWith.getCharasteristic("BODY").getCurrentVaue();
		DamageAmount damageToObject = collisionResult.getDamageResults();
		int collisonBodyDamage = damageToObject.getBODY();
		assertEquals( (startingObjectBOD - collisonObjectBOD), collisonBodyDamage);
		
		//assert - defender took correct amount of damage
		DamageAmount kdamage = knr.getKnockbackDamage();
		double starting =r.getDefender().getCharasteristic("STUN").getStartingValue();
		double now  =r.getDefender().getCharasteristic("STUN").getCurrentVaue();
		int kbstunDamage = kdamage.getSTUN(); 
		int stunDamage = r.getDamageResults().getSTUN(); 
		assertEquals( (starting - now)- stunDamage, kbstunDamage);
	}
	
	protected void changeModifersAndAssertCorrectModifiersHaveChanged(AttackAdapter individualAttack ) {
		//arrange
		
		//int OCV = attackTarget.getAttackerOCV();
		//int DCV = attackTarget.getDefenderDCV();
		
		
		ToHitModifiers modifiers = individualAttack.getToHitModifiers();
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
		
		int totalMods = 3-3-2-3-2-2;
		
		//only do for non aa attacks)
		if(modifiers.getTotalModiferAmount() >0)
		{
			assertEquals(totalMods, modifiers.getTotalModiferAmount());
		}
	}
	protected void assertCollisionForBothObjects(PhysicalObjectAdapter collidingWith,
			PhysicalObjectAdapter secondCollidingWith, AttackResultAdapter r, String collidingWithName, String secondCollidingWithName) {
		//assert - 2 collisions
		KnockbackResult knr = r.getKnockbackResult();
		ArrayList<CollisionResultAdapter>  collisions = knr.getObstaclesCollidedWith();
		assertEquals(2, collisions.size());		
		
		assertCollidedWithCorrectObjectAndObjectIsDamagedByKnockbackCollision(collidingWith, collisions.get(0),collidingWithName);
		assertCollidedWithCorrectObjectAndObjectIsDamagedByKnockbackCollision(secondCollidingWith, collisions.get(1),secondCollidingWithName);
	}
	
	protected void assertJSONResultEqualsResult(JSONObject resultJSON, AttackResultAdapter result) {
		assertEquals( result.getDefender().getName(), ((JSONObject) resultJSON.get("Defender")).get("Name"));
		
		AttackResultAdapter obs = result.getObstructionDamageResults().get(0);
		JSONArray obj = (JSONArray) resultJSON.get("Obstruction Damage Results");
		JSONObject objc = (JSONObject) ((JSONObject) obj.get(0)).get("Defender");
		assertEquals(obs.getDefender().getName(), objc.get("Name"));
		assert(
				obs.getDefender().getCharacterEffect().getEffects().contains("Partially Destroyed")
				|| obs.getDefender().getCharacterEffect().getEffects().contains("Destroyed")
				|| obs.getDefender().getCharacterEffect().getEffects().contains("Dead"));
		KnockbackResult r = result.getKnockbackResult();
		JSONObject kr = (JSONObject) resultJSON.get("Knockback Result");
		JSONObject kro = (JSONObject) ((JSONArray) kr.get("Collisions")).get(0);
		assertEquals(r.getObstaclesCollidedWith().get(0).getObjectCollidingWith().getName(), ((JSONObject) kro.get("Object Collided With")).get("Name"));
		assertEquals(r.getDistance(), kr.get("Distance"));
	}
	protected void assertCollidedWithCorrectObjectAndObjectIsDamagedByKnockbackCollision(PhysicalObjectAdapter collidingWith,
			CollisionResultAdapter collisionResult,String objectName ) {
		
 		BasicTargetAdapter collidedWith = collisionResult.getObjectCollidingWith();
		assertEquals(objectName, collidedWith.getName());
		assert( collidedWith.getCharacterEffect().getEffects().contains("Partially Destroyed") ||  collidedWith.getCharacterEffect().getEffects().contains("Destroyed"));

		//assert damage marches battleEvent
		DamageAmount damageToObject = collisionResult.getDamageResults();
		int damageFromCollision = damageToObject.getBODY();
			int targetIndex = collisionResult.battleEvent.getActivationInfo().getTargetIndex(collisionResult.getTarget());
		int expectedDamageToObject = collisionResult.battleEvent.getDamageEffect(targetIndex).getTotalAdjustedBodyDamage();
		assertEquals(expectedDamageToObject , damageToObject.getBODY() );
		
		//assert - object took correct damage
		int startingBody = (int) collidingWith.getCharasteristic("BODY").getStartingValue();
		int currentBody = collidedWith.getCharasteristic("BODY").getCurrentVaue();
		int collisonDamageBody = damageToObject.getBODY();
		
		int knockbackDamage = collisionResult.getKnockbackResult().getKnockbackDamage().getBODY();
		if(knockbackDamage==collisonDamageBody)
		{
			knockbackDamage=0;
		}
		assertEquals( (startingBody - currentBody), collisonDamageBody+knockbackDamage);

	}
	
	protected void buildAttackTargetJSON(JSONObject attackJSON, String defenderName, String obstruction, String objectCollidingWith, int collisionDistance) {
		attackJSON.put("Defender", defenderName);
		
		
		JSONArray physicalObjects = new JSONArray();
		attackJSON.put("Obstructions", physicalObjects);
		physicalObjects.add(obstruction);
		
		JSONObject toHitModifiers = new JSONObject();
		attackJSON.put("To Hit Modifiers", toHitModifiers);
		
		toHitModifiers.put("Surprise Move", -3);		
		toHitModifiers.put("From Behind", true);
		toHitModifiers.put("Defender Entangled", true);
		
		attackJSON.put("Targeting Sense", "Normal Hearing");
		
		JSONArray potentialKnocbackCollisions = new JSONArray();
		JSONObject knockbackCollision = new JSONObject();
		knockbackCollision.put("Collision Object", objectCollidingWith);

		knockbackCollision.put("Collision Distance", collisionDistance);
		potentialKnocbackCollisions.add(knockbackCollision);
		attackJSON.put("Potential Knockback Collisions", potentialKnocbackCollisions);
	}
	protected void assertAttackHit(AttackAdapter attackTarget, AttackResultAdapter result) {
		assertEquals(true, result.getAttackHit());
		assertEquals(result.getDefender().getName(), attackTarget.getDefender().getName());
		assertEquals(true, result.getAttackHit());
	}
	protected void assertAttackDamage(AttackResultAdapter result, CharacterAdaptor defender) {
		int now  = defender.getCharasteristic("STUN").getCurrentVaue();
		DamageAmount knockbackDamageAmount = result.getKnockbackResult().getKnockbackDamage();
		int kbstun = knockbackDamageAmount.getSTUN(); 
		double starting =defender.getCharasteristic("STUN").getStartingValue();
		int stunDamage = result.getDamageResults().getSTUN(); 
		
		assertEquals( starting - now-kbstun, stunDamage);
	}
	protected void assertObstructionAbsorbsAttackAndIsDestroyed(AttackResultAdapter result, String objectName) {
		ArrayList<AttackResultAdapter> obtacleDamageresults = result.getObstructionDamageResults();
		assertEquals(1, obtacleDamageresults.size());
		
		AttackResultAdapter doorResult = obtacleDamageresults.get(0);
		assertEquals(objectName, doorResult.getDefender().getName());
		ArrayList<String> effects = doorResult.getDefenderEffects().getEffects();
		String actual = effects.get(0);
		assert(actual.equals("Partially Destroyed") 
				|| actual.equals("Dead") ||actual.equals("Destroyed" ));
		assert(doorResult.getDamageResults().getBODY() > doorResult.getDefender().getCharasteristic("BODY").getStartingValue());
	}

	protected void closeAllRosters() {
		Battle.currentBattle.setStopped(true);
		roster.removeAll();
	}
	
	@AfterAll
	public static void HeroCombatSimulatorIsShutDown() {
		if(Battle.currentBattle.findRoster("Obstructions")!=null){
			Roster r = Battle.currentBattle.findRoster("Obstructions");
			Battle.currentBattle.removeRoster(r);
		}
		AttackTreePanel.Panel.cancelAttack();
		Battle.currentBattle.setStopped(true);
		
	}
	
}
