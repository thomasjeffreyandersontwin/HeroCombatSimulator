/*
 * TTSelectTargetModel.java
 *
 * Created on September 13, 2005, 11:08 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package champions.targetTree;

import champions.Target;
import tjava.Filter;
import javax.swing.Icon;

/**
 *
 * @author 1425
 */
public class TTSelectTargetModel extends TTModel {
    
    /** Source who is targetting an ability.
     *
     */
    private Target source;
    
    /** Indicates the list should be flat (no folders).
     *
     * When the list is flat, some special folders are not shown.
     * Specifically, at least the recent targets folder is skipped.
     */
    private boolean flat;
    
    /** Indicates that the special targets should contain a create target option.
     *
     */
    private boolean createTargetAllowed = true;
    
    private static final String SELECT_TARGET_COLUMNS[] = { "Target", "BODY", "STUN", "Effects" };
    
    /** Creates a new instance of TTSelectTargetModel */
    public TTSelectTargetModel(TTNode root) {
        super(root, "Select Target");
        
        setDefaultVisibleColumns(SELECT_TARGET_COLUMNS);
    }
    
    public TTSelectTargetModel(Target source) {
        this(source, null);
    }
    
    public TTSelectTargetModel(Target source, Filter<Target> filter) {
        super(new TTNode(), "Select Target");
        
        setSource(source);
        setTargetFilter(filter);
        
        setDefaultVisibleColumns(SELECT_TARGET_COLUMNS);
        
        
        TTNode newRoot = new TTSelectTargetRootNode();
        setRoot(newRoot);
        newRoot.setModel(this);
    }

    public Target getSource() {
        return source;
    }

    public void setSource(Target source) {
        this.source = source;
    }

    public boolean isFlat() {
        return flat;
    }

    public void setFlat(boolean flat) {
        this.flat = flat;
    }
    
    /** Returns the leaf icon.
     *
     * We don't want icon on the leaves, so return null here.
     */
    public Icon getLeafIcon() {
        return null;
    }

    public boolean isCreateTargetEnabled() {
        return createTargetAllowed;
    }

    public void setCreateTargetEnabled(boolean createTargetNode) {
        this.createTargetAllowed = createTargetNode;
    }
    
    public void destroy() {
        super.destroy();
        
        setSource(null);
    }

    
}
