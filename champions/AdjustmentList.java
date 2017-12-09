/*
 * AdjustmentList.java
 *
 * Created on March 6, 2002, 10:26 PM
 */

package champions;

import champions.adjustmentPowers.Adjustable;

/**
 *
 * @author  Trevor Walker
 * @version
 */
public class AdjustmentList extends DetailList {
    
    /** Creates new AdjustmentList */
    public AdjustmentList(Target target) {
        setAdjustableTarget(target);
    }
    
    public int getAdjustableCount() {
        return getIndexedSize("Adjustable");
    }
    
    public int getAdjustableIndex(Adjustable adjustable) {
        return findIndexed("Adjustable", "ADJUSTABLE", adjustable);
    }
    
    public int addAdjustable(Object adjustable, int percentage) {
        int index = findIndexed("Adjustable", "ADJUSTABLE", adjustable);
        if ( index == -1 ) {
            index = createIndexed("Adjustable", "ADJUSTABLE", adjustable, false);
        }
        addIndexed(index, "Adjustable", "PERCENTAGE", new Integer(percentage), true, false);
        
        fireIndexedChanged("Adjustable");
        
        return index;
    }
    
    public Adjustable getAdjustableObject(int index) {
        return (Adjustable)getIndexedValue(index, "Adjustable", "ADJUSTABLE");
    }
    
    public Target getAdjustableTarget() {
        return (Target)getValue("Adjustable.TARGET");
    }
    
    public void setAdjustableTarget(Target target) {
        add("Adjustable.TARGET", target, true);
    }
    
    public int getAdjustablePercentage(int index) {
        Integer i = getIndexedIntegerValue(index, "Adjustable", "PERCENTAGE");
        return ( i == null) ? 0 : i.intValue();
    }
    
    public void removeAdjustable(Adjustable adjustable) {
        int index = findIndexed("Adjustable", "ADJUSTABLE", adjustable);
        if ( index != -1 ) {
            removeAllIndexed(index, "Adjustable");
        }
    }
    
    public void removeAllAdjustables() {
        removeAll("Adjustable");
        fireIndexedChanged("Adjustable");
    }
    
    public Object clone() {
        AdjustmentList al = new AdjustmentList(getAdjustableTarget());
        int index, count;
        
        count = getAdjustableCount();
        for(index = 0; index < count; index++) {
            al.addAdjustable( getAdjustableObject(index), getAdjustablePercentage(index));
        }
        return al;
    }
    
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("AdjustmentList [Target: ");
        sb.append( getAdjustableTarget() );
        sb.append( ", Adjustables: ");
        int index = getAdjustableCount() - 1;
        for(;index >= 0; index--) {
            sb.append( getAdjustableObject(index) );
            sb.append( "/" );
            sb.append( getAdjustablePercentage(index) );
            sb.append( "%" );
            if ( index != 0 ) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
    
    /*public void mergeAdjustable(AdjustmentList newList, Target target) {
        boolean found;
        int ocount = getAdjustableCount();
        int ncount = newList.getAdjustableCount();
        
        int oindex, nindex;
        
        // Premark the old list
        oindex = 0;
        for(; oindex < ocount; oindex++) {
            if ( getAdjustableTarget(oindex) == target ) {
                addIndexed(oindex, "Adjustable", "REMOVEME", "TRUE");
            }
        }
        
        // Run through the new list, adding/copying all new Adjustables
        for(nindex = 0; nindex < ncount; nindex++) {
            if ( newList.getAdjustableTarget(nindex) == target ) {
                found = false;
                Object adjustable = newList.getAdjustableObject(nindex);
                
                // Check for this adjustable in the old list
                for(oindex = 0; oindex < ocount; oindex++) {
                    if ( getAdjustableTarget(oindex) == target && getAdjustableObject(oindex).equals(adjustable) ) {
                        addIndexed(oindex, "Adjustable", "REMOVEME", "FALSE");

                        addIndexed(oindex, "Adjustable", "PERCENTAGE", new Integer(newList.getAdjustablePercentage(nindex)), true, false);
                        found = true;
                        break;
                    }
                }
                
                if ( found == false ) {
                    // The adjustable isn't in the old list, so add it...
                    addAdjustable(adjustable, target, newList.getAdjustablePercentage(nindex));
                }
            }
        }

        // Scan old list for removeme entries...
        oindex = 0;
        for(; oindex < ocount; oindex++) {
            if ( getAdjustableTarget(oindex) == target && getIndexedBooleanValue(oindex, "Adjustable", "REMOVEME") ) {
                removeAllIndexed(oindex, "Adjustable");
                oindex --;
                ocount --;
            }
        }
    } */
    
}
