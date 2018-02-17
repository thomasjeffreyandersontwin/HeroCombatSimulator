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
    

    public void additionalPointsSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        pl.addIndexedParameterValue("AidTo", attrValue);
    }
    
 
    public XMLParseError finalize(Ability ability) {
        XMLParseErrorList errorList = null;
        
        Target source = ability.getSource();
        AbilityList abilities = source.getAbilityList();
        
        ArrayList aidToAbilities = new ArrayList();
        
        ParameterList pl = ability.getPower().getParameterList(ability, -1);
        for(int index = 0; index < pl.getIndexedParameterSize("AidTo"); index++) 
        {
            String aidToString = (String)pl.getIndexedParameterValue("AidTo", index);
            
            SpecialEffect s;
            boolean found = false;
            String[] aidToStrings ={aidToString};
            if(aidToString.contains(",")) 
            {
            	aidToStrings = aidToString.split(",");
            }
            
            for(int j =0; j< aidToStrings.length;j++) {
            	aidToStrings[j] = aidToStrings[j].trim();
	            if ( (s = PADRoster.getSharedSpecialEffectInstance(aidToStrings[j])) != null ) {
	                if (s != null) {
	                    aidToAbilities.add(s);
	                    found = true;
	                }
	            }
	            
	            if ( ! found && source.hasStat( aidToStrings[j].toUpperCase() )) {
	                aidToAbilities.add(Characteristic.createCharacteristic(aidToStrings[j].toUpperCase(), false));
	                found = true;
	            }
	            
	            if ( ! found ) {
	                Ability aidToAbility = abilities.getAbility(aidToStrings[j],true);
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
	                    if ( name.equals( aidToStrings[j] ) ){
	                        Ability a = PADRoster.getSharedAbilityInstance(name);
	                        Power p = a.getPower();
	                        aidToAbilities.add(p);
	                        found = true;
	                    }
	                }
	            }
	            if ( !found ) {
	                if ( errorList == null ) errorList = new XMLParseErrorList();
	                errorList.addXMLParseError( new HDImportError("Aid destination \"" + aidToStrings[j] + "\" not recognized during import", HDImportError.IMPORT_ERROR));
	            }
	            found=false;
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
