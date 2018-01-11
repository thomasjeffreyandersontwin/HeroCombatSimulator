package VirtualDesktop.Attack;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.FileWriter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import VirtualDesktop.Ability.AbilityAdapter;
import VirtualDesktop.Character.CharacterAdaptor;
import VirtualDesktop.Controller.GLOBALS;
import champions.ActivationInfo;
import champions.Battle;
import champions.BattleEngine;
import champions.BattleEvent;
import champions.CVMultiplePanel;
import champions.DefenseList;
import champions.ObstructionList;
import champions.Target;
import champions.TargetList;
import champions.attackTree.AttackParametersNode;
import champions.attackTree.AttackParametersPanel;
import champions.attackTree.AttackTreeModel;
import champions.attackTree.AttackTreeNode;
import champions.attackTree.AttackTreePanel;
import champions.attackTree.HitLocationNode;
import champions.attackTree.HitLocationPanel;
import champions.attackTree.KnockbackEffectNode;
import champions.attackTree.KnockbackNode;
import champions.attackTree.KnockbackSecondaryTargetNode;
import champions.attackTree.KnockbackTargetNode;
import champions.attackTree.ObstructionNode;
import champions.attackTree.ObstructionPanel;
import champions.attackTree.SelectTargetPanel;
import champions.attackTree.SingleTargetNode;
import champions.attackTree.SummaryNode;
import champions.attackTree.ToHitNode;
import champions.attackTree.ToHitPanel;
import champions.battleMessage.AbstractBattleMessageGroup;
import champions.battleMessage.ActivateAbilityMessageGroup;
import champions.battleMessage.AttackMessage;
import champions.battleMessage.BattleMessage;
import champions.battleMessage.KnockbackMessageGroup;
import champions.battleMessage.KnockbackSummaryGroup;
import champions.battleMessage.KnockbackSummaryMessage;
import champions.battleMessage.SingleAttackMessageGroup;
import champions.enums.KnockbackEffect;
import champions.event.PADValueEvent;
import champions.interfaces.IndexIterator;
import sun.rmi.server.Activation;


public class SingleAttackAdapter extends AbilityAdapter {
	protected SelectTargetPanel selectTargetPanel;
	protected AttackParametersPanel attackParmetersPanel;
	protected ToHitPanel ToHitPanel;
	protected ObstructionPanel ObstructionPanel;
	public int targetNum=0;
	
	public SingleAttackAdapter(String name, CharacterAdaptor character) {
		super(name, character);
		selectTargetPanel = SelectTargetPanel.ad;
	}

	public CharacterAdaptor Target;
	
	public CharacterAdaptor SetTargetByName(String targetName) {
		Target = new CharacterAdaptor(targetName);
		Battle battle = Battle.currentBattle;
    	TargetList targets = battle.getTargetList(true);
    	champions.Target underlyingTarget = targets.getTarget(targetName, true);
    	SetTarget(Target);
    	return Target;    		
		}

	
	public void SetTarget(CharacterAdaptor target) {
		Target = target;
		if(selectTargetPanel==null) {
			selectTargetPanel = SelectTargetPanel.ad;
		}	
			selectTargetPanel.selectTarget(target.target);

	}
	
	public void setAttackTarget(CharacterAdaptor character)
	{
		Target target= character.target;
		if(battleEvent==null) {
			battleEvent = UnderlyingAbility.getActivateAbilityBattleEvent(UnderlyingAbility, null, null);
			
		}
		ActivationInfo ai = battleEvent.getActivationInfo();
        if ( target != null ) {
        	 ai.removeTarget(target, ".ATTACK");
        }
        ai.addTarget(target, ".ATTACK");
        int tindex = ai.getTargetIndex(target, ".ATTACK");
        
        DefenseList dl = new DefenseList();
        BattleEngine.buildDefenseList(dl,target);
        ai.setDefenseList(tindex, dl);
        battle.addEvent( battleEvent );
            
	}
	public void SetKnockBackTargetByName(String targetName) {
		CharacterAdaptor knockTarget = new CharacterAdaptor(targetName);
		Battle battle = Battle.currentBattle;
		TargetList targets = battle.getTargetList(true);
		champions.Target underlyingTarget = targets.getTarget(targetName, true);
		if(selectTargetPanel==null) {
			selectTargetPanel = SelectTargetPanel.ad;
		}	
		selectTargetPanel.fireTargetingEvent( knockTarget.target , true);
		ShowSummary();
		
		  
		
	}
	public void StartSelectingTargets() {
		if(selectTargetPanel==null) {
			selectTargetPanel = SelectTargetPanel.ad;
		}	
		AttackTreePanel.Panel.model.advanceAndActivate(null,null);
		//selectTargetPanel.selectTarget(null);
	}
	public void EnterAttackParameters() {
		AttackTreeModel model = AttackTreeModel.treeModel;
		model.advanceAndActivate(AttackParametersNode.Node , AttackParametersNode.Node);
		
		attackParmetersPanel = AttackParametersPanel.getAttackParametersPanel(battleEvent);
	}
	
	public void EnterToHitParameters() {
		AttackTreeModel model = AttackTreeModel.treeModel;
			model.advanceAndActivate(ToHitNode.Node , ToHitNode.Node);
		
		ToHitPanel = ToHitPanel.getToHitPanel(battleEvent, Target.target, selectTargetPanel.getTargetGroup(), 0 );
	}
	
	public void setHitLocation(String hitLocation) {
		HitLocationNode node = HitLocationNode.Node;
		AttackTreeModel.treeModel.advanceAndActivate(node, node);
		HitLocationPanel panel = HitLocationPanel.Panel;
		panel.setHitLocation(hitLocation);
	}
	
	public void StartEnteringKnockback() {
		AttackTreeModel model = AttackTreeModel.treeModel;
		
		KnockbackTargetNode node = (KnockbackTargetNode)KnockbackTargetNode.Node;
		KnockbackEffectNode n = KnockbackEffectNode.Node;
		if (n.getBattleEvent()==null)
		{
			n.setBattleEvent(KnockbackTargetNode.Node.getBattleEvent());
		}

		model.advanceAndActivate(n ,n);	
		model.advanceAndActivate(SingleTargetNode.Node, SingleTargetNode.Node);			
	}
	
	public void StartAddObstructionInFrontOfTarget() {
		AttackTreeModel model = AttackTreeModel.treeModel;
		model.advanceAndActivate(ObstructionNode.Node , ObstructionNode.Node);
		
		ObstructionPanel = ObstructionPanel.getDefaultPanel(battleEvent, 0,selectTargetPanel.getTargetGroup() );
	}
	
	public champions.Target AddObstructionInFrontOfTarget(String name) {
		ObstructionList ol = battleEvent.getActivationInfo().getObstructionList(targetNum);
		CharacterAdaptor obstruction = new CharacterAdaptor(name);
		if(obstruction.target != null){
			ol.addObstruction(obstruction.target);
		}
		return null;
		
	}
	
	
	

	
	public void SetBurnStun(boolean burnStun) {
		attackParmetersPanel.burnStunPAD.setValue(burnStun);
		PADValueEvent evt = new PADValueEvent(attackParmetersPanel.burnStunPAD, 
				"autofireShots", false, attackParmetersPanel.burnStunPAD.getValue());
		attackParmetersPanel.PADValueChanging(evt);		
	}
	
	public void SetPushedStrength(int pushedStr) {
		attackParmetersPanel.pushedStrPAD.setValue(pushedStr);
		PADValueEvent evt = new PADValueEvent(attackParmetersPanel.pushedStrPAD, 
				"pushedStr", 0, attackParmetersPanel.pushedStrPAD.getValue());
		attackParmetersPanel.PADValueChanging(evt);		
	}
	
	private void updateCVListContainingModifier(String cvKey, Boolean active, Long amount) {
		Object oldVal=null;
		Object newVal=null;
		if( active==true) {
			newVal = "TRUE";
			oldVal = "FALSE";
		}
		else {
			newVal= "FALSE";
			oldVal = "TRUE";
		}
		PropertyChangeEvent evt = new PropertyChangeEvent(this, cvKey + ".ACTIVE", oldVal, newVal);
		ToHitPanel.cvPanel.propertyChange(evt);
		
		if(amount >0 || amount < 0)
		{
			newVal= ((Long)amount).intValue();
			oldVal = 0;
			evt = new PropertyChangeEvent(this, cvKey+".VALUE", oldVal, newVal);
			ToHitPanel.cvPanel.propertyChange(evt);
		}
		
		
		
	}

	public void SetGenericModifier(Long generic) {
		updateCVListContainingModifier("Source0", true, generic );	
		
	}

	public void SetOffHand(Boolean offHand) {
		updateCVListContainingModifier("Source1", offHand,0l);
		
	}

	public void SetUnfamiliarWeapon(Boolean unfam) {
		updateCVListContainingModifier("Source2", unfam,0l);
		
	}

	public void SetEncumbrance(Long encumbrance) {
		updateCVListContainingModifier("Source4", true, encumbrance );
		
	}

	public void SetSurpriseMove(Long surpriseMove) {
		updateCVListContainingModifier("Source3", true, surpriseMove );
		
	}
	
	public void SetConcealment(String hidden) {
		long hiddenMod= 0; 
		if(hidden=="Half Hidden") {
			hiddenMod= -2;
		}
		else {
			if(hidden=="Heads and Shoulders Visible") {
				hiddenMod= -4;
			}	
		}
		updateCVListContainingModifier("Source5", true, hiddenMod );
		
	}

	public void SetRange(int range) {
		long rangeMod = 0;
		if (range < 3) {
			rangeMod=0;
		}
		else {
			rangeMod = (long) (1 + Math.ceil((range - 3) / 3)) * -1;
		}
		updateCVListContainingModifier("Source6", true, rangeMod );
		
	}
	
	public void SetTargetGenericModifier(Long generic) {
		updateCVListContainingModifier("Target0", true, generic );	
		
	}
	
	public void SetAttackFromBehind(boolean fromBehind) {
		updateCVListContainingModifier("Target1", fromBehind,0l);	
	}
	
	public void SetAttackSurprised(boolean fromSurprise) {
		updateCVListContainingModifier("Target2", fromSurprise,0l);	
	}
	
	public void SetTargetEntangled(boolean entangled) {
		updateCVListContainingModifier("Target3", entangled,0l);	
	}
	
	public void SetTargetRecovering(boolean recovering) {
		updateCVListContainingModifier("Target4", recovering, 0l);	
	}
	
	public void ConfirmAttack() {
		super.ConfirmAttack();
		targetNum=0;
	}

	public void CancelAttack() {
		AttackTreePanel attackPanel = AttackTreePanel.defaultAttackTreePanel;
		attackPanel.cancelAttack();
		targetNum=0;
		
	}

	public void setFromBehind(Boolean fromBehind) {
		HitLocationNode node = HitLocationNode.Node;
		AttackTreeModel.treeModel.advanceAndActivate(node, node);
		HitLocationPanel panel = HitLocationPanel.Panel;
		panel.setFromBehind(fromBehind);
		
	}

	public void ShowSummary() {
		//StartSelectingTargets();
		SummaryNode node = SummaryNode.Node;
		AttackTreeModel.treeModel.advanceAndActivate(null, node);
		
	}

	public void Export(String token) {
		BattleEvent B = battleEvent;
		JSONObject attack = ExportBasedOnBattleEvent(token, B);
		
		WriteJSON(attack);

	}


	public JSONObject ExportBasedOnBattleEvent(String token, BattleEvent B) {
		JSONObject attack = CreateAbility(B);
		ExportSingleAttackResults(attack, B,0);
		ExportSingleKnockback( attack, B,0);
		attack.put("Token", token);
		return attack;
	}
	
	
	
	protected JSONObject CreateAbility(BattleEvent event) {
		String type= "AttackSingleTargetResult";
		JSONObject attackJSON = new JSONObject();
		attackJSON.put("Type", type);
		String ab=null;
		ab=event.getAbility().getName();
		attackJSON.put("Ability", ab);
		return attackJSON;
	}

protected  JSONObject ExportSingleAttackResults(JSONObject attackJSON, BattleEvent event, int index) {
	JSONObject targetJSON = new JSONObject();
	attackJSON.put("Target", targetJSON );
	
	Target t = event.getActivationInfo().getTarget(index);
	Boolean isHit = event.getActivationInfo().getTargetHit(index);
	JSONObject results = ExportDamageResults( targetJSON, t, isHit);
	attackJSON.put("Results", results);
	results.put("Hit", isHit);
	
	ObstructionList ol = event.getActivationInfo().getObstructionList(index);
	if(ol!=null) {
		for(int i=0;i<ol.size();i++) {
			Target ob = ol.getObstruction(i);
			if(ob!=null) {
				JSONObject obJSON = new JSONObject();
				JSONObject obstructResult = ExportDamageResults( obJSON, ob, true);
				obstructResult.put("Obstruction Name", ob.getName());
				attackJSON.put("Obstruction Result", obstructResult);
			
			}
		
		}
	}
	
	return results;
}

protected void ExportSingleKnockback(JSONObject attackJSON,BattleEvent event, int index) {
	JSONObject results = (JSONObject) attackJSON.get("Results");
	JSONObject knockback = new JSONObject();
	results.put("Knockback", knockback);
	int  tindex=index;
	
	Target t  = battleEvent.getActivationInfo().getTarget(tindex);
	int kbi = battleEvent.getKnockbackIndex(t, "KB");
	battleEvent.getKnockbackTarget(0);
	
	String name = t.getName();

	int distance = event.getKnockbackAmount(kbi);
	if(distance>0) {
		knockback.put("Distance", distance);
	
		if(event.getKnockbackEffect(kbi)== KnockbackEffect.POSSIBLECOLLISION) {
			JSONObject collision = new JSONObject();
		
			int i=0;
			IndexIterator kbindex = event.getActivationInfo().getTargetGroupIterator(".KB."+ name);
			while(kbindex.hasNext()) {
				int  j =  kbindex.nextIndex();
				Target sec  = battleEvent.getActivationInfo().getTarget(j);
				if (sec!=null) {
					if(event.getActivationInfo().isTargetKnockbackSecondary(j)==true) {
						results =  ExportDamageResults(collision, sec, true);	
						knockback.put("Obstacle Collision", collision);
						collision.put("Obstacle Damage Results", results);
					}
				
			}
		}
	}
		
		
		
	}
	
	//knockback.put("Breakfall Successful", true);
		
		
		
		
			
		
	
}
	
protected JSONObject ExportDamageResults(JSONObject targetJSON, Target t,Boolean isHit) {
	targetJSON.put("Name", t.getName());
	
	
	if(t.hasStat("STUN")) {
		JSONObject statJSON = new JSONObject();
		targetJSON.put("STUN", statJSON);
		statJSON.put("Current", t.getCurrentStat("STUN"));
		statJSON.put("Max", t.getCharacteristic("STUN").getBaseStat());
	}
	if(t.hasStat("BODY")) {
		JSONObject statJSON = new JSONObject();
		targetJSON.put("BODY", statJSON);
		statJSON.put("Current", t.getCurrentStat("BODY"));
		statJSON.put("Max", t.getCharacteristic("BODY").getBaseStat());
	}
	JSONObject results = new JSONObject();
	
	
	if( isHit== true){
		JSONArray effects = new JSONArray();
		results.put("Effects", effects);

		if(t.isDead()==true){
			effects.add("Dead");
		}
		if(t.isDying()==true){
			effects.add("Dying");
		}
		if(t.stunned==true){
			effects.add("Stunned");
		}
		if(t.unconscious==true){
			effects.add("Unconscious");
		}
		if(t.hasEffect("Partially Destroyed")){
			effects.add("Partially Destroyed");
		}
		if(t.hasEffect("Destroyed")){
			effects.add("Destroyed");
		}
		
	}
	return results;
}

public void WriteJSON(JSONObject attackJSON) {
	try {
		File f = new java.io.File(GLOBALS.EXPORT_PATH + "AbilityAttackResult.event");
		if(f.exists()) {
			Thread.sleep(500);
		}		
		FileWriter writer = new FileWriter(GLOBALS.EXPORT_PATH+"AbilityAttackResult.event");
		writer.write(attackJSON.toJSONString());
		writer.close();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}


public int getKnockbackDistance() {
	int target = this.targetNum;
	return getKnockbackDistanceForTarget(target);
	
}


public int getKnockbackDistanceForTarget(int target) {
	return battleEvent.getKnockbackDistance(target);
}


}