package VirtualDesktop.Attack.SingleAttack;

import java.util.Iterator;

import org.json.simple.JSONObject;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

import VirtualDesktop.Attack.SingleAttack.SingleAttackAdapter;
import VirtualDesktop.Character.CharacterAdaptor;
import VirtualDesktop.Controller.AbstractDesktopCommand;
import champions.BattleEvent;
import champions.Sense;
import champions.Target;
import champions.TargetList;
import champions.attackTree.AttackTreeModel;
import champions.attackTree.AttackTreeNode;
import champions.attackTree.PerceptionPanel;
import champions.attackTree.SelectTargetPanel;
import champions.attackTree.SourcePerceptionsNode;

//eg {"Type":"AttackSingleTarget","Ability":"Spider Strike","Target":"Ogun", "PushedStr":12, "Generic":3,"Off Hand":true, "Unfamiliar Weapon":true, "Surprise Move":2	,"Encumbrance":-3}

public class AttackSingleTargetCommand extends AbstractDesktopCommand {
	
	
	public static  SingleAttackAdapter AttackInProgress;
	public static JSONObject lastMessage;
	@Override
	public void ExecuteDesktopEventOnSimulatorBasedOnMessageType(JSONObject message,
			CharacterAdaptor character) throws Exception  {
		
		String abilityName = (String)message.get("Ability");
		character.ActivateAbilityByName(abilityName);
	
		SingleAttackAdapter attack = (SingleAttackAdapter) character.ActiveAbility;
		AttackInProgress = attack;
		EnterAttackParameters(message, attack);
		
		String targetName = (String) message.get("Target");
		ExecuteAttackOnTarget(message, attack, targetName);


	}
	
	public void EnterKnockbackCollision(SingleAttackAdapter attack, JSONObject message) {
		JSONObject potCol = (JSONObject)message.get("Potential Collision");
		if (potCol!=null) {
			Long distanceToCollision = (Long) potCol.get("Distance From Target");
			String knockbackTargetName = (String) potCol.get("Obstacle");
			if(knockbackTargetName!=null){
				attack.StartEnteringKnockback();
				int knockbackDistance = attack.getKnockbackDistance();
				if(knockbackDistance  >=distanceToCollision )
					attack.SetKnockBackTargetByName(knockbackTargetName);
				
			}
		}
		
	}
	

	public void ExecuteAttackOnTarget(JSONObject message, SingleAttackAdapter attack, String targetName) {
		attack.StartSelectingTargets();
		InvokeSIngleAttack(message, attack, targetName);
		attack.Export(Token);
		
	}



	protected void InvokeSIngleAttack(JSONObject message, SingleAttackAdapter attack, String targetName) {
		InvokeSingleAttackWithoutKnockback(message, attack, targetName);
		EnterKnockbackCollision( attack,  message);


		
	}

	protected void InvokeSingleAttackWithoutKnockback(JSONObject message, SingleAttackAdapter attack, String targetName) {
		lastMessage = message;
		CharacterAdaptor target = new CharacterAdaptor(targetName);

		attack.SetTarget(target);
		EnterToHitParameters(attack, message);
		SelectTargetingSense(attack, message);
		EnterHitLocation(message, attack);
		AddObstructions(message, attack);
	}



	private void EnterHitLocation(JSONObject message, SingleAttackAdapter attack) {
		attack.setHitLocation((String) message.get("Hit Location"));
		attack.setFromBehind((Boolean) message.get("From Behind"));
	}

	private void SelectTargetingSense(SingleAttackAdapter attack, JSONObject message) {
		String targetingSense = (String) message.get("Targeting Sense");
		if(targetingSense!=null)
		{
			AttackTreeModel model = AttackTreeModel.treeModel;
			SourcePerceptionsNode node =SourcePerceptionsNode.Node;
			model.advanceAndActivate(node, node);
			
			PerceptionPanel panel = PerceptionPanel.defaultPanel;
			
			java.util.List<Sense> senses = panel.getSenseList();
			for (Iterator i = senses.iterator(); i.hasNext();) {
				Sense sense = (Sense) i.next();
				if(sense.getSenseName().equals(targetingSense)){
					panel.setSelectedSense(sense);
				}
				
			}
		}
		
	}



	protected void AddObstructions(JSONObject message, SingleAttackAdapter attack) {
		String obstacle = (String) message.get("Obstruction");
		attack.StartAddObstructionInFrontOfTarget();
		if(obstacle!=null) {
			
			attack.AddObstructionInFrontOfTarget(obstacle);
		}
		attack.ShowSummary();
	}

	public void EnterAttackParameters(JSONObject message, SingleAttackAdapter attack) {
		attack.EnterAttackParameters();
		Boolean burnStun = (Boolean) message.get("BurnStun");
		if(burnStun !=null) {
			attack.SetBurnStun(burnStun);
		}
		Long pushedStr = (Long) message.get("PushedStr");
		if(pushedStr !=null) {
			attack.SetPushedStrength(pushedStr.intValue());
		}
	}

	protected void EnterToHitParameters(SingleAttackAdapter attack, JSONObject message) {
		attack.EnterToHitParameters();
		Long generic = (Long) message.get("Generic");
		if(generic !=null) {
			attack.SetGenericModifier(generic);
		}
		
		Boolean offHand = (Boolean) message.get("Off Hand");
		if(offHand !=null) {
			attack.SetOffHand(offHand);
		}
		
		Boolean unfam = (Boolean) message.get("Unfamiliar Weapon");
		if(unfam !=null) {
			attack.SetUnfamiliarWeapon(unfam);
		}
		
		Long surpriseMove = (Long) message.get("Surprise Move");
		if(surpriseMove !=null) {
			attack.SetSurpriseMove(surpriseMove);
		}
		
		Long encumbrance = (Long) message.get("Encumbrance");
		if(encumbrance !=null) {
			attack.SetEncumbrance(encumbrance);
		}
		
		String concealment= (String) message.get("Concealment");
		if(concealment !=null) {
			attack.SetConcealment(concealment);
		}
		
		Long range = (Long) message.get("Range");
		if(range !=null) {
			attack.SetEncumbrance(range);
		}
		
		Long targetGeneric = (Long) message.get("Target Generic");
		if(targetGeneric !=null) {
			attack.SetEncumbrance(targetGeneric);
		}
		
		Boolean fromBehind = (Boolean) message.get("From Behind");
		if(fromBehind !=null) {
			attack.SetAttackFromBehind(fromBehind);
		}
		
		Boolean surprised = (Boolean) message.get("Surprised");
		if(surprised !=null) {
			attack.SetAttackSurprised(surprised);
		}
		
		Boolean recovering = (Boolean) message.get("Recovering");
		if(recovering !=null) {
			attack.SetTargetRecovering(recovering);
		}
		
		Boolean entangled = (Boolean) message.get("Entangled");
		if(entangled !=null) {
			attack.SetTargetEntangled(entangled);
		}
		
		if(range!=null) {
		attack.SetRange(range.intValue());
		}
		attack.ConfirmAttack();
		
			
	}
}
