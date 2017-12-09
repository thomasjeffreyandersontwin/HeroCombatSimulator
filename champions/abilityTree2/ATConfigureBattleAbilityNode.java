/*
 * ATAbilityInstanceGroup.java
 *
 * Created on June 3, 2004, 11:09 PM
 */

package champions.abilityTree2;

import champions.Ability;
import champions.FrameworkAbility;
import champions.Target;
import champions.attackTree.ConfigureBattleActivationList.ConfigureBattleActivationListAction;
import champions.interfaces.AbilityList;
import tjava.Filter;
import javax.swing.Icon;
import treeTable.TreeTable;
import treeTable.TreeTableCellEditor;
import treeTable.TreeTableCellRenderer;

/**
 *
 * @author  1425
 */
public class ATConfigureBattleAbilityNode extends ATAbilityNode  {
    
    static private ATConfigureBattleActionTreeTableCellEditor configureBattleactionEditor;
    static private ATConfigureBattleActionTreeTableCellEditor configureBattleactionRenderer;
    
    /** Creates a new instance of ATAbilityInstanceGroup.
     *
     * The baseAbility will be used to extract the AbilityInstanceGroup.  The
     * actual ability held in the Ability
     */
    public ATConfigureBattleAbilityNode(Ability ability, AbilityList abilityList, ATNodeFactory nodeFactory, Filter<Object> nodeFilter, boolean pruned) {
        super(ability, abilityList, nodeFactory, nodeFilter, pruned);
    }
    
    protected void setupEditors() {
        if ( configureBattleactionEditor == null ) configureBattleactionEditor = new ATConfigureBattleActionTreeTableCellEditor();
         if ( configureBattleactionRenderer == null ) configureBattleactionRenderer = new ATConfigureBattleActionTreeTableCellEditor();
        
        super.setupEditors();
    }
    
    
    protected void buildNode() {
        
    }
    
    protected Icon getBaseIcon(TreeTable treeTable) {
        Icon baseIcon;
        if ( ability.getInstanceGroup().getBaseInstance() instanceof FrameworkAbility ) {
            if ( ability.isActivated(ability.getSource()) ) {
                baseIcon = frameworkRunningIcon;
            } else {
                baseIcon = frameworkStoppedIcon;
            }
        } else {
            boolean activated = ability.isActivated(ability.getSource());
            if ( activated ) {
                baseIcon = runningIcon;
            } else {
                baseIcon = stoppedIcon;
            }
        }
        return baseIcon;
    }

    public Object getValueAt(int column) {
        if ( ability == null ) return null;
        ATColumn atc = ATColumn.values()[column];
        switch(atc){
            case NAME_COLUMN:
                return abilityName;
            case AUTO_ACTIVATE_COLUMN:
                return ability.isNormallyOn();
            case LAUNCH_COLUMN:
                return getConfigureBattleAction();
            default:
                return null; 
        }
    }

    public String getToolTipText(int column) {
        ATColumn atc = ATColumn.values()[column];
        if ( atc == ATColumn.LAUNCH_COLUMN ) {
            return "<html><b>Start/Stop Action</b><br><br>Click here to switch the action.<br><br>" +
                    "At the start of battle, all actions marked auto-activate will be set to activate.</html>";
        }
        else {
            return super.getToolTipText(column);
        }
    }
    
    public TreeTableCellEditor getTreeTableCellEditor(int columnIndex) {
        if ( columnIndex == ATColumn.LAUNCH_COLUMN.ordinal() ) {
            return configureBattleactionEditor;
        }
        return super.getTreeTableCellEditor(columnIndex);
    }

    public TreeTableCellRenderer getTreeTableCellRenderer(int columnIndex) {
        if ( columnIndex == ATColumn.LAUNCH_COLUMN.ordinal() ) {
            return configureBattleactionRenderer;
        }
        return super.getTreeTableCellRenderer(columnIndex);
    }
    
    public ConfigureBattleActivationListAction getConfigureBattleAction() {
        ConfigureBattleActivationListAction action = ConfigureBattleActivationListAction.NOACTION;
        
        Target t = ability.getSource();
        ATConfigureBattleModel model = ATConfigureBattleModel.class.cast(getModel());
        
        if ( model != null && t != null ) {
            action = model.getActivationList().getAction(t,ability);
        }
        return action;
    }
    
    public void setConfigureBattleAction(ConfigureBattleActivationListAction action) {
        Target t = ability.getSource();
        ATConfigureBattleModel model = ATConfigureBattleModel.class.cast(getModel());
        
        if ( model != null && t != null ) {
            ConfigureBattleActivationListAction oldAction = model.getActivationList().getAction(t,ability);
            if ( action != oldAction ) {
                model.getActivationList().setAction(t,ability,action);
                
                model.nodeChanged(this);
            }
        }
    }
    
    public void toggleConfigureBattleAction() {
        ConfigureBattleActivationListAction action = getConfigureBattleAction();
        
        if ( ability.isActivated(ability.getSource()) || ability.isDelayActivating(ability.getSource())) {
            if ( action == ConfigureBattleActivationListAction.NOACTION ) {
                setConfigureBattleAction( ConfigureBattleActivationListAction.DEACTIVATE );
            }
            else {
                setConfigureBattleAction( ConfigureBattleActivationListAction.NOACTION );
            }
        }
        else {
            if ( action == ConfigureBattleActivationListAction.NOACTION ) {
                setConfigureBattleAction( ConfigureBattleActivationListAction.ACTIVATE );
                ATConfigureBattleModel model = ATConfigureBattleModel.class.cast( getModel() );
                if ( model != null && model.isStartOfBattle() ) {
                    ability.setNormallyOn(true);
                }
            }
            else {
                setConfigureBattleAction( ConfigureBattleActivationListAction.NOACTION );
                ATConfigureBattleModel model = ATConfigureBattleModel.class.cast( getModel() );
                if ( model != null && model.isStartOfBattle() ) {
                    ability.setNormallyOn(false);
                }
            }
        }
            
    }
    
}
