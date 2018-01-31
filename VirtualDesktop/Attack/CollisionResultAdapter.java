package VirtualDesktop.Attack;

import org.json.simple.JSONObject;

import VirtualDesktop.Ability.AbstractBattleClassAdapter;
import VirtualDesktop.Attack.DamageAmount.DamageType;
import VirtualDesktop.Character.CharacterAdaptor;
import VirtualDesktop.Character.CharacterEffectsAdapter;
import champions.BattleEvent;
import champions.Target;

public class CollisionResultAdapter extends AbstractBattleClassAdapter {

public CollisionResultAdapter(BattleEvent battleEvent, int tindex) {
		this.battleEvent = battleEvent;
		targetIndex = tindex;
	}

public PhysicalObjectAdapter getObjectCollidingWith() {
		
		Target t = getTarget();
		PhysicalObjectAdapter collision = new PhysicalObjectAdapter(t);
		return collision;
	}
	
	public CharacterEffectsAdapter getEffectsOnObjectCollidingWith()
	{
		return new CharacterEffectsAdapter(getTarget());
	}
	
	public DamageAmount getDamageResults() {
		
		DamageAmount d = new DamageAmount(battleEvent, targetIndex, DamageType.Attack);
		return d;
	}
	
	
	public KnockbackResult getKnockbackResult() {
		
		KnockbackResult k = new KnockbackResult(battleEvent, targetIndex);
		return k;
	}

	public JSONObject exportToJSON() {
		// TODO Auto-generated method stub
		JSONObject ex = new JSONObject();
		PhysicalObjectAdapter pyObject = new PhysicalObjectAdapter(getTarget());
		JSONObject obJSON = pyObject.exportToJSON();
		ex.put("Object Collided With", obJSON);
		
		JSONObject damageJSON = getDamageResults().exportToJSON();
		ex.put("Collision Damage Results", damageJSON);
		return ex;
	}
}
