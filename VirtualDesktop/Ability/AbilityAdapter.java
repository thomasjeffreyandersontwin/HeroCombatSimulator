package VirtualDesktop.Ability;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.UndeclaredThrowableException;

import org.json.simple.JSONObject;

import VirtualDesktop.Attack.SingleAttackAdapter;
import VirtualDesktop.Attack.AttackAdapter;
import VirtualDesktop.Attack.AttackResultAdapter;
import VirtualDesktop.Attack.AreaEffect.AreaEffectAttackAdapter;
import VirtualDesktop.Attack.Autofire.AutofireAttackAdapter;
import VirtualDesktop.Attack.MoveThrough.MoveThroughAdapter;
import VirtualDesktop.Attack.Sweep.SimulatorSweepAttack;
import VirtualDesktop.Character.CharacterAdaptor;
import VirtualDesktop.Controller.GLOBALS;
import champions.Ability;
import champions.Battle;
import champions.BattleEvent;
import champions.attackTree.AttackParametersNode;
import champions.attackTree.AttackParametersPanel;
import champions.attackTree.AttackTreeModel;
import champions.attackTree.AttackTreePanel;
import champions.attackTree.SelectTargetPanel;
import champions.exception.BattleEventException;
import champions.powers.advantageAreaEffect;

public class AbilityAdapter extends AbstractBattleClassAdapter {
	
	public String Name;
	public String getName() {return "";}
	
	protected CharacterAdaptor Character;
	
	int getPhaseLength() {return 0;}
	
	boolean isAbortable() { return false;}
	boolean isDefault() { return false;}
	
	public int getAbilityType() {return 0;}
	
	boolean CanPerform() { return false; }
    protected boolean Activate() 
    {
    	try {
			activateAbility();
		} catch (Exception e) {
			
			e.printStackTrace();
			return false;
		}
    	return isActive();
    		
 
    	
    }
    
	public champions.Ability UnderlyingAbility;
	
	
	protected boolean burnStunWhenPushing;
	protected int Pushed;
	protected Battle battle;
	public AbilityAdapter(String name, CharacterAdaptor character ){
		if(name!=null && character!=null) {
			Name = name;
			Character = character;
			battle= Battle.currentBattle;
			if(Character !=null) {
				UnderlyingAbility = Character.target.getAbility(Name);
				if(UnderlyingAbility==null) {
					UnderlyingAbility = battle.getDefaultAbilities().getAbility(Name, true);	
				}
			}
		}
		
	}
	
	
	public AttackResultAdapter activateAbility() {
		
		if(UnderlyingAbility!=null) {
			battleEvent = null;
			if(!UnderlyingAbility.isActivated(Character.target)) {
				battleEvent = UnderlyingAbility.getActivateAbilityBattleEvent(UnderlyingAbility, null, null);
				battle.addEvent( battleEvent );
			}
			else {
				try {
					UnderlyingAbility.shutdownActivated(battleEvent, true);
				} catch (BattleEventException e) {
					e.printStackTrace();
				}
				
				
			}
			
			Character.ActiveAbility = this;
			//Thread.sleep(900);
		}
		return null;
	}
	
	public static AbilityAdapter CreateAbilityWrapper(String abilityName, CharacterAdaptor character)
	{
		AbilityAdapter ability=null;
		//Battle.currentBattle.getDefaultAbilities().getAbility(abilityName, true);
		Ability a = character.target.getAbility(abilityName);	
		if(a==null) {
			 a = Battle.getCurrentBattle().getDefaultAbilities().getAbility(abilityName, true);
		}
		if(a!= null) {
			if(a.hasAdvantage(advantageAreaEffect.advantageName)) {
				ability = new AreaEffectAttackAdapter(abilityName, character);
				return ability;
			}
			if(a.isAttack()) {
				ability = new AttackAdapter(abilityName, character);
				return ability;
			}
				
			ability  = new AbilityAdapter(abilityName, character);
			return ability;
		}
		return null;
		
	}
	public static AbilityAdapter CreateAbility(String abilityName, CharacterAdaptor character)
	{
		AbilityAdapter ability=null;
		//Battle.currentBattle.getDefaultAbilities().getAbility(abilityName, true);
		Ability a = character.target.getAbility(abilityName);	
		if(a==null) {
			 a = Battle.getCurrentBattle().getDefaultAbilities().getAbility(abilityName, true);
		}
		if(a!= null) {
			if(abilityName=="Move Through" || abilityName=="Move By") {
				ability = new MoveThroughAdapter(abilityName, character);
				return ability;
			}
			if(a.hasAdvantage("Autofire")){
				ability = new AutofireAttackAdapter(abilityName, character);
				return ability;
			}
			if(a.hasAdvantage("Area Effect")) {
				ability = new AreaEffectAttackAdapter(abilityName, character);
				return ability;
			}
			if(a.getName().equals("Sweep")) {
				ability = new SimulatorSweepAttack(abilityName, character);
				return ability;
			}
			if(a.isAttack()) {
				ability = new AttackAdapter(abilityName, character);
				return ability;
			}
			if(a.isMovementPower()) {
				ability = new MovementAdapter(abilityName, character);
			return ability;
			}
			
			
			ability  = new AbilityAdapter(abilityName, character);
			return ability;
		}
		return null;
	}
	
	public void WriteJSON(JSONObject attackJSON) {
		try {
			File f = new java.io.File(GLOBALS.EXPORT_PATH + "AbilityAttackResult.event");
			if(f.exists()) {
				Thread.sleep(500);
			}		
			FileWriter writer = new FileWriter(GLOBALS.EXPORT_PATH+"AbilityAttackResult.event");
			writer.write(attackJSON.toJSONString());
			writer.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	public boolean isActive() {
		// TODO Auto-generated method stub
		return UnderlyingAbility.isActivated(this.Character.target);
	}

	public JSONObject processJSON(JSONObject abilityJSON) {
		this.Activate();
		return null;
	}
}
