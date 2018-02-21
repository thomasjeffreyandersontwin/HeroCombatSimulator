/*
 * advantageContinuous.java
 *
 * Created on March 29, 2004, 10:11 PM
 */

package champions.ioAdapter.heroDesigner.adapters;

import champions.ioAdapter.heroDesigner.AbstractModifierXMLAdapter;
import champions.ioAdapter.heroDesigner.ModifierXMLAdapter;

/**
 *
 * @author  1425
 */
public class advantageUsableByOthersAdapter extends AbstractModifierXMLAdapter implements ModifierXMLAdapter { 
    
    private static String XMLID = "UOO";
    private static String[][] translationArray = {};
    

    public String getXMLID() {
        return XMLID;
    }
    

    public String[][] getTranslationArray() {
        return translationArray;
    }
}
