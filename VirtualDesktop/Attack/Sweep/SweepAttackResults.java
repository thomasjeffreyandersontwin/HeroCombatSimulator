package VirtualDesktop.Attack.Sweep;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import VirtualDesktop.Ability.AbilityExporter;
import VirtualDesktop.Ability.AbilityResultFactory;
import VirtualDesktop.Roster.SingleAttackResults;
import champions.BattleEvent;
import champions.battleMessage.AbstractBattleMessageGroup;
import champions.battleMessage.ActivateAbilityMessageGroup;
import champions.battleMessage.EmbeddedBattleEventMessageGroup;

public class SweepAttackResults extends SingleAttackResults {
	public JSONObject buildFrom(AbstractBattleMessageGroup group) {
		String type= getClass().getName();
		JSONObject attackJSON = new JSONObject();
		attackJSON.put("Type", type);
		
		JSONArray attacks = new JSONArray();
		attackJSON.put("Attacks", attacks);

		for(int i=0; i< group.getChildCount();i++) {
			if(group.getChild(i) instanceof EmbeddedBattleEventMessageGroup) {
				EmbeddedBattleEventMessageGroup emb = (EmbeddedBattleEventMessageGroup)group.getChild(i);
				BattleEvent be = emb.battleEvent;
				ActivateAbilityMessageGroup embeddedGroup = (ActivateAbilityMessageGroup) be.getPrimaryBattleMessageGroup();
				type = AbilityExporter.DetermineAttackType(embeddedGroup);
				SingleAttackResults abilityResult = AbilityResultFactory.GetAbilityForType(type);
				JSONObject resultJSON =  abilityResult.buildFrom(embeddedGroup);
				attacks.add(resultJSON);
			}
		}

		return attackJSON;
	}
}
