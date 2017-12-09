/*
 * TargetEditor.java
 *
 * Created on November 14, 2000, 1:25 AM
 */

package champions;

import champions.ColumnList;
import tjava.ContextMenu;
import tjava.ContextMenuListener;
import champions.abilityTree.PADTree.PADTreeTable;
import javax.swing.*;
import java.beans.*;
import java.awt.*;
import java.awt.event.*;
import champions.interfaces.*;
import champions.event.*;

import champions.abilityTree.*;

import tjava.*;
/**
 *
 * @author  unknown
 * @version
 */
public class TargetEditorPanel extends javax.swing.JPanel implements PropertyChangeListener, PADValueListener, ContextMenuListener, Destroyable, ComponentListener {
    
    /** Holds value of property target. */ 
    private Target target;
    
    private Action saveAction, saveAsAction, debugTarget, reconfigureAction, imageAction;
    
    private boolean abilitiesOpen = false;
    private boolean editorOpen = false;
    
    private TargetEditorAbilityPanel targetEditorAbilityPanel = null;
    private TargetEditorStatPanel  targetEditorStatPanel = null;
    private PADTreeTable padTreeTable = null;
    
    
    /** Creates new form TargetEditor */
    public TargetEditorPanel() {
        // Check to make sure the PADRoster is initialized, since everything will
        // blow up if it isn't.  This is usually only necessary when we are working
        // inside the IDE with forms or beans.
        if ( PADRoster.isInitialized() == false ) {
            PADRoster.initialize();
        }
        
        initComponents();
        
        targetEditorStatPanel = new TargetEditorStatPanel();
        statGroup.add(targetEditorStatPanel, BorderLayout.CENTER);
        
        abilityLabel.setVisible(false);
        editToggle.setVisible(false);
        abilityGroup.setVisible(false);
        editGroup.setVisible(false);
        editLabel.setVisible(false);
        
        // Setup Context Menus
        ContextMenu.addContextMenu(targetEditorStatPanel);
        ContextMenu.addContextMenu(statLabel);
        ContextMenu.addContextMenu(abilityLabel);
        ContextMenu.addContextMenu(editLabel);
        ContextMenu.addContextMenu(statToggle);
        //    column1.addMouseListener(contextMenu);
        //   column2.addMouseListener(contextMenu);
        setupActions();
        
        //  padTreeTable.setTree(abilityTree);
        
        setupColorsAndIcons();
        
        installKeyboardActions();
        
        openAbilities();
        openEditor();
    }
    
    public void setupColorsAndIcons() {
        Color bg = UIManager.getColor( "TargetEditor.headerBackground");
        Color fg = UIManager.getColor( "TargetEditor.headerForeground");
        
        if ( bg != null ) {
            statLabel.setBackground(bg);
            abilityLabel.setBackground(bg);
            editLabel.setBackground(bg);
        }
        
        if ( fg != null ) {
            statLabel.setForeground(fg);
            abilityLabel.setForeground(fg);
            editLabel.setForeground(fg);
        }
        
        Icon open = UIManager.getIcon("TargetEditor.headerOpenIcon");
        Icon closed = UIManager.getIcon("TargetEditor.headerClosedIcon");
        
        if ( open != null && closed != null ) {
            abilityToggle.setIcon(closed);
            abilityToggle.setSelectedIcon(open);
            abilityToggle.setPreferredSize( new Dimension(open.getIconWidth(), open.getIconHeight()));
            editToggle.setIcon(closed);
            editToggle.setSelectedIcon(open);
            editToggle.setPreferredSize( new Dimension(open.getIconWidth(), open.getIconHeight()));
        }
        
        
        Icon lead = UIManager.getIcon("TargetEditor.headerLeadIcon");
        if ( lead != null ) {
            statToggle.setIcon(lead);
            statToggle.setPreferredSize( new Dimension( lead.getIconWidth(), lead.getIconHeight()));
        }
    }
    
    public ColumnList getAbilityHeaderDL() {
        // Setup correct headers for abilityPanels
        ColumnList headerDL = new ColumnList();
        
        int index = headerDL.createIndexed( "Column","NAME","PTS") ;
        headerDL.addIndexed(index,   "Column","TYPE", "SPECIAL" ,  true);
        headerDL.addIndexed(index,   "Column","SPECIAL", "CP" ,  true);
        headerDL.addIndexed(index,   "Column","WIDTH", new Integer(30) ,  true);
        
        index = headerDL.createIndexed( "Column","NAME","Ability Name") ;
        headerDL.addIndexed(index,   "Column","TYPE", "SPECIAL" ,  true);
        headerDL.addIndexed(index,   "Column","SPECIAL", "NAME" ,  true);
        headerDL.addIndexed(index,   "Column","WIDTH", new Integer(114) ,  true);
        
        index = headerDL.createIndexed( "Column","NAME","END") ;
        headerDL.addIndexed(index,   "Column","TYPE", "SPECIAL" ,  true);
        headerDL.addIndexed(index,   "Column","SPECIAL", "END" ,  true);
        headerDL.addIndexed(index,   "Column","WIDTH", new Integer(30) ,  true);
        
        return headerDL;
    }
    
    
    public void setupActions() {
        saveAction = new AbstractAction("Save Character") {
            public void actionPerformed(ActionEvent e) {
                if ( target != null ) {
                    if ( target.getFile() != null ) {
                        try {
                            target.save();
                        }
                        catch (Exception exc) {
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
                    }
                    catch (Exception exc) {
                        JOptionPane.showMessageDialog(null,
                        "An Error Occurred while saving character:\n" +
                        exc.toString(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        };
        
        debugTarget = new AbstractAction("Debug Character...") {
            public void actionPerformed(ActionEvent e) {
                if ( target != null ) target.debugDetailList( "Debug" + target.getName() );
            }
        };
        
        reconfigureAction = new AbstractAction("Reconfigure Abilities") {
            public void actionPerformed(ActionEvent e) {
                if ( target != null ) target.reconfigureAbilities();
            }
        };
        
        imageAction = new AbstractAction("View Character Portrait") {
            public void actionPerformed(ActionEvent e) {
                if ( target != null ) {
                    Image image = target.getImage();
                    if ( image != null ) {
                        JFrame frame = new JFrame( target.getName() + " Image");
                        
                        ImageIcon icon = new ImageIcon(image);
                        JLabel label = new JLabel();
                        label.setIcon(icon);
                        frame.getContentPane().add(label);
                        frame.pack();
                        frame.setVisible(true);
                    }
                }
            }
        };
    }
    
    void installKeyboardActions() {
        /*     ActionMap amap = getActionMap();
         
        amap.clear();
        amap.put( "nextAction", new focusNextAction(this) );
         
        InputMap imap = getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
        imap.put( KeyStroke.getKeyStroke("pressed TAB"), "nextAction");
        //imap.put( KeyStroke.getKeyStroke("released SPACE"), "toggleReleased");
        //imap.put( KeyStroke.getKeyStroke("pressed ENTER"), "pressed");
        //imap.put( KeyStroke.getKeyStroke("released ENTER"), "released");
         
         */
        
    }
    
 /*   public void setupStats() {
        int index;
        TargetStatEditor2 tse = null;
        TargetStatEditor2 last_tse = null;
  
        statEditorVector.removeAllElements();
        statPanel.removeAll();
  
        if ( target == null ) return;
  
        for (index = 0; index < statList.length; index ++ ) {
            if ( target.containsStat( statList[index] ) ) {
                last_tse = tse;
                tse = new TargetStatEditor2();
  
                statEditorVector.add(tse);
                tse.setFont( getFont() );
                tse.setForeground( getForeground() );
                tse.setBackground( getBackground() );
                tse.setTarget(target);
                tse.setStat(statList[index]);
                tse.setColumnList( statHeaderDL );
               // tse.setBorder(new LineBorder( Color.red));
                statPanel.add(tse);
  
                if ( last_tse != null ) {
                    last_tse.setNextFocusableComponent( tse );
                }
            }
        }
    } */
    
   /* public void updatePanels() {
        Iterator i = statEditorVector.iterator();
        while ( i.hasNext() ) {
            TargetStatEditor2 tse = (TargetStatEditor2) i.next();
            tse.setBase(base);
            tse.updatePanel();
        }
    
        if ( target != null ) {
            namePAD.setValue( target.getName() );
        }
    } */
    
/*    public void adjustColumns() {
        Iterator i = statEditorVector.iterator();
        while ( i.hasNext() ) {
            TargetStatEditor2 tse = (TargetStatEditor2) i.next();
            tse.adjustColumns();
        }
    } */
    
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
        if ( this.target != null ) {
            this.target.removePropertyChangeListener(this);
        }
        
        this.target = target;
        if ( targetEditorStatPanel != null ) targetEditorStatPanel.setTarget(target);
        if ( targetEditorAbilityPanel != null ) targetEditorAbilityPanel.setTarget(target);
        if ( padTreeTable != null ) padTreeTable.setTarget(target);
        
        if ( this.target != null ) {
            this.target.addPropertyChangeListener(this);
        }
    }
    
   /* static public void editTarget(Target t) {
        if ( t != null ) {
            final JFrame f = new JFrame("Target Editor");
            final TargetEditor cd = new TargetEditor();
    
            f.getContentPane().setLayout(new java.awt.BorderLayout());
            f.getContentPane().add(cd);
            cd.setTarget( t );
            f.pack();
            f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            f.addWindowListener( new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    f.setVisible(false);
                    f.getContentPane().removeAll();
                    cd.destroy();
                    f.dispose();
                }
            });
            f.setVisible(true);
    
        }
    } */
    /** This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source
     *  	and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
  /*      if ( evt.getSource() == statHeaderDL ) {
            adjustColumns();
        }
        else if ( evt.getSource() == target ) {
            updatePanels();
        } */
        
    }
    public void PADValueChanged(PADValueEvent evt) {
        if ( target != null) {
            target.add( evt.getKey(), evt.getValue(), true);
        }
    }
    public boolean PADValueChanging(PADValueEvent evt) {
        return true;
    }
    
    public boolean invokeMenu(JPopupMenu popup,Component inComponent,Point inPoint) {
        if ( target != null ) {
            
            if ( Battle.debugLevel >= 1 ) popup.add( debugTarget);
            popup.add( reconfigureAction );
            popup.add( imageAction );
            imageAction.setEnabled( target.getImage() != null ) ;
            popup.addSeparator();
            popup.add( saveAction);
            popup.add( saveAsAction);
            saveAction.setEnabled( ( target.getFile() == null ) ? false : true);
            return true;
        }
        return false;
    }
    
    public void setupAbilities() {
        if ( targetEditorAbilityPanel == null ) {
            targetEditorAbilityPanel = new TargetEditorAbilityPanel();
            targetEditorAbilityPanel.setTarget(target);
            abilityGroup.add(targetEditorAbilityPanel, BorderLayout.CENTER);
            if ( padTreeTable != null ) padTreeTable.setAbilityTreeTable( (AbilityTreeTable)targetEditorAbilityPanel.getTree());
        }
    }
    
    public void openAbilities() {
        if ( abilitiesOpen == false ) {
            setupAbilities();
            
            abilityLabel.setVisible(true);
            editToggle.setVisible(true);
            abilityGroup.setVisible(true);
            if ( editorOpen ) {
                editGroup.setVisible(true);
                editLabel.setVisible(true);
            }
            
           // resizeWindow();
            
            abilitiesOpen = true;
            abilityToggle.setSelected(true);
        }
    }
    
    
    public void closeAbilities() {
        if ( abilitiesOpen == true ) {
            int removeWidth = 0;
            
            abilityLabel.setVisible(false);
            editToggle.setVisible(false);
            abilityGroup.setVisible(false);
            if ( editorOpen ) {
                editGroup.setVisible(false);
                editLabel.setVisible(false);
            }
            
           // resizeWindow();
            // revalidate();
            abilitiesOpen = false;
            abilityToggle.setSelected(false);
        }
    }
    
    public void openEditor() {
        if ( editorOpen == false ) {
            setupEditor();
            
            if ( abilitiesOpen == true ) {
                int additionalWidth = 0;
                
                editGroup.setVisible(true);
                editLabel.setVisible(true);
                
               // resizeWindow();
            }
            // revalidate();
            editorOpen = true;
            editToggle.setSelected(true);
        }
    }
    
    public void closeEditor() {
        if ( editorOpen == true ) {
            if ( abilitiesOpen == true ) {
                
                editGroup.setVisible(false);
                editLabel.setVisible(false);
                
                //resizeWindow();
                
            }
            editorOpen = false;
            editToggle.setSelected(false);
        }
    }
    
    private void setupEditor() {
        if ( padTreeTable == null ) {
            JScrollPane jsp = new JScrollPane();
            editGroup.add(jsp, BorderLayout.CENTER);
            
            padTreeTable = new PADTreeTable();
            padTreeTable.setTarget(target);
            padTreeTable.setOpaque(false);
            if ( targetEditorAbilityPanel != null ) padTreeTable.setAbilityTreeTable( (AbilityTreeTable)targetEditorAbilityPanel.getTree());
            
            jsp.setMinimumSize( new Dimension(200, 200) );
            jsp.setViewportView( padTreeTable );
        }
    }
    
    
    /**
     * @param widthChange  */
    /*private void resizeWindow() {
        if ( window != null ) {
            window.pack();
        }
    } */
    
    public void destroy() {
        
        saveAction = null;
        saveAsAction = null;
        debugTarget = null;
        reconfigureAction = null;
    
        targetEditorStatPanel.destroy();
        targetEditorAbilityPanel.destroy();


        removeAll();
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        statToggle = new javax.swing.JLabel();
        statLabel = new javax.swing.JLabel();
        abilityToggle = new javax.swing.JToggleButton();
        abilityLabel = new javax.swing.JLabel();
        editToggle = new javax.swing.JToggleButton();
        editLabel = new javax.swing.JLabel();
        statGroup = new javax.swing.JPanel();
        abilityGroup = new javax.swing.JPanel();
        editGroup = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        add(statToggle, new java.awt.GridBagConstraints());

        statLabel.setText("Stats");
        statLabel.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0E-4;
        add(statLabel, gridBagConstraints);

        abilityToggle.setBorder(null);
        abilityToggle.setBorderPainted(false);
        abilityToggle.setContentAreaFilled(false);
        abilityToggle.setFocusPainted(false);
        abilityToggle.setMargin(new java.awt.Insets(0, 0, 0, 0));
        abilityToggle.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                abilityToggleStateChanged(evt);
            }
        });

        add(abilityToggle, new java.awt.GridBagConstraints());

        abilityLabel.setText("Abilities");
        abilityLabel.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0E-4;
        add(abilityLabel, gridBagConstraints);

        editToggle.setBorder(null);
        editToggle.setBorderPainted(false);
        editToggle.setContentAreaFilled(false);
        editToggle.setFocusPainted(false);
        editToggle.setMargin(new java.awt.Insets(0, 0, 0, 0));
        editToggle.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                editToggleStateChanged(evt);
            }
        });

        add(editToggle, new java.awt.GridBagConstraints());

        editLabel.setText("Add Ability");
        editLabel.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0E-4;
        gridBagConstraints.weighty = 1.0E-4;
        add(editLabel, gridBagConstraints);

        statGroup.setLayout(new java.awt.BorderLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        add(statGroup, gridBagConstraints);

        abilityGroup.setLayout(new java.awt.BorderLayout());

        abilityGroup.setMinimumSize(new java.awt.Dimension(200, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(abilityGroup, gridBagConstraints);

        editGroup.setLayout(new java.awt.BorderLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(editGroup, gridBagConstraints);

    }//GEN-END:initComponents
    
    private void editToggleStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_editToggleStateChanged
        // Add your handling code here:
        if ( editToggle.isSelected() ) {
            openEditor();
        }
        else {
            closeEditor();
        }
    }//GEN-LAST:event_editToggleStateChanged
    
    private void abilityToggleStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_abilityToggleStateChanged
        // Add your handling code here:
        if ( abilityToggle.isSelected() ) {
            openAbilities();
        }
        else {
            closeAbilities();
        }
    }//GEN-LAST:event_abilityToggleStateChanged
    
  private void currentRadioActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_currentRadioActionPerformed
      // Add your handling code here:
 /*   if ( currentRadio.isSelected() ) {
        base = false;
        updatePanels();
    } */
  }//GEN-LAST:event_currentRadioActionPerformed
  
  private void baseRadioActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_baseRadioActionPerformed
      // Add your handling code here:
  /*  if ( baseRadio.isSelected() ) {
        base = true;
        updatePanels();
    } */
  }//GEN-LAST:event_baseRadioActionPerformed
  
  /** Getter for property window.
   * @return Value of property window.
   */
  public Window getWindow() {
      return window;
  }
  
  /** Setter for property window.
   * @param window New value of property window.
   */
  public void setWindow(Window window) {
      if ( this.window != window ) {
          if ( this.window != null ) {
              this.window.removeComponentListener(this);
          }
          
          this.window = window;
          
          if ( this.window != null ) {
              this.window.addComponentListener(this);
          }
      }
  }
  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel abilityGroup;
    private javax.swing.JLabel abilityLabel;
    private javax.swing.JToggleButton abilityToggle;
    private javax.swing.JPanel editGroup;
    private javax.swing.JLabel editLabel;
    private javax.swing.JToggleButton editToggle;
    private javax.swing.JPanel statGroup;
    private javax.swing.JLabel statLabel;
    private javax.swing.JLabel statToggle;
    // End of variables declaration//GEN-END:variables
  
  /** Holds value of property window. */
  private Window window;
  
  public static void main(String args[]) {
      
     /* UIManager.put( "AbilityEditor.background", new Color(204,204,204) );
      UIManager.put( "AbilityEditor.foreground", Color.black );
      UIManager.put( "TargetEditor.headerBackground", Color.black);
      UIManager.put( "TargetEditor.headerForeground", Color.white);
      UIManager.put( "TargetEditor.headerOpenIcon", new ImageIcon( Toolkit.getDefaultToolkit().getClass().getResource("/graphics/targetEditorHeaderOpen.gif")) );
      UIManager.put( "TargetEditor.headerClosedIcon", new ImageIcon( Toolkit.getDefaultToolkit().getClass().getResource("/graphics/targetEditorHeaderClosed.gif")) );
      UIManager.put( "TargetEditor.headerLeadIcon", new ImageIcon( Toolkit.getDefaultToolkit().getClass().getResource("/graphics/targetEditorHeaderLead.gif")) );
      
      Character c = new Character("Test Target");
      c.editTarget(); */
      
      TargetEditorPanel tep = new TargetEditorPanel();
  }
  
  /**
   * Invoked when the component has been made visible.
   */
  public void componentShown(ComponentEvent e) {
  }
  
  /**
   * Invoked when the component's position changes.
   */
  public void componentMoved(ComponentEvent e) {
      if ( target != null ) {
        target.setTargetEditorLocation( getWindow().getLocation() );
      }
  }
  
  /**
   * Invoked when the component's size changes.
   */
  public void componentResized(ComponentEvent e) {
      if ( target != null ) {
        target.setTargetEditorSize( getWindow().getSize() );
      }
  }
  
  /**
   * Invoked when the component has been made invisible.
   */
  public void componentHidden(ComponentEvent e) {
  }
  
}