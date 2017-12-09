/*
 * Skill.java
 *
 * Created on September 24, 2002, 1:07 PM
 */

package champions;

import champions.interfaces.*;

/**
 *
 * @author  Trevor Walker
 */
public abstract class Skill extends Power {
    
    /**
     * Creates a new instance of Skill
     */
    public Skill() {
    }
    
    /**
     * Returns the roll necessary to make a skill roll, based upon the ability and target.
     * 
     * The default version of Skill.getSkillRoll returns the value set in the
     * v/p Ability.SKILLROLL.  This method should be overriden with a method which
     * calculates the SKILLROLL on the fly when ever possible.
     * 
     * 
     * @param ability Ability to base roll upon.
     * @param target Target to base roll upon.
     * @return Roll necessary to succeed at skill.
     */    
    public int getSkillRoll(Ability ability, Target target) {
        Integer i = ability.getIntegerValue("Ability.SKILLROLL");
        return i == null ? 0 : i.intValue();
    }
    
    /** Set the Default Skill Roll for a skill.
     *
     * This method sets the default skill roll for a skill, but setting the Ability.SKILLROLL v/p.
     * This method can be used if the skill roll is completely static and will not change based upon 
     * the target who uses the skill.  If the skill roll can change, the subclass should override 
     * getSkillRoll and return a calculated value.
     *
     * @param ability Ability to store skillroll info in.
     * @param skillRoll Skill Roll value to store.
     */
    public void setSkillRoll(Ability ability, int skillRoll) {
        ability.add("Ability.SKILLROLL", new Integer(skillRoll), true, false);
    }
    
}
