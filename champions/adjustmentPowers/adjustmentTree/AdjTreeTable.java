/*
 * PADTree.java
 *
 * Created on February 27, 2002, 11:16 PM
 */

package champions.adjustmentPowers.adjustmentTree;

import treeTable.*;
import champions.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;

/**
 *
 * @author  twalker
 * @version
 */
public class AdjTreeTable extends DefaultTreeTable {
    
    /** Creates new PADTree */
    public AdjTreeTable() {
        
      //  setupModel();
        
        this.setRootVisible(false);
        
        setupColors();
    }
    
    public void updateUI() {
        super.updateUI();
      //  setupColors();
    }
    
    protected void setupColors() {
        Color c;
        c = UIManager.getColor( "AdjustmentAttackTree.foreground" );
        if ( c != null ) this.setForeground(c);
        c = UIManager.getColor( "AdjustmentAttackTree.background" );
        if ( c != null ) this.setBackground(c);
    }
    
}
