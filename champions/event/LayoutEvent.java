/*
 * BattleMessageEvent.java
 *
 * Created on September 26, 2000, 4:48 PM
 */

package champions.event;

import java.util.*;
import java.awt.Container;
/**
 *
 * @author  unknown
 * @version
 */
public class LayoutEvent extends EventObject {

    /** Holds value of property type. */
    private Container parent;
    /** Creates new BattleMessageEvent */
    public LayoutEvent(Object s, Container parent) {
        super(s);
        this.parent = parent;
    }
    public Container getParent() {
        return parent;
    }
}