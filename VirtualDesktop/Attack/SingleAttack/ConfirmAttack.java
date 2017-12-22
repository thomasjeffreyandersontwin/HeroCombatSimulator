package VirtualDesktop.Attack.SingleAttack;

import org.json.simple.JSONObject;

import VirtualDesktop.Character.CharacterAdaptor;
import VirtualDesktop.Controller.AbstractDesktopCommand;




public class ConfirmAttack extends AbstractDesktopCommand {



	public ConfirmAttack() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void ExecuteDesktopEventOnSimulatorBasedOnMessageType(JSONObject message, CharacterAdaptor character)
			throws Exception {
		String status = (String) message.get("Status");
		
		SimulatorSingleAttack attack = AttackSingleTargetCommand.LastAttack;
		if(attack==null) {
			attack = new SimulatorSingleAttack(null, null);
		}
		if(status.equals("Confirm")) {
			attack.ConfirmAttack();
			attack.ConfirmAttack();
		}
		else
			attack.CancelAttack();
		AttackSingleTargetCommand.LastAttack = null;
		
	}

	

}
