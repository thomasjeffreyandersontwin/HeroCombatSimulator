/*
 * TargetButton.java
 *
 * Created on October 22, 2000, 3:22 PM
 */

package champions;

import champions.exception.BattleEventException;
import tjava.ContextMenuListener;
import champions.powers.effectGeneric;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import tjava.Destroyable;
import champions.Effect;
import champions.powers.effectHipshot;

/**
 *
 * @author  unknown
 * @version
 */
public class TargetButton2 extends JPanel
        implements ContextMenuListener, ActionListener, MouseListener, ChangeListener, Destroyable {
    
    /** Holds value of property target. */
    private Target target;
    /** Holds value of property model. */
    private ButtonModel model;
    private Action editTarget,debugTarget, saveAction, saveAsAction, abortAction,hipshotAction;
    private Action addGenericEffect;
    private Action showSenses;
    static protected HealTargetAction healAction;
    
    
    /** Stores the total number of buttonUpdates that occur
     * in any TargetButton.  Used to watch for excess updates.
     */
    static public int buttonUpdates = 0;
    /** Indicates extra debug should be done.
     */
    static public final int DEBUG = 0;
    
    
    
    /** Creates new form TargetButton */
    
    public TargetButton2() {
        initComponents();
        setupIcons();
        targetStats.setLayout(null);
        
        targetStats.setForeground( getForeground() );
        targetStats.setBackground( getBackground() );
        effectPanel.setForeground( getForeground() );
        effectPanel.setBackground( getBackground() );
        
        setModel( new TargetButtonModel()  );
        
        this.addMouseListener(this);
        
        setupActions();
        
        effectPanel.setVisible(false);
        
        
        // setupIcons();
        
    }
    
    private void setupIcons() {
        Icon i = UIManager.getIcon("ToggleButton.rightArrowIcon");
        toggleButton.setSelectedIcon(i);
        
        i = UIManager.getIcon("ToggleButton.diagArrowIcon");
        toggleButton.setPressedIcon(i);
        
        i = UIManager.getIcon("ToggleButton.downArrowIcon");
        toggleButton.setIcon(i);
    }
    
    
    /** sets up actions which will be used in the invokeMenu method.
     *
     */
    private void setupActions() {
        editTarget = new AbstractAction("Edit Character...") {
            public void actionPerformed(ActionEvent e) {
                //  if ( target != null ) target.editTarget();
                if ( target != null ) target.editTarget();
            }
        };
        
        debugTarget = new AbstractAction("Debug Character...") {
            public void actionPerformed(ActionEvent e) {
                if ( target != null ) target.debugDetailList( "Debug" + target.getName() );
            }
        };
        
        saveAction = new AbstractAction("Save Character") {
            public void actionPerformed(ActionEvent e) {
                if ( target != null ) {
                    if ( target.getFile() != null ) {
                        try {
                            target.save();
                        } catch (Exception exc) {
                            JOptionPane.showMessageDialog(null,
                                    "An Error Occurred while saving target:\n" +
                                    exc.toString(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        };
        
        saveAsAction = new AbstractAction("Save Character As...") {
            public void actionPerformed(ActionEvent e) {
                if ( target != null ) {
                    try {
                        target.save(null);
                    } catch (Exception exc) {
                        JOptionPane.showMessageDialog(null,
                                "An Error Occurred while saving character:\n" +
                                exc.toString(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        };
        
        abortAction = new AbstractAction("Abort next action...") {
            public void actionPerformed(ActionEvent e) {
                if ( target != null && target.isAbortable() ) {
                    BattleEvent be = new BattleEvent(BattleEvent.ACTIVE_TARGET, target);
                    be.addCombatStateEvent(target, target.getCombatState(), CombatState.STATE_ABORTING );
                    target.setCombatState( CombatState.STATE_ABORTING );
                    Battle.currentBattle.addEvent(be);
                }
            }
        };
        
        
        hipshotAction = new AbstractAction("Hipshot") {
            public void actionPerformed(ActionEvent e) {
                if ( target != null && target.isAbortable() ) {
                    Effect effect =  new effectHipshot();
                    BattleEvent be = new BattleEvent( BattleEvent.ADD_EFFECT, effect, target);
                    
                    if ( Battle.currentBattle != null || !Battle.currentBattle.isStopped()) {
                        Battle.currentBattle.addEvent(be);
                    } else {
                        try {
                            effect.addEffect(be, target);
                        } catch ( BattleEventException bee) {
                            be.displayBattleError(bee);
                        }
                    }
                }
            }
        };
        
        addGenericEffect = new AbstractAction("Add Generic Effect...") {
            public void actionPerformed(ActionEvent e) {
                if ( target != null ) {
                    
                    effectGeneric eg = new effectGeneric("New Effect");
                    BattleEvent be = new BattleEvent( BattleEvent.ADD_EFFECT, eg, target);
                    
                    if ( Battle.currentBattle != null || !Battle.currentBattle.isStopped()) {
                        Battle.currentBattle.addEvent(be);
                    } else {
                        try {
                            eg.addEffect(be, target);
                        } catch ( BattleEventException bee) {
                            be.displayBattleError(bee);
                        }
                    }
                    GenericEffectDetail ged = new GenericEffectDetail( eg );
                    ged.showEffectDetail( null );
                }
            }
        };
        
        showSenses = new AbstractAction("Show Senses...") {
            public void actionPerformed(ActionEvent e) {
                if ( target != null ) {
                    target.displaySenseWindow();
                }
            }
        };
        
        if ( healAction == null ) healAction = new HealTargetAction();
        
        
    }
    
    /** Getter for property target.
     * @return Value of property target.
     */
    public Target getTarget() {
        return target;
    }
    /** Setter for property target.
     * @param target New value of property target.
     */
    public void setTarget(Target target) {
        if ( this.target != target ) {
            this.target = target;
            if ( model != null && model instanceof TargetButtonModel ) {
                ((TargetButtonModel)model).setTarget(target);
            }
            targetStats.setTarget(target);
            EffectListModel elm = new EffectListModel(target);
            elm.setFilter(Effect.ALL);
            effectPanel.setModel(elm);
        }
    }
    
    /** Setter for property columns.
     * @param columns New value of property columns.
     */
    public void setStatList(DetailList detailList) {
        targetStats.setStatList(detailList);
    }
    
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e) ) {
            if(this.contains(e.getX(), e.getY())) {
                ButtonModel model = getModel();
                if (!model.isEnabled()) {
                    // Disabled buttons ignore all input...
                    return;
                }
                if (!model.isArmed()) {
                    // button not armed, should be
                    model.setArmed(true);
                }
                model.setPressed(true);
                if(!this.hasFocus()) {
                    this.requestFocus();
                }
            }
        }
    }
    
    /** Invoked when a mouse button has been released on a component.
     */
    public void mouseReleased(MouseEvent e) {
        if(!this.contains(e.getX(), e.getY())) {
            ButtonModel model = this.getModel();
            if( false ) {
                model.setRollover(false);
            }
            model.setArmed(false);
        }
        ButtonModel model = this.getModel();
        model.setPressed(false);
    }
    /** Invoked when the mouse enters a component.
     */
    public void mouseEntered(MouseEvent e) {
        if(this.contains(e.getX(), e.getY())) {
            ButtonModel model = this.getModel();
            if( false ) {
                model.setRollover(true);
            }
            model.setArmed(true);
        }
    }
    /** Invoked when the mouse exits a component.
     */
    public void mouseExited(MouseEvent e) {
        if(!this.contains(e.getX(), e.getY())) {
            ButtonModel model = this.getModel();
            if( false ) {
                model.setRollover(false);
            }
            model.setArmed(false);
        }
    }
    
    public void mouseClicked(MouseEvent e) {
        
    }
    /** Getter for property model.
     * @return Value of property model.
     */
    public ButtonModel getModel() {
        return model;
    }
    /** Setter for property model.
     * @param model New value of property model.
     */
    public void setModel(ButtonModel model) {
        if ( this.model != null ) {
            this.model.removeActionListener(this);
        }
        this.model = model;
        targetStats.setModel( model);
        if ( this.model != null ) {
            this.model.addActionListener(this);
        }
    }
    /** Invoked when the target of the listener has changed its state.
     *
     * @param e  a ChangeEvent object
     */
    public void stateChanged(ChangeEvent e) {
        
    }
    /** Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        //editTarget(target);
        editTarget.actionPerformed(e);
    }
    
    public boolean invokeMenu(JPopupMenu popup,Component inComponent,Point inPoint) {
        if ( target != null ) {
            popup.add( abortAction );
            abortAction.setEnabled( target.isAbortable() );
            popup.add( hipshotAction );
            hipshotAction.setEnabled( target.isAbortable() );
            popup.addSeparator();
            popup.add( addGenericEffect);
            popup.addSeparator();
            
            healAction.setTarget(target );
            popup.add(healAction);
            
            popup.add( editTarget );
            
            popup.add( showSenses );
            if ( Battle.debugLevel >= 1) {
                popup.add( debugTarget);
                popup.addSeparator();
            }
            popup.add( saveAction);
            popup.add( saveAsAction);
            saveAction.setEnabled( ( target.getFile() == null ) ? false : true);
            return true;
        }
        return false;
    }
    
    public void addMouseListener(MouseListener ml) {
        super.addMouseListener(ml);
        targetStats.addMouseListener(ml);
    }
    
    public void removeMouseListener(MouseListener ml) {
        super.removeMouseListener(ml);
        targetStats.removeMouseListener(ml);
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        toggleButton = new javax.swing.JToggleButton();
        targetStats = new champions.TargetButtonStats();
        effectPanel = new champions.HTMLButtonPanel();
        effectsLabel = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        setName("Target");
        toggleButton.setBorderPainted(false);
        toggleButton.setContentAreaFilled(false);
        toggleButton.setFocusPainted(false);
        toggleButton.setMaximumSize(new java.awt.Dimension(12, 12));
        toggleButton.setMinimumSize(new java.awt.Dimension(12, 12));
        toggleButton.setPreferredSize(new java.awt.Dimension(12, 12));
        toggleButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                toggleButtonItemStateChanged(evt);
            }
        });
        toggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(toggleButton, gridBagConstraints);

        targetStats.setLayout(new java.awt.FlowLayout());

        targetStats.setFont(new java.awt.Font("Arial", 0, (int) (22 * GlobalFontSettings.SizeMagnification)));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.01;
        add(targetStats, gridBagConstraints);

        effectPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 2));

        effectsLabel.setFont(new java.awt.Font("Arial", 0, (int) (22 * GlobalFontSettings.SizeMagnification)));
        effectsLabel.setText("Effects:  ");
        effectPanel.add(effectsLabel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(effectPanel, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents

    private void toggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleButtonActionPerformed

    }//GEN-LAST:event_toggleButtonActionPerformed
    
  private void toggleButtonItemStateChanged (java.awt.event.ItemEvent evt) {//GEN-FIRST:event_toggleButtonItemStateChanged
      // Add your handling code here:
      if ( evt.getStateChange() == ItemEvent.SELECTED ) {
          effectPanel.setVisible(true);
      } else {
          effectPanel.setVisible(false);
      }
  }//GEN-LAST:event_toggleButtonItemStateChanged
  
  /** Getter for property shadow.
   * @return Value of property shadow.
   */
  public Color getShadow() {
      return shadow;
  }
  
  /** Setter for property shadow.
   * @param shadow New value of property shadow.
   */
  public void setShadow(Color shadow) {
      this.shadow = shadow;
  }
  
  /** Setter for property normalForeground.
   * @param normalForeground New value of property normalForeground.
   */
  public void setForeground(Color fg) {
      super.setForeground(fg);
      if ( targetStats != null ) targetStats.setForeground(fg);
      if ( effectPanel != null ) effectPanel.setForeground( getForeground() );
  }
  
  public void setBackground(Color bg) {
      super.setBackground(bg);
      if ( targetStats != null ) targetStats.setBackground(bg);
      if ( effectPanel != null ) effectPanel.setBackground( getBackground() );
  }
  
  public void destroy() {
      setTarget(null);
      targetStats.destroy();
      
  }
  
  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private champions.HTMLButtonPanel effectPanel;
    private javax.swing.JLabel effectsLabel;
    private champions.TargetButtonStats targetStats;
    private javax.swing.JToggleButton toggleButton;
    // End of variables declaration//GEN-END:variables
    
    /** Holds value of property shadow. */
    private Color shadow;
    
    /** Holds value of property normalForeground. */
    private Color normalForeground;
    
    /** Generate the BattleEvent containing a ExecuteHealRosterAction.
     */
    public static class HealTargetAction extends AbstractAction {
        Target target = null;
        
        public HealTargetAction() {
            super("Heal Target");
        }
        
        /** Invoked when an action occurs.
         *
         */
        public void actionPerformed(ActionEvent e) {
            if ( target != null ) {
                ExecuteHealTargetAction a = new ExecuteHealTargetAction();
                BattleEvent battleEvent = new BattleEvent(a);
                a.setBattleEvent(battleEvent);
                a.setTarget(target);
                
                Battle.getCurrentBattle().addEvent(battleEvent);
            }
        }
        
        /** Getter for property target.
         * @return Value of property target.
         *
         */
        public Target getTarget() {
            return target;
        }
        
        /** Setter for property target.
         * @param target New value of property target.
         *
         */
        public void setTarget(Target target) {
            this.target = target;
            if ( target != null ) {
                putValue( Action.NAME, "Heal " + target.getName() );
            }
        }
        
        public boolean isEnabled() {
            return target != null;
        }
    }
    
    public static class ExecuteHealTargetAction extends AbstractAction {
        BattleEvent battleEvent = null;
        Target target = null;
        
        public ExecuteHealTargetAction() {
            super("Heal Target");
        }
        
        public void setBattleEvent(BattleEvent be) {
            battleEvent = be;
        }
        
        public void setTarget(Target target) {
            this.target = target;
        }
        
        public void actionPerformed(ActionEvent evt) {
            target.healCompletely(battleEvent);
        }
    }
    
}