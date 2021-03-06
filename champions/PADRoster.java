/*
 * PADRoster.java
 *
 * Created on October 4, 2000, 12:36 AM
 */

package champions;

import champions.exceptionWizard.ExceptionWizard;
import champions.interfaces.AbilityList;
import champions.interfaces.Advantage;
import champions.interfaces.IOAdapter;
import champions.interfaces.Limitation;
import champions.interfaces.SpecialParameter;
import champions.powers.maneuverBlock;
import champions.powers.maneuverDodge;
import champions.powers.maneuverGrab;
import champions.powers.maneuverDisarm;
import champions.powers.maneuverHaymaker;
import champions.powers.maneuverMoveThrough;
import champions.powers.maneuverMoveby;
import champions.powers.maneuverProne;
import champions.powers.maneuverRapidFire;
import champions.powers.maneuverSet;
import champions.powers.maneuverStrike;
import champions.powers.maneuverSweep;
import champions.powers.powerDrawAWeapon;
import champions.powers.powerGenericAbility;
import champions.powers.powerHalfphase;
import champions.powers.powerHold;
import champions.powers.powerOffGround;
import champions.powers.powerPass;
import champions.powers.powerRecovery;
import champions.powers.powerUnstun;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import javax.swing.Icon;
import javax.swing.UIManager;


/**
 *
 * @author  unknown
 * @version
 */
public class PADRoster extends Object {
    private static String powerPackage = "champions.powers";
    
    
    /** List of power classes to be loaded by the Simulator.
     *
     * This is actually a list of all the powers and skills loaded by the simulator.
     * It does not include any of the templates.  Those are in the templateList.
     *
     * The first parameter is the name that the power will be listed as.  The second
     * parameter is the class of the power, located in the champions.powers package.
     * The third parameter is the "folder" the power will be located in.  The folder
     * name can be colon delimited to include subfolders. (i.e. "Skills:Martial Arts").
     */
    private static String[][] powerList = {
        {"Absorption","powerAbsorption", "Powers"},
        {"Aid","powerAid", "Powers"},
        {"Armor","powerArmor", "Powers"},
        {"Automaton", "powerAutomaton", "Powers"},
        {"Characteristic","powerCharacteristic", "Powers"},
        {"Change Environment","powerChangeEnvironment", "Powers"},
        {"Clinging","powerClinging", "Powers"},
        {"Damage Reduction","powerDamageReduction", "Powers"},
        {"Damage Resistance","powerDamageResistance", "Powers"},
        {"Darkness","powerDarkness", "Powers"},
        {"Density Increase","powerDensityIncrease", "Powers"},
        {"Dispel","powerDispel", "Powers"},
        {"Does Not Bleed","powerDoesNotBleed", "Powers"},
        {"Drain","powerDrain", "Powers"},
        {"Ego Attack", "powerEgoAttack", "Powers"},
        {"END Reserve","powerENDReserve", "Powers"},
        {"Energy Blast","powerEB", "Powers"},
        {"Enhanced Senses","powerEnhancedSenses", "Powers"},
        {"Entangle","powerEntangle", "Powers"},
        {"Extra-Dimensional Movement","powerExtraDimensionalMovement", "Powers"},
        {"Extra Limbs","powerExtraLimbs", "Powers"},
        {"Flash","powerFlash", "Powers"},
        {"Flash Defense","powerFlashDefense", "Powers"},
        {"Flight","powerFlight", "Powers"},
        {"Force Field","powerForceField", "Powers"},
        {"Force Wall","powerForceWall", "Powers"},
        {"Gliding","powerGliding", "Powers"},
        {"Growth","powerGrowth", "Powers"},
        {"Hand-to-Hand Killing","powerHKA", "Powers"},
        {"Hand-to-Hand Attack","powerHandToHandAttack", "Powers"},
        {"Healing","powerHeal", "Powers"},
        {"Hero ID", "powerIHID", "Powers"},
        {"Images","powerImages", "Powers"},
        {"Invisibility","powerInvisibility", "Powers"},
        {"Instant Change", "powerInstantChange", "Powers"},
        {"Knockback Resistance","powerKnockbackResistance", "Powers"},
        {"Life Support","powerLifeSupport", "Powers"},
        {"Mental Defense", "powerMentalDefense", "Powers"},
        {"Mental Illusions", "powerMentalIllusions", "Powers"},
        {"Mind Control", "powerMindControl", "Powers"},
        {"Mind Scan", "powerMindScan", "Powers"},
        {"Missile Deflection/Reflection", "powerMissileDeflection", "Powers"},
        {"Mental Defense", "powerMentalDefense", "Powers"},
        {"Mental Illusions", "powerMentalIllusions", "Powers"},
        {"No Hit Locations", "powerNoHitLocations", "Powers"},
        {"Power Defense", "powerPowerDefense", "Powers"},
        {"Ranged Killing Attack","powerRKA", "Powers"},
        {"Regeneration","powerRegeneration", "Powers"},
        {"Running","powerRunning", "Powers"},
        {"Shape Shift", "powerShapeShift", "Powers"},
        {"Shrinking", "powerShrinking", "Powers"},
        {"Leaping","powerLeaping", "Powers"},
        {"Luck","powerLuck", "Powers"},
        {"Swimming","powerSwimming", "Powers"},
        {"Unidentified", "powerUnidentified", "Powers"},
        //  {"Takes No Stun","powerTakesNoStun", "Powers"},
        {"Suppress","powerSuppress", "Powers"},
        {"Swinging","powerSwinging", "Powers"},
        {"Stretching","powerStretching", "Powers"},
        {"Telekinesis","powerTK", "Powers"},
        {"Teleportation","powerTeleport", "Powers"},
        {"Transfer","powerTransfer", "Powers"},
        {"Tunneling","powerTunneling", "Powers"},
        {"Acrobatics","skillAcrobatics", "Skills"},
        {"Analyze","skillAnalyze", "Skills"},
        //  {"Acting","skillActing", "Skills"},
        {"Breakfall","skillBreakfall", "Skills"},
        //  {"Bribery","skillBribery", "Skills"},
        //  {"Bugging","skillBugging", "Skills"},
        {"Combat Driving", "skillCombatDriving", "Skills"},
        {"Combat Piloting", "skillCombatPiloting", "Skills"},
        {"Climbing", "skillClimbing", "Skills"},
        //  {"Conversation", "skillConversation", "Skills"},
        {"Strike","maneuverStrike", "Skills"},
        {"Animal Handler","skillAnimalHandler", "Skills"},
        {"Combat Levels","powerCombatLevels", "Skills"},
        //     {"Concealment","skillConcealment", "Skills"},
        //    {"Contortionist","skillContortionist", "Skills"},
        {"Cramming","skillCramming", "Skills"},
        {"Defense Maneuver","skillDefenseManeuver", "Skills"},
        //     {"Deduction", "skillDeduction", "Skills"},
        //      {"Disguise","skillDisguise", "Skills"},
        //      {"Fast Draw","skillFastDraw", "Skills"},
        {"Forgery","skillForgery", "Skills"},
        {"Gambling","skillGambling", "Skills"},
        //   {"High Society", "skillHighSociety", "Skills"},
        {"Language", "skillLanguage", "Skills"},
        //     {"Lockpicking", "skillLockpicking", "Skills"},
        //     {"Oratory", "skillOratory", "Skills"},
        //    {"Shadowing","skillShadowing", "Skills"},
        //    {"Sleight of Hand","skillSleightofHand", "Skills"},
        {"Stealth","skillStealth", "Skills"},
        {"Block","maneuverBlock", "Skills"},
        {"Dodge","maneuverDodge", "Skills"},
        {"Escape","maneuverEscape", "Skills"},
        {"Skill Levels","skillSkillLevels", "Skills"},
        {"Power","skillPower", "Skills"},
        {"Haymaker","maneuverHaymaker", "Skills"},
        {"Grab","maneuverGrab", "Skills"},
        {"Disarm","maneuverDisarm", "Skills"},
        {"Knowledge Skill", "skillKnowledgeSkill", "Skills"},
        {"Move-Through","maneuverMoveThrough", "Skills"},
        {"Martial Damage Class","powerMartialDC", "Skills"},
        {"Throw","maneuverThrow", "Skills"},
        {"Move-By","maneuverMoveby", "Skills"},
        {"Normal Strike","maneuverStrike", "Skills"},
        {"Killing Strike","maneuverKillingStrike", "Skills"},
//        {"Generic Maneuver","maneuverGeneric", "Skills:Martial Arts"},
        {"Accurate Sprayfire", "skillAccurateSprayfire", "Skills:Autofire"},
        {"Concentrated Sprayfire", "skillConcentratedSprayfire", "Skills:Autofire"},
        {"Rapid Autofire", "skillRapidAutofire", "Skills:Autofire"},
        {"Skipover Sprayfire", "skillSkipoverSprayfire", "Skills:Autofire"},
        {"Accidental Change", "disadvantageAccidentalChange", "Disadvantages"},
        {"Age", "disadvantageAge", "Disadvantages"},
        {"Dependence", "disadvantageDependence", "Disadvantages"},
        {"Dependent NPC", "disadvantageDependentNPC", "Disadvantages"},
        {"Distinctive Features", "disadvantageDistinctiveFeatures", "Disadvantages"},
        {"Vulnerability", "disadvantageVulnerability", "Disadvantages"},
        {"Enraged/Berserk", "disadvantageEnragedBerserk", "Disadvantages"},
        {"Hunted", "disadvantageHunted", "Disadvantages"},
        {"Normal Characteristic Maxima", "disadvantageNormalCharacteristicMaxima", "Disadvantages"},
        {"Physical Limitation", "disadvantagePhysicalLimitation", "Disadvantages"},
        {"Psychological Limitation", "disadvantagePsychologicalLimitation", "Disadvantages"},
        {"Reputation", "disadvantageReputation", "Disadvantages"},
        {"Rivalry", "disadvantageRivalry", "Disadvantages"},
        {"Social Limitation", "disadvantageSocialLimitation", "Disadvantages"},
        {"Susceptibility", "disadvantageSusceptibility", "Disadvantages"},
        {"Unluck", "disadvantageUnluck", "Disadvantages"},
        {"Professional Skill", "skillProfessionalSkill", "Skills" },
        {"Science Skill", "skillScienceSkill", "Skills" },
        {"Rapid Attack (HTH)", "skillRapidAttackHTH", "Skills" },
        {"Rapid Attack (Ranged)", "skillRapidAttackRanged", "Skills" },
        {"Transport Familiarity", "skillTransportFamiliarity", "Skills" },
        {"Two-Weapon Fighting (HTH)", "skillTwoWeaponFightingHTH", "Skills" },
        {"Two-Weapon Fighting (Ranged)", "skillTwoWeaponFightingRanged", "Skills" },
        {"Standard Char-based Skill","skillStandardCharBased", "Skills"}, // Keep this down here so that more specific skill get imported first
        {"Weapon Familiarity", "skillWeaponFamiliarity", "Skills" },
        
        {"Absolute Range Sense", "TalentAbsoluteRangeSense", "Talents"},
        {"Absolute Time Sense", "TalentAbsoluteTimeSense", "Talents"},
        {"Bump Of Direction", "TalentBumpOfDirection", "Talents"},
        {"Eidetic Memory", "TalentEideticMemory", "Talents"},
        {"Universal Translator", "TalentUniversalTranslator", "Talents"},
        
        {"Access", "PerkAccess", "Perks"},
        {"Advanced Tech", "PerkAdvancedTech", "Perks"},
        {"Anonymity", "PerkAnonymity", "Perks"},
        {"Computer Link", "PerkComputerLink", "Perks"},
        {"Contact", "PerkContact", "Perks"},
        {"Custom Perk", "PerkCustomPerk", "Perks"},
        {"Deep Cover", "PerkDeepCover", "Perks"},
        {"False Identity", "PerkFalseIdentity", "Perks"},
        {"Favor", "PerkFavor", "Perks"},
    };
    
    /** List of templates loaded by the Simulator.
     *
     * This is a list of all the powers/skills templates loaded by the simulator.
     * Templates are actually just saved abilities.
     *
     * The first parameter is the name that the template will be listed as.  The second
     * parameter is the name of the saved ability, located in the champions.templates package.
     * The third parameter is the "folder" the loaded ability will be located in.  The folder
     * name can be colon delimited to include subfolders. (i.e. "Skills:Martial Arts").
     */
    private static String[][] templateList = {
        {"Acting", "Acting.abt", "Skills"},
        {"Bribery","Bribery.abt", "Skills"},
        {"Bugging","Bugging.abt", "Skills"},
        {"Bureaucratics","Bureaucratics.abt", "Skills"},
//        {"Combat Driving","CombatDriving.abt", "Skills"},
//        {"Combat Piloting","CombatPiloting.abt", "Skills"},
        {"Computer Programming","ComputerProgramming.abt", "Skills"},
        {"Concealment","Concealment.abt", "Skills"},
        {"Conversation","Conversation.abt", "Skills"},
        {"Contortionist","Contortionist.abt", "Skills"},
        {"Criminalogy","Criminalogy.abt", "Skills"},
        {"Cryptography","Cryptography.abt", "Skills"},
        {"Deduction","Deduction.abt", "Skills"},
        {"Demolitions","Demolitions.abt", "Skills"},
        {"Disguise","Disguise.abt", "Skills"},
        {"Electronics","Electronics.abt", "Skills"},
        {"Fast Draw","FastDraw.abt", "Skills"},
        {"High Society","HighSociety.abt", "Skills"},
        {"Choke Hold","ChokeHold.abt", "Skills:Martial Arts"},
        {"Defensive Strike","DefensiveStrike.abt", "Skills:Martial Arts"},
        {"Forensic Medicine","ForensicMedicine.abt", "Skills"},
        {"Lockpicking","Lockpicking.abt", "Skills"},
        {"Oratory","Oratory.abt", "Skills"},
        {"Paramedics", "Paramedics.abt", "Skills"},
        {"Streetwise","Streetwise.abt", "Skills"},
        {"Shadowing","Shadowing.abt", "Skills"},
        {"Slight Of Hand","SlightOfHand.abt", "Skills"},
        {"Systems Operations","SystemsOperation.abt", "Skills"},
        {"Tactics","Tactics.abt", "Skills"},
        
        {"Legsweep","Legsweep.abt", "Skills:Martial Arts"},
        {"Counterstrike", "Counterstrike.abt", "Skills:Martial Arts"},
        {"Choke Hold", "Choke Hold.abt", "Skills:Martial Arts"},
      //  {"Crush", "Crush.abt", "Skills:Martial Arts"},
        {"Defensive Block", "Defensive Block.abt", "Skills:Martial Arts"},
        {"Defensive Strike", "Defensive Strike.abt", "Skills:Martial Arts"},
        
        {"Disarming Throw", "Disarming Throw.abt", "Skills:Martial Arts"},
        {"Fast Strike", "Fast Strike.abt", "Skills:Martial Arts"},
        {"Flying Dodge", "Flying Dodge.abt", "Skills:Martial Arts"},
        
        {"Flying Grab", "Flying Grab.abt", "Skills:Martial Arts"},
       // {"Flying Tackle", "Counterstrike.abt", "Skills:Martial Arts"},
        {"Grappling Block", "Grappling Block.abt", "Skills:Martial Arts"},
        {"Grappling Throw", "Grappling Throw", "Skills:Martial Arts"},
        
        
        //{"Joint Lock/Throw", "MartialThrow.abt", "Skills:Martial Arts"},
        {"Killing Strike", "KillingStrike.abt", "Skills:Martial Arts"},
        //{"Killing Throw", "MartialThrow.abt", "Skills:Martial Arts"},
        {"Martial Flash", "Martial Flash.abt", "Skills:Martial Arts"},
       // {"Passing Strike", "MartialStrike.abt", "Skills:Martial Arts"},
        {"Sacrifice Throw", "SacrificeThrow.abt", "Skills:Martial Arts"},
       // {"Passing Throw", "MartialThrow.abt", "Skills:Martial Arts"},
        
        {"Reversal", "Reversal.abt", "Skills:Martial Arts"},
        {"Root", "Root.abt", "Skills:Martial Arts"},
        {"Sacrifice Disarm", "SacrificeDisarm.abt", "Skills:Martial Arts"},
       // { "Joint Break", "Counterstrike.abt", "Skills:Martial Arts"},
        
      //  {"Sacrifice Lunge", "SacrificeLunge.abt", "Skills:Martial Arts"},
        {"Shove", "Shove.abt", "Skills:Martial Arts"},
        {"Takeaway", "Takeaway.abt", "Skills:Martial Arts"},
        {"Takedown", "Takedown.abt", "Skills:Martial Arts"},
        {"Weapon Bind", "Weapon Bind.abt", "Skills:Martial Arts"},
        
        {"Killing Strike", "KillingStrike.abt", "Skills:Martial Arts"},
        {"Martial Block","MartialBlock.abt", "Skills:Martial Arts"},
        {"Martial Disarm","MartialDisarm.abt", "Skills:Martial Arts"},
        {"Martial Dodge","MartialDodge.abt", "Skills:Martial Arts"},
        {"Martial Escape","MartialEscape.abt", "Skills:Martial Arts"},
        {"Martial Grab","MartialGrab.abt", "Skills:Martial Arts"},
        {"Martial Strike","MartialStrike.abt", "Skills:Martial Arts"},
        {"Martial Throw","MartialThrow.abt", "Skills:Martial Arts"},
        {"Nerve Strike", "NerveStrike.abt", "Skills:Martial Arts"},
        {"Offensive Strike","OffensiveStrike.abt", "Skills:Martial Arts"},
        {"STR Roll","STRRoll.abt", null},
        {"STR Roll","STRRoll.abt", null},
        {"DEX Roll","DEXRoll.abt", null},
        {"CON Roll","CONRoll.abt", null},
        {"INT Roll","INTRoll.abt", null},
        {"PER Roll","PERRoll.abt", null},
        {"EGO Roll","EGORoll.abt", null},
        {"PRE Roll","PERRoll.abt", null},
    };
    
    
    private static Map powerNameToCanonical;
    private static Map powerClassToClass;
    
    /** List of advantages loaded by the Simulator.
     *
     * This is a list of all the advantages loaded by the simulator.
     *
     * The first parameter is the class name of the advantages located in the champions.templates package.
     * The second parameter is the "folder" the advantageswill be located in.  The folder
     * name can be colon delimited to include subfolders. (i.e. "Advantages:Adjustment Powers").
     */
    private static String[][] advantagesList = {
    	{"advantageMultipleSpecialEffects", "Advantages"},
    	{"advantageUsableUnderWater", "Advantages"},
    	{"advantageUsableAsFlight", "Advantages"},
    	{"advantageCannotPassThrough", "Advantages"},
    	{"advantageUsableAsSwinging", "Advantages"},
    	{"advantageUsableAsGliding", "Advantages"},
    	{"advantageNoTurnMode", "Advantages"},
    	{"advantageDecreasedReuse", "Advantages"},
    	{"advantageNonCombatAcceleration", "Advantages"},
    	{"advantageRapidNonCombatMovement", "Advantages"},
    	{"advantageUsableAsGliding", "Advantages"},
    	{"advantageOnlyInGiven", "Advantages"},
    	{"advantageCumulative", "Advantages"},
    	{"advantageUsableByOthers", "Advantages"},
    	{"advantageStopsAGivenSense", "Advantages"},
    	{"advantageEntangleAndCharacter", "Advantages"},
    	{"advantageCombatAcceleration", "Advantages"},
    	{"advantageDoesKnockback", "Advantages"},
        {"advantageAffectsDesolidified", "Advantages"},
        {"advantageAVLD", "Advantages"},
        {"advantageAreaEffect", "Advantages"},
        {"advantageArmorPiercing", "Advantages"},
        {"advantageBasedOnECV", "Advantages"},
        {"advantageOrganizationContact", "Advantages"},
        {"advantagePenetrating", "Advantages"},
        {"advantagePersonalImmunity", "Advantages"},
        {"advantageReducedEndurance", "Advantages"},
        {"advantageNND", "Advantages"},
        {"advantageNoRangePenalty", "Advantages"},
        {"advantageLineOfSight", "Advantages"},
        {"advantageAutofire", "Advantages"},
        {"advantageGenericAdvantage", "Advantages"},
        {"advantageDelayedEffect", "Advantages"},
        {"advantageDoubleKnockback", "Advantages"},
        {"advantageExplosion", "Advantages"},
        {"advantageFadeRate", "Advantages"},
        {"advantageHardened", "Advantages"},
        {"advantageHoleInTheMiddle", "Advantages"},
        {"advantageIncreasedStunMultiplier", "Advantages"},
        {"advantageIndirect", "Advantages"},
        {"advantageContinuous", "Advantages"},
        {"advantageRanged", "Advantages"},
        {"advantageTimeDelay", "Advantages"},
        {"advantageTrigger", "Advantages"},
        {"advantageCEVary", "Advantages"},
        {"advantagePowersCanBeChangedAsAHalfPhaseAction", "Advantages"},
        {"advantagePowersCanBeChangedAsAZeroPhaseAction", "Advantages"},
        {"advantageCosmic", "Advantages"},
        {"advantageNoSkillRollRequired", "Advantages"},
        {"advantageInvisiblePowerEffects", "Advantages"},
        {"advantageVariableEffect", "Advantages"},
        {"advantageVaryingEffect", "Advantages"},
        {"advantageSafeBlindTravel", "Advantages"},
        {"advantageSpiritContact", "Advantages"},
        {"advantageTakesNoDamageFromAttacks", "Advantages"},
        {"advantageTelepathic", "Advantages"},
        {"advantagePersistent", "Advantages"},
        {"advantageInherent", "Advantages"},
        {"advantageCostsENDOnlyToActivate", "Advantages"},
        {"advantageWorksAgainstEgoNotStr", "Advantages"},
        {"advantageVariableAdvantage", "Advantages"},
        {"advantageVariableSpecialEffect", "Advantages"},
    };
    
    /** List of limitations loaded by the Simulator.
     *
     * This is a list of all the limitations loaded by the simulator.
     *
     * The first parameter is the class name of the limitations located in the champions.templates package.
     * The second parameter is the "folder" the limitations will be located in.  The folder
     * name can be colon delimited to include subfolders. (i.e. "Limitations:Adjustment Powers").
     */
    private static String[][] limitationsList = {
    	{"limitationVPPOnlyBetweenAdventures", "Limitations"},
    	{"limitationNoConsiousControl", "Limitations"},
    	{"limitationNoVelocityDamage", "Limitations"},
    	{"limitationMustPassThroughSpace", "Limitations"},
    	{"limitationLevitation", "Limitations"},
    	{"limitationRealWeapon", "Limitations"},
    	{"limitationRealArmour", "Limitations"},
    	{"limitationSummonedBeingInhabit", "Limitations"},
    	{"limitationArrivesUnderOwn", "Limitations"},
    	{"limitationForwardMovement", "Limitations"},
    	{"limitationAllOrNothing", "Limitations"},
    	
    	{"limitationCannotPassThroughSolid", "Limitations"},
    	{"limitationDoesNotProtectAgainst", "Limitations"},
    	//{"limitationSetffect", "Limitations"},
    	{"limitationAffectsWholeObject", "Limitations"},	
    	{"limitationOnlyInContact", "Limitations"},
    	{"limitationLockout", "Limitations"},	
    	{"limitationSenseAffectedAs", "Limitations"},	
    	{"limitationPercievable", "Limitations"},	
    	{"limitationRangeBasedOnSTR", "Limitations"},	
    	{"limitationAlwaysDirect", "Limitations"},	
    	{"limitationUnifiedPower", "Limitations"},	
    	{"limitationExtraTimeRegen", "Limitations"},	
    	{"limitationInstant", "Limitations"},	
    	{"limitationNoNonCombat", "Limitations"},
    	{"limitationVulnerable", "Limitations"},
    	{"limitationLimitedRange", "Limitations"},
        {"limitationActivation", "Limitations"},
        {"limitationAffectsBodyOnly", "Limitations"},
        {"limitationBeam", "Limitations"},
        {"limitationBrightFringe", "Limitations"},
        {"limitationCanBeMissileDeflected", "Limitations"},
        {"limitationCannotFormBarriers", "Limitations"},
        {"limitationChameleon", "Limitations"},
        {"limitationCharges", "Limitations"},
        {"limitationConcentration", "Limitations"},
        {"limitationLimitedClassOfPowersAvailable", "Limitations"},
        {"limitationNoFiguredCharacteristics", "Limitations"},
        {"limitationEDApplies", "Limitations"},
        {"limitationExtraTime", "Limitations"},
        {"limitationFillInAlwaysOccurs", "Limitations"},
        {"limitationFocus", "Limitations"},
        {"limitationGenericLimitation", "Limitations"},
        {"limitationGestures", "Limitations"},
        {"limitationGroundGliding", "Limitations"},
        {"limitationHandToHand", "Limitations"},
        {"limitationIncantation", "Limitations"},
        {"limitationIncreasedEndurance", "Limitations"},
        {"limitationLimitedEffect", "Limitations"},
        {"limitationLimitedSpecialEffect", "Limitations"},
        {"limitationLinked", "Limitations"},
        {"limitationNoKnockback", "Limitations"},
        {"limitationNonpersistent", "Limitations"},
        {"limitationNoRange", "Limitations"},
        {"limitationOnlyWhenNotAttacking", "Limitations"},
        {"limitationOthersOnly", "Limitations"},
        {"limitationOnlyinHeroID", "Limitations"},
        {"limitationOnlyUpToAmountAbsorbed", "Limitations"},
        {"limitationPDApplies", "Limitations"},
        {"limitationLimitedPhenomena", "Limitations"},
        {"limitationReducedPenetration", "Limitations"},
        {"limitationRestrainable", "Limitations"},
        {"limitationSelfOnly", "Limitations"},
        {"limitationSkillRoll", "Limitations"},
        {"limitationCostsEND", "Limitations"},
        {"limitationLimitedMedium", "Limitations"},
        {"limitationSetEffect", "Limitations"},
        {"limitationSTRMinimum", "Limitations"},
        {"limitationLimitedBySenses", "Limitations"},
        {"limitationBasedOnCON", "Limitations"},
        {"limitationEyeContactRequired", "Limitations"},
        {"limitationNormalRange", "Limitations"},
        {"limitationCannotBeUsedThroughMindLink", "Limitations"},
        {"limitationDoesNotProvideMentalAwareness", "Limitations"},
        {"limitationMandatoryEffect", "Limitations"},
        {"limitationSkinContactRequired", "Limitations"},
        {"limitationResurrectionOnly", "Limitations"},
        {"limitationLimitedPower", "Limitations"},
        {"limitationConditionalPower", "Limitations"},
        {"limitationAlwaysOn", "Limitations"},
        {"limitationNotAgainstHeavyMissiles", "Limitations"}, };
    
    
    
    /** List of special effects loaded by the Simulator.
     *
     * This is a list of all the special effects loaded by the simulator.
     *
     * The first parameter is the class name of the limitations located in the champions.templates package.
     * The second parameter is the icon key for the SFX, as stored by the UIManager.
     * The thrid parameter is the "folder" the special effects will be located in.  The folder
     * name can be colon delimited to include subfolders. (i.e. "Special Effects:Special Effects with Crappy Icons").
     */
    private static String[][] specialEffectsList = {
        { "Angelic", "SpecialEffect.MagicIcon", "Special Effects"},
        { "Fire", null, "Special Effects"},
        { "Ice", null, "Special Effects"},
        { "Magic", "SpecialEffect.MagicIcon", "Special Effects"},
        { "Heat", null, "Special Effects"},
        { "Cold", "SpecialEffect.Cold1Icon", "Special Effects"},
        { "Chi", "SpecialEffect.ChiIcon", "Special Effects"},
        { "Electricity", "SpecialEffect.Electrical1Icon", "Special Effects"},
        { "Visible Light", "SpecialEffect.VisibleLightIcon", "Special Effects"},
        { "Gamma Radiation", "SpecialEffect.Electrical1Icon", "Special Effects"},
        { "Microwave Radiation", "SpecialEffect.Electrical1Icon", "Special Effects"},
        { "X-Ray Radiation", "SpecialEffect.Electrical1Icon", "Special Effects"},
        { "Ultraviolet Radiation", "SpecialEffect.Electrical1Icon", "Special Effects"},
        { "Luck 1", "SpecialEffect.Luck1Icon", "Special Effects"},
        { "Magnetism", "SpecialEffect.MagnetismIcon", "Special Effects"},
        { "Water", "SpecialEffect.WaterIcon", "Special Effects"},
        { "Wind", null, "Special Effects"},
        { "Air", "SpecialEffect.Air1Icon", "Special Effects"},
        {"Air 2", "SpecialEffect.Air2Icon", "Special Effects"},
        { "Cold 2", "SpecialEffect.Cold2Icon", "Special Effects"},
        { "Earth","SpecialEffect.EarthIcon", "Special Effects"},
        {"Electrical 2", "SpecialEffect.Electrical2Icon", "Special Effects"},
        {"Luck 2","SpecialEffect.Luck2Icon", "Special Effects"},
        {"Luck 3","SpecialEffect.Luck3Icon", "Special Effects"},
        {"Mental Powers","SpecialEffect.PsionicIcon", "Special Effects"},
        {"Mutant","SpecialEffect.MutantIcon", "Special Effects"},
        {"Poison","SpecialEffect.PoisonIcon", "Special Effects"},
        {"Psionic","SpecialEffect.PsionicIcon", "Special Effects"},
        {"Sonic","SpecialEffect.SonicIcon", "Special Effects"},
        {"Technology","SpecialEffect.Tech1Icon", "Special Effects"},
        {"Technology 2","SpecialEffect.Tech2Icon", "Special Effects"},
        {"Technology 3","SpecialEffect.Tech3Icon", "Special Effects"},
        {"Technology 4","SpecialEffect.Tech4Icon", "Special Effects"},
        {"Temporal","SpecialEffect.TemporalIcon", "Special Effects" },
        {"Iron","SpecialEffect.TemporalIcon", "Special Effects" },
        {"Holy Water","SpecialEffect.TemporalIcon", "Special Effects" },
    };
    
    /** List of special parameter loaded by the Simulator.
     *
     * This is a list of all the special parameter loaded by the simulator.
     *
     * The first parameter is the class name of the special parameter located in the champions.templates package.
     * The second parameter is the "folder" the special parameter will be located in.  The folder
     * name can be colon delimited to include subfolders. (i.e. "Special Parameter:").
     */
    private static String[][] specialParametersList = {
        {"SpecialParameterAlwaysTargetSelf", "Special Parameters"},
        {"SpecialParameterDCVModifier", "Special Parameters"},
        {"SpecialParameterENDSource", "Special Parameters"},
        {"SpecialParameterFixedCost", "Special Parameters"},
        {"SpecialParameterNormallyOn", "Special Parameters"},
        {"SpecialParameterOCVModifier", "Special Parameters"},
        {"SpecialParameterIsMartialManeuver", "Special Parameters"},
        {"SpecialParameterRollBasedSurpriseBonus", "Special Parameters"},
        {"SpecialParameterIsSkill", "Special Parameters"},
        //{"SpecialParameterSTRMinimum", "Special Parameters"},
        {"SpecialParameterDisallowForcedActivation", "Special Parameters"},
        {"SpecialParameterWeapon", "Special Parameters"},
    };
    
    /** List of all standard sense names and groups.
     *
     * This is just a list of the names.  Every item in this list should
     * also have an entry in the getNewSense method.
     */
    private static String[] sensesNameList = {
        "Hearing Group", //Only in Options menu in HD Invisibility
        "Mental Group",
        "Radio Group",
        "Sight Group",
        "Smell/Taste Group",
        "Touch Group",
        "Mystic Group",
        "Normal Hearing",
        "Normal Sight",
        "Normal Smell",
        "Normal Taste",
        "Normal Touch",
        "Danger Sense",
        "Combat Sense",
        "Mind Scan",
        "Active Sonar",
        "Detect",
        "High Range Radio Perception",
        "Danger Sense",
        "Infrared Perception",
        "Mental Awareness",
        "Nightvision",
        "N-Ray Perception",
        "Radar",
        "Radio Perception/Transmission",
        "Radio Perception",
        "Spatial Awareness",
        "Ultrasonic Perception",
        "Ultraviolet Perception",
        //removed because they aren't listed in HD Invisibility.  Need to confirm why
        //"Tracking Scent",
        //"Microscopic Vision",
        //"360 Degree Sensing",
    };
    
    /** Maps sense names to share sense instances.
     *
     */
    private static ArrayList sensesList;
    
    /** Maps the PAD name to the Class file of the PAD object.
     *
     * This is keyed on the English name of the PAD, typically as returned by getName()
     * method.
     *
     * This map contains one entry for every single PAD entry.  In general, the Class
     * will be the specific class type necessary to create an entry.  However, for
     * all abilities will be listed as strictly the Ability class and all special effects
     * will be listed as just SpecialEffect class.
     */
    public static Map padClassMap;
    
    /** Maps the PAD name to the shared instance of the PAD.
     *
     * This is keyed on the English name of the PAD, typically as returned by getName()
     * method.
     *
     * Not all object will necessarily have an entry in this map.  As shared instances
     * are created, this map will be populated.
     */
    public static Map padSharedInstanceMap;
    
    /** Maps the Ability names to the AbilityTemplate objects.
     *
     * This map is keyed on the English name of the ability, as specified in the powerList or
     * templateList.
     *
     * This map contains one entry for every Power or Template loaded.  The entries are AbilityTemplate
     * objects, which can be used to generate either the shared instance or a new instance of the
     * ability.
     */
    private static Map abilityTemplateMap;
    
    /** The Root Folder containing the organization structure of the PADs.
     *
     */
    private static PADFolder rootFolder;
    
    /** The AbilityList containing the generic presets.
     *
     */
    private static AbilityList genericPresetList;
    
    /** The TargetList containing the generic objects.
     *
     */
    private static TargetList presetTargetsList;
    
    /** List of IOAdapters to load into the IOAdapter list.
     *
     */
    private static Class[] ioAdapterClasses = {
        champions.ioAdapter.heroDesigner.HeroDesignerIOAdapter.class
    };
    
    /** ArrayList holding instances of the loaded ioAdapters.
     *
     */
    private static List ioAdapterList;
    
    /** Indicates if the PADRoster has been initialized.
     *
     * The PADRoster should usually be initialize early in the Simulator startup.
     */
    private static boolean inited = false;
    
    /** Indicates the Location of the Templates in the Package structure.
     */
    private static String templateLocation = "/champions/templates/";
    
    static {
        // Static Initialize run when the class is loaded.
        // Hate to do this, but the PADRoster must be gauranteed to be
        // initialized
        if ( inited == false ) initialize();
    }
    
    /** Returns the shared Instance of the PAD object with the specified name.
     *
     * If the shared object does not exist, this will create the object.
     *
     * @param name Name of the PAD Object requested.
     * @return An object of the appropriate type. Null if the name is invalid.
     */
    public static Object getSharedPADInstance(String name) {
        Class c = (Class)padClassMap.get(name);
        
        if ( c != null ) {
            if ( c.equals(Ability.class) ) {
                return getSharedAbilityInstance(name);
            } else if ( Advantage.class.isAssignableFrom( c ) ) {
                return getSharedAdvantageInstance(name);
            } else if ( Limitation.class.isAssignableFrom( c ) ) {
                return getSharedLimitationInstance(name);
            } else if ( SpecialParameter.class.isAssignableFrom( c ) ) {
                return getSharedSpecialParameterInstance(name);
            } else if ( SpecialEffect.class.equals(c) ) {
                return getSharedSpecialEffectInstance(name);
            }
        }
        return null;
    }
    
    /** Returns a new Instance of the PAD object with the specified name.
     *
     * Not all PAD objects have unique new instances.  Specifically, Special Effects
     * will always return the shared instance instead of a new instance.
     *
     * @param name Name of the PAD Object requested.
     * @return An object of the appropriate type. Null if the name is invalid.
     */
    public static Object getNewPADInstance(String name) {
        Class c = (Class)padClassMap.get(name);
        
        if ( c != null ) {
            if ( c.equals(Ability.class) ) {
                return getNewAbilityInstance(name);
            } else if ( Advantage.class.isAssignableFrom( c ) ) {
                return getNewAdvantageInstance(name);
            } else if ( Limitation.class.isAssignableFrom( c ) ) {
                return getNewLimitationInstance(name);
            } else if ( SpecialParameter.class.isAssignableFrom( c ) ) {
                return getNewSpecialParameterInstance(name);
            } else if ( SpecialEffect.class.equals(c) ) {
                return getNewSpecialEffectInstance(name);
            }
        }
        return null;
    }
    
    /** Returns the Shared instance of the Ability with the specified name.
     *
     * @param name Name of the Ability Object requested.
     * @return An object of the appropriate type. Null if the name is invalid.
     */
    public static Ability getSharedAbilityInstance(String name) {
        Class c = (Class)padClassMap.get(name);
        
        if ( c != null && Ability.class.equals( c ) ) {
            Ability sharedInstance = (Ability)padSharedInstanceMap.get(name);
            
            // Make sure there was a shared instance.  If it wasn't a template originally,
            // it probably doesn't have a shared instance either.
            if ( sharedInstance == null ) {
                AbilityTemplate at = (AbilityTemplate)abilityTemplateMap.get(name);
                sharedInstance = at.newAbilityInstance();
                padSharedInstanceMap.put(name, sharedInstance);
            }
            
            return sharedInstance;
        }
        return null;
    }
    
    /** Returns a new instance of the Ability with the specified name.
     *
     * @param name Name of the Ability Object requested.
     * @return An object of the appropriate type. Null if the name is invalid.
     */
    public static Ability getNewAbilityInstance(String name) {
        Class c = (Class)padClassMap.get(name);
        
        if ( c != null && Ability.class.equals( c ) ) {
            AbilityTemplate at = (AbilityTemplate)abilityTemplateMap.get(name);
            Ability newInstance = at.newAbilityInstance();
            
            return newInstance;
        }
        return null;
    }
    
    /** Returns the Shared instance of the Advantage with the specified name.
     *
     * @param name Name of the Advantage Object requested.
     * @return An object of the appropriate type. Null if the name is invalid.
     */
    public static Advantage getSharedAdvantageInstance(String name) {
        Class c = (Class)padClassMap.get(name);
        
        if ( c != null && Advantage.class.isAssignableFrom( c ) ) {
            // There is always a shared instance for advantages.
            Advantage sharedInstance = (Advantage)padSharedInstanceMap.get(name);
            return sharedInstance;
        }
        return null;
    }
    
    /** Returns a new instance of the Advantage with the specified name.
     *
     * @param name Name of the Advantage Object requested.
     * @return An object of the appropriate type. Null if the name is invalid.
     */
    public static Advantage getNewAdvantageInstance(String name) {
        Class c = (Class)padClassMap.get(name);
        
        if ( c != null && Advantage.class.isAssignableFrom( c ) ) {
            try {
                Advantage newInstance = (Advantage)c.newInstance();
                return newInstance;
            } catch ( Exception e ) {
                ExceptionWizard.postException(e);
            }
        }
        return null;
    }
    
    /** Returns the Shared instance of the Limitation with the specified name.
     *
     * @param name Name of the Limitation Object requested.
     * @return An object of the appropriate type. Null if the name is invalid.
     */
    public static Limitation getSharedLimitationInstance(String name) {
        Class c = (Class)padClassMap.get(name);
        
        if ( c != null && Limitation.class.isAssignableFrom( c ) ) {
            // There is always a shared instance for advantages.
            Limitation sharedInstance = (Limitation)padSharedInstanceMap.get(name);
            return sharedInstance;
        }
        return null;
    }
    
    /** Returns a new instance of the Limitation with the specified name.
     *
     * @param name Name of the Limitation Object requested.
     * @return An object of the appropriate type. Null if the name is invalid.
     */
    public static Limitation getNewLimitationInstance(String name) {
        Class c = (Class)padClassMap.get(name);
        
        
        if ( c != null && Limitation.class.isAssignableFrom( c ) ) {
            try {
                Limitation newInstance = (Limitation)c.newInstance();
                return newInstance;
            } catch ( Exception e ) {
                ExceptionWizard.postException(e);
            }
        }
        return null;
    }
    
    /** Returns the Shared instance of the Special parameter with the specified name.
     *
     * @param name Name of the Special parameter Object requested.
     * @return An object of the appropriate type. Null if the name is invalid.
     */
    public static SpecialParameter getSharedSpecialParameterInstance(String name) {
        Class c = (Class)padClassMap.get(name);
        
        if ( c != null && SpecialParameter.class.isAssignableFrom( c ) ) {
            // There is always a shared instance for advantages.
            SpecialParameter sharedInstance = (SpecialParameter)padSharedInstanceMap.get(name);
            return sharedInstance;
        }
        return null;
    }
    
    /** Returns a new instance of the SpecialParameter with the specified name.
     *
     * @param name Name of the SpecialParameter Object requested.
     * @return An object of the appropriate type. Null if the name is invalid.
     */
    public static SpecialParameter getNewSpecialParameterInstance(String name) {
        Class c = (Class)padClassMap.get(name);
        
        if ( c != null && SpecialParameter.class.isAssignableFrom( c ) ) {
            try {
                SpecialParameter newInstance = (SpecialParameter)c.newInstance();
                return newInstance;
            } catch ( Exception e ) {
                ExceptionWizard.postException(e);
            }
        }
        return null;
    }
    
    /** Returns the Shared instance of the Special Effect with the specified name.
     *
     * @param name Name of the Special Effect Object requested.
     * @return An object of the appropriate type. Null if the name is invalid.
     */
    public static SpecialEffect getSharedSpecialEffectInstance(String name) {
        Class c = (Class)padClassMap.get(name);
        
        if ( c != null && SpecialEffect.class.isAssignableFrom( c ) ) {
            // There is always a shared instance for advantages.
            SpecialEffect sharedInstance = (SpecialEffect)padSharedInstanceMap.get(name);
            return sharedInstance;
        }
        return null;
    }
    
    /** Returns a new instance of the Special Effect with the specified name.
     *
     * For SFX, getNewInstance returns the same instance as shared instance.
     *
     * @param name Name of the Special Effect Object requested.
     * @return An object of the appropriate type. Null if the name is invalid.
     */
    public static SpecialEffect getNewSpecialEffectInstance(String name) {
        Class c = (Class)padClassMap.get(name);
        
        if ( c != null && SpecialEffect.class.isAssignableFrom( c ) ) {
            // There is always a shared instance for advantages.
            SpecialEffect sharedInstance = (SpecialEffect)padSharedInstanceMap.get(name);
            return sharedInstance;
        }
        
        // It was null, so create a new GenericSpecialEffect with that name...
        return new SpecialEffect(name);
    }
    
    /** Returns an Iterator of the names of all the Abilities.
     *
     * The objects returned by the iterator will be the PAD Ability
     * names as strings.
     */
    public static Iterator getAbilityIterator() {
        return new PADClassIterator(Ability.class);
    }
    
    /** Returns an Iterator of the names of all the Advantages.
     *
     * The objects returned by the iterator will be the PAD Advantage
     * names as strings.
     */
    public static Iterator getAdvantageIterator() {
        return new PADClassIterator(Advantage.class);
    }
    
    /** Returns an Iterator of the names of all the Limitations.
     *
     * The objects returned by the iterator will be the PAD Limitation
     * names as strings.
     */
    public static Iterator getLimitationIterator() {
        return new PADClassIterator(Limitation.class);
    }
    
    /** Returns an Iterator of the names of all the Special Parameters.
     *
     * The objects returned by the iterator will be the PAD Special Parameter
     * names as strings.
     */
    public static Iterator getSpecialParameterIterator() {
        return new PADClassIterator(SpecialParameter.class);
    }
    
    /** Returns an Iterator of the names of all the Special Effects.
     *
     * The objects returned by the iterator will be the PAD Special Effect
     * names as strings.
     */
    public static Iterator getSpecialEffectIterator() {
        return new PADClassIterator(SpecialEffect.class);
    }
    
    /** Returns the Root Folder which contains the complete Structure of PADs.
     *
     */
    public static PADFolder getFolder() {
        return rootFolder;
    }
    
    /** Returns the Folder named name from the PAD structure.
     *
     */
    public static PADFolder getFolder(String folderName) {
        return rootFolder.findFolder(folderName);
    }
    
    /** Returns the Class for the specified PAD name.
     *
     */
    public static Class getPADClass(String padName) {
        return (Class)padClassMap.get(padName);
    }
    
    public static boolean isInitialized() {
        return inited;
    }
    
    public static void initialize() {
        setup();
    }
    
    /** Sets up all the PADRoster statics.
     */
    public static void setup() {
        if ( inited ) return;
        
        inited = true;
        
        padClassMap = new LinkedHashMap();
        padSharedInstanceMap = new LinkedHashMap();
        abilityTemplateMap = new LinkedHashMap();
        
        rootFolder = new PADFolder("");
        
        SplashScreen.setModule("Initializing Powers");
        
        String name, power, folder, templateName, iconKey;
        AbilityTemplate at = null;
        
        for (int i=0;i < powerList.length;i++) {
            name = powerList[i][0];
            power = powerList[i][1];
            folder = powerList[i][2];
            
            SplashScreen.setDescription("Initializing Powers(" + name + ")");
            
            
            
            try {
                // Create an Ability Template and store it in the abilityTemplateMap.
                at = new AbilityTemplate(name,power,null);
                abilityTemplateMap.put(name, at);
                
                // Initialize the Power of the Ability which was just created.
                at.getPower().initialize();
                
                // Add the PAD to the folder structure
                addPADtoFolder(name, folder);
                
                // Mark the Class as Ability.
                padClassMap.put(name, Ability.class);
            } catch(Exception e) {
                ExceptionWizard.postException(e);
            }
            
            SplashScreen.advangeProgress();
        }
        
        SplashScreen.setModule("Initializing Templates");
        
        for (int i=0;i < templateList.length;i++) {
            name = templateList[i][0];
            templateName = templateList[i][1];
            folder = templateList[i][2];
            
            SplashScreen.setDescription("Initializing Templates(" + name + ")");
            
            
            
            try {
                // Create an Ability Template and store it in the abilityTemplateMap.
                at = new AbilityTemplate(name, null, templateName);
                if ( at != null && at.getPower() != null) {
                    abilityTemplateMap.put(name, at);
                    padSharedInstanceMap.put(name, at.getTemplateInstance());
                    
                    // Initialize the Power of the Ability which was just created.
                    at.getPower().initialize();
                    
                    // Add the PAD to the folder structure
                    addPADtoFolder(name, folder);
                    
                    
                    // Mark the Class as Ability.
                    padClassMap.put(name, Ability.class);
                }
            } catch(Exception e) {
                ExceptionWizard.postException(e);
            }
            
            SplashScreen.advangeProgress();
        }
        
        
        SplashScreen.setModule("Initializing Advantages");
        Advantage a;
        
        for (int j=0; j<advantagesList.length; j++) {
            try {
                String classCanonical = powerPackage + "." + advantagesList[j][0];
                folder = advantagesList[j][1];
                
                // Grab an actual class for the Advantage.
                Class c = Class.forName( classCanonical );
                
                // Create an instance of the Advantage.
                a = (Advantage)c.newInstance();
                name = a.getName();
                
                // Store the Class of the PAD.
                padClassMap.put(name, c);
                
                SplashScreen.setDescription("Initializing Advantages(" + name + ")");
                
                // Store the Created copy as a shared instance.
                padSharedInstanceMap.put(name, a);
                
                // Initialize the Advantage.
                a.initialize();
                
                // Add the PAD to the folder structure
                addPADtoFolder(name, folder);
                
                SplashScreen.advangeProgress();
            } catch (Exception exc ) {
                ExceptionWizard.postException(exc);
            }
        }
        
        SplashScreen.setModule("Initializing Limitations");
        Limitation l;
        
        for (int j=0; j<limitationsList.length; j++) {
            try {
                String classCanonical = powerPackage + "." + limitationsList[j][0];
                folder = limitationsList[j][1];
                
                // Grab an actual class for the Limitation.
                Class c = Class.forName( classCanonical );
                
                // Create an instance of the Limitation.
                l = (Limitation)c.newInstance();
                name = l.getName();
                
                // Store the Class of the PAD.
                padClassMap.put(name, c);
                
                SplashScreen.setDescription("Initializing Limitations(" + name + ")");
                
                // Store the Created copy as a shared instance.
                padSharedInstanceMap.put(name, l);
                
                // Initialize the Limitation.
                l.initialize();
                
                // Add the PAD to the folder structure
                addPADtoFolder(name, folder);
                
                SplashScreen.advangeProgress();
            } catch (Exception exc ) {
                ExceptionWizard.postException(exc);
            }
        }
        
        SplashScreen.setModule("Initializing Special Parameters");
        SpecialParameter s;
        
        for (int j=0;j<specialParametersList.length;j++) {
            try {
                String classCanonical = powerPackage + "." + specialParametersList[j][0];
                folder = specialParametersList[j][1];
                
                // Grab an actual class for the SpecialParameter.
                Class c = Class.forName( classCanonical );
                
                // Create an instance of the SpecialParameter.
                s = (SpecialParameter)c.newInstance();
                name = s.getName();
                
                // Store the Class of the PAD.
                padClassMap.put(name, c);
                
                SplashScreen.setDescription("Initializing Special Parameters(" + name + ")");
                
                // Store the Created copy as a shared instance.
                padSharedInstanceMap.put(name, s);
                
                // Initialize the Limitation.
                s.initialize();
                
                // Add the PAD to the folder structure
                addPADtoFolder(name, folder);
                
                SplashScreen.advangeProgress();
            } catch (Exception exc ) {
                ExceptionWizard.postException(exc);
            }
        }
        
        SplashScreen.setModule("Initializing SFX");
        
        for(int i = specialEffectsList.length - 1; i >= 0; i--) {
            name = specialEffectsList[i][0];
            iconKey = specialEffectsList[i][1];
            folder = specialEffectsList[i][2];
            
            SplashScreen.setDescription("Initializing SFX(" + name + ")");
            
            // Store the Class type as Special Effect
            AddSpecialEffect(name, folder, iconKey);
            
            SplashScreen.advangeProgress();
        }
        
       
        
        sensesList = new ArrayList();
        for(int i = 0; i < sensesNameList.length; i++) {
            sensesList.add( getNewSense(sensesNameList[i]));
        }
        
        loadIOAdapters();
    }

	public static void AddSpecialEffect(String name, String folder, String iconKey) {
		padClassMap.put(name, SpecialEffect.class);
		
		// Create an instance of the SFX.
		SpecialEffect se = new SpecialEffect( name );
		if ( iconKey != null ) {
		    Icon icon = UIManager.getIcon(iconKey);
		    if ( icon != null ) se.setIcon(icon);
		}
		
		// Store the shared instance.
		padSharedInstanceMap.put(name, se);
		
		// Add the PAD to the folder structure
		addPADtoFolder(name, folder);
	}
    
    /** Returns a new instance of the indicated sense, with the default configuration.
     *
     *
     */
    static public Sense getNewSense(String name) {
        return getNewSense(name, null);
    }
    
    static public Sense getNewSense(String name, String group) {
        Sense s = null;
        if ( "Sight Group".equals(name) ) {
            s = new SenseGroup("Sight Group", true, true);
            s.setCost(10);
        } else if ( "Smell/Taste Group".equals(name) ) {
            s = new SenseGroup("Smell/Taste Group", false, true);
            s.setCost(5);
        } else if ( "Hearing Group".equals(name) ) {
            s = new SenseGroup("Hearing Group", false, true);
            s.setTransmitSense(true);
            s.setCost(5);
        } else if ( "Mental Group".equals(name) ) {
            s = new SenseGroup("Mental Group", false, true);
            s.setCost(5);
        } else if ( "Radio Group".equals(name) ) {
            s = new SenseGroup("Radio Group", false, true);
            s.setCost(5);
        } else if ( "Unusual Group".equals(name) ) {
            s = new SenseGroup("Unusual Group", false, false);
            s.setCost(5);
        } else if ( "Touch Group".equals(name) ) {
            s = new SenseGroup("Touch Group", false, false);
            s.setCost(5);
        } else if ( "Mystic Group".equals(name) ) {
            s = new SenseGroup("Mystic Group", false, true);
            s.setCost(5);
        } else if ( "Normal Sight".equals(name) ) {
            s = new Sense("Normal Sight", "Sight Group");
            s.setTargettingSense(true);
            s.setCost(5);
        } else if ( "Normal Smell".equals(name) ) {
            s = new Sense("Normal Smell", "Smell/Taste Group");
            s.setCost(3);
        } else if ( "Normal Taste".equals(name) ) {
            s = new Sense("Normal Taste", "Smell/Taste Group");
            s.setCost(3);
            s.setRangedSense(false);
        } else if ( "Normal Touch".equals(name) ) {
            s = new Sense("Normal Touch", "Touch Group");
            s.setCost(3);
        } else if ( "Radar".equals(name) ) {
            s = new Sense("Radar", "Radio Group");
            s.setTargettingSense(true);
            s.setCost(5);
        } else if ( "Radio Perception/Transmission".equals(name) ) {
            s = new Sense("Radio Perception/Transmission", "Radio Group");
            s.setCost(3);
        } else if ( "Radio Perception".equals(name) ) {
            s = new Sense("Radio Perception", "Radio Group");
            s.setCost(3);
        } else if ( "High Range Radio Perception".equals(name) ) {
            s = new Sense("High Range Radio Perception", "Radio Group");
            s.setCost(3);
        } else if ( "Normal Hearing".equals(name) ) {
            s = new Sense("Normal Hearing", "Hearing Group");
            s.setCost(3);
        } else if ( group == null && "Spatial Awareness".equals(name)  ) {
            s = new Sense("Spatial Awareness", "Unusual Group");
            s.setTargettingSense(true);
            s.setCost(5);
        } else if ( group == null && "Combat Sense".equals(name)  ) {
            s = new Sense("Combat Sense", "Unusual Group");
            s.setTargettingSense(true);
            s.setCost(5);
        } else if (  "Danger Sense".equals(name)  ) {
            s = new Sense("Danger Sense", "Unusual Group");
            s.setTargettingSense(true);
            s.setCost(5);
        } else if ( "Mind Scan".equals(name) ) {
            s = new Sense("Mind Scan", "Mental Group");
            s.setTargettingSense(true);
            s.setCost(5);
        } else if ( "Mental Awareness".equals(name) ) {
            s = new Sense("Mental Awareness", "Mental Group");
            s.setCost(3);
        } else if ( "Normal Hearing".equals(name) ) {
            s = new Sense("Normal Hearing", "Hearing Group");
            s.setCost(5);
        } else if ( "Active Sonar".equals(name) ) {
            s = new Sense("Active Sonar", "Hearing Group");
            s.setTargettingSense(true);
            s.setCost(5);
        } else if ( "Ultrasonic Perception".equals(name) ) {
            s = new Sense("Ultrasonic Perception", "Hearing Group");
            s.setCost(3);
        } else if ( "Infrared Perception".equals(name) ) {
            s = new Sense("Infrared Perception", "Sight Group");
            s.setTargettingSense(true);
            s.setCost(5);
        } else if ( "Nightvision".equals(name) ) {
            s = new Sense("Nightvision", "Sight Group");
            s.setTargettingSense(true);
            s.setCost(5);
        } else if ( "N-Ray Perception".equals(name) ) {
            s = new Sense("N-Ray Perception", "Sight Group");
            s.setTargettingSense(true);
            s.setCost(5);
        } else if ( "Ultraviolet Perception".equals(name) ) {
            s = new Sense("Ultraviolet Perception", "Sight Group");
            s.setTargettingSense(true);
            s.setCost(5);
        } else if ( "Detect".equals(name) ) {
            s = new Sense("Detect", "Unusual Group");
            s.setCost(3);
        } else {
            
            s = new Sense(name, group);
        }
        return s;
    }
    
    /** Returns an iterator over all standard senses.
     *
     */
    static public Iterator getSenses() {
        return sensesList.iterator();
        
    }
    
    /** Sets up the Default Ability List.
     */
    public static AbilityList createDefaults() {
        Ability ability;
        // Create an empty target to attach default abilities to.
        AbilityList defaultAbilities = new DefaultAbilityList("Default Abilities");
        
        SplashScreen.setDescription("Initializing Ability(Recovery)");
        Ability recovery = new Ability("Recovery");
        recovery.addPAD( new powerRecovery(), null);
        defaultAbilities.addAbility(recovery);
        Battle.setRecoveryAbility(recovery);
        
        SplashScreen.setDescription("Initializing Ability(Stun Recovery)");
        Ability unstun = new Ability("Stun Recovery");
        unstun.addPAD( new powerUnstun(), null);
        defaultAbilities.addAbility(unstun);
        Battle.setStunRecoveryAbility(unstun);
        
        SplashScreen.setDescription("Initializing Ability(Pass Turn)");
        Ability pass = new Ability("Pass Turn");
        pass.addPAD( new powerPass(), null );
        defaultAbilities.addAbility( pass );
        Battle.setPassAbility(pass);
        
        // System.out.println( pass.dumpDetailList() );
        
      /*  Ability unknockdown = new Ability("Knockdown Recovery");
        unknockdown.addPAD ( new powerUnknockdown(), null);
        defaultAbilities.addAbility(unknockdown); */
        
        SplashScreen.setDescription("Initializing Ability(Half Phase Action)");
        Ability half = new Ability("Half Phase Action");
        half.addPAD( new powerHalfphase(), null );
        defaultAbilities.addAbility( half );
        
        SplashScreen.setDescription("Initializing Ability(Hold Action)");
        Ability hold = new Ability("Hold Action");
        hold.addPAD( new powerHold(), null );
        defaultAbilities.addAbility( hold );
        
        Ability drawaweapon = new Ability("Draw A Weapon");
        drawaweapon.addPAD( new powerDrawAWeapon(), null );
        defaultAbilities.addAbility( drawaweapon );
        
        SplashScreen.setDescription("Initializing Ability(Dodge)");
        Ability dodge = new Ability("Dodge");
        dodge.addPAD( new maneuverDodge(), null );
        dodge.setAutoSource(true);
        dodge.setDCVModifier(3);
        defaultAbilities.addAbility( dodge );
        
        
        //    Ability escape = new Ability ("Escape");
        //    escape.addPAD( new maneuverEscapeFromGrab(), null );
        //   escape.setAutoSource(true);
        //   defaultAbilities.addAbility( escape );
        
        SplashScreen.setDescription("Initializing Ability(Strike)");
        Ability strike = new Ability("Strike");
        strike.addPAD( new maneuverStrike(), null );
        strike.setAutoSource(true);
        defaultAbilities.addAbility( strike );
        
        SplashScreen.setDescription("Initializing Ability(Haymaker)");
        Ability haymaker = new Ability("Haymaker");
        haymaker.addPAD( new maneuverHaymaker(), null );
        haymaker.setAutoSource(true);
        defaultAbilities.addAbility( haymaker );
        
        SplashScreen.setDescription("Initializing Ability(Prone)");
        Ability prone = new Ability("Prone");
        prone.addPAD( new maneuverProne(), null );
        prone.setAutoSource(true);
        prone.setOCVModifier(0);
        prone.setDCVModifier(0);
        defaultAbilities.addAbility( prone );
        
        SplashScreen.setDescription("Initializing Ability(Move By)");
        Ability moveby = new Ability("Move By");
        moveby.addPAD( new maneuverMoveby(), null );
        moveby.setAutoSource(true);
        moveby.setOCVModifier(-2);
        moveby.setDCVModifier(-2);
        defaultAbilities.addAbility( moveby );
        
        SplashScreen.setDescription("Initializing Ability(Move Through)");
        Ability moveThrough = new Ability("Move Through");
        moveThrough.addPAD( new maneuverMoveThrough(), null );
        moveThrough.setAutoSource(true);
        defaultAbilities.addAbility( moveThrough );
        
        SplashScreen.setDescription("Initializing Ability(Grab)");
        Ability grab = new Ability("Grab");
        grab.addPAD( new maneuverGrab(), null );
        grab.setAutoSource(true);
        grab.setOCVModifier(-1);
        grab.setDCVModifier(-2);
        defaultAbilities.addAbility( grab );
        
        SplashScreen.setDescription("Initializing Ability(Disarm)");
        Ability disarm = new Ability("Disarm");
        disarm.addPAD( new maneuverDisarm(), null );
        disarm.setAutoSource(true);
        disarm.setOCVModifier(-2);
        defaultAbilities.addAbility( disarm );

        SplashScreen.setDescription("Initializing Ability(Block)");
        Ability block = new Ability("Block");
        block.addPAD( new maneuverBlock(), null );
        block.setAutoSource(true);
        defaultAbilities.addAbility( block );
        
        SplashScreen.setDescription("Initializing Ability(Set)");
        Ability set = new Ability("Set");
        set.addPAD( new maneuverSet(), null );
        set.setAutoSource(true);
        defaultAbilities.addAbility( set );
        
        SplashScreen.setDescription("Initializing Ability(Sweep)");
        Ability sweep = new Ability("Sweep");
        sweep.addPAD( new maneuverSweep(), null );
        sweep.setAutoSource(true);
        defaultAbilities.addAbility( sweep );
        
        SplashScreen.setDescription("Initializing Ability(Rapid Fire)");
        Ability rapidFire = new Ability("Rapid Fire");
        rapidFire.addPAD( new maneuverRapidFire(), null );
        rapidFire.setAutoSource(true);
        defaultAbilities.addAbility( rapidFire );
        
        SplashScreen.setDescription("Initializing Ability(Off Ground)");
        Ability offGround = new Ability("Off Ground");
        offGround.addPAD( new powerOffGround(), null );
        offGround.setAutoSource(true);
        defaultAbilities.addAbility( offGround );
        
        //        SplashScreen.setDescription("Initializing Ability(Sequence on Ego)");
        //        Ability sequenceOnEgo = new Ability("Sequence on Ego");
        //        sequenceOnEgo.addPAD( new powerSequenceOnEgo(), null );
        //        sequenceOnEgo.setAutoSource(true);
        //        defaultAbilities.addAbility( sequenceOnEgo );
        
        //        SplashScreen.setDescription("Initializing Ability(Hold Till Dex)");
        //        Ability holdTillDex = new Ability("Hold Till Dex");
        //        holdTillDex.addPAD( new powerHoldTillDex(), null );
        //        holdTillDex.setAutoSource(true);
        //        defaultAbilities.addAbility( holdTillDex );
        
        SplashScreen.setDescription("Initializing Ability(Generic Damage/Power)");
        ability = new Ability("Generic Damage/Power");
        ability.addPAD( new powerGenericAbility(), null );
        ability.setAutoSource(true);
        defaultAbilities.addAbility( ability );
        
        //Battle.setDefaultAbilities(defaultAbilities);
        return defaultAbilities;
    }
    
    private static void readGenericPresets() {
        String preset = (String)Preferences.getPreferenceList().getParameterValue("GenericPresetDirectory");
        
        String defaults = (String)Preferences.getPreferenceList().getParameterValue("DefaultDirectory");
        File f;
        
        f = new File(preset);
        if ( f.isAbsolute() == false || f.exists() == false || f.isDirectory() == false) {
            f = new File(defaults + f.separator + preset);
        }
        
        // Should have the correct path based on the above...
        genericPresetList = buildAbilityList(f);
        if ( genericPresetList != null ) genericPresetList.setName("Presets");
        
    }
    
    private static void readPresetTargets() {
        /*String preset = (String)Preferences.getPreferenceList().getValue("PresetTargets.DIRECTORY");
         
        String defaults = (String)Preferences.getPreferenceList().getValue("Default.DIRECTORY");
        File f;
         
        f = new File(preset);
        if ( f.isAbsolute() == false || f.exists() == false || f.isDirectory() == false) {
            f = new File(defaults + f.separator + preset);
        }
         
        // Should have the correct path based on the above...
        presetTargetsList = buildTargetList(f);
        if ( presetTargetsList != null ) {
            presetTargetsList.setName("Presets");
        }
        else {
            presetTargetsList = new TargetList("Presets");
        } */
        
        if ( presetTargetsList == null ) {
            presetTargetsList = createObjectPresets();
        }
    }
    
    
    public static TargetList createObjectPresets() {
        TargetList tl = new TargetList("Preset Objects");
        
        Target target;
        TargetList sublist = new TargetList("Doors");
        tl.addSublist(sublist);
        
        target = new ObjectTarget("Airlock Door", 100, "Unliving", "Casting", 7, 8, false);
        sublist.addTarget(target);
        
        target = new ObjectTarget("City gates, small", 800, "Unliving", "Chain or heavy tube", 10, 5, false);
        sublist.addTarget(target);
        
        target = new ObjectTarget("City gates, large/heavy", 1600, "Unliving", "Chain or heavy tube", 20, 8, false);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Interior wood door", 25, "Unliving", "Thin board", 3, 2, false);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Interior spaceship door", 25, "Unliving", "Heavy fiberglass", 4, 6, false);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Exterior wood door", 50, "Unliving", "Heavy wood", 4, 3, false);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Metal fire door", 100, "Unliving", "Heavy bar", 5, 5, false);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Safe door", 100, "Unliving", "Casting", 9, 10, false);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Large vault door", 1600, "Unliving", "Casting", 16, 9, false);
        sublist.addTarget(target);
        
        sublist = new TargetList("Furniture");
        tl.addSublist(sublist);
        
        target = new ObjectTarget("Chamber pot", 1.6, "Unliving", "Other", 2, 2, true);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Computer, personal", 3.2, "Complex", "Other", 2, 2, true);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Furniture, light wood", 25, "Unliving", "Thin board", 3, 3, true);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Furniture, heavy wood", 100, "Unliving", "Heavy wood", 5, 4, true);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Furniture, plastic", 12.5, "Unliving", "Plastic casting", 3, 2, true);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Furniture, steal-reinforced", 12.5, "Unliving", "Chain or heavy tube", 5, 5, true);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Furniture, glass", 50, "Unliving", "Other", 1, 1, true);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Furniture, glass, reinforced", 50, "Unliving", "Other", 1, 2, true);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Hearth/fireplace", 800, "Unliving", "Brick", 10, 5, false);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Lamp post (breakaway)", 200, "Unliving", "Casting", 3, 5, false);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Lantern", 25, "Unliving", "Sheet metal", 2, 1, true);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Mantel", 400, "Unliving", "Brick", 3, 3, false);
        sublist.addTarget(target);
        
        sublist = new TargetList("Locks");
        tl.addSublist(sublist);
        
        target = new ObjectTarget("House door lock", 0.4, "Unliving", "Other", 2, 3, false);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Magnetic lock", 0.8, "Unliving", "Other", 3, 5, false);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Padlock", 0.8, "Unliving", "Other", 3, 4, false);
        sublist.addTarget(target);
        
        sublist = new TargetList("Machinery");
        tl.addSublist(sublist);
        
        target = new ObjectTarget("Light machinery", 100, "Complex", "Other", 4, 5, true);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Medium machinery", 400, "Complex", "Other", 6, 7, true);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Heavy machinery", 1600, "Complex", "Other", 8, 9, true);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Spacesuit", 50, "Complex", "Other", 3, 2, true);
        sublist.addTarget(target);
        
        sublist = new TargetList("Outdoor Items, Misc.");
        tl.addSublist(sublist);
        
        target = new ObjectTarget("Awning", 50, "Unliving", "Other", 3, 1, false);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Boulder (single)", 400, "Unliving", "Other", 13, 5, true);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Bridge, small", 1600, "Unliving", "Other", 21, 9, false);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Bridge, large", 100000, "Unliving", "Other", 27, 9, false);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Bushes", 25, "Living", "Other", 2, 2, true);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Cobblestone, single", 25, "Unliving", "Other", 4, 4, true);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Dirt (per hex)", 0.4, "Unliving", "Other", 10, 0, true);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Flagpole (breakaway)", 200, "Unliving", "Other", 2, 4, false);
        sublist.addTarget(target);
        
        target = new ObjectTarget("I Beam (per 2m length)", 800, "Unliving", "Casting", 8, 9, false);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Manhold cover", 25, "Unliving", "Other", 5, 9, true);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Railroad tracks", 800, "Unliving", "Other", 5, 4, false);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Roadway (0.5m thick)", 1600, "Unliving", "Other", 11, 5, false);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Stone (per hex)", 3200, "Unliving", "Other", 19, 5, false);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Telephone pole", 800, "Unliving", "Heavy wood", 5, 5, false);
        sublist.addTarget(target);
        
        sublist = new TargetList("Trees");
        tl.addSublist(sublist);
        
        target = new ObjectTarget("Small tree (less than 1')", 100, "Living", "Heavy wood", 5, 4, false);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Medium tree (less than 5')", 400, "Living", "Heavy wood", 8, 5, false);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Large tree (5' or more)", 1600, "Living", "Heavy wood", 11, 5, false);
        sublist.addTarget(target);
        
        sublist = new TargetList("Vehicles");
        tl.addSublist(sublist);
        
        target = new ObjectTarget("Armored car", 3200, "Vehical", "Other", 18, 8, true);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Automobile", 1600, "Vehical", "Other", 15, 3, true);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Bicycle", 25, "Vehical", "Other", 2, 3, true);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Cart, small", 50, "Vehical", "Other", 8, 3, true);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Cart, large", 100, "Vehical", "Other", 12, 4, true);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Chariot", 400, "Vehical", "Other", 4, 8, true);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Helicopter", 3200, "Vehical", "Other", 14, 3, true);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Off Groundcraft", 6400, "Vehical", "Other", 14, 5, true);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Jetpack", 50, "Vehical", "Other", 4, 4, true);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Motorcycle", 400, "Vehical", "Other", 11, 3, true);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Plane, Light", 1600, "Vehical", "Other", 13, 3, true);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Plane, Twin engine", 3200, "Vehical", "Other", 15, 3, true);
        sublist.addTarget(target);
        
        
        target = new ObjectTarget("Plane, Multi-engine", 6400, "Vehical", "Other", 19, 3, true);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Railroad car", 12500, "Vehical", "Other", 15, 6, true);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Spaceship, small", 25000, "Vehical", "Other", 10, 10, true);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Spaceship, medium", 100000, "Vehical", "Other", 30, 10, true);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Spaceship, large", 200000, "Vehical", "Other", 55, 15, true);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Submarine", 100000, "Vehical", "Other", 20, 10, true);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Tank (front armor)", 50000, "Vehical", "Other", 19, 20, true);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Tank (side, top, rear, bottom)", 50000, "Vehical", "Other", 19, 16, true);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Truck or bus", 12500, "Vehical", "Other", 17, 4, true);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Wagon, covered", 800, "Vehical", "Other", 12, 3, true);
        sublist.addTarget(target);
        
        sublist = new TargetList("Walls");
        tl.addSublist(sublist);
        
        target = new ObjectTarget("Armored wall, 250mm", 250, "Wall", "Other", 7, 13, false);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Brick wall, 64mm", 64, "Wall", "Brick", 3, 5, false);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Concrete wall, 125mm", 125, "Wall", "Concrete", 5, 6, false);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Home inside wall, 32mm", 32, "Wall", "Plywood", 3, 3, false);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Home outside wall, 32mm", 32, "Wall", "Heavy wood", 3, 4, false);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Reinforced concrete wall, 125mm", 125, "Wall", "Reinforced Concrete", 5, 8, false);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Spaceship interior wall, 32mm", 32, "Wall", "Armored plastics", 6, 8, false);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Wooden wall, 32mm", 32, "Wall", "Heavy wood", 3, 4, false);
        sublist.addTarget(target);
        
        sublist = new TargetList("Weapons");
        tl.addSublist(sublist);
        
        target = new ObjectTarget("Heavy weapon", 6.4, "Complex", "Other", 8, 6, true);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Pistol", 1.6, "Complex", "Other", 3, 4, true);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Rifle", 6.4, "Complex", "Other", 6, 4, true);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Sword", 6.4, "Unliving", "Other", 5, 4, true);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Very large heavy weapon", 25, "Unliving", "Other", 12, 6, true);
        sublist.addTarget(target);
        
        sublist = new TargetList("Miscellaneous");
        tl.addSublist(sublist);
        
        target = new ObjectTarget("Barrel", 25, "Unliving", "Heavy wood", 3, 6, true);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Control console (per hex)", 25, "Complex", "Other", 4, 4, true);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Drum, 55-gallon, steel", 50, "Complex", "Sheet metal", 6, 4, true);
        sublist.addTarget(target);
        
        target = new ObjectTarget("Wooden crate (� square)", 25, "Complex", "Heavy wood", 7, 4, true);
        sublist.addTarget(target);
        
        return tl;
    }
    
    private static AbilityList buildAbilityList(File directory) {
        if ( directory.exists() && directory.isDirectory() && directory.canRead() ) {
            AbilityList al = new DefaultAbilityList(directory.getName());
            File[] files = directory.listFiles();
            for(int i = 0; i < files.length; i++) {
                if ( files[i].isDirectory() ) {
                    AbilityList sublist = buildAbilityList(files[i]);
                    if ( sublist != null ) al.addSublist(sublist);
                } else if ( files[i].getName().endsWith(".abt") ) {
                    try {
                        Ability a = (Ability)Ability.open( files[i] );
                        al.addAbility(a);
                    } catch ( java.io.IOException ioe) {
                        ExceptionWizard.postException(ioe);
                    } catch ( ClassNotFoundException cnfe ) {
                        ExceptionWizard.postException(cnfe);
                    }
                }
            }
            return al;
        }
        return null;
    }
    
    private static TargetList buildTargetList(File directory) {
        if ( directory.exists() && directory.isDirectory() && directory.canRead() ) {
            TargetList al = new TargetList(directory.getName());
            File[] files = directory.listFiles();
            for(int i = 0; i < files.length; i++) {
                if ( files[i].isDirectory() ) {
                    TargetList sublist = buildTargetList(files[i]);
                    if ( sublist != null ) al.addSublist(sublist);
                } else if ( files[i].getName().endsWith(".tgt") ) {
                    try {
                        Target a = (Target)Target.open( files[i] );
                        al.addTarget(a);
                    } catch ( java.io.IOException ioe) {
                        ExceptionWizard.postException(ioe);
                    } catch ( ClassNotFoundException cnfe ) {
                        ExceptionWizard.postException(cnfe);
                    }
                }
            }
            return al;
        }
        return null;
    }
    
    public static AbilityList getGenericPresets() {
        if ( genericPresetList == null ) {
            readGenericPresets();
        }
        
        return genericPresetList;
    }
    
    public static TargetList getPresetTargets() {
        if ( presetTargetsList == null ) {
            readPresetTargets();
        }
        
        return presetTargetsList;
    }
    
    /** Adds the specified padName to the specified folderList location.
     *
     * Folders will be created, as necessary, to create the specified structure.
     *
     * The folderList should be a colon delimited list of folders.
     */
    private static void addPADtoFolder(String padName, String folderList) {
        if ( folderList == null ) return;
        
        PADFolder folder = rootFolder;
        PADFolder nextFolder;
        
        StringTokenizer st = new StringTokenizer(folderList, ":");
        while (st.hasMoreTokens() ) {
            String folderName = st.nextToken();
            nextFolder = folder.findFolder( folderName );
            if ( nextFolder == null ) {
                nextFolder = new PADFolder(folderName);
                folder.addFolder(nextFolder);
            }
            folder = nextFolder;
        }
        folder.addPAD(padName);
    }
    
    //    /** Returns an array contain the names of all the powers currently configured.
    //     * @return
    //     */
    //    public static Object[] powerNameArray() {
    //        if ( ! inited ) setup();
    //
    //        return powerNameVector.toArray();
    //    }
    //
    //    /** Returns an array contain the names of all the skills currently configured.
    //     * @return
    //     */
    //    public static Object[] skillNameArray() {
    //        if ( ! inited ) setup();
    //
    //        return skillNameVector.toArray();
    //    }
    //
    //    /** Returns an Ability instance configured according to the power indicated by
    //     * name.  The Ability is always the same object and should not be used to configure
    //     * the ability direction.  Instead newPowerInstance should be used to get a
    //     * unique instance of the power.  GetPowerInstance is useful for getting a copy of
    //     * the ability template to use for identification purposes.
    //     * @param name
    //     * @return
    //     */
    //    public static Ability getPowerInstance(String name) {
    //        if ( powerObjectMap.containsKey( name ) ) {
    //            return (Ability) powerObjectMap.get(name);
    //        }
    //        else {
    //            Ability newTemplate = newPowerInstance(name);
    //            if ( newTemplate != null ) {
    //                powerObjectMap.put(name, newTemplate);
    //            }
    //            return newTemplate;
    //        }
    //    }
    //
    //
    //    /** Returns an Ability instance configured according to the power indicated by
    //     * name.  The Ability is always a new object.
    //     * @param name
    //     * @return
    //     */
    //    public static Ability newPowerInstance(String name) {
    //        if ( powerTemplateMap.containsKey( name ) ) {
    //            Object o = ((AbilityTemplate)powerTemplateMap.get(name)).newAbilityInstance();
    //            if ( o != null ) {
    //                return (Ability)o;
    //            }
    //        }
    //        return null;
    //    }
    
    
    /** Returns the Power configured in the power/skill template specified by name.
     * The Power returned is always the same.
     * @param name
     * @return
     */
    public static Power getPower(String name) {
        Ability sharedInstance = getSharedAbilityInstance(name);
        if ( sharedInstance != null ) {
            return sharedInstance.getPower();
        }
        
        return null;
    }
    
    /** Returns an IOAdapter capable of reading the indicated file extension.
     *
     * This method query's the registered IOAdapters and returns the first one
     * that is capable of reading the indicated file extension.
     */
    public static IOAdapter getReadAdapterForExtension(String fileExtension) {
        Iterator i = ioAdapterList.iterator();
        
        while( i.hasNext() ) {
            IOAdapter ioa = (IOAdapter)i.next();
            if ( ioa.lookupReadFileExtension(fileExtension) != -1 ) {
                return ioa;
            }
        }
        return null;
    }
    
    /** Returns an IOAdapter capable of reading the indicated file extension.
     *
     * This method query's the registered IOAdapters and returns the first one
     * that is capable of reading the indicated file extension.
     */
    public static IOAdapter getReadAdapterForMimeType(String mimeType) {
        Iterator i = ioAdapterList.iterator();
        
        while( i.hasNext() ) {
            IOAdapter ioa = (IOAdapter)i.next();
            if ( ioa.lookupReadMimeType(mimeType) != -1 ) {
                return ioa;
            }
        }
        return null;
    }
    
    /** Fills the provided vectors with file extensions and descriptions for
     * the indicated class.
     *
     */
    public static Map getReadInfoForClass(Class c) {
        HashMap map = new HashMap();
        if ( ioAdapterList != null ) {
            Iterator i = ioAdapterList.iterator();
            
            while( i.hasNext() ) {
                IOAdapter ioa = (IOAdapter)i.next();
                int count = ioa.getReadFormatCount();
                for(int index = 0; index < count; index++) {
                    if ( ioa.getReadClass(index).isAssignableFrom(c) ){
                        map.put( ioa.getReadFileExtension(index), ioa.getReadFormatDescription(index) );
                    }
                }
            }
        }
        
        return map;
    }
    
    /** Returns an IOAdapter capable of Writeing the indicated file extension.
     *
     * This method query's the registered IOAdapters and returns the first one
     * that is capable of Writeing the indicated file extension.
     */
    public static IOAdapter getWriteAdapterForExtension(String fileExtension) {
        Iterator i = ioAdapterList.iterator();
        
        while( i.hasNext() ) {
            IOAdapter ioa = (IOAdapter)i.next();
            if ( ioa.lookupWriteFileExtension(fileExtension) != -1 ) {
                return ioa;
            }
        }
        return null;
    }
    
    /** Returns an IOAdapter capable of Writeing the indicated file extension.
     *
     * This method query's the registered IOAdapters and returns the first one
     * that is capable of Writeing the indicated file extension.
     */
    public static IOAdapter getWriteAdapterForMimeType(String mimeType) {
        Iterator i = ioAdapterList.iterator();
        
        while( i.hasNext() ) {
            IOAdapter ioa = (IOAdapter)i.next();
            if ( ioa.lookupWriteMimeType(mimeType) != -1 ) {
                return ioa;
            }
        }
        return null;
    }
    
    /** Fills the provided vectors with file extensions and descriptions for
     * the indicated class.
     *
     */
    public static Map getWriteInfoForClass(Class c) {
        HashMap map = new HashMap();
        if ( ioAdapterList != null ) {
            Iterator i = ioAdapterList.iterator();
            
            while( i.hasNext() ) {
                IOAdapter ioa = (IOAdapter)i.next();
                int count = ioa.getWriteFormatCount();
                for(int index = 0; index < count; index++) {
                    if ( c.isInstance( ioa.getWriteClass(index) ) ) {
                        map.put( ioa.getWriteFileExtension(index), ioa.getWriteFormatDescription(index) );
                    }
                }
            }
        }
        return map;
    }
    
    /** Fills the ioAdapterList from the ioAdapterClasses array.
     */
    private static void loadIOAdapters() {
        for(int i = 0; i < ioAdapterClasses.length; i++) {
            loadIOAdapter(ioAdapterClasses[i]);
        }
    }
    
    /** Loads an IOAdapter and places it in the ioAdapterList.
     */
    public static void loadIOAdapter(Class c) {
        try {
            if ( champions.interfaces.IOAdapter.class.isAssignableFrom(c) ) {
                Object o = c.newInstance();
                if ( ioAdapterList == null ) ioAdapterList = new ArrayList();
                
                ioAdapterList.add(o);
            }
        } catch ( Exception e ) {
            ExceptionWizard.postException(e);
        }
        
    }
    
    //    public static SpecialEffect getSpecialEffectInstance(String specialEffectName) {
    //        return (SpecialEffect) specialEffectsNamesToObject.get(specialEffectName);
    //    }
    //
    //    public static SpecialEffect newSpecialEffectInstance(String specialEffectName) {
    //        SpecialEffect se = getSpecialEffectInstance(specialEffectName);
    //        if ( se != null ) {
    //            se = se.newInstance();
    //        }
    //        return se;
    //    }
    //
    //    public static Object[] specialEffectNameArray() {
    //        if ( ! inited ) setup();
    //
    //        return specialEffectsNamesToObject.keySet().toArray();
    //    }
    
    static public class AbilityTemplate extends Object {
        private String name;
        private String powerName = null;
        private String templateName = null;
        transient private Power powerInstance =  null;
        transient private Class powerClass;
        transient private Ability templateAbility;
        
        public AbilityTemplate(String name, String powerName, String templateName) throws IOException, ClassNotFoundException {
            InputStream inputStream = null;
            ObjectInputStream ois;
            
            this.name = name;
            if ( powerName != null ) {
                powerClass = Class.forName( powerPackage + "." + powerName );
            } else if ( templateName != null ) {
                String directory;
//                    if ( (directory = Preferences.getPreferenceList().getStringValue("Template.DIRECTORY" )) != null ) {
//
//                    }
                if ((directory = (String)Preferences.getPreferenceList().getParameterValue("DefaultDirectory" )) != null ) {
                    
                }
                
                // if Template Wasn't found in template/default directory, look in the distribution
                if ( templateAbility == null ) {
                    URL templateURL = getClass().getResource( templateLocation + templateName );
                    if ( templateURL != null ) {
                        inputStream = templateURL.openStream();
                    }
                }
                
                if ( inputStream != null ) {
                    if ( inputStream.available() == 0 ) {
                        throw new IOException("Unable to load file containing " + templateName);
                    }
                    
                    ois = new ObjectInputStream(inputStream);
                    Object o;
                    try {
                        o = ois.readObject();
                    } catch (IOException ex) {
                        System.out.println("Error occurred while loading template for " + templateName);
                        throw ex;
                    } catch (ClassNotFoundException ex) {
                        System.out.println("Error occurred while loading template for " + templateName);
                        throw ex;
                    }
                    
                    if ( o instanceof Ability ) {
                        templateAbility = (Ability)o;
                    }
                }
                
                if ( templateAbility != null ) {
                    templateAbility.setName(name);
                }
                
                
            }
            
        }
        
        public Ability getTemplateInstance() {
            return templateAbility;
        }
        
        public Ability newAbilityInstance() {
            Ability a = null;
            
            if ( powerClass != null ) {
                try {
                    Object p = powerClass.newInstance();
                    if ( p != null ) {
                        a = new Ability(name);
                        a.addPAD((Power)p,null);
                    }
                } catch (Exception exc ) {
                    ExceptionWizard.postException(exc);
                    System.out.println("Exception while loading class: " + exc.toString() );
                    exc.printStackTrace();
                }
            } else if ( templateAbility != null ) {
                a = (Ability)templateAbility.clone();
                //a.remove( "Ability.SOURCE" );
                a.setSource(null);
                a.setName(name);
            }
            return a;
        }
        
        public Power getPower() {
            if ( powerInstance == null && powerClass != null ) {
                try {
                    Object p = powerClass.newInstance();
                    if ( p != null ) {
                        powerInstance = (Power)p;
                    }
                } catch (Exception exc ) {
                    ExceptionWizard.postException(exc);
                }
            } else if ( templateAbility != null ) {
                powerInstance = templateAbility.getPower();
            }
            
            return powerInstance;
        }
    }
    
    public static class PADClassIterator implements Iterator {
        String next = null;
        Iterator iterator = padClassMap.keySet().iterator();
        Class iteratorClass;
        
        public PADClassIterator(Class c) {
            iteratorClass = c;
        }
        
        /** Returns <tt>true</tt> if the iteration has more elements. (In other
         * words, returns <tt>true</tt> if <tt>next</tt> would return an element
         * rather than throwing an exception.)
         *
         * @return <tt>true</tt> if the iterator has more elements.
         *
         */
        public boolean hasNext() {
            if ( next == null ) {
                findNext();
            }
            
            return next != null;
        }
        
        /** Returns the next element in the iteration.
         *
         * @return the next element in the iteration.
         * @exception NoSuchElementException iteration has no more elements.
         *
         */
        public Object next() {
            if ( next == null ) {
                findNext();
            }
            
            if ( next == null ) {
                throw new NoSuchElementException();
            }
            
            // Store the next, so we can clear it...
            Object result = next;
            // Reset next, so a new one is fetched next time.
            next = null;
            
            return result;
        }
        
        /**
         * Removes from the underlying collection the last element returned by the
         * iterator (optional operation).  This method can be called only once per
         * call to <tt>next</tt>.  The behavior of an iterator is unspecified if
         * the underlying collection is modified while the iteration is in
         * progress in any way other than by calling this method.
         *
         * @exception UnsupportedOperationException if the <tt>remove</tt>
         * 		  operation is not supported by this Iterator.
         *
         * @exception IllegalStateException if the <tt>next</tt> method has not
         * 		  yet been called, or the <tt>remove</tt> method has already
         * 		  been called after the last call to the <tt>next</tt>
         * 		  method.
         *
         */
        public void remove() {
            throw new UnsupportedOperationException();
        }
        
        private void findNext() {
            next = null;
            while ( iterator.hasNext() ) {
                String key = (String)iterator.next();
                Class c = (Class)padClassMap.get(key);
                if ( iteratorClass.isAssignableFrom( c ) ) {
                    next = key;
                    break;
                }
            }
        }
    }
    
}