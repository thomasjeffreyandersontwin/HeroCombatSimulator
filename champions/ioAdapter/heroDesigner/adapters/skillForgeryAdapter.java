/*
 * powerGrowth.java
 *
 * Created on April 14, 2004, 12:12 AM
 */

package champions.ioAdapter.heroDesigner.adapters;

import champions.Ability;
import champions.ioAdapter.heroDesigner.AbstractPowerXMLAdapter;
import champions.ioAdapter.heroDesigner.PowerXMLAdapter;
import champions.parameters.ParameterList;
import org.w3c.dom.Node;

/**
 *
 * @author  1425
 */
public class skillForgeryAdapter extends AbstractPowerXMLAdapter implements PowerXMLAdapter {
    
    private static String XMLID = "FORGERY";
    private static String[][] translationArray = {
        { "LEVELS", "Levels" },
        { "ADDER(XMLID=DOCUMENTS)", "Categories", "docSpecial" },
        { "ADDER(XMLID=MONEY)", "Categories", "moneySpecial" },
        { "ADDER(XMLID=ART)", "Categories", "artSpecial" },
        { "ADDER(XMLID=COMMERCIALGOODS)", "Categories", "cgSpecial" },
//        { "ADDER().ALIAS", "SubCategories" }
    };
    
    /** Returns the XMLID for this Power.
     *
     * Subclass should either override this to return their XMLID or override
     * the identify method to do more complicated identification tasks.
     */
    public String getXMLID() {
        return XMLID;
    }
    
    /** Returns the Translation Array for the PowerAdapter.
     *
     * The Subclass should either override this to return their translationArray
     *or override the importXML method to do more complicated import tasks.
     */
    public String[][] getTranslationArray() {
        return translationArray;
    }
    
    public void docSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        pl.addIndexedParameterValue("Categories", "Documents");
    }
    
    public void moneySpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        pl.addIndexedParameterValue("Categories", "Money");
    }
    
    public void artSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        pl.addIndexedParameterValue("Categories", "Art");
    }
    
    public void cgSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        pl.addIndexedParameterValue("Categories", "Commercial Goods");
    }
}
