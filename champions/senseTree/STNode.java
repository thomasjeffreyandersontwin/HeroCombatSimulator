/*
 * PADTreeTableNode.java
 *
 * Created on February 28, 2002, 10:12 AM
 */

package champions.senseTree;

import champions.Target;
import champions.interfaces.AbilityFilter;
import java.awt.Color;
import java.awt.Font;
import javax.swing.tree.MutableTreeNode;
import treeTable.DefaultTreeTableNode;

/**
 *
 * @author  twalker
 * @version 
 */
public class STNode extends DefaultTreeTableNode {

    /** Creates new PADTreeTableNode */
    public STNode() {
    }
    
    public STNode(String name) {
        setUserObject(name);
    }
    
    public void add(MutableTreeNode node) {
        super.add(node);
        
        if ( node instanceof DefaultTreeTableNode ) {
            ((DefaultTreeTableNode)node).setModel( getModel() );
            ((DefaultTreeTableNode)node).setTree( getTree() );
        }
    }
    
    protected void buildChildren() {
        
    }
    
    public void setName(String name) {
        setUserObject( name );
    }
    
    /** Tells the renderer what font the node thinks is appropriate for column.
     *
     * Depending on the renderer, this hint might be used or ignored.
     * If null is returned, the default renderer font will be used.
     */
    public Font getColumnFont(int column) {
        return null;
    }
    
    /** Tells the Node that it should trigger it's default action. 
     *
     */
    public void triggerDefaultAction() {
        
    }
    
    /** Tells the renderer/editor what color the node thinks is appropriate for the column.
     *
     */
    public Color getColumnColor(int column) {
        return null;
    }
    
    /** Returns the Tool text for the node. */
    public String getToolTipText() {
        return null;
    }
    
    /** Returns whether the node should be expanded by default.
     *
     */
    public boolean expandByDefault() {
        return true;
    }
    
    /** Indicates the node is enabled.
     *
     * This dictates both the renderer look and if actions are taken when 
     * the editor is clicked on...
     */
    public boolean isEnabled() {
        return true;
    }
    
    /** Tells the node to update it's information and the information of it children.
     *
     * This typically will involve only cosmetic changes and not structural 
     * changes.  Use it to update cached values that need to change after 
     * ability's have been activated/deactivate or the battleEngine has done
     * some processing.
     */
    public void updateNode() {
        // Just try updating the children
        int count = getChildCount();
        for(int i = 0; i < count; i++) {
            STNode node = (STNode)getChildAt(i);
            node.updateNode();
        }
    }
}
