/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treeTable;

import java.awt.Component;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

/**
 *
 * @author twalker
 */
public class TreeTableKeyMapHandler implements TreeSelectionListener {

    TreeTable treeTable;

    ActionMap actionMap;

    InputMap inputMap;

    public TreeTableKeyMapHandler(TreeTable treeTable) {
        setTreeTable(treeTable);
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        if ( actionMap != null && inputMap != null ) {
            TreeTableModel ttm = treeTable.getProxyTreeTableModel();

            if ( ttm != null ) {

                actionMap.clear();
                inputMap.clear();
                
                JPopupMenu menu = new JPopupMenu();
                boolean changed = ttm.invokeMenu(treeTable, e.getPaths(), menu);

                if ( changed ) {
                    buildMaps(menu);
                }
            }
        }
    }

    private void buildMaps(JComponent menu) {
        for(int i = 0; i < menu.getComponentCount(); i++) {
            Component c = menu.getComponent(i);

            if ( c instanceof JPopupMenu ) {
                buildMaps((JPopupMenu)c);
            }
            else if (c instanceof JMenuItem) {
                JMenuItem menuItem = (JMenuItem) c;
                KeyStroke key = menuItem.getAccelerator();
                Action action = menuItem.getAction();

                if ( key != null && action != null ) {
                    Object existingMapping = inputMap.get(key);
                    if ( existingMapping == null ) {
                        Object actionMapKey = action.getValue(Action.ACTION_COMMAND_KEY);
                        inputMap.put(key, actionMapKey != null ? actionMapKey : action);
                        actionMap.put(actionMapKey != null ? actionMapKey : action, action);
                    }
                }
            }
        }
    }

    public TreeTable getTreeTable() {
        return treeTable;
    }

    public void setTreeTable(TreeTable treeTable) {
        if (this.treeTable != treeTable) {
            if (this.treeTable != null) {
                this.treeTable.removeTreeSelectionListener(this);
                removeActionAndInputMaps();
            }

            this.treeTable = treeTable;

            
            if (this.treeTable != null) {
                this.treeTable.addTreeSelectionListener(this);
                insertActionAndInputMaps();
            }
        }
    }

    private void replaceActionAndInputMaps() {
        removeActionAndInputMaps();
        insertActionAndInputMaps();
    }

    private void insertActionAndInputMaps() {
        actionMap = new ActionMap();
        ActionMap treeTableMap = treeTable.getActionMap();

        if ( treeTableMap != null ) {
            actionMap.setParent(treeTableMap);
        }

        treeTable.setActionMap(actionMap);

        inputMap = new InputMap();
        InputMap treeTableInputMap = treeTable.getInputMap(JComponent.WHEN_FOCUSED);

        if ( treeTableInputMap != null ) {
            inputMap.setParent(treeTableInputMap);
        }

        treeTable.setInputMap(JComponent.WHEN_FOCUSED, inputMap);
    }

    private void removeActionAndInputMaps() {
        if ( actionMap != null ) {
            ActionMap treeTableMap = treeTable.getActionMap();

            if ( treeTableMap == actionMap ) {
                treeTable.setActionMap(actionMap.getParent());
            }

            actionMap = null;
        }

        if ( inputMap != null ) {
            InputMap treeTableMap = treeTable.getInputMap(JComponent.WHEN_FOCUSED);

            if ( treeTableMap == inputMap ) {
                treeTable.setInputMap(JComponent.WHEN_FOCUSED, inputMap.getParent());
            }

            inputMap = null;
        }
    }


}
