package VirtualDesktop.Ability;

import org.json.simple.JSONObject;

import VirtualDesktop.Actions.AbortNextActionCommand;
import VirtualDesktop.Character.CharacterAdaptor;
import VirtualDesktop.Controller.AbstractDesktopCommand;
import champions.Ability;
import champions.Battle;

public class SimpleAbilityCommand extends AbstractDesktopCommand {

	@Override
	public void ExecuteDesktopEventOnSimulatorBasedOnMessageType(JSONObject message, CharacterAdaptor character)
			throws Exception {
		String abilityName = (String)message.get("Ability");
		character.ActivateAbilityByName(abilityName);
		if(AbortNextActionCommand.isAbortActivated==true) {
			new AbortNextActionCommand().ResumeInteruptedAttack();
		}
	}

	
}
