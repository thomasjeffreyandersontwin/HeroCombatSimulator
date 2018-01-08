package VirtualDesktop.Attack.AreaEffect;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.sun.media.sound.ModelAbstractChannelMixer;

import VirtualDesktop.Character.CharacterAdaptor;
import champions.Target;
import champions.TargetList;
import champions.attackTree.AttackTreeModel;
import champions.attackTree.AttackTreeNode;
import champions.attackTree.KnockbackEffectNode;
import champions.attackTree.KnockbackNode;
import champions.attackTree.KnockbackTargetNode;
import champions.attackTree.SelectTargetPanel;
import champions.attackTree.SingleTargetNode;

//eg {"Type":"AttackSingleTarget","Ability":"Spider Strike","Target":"Ogun", "PushedStr":12, "Generic":3,"Off Hand":true, "Unfamiliar Weapon":true, "Surprise Move":2	,"Encumbrance":-3}
public class AttackAreaEffectTargetsCommand extends AttackMultipleTargetsCommand {
	

	@Override
	public void ExecuteDesktopEventOnSimulatorBasedOnMessageType(JSONObject message, CharacterAdaptor character) throws Exception  {
		
		int areaEffectTargets =  ((JSONArray) message.get("Targets")).size();
		
		String abilityName = (String)message.get("Ability");
		character.ActivateAbilityByName(abilityName);
		AreaEffectAttackAdapter attack =  (AreaEffectAttackAdapter)character.ActiveAbility;
		
		//EnterAttackParameters(message, attack);
		
		JSONObject centerTarget = (JSONObject) message.get("Center");
		String centerTargetString = (String) centerTarget.get("Target");
		if(centerTargetString==null) 
		{
			centerTargetString = "Hex";	
		}
	//	attack.StartSelectingTargets();
		//InvokeSIngleAttack(message, attack, centerTargetString);
		
		
	//	EnterAttackForAllTargets(message, attack);
		
	//	attack.Export(this.Token);

	
		
	}

	

	

	
}
