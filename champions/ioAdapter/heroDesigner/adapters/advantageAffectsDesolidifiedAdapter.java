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
public class advantageAffectsDesolidifiedAdapter extends AbstractModifierXMLAdapter implements ModifierXMLAdapter { 
    
    private static String XMLID = "AFFECTSDESOLID";
    private static String[][] translationArray = {
        { "", "AffectsDesolidified", "trueSpecial" }, 
    };
    
    public String getXMLID() {
        return XMLID;
    }
    

    public String[][] getTranslationArray() {
        return translationArray;
    }
}
