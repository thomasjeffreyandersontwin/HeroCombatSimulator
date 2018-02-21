/*
 * advantageAreaEffect.java
 *
 * Created on April 10, 2004, 2:36 PM
 */

package champions.ioAdapter.heroDesigner.adapters;

import champions.Ability;
import champions.PADRoster;
import champions.Sense;
import champions.parameters.ParameterList;
import champions.ioAdapter.heroDesigner.AbstractModifierXMLAdapter;
import champions.ioAdapter.heroDesigner.ModifierXMLAdapter;
import org.w3c.dom.Node;


/**
 *
 * @author  Kevin Richard
 */
public class advantageStopsAGivenSenseAdapter extends AbstractModifierXMLAdapter implements ModifierXMLAdapter { 
    
    private static String XMLID = "STOPSENSEGROUP";
    private static String[][] translationArray = {
        
    		 { "OPTION_ALIAS", null, "senseGroupSpecial" }, 
    };
    

    public String getXMLID() {
        return XMLID;
    }
    
    public String[][] getTranslationArray() {
        return translationArray;
    }
    
     public void senseGroupSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {

         Sense sense = PADRoster.getNewSense(attrValue);
    	 pl.setParameterValue("SenseGroup", sense);
    
    }
}
