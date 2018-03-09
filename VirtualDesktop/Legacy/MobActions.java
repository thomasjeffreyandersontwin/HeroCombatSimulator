package VirtualDesktop.Legacy;

import java.awt.event.ActionEvent;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.Action;

import VirtualDesktop.Legacy.MessageExporter.VirtualDesktopControllerAction;
import VirtualDesktop.Mob.MobEffect;
import champions.Roster;
import champions.Target;

public class MobActions {
	
	public static VirtualDesktopControllerAction GetMobLeaderToggleAction(Target t){
		return new VirtualDesktopControllerAction("Toggle Mob Leader") 
		{
			Roster myRoster; 
			Target target= t;
			public void actionPerformed(ActionEvent e) {
				String toggle="";
				target = this.getTarget();
		        if ( target != null ) {
		        	myRoster = target.getRoster();
		        	if (myRoster.MobLeader == target)
		        	{
		        		myRoster.MobLeader = null;
		        		myRoster.MobMode = false;
		              	toggle="";
		        	}
		        	else
		        	{
		        		myRoster.MobLeader = target;
		        		myRoster.MobMode=true;
		        		toggle="Off";
		        	}
		      
		            putValue(Action.NAME, "Toggle Mob Leader " + toggle);
		            MobEffect me = new MobEffect(); 
		            for(Target t: myRoster.getCombatants())
		            {
		            	if(t.hasEffect(me.getName()))
		            	{
		            		t.remove(me.getName());
		            	}
		            	t.addEffectEntry(me);
		            }
		            
		        }
			}
		};
}
		
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
  
