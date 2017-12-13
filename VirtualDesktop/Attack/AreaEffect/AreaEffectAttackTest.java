
package VirtualDesktop.Attack.AreaEffect;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;

import VirtualDesktop.Attack.SingleAttack.*;

public class AreaEffectAttackTest extends SingleAttackTest {
	
	@Test
	void testAreaEffectRequestIsCreatedAgainstTargets_AreaEffectAttackResponseIsCreatedWithEffectsForAllTargets() {
	
		try {
			//act
			InvokeAbility("Pass Turn");
			InvokeAbility("Pass Turn");
			String[] targets = {"Blind Justice","Ogun","Spyder"} ;
			InvokeAreaEffectAttack("Air - Strike", targets, 0,null);			
			JSONObject result  = LoadAttackResult();
			
			//assert			
			assertEquals("Air - Strike", (String)result.get("Ability"));
			assertEquals("AttackMultiTargetResult", result.get("Type"));
			
			JSONArray targetsJSON = (JSONArray) result.get("Targets");
			
			for (int i=0; i < targetsJSON.size(); i++) {
				JSONObject tresult = (JSONObject) targetsJSON.get(i);
				String actualTarget = (String) ((JSONObject)tresult.get("Target")).get("Name");
				boolean found=false;
				if(actualTarget.equals("Hex")) {
					found=true;
				}
				for (String n : targets) {
			         if (actualTarget.equals( n)) {
			           found=true;
			         }
			      }
			      
				assertEquals(true, found);
				if(!actualTarget.equals("Hex")) {
					Boolean actualHit = (Boolean) ((JSONObject)tresult.get("Results")).get("Hit");
					assertEquals(actualHit, true);
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
	}

	private void InvokeAreaEffectAttack(String attack, String[] targets, int pushedStr, String toHitParameters) throws InterruptedException {
		String request = getBaseAttackString(attack);
		if(pushedStr!=0) {
			request+= ", \"PushedStr\":"+ pushedStr;
		}
		if(toHitParameters!=null && toHitParameters!="") {
			request+= ","+toHitParameters;
		}
		request+= ",\"Targets\": [";
		for (int i=0; i < targets.length;i++)
		{
			request+="{ \"Target\":\"" + targets[i]+"\"";
			if(toHitParameters!=null) {
				request+= "," + toHitParameters;
			}
			if(i< targets.length-1) {
				request+= "},";
			}
			else {
				request+= "}";
			}
		}
		request+= "]}";
		WriteAbilityActivatedEventFile(request);
		Thread.sleep(6000);
		
	}
	
	private String getBaseAttackString(String attack) {
		// TODO Auto-generated method stub
		return "{\"Type\": \"AreaEffectTargets\",	\"Ability\": \""+ attack +"\"";
	}

}
