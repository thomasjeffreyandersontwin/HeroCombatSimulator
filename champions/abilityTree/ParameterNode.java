/*
 * ParameterNode.java
 *
 * Created on June 11, 2001, 7:53 PM
 */

package champions.abilityTree;

import champions.DetailList;
import champions.interfaces.ChampionsConstants;
import champions.parameterEditor.AbstractParameterEditor;
import champions.parameterEditor.DefaultParameterEditor;
import champions.parameters.Parameter;
import champions.parameters.ParameterList;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import treeTable.TreeTableCellEditor;
import treeTable.TreeTableCellRenderer;


/**
 *
 * @author  twalker
 * @version
 */
public class ParameterNode extends AbilityTreeNode
        implements PropertyChangeListener, ChampionsConstants {
    
    /** Holds value of property parameterList. */
    private ParameterList parameterList;
    
    /** Holds value of property parameter. */
    private String parameter;
    
    /** Holds value of property source. */
    private DetailList source;
    
    /** Holds Parameter Renderer/Editor */
    private AbstractParameterEditor renderer;
    
    static private Icon defaultIcon = null;
    
    /** Holds value of property enabled. */
    private boolean enabled = true;
    
    private boolean parameterEnabled;
    
    private String indexName = null;
    
    /** Holds value of property icon. */
    private Icon icon = null;
    
    /** Hold the propertyChange Name for the parameters set message. */
    private String valuePropertyChangeString = null;
    private String setPropertyChangeString = null;
    
    /** Creates new ParameterNode */
    public ParameterNode(AbilityTreeTableModel model, DetailList source, ParameterList list, MutableTreeNode parent, String parameter) {
        setModel(model);
        setSource(source);
        setParameterList(list);
        setParent(parent);
        setParameter(parameter);
        
        setupRenderer();
        
        if ( defaultIcon == null ) defaultIcon = new ImageIcon( getClass().getResource("/graphics/parameterIcon.gif") );
    }
    
    public void setupRenderer() {
        if ( parameterList != null && parameter != null ) {
            renderer = parameterList.getCustomEditor(parameter);
        }
        
        if ( renderer == null ) {
            renderer = DefaultParameterEditor.getRendererFor(parameterList, parameter);
        }
        
        renderer.setParameterList(parameterList);
    }
    
    /** Getter for property parameterList.
     * @return Value of property parameterList.
     */
    public ParameterList getParameterList() {
        return parameterList;
    }
    
    /** Setter for property parameterList.
     * @param parameterList New value of property parameterList.
     */
    public void setParameterList(ParameterList parameterList) {
        if ( this.parameterList != parameterList ) {
            if ( this.parameterList != null && valuePropertyChangeString != null) {
                this.parameterList.removePropertyChangeListener(setPropertyChangeString, this);
                this.parameterList.removePropertyChangeListener(valuePropertyChangeString, this);
            }
            
            
            this.parameterList = parameterList;
            
            if ( this.parameterList != null && valuePropertyChangeString != null ) {
                this.parameterList.addPropertyChangeListener(setPropertyChangeString, this);
                this.parameterList.addPropertyChangeListener(valuePropertyChangeString, this);
            }
        }
    }
    
    /** Getter for property parameter.
     * @return Value of property parameter.
     */
    public String getParameter() {
        return parameter;
    }
    
    /** Setter for property parameter.
     * @param parameter New value of property parameter.
     */
    public void setParameter(String parameter) {
        this.parameter = parameter;
        if ( parameterList != null && source != null ) {
            //int index = parameterList.getParameterIndex(parameter);
            
            Parameter p = parameterList.getParameter(parameter);
            String key = p.getKey();
            String desc = p.getDescription();
            boolean enabled = parameterList.isParameterEnabled(parameter);
            
            
            // Setup the property change listener...
            if ( this.parameterList != null && valuePropertyChangeString != null) {
                this.parameterList.removePropertyChangeListener(valuePropertyChangeString, this);
                this.parameterList.removePropertyChangeListener(setPropertyChangeString, this);
            }
            
            setPropertyChangeString = parameter + ".SET";
            valuePropertyChangeString = parameter + ".VALUE";
            
            if ( this.parameterList != null && valuePropertyChangeString != null ) {
                this.parameterList.addPropertyChangeListener(valuePropertyChangeString, this);
                this.parameterList.addPropertyChangeListener(setPropertyChangeString, this);
            }
            
            
            //indexName = "parameter" + Integer.toString(index);
            
            Object value = parameterList.getParameterValue(parameter);
            
            setUserObject( value );
            
            setParameterEnabled(enabled);
        }
    }
    
    /** Getter for property source.
     * @return Value of property source.
     */
    public DetailList getSource() {
        return source;
    }
    
    /** Setter for property source.
     * @param source New value of property source.
     */
    public void setSource(DetailList source) {
        this.source = source;
        
    }
    
    public Icon getIcon(JTree tree,boolean selected,boolean expanded,boolean leaf,int row,boolean hasFocus) {
        return ( icon != null ) ? icon : defaultIcon;
    }
    
    public TreeCellEditor getTreeCellEditor(JTree tree) {
        return renderer;
    }
    
    public TreeCellRenderer getTreeCellRenderer(JTree tree) {
        return renderer;
    }
    
    
    public AbstractParameterEditor getParameterEditor() {
        return renderer;
    }
    
    public int getNodeStatus() {
        if ( parameterList != null && parameter != null ) {
            int index = parameterList.getParameterIndex(parameter);
            if ( parameterList.isVisible(index) && parameterList.isRequired(index) && parameterList.isParameterSet(index) == false ) {
                return ERROR_STATUS;
            }
        }
        return OKAY_STATUS;
    }
    
    /** Called to destroy the object.  All references should be set to null.
     * Once destroy has been called, the object will not be used anymore.
     */
    public void destroy() {
        super.destroy();
        
        setParameterList(null);
        
        if ( renderer != null ) {
            renderer.setParameterList(null);
            renderer = null;
        }
    }
    
    /** Getter for property enabled.
     * @return Value of property enabled.
     */
    public boolean isEnabled() {
        return enabled && parameterEnabled;
    }
    
    /** Setter for property enabled.
     * @param enabled New value of property enabled.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    /** Setter for property icon.
     * @param icon New value of property icon.
     */
    public void setIcon(Icon icon) {
        this.icon = icon;
    }
    
    
    
    public String toString() {
        if ( parameterList != null && source != null ) {
            
            int index = parameterList.getParameterIndex(parameter);
            String key = parameterList.getParameterKey(index);
            String desc = parameterList.getParameterDescription(index);
            
            Object value = source.getValue(key);
            
            return key + ": " + value;
        }
        return "ParameterNode Error";
    }
    
    /**
     * This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source
     *  	and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        
        // if ( evt.getPropertyName().equals( valuePropertyChangeString ) ) {
        if ( model != null ) fireNodeStatusChanged();
        // }
        
    }
    
    
    public TreeTableCellEditor getTreeTableCellEditor(int columnIndex) {
        return (columnIndex == AbilityTreeTableModel.ABILITY_TREE_ABILITYCOLUMN) ? renderer : null;
    }
    
    public TreeTableCellRenderer getTreeTableCellRenderer(int columnIndex) {
        return (columnIndex == AbilityTreeTableModel.ABILITY_TREE_ABILITYCOLUMN) ? renderer : null;
    }
    
    public boolean isEditable(int column) {
        return isEnabled() && column == AbilityTreeTableModel.ABILITY_TREE_ABILITYCOLUMN;
    }
    
    public int getColumnSpan(int column) {
        if ( column == AbilityTreeTableModel.ABILITY_TREE_ABILITYCOLUMN ) {
            return AbilityTreeTableModel.ABILITY_TREE_WIDTH;
        } else {
            return 1;
        }
    }
    
    public boolean isParameterEnabled() {
        return parameterEnabled;
    }
    
    public void setParameterEnabled(boolean parameterEnabled) {
        this.parameterEnabled = parameterEnabled;
    }
    
    
    
}
