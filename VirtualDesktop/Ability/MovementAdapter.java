package VirtualDesktop.Ability;

import org.json.simple.JSONObject;

import VirtualDesktop.Character.CharacterAdaptor;
import champions.attackTree.MovementDistancePanel;

public class MovementAdapter extends AbilityWrapper {

	public int Distance;

	public MovementAdapter(String name, CharacterAdaptor character) {
		super(name, character);
		// TODO Auto-generated constructor stub
	}
	
	public void setDistance(int distance) {
		this.Distance= distance;
		MovementDistancePanel.defaultPanel.setDistance(distance);
		
		//MovementDistancePanel.defaultPanel.setFullMoveAllowed(false);
		
		
	}
	
	public JSONObject toJSOObject() {
		JSONObject jso = new JSONObject();
		jso.put("Name", this.Name);
		jso.put("Distancce", this.Distance);	
		return jso;
	}
}
