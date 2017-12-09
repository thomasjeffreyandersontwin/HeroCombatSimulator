/*
 * AttackParametersPanel.java
 *
 * Created on October 31, 2001, 11:53 PM
 */

package champions.attackTree;

import champions.Ability;
import champions.DetailListListModel;
import champions.LinkedBattleEvent;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.event.*;



/**
 *
 * @author  twalker
 */
public class LinkedSetupPanel extends JPanel implements AttackTreeInputPanel, ListSelectionListener {
    /** Stores a cached AttackParametersPanel which can be reused. */
    static protected LinkedSetupPanel ad = null;
    
    /** Stores a cached AttackParametersPanel which can be reused. */
    protected LinkedBattleEvent battleEvent = null;
    
    /** Stores the EventListenerList */
    protected EventListenerList listenerList;
    
    private Action upAction, downAction, enableAction, disableAction;
    
    /** Creates new form AttackParametersPanel */
    public LinkedSetupPanel() {
        initComponents();
        
        linkedList.setCellRenderer( new LinkedListCellRenderer("LinkedSetupPanel.checkedIcon", "LinkedSetupPanel.uncheckedIcon"));
        linkedList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        linkedList.addListSelectionListener(this);
        
        setupActions();
    }
    
    public void setupActions() {
        upAction = new AbstractAction( "Move Up" ) {
            public void actionPerformed(ActionEvent e) {
                int index = linkedList.getSelectedIndex();
                if ( index != -1 && index != 0 ) {
                    battleEvent.changeLinkedAbilityOrder(index, index - 1);
                    linkedList.setSelectedIndex(index - 1);
                    updatePanel();
                }
            }
        };
        moveUpButton.setAction(upAction);
        
        downAction = new AbstractAction( "Move Down" ) {
            public void actionPerformed(ActionEvent e) {
                int index = linkedList.getSelectedIndex();
                if ( index != -1 && index != linkedList.getModel().getSize()) {
                    battleEvent.changeLinkedAbilityOrder(index, index + 1);
                    linkedList.setSelectedIndex(index + 1);
                    updatePanel();
                }
            }
        };
        moveDownButton.setAction(downAction);
        
        enableAction = new AbstractAction( "Enable" ) {
            public void actionPerformed(ActionEvent e) {
                int selection = linkedList.getSelectedIndex();
                if ( selection != -1 ) {
                    battleEvent.setLinkedAbilityEnabled(selection, true);
                }
                updatePanel();
            }
        };
        enableButton.setAction(enableAction);
        
        disableAction = new AbstractAction( "Disable" ) {
            public void actionPerformed(ActionEvent e) {
                int selection = linkedList.getSelectedIndex();
                if ( selection != -1 ) {
                    battleEvent.setLinkedAbilityEnabled(selection, false);
                }
                updatePanel();
            }
        };
        disableButton.setAction(disableAction);
        
        linkedList.addMouseListener( new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int index = linkedList.getSelectedIndex();
                if ( e.getClickCount() == 2 && index != -1 ) {
                    battleEvent.setLinkedAbilityEnabled(index, ! battleEvent.isLinkedAbilityEnabled(index));
                }
                updatePanel();
            }
        });
        
    }
    
    static public LinkedSetupPanel getLinkedSetupPanel(LinkedBattleEvent be) {
        if ( ad == null ) ad = new LinkedSetupPanel();
        
        ad.setBattleEvent(be);
        
        // Clear any old listeners by creating a new listener list.
        ad.listenerList = new EventListenerList();
        
        return ad;
    }
    
    /** Getter for property battleEvent.
     * @return Value of property battleEvent.
     */
    public LinkedBattleEvent getBattleEvent() {
        return battleEvent;
    }
    /** Setter for property battleEvent.
     * @param battleEvent New value of property battleEvent.
     */
    public void setBattleEvent(LinkedBattleEvent battleEvent) {
        this.battleEvent = battleEvent;
    }
    
    protected void fireChangeEvent() {
        if ( listenerList != null ) {
            ChangeEvent e = new ChangeEvent(this);
            // Guaranteed to return a non-null array
            Object[] listeners = listenerList.getListenerList();
            // Process the listeners last to first, notifying
            // those that are interested in this event
            for (int i = listeners.length-2; i>=0; i-=2) {
                if (listeners[i]==ChangeListener.class) {
                    // System.out.println( "Firing chance event to: " + listeners[i+1].toString());
                    ((ChangeListener)listeners[i+1]).stateChanged(e);
                }
            }
        }
    }
    
    private void updatePanel() {
        updateControls();
        linkedList.repaint();
    }
    
    /**
     *  Adds a <code>Battle</code> listener.
     *
     *  @param l  the <code>ChangeListener</code> to add
     */
    public void addChangeListener(ChangeListener l) {
        listenerList.add(ChangeListener.class,l);
    }
    
    /**
     * Removes a <code>Battle</code> listener.
     *
     * @param l  the <code>BattleListener</code> to remove
     */
    public void removeChangeListener(ChangeListener l) {
        listenerList.remove(ChangeListener.class,l);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        continuousGroup = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        linkedList = new javax.swing.JList();
        controlGroup1 = new javax.swing.JPanel();
        moveUpButton = new javax.swing.JButton();
        moveDownButton = new javax.swing.JButton();
        enableButton = new javax.swing.JButton();
        disableButton = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        continuousGroup.setLayout(new java.awt.GridBagLayout());

        continuousGroup.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(5, 5, 0, 0)));
        jLabel1.setText("The following abilities are linked and will be triggered in the shown order:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        continuousGroup.add(jLabel1, gridBagConstraints);

        jScrollPane1.setViewportView(linkedList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        continuousGroup.add(jScrollPane1, gridBagConstraints);

        controlGroup1.setLayout(new java.awt.GridLayout(4, 1));

        moveUpButton.setText("move up");
        controlGroup1.add(moveUpButton);

        moveDownButton.setText("move down");
        controlGroup1.add(moveDownButton);

        enableButton.setText("Enable");
        controlGroup1.add(enableButton);

        disableButton.setText("Disable");
        controlGroup1.add(disableButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        continuousGroup.add(controlGroup1, gridBagConstraints);

        add(continuousGroup, java.awt.BorderLayout.CENTER);

    }//GEN-END:initComponents
    
    public void showPanel(AttackTreePanel atip) {
        setupPanel();
    }
    
    public JPanel getPanel() {
        return this;
    }
    
    public void hidePanel() {
        
    }
    
    public void setupPanel() {
        linkedList.setModel( new DetailListListModel(battleEvent, "LinkedAbility", "ABILITY") );
        updateControls();
    }
    
    public void valueChanged(ListSelectionEvent e) {
        updateControls();
    }
    
    private void updateControls() {
        
        boolean up = false;
        boolean down = false;
        boolean enable = false;
        boolean disable = false;
        
        int selection = linkedList.getSelectedIndex();
        int count = linkedList.getModel().getSize();
        
        if ( selection != -1 ) {
            if ( selection != 0 && count > 1) {
                up = true;
            }
            
            if ( selection != count - 1 && count > 1 ) {
                down = true;
            }
            
            boolean e = battleEvent.isLinkedAbilityEnabled(selection);
            
            enable = !e;
            disable = e;
        }
        
        upAction.setEnabled(up);
        downAction.setEnabled(down);
        enableAction.setEnabled(enable);
        disableAction.setEnabled(disable);
        
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel continuousGroup;
    private javax.swing.JPanel controlGroup1;
    private javax.swing.JButton disableButton;
    private javax.swing.JButton enableButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList linkedList;
    private javax.swing.JButton moveDownButton;
    private javax.swing.JButton moveUpButton;
    // End of variables declaration//GEN-END:variables
    
    public class LinkedListCellRenderer extends JLabel implements ListCellRenderer {
        
        private Icon checkedIcon = null;
        private Icon uncheckedIcon = null;
        
        public LinkedListCellRenderer(String checkedIconProperty, String uncheckedIconProperty) {
            checkedIcon = UIManager.getIcon(checkedIconProperty);
            uncheckedIcon = UIManager.getIcon(uncheckedIconProperty);
        }
        // This is the only method defined by ListCellRenderer.
        // We just reconfigure the JLabel each time we're called.
        
        public Component getListCellRendererComponent(
        JList list,
        Object value,            // value to display
        int index,               // cell index
        boolean isSelected,      // is the cell selected
        boolean cellHasFocus)    // the list and the cell have the focus
        {
            String s = value.toString();
            setText(s);
            
            if ( battleEvent != null ) {
                if ( battleEvent.isLinkedAbilityEnabled(index) ) {
                    setIcon(checkedIcon);
                }
                else {
                    setIcon(uncheckedIcon);
                }
            }
            
            if ( isSelected ) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            }
            else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setOpaque(true);
            return this;
        }
    }
    
    public class LinkedListModel extends AbstractListModel {
        
        public Object getElementAt(int index) {
            if ( battleEvent != null ) {
                Ability ability = battleEvent.getLinkedAbility(index);
                Ability maneuver = battleEvent.getLinkedManeuver(index);
                
                if ( maneuver != null ) {
                    return ability.getName() + " with maneuver " + maneuver.getName();
                }
                else {
                    return ability.getName();
                }
            }
            return "ERROR";
        }
        
        public int getSize() {
            return (battleEvent == null) ? 0 : battleEvent.getLinkedAbilityCount();
        }
        
    }
}
