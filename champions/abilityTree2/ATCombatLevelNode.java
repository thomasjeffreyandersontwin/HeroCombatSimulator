/*
 * PADCombatLevelEntryNode.java
 *
 * Created on February 27, 2002, 11:43 PM
 */
package champions.abilityTree2;

import champions.CombatLevelListEntry;
import champions.Target;
import champions.event.PADValueEvent;
import tjava.Filter;
import champions.interfaces.PADValueListener;
import champions.parameterEditor.AbstractParameterEditor;
import champions.parameterEditor.BooleanParameterEditor;
import champions.parameterEditor.IntegerParameterEditor;
import javax.swing.UIManager;
import treeTable.TreeTableCellEditor;
import treeTable.TreeTableCellRenderer;

/**
 *
 * @author  twalker
 * @version
 */
public class ATCombatLevelNode extends ATNode implements PADValueListener {

    public static final long serialVersionUID = 2309409230948L;

    private CombatLevelListEntry combatLevelEntry = null;

    private  IntegerParameterEditor integerParameterRenderer = new IntegerParameterEditor();
    private  IntegerParameterEditor integerParameterEditor = new IntegerParameterEditor();
    private  BooleanParameterEditor booleanParameterRenderer = new BooleanParameterEditor();
    private  BooleanParameterEditor booleanParameterEditor = new BooleanParameterEditor();
    private  ATCombatLevelNode currentEditingNode = null;
    private  AbstractParameterEditor currentEditor = null;
//    /** Creates new PADCombatLevelEntryNode */
//    public ATRosterNode(Roster roster) {
//        this(roster, null, false);
//    }

    public ATCombatLevelNode(CombatLevelListEntry combatLevelEntry, ATNodeFactory nodeFactory, Filter<Object> nodeFilter, boolean pruned) {
        super(nodeFactory, nodeFilter, pruned);

        setCombatLevelEntry(combatLevelEntry);

        icon = UIManager.getIcon("AttackTree.attackParametersIcon");
    }

    @Override
    public Object getValueAt(int column) {
        ATColumn c = ATColumn.values()[column];
        switch (c) {
            case NAME_COLUMN:
                return combatLevelEntry.getProvider().getCombatLevelName();
            case OCV_COLUMN:
                return combatLevelEntry.getProvider().getConfiguredOCVModifier();
            case DCV_COLUMN:
                return combatLevelEntry.getProvider().getConfiguredDCVModifier();
            case DC_COLUMN:
                return combatLevelEntry.getProvider().getConfiguredDCModifier();
            case ENABLED_COLUMN:
                return combatLevelEntry.isEnabled();
            default:
                return null;
        }
    }

    @Override
    public TreeTableCellRenderer getTreeTableCellRenderer(int columnIndex) {
        ATColumn c = ATColumn.values()[columnIndex];
        switch (c) {
            //case NAME_COLUMN:
            case OCV_COLUMN:
            case DCV_COLUMN:
            case DC_COLUMN:
                return integerParameterRenderer;
            case ENABLED_COLUMN:
                return booleanParameterRenderer;
            default:
                return null;
        }
    }

    @Override
    public TreeTableCellEditor getTreeTableCellEditor(int columnIndex) {
        ATColumn c = ATColumn.values()[columnIndex];

        AbstractParameterEditor editor = null;
        String parameter = null;
        switch (c) {
            case OCV_COLUMN:
                parameter = "ocv";
                editor = integerParameterEditor;
                break;
            case DCV_COLUMN:
                parameter = "dcv";
                editor = integerParameterEditor;
                break;
            case DC_COLUMN:
                parameter = "dc";
                editor = integerParameterEditor;
                break;
            case ENABLED_COLUMN:
                parameter = "enabled";
                editor = booleanParameterEditor;
                break;
            default:

        }

        if (editor != null) {
            if (currentEditor != null && currentEditingNode != null) {
                currentEditor.removePADValueListener(currentEditingNode);
            }

            currentEditor = editor;
            currentEditingNode = this;

            currentEditor.addPADValueListener(this);

            editor.setParameter(parameter);
        }

        return editor;
    }

    @Override
    public boolean isCellEditable(int column) {
        ATColumn c = ATColumn.values()[column];
        switch (c) {
            //case NAME_COLUMN:
            case OCV_COLUMN:
            case DCV_COLUMN:
            case DC_COLUMN:
            case ENABLED_COLUMN:
                return true;
            default:
                return false;
        }
    }

    public void destroy() {
        super.destroy();

        setCombatLevelEntry(null);
    }

    /**
     * @return the combatLevelEntry
     */
    public CombatLevelListEntry getCombatLevelEntry() {
        return combatLevelEntry;
    }

    /**
     * @param combatLevelEntry the combatLevelEntry to set
     */
    public void setCombatLevelEntry(CombatLevelListEntry combatLevelEntry) {
        this.combatLevelEntry = combatLevelEntry;
    }

    public void PADValueChanged(PADValueEvent evt) {
        if (evt.getKey().equals("enabled")) {
            combatLevelEntry.setEnabled((Boolean) evt.getValue());
        } else {
            int ocv = combatLevelEntry.getProvider().getConfiguredOCVModifier();
            int dcv = combatLevelEntry.getProvider().getConfiguredDCVModifier();
            int dc = combatLevelEntry.getProvider().getConfiguredDCModifier();

            if (evt.getKey().equals("ocv")) {
                ocv = (Integer) evt.getValue();
            } else if (evt.getKey().equals("dcv")) {
                dcv = (Integer) evt.getValue();
            } else if (evt.getKey().equals("dc")) {
                dc = (Integer) evt.getValue();
            }

            combatLevelEntry.getProvider().setConfiguredModifiers(ocv, dcv, dc);
        }

         combatLevelEntry.markAsModified();
        
    }

    public boolean PADValueChanging(PADValueEvent evt) {
        if (evt.getKey().equals("enabled")) {
            return true;
        } else {
            int ocv = combatLevelEntry.getProvider().getConfiguredOCVModifier();
            int dcv = combatLevelEntry.getProvider().getConfiguredDCVModifier();
            int dc = combatLevelEntry.getProvider().getConfiguredDCModifier();

            if (evt.getKey().equals("ocv")) {
                ocv = (Integer) evt.getValue();
            } else if (evt.getKey().equals("dcv")) {
                dcv = (Integer) evt.getValue();
            } else if (evt.getKey().equals("dc")) {
                dc = (Integer) evt.getValue();
            }

            return combatLevelEntry.getProvider().isConfigurationLegal(ocv, dcv, dc);
        }
    }
}
