/*
 * ChampionsConstants.java
 *
 * Created on June 14, 2001, 2:23 PM
 */

package champions.interfaces;

import champions.Ability;
import champions.BattleEvent;
import champions.Target;
import champions.exception.BattleEventException;

/**
 *
 * @author  twalker
 * @version
 */
public interface ChampionsConstants {
    public static final String VISIBLE = "TRUE";
    public static final String HIDDEN = "FALSE";
    public static final String ENABLED = "TRUE";
    public static final String DISABLED = "FALSE";
    public static final String REQUIRED = "TRUE";
    public static final String NOTREQUIRED = "FALSE";
    
    public static final String COMBO_PARAMETER = "COMBO"; // Requires additional Object[] Options parameter
    public static final String BOOLEAN_PARAMETER = "BOOLEAN";
    public static final String STRING_PARAMETER = "STRING";
    public static final String INTEGER_PARAMETER = "INTEGER";
    public static final String DOUBLE_PARAMETER = "DOUBLE"; // Requires additional Double Increment parameter
    public static final String DICE_PARAMETER = "DICE";
    public static final String LIST_PARAMETER = "LIST";
    public static final String MUTABLE_LIST_PARAMETER = "MUTABLELIST";
    public static final String FILE_PARAMETER = "FILE";
    
    // Cost Calculation and Reconfiguration constants.
    public static final String NORMAL_DICE_COST = "NORMAL_DICE_COST";
    public static final String KILLING_DICE_COST = "KILLING_DICE_COST";
    public static final String GEOMETRIC_COST = "GEOMETRIC_COST";
    public static final String LOGRITHMIC_COST = "LOGRITHMIC_COST";
    public static final String LIST_COST = "LIST_COST";
    public static final String BOOLEAN_COST = "BOOLEAN_COST";
    public static final String COMBO_COST = "COMBO_COST";
    public static final String BASE_COST = "BASE_COST";
    public static final String TOTALMULTIPLIER_COST = "TOTALMULTIPLIER_COST";
    
    public static final String DYNAMIC_RECONFIG = "DYNAMIC_RECONFIG";
    public static final String STATIC_RECONFIG = "STATIC_RECONFIG";
    
    public static final Integer PROPORTIONAL_RECONFIG = new Integer(-1);
    public static final Integer ZERO_RECONFIG = new Integer(0);
    public static final Integer ALL_RECONFIG = new Integer(100);
    
    // Different Kinds of Subeffect Types
    public static final String DAMAGE = "DAMAGE";
    public static final String HEAL = "HEAL";
    public static final String INCREASE = "INCREASE";
    public static final String DECREASE = "DECREASE";
    public static final String AID = "AID";
    public static final String DRAIN = "DRAIN";
    public static final String SET = "SET";
    
    // Constants to specify limits on subeffects
    public static final double UNLIMITED = Integer.MIN_VALUE; // Unconstrained.
    public static final double TARGET_LIMITED = Integer.MAX_VALUE; // Limited by the minimum/maximum designated by the target.
    public static final double BASE_VALUE = Integer.MAX_VALUE - 1; // Refers to the Adjusted Base in this case
    
    // Constants specifying the Affect Type for Stat changes
    public static final String BASE_STAT = "BASE STAT";
    public static final String ADJUSTED_STAT = "ADJUSTED STAT";
    public static final String CURRENT_STAT = "CURRENT STAT";
    //   public static final String ADJUSTED_AND_CURRENT_STAT = "ADJUSTED&CURRENT";
    
    // Constants for Ability Trees
    public static final int CRITICAL_STATUS = 0;
    public static final int ERROR_STATUS = 1;
    public static final int QUESTION_STATUS = 2;
    
    public static final int CHILD_CRITICAL_STATUS = 5;
    public static final int CHILD_ERROR_STATUS = 6;
    public static final int CHILD_QUESTION_STATUS = 7;
    public static final int OKAY_STATUS = 8;
    
    public static final int MSG_COMBAT = 1;
    public static final int MSG_DEBUG = 2;
    public static final int MSG_ABILITY = 3;
    public static final int MSG_UTILITY = 4;
    public static final int MSG_ROSTER = 5;
    public static final int MSG_SEGMENT = 6;
    public static final int MSG_NOTICE = 7;
    public static final int MSG_HIT=8;
    public static final int MSG_MISS=9;
    public static final int MSG_DICE=10;
    public static final int MSG_END=11;
    
    // Constants specifying ROLLMODE options for dice rolls
    public static final String MANUAL_ROLL = "MANUAL_ROLL";
    public static final String AUTO_ROLL = "AUTO_ROLL";
    
    public static final String USEDICE = "USEDICEROLL";
    public static final String FORCEHIT = "FORCEHIT";
    public static final String FORCEMISS = "FORCEMISS";
    public static final String OVERRIDE = "OVERRIDE";
    
    // Constats specifying the Type of Dice
    public static final String STUN_AND_BODY = "STUNANDBODY";
    public static final String STUN_ONLY = "STUNONLY";
    public static final String BODY_ONLY = "BODYONLY";
    
    // Targeting Type Constants
    public static final int NORMAL_TARGET = 1;
    public static final int KNOCKBACK_TARGET = 2;
    public static final int AE_TARGET = 3;
    public static final int AE_SELECTIVE_TARGET = 4;
    public static final int AE_NONSELECTIVE_TARGET = 5;
    public static final int SECONDARY_TARGET = 6;
    public static final int SKILL_TARGET = 7;
    public static final int AE_CENTER = 8;
    

    
    // Adjustment Model Types
    public static final int ADJ_CONFIG_DRAIN_FROM = 1;
    public static final int ADJ_CONFIG_AID_TO = 2;
    public static final int ADJ_CONFIG_TRANSFER_FROM = 3;
    public static final int ADJ_CONFIG_TRANSFER_TO = 4;
    public static final int ADJ_CONFIG_ABSORB_TO = 5;
    
    public static final int ADJUSTMENT_DRAINED = ADJ_CONFIG_DRAIN_FROM;
    public static final int ADJUSTMENT_AIDED = ADJ_CONFIG_AID_TO;
    public static final int ADJUSTMENT_ABSORBED = ADJ_CONFIG_ABSORB_TO;
    public static final int ADJUSTMENT_TRANSFERRED_FROM = ADJ_CONFIG_TRANSFER_FROM;
    public static final int ADJUSTMENT_TRANSFERRED_TO = ADJ_CONFIG_TRANSFER_TO;
    
    // Adjustment Levels
    public static final int ADJ_SINGLE_ADJUSTMENT = 1;
    public static final int ADJ_VARIABLE_1_ADJUSTMENT = 2;
    public static final int ADJ_VARIABLE_2_ADJUSTMENT = 3;
    public static final int ADJ_VARIABLE_4_ADJUSTMENT = 4;
    public static final int ADJ_VARIABLE_ALL_ADJUSTMENT = 5;
    
    
    // Ability Interrupt Levels
    public static final int NOT_INTERRUPTIBLE = 0;
    public static final int INTERRUPTIBLE_BY_DAMAGE = 1;
    public static final int INTERRUPTIBLE_BY_DAMAGE_AND_KNOCKBACK = 2;
    public static final int INTERRUPTIBLE_BY_ANY_DAMAGING_ATTACK = 3;
    
    // Standard Time Representation Constant
    // 5E Time Representation Constant
    public static final int TIME_ONE_SEGMENT = 0;
    public static final int TIME_ONE_PHASE = 1;
    public static final int TIME_ONE_TURN = 2;
    public static final int TIME_ONE_MINUTE = 3;
    public static final int TIME_FIVE_MINUTES = 4;
    public static final int TIME_TWENTY_MINUTES = 5;
    public static final int TIME_ONE_HOUR = 6;
    public static final int TIME_SIX_HOURS = 7;
    public static final int TIME_1_DAY = 8;
    public static final int TIME_1_WEEK = 9;
    public static final int TIME_1_MONTH = 10;
    public static final int TIME_1_SEASON = 11;
    public static final int TIME_1_YEAR = 12;
    public static final int TIME_5_YEARS = 13;
    public static final int TIME_25_YEARS = 14;
    
    public static final String[] TIME_CHART = { "Segment", 
                                                "Phase", 
                                                "Turn", 
                                                "Minute",
                                                "Five Minutes", 
                                                "Twenty Minutes", 
                                                "One Hour", 
                                                "Six Hours", 
                                                "One Day",
                                                "One Week", 
                                                "One Month", 
                                                "One Season", 
                                                "One Year", 
                                                "Five Years", 
                                                "25 Years" };
                                                
    public static final long[] TIME_CHART_SECONDS = { 1,
                                                    1,
                                                    12,
                                                    60,
                                                    300,
                                                    1200,
                                                    3600,
                                                    21600,
                                                    86400,
                                                    604800,
                                                    2629744,
                                                    7889231,
                                                    31556926,
                                                    157784630,  
                                                    788923149 };

   
      
                                                
    //integer too large
    //    public static final int TIME_1_CENTURY = 15;
    
    // Sequence Constants for Delayed Events...
    public static final int SEQUENCE_END_OF_SEGMENT = -1;
    public static final int SEQUENCE_BEFORE_TARGET = -2;
    
    // ActivationInfo State Constants
    public static final String AI_STATE_NEW = "NEW";
    public static final String AI_STATE_PREDELAY = "PREDELAY";
    public static final String AI_STATE_ABILITY_DELAYED = "ABILITY_DELAYED";
    public static final String AI_STATE_WAIT_FOR_ABILITY_TRIGGER = "ABILITY_TRIGGER";
    public static final String AI_STATE_ABILITY_TRIGGERED = "ABILITY_TRIGGERED";
    public static final String AI_STATE_PREACTIVATE_ABILITY = "PREACTIVATE_ABILITY";
    public static final String AI_STATE_MANEUVER_DELAYED = "MANEUVER_DELAYED";
    public static final String AI_STATE_MANEUVER_TRIGGERED = "MANEUVER_TRIGGERED";
    public static final String AI_STATE_PREACTIVATE_MANEUVER = "PREACTIVATE_MANEUVER";
    public static final String AI_STATE_ACTIVATION_FAILED = "ACTIVATION_FAILED";
    public static final String AI_STATE_ACTIVATED = "ACTIVATED";
    public static final String AI_STATE_CONTINUING = "CONTINUING";
    public static final String AI_STATE_DEACTIVATING = "DEACTIVATING";
    public static final String AI_STATE_DEACTIVATED = "DEACTIVATED";
    //public static final String AI_STATE_CANCELLED = "CANCELLED";
    
    // Special DistanceFromCollision Indicators
    public static final int MOVEMENT_FULL_MOVE = -1;
    public static final int MOVEMENT_HALF_MOVE = -2;
    
    // Constants Specifying types of CVLists
    public static final int CVLIST_OCVDCV = 1;
    public static final int CVLIST_ECV = 2;
    public static final int CVLIST_BLOCK = 3;

    // These are used by PAD Ability Node (in the ability editor) to specify
    // what should be added...
    public static final int POWER_MASK = 1;
    public static final int SKILL_MASK = 2;
    public static final int ADVANTAGE_MASK = 4;
    public static final int LIMITATION_MASK = 8;
    public static final int DISADVANTAGE_MASK = 16;
    public static final int SPECIAL_PARAMETERS_MASK = 32;
    public static final int SPECIAL_EFFECTS_MASK = 64;
    public static final int GENERIC_PRESETS_MASK = 128;
    public static final int GENERIC_RECENT_MASK = 256;
    public static final int FRAMEWORK_MASK = 512;
    public static final int TALENT_MASK = 1024;
    public static final int PERK_MASK = 2048;
    
    /** ALL_MASK includes all of the creation masks. */
    public static final int ALL_MASK = POWER_MASK | SKILL_MASK | ADVANTAGE_MASK | 
                                        LIMITATION_MASK | DISADVANTAGE_MASK |
                                        SPECIAL_PARAMETERS_MASK | SPECIAL_EFFECTS_MASK |
                                        FRAMEWORK_MASK | TALENT_MASK | PERK_MASK ;
    /** MODIFIERS_MASK includes only things that can modify an ability. */
    public static final int MODIFIERS_MASK = ADVANTAGE_MASK | LIMITATION_MASK |
                                        SPECIAL_PARAMETERS_MASK | SPECIAL_EFFECTS_MASK; 
    /** GENERIC_MASK includes all ability/power creation masks. */
    public static final int GENERIC_MASK = POWER_MASK | SKILL_MASK | 
                                        DISADVANTAGE_MASK | GENERIC_PRESETS_MASK | 
                                        GENERIC_RECENT_MASK | TALENT_MASK | PERK_MASK;
	}

