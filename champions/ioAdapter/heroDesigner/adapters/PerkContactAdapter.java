/*
 * powerArmor.java
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
public class PerkContactAdapter extends AbstractPowerXMLAdapter implements PowerXMLAdapter {
    
    private static String XMLID = "CONTACT";
    private static String[][] translationArray = {
        { "INPUT" , "name",  "nameSpecial" },
        { "LEVELS", "Cost" },
        { "ROLL", "Roll" },
        { "ADDER(XMLID=LIMITEDBYID)", "LimitedbyID","trueSpecial" },
        { "ADDER(XMLID=USEFUL).OPTION", "Resources", "resourcesSpecial"},
        { "ADDER(XMLID=ACCESSTOINSTITUTIONS)", "AccessToInstitutions","trueSpecial" },
        { "ADDER(XMLID=CONTACTHASCONTACTS)", "ContactHasContacts","trueSpecial" },
        { "ADDER(XMLID=GOODRELATIONSHIP).ALIAS", "Relationship", "relationshipSpecial"},
        { "ADDER(XMLID=VERYGOODRELATIONSHIP).ALIAS", "Relationship", "relationshipSpecial"},
        { "ADDER(XMLID=SLAVISHLYLOYAL)", "SlavishlyLoyal","trueSpecial" },
        { "ADDER(XMLID=BLACKMAILED)", "Blackmailed","trueSpecial" },
        //{ "OPTION", "Shape", "shapeSpecial" },
        //{ "ADDER(XMLID=VERYUSEFUL)", "Resources", "resourcesSpecial"},
        // { "ADDER(XMLID=NCI)", "ExtensiveNonCombatInfluence", "trueSpecial" },
    };
    
    /*
     *
     *        {"Cost","Power.COST", Integer.class, new Integer(1),  "Points For Roll", INTEGER_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"Roll","Power.ROLL", Integer.class, new Integer(8), "Roll", INTEGER_PARAMETER, VISIBLE, DISABLED, NOTREQUIRED},
        {"LimitedbyID","Power.LIMITEDBYID", Boolean.class, new Boolean(false), "Limited By Identity", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"Resources","Power.RESOURCES", String.class, "Typical Skills or Resources", "Contact Has", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", resourcesArray},
        {"AccessToInstitutions","Power.ACCESSTOINSTITUTIONS", Boolean.class, new Boolean(false), "Contact Has Access To Major Institutions", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"ContactHasContacts","Power.CONTACTHASCONTACTS", Boolean.class, new Boolean(false), "Contact Has Significant Contacts Of His Own", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"Relationship","Power.GOODRELATIONSHIP", String.class, "Indifferent", "Relationship with Contact", COMBO_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED, "OPTIONS", relationshipArray},
        {"SlavishlyLoyal","Power.SLAVISHLYLOYAL", Boolean.class, new Boolean(false), "Contact Is Slavishly Loyal To Character", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
        {"Blackmailed","Power.BLACKMAILED", Boolean.class, new Boolean(false), "Contact Has Been Blackmailed By The Character", BOOLEAN_PARAMETER, VISIBLE, ENABLED, NOTREQUIRED},
     */
    
    
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
    /** specialHandler that translates HD Shape option to HCS shape option.
     *
     */
    
    
    public void resourcesSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        if ( "USEFUL".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "Useful Skills or Resources");
        } else if ( "VERYUSEFUL".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "Very Useful Skills or Resources");
        } else if ( "EXTREMELYUSEFUL".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "Extremely Useful Skills or Resources");
        }
    }
    
    public void relationshipSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        if ( "Good relationship with Contact".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "Good");
        } else if ( "Very Good relationship with Contact".equals(attrValue) ) {
            pl.setParameterValue(parameterName, "Very Good");
        }
    }
    
    public void nameSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        ability.setName("Contact: " + attrValue);
    }
}