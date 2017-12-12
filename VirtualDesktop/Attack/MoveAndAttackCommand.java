package VirtualDesktop.Attack;

import org.json.simple.JSONObject;

import VirtualDesktop.Ability.MoveCommand;
import VirtualDesktop.Character.CharacterAdaptor;
import VirtualDesktop.Controller.AbstractDesktopCommand;
import VirtualDesktop.Controller.DesktopCommandFactory;

public class MoveAndAttackCommand extends AbstractDesktopCommand {

	@Override
	public void ExecuteDesktopEventOnSimulatorBasedOnMessageType(JSONObject message, CharacterAdaptor character)
			throws Exception {

		JSONObject move = (JSONObject) message.get("MovementAbility");
		
		MoveCommand moveCommand  = new MoveCommand();
		moveCommand.ExecuteDesktopEventOnSimulator(move);
		
		
		JSONObject attack =  (JSONObject) message.get("AttackAbility");
		AbstractDesktopCommand command =DesktopCommandFactory.GetCommand(attack);
		command.ExecuteDesktopEventOnSimulator(attack);
				

	}

}
