package VirtualDesktop;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;

import VirtualDesktop.Ability.MoveCommand;
import VirtualDesktop.Ability.SimpleAbilityCommand;
import VirtualDesktop.Attack.AttackSingleTargetCommand;
import VirtualDesktop.Attack.ConfirmAttack;
import VirtualDesktop.Attack.KnockbackCollisionSingleTargetCommand;
import VirtualDesktop.Attack.MoveAndAttackCommand;
import VirtualDesktop.Attack.SimulatorSingleAttack;
import VirtualDesktop.Attack.AreaEffect.AttackAreaEffectTargetsCommand;
import VirtualDesktop.Attack.AreaEffect.KnockbackMultiTargetsCommand;
import VirtualDesktop.Attack.Autofire.AttackAutofireTargetsCommand;
import VirtualDesktop.Attack.MoveThrough.AttackMoveThroughTargetCommand;
import VirtualDesktop.Attack.Sweep.AttackSweepTargetCommand;
import VirtualDesktop.Attack.Sweep.KnockbackSweepTargetsCommand;

public class DesktopCommandFactory {
	private Map<String, AbstractDesktopCommand> commands = new HashMap<String, AbstractDesktopCommand>();
	
	public static AbstractDesktopCommand GetCommand(JSONObject message) {
		String type = (String) message.get("Type");
		if(type.equals("AttackSingleTarget")) {
			return new AttackSingleTargetCommand();
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
		if(type.equals("ConfirmAttack") ){
			return new ConfirmAttack();
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
		return null;
		
	}
	
	public static AbstractDesktopCommand GetCommand(SimulatorSingleAttack attack) {
		String type = (String) attack.getClass().getName();
		if(type.equals("SimulatorSingleAttack")) {
			return new AttackSingleTargetCommand();
		}
		
		/*
		if(type== "Ability") {
			return new AbilityCommand();
		}
		*/
		if(type.equals("SimulatorAreaEffectAttack")) {
			return new AttackAreaEffectTargetsCommand();
		}
		
		if(type.equals("SimulatorAutofireAttack") ){
			return new AttackAutofireTargetsCommand();
		}
		return null;
		
	}
	
		

}
