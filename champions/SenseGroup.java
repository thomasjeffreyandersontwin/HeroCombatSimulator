/*
 * SenseGroup.java
 *
 * Created on June 16, 2004, 12:49 PM
 */

package champions;

import champions.interfaces.SenseCapabilities;
import java.io.Serializable;
import java.util.Iterator;

/**
 *
 * @author  1425
 */
public class SenseGroup extends Sense
implements SenseCapabilities, Serializable {
    static final long serialVersionUID = 8012523536066525223L;
    
    /** Creates a new instance of SenseGroup */
    public SenseGroup(String groupName) {
        super(groupName);
        
        arcOfPerception = ARC_OF_PERCEPTION_120;
        concealLevel = 0;
        enhancedPerceptionLevel = 0;
        microscopicLevel = 0;
        rapidLevel = 0;
        telescopicLevel = 0;
        analyze = SENSE_FALSE;
        detect = SENSE_FALSE;
        sense = SENSE_FALSE;
        targetting = SENSE_FALSE;
        ranged = SENSE_FALSE;
        functioning = SENSE_TRUE;
        tracking = SENSE_FALSE;
        transmit = SENSE_FALSE;
    }
    
    /** Creates a new instance of SenseGroup */
    public SenseGroup(String groupName, boolean targetting, boolean ranged) {
        this(groupName);
        this.targetting = targetting ? SENSE_TRUE : SENSE_FALSE;
        this.ranged = ranged ? SENSE_TRUE : SENSE_FALSE;
    }
    
    public boolean isSenseGroup() {
        return true;
    }
    
    public boolean isSense() {
        return false;
    }
    
    public void setSenseGroup(SenseGroup senseGroup) {
        if ( senseGroup != null && senseGroup != this ) throw new IllegalArgumentException("SenseGroups can not belong to other sense groups");
    }
    
    public String getSenseGroupName() {
        return senseName;
    }
    
    public boolean isFunctioningOverridden() {
        if ( penaltyModifiers == null ) return false;
        Iterator it = penaltyModifiers.iterator();
        while(it.hasNext()) {
            SensePenaltyModifier sc = (SensePenaltyModifier) it.next();
            if ( sc.isFunctioningPenalty() == true ) return true;
        }
        return false;
    }
    
    public boolean isRangedOverridden() {
        if ( penaltyModifiers == null ) return false;
        Iterator it = penaltyModifiers.iterator();
        while(it.hasNext()) {
            SensePenaltyModifier sc = (SensePenaltyModifier) it.next();
            if ( sc.isRangedPenalty() == true ) return true;
        }
        return false;
    }
    
    public boolean isTargettingOverridden() {
        if ( penaltyModifiers == null ) return false;
        Iterator it = penaltyModifiers.iterator();
        while(it.hasNext()) {
            SensePenaltyModifier sc = (SensePenaltyModifier) it.next();
            if ( sc.isTargettingPenalty() == true ) return true;
        }
        return false;
    }
    
    public boolean isTransmitOverridden() {
        if ( penaltyModifiers == null ) return false;
        Iterator it = penaltyModifiers.iterator();
        while(it.hasNext()) {
            SensePenaltyModifier sc = (SensePenaltyModifier) it.next();
            if ( sc.isTransmitPenalty() == true ) return true;
        }
        return false;
    }
    
    public boolean equals(Object that) {
        return that != null && 
                that instanceof SenseGroup && 
                ( senseName == ((SenseGroup)that).senseName ||
                    senseName.equals( ((SenseGroup)that).senseName));
    }
    
}
