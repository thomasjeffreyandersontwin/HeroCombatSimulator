/*
 * AbilityNode.java
 *
 * Created on June 11, 2001, 7:32 PM
 */

package champions.abilityTree;

import champions.*;
import champions.event.PADValueEvent;
import champions.interfaces.*;
import champions.parameters.ParameterList;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import treeTable.DefaultTreeTableCellRenderer;

/**
 *
 * @author  twalker
 * @version
 */
public class VariableAdvantageNode extends AbilityTreeNode
        implements PropertyChangeListener, ChampionsConstants {
    
    /** Holds value of property ability. */
    protected Ability ability;
    
    private int advantageIndex = -1;
    
    private ParameterList advantageParameterList;
    
    private String text = "";
    private int nodeStatus = OKAY_STATUS;
    
    private Icon icon;
    
    
    protected static DefaultTreeTableCellRenderer ptsRenderer;
    // private static AbilityNode.CopyAction copyAction;
    
    /** Creates new AbilityNode */
    public VariableAdvantageNode(AbilityTreeTableModel model, MutableTreeNode parent, Ability ability, int advantageIndex) {
        setModel(model);
        setParent(parent);
        setAbility(ability);
        setAdvantageIndex(advantageIndex);
        
        Advantage p = PADRoster.getSharedAdvantageInstance("Variable Advantage");
        Icon icon = p.getIcon();
        setIcon(icon);
    }
    
    protected void updateText() {
        
        String origText = text;
        int origStatus = nodeStatus;
        
        double required = getRequiredAdvantages();
        
        if ( required > 0 ) {
            text = "Add variable advantages here (" + required + " available)";
            nodeStatus = QUESTION_STATUS;
        }
        else if ( required < 0 ) {
           text = "Too many variable advantages config (" + (required * -1) + " over)";
           nodeStatus = ERROR_STATUS;
        }
        else {
            text = "Variable advantage configuration";
            nodeStatus = OKAY_STATUS;
        }
        
        if ( text.equals(origText) == false || origStatus != nodeStatus) {
            fireNodeStatusChanged();
        }
    }

    public int getColumnSpan(int column) {
        if ( column == AbilityTreeTableModel.ABILITY_TREE_ABILITYCOLUMN ) {
            return AbilityTreeTableModel.ABILITY_TREE_WIDTH;
        }
        return 0;
    }
    
    
    protected double getRequiredAdvantages() {
        if ( ability != null && ability.getParentAbility() != null && advantageParameterList != null ) {
            double pMult = ability.getParentAbility().getAdvantagesMultiplier();
            double mult = ability.getAdvantagesMultiplier();

            //double avMult = ability.getAdvantage(advantageIndex).calculateMultiplier(ability, advantageIndex);
            //double avRequire = (Double)advantageParameterList.getParameterValue("Level");
        
            return ( pMult  - mult );
        }
        else {
            return 0;
        }
    }

    
    public int getNodeStatus() {
        return nodeStatus;
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
        if ( ability != this.ability ) {
            if ( this.ability != null ) {
                this.ability.removePropertyChangeListener(this);
                
                if ( this.ability.getParentAbility() != null ) {
                    this.ability.getParentAbility().removePropertyChangeListener(this);
                }
            }
            
            this.ability = ability;
            
            if ( this.ability != null ) {
                this.ability.addPropertyChangeListener(this);
                
                if ( this.ability.getParentAbility() != null ) {
                    this.ability.getParentAbility().addPropertyChangeListener(this);
                }
                
                updateText();
            } 
            
            if ( this.advantageIndex != -1 && ability != null) {
                setAdvantageParameterList(ability.getAdvantage(advantageIndex).getParameterList(ability, advantageIndex));
            }
            else {
                setAdvantageParameterList(null);
            }
        }
    }

    
    public String toString() {
       return text;
    }
    
    public TreeCellEditor getTreeCellEditor(JTree tree) {
        //   parameterList.setParameterValue( "Name", getAbility().getName() );
        //   editor.setParameterList(parameterList);
        
        //   return editor;
        return null;
    }
    
    public TreeCellRenderer getTreeCellRenderer(JTree tree) {
        return null;
    }
   
 
    /**
     * This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source
     *  	and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        String property = evt.getPropertyName();
        
        if ( evt.getSource() == ability ) {
            if ( property.equals("Ability.APCOST") ) {
                updateText();
            } 
        }
        else if ( evt.getSource() == ability.getParentAbility() ) {
             if ( property.equals("Ability.APCOST") ) {
                 updateText();
            }
        }
        else if (evt.getSource() == advantageParameterList ) {
            updateText();
        }
    }

    
    public Object getValue(int columnIndex) {
        Object v = null;
        
        if ( ability != null ) {
            switch (columnIndex) {
                case AbilityTreeTableModel.ABILITY_TREE_ABILITYCOLUMN:
                    v = text;
                    break;
                case AbilityTreeTableModel.ABILITY_TREE_ENDCOLUMN:
                    break;
                case AbilityTreeTableModel.ABILITY_TREE_PTSCOLUMN:
                    break;
                case AbilityTreeTableModel.ABILITY_TREE_APCOLUMN:
                    break;
                case AbilityTreeTableModel.ABILITY_TREE_REALCOLUMN:
                    break;
            }
        }
        
        return v;
        
    }
    
    /** Returns the Text for the ToolTip for the node which the cursor is currently over.
     */
//    public String getToolTipText() {
//        String toolTip = "<B>" + ability.getName() + "</B><P>" + ability.getDescription();
//        return ChampionsUtilities.createWrappedHTMLString(toolTip, 40);
//    }

    public ParameterList getAdvantageParameterList() {
        return advantageParameterList;
    }

    public void setAdvantageParameterList(ParameterList advantageParameterList) {
        if ( this.advantageParameterList != advantageParameterList ) {
            if ( this.advantageParameterList != null ) {
                this.advantageParameterList.removePropertyChangeListener(this);
            }
        
            this.advantageParameterList = advantageParameterList;
            updateText();
        
            if ( this.advantageParameterList != null ) {
                this.advantageParameterList.addPropertyChangeListener(this);
            }
        }
    }

    public int getAdvantageIndex() {
        return advantageIndex;
    }

    public void setAdvantageIndex(int advantageIndex) {
        if ( this.advantageIndex != advantageIndex) {
            
            this.advantageIndex = advantageIndex;
            
            if ( this.advantageIndex != -1 && ability != null) {
                setAdvantageParameterList(ability.getAdvantage(advantageIndex).getParameterList(ability, advantageIndex));
            }
            else {
                setAdvantageParameterList(null);
            }
        }
    }
    
    public Icon getIcon(JTree tree,boolean selected,boolean expanded,boolean leaf,int row,boolean hasFocus) {
         return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }
}
