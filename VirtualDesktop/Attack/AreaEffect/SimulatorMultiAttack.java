package VirtualDesktop.Attack.AreaEffect;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import VirtualDesktop.Attack.SingleAttack.SimulatorSingleAttack;
import VirtualDesktop.Character.CharacterAdaptor;
import champions.BattleEvent;
import champions.Target;
import champions.attackTree.AttackTreeModel;
import champions.attackTree.KnockbackEffectNode;
import champions.attackTree.KnockbackNode;
import champions.attackTree.SingleTargetNode;
import champions.battleMessage.AreaEffectAttackMessageGroup;
import champions.battleMessage.BattleMessage;
import champions.battleMessage.DefaultAttackMessage;
import champions.battleMessage.SingleAttackMessageGroup;

public class SimulatorMultiAttack extends SimulatorSingleAttack {

	public SimulatorMultiAttack(String name, CharacterAdaptor character) {
		super(name, character);
		// TODO Auto-generated constructor stub
	}
	
	private KnockbackNode knockBackNode = null;
	public void EnterKnockbackForSpecificTarget(String knockbackTargetName, int i) {
		AttackTreeModel model = AttackTreeModel.treeModel;
		if(knockBackNode==null) {
			knockBackNode= (KnockbackNode) KnockbackNode.Node;
		}
		SingleTargetNode knockBackTargetNode = (SingleTargetNode) knockBackNode.getChildAt(i).getChildAt(1).getChildAt(0);
		KnockbackEffectNode effectNode = (KnockbackEffectNode) knockBackNode.getChildAt(i).getChildAt(1);
		model.advanceAndActivate(effectNode , knockBackTargetNode);		
		SetTargetByName(knockbackTargetName);
	}
	
	
	

	private Map<String, CharacterAdaptor> Targets = new HashMap<String, CharacterAdaptor>();
	public void AddTargetByName(String targetName) {
		Targets.put(targetName, SetTargetByName(targetName));
	}
	
	public void Export() {
		String type= "AttackMultiTargetResult";
		JSONObject attackJSON = new JSONObject();
		attackJSON.put("Type", type);
		String ab=null;
		ab=battleEvent.getAbility().getName();
		attackJSON.put("Ability", ab);
		
		JSONArray targets = new JSONArray(); 
		attackJSON.put("Targets", targets );
		
		
		
		int i=0;
		while(	battleEvent.getActivationInfo().getTarget(i)!=null)
		{
			JSONObject targetJSON = new JSONObject();
			ExportSingleAttackResults(targetJSON, battleEvent,i);
			ExportSingleKnockback( targetJSON, battleEvent,i);
			i++;
			targets.add(targetJSON);
		}
		WriteJSON(attackJSON);
		
	}
		/*attackJSON.put("Target", targetJSON );
		
		UnderlyingAbility.
		Target t = attack.getTarget();
		Boolean isHit = attack.isTargetHit();
		
		JSONObject results = ExportDamageResults( targetJSON, t, isHit);
		attackJSON.put("Results", results);
		results.put("Hit", isHit);
		return results;
		
		
		for(int i= 0 ;  i < group.getChildCount();i++) {
			BattleMessage bm = group.getChild(i);
			if(bm instanceof SingleAttackMessageGroup) {
				SingleAttackMessageGroup attack =(SingleAttackMessageGroup)bm;           			
				ExportSingleAttackResults(attackJSON,  attack);
			}
		}
		
	}
	
}






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
}*/
}
