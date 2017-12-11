package VirtualDesktop.Attack.Sweep;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import VirtualDesktop.CombatSimulatorCharacter;
import VirtualDesktop.Attack.AreaEffect.KnockbackMultiTargetsCommand;
import VirtualDesktop.SingleAttack.AttackSingleTargetCommand;

public class KnockbackSweepTargetsCommand extends KnockbackMultiTargetsCommand{
	@Override
	public void ExecuteDesktopEventOnSimulatorBasedOnMessageType(JSONObject message, CombatSimulatorCharacter character)
			throws Exception {
		SimulatorSweepAttack attack = (SimulatorSweepAttack) AttackSingleTargetCommand.LastAttack;
		
		JSONArray targets = (JSONArray) message.get("Targets");
		for (int i = 0; i < targets.size(); i++) {
			JSONObject targetObject =(JSONObject) targets.get(i);
			String targetName =  (String)targetObject.get("Target");
			EnterKnockbackForSpecificTarget(attack,  targetObject,i);
		}
		
		if(attack!=null) {
			//attack.ConfirmAttack();
//			attack.ConfirmAttack();
		}
		AttackSingleTargetCommand.LastAttack=null;
		
	}
	
	public void EnterKnockbackForSpecificTarget(SimulatorSweepAttack attack, JSONObject message, int i) {
		String knockbackTargetName = (String) message.get("Knockback Target");
		if(knockbackTargetName!=null) {
			attack.EnterKnockbackForSpecificTarget(knockbackTargetName,i);
			
		}
		
	}
}
