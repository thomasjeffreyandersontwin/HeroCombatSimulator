/*
 * BlockNode.java
 *
 * Created on May 2, 2002, 10:21 PM
 */

package champions.attackTree;


import champions.ActivationInfo;
import champions.BattleEvent;
import champions.CVList;
import champions.MentalEffectInfo;
import champions.Preferences;
import champions.Target;
import javax.swing.UIManager;

import champions.exception.BattleEventException;

/**
 *
 * @author  Trevor Walker
 * @version
 */
public class MentalEffectNode extends DefaultAttackTreeNode {
    
    /** Holds value of property targetReferenceNumber. */
    private int targetReferenceNumber;
    
    /** Holds value of property target. */
    private Target target;
    
    /** Creates new BlockNode */
    public MentalEffectNode(String name) {
        this.name = name;
        icon = UIManager.getIcon("AttackTree.mentalEffectsIcon");
    }
    
    public boolean activateNode(boolean manualOverride) {
        boolean acceptActivation = false;
        
        ActivationInfo ai = getBattleEvent().getActivationInfo();
        boolean allowotherclassofmind = (Boolean) Preferences.getPreferenceList().getParameterValue("MentalEffectAllowOtherClasseOfMind" );
        
        int tindex = ai.getTargetIndex(targetReferenceNumber, getTargetGroup());
        
        if ( tindex != -1 ) {
            
            Target target = ai.getTarget(tindex);
            Target source = ai.getAbility().getSource();
            
            // Look up the info...
            MentalEffectInfo mei = (MentalEffectInfo)ai.getIndexedValue(tindex, "Target","MENTALEFFECTINFO");
            if (mei == null ) {
                mei = new MentalEffectInfo();
                
                // Put it in the ActivationInfo for later...
                ai.addIndexed(tindex, "Target", "MENTALEFFECTINFO", mei, true);
            }
            String mentaleffectleveldesc = mei.getMentalEffectLevelDesc();
            int effectlevelvalue = mei.convertMentalEffectLevelDesctoInt(mentaleffectleveldesc);
            //mei.setMentalEffectLevel(effectlevelvalue);
            mei.setMentalEffectSource(source);
            
            //if the class of minds aren't equal then check to see if the preferences
            //allow for attacking class of minds outside of the attacker's list
            //if preferences do allow it then put in the -3 penalty if they don't
            //stop the attack activation
            boolean match = false;
            int targetcmsize = target.getIndexedSize("ClassOfMind");
            int abilitycmsize = ai.getAbility().getIndexedSize("AdditionalClasses");
            for (int index = 0; index < abilitycmsize; index++) {
                for (int index2 = 0; index2 < targetcmsize; index2++) {
                    if (ai.getAbility().getIndexedStringValue(index,"AdditionalClasses","CLASS" ).equals(target.getIndexedStringValue(index2,"ClassOfMind","NAME" ))) {
                        match = true;
                    }
                }
                
            }
            if (match == false) {
                if ( allowotherclassofmind ) {
                    mei.setMentalEffectClassOfMindEffectPenalty(new Integer(-10));
                    CVList cvl = ai.getCVList(tindex);
                    cvl.addSourceCVModifier("Other Class of Mind Penalty", -3 );
                }
                else {
                    ai.setTargetHitMode(tindex, "FORCEMISS");
                    getBattleEvent().addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " cannot be affected by " + ai.getAbility().getName() + ". Target  has different class of mind.", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(target.getName() + " cannot be affected by " + ai.getAbility().getName() + ". Target  has different class of mind.", BattleEvent.MSG_NOTICE)); // .addMessage(target.getName() + " cannot be affected by " + ai.getAbility().getName() + ". Target  has different class of mind.", BattleEvent.MSG_NOTICE);
                    
                }
            }
            
            if ( manualOverride || nodeRequiresInput() ) {
                acceptActivation = true;
                attackTreePanel.setInstructions( "Enter Mental Effect Information...");
                attackTreePanel.showInputPanel(this, MentalEffectPanel.getDefaultPanel(battleEvent, getTargetGroup(), targetReferenceNumber, mei));
            }
        }
        
        
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        return acceptActivation;
    }
    
    private boolean nodeRequiresInput() {
        return getAutoBypassTarget().getBooleanProfileOption( getAutoBypassOption());
    }
    
    public boolean processAdvance() throws BattleEventException {
        boolean advance = true;
        
        return advance;
    }
    
    /** Getter for property targetReferenceNumber.
     * @return Value of property targetReferenceNumber.
     */
    public int getTargetReferenceNumber() {
        return targetReferenceNumber;
    }
    
    /** Setter for property targetReferenceNumber.
     * @param targetReferenceNumber New value of property targetReferenceNumber.
     */
    public void setTargetReferenceNumber(int targetReferenceNumber) {
        this.targetReferenceNumber = targetReferenceNumber;
    }
    
    /** Getter for property target.
     * @return Value of property target.
     */
    public Target getTarget() {
        return target;
    }
    
    /** Setter for property target.
     * @param target New value of property target.
     */
    public void setTarget(Target target) {
        this.target = target;
    }
    
    
    
    public String getAutoBypassOption() {
        return "SHOW_MENTAL_EFFECT_PANEL";
    }
    
    public Target getAutoBypassTarget() {
        return battleEvent.getSource();
    }
}
