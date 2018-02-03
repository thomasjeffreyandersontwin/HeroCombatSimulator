package VirtualDesktop.Attack.Autofire;

import org.json.simple.JSONObject;

import VirtualDesktop.Attack.PhysicalObjectAdapter;
import VirtualDesktop.Attack.AreaEffect.AreaEffectAttackAdapter;
import VirtualDesktop.Attack.MultiAttack.MultiAttackAdapter;
import VirtualDesktop.Attack.MultiAttack.MultiAttackAdapterTest;
import VirtualDesktop.Character.CharacterAdaptor;

public class AutofireAttackAdapterTest extends MultiAttackAdapterTest{
	
	

	@Override
	protected MultiAttackAdapter activateAndSetupAttack() {
		AutofireAttackAdapter attack = (AutofireAttackAdapter) attacker.getAbilityWrapper("Rapid Punch");
		
		attack.activateAbility();
		attack.SetAutoFireSprayMode(true);
		attack.SetAutoFireShots(3);
		return attack;
	}


	@Override
	protected CharacterAdaptor createAttacker() {
		 attacker = new CharacterAdaptor(System.getProperty("user.dir") + "\\eventinfo\\testdata\\Off-Road\\Off-Road");
		return attacker;
	}


	@Override
	protected JSONObject buildBaseAttackJSON() {
		JSONObject attackJSON = new JSONObject();
		attackJSON.put("Ability" , "Rapid Punch");
		attackJSON.put("Spray Fire", true);
		attackJSON.put("Width", 1);
		attackJSON.put("Shots", 3);
		
		return attackJSON;
	}
	
		
}
