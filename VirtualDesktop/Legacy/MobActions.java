package VirtualDesktop.Legacy;

import java.awt.event.ActionEvent;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.Action;

import VirtualDesktop.Legacy.MessageExporter.VirtualDesktopControllerAction;
import champions.Roster;

public class MobActions {
	
	
		
		public static VirtualDesktopControllerAction GetMobModeToggleAction(Roster aRoster){
			return new VirtualDesktopControllerAction("MobMode On") 
			{
				Roster roster=aRoster;
				public void actionPerformed(ActionEvent e) {
					String toggle="";
			        Roster myRoster = roster;
			        if ( myRoster != null ) {
			        	if(myRoster.MobMode== false)
			        		myRoster.MobMode= true;
			            else
			            	myRoster.MobMode= false;
			        	if(myRoster.MobMode == true){
			        		AssignRandomMobLeader();
			        	}
			        	super.actionPerformed(e);
			            if(roster.MobMode==true){
			            	toggle="Off";
			            	VirtualDesktop.Legacy.MessageExporter.exportEvent("MobMode",roster.MobLeader, roster);
			            }
			            else
			            	toggle="On";
			            putValue(Action.NAME, "MobMode " + toggle);
			            
			        }
				}
				
				public void AssignRandomMobLeader() {
			    	int targetCount = roster.getCombatants().size();
			    	Random randomGenerator = new Random();
			    	int mobLeaderIndex = randomGenerator.nextInt(targetCount);
			    	
			    	roster.MobLeader = roster.getCombatants().get(mobLeaderIndex);
			    }
			};
	}
}
  
