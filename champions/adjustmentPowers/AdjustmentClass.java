/*
 * AdjustmentClass.java
 *
 * Created on November 12, 2006, 12:06 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.adjustmentPowers;

import champions.Target;
import java.util.ArrayList;

/** Object which define a set of adjustable object.
 *
 * @author 1425
 */
public interface AdjustmentClass {
    
    /** Returns the adjustables in this class that the @param target has.
     *
     *  This should search the target for adjustable in the class.  If @param list
     *  is defined, this should add those adjustables to the list.  If list is 
     *  null, then a list should be created and returned.
     *
     *  If there are no adjustable on the target for this class, this method can 
     *  pass through @param list, even in cases where it is null.
     */
    public ArrayList<Adjustable> getAdjustablesForTarget(Target target, ArrayList<Adjustable> list);
}
