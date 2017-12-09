/*
 * InlinePane.java
 *
 * Created on February 18, 2001, 3:35 PM
 */

package champions;

import champions.event.SegmentAdvancedEvent;
import champions.event.SequenceChangedEvent;
import champions.event.TargetSelectedEvent;
import champions.interfaces.BattleListener;
import champions.interfaces.InlinePanel;
import dockable.DockingPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;


/**
 *
 * @author  unknown
 * @version
 */
public class InlineView extends javax.swing.JPanel
implements BattleListener, PropertyChangeListener {
    
    /** Holds value of property battleMode. */
    protected  boolean battleMode;
    /** Holds value of property processingMode. */
    protected  boolean processingMode;
    /** Holds value of property viewState. */
    protected  int viewState = UNINITIALIZED;
    
    protected Vector battleInlineVector = new Vector();
    protected Object[] buttonArray;
    protected JButton battleButton[];
    
    protected Value value = new Value();
    
    protected static final int MAXBUTTONS = 4;
    protected static final int UNINITIALIZED = -2;
    public static final int NORMAL_STATE = 1;
    public static final int BATTLE_STATE = 2;
    public static final int BLANK_STATE = 3;
    
    /** Holds value of property normalTitle. */
    private String normalTitle = "InlineView";
    /** Holds value of property dockingPanel. */
    private DockingPanel dockingPanel = null;
    /** Creates new form InlinePane */
    public InlineView() {
        initComponents();
        battleButton = new JButton[] { battleButton1, battleButton2, battleButton3, battleButton4 };
        
        battleView.setVisible(false);
        blankView.setVisible(false);
        normalView.setVisible(true);
        this.revalidate();
        viewState = NORMAL_STATE;
        
        Battle.addBattleListener(this);
        battleTab.addIndexChangeListener( this );
    }
    
    public  void reset() {
        if ( getValue() == UNINITIALIZED ) {
            setValue( JOptionPane.CLOSED_OPTION );
        }
        
        setBattleMode(false);
        
        battleInlineVector.clear();
    }
    
    public  void addBattlePanel(InlinePanel ip) {
        battleInlineVector.add(ip);
    }
    
    /** Displays the BattleView and waits for a final result.
     *
     * This method displays the BattleView.  It will then block until a final result is 
     * generated.  This method should not be called from the EventDispatch Thread, as it 
     * will block that thread and lock the gui.
     */
    public  int showBattleViewAndWait() {
        if ( battleInlineVector.size() == 0 ) {
            return JOptionPane.CANCEL_OPTION;
        }
        
        setValue( UNINITIALIZED );
        setupBattleView();
        setBattleMode(true);
        int result = value.waitForValue();
        reset();
        return result;
    }
    
    /** Displays the BattleView without waiting for a result.
     *
     * This method displays the BattleView.  It will not wait for a final result
     * to be generated.  The final result must be gathered in a different method.
     *
     * This method can be called from any thread.
     */
    public void showBattleView() {
        if ( battleInlineVector.size() == 0 ) {
            return;
        }
        
        setValue( UNINITIALIZED );
        setupBattleView();
        setBattleMode(true);
    }
    
    /** Hide the battleView if it is currently visible. 
     * 
     * This method will check the state of the battleView.  If it is visible, it will
     * hide it, switching to the blankView.  It will also set the result of the BattleView
     * to be the indicated value.
     */
    public void hideBattleView(int value) {
        if ( viewState == BATTLE_STATE ) {
            setValue(value);
            reset();
        }
    }
    
    protected void updateState() {
        if ( viewState == BATTLE_STATE  ) {
            if ( isBattleMode() == false && isProcessingMode() == true ) {
                switchToBlankView();
            }
            else if ( isBattleMode() == false && isProcessingMode() == false ) {
                switchToNormalView();
            }
        }
        else if ( viewState == BLANK_STATE ) {
            if ( isBattleMode() == true ) {
                switchToBattleView();
            }
            else if ( isProcessingMode() == false ) {
                switchToNormalView();
            }
        }
        else if ( viewState == NORMAL_STATE ) {
            if ( isBattleMode() == true ) {
                switchToBattleView();
            }
        }
    }
    
    protected void setupBattleView() {
        if ( SwingUtilities.isEventDispatchThread() == false )  {
            try {
                SwingUtilities.invokeAndWait( new Runnable() {
                    public void run() {
                        setupBattleView();
                    }
                });
            }
            catch ( Exception exc ) {
                System.err.println( exc.toString() );
            }
            return;
        }
        
        battleTab.removeAllTabs();
        Iterator i = battleInlineVector.iterator();
        while ( i.hasNext() ) {
            InlinePanel ip = (InlinePanel)i.next();
            battleTab.addTab( ip.getTitle() );
            
        }
        
        // Setup Buttons
        if ( battleInlineVector.size() > 0 ) {
            InlinePanel ip = (InlinePanel)battleInlineVector.get(0);
            buttonArray = ip.getButtons();
            
            int k,j;
            for (k=0,j=0; k<buttonArray.length;k+=2,j++) {
                final int newValue = ((Integer)buttonArray[k+1]).intValue();
                battleButton[j].setAction( new AbstractAction( (String) buttonArray[k] ) {
                    public void actionPerformed(ActionEvent e) {
                        // Give the Inline panels a chance to respond to the button...
                        boolean fireSetValue = true;
                        Iterator i = battleInlineVector.iterator();
                        while ( i.hasNext() ) {
                            InlinePanel ip = (InlinePanel)i.next();
                            if ( ip.buttonPressed(InlineView.this, newValue) == false ) {
                                fireSetValue = false;
                            }
                        }
                        
                        if ( fireSetValue == true ) {
                            value.setValue( newValue );
                        }
                    }
                });
                battleButton[j].setVisible(true);
            }
            for(;j<MAXBUTTONS;j++) {
                battleButton[j].setVisible(false);
            }
        }
        
        selectBattlePanel(0);
    }
    
    protected void selectBattlePanel(int index) {
        if ( SwingUtilities.isEventDispatchThread() == false ) {
            System.err.println("selectBattlePanel performed outside event thread!");
            Thread.dumpStack();
            return;
        }
        
        if ( index < 0 || index > battleInlineVector.size() ) {
            return;
        }
        
        if ( battleTab.getSelectedIndex() != index ) {
            battleTab.setSelectedIndex(index);
        }
        
        InlinePanel ip = (InlinePanel)battleInlineVector.get(index);
        
        if ( ip.requiresScroll(this) ) {
            boolean found = false;
            Component[] c = scrollGroup.getComponents();
            for (int i = 0; i < c.length; i++) {
                if ( c[i] == battleScroll ) {
                    found = true;
                }
                else {
                    scrollGroup.remove(c[i]);
                }
            }
            
            if ( ! found ) {
                scrollGroup.add(battleScroll, java.awt.BorderLayout.CENTER);
            }
            
            battleScroll.getViewport().setView( ip.getInlineComponent() );
            
            
        }
        else {
            Component newPanel = ip.getInlineComponent();
            
            boolean found = false;
            Component[] c = scrollGroup.getComponents();
            for (int i = 0; i < c.length; i++) {
                if ( c[i] == newPanel ) {
                    found = true;
                }
                else {
                    scrollGroup.remove(c[i]);
                }
                
                if ( ! found ) {
                    scrollGroup.add(newPanel, java.awt.BorderLayout.CENTER);
                }
            }
        }
        
        updateTitle( ip.getTitle() );
        
    }
    
    public void addNormalPanel(Component c) {
        normalView.removeAll();
        normalView.add(c, BorderLayout.CENTER );
    }
    
    protected void switchToBattleView() {
        if ( SwingUtilities.isEventDispatchThread() == false ) {
            System.err.println("switchToBattleView performed outside event thread!");
            Thread.dumpStack();
            return;
        }
        
        if ( viewState != BATTLE_STATE ) {
            normalView.setVisible(false);
            blankView.setVisible(false);
            battleView.setVisible(true);
            this.revalidate();
            
            activateBattleInlines();
            
            viewState = BATTLE_STATE;
            
            updateTitle( getCurrentBattleTitle() );
        }
    }
    
    protected void switchToBlankView() {
        if ( SwingUtilities.isEventDispatchThread() == false ) {
            System.err.println("switchToBlankView performed outside event thread!");
            Thread.dumpStack();
            return;
        }
        
        if ( viewState != BLANK_STATE ) {
            if ( viewState == BATTLE_STATE ) {
                deactivateBattleInlines();
            }
            
            normalView.setVisible(false);
            battleView.setVisible(false);
            blankView.setVisible(true);
            this.revalidate();
            
            viewState = BLANK_STATE;
            
        }
    }
    
    protected void switchToNormalView() {
        if ( SwingUtilities.isEventDispatchThread() == false ) {
            System.err.println("switchToNormalView performed outside event thread!");
            Thread.dumpStack();
            return;
        }
        
        if ( viewState != NORMAL_STATE ) {
            if ( viewState == BATTLE_STATE ) {
                deactivateBattleInlines();
            }
            
            battleView.setVisible(false);
            blankView.setVisible(false);
            normalView.setVisible(true);
            this.revalidate();
            
            viewState = NORMAL_STATE;
            
            updateTitle(normalTitle);
        }
    }
    
    protected void activateBattleInlines() {
        Iterator i = battleInlineVector.iterator();
        while ( i.hasNext() ) {
            InlinePanel ip = (InlinePanel)i.next();
            ip.activateInline(this);
        }
    }
    
    protected void deactivateBattleInlines() {
        Iterator i = battleInlineVector.iterator();
        while ( i.hasNext() ) {
            InlinePanel ip = (InlinePanel)i.next();
            ip.deactivateInline(this, value.getValue());
        }
    }
    
    public static InlineView defaultInlineView = null;
    static public InlineView getDefaultInlineView() {
        if ( defaultInlineView == null ) {
            defaultInlineView = new InlineView();
        }
        return defaultInlineView;
    }
    
    public void updateTitle(String title) {
        if ( dockingPanel != null && title != null && title.equals("") == false) {
            dockingPanel.setName( title );
        }
    }
    
    protected String getCurrentBattleTitle() {
        int index = battleTab.getSelectedIndex();
        if ( index > 0 && index < battleInlineVector.size() ) {
            InlinePanel ip = (InlinePanel)battleInlineVector.get(index);
            return ip.getTitle();
        }
        return "";
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        battleView = new javax.swing.JPanel();
        scrollGroup = new javax.swing.JPanel();
        battleScroll = new javax.swing.JScrollPane();
        battleTabGroup = new javax.swing.JPanel();
        battleTab = new tjava.TabControl();
        battleButtonGroup = new javax.swing.JPanel();
        battleButton1 = new javax.swing.JButton();
        battleButton2 = new javax.swing.JButton();
        battleButton3 = new javax.swing.JButton();
        battleButton4 = new javax.swing.JButton();
        blankView = new javax.swing.JPanel();
        normalView = new javax.swing.JPanel();
        
        setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints1;
        
        battleView.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gridBagConstraints2;
        
        scrollGroup.setLayout(new java.awt.BorderLayout());
        
        scrollGroup.add(battleScroll, java.awt.BorderLayout.CENTER);
        
        gridBagConstraints2 = new java.awt.GridBagConstraints();
        gridBagConstraints2.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints2.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.weighty = 1.0;
        battleView.add(scrollGroup, gridBagConstraints2);
        
        battleTabGroup.setLayout(new java.awt.BorderLayout());
        
        battleTabGroup.add(battleTab, java.awt.BorderLayout.CENTER);
        
        gridBagConstraints2 = new java.awt.GridBagConstraints();
        gridBagConstraints2.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints2.anchor = java.awt.GridBagConstraints.NORTHWEST;
        battleView.add(battleTabGroup, gridBagConstraints2);
        
        battleButtonGroup.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        
        battleButton1.setText("jButton1");
        battleButtonGroup.add(battleButton1);
        
        battleButton2.setText("jButton2");
        battleButtonGroup.add(battleButton2);
        
        battleButton3.setText("jButton3");
        battleButtonGroup.add(battleButton3);
        
        battleButton4.setText("jButton4");
        battleButtonGroup.add(battleButton4);
        
        gridBagConstraints2 = new java.awt.GridBagConstraints();
        gridBagConstraints2.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints2.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints2.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints2.weightx = 1.0;
        battleView.add(battleButtonGroup, gridBagConstraints2);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.weighty = 1.0;
        add(battleView, gridBagConstraints1);
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.weighty = 1.0;
        add(blankView, gridBagConstraints1);
        
        normalView.setLayout(new java.awt.BorderLayout());
        
        gridBagConstraints1 = new java.awt.GridBagConstraints();
        gridBagConstraints1.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.weighty = 1.0;
        add(normalView, gridBagConstraints1);
        
    }//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel battleView;
    private javax.swing.JPanel scrollGroup;
    private javax.swing.JScrollPane battleScroll;
    private javax.swing.JPanel battleTabGroup;
    private tjava.TabControl battleTab;
    private javax.swing.JPanel battleButtonGroup;
    private javax.swing.JButton battleButton1;
    private javax.swing.JButton battleButton2;
    private javax.swing.JButton battleButton3;
    private javax.swing.JButton battleButton4;
    private javax.swing.JPanel blankView;
    private javax.swing.JPanel normalView;
    // End of variables declaration//GEN-END:variables
    
    /** Getter for property battleMode.
     * @return Value of property battleMode.
     */
    public boolean isBattleMode() {
        return battleMode;
    }
    /** Setter for property battleMode.
     * @param battleMode New value of property battleMode.
     */
    public void setBattleMode(final boolean battleMode) {
        if ( this.battleMode != battleMode ) {
            if ( SwingUtilities.isEventDispatchThread() == false ) {
                try {
                    SwingUtilities.invokeAndWait( new Runnable() {
                        public void run() {
                            setBattleMode( battleMode );
                        }
                    });
                }
                catch( Exception e ) {
                    System.err.println("Exception caught during invokeAndWait from setBattleMode: " + e.toString() );
                }
            }
            else {
                this.battleMode = battleMode;
                updateState();
            }
        }
    }
    /** Getter for property processingMode.
     * @return Value of property processingMode.
     */
    public boolean isProcessingMode() {
        return processingMode;
    }
    /** Setter for property processingMode.
     * @param processingMode New value of property processingMode.
     */
    public void setProcessingMode(final boolean processingMode) {
        if ( this.processingMode != processingMode ) {
            if ( SwingUtilities.isEventDispatchThread() == false ) {
                try {
                    SwingUtilities.invokeAndWait( new Runnable() {
                        public void run() {
                            setProcessingMode( processingMode );
                        }
                    });
                }
                catch( Exception e ) {
                    System.err.println("Exception caught during invokeAndWait from setProcessingMode: " + e.toString() );
                }
            }
            else {
                this.processingMode = processingMode;
                updateState();
            }
        }
    }
    /** Getter for property viewState.
     * @return Value of property viewState.
     */
    public int getViewState() {
        return viewState;
    }
    /** Setter for property viewState.
     * @param viewState New value of property viewState.
     */
    public void setViewState(int viewState) {
        this.viewState = viewState;
    }
    /** Getter for property value.
     * @return Value of property value.
     */
    public int getValue() {
        return value.getValue();
    }
    /** Setter for property value.
     * @param value New value of property value.
     */
    public void setValue(int value) {
        this.value.setValue(value);
    }
    
    public void battleTargetSelected(TargetSelectedEvent e) {
    }
    public void battleSegmentAdvanced(SegmentAdvancedEvent e) {
    }
    public void battleSequenceChanged(SequenceChangedEvent e) {
    }
    public void stateChanged(BattleChangeEvent e) {
    }
    public void eventNotification(ChangeEvent e) {
    }
    public void combatStateChange(ChangeEvent e) {
    }
    public void processingChange(BattleChangeEvent event) {
        setProcessingMode( Battle.currentBattle.isProcessing() );
    }
    
    
    /** This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source
     *  	and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if ( evt.getSource() == battleTab ) {
            selectBattlePanel( ((Integer)evt.getNewValue()).intValue() );
        }
    }
    
    /** Getter for property normalTitle.
     * @return Value of property normalTitle.
     */
    public String getNormalTitle() {
        return normalTitle;
    }
    /** Setter for property normalTitle.
     * @param normalTitle New value of property normalTitle.
     */
    public void setNormalTitle(String normalTitle) {
        this.normalTitle = normalTitle;
        if ( viewState == NORMAL_STATE ) {
            updateTitle(normalTitle);
        }
    }
    
    /** Getter for property dockingPanel.
     * @return Value of property dockingPanel.
     */
    public DockingPanel getDockingPanel() {
        return dockingPanel;
    }
    /** Setter for property dockingPanel.
     * @param dockingPanel New value of property dockingPanel.
     */
    public void setDockingPanel(DockingPanel dockingPanel) {
        this.dockingPanel = dockingPanel;
    }
    
    public class Value extends Object {
        /** Holds value of property value. */
        private int value;
        public Value() {
            value = UNINITIALIZED;
        }
        
        /** Getter for property value.
         * @return Value of property value.
         */
        public synchronized int getValue() {
            return value;
        }
        /** Setter for property value.
         * @param value New value of property value.
         */
        public synchronized void setValue(int value) {
            if ( this.value != value ) {
                this.value = value;
                notifyAll();
            }
        }
        
        public synchronized int waitForValue() {
            while ( true ) {
                if ( getValue() != UNINITIALIZED ) break;
                try {
                    wait();
                }
                catch( InterruptedException ie ) {
                    
                }
            }
            return value;
        }
    }
}