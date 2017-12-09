/*
 * Character.java
 *
 * Created on September 10, 2000, 3:49 PM
 */

package champions;

import champions.exception.BattleEventException;
import champions.importWizard.ImportWizard;
import champions.interfaces.AbilityList;
import champions.powers.effectDead;
import champions.powers.powerLeaping;
import champions.powers.powerRunning;
import champions.powers.powerSwimming;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author  unknown
 * @version
 */
public class Character extends Target implements Serializable {
    static final long serialVersionUID = 4512644525470297245L;
    
    
    /** Holds value of property statNames. */
    //"MD", added by PR
    static public String[] statNames = {"STR","CON","DEX","BODY","INT","EGO","PRE","COM","PD",
    "ED","SPD","REC","END","STUN","MD","rPD","rED"/*, "OCV", "DCV", "ECV"*/};
    
    //0 added for MD by PR
    int stats[] = new int[] {10,10,10,10,10,10,10,10,2,2,2,4,20,20,0,0,0/*,3,3,3*/};
    
    public String fileExtension = "hcs";
    
    /** Creates new Character */
    public Character(String inName) {
        this();
        setName(inName);
        //add("Target.CLASSOFMIND", "Human", true, true);
        
    }
    
    /** Creates new Character */
    public Character() {
        setName("New Character");
        
        int i;
        for (i=0;i<stats.length;i++) {
            createCharacteristic(statNames[i]);
        }
        
        for(i = 0; i < stats.length; i++ ) {    
            setBaseStat(statNames[i],stats[i]);
            setAdjustedStat(statNames[i],stats[i],true);
            setCurrentStat(statNames[i],stats[i]);
        }
        
        addDefaultAbilities();
        addDefaultSenses();
        addDefaultClassesOfMind();
        
        setCombatState(CombatState.STATE_FIN);
    }
    
    public void addDefaultAbilities() {
        AbilityList powers, skills, disadvantages, equipment, talents, perks;
        
        powers = this.findSublist("Powers");
        skills = this.findSublist("Skills");
        disadvantages = this.findSublist("Disadvantages");
        talents = this.findSublist("Talents");
        perks = this.findSublist("Perks");
        equipment = this.findSublist("Equipment");
        
        if ( powers == null ) {
            powers = createSublist("Powers",null);
        }
        
        if ( skills == null ) {
            skills = createSublist("Skills",null);
        }

        if ( disadvantages == null ) {
            disadvantages = createSublist("Disadvantages",null);
        }
        
        if ( talents == null ) {
            talents = createSublist("Talents",null);
        }
        
        if ( perks == null ) {
            perks = createSublist("Perks",null);
        }
        
        if ( equipment == null ) {
            disadvantages = createSublist("Equipment",null);
        }
        
        Ability STRRoll = PADRoster.getSharedAbilityInstance("STR Roll");
        
        if ( this.hasAbility(STRRoll) == false ) {
            Ability newSTRRoll = PADRoster.getNewAbilityInstance("STR Roll");
            skills.addAbility(newSTRRoll);
        }
        
        Ability DEXRoll = PADRoster.getSharedAbilityInstance("DEX Roll");
        
        if ( this.hasAbility(DEXRoll) == false ) {
            Ability newDEXRoll = PADRoster.getNewAbilityInstance("DEX Roll");
            skills.addAbility(newDEXRoll);
        }
        
        Ability CONRoll = PADRoster.getSharedAbilityInstance("CON Roll");
        
        if ( this.hasAbility(CONRoll) == false ) {
            Ability newCONRoll = PADRoster.getNewAbilityInstance("CON Roll");
            skills.addAbility(newCONRoll);
        }
        
        
        Ability INTRoll = PADRoster.getSharedAbilityInstance("INT Roll");
        
        if ( this.hasAbility(INTRoll) == false ) {
            Ability newINTRoll = PADRoster.getNewAbilityInstance("INT Roll");
            skills.addAbility(newINTRoll);
        }
        
        Ability PERRoll = PADRoster.getSharedAbilityInstance("PER Roll");
        
        if ( this.hasAbility(PERRoll) == false ) {
            Ability newPERRoll = PADRoster.getNewAbilityInstance("PER Roll");
            skills.addAbility(newPERRoll);
        }
        
        //        Ability PERRoll = PADRoster.getPowerInstance("PER Roll");
        //
        //        if ( this.hasAbility(PERRoll) == false ) {
        //            Ability newPERRoll = PADRoster.newPowerInstance("PER Roll");
        //            skills.addAbility(newPERRoll);
        //        }
        
        Ability EGORoll = PADRoster.getSharedAbilityInstance("EGO Roll");
        
        if ( this.hasAbility(EGORoll) == false ) {
            Ability newEGORoll = PADRoster.getNewAbilityInstance("EGO Roll");
            skills.addAbility(newEGORoll);
        }
        Ability PRERoll = PADRoster.getSharedAbilityInstance("PRE Roll");
        
        if ( this.hasAbility(PRERoll) == false ) {
            Ability newPRERoll = PADRoster.getNewAbilityInstance("PRE Roll");
            skills.addAbility(newPRERoll);
        }
        
        Ability running = new Ability("Running");
        
        running.addPAD( new powerRunning(), null);
        running.getPowerParameterList().setParameterValue("Base", true);
        running.reconfigurePower();
        if ( this.hasAbility(running) == false ) {
            powers.addAbility(running);
        }
        
        Ability swimming = new Ability("Swimming");
        
        swimming.addPAD( new powerSwimming(), null);
        swimming.getPowerParameterList().setParameterValue("Base", true);
        swimming.reconfigurePower();
        if ( this.hasAbility(swimming) == false ) {
            powers.addAbility(swimming);
        }
        
        Ability leaping = new Ability("Leaping");
        
        leaping.addPAD( new powerLeaping(), null);
        leaping.getPowerParameterList().setParameterValue("Base", true);
        leaping.reconfigurePower();
        if ( this.hasAbility(leaping) == false ) {
            powers.addAbility(leaping);
        }
        
    }
    
    public void addDefaultSenses() {
        addSense( PADRoster.getNewSense("Normal Sight"));
        addSense( PADRoster.getNewSense("Normal Hearing"));
        addSense( PADRoster.getNewSense("Normal Smell"));
        addSense( PADRoster.getNewSense("Normal Taste"));
        addSense( PADRoster.getNewSense("Normal Touch"));
    }
    
    public void addDefaultClassesOfMind() {
        addClassOfMind("Human");
    }
    
    
    static public int getStatCount() {
        return statNames.length;
    }
    
    static public String getStatName(int index) {
        return statNames[index];
    }
    
    public String getFileExtension() {
        return "hcs";
    }
    
    public void posteffect(BattleEvent be, Effect e) {
        int baseBody = getBaseStat("BODY");
        int currentBody = getCurrentStat("BODY");
        
        if ( isAlive() && currentBody <= -1 * baseBody ) {
            try {
                new effectDead().addEffect( be , this);
            }
            catch (BattleEventException bee) {
                be.setError( bee.toString() );
            }
        }
        else if ( isDead() && currentBody > -1 * baseBody ) {
            try {
                removeEffect(be, getDeadEffectName());
            }
            catch (BattleEventException bee) {
                be.setError( bee.toString() );
            }
        }
    }
    
    public String getEyeColor() {
        return getStringValue("Target.EYECOLOR");
    }
    
    public void setEyeColor(String color){
        add("Target.EYECOLOR", color, true, false);
    }
    
    public String getHairColor() {
        return getStringValue("Target.HAIRCOLOR");
    }
    
    public void setHairColor(String color){
        add("Target.HAIRCOLOR", color, true, false);
    }
    
    /** Returns an array list of the Target Options.
     *
     * The Target Options are the miscellaneous options that apply to only
     * certain types of targets.  Generally, you should call the super method
     * to get the base array list and then add your own TargetOptions objects
     * to it.
     *
     * Only instances of classes supporting the TargetOptions interface should
     * be added to the returned array list.
     */
    public ArrayList getTargetOptions() {
        ArrayList options = super.getTargetOptions();
        
        options.add( new CharacterMiscOptions() );
        options.add( new CharacterMindClassOptions() );
        
        return options;
    }
    
    static public void importCharacter(Character existingCharacter) {
        //   Wizard wizard = new Wizard("Character Import Wizard", new champions.importWizard.DestinationPanel());
        //   wizard.setSize(400,400);
        //  wizard.setVisible(true);
        new ImportWizard();
    }
}