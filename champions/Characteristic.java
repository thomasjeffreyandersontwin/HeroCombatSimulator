/*
 * Characteristic.java
 *
 * Created on March 3, 2002, 11:06 AM
 */

package champions;

import champions.adjustmentPowers.Adjustable;
import champions.adjustmentPowers.AdjustmentClass;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 *
 * @author  twalker
 * @version 
 */
public class Characteristic implements Serializable, AdjustmentClass, Adjustable {
    static final long serialVersionUID = -4330957345362846705L;
    
    static final public String[] characteristicNames = {
        "DEX","SPD","STUN","CON","BODY","PD",
        "ED","STR","REC","END","INT","EGO","PRE",
        "COM", "MD","rPD","rED","POWERDEFENSE"
    };
    

    private static Hashtable cpCost;
    private static Hashtable statBase;
    
    static {
        //System.out.println( "Loading Hashtables" );
        cpCost = new Hashtable();
        cpCost.put( "STR", new Double(1) );
        cpCost.put( "DEX", new Double(3) );
        cpCost.put( "CON", new Double(2) );
        cpCost.put( "BODY",new Double(2) );
        cpCost.put( "INT", new Double(1) );
        cpCost.put( "EGO", new Double(2) );
        cpCost.put( "PRE", new Double(1) );
        cpCost.put( "COM", new Double(0.5) );
        cpCost.put( "PD", new Double(1) );
        cpCost.put( "ED", new Double(1) );
        cpCost.put( "SPD", new Double(10) );
        cpCost.put( "REC", new Double(2) );
        cpCost.put( "END", new Double(0.5) );
        cpCost.put( "STUN", new Double(1) );
        //1 line added by PR
        cpCost.put( "MD", new Double(1) );
        //System.out.println("Hashtable: " + cpCost.toString() );
        
        statBase = new Hashtable();
        statBase.put( "STR", new Integer(10) );
        statBase.put( "DEX", new Integer(10) );
        statBase.put( "CON", new Integer(10) );
        statBase.put( "BODY",new Integer(10) );
        statBase.put( "INT", new Integer(10) );
        statBase.put( "EGO", new Integer(10) );
        statBase.put( "PRE", new Integer(10) );
        statBase.put( "COM", new Integer(10) );
        statBase.put( "PD", new Integer(-1) );
        statBase.put( "ED", new Integer(-1) );
        statBase.put( "SPD", new Integer(-1) );
        statBase.put( "REC", new Integer(-1) );
        statBase.put( "END", new Integer(-1) );
        statBase.put( "STUN", new Integer(-1) );
        //1 line added by PR
        statBase.put( "MD", new Integer(-1) );
    }
    /** Holds value of property name. */
    protected String name;
    
    /** Creates new Characteristic */
    public Characteristic(String name) {
        setName(name);
    }

    /** Getter for property name.
     * @return Value of property name.
     */
    public String getName() {
        return name;
    }
    
    /** Setter for property name.
     * @param name New value of property name.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    public boolean equals(Object that) {
        return (that instanceof Characteristic && this.getName().equals(((Characteristic)that).getName()));
    }
    
    public String toString() {
        return name;
    }
    
    static public double getCPperPoint(String stat) {
        if ( cpCost.containsKey( stat ) ) {
            return ((Double)cpCost.get(stat)).doubleValue();
        }
        else {
            return -1;
        }
    }
    
    static public double getStartingValue(String stat) {
        if ( statBase.containsKey( stat ) ) {
            return ((Integer)statBase.get(stat)).intValue();
        }
        else {
            return -1;
        }
    }
    
    static public CharacteristicPrimary createCharacteristic(String name, boolean useNormalCharacteristicMaximum) {
        if ( name.equals("SPD") ) {
            CharacteristicSpeed c = new CharacteristicSpeed();
            c.setUseNormalCharacteristicMaximum(useNormalCharacteristicMaximum);
            return c;
        }
        else if( getStartingValue(name) != -1 ) {
            CharacteristicPrimary c = new CharacteristicPrimary(name, getStartingValue(name), getCPperPoint(name));
            c.setUseNormalCharacteristicMaximum(useNormalCharacteristicMaximum);
            return c;
        }
        else {
            CharacteristicFigured c = new CharacteristicFigured(name, getCPperPoint(name));
            c.setUseNormalCharacteristicMaximum(useNormalCharacteristicMaximum);
            return c;
        }
    }

    public ArrayList<Adjustable> getAdjustablesForTarget(Target target, ArrayList<Adjustable> list) {
        if ( target.hasStat( this.getName() ) ) {
            if ( list == null ) list = new ArrayList();
            list.add(this);
        }
        return list;
    }
    
    static public int getCharacteristicIndex(String name) {
        for(int i = 0; i < characteristicNames.length; i++) {
            if(characteristicNames[i].equals(name)) return i;
        }
        return -1;
    }
    
}
