package VirtualDesktop.Ability;

import org.json.simple.JSONObject;

import VirtualDesktop.AbortNextActionCommand;
import VirtualDesktop.AbstractDesktopCommand;
import VirtualDesktop.CombatSimulatorCharacter;
import champions.Ability;
import champions.Battle;

public class SimpleAbilityCommand extends AbstractDesktopCommand {

	@Override
	public void ExecuteDesktopEventOnSimulatorBasedOnMessageType(JSONObject message, CombatSimulatorCharacter character)
			throws Exception {
		String abilityName = (String)message.get("Ability");
		character.ActivateAbilityByName(abilityName);
		if(AbortNextActionCommand.isAbortActivated==true) {
			new AbortNextActionCommand().ResumeInteruptedAttack();
		}
	}

	
}
