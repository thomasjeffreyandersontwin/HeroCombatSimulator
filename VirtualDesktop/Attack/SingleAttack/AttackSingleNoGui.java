package VirtualDesktop.Attack.SingleAttack;

import java.util.Iterator;

import org.json.simple.JSONObject;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

import VirtualDesktop.Attack.SingleAttack.SingleAttackAdapter;
import VirtualDesktop.Character.CharacterAdaptor;
import VirtualDesktop.Controller.AbstractDesktopCommand;
import champions.Ability;
import champions.ActivationInfo;
import champions.Battle;
import champions.BattleEngine;
import champions.BattleEvent;
import champions.DefenseList;
import champions.Dice;
import champions.Sense;
import champions.Target;
import champions.TargetList;
import champions.attackTree.AttackTreeModel;
import champions.attackTree.AttackTreeNode;
import champions.attackTree.PerceptionPanel;
import champions.attackTree.SelectTargetPanel;
import champions.attackTree.SourcePerceptionsNode;
import champions.battleMessage.SingleAttackMessageGroup;
import champions.exception.BattleEventException;
import champions.interfaces.IndexIterator;

//eg {"Type":"AttackSingleTarget","Ability":"Spider Strike","Target":"Ogun", "PushedStr":12, "Generic":3,"Off Hand":true, "Unfamiliar Weapon":true, "Surprise Move":2	,"Encumbrance":-3}

public class AttackSingleNoGui extends AbstractDesktopCommand {
	
	
	public static  SingleAttackAdapter AttackInProgress;
	public static JSONObject lastMessage;
	@Override
	public void ExecuteDesktopEventOnSimulatorBasedOnMessageType(JSONObject message,
			CharacterAdaptor character) throws Exception  {
		
		//get attacker
		Battle battle= Battle.currentBattle;
		Target attacker = battle.getActiveTarget();

		//get defender
		String targetName = (String) message.get("Target");
		TargetList targets = battle.getTargetList(true);
		champions.Target defender = targets.getTarget(targetName, true);
		
		//get attack
		String abilityName = (String)message.get("Ability");
		Ability attack = attacker.getAbility(abilityName);
		
		//create event
		BattleEvent	battleEvent = attack.getActivateAbilityBattleEvent(attack, null, attacker);
		battleEvent.setSource(attacker);
		//battle.addEvent( battleEvent );
		
//		AttackTreeModel.treeModel.advanceAndActivate(null,null);
		
		//add defender to event
		ActivationInfo ai = battleEvent.getActivationInfo();
        if ( defender != null ) {
        	 ai.removeTarget(defender, ".ATTACK");
        }
        ai.addTarget(defender, ".ATTACK");
        int tindex = ai.getTargetIndex(defender, ".ATTACK");
        
        DefenseList dl = new DefenseList();
        BattleEngine.buildDefenseList(dl,defender);
        ai.setDefenseList(tindex, dl);
        
        int tgindex = ai.addTargetGroup(".ATTACK");
        ai.setKnockbackGroup(tgindex, "KB");
        try {
            BattleEngine.resetDamageCalculation(battleEvent, ".ATTACK");
            BattleEngine.calculateDamage(battleEvent, attacker, ".ATTACK");
        }
        catch (BattleEventException bee) {}
            
        //set dice values
        SingleAttackMessageGroup singleAttackMessageGroup = new SingleAttackMessageGroup(attacker);
        battleEvent.openMessageGroup(singleAttackMessageGroup);
        
      //set dice from attack to battleEvent
        Dice dice;
        IndexIterator ii = battleEvent.getDiceIterator(".ATTACK");
        int dindex;
        while ( ii.hasNext() ) {
            dindex = ii.nextIndex();
            dice = battleEvent.getDiceRoll(dindex);
            if ( dice == null ) {
            	String size = battleEvent.getIndexedStringValue(dindex, "Die", "SIZE");    
                dice = new Dice( size, true);   
                battleEvent.setDiceRoll(dindex, dice);
                battleEvent.setDiceAutoRoll(dindex, true);
            }
        }
     //   AttackTreeModel.treeModel.advanceAndActivate(null,null);
        battle.addEvent( battleEvent );
        
        int ref = battleEvent.getActivationInfo().getTargetReferenceNumber(0);
        BattleEngine.processEffectsForTarget(battleEvent, ".ATTACK", ref);
        
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
	



}
