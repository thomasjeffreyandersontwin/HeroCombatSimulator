package VirtualDesktop.Attack;

import java.beans.PropertyChangeEvent;

import org.json.simple.JSONObject;

import VirtualDesktop.Ability.AbstractBattleClassAdapter;
import VirtualDesktop.Attack.ToHitModifiers.Concealment;
import champions.BattleEvent;
import champions.CVList;
import champions.attackTree.ToHitNode;
import champions.attackTree.ToHitPanel;

public class ToHitModifiers extends AbstractBattleClassAdapter {

	public enum c {

	}

	private ToHitPanel ToHitPanel;
	
	public Concealment setTargetConcealement; 
	
	public ToHitModifiers(BattleEvent battleEvent, int targetIndex) {
		this.battleEvent = battleEvent;
		this.targetIndex = targetIndex;
		
		ToHitPanel = ToHitPanel.getToHitPanel(battleEvent, getTarget(),".Attack", getTargetReferenceNumber());
		ToHitNode node = ToHitNode.Node;
		if(node==null)
		{
			node = new ToHitNode("to hit node");
		}
		node.activateNode(true);
		/*
		setGenericAttacker(0);
		setGenericDefender(0);
		setOffHand(false);;
		setUnfamiliarWeapon(false);
		setEncumbrance(0);
		setSurpriseMove(0);
		setTargetConcealment(Concealment.NotHidden);
		setTargetConcealment(Concealment.NotHidden);
		setRange(0);
		setAttackFromBehind(false);
		setDefenderSurprised(false);
		setDefenderEntangled(false);
		setTargetRecovering(false);
		*/

		
		
		
	}
	
	public void setGenericAttacker(int generic) {
		updateCVListContainingModifier("Source0", true, generic );	
	}
	public int getGenericAttacker() {
		return getCVModifierValue("Source0");
	}
	
	public void setGenericDefender(int generic) {
		updateCVListContainingModifier("Target0", true, generic );	
	}
	public int getGenericDefender() {
		return getCVModifierValue("Target0");
	}
	
	public void setOffHand(Boolean offHand) {
		updateCVListContainingModifier("Source1", offHand,0);
		
	}
	public int getOffHand() {
		return getCVModifierValue("Source1");
	}

	public void setUnfamiliarWeapon(Boolean unfam) {
		updateCVListContainingModifier("Source2", unfam,0);
		
	}
	public int getUnfamiliarWeapon() {
		return getCVModifierValue("Source2");
	}

	public void setEncumbrance(int encumbrance) {
		updateCVListContainingModifier("Source4", true, encumbrance );
		
	}
	public int getEncumbrance() {
		return getCVModifierValue("Source4");
	}
	
	public void setSurpriseMove(int surpriseMove) {
		updateCVListContainingModifier("Source3", true, surpriseMove );
		
	}
	public int getSurpriseMove() {
		return getCVModifierValue("Source3");
	}
	
	public void setAttackerPerception(boolean perception) {
		updateCVListContainingModifier("Source6", perception, 0 );
		
	}
	public int getAttakerPerception() {
		return getCVModifierValue("Source6");
	}
	
	
	public void setDefenderPerception(boolean perception) {
		updateCVListContainingModifier("Target3", perception, 0 );
		
	}
	public int getDefenderPerception() {
		return getCVModifierValue("Target3");
	}
	
	public void setTargetConcealment(Concealment c) {
		int hiddenMod= 0; 
		if(c==Concealment.HalfHidden) {
			hiddenMod= -2;
		}
		else {
			if(c==Concealment.HeadAndShouldersVisible) {
				hiddenMod= -4;
			}	
		}
		updateCVListContainingModifier("Source5", true, hiddenMod );
		
	}
	public int getTargetConcealment() {
		return getCVModifierValue("Source5");
		
	}
	public Concealment getTargetConcealmentType() {
		int hiddenMod=getTargetConcealment();
		if(hiddenMod==-2) {
			return Concealment.HalfHidden;
		}
		else {
			if(hiddenMod== -4)
			{
				return Concealment.HeadAndShouldersVisible;
			}	
		}
		return Concealment.NotHidden;
	}
	
	public void setRange(int range) {
		int rangeMod = 0;
		if (range < 3) {
			rangeMod=0;
		}
		else {
			rangeMod = (int) (1 + Math.ceil((range - 3) / 3) * -1);
		}
		updateCVListContainingModifier("Source6", true, rangeMod );
		
	}
	public int getRangeModifier() {
		return getCVModifierValue("Source6");
	}

	public void setAttackFromBehind(boolean fromBehind) {
		updateCVListContainingModifier("Target1", fromBehind,0);	
	}
	public int getAttackFromBehind() {
		// TODO Auto-generated method stub
		return getCVModifierValue("Target1");
	}
	
	public void setDefenderSurprised(boolean fromSurprise) {
		updateCVListContainingModifier("Target2", fromSurprise,0);	
	}
	public int getDefenderSurprised() {
		// TODO Auto-generated method stub
		return getCVModifierValue("Target3");
	}

	public void setDefenderEntangled(boolean entangled) {
		updateCVListContainingModifier("Target3", entangled,0);	
	}
	public int getDefenderEntangled() {
		// TODO Auto-generated method stub
		return getCVModifierValue("Target3");
	}
	
	public void setTargetRecovering(boolean recovering) {
		updateCVListContainingModifier("Target4", recovering, 0);	
	}
	public int getTargetRecovering() {
		// TODO Auto-generated method stub
		return  getCVModifierValue("Target4");
	}
	
	private void updateCVListContainingModifier(String cvKey, Boolean active, int amount) {
		Object oldVal=null;
		Object newVal=null;
		if( active==true ) {
			newVal = "TRUE";
			oldVal = "FALSE";
		}
		else{
			newVal= "FALSE";
			oldVal = "TRUE";
		}
		PropertyChangeEvent evt = new PropertyChangeEvent(this, cvKey + ".ACTIVE", oldVal, newVal);
		ToHitPanel.cvPanel.propertyChange(evt);
		
		if(amount >0 || amount < 0)
		{
			newVal= amount;
			oldVal = 0;
			evt = new PropertyChangeEvent(this, cvKey+".VALUE", oldVal, newVal);
			ToHitPanel.cvPanel.propertyChange(evt);

		}
		
		
		
	}
	
	public enum Concealment { HalfHidden, NotHidden, HeadAndShouldersVisible,}

	public int getCVModifierValue(String key) {
		CVList list = battleEvent.getActivationInfo().getCVList(targetIndex);
		try {
			if(list.getBooleanValue(key+".ACTIVE"))
				return list.getIntegerValue(key +".VALUE");
		}catch(Exception e) {}
		return 0;
	}

	public int getTotalModiferAmount() {
		int total =0;
		total+= getTargetRecovering()+ getDefenderEntangled();
		total = total - getDefenderSurprised();
		total = total - getAttackFromBehind();
		total = total - getGenericDefender();
		total = total + getRangeModifier();
		total = total + getTargetConcealment();
		total = total + getSurpriseMove();
		total = total + getEncumbrance();
		total = total + getUnfamiliarWeapon();
		total = total + getOffHand();
		total = total + getGenericAttacker();
		return total;
	}

	public void processJSON(JSONObject modifiersJSON) {
		if(modifiersJSON.get("From Behind")!=null) 
			setAttackFromBehind((boolean) modifiersJSON.get("From Behind"));
		if(modifiersJSON.get("Defender Entangled")!=null) 
			setDefenderEntangled((boolean) modifiersJSON.get("Defender Entangled"));
		if(modifiersJSON.get("Defender Surprised")!=null) 
			setDefenderSurprised((boolean) modifiersJSON.get("Defender Surprised"));
		if(modifiersJSON.get("Range")!=null) 
			setRange(((Long) modifiersJSON.get("Range")).intValue());
		if(modifiersJSON.get("Target Concealment")!=null) 
			setTargetConcealment((Concealment) modifiersJSON.get("Target Concealment"));
		if(modifiersJSON.get("Defender Perception")!=null) 	
			setDefenderPerception((boolean) modifiersJSON.get("Defender Perception"));
		if(modifiersJSON.get("Attacker Perception")!=null) 
			setAttackerPerception((boolean) modifiersJSON.get("Attacker Perception"));
		if(modifiersJSON.get("Surprise Move")!=null) 
			setSurpriseMove((int) modifiersJSON.get("Surprise Move"));
		if(modifiersJSON.get("Encumbrance")!=null) 
			setEncumbrance(((Long) modifiersJSON.get("Encumbrance")).intValue());
		if(modifiersJSON.get("Unfamiliar Weapon")!=null)
			setUnfamiliarWeapon((boolean) modifiersJSON.get("Unfamiliar Weapon"));
		if(modifiersJSON.get("Target Recovering")!=null)
			setOffHand((boolean) modifiersJSON.get("Target Recovering"));
		if(modifiersJSON.get("Generic Defender")!=null)
			setGenericDefender(((Long) modifiersJSON.get("Generic Defender")).intValue());
		if(modifiersJSON.get("Generic Attacker")!=null)
			setGenericAttacker(((Long) modifiersJSON.get("Generic Attacker")).intValue());
		if(modifiersJSON.get("Target Recovering")!=null) 
			setTargetRecovering((boolean) modifiersJSON.get("Target Recovering"));

		}
}
