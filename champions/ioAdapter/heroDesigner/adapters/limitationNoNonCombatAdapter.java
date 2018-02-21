/*
 * limitationIncreasedEndurance.java
 *
 * Created on March 29, 2004, 10:11 PM
 */

package champions.ioAdapter.heroDesigner.adapters;

import champions.Ability;
import champions.parameters.ParameterList;
import champions.ioAdapter.heroDesigner.AbstractModifierXMLAdapter;
import champions.ioAdapter.heroDesigner.ModifierXMLAdapter;
import org.w3c.dom.Node;


/**
 *
 * @author  1425
 */
public class limitationNoNonCombatAdapter extends AbstractModifierXMLAdapter implements ModifierXMLAdapter { 
    
	  private static String XMLID = "NONONCOMBAT";
	    private static String[][] translationArray = {
	    };
	    
	    public String getXMLID() {
	        return XMLID;
	    }
	    

	    public String[][] getTranslationArray() {
	        return translationArray;
	    }

}
