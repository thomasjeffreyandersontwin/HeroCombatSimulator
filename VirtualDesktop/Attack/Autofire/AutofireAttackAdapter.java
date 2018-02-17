package VirtualDesktop.Attack.Autofire;

import java.util.HashMap;
import java.util.Map;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import VirtualDesktop.Attack.AttackTargetCommand;
import VirtualDesktop.Attack.BasicTargetAdapter;
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

	private AttackParametersPanel attackParameterPanel;
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

	@Override
	public void Process() {
		try {		Thread.sleep(500);}catch(Exception e) {}
	}
	@Override
	protected MultiAttackResultAdapter BuildNewMultiAttackResult(BattleEvent battleEvent) {
		
		return  new AutofireAttackResultAdapter(battleEvent);
	}
	@Override
	protected void preProcessJSON(JSONObject attackJSON) {
	
		
		try {Thread.sleep(500);}catch(Exception e) {}
		if(attackJSON.get("Spray Fire")!=null) {
			this.SetAutoFireSprayMode((boolean) attackJSON.get("Spray Fire"));
		}
		int width=0;
		if(attackJSON.get("Width")!=null) {
			try {
				width = new Long((long) attackJSON.get("Width")).intValue();
			}
			catch(Exception e)
			{
				width = (int) attackJSON.get("Width");
				
			}
			this.SetAutoFireWidth(width );
		}
		if(attackJSON.get("Shots")!=null) {
			try {
				this.SetAutoFireShots((int) attackJSON.get("Shots"));
			}
			catch(Exception e)
			{
				this.SetAutoFireShots(((Long) attackJSON.get("Shots")).intValue());
				
			}
			
		}
		try {Thread.sleep(500);}catch(Exception e) {}
		
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
	@Override
	public DefaultAttackTreeNode getSelectTargetingNodeForTarget(Target target) {

		if( AutofireAttackNode.AFNode!=null) {
			for(int i=0; i < AutofireAttackNode.AFNode.getChildCount();i++) {
				SingleTargetNode node = (SingleTargetNode) AutofireAttackNode.AFNode.getChildAt(i).getChildAt(0);
				if(node.getTarget() == target) {
					return  (DefaultAttackTreeNode) node;
				}
			}
		}
		return null;
	}
}
