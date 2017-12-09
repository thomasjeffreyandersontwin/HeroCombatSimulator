package VirtualDesktop.Ability;

import VirtualDesktop.CombatSimulatorCharacter;
import VirtualDesktop.Attack.SimulatorSingleAttack;
import VirtualDesktop.Attack.AreaEffect.SimulatorAreaEffectAttack;
import VirtualDesktop.Attack.Autofire.SimulatorAutofireAttack;
import VirtualDesktop.Attack.MoveThrough.SimulatorMoveThrough;
import VirtualDesktop.Attack.Sweep.SimulatorSweepAttack;
import champions.Ability;
import champions.Battle;
import champions.BattleEvent;
import champions.attackTree.AttackParametersNode;
import champions.attackTree.AttackParametersPanel;
import champions.attackTree.AttackTreeModel;
import champions.attackTree.AttackTreePanel;
import champions.attackTree.SelectTargetPanel;

public class SimulatorAbility {
	public String Name;
	protected CombatSimulatorCharacter Character;
	public champions.Ability UnderlyingAbility;
	protected Battle battle;
	protected BattleEvent battleEvent;
	protected boolean burnStunWhenPushing;
	protected int Pushed;
	
	public SimulatorAbility(String name, CombatSimulatorCharacter character ){
		Name = name;
		Character = character;
		battle= Battle.currentBattle;
		if(Character !=null) {
			UnderlyingAbility = Character.UnderlyingCharacter.getAbility(Name);
			if(UnderlyingAbility==null) {
				UnderlyingAbility = battle.getDefaultAbilities().getAbility(Name, true);	
			}
		}
		
	}
	
	
	public void ActivateAbility() throws Exception {
		
		if(UnderlyingAbility!=null) {
			battleEvent = null;
			battleEvent = UnderlyingAbility.getActivateAbilityBattleEvent(UnderlyingAbility, null, null);
			battle.addEvent( battleEvent );
			Character.ActiveAbility = this;
			Thread.sleep(900);
		}
		
	}
	
	public static SimulatorAbility CreateAbility(String abilityName, CombatSimulatorCharacter character)
	{
		SimulatorAbility ability=null;
		//Battle.currentBattle.getDefaultAbilities().getAbility(abilityName, true);
		Ability a = character.UnderlyingCharacter.getAbility(abilityName);	
		if(a==null) {
			 a = Battle.getCurrentBattle().getDefaultAbilities().getAbility(abilityName, true);
		}
		if(a!= null) {
			if(abilityName=="Move Through" || abilityName=="Move By") {
				ability = new SimulatorMoveThrough(abilityName, character);
				return ability;
			}
			if(a.hasAdvantage("Autofire")){
				ability = new SimulatorAutofireAttack(abilityName, character);
				return ability;
			}
			if(a.hasAdvantage("Area Effect")) {
				ability = new SimulatorAreaEffectAttack(abilityName, character);
				return ability;
			}
			if(a.getName().equals("Sweep")) {
				ability = new SimulatorSweepAttack(abilityName, character);
				return ability;
			}
			if(a.isAttack()) {
				ability = new SimulatorSingleAttack(abilityName, character);
				return ability;
			}
			if(a.isMovementPower()) {
				ability = new SimulatorMovement(abilityName, character);
			return ability;
			}
			
			
				ability  = new SimulatorAbility(abilityName, character);
				return ability;
		}
		return null;
	}

	public void ConfirmAttack() {
		AttackTreePanel attackPanel = AttackTreePanel.defaultAttackTreePanel;
		attackPanel.advanceNode();
	}
}
