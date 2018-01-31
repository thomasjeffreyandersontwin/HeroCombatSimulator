package VirtualDesktop.Attack;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import VirtualDesktop.Ability.AbstractBattleClassAdapter;
import VirtualDesktop.Attack.DamageAmount.DamageType;
import champions.BattleEvent;
import champions.Effect;
import champions.Target;
import champions.enums.KnockbackEffect;
import champions.interfaces.IndexIterator;

public class KnockbackResult extends AbstractBattleClassAdapter{
	public KnockbackResult(BattleEvent e, int tindex) {
		battleEvent = e;
		targetIndex = tindex;
		// TODO Auto-generated constructor stub
	}
	Effect kde = null;
	public void setKnockbackDamageEffect(Effect e) {
		kde = e;
	}
	public DamageAmount getKnockbackDamage() {
		
		DamageAmount kdam =  new DamageAmount(battleEvent, targetIndex, DamageType.Knockback);
		kdam.DamageEffect = battleEvent.getKnockbackDamageEffect(targetIndex);
		return kdam;
	}
	public ArrayList<CollisionResultAdapter> getObstaclesCollidedWith() {
		

		ArrayList<CollisionResultAdapter> knockbackCollisions = new ArrayList<CollisionResultAdapter>() ;
		Target t  = getTarget();
		int kbi = battleEvent.getKnockbackIndex(t, "KB");
		
		String name = t.getName();
		IndexIterator i = battleEvent.getKnockbackTargets(".KB."+ name);
	
	
		
		
		int distance = battleEvent.getKnockbackAmount(kbi);
		
		if(distance>0) {
		
			if(battleEvent.getKnockbackEffect(kbi)== KnockbackEffect.POSSIBLECOLLISION) {
				
				
				IndexIterator kbindex = getActivationInfo().getTargetGroupIterator(".KB."+ name);
				//IndexIterator kbindex = battleEvent.getKnockbackTargets(name);
				
				while(kbindex.hasNext()) {
					int  j =  kbindex.nextIndex();
					Target collision  = getActivationInfo().getTarget(j);
					
					if (collision!=null && collision!=t) {
						
						CollisionResultAdapter collisionResult = new CollisionResultAdapter(battleEvent, j);
						knockbackCollisions.add(collisionResult);
						
					}	
				}
				return knockbackCollisions;
			}
		}
		return null;
	}
	private int getKnockbackIndex() {
		return battleEvent.getKnockbackIndex(getTarget(), "KB");
	}
	public  int getDistance() {
		return  battleEvent.getKnockbackAmount(getKnockbackIndex());
	}
	public JSONObject exportToJSON() {
		JSONObject ex = new JSONObject();
		ex.put("Distance", getDistance());
		ArrayList<CollisionResultAdapter>  collisions = getObstaclesCollidedWith();
		if(collisions!=null) {
			JSONArray cJSON = new JSONArray();
			ex.put("Collisions", cJSON);
			for(int i=0; i < collisions.size(); i++ ) {
				JSONObject c = collisions.get(i).exportToJSON();
				cJSON.add(c);
			}
			
		}
		return ex;
	}
}
