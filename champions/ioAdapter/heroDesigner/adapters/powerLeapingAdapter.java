/*
 * powerEnergyBlast.java
 *
 * Created on March 29, 2004, 10:11 PM
 */

package champions.ioAdapter.heroDesigner.adapters;

import champions.Ability;
import champions.parameters.ParameterList;
import champions.ioAdapter.heroDesigner.AbstractPowerXMLAdapter;
import champions.ioAdapter.heroDesigner.HDImportError;
import champions.ioAdapter.heroDesigner.PowerXMLAdapter;
import org.w3c.dom.Node;
import xml.XMLParseError;

/**
 *
 * @author  1425
 */
public class powerLeapingAdapter extends AbstractPowerXMLAdapter implements PowerXMLAdapter {
    
    private static String XMLID = "RUNNING";
    private static String[][] translationArray = {
        { "LEVELS", "DistanceFromCollision", "levelsSpecial"},
        { "ADDER(XMLID=IMPROVEDNONCOMBAT).LEVELS", "NoncombatX", "noncombatSpecial"},
        { "ADD_MODIFIERS_TO_BASE", "AddsToBase", "addsToBaseSpecial" }
    };
    
    private boolean basePower = false;
    
    public powerLeapingAdapter() {
        super();
    }
    
    public powerLeapingAdapter(boolean basePower) {
        super();
        
        this.basePower = basePower;
    }
    
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
    
    public XMLParseError importXML(Ability ability, Node node) {
        XMLParseError result = super.importXML(ability, node);
        
        if ( (result == null || result.getErrorSeverity() < HDImportError.IMPORT_FAILURE) && basePower ) {
            ParameterList pl = ability.getPower().getParameterList(ability, -1);
            pl.setParameterValue("Base", "TRUE");
            ability.reconfigurePower();
        }
        
        return result;
    }
    
    public void levelsSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        int levels = Integer.parseInt(attrValue);
        
        pl.setParameterValue("DistanceFromCollision", new Integer(levels));
    }
    
    public void noncombatSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        int levels = Integer.parseInt(attrValue);
        
        int nonCombatX = 2 * (int)Math.pow(2, levels);
        
        pl.setParameterValue("NoncombatX", new Integer(nonCombatX));
    }
    
    public void addsToBaseSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        pl.setParameterValue("AddsToBase", ("Yes".equals(attrValue) ? "TRUE" : "FALSE" ));
    }
    
}
