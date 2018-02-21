/*
 * advantageNoRangePenalty.java
 *
 * Created on April 10, 2004, 10:11 PM
 */

package champions.ioAdapter.heroDesigner.adapters;

import champions.ioAdapter.heroDesigner.AbstractModifierXMLAdapter;
import champions.ioAdapter.heroDesigner.ModifierXMLAdapter;

/**
 *
 * @author  1425
 */
public class advantageLineOfSightAdapter extends AbstractModifierXMLAdapter implements ModifierXMLAdapter { 
    
    private static String XMLID = "LOS";
    private static String[][] translationArray = {
        
    };
    
    public String getXMLID() {
        return XMLID;
    }
    
    public String[][] getTranslationArray() {
        return translationArray;
    }
}
