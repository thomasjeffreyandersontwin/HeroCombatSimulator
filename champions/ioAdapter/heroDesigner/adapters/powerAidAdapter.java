/*
 * powerAid.java
 *
 * Created on March 29, 2004, 10:11 PM
 */

package champions.ioAdapter.heroDesigner.adapters;

import champions.Ability;
import champions.Characteristic;
import champions.PADRoster;
import champions.parameters.ParameterList;
import champions.Target;
import champions.interfaces.AbilityList;
import champions.Power;
import champions.SpecialEffect;
import champions.ioAdapter.heroDesigner.AbstractPowerXMLAdapter;
import champions.ioAdapter.heroDesigner.HDImportError;
import champions.ioAdapter.heroDesigner.PowerXMLAdapter;
import java.util.ArrayList;
import java.util.Iterator;
import org.w3c.dom.Node;
import xml.XMLParseError;
import xml.XMLParseErrorList;



/**
 *
 * @author  1425
 */
public class powerAidAdapter extends AbstractPowerXMLAdapter implements PowerXMLAdapter {
    
    private static String XMLID = "AID";
    private static String[][] translationArray = {
        { "LEVELS", "AidDie" },
        { "INPUT", "AidTo"},
        { "ADDER(XMLID=INCREASEDMAX).LEVELS", "IncreasedMax"},
        { "ADDER(XMLID=ADDITIONALPOINTSGOINTO).INPUT", "", "additionalPointsSpecial"},
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
    
    /** Imports Parameters.
     *
     * Override this method to provide custom import stuff.  This should be
     * necessary in most cases!!!
     */
   /*  public boolean importXML(Ability ability, Node node) {
         // Make sure we do the normal import first...
         boolean result = super.importXML(ability,node);
    
        // Stupid STUN Only implemented as an advantage in Hero Designer...
         // How the hell is that an Advantage?!?
    
         // Search the children for a node with name of "MODIFIER" and
         // XMLID of STUNONLY
         Node child = node.getFirstChild();
         while( child != null ) {
             if ( "MODIFIER".equals(child.getNodeName()) &&
                  "STUNONLY".equals(child.getAttributes().getNamedItem("XMLID").getNodeValue() ) ) {
                  ParameterList pl = ability.getPower().getParameterList(ability,-1);
                  pl.setParameterValue("StunOnly", "TRUE");
                  break;
             }
    
             child = child.getNextSibling();
         }
    
         return result;
     }*/
    
       /* <ADDER XMLID="ADDITIONALPOINTSGOINTO" ID="1124633459337" BASECOST="0.0" 
LEVELS="0" ALIAS="Additional Absorbed Points Go Into:" POSITION="0" 
MULTIPLIER="1.0" GRAPHIC="Burst" COLOR="255 255 255" SFX="Default" 
SHOW_ACTIVE_COST="Yes" INCLUDE_NOTES_IN_PRINTOUT="Yes" NAME="" INPUT="DEX" 
SHOWALIAS="Yes" PRIVATE="No" REQUIRED="No" GROUP="No" SELECTED="YES">*/
    public void additionalPointsSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        pl.addIndexedParameterValue("AidTo", attrValue);
    }
    
    /** CustomHandler for processing parameter.
     *
     */
    //    public void aidToSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
    //        pl.setParameterValue(parameterName, attrValue);
    //        ability.createIndexed("aidToImport", "OBJECT", attrValue, false);
    //
    //    }
    /** Finilizes the Import of a power/adder from an XML source.
     *
     * This method should be called after the Ability has been added to it's
     * source (if one exists).  If called with a null source, then the
     * ability (and the power and adders) can assume that it will not be
     * attached to a specific source.
     */
    public XMLParseError finalize(Ability ability) {
        XMLParseErrorList errorList = null;
        
        Target source = ability.getSource();
        AbilityList abilities = source.getAbilityList();
        
        ArrayList aidToAbilities = new ArrayList();
        
        ParameterList pl = ability.getPower().getParameterList(ability, -1);
        for(int index = 0; index < pl.getIndexedParameterSize("AidTo"); index++) {
            String aidToString = (String)pl.getIndexedParameterValue("AidTo", index);
            
            SpecialEffect s;
            boolean found = false;
            if ( (s = PADRoster.getSharedSpecialEffectInstance(aidToString)) != null ) {
                if (s != null) {
                    aidToAbilities.add(s);
                    found = true;
                }
            }
            
            if ( ! found && source.hasStat( aidToString.toUpperCase() )) {
                aidToAbilities.add(Characteristic.createCharacteristic(aidToString.toUpperCase(), false));
                found = true;
            }
            
            if ( ! found ) {
                Ability aidToAbility = abilities.getAbility(aidToString,true);
                if ( aidToAbility != null ) {
                    aidToAbilities.add(aidToAbility);
                    found = true;
                }
            }
            
            if ( ! found ) {
                // Not a stat, so iterate through the Powers Types...
                Iterator i = PADRoster.getAbilityIterator();
                while(i.hasNext()) {
                    String name = (String)i.next();
                    if ( name.equals( aidToString ) ){
                        Ability a = PADRoster.getSharedAbilityInstance(name);
                        Power p = a.getPower();
                        aidToAbilities.add(p);
                        found = true;
                    }
                }
            }
            
            if ( !found ) {
                if ( errorList == null ) errorList = new XMLParseErrorList();
                errorList.addXMLParseError( new HDImportError("Aid destination \"" + aidToString + "\" not recognized during import", HDImportError.IMPORT_ERROR));
            }
        }
        
        pl.removeAllIndexedParameterValues("AidTo");
        
        for(int index = 0; index < aidToAbilities.size(); index++) {
            pl.addIndexedParameterValue("AidTo", aidToAbilities.get(index));
        }
        
        ability.reconfigurePower();
        
        return errorList;
    }
}
