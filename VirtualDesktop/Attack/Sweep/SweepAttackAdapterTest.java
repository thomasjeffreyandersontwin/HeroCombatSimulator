package VirtualDesktop.Attack.Sweep;

import static org.junit.jupiter.api.Assertions.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;

import VirtualDesktop.Attack.AttackAdapter;
import VirtualDesktop.Attack.Autofire.AutofireAttackAdapter;
import VirtualDesktop.Attack.MultiAttack.MultiAttackAdapter;
import VirtualDesktop.Attack.MultiAttack.MultiAttackAdapterTest;
import VirtualDesktop.Character.CharacterAdaptor;
import champions.Target;
import champions.attackTree.AttackTreePanel;
import champions.attackTree.DefaultAttackTreeNode;
import champions.attackTree.KnockbackTargetNode;
import champions.attackTree.ProcessActivateRootNode;

class SweepAttackAdapterTest extends MultiAttackAdapterTest {



	@Override
	protected CharacterAdaptor createAttacker() {
		attacker = new CharacterAdaptor(System.getProperty("user.dir") + "\\eventinfo\\testdata\\Blind Justice\\Blind Justice");
		return attacker;
	}

	@Override
	protected MultiAttackAdapter activateAndSetupAttack() {
		SweepAttackAdapter attack = (SweepAttackAdapter) attacker.getAbilityWrapper("Sweep");
		attack.activateAbility();
		attack.addAttackAbility((AttackAdapter)attacker.getAbilityWrapper("Defensive Strike"));
		attack.addAttackAbility((AttackAdapter)attacker.getAbilityWrapper("Offensive Strike"));
		attack.addAttackAbility((AttackAdapter)attacker.getAbilityWrapper("Martial Strike"));
		//attack.addAttackAbility((AttackAdapter)attacker.getAbilityWrapper("Strike"));
		attack.finishAddingAttacks();
		return attack;
		
	}
	
	

	@Override
	protected JSONObject buildBaseAttackJSON() {
		JSONObject attackJSON = new JSONObject();
		attackJSON.put("Ability", "Sweep");
		return attackJSON;
	}
	
	
	@Override
	protected JSONObject buildAttackJSON() {
		JSONObject attackJSON = buildBaseAttackJSON();
		JSONArray attackTargets = new JSONArray();
		attackJSON.put("Attack Targets",attackTargets);
		
		JSONObject attackTarget = new JSONObject();
		buildAttackTargetJSON(attackTarget, "Ogun", "Interior wood door","Magnetic lock", 2);
		attackTarget.put("Ability", "Defensive Strike");
		attackTargets.add(attackTarget);
		
		attackTarget = new JSONObject();
		buildAttackTargetJSON(attackTarget, "Spyder", "Barrel","Bicycle", 3);
		attackTarget.put("Ability","Offensive Strike");
		attackTargets.add(attackTarget);
		
		attackTarget = new JSONObject();
		buildAttackTargetJSON(attackTarget, "Saviour", "Chamber pot","Padlock", 4);
		attackTarget.put("Ability","Martial Strike");
		attackTargets.add(attackTarget);
		return attackJSON;
	}
	
	void characterIsActivated_AbilitiesIndicateIfTheyAreSweepable() {}
	
	@Test
	void defenderIsAttackedBySweepWithDifferentAttacks_eachIndependantAttackResultReturnsCorrectPower() 
	{
		SweepAttackAdapter attack = (SweepAttackAdapter) targetAllDefendersWithAttack();

		AttackAdapter singleAttack =(AttackAdapter) attack.getIndividualAttack(0);
		assertEquals(singleAttack.UnderlyingAbility,  attacker.getAbilityWrapper("Defensive Strike").UnderlyingAbility);
	
		singleAttack = (AttackAdapter) attack.getIndividualAttack(1);
		assertEquals(singleAttack.UnderlyingAbility, attacker.getAbilityWrapper("Offensive Strike").UnderlyingAbility);

		singleAttack = (AttackAdapter) attack.getIndividualAttack(2);
		assertEquals(singleAttack.UnderlyingAbility, attacker.getAbilityWrapper("Martial Strike").UnderlyingAbility);
		
		singleAttack = (AttackAdapter) attack.getIndividualAttack(0);
		assertEquals(singleAttack.getDefender().getName(), defender1.getName());
		
		singleAttack = (AttackAdapter) attack.getIndividualAttack(1);
		assertEquals(singleAttack.getDefender().getName(), defender2.getName());
		
		singleAttack = (AttackAdapter) attack.getIndividualAttack(2);
		assertEquals(singleAttack.getDefender().getName(), defender3.getName());

	}
}
