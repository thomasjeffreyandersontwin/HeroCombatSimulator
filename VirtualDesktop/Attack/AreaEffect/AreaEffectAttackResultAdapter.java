package VirtualDesktop.Attack.AreaEffect;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import VirtualDesktop.Attack.AttackResultAdapter;
import VirtualDesktop.Attack.PhysicalObjectAdapter;
import champions.BattleEvent;
import champions.DetailList;
import champions.Target;
import champions.attackTree.AEAffectedTargetsNode;
import champions.attackTree.AttackTreePanel;
import champions.attackTree.SingleTargetNode;

public class AreaEffectAttackResultAdapter extends AttackResultAdapter{

	public AreaEffectAttackResultAdapter(BattleEvent battleEvent) 
	{
		super(battleEvent, -1);
		// TODO Auto-generated constructor stub
	}

	
	public boolean attackHitCenter() 
	{
		return  battleEvent.getActivationInfo().getTargetHit(0);
		
	}
	
	public ArrayList<AttackResultAdapter> getAffectedTargetResults()
	{
		ArrayList<AttackResultAdapter> results = new ArrayList<AttackResultAdapter>();
		AEAffectedTargetsNode aeNode = (AEAffectedTargetsNode) AEAffectedTargetsNode.AENode;
		for(int i=0; i < aeNode.getChildCount();i++)
		{
			Target t = ((SingleTargetNode) aeNode.getChildAt(i)).getTarget();
			int  tindex = getActivationInfo().getTargetIndex(t);
			AttackResultAdapter result;
			if(tindex!=-1) 
			{
				result = new AttackResultAdapter(battleEvent, tindex);
				results.add(result);
			}		
		}
		return results;
	}
	
	public JSONObject exportToJSON()
	{
		JSONObject attackResultJSON = new JSONObject();
		attackResultJSON.put("Ability", getActivationInfo().getAbility().getName());
		
		JSONArray affectedTargetsJSON = new JSONArray();
		attackResultJSON.put("Affected Targets", affectedTargetsJSON);
		for(int i=0; i< getAffectedTargetResults().size();i++)
		{
			JSONObject arJSON = new JSONObject();
			AttackResultAdapter res = getAffectedTargetResults().get(i);
			exportAffectedTargetToJSON(res,arJSON);
			affectedTargetsJSON.add(arJSON);
			
		}
		return attackResultJSON;
	}


}
