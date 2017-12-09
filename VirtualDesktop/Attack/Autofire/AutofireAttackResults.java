package VirtualDesktop.Attack.Autofire;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import VirtualDesktop.Attack.AreaEffect.AreaEffectAttackResults;
import champions.battleMessage.AFShotMessageGroup;
import champions.battleMessage.AbstractBattleMessageGroup;
import champions.battleMessage.ActivateAbilityMessageGroup;
import champions.battleMessage.AreaEffectAttackMessageGroup;
import champions.battleMessage.BattleMessage;
import champions.battleMessage.KnockbackMessageGroup;
import champions.battleMessage.KnockbackSummaryGroup;
import champions.battleMessage.KnockbackSummaryMessage;
import champions.battleMessage.SingleAttackMessageGroup;

public class AutofireAttackResults extends AreaEffectAttackResults {
	public JSONObject buildFrom(AbstractBattleMessageGroup group) {
		JSONObject attackJSON = CreateAbility(group);
		
		CreateAreaEffectResultsFromAbilityActivation(group, attackJSON);
		CreateMultiKnockbackFromAbilityActivation(group, attackJSON);
		return attackJSON;
	}
	
	
	
	public void  CreateAreaEffectResultsFromAbilityActivation(AbstractBattleMessageGroup group, JSONObject attackJSON) {
		JSONArray targets = new JSONArray();
		attackJSON.put("Targets", targets );
		for(int i= 0 ;  i < group.getChildCount();i++) {
			BattleMessage bm = group.getChild(i);
			if(bm.getClass() == AFShotMessageGroup.class) {
				SingleAttackMessageGroup a= (SingleAttackMessageGroup) ((AFShotMessageGroup)bm).attackMessage;
				JSONObject attack = new JSONObject();
				targets.add(attack);
				ExportSingleAttackResults(attack,a);
			}
		}
	}
	
	public void CreateMultiKnockbackFromAbilityActivation(AbstractBattleMessageGroup group, JSONObject attackJSON)
	{
		int targetNum=0;
		for(int i= 0 ;  i < group.getChildCount();i++) {
			BattleMessage bm = group.getChild(i);
			if(bm instanceof  KnockbackMessageGroup || bm instanceof KnockbackSummaryGroup) {
				
				JSONObject results = (JSONObject) ((JSONObject) ((JSONArray)attackJSON.get("Targets")).get(targetNum)).get("Results");
				targetNum++;
				bm = getKnockBackNode(bm);
				if(bm !=null) {
					ExportSingleKnockback(results, (KnockbackSummaryMessage)bm);
				}
			}
		}
			
	}
	
	
}
