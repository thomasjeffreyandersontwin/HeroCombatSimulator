package VirtualDesktop.Controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONObject;

import com.sun.prism.impl.Disposer.Target;
import com.sun.xml.internal.ws.util.StringUtils;

import champions.Ability;
import champions.Battle;
import champions.BattleEvent;
import champions.TargetList;
import champions.attackTree.AttackParametersNode;
import champions.attackTree.AttackParametersPanel;
import champions.attackTree.AttackTreeModel;
import champions.attackTree.AttackTreePanel;
import champions.attackTree.SelectTargetPanel;

public class FileMessageListenerThread implements Runnable{
	private Thread t;
	private Battle battle;

	public FileMessageListenerThread(Battle aBattle){
		battle = aBattle;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("running file listener thread");
		HandleAbilityFiredFromDesktop();
	}
	
	public void start () {
	      System.out.println("Starting file listener" );
	      if (t == null) {
	         t = new Thread (this, "File Message Listener");
	         t.start ();
	      }
	   }
	
	public void HandleAbilityFiredFromDesktop() {
			while (true) {

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		       	File file = new  File(GLOBALS.EXPORT_PATH+"\\AbilityActivatedFromDesktop.event");
		       	if(file.exists()) {
		       		try {
				        DesktopMessageFactory factory = new DesktopMessageFactory();
				       	JSONObject message = factory.ParseMessage();
				       	AbstractDesktopCommand command = DesktopCommandFactory.GetCommand(message);
				       	command.ExecuteDesktopEventOnSimulator(message);	
			        }
			        catch(Exception e) 
			        {
 			        	e.printStackTrace();
 			        	file.delete();
			        }
			        finally
			        {
			        	file.delete();
			        }
			   }       
			}
		}
	}
