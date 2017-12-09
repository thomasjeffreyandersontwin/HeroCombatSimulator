/*
 * powerHKA.java
 *
 * Created on March 29, 2004, 10:11 PM
 */

package champions.ioAdapter.heroDesigner.adapters;

import champions.Ability;
import champions.ChampionsMatcher;
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
import xml.XMLParseError;
import xml.XMLParseErrorList;

/**
 *
 * @author  1425
 */
public class powerTransferAdapter extends AbstractPowerXMLAdapter implements PowerXMLAdapter {
    
    private static String XMLID = "TRANSFER";
    private static String[][] translationArray = {
        { "LEVELS", "TransferDie" },
        { "INPUT", "TransferTo"},
                
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
    
    public XMLParseError finalize(Ability ability) {
        XMLParseErrorList errorList = null;
        
        Target source = ability.getSource();
        AbilityList abilities = source.getAbilityList();
        
        ArrayList transferToAbilities = new ArrayList();
        ArrayList transferFromAbilities = new ArrayList();
        
        ParameterList pl = ability.getPower().getParameterList(ability, -1);
        for(int index = 0; index < pl.getIndexedParameterSize("TransferTo"); index++) {
            String transferString = (String)pl.getIndexedParameterValue("TransferTo", index);
            String transferToString;
            String transferFromString;
            
            if ( ChampionsMatcher.matches("(From|from)?\\s*(.*)\\s+(to|To)\\s+(.*)", transferString) ) {
                transferFromString = ChampionsMatcher.getMatchedGroup(2);
                transferToString = ChampionsMatcher.getMatchedGroup(4);
            }
            else {
                System.out.println("Unreccognized Transfer Description.  Use Format:\n  [From] <Power|Stat> to <Power|Stat>\n");
                continue;
            }
            
            
            SpecialEffect s;
            boolean found;
            
            found = false;
            if ( (s = PADRoster.getSharedSpecialEffectInstance(transferToString)) != null ) {
                if (s != null) {
                    transferToAbilities.add(s);
                    found = true;
                }
            }
            
            if ( ! found && source.hasStat( transferToString.toUpperCase() )) {
                transferToAbilities.add(Characteristic.createCharacteristic(transferToString.toUpperCase(), false));
                found = true;
            }
            
            if ( ! found ) {
                Ability transferToAbility = abilities.getAbility(transferToString,true);
                if ( transferToAbility != null ) {
                    transferToAbilities.add(transferToAbility);
                    found = true;
                }
            }
            
            if ( ! found ) {
                // Not a stat, so iterate through the Powers Types...
                Iterator i = PADRoster.getAbilityIterator();
                while(i.hasNext()) {
                    String name = (String)i.next();
                    if ( name.equals( transferToString ) ){
                        Ability a = PADRoster.getSharedAbilityInstance(name);
                        Power p = a.getPower();
                        transferToAbilities.add(p);
                        found = true;
                    }
                }
            }
            
            if ( !found ) {
                if ( errorList == null ) errorList = new XMLParseErrorList();
                errorList.addXMLParseError( new HDImportError("Transfer source \"" + transferToString + "\" not recognized during import", HDImportError.IMPORT_ERROR));
            }
            
            found = false;
            if ( (s = PADRoster.getSharedSpecialEffectInstance(transferFromString)) != null ) {
                if (s != null) {
                    transferFromAbilities.add(s);
                    found = true;
                }
            }
            
            if ( ! found && source.hasStat( transferFromString.toUpperCase() )) {
                transferFromAbilities.add(Characteristic.createCharacteristic(transferFromString.toUpperCase(), false));
                found = true;
            }
            
            if ( ! found ) {
                Ability transferFromAbility = abilities.getAbility(transferFromString,true);
                if ( transferFromAbility != null ) {
                    transferFromAbilities.add(transferFromAbility);
                    found = true;
                }
            }
            
            if ( ! found ) {
                // Not a stat, so iterate through the Powers Types...
                Iterator i = PADRoster.getAbilityIterator();
                while(i.hasNext()) {
                    String name = (String)i.next();
                    if ( name.equals( transferFromString ) ){
                        Ability a = PADRoster.getSharedAbilityInstance(name);
                        Power p = a.getPower();
                        transferFromAbilities.add(p);
                        found = true;
                    }
                }
            }
            
            if ( !found ) {
                if ( errorList == null ) errorList = new XMLParseErrorList();
                errorList.addXMLParseError( new HDImportError("Transfer destination \"" + transferFromString + "\" not recognized during import", HDImportError.IMPORT_ERROR));
            }
        }
        
        pl.removeAllIndexedParameterValues("TransferTo");
        pl.removeAllIndexedParameterValues("TransferFrom");
        
        for(int index = 0; index < transferToAbilities.size(); index++) {
            pl.addIndexedParameterValue("TransferTo", transferToAbilities.get(index));
        }
        
        for(int index = 0; index < transferFromAbilities.size(); index++) {
            pl.addIndexedParameterValue("TransferFrom", transferFromAbilities.get(index));
        }
        
        ability.reconfigurePower();
        
        return errorList;
    }
}
