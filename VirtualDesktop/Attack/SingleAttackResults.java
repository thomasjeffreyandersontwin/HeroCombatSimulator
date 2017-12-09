package VirtualDesktop.Attack;

import java.io.File;
import java.io.FileWriter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import VirtualDesktop.CombatSimulatorCharacter;
import VirtualDesktop.GLOBALS;
import VirtualDesktop.Ability.AbilityResults;
import champions.Target;
import champions.battleMessage.AbstractBattleMessageGroup;
import champions.battleMessage.ActivateAbilityMessageGroup;
import champions.battleMessage.AttackMessage;
import champions.battleMessage.BattleMessage;
import champions.battleMessage.KnockbackMessageGroup;
import champions.battleMessage.KnockbackSummaryGroup;
import champions.battleMessage.KnockbackSummaryMessage;
import champions.battleMessage.SingleAttackMessageGroup;

public class SingleAttackResults extends AbilityResults {
	

	
	public JSONObject buildFrom(AbstractBattleMessageGroup group) {
		JSONObject attackJSON = CreateAbility(group);
		
		CreateResultsFromAbilityActivation(group, attackJSON);
		CreateKnockbackFromAbilityActivation(group, attackJSON);

		return attackJSON;
	}
	
	protected void CreateKnockbackFromAbilityActivation(AbstractBattleMessageGroup group, JSONObject attackJSON) {
		JSONObject results = (JSONObject) attackJSON.get("Results");
		for(int i= 0 ;  i < group.getChildCount();i++) {
			BattleMessage bm = group.getChild(i);
			if(bm instanceof  KnockbackMessageGroup || bm instanceof KnockbackSummaryGroup) 
			{
				bm = getKnockBackNode(bm);
				if( bm !=null && bm instanceof KnockbackSummaryMessage) {
					ExportSingleKnockback(results, (KnockbackSummaryMessage)bm);
				}
			/*	if( bm !=null && bm instanceof KnockbackMessageGroup) {
					
					ExportSingleKnockback(results, (KnockbackSummaryMessage)bm);
				}
				*/
			}
			
		}
	}
	protected void  CreateResultsFromAbilityActivation(AbstractBattleMessageGroup group, JSONObject attackJSON) {
		for(int i= 0 ;  i < group.getChildCount();i++) {
			BattleMessage bm = group.getChild(i);
			if(bm instanceof SingleAttackMessageGroup) {
				SingleAttackMessageGroup attack =(SingleAttackMessageGroup)bm;           			
				ExportSingleAttackResults(attackJSON,  attack);
			}
		}
	}
	
	
	protected BattleMessage getKnockBackNode(BattleMessage bm) {
		KnockbackSummaryMessage kbm = null;	
		AbstractBattleMessageGroup kbsg = (AbstractBattleMessageGroup)bm;
		for (int j= 0;j< kbsg.getChildCount();j++ ){
			BattleMessage kbsgChild = kbsg.getChild(j);
			if (kbsgChild instanceof KnockbackSummaryMessage){
				if(kbm!=null) {
					kbm.summary+=((KnockbackSummaryMessage) kbsgChild).getSummary();
				}
				else {
					kbm = (KnockbackSummaryMessage)kbsgChild;
				}
				bm =kbm;
			}
			
		}
		return bm;
	}

	protected  JSONObject ExportSingleAttackResults(JSONObject attackJSON, AttackMessage attack) {
		
		JSONObject targetJSON = new JSONObject();
		
		attackJSON.put("Target", targetJSON );
		
		Target t = attack.getTarget();
		Boolean isHit = attack.isTargetHit();
		
		JSONObject results = ExportDamageResults( targetJSON, t, isHit);
		attackJSON.put("Results", results);
		results.put("Hit", isHit);
		return results;
	}
	
	protected void ExportSingleKnockback(JSONObject results, KnockbackSummaryMessage bm) {
		JSONObject knockback = new JSONObject();
		results.put("Knockback", knockback);
		
		KnockbackSummaryMessage kbm=bm;
		String kb = kbm.getMessage();
		if(kb.indexOf("was knocked back")> 0 || kb.indexOf("was knocked down")>0 ){
			kb =kb.substring(kb.indexOf("was knocked back ")+17,kb.length());
			if(kb.indexOf("\" with no collision and breakfell successfully.")!=-1){
				kb=kb.substring(0, kb.length()-("\" with no collision and breakfell successfully.").length());
				knockback.put("Breakfall Successful", true);
			}
			else
			{
				if(kb.indexOf("\" with no collision.")!=-1){
					kb=kb.substring(0, kb.length()-("\" with no collision.").length());
				}
			}
			int end = kb.indexOf("\"");
			if(end!=-1) {
				kb = kb.substring(0,end);
			}
			knockback.put("Distance", kb);
			
			kb = bm.getSummary();
			if(kb.contains("collided with ")) {
				int start = kb.indexOf("collided with ")+ "collided with ".length();
				end =kb.indexOf(" and ", start);
				kb = kb.substring(start, end);
				
				JSONObject collision = new JSONObject();
				knockback.put("Obstacle Collision", collision);
				String obtacleName = kb;
				Target obstacle = new CombatSimulatorCharacter(kb).UnderlyingCharacter;
				if(obstacle !=null) {
					results =  ExportDamageResults(collision, obstacle, true);	
				}
			}
		}
	}
		
	protected JSONObject ExportDamageResults(JSONObject targetJSON, Target t,Boolean isHit) {
		targetJSON.put("Name", t.getName());
		
		
		if(t.hasStat("STUN")) {
			JSONObject statJSON = new JSONObject();
			targetJSON.put("STUN", statJSON);
			statJSON.put("Current", t.getCurrentStat("STUN"));
			statJSON.put("Max", t.getCharacteristic("STUN").getBaseStat());
		}
		if(t.hasStat("BODY")) {
			JSONObject statJSON = new JSONObject();
			targetJSON.put("BODY", statJSON);
			statJSON.put("Current", t.getCurrentStat("BODY"));
			statJSON.put("Max", t.getCharacteristic("BODY").getBaseStat());
		}
		JSONObject results = new JSONObject();
		
		
		if( isHit== true){
			JSONArray effects = new JSONArray();
			results.put("Effects", effects);

			if(t.isDead()==true){
				effects.add("Dead");
			}
			if(t.isDying()==true){
				effects.add("Dying");
			}
			if(t.stunned==true){
				effects.add("Stunned");
			}
			if(t.unconscious==true){
				effects.add("Unconscious");
			}
			if(t.hasEffect("Partially Destroyed")){
				effects.add("Partially Destroyed");
			}
			if(t.hasEffect("Destroyed")){
				effects.add("Destroyed");
			}
			
		}
		return results;
	}
	
	protected void WriteJSON(JSONObject attackJSON) {
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

}
