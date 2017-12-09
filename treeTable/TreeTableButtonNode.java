/*
 * TreeTableButtonInterface.java
 *
 * Created on November 25, 2006, 9:38 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package treeTable;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;

/**
 *
 * @author 1425
 */
public interface TreeTableButtonNode {
    public void actionPerformed(int column, ActionEvent e);
    public boolean isColumnEnabled(int column);
    public Color getColumnColor(int column);
    public Font getColumnFont(int column);
}
