/*
 * PADFolderNode.java
 *
 * Created on January 14, 2003, 2:49 PM
 */

package champions.abilityTree.PADTree;

import champions.*;
import champions.SpecialEffect;
import champions.abilityTree.*;
import champions.interfaces.*;
import java.awt.event.KeyEvent;
import java.util.*;
import javax.swing.tree.TreePath;
/**
 *
 * @author  Trevor Walker
 */
public class PADFolderNode extends PADTreeTableNode {
    
    /** Holds value of property folderName. */
    private String folderName;
    
    /** Creates a new instance of PADFolderNode */
    public PADFolderNode(String folderName) {
        setFolderName(folderName);
        buildChildren();
    }
    
    private void buildChildren() {
        PADFolder folder = PADRoster.getFolder(folderName);
        
        Iterator i;
        
        i = folder.getFolderIterator();
        while ( i.hasNext() ) {
            PADFolder f = (PADFolder)i.next();
            PADFolderNode node = new PADFolderNode(f.getName());
            add(node);
        }
        
        i = folder.getPADIterator();
        while ( i.hasNext() ) {
            String name = (String)i.next();
            PADTreeTableNode node = null;
            Class c = PADRoster.getPADClass(name);
            if ( c != null ) {
                if ( c.equals(Ability.class) ) {
                    node = new PADPowerNode(name);
                }
                else if ( Advantage.class.isAssignableFrom( c ) ) {
                    node = new PADAdvantageNode(name);
                }
                else if ( Limitation.class.isAssignableFrom( c ) ) {
                    node = new PADLimitationNode(name);
                }
                else if ( SpecialParameter.class.isAssignableFrom( c ) ) {
                    node = new PADSpecialParameterNode(name);
                }
                else if ( SpecialEffect.class.equals(c) ) {
                    node = new PADSpecialEffectNode(name);
                }
                add(node);
            }
        }
    }
    
    /** Getter for property folderName.
     * @return Value of property folderName.
     *
     */
    public String getFolderName() {
        return this.folderName;
    }
    
    /** Setter for property folderName.
     * @param folderName New value of property folderName.
     *
     */
    public void setFolderName(String folderName) {
        setUserObject(folderName);
        this.folderName = folderName;
    }

    public void handleKeyTyped(KeyEvent keyEvent, champions.abilityTree.PADTree.PADTreeTable padTree, AbilityTreeTable tree, TreePath abilityPath) {
        if ( keyEvent.getKeyChar() == '\n' ) {
            TreePath tp = new TreePath(this.getPath());
            if ( padTree.isExpanded(tp) ) {
                padTree.collapsePath(tp);
            }
            else {
                padTree.expandPath(tp);
            }
        }
    }
    

    
}
