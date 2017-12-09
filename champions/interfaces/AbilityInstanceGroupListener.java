/*
 * AbilityInstanceGroupListener.java
 *
 * Created on June 4, 2004, 1:57 AM
 */

package champions.interfaces;

import champions.event.AbilityAddedEvent;
import champions.event.AbilityRemovedEvent;
import champions.event.ActivationStateChangeEvent;
import champions.event.InstanceChangedEvent;
import java.util.EventListener;

/**
 *
 * @author  1425
 */
public interface AbilityInstanceGroupListener extends EventListener {
    /** This will be fired whenever an ability is added to the instance group.
     *
     */
    public void abilityAdded(AbilityAddedEvent evt);
    
    /** This will be fired whenever an ability is removed from the instance group.
     *
     */
    public void abilityRemove(AbilityRemovedEvent evt);
    
    /** This will be fired any time one of the instance settings change.
     *
     */
    public void instanceChanged(InstanceChangedEvent evt);
    
    /** This will be fired whenever the activation state of any ability changes.
     *
     * Any activation change of an ability in the instance group will cause
     * this message to be sent.  However, depending on the circumstances,
     * the AI_STATE_DEACTIVATED may not be fired since the ability is 
     * often removed from the instance group prior to the activation info being 
     * changed.
     */
    public void activationStateChanged(ActivationStateChangeEvent evt);
}
