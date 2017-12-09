/*
 * powerHeal.java
 *
 * Created on April 14, 2004, 12:12 AM
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
public class powerKnockbackResistanceAdapter extends AbstractPowerXMLAdapter implements PowerXMLAdapter {
    
    private static String XMLID = "KBRESISTANCE";
    private static String[][] translationArray = {
        { "LEVELS", "Resistance" },
                
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
    
    /** CustomHandler for processing parameter.
     *
     */
//

    public void rateSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        if ( "TURN".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "Turn");
        }
            else if ( "1MINUTE".equals(attrValue) ) {
                pl.setParameterValue(parameterName, "Minute");
            }
            else if ( "5MINUTES".equals(attrValue) ) {
                pl.setParameterValue(parameterName, "Five Minutes");
            }
            else if ( "20MINUTES".equals(attrValue) ) {
                pl.setParameterValue(parameterName, "Twenty Minutes");
            }
            else if ( "1HOUR".equals(attrValue) ) {
                pl.setParameterValue(parameterName, "One Hour");
            }
            else if ( "6HOURS".equals(attrValue) ) {
                pl.setParameterValue(parameterName, "Six Hours");
            }
            else if ( "1DAY".equals(attrValue) ) {
                pl.setParameterValue(parameterName, "One Day");
            }
            else if ( "1WEEK".equals(attrValue) ) {
                pl.setParameterValue(parameterName, "One Week");
            }
            else if ( "1MONTH".equals(attrValue) ) {
                pl.setParameterValue(parameterName, "One Month");
            }
            else if ( "1SEASON".equals(attrValue) ) {
                pl.setParameterValue(parameterName, "One Season");
            }
            else if ( "1YEAR".equals(attrValue) ) {
                pl.setParameterValue(parameterName, "One Year");
            }
            else if ( "5YEARS".equals(attrValue) ) {
                pl.setParameterValue(parameterName, "Five Years");
            }
            else if ( "25YEARS".equals(attrValue) ) {
                pl.setParameterValue(parameterName, "25 Years");
            }

        }
}
