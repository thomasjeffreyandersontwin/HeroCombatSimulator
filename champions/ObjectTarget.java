/*
 * ObjectTarget.java
 *
 * Created on December 17, 2000, 4:58 PM
 */

package champions;

import champions.exception.BattleEventException;
import champions.exceptionWizard.ExceptionWizard;
import champions.powers.effectDestroyed;
import champions.powers.effectPartiallyDestroyed;
import java.io.File;

/**
 *
 * @author  unknown
 * @version
 */
public class ObjectTarget extends Target {
    
    static protected String[] materials = {
        "Thin board", "Plywood", "Heavy wood", "Very heavy wood",  "Sheet metal",
        "Chain or heavy tube", "Heavy bar", "Plate", "Casting", "Hardened casting",
        "Light armor", "Medium armor", "Vault doors", "Heavy armor", "Brick",
        "Concrete", "Reinforced brick", "Reinforced concrete", "Light plastic", "Plastic castings",
        "Light fiberglass", "Heavy fiberglass", "Armored plastics", "Other"
    };
    
    // This array indexes from the materials array into the row of the wallBody array
    static int[] wallBodyIndex = {
        0, 0, 0, 0, 2,
        2, 2, 2, 2, 2,
        2, 2, 2, 2, 1,
        1, 1, 1, 3, 3,
        3, 3, 3, 3
    };
    
    
    
    static protected int materialDef[] = {
        2, 3, 4, 5, 4,
        5, 6, 7, 8, 9,
        10, 13, 16, 19, 5,
        6, 7, 8, 1, 2,
        4, 6, 8, 1
    };
    
    static protected double massAmounts[] = {
        0.8, 6.4, 25, 100, 200,
        400, 800, 1600, 3200, 6400,
        12500, 25000, 50000, 100000, 200000,
        400000, 800000, 1600000, 3200000, 6400000,
        12500000, 25000000, 50000000, 100000000,
    };
    
    static protected String massDescription[] = {
        "grenade", "assult rifle", "TV Set", "man", "piano", 
        "chaiot", "sportscar", "small car", "garbage truck", "small jet",
        "subway car", "frigate", "tank", "space shuttle", "Statue Of Liberty",
        "trawler", "drilling rig", "small bridge", "loaded freighter", "unloaded destroyer", 
        "temple", "large bridge", "medium cruise ship", "aircraft carrier"
    };
    
    static protected int thicknesses[] = {4, 8, 16, 32, 64, 125, 250, 500, 1000, 2000};
    
    static protected String[] wallMaterials = {
        "Wood", "Stone", "Metal", "Plastic", "Other"
    };
    
    static protected int wallBody[][] = {
        { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 },
        { 0, 0, 0, 1, 3, 5, 7, 9, 11, 13 },
        { 1, 3, 5, 7, 9, 11, 13, 15, 17, 19 },
        { 1, 3, 4, 6, 7, 9, 10, 12, 13, 15 },
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }
    };
    
    static protected String objectTypes[] = {"Living", "Unliving", "Complex", "Vehical", "Wall"};
    
    private String description;
    
    /** Creates new ObjectTarget */
    public ObjectTarget(String name, double mass, String objectType, String objectMaterial, int body, int defense, boolean affectedByKnockback) {
        //add("Target.NAME", name, true);
        setName(name);
        
        //add("Target.DESCRIPTION", name, true);
        setDescription(name);
        
        createCharacteristic("BODY");
        createCharacteristic("PD");
        createCharacteristic("rPD");
        createCharacteristic("ED");
        createCharacteristic("rED");
        createCharacteristic("MD");
        
        if ( "Wall".equals(objectType)) {
            
            setWallThickness(mass);
        }
        else {
            setMass(mass);
        }
        setObjectType(objectType);
        setObjectMaterial(objectMaterial);
        
        setCanBeKnockedback(affectedByKnockback);
        
        add("Target.USESHITLOCATION",  "FALSE",  true);
        add("Target.HASSENSES",  "FALSE",  true);
        add("Target.ISOBSTRUCTION", "TRUE", true);
        
        add("Target.HASDEFENSES",  "TRUE",  true);
        
        if ( defense != -1 ) {
            setBaseStat("PD",defense);
            setBaseStat("rPD",defense);
            setBaseStat("ED",defense);
            setBaseStat("MD",defense);
            setBaseStat("rED",defense);
            setCurrentStat("PD",defense);
            setCurrentStat("rPD",defense);
            setCurrentStat("ED",defense);
            setCurrentStat("rED",defense);
        }
        
        if ( body != -1 ) {
            setBaseStat("BODY",body);
            setCurrentStat("BODY",body);
        }
    }
    
    
   public ObjectTarget(String name, int body, int defense) {   
       this(name, 100, "Unliving", "Other", body, defense, false);
   }

   public ObjectTarget(String name) {
       this(name, 100, "Unliving", "Other", 7, 1, false);
   }
 
    public void editTarget() {
        ObjectEditor cd = new ObjectEditor();
        
        cd.setTarget(this);
        cd.setVisible(true);
    }
    
    public static String getRelativeMassDescription(double mass) {
        double minDiff = Double.MAX_VALUE;
        int minIndex = -1;
        
        String result;
        
        for(int i = 0; i < massAmounts.length; i++) {
            double newDiff = Math.abs(massAmounts[i] - mass);

            if ( newDiff < minDiff ) {
                minDiff = newDiff;
                minIndex = i;
            }
        }
    
        if ( mass < massAmounts[minIndex] ) {
            result = "< " + massDescription[minIndex];
        }
        else {
            int factor = (int)Math.floor(mass / massAmounts[minIndex]);
            result = "Approx. = " + factor + " " + massDescription[minIndex];
            
        }    
        
        return result;
    }
    
    public static int getObjectStartingDefense(String objectType, String material) {
        for(int i = 0; i < materials.length; i++) {
            if ( materials[i].equals(material) ) {
                return materialDef[i];
            }
        }
        return 0;
    }
    
    public static int getObjectStartingBody(double massOrThickness, String objectType, String material) {
        
        int baseBody = 0;
        
        if ( "Wall".equals(objectType)) {
            double minDiff = Double.MAX_VALUE;
            int minThickness = 0;
            
            for(int i = 0; i < thicknesses.length; i++) {
                double diff = Math.abs(thicknesses[i] - massOrThickness);
                if (diff < minDiff ) {
                    minDiff = diff;
                    minThickness = i;
                }
            }
            
            int materialIndex = materials.length - 1; // Default to last type if not found...
            for(int i = 0; i < materials.length - 1; i++) {
                if(materials[i].equals(material)) {
                    materialIndex = i;
                    break;
                }
            }
          
            baseBody = (int)Math.round(massOrThickness / thicknesses[minThickness] * wallBody[wallBodyIndex[materialIndex]][minThickness]);
        }
        else {
            baseBody = (int)Math.round( Math.log( massOrThickness / 100 ) / Math.log(2) ) + 10;
            if( objectType != null) {
                if ( objectType.equals("Unliving") ) {
                    baseBody -= 3;
                }
                else if ( objectType.equals("Complex")) {
                    baseBody -= 5;
                }
            }
        }
        
        return Math.max(0, baseBody);
    }
    
    public static int getObjectBaseKnockbackResistance(double mass) {
        int baseKB = (int)Math.round( Math.log( mass / 100 ) / Math.log(2) );
        
        return baseKB;
    }
    
    public void posteffect(BattleEvent be, Effect e) throws BattleEventException {
        int baseBody = getBaseStat("BODY");
        int currentBody = getCurrentStat("BODY");
        
        
        if ( currentBody <= -1 * baseBody ) {
            if ( hasEffect(getDeadEffectName()) == false ) {
                try {
                    if ( hasEffect("Partially Destroyed") ) {
                        Effect pd = getEffect("Partially Destroyed");
                        pd.removeEffect(be, this);
                    }
                    new effectDestroyed().addEffect( be , this);
                } catch (BattleEventException bee) {
                    be.setError( bee.toString() );
                }
            }
        } else if ( currentBody <= 0 ) {
            if ( hasEffect("Partially Destroyed") == false ) {
                try {
                    new effectPartiallyDestroyed().addEffect( be , this);
                } catch (BattleEventException bee) {
                    be.setError( bee.toString() );
                }
            }
        }
    }
    
    public String getDeadEffectName() {
        return "Destroyed";
    }
    
    public void healCompletely(BattleEvent battleEvent) {
        super.healCompletely(battleEvent);
        
        try {
            Effect e;
            if ( hasEffect("Partially Destroyed") ) {
                e = getEffect("Partially Destroyed");
                e.removeEffect(battleEvent, this);
            }
        } catch ( BattleEventException bee) {
            ExceptionWizard.postException(bee);
        }
    }
    public boolean suffersDCVPenaltyDueToSenses(){
        return false;
    }

    public double getMass() {
        Double mass = getDoubleValue("Target.MASS");
        return mass == null ? 0 : mass;
    }

    public void setMass(double mass) {
        double oldMass = getMass();
        if( mass !=  oldMass) {
            add("Target.MASS", new Double(mass), true, true);
            
            updateStartingDefenseAndBody();
        }
    }

    public String getObjectType() {
        return getStringValue("Target.OBJECTTYPE");
    }

    public void setObjectType(String type) {
        if(type != null && type.equals( getObjectType() ) == false ) {
            
            add("Target.OBJECTTYPE", type, true, true);
            
            updateStartingDefenseAndBody();
             
             if( type.equals("Living")) {
                 add("Target.ISALIVE",  "TRUE",  true);
             }
             else {
                 add("Target.ISALIVE",  "FALSE",  true);
             }
        }
    }

    public String getObjectMaterial() {
        return getStringValue("Target.OBJECTMATERIAL");
    }

    public void setObjectMaterial(String material) {
        if(material != null && material.equals(getObjectMaterial())==false ) {

            add("Target.OBJECTMATERIAL", material, true, true);
            
           updateStartingDefenseAndBody();
        }
    }
    
    public void setWallThickness(double thickness) {
        if( thickness != getWallThickness() ) {
            add("Target.WALLTHICKNESS", thickness, true, true);
            
            updateStartingDefenseAndBody();
        }
    }
    
    public double getWallThickness() {
        Double d = getDoubleValue("Target.WALLTHICKNESS");
        return d != null ? d : 0;
    }
    
    protected void updateStartingDefenseAndBody() {
        String type = getObjectType();
        String material = getObjectMaterial();
        
        double massOrThickness;
        
        if("Wall".equals(type)) {
            massOrThickness = getWallThickness();
        }
        else {
            massOrThickness = getMass();
        }
        
        int def = getObjectStartingDefense(type, material);

        setStartingStat("PD", def);
        setStartingStat("ED", def);
        setStartingStat("rPD", def);
        setStartingStat("rED", def);



        int body = getObjectStartingBody(massOrThickness, type, material);
        setStartingStat("BODY", body);
        
    }

    protected void calculateFiguredStat(String stat) {
        if ( stat == null ) return;
        if ( stat.equals("BODY")) return;
        super.calculateFiguredStat(stat);
    }
    
    protected void calculateFiguredAdjustedStat(String stat) {
        if ( stat == null ) return;
        if ( stat.equals("BODY")) return;
        super.calculateFiguredStat(stat);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if ( this.description != description ) {
            String oldDesc = this.description;
        
            this.description = description;
            firePropertyChange(this, "Target.DESCRIPTION", oldDesc, description);
        }
    }
 
}