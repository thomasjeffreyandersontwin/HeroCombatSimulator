package VirtualDesktop.Attack.MultiAttack;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import VirtualDesktop.Attack.AttackResultAdapter;
import champions.Ability;
import champions.BattleEvent;
import champions.Target;
import champions.attackTree.AEAffectedTargetsNode;
import champions.attackTree.DefaultAttackTreeNode;
import champions.attackTree.SingleTargetNode;

public abstract class MultiAttackResultAdapter extends AttackResultAdapter {

	private String token;
	public MultiAttackResultAdapter(BattleEvent battleEvent, int tindex) {
		super(battleEvent, tindex);
		// TODO Auto-generated constructor stub
	}

	public JSONObject exportToJSON()
	{
		JSONObject attackResultJSON = new JSONObject();
		attackResultJSON.put("Ability", getAbilityName());
		attackResultJSON.put("Token", this.token);
		JSONArray affectedTargetsJSON = new JSONArray();
		attackResultJSON.put("Affected Targets", affectedTargetsJSON);
		for(int i=0; i< getAffectedTargetResults().size();i++)
		{
			JSONObject arJSON = new JSONObject();
			AttackResultAdapter res = getAffectedTargetResults().get(i);
			exportAffectedTargetToJSON(res,arJSON);
			Ability a  = res.battleEvent.getAbility();
			arJSON.put("Ability", a.getName());
			affectedTargetsJSON.add(arJSON);
			
		}
		return attackResultJSON;
	}
	
	public String getAbilityName() {
		return getActivationInfo().getAbility().getName();
	}
	
	public ArrayList<AttackResultAdapter> getAffectedTargetResults()
	{
		BattleEvent event = battleEvent;
		ArrayList<AttackResultAdapter> results = new ArrayList<AttackResultAdapter>();
		DefaultAttackTreeNode rootNode = getRootAttackNode();
		for(int i=0; i < rootNode.getChildCount();i++)
		{
			buildAffectedTargetResultFromTargetAndBattleEvent(event, results, i);
		}
		return results;
	}

	protected void buildAffectedTargetResultFromTargetAndBattleEvent(BattleEvent event,
		ArrayList<AttackResultAdapter> results, int i) {
		Target t = getSelectTargetingNode(i).getTarget();
		if(t!=null && t.getName()!="Hex") {
			int  tindex = event.getActivationInfo().getTargetIndex(t);
			AttackResultAdapter result;
			if(tindex!=-1) 
			{
				result = new AttackResultAdapter(event, tindex);
				results.add(result);
			}		
		}
	}
	
	protected abstract SingleTargetNode getSelectTargetingNode(int i);

	
	protected abstract DefaultAttackTreeNode getRootAttackNode();
	
	public void setToken(String guid) {
		this.token = guid;
		
	}
}
