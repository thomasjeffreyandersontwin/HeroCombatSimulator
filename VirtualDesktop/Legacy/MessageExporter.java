package VirtualDesktop.Legacy;

import champions.*;
import champions.Character;

import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import champions.battleMessage.*;
import champions.interfaces.AbilityIterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;

public class MessageExporter {

	public static void exportMessage(BattleMessage message, BattleMessageGroup messageGroup){
		try{
		String output =  "";
		boolean kbheader=false;
		
		
		
		
		
		if(message instanceof ENDSummaryMessage ){
		
			if(messageGroup instanceof ActivateAbilityMessageGroup ){
				ActivateAbilityMessageGroup activity=(ActivateAbilityMessageGroup) messageGroup;
				Ability ability=activity.activateAbilityMessage.ability;
				
				output=output+"ability:\r\n\tName: " + ability.getName() + "\r\n";
				output=output+"character:\r\n\tname:"+ability.getSource().getName()+ "\r\n";
				output=output+"\tCrowd:\r\n\t\tname:"+ability.getSource().getRoster().getName();
				
				Integer CurrStun= ability.getSource().getCurrentStat("STUN");
				Integer MaxStun = ability.getSource().getCharacteristic("STUN").getBaseStat();
				
				Integer CurrEndurance= ability.getSource().getCurrentStat("END");
				Integer MaxEndurance = ability.getSource().getCharacteristic("END").getBaseStat();
				
				output=output+ "\r\n\tMaxStun:" + MaxStun;
				output=output+ "\r\n\tMaxEndurance:" + MaxEndurance;
				output=output+ "\r\n\tCurrStun:" + CurrStun;
				output=output+ "\r\n\tCurrEndurance:" + CurrEndurance + "\r\n";
				
				
				for (int  i= 0;i< activity.getChildCount()-1;i++ ){
					Object child = activity.getChild(i);
					if (child instanceof SingleAttackMessageGroup){
						output+= "type: single\r\n";
						SingleAttackMessageGroup attack= (SingleAttackMessageGroup)child;
						
						
						output+= "target: \r\n\tname: " + attack.getTarget().getName() + "\r\n";
						
						CurrStun= attack.getTarget().getCurrentStat("STUN");
						MaxStun = attack.getTarget().getCharacteristic("STUN").getBaseStat();
						
						CurrEndurance= attack.getTarget().getCurrentStat("END");
						MaxEndurance = attack.getTarget().getCharacteristic("END").getBaseStat();
						
						output=output+ "\r\n\tMaxStun:" + MaxStun;
						output=output+ "\r\n\tMaxEndurance:" + MaxEndurance;
						output=output+ "\r\n\tCurrStun:" + CurrStun;
						output=output+ "\r\n\tCurrEndurance:" + CurrEndurance + "\r\n";
						
						output=output+"\tRoster:\r\n\t\tname:"+attack.getTarget().getRoster().getName()+ "\r\n";
						if( attack.isTargetHit()== true){
							output+= "result: hit\r\n";
							output+= "Effects: \r\n";
							if(attack.getTarget().isDead()==true){
								output+= "\t- dead\r\n";
							}
							if(attack.getTarget().isDying()==true){
								output+= "\t- dying\r\n";
							}
							if(attack.getTarget().stunned==true){
								output+= "\t- Stunned\r\n";
							}
							if(attack.getTarget().unconscious==true){
								output+= "\t- Unconsious\r\n";
							}
						}
						else{
							output+= "result:miss \r\n ";
						}
					}
					if (child instanceof AreaEffectAttackMessageGroup){
						AreaEffectAttackMessageGroup attack= (AreaEffectAttackMessageGroup)child;
						output+= "type: area effect\r\n";
						output+= "IndividualAttacks\r\n";
						for (int  j= 0;j< attack.getChildCount();j++ ){
							if(attack.getChild(j)!=null){
								if(attack.getChild(j) instanceof DefaultAttackMessage){
									DefaultAttackMessage individualAttack= (DefaultAttackMessage) attack.getChild(j);
									output+= "\t- \r\n";
									
									output+= "\t\t character:\r\n\t\t\tName:" +individualAttack.getTarget().getName();
									CurrStun= individualAttack.getTarget().getCurrentStat("STUN");
									MaxStun = individualAttack.getTarget().getCharacteristic("STUN").getBaseStat();
									
									CurrEndurance= individualAttack.getTarget().getCurrentStat("END");
									MaxEndurance = individualAttack.getTarget().getCharacteristic("END").getBaseStat();
									
									output=output+ "\r\n\t\t\tMaxStun:" + MaxStun;
									output=output+ "\r\n\t\t\tMaxEndurance:" + MaxEndurance;
									output=output+ "\r\n\t\t\tCurrStun:" + CurrStun;
									output=output+ "\r\n\t\t\tCurrEndurance:" + CurrEndurance + "\r\n";
									
									
									String rname=individualAttack.getTarget().getRoster().getName();
									output=output+"\t\t\tCrowd:\r\n\t\t\t\tname:"+rname+ "\r\n";
									if( attack.isTargetHit()== true){
										output+= "\t\t result: hit\r\n";
										output+= "\t\t Effects: \r\n";
										if(individualAttack.getTarget().isDead()==true){
											output+= "\t\t\t- dead\r\n";
										}
										if(individualAttack.getTarget().isDying()==true){
											output+= "\t\t\t- dying\r\n";
										}
										if(individualAttack.getTarget().stunned==true){
											output+= "\t\t\t- Stunned\r\n";
										}
										if(individualAttack.getTarget().unconscious==true){
											output+= "\t\t\t- Unconscious\r\n";
										}
									}
									else{
										output+= "\t\t result: miss\r\n";
									}
								}
							}
						}
					}
					if (child instanceof KnockbackSummaryGroup){
						if(kbheader==false)
						{
							kbheader=true;
							output+= "Knockback\r\n";
						}
						
						KnockbackSummaryGroup kbsg= (KnockbackSummaryGroup)child;;
						for (int j= 0;j< kbsg.getChildCount();j++ ){
							Object kbsgChild = kbsg.getChild(j);
							if (kbsgChild instanceof KnockbackSummaryMessage){
								KnockbackSummaryMessage kbm=(KnockbackSummaryMessage)kbsgChild;
								output+= "\t- \r\n";
								output+= "\t\ttarget:\r\n\t\t\tname:" + kbm.getTarget().getName() + "\r\n";
								output=output+"\t\t\tRoster:\r\n\t\t\t\tname:"+kbm.getTarget().getRoster().getName()+ "\r\n";
								//((EffectSummaryMessage)kbm).getEffect().getName()
								String kb = kbm.getMessage();

								 
								if(kb.indexOf("was not knocked back or knocked down")<0 && kb.indexOf("was not knocked back but was knocked down")<0 ){
	
									kb =kb.substring(kb.indexOf("back ")+5,kb.length());
									
									if(kb.indexOf("\" with no collision and breakfell successfully.")!=-1){
										kb=kb.substring(0, kb.length()-("\" with no collision and breakfell successfully.").length());
										output+= "\t\tbreakfall:success\r\n";
									}
									else
									{
										if(kb.indexOf("\" with no collision.")!=-1){
											kb=kb.substring(0, kb.length()-("\" with no collision.").length());
										}
									}
									output+= "\t\tdistance:" + kb + "\r\n";
									output+= "\t\teffect:knockedback\r\n";
								}
								if(kb.indexOf("was not knocked back but was knocked down") > 0 || kb.indexOf("and is knocked down") > 0 ){
									output+= "\t\teffect:knockeddown\r\n";
								}
							}
							
						}
					}	
				}
				try{
			       	File file = new  File("EventInfo\\Ability.info");
			        if (file.exists()) {
			        	file.delete();
			        }
		            file.createNewFile();
		            FileWriter fw = new FileWriter(file.getAbsoluteFile());
			        BufferedWriter bw = new BufferedWriter(fw);
			        bw.write(output);
			        bw.close();
		        }catch (IOException e) {
		        	e.printStackTrace();
		        }
			}
		}
		}
		catch(Exception e)
		{}
	}
	 
	public static void exportActiveCharacter(Target activeTarget){
		
		try{
			String character= buildCharFromTarget(activeTarget);
			if(activeTarget==null) {
				return;
			}
			Roster roster=activeTarget.getRoster();
			
			
		
	       	File file = new  File("EventInfo\\Activity.info");
	        if (file.exists()) {
	        	file.delete();
	        }
            file.createNewFile();
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
	        BufferedWriter bw = new BufferedWriter(fw);
	        bw.write(character + "\r\nActivate: True\r\n");
	        
	        bw.write(buildRosterFromR(roster));
	 
	        bw.close();
        }catch (Exception e) {
        	e.printStackTrace();
        }
	}
	
	public static void exportItemCLicked(Target target, String itemName){

		try{
	       	File file = new  File("EventInfo\\Ability.info");
	        if (file.exists()) {
	        	file.delete();
	        }
            file.createNewFile();
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
	        BufferedWriter bw = new BufferedWriter(fw);
	        String character= buildCharFromTarget(target);
	        Roster r=target.getRoster();
	        String roster = buildRosterFromR(r);
	        itemName = "Ability:\r\n\tName:" + itemName + character + roster;
	        itemName=itemName +"\r\nMiscItem: True";
	        bw.write(itemName);
	        bw.close();
        }catch (Exception e) {
        	e.printStackTrace();
        }
	}
	
	public static void exportVirtualDesktopAction(String action, Target t){
	try{
       	File file = new  File("EventInfo\\DesktopAction.info");
        if (file.exists()) {
        	file.delete();
        }
        file.createNewFile();
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        action = "Action:" + action +"\r\nActionSent: True";
        if(t!=null){
        	action = action + buildCharFromTarget(t);
        	Roster r =t.getRoster() ;
        	if(r!=null){
        		action+=buildRosterFromR(r); 
        	}
        }
        bw.write(action+ "");
        bw.close();
    }catch (IOException e) {
    	e.printStackTrace();
    }
}
	
	private static void WriteDataToFile(String data, String fileName){
		try{
	       	File file = new  File("EventInfo\\" + fileName + ".info");
	        if (file.exists()) {
	        	file.delete();
	        }
            file.createNewFile();
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
	        BufferedWriter bw = new BufferedWriter(fw);
	        bw.write(data);
	        bw.close();
        }catch (IOException e) {
        	e.printStackTrace();
        }
		
		
	}

	private static String buildRosterFromR(Roster r){
		Iterator combatants = r.getCombatants().iterator();
		String export="";
		for(Target target: r.getCombatants()){
    	      export+=buildRosterCharFromTarget(target);
    	 }
    	if (r.MobMode== true){
    		export+="\r\n\tMobLeader:" + r.MobLeader;
    		export+="\r\n\tMobMode:" + r.MobMode;
    	}
		return "\r\n\tCrowd: \r\n\t\tName:" + r.getName() + "\r\n\t\tCharacters:"+export;
	}
	
	private static String buildRosterCharFromTarget(Target t){
		if(t!= null){
			AbilityIterator i= t.getAbilities("Costume");
			String costume="";
			String model="";
			try{model=i.nextAbility().getName();}catch(Exception e){}
			//try{model=i.nextAbility().getName();}catch(Exception e){}
		 
			String character="\r\n\t\t\t" +t.getName()+":"  + "\r\n\t\t\t\tname:" + t.getName() +    "\r\n\t\t\t\tcostume:" +  costume+ "\r\n\t\t\t\tmodel:" + model;
			return character;
		}
		else{
			return "";
		}
	}
	
	private static String buildCharFromTarget(Target t){
		try {
		if(t!= null){
			AbilityIterator i= t.getAbilities("Costume");
			String costume="";
			String model="";
			//try{costume=i.nextAbility().getName();}catch(Exception e){}
			try{model=i.nextAbility().getName();}catch(Exception e){}
		 
			String character="\r\ncharacter:\r\n\tname:" + t.getName() +    "\r\n\tcostume:" +  costume+ "\r\n\tmodel:" + model;
			
			
			Characteristic stat = t.getCharacteristic("STUN");
			
			if( t.getCharacteristic("STUN")!=null &&  t.getCharacteristic("STUN")!=null )
			{
				Integer CurrStun= t.getCurrentStat("STUN");
				Integer MaxStun = t.getCharacteristic("STUN").getBaseStat();
			
				Integer CurrEndurance= t.getCurrentStat("END");
				Integer MaxEndurance = t.getCharacteristic("END").getBaseStat();
				
				character=character+ "\r\n\tMaxStun:" + MaxStun;
				character=character+ "\r\n\tMaxEndurance:" + MaxEndurance;
				character=character+ "\r\n\tCurrStun:" + CurrStun;
				character=character+ "\r\n\tCurrEndurance:" + CurrEndurance;
			}
			return character;
		}
		else{
			return "";
		}
		}
		catch(Exception e) {
			return null;
		}	
		
		
	}
	
	public static void exportEvent(String event, Target activeTarget, Roster r) {
		
		String character="";
		String roster="";
		
		if(activeTarget!=null){
			character = buildCharFromTarget(activeTarget); 
		}
		if(r!=null){
			roster= buildRosterFromR(r); 
		}
		WriteDataToFile ("Event: " + "\r\n\tName:" + event + character + roster ,"HCSEvent.info"); 
	}

	public static void exportRenameEvent(Target target, String oldName, String name) {
		try{
			Roster r = target.getRoster();
			if(r!=null) {
				WriteDataToFile("Event:\r\n\tName:CharacterRenamed\r\n"+ "character:\r\n\tname:  "+ name +"\r\n\toldName: " + oldName+"\r\n\tCrowd:\r\n\t\tName:" + r.getName(),"HCSEvent.info");
			}
		}
		catch(Exception e){}
	}

	public static  VirtualDesktopControllerAction GetVIrtualDesktopAction(String virtualDesktopAction){
		return new VirtualDesktopControllerAction(virtualDesktopAction);
	}
        
    public static class VirtualDesktopControllerAction extends AbstractAction {  
	private Target target;
        private Roster roster;
        private String virtualDesktopAction;
        public VirtualDesktopControllerAction(String virtualDesktopAction) {
            super(virtualDesktopAction);
            this.virtualDesktopAction= virtualDesktopAction;
            this.setEnabled(true);
        }
        public void actionPerformed(ActionEvent e) {
            if ( target != null) {
            	Roster r = target.getRoster();
               	VirtualDesktop.Legacy.MessageExporter.exportEvent(this.virtualDesktopAction, target, r);
            }
        }
        
        public boolean isEnabled() {
            return true ;
        }
        
        public Target getTarget() {
            return target;
        }
        
        public void setTarget(Target target) {
            this.target = target;
        }
        
        public Roster getRoster() {
            return roster;
        }
        
        public void setRoster(Roster roster) {
            this.roster = roster;
        }
    }

	public static void exportAbilityActivation(Ability ability) {
		
		String abilityName=ability.getName();
		if(abilityName != null)
        {
        	String output="Ability:\r\n\tName:" +abilityName;
        	Target t= ability.getSource();
        	output=output+ buildCharFromTarget(t);
        	Roster r= t.getRoster();
        	output=output+ buildRosterFromR(r);
        	try{
	        	File file = new  File("C:\\Champions\\hcs\\EventInfo\\Ability.info");
	        	 // if file doesn't exists, then create it    
	            if (file.exists()) {
	                file.delete();
	                
	            }
	            file.createNewFile();
	            FileWriter fw = new FileWriter(file.getAbsoluteFile());
	            BufferedWriter bw = new BufferedWriter(fw);
	            bw.write(output);
	            bw.close();
        	}catch (IOException e) {
                e.printStackTrace();    
                
            }
        			
        }
		
	}

	public static void exportRosterEvent(String event, Roster[] rosters) {
		String roster="";
		
		for(Roster r:rosters){
			if(r!=null){
				roster += buildRosterFromR(r); 
			}
		}
		WriteDataToFile ("Event: " + event  + roster ,"HCSEvent.info");
		
	}

	
	
}
