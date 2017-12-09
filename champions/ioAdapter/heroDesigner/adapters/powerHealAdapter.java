/*
 * powerHeal.java
 *
 * Created on April 14, 2004, 12:12 AM
 */

package champions.ioAdapter.heroDesigner.adapters;

import champions.Ability;
import champions.Characteristic;
import champions.PADRoster;
import champions.Power;
import champions.SpecialEffect;
import champions.Target;
import champions.interfaces.AbilityList;
import champions.parameters.ParameterList;
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
public class powerHealAdapter extends AbstractPowerXMLAdapter implements PowerXMLAdapter {
    
    private static String XMLID = "HEALING";
    private static String[][] translationArray = {
        { "LEVELS", "HealDie" },
        { "INPUT", "HealTo", "healToSpecial"},
        { "ADDER(XMLID=CANHEALLIMBS)", "CanHealLimbs", "trueSpecial" },
        { "ADDER(XMLID=RESURRECTION)", "Resurrection", "trueSpecial" },
        { "MODIFIER(XMLID=REGENEXTRATIME)", "Type", "setSpecial", "Regeneration" },
        { "MODIFIER(XMLID=REGENEXTRATIME).OPTIONID", "Rate", "rateSpecial" },
        
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
    
    public void healToSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        if ( "Simplified Healing".equals(attrValue) ) {
            pl.setParameterValue("Type", "Simplified Healing");
        }
        else {
            pl.addIndexedParameterValue(parameterName, attrValue );
        }
    }
    
    public void rateSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        System.out.println("powerHealAdapter.rateSpecial() - Not writen.  Please finish.");
    }
    
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
        for(int index = 0; index < pl.getIndexedParameterSize("HealTo"); index++) {
            String aidToString = (String)pl.getIndexedParameterValue("HealTo", index);
            
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
        
        pl.removeAllIndexedParameterValues("HealTo");
        
        for(int index = 0; index < aidToAbilities.size(); index++) {
            pl.addIndexedParameterValue("HealTo", aidToAbilities.get(index));
        }
        
        ability.reconfigurePower();
        
        return errorList;
    }
}
