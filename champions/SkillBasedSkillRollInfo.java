/*
 * SkillBasedSkillRollInfo.java
 *
 * Created on November 9, 2006, 12:27 AM
 *
 * This is a SkillRollInfo based upon a Skill ability.
 */

package champions;

import champions.genericModifiers.BaseGenericModifier;
import champions.genericModifiers.GenericModifierList;
import champions.genericModifiers.IncrementGenericModifier;
import champions.genericModifiers.SkillModifierList;
import java.io.Serializable;

/**
 *
 * @author 1425
 */
public class SkillBasedSkillRollInfo extends SkillRollInfo implements Serializable {
    
    protected Ability skill;
    
    /**
     * Creates a new instance of SkillBasedSkillRollInfo
     */
    protected SkillBasedSkillRollInfo() {
        
    }
  
    public SkillBasedSkillRollInfo(Target source, Ability skill) {
        super(source);
        
        setSkill(skill);
    } 

    public Ability getSkill() {
        return skill;
    }

    public void setSkill(Ability skill) {
        this.skill = skill;
    }

    public int getBaseRollNeeded() {
        return skill.getSkillRoll( getSource() );
    }

    /** Sets the difficulty of the roll.
     *
     * Negative numbers indicate a more difficult roll.
     *
     * @param difficult
     */
    public void setAdjustment(int difficulty) {
        SkillModifierList gml = getModifierList();
        if ( gml == null ) {
            gml = createModifierList();
        }
        
        gml.setUserModifier( new IncrementGenericModifier("Adjustment", difficulty));
    }
    
    /** Returns a short description of the skill roll.
     *
     *  It should be possible to place the description
     *  in the follow text:
     *
     *  "Target is make a <Desc> skill roll."
     */
    public String getShortDescription() {
        return skill.getName();
    }

    public SkillModifierList createModifierList() {
        SkillModifierList gml = new SkillModifierList(getShortDescription() + " roll");
        gml.setBaseModifier( new BaseGenericModifier( getBaseRollNeeded() ) );
        gml.setUserModifier( new IncrementGenericModifier("Adjustment", 0));
        
        setModifierList(gml);
        return gml;
    }
    
}
