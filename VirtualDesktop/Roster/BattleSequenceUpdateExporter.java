package VirtualDesktop.Roster;

import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import VirtualDesktop.Controller.GLOBALS;
import champions.BattleSequence;
import champions.Character;
import champions.Target;

public class BattleSequenceUpdateExporter {
	public void ExportBattleSequence(BattleSequence sequence, String SequenceName) {
		try {
			
	        JSONArray combatants = new JSONArray();
	        JSONObject onDeckOutput = new JSONObject();
	        onDeckOutput.put("Combatants", combatants);
	        for (int i=0;i < sequence.size(); i++) {
	        	JSONObject combatant = new JSONObject();
	        	if(sequence.get(i).getTarget().getClass() == Character.class) {
	        	combatant.put("Character", ((Character)sequence.get(i).getTarget()).getName());
	        	combatant.put("Phase",sequence.get(i).getTime().toString());
	        	combatants.add(combatant);
	        	}
	        }
        
	        FileWriter writer;
		
			writer = new FileWriter(GLOBALS.EXPORT_PATH  +SequenceName+".info");
			writer.write(onDeckOutput.toJSONString());
	        writer.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
