package VirtualDesktop.Attack;

import org.json.simple.JSONObject;

import VirtualDesktop.AbstractDesktopCommand;
import VirtualDesktop.CombatSimulatorCharacter;

public class KnockbackCollisionSingleTargetCommand extends AbstractDesktopCommand {

	
	public void EnterKnockback(SimulatorSingleAttack attack, JSONObject message) {
		String knockbackTargetName = (String) message.get("Knockback Target");
		if(attack==null) {
			attack = new SimulatorSingleAttack(null, null);
		}
		if(knockbackTargetName!=null){
			attack.StartEnteringKnockback();
			//attack.SetTargetByName(knockbackTargetName); 
			attack.SetKnockBackTargetByName(knockbackTargetName);
			
		}
		
	}

	@Override
	public void ExecuteDesktopEventOnSimulatorBasedOnMessageType(JSONObject message, CombatSimulatorCharacter character)
			throws Exception {
		SimulatorSingleAttack attack = AttackSingleTargetCommand.LastAttack;
		EnterKnockback(attack, message);
		if(attack!=null) {
			//attack.ConfirmAttack();
			//attack.ConfirmAttack();
		}
		attack.ShowSummary();

		attack.Export();
		AttackSingleTargetCommand.LastAttack=null;
	}
}
