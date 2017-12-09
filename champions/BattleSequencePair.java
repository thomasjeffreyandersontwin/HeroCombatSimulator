/*
 * BattleSequencePair.java
 *
 * Created on September 12, 2001, 7:42 AM
 */

package champions;

/**
 *
 * @author  twalker
 * @version 
 */
public class BattleSequencePair {

    public Object target;
    private Chronometer time;
	
    
    /** Creates new BattleSequencePair */
    public BattleSequencePair(Object target, Chronometer time) {
        this.target = target;
        if ( time != null ) this.time = (Chronometer)time.clone();
    }
    
    public Object getTarget() {
        return target;
    }
    
    public void setTarget(Object value) {
         target= value;
    }   
    
    
    public Chronometer getTime() {
        return time;
    }
    
    public void clear() {
        target = null;
        time = null;
    }
    
    public String toString() {
        if ( target != null ) return target.toString();
        return "BSP: No Target Set";
    }

}
