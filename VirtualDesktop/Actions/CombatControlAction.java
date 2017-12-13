package VirtualDesktop.Actions;

import org.json.simple.JSONObject;

import VirtualDesktop.Character.CharacterAdaptor;
import VirtualDesktop.Controller.AbstractDesktopCommand;
import champions.Battle;
import champions.BattleEvent;
import champions.Chronometer;
import champions.ConfigureBattleBattleEvent;

public class CombatControlAction extends AbstractDesktopCommand {

	@Override
	public void ExecuteDesktopEventOnSimulatorBasedOnMessageType(JSONObject message, CharacterAdaptor character) throws Exception {
		String action = (String) message.get("Action");
		BattleEvent be=null;
		if(action.equals("Start")) {	
			 Battle.currentBattle.setStopped(false);
	         be = new ConfigureBattleBattleEvent();
		}
		if(action.equals("Force Next Segment")) {
			Chronometer stopTime = (Chronometer)Battle.currentBattle.getTime().clone();
            stopTime.setTime( stopTime.getTime() + 1 );
            be = new BattleEvent(stopTime);
            be.setForcedAdvance(true);
		}
		if(action.equals("Force Next Character")) {
			if ( Battle.currentBattle.isStopped() ) {
                Battle.currentBattle.setStopped(false);
            }
            be = new BattleEvent(BattleEvent.ADVANCE_TIME, true);

		}
		if(action.equals("New Battle")) {
			Battle b = new Battle();
	        b.setCurrentBattle(b);
		}
		Battle.currentBattle.addEvent(be);
	}

}
