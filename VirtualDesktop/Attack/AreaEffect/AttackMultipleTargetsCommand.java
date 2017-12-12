package VirtualDesktop.Attack.AreaEffect;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import VirtualDesktop.Attack.SingleAttack.AttackSingleTargetCommand;

public class AttackMultipleTargetsCommand extends  AttackSingleTargetCommand{

		
	protected void EnterAttackForAllTargets(JSONObject message, SimulatorMultiAttack attack) {
		JSONArray targets = (JSONArray) message.get("Targets");
		for (int i = 0; i < targets.size(); i++) {
			JSONObject targetObject =(JSONObject) targets.get(i);
			String targetName =  (String)targetObject.get("Target");
			ExecuteAttackOnTarget(targetObject, attack, targetName);
		}
		attack.StartSelectingTargets();
		//attack.ConfirmAttack();
		
	}
	
}
