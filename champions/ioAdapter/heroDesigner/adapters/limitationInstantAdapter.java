/*
 * advantageAffectsDesolidified.java
 *
 * Created on April 10, 2004, 2:36 PM
 */

package champions.ioAdapter.heroDesigner.adapters;

import champions.ioAdapter.heroDesigner.AbstractModifierXMLAdapter;
import champions.ioAdapter.heroDesigner.ModifierXMLAdapter;

/**
 *
 * @author  Kevin Richard
 */
public class limitationInstantAdapter extends AbstractModifierXMLAdapter implements ModifierXMLAdapter { 
    
    private static String XMLID = "INSTANT";
    private static String[][] translationArray = {
    };
    
    public String getXMLID() {
        return XMLID;
    }
    

    public String[][] getTranslationArray() {
        return translationArray;
    }
}
