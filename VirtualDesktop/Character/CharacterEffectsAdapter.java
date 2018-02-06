package VirtualDesktop.Character;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import VirtualDesktop.Attack.BasicTargetAdapter;
import champions.Effect;
import champions.Target;
import jdk.nashorn.api.scripting.JSObject;

public class CharacterEffectsAdapter {
	ArrayList<String> effects;
	Target t;
	public CharacterEffectsAdapter(Target t)
	{
		this.t = t;
	}
	
	public boolean isDead() {
		return t.isDead();
	}
	public boolean isDying() {
		return t.isDying();
	}
	public boolean isUnconscious() {
		return t.isUnconscious();
	}
	public boolean isStunned() {
		return t.isStunned();
	}
	
	public ArrayList<String> getEffects()
	{
		effects = new ArrayList<String>();
		
		if(t.isStunned()) 
			effects.add("Stunned");
		if(t.isUnconscious()) 
			effects.add("Unconscious");
		if(t.isDying()) 
			effects.add("Dying");
		if(t.isDead()) 
			effects.add("Dead");
		if(t.getEffect("Destroyed")!=null) 
			effects.add("Destroyed");
		if(t.getEffect("Partially Destroyed")!=null) 
			effects.add("Partially Destroyed");
		
		for(int i=0;i< t.getEffectCount(); i++)
		{
			Effect e = t.getEffect(i);
			effects.add(e.getName());
		}
		return effects;

	}

	public JSONArray exportToJSON() {
		JSONArray ex = new JSONArray();
		if(t.isStunned()) 
			ex.add("Stunned");
		if(t.isUnconscious()) 
			ex.add("Unconscious");
		if(t.isDying()) 
			ex.add("Dying");
		if(t.isDead()) 
			ex.add("Dead");
		if(t.getEffect("Destroyed")!=null) 
			ex.add("Destroyed");
		if(t.getEffect("Partially Destroyed")!=null) 
			ex.add("Partially Destroyed");
		return ex;
	}
}
