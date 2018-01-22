package VirtualDesktop.Attack;

import java.util.Iterator;

import org.json.simple.JSONObject;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

import VirtualDesktop.Character.CharacterAdaptor;
import VirtualDesktop.Controller.AbstractDesktopCommand;
import champions.BattleEvent;
import champions.Sense;
import champions.Target;
import champions.TargetList;
import champions.attackTree.AttackTreeModel;
import champions.attackTree.AttackTreeNode;
import champions.attackTree.PerceptionPanel;
import champions.attackTree.SelectTargetPanel;
import champions.attackTree.SourcePerceptionsNode;

//eg {"Type":"AttackSingleTarget","Ability":"Spider Strike","Target":"Ogun", "PushedStr":12, "Generic":3,"Off Hand":true, "Unfamiliar Weapon":true, "Surprise Move":2	,"Encumbrance":-3}

public class AttackTargetCommand extends AbstractDesktopCommand {
	
	
	public static  AttackAdapter AttackInProgress;
	public static JSONObject lastMessage;
	@Override
	public void ExecuteDesktopEventOnSimulatorBasedOnMessageType(JSONObject message,
		CharacterAdaptor character) throws Exception  {
		JSONObject responseJSON = character.processJSON(message);
		String abilityName = (String)message.get("Ability");
		AttackInProgress = (AttackAdapter) character.ActiveAbility;


	}
}
