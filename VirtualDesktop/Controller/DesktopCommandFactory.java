package VirtualDesktop.Controller;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import VirtualDesktop.Ability.MoveCommand;
import VirtualDesktop.Ability.SimpleAbilityCommand;
import VirtualDesktop.Actions.AbortNextActionCommand;
import VirtualDesktop.Actions.ActivateHeldCharacterCommand;
import VirtualDesktop.Actions.CombatControlAction;
import VirtualDesktop.Attack.AttackTargetCommand;
import VirtualDesktop.Attack.ConfirmAttack;
import VirtualDesktop.Attack.MoveAndAttackCommand;
import VirtualDesktop.Attack.MoveThrough.AttackMoveThroughTargetCommand;
import VirtualDesktop.Attack.Sweep.AttackSweepTargetCommand;
import VirtualDesktop.Roster.RosterSynchCommand;

public class DesktopCommandFactory {
	private Map<String, AbstractDesktopCommand> commands = new HashMap<String, AbstractDesktopCommand>();
	
	public static AbstractDesktopCommand GetCommand(JSONObject message) {
		String type = (String) message.get("Type");
		if(type.equals("AttackSingleTarget")) {
			return new AttackTargetCommand();
		}
		if(type.equals("RosterSynchronization")){
			return new RosterSynchCommand();
		}
		if(type.equals("CombatAction"))  {
			return new CombatControlAction();
		}
		if(type.equals("Sweep") || type.equals("Rapid Fire")) {
			return new AttackSweepTargetCommand();
		}
		
		if(type.equals("AreaEffectTargets")) {
			return new AttackTargetCommand();
		}
		
		if(type.equals("AutofireTargets") ){
			return new AttackTargetCommand();		}
		if(type.equals("AbortNextAction") ){
			return new AbortNextActionCommand();
		}
		if(type.equals("ActivateHeldCharacter") ){
			return new ActivateHeldCharacterCommand();
		}
		
		if(type.equals("SimpleAbility") ){
			return new SimpleAbilityCommand();
		}
		if(type.equals("Movement") ){
			return new MoveCommand();
		}
		if(type.equals("MoveThrough") || type.equals("MoveBy") ){
			return new AttackMoveThroughTargetCommand ();
		}
		
		if(type.equals("MoveAndAttack")){
			return new MoveAndAttackCommand();
		}
		if(type.equals("ConfirmAttack") ){
			return new ConfirmAttack();
		}
		if(type.equals("Attack") ){
			return new AttackTargetCommand();
		}
		if(type.equals("AreaEffect") ){
			return new AttackTargetCommand();
		}
		
		return null;
		
	}
	
	
		

}
