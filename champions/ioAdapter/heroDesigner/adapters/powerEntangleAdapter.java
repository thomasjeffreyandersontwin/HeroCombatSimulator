/*
 * powerEntagle.java
 *
 * Created on April 14, 2004, 12:12 AM
 */

package champions.ioAdapter.heroDesigner.adapters;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

import champions.Ability;
import champions.PADRoster;
import champions.Sense;
import champions.ioAdapter.heroDesigner.AbstractPowerXMLAdapter;
import champions.ioAdapter.heroDesigner.PowerXMLAdapter;
import champions.parameters.ParameterList;

/**
 *
 * @author  1425
 */
public class powerEntangleAdapter extends AbstractPowerXMLAdapter implements PowerXMLAdapter {
    
    private static String XMLID = "ENTANGLE";
    private static String[][] translationArray = {
        { "LEVELS", "EntangleDie" },
        { "ADDER(XMLID=STOPSENSEGROUP).ALIAS", null, "sensesSpecial"},

    };
    
    public void sensesSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
    
     String senseStr = node.getAttributes().getNamedItem("OPTION_ALIAS").getNodeValue();
     pl.addIndexedParameterValue("Senses", PADRoster.getNewSense(senseStr));
   }
    public String getXMLID() {
        return XMLID;
    }
    
  
    public String[][] getTranslationArray() {
        return translationArray;
    }
    
 
}
