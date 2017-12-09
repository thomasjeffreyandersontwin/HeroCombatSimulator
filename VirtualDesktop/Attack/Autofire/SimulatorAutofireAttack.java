package VirtualDesktop.Attack.Autofire;

import java.util.HashMap;
import java.util.Map;

import VirtualDesktop.CombatSimulatorCharacter;
import VirtualDesktop.Attack.AreaEffect.SimulatorMultiAttack;
import champions.Battle;
import champions.TargetList;
import champions.event.PADValueEvent;

public class SimulatorAutofireAttack extends SimulatorMultiAttack {

	private int numberOfShots;
	private int widthOfAttack;
	
	private boolean spray;
	
	
	public SimulatorAutofireAttack(String name, CombatSimulatorCharacter character) {
		super(name, character);
		// TODO Auto-generated constructor stub
	}



	public void SetAutoFireSprayMode(boolean b) {	
		attackParmetersPanel.sprayButton.setSelected(b);
		attackParmetersPanel.sprayButtonActionPerformed(null);
	}



	public void SetAutoFireShots(int autofireShots) {
		attackParmetersPanel.autofireShots.setValue(autofireShots);
		PADValueEvent evt = new PADValueEvent(attackParmetersPanel.autofireShots, 
				"autofireShots", autofireShots, attackParmetersPanel.autofireShots.getValue());
		attackParmetersPanel.PADValueChanging(evt);		
	}



	public void SetAutoFireWidth(int autofireWidth) {
		attackParmetersPanel.autofireWidth.setValue(autofireWidth);
		PADValueEvent evt = new PADValueEvent(attackParmetersPanel.autofireShots, 
				"autofireWidth", autofireWidth, attackParmetersPanel.autofireWidth.getValue());
		attackParmetersPanel.PADValueChanging(evt);	
		
	}




}
