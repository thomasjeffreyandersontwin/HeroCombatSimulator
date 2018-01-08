package VirtualDesktop.Character;

import java.beans.PropertyChangeEvent;
import java.util.Iterator;

import VirtualDesktop.Ability.AbstractBattleClassAdapter;
import VirtualDesktop.Attack.ToHitModifiers;
import champions.BattleEvent;
import champions.Sense;
import champions.attackTree.AttackTreeModel;
import champions.attackTree.AttackTreePanel;
import champions.attackTree.PerceptionPanel;
import champions.attackTree.SourcePerceptionsNode;
import champions.attackTree.ToHitPanel;

public class SenseAdapter extends AbstractBattleClassAdapter {

	String Name;
	public SenseAdapter(BattleEvent battleEvent, int targetIndex, String name) {
		this.battleEvent = battleEvent;
		this.targetIndex = targetIndex;
		Name =name;
	}
	
	public void Activate() {
		
		AttackTreeModel model = AttackTreeModel.treeModel;
		SourcePerceptionsNode node = SourcePerceptionsNode.Node;
		node.activateNode(true);
		
		PerceptionPanel panel = PerceptionPanel.defaultPanel;
		panel.setBattleEvent(battleEvent);
		
		java.util.List<Sense> senses = panel.getSenseList();
		for (Iterator i = senses.iterator(); i.hasNext();) {
			Sense sense = (Sense) i.next();
			if(sense.getSenseName().equals(Name)){
				panel.setSelectedSense(sense);
			}
			
		}
		model.advanceAndActivate(node, null);
		
		//node.activateNode(true);
		/*ToHitModifiers mod = new ToHitModifiers(battleEvent, targetIndex);
		if(panel.getCVModifier().getDescription().equals("FULL OCV"))
		{
			
			//mod.setAttackerPerception(true);
			ToHitPanel.ad.cvPanel.cvList.add("Source6.ACTIVE",false,true);
		}
		else {
			ToHitPanel.ad.cvPanel.cvList.add("Source6.ACTIVE",true,true);
			ToHitPanel.ad.cvPanel.cvList.add("Source6.DESCRIPTION","Perception",true);
			ToHitPanel.ad.cvPanel.cvList.add("Source6.SPECIAL","PERCEPTION",true);
			ToHitPanel.ad.cvPanel.cvList.add("Source6.SPECIAL","PERCEPTION",true);
			ToHitPanel.ad.cvPanel.cvList.add("Source6.TYPE","MULTIPLE",true);
			ToHitPanel.ad.cvPanel.cvList.add("Source6.VALUE",0.5,true);
			mod.setAttackerPerception(true);
		}
		*/
		//AttackTreePanel.Panel.advanceNode();
		
	}

	public void DeActivate() {
		// TODO Auto-generated method stub
		
	}
	
	


}
