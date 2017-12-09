/*
 * ATDCInfoRoot.java
 *
 * Created on February 6, 2008, 11:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.abilityTree2;

import champions.Ability;
import champions.BattleEvent;
import champions.ChampionsUtilities;
import champions.Preferences;
import champions.powers.SpecialParameterIsMartialManeuver;
import champions.powers.powerHandToHandAttack;
import treeTable.DefaultTreeTableModel;

/**
 *
 * @author twalker
 */
public class ATDCInfoRoot extends ATNode {
    
    private BattleEvent battleEvent = null;
    
    /** Creates a new instance of ATDCInfoRoot */
    public ATDCInfoRoot() {
        super(null, null, false);
        
        setExpandedByDefault(true);
    }
    
    public BattleEvent getBattleEvent() {
        return battleEvent;
    }
    
    public void buildNode() {
        
        ATNode node;
        
        removeAndDestroyAllChildren();
        
        if ( battleEvent != null ) {
            Double adjustedBaseAbilityDC = battleEvent.getDoubleValue("AdjustedBase.BASE_DC");
            Double adjustedBaseManeuverDC = battleEvent.getDoubleValue("AdjustedBase.MANEUVER_DC");
            Double adjustedBaseStrUsed = battleEvent.getDoubleValue("AdjustedBase.STR_USED");
            Double adjustedBaseStrDC = battleEvent.getDoubleValue("AdjustedBase.STR_DC");
            Double adjustedMartialDC = battleEvent.getDoubleValue("AdjustedBase.MARTIAL_DC");
            Double adjustedBaseDC = battleEvent.getDoubleValue("AdjustedBase.DC");
            
            Integer bonusBody = battleEvent.getIntegerValue("Bonus.BODY");
            Integer bonusStun = battleEvent.getIntegerValue("Bonus.STUN");
            Double bonusCombatDC = battleEvent.getDoubleValue("Bonus.COMBAT_DC");
            Double bonusMiscDC = battleEvent.getDoubleValue("Bonus.MISC_DC");
            Double bonusStrDC = battleEvent.getDoubleValue("Bonus.STR_DC");
           // Double bonusPushedDC = battleEvent.getDoubleValue("Bonus.PUSHED_DC");
            Double bonusMartialDC = battleEvent.getDoubleValue("Bonus.MARTIAL_DC");
            Double bonusManeuverDC = battleEvent.getDoubleValue("Bonus.MANEUVER_DC");
            Double bonusMovementDC = battleEvent.getDoubleValue("Bonus.MOVEMENT_DC");
            Integer bonusStrUsed = battleEvent.getIntegerValue("Bonus.STR_USED");
            Integer bonusPushedUsed = battleEvent.getIntegerValue("Bonus.PUSHED_USED");
            
            Double ppdcStr = battleEvent.getDoubleValue("PPDC.STR");
            Integer normalStrUsed = battleEvent.getIntegerValue("Normal.STR_USED");
            Integer pushedStrUsed = battleEvent.getIntegerValue("Pushed.STR_USED");
            Double totalDC = battleEvent.getDoubleValue("Total.DC");
            
            Double strMultiplier = battleEvent.getDoubleValue("RealMultipler.STR");
            Integer strMaximum = battleEvent.getIntegerValue("Maximum.STR");
            Integer strMinimum = battleEvent.getIntegerValue("Minimum.STR");
            
            
            Ability ability = battleEvent.getAbility();
            Ability maneuver = battleEvent.getManeuver();
            
            boolean isWeapon = ability.isWeapon();
            boolean isKilling = ability.isKillingAttack();
            boolean isAbilityMartial = ability.hasSpecialParameter(SpecialParameterIsMartialManeuver.specialParameterName);
            boolean isHA = ability.getPower() instanceof powerHandToHandAttack;
            boolean isHeroic;
            String h = (String)Preferences.getPreferenceList().getParameterValue("RuleSet" );
            isHeroic = ( h != null && h.equals( "Heroic" ) );
            
            if ( ability.isManeuver() == false ) {
                add(new ATDCInfoEntryNode("Ability Base DC", "+ " + adjustedBaseAbilityDC, "Damage Classes from ability."));
          
               
                if ( isHA ) {
                    add(new ATDCInfoEntryNode("HA Base STR DC", "+ " + adjustedBaseStrDC, String.format("Hand attacks count STR DCs as base damage.  %.0f STR used at +1 DC per %.1f STR.", adjustedBaseStrUsed, ppdcStr/strMultiplier)));
                }
                
                if ( isAbilityMartial ) {
                    // Martial abilities get their Martial DC as base
                    if ( isKilling ) {
                        if ( adjustedMartialDC != null && adjustedMartialDC > 0 ) {
                            add(new ATDCInfoEntryNode("Martial Base DC", "+ " + adjustedMartialDC, "Martial abilities count martial DCs as base damage.  For KA, 2 Martial DC = +1 Base DC."));
                        }
                    } else {
                        if ( adjustedMartialDC != null && adjustedMartialDC > 0 ) {
                            add(new ATDCInfoEntryNode("Martial Base DC", "+ " + adjustedMartialDC, "Martial abilities count martial DCs count as base damage.  1 Martial DC = +1 Base DC."));
                        }
                    }
                }
            } else {
                if ( isKilling ) {
                    add(new ATDCInfoEntryNode("Killing Base DC", "+ " + adjustedBaseManeuverDC, "Killing maneuver base damage."));
                    if ( adjustedMartialDC != null && adjustedMartialDC > 0 ) {
                        add(new ATDCInfoEntryNode("Martial Base DC", "+ " + adjustedMartialDC, "Killing martial maneuvers count martial DCs as base damage with 2 Martial DC = +1 Base DC"));
                    }
                } else {
                    add(new ATDCInfoEntryNode("HA Base STR", "+ " + adjustedBaseStrDC, String.format("Normal maneuvers count STR DCs as base damage.  %.0f STR used at +1 DC per %.1f STR.", adjustedBaseStrUsed, ppdcStr/strMultiplier)));
                    // determined by STR + Extra Martial DC
                    if ( adjustedMartialDC != null && adjustedMartialDC > 0 ) {
                        add(new ATDCInfoEntryNode("Martial DC", "+ " + adjustedMartialDC, "Normal martial maneuvers count martial DCs as base damage with 1 Martial DC = +1 Base DC."));
                    }
                }
            }
            
            add(new ATDCInfoEntryNode("Total Base DC", "= " + adjustedBaseDC, "Base DC of attack."));
            
            add(new ATDCInfoEntryNode(" ", "", ""));
            
            if ( bonusMiscDC != null && bonusMiscDC > 0 ) {
                // We don't know what this come from, so we will just add to total, limited by max
                add(new ATDCInfoEntryNode("Misc. DC Bonus", "+ " + bonusMiscDC, "Miscellaneous adjustment to attack DC."));
            } else if ( bonusMiscDC != null && bonusMiscDC < 0 ) {
                // We don't know what this come from, so we are just going to penalize the total dc
                add(new ATDCInfoEntryNode("Misc. DC Penalty", Double.toString(bonusMiscDC), "Miscellaneous adjustment to attack DC."));
            }
            
            if ( bonusCombatDC != null && bonusCombatDC > 0 && isHeroic) {
                // Heroic 2 CSL = 1 DC
                add(new ATDCInfoEntryNode("CLS DC Bonus", "+ " + bonusMiscDC, "Heroic Combat level provide DC bonus.  2 CSL = +1 DC."));
            } else if ( isKilling && bonusBody != null && bonusBody > 0 ) {
                add(new ATDCInfoEntryNode("CLS DC Bonus", "+ " + bonusBody + " Body", "Superheroic Combat levels provide damage.  1 CSL = +1 Body."));
            } else if ( bonusStun != null && bonusStun > 0 ) {
                add(new ATDCInfoEntryNode("CLS DC Bonus", "+ " + bonusStun + " Stun", "Superheroic Combat levels provide damage.  1 CSL = +3 Stun."));
            }
            
            if ( bonusManeuverDC != null && bonusManeuverDC > 0 ) {
                if ( isKilling ) {
                    add(new ATDCInfoEntryNode("Manuever DC Bonus", "+ " + bonusManeuverDC, "Killing maneuver DC bonus.  2 Manuever DCs = +1 DC."));
                } else {
                    add(new ATDCInfoEntryNode("Manuever DC Bonus", "+ " + bonusManeuverDC, "Normal maneuver DC bonus.  1 Manuever DCs = +1 DC."));
                }
            }
            
            if ( bonusMartialDC != null && bonusMartialDC > 0 ) {
                if ( isKilling ) {
                    add(new ATDCInfoEntryNode("Martial DC Bonus", "+" + bonusMartialDC, "Killing martial DC bonus.  2 Martial DCs = +1 DC"));
                } else {
                    add(new ATDCInfoEntryNode("Martial DC Bonus", "+" + bonusMartialDC, "Normal martial DC bonus.  1 Martial DCs = +1 DC"));
                }
            }
            
            if ( isKilling && bonusMovementDC != null && bonusMovementDC > 0 ) {
                // Killing +1 DC per movement DC, can not exceed max
                // Add this prior to strength...no need to waste end on str when you
                // already have the DCs from movement...
                add(new ATDCInfoEntryNode("Movement DC Bonus", "+ " + bonusMovementDC, "Killing DC bonus for movement, adjusted by advantages on ability."));
            }
            
            if ( bonusStrDC != null && bonusStrDC > 0  ) {
                int a = bonusStrUsed == null ? 0 : bonusStrUsed;
                int b = pushedStrUsed == null ? 0 : pushedStrUsed;
                add(new ATDCInfoEntryNode("STR DC Bonus", "+ " + bonusStrDC, String.format("DC bonus for STR (normal not counted as base or minimum req. and pushed).  %d STR (%d normal, %d pushed) used at +1 DC per %.1f STR.", a+b, a, b, ppdcStr/strMultiplier)));
            }
            
//            if ( bonusPushedDC != null && bonusPushedDC > 0  ) {
//                add(new ATDCInfoEntryNode("Pushed STR DC Bonus", "+ " + bonusPushedDC, String.format("DC bonus for pushed STR.  %d STR used at +1 DC per %.1f STR.", bonusPushedUsed, ppdcStr/strMultiplier)));
//            }
            
            if ( isHeroic || isKilling ) {
                // In all combinations of killing and heroic,
                // maximum DC is 2 x AdjustedBase.  However,
                // movement powers can exceed this limit.
                double maximumDC = adjustedBaseDC * 2;
                if ( totalDC >= maximumDC ) {
                    if ( isKilling ) {
                        add(new ATDCInfoEntryNode("Maximum DC", "= " + adjustedBaseDC * 2, "Maximum attack DC.  Killing attack may do 2x Base maximum damage."));
                    add(new ATDCInfoEntryNode(" ", "", ""));
                    }
                    else {
                        add(new ATDCInfoEntryNode("Maximum DC", "= " + adjustedBaseDC * 2, "Maximum attack DC.  Normal heroic attack may do 2x Base maximum damage (movement DC can exceed this)."));
                    add(new ATDCInfoEntryNode(" ", "", ""));
                    }
                }
            } 
            
            if ( isKilling == false && bonusMovementDC != null && bonusMovementDC > 0 ) {
                // Normal +1 DC per movement DC, can exceed max
                // When it is a normal attack, add movement bonuses after strength, since
                // strength only applies pre-limit but movement can exceed the limit
                add(new ATDCInfoEntryNode("Movement DC Bonus", "+ " + bonusMovementDC, "Normal DC bonus for movement, adjusted by advantages on ability."));
            }
            
            
            if ( isKilling ) {
                String killingDice = ChampionsUtilities.DCToKillingString((int)totalDC.doubleValue());
                add(new ATDCInfoEntryNode("Final DC", "= " + totalDC, "Final damage class of attack.  Killing attack damage dice is " + killingDice + "."));
            
            }
            else {
                String normalDice = ChampionsUtilities.DCToNormalString(totalDC);
                add(new ATDCInfoEntryNode("Final DC", "= " + totalDC, "Final damage class of attack.  Normal attack damage dice is " + normalDice + "."));

            }
                
            if ( model instanceof DefaultTreeTableModel ) {
                ((DefaultTreeTableModel)model).nodeStructureChanged(this);
            }
        }
    }
    
    public void setBattleEvent(BattleEvent battleEvent) {
            this.battleEvent = battleEvent;
            
            buildNode();
    }
    
}
