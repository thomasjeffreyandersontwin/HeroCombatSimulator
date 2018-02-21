/*
 * advantageAreaEffect.java
 *
 * Created on April 10, 2004, 2:36 PM
 */

package champions.ioAdapter.heroDesigner.adapters;

import champions.Ability;
import champions.parameters.ParameterList;
import champions.ioAdapter.heroDesigner.AbstractModifierXMLAdapter;
import champions.ioAdapter.heroDesigner.ModifierXMLAdapter;
import org.w3c.dom.Node;


/**
 *
 * @author  Kevin Richard
 */
public class advantageEntangleAndCharacterAdapter extends AbstractModifierXMLAdapter implements ModifierXMLAdapter { 
    
    private static String XMLID = "BOTHDAMAGE";
    private static String[][] translationArray = {
    };
    
    public String getXMLID() {
        return XMLID;
    }
    
    public String[][] getTranslationArray() {
        return translationArray;
    }
    
   
}
