package VirtualDesktop.Attack.AreaEffect;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import VirtualDesktop.Roster.SingleAttackResults;
import champions.battleMessage.AbstractBattleMessageGroup;
import champions.battleMessage.ActivateAbilityMessageGroup;
import champions.battleMessage.AreaEffectAttackMessageGroup;
import champions.battleMessage.AttackMessage;
import champions.battleMessage.BattleMessage;
import champions.battleMessage.DefaultAttackMessage;
import champions.battleMessage.KnockbackMessageGroup;
import champions.battleMessage.KnockbackSummaryGroup;
import champions.battleMessage.KnockbackSummaryMessage;
import champions.battleMessage.SingleAttackMessageGroup;

public class AreaEffectAttackResults extends SingleAttackResults {
	public JSONObject buildFrom(AbstractBattleMessageGroup group) {
		JSONObject attackJSON = CreateAbility(group);
		
		CreateAreaEffectResultsFromAbilityActivation(group, attackJSON);
		CreateMultiKnockbackFromAbilityActivation(group, attackJSON);
		return null;
	}
	
	public void  CreateAreaEffectResultsFromAbilityActivation(AbstractBattleMessageGroup group, JSONObject attackJSON) {
		JSONArray targets = new JSONArray();
		attackJSON.put("Targets", targets );
		for(int i= 0 ;  i < group.getChildCount();i++) {
			BattleMessage bm = group.getChild(i);
			if (bm instanceof AreaEffectAttackMessageGroup){
				AreaEffectAttackMessageGroup areaAttack = (AreaEffectAttackMessageGroup)bm;
				ExportAllAttackResults(targets, areaAttack);
			}
		}
	}

	private void ExportAllAttackResults( JSONArray targets,
			AreaEffectAttackMessageGroup areaAttack) {
		for (int  j= 0;j< areaAttack.getChildCount();j++ ){
			if(areaAttack.getChild(j)!=null){
				if(areaAttack.getChild(j) instanceof DefaultAttackMessage){
					DefaultAttackMessage attackMessage = (DefaultAttackMessage) areaAttack.getChild(j);
					JSONObject target = new JSONObject();
					targets.add(target);
				
					ExportSingleAttackResults( target,  attackMessage);
				}
			}
		}
	}
	public void CreateMultiKnockbackFromAbilityActivation(AbstractBattleMessageGroup group, JSONObject attackJSON)
	{
		int targetNum=0;
		for(int i= 0 ;  i < group.getChildCount();i++) {
			BattleMessage bm = group.getChild(i);
			if(bm instanceof  KnockbackMessageGroup || bm instanceof KnockbackSummaryGroup) {
				targetNum=i;
				JSONObject results = (JSONObject) ((JSONObject) ((JSONArray)attackJSON.get("Targets")).get(targetNum)).get("Results");
				bm = getKnockBackNode(bm);
				if(bm !=null) {
					ExportSingleKnockback(results, (KnockbackSummaryMessage)bm);
				}
			}
		}
			
	}
	
	
}
