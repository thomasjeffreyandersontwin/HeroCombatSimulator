package VirtualDesktop.Attack;

import java.io.File;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.sun.corba.se.spi.oa.ObjectAdapter;

import VirtualDesktop.Ability.AbilityAdapter;
import VirtualDesktop.Attack.AttackTarget.HitLocation;
import VirtualDesktop.Character.CharacterAdaptor;
import VirtualDesktop.Character.CharacterSensesAdapter;
import VirtualDesktop.Character.SenseAdapter;
import champions.ActivationInfo;
import champions.ActiveTargetPanel;
import champions.Battle;
import champions.BattleEngine;
import champions.BattleEvent;
import champions.CVList;
import champions.DefenseList;
import champions.Dice;
import champions.ObstructionList;
import champions.SelectedTargetModel;
import champions.Target;
import champions.TargetList;
import champions.attackTree.AttackParametersNode;
import champions.attackTree.AttackParametersPanel;
import champions.attackTree.AttackTreeModel;
import champions.attackTree.AttackTreeNode;
import champions.attackTree.AttackTreePanel;
import champions.attackTree.DefaultAttackTreeNode;
import champions.attackTree.HitLocationNode;
import champions.attackTree.HitLocationPanel;
import champions.attackTree.KnockbackEffectNode;
import champions.attackTree.KnockbackTargetNode;
import champions.attackTree.ObstructionNode;
import champions.attackTree.SelectTargetPanel;
import champions.attackTree.SingleTargetNode;
import champions.attackTree.SummaryNode;
import champions.attackTree.ToHitNode;
import champions.exception.BadDiceException;
import champions.exception.BattleEventException;
import champions.interfaces.IndexIterator;


public class AttackAdapter extends AbilityAdapter {
	
	
	protected AttackResultAdapter Result;
	public AttackAdapter(String name, CharacterAdaptor character) {
		super(name, character);
		
		battleEvent = UnderlyingAbility.getActivateAbilityBattleEvent(UnderlyingAbility, null, character.target);
		battleEvent.setSource(character.target);
		attackTarget = new AttackTarget(targetIndex, battleEvent);
		BattleEngine.setupAttackParameters(battleEvent);
	}

	public AttackResultAdapter activateAbility() {
		AttackTreeModel.treeModel.battleEvent = this.battleEvent;
		Dice dice;
        IndexIterator ii = battleEvent.getDiceIterator(".ATTACK");
        int dindex;
        while ( ii.hasNext() ) {
            dindex = ii.nextIndex();
            dice = battleEvent.getDiceRoll(dindex);
            if ( dice == null ) {
            	String size = battleEvent.getIndexedStringValue(dindex, "Die", "SIZE");    
                try {
					dice = new Dice( size, true);
				} catch (BadDiceException e) {
					e.printStackTrace();
					return null;
				}   
                battleEvent.setDiceRoll(dindex, dice);
                battleEvent.setDiceAutoRoll(dindex, true);
            }
        }
     	
        battle.addEvent( battleEvent );
        while(AttackTreePanel.Panel.isShowing()==false)
        {
        	try {Thread.sleep(100);}catch(Exception e) {}
        }
        DefaultAttackTreeNode node = DefaultAttackTreeNode.Node;
        node.setBattleEvent(battleEvent);
        
        
        if(getDefender()!=null) {
        	AttackResultAdapter result = new AttackResultAdapter(battleEvent, targetIndex);
        	Result = result;
        	return result;
        }
        return null;
	}
	
	public void pushWithStr(int push) {
			
			/*battleEvent.add("Normal.STR", new Integer(battleEvent.getSource().getCurrentStat("STR")), true);
			battleEvent.add( "Pushed.STR",push,true );
			battleEvent.add("Bonus.PUSHED_USED", push);
			battleEvent.add("Pushed.STR_USED", push);
			battleEvent.add("Bonus.PUSHED.STR", push);
			battleEvent.add("Bonus.PUSHED.STR",push);
			*/
			
			AttackParametersNode aNode = AttackParametersNode.Node;
			aNode.activateNode(true);	
			
			AttackParametersPanel.ad.pushedStrPAD.setValue(push);
			AttackTreePanel.Panel.model.advanceAndActivate(aNode,null);
		
		}
	
	public AttackResultAdapter completeAttack() {	
		AttackResultAdapter result=null;
		if(AttackTreePanel.Panel.isShowing()) {
			if(AttackTreePanel.Panel.model.battleEvent==null)
			{
				AttackTreePanel.Panel.model.battleEvent = battleEvent;
			}
			AttackTreePanel.Panel.advanceNode();
			
			result = new AttackResultAdapter(battleEvent, targetIndex);
			Result = result;
			try{
				
				Thread.sleep(700);
				AttackTreePanel.Panel.advanceNode();
				while(AttackTreePanel.Panel.isShowing()) {
					Thread.sleep(50);
					break;
				}
			}
			catch (Exception e) {e.printStackTrace();}
		}
	    	return result;
	}
	public void CancelAttack() {
		AttackTreePanel attackPanel = AttackTreePanel.defaultAttackTreePanel;
		attackPanel.cancelAttack();
		//targetNum=0;
		
	}
	public int getToHitRoll() {
		getToHitModifiers();
		CVList cvl = (CVList) battleEvent.getActivationInfo().getIndexedValue(targetIndex, "Target", "CVLIST");
		int OCV=  cvl.getSourceCV();
		int DCV= cvl.getTargetCV();
		return 11 + OCV - DCV;
	}
	public int getAttackerOCV() {
		getToHitModifiers();
		CVList cvl = (CVList) battleEvent.getActivationInfo().getIndexedValue(targetIndex, "Target", "CVLIST");
		return  cvl.getSourceCV();
	}
	public int getDamageClass() {
		return (int) battleEvent.getAbility().getDamageDie();
		
	}
	
	
	ToHitModifiers ToHitModifiers;
	public ToHitModifiers enterToHitModifiers() {
		return attackTarget.enterToHitModifiers();
	}
	public ToHitModifiers getToHitModifiers() 
	{
		return attackTarget.getToHitModifiers();
	}
		
	public CharacterSensesAdapter getAttackerSenses() {
		return attackTarget.getAttackerSenses();
	}
	
	public JSONObject processJSON (JSONObject attackJSON) {
		
		super.processJSON(attackJSON);
		processAttackerJSON(attackJSON);
		
		JSONObject r = attackTarget.processJSON(attackJSON);
		Result = attackTarget.Result;
		return r;

	}

	protected void processAttackerJSON(JSONObject attackJSON) {
		int push =0;
		if(attackJSON.get("PushedStr")!=null) {
			if( attackJSON.get("PushedStr") instanceof Long) {
				push = ((Long)attackJSON.get("PushedStr")).intValue();
			}
			else
				push=(int) attackJSON.get("PushedStr") ;
			pushWithStr(push);
		}
		
	}

	public enum CombatRole{ Attacker, Defender}
	public void Process() {
		AttackTreePanel.Panel.advanceNode();
	}
	public AttackResultAdapter getLastResult() {
		// TODO Auto-generated method stub
		return Result;
	}
	
	
	protected AttackTarget attackTarget;
	public CharacterAdaptor getDefender() 
	{
		return attackTarget.getDefender();
	}
	public void targetDefender(BasicTargetAdapter defender)
	{
		if(isAttackActivated()==false)
        {
        	Activate();
        }
		attackTarget.targetDefender(defender);
	}
	
	private boolean isAttackActivated() {
		// TODO Auto-generated method stub
		return AttackTreePanel.Panel.isShowing() && battleEvent.getAbility().getName().equals(this.UnderlyingAbility.getName());
	}

	public int getDefenderDCV() {
		return attackTarget.getDefenderDCV();
	}
	public void addObstruction(PhysicalObjectAdapter obstruction) {;
		attackTarget.addObstruction(obstruction);
	}
	public void removeObstruction(PhysicalObjectAdapter obstruction) {
		attackTarget.removeObstruction(obstruction);
	}
	public PhysicalObjectAdapter getObstruction(int i) {
		return attackTarget.getObstruction(i);
	}
	public int getObstructionCount() {
		return attackTarget.getObstructionCount();
	}
	public void ForceHit() {
		attackTarget.ForceHit();	
	}
	public void targetHitLocation(HitLocation location) {
		attackTarget.targetHitLocation(location);
	}
	public void placeObjectDirectlyBehindDefender(PhysicalObjectAdapter obj, int distance) 
	{
		attackTarget.placeObjectDirectlyBehindDefender(obj, distance);
	}
	private int getKnockbackDistance() {
		return attackTarget.getKnockbackDistance();
	}

	public AttackTarget getAttackTarget() {
		return attackTarget;
	}
}
