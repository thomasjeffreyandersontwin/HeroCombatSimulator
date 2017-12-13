package VirtualDesktop.Controller;

import org.json.simple.JSONObject;

import VirtualDesktop.Character.CharacterAdaptor;
import champions.Ability;
import champions.Battle;
import champions.BattleEvent;

public abstract class AbstractDesktopCommand {
	
	public void ExecuteDesktopEventOnSimulator(JSONObject message) throws Exception{
		
		CharacterAdaptor character = CharacterAdaptor.GetActiveCharacter();	
    	ExecuteDesktopEventOnSimulatorBasedOnMessageType(message, character);
	}
	
	public abstract void ExecuteDesktopEventOnSimulatorBasedOnMessageType(JSONObject message, CharacterAdaptor character) throws Exception;
}




