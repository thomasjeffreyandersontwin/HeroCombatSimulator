/*
 * SkillRollInfo.java
 *
 * Created on November 8, 2006, 11:55 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions;

import champions.enums.DiceType;
import champions.genericModifiers.SkillModifierList;

/**
 *
 * @author 1425
 */
abstract public class SkillRollInfo {
    
    protected Target source;
    private SkillModifierList modifierList;
    protected DiceRollInfo roll;
    protected String description;
    
    /** Creates a new instance of SkillRollInfo */
    protected SkillRollInfo() {
        
    }
    
    public SkillRollInfo(Target source) {
        setSource(source);
    }
    
    
    /** Return the unmodified base roll needed in order to succeed at the skill.
     *
     *  The roll should not include any modifiers.  This will be added
     *  later.
     */
    public abstract int getBaseRollNeeded();
    
    /** Creates the modifier list.
     *
     *  The modifier list should include a base skill roll entry, a generic
     *  adjustment entry, as well as any special skill roll dependent entries
     *  that should be applied.
     *
     *  As a side effect, this should also set the modifierList variable
     *  to the newly created modifier list.
     */
    public abstract SkillModifierList createModifierList();
    
    /** Returns the modified final roll needed in order to succeed at the skill.
     *
     */
    public int getFinalRollNeeded() {
        int baseRollNeeded = getBaseRollNeeded();
        if ( modifierList == null ) {
            return baseRollNeeded;
        }
        else {
            return modifierList.getValue(baseRollNeeded);
        }
    }
    
    /** Returns a short description of the skill roll.
     *
     *  It should be possible to place the description
     *  in the follow text:
     *
     *  "Target is make a <Desc> skill roll."
     */
    public abstract String getShortDescription();
    
    public boolean isSuccessful() {

        int stun = getDiceRollInfo().getDice().getStun(); 
        int needed = getFinalRollNeeded();
        
        return stun <= needed;
    }
    
    public int getSuccessMargin() {
        
        int stun = getDiceRollInfo().getDice().getStun(); 
        int needed = getBaseRollNeeded();
        
        return needed - stun;
    }

    public DiceRollInfo getDiceRollInfo() {
        if ( roll == null ) {
            roll = new DiceRollInfo(getShortDescription(), 3);
            roll.setType( DiceType.STUNONLY);
        }
        
        return roll;
    }

    public void setRoll(DiceRollInfo roll) {
        this.roll = roll;
    }

    public Target getSource() {
        return source;
    }

    public void setSource(Target source) {
        this.source = source;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SkillModifierList getModifierList() {
        return modifierList;
    }

    public void setModifierList(SkillModifierList modifierList) {
        this.modifierList = modifierList;
    }

}
