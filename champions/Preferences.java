/*
 * Preferences.java
 *
 * Created on October 31, 2000, 3:14 PM
 */

package champions;

import champions.parameters.PreferenceList;
import champions.parameters.Parameter;
import champions.parameters.ParameterList;
import java.io.*;
import java.net.*;
import java.util.Iterator;
/**
 *
 * @author  unknown
 * @version
 */
public class Preferences extends Object {
    
    private static PreferenceList preferenceList = null;
    //static private Preferences preferences = null;
    
    //static private PreferenceList parameterDefinition = null;
    /** Creates new Preferences */
//    public Preferences() {
//        loadPreferences();
//        //setDefaultPreferences();
//    }
    
    
    public static PreferenceList getPreferenceList() {
        if ( preferenceList == null ) loadPreferences();
        return preferenceList;
    }
    
    public static boolean getBooleanValue(String preference) {
        return (Boolean)getPreferenceList().getParameterValue(preference);
    }
    
    public static void savePreferenceList() {
        if ( preferenceList != null ) savePreferences();
    }
    
    
    protected static void loadPreferences() {
        try {
            File f = new File("HCSprefs.ini");
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fis);
            preferenceList = (PreferenceList) ois.readObject();
            updatePreferenceParameters(preferenceList);
            
            System.out.println("Loaded preferences from " + f.getAbsoluteFile());
        } catch (FileNotFoundException fnfe) {
        // ignore this
        } catch (Exception ex) {
            champions.exceptionWizard.ExceptionWizard.postException(ex);
        }
        
        
        
        if ( preferenceList == null ) {
            try {
                // Attempt to load the defaults...
                URL defaultPrefs = Preferences.class.getResource("/champions/templates/HCSprefs.ini");
                if ( defaultPrefs != null ) {
                    ObjectInputStream ois = new ObjectInputStream(defaultPrefs.openStream());
                    preferenceList = (PreferenceList)ois.readObject();
                }
            } catch (FileNotFoundException fnfe) {
            // ignore this
            }catch (Exception ex) {
                champions.exceptionWizard.ExceptionWizard.postException(ex);
            }
        }
        
        if ( preferenceList == null ) {
            preferenceList = createPreferenceParameters();
            //setDefaultPreferences();
        }
        
        //preferenceList.setFireChangeByDefault(false);
    }
    
    
    
    protected static void savePreferences() {
        if ( preferenceList != null ) {
            try {
                File file = new File("HCSprefs.ini");
                FileOutputStream fos = new FileOutputStream(file);
                ObjectOutputStream out = new ObjectOutputStream(fos);
                
                out.writeObject(preferenceList);
                out.flush();
                out.close();
            } catch (FileNotFoundException fnfe) {
            } catch (IOException ioe) {
            }
        }
    }
    
//    protected void setDefaultPreferences() {
//        if ( preferenceList == null ) return;
//        
//        int index,count;
//        String key;
//        Object o;
//        
//        PreferenceList pl = getPreferenceParameters();
////        Iterator<Parameter> it = pl.getParameters();
////        //count = pl.getIndexedSize("parameter");
////        //for ( index=0;index<count;index++) {
////        while ( it.hasNext() ) {
////            Parameter p = it.next();
////            key = p.getKey();
////            o = pl.getParameterValue( p.getName() );
////            preferenceList.add( key, o, false, false );
////        }
//        
//    }
    
    protected static PreferenceList createPreferenceParameters() {
        //if ( parameterDefinition == null ) {
            //PreferenceList parameterDefinition 
            
            PreferenceList pl = new PreferenceList();
            updatePreferenceParameters(pl);
            
            return pl;
    }
    
    protected static void updatePreferenceParameters(PreferenceList pl) {
            Parameter param;
            
            // General Parameters
            addFileParameter(pl, "DefaultDirectory", "Default.DIRECTORY", "Default Directory", "" ,false );
            pl.setPanel("DefaultDirectory","General");
            pl.setHelpText("DefaultDirectory","Default Directory: Specifies the default directory to find HCS files.");
            
            addFileParameter(pl, "GenericPresetDirectory", "GenericPreset.DIRECTORY", "Generic Ability Directory", "presets" ,false );
            pl.setPanel("GenericPresetDirectory","General");
            pl.setHelpText("GenericPresetDirectory","Generic Ability Directory: Specifies the location of Generic Abilities which are " +
                    "automatically loaded into the presets folder used the Generic Damage/Power ability.  If an incomplete path is " +
                    "provided, the directory will be relative to the default directory.");
            
            addFileParameter(pl, "PresetTargetsDirectory", "PresetTargets.DIRECTORY", "Preset Target Directory", "objects" ,false );
            pl.setPanel("PresetTargetsDirectory","General");
            pl.setHelpText("PresetTargetsDirectory","Preset Target Directory: Specifies the location of Preset Targets which are " +
                    "automatically loaded to create Targets quickly.  If an incomplete path is " +
                    "provided, the directory will be relative to the default directory.");
            
            addFileParameter(pl, "AutoLoadRoster", "Autoload.ROSTER", "Autoload Roster", "" ,true );
            pl.setPanel("AutoLoadRoster","General");
            pl.setHelpText( "AutoLoadRoster","Autoload Roster: Specifies a default roster which will be opened every time HCS starts.");
            
            addBooleanParameter(pl, "FullDice", "FullDice.ENABLED", "Show Individual Dice", false);
            pl.setPanel("FullDice","General");
            pl.setHelpText( "FullDice","Show Individual Dice: Shows all of the rolled dice, plus the Stun and Body totals whenever dice rolls are displayed.");
            
            addBooleanParameter(pl, "NameWithDamage", "NameWithDamage.ENABLED", "Show Powers with Damage", true);
            pl.setPanel("NameWithDamage","General");
            pl.setHelpText( "NameWithDamage","Show Powers with Damage: Controls whether estimated damage dice are displaying for Powers and Manuevers in the ability list. "
                    + "For better performance, disable this parameter.");
            
            addBooleanParameter(pl, "AutoUpdate", "AutoUpdate.ENABLED", "AutoUpdate Enabled", true);
            pl.setPanel("AutoUpdate","General");
            pl.setHelpText( "AutoUpdate","AutoUpdate Enabled: Check for a new version of HCS every time HCS starts.");
            
            param = addIntegerParameter(pl, "DebugLevel", "HCS.DEBUGLEVEL", "Debug Level", 0);
            pl.setPanel("DebugLevel","General");
            pl.setHelpText( "DebugLevel","Debug Level: Specifies the level of debugging to use.\n0 - No Debugging\n1 - Show debugging menu options. 2 thru 5 - Low level debugging.");
            param.setClassVariable("champions.Battle.debugLevel");
            
            addBooleanParameter(pl, "ShowExceptions", "HCS.USEEXCEPTIONDIALOG", "Show Simulator Exceptions", true);
            pl.setPanel("ShowExceptions","General");
            pl.setHelpText( "ShowExceptions","Show Simulator Exceptions: When a low-level error occurs in HCS, a dialog displaying the error will be shown.");
            
            addBooleanParameter(pl, "LookAndFeel", "UseSystemLookAndFeel.ENABLED", "Use the system native look and feel (Requires restart)", false);
            pl.setPanel("LookAndFeel","General");
            pl.setHelpText( "LookAndFeel","Use System Native Look and Feel: When enabled, the system native look and feel is used.  When disable, the java cross platform look and feel is used.");
            
            
            addBooleanParameter(pl, "AutoPost12", "AutoPost12.ENABLED", "Automatic processing of Post-12 Actions", true);
            pl.setPanel("AutoPost12","General");
            pl.setHelpText( "AutoPost12","Automatic processing of Post-12 Actions: When Enabled, the system will automatically take Post-12 Recoveries for all eligible characters and bypass characters not eligible.");
            
            addBooleanParameter(pl, "AutoStunRecovery", "AutoStunRecovery.ENABLED", "Automatic processing of Stun Recoveries", false);
            pl.setPanel("AutoStunRecovery","General");
            pl.setHelpText( "AutoStunRecovery","Automatic processing of Stun Recoveries: When Enabled, the system will automatically take Stun Recoveries for Stunned characters.");
            
            addBooleanParameter(pl, "AutoUnconscious", "AutoUnconscious.ENABLED", "Automatic processing of Unconscious Characters", true);
            pl.setPanel("AutoUnconscious","General");
            pl.setHelpText( "AutoUnconscious","Automatic processing of Unconscious Characters: When Enabled, the system will automatically handle the action phase for unconscious characters.  If the character is unconscious, a stun recovery will be taken if necessary, then normal recoveries will be taken whenever possible");
            
            addBooleanParameter(pl, "SingleAdvanceWhenEligible", "SingleAdvanceWhenEligible.ENABLED", "Single Segment Advance w/Eligible Characters", true);
            pl.setPanel("SingleAdvanceWhenEligible","General");
            pl.setHelpText( "SingleAdvanceWhenEligible","Single Segment Advance w/Eligible Characters: When Enabled, the system will, by default, only advance a single segment at a time, if there are eligible characters.  Eligible characters include any character which has a held action.");
            
            
            addIntegerParameter(pl, "RecentTargets", "RecentTargets.MAXIMUM", "Maximum Number of Recent Targets", new Integer(10));
            pl.setPanel("RecentTargets","General");
            pl.setHelpText( "RecentTargets","Maximum Number of Recent Targets: Specifies the Maximum number of recent targets which are recored for any particular character.");
            
            
            addComboParameter(pl, "FrameworkMode", "Framework.MODE", "Default Framework Mode", "Warning Only", new String[] { "Warning Only", "Implicit Reconfiguration", "Explicit Reconfiguration"});
            pl.setPanel("FrameworkMode", "General");
            pl.setHelpText("FrameworkMode", "Framework Mode: Indicates the default mode for Framework powers.  Warning Only will allow any framework power to be activated at any time." +
                    "  Implicit Reconfiguration will automatically reconfigure the Framework as powers are activated and deactivate and will restrict power activation according to the framework rules" +
                    "  Explicit reconfiguration requires the framework to be explicitly reconfigured.  Power not in the current configuration will be disabled.");
            
            addComboParameter(pl, "AutopurgeTime", "Autopurge.AGE", "Autopurge Age", "1 Minute", new String[] { "Never", "1 Turn", "2 Turns", "3 Turns", "4 Turns", "1 Minute", "5 Minutes"});
            pl.setPanel("AutopurgeTime", "General");
            pl.setHelpText("AutopurgeTime", "Autopurge Age: As a battle is run, memory is consumed storing past actions.  Autopurge will automatically " +
                    "clean up actions which are older then the specified age.  Once an action is purged, it is no longer undoable but all messages, effects, etc. " +
                    "related to the action will be retained.");
            
            // Rules parameters
            addComboParameter(pl, "RuleSet", "Champions.RULESET", "Rule Set", "Superheroic", new String[] { "Superheroic","Heroic" });
            pl.setPanel("RuleSet","Rules");
            pl.setHelpText( "RuleSet","Rule Set: Indicates which ruleset to use for gameplay.\nCurrently, this changes nothing.");
            
            // Optional Rules
            addBooleanParameter(pl, "KnockbackEnabled", "Knockback.ENABLED", "Knockback Enabled", true);
            pl.setPanel("KnockbackEnabled","Optional Rules");
            pl.setHelpText( "KnockbackEnabled","Knockback Enabled: Enables Hero System Knockback rules.");
            
            addBooleanParameter(pl, "AllowHitLocation", "HitLocation.ALLOWED", "Allow Hit Locations", true);
            pl.setPanel("AllowHitLocation","Optional Rules");
            pl.setHelpText( "AllowHitLocation","Allow Hit Locations: Allow called shots to be made, if the player desires.  The hit location will only be used if a called shot is desired.");
            
            addBooleanParameter(pl, "HitLocationRequired", "HitLocation.REQUIRED", "Require Hit Locations", true);
            pl.setPanel("HitLocationRequired","Optional Rules");
            pl.setHelpText( "HitLocationRequired","Require Hit Locations: A Hit Location is required for every attack.  Random hit location will be rolled if the player does not attempt a called shot." +
                    "NOTE: If a user wants to display an informational hit location area then check this box and the Require Hit Location but DO NOT check Hit Location Affect Damage.");
            
            addBooleanParameter(pl, "HitLocationAffectDamage", "HitLocation.AFFECTSDAMAGE", "Hit Locations Affect Damage", false);
            pl.setPanel("HitLocationAffectDamage","Optional Rules");
            pl.setHelpText( "HitLocationAffectDamage","Hit Locations Affect Damage: Hit Locations affect the damage an attack does, according to the hit location table.  If no hit location was determined for " +
                    "the attack, normal damage determination will be used.");
            
            addBooleanParameter(pl, "MentalEffectAllowOtherClasseOfMind", "MentalEffect.ALLOWOTHERCLASSOFMIND", "Mental Powers Can Effect unlisted Classes Of Minds", true);
            pl.setPanel("MentalEffectAllowOtherClasseOfMind","Optional Rules");
            pl.setHelpText( "MentalEffectAllowOtherClasseOfMind","Mental Powers such as Mind Control generally only affect the Classes of Minds that are specificed within their" +
                    "configuration.  This optional rule allows attacking minds outside of the configured list for -3 to ECV.");
            
            
            
            // House Rules
            addBooleanParameter(pl, "HalfMoveSequencer", "HalfMoveSequencer.ENABLED", "Alternate Combat Sequence 1", false);
            pl.setPanel("HalfMoveSequencer","House Rules");
            pl.setHelpText( "HalfMoveSequencer","Alternate Combat Sequence(1): Changes the combat sequence, such that when a character takes a "
                    + "half move in a particular segment, all other characters active in that segment get to take their action before the original "
                    + "character can finish his action.  (Must create new battle or restart to take effect)");
            
            // Messages
            addBooleanParameter(pl, "DamageAbsorbedMessage", "DamageAbsorbedMessage.ENABLED", "Display Damage absorbed by Defenses Messages", true);
            pl.setPanel("DamageAbsorbedMessage","Messages");
            pl.setHelpText( "DamageAbsorbedMessage","Display Damage absorbed by Defenses Message: When Enabled, the system will display how much damage was absorbed by defenses during an attack.");
            
            addBooleanParameter(pl, "DieRollMessages", "DieRollMessages.ENABLED", "Display Die Roll Messages", true);
            pl.setPanel("DieRollMessages","Messages");
            pl.setHelpText( "DieRollMessages","Display Die Roll Messages: When Enabled, the system will display informational messages about every die roll which is made.");
            
            addBooleanParameter(pl, "AdjustmentFadeMessage", "AdjustmentFadeMessage.ENABLED", "Display Adjustment Fade Messages", true);
            pl.setPanel("AdjustmentFadeMessage","Messages");
            pl.setHelpText( "AdjustmentFadeMessage","Display Adjustment Fade Messages: When Enabled, the system will display informational messages concerning the fade timing for all adjustment powers.");
            
       // }
    }

    static void resetPreferences() {
        preferenceList = createPreferenceParameters();
    }

    private static Parameter addBooleanParameter(ParameterList pl, String parameterName, String parameterKey, String description, boolean defaultValue) {
        if ( pl.getParameter(parameterName) == null ) {
            pl.addBooleanParameter(parameterName, parameterKey, description, defaultValue);
        }
        
        return pl.getParameter(parameterName);
    }

    private static Parameter addComboParameter(ParameterList pl, String parameterName, String parameterKey, String description, String defaultValue, String[] options) {
        if ( pl.getParameter(parameterName) == null ) {
            pl.addComboParameter(parameterName, parameterKey, description, defaultValue, options);
        }
        
        return pl.getParameter(parameterName);
    }

    private static Parameter addFileParameter(ParameterList pl, String parameterName, String parameterKey, String description, String defaultValue, boolean b) {
        if ( pl.getParameter(parameterName) == null ) {
            pl.addFileParameter(parameterName, parameterKey, description, defaultValue, b);
        }
        
        return pl.getParameter(parameterName);
    }

    private static Parameter addIntegerParameter(ParameterList pl, String parameterName, String parameterKey, String description, int defaultValue) {
        if ( pl.getParameter(parameterName) == null ) {
            pl.addIntegerParameter(parameterName, parameterKey, description, defaultValue);
        }
        
        return pl.getParameter(parameterName);
    }
}