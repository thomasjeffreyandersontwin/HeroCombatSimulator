package VirtualDesktop.Attack.MoveThrough;

import javax.swing.tree.TreeNode;

import org.json.simple.JSONObject;

import VirtualDesktop.Attack.AttackTargetCommand;
import VirtualDesktop.Character.CharacterAdaptor;
import champions.Ability;
import champions.Battle;
import champions.BattleEngine;
import champions.BattleEvent;
import champions.Target;
import champions.attackTree.AttackTreeModel;
import champions.attackTree.AttackTreeNode;
import champions.attackTree.AttackTreePanel;
import champions.attackTree.DefaultAttackTreeNode;
import champions.attackTree.EffectNode;
import champions.attackTree.EffectPanel;
import champions.attackTree.MoveByEffectNode;
import champions.attackTree.MoveThroughEffectNode;
import champions.attackTree.MovementManeuverSetupPanel;
import champions.powers.maneuverMoveThrough;

public class AttackMoveThroughTargetCommand extends AttackTargetCommand {

	public void ExecuteDesktopEventOnSimulatorBasedOnMessageType(JSONObject message,
			CharacterAdaptor character) throws Exception  {
		
		
		if(((String) message.get("Type")).equals("MoveThrough")){
			character.ActivateAbilityByName("Move Through");
		}
		else {
			if(((String) message.get("Type")).equals("MoveBy")){
				character.ActivateAbilityByName("Move By");
			}
		}
		
		MoveThroughAdapter movethrough = (MoveThroughAdapter) character.ActiveAbility;
		
		JSONObject movement = (JSONObject)message.get("MovementAbility");
		movethrough.setMovementAbility((String) movement.get("Movement"));
		movethrough.setDistance(((Long)movement.get("DistanceFromCollision")).intValue());
		
		JSONObject attackAbility = (JSONObject)message.get("AttackAbility");
		movethrough.setAttack((String) attackAbility.get("Ability"));
		movethrough.ConfirmAttack();
        
		String targetName = (String) attackAbility.get("Target");
		//ExecuteAttackOnTarget(attackAbility,movethrough, targetName);
		
		movethrough.UpdateAttackWithAdjustedDamageDice();		
		
		
	}
}
