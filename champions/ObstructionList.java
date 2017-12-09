/*
 * ObstructionList.java
 *
 * Created on January 6, 2002, 11:51 AM
 */

package champions;

import champions.interfaces.*;

/**
 *
 * @author  twalker
 * @version 
 */
public class ObstructionList extends DetailList {

    private static final long serialVersionUID = 7307723181180988299L;
    
    /** Creates new ObstructionList */
    public ObstructionList() {
        setFireChangeByDefault(false);
    }
    
    public int addObstruction(Target obstruction) {
        return addObstruction(obstruction, Integer.MAX_VALUE);
    }
    
    public int addObstruction(Target obstruction, int position) {
        int oindex = findObstruction(obstruction);
        if ( oindex == -1 ) {
                oindex = createIndexed(position, "Obstruction", "TARGET", obstruction);
                addIndexed(oindex, "Obstruction", "ACTIVE", "TRUE", true);
        }
        fireIndexedChanged("Obstruction");
        return oindex;
    }
    
    public Target getObstruction(int oindex) {
        Target t = (Target)getIndexedValue(oindex, "Obstruction", "TARGET");
        return t;
    }
    
    public boolean isActive(int oindex) {
        return getIndexedBooleanValue(oindex, "Obstruction", "ACTIVE");
    }
    
    public void setActive(int oindex, boolean active) {
        addIndexed(oindex, "Obstruction", "ACTIVE", active ? "TRUE" : "FALSE", true, true);
    }
    
    public void removeObstruction(int oindex) {
        removeAllIndexed(oindex, "Obstruction");
    }
    
    public void removeAllObstructions() {
        removeAll("Obstruction");
        fireIndexedChanged("Obstruction");
    }
    
    public int findObstruction(Target obstruction) {
        int oindex = findIndexed("Obstruction", "TARGET", obstruction);
        return oindex;
    }
    
    public void moveObstruction(int startindex, int endindex) {
        moveIndexed(startindex, endindex, "Obstruction", true);
    }

    public IndexIterator getObstructionIterator() {
        return getIteratorForIndex("Obstruction");
    }
    
    public int getObstructionCount() {
        return getIndexedSize("Obstruction");
    }
    
    public Object clone() {
        
        ObstructionList newList = new ObstructionList();
        
        int oindex;
        IndexIterator ii = getObstructionIterator();
        
        Target obstruction;
        boolean active;
        
        while (ii.hasNext() ) {
            oindex = ii.nextIndex();
            
            obstruction = getObstruction(oindex);
            active = isActive(oindex);
            
            oindex = newList.addObstruction(obstruction);
            newList.setActive(oindex, active);
        }
        
        return newList;
    }
    
    public void mergeObstructions(ObstructionList newList) {
        int oindex;
        IndexIterator ii = newList.getObstructionIterator();
        
        Target obstruction;
        boolean active;
        
        while (ii.hasNext() ) {
            oindex = ii.nextIndex();
            
            obstruction = newList.getObstruction(oindex);
            active = newList.isActive(oindex);
            
            oindex = addObstruction(obstruction);
            setActive(oindex, active);
        }
    } 
}
