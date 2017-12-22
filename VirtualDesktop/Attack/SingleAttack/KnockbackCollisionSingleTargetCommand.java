package VirtualDesktop.Attack.SingleAttack;

import org.json.simple.JSONObject;

import VirtualDesktop.Attack.SingleAttack.AttackSingleTargetCommand;
import VirtualDesktop.Character.CharacterAdaptor;
import VirtualDesktop.Controller.AbstractDesktopCommand;
import champions.Ability;
import champions.Battle;
import champions.BattleEngine;
import champions.BattleEvent;
import champions.attackTree.SelectTargetPanel;

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
	public void ExecuteDesktopEventOnSimulatorBasedOnMessageType(JSONObject message, CharacterAdaptor character)
			throws Exception {
		SimulatorSingleAttack attack = AttackSingleTargetCommand.LastAttack;

		
		EnterKnockback(attack, message);
		if(attack!=null) {
			//attack.ConfirmAttack();
			//attack.ConfirmAttack();
		}
		else {
			attack = new SimulatorSingleAttack(null, null);
		}
		attack.ShowSummary();

		attack.Export();
		AttackSingleTargetCommand.LastAttack=null;
	}
}
