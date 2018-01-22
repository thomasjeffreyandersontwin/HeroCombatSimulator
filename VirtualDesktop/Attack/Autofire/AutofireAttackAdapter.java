package VirtualDesktop.Attack.Autofire;

import java.util.HashMap;
import java.util.Map;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import VirtualDesktop.Attack.AttackTargetCommand;
import VirtualDesktop.Attack.AreaEffect.AreaEffectAttackResultAdapter;
import VirtualDesktop.Attack.MultiAttack.MultiAttackAdapter;
import VirtualDesktop.Attack.MultiAttack.MultiAttackResultAdapter;
import VirtualDesktop.Character.CharacterAdaptor;
import champions.Battle;
import champions.BattleEngine;
import champions.BattleEvent;
import champions.Target;
import champions.TargetList;
import champions.attackTree.AFShotNode;
import champions.attackTree.AttackParametersNode;
import champions.attackTree.AttackParametersPanel;
import champions.attackTree.AttackTreePanel;
import champions.attackTree.AutofireAttackNode;
import champions.attackTree.AutofireSprayAttackNode;
import champions.attackTree.DefaultAttackTreeNode;
import champions.attackTree.SingleTargetNode;
import champions.event.PADValueEvent;
import champions.interfaces.IndexIterator;

public class AutofireAttackAdapter extends MultiAttackAdapter {

	private int numberOfShots;
	private int widthOfAttack;
	private AttackParametersPanel attackParameterPanel;
	private boolean spray;
	
	
	public AutofireAttackAdapter(String name, CharacterAdaptor character) {
		super(name, character);
	}

	

	public void SetAutoFireSprayMode(boolean b) {	
		AttackParametersNode.Node.activateNode(true);
		attackParameterPanel = AttackParametersPanel.ad;

		attackParameterPanel.sprayButton.setSelected(b);
		attackParameterPanel.sprayButtonActionPerformed(null);
	}



	public void SetAutoFireShots(int autofireShots) {
		AttackParametersNode.Node.activateNode(true);
		attackParameterPanel = AttackParametersPanel.ad;
		
		attackParameterPanel.autofireShots.setValue(autofireShots);
		PADValueEvent evt = new PADValueEvent(attackParameterPanel.autofireShots, 
				"autofireShots", autofireShots, attackParameterPanel.autofireShots.getValue());
		attackParameterPanel.PADValueChanging(evt);		
	}



	public void SetAutoFireWidth(int autofireWidth) {
		AttackParametersNode.Node.activateNode(true);
		attackParameterPanel = AttackParametersPanel.ad;
		
		attackParameterPanel.autofireWidth.setValue(autofireWidth);
		PADValueEvent evt = new PADValueEvent(attackParameterPanel.autofireShots, 
				"autofireWidth", autofireWidth, attackParameterPanel.autofireWidth.getValue());
		attackParameterPanel.PADValueChanging(evt);	
		
	}

	
	
	public JSONObject ExportBasedOnBattleEvent(String token, BattleEvent battleEvent) {
		
		String type= "AttackMultiTargetResult";
		JSONObject attackJSON = new JSONObject();
		attackJSON.put("Type", type);
		/*String ab=null;
		ab=battleEvent.getAbility().getName();
		attackJSON.put("Ability", ab);
		
		JSONArray targets = new JSONArray(); 
		attackJSON.put("Targets", targets );
		
		
		attackJSON.put("Token", token);
		
		int shots = (int) battleEvent.getActivationInfo().getValue("Attack.SHOTS");
		
		for(int i=1; i<=shots; i++) {
			IndexIterator index =battleEvent.getActivationInfo().getTargetGroupIterator(".ATTACK.Shot "+i);
			while(index.hasNext()) {
				int  j =  index.nextIndex();
				Target t  = battleEvent.getActivationInfo().getTarget(j);
				if(t!=null) {
					int tindex = battleEvent.getActivationInfo().getTargetIndex(t, ".ATTACK.Shot "+i) ;
					JSONObject targetJSON = new JSONObject();
					ExportSingleAttackResults(targetJSON, battleEvent,tindex);
					ExportSingleKnockback( targetJSON, battleEvent,tindex);
					
					targets.add(targetJSON);
				}
				
			}
		}
		*/
		
		return attackJSON;
	}



	@Override
	public void Process() {
		try {		Thread.sleep(500);}catch(Exception e) {}
	}




	@Override
	protected MultiAttackResultAdapter BuildNewMultiAttackResult(BattleEvent battleEvent) {
		
		return  new AutofireAttackResultAdapter(battleEvent);
	}


	@Override
	protected DefaultAttackTreeNode getRootAttackNode() {
		   return AutofireAttackNode.AFNode;
          
	}
	@Override
	protected SingleTargetNode getSelectTargetingNode(int i) {
		DefaultAttackTreeNode rootNode =  getRootAttackNode();
		 AFShotNode afsn= (AFShotNode) rootNode.getChildAt(i);
         return (SingleTargetNode) afsn.getChildAt(0);
	}


}
