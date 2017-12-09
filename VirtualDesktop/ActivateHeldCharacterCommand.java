package VirtualDesktop;

import org.json.simple.JSONObject;

import champions.Battle;
import champions.BattleEvent;
import champions.BattleSequence;
import champions.BattleSequencePair;
import champions.Character;
import champions.EligibleDockingPanel;
import champions.Target;

public class ActivateHeldCharacterCommand extends AbstractDesktopCommand{

	@Override
	public void ExecuteDesktopEventOnSimulatorBasedOnMessageType(JSONObject message, CombatSimulatorCharacter character)
			throws Exception {
		BattleSequence combatants = EligibleDockingPanel.eligibleModel.eligibleCombatants;
		for(int i=0;i<EligibleDockingPanel.eligibleModel.eligibleCombatants.size(); i++) {
			BattleSequencePair pair =combatants.get(i);
			Target c =(Target) pair.getTarget();
			if(c.getName().equals(message.get("Character"))) {
				BattleEvent be = new BattleEvent(BattleEvent.ACTIVE_TARGET, c);
				Battle.currentBattle.addEvent(be);
				return;
			}
		}
	}
}
