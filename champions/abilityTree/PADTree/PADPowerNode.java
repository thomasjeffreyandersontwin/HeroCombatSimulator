/*
 * PADPowerNode.java
 *
 * Created on February 27, 2002, 11:43 PM
 */

package champions.abilityTree.PADTree;

import champions.Ability;
import tjava.ObjectTransferable;
import champions.PADRoster;
import champions.Power;
import champions.abilityTree.AbilityListNode;
import champions.abilityTree.AbilityTreeNode;
import champions.abilityTree.AbilityTreeTable;
import champions.interfaces.Advantage;
import champions.interfaces.Limitation;
import champions.interfaces.SpecialParameter;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.tree.TreePath;
import treeTable.DefaultTreeTable;
import treeTable.TreeTable;


/**
 *
 * @author  twalker
 * @version
 *
 * PADPowerNode's hold references to powers/templates stored in the PADRoster.
 * It should not be used for abilities which come from an AbilityList.
 */
public class PADPowerNode extends PADTreeTableNode {
    
    /** Holds value of property powerName. */
    private String name;
    
    /** Creates new PADPowerNode */
    public PADPowerNode(String powerName) {
        setName(powerName);
        
    }
    
    /** Getter for property powerName.
     * @return Value of property powerName.
     */
    public String getName() {
        return name;
    }
    
    /** Setter for property powerName.
     * @param powerName New value of property powerName.
     */
    public void setName(String name) {
        this.name = name;
        setUserObject(name);
        
        Power p = PADRoster.getPower(name);
        Icon icon = p.getIcon();
        setIcon(icon);
        
        buildChildren();
    }
    
    private void buildChildren() {
        if ( getChildCount() > 0 ) removeAllChildren();
        
        Power p = PADRoster.getPower(name);
        Object[] array = p.getCustomAddersArray();
        
        if ( array != null ) {
            int index;
            for ( index = 0; index < array.length; index +=2 ) {
                Class c = (Class)array[index];
                String n = (String)array[index+1];
                
                if (  c.equals( Limitation.class ) ) {
                    if ( PADRoster.getSharedLimitationInstance(n) != null) {
                        PADLimitationNode node = new PADLimitationNode(n);
                        this.add(node);
                    }
                    else {
                        System.out.println("Limitation Name " + n + " invalid while building children for PADPowerNode for " + name + ".");
                    }
                }
                else if ( c.equals( Advantage.class ) ) {
                    if ( PADRoster.getSharedAdvantageInstance(n) != null ) {
                        PADAdvantageNode node = new PADAdvantageNode(n);
                        this.add(node);
                    }
                    else {
                        System.out.println("Advantage Name " + n + " invalid while building children for PADPowerNode for " + name + ".");
                    }
                }
                else if ( c.equals( SpecialParameter.class ) ) {
                    if ( PADRoster.getSharedSpecialParameterInstance(n) != null ) {
                        PADSpecialParameterNode node = new PADSpecialParameterNode(n);
                        this.add(node);
                    }
                    else {
                        System.out.println("Special Parameter Name " + n + " invalid while building children for PADPowerNode for " + name + ".");
                    }
                }
            }
        }
        
        
    }
    
    public boolean startDrag(TreeTable tree, TreePath path, DragSourceListener listener, DragGestureEvent dge) {
        if ( getName() != null ) {
            
            Ability o = PADRoster.getNewAbilityInstance( name );
            
            if ( o != null ) {
                Transferable t = new ObjectTransferable(o, Ability.class);
                
                Point p = dge.getDragOrigin();
                Rectangle bounds = tree.getPathBounds(path);
                Point offset = new Point(p.x - bounds.x, p.y - bounds.y);
                
                BufferedImage i = ((DefaultTreeTable)tree).buildDragImage(path);
                DefaultTreeTable.startDrag(i, offset);
                
                dge.startDrag(null,i,offset, t, listener);
                return true;
            }
        }
        return false;
    }
    
    public void handleDoubleClick(champions.abilityTree.PADTree.PADTreeTable padTree, AbilityTreeTable tree, TreePath abilityPath) {
        AbilityTreeNode node = (AbilityTreeNode)abilityPath.getLastPathComponent();
        
        while ( node != null && ! ( node instanceof AbilityListNode ) ) {
            node = (AbilityTreeNode)node.getParent();
        }
        
        if ( node != null ) {
            //  String sublist = ((SublistNode)node).getSublist();
            Ability ability = PADRoster.getNewAbilityInstance(name);
            // ability.setSublist(sublist);
            ((AbilityListNode)node).addAbility(ability);
        }
    }
    
    /** Returns the PAD (Power, Skill, Adv, Lim, etc) associated with node.
     *
     * This should return a new instance of whatever the node represents.
     *
     * @return null if no PAD is associated, such as in the case of a folder.
     */
    public Object getPAD() {
        return PADRoster.getNewAbilityInstance(name);
    }
    
}
