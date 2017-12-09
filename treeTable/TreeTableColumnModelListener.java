/*
 * @(#)TreeTableColumnModelListener.java	1.12 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package treeTable;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ChangeEvent;
import java.util.EventListener;

/**
 * TreeTableColumnModelListener defines the interface for an object that listens
 * to changes in a TableColumnModel.
 *
 * @version 1.12 01/23/03
 * @author Alan Chung
 * @see TableColumnModelEvent
 */

public interface TreeTableColumnModelListener extends EventListener
{
    /** Tells listeners that a column was added to the model.
     * @param e 
     */
    public void columnAdded(TreeTableColumnModelEvent e);

    /** Tells listeners that a column was removed from the model.
     * @param e
     */
    public void columnRemoved(TreeTableColumnModelEvent e);

    /** Tells listeners that a column was repositioned.
     * @param e
     */
    public void columnMoved(TreeTableColumnModelEvent e);

    /** Tells listeners that a column was moved due to a margin change.
     * @param e
     */
    public void columnMarginChanged(ChangeEvent e);

    /**
     * Tells listeners that the selection model of the
     * TableColumnModel changed.
     * @param e
     */
    public void columnSelectionChanged(ListSelectionEvent e);

    /**
     * Tells listeners that the sort column or direction changed.
     * @param e
     */
    public void columnSorted(TreeTableColumnModelEvent e);
}

