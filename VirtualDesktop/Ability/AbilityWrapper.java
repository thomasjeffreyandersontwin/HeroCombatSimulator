package VirtualDesktop.Ability;

import org.json.simple.JSONObject;

import VirtualDesktop.Attack.AreaEffect.SimulatorAreaEffectAttack;
import VirtualDesktop.Attack.Autofire.SimulatorAutofireAttack;
import VirtualDesktop.Attack.MoveThrough.SimulatorMoveThrough;
import VirtualDesktop.Attack.SingleAttack.SimulatorSingleAttack;
import VirtualDesktop.Attack.Sweep.SimulatorSweepAttack;
import VirtualDesktop.Character.CharacterAdaptor;
import champions.Ability;
import champions.Battle;
import champions.BattleEvent;
import champions.attackTree.AttackParametersNode;
import champions.attackTree.AttackParametersPanel;
import champions.attackTree.AttackTreeModel;
import champions.attackTree.AttackTreePanel;
import champions.attackTree.SelectTargetPanel;

public class AbilityWrapper {
	public String Name;
	protected CharacterAdaptor Character;
	public champions.Ability UnderlyingAbility;
	protected Battle battle;
	protected BattleEvent battleEvent;
	protected boolean burnStunWhenPushing;
	protected int Pushed;
	
	public AbilityWrapper(String name, CharacterAdaptor character ){
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
	
	public static AbilityWrapper CreateAbility(String abilityName, CharacterAdaptor character)
	{
		AbilityWrapper ability=null;
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
				ability = new MovementAdaptor(abilityName, character);
			return ability;
			}
			
			
				ability  = new AbilityWrapper(abilityName, character);
				return ability;
		}
		return null;
	}

	public void ConfirmAttack() {
		AttackTreePanel attackPanel = AttackTreePanel.defaultAttackTreePanel;
		attackPanel.advanceNode();
	}
	
	
	public JSONObject toJSON() {
		JSONObject jso = new JSONObject();
		jso.put("Name", Name);
		
		return jso;
	}
}
