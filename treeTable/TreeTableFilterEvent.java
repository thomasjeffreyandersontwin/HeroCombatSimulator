/*
 * TreeTableFilterEvent.java
 *
 * Created on January 20, 2008, 3:47 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package treeTable;

import java.awt.Event;


/**
 *
 * @author twalker
 */
public class TreeTableFilterEvent extends Event{

    public static final int FILTER_CLEARED = 0;
    public static final int FILTER_COMMITTED = 1;
    public static final int FILTER_MODIFIED = 2;

    public TreeTableFilterEvent(Object target, int id, Object filterObject) {
        super(target, id, filterObject);
    }

    public Object getFilterObject() {
        return arg;
    }

}
