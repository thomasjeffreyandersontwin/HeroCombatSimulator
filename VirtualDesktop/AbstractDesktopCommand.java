package VirtualDesktop;

import org.json.simple.JSONObject;
import champions.Ability;
import champions.Battle;
import champions.BattleEvent;

public abstract class AbstractDesktopCommand {
	
	public void ExecuteDesktopEventOnSimulator(JSONObject message) throws Exception{
		
		CombatSimulatorCharacter character = CombatSimulatorCharacter.GetActiveCharacter();	
    	ExecuteDesktopEventOnSimulatorBasedOnMessageType(message, character);
	}
	
	public abstract void ExecuteDesktopEventOnSimulatorBasedOnMessageType(JSONObject message, CombatSimulatorCharacter character) throws Exception;
}




