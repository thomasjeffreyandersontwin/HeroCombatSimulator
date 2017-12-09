package VirtualDesktop.Ability;

import org.json.simple.JSONObject;

import champions.battleMessage.AbstractBattleMessageGroup;
import champions.battleMessage.ActivateAbilityMessageGroup;
import champions.battleMessage.SweepMessageGroup;

public class AbilityResults {
	public JSONObject buildFrom(AbstractBattleMessageGroup group) {
		
		JSONObject attackJSON = CreateAbility(group);
		
		return null;
	}

	protected JSONObject CreateAbility(AbstractBattleMessageGroup group) {
		String type= getClass().getName();
		JSONObject attackJSON = new JSONObject();
		attackJSON.put("Type", type);
		String ab=null;
		if(group instanceof ActivateAbilityMessageGroup) {
			ab=((ActivateAbilityMessageGroup) group).ability.getName();
		}
		attackJSON.put("Ability", ab);
		return attackJSON;
	}
}
