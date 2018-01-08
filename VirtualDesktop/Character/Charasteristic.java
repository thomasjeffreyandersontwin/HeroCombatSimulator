package VirtualDesktop.Character;

import org.json.simple.JSONObject;

import champions.Target;

public class Charasteristic {

	private Target target;
	private String name;
	public Charasteristic(String name, Target target) {
		this.target = target;
		this.name = name;
	}

	public int getCurrentVaue() {
		return target.getCharacteristic(name).getCurrentStat() ;
	}

	public double getStartingVaue() {
		if(target.getCharacteristic(name)!=null) {
			return target.getCharacteristic(name).getAdjustedStat() ;
		}
		return -999d;
	}
	
	public String getName() {
		return name;
	}

	public JSONObject exportToJSON() {
		JSONObject ex = new JSONObject();
		ex.put("Name", getName());
		if(getStartingVaue() != -999d){
			ex.put("Starting", getStartingVaue());
			ex.put("Currrent", getCurrentVaue());
		}
		return ex;
	}

}
