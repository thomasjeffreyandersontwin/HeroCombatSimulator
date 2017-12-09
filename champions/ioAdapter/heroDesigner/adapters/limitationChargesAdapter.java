/*
 * limitationCharges.java
 *
 * Created on April 09, 2004, 8:50 AM
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
public class limitationChargesAdapter extends AbstractModifierXMLAdapter implements ModifierXMLAdapter {
    
    private static String XMLID = "CHARGES";
    private static String[][] translationArray = {
        { "OPTION_ALIAS", "Uses" },
                //{ "ADDER(XMLID=CLIPS).LEVELS", "Clips" },
        { "ADDER(XMLID=RECOVERABLE)", "Recoverable", "trueSpecial" },
        { "ADDER(XMLID=CONTINUING)", "UsesContinuous", "trueSpecial" },
        { "ADDER(XMLID=CONTINUING).OPTIONID", "ChargeLength", "durationSpecial"},
        { "ADDER(XMLID=INCREASEDTIME).OPTIONID", "RecoveryLength","recoverableSpecial"}
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
    public void durationSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        if ( "EXTRAPHASE".equals(attrValue) ) {
            pl.setParameterValue("ChargeLength", "Extra Phase");
        } else if ( "TURN".equals(attrValue) ) {
            pl.setParameterValue("ChargeLength", "1 Turn");
        } else if ( "MINUTE".equals(attrValue) ) {
            pl.setParameterValue("ChargeLength", "1 Minute");
        } else if ( "FIVEMINUTES".equals(attrValue) ) {
            pl.setParameterValue("ChargeLength", "5 Minutes");
        } else if ( "TWENTYMINUTES".equals(attrValue) ) {
            pl.setParameterValue("ChargeLength", "20 Minutes");
        } else if ( "HOUR".equals(attrValue) ) {
            pl.setParameterValue("ChargeLength", "1 Hour");
        } else if ( "SIXHOURS".equals(attrValue) ) {
            pl.setParameterValue("ChargeLength", "6 Hours");
        } else if ( "ONEDAY".equals(attrValue) ) {
            pl.setParameterValue("ChargeLength", "1 Day");
        } else if ( "ONEWEEK".equals(attrValue) ) {
            pl.setParameterValue("ChargeLength", "1 Week");
        } else if ( "ONEMONTH".equals(attrValue) ) {
            pl.setParameterValue("ChargeLength", "1 Month");
        } else if ( "ONESEASON".equals(attrValue) ) {
            pl.setParameterValue("ChargeLength", "1 Season");
        } else if ( "ONEYEAR".equals(attrValue) ) {
            pl.setParameterValue("ChargeLength", "1 Year");
        } else if ( "GENERIC_OBJECT".equals(attrValue) ) {
            pl.setParameterValue("ChargeLength", "5 Years");
        } else if ( "TWENTYFIVEYEARS".equals(attrValue) ) {
            pl.setParameterValue("ChargeLength", "25 Years");
        } else if ( "ONECENTURY".equals(attrValue) ) {
            pl.setParameterValue("ChargeLength", "1 Century");
        }
        if ( "ONEWEEK".equals(attrValue) ) {
            pl.setParameterValue("RecoveryLength", "1 Week");
        } else if ( "ONEMONTH".equals(attrValue) ) {
            pl.setParameterValue("RecoveryLength", "1 Month");
        } else if ( "ONESEASON".equals(attrValue) ) {
            pl.setParameterValue("RecoveryLength", "1 Season");
        } else if ( "LONGTIME".equals(attrValue) ) {
            pl.setParameterValue("RecoveryLength", ">1 Season");
        }
    }
    public void recoverableSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        if ( "ONEWEEK".equals(attrValue) ) {
            pl.setParameterValue("RecoveryLength", "1 Week");
        } else if ( "ONEMONTH".equals(attrValue) ) {
            pl.setParameterValue("RecoveryLength", "1 Month");
        } else if ( "ONESEASON".equals(attrValue) ) {
            pl.setParameterValue("RecoveryLength", "1 Season");
        } else if ( "LONGTIME".equals(attrValue) ) {
            pl.setParameterValue("RecoveryLength", ">1 Season");
        }
    }
}
