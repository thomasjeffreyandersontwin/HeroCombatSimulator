/*
 * powerCombatLevels.java
 *
 * Created on March 29, 2004, 10:11 PM
 */

package champions.ioAdapter.heroDesigner.adapters;

import champions.Ability;
import champions.parameters.ParameterList;
import champions.ioAdapter.heroDesigner.AbstractPowerXMLAdapter;
import champions.ioAdapter.heroDesigner.PowerXMLAdapter;
import org.w3c.dom.Node;


/**
 *
 * @author  1425
 */
public class skillSkillLevelsAdapter extends AbstractPowerXMLAdapter implements PowerXMLAdapter {
    
    private static String XMLID = "SKILL_LEVELS";
    private static String[][] translationArray = {
        { "LEVELS", "Level" },
        { "OPTIONID", "LevelType", "leveltypeSpecial" },
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
    
     /** specialHandler that translates HD skill level options to HCS skill level options.
     *
     */
    public void leveltypeSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        if ( "CHARACTERISTIC".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "Single Skill");
        }
        else if ( "RELATED".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "Three Related Skills");
        }
        else if ( "SIMILAR".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "Group of Similar Skills");
        }
        else if ( "NONCOMBAT".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "All Non-Combat Skills");
        }
        else if ( "OVERALL".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "Overall Level");
        }
    }
}
