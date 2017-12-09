/*
 * StringNode.java
 *
 * Created on July 4, 2001, 10:07 AM
 */

package champions.abilityTree;

import champions.Ability;
import champions.SpecialEffect;
import java.awt.event.ActionEvent;
import javax.swing.*;


import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import tjava.SharedPopupAction;

/**
 *
 * @author  twalker
 * @version
 */
public class SpecialEffectNode extends AbilityTreeNode {
    
    /** Holds value of property icon. */
    private Icon icon;
    
    /** Holds value of property specialEffect. */
    private SpecialEffect specialEffect;
    
    private SpecialEffectNode.RemoveSpecialEffectAction removeAction;
    private static JMenuItem removeMenuItem;
    
    private static InputMap inputMap;
    private static ActionMap actionMap;
    
    /** Holds value of property ability. */
    private Ability ability;
    
    /** Creates new StringNode */
    public SpecialEffectNode(AbilityTreeTableModel model, MutableTreeNode parent, Ability ability, SpecialEffect specialEffect) {
        setModel(model);
        setParent(parent);
        setAbility(ability);
        setSpecialEffect(specialEffect);
        
        setupActions();
        setupKeyBindings();
        
    }
    
    public void setupActions() {
        if ( removeAction == null ) {
            removeAction = new RemoveSpecialEffectAction();
            removeMenuItem = new JMenuItem(removeAction);
           // removeMenuItem.setAccelerator( KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0) );
        }
    }
    
    public void setupKeyBindings() {
        if ( inputMap == null ) {
            inputMap = new InputMap();
         //   inputMap.put( KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "RemoveSpecialEffect" );
          //  inputMap.put( KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "RemoveSpecialEffect" );
        }
        
        if ( actionMap == null ) {
            actionMap = new ActionMap();
            actionMap.put( "RemoveSpecialEffect", removeAction );
        }
        
    }
    
    public InputMap getInputMap() {
        
        // Setup the Static Action to refer to this particulars nodes configuration
        //removeAction.setTarget(source);
        removeAction.setAbility(ability);
        removeAction.setSpecialEffect(specialEffect);
        
        return inputMap;
    }
    
    /** Returns the Nodes custom action map.
     * This method should return a custom action map of action the Node can perform and has key bindings for.
     * When this node is selected, the AbilityTree will load this action map and bind the keys appropriately.
     *
     * MenuItems returned by invokeMenu which have key bindings should populate an action map and return it.
     */
    
    public ActionMap getActionMap() {
        return actionMap;
    }
    
    public boolean invokeMenu(JPopupMenu popup, JTree tree, TreePath path) {
       
        
        //removeAction.setTarget(source);
        removeAction.setAbility(ability);
        removeAction.setSpecialEffect(specialEffect);
        removeMenuItem.setEnabled(removeAction.isEnabled());
        popup.add(removeMenuItem);

        return true;
    }
    
    /** Returns the current status of the node.
     * The status of the node determine if additional marking are placed on the icons of the tree hierarchy.
     * OKAY_STATUS indicates there are no problems.
     * QUESTION_STATUS indicates a question or misconfiguration exists, but is not causing an error.
     * ERROR_STATUS indicates that a misconfiguration exists which will disable the node.
     * CRITICAL_STATUS indicates immidiate attention must be payed to the node.
     */
    
    public int getNodeStatus() {
        return OKAY_STATUS;
    }
    
    public Icon getIcon(JTree tree,boolean selected,boolean expanded,boolean leaf,int row,boolean hasFocus) {
        return (specialEffect != null) ? specialEffect.getIcon() : null;
    }
    
    public String toString() {
        return (specialEffect != null) ? specialEffect.getName() : "";
    }
    
    /** Getter for property specialEffect.
     * @return Value of property specialEffect.
     */
    public SpecialEffect getSpecialEffect() {
        return specialEffect;
    }
    
    /** Setter for property specialEffect.
     * @param specialEffect New value of property specialEffect.
     */
    public void setSpecialEffect(SpecialEffect specialEffect) {
        this.specialEffect = specialEffect;
    }
    
    /** Getter for property ability.
     * @return Value of property ability.
     */
    public Ability getAbility() {
        return ability;
    }
    
    /** Setter for property ability.
     * @param ability New value of property ability.
     */
    public void setAbility(Ability ability) {
        this.ability = ability;
    }
    
    public static class RemoveSpecialEffectAction extends SharedPopupAction {
        private Ability ability;
        private SpecialEffect specialEffect;
        
        public RemoveSpecialEffectAction() {
            super("Delete Special Effect");
        }
        
        public void actionPerformed(ActionEvent e ) {
            
            if ( ability != null && specialEffect != null ) {
                ability.removeSpecialEffect(specialEffect);
            }
        }
        
        public void setAbility(Ability ability) {
            this.ability = ability;
        }
        
        /** Setter for property specialEffect.
         * @param specialEffect New value of property specialEffect.
         */
        public void setSpecialEffect(SpecialEffect specialEffect) {
            this.specialEffect = specialEffect;
        }
    }
}
