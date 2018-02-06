package VirtualDesktop.Attack;

import java.io.File;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import VirtualDesktop.Ability.AbstractBattleClassAdapter;
import VirtualDesktop.Attack.AttackAdapter.HitLocation;
import VirtualDesktop.Attack.DamageAmount.DamageType;
import VirtualDesktop.Character.CharacterAdaptor;
import VirtualDesktop.Character.CharacterEffectsAdapter;
import champions.ActivationInfo;
import champions.BattleEvent;
import champions.Dice;
import champions.Effect;
import champions.ObstructionList;
import champions.Target;
import champions.abilityTree2.ATAbilityNode.CancelActivateAbilityAction;
import champions.enums.DefenseType;
import champions.enums.KnockbackEffect;
import champions.interfaces.IndexIterator;
import champions.interfaces.Undoable;
import champions.undoableEvent.EffectUndoable;

public class AttackResultAdapter extends AbstractBattleClassAdapter{
	public AttackResultAdapter(BattleEvent battleEvent, int tindex) {
		this.battleEvent = battleEvent;
		targetIndex = tindex;
	}
	
	public boolean forObject=false;
	private String token;
	public CharacterEffectsAdapter getDefenderEffects()
	{
		return new CharacterEffectsAdapter(getTarget());
	}
	
	public CharacterAdaptor getDefender() {
		
		Target t = getTarget();
		CharacterAdaptor defender = new CharacterAdaptor(t);
		return defender;
	}
	public boolean getAttackHit() {
		ActivationInfo ai =battleEvent.getActivationInfo();
		
	    return battleEvent.getActivationInfo().getHasHitTargets();
	}
	public DamageAmount getDamageResults() {
		
		DamageAmount d = new DamageAmount(battleEvent, targetIndex, DamageType.Attack);
		if(forObject) {
			d.forObject = true;
		}
		return d;
	}
	public KnockbackResult getKnockbackResult() {
		
		KnockbackResult k = new KnockbackResult(battleEvent, targetIndex);
		return k;
	}
	
	
	private int getKnockbackIndex() {
		return battleEvent.getKnockbackIndex(getTarget(), "KB");
	}
	private int getDistance() {
		return  battleEvent.getKnockbackAmount(getKnockbackIndex());
	}
	public ArrayList<AttackResultAdapter> getObstructionDamageResults() {
		ArrayList<AttackResultAdapter> results = new ArrayList<AttackResultAdapter>();
	
		ObstructionList ol = getActivationInfo().getObstructionList(targetIndex);

		if(ol!=null) {
			for(int i=0;i<ol.size();i++) {
				Target ob = ol.getObstruction(i);
				if(ob!=null) {
					
					PhysicalObjectAdapter object = new PhysicalObjectAdapter(ob);
					
					int tindex = getActivationInfo().getTargetIndex(ob);
					if(tindex!=-1) {
						AttackResultAdapter result = new AttackResultAdapter(battleEvent, tindex);
						result.forObject=true;
						results.add(result);
					}
				}
			}

		}
		
		return results;
	}	
	
	public HitLocation getLocationHit() {
		String locationString =  getActivationInfo().getTargetHitLocation(targetIndex);
		if(locationString!=null) {
		switch (locationString)
		{
			case "HEAD": return HitLocation.HEAD;
			case "ARMS": return HitLocation.ARMS;
			case "CHEST": return HitLocation.CHEST;
			case "FEET": return HitLocation.FEET;
			case "HANDS": return HitLocation.HANDS;
			case "LEGS": return HitLocation.LEGS;
			case "SHOULDERS": return HitLocation.SHOULDERS;
			case "STOCMACH": return HitLocation.STOMACH;
			case "THIGHS": return HitLocation.THIGHS;
			case "VITALS": return HitLocation.VITALS;
		} 
		}
		return null;
	}

	public JSONObject exportToJSON() {
		JSONObject attackResultJSON = new JSONObject();
		attackResultJSON.put("Ability", getActivationInfo().getAbility().getName());
		exportAffectedTargetToJSON(this,attackResultJSON);
		return attackResultJSON;
	}

	protected void exportAffectedTargetToJSON(AttackResultAdapter attackResultAdapter, JSONObject attackResultJSON) {
		CharacterAdaptor defender = new CharacterAdaptor(attackResultAdapter.getTarget());
		JSONObject defenderJSON = defender.exportToJSON();
		attackResultJSON.put("Token", this.token);
		attackResultJSON.put("Defender", defenderJSON);
		
		JSONObject damageJSON = attackResultAdapter.getDamageResults().exportToJSON();
		attackResultJSON.put("Damage Results", damageJSON);
		
		attackResultJSON.put("Hit", attackResultAdapter.getAttackHit());
		if(attackResultAdapter.getKnockbackResult()!=null) {
			JSONObject kbJSON = attackResultAdapter.getKnockbackResult().exportToJSON();
			attackResultJSON.put("Knockback Result", kbJSON);
		}
		
		JSONArray objstructionsJSON = new JSONArray();
		if(attackResultAdapter.getObstructionDamageResults().size() !=0) {
			ArrayList<AttackResultAdapter> obsResult = attackResultAdapter.getObstructionDamageResults();
			for(int i =0; i < obsResult.size();i++)
			{
				objstructionsJSON.add(obsResult.get(i).exportToJSON());
			}
			
			attackResultJSON.put("Obstruction Damage Results", objstructionsJSON);
		}
	}

	public void setToken(String guid) {
		this.token = guid;
		
	}
	
}
