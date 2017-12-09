/*
 * StringNode.java
 *
 * Created on July 4, 2001, 10:07 AM
 */

package champions.abilityTree;

import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;

import treeTable.*;
/**
 *
 * @author  twalker
 * @version 
 */
public class TextNode extends AbilityTreeNode {

    /** Holds value of property text. */
    private String text;
    
    /** Holds value of property state. */
    private int state;
    
    /** Holds value of property icon. */
    private Icon icon;
    
    /** Creates new StringNode */
    public TextNode(AbilityTreeTableModel model, MutableTreeNode parent, String text, int state, Icon icon) {
        setModel(model);
        setParent(parent);
        setText(text);
        setState(state);
        setIcon(icon);
    }

    /** Getter for property text.
     * @return Value of property text.
 */
    public String getText() {
        return text;
    }
    
    /** Setter for property text.
     * @param text New value of property text.
 */
    public void setText(String text) {
        this.text = text;
    }
    
    /** Getter for property state.
     * @return Value of property state.
 */
    public int getState() {
        return state;
    }
    
    /** Setter for property state.
     * @param state New value of property state.
 */
    public void setState(int state) {
        this.state = state;
    }
    
    /** Returns the current status of the node.
 * The status of the node determine if additional marking are placed on the icons of the tree hierarchy.
 * OKAY_STATUS indicates there are no problems.
 * QUESTION_STATUS indicates a question or misconfiguration exists, but is not causing an error.
 * ERROR_STATUS indicates that a misconfiguration exists which will disable the node.
 * CRITICAL_STATUS indicates immidiate attention must be payed to the node.
 */
    
    public int getNodeStatus() {
        return state;
    }
    
    /** Getter for property icon.
     * @return Value of property icon.
 */
    public Icon getIcon() {
        return icon;
    }
    
    /** Setter for property icon.
     * @param icon New value of property icon.
 */
    public void setIcon(Icon icon) {
        this.icon = icon;
    }
    
    public Icon getIcon(JTree tree,boolean selected,boolean expanded,boolean leaf,int row,boolean hasFocus) {
        return icon;
    }
    
    public String toString() {
     return text;   
    }
}
