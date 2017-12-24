package VirtualDesktop.Attack.AreaEffect;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import VirtualDesktop.Attack.SingleAttack.AttackSingleTargetCommand;
import VirtualDesktop.Attack.SingleAttack.SingleAttackAdapter;

public class AttackMultipleTargetsCommand extends  AttackSingleTargetCommand{

		
	protected void EnterAttackForAllTargets(JSONObject message, MultAttackAdapter attack) {
		JSONArray targets = (JSONArray) message.get("Targets");
		for (int i = 0; i < targets.size(); i++) {
			JSONObject targetObject =(JSONObject) targets.get(i);
			String targetName =  (String)targetObject.get("Target");
			InvokeSingleAttackWithoutKnockback(targetObject, attack, targetName);
			
		}
		attack.StartSelectingTargets();
		EnterKnockbackForAllTargets(message, attack);
	}
	
	protected void EnterKnockbackForAllTargets(JSONObject message,MultAttackAdapter attack) {
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
		AttackSingleTargetCommand.AttackInProgress=null;
	}
	
	public void EnterKnockbackForSpecificTarget(MultAttackAdapter attack, JSONObject message, int i) {
		
		JSONObject potCol = (JSONObject)message.get("Potential Collision");
		if (potCol!=null) {
			Long distanceToCollision = (Long) potCol.get("Distance From Target");
			String knockbackTargetName = (String) potCol.get("Obstacle");
			if(knockbackTargetName!=null) {
				int knockbackDistance = attack.getKnockbackDistanceForTarget(i);
				if(knockbackDistance  >=distanceToCollision ) 
				{
					attack.EnterKnockbackForSpecificTarget(knockbackTargetName,i);
				}	
			}
		}
		
	}
	
}
