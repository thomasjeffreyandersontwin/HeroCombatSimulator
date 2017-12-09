/*
 * ATTreeTableRendererAdapter.java
 *
 * Created on January 27, 2008, 7:54 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.abilityTree2;

import java.awt.Color;
import treeTable.TreeTableRendererAdapter;

/**
 *
 * @author twalker
 */
public class ATTreeTableRendererAdapter extends TreeTableRendererAdapter {
    
    /** Creates a new instance of ATTreeTableRendererAdapter */
    public ATTreeTableRendererAdapter() {
    }

    public Color getBackgroundNonSelectionColor() {
        Color c = null;
        
        if ( getNode() instanceof ATNode ) {
            c = ((ATNode)getNode()).getBackgroundColor();
        }
        
        if ( c == null ) c = super.getBackgroundNonSelectionColor();
        
        return c;
    }
    

    
}
