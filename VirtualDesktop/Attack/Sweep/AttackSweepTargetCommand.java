package VirtualDesktop.Attack.Sweep;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import VirtualDesktop.Attack.AttackSingleTargetCommand;
import VirtualDesktop.Character.CharacterAdaptor;
import champions.Ability;
import champions.Battle;
import champions.attackTree.SweepSetupPanel;

public class AttackSweepTargetCommand extends AttackSingleTargetCommand {

	@Override
	public void ExecuteDesktopEventOnSimulatorBasedOnMessageType(JSONObject message,
			CharacterAdaptor character) throws Exception  {
		character.ActivateAbilityByName((String) message.get("Type"));
		SimulatorSweepAttack attack = (SimulatorSweepAttack) character.ActiveAbility;
		
		JSONArray abilities = (JSONArray)message.get("Abilities");
		for (int i = 0; i < abilities.size(); i++) {
			Thread.sleep(500);
			JSONObject abilityMsg = (JSONObject)abilities.get(i);
			String abilityName = (String) abilityMsg.get("Ability");
			attack.AddAbility(abilityName);
		}
		attack.ConfirmAttack();
		
		
		for (int i = 0; i < abilities.size(); i++) {
			JSONObject abilityMsg = (JSONObject)abilities.get(i);
			String targetName = (String) abilityMsg.get("Target");
			attack.SelectTargetForSpecificAttack(targetName, i);
			
			//EnterAttackParameters(message, attack);
//to do fix	EnterToHitParameters(attack, message);
			
//			AddObstructions(message, attack); //to do fix	
			
		
		}
		//LastAttack = attack;
	}
	
	
	public void SelectTargetForSpecificAttack(SimulatorSweepAttack attack, JSONObject message, int i) {
		String knockbackTargetName = (String) message.get("Knockback Target");
		if(knockbackTargetName!=null) {
			attack.SelectTargetForSpecificAttack(knockbackTargetName,i);
			
		}
		
	}
}
