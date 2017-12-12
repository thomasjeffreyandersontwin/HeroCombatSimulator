package VirtualDesktop.Attack.Autofire;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import VirtualDesktop.Attack.AreaEffect.AttackMultipleTargetsCommand;
import VirtualDesktop.Character.CharacterAdaptor;


//eg {"Type":"AutofireTargets","Ability":"Flurry","Shots":4, "Width:5, Spray:True, "Targets":[{Target:"Ogun"}, {Target:"Spyder"}, {Target:"Hex"}]}
public class AttackAutofireTargetsCommand extends AttackMultipleTargetsCommand{

	@Override
	public void ExecuteDesktopEventOnSimulatorBasedOnMessageType(JSONObject message, CharacterAdaptor character)
			throws Exception {
		int autofireShots =  ((JSONArray) message.get("Targets")).size();
		long autofireWidth = (long) message.get("Width");
		boolean autofireSprayMode = (boolean) message.get("Spray");
		
		//activate ability
		String abilityName = (String)message.get("Ability");
		character.ActivateAbilityByName(abilityName);
		
		
		//enter autofire parameters
		SimulatorAutofireAttack attack =  (SimulatorAutofireAttack)character.ActiveAbility;
		EnterAttackParameters(message, attack);
		//attack.EnterAttackParameters();
		attack.SetAutoFireSprayMode(true);
		attack.SetAutoFireShots((int)autofireShots);
		attack.SetAutoFireWidth((int)autofireWidth);

		EnterAttackForAllTargets(message, attack);
		
		//attack.ConfirmAttack();
		
		

	}

}
