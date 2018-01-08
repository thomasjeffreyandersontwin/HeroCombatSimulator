package VirtualDesktop.Ability;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import VirtualDesktop.Character.CharacterAdaptor;
import VirtualDesktop.Controller.GLOBALS;
import champions.Battle;
import champions.CombatSimulator;
import champions.DetailList;
import champions.Roster;
import champions.Target;
import junit.framework.Assert;

class AbilityAdapterTest {
	
	CharacterAdaptor validCharacter;
	Roster roster;
	
	@BeforeAll
	public static void startHCS() {
		CombatSimulator.main(null);
		
	}
	
	@BeforeEach
	void validCharacterInRoster() {
		
		try {
     		String name = System.getProperty("user.dir") + "\\eventinfo\\testdata\\Ogun.rst";
			roster = Roster.open(new File(name));
			Battle.currentBattle.addRoster(roster);
			
			Battle.currentBattle.startBattle();		
			Thread.sleep(500);
			
			validCharacter= new CharacterAdaptor("Ogun");
			
		} catch (ClassNotFoundException | IOException |InterruptedException e) {
			e.printStackTrace();
		}
		

		
		
	}
	
	@Test
	void CharacterIsNotActiveAndCharacterPersistentAbilityIsActivated_UnderlyingAbilityIsActive() {
		

		try {
			
			CharacterAdaptor spyderChar = new CharacterAdaptor(System.getProperty("user.dir") + "\\eventinfo\\testdata\\Spyder\\Spyder");
			roster.add(spyderChar.target);
			
			validCharacter.getAbility("Pass turn").Activate();
			AbilityAdapter ability = validCharacter.getAbility("Force Field");
			ability.Activate();
			Thread.sleep(500);
			Assert.assertEquals(true, ability.isActive());
		} catch (InterruptedException  e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	@Test
	void persistentAbilityIsActivated_UnderlyingAbilityIsActive() {
		

		try {			
			AbilityAdapter ability = validCharacter.getAbility("Force Field");
			ability.Activate();
			Thread.sleep(500);
			Assert.assertEquals(true, ability.isActive());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@AfterEach
	void removeRosterAndStopBattle(){
		try {
		Battle.currentBattle.removeRoster(roster);
		}catch(Exception e) {
			e.printStackTrace();
		}
		Battle.currentBattle.setStopped(true);
		
		
	}
	
}
