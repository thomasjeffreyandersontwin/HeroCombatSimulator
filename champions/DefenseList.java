/*
 * CVList.java
 *
 * Created on November 13, 2000, 7:01 PM
 */

package champions;

import champions.enums.DefenseType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author  unknown
 * @version
 */
public class DefenseList implements Serializable {
    static final long serialVersionUID = 23940261410791118L;
    
    protected String name;
    protected List<DefenseListEntry> defenseEntries = new ArrayList<DefenseListEntry>();
    protected List<AdjustmentEntry> adjustmentEntries;
    
    /** Creates new CVList */
    public DefenseList() {
    }
    
    public void setName( String name ) {
        //add("Target.NAME",  name ,  true, false);
        this.name = name;
    }
    
    public String getName() {
        //return getStringValue("Target.NAME");
        return name;
    }
    
    /** Removes all defense and adjustment entries from defense list.
     *
     */
    public void clear() {
        defenseEntries = new ArrayList<DefenseListEntry>();
        adjustmentEntries = null;
    }
    
    public void setDefenseBase(DefenseType def, int base) {
        // add (   "Base" + ".BASECV", new Integer(base) , true, false);
        addDefenseModifier(def, "Target Base", base);
    }
    
    public int findDefenseEntry(String description) {
        if ( description != null ) {
            for(int i = 0; i < defenseEntries.size(); i++) {
                DefenseListEntry de = defenseEntries.get(i);
                if ( description.equals( de.getDescription() ) ) return i;
            }
        }
        return -1;
    }
    
    public int addDefenseModifier(DefenseType defenseType, String desc, int value ) {

        DefenseListEntry me = null;
        int index = findDefenseEntry(desc);
        
        if ( index == -1 ) {
            me = new DefenseListEntry(desc);
            defenseEntries.add(me);
            index = defenseEntries.size() - 1;
        }
        else {
            me = defenseEntries.get(index);
        }
        
        me.setModifier(defenseType, value);
        
        return index;
    }
    
    public int getDefenseModifier(DefenseType defenseType, String desc) {
        int index = findDefenseEntry(desc);
        if ( index != -1 ) {
            DefenseListEntry me = defenseEntries.get(index);
            return me.getModifier(defenseType);
        }
        return 0;
    }
    
    public int addDefenseMultiplier(DefenseType defenseType, String desc, double value ) {
        DefenseListEntry me = null;
        int index = findDefenseEntry(desc);
        
        if ( index == -1 ) {
            me = new DefenseListEntry(desc);
            defenseEntries.add(me);
            index = defenseEntries.size() - 1;
        }
        else {
            me = defenseEntries.get(index);
        }
        
        me.setMultiplier(defenseType, value);
        
        return index;
    }
    
    public String getDefenseDescription(int dindex) {
        return defenseEntries.get(dindex).getDescription();
    }
    
    public void setHardenedLevel(String desc, int hardenedLevels) {
        DefenseListEntry me = null;
        int index = findDefenseEntry(desc);
        
        if ( index == -1 ) {
            me = new DefenseListEntry(desc);
            defenseEntries.add(me);
            index = defenseEntries.size() - 1;
        }
        else {
            me = defenseEntries.get(index);
        }
        
        me.setHardenedLevel(hardenedLevels);
    }
    
    public int getHardenedLevel(int dindex) {
        return defenseEntries.get(dindex).getHardenedLevel();
    }
    
    public int getDefenseCount() {
        return defenseEntries.size();
    }
    
    public int addAdjustmentMultiplier(String desc, Object modifier, double multiplier) {
        int index = findAdjustmentIndex(desc, modifier);
        if ( index == -1 ) {
            AdjustmentEntry ae = new AdjustmentEntry(desc, modifier, multiplier);
            if ( adjustmentEntries == null ) adjustmentEntries = new ArrayList<AdjustmentEntry>();
            adjustmentEntries.add(ae);
            index = adjustmentEntries.size() - 1;
        }
        else {
            AdjustmentEntry ae = adjustmentEntries.get(index);
            ae.setMultiplier(multiplier);
        }
        
        return index;
    }
    
    public int findAdjustmentIndex(String desc, Object modifier) {
        if ( adjustmentEntries != null ) {
            for(int i = 0; i < adjustmentEntries.size(); i++) {
                AdjustmentEntry ae = adjustmentEntries.get(i);
                if( desc.equals(ae.getDescription()) && ae.getModifier() == modifier) return i;
            }
        }
        
        return -1;
    }
    
    public double getTotalAdjustmentMultiplier(String desc) {
        double total = 1;
        
        if ( adjustmentEntries != null ) {
            for(int i = 0; i < adjustmentEntries.size(); i++) {
                AdjustmentEntry ae = adjustmentEntries.get(i);
                if( desc.equals(ae.getDescription()) ) {
                    total *= ae.getMultiplier();
                }
            }
        }
        
        return total;
    }
    
    public DefenseListEntry getDefenseListEntry(int index) {
        return defenseEntries.get(index);
    }
    
    
   /* public int addDefenseSet(String defense, String desc, int value ) {
        int index = createIndexed(   defense, "TYPE", "SET" , false) ;
        addIndexed(index,  defense, "DESCRIPTION", desc, true, false);
        addIndexed(index,  defense, "VALUE", new Integer(value), true, false);
        addIndexed(index,  defense, "ACTIVE", "TRUE", true, false);
        return index;
    } */
    
    public int getTotalDefenseModifier(DefenseType defense) {
        int finalDefense = 0;
        int count, index;
        boolean active;
        Object value;
        String type;
        double adjustmentMultiplier;
        String description;
        
        //count = getIndexedSize( "Defense" );
        count = defenseEntries.size();
        
        // Find the modifiers...
        for(index=0;index<count;index++) {
            DefenseListEntry de = defenseEntries.get(index);
            
            active = de.isActive();
            if ( active ) {
                description = de.getDescription();
                adjustmentMultiplier = getTotalAdjustmentMultiplier(description);
                
                finalDefense += Math.round( de.getModifier(defense) * adjustmentMultiplier ) ;
            }
        }
        
        return finalDefense;
    }
    
    public double getTotalDefenseMultiplier(DefenseType defense) {
        double finalDefense = 1;
        int count, index;
        boolean active;
        Double value;
        String type;
        
        count = defenseEntries.size();
        
        // Find the modifiers...
        for(index=0;index<count;index++) {
            DefenseListEntry de = defenseEntries.get(index);
            active = de.isActive();
            if ( active ) {
                finalDefense *= de.getMultiplier(defense);
            }
        }
        
        return finalDefense;
    }
    
    public static class AdjustmentEntry implements Serializable {
        protected String description;
        protected Object modifier;
        protected double multiplier;
        
        protected AdjustmentEntry(String description, Object modifier, double multiplier) {
            this.description = description;
            this.modifier = modifier;
            this.multiplier = multiplier;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Object getModifier() {
            return modifier;
        }

        public void setModifier(Object modifier) {
            this.modifier = modifier;
        }

        public double getMultiplier() {
            return multiplier;
        }

        public void setMultiplier(double multiplier) {
            this.multiplier = multiplier;
        }
    }
    
}