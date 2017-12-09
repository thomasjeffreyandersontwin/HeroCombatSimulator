/*
 * AbilityTreeModel.java
 *
 * Created on June 11, 2001, 5:31 PM
 */

package champions.abilityTree;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.tree.*;
import treeTable.*;
import javax.swing.event.*;

import tjava.Destroyable;

import java.util.*;

import champions.interfaces.*;
/**
 *
 * @author  twalker
 * @version 
 */
public class ImportTreeTableModel extends AbilityTreeTableModel 
implements ChampionsConstants, Destroyable {
    
    /** Creates new AbilityTreeModel */
    public ImportTreeTableModel(TreeNode root) {
        super(root);
        
        columnNames = new String[]{ "Ability" }; 
    }
}
