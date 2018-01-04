package VirtualDesktop.Attack.Autofire;

import java.util.HashMap;
import java.util.Map;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import VirtualDesktop.Attack.AttackTargetCommand;
import VirtualDesktop.Attack.AreaEffect.MultAttackAdapter;
import VirtualDesktop.Character.CharacterAdaptor;
import champions.Battle;
import champions.BattleEvent;
import champions.Target;
import champions.TargetList;
import champions.event.PADValueEvent;
import champions.interfaces.IndexIterator;

public class AutofireAttackAdapter extends MultAttackAdapter {

	private int numberOfShots;
	private int widthOfAttack;
	
	private boolean spray;
	
	
	public AutofireAttackAdapter(String name, CharacterAdaptor character) {
		super(name, character);
		// TODO Auto-generated constructor stub
	}



	public void SetAutoFireSprayMode(boolean b) {	
		attackParmetersPanel.sprayButton.setSelected(b);
		attackParmetersPanel.sprayButtonActionPerformed(null);
	}



	public void SetAutoFireShots(int autofireShots) {
		attackParmetersPanel.autofireShots.setValue(autofireShots);
		PADValueEvent evt = new PADValueEvent(attackParmetersPanel.autofireShots, 
				"autofireShots", autofireShots, attackParmetersPanel.autofireShots.getValue());
		attackParmetersPanel.PADValueChanging(evt);		
	}



	public void SetAutoFireWidth(int autofireWidth) {
		attackParmetersPanel.autofireWidth.setValue(autofireWidth);
		PADValueEvent evt = new PADValueEvent(attackParmetersPanel.autofireShots, 
				"autofireWidth", autofireWidth, attackParmetersPanel.autofireWidth.getValue());
		attackParmetersPanel.PADValueChanging(evt);	
		
	}

	
	
	public JSONObject ExportBasedOnBattleEvent(String token, BattleEvent battleEvent) {
		
		String type= "AttackMultiTargetResult";
		JSONObject attackJSON = new JSONObject();
		attackJSON.put("Type", type);
		String ab=null;
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
		
		
		return attackJSON;
	}


}
