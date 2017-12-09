/*
 * AttributeBasedSkillRollInfo.java
 *
 * Created on November 9, 2006, 5:17 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions;

import champions.genericModifiers.BaseGenericModifier;
import champions.genericModifiers.GenericModifierList;
import champions.genericModifiers.IncrementGenericModifier;
import champions.genericModifiers.SkillModifierList;

/**
 *
 * @author 1425
 */
public class AttributeBasedSkillRollInfo extends SkillRollInfo {
    
    protected String attribute;
    
    /** Creates a new instance of AttributeBasedSkillRollInfo */
    public AttributeBasedSkillRollInfo(Target source, String attribute) {
        super(source);
        
        setAttribute(attribute);
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }
    
    public int getBaseRollNeeded() {
        return 9 + (source.getCurrentStat(attribute) / 5);
    }
    
    /** Returns a short description of the skill roll.
     *
     *  It should be possible to place the description
     *  in the follow text:
     *
     *  "Target is make a <Desc> skill roll."
     */
    public String getShortDescription() {
        return attribute + " based";
    }
    
    public SkillModifierList createModifierList() {
        SkillModifierList gml = new SkillModifierList(getShortDescription());
        gml.setBaseModifier( new BaseGenericModifier( getBaseRollNeeded() ) );
        gml.setUserModifier( new IncrementGenericModifier("Adjustment", 0));
        
        setModifierList(gml);
        return gml;
    }
}
