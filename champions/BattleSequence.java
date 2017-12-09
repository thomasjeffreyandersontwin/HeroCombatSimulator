/*
 * BattleSequence.java
 *
 * Created on September 12, 2001, 6:40 AM
 */

package champions;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author  twalker
 * @version 
 */
public class BattleSequence {

    private List<BattleSequencePair> pairs = new ArrayList<BattleSequencePair>();
    /** Creates new BattleSequence */
   // private BattleSequencePair[] pairs;
    //private Chronometer[] segments;
   // private int size; // Number of element in BattleSequence current
   // private int length; // Length of allocated arrays.
    
   // static private int initialSize = 20;
   // static private int blockSize = 20;
    
    public BattleSequence() {
       // pairs = new BattleSequencePair[initialSize];
        //segments = new Chronometer[initialSize];
       // length = initialSize;
       // size = 0;
    }
    
//    private void expandArray(int increase) {
//        if ( increase <= 0 ) throw new ArrayIndexOutOfBoundsException(increase);
//        
//        BattleSequencePair[] newPairs = new BattleSequencePair[length + increase];
//       // Chronometer[] newSegments = new Chronometer[length + increase];
//        
//        int index;
//        for(index = size -1; index >=0; index -- ) {
//            newPairs[index] = pairs[index];
//            //newSegments[index] = segments[index];
//            pairs[index] = null;
//        }
//        
//        pairs = newPairs;
//       // segments = newSegments;
//    }
    
    public void add(Object target, Chronometer segment) {
       // if ( size >= length ) expandArray(blockSize);
             
        pairs.add( new BattleSequencePair(target, segment) );
        //size++;
    }
    
    public void clear() {
        pairs.clear();
//        int index;
//        for(index = size -1; index >=0; index -- ) {
//            pairs[index].clear();
//            pairs[index] = null;
//            //segments[index] = null;
//        }
//        size = 0;
    }
    
    public BattleSequencePair elementAt(int position) {
        //if ( position < 0 || position >= size ) throw new ArrayIndexOutOfBoundsException(position);
        
        return pairs.get(position);
    }
    
    public BattleSequencePair get(int position) {
        return pairs.get(position);
    }
    
    public boolean containsTarget(Target target) {
        for( BattleSequencePair bsp : pairs ) {
        //for ( int index = size -1; index >= 0; index--) {
            if ( bsp.getTarget() == target ) return true;
        }
        return false;
    }
    
   /* public Chronometer getSegment(int position) {
        if ( position < 0 || position >= size ) throw new ArrayIndexOutOfBoundsException(position);
        
        return segments[position];
    } */
    
    public int size() {
        return pairs.size();
    }
    
    public void set(int index, BattleSequencePair bsp) {
        //if ( index < 0 || index > size ) throw new ArrayIndexOutOfBoundsException(index);
       
        // For conviencence allow the very last index to exceed size by one, causing an
        // array expansion.
        
        if ( bsp == null ) {
            throw new NullPointerException();
        }
        
        if ( index == pairs.size() ) {
            pairs.add(bsp);
        }
        else {
            pairs.set(index,bsp);
        }
    }


}
