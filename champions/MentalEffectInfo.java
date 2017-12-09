/*
 * MentalEffectInfo.java
 *
 * Created on November 16, 2003, 1:51 PM
 */

package champions;
import champions.Ability;
import champions.parameters.ParameterList;

import champions.powers.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

import champions.*;
import champions.interfaces.*;
import champions.event.*;
import champions.exception.*;
import champions.attackTree.*;
import champions.*;
import champions.ArrayListModel;
import champions.BattleEvent;
import champions.event.PADValueEvent;
import champions.interfaces.ChampionsConstants;
import champions.interfaces.PADValueListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import tjava.PopupListCellRenderer;
import tjava.ToggleSelectionModel;

import champions.powers.powerMentalIllusions;
import champions.powers.powerMindControl;
import champions.powers.powerMindScan;
import java.util.Vector;
import javax.swing.event.*;
import java.awt.event.*;
/**
 *
 * /**
 *
 * @author  1425
 */
public class MentalEffectInfo extends DetailList {
    
    /** Creates a new instance of MentalEffectInfo */
    public MentalEffectInfo() {
        setFireChangeByDefault(false);
    }
  
    //these functions should never be used...convert use these instead:
    //setMentalEffectLevelDesc()
    //getMentalEffectLevelDesc()
    //convert to int with:
    //convertMentalEffectLevelDesctoInt()
    
//    /** Returns level representing mental effect.
//     *
//     * 0 = EGO
//     * 1 = EGO+10
//     * 2 = EGO+20
//     * 3 = EGO+30
//     */
//    public int getMentalEffectLevel() {
//        Integer i = getIntegerValue("MentalEffect.LEVEL");
//        return (i==null)?0:i.intValue();
//    }
    
//    /** Set the mental effect level.
//     *
//     * 0 = EGO
//     * 1 = EGO+10
//     * 2 = EGO+20
//     * 3 = EGO+30
//     */
//    
//    
//    public void setMentalEffectLevel(int level) {
//        add("MentalEffect.LEVEL", new Integer(level), true);
//    }
    
    
    //get and set the leveldesc for the mental power.  Every string contains the same startsWith()
    //string that can be used to convert to an int value and therefore a level to add to the base stat
    //for effect processing
    public String getMentalEffectLevelDesc() {
        String leveldesc = getStringValue("MentalEffect.LEVELDESC");
        return leveldesc;
    }
    
    public void setMentalEffectLevelDesc(String leveldesc) {
        add("MentalEffect.LEVELDESC", leveldesc, true);
    }

    public int convertMentalEffectLevelDesctoInt(String mentaleffectleveldesc) {
        
        int i = 0;
        if (mentaleffectleveldesc != null) {
            if  (mentaleffectleveldesc.startsWith("Greater")) {
                i = 0;
            }
            else if (mentaleffectleveldesc.startsWith("STAT + 10")) {
                i = 1;
            }
            else if (mentaleffectleveldesc.startsWith("STAT + 20")) {
                i = 2;
            }
            else if (mentaleffectleveldesc.startsWith("STAT + 30")) {
                i = 3;
            }
        }
        return i;
    }
        
    //this pair is used to set and get the first possibile effect level for the power.  This 
    //was needed for the mandatory effect limitation when applied
    public void setFirstLevelDesc(String leveldesc) {
        add("MentalEffect.FIRSTLEVELDESC", leveldesc, true);
    }
    
    public String getFirstEffectLevelDesc() {
        String leveldesc = getStringValue("MentalEffect.FIRSTLEVELDESC");
        return leveldesc;
    }
    
    /** Returns any additional modifiers that were set.
     */
    public int getMentalEffectAdditionalModifier() {
        Integer i = getIntegerValue("AdditionalModifier.LEVEL");
        return (i==null)?0:i.intValue();
    }
    
    

    /** Set any additional modifiers.
     *
     */
    public void setMentalEffectAdditionalModifier(int modifier) {
        add("AdditionalModifier.LEVEL", new Integer(modifier), true);
    }
    
    /** Returns the mental effect description.
     */
    public String getMentalEffectDescription() {
        return getStringValue("MentalEffect.DESCRIPTION");
    }
    
    /** Sets the mental effect description.
     */
    public void setMentalEffectDescription(String description) {
        add("MentalEffect.DESCRIPTION", description, true);
    }
    
    /** Returns the mental effect other class of mind effect penalty.
     */
    public Integer getMentalEffectClassOfMindEffectPenalty() {
        return getIntegerValue("MentalEffect.CLASSOFMINDEFFECTPENALTY");
    }
    
    
    /** Sets the mental effect other class of mind effect penalty.
     */
    public void setMentalEffectClassOfMindEffectPenalty(Integer classofmindeffectpenalty) {
        add("MentalEffect.CLASSOFMINDEFFECTPENALTY", classofmindeffectpenalty , true);
    }
    
    
    /** Sets the mental effect source
     */
    public void setMentalEffectSource(Target source) {
        add("MentalEffect.MENTALEFFECTSOURCE", source , true);
    }
    
    /** Returns the mental effect other class of mind effect penalty.
     */
    public Target getMentalEffectSource() {
        return (Target)getValue("MentalEffect.MENTALEFFECTSOURCE");
    }
    
    /** Adds a mental effect standard modifier.
     *
     * Standard Modifiers are stored with a string (the long description)
     * and the value of the modifier.<P>
     *
     * If a modifier already exists, the value of the original will be updated.
     * If a modifier does not exist, it will be added and the value set
     * appropriately.<P>
     *
     * @returns Index of added/modified Modifier.
     */
    public int addMentalEffectModifier(String description, int value) {
        int index = findIndexed("Modifier", "DESCRIPTION", description);
        if ( index == -1 ) {
            // Negative 1 indicates it does not already exist...
            index = createIndexed("Modifier","DESCRIPTION", description);
        }
        
        // Update the value
        addIndexed(index, "Modifier", "VALUE", new Integer(value), true);
        
        return index;
    }
    
    /** Returns the number of standard modifiers currently set.
     */
    public int getMentalEffectModifierCount() {
        return getIndexedSize("Modifier");
    }
    
    /** Removes a standard mental effect modifier from the list.
     *
     * If the modifier indicated does not exist, nothing is done.
     */
    public void removeMentalEffectModifier(String description) {
        int index = findIndexed("Modifier", "DESCRIPTION", description);
        if ( index != -1 ) {
            // Found it...
            removeMentalEffectModifier(index);
        }
    }
    
    /** Removes a standard mental effect modifier from the list.
     *
     * If the modifier indicated does not exist, nothing is done.
     */
    public void removeMentalEffectModifier(int index) {
        removeAllIndexed(index, "Modifier");
    }
    
    /** Returns index <code>index</code>'s description.
     */
    public String getMentalEffectModifierDescription(int index) {
        return getIndexedStringValue(index, "Modifier", "DESCRIPTION");
    }
    
    /** Returns index <code>index</code>'s description.
     */
    public int getMentalEffectModifierValue(int index) {
        Integer i = getIndexedIntegerValue(index, "Modifier", "VALUE");
        return (i==null)?0:i.intValue();
    }
    
    /** Finds the index of a standard modifier based on description.
     *
     * @returns -1 if the modifier is not found.
     */
    public int findMentalEffectModifier(String description) {
        return findIndexed("Modifier", "DESCRIPTION", description);
    }
    
    /** Returns the total modifier given the currently set values.
     *
     * The total modifier is relative to the EGO value of the
     * target.
     */
    public int getMentalEffectTotalModifiers() {
        int mod = 0;
        //grab the level desired by the attacker from the mei
        String leveldesc = getMentalEffectLevelDesc();
        Integer leveldesired = new Integer(convertMentalEffectLevelDesctoInt(leveldesc));

        
        mod += 10 * leveldesired.intValue();
        mod += getMentalEffectAdditionalModifier();
        
        int index = getMentalEffectModifierCount() - 1 ;
        for(; index >= 0; index--) {
            mod += getMentalEffectModifierValue(index);
        }
        
        return mod;
    }
    
    public String getLimitationMandatoryEffectLevelDesc(BattleEvent battleEvent) {
        ActivationInfo ai = battleEvent.getActivationInfo();
        Ability ability = ai.getAbility();
        
        String mandatoryEffectName = limitationMandatoryEffect.limitationName;
        
        boolean mandatoryeffectenforced = false;
        String level;
        
        int index2 = ability.findLimitation(mandatoryEffectName);
        
        if ( index2 != -1) {
            Limitation lim = ability.getLimitation(index2);
            ParameterList pl2 = lim.getParameterList();
            level = (String)pl2.getParameterValue("Level");
            return level;
        }
        return null;
    }
    
    public boolean hasLimitationMandatoryEffect(BattleEvent battleEvent) {
        ActivationInfo ai = battleEvent.getActivationInfo();
        Ability ability = ai.getAbility();
        Limitation mandatoryeffect = new limitationMandatoryEffect();
        boolean mandatoryeffectenforced = false;
        if (ability.hasLimitation(mandatoryeffect)) {
            return true;
        }
        return false;
    }
    
}
