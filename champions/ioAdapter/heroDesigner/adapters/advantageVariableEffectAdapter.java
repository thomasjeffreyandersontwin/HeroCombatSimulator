/*
 * advantageVariableEffect.java
 *
 * Created on April 10, 2004, 10:11 PM
 */

package champions.ioAdapter.heroDesigner.adapters;

import champions.ChampionsMatcher;
import champions.Ability;
import champions.parameters.ParameterList;
import champions.Power;
import champions.ioAdapter.heroDesigner.AbstractModifierXMLAdapter;
import champions.ioAdapter.heroDesigner.ModifierXMLAdapter;
import champions.powers.powerAid;
import champions.powers.powerDrain;
import champions.powers.powerSuppress;
import champions.powers.powerDispel;
import org.w3c.dom.Node;
/**
 *
 * @author  1425
 */
public class advantageVariableEffectAdapter extends AbstractModifierXMLAdapter implements ModifierXMLAdapter {
    
    private static String XMLID = "VARIABLEEFFECT";
    private static String[][] translationArray = {
        // { "COMMENTS", "AdjustmentAffectsImport" },
        { "OPTIONID", null, "optionIdSpecial" },
        { "OPTION_ALIAS", null, "aliasSpecial"}
    };
    
    /** Returns the XMLID for this Power.
     *
     * Subclass should either override this to return their XMLID or override
     * the identify method to do more complicated identification tasks.
     */
    public String getXMLID() {
        return XMLID;
    }
    
    public void optionIdSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        //ability.createIndexed("aidTo", "OBJECT", attrValue, false);
        if ( "ONE".equals(attrValue) ) {
            pl.setParameterValue("AdjustmentAffects", "Any 1 Power Of A Given SFX");
        } else if ( "TWO".equals(attrValue) ) {
            pl.setParameterValue("AdjustmentAffects", "Any 2 Powers Of A Given SFX");
        } else if ( "FOUR".equals(attrValue) ) {
            pl.setParameterValue("AdjustmentAffects", "Any 4 Powers Of A Given SFX");
        } else if ( "ALL".equals(attrValue) ) {
            pl.setParameterValue("AdjustmentAffects", "Variable, All Powers Of A Given SFX");
        }
    }
    
    public void aliasSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
        //ability.createIndexed("aidTo", "OBJECT", attrValue, false);
        String sfx = null;
        if ( ChampionsMatcher.matches("all (.*) powers simultaneously", attrValue)) {
            sfx = ChampionsMatcher.getMatchedGroup(1);
        } else if (ChampionsMatcher.matches("(.*) simultaneously", attrValue)) {
            sfx = ChampionsMatcher.getMatchedGroup(1);
        } else if (ChampionsMatcher.matches("any (.*) power one at a time", attrValue)) {
            sfx = ChampionsMatcher.getMatchedGroup(1);
        }
        
        if ( sfx != null ) {
            Power p = ability.getPower();
            
            if ( p instanceof powerAid ) {
                ParameterList apl = p.getParameterList(ability,-1);
                apl.addIndexedParameterValue("AidTo", sfx);
            } else if ( p instanceof powerDrain ) {
                ParameterList apl = p.getParameterList(ability,-1);
                apl.addIndexedParameterValue("DrainFrom", sfx);
            } else if ( p instanceof powerSuppress ) {
                ParameterList apl = p.getParameterList(ability,-1);
                apl.addIndexedParameterValue("DrainFrom", sfx);
            } else if ( p instanceof powerDispel ) {
                ParameterList apl = p.getParameterList(ability,-1);
                apl.addIndexedParameterValue("DrainFrom", sfx);
            }
        }
    }
    /** Returns the Translation Array for the Power.
     *
     * The Subclass should either override this to return their translationArray
     *or override the importXML method to do more complicated import tasks.
     */
    public String[][] getTranslationArray() {
        return translationArray;
    }
}
