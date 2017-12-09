/*
 * BattleChangeEvent.java
 *
 * Created on January 29, 2008, 8:15 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions;

import champions.BattleChangeType;
import javax.swing.event.ChangeEvent;

/**
 *
 * @author twalker
 */
public class BattleChangeEvent extends ChangeEvent {
    
    private BattleChangeType type;
    private Object referenceObject;
    
    /** Creates a new instance of BattleChangeEvent */
    public BattleChangeEvent(Object source, BattleChangeType type, Object referenceObject) {
        super(source);
        setType(type);
        setReferenceObject(referenceObject);
    }

    public BattleChangeType getType() {
        return type;
    }

    public void setType(BattleChangeType type) {
        this.type = type;
    }

    public Object getReferenceObject() {
        return referenceObject;
    }

    public void setReferenceObject(Object referenceObject) {
        this.referenceObject = referenceObject;
    }
    
}
