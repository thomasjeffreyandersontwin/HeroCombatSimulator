package VirtualDesktop;

import org.json.simple.JSONObject;

import VirtualDesktop.SingleAttack.AttackSingleTargetCommand;
import champions.Battle;
import champions.BattleEvent;
import champions.CombatState;
import champions.Target;
import champions.attackTree.AttackTreePanel;
import champions.exception.BattleEventException;

public class AbortNextActionCommand extends AbstractDesktopCommand {
	public static Boolean isAbortActivated=false;
	public static JSONObject lastAttackMessage=null;
	@Override
	public void ExecuteDesktopEventOnSimulatorBasedOnMessageType(JSONObject message, CombatSimulatorCharacter character)
			throws Exception {
		 isAbortActivated= true;
		
		 lastAttackMessage = AttackSingleTargetCommand.lastMessage;
		
		
		AttackTreePanel.Panel.cancelAttack();
		String characterName = (String)message.get("Character");
		character= new CombatSimulatorCharacter(characterName);;
		
		Target target = character.UnderlyingCharacter;
		BattleEvent be = new BattleEvent(BattleEvent.ACTIVE_TARGET,target );
        be.addCombatStateEvent(target, target.getCombatState(), CombatState.STATE_ABORTING );
        target.setCombatState( CombatState.STATE_ABORTING );
        Battle.currentBattle.addEvent(be); 

	}
	
	public void ResumeInteruptedAttack() throws Exception{
		if(lastAttackMessage!=null) {
			AbstractDesktopCommand command = DesktopCommandFactory.GetCommand(lastAttackMessage);
			command.ExecuteDesktopEventOnSimulator(lastAttackMessage);
			isAbortActivated=false;
			lastAttackMessage= null;
		}
	}

}
