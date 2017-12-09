/*
 * DiceEditorPanel.java
 *
 * Created on January 28, 2001, 5:15 PM
 */

package champions;

import champions.interfaces.*;
import champions.exception.*;

import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
/**
 *
 * @author  unknown
 * @version
 */
public class PADDiceValueEditor extends PADAbstractEditor
implements ChampionsConstants{
    
    /** Holds value of property dice. */
    protected Dice dice;
    
    
    protected boolean autoroll = true;
    /** Holds value of property diceKey. */
    protected String diceKey;
    /** Holds value of property diceSize. */
    protected String diceSize;
    /** Holds value of property description. */
    protected String description;
    /** Holds value of property individualDice. */
    protected boolean individualDice;
      /** Holds value of property diceType. */
  private String diceType;  
    /** Holds value of property stunLabel. */
  private String stunLabel;  
    /** Holds value of property bodyLabel. */
  private String bodyLabel;  
    
    protected Vector individualVector = new Vector();
    
    public PADDiceValueEditor() {
        initComponents();
    }
    /** Creates new form DiceEditorPanel */
    public PADDiceValueEditor(String diceKey,String description,PADValueListener listener,String diceSize) {
        initComponents();
        setDiceKey( diceKey );
        setDiceSize( diceSize );
        setDescription( description );
        addPADValueListener(listener);
        setAutoroll(true);
    }
    
    public PADDiceValueEditor(String diceKey,String description,Object value,PADValueListener listener,String diceSize) {
        initComponents();
        setDiceKey( diceKey );
        setDiceSize( diceSize );
        setDescription( description );
        addPADValueListener(listener);
        setAutoroll(false);
        setValue(value);
        
    }
    
    public void setupIndividual() {
        
        int count = getDiceCount();
        if ( individualDice == false ||  count == 0 ) {
            individualGroup.setVisible(false);
            stunField.setEnabled(true);
            stunField.setEditable(true);
            bodyField.setEnabled(true);
            bodyField.setEditable(true);
        }
        else {
            individualGroup.removeAll();
            individualVector.clear();
            individualGroup.add( new JLabel( "Die Rolls" ) );
            int index;
            for ( index=0;index <count;index++ ) {
                JTextField jtf = new JTextField();
                final int dieNumber = index + 1;
                jtf.addActionListener( new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        JTextField field = ((JTextField)individualVector.get(dieNumber-1));
                        if ( ! field.getText().equals( "AUTO" ) ) {
                            
                            int die = 0;
                            try {
                                die = Integer.parseInt( field.getText() );
                            }
                            catch ( NumberFormatException nfe ) {
                            }
                            if ( autoroll == true ) {
                                setAutoroll(false);
                                clearDice();
                            }
                            getDice().setIndividualDie(dieNumber, die);
                            updatePanel();
                        }
                    }
                });
                jtf.addFocusListener( new FocusListener() {
                    public void focusGained(java.awt.event.FocusEvent evt) {
                        JTextField field = ((JTextField)individualVector.get(dieNumber-1));
                        field.selectAll();
                    }
                    public void focusLost(java.awt.event.FocusEvent evt) {
                        JTextField field = ((JTextField)individualVector.get(dieNumber-1));
                        if ( ! field.getText().equals( "AUTO" ) ) {
                            
                            int die = 0;
                            try {
                                die = Integer.parseInt( field.getText() );
                            }
                            catch ( NumberFormatException nfe ) {
                            }
                            if ( autoroll == true ) {
                                setAutoroll(false);
                                clearDice();
                            }
                            getDice().setIndividualDie(dieNumber, die);
                            updatePanel();
                        }
                    }
                });
                individualGroup.add( jtf );
                individualVector.add(jtf);
            }
            individualGroup.setVisible(true);
            stunField.setEnabled(false);
            stunField.setEditable(false);
            bodyField.setEnabled(false);
            bodyField.setEditable(false);
        }
        revalidate();
        repaint();
    }
    
    public void updatePanel() {
        if ( diceSize != null ) {
            descriptionLabel.setText( "Enter Roll for " + description + ": " + diceSize );
        }
        else {
            descriptionLabel.setText( "Enter Roll for " + description  );
        }
        
        if ( autoroll ) {
            bodyField.setText("AUTO");
            stunField.setText("AUTO");
            
            if ( individualDice) {
                int index,count;
                count = individualVector.size();
                for(index=0;index<count;index++) {
                    ((JTextField)individualVector.get(index)).setText("AUTO");
                }
            }
        }
        else {
            if ( dice != null ) {
                if ( dice.isRealized() ) {
                    bodyField.setText( dice.getBody().toString() );
                    stunField.setText( dice.getStun().toString() );
                }
                else {
                    bodyField.setText( "0" );
                    stunField.setText( "0" );
                }
                if ( individualDice) {
                    int index,count;
                    count = individualVector.size();
                    for(index=0;index<count;index++) {
                        ((JTextField)individualVector.get(index)).setText( Integer.toString(dice.getIndividualDie(index+1)));
                    }
                }
            }
        }
    }
    
    public void setAutoroll(boolean auto) {
        autoroll = auto;
        autoCheck.setSelected(auto);
        if ( auto == true || dice == null ) {
            clearDice();
        }
    }
    
    public boolean isAutoroll() {
        return autoroll;
    }
    
    public void clearDice() {
        try {
            Dice d = new Dice(diceSize,autoroll);
            setDice(d);
        }
        catch ( BadDiceException bde ) {
            setDice( new Dice(0) );
        }
    }
    
    /** Getter for property diceKey.
     * @return Value of property diceKey.
     */
    public String getDiceKey() {
        return diceKey;
    }
    /** Setter for property diceKey.
     * @param diceKey New value of property diceKey.
     */
    public void setDiceKey(String diceKey) {
        this.diceKey = diceKey;
    }
    /** Getter for property diceSize.
     * @return Value of property diceSize.
     */
    public String getDiceSize() {
        return diceSize;
    }
    /** Setter for property diceSize.
     * @param diceSize New value of property diceSize.
     */
    public void setDiceSize(int diceSize) {
        setDiceSize( Integer.toString(diceSize) + "d6" );
    }
    
    
    public void setDiceSize(String diceSize) {
        this.diceSize = diceSize;
        clearDice();
        setupIndividual();
        updatePanel();
        //descriptionLabel.setText( "Enter Roll for " + description + ": " + diceSize );
    }
    
    public int getDiceCount() {
        try {
            Dice d = new Dice(diceSize);
            //System.out.println("Dice " + d);
            return d.getD6();
        }
        catch ( BadDiceException bde ) {
            return 0;
        }
    }
    /** Getter for property description.
     * @return Value of property description.
     */
    public String getDescription() {
        return description;
    }
    /** Setter for property diceSize.
     * @param diceSize New value of property diceSize.
     */
    public void setDescription(String description) {
        this.description = description;
        descriptionLabel.setText( "Enter Roll for " + description + ": " + diceSize );
    }
    
    public Dice getDice() {
        updateBodyValue(false);
        updateStunValue(false);
        
        return dice;
    }
    
    public void setDice( Dice dice ) {
        if ( firePADValueChanging( diceKey, this.dice, dice ) == true ) {
            Dice olddice = this.dice;
            this.dice = dice;
            firePADValueChanged(diceKey, dice, olddice );
            updatePanel();
        }
    }
    
    public void setValue(Object value) {
        if ( value instanceof Dice ) {
            setDice( (Dice)value );
        }
    }
    
    /** Getter for property individualDice.
     * @return Value of property individualDice.
     */
    public boolean isIndividualDice() {
        return individualDice;
    }
    /** Setter for property individualDice.
     * @param individualDice New value of property individualDice.
     */
    public void setIndividualDice(boolean individualDice) {
        this.individualDice = individualDice;
        setupIndividual();
        updatePanel();
    }
    
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        
        descriptionLabel.setEnabled( enabled  );
        
        stunFieldLabel.setEnabled( enabled && isIndividualDice() == false );
        stunField.setEnabled( enabled && isIndividualDice() == false);
        bodyFieldLabel.setEnabled( enabled && isIndividualDice() == false);
        bodyField.setEnabled( enabled && isIndividualDice() == false);
        autoCheck.setEnabled( enabled && isIndividualDice() == false);
        
    }
    
     
  private void updateBodyValue(boolean updatePanel) {
      if ( ! bodyField.getText().equals( "AUTO" ) ) {
          
          int die = 0;
          try {
              die = Integer.parseInt( bodyField.getText() );
          }
          catch ( NumberFormatException nfe ) {
          }
          if ( autoroll == true ) {
              setAutoroll(false);
              
              dice.setStun(0);
          }
          dice.setBody(die);
          if ( updatePanel ) updatePanel();
      }
  }
  private void updateStunValue(boolean updatePanel) {
      if ( ! stunField.getText().equals( "AUTO" ) ) {
          
          int die = 0;
          try {
              die = Integer.parseInt( stunField.getText() );
          }
          catch ( NumberFormatException nfe ) {
          }
          if ( autoroll == true ) {
              setAutoroll(false);
              
              dice.setBody(0);
          }
          dice.setStun(die);
          if ( updatePanel ) updatePanel();
      }
  }
  

  /** Getter for property diceType.
   * @return Value of property diceType.
   */
  public String getDiceType() {
      return diceType;
  }  
  
  /** Setter for property diceType.
   * @param diceType New value of property diceType.
   */
  public void setDiceType(String diceType) {
      this.diceType = diceType;
      if ( diceType == null || diceType.equals(STUN_AND_BODY) ) {
          stunFieldLabel.setVisible(true);
          stunField.setVisible(true);
          bodyFieldLabel.setVisible(true);
          bodyField.setVisible(true);
      }
      else if ( diceType.equals(STUN_ONLY) ) {
          stunFieldLabel.setVisible(true);
          stunField.setVisible(true);
          bodyFieldLabel.setVisible(false);
          bodyField.setVisible(false);
      }
      else {
          // Body only
          stunFieldLabel.setVisible(false);
          stunField.setVisible(false);
          bodyFieldLabel.setVisible(true);
          bodyField.setVisible(true);
      }
  }  
  
  /** Getter for property stunLabel.
   * @return Value of property stunLabel.
   */
  public String getStunLabel() {
      return stunLabel;
  }
  
  /** Setter for property stunLabel.
   * @param stunLabel New value of property stunLabel.
   */
  public void setStunLabel(String stunLabel) {
      this.stunLabel = stunLabel;
      stunFieldLabel.setText(stunLabel);
  }
  
  /** Getter for property bodyLabel.
   * @return Value of property bodyLabel.
   */
  public String getBodyLabel() {
      return bodyLabel;
  }
  
  /** Setter for property bodyLabel.
   * @param bodyLabel New value of property bodyLabel.
   */
  public void setBodyLabel(String bodyLabel) {
      this.bodyLabel = bodyLabel;
      
      bodyFieldLabel.setText(bodyLabel);
  }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        descriptionLabel = new javax.swing.JLabel();
        fieldGroup = new javax.swing.JPanel();
        stunFieldLabel = new javax.swing.JLabel();
        stunField = new javax.swing.JTextField();
        bodyFieldLabel = new javax.swing.JLabel();
        bodyField = new javax.swing.JTextField();
        autoCheck = new javax.swing.JCheckBox();
        individualGroup = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        descriptionLabel.setText("Enter Dice Roll for");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        add(descriptionLabel, gridBagConstraints);

        fieldGroup.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        stunFieldLabel.setText("Stun");
        fieldGroup.add(stunFieldLabel);

        stunField.setText("0");
        stunField.setPreferredSize(new java.awt.Dimension(40, 20));
        stunField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stunFieldActionPerformed(evt);
            }
        });
        stunField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                stunFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                stunFieldFocusLost(evt);
            }
        });

        fieldGroup.add(stunField);

        bodyFieldLabel.setText("Body");
        fieldGroup.add(bodyFieldLabel);

        bodyField.setText("0");
        bodyField.setPreferredSize(new java.awt.Dimension(40, 20));
        bodyField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bodyFieldActionPerformed(evt);
            }
        });
        bodyField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                bodyFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                bodyFieldFocusLost(evt);
            }
        });

        fieldGroup.add(bodyField);

        autoCheck.setText("AutoRoll");
        autoCheck.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                autoCheckStateChanged(evt);
            }
        });

        fieldGroup.add(autoCheck);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        add(fieldGroup, gridBagConstraints);

        individualGroup.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(individualGroup, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents
    
  private void autoCheckStateChanged (javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_autoCheckStateChanged
      // Add your handling code here:
      if ( autoroll != autoCheck.isSelected() ) {
          setAutoroll( autoCheck.isSelected() );
          updatePanel();
      }
  }//GEN-LAST:event_autoCheckStateChanged
  
  private void bodyFieldFocusLost (java.awt.event.FocusEvent evt) {//GEN-FIRST:event_bodyFieldFocusLost
      // Add your handling code here:
      updateBodyValue(true);
  }//GEN-LAST:event_bodyFieldFocusLost
  
  private void bodyFieldFocusGained (java.awt.event.FocusEvent evt) {//GEN-FIRST:event_bodyFieldFocusGained
      // Add your handling code here:
      bodyField.selectAll();
  }//GEN-LAST:event_bodyFieldFocusGained
  
  private void bodyFieldActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bodyFieldActionPerformed
      updateBodyValue(true);
  }//GEN-LAST:event_bodyFieldActionPerformed
  
  private void stunFieldFocusLost (java.awt.event.FocusEvent evt) {//GEN-FIRST:event_stunFieldFocusLost
      updateStunValue(true);
  }//GEN-LAST:event_stunFieldFocusLost
  
  private void stunFieldFocusGained (java.awt.event.FocusEvent evt) {//GEN-FIRST:event_stunFieldFocusGained
      // Add your handling code here:
      stunField.selectAll();
  }//GEN-LAST:event_stunFieldFocusGained
  
  private void stunFieldActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stunFieldActionPerformed
      // Add your handling code here:
      updateStunValue(true);
  }//GEN-LAST:event_stunFieldActionPerformed

 
  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox autoCheck;
    private javax.swing.JTextField bodyField;
    private javax.swing.JLabel bodyFieldLabel;
    private javax.swing.JLabel descriptionLabel;
    private javax.swing.JPanel fieldGroup;
    private javax.swing.JPanel individualGroup;
    private javax.swing.JTextField stunField;
    private javax.swing.JLabel stunFieldLabel;
    // End of variables declaration//GEN-END:variables




}