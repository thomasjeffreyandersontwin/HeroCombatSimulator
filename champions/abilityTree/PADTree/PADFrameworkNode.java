/*
 * PADAbilityNode.java
 *
 * Created on February 27, 2002, 11:43 PM
 */

package champions.abilityTree.PADTree;

import champions.Ability;
import champions.DefaultAbilityList;
import tjava.ObjectTransferable;
import champions.abilityTree.AbilityListNode;
import champions.abilityTree.AbilityTreeNode;
import champions.abilityTree.AbilityTreeTable;
import champions.exceptionWizard.ExceptionWizard;
import champions.interfaces.AbilityList;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.UIManager;
import javax.swing.tree.TreePath;
import treeTable.DefaultTreeTable;
import treeTable.TreeTable;





/**
 *
 * @author  twalker
 * @version
 *
 * PADAbilityNode's hold references to Abilities stored in an Ability list.
 * It should not be used for powers/templates which are cached by the PADRoster
 * shared instance mechanism.
 */
public class PADFrameworkNode extends PADTreeTableNode {
    
    /** Holds value of property ability. */
    private Class frameworkClass;
    
    private static Icon frameworkIcon;
    
    /** Creates new PADAbilityNode */
    public PADFrameworkNode(String name, Class frameworkClass) {
        setUserObject(name);
        setFrameworkClass(frameworkClass);
        
        setupIcons();
        setIcon(frameworkIcon);
    }
    
    protected void setupIcons() {
        if ( frameworkIcon == null ) frameworkIcon = UIManager.getIcon("Framework.DefaultIcon");
    }
    
    public boolean startDrag(TreeTable tree, TreePath path, DragSourceListener listener, DragGestureEvent dge) {
        if ( frameworkClass != null ) {
            AbilityList al = createFrameworkAbilityList();
            
            Transferable t = new ObjectTransferable(al, AbilityList.class);

            Point p = dge.getDragOrigin();
            Rectangle bounds = tree.getPathBounds(path);
            Point offset = new Point(p.x - bounds.x, p.y - bounds.y);

            BufferedImage i = ((DefaultTreeTable)tree).buildDragImage(path);
            DefaultTreeTable.startDrag(i, offset);

            dge.startDrag(null,i,offset, t, listener);
            return true;
        }
        return false;
    }
    
    public void handleDoubleClick(champions.abilityTree.PADTree.PADTreeTable padTree, AbilityTreeTable tree, TreePath abilityPath) {
        AbilityTreeNode node = (AbilityTreeNode)abilityPath.getLastPathComponent();
        
        while ( node != null && ! ( node instanceof AbilityListNode ) ) {
            node = (AbilityTreeNode)node.getParent();
        }
        
        if ( node != null ) {
            AbilityList al = createFrameworkAbilityList();
            ((AbilityListNode)node).getAbilityList().addSublist(al);
        }
    }
    
    protected AbilityList createFrameworkAbilityList() {
        try {
            AbilityList al = new DefaultAbilityList("Framework");
            Ability ability = (Ability)frameworkClass.newInstance();
            
            al.setFramework( ability.getFramework() );
            al.addAbility(ability);
            
            return al;
        }
        catch ( InstantiationException ie) {
            ExceptionWizard.postException(ie);
        }
        catch ( IllegalAccessException ie) {
            ExceptionWizard.postException(ie);
        }
        return null;
    }
    
    /** Returns the PAD (Power, Skill, Adv, Lim, etc) associated with node.
     *
     * This should return a new instance of whatever the node represents.
     *
     * @return null if no PAD is associated, such as in the case of a folder.
     */
    public Object getPAD() {
        return null;
    }
    
    /**
     * Getter for property frameworkClass.
     * @return Value of property frameworkClass.
     */
    public java.lang.Class getFrameworkClass() {
        return frameworkClass;
    }    

    /**
     * Setter for property frameworkClass.
     * @param frameworkClass New value of property frameworkClass.
     */
    public void setFrameworkClass(java.lang.Class frameworkClass) {
        this.frameworkClass = frameworkClass;
    }    
 
    

}
