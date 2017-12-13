package VirtualDesktop.Roster;

import java.io.File;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import VirtualDesktop.Controller.GLOBALS;
import champions.Battle;
import champions.Roster;

public class SimulatorRoster {

	public void SynchRosters(JSONArray rosterNames) {

		for (int i = 0; i < rosterNames.size(); i++) {
			
    	  String rName = (String)rosterNames.get(i);
    	  Roster roster;
		try {
			String name = GLOBALS.SIMULATOR_CHARACTER_PATH + rName +"\\"+ rName +".rst";
			roster = Roster.open(new File(name));
			Battle.currentBattle.addRoster(roster);
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
    	
		}
	}
}
