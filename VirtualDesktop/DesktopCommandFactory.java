package VirtualDesktop;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import VirtualDesktop.Ability.MoveCommand;
import VirtualDesktop.Ability.SimpleAbilityCommand;
import VirtualDesktop.Attack.MoveAndAttackCommand;
import VirtualDesktop.Attack.AreaEffect.AttackAreaEffectTargetsCommand;
import VirtualDesktop.Attack.AreaEffect.KnockbackMultiTargetsCommand;
import VirtualDesktop.Attack.Autofire.AttackAutofireTargetsCommand;
import VirtualDesktop.Attack.MoveThrough.AttackMoveThroughTargetCommand;
import VirtualDesktop.Attack.Sweep.AttackSweepTargetCommand;
import VirtualDesktop.Attack.Sweep.KnockbackSweepTargetsCommand;
import VirtualDesktop.SingleAttack.KnockbackCollisionSingleTargetCommand;
import VirtualDesktop.Roster.RosterSynchCommand;
import VirtualDesktop.SingleAttack.SimulatorSingleAttack;
import VirtualDesktop.SingleAttack.AttackSingleTargetCommand;
import VirtualDesktop.SingleAttack.ConfirmAttack;

public class DesktopCommandFactory {
	private Map<String, AbstractDesktopCommand> commands = new HashMap<String, AbstractDesktopCommand>();
	
	public static AbstractDesktopCommand GetCommand(JSONObject message) {
		String type = (String) message.get("Type");
		if(type.equals("AttackSingleTarget")) {
			return new AttackSingleTargetCommand();
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
			return new AttackAreaEffectTargetsCommand();
		}
		if(type.equals("KnockbackCollisionSingleTarget")) {
			return new KnockbackCollisionSingleTargetCommand();
		}
		if(type.equals("KnockbackCollisionMultiTargets")) {
			return new KnockbackMultiTargetsCommand();
		}
		if(type.equals("KnockbackCollisionSweepTargets")) {
			return new KnockbackSweepTargetsCommand();
		}
		
		if(type.equals("AutofireTargets") ){
			return new AttackAutofireTargetsCommand();
		}
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
		
		return null;
		
	}
	
	
		

}
