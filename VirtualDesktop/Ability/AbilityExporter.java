package VirtualDesktop.Ability;

import java.io.File;
import java.io.FileWriter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import VirtualDesktop.CombatSimulatorCharacter;
import VirtualDesktop.GLOBALS;
import VirtualDesktop.Roster.SingleAttackResults;
import champions.BattleEvent;
import champions.Target;
import champions.battleMessage.AFShotMessageGroup;
import champions.battleMessage.AbstractBattleMessageGroup;
import champions.battleMessage.ActivateAbilityMessageGroup;
import champions.battleMessage.AreaEffectAttackMessageGroup;
import champions.battleMessage.AttackMessage;
import champions.battleMessage.BattleMessage;
import champions.battleMessage.DefaultAttackMessage;
import champions.battleMessage.EmbeddedBattleEventMessageGroup;
import champions.battleMessage.KnockbackMessageGroup;
import champions.battleMessage.KnockbackSummaryGroup;
import champions.battleMessage.KnockbackSummaryMessage;
import champions.battleMessage.SingleAttackMessageGroup;
import champions.battleMessage.SummaryMessage;
import champions.battleMessage.SummaryMessageGroup;
import champions.battleMessage.SweepMessageGroup;
import jdk.nashorn.internal.objects.Global;

public class AbilityExporter {

	private static final AbilityResultFactory SimulatorAbilityFactory = null;

	public static void export(ActivateAbilityMessageGroup group) {
			JSONObject attackJSON = new JSONObject();
    		CreateJSONFromAbilityActivation(group, attackJSON);
        	WriteJSON(attackJSON);
	}

	public static String DetermineAttackType(AbstractBattleMessageGroup group) {
		if(group instanceof ActivateAbilityMessageGroup) {
			for(int i= 0 ;  i < group.getChildCount();i++) {
				BattleMessage bm = group.getChild(i);
				if(bm instanceof AFShotMessageGroup) {
					return "AttackAutofireTargetsResult";
				}
				if(bm instanceof SingleAttackMessageGroup) {
					return "AttackSingleTargetResult";
				}
				if(bm instanceof AreaEffectAttackMessageGroup) {
					return "AttackAreaEffectTargetResults";
				}
			}
		}
		else if(group instanceof SweepMessageGroup) {
			return "AttackSweepTargetsResult";	
		}
		return "AbilityResult";
		
	}
	
	public static void exportR(AbstractBattleMessageGroup group) {
		String type = DetermineAttackType(group);
		SingleAttackResults abilityResult = SimulatorAbilityFactory.GetAbilityForType(type);
		JSONObject resultJSON =  abilityResult.buildFrom(group);
		WriteJSON(resultJSON);
		
		
	}
	private static void CreateJSONFromAbilityActivation(ActivateAbilityMessageGroup group, JSONObject attackJSON) {
		JSONObject results = new JSONObject();
		
		attackJSON.put("Type", "AttackSingleTargetResult");
		String ab=group.ability.getName();
		attackJSON.put("Ability", ab);
		JSONArray attacks = null;
		boolean isAreaAttack = false;
		boolean isAutoAttack = false;
		int targetNum=0;
		for(int i= 0 ;  i < group.getChildCount();i++) {
			BattleMessage bm = group.getChild(i);
			if(bm.getClass() == AFShotMessageGroup.class) {
				SingleAttackMessageGroup a= (SingleAttackMessageGroup) ((AFShotMessageGroup)bm).attackMessage;
				if(attacks==null) {
					attacks = new JSONArray();
					attackJSON.put("Targets", attacks);
				}
				JSONObject attack = new JSONObject();
				attacks.add(attack);
				results = ExportSingleAttack(attack,a);
				isAutoAttack = true;	
			} 
			
			if(bm.getClass() == SingleAttackMessageGroup.class) {
				SingleAttackMessageGroup attack =(SingleAttackMessageGroup)bm;           			
				results = ExportSingleAttack(attackJSON,  attack);
			}
			else if (bm instanceof AreaEffectAttackMessageGroup){
				AreaEffectAttackMessageGroup areaAttack = (AreaEffectAttackMessageGroup)bm;
				ExportAreaEffectAttack(attackJSON,  areaAttack);
				isAreaAttack=true;
				
			}
			else
			{
				if(bm instanceof  KnockbackMessageGroup || bm instanceof KnockbackSummaryGroup) {
					if(isAreaAttack==true || isAutoAttack==true) {
						KnockbackSummaryGroup ksm = (KnockbackSummaryGroup)bm;
						
						if(isAreaAttack)
							targetNum=i;
						results = (JSONObject) ((JSONObject) ((JSONArray)attackJSON.get("Targets")).get(targetNum)).get("Results");
						if(isAutoAttack)
							targetNum++;
					}
					bm = getKnockBackNode(bm);
					if(bm !=null) {
						ExportSingleKnockback(results, (KnockbackSummaryMessage)bm);
					}
				}
			}
			
		}
	}

	private static void ExportAreaEffectAttack(JSONObject attackJSON, AreaEffectAttackMessageGroup attack) {
		JSONArray targets = new JSONArray();
		attackJSON.put("Targets", targets );
		
		
		for (int  j= 0;j< attack.getChildCount();j++ ){
			if(attack.getChild(j)!=null){
				if(attack.getChild(j) instanceof DefaultAttackMessage){
					DefaultAttackMessage attackMessage = (DefaultAttackMessage) attack.getChild(j);
					JSONObject target = new JSONObject();
					targets.add(target);
					ExportSingleAttack(target , attackMessage);
				}
			}
		}
	}

	private static void WriteJSON(JSONObject attackJSON) {
		try {
			File f = new java.io.File(GLOBALS.EXPORT_PATH +"AbilityAttackResult.event");
			if(f.exists()) {
				Thread.sleep(500);
			}		
			FileWriter writer = new FileWriter(GLOBALS.EXPORT_PATH + "AbilityAttackResult.event");
			writer.write(attackJSON.toJSONString());
			writer.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static BattleMessage getKnockBackNode(BattleMessage bm) {
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

	private static void ExportSingleKnockback(JSONObject results, KnockbackSummaryMessage bm) {
		
							
		JSONObject knockback = new JSONObject();
		results.put("Knockback", knockback);
		
		KnockbackSummaryMessage kbm=(KnockbackSummaryMessage)bm;
		String kb = kbm.getMessage();
		if(kb.indexOf("was knocked back")> 0 || kb.indexOf("was knocked down")>0 ){
			kb =kb.substring(kb.indexOf("back ")+5,kb.length());
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
			kb = kb.substring(0,end);
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

	private static JSONObject ExportSingleAttack(JSONObject attackJSON, AttackMessage attack) {
		
		JSONObject targetJSON = new JSONObject();
		
		attackJSON.put("Target", targetJSON );
		
		Target t = attack.getTarget();
		Boolean isHit = attack.isTargetHit();
		
		JSONObject results = ExportDamageResults( targetJSON, t, isHit);
		attackJSON.put("Results", results);
		results.put("Hit", isHit);
		return results;
	}

	private static JSONObject ExportDamageResults(JSONObject targetJSON, Target t,
			Boolean isHit) {
		targetJSON.put("Name", t.getName());
		JSONObject statJSON = new JSONObject();
		
		if(t.hasStat("STUN")) {
			targetJSON.put("STUN", statJSON);
			statJSON.put("Current", t.getCurrentStat("STUN"));
			statJSON.put("Max", t.getCharacteristic("STUN").getBaseStat());
		}
		if(t.hasStat("BODY")) {
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

	public static void exportSweep(SweepMessageGroup group) {
		JSONObject attackJSON = new JSONObject();
		attackJSON.put("Type", "AttackSweepTargetsResult");
		JSONArray attacks = new JSONArray();
		attackJSON.put("Attacks", attacks);
		for(int i=0; i< group.getChildCount();i++) {
			if(group.getChild(i) instanceof EmbeddedBattleEventMessageGroup) {
				EmbeddedBattleEventMessageGroup emb = (EmbeddedBattleEventMessageGroup)group.getChild(i);
				BattleEvent be = emb.battleEvent;
				ActivateAbilityMessageGroup embeddedGroup = (ActivateAbilityMessageGroup) be.getPrimaryBattleMessageGroup();
				JSONObject embeddedAttackJSON = new JSONObject();
				attacks.add(embeddedAttackJSON);
				CreateJSONFromAbilityActivation(embeddedGroup, embeddedAttackJSON);
			}
		}
		WriteJSON(attackJSON);
		
	}
}
