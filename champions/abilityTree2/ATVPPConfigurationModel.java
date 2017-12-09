/*
 * ATConfigureBattleModel.java
 *
 * Created on November 21, 2007, 7:46 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package champions.abilityTree2;

import champions.Ability;
import champions.AbilityKey;
import champions.VariablePointPoolAbilityConfiguration;
import champions.VariablePointPoolAbilityConfiguration.VPPConfigurationAction;
import champions.event.PADValueEvent;
import champions.interfaces.Framework;
import champions.parameterEditor.ComboParameterEditor;
import java.util.HashMap;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import treeTable.TreeTableCellEditor;
import treeTable.TreeTableCellRenderer;

/**
 *
 * @author twalker
 */
public class ATVPPConfigurationModel extends ATModel {

    protected static final int[] visibleColumns = {
        ATColumn.NAME_COLUMN.ordinal(),
        ATColumn.VPP_CURRENT_ABILITY_STATUS.ordinal(),
        ATColumn.AP_COLUMN.ordinal(),
        ATColumn.CP_COLUMN.ordinal(),
        ATColumn.VPP_ABILITY_ACTION.ordinal()
        
    };
    
    protected Map<AbilityKey,ComboParameterEditor> editorMap = new HashMap<AbilityKey,ComboParameterEditor>();

    /** Creates a new instance of ATConfigureBattleModel */
    public ATVPPConfigurationModel(ATNode root, String title) {
        super(root, title);
    }

    public int[] getVisibleColumns() {
        return visibleColumns;
    }

    public boolean isColumnAllowed(int modelIndex) {
        // Allow only the columns in visibleColumns...
        for (int i : visibleColumns) {
            if (modelIndex == i) {
                return true;
            }
        }

        return false;
    }

    public boolean isColumnRequired(int modelIndex) {
        // Require the name column to be shown...
        return modelIndex == ATColumn.NAME_COLUMN.ordinal();
    }
    
    
 //   public static VariablePointPoolAbilityConfiguration conf = new VariablePointPoolAbilityConfiguration();

    public VariablePointPoolAbilityConfiguration getConfiguration() {
        ATVPPConfigurationRoot r = (ATVPPConfigurationRoot)getRoot();
        
        Framework f = r.getFramework();
        return f.getConfiguration();
    }

    @Override
    public Object getValueAt(Object node, int column) {

        if (node instanceof ATAbilityNode) {

            ATAbilityNode n = (ATAbilityNode) node;
            Ability ability = n.getAbility();

            VariablePointPoolAbilityConfiguration c = getConfiguration();

            switch (ATColumn.values()[column]) {
                case VPP_CURRENT_ABILITY_STATUS:
                    return c.getAbilityStatus(ability);


                case VPP_ABILITY_ACTION:
                    VariablePointPoolAbilityConfiguration.VPPConfigurationStatus status = c.getAbilityStatus(ability);
                    VariablePointPoolAbilityConfiguration.VPPConfigurationAction actions[] = VariablePointPoolAbilityConfiguration.VPPConfigurationAction.getAvailableActions(status);
                    if ( actions == null ) {
                        return "";
                    }
                    
                    return c.getAbilityAction(ability);

                case VPP_ADDITIONAL_INFO:
                    return "";
            }

        }
        
        return super.getValueAt(node, column);
    }

    @Override
    public void setValueAt(Object node, int column, Object aValue) {

        super.setValueAt(node, column, aValue);
    }
    
    //private TreeTableCellEditor comboEditor = new ComboParameterEditor(null,null);
    //private TreeTableCellRenderer comboRenderer = new ComboParameterEditor(null,null);
    
    public ComboParameterEditor getEditor(Ability ability) {
        ComboParameterEditor e = editorMap.get(new AbilityKey(ability));
        
        VariablePointPoolAbilityConfiguration c = getConfiguration();
        if ( e == null ) {
        
            
            //VariablePointPoolAbilityConfiguration.VPPConfigurationStatus status = c.getAbilityStatus(ability);
            //VariablePointPoolAbilityConfiguration.VPPConfigurationAction actions[] = VariablePointPoolAbilityConfiguration.VPPConfigurationAction.getAvailableActions(status);

            e = new ComboParameterEditor(null,null);
            //e.setComboBoxModel( new DefaultComboBoxModel( actions ) );
            e.addPADValueListener( new ComboBoxListener(c, ability));

            editorMap.put( new AbilityKey(ability), e);
        }
        
        VariablePointPoolAbilityConfiguration.VPPConfigurationStatus status = c.getAbilityStatus(ability);
            VariablePointPoolAbilityConfiguration.VPPConfigurationAction actions[] = VariablePointPoolAbilityConfiguration.VPPConfigurationAction.getAvailableActions(status);

            e.setComboBoxModel( new DefaultComboBoxModel( actions ) );
        
        e.setCurrentValue( c.getAbilityAction(ability) );
        
        return e;
    }

    @Override
    public TreeTableCellEditor getCellEditor(Object node, int column) {
        
        if ( node instanceof ATAbilityNode && column == ATColumn.VPP_ABILITY_ACTION.ordinal() ) {
            
            ATAbilityNode n = (ATAbilityNode) node;
            Ability ability = n.getAbility();
            
            return getEditor(ability);
        }
        
        return super.getCellEditor(node, column);
    }

    protected static TreeTableCellRenderer renderer = new ComboParameterEditor(null, null);

      
    
    @Override
    public TreeTableCellRenderer getCellRenderer(Object node, int column) {
        
        if ( node instanceof ATAbilityNode && column == ATColumn.VPP_ABILITY_ACTION.ordinal() ) {
            
            ATAbilityNode n = (ATAbilityNode) node;
            Ability ability = n.getAbility();
            
            return getEditor(ability);
        }
        
        return super.getCellRenderer(node, column);
    }

    @Override
    public boolean isCellEditable(Object node, int column) {
        return (node instanceof ATAbilityNode && column == ATColumn.VPP_ABILITY_ACTION.ordinal());
    }
    
   
    
    public void applyActions() {
        getConfiguration().applyActions(null);
        getRoot().buildNode();
    }
    
    public void cancelActions() {
        getConfiguration().cancelActions();
        getRoot().buildNode();
    }
    
    public static class ComboBoxListener implements champions.interfaces.PADValueListener {

        VariablePointPoolAbilityConfiguration configuration;
        Ability ability;

        public ComboBoxListener(VariablePointPoolAbilityConfiguration configuration, Ability ability) {
            this.configuration = configuration;
            this.ability = ability;
        }
        
        
        
        public void PADValueChanged(PADValueEvent evt) {
            VariablePointPoolAbilityConfiguration.VPPConfigurationAction action = (VPPConfigurationAction)evt.getValue();
            
            configuration.setAbilityAction(ability, action);
        }

        public boolean PADValueChanging(PADValueEvent evt) {
            //throw new UnsupportedOperationException("Not supported yet.");
            return true;
        }
        
    }
}
