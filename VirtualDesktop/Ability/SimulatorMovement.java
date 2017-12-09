package VirtualDesktop.Ability;

import VirtualDesktop.CombatSimulatorCharacter;
import champions.attackTree.MovementDistancePanel;

public class SimulatorMovement extends SimulatorAbility {

	public int Distance;

	public SimulatorMovement(String name, CombatSimulatorCharacter character) {
		super(name, character);
		// TODO Auto-generated constructor stub
	}
	
	public void setDistance(int distance) {
		this.Distance= distance;
		MovementDistancePanel.defaultPanel.setDistance(distance);
		
		//MovementDistancePanel.defaultPanel.setFullMoveAllowed(false);
		
		
	}
}
