package VirtualDesktop.Ability;

import org.json.simple.JSONObject;

import VirtualDesktop.Actions.AbortNextActionCommand;
import VirtualDesktop.Character.CharacterAdaptor;

public class MoveCommand extends SimpleAbilityCommand {
	@Override
	public void ExecuteDesktopEventOnSimulatorBasedOnMessageType(JSONObject message, CharacterAdaptor character)
			throws Exception {
		String movementName = (String)message.get("Movement");
		Long distance= (Long)message.get("Distance");
		character.MoveByName(movementName, distance.intValue());
		
		if(AbortNextActionCommand.isAbortActivated==true) {
			new AbortNextActionCommand().ResumeInteruptedAttack();
		}
	}
}
