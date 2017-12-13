package VirtualDesktop.Roster;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import VirtualDesktop.Character.CharacterAdaptor;
import VirtualDesktop.Controller.AbstractDesktopCommand;

public class RosterSynchCommand extends AbstractDesktopCommand  {

	@Override
	public void ExecuteDesktopEventOnSimulatorBasedOnMessageType(JSONObject message, CharacterAdaptor character)
			throws Exception {
		JSONArray rosters = (JSONArray) message.get("Rosters");
		new SimulatorRoster().SynchRosters(rosters);

		
	}

}
