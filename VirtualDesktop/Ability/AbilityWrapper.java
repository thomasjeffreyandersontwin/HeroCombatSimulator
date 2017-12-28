package VirtualDesktop.Ability;

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
import champions.Ability;
import champions.Battle;
import champions.BattleEvent;
import champions.attackTree.AttackParametersNode;
import champions.attackTree.AttackParametersPanel;
import champions.attackTree.AttackTreeModel;
import champions.attackTree.AttackTreePanel;
import champions.attackTree.SelectTargetPanel;

public class AbilityWrapper extends AbstractBattleClassAdapter {
	
	public String Name;
	public String getName() {return "";}
	
	protected CharacterAdaptor Character;
	public CharacterAdaptor getCharacter() {return null;}
	
	int getPhaseLength() {return 0;}
	
	boolean isAbortable() { return false;}
	boolean isDefault() { return false;}
	
	public int getAbilityType() {return 0;}
	
	boolean CanPerform() { return false; }
    boolean Activate() 
    {
    	try {
			ActivateAbility();
		} catch (Exception e) {
			
			e.printStackTrace();
			return false;
		}
    	return isActive();
    		
 
    	
    }
    
	public champions.Ability UnderlyingAbility;
	
	public BattleEvent battleEvent;
	protected boolean burnStunWhenPushing;
	protected int Pushed;
	protected Battle battle;
	public AbilityWrapper(String name, CharacterAdaptor character ){
		if(name!=null && character!=null) {
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
		
	}
	
	
	public AttackResultAdapter ActivateAbility() {
		
		if(UnderlyingAbility!=null) {
			battleEvent = null;
			battleEvent = UnderlyingAbility.getActivateAbilityBattleEvent(UnderlyingAbility, null, null);
			battle.addEvent( battleEvent );
			Character.ActiveAbility = this;
			//Thread.sleep(900);
		}
		return null;
	}
	
	public static AbilityWrapper CreateAbilityWrapper(String abilityName, CharacterAdaptor character)
	{
		AbilityWrapper ability=null;
		//Battle.currentBattle.getDefaultAbilities().getAbility(abilityName, true);
		Ability a = character.UnderlyingCharacter.getAbility(abilityName);	
		if(a==null) {
			 a = Battle.getCurrentBattle().getDefaultAbilities().getAbility(abilityName, true);
		}
		if(a!= null) {
			if(a.isAttack()) {
				ability = new AttackAdapter(abilityName, character);
				return ability;
			}
				
			ability  = new AbilityWrapper(abilityName, character);
			return ability;
		}
		return null;
		
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
				ability = new SingleAttackAdapter(abilityName, character);
				return ability;
			}
			if(a.isMovementPower()) {
				ability = new MovementAdapter(abilityName, character);
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

	public boolean isActive() {
		// TODO Auto-generated method stub
		return UnderlyingAbility.isActivated(this.Character.UnderlyingCharacter);
	}
}
