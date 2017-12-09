package VirtualDesktop.Attack;

import org.json.simple.JSONObject;

import VirtualDesktop.AbstractDesktopCommand;
import VirtualDesktop.CombatSimulatorCharacter;
import VirtualDesktop.DesktopCommandFactory;
import VirtualDesktop.Ability.MoveCommand;

public class MoveAndAttackCommand extends AbstractDesktopCommand {

	@Override
	public void ExecuteDesktopEventOnSimulatorBasedOnMessageType(JSONObject message, CombatSimulatorCharacter character)
			throws Exception {

		JSONObject move = (JSONObject) message.get("MovementAbility");
		
		MoveCommand moveCommand  = new MoveCommand();
		moveCommand.ExecuteDesktopEventOnSimulator(move);
		
		
		JSONObject attack =  (JSONObject) message.get("AttackAbility");
		AbstractDesktopCommand command =DesktopCommandFactory.GetCommand(attack);
		command.ExecuteDesktopEventOnSimulator(attack);
				

	}

}
