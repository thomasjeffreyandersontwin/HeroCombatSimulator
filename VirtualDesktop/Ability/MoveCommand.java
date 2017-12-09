package VirtualDesktop.Ability;

import org.json.simple.JSONObject;

import VirtualDesktop.AbortNextActionCommand;
import VirtualDesktop.CombatSimulatorCharacter;

public class MoveCommand extends SimpleAbilityCommand {
	@Override
	public void ExecuteDesktopEventOnSimulatorBasedOnMessageType(JSONObject message, CombatSimulatorCharacter character)
			throws Exception {
		String movementName = (String)message.get("Movement");
		Long distance= (Long)message.get("Distance");
		character.MoveByName(movementName, distance.intValue());
		
		if(AbortNextActionCommand.isAbortActivated==true) {
			new AbortNextActionCommand().ResumeInteruptedAttack();
		}
	}
}
