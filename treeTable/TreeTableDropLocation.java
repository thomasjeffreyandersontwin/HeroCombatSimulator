/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package treeTable;

import java.awt.Point;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

/**
 *
 * @author twalker
 */
public class TreeTableDropLocation {

    private Point dropPoint;

    private TreePath path;

    private int childIndex;

    public TreeTableDropLocation(JTree.DropLocation dropLocation) {
        dropPoint = dropLocation.getDropPoint();
        path = dropLocation.getPath();
        childIndex = dropLocation.getChildIndex();
    }

    public TreeTableDropLocation(Point dropPoint, TreePath path, int childIndex) {
        this.dropPoint = dropPoint;
        this.path = path;
        this.childIndex = childIndex;
    }

    /**
     * @return the dropPoint
     */
    public Point getDropPoint() {
        return dropPoint;
    }

    /**
     * @param dropPoint the dropPoint to set
     */
    public void setDropPoint(Point dropPoint) {
        this.dropPoint = dropPoint;
    }

    /**
     * @return the path
     */
    public TreePath getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(TreePath path) {
        this.path = path;
    }

    /**
     * @return the childIndex
     */
    public int getChildIndex() {
        return childIndex;
    }

    /**
     * @param childIndex the childIndex to set
     */
    public void setChildIndex(int childIndex) {
        this.childIndex = childIndex;
    }

    public String toString() {
        return getClass().getName() + "[dropPoint=" + getDropPoint() + "," + "path=" + getPath() + "," + "childIndex=" + getChildIndex() + "]";
    }
}
