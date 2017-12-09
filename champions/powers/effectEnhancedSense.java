/*
 * effectUnconscious.java
 *
 * Created on October 2, 2000, 4:06 PM
 */

package champions.powers;

import champions.Ability;
import champions.BattleEvent;
import champions.LinkedEffect;
import champions.PADRoster;
import champions.SenseBonusModifier;
import champions.Target;
import champions.battleMessage.GainedEffectSummaryMessage;
import champions.exception.BattleEventException;
import champions.parameters.ParameterList;


/**
 *
 * @author  unknown
 * @version
 */
public class effectEnhancedSense extends LinkedEffect {
    
    /** Creates new effectUnconscious */
    public effectEnhancedSense(String effectName, Ability ability) {
        super( effectName, "LINKED", true );
        setAbility(ability);
    }
    
    public boolean addEffect(BattleEvent be, Target target) throws BattleEventException {
        Ability ability = getAbility();
        ParameterList pl = ability.getPower().getParameterList(ability, -1);
        
        String senseName = (String)pl.getParameterValue("EnhancedSense");
        String senseGroup = (String)pl.getParameterValue("SenseGroup");
        if ( senseName == null || senseName.equals("None")) return false;
        
        if ( target.getSense(senseName) == null ) {
            // Add the base sense to the target...
            target.addSense( PADRoster.getNewSense(senseName, senseGroup) );
        }

        
        // Create a SenseBonusModifier with all the options on the sense...
        String arcOfPerception = pl.getParameterStringValue("ArcOfPerception");
        boolean detect = pl.getParameterBooleanValue("Detect");
        boolean isSense = pl.getParameterBooleanValue("DetectSense");
        boolean discriminatory = pl.getParameterBooleanValue("Discriminatory");
        boolean analyze = pl.getParameterBooleanValue("Analyze");
        boolean targeting = pl.getParameterBooleanValue("Targeting");
        boolean ranged = pl.getParameterBooleanValue("Ranged");
        boolean tracking = pl.getParameterBooleanValue("Tracking");
        boolean transmit = pl.getParameterBooleanValue("Transmit");
        int enhancedPerception = pl.getParameterIntValue("EnhancedPerception");
        int magnification = pl.getParameterIntValue("Magnification");
      //  int rangeMultiplier = pl.getParameterIntValue("RangeMultiplier");
        int rapidLevel = pl.getParameterIntValue("RapidLevel");
        int telescopicLevel = pl.getParameterIntValue("TelescopicLevel");
        
        // Check to see if we already created it...
        
        SenseBonusModifier sbm = (SenseBonusModifier)getValue("Effect.SENSEBONUSMODIFIER");
        if ( sbm == null ) {
            sbm = new SenseBonusModifier( ability.getName(), senseName );
            add("Effect.SENSEBONUSMODIFIER", sbm, true);
        }
        
        sbm.setFunctioningBonus(true);
        
        sbm.setAnalyzeBonus(analyze);
        sbm.setDetectBonus(detect);
        sbm.setSenseBonus(isSense);
        sbm.setTargettingBonus(targeting);
        sbm.setRangedBonus(ranged);
        sbm.setTrackingBonus(tracking);
        sbm.setTransmitBonus(transmit);
        
        sbm.setEnhancedPerceptionBonus(enhancedPerception);
        sbm.setMicroscopicBonus(magnification);
        sbm.setTelescopicBonus(telescopicLevel);
        sbm.setRapidBonus(rapidLevel);
        
        be.addUndoableEvent( target.addSenseBonus( sbm ) );
        
        be.addBattleMessage( new GainedEffectSummaryMessage(target, this, true));
        
        return super.addEffect(be,target );
    }
    
    public void removeEffect(BattleEvent be, Target target) throws BattleEventException {
        super.removeEffect(be,target );
        
        SenseBonusModifier sbm = (SenseBonusModifier)getValue("Effect.SENSEBONUSMODIFIER");
        if ( sbm != null ) {
            be.addUndoableEvent( target.removeSenseBonus( sbm ) );
        }
        
        be.addBattleMessage( new GainedEffectSummaryMessage(target, this, false));
    }
    
    public void setAbility(Ability ability) {
        add("Effect.ABILITY", ability, true);
    }
    
    public Ability getAbility() {
        return (Ability)getValue("Effect.ABILITY");
    }
    
}