package VirtualDesktop.Attack.AreaEffect;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import VirtualDesktop.Attack.SingleAttack.SingleAttackAdapter;
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
import champions.interfaces.IndexIterator;

public class MultAttackAdapter extends SingleAttackAdapter {

	public MultAttackAdapter(String name, CharacterAdaptor character) {
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
	
	public JSONObject ExportBasedOnBattleEvent(String token, BattleEvent b) {
		String type= "AttackMultiTargetResult";
		JSONObject attackJSON = new JSONObject();
		attackJSON.put("Type", type);
		String ab=null;
		ab=b.getAbility().getName();
		attackJSON.put("Ability", ab);
		
		JSONArray targets = new JSONArray(); 
		attackJSON.put("Targets", targets );
		
		int i=0;
		
		
		IndexIterator index =battleEvent.getActivationInfo().getTargetGroupIterator(".ATTACK.AE");
		while(index.hasNext()) {
			int  j =  index.nextIndex();
			Target t  = battleEvent.getActivationInfo().getTarget(j);
			if(t!=null) {
				int tindex = battleEvent.getActivationInfo().getTargetIndex(t, ".ATTACK.AE") ;
				JSONObject targetJSON = new JSONObject();
				ExportSingleAttackResults(targetJSON, b,tindex);
				ExportSingleKnockback( targetJSON, b,tindex);
				targets.add(targetJSON);
			}
		}
		attackJSON.put("Token", token);
		return attackJSON;
	}
	
	public void Export(String token) {
		JSONObject attackJSON = ExportBasedOnBattleEvent(token, battleEvent);
		WriteJSON(attackJSON);
		
	}


}
