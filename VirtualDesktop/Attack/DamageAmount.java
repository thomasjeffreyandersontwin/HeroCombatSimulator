package VirtualDesktop.Attack;

import java.util.ArrayList;

import org.json.simple.JSONObject;

import VirtualDesktop.Ability.AbstractBattleClassAdapter;
import champions.BattleEvent;
import champions.Effect;
import champions.ObstructionList;
import champions.Target;

public class DamageAmount extends AbstractBattleClassAdapter{
	public enum DamageType{Attack, Knockback}
	Boolean forObject = false;
	Effect DamageEffect;
	DamageType type;
	public DamageAmount(BattleEvent e, int tindex, DamageType type) {
		this.type = type;
		battleEvent = e;
		targetIndex = tindex;
		// TODO Auto-generated constructor stub
	}
	
	public int getSTUN() {
		
		String stat="STUN";
		if(type.equals(DamageType.Knockback) && battleEvent.getKnockbackDamageEffect(targetIndex)!=null)
			return battleEvent.getKnockbackDamageEffect(targetIndex).getTotalAdjustedStunDamage();
		else {
			if(battleEvent.getDamageEffect(targetIndex)!=null)
				return battleEvent.getDamageEffect(targetIndex).getTotalAdjustedStunDamage();
		}
		return 0;
	}

	private int getDamageValueForStat(String stat) {
		Effect e;
		if(DamageEffect==null) {
		e =  battleEvent.getDamageEffect(targetIndex);
		}
		else {
			e= DamageEffect;
		}
		int bindex = e.findIndexed("Subeffect", "VERSUS", stat);

		
        int count = e.getIndexedSize("Subeffect");
		for (int i = 0; i < count; i++) {
			e.getSubeffectValue(i);
			int adjusted = (int) e.getSubeffectAdjustedAmount(i);
			if(adjusted==0)
				adjusted = (int) e.getSubeffectValue(i);
			if(adjusted > 0 && e.getSubeffectVersusObject(i).equals(stat)){
				return adjusted;
				
			}
		}
		return 0;
	}
	
	public int getBODY() {
		
		String stat="BODY";
		if(!forObject) 
		{
			if(type.equals(DamageType.Knockback)&&battleEvent.getKnockbackDamageEffect(targetIndex)!=null)
				return battleEvent.getKnockbackDamageEffect(targetIndex).getTotalAdjustedBodyDamage();
			else {
				if(battleEvent.getDamageEffect(targetIndex)!=null)
					return battleEvent.getDamageEffect(targetIndex).getTotalAdjustedBodyDamage();
			}
		}
		else 
		{	
			if(type.equals(DamageType.Knockback)&&battleEvent.getKnockbackDamageEffect(targetIndex)!=null) 
			{
				if(battleEvent.getDamageEffect(targetIndex)!=null)
					return battleEvent.getKnockbackDamageEffect(targetIndex).getTotalAdjustedStunDamage();
				return 0;
			}
			else 
			{
				if(battleEvent.getDamageEffect(targetIndex)!=null)
					return battleEvent.getDamageEffect(targetIndex).getTotalAdjustedStunDamage();
				return 0;
			}
		}
		return 0;
	}

	public JSONObject exportToJSON() {
		JSONObject ex = new JSONObject();
		ex.put("STUN", getSTUN());
		ex.put("BODY", getBODY());
		return ex;
	}


}
