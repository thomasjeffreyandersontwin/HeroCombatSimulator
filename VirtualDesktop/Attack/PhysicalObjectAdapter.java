package VirtualDesktop.Attack;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONStreamAware;

import champions.Battle;
import champions.ObjectTarget;
import champions.PADRoster;
import champions.Target;
import champions.TargetList;

public class PhysicalObjectAdapter extends BasicTargetAdapter {

	public PhysicalObjectAdapter(Target t) {
		this.target = t;
	}
	
	public PhysicalObjectAdapter(String name) {
		Battle battle = Battle.currentBattle;
		//battle.startBattle();
		TargetList targets = battle.getTargetList(true);
		champions.Target target = targets.getTarget(name, true);
		if(target==null) {
			targets = battle.getSpecialTargetList();
			target = targets.getTarget(name, true);
		}
		if(target==null) 
		{
			TargetList tl = PADRoster.createObjectPresets();
			target = tl.getTarget(name, true);
		}
		if(target==null) 
		{
			target = loadFromFile(name,".tgt");
		}
		if(target==null)
		{
			target  = new ObjectTarget(name, 100, "Unliving", "Other", 5, 5, true);
		}
		this.target =target;
	}

	public PhysicalObjectAdapter loadFromFile(String obstructionName) {
		Target t = loadFromFile(obstructionName,".tgt");
		return new PhysicalObjectAdapter(t);
	}

	public static PhysicalObjectAdapter newObjectFromPresests(String name) {
		TargetList tl = PADRoster.createObjectPresets();
		return new PhysicalObjectAdapter(tl.getTarget(name, true));
	}

	public JSONObject exportToJSON() {
		JSONObject ex = new JSONObject();
		ex.put("Name", getName());
		
		JSONObject bodyJSO = getCharasteristic("BODY").exportToJSON();
		ex.put("BODY",bodyJSO );
		
		JSONArray damageEffects = getCharacterEffect().exportToJSON();
		ex.put("Effects", damageEffects  );
		
		return ex;
	}
}
;