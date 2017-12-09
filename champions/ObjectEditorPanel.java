/*
 * TargetEditor.java
 *
 * Created on November 14, 2000, 1:25 AM
 */

package champions;

import tjava.ContextMenu;
import champions.event.PADValueEvent;
import tjava.ContextMenuListener;
import champions.interfaces.PADValueListener;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import tjava.Destroyable;

/**
 *
 * @author  unknown
 * @version
 */
public class ObjectEditorPanel extends javax.swing.JPanel implements PropertyChangeListener, PADValueListener, ContextMenuListener, Destroyable, ComponentListener {
    
    /** Holds value of property target. */ 
    private ObjectTarget target;
    
    private Action saveAction, saveAsAction, debugTarget, reconfigureAction, imageAction;
    
 //   private boolean abilitiesOpen = false;
 //   private boolean editorOpen = false;
    
 //   private TargetEditorAbilityPanel targetEditorAbilityPanel = null;
    //private ObjectEditorStatPanel  objectEditorStatPanel = null;
 //   private PADTreeTable padTreeTable = null;
    
    
    /** Creates new form TargetEditor */
    public ObjectEditorPanel() {
        // Check to make sure the PADRoster is initialized, since everything will
        // blow up if it isn't.  This is usually only necessary when we are working
        // inside the IDE with forms or beans.
        if ( PADRoster.isInitialized() == false ) {
            PADRoster.initialize();
        }
        
        initComponents();
        
        materialComboBox.setModel( new DefaultComboBoxModel(ObjectTarget.materials));
        typeComboBox.setModel( new DefaultComboBoxModel(ObjectTarget.objectTypes));
//        
//        objectEditorStatPanel = new objectEditorStatPanel();
//        statGroup.add(objectEditorStatPanel, BorderLayout.CENTER);
//        
        
        // Setup Context Menus
        ContextMenu.addContextMenu(objectEditorStatPanel);
        //ContextMenu.addContextMenu(statLabel);
        //ContextMenu.addContextMenu(statToggle);
        //    column1.addMouseListener(contextMenu);
        //   column2.addMouseListener(contextMenu);
        setupActions();
        
        //  padTreeTable.setTree(abilityTree);
        
        setupColorsAndIcons();
        
        installKeyboardActions();
        
        openAbilities();
        openEditor();
        
        nameEditor.addPADValueListener(this);
        descriptionEditor.addPADValueListener(this);
    }
    
    public void setupColorsAndIcons() {
        Color bg = UIManager.getColor( "TargetEditor.headerBackground");
        Color fg = UIManager.getColor( "TargetEditor.headerForeground");
        
//        if ( bg != null ) {
//            nameGroup.setBackground(bg);
//            nameEditor.setBackground(bg);
//            descriptionEditor.setBackground(bg);
//            statLabel.setBackground(bg);
//            //abilityLabel.setBackground(bg);
//            //editLabel.setBackground(bg);
//        }
//        
//        if ( fg != null ) {
//            nameGroup.setForeground(fg);
//            nameEditor.setForeground(fg);
//            descriptionEditor.setForeground(fg);
//            statLabel.setForeground(fg);
//            //abilityLabel.setForeground(fg);
//            //editLabel.setForeground(fg);
//        }
        
//        Icon open = UIManager.getIcon("TargetEditor.headerOpenIcon");
//        Icon closed = UIManager.getIcon("TargetEditor.headerClosedIcon");
//        
//        if ( open != null && closed != null ) {
////            abilityToggle.setIcon(closed);
////            abilityToggle.setSelectedIcon(open);
////            abilityToggle.setPreferredSize( new Dimension(open.getIconWidth(), open.getIconHeight()));
////            editToggle.setIcon(closed);
////            editToggle.setSelectedIcon(open);
////            editToggle.setPreferredSize( new Dimension(open.getIconWidth(), open.getIconHeight()));
//        }
//        
//        
////        Icon lead = UIManager.getIcon("TargetEditor.headerLeadIcon");
////        if ( lead != null ) {
////            statToggle.setIcon(lead);
////            statToggle.setPreferredSize( new Dimension( lead.getIconWidth(), lead.getIconHeight()));
////        }
    }
    
//    public ColumnList getAbilityHeaderDL() {
//        // Setup correct headers for abilityPanels
//        ColumnList headerDL = new ColumnList();
//        
//        int index = headerDL.createIndexed( "Column","NAME","PTS") ;
//        headerDL.addIndexed(index,   "Column","TYPE", "SPECIAL" ,  true);
//        headerDL.addIndexed(index,   "Column","SPECIAL", "CP" ,  true);
//        headerDL.addIndexed(index,   "Column","WIDTH", new Integer(30) ,  true);
//        
//        index = headerDL.createIndexed( "Column","NAME","Ability Name") ;
//        headerDL.addIndexed(index,   "Column","TYPE", "SPECIAL" ,  true);
//        headerDL.addIndexed(index,   "Column","SPECIAL", "NAME" ,  true);
//        headerDL.addIndexed(index,   "Column","WIDTH", new Integer(114) ,  true);
//        
//        index = headerDL.createIndexed( "Column","NAME","END") ;
//        headerDL.addIndexed(index,   "Column","TYPE", "SPECIAL" ,  true);
//        headerDL.addIndexed(index,   "Column","SPECIAL", "END" ,  true);
//        headerDL.addIndexed(index,   "Column","WIDTH", new Integer(30) ,  true);
//        
//        return headerDL;
//    }
    
    
    public void setupActions() {
        saveAction = new AbstractAction("Save Object") {
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
        
        saveAsAction = new AbstractAction("Save Object As...") {
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
        
        debugTarget = new AbstractAction("Debug Object...") {
            public void actionPerformed(ActionEvent e) {
                if ( target != null ) target.debugDetailList( "Debug" + target.getName() );
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
    public ObjectTarget getTarget() {
        return target;
    }
    /** Setter for property target.
     * @param target New value of property target.
     */
    public void setTarget(ObjectTarget target) {
        if ( this.target != null ) {
            this.target.removePropertyChangeListener(this);
        }
        
        this.target = target;
        if ( objectEditorStatPanel != null ) objectEditorStatPanel.setTarget(target);
        
        if ( target != null ) {
            nameEditor.setValue( target.getName() );
            //nameEditor.setValue( target.getStringValue("Target.NAME"));
            //String description = target.getStringValue("Target.DESCRIPTION");
            String description = target.getDescription();
            descriptionEditor.setValue( description == null ? "" : description );
        }

        updateControls();
        
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
        updateControls();

    }
    public void PADValueChanged(PADValueEvent evt) {
        if ( target != null) {
            if ( evt.getKey().equals("Target.NAME") ) {
                target.setName( (String)evt.getValue() );
            }
            else if ( evt.getKey().equals("Target.DESCRIPTION") ) {
                target.setDescription( (String)evt.getValue() );
            }
            else {
                target.add( evt.getKey(), evt.getValue(), true);
            }
        }
    }
    
    public boolean PADValueChanging(PADValueEvent evt) {
        return true;
    }
    
    public boolean invokeMenu(JPopupMenu popup,Component inComponent,Point inPoint) {
        if ( target != null ) {
            
            if ( Battle.debugLevel >= 1 ) {
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
    
    public void setupAbilities() {
//        if ( targetEditorAbilityPanel == null ) {
//            targetEditorAbilityPanel = new TargetEditorAbilityPanel();
//            targetEditorAbilityPanel.setTarget(target);
//          //  abilityGroup.add(targetEditorAbilityPanel, BorderLayout.CENTER);
//            if ( padTreeTable != null ) padTreeTable.setAbilityTreeTable( (AbilityTreeTable)targetEditorAbilityPanel.getTree());
//        }
    }
    
    public void openAbilities() {
//        if ( abilitiesOpen == false ) {
//            setupAbilities();
//            
////            abilityLabel.setVisible(true);
////            editToggle.setVisible(true);
////            abilityGroup.setVisible(true);
////            if ( editorOpen ) {
////                editGroup.setVisible(true);
////                editLabel.setVisible(true);
////            }
////            
////           // resizeWindow();
////            
////            abilitiesOpen = true;
////            abilityToggle.setSelected(true);
////        }
    }
    
    
    public void closeAbilities() {
//        if ( abilitiesOpen == true ) {
//            int removeWidth = 0;
//            
//            abilityLabel.setVisible(false);
//            editToggle.setVisible(false);
//            abilityGroup.setVisible(false);
//            if ( editorOpen ) {
//                editGroup.setVisible(false);
//                editLabel.setVisible(false);
//            }
//            
//           // resizeWindow();
//            // revalidate();
//            abilitiesOpen = false;
//            abilityToggle.setSelected(false);
//        }
    }
    
    public void openEditor() {
//        if ( editorOpen == false ) {
//            setupEditor();
//            
//            if ( abilitiesOpen == true ) {
//                int additionalWidth = 0;
//                
//                editGroup.setVisible(true);
//                editLabel.setVisible(true);
//                
//               // resizeWindow();
//            }
//            // revalidate();
//            editorOpen = true;
//            editToggle.setSelected(true);
//        }
    }
    
    public void closeEditor() {
//        if ( editorOpen == true ) {
//            if ( abilitiesOpen == true ) {
//                
//                editGroup.setVisible(false);
//                editLabel.setVisible(false);
//                
//                //resizeWindow();
//                
//            }
//            editorOpen = false;
//            editToggle.setSelected(false);
//        }
    }
    
    private void setupEditor() {
//        if ( padTreeTable == null ) {
//            JScrollPane jsp = new JScrollPane();
//            editGroup.add(jsp, BorderLayout.CENTER);
//            
//            padTreeTable = new PADTreeTable();
//            padTreeTable.setTarget(target);
//            if ( targetEditorAbilityPanel != null ) padTreeTable.setAbilityTreeTable( (AbilityTreeTable)targetEditorAbilityPanel.getTree());
//            
//            jsp.setMinimumSize( new Dimension(200, 200) );
//            jsp.setViewportView( padTreeTable );
//        }
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
    
        objectEditorStatPanel.destroy();
      //  targetEditorAbilityPanel.destroy();
        removeAll();
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        nameEditor = new champions.PADStringEditor();
        descriptionEditor = new champions.PADStringEditor();
        miscGroup = new javax.swing.JPanel();
        massLabel = new javax.swing.JLabel();
        massTextField = new javax.swing.JTextField();
        massDescriptionLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        materialComboBox = new javax.swing.JComboBox();
        materialDescriptionLabel = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        typeComboBox = new javax.swing.JComboBox();
        typeDescriptionLabel = new javax.swing.JLabel();
        knockbackCheckbox = new javax.swing.JCheckBox();
        knockbackModifierLabel = new javax.swing.JLabel();
        knockbackModifierField = new javax.swing.JTextField();
        knockbackDescriptionLabel = new javax.swing.JLabel();
        knockbackDescriptionLabel2 = new javax.swing.JLabel();
        objectEditorStatPanel = new champions.ObjectEditorStatPanel();

        nameEditor.setDescription("Name");
        nameEditor.setPropertyName("Target.NAME");

        descriptionEditor.setDescription("Description");
        descriptionEditor.setPropertyName("Target.DESCRIPTION");

        miscGroup.setBorder(javax.swing.BorderFactory.createTitledBorder("Attributes"));

        massLabel.setText("Thickness (mm)");

        massTextField.setText("200");
        massTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                massTextFieldActionPerformed(evt);
            }
        });
        massTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                massTextFieldFocusLost(evt);
            }
        });

        massDescriptionLabel.setFont(new java.awt.Font("SansSerif", 0, 11));
        massDescriptionLabel.setText("Test");

        jLabel2.setText("Material");

        materialComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                materialComboBoxActionPerformed(evt);
            }
        });

        materialDescriptionLabel.setFont(new java.awt.Font("SansSerif", 0, 11));
        materialDescriptionLabel.setText("Test");

        jLabel3.setText("Type");

        typeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                typeComboBoxActionPerformed(evt);
            }
        });

        typeDescriptionLabel.setFont(new java.awt.Font("SansSerif", 0, 11));
        typeDescriptionLabel.setText("Test");

        knockbackCheckbox.setText("Affected by knockback");
        knockbackCheckbox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        knockbackCheckbox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        knockbackCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                knockbackCheckboxActionPerformed(evt);
            }
        });

        knockbackModifierLabel.setText("Knockback Modifier");

        knockbackModifierField.setMinimumSize(new java.awt.Dimension(40, 19));
        knockbackModifierField.setPreferredSize(new java.awt.Dimension(40, 19));
        knockbackModifierField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                knockbackModifierFieldActionPerformed(evt);
            }
        });
        knockbackModifierField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                knockbackModifierFieldFocusLost(evt);
            }
        });
        knockbackModifierField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                knockbackModifierFieldKeyTyped(evt);
            }
        });

        knockbackDescriptionLabel.setFont(new java.awt.Font("SansSerif", 0, 11));
        knockbackDescriptionLabel.setText("Test");

        knockbackDescriptionLabel2.setFont(new java.awt.Font("SansSerif", 0, 11));
        knockbackDescriptionLabel2.setText("Test");

        javax.swing.GroupLayout miscGroupLayout = new javax.swing.GroupLayout(miscGroup);
        miscGroup.setLayout(miscGroupLayout);
        miscGroupLayout.setHorizontalGroup(
            miscGroupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(miscGroupLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(miscGroupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(miscGroupLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(typeComboBox, 0, 86, Short.MAX_VALUE)
                        .addGap(10, 10, 10)
                        .addComponent(typeDescriptionLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE))
                    .addGroup(miscGroupLayout.createSequentialGroup()
                        .addGroup(miscGroupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(miscGroupLayout.createSequentialGroup()
                                .addGroup(miscGroupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(massLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(miscGroupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(materialComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(massTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(miscGroupLayout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addComponent(knockbackModifierLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(knockbackModifierField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(knockbackCheckbox))
                        .addGap(10, 10, 10)
                        .addGroup(miscGroupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(knockbackDescriptionLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
                            .addComponent(massDescriptionLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
                            .addComponent(materialDescriptionLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
                            .addComponent(knockbackDescriptionLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE))))
                .addContainerGap())
        );

        miscGroupLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel2, jLabel3, massLabel});

        miscGroupLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {massTextField, materialComboBox, typeComboBox});

        miscGroupLayout.setVerticalGroup(
            miscGroupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(miscGroupLayout.createSequentialGroup()
                .addGroup(miscGroupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(typeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(typeDescriptionLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(miscGroupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(massLabel)
                    .addComponent(massTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(massDescriptionLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(miscGroupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(materialDescriptionLabel)
                    .addComponent(jLabel2)
                    .addComponent(materialComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(miscGroupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(knockbackCheckbox, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(knockbackDescriptionLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(miscGroupLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(knockbackModifierLabel)
                    .addComponent(knockbackDescriptionLabel2)
                    .addComponent(knockbackModifierField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        objectEditorStatPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Stats"));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(objectEditorStatPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 348, Short.MAX_VALUE)
                    .addComponent(miscGroup, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(descriptionEditor, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 348, Short.MAX_VALUE)
                    .addComponent(nameEditor, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 348, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(nameEditor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(descriptionEditor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(miscGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(objectEditorStatPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void materialComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_materialComboBoxActionPerformed

        if(target != null) {
            target.setObjectMaterial((String)materialComboBox.getSelectedItem());
        }
    }//GEN-LAST:event_materialComboBoxActionPerformed

    private void massTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_massTextFieldFocusLost

        if(target != null) {
            try {
                double mass = Double.parseDouble(massTextField.getText());
                String objectType = target.getObjectType();
                if(objectType.equals("Wall")) {
                    target.setWallThickness(mass);
                }
                else {
                    target.setMass(mass);
                }
            } catch(NumberFormatException nfe) {

            }
        }
    }//GEN-LAST:event_massTextFieldFocusLost

    private void massTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_massTextFieldActionPerformed
        if(target != null) {
            try {
                double mass = Double.parseDouble(massTextField.getText());
                String objectType = target.getObjectType();
                if(objectType.equals("Wall")) {
                    target.setWallThickness(mass);
                }
                else {
                    target.setMass(mass);
                }
            } catch(NumberFormatException nfe) {

            }
        }
    }//GEN-LAST:event_massTextFieldActionPerformed

    private void typeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_typeComboBoxActionPerformed

        if(target != null) {
            target.setObjectType((String)typeComboBox.getSelectedItem());
        }
    }//GEN-LAST:event_typeComboBoxActionPerformed

    private void knockbackModifierFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_knockbackModifierFieldFocusLost

        try {
            int modifier = Integer.parseInt( knockbackModifierField.getText());
            setKnockbackModifier( modifier );
        }
        catch(NumberFormatException nfe) {
            
        }
    }//GEN-LAST:event_knockbackModifierFieldFocusLost

    private void knockbackModifierFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_knockbackModifierFieldKeyTyped

        try {
            int modifier = Integer.parseInt( knockbackModifierField.getText());
            setKnockbackModifier( modifier );
        }
        catch(NumberFormatException nfe) {
            
        }
    }//GEN-LAST:event_knockbackModifierFieldKeyTyped

    private void knockbackCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_knockbackCheckboxActionPerformed

        setKnockbackEnabled( knockbackCheckbox.isSelected() );
    }//GEN-LAST:event_knockbackCheckboxActionPerformed

    private void knockbackModifierFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_knockbackModifierFieldActionPerformed

        try {
            int modifier = Integer.parseInt( knockbackModifierField.getText());
            setKnockbackModifier( modifier );
        }
        catch(NumberFormatException nfe) {
            
        }
    }//GEN-LAST:event_knockbackModifierFieldActionPerformed
            
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
    private champions.PADStringEditor descriptionEditor;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JCheckBox knockbackCheckbox;
    private javax.swing.JLabel knockbackDescriptionLabel;
    private javax.swing.JLabel knockbackDescriptionLabel2;
    private javax.swing.JTextField knockbackModifierField;
    private javax.swing.JLabel knockbackModifierLabel;
    private javax.swing.JLabel massDescriptionLabel;
    private javax.swing.JLabel massLabel;
    private javax.swing.JTextField massTextField;
    private javax.swing.JComboBox materialComboBox;
    private javax.swing.JLabel materialDescriptionLabel;
    private javax.swing.JPanel miscGroup;
    private champions.PADStringEditor nameEditor;
    private champions.ObjectEditorStatPanel objectEditorStatPanel;
    private javax.swing.JComboBox typeComboBox;
    private javax.swing.JLabel typeDescriptionLabel;
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
      
      ObjectEditorPanel tep = new ObjectEditorPanel();
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
  
  public void updateControls() {
      
     
      if(target != null ) {
          double mass = target.getMass();
          String objectType = target.getObjectType();
          String objectMaterial = target.getObjectMaterial();
          int body;
          
          if( objectType != null && objectType.equals("Wall")) {
              massLabel.setText("Thickness (mm)");
              double thickness = target.getWallThickness();
              massTextField.setText(Double.toString(thickness));
              
//              if ( thickness < 2000.0 ) {
//                  massDescriptionLabel.setText("That's one thick wall");
//              }
//              else {
//                  massDescriptionLabel.setText("");
//              }
              massDescriptionLabel.setText("");
              
              body = ObjectTarget.getObjectStartingBody(thickness,objectType,objectMaterial);
          }
          else {
              massLabel.setText("Mass (kg)");
              massTextField.setText(Double.toString(mass));
              
              String massDescription = ObjectTarget.getRelativeMassDescription( mass );
              massDescriptionLabel.setText(massDescription);
              
              body = ObjectTarget.getObjectStartingBody(mass,objectType,objectMaterial);
          }
          
          materialComboBox.setSelectedItem(objectMaterial);
          int def = ObjectTarget.getObjectStartingDefense(objectType, objectMaterial);
          materialDescriptionLabel.setText("Base Def = " + def);
          
          typeComboBox.setSelectedItem(objectType);
          typeDescriptionLabel.setText("Base Body = " + body);
          
          knockbackCheckbox.setSelected( isKnockbackEnabled() );
          knockbackModifierLabel.setEnabled( isKnockbackEnabled() );
          knockbackModifierField.setEnabled( isKnockbackEnabled() );
          
          if( isKnockbackEnabled() ) {
               int baseKB = ObjectTarget.getObjectBaseKnockbackResistance(mass);
               int targetKB = target.getKnockbackResistance();
               
               knockbackDescriptionLabel.setText("Base KB Resist = " + baseKB);
               
               int kbDiff = targetKB - baseKB;
               knockbackModifierField.setText( Integer.toString(kbDiff) );
               
               knockbackDescriptionLabel2.setText("Final KB Resist = " + targetKB);
          }
          else {
              knockbackDescriptionLabel.setText("Immune to knockback");
              knockbackDescriptionLabel2.setText("");
          }
      }
  }

    public boolean isKnockbackEnabled() {
        if ( target != null ) {
            return target.canBeKnockedback();
        }
        return false;
    }

    public void setKnockbackEnabled(boolean knockbackEnabled) {
        if ( isKnockbackEnabled() != knockbackEnabled ) {
            target.setCanBeKnockedback(knockbackEnabled);
            updateControls();
        }
    }

    public int getKnockbackModifier() {
        if ( target != null ) {
            return 0;
        }
        return 0;
    }

    public void setKnockbackModifier(int knockbackModifier) {
        
    }
  
}