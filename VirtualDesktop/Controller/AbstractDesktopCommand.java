package VirtualDesktop.Controller;

import org.json.simple.JSONObject;

import VirtualDesktop.Character.CharacterAdaptor;
import champions.Ability;
import champions.Battle;
import champions.BattleEvent;

public abstract class AbstractDesktopCommand {
	
	public static String Token;

	public void ExecuteDesktopEventOnSimulator(JSONObject message) throws Exception{
		
		CharacterAdaptor character = CharacterAdaptor.GetActiveCharacter();
		String abilityName = (String)message.get("Ability");
		String token = (String)message.get("Token");
		if(token.equals(Token) || token !=null && Token==null) {
			Token = token;
		
			ExecuteDesktopEventOnSimulatorBasedOnMessageType(message, character);
		}
	}
	
	public abstract void ExecuteDesktopEventOnSimulatorBasedOnMessageType(JSONObject message, CharacterAdaptor character) throws Exception;
}




