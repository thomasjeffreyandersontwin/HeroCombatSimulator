/*
 * CreateTemplates.java
 *
 * Created on February 11, 2007, 3:46 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.templates;

import champions.Ability;
import champions.parameters.ParameterList;
import champions.Power;
import champions.exceptionWizard.ExceptionWizard;
import champions.interfaces.SpecialParameter;
import champions.powers.SpecialParameterFixedCost;
import champions.powers.SpecialParameterIsMartialManeuver;
import champions.powers.maneuverBlock;
import champions.powers.maneuverDodge;
import champions.powers.maneuverEscape;
import champions.powers.maneuverGrab;
import champions.powers.maneuverMoveby;
import champions.powers.maneuverNerveStrike;
import champions.powers.maneuverStrike;
import champions.powers.maneuverThrow;
import champions.powers.skillStandardCharBased;
import java.io.File;

/**
 *
 * @author twalker
 */
public class CreateTemplates {
    
    protected String directoryName = "templates";
    
    protected AbilityCreator[] templates = {
        //new AbilityCreator("Bribery.abt", "Bribery", skillBribery.class),
        new CharBasedSkillCreator("Bureaucratics.abt", "Bureaucratics", "PRE"),
        new AbilityCreator("Charge.abt", "Charge", maneuverMoveby.class),
        
        new CharBasedSkillCreator("Acting.abt", "Acting", "PRE"),
        new CharBasedSkillCreator("Bribery.abt", "Bribery", "PRE"),
        new CharBasedSkillCreator("Bugging.abt", "Bugging", "INT"),
        new CharBasedSkillCreator("Conversation.abt", "Conversation", "PRE"),
        new CharBasedSkillCreator("Concealment.abt", "Concealment", "INT"),
        new CharBasedSkillCreator("Contortionist.abt", "Contortionist", "DEX"),
        new CharBasedSkillCreator("CombatDriving.abt", "Combat Driving", "DEX"),
        new CharBasedSkillCreator("CombatPiloting.abt", "Combat Piloting", "DEX"),
        new CharBasedSkillCreator("ComputerProgramming.abt", "Computer Programming", "INT"),
        new CharBasedSkillCreator("Criminalogy.abt", "Criminalogy", "INT"),
        new CharBasedSkillCreator("Deduction.abt", "Deduction", "INT"),
        new CharBasedSkillCreator("Demolitions.abt", "Demolitions", "INT"),
        new CharBasedSkillCreator("Disguise.abt", "Disguise", "INT"),
        new CharBasedSkillCreator("Electronics.abt", "Electronics", "INT"),
        new CharBasedSkillCreator("FastDraw.abt", "Fast Draw", "INT"),
        new CharBasedSkillCreator("ForensicMedicine.abt", "Forensic Medicine", "INT"),
        new CharBasedSkillCreator("HighSociety.abt", "High Society", "PRE"),
        new CharBasedSkillCreator("Lockpicking.abt", "Lockpicking", "DEX"),
        new CharBasedSkillCreator("Oratory.abt", "Oratory", "PRE"),
        new CharBasedSkillCreator("Paramedics.abt", "Paramedics", "INT"),
        new CharBasedSkillCreator("SlightOfHand.abt", "Slight Of Hand", "DEX"),
        new CharBasedSkillCreator("Shadowing.abt", "Shadowing", "INT"),
        new CharBasedSkillCreator("Streetwise.abt", "Streetwise", "INT"),
        new CharBasedSkillCreator("SystemsOperation.abt", "Systems Operation", "INT"),
        new CharBasedSkillCreator("Tactics.abt", "Tactics", "INT"),
        
        // Martial maneuvers
        new ManeuverCreator("ChokeHold.abt", "Choke Hold", maneuverNerveStrike.class, true, -2, 0, 0, 4),
        new ManeuverCreator("DefensiveStrike.abt", "Defensive Strike", maneuverStrike.class, true, +1, +3, 5),
        new ManeuverCreator("Legsweep.abt", "Legsweep", maneuverThrow.class, true, 2, -1, 0, 3),
        new ManeuverCreator("KillingStrike.abt", "Killing Strike", maneuverStrike.class, true, -2, 0, 0.5, 4),
        new ManeuverCreator("MartialBlock.abt", "Martial Block", maneuverBlock.class, true, 2, 2, 0, 4),
        new ManeuverCreator("MartialDodge.abt", "Martial Dodge", maneuverDodge.class, true, 0, 5, 0, 4),
        new ManeuverCreator("MartialEscape.abt", "Martial Escape", maneuverEscape.class, true, 0, 0, 0, 4, new Object[] { "Strength", new Integer(15) }),
        new ManeuverCreator("MartialGrab.abt", "Martial Grab", maneuverGrab.class, true, -1, -1, 0, 3, new Object[] { "Strength", new Integer(10) }),
        new ManeuverCreator("MartialStrike.abt", "Martial Strike", maneuverStrike.class, true, 0, 2, 2, 4),
        new ManeuverCreator("MartialThrow.abt", "Martial Throw", maneuverThrow.class, true, 0, 1, 0, 3, new Object[] { "DC", new Double(1) }),
        new ManeuverCreator("NerveStrike.abt", "Nerve Strike", maneuverNerveStrike.class, true, -1, 1, 2, 4),
        new ManeuverCreator("OffensiveStrike.abt", "Offensive Strike", maneuverStrike.class, true, -2, 1, 4, 5),
        new ManeuverCreator("Strike.abt", "Strike", maneuverStrike.class, true, 0, 0, 0),
        // Stat based rolls...
        new CharBasedSkillCreator("CONRoll.abt", "CON Roll", "CON", 0),
        new CharBasedSkillCreator("STRRoll.abt", "STR Roll", "STR", 0),
        new CharBasedSkillCreator("DEXRoll.abt", "DEX Roll", "DEX", 0),
        new CharBasedSkillCreator("INTRoll.abt", "INT Roll", "INT", 0),
        new CharBasedSkillCreator("PERRoll.abt", "PER Roll", "INT", 0),
        new CharBasedSkillCreator("PRERoll.abt", "PRE Roll", "PRE", 0),
        new CharBasedSkillCreator("EGORoll.abt", "EGO Roll", "EGO", 0),
        
    };
    
    /** Creates a new instance of CreateTemplates */
    public CreateTemplates() {
        
        File dir = new File(directoryName);
        if ( dir.exists() == false) {
            dir.mkdir();
        }
        
        System.out.println("Creating templates in directory: " + dir.getAbsolutePath());
        
        for( AbilityCreator t : templates) {
            Ability a = t.create(dir);
            if ( a != null ) {
                System.out.println("Created " + a.getName());
            } else {
                System.out.println("ERROR: Failed to create " + t.name);
            }
        }
        System.out.println("\nFinished Creating Templates.");
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new CreateTemplates();
    }
    
    public class AbilityCreator {
        public String fileName;
        public String name;
        
        public Class powerClass;
        
        public AbilityCreator(String fileName, String name, Class powerClass) {
            this.fileName = fileName;
            this.name = name;
            this.powerClass = powerClass;
        }
        
        public Ability create(File directory) {
            
            try {
                Ability ability = create();
                
                if ( fileName != null && directory != null && directory.isDirectory() && directory.canWrite() ) {
                    File f = new File( directory.getPath() + "/" + fileName);
                    ability.save( f );
                }
                
                return ability;
            } catch(Exception e) {
                ExceptionWizard.postException(e);
            }
            return null;
        }
        
        public Ability create() throws InstantiationException, IllegalAccessException {
            Ability ability = new Ability(name);
            ability.addPAD( (Power)powerClass.newInstance(), null);
            return ability;
        }
    }
    
    public class CharBasedSkillCreator extends AbilityCreator {
        public String stat;
        
        public int fixedCost = -1;
        
        
        public CharBasedSkillCreator(String fileName, String name, String stat) {
            super(fileName,name,skillStandardCharBased.class);
            
            this.stat = stat;
        }
        
        public CharBasedSkillCreator(String fileName, String name, String stat, int fixedCost) {
            super(fileName,name,skillStandardCharBased.class);
            
            this.stat = stat;
            this.fixedCost = fixedCost;
        }
        
        public Ability create() throws InstantiationException, IllegalAccessException {
            Ability ability = super.create();
            
            ability.getPower().getParameterList(ability).setParameterValue("BaseStat", stat);
            
            if ( fixedCost != -1 ) {
                SpecialParameter sp = new SpecialParameterFixedCost();
                ability.addSpecialParameter(sp);
                int index = ability.getSpecialParameterIndex(sp);
                sp.getParameterList(ability,index).setParameterValue("FixedCPCost", fixedCost);
                
            }
            
            ability.reconfigurePower();
            
            return ability;
        }
    }
    
    public class ManeuverCreator extends AbilityCreator {
        public int dcv;
        public int ocv;
        public double dc;
        public boolean martial;
        public Object[] additionalParameters = null;
        
        public int fixedCost = -1;
        
        public ManeuverCreator(String fileName, String name, Class powerClass, boolean martial, int ocv, int dcv, double dc) {
            super(fileName, name, powerClass);
            
            this.dcv = dcv;
            this.ocv = ocv;
            this.dc = dc;
            this.martial = martial;
        }
        
        public ManeuverCreator(String fileName, String name, Class powerClass, boolean martial, int ocv, int dcv, double dc, int fixedCost) {
            this(fileName, name, powerClass, martial, ocv, dcv, dc);
            
            this.fixedCost = fixedCost;
        }
        
        public ManeuverCreator(String fileName, String name, Class powerClass, boolean martial, int ocv, int dcv, double dc, int fixedCost, Object[] additionalParameters) {
            this(fileName, name, powerClass, martial, ocv, dcv, dc, fixedCost);
            
            this.additionalParameters = additionalParameters;
        }
        
        public Ability create() throws InstantiationException, IllegalAccessException {
            Ability ability = super.create();
            
            if ( dcv != 0 ) ability.setDCVModifier(dcv);
            if ( ocv != 0 ) ability.setOCVModifier(dcv);
            if ( dc != 0 ) {
                if ( dc == Math.floor(dc) ) {
                    ability.getPower().getParameterList(ability).setParameterValue("DamageDie", ((int)dc)+"d6");
                }
                else {
                    ability.getPower().getParameterList(ability).setParameterValue("DamageDie", dc+"d6");
                }
                ability.reconfigurePower();
            }
            
            if ( martial ) {
                ability.addSpecialParameter( new SpecialParameterIsMartialManeuver() );
            }
            
            if ( fixedCost != -1 ) {
                SpecialParameter sp = new SpecialParameterFixedCost();
                ability.addSpecialParameter(sp);
                int index = ability.getSpecialParameterIndex(sp);
                sp.getParameterList(ability,index).setParameterValue("FixedCPCost", fixedCost);
                
            }
            
            if ( additionalParameters != null ) {
                ParameterList pl = ability.getPower().getParameterList(ability);
                for(int i = 0; i < additionalParameters.length; i+=2) {
                    pl.setParameterValue( (String)additionalParameters[i], additionalParameters[i+1]);
                    
                }
            }
            
            ability.reconfigure();
            
            return ability;
        }
    }
}
