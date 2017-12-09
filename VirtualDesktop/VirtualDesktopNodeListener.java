package VirtualDesktop;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import champions.Roster;
import champions.Target;
import champions.abilityTree2.ATNode;
import champions.abilityTree2.ATRosterNode;
import champions.abilityTree2.ATTargetNode;


public class VirtualDesktopNodeListener implements TreeSelectionListener{
	public static Roster rosterSelected;
	public static Target targetSelected;
	@Override
	public void valueChanged(TreeSelectionEvent event) {
		Target t = null;
		Roster r=null;
		try{
			TreePath path= event.getPath();
			if(path.getLastPathComponent() instanceof ATTargetNode){
				ATNode node = (ATTargetNode) path.getLastPathComponent();
				this.targetSelected=node.getTarget();
				if(event.getPath().getLastPathComponent().getClass()==ATRosterNode.class) {
					ATRosterNode rnode=(ATRosterNode) event.getPath().getLastPathComponent();
					this.rosterSelected= rnode.getRoster();
				}
			}
		}catch(Exception e){}
		//VirtualDesktop.MessageExporter.exportEvent("Node Single CLick", t,r );
    	
    	
 
	}
}
