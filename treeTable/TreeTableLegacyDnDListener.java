/*
 * TreeTableLegacyDnDListener.java
 *
 * Created on February 28, 2002, 9:23 AM
 */

package treeTable;

import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTargetListener;
import java.io.Serializable;
/**
 *
 * @author  twalker
 * @version 
 */
public interface TreeTableLegacyDnDListener extends DropTargetListener, DragGestureListener, DragSourceListener, Serializable {

}

