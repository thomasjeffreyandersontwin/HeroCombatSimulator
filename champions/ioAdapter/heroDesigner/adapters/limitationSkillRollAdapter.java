/*
 * limitationSkillRollAdapter.java
 *
 * Created on April 11, 2004, 12:30 AM
 */

package champions.ioAdapter.heroDesigner.adapters;

import champions.Ability;
import champions.AbilityAlias;
import champions.Target;
import champions.interfaces.Limitation;
import champions.ioAdapter.heroDesigner.AbstractModifierXMLAdapter;
import champions.ioAdapter.heroDesigner.HDImportError;
import champions.ioAdapter.heroDesigner.ModifierXMLAdapter;
import champions.parameters.ParameterList;
import org.w3c.dom.Node;
import xml.DefaultXMLParseError;
import xml.XMLParseError;
import xml.XMLParseErrorList;

/**
 *
 * @author  1425
 */
public class limitationSkillRollAdapter extends AbstractModifierXMLAdapter implements ModifierXMLAdapter { 
    
    private static String XMLID = "REQUIRESASKILLROLL";
    private static String[][] translationArray = {
        { "ROLLALIAS", null, "skillSpecial" },
        { "OPTION", null, "skillOption" },
        { "ADDER(XMLID=NOAPPENALTY)", "APModifier", "setSpecial", "No Active Point Penalty"},
        { "ADDER(XMLID=MINUS1PER20)", "APModifier", "setSpecial", "-1 per 20 Active Points"},
        { "ADDER(XMLID=MINUS1PER5)", "APModifier", "setSpecial", "-1 per 5 Active Points"},
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
    public void skillSpecial(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {
            
        ability.add("RequiresSkillRoll.SKILLNAME", attrValue);
    }
    
    public void skillOption(Ability ability, Node node, String attrValue, ParameterList pl, String parameterName, String specialData) {

        ability.add("RequiresSkillRoll.OPTION", attrValue);
    }

    /** Finalizes the Import of a power/adder from an XML source.
     *
     * This method should be called after the Ability has been added to it's
     * source (if one exists).  If called with a null source, then the 
     * ability (and the power and adders) can assume that it will not be
     * attached to a specific source.
     */
    public XMLParseError finalize(Ability ability, Object modifier, int index)  {
        XMLParseErrorList errorList = null;

        String option = ability.getStringValue("RequiresSkillRoll.OPTION");
        ability.remove("RequiresSkillRoll.OPTION");
        
        String skillName = ability.getStringValue("RequiresSkillRoll.SKILLNAME");
        ability.remove("RequiresSkillRoll.SKILLNAME");
        
        Ability a = null;
        Target source = ability.getSource();

        /// \todo If option is BASICRSR, then we look for a skillPower ability.
        /// If option is a number, then we need a basic "roll ability," or maybe
        /// the roll can just happen in limitationSkillRoll (getSkillRoll can
        /// just return the roll value).
        if ( skillName != null ) {
            // We have to translate the AbsorbTo to be abilities...

            a = source.getAbility(skillName);

            if ( a == null ) {
                a = source.getAbility(skillName + " Roll");
            }

            if ( a == null ) {
                a = source.getAbility(skillName.toUpperCase() + " Roll");
            }
        }
        
        if ( a != null ) {
            Limitation l = ability.getLimitation(index);

            ParameterList pl = l.getParameterList();
            pl.setParameterValue("Skill", new AbilityAlias(source, a));

            ability.reconfigureLimitation(l, pl, index);
        }
        else {
            Limitation l = ability.getLimitation(index);
            ParameterList pl = l.getParameterList();
            pl.setParameterValue("Skill", "None");

            //errorList.addXMLParseError( new DefaultXMLParseError("Character does not have skill '" + skillName + "'.", HDImportError.IMPORT_ERROR));
            if ( errorList == null ) errorList = new XMLParseErrorList();
            errorList.addXMLParseError( new DefaultXMLParseError("Character does not have skill'" + skillName + "'.", HDImportError.IMPORT_ERROR));
        }
        
        return errorList;
    }
}
