package VirtualDesktop.Attack;

import java.io.File;
import java.io.IOException;

import VirtualDesktop.Ability.AbstractBattleClassAdapter;
import VirtualDesktop.Character.CharacterEffectsAdapter;
import VirtualDesktop.Character.Charasteristic;
import champions.DetailList;
import champions.Target;

public class BasicTargetAdapter {
	public Target target;
	
	
	public String getName() {
		return target.getName();
	}
	public Charasteristic getCharasteristic(String name) {
		
		return new Charasteristic(name, target);
	}
	
	public CharacterEffectsAdapter getCharacterEffect()
	{
		return new CharacterEffectsAdapter(target);
	}
	
	protected Target loadFromFile(String characterName, String extension) {
		File file = new File(characterName +extension);
		DetailList d = new DetailList();
		Target target=null;
		try {
			target = (Target) d.open(file);
		} catch (ClassNotFoundException | IOException e) {
			//System.out.println(characterName +"file not found");
		}
		return target;
	}
    
	

}
