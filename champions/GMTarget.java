/*
 * GMTarget.java
 *
 * Created on March 20, 2004, 12:19 PM
 */

package champions;

/**
 *
 * @author  1425
 */
public class GMTarget extends Target {
    
    /** Holds value of property statNames. */
    //"MD", added by PR
    static public String[] statNames = {"STR","CON","DEX","BODY","INT","EGO","PRE","COM","PD",
    "ED","SPD","REC","END","STUN","MD","rPD","rED"/*, "OCV", "DCV", "ECV"*/};
    
    //0 added for MD by PR
    int stats[] = new int[] {0,0,100,100,100,100,100,100,200,200,12,400,200,200,100,100,100/*,3,3,3*/};
    
    public String fileExtension = "hcs";
    
    /** Creates new Character */
    public GMTarget(String inName) {
        this();
        setName(inName);
        
    }
    
    /** Creates new Character */
    public GMTarget() {
        setName("The GM");
        
        int i;
        for (i=0;i<stats.length;i++) {
            createCharacteristic(statNames[i]);
            setBaseStat(statNames[i],stats[i]);
            setAdjustedStat(statNames[i],stats[i],true);
            setCurrentStat(statNames[i],stats[i]);
        }
        
        addDefaultSenses();
        addDefaultClassesOfMind();
        
        setCombatState(CombatState.STATE_FIN);
    }
    
    public void addDefaultSenses() {
        addSense( PADRoster.getNewSense("Normal Sight"));
        addSense( PADRoster.getNewSense("Normal Smell"));
        addSense( PADRoster.getNewSense("Normal Taste"));
        addSense( PADRoster.getNewSense("Normal Hearing"));
        addSense( PADRoster.getNewSense("Normal Touch"));
    }
    
    public void addDefaultClassesOfMind() {
        addClassOfMind("Human");
    }
    
}
