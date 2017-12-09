/*
 * TargetStatEditor2.java
 *
 * Created on November 14, 2000, 1:25 AM
 */

package champions;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.text.*;
import tjava.Destroyable;
/**
 *
 * @author  unknown
 * @version
 */
public class TargetStatEditor2 extends javax.swing.JPanel 
implements Destroyable {
    
    /** Holds value of property columnList. */
    private DetailList columnList;
    /** Holds value of property target. */
    private Target target;
    /** Holds value of property stat. */
    private String stat;
    /** Holds value of property base. */
    private boolean base = true;
    /** Holds value of property color1. */
    private Color color1 = new Color(0,102,0);
    /** Holds value of property color2. */
    private Color color2 = Color.yellow;
    /** Holds value of property color3. */
    private Color color3 = Color.red;
    
    private Rectangle valueLabel = new Rectangle();
    private Rectangle cpLabel = new Rectangle();
    private Rectangle costLabel = new Rectangle();
    private Rectangle baseLabel = new Rectangle();
    private Rectangle statLabel = new Rectangle();
    
    private String valueText;
    private String cpText;
    private String costText;
    private String baseText;
    private String statText;
    
    private Color valueColor;
    
    /** Creates new form TargetStatEditor2 */
    public TargetStatEditor2() {
        initComponents();
        setLayout( null );
        
        valueField.setVisible(false);
        cpField.setVisible(false);
        
        installKeyboardActions();
    }
    
    public void updatePanel() {
        if ( columnList == null ) return;
        if ( target == null ||  target.hasStat( stat ) == false ) {
            valueText = "n/a";
            statText = stat;
            costText = "n/a";
            baseText = "n/a";
            cpText = "n/a";
        }
        else {
            if ( base ) {
                int baseStat = target.getBaseStat( stat );
                int adjustedStat = target.getAdjustedStat( stat );
                String value = Integer.toString(target.getBaseStat( stat ));
                
                if ( baseStat == adjustedStat ) {
                    valueText = value;
                }
                else {
                    valueText = value + "/" + Integer.toString(adjustedStat);
                }
                
                if ( !valueField.getText().equals(value) ) {
                    valueField.setText( value );
                    valueField.selectAll();
                }
                
                value = Double.toString(target.getBaseStatCP( stat ));
                cpText = value;
                if ( !cpField.getText().equals(value) ) {
                    cpField.setText( value );
                    cpField.selectAll();
                }
                
                valueColor = getForeground();
            }
            else {
                int base = target.getAdjustedStat( stat );
                int current = target.getCurrentStat( stat );
                
                String value = Integer.toString( current );
                
                valueText = value;
                valueField.setText( value );
                
                Color c = Color.black;
                if ( current > base ) {
                    c = color1;
                }
                else if ( current < base  && current <= 0 ) {
                    c = color3;
                }
                else if ( current < base ) {
                    c = color2;
                }
                
                valueColor = c;
                
                
                
            //    value = Double.toString(target.getCurrentStatCP( stat ));
                cpText = "n/a";
                cpField.setText( cpText );
            }
            
            costText = Double.toString( Characteristic.getCPperPoint(stat) );
            
            double base = Characteristic.getStartingValue(stat);
            if ( base == -1 ) {
                Double figuredBase = target.getDoubleValue(stat + ".FIGUREDCURRENTSTAT" );
                base = (figuredBase == null) ? 0 : figuredBase.doubleValue();
            }
            baseText =  Double.toString( base );
            
            statText = stat;
            
            repaint();
            
            TargetButton2.buttonUpdates ++;
        }
        
    }
    
    public boolean isManagingFocus() {
        return true;
    }
    
    void installKeyboardActions() {
        // ActionMap amap = getActionMap();
        ActionMap amap = valueField.getActionMap();
        
        //amap.clear();
        amap.put( "nextAction", new focusNextAction(this) );
        
        InputMap imap = valueField.getInputMap( JComponent.WHEN_FOCUSED );
        imap.put( KeyStroke.getKeyStroke("pressed TAB"), "nextAction");
        
        // ActionMap amap = getActionMap();
        amap = cpField.getActionMap();
        
        amap.put( "nextAction", new focusNextAction(this) );
        
        imap = cpField.getInputMap( JComponent.WHEN_FOCUSED );
        imap.put( KeyStroke.getKeyStroke("pressed TAB"), "nextAction");
        
        
        
    }
    
    public void adjustColumns() {
        if ( columnList == null ) return;
        int index, count;
        DetailList dl = columnList;
        Insets insets;
        Object o;
        
        String type, key;
        Integer start, width;
        
        Insets panelInsets = getInsets();
        
        int maxWidth = 0;
        int x = panelInsets.left;
        
        int height = getFontMetrics(getFont()).getHeight() - 3;
        
        count = dl.getIndexedSize( "Column" );
        
        for (index = 0; index < count; index ++ ) {
            if ( (type = dl.getIndexedStringValue( index, "Column","TYPE" )) == null
            || ( width = dl.getIndexedIntegerValue( index, "Column","WIDTH" )) == null
            ) {
                continue;
            }
            
            if ( (o = dl.getIndexedValue( index, "Column", "INSETS")) == null ) {
                insets = new Insets(0,2,0,2);
            }
            else {
                insets = (Insets)o;
            }
            
            if ( ! type.equals( "SPECIAL" ) || (key = dl.getIndexedStringValue(index, "Column", "SPECIAL")) == null ) {
                continue;
            }
            
            if ( key.equals( "VALUE" ) ){
                valueLabel.setBounds( x + insets.left, 1, width.intValue() - insets.left - insets.right, height);
                valueField.setBounds( x + insets.left , 1, width.intValue() - insets.left - insets.right, height);
                if ( maxWidth < x + width.intValue() ) maxWidth = x + width.intValue();
                x += width.intValue();
            }
            else if ( key.equals( "STAT" ) ) {
                statLabel.setBounds( x + insets.left , 1, width.intValue() - insets.left - insets.right , height);
                if ( maxWidth < x + width.intValue() ) maxWidth = x + width.intValue();
                x += width.intValue();
                
            }
            else if ( key.equals( "COST" ) ) {
                costLabel.setBounds( x + insets.left , 1, width.intValue() - insets.left - insets.right, height);
                if ( maxWidth < x + width.intValue() ) maxWidth = x + width.intValue();
                x += width.intValue();
                
            }
            else if ( key.equals( "BASE" ) ) {
                baseLabel.setBounds( x + insets.left , 1, width.intValue() - insets.left - insets.right , height);
                if ( maxWidth < x + width.intValue() ) maxWidth = x + width.intValue();
                x += width.intValue();
            }
            else if ( key.equals( "CP" ) ) {
                cpLabel.setBounds( x + insets.left , 1, width.intValue() - insets.left - insets.right , height);
                cpField.setBounds( x + insets.left , 1, width.intValue() - insets.left - insets.right , height);
                if ( maxWidth < x + width.intValue() ) maxWidth = x + width.intValue();
                x += width.intValue();
            }
        }
        
        maxWidth += panelInsets.right;
        
       // System.out.println("TSE2 PanelInsets: " + panelInsets);
       // System.out.println("Max width: " + Integer.toString(maxWidth));
        this.setPreferredSize( new Dimension( maxWidth, height + 1));
        this.setMinimumSize( new Dimension( maxWidth, height + 1));
        revalidate();
    }
    
    public void editValue() {
       // valueLabel.setVisible(false);
        valueField.setVisible(true);
        
        valueField.grabFocus();
        valueField.selectAll();
    }
    
    public void editCP() {
       // cpLabel.setVisible(false);
        cpField.setVisible(true);
        
        cpField.grabFocus();
        cpField.selectAll();
    }
    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        String text;
        
        FontMetrics fm;
        
        g.setFont(getFont());
        fm = g.getFontMetrics();
        
        int ascent = fm.getAscent();
        
        // Paint ValueLabel
        g.setColor(valueColor);
        text = getAbbreviatedString(valueText, valueLabel.getWidth(), fm);
        g.drawString(text, (int) valueLabel.getX(), (int)valueLabel.getY() + ascent);
        
        g.setColor(getForeground());
        
        text = getAbbreviatedString(statText, statLabel.getWidth(), fm);
        g.drawString(text, (int) statLabel.getX(), (int) statLabel.getY() +ascent);
        
        text = getAbbreviatedString(costText, costLabel.getWidth(), fm);
        g.drawString(text, (int) (costLabel.getX() + costLabel.getWidth() - SwingUtilities.computeStringWidth(fm, text)), (int) costLabel.getY() + ascent);

        text = getAbbreviatedString(baseText, baseLabel.getWidth(), fm);
        g.drawString(text, (int) (baseLabel.getX() + baseLabel.getWidth() - SwingUtilities.computeStringWidth(fm, text)), (int) baseLabel.getY() + ascent);
 
        text = getAbbreviatedString(cpText, cpLabel.getWidth(), fm);
        g.drawString(text, (int) (cpLabel.getX() + cpLabel.getWidth() - SwingUtilities.computeStringWidth(fm, text)), (int) cpLabel.getY() + ascent);
        
    }
    
    private String getAbbreviatedString(String text, double maxWidth, FontMetrics fm) {
        
        if ( SwingUtilities.computeStringWidth(fm,text) <= maxWidth ) return text;
        
        String clipString = "...";
        int totalWidth = SwingUtilities.computeStringWidth(fm,clipString);
        int nChars;
        for(nChars = 0; nChars < text.length(); nChars++) {
            totalWidth += fm.charWidth(text.charAt(nChars));
            if (totalWidth > maxWidth) {
                break;
            }
        }
        return text.substring(0, nChars) + clipString;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        valueField = new champions.MyTextField();
        cpField = new champions.MyTextField();

        setLayout(new java.awt.GridBagLayout());

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });

        valueField.setText("myTextField1");
        valueField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                valueFieldActionPerformed(evt);
            }
        });
        valueField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                valueFieldFocusLost(evt);
            }
        });

        add(valueField, new java.awt.GridBagConstraints());

        cpField.setText("myTextField1");
        cpField.setHorizontalAlignment(SwingConstants.RIGHT);
        cpField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cpFieldActionPerformed(evt);
            }
        });
        cpField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cpFieldFocusLost(evt);
            }
        });

        add(cpField, new java.awt.GridBagConstraints());

    }//GEN-END:initComponents

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        // Add your handling code here:
        if ( valueLabel.contains( evt.getPoint() ) ){
            editValue();
        }
        else if (base == true&& cpLabel.contains( evt.getPoint() ) ){
            editCP();
        }
    }//GEN-LAST:event_formMouseClicked
    
  private void cpFieldFocusLost (java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cpFieldFocusLost
      // Add your handling code here:
      try {
          double value = Double.parseDouble( cpField.getText() );
          if ( base ) {
              int baseStat = target.getBaseStat(stat);
              int adjustedStat = target.getAdjustedStat(stat);
              
              target.setBaseStatCP( stat, value);
              
         //     int newBaseStat = target.getBaseStat(stat);
              
         //     target.setAdjustedStat(stat, adjustedStat + newBaseStat - baseStat, true);
              
         //     if ( target.getCurrentStat(stat) == baseStat ) {
         //         target.setCurrentStatCP( stat, (int)value, true );
         //     }
          }
        //  else {
       //       target.setCurrentStatCP(stat, value, false );
       //   }
          
          cpField.setVisible(false);
         // cpLabel.setVisible(true);
      }
      catch ( NumberFormatException nfe ) {
          updatePanel();
          cpField.selectAll();
      }
  }//GEN-LAST:event_cpFieldFocusLost
  
  private void cpFieldActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cpFieldActionPerformed
      // Add your handling code here:
      try {
          double value = Double.parseDouble( cpField.getText() );
          if ( base ) {
              int baseStat = target.getBaseStat(stat);
              int adjustedStat = target.getAdjustedStat(stat);
              
              target.setBaseStatCP( stat, value );
              
         //     int newBaseStat = target.getBaseStat(stat);
              
         //     target.setAdjustedStat(stat, adjustedStat + newBaseStat - baseStat, true);
              
          //    if ( target.getCurrentStat(stat) == baseStat ) {
          //        target.setCurrentStatCP( stat, (int)value, true );
         //     }
          }
         // else {
        //      target.setCurrentStatCP(stat, value, false );
        //  }
          
          cpField.setVisible(false);
          //cpLabel.setVisible(true);
      }
      catch ( NumberFormatException nfe ) {
          updatePanel();
          cpField.selectAll();
      }
  }//GEN-LAST:event_cpFieldActionPerformed
  
  private void valueFieldFocusLost (java.awt.event.FocusEvent evt) {//GEN-FIRST:event_valueFieldFocusLost
      // Add your handling code here:
      try {
          int value = Integer.parseInt( valueField.getText() );
          if ( base ) {
              int baseStat = target.getBaseStat(stat);
              int adjustedStat = target.getAdjustedStat(stat);
              int difference = value - baseStat;
              
              target.setBaseStat(stat, value);
          }
          else {
              target.setCurrentStat(stat, value );
          }

          
          valueField.setVisible(false);
          //valueLabel.setVisible(true);
      }
      catch ( NumberFormatException nfe ) {
          updatePanel();
          // valueField.selectAll();
      }
  }//GEN-LAST:event_valueFieldFocusLost
  
  private void valueFieldActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_valueFieldActionPerformed
      // Add your handling code here:
      try {
          int value = Integer.parseInt( valueField.getText() );
          if ( base ) {
              int baseStat = target.getBaseStat(stat);
              int adjustedStat = target.getAdjustedStat(stat);
              int difference = value - baseStat;
              
              target.setBaseStat(stat, value);
          //    target.setAdjustedStat(stat, adjustedStat + difference);
              
          //    if ( target.getCurrentStat(stat) == baseStat ) {
          //        target.setCurrentStat( stat, value);
          //    }
              
          }
          else {
              target.setCurrentStat(stat, value );
          }
          
          valueField.setVisible(false);
          //valueLabel.setVisible(true);
      }
      catch ( NumberFormatException nfe ) {
          updatePanel();
          // valueField.selectAll();
      }
  }//GEN-LAST:event_valueFieldActionPerformed
  
  private void cpLabelMouseClicked (java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cpLabelMouseClicked
      // Add your handling code here:
      
     // cpLabel.setVisible(false);
      cpField.setVisible(true);
      cpField.selectAll();
      cpField.requestFocus();
  }//GEN-LAST:event_cpLabelMouseClicked
  
  private void valueLabelMouseClicked (java.awt.event.MouseEvent evt) {//GEN-FIRST:event_valueLabelMouseClicked
      // Add your handling code here:
      
      //valueLabel.setVisible(false);
      valueField.setVisible(true);
      valueField.selectAll();
      valueField.requestFocus();
  }//GEN-LAST:event_valueLabelMouseClicked
  
  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private champions.MyTextField cpField;
    private champions.MyTextField valueField;
    // End of variables declaration//GEN-END:variables
  
  /** Getter for property columnList.
   * @return Value of property columnList.
   */
  public DetailList getColumnList() {
      return columnList;
  }
  /** Setter for property columnList.
   * @param columnList New value of property columnList.
   */
  public void setColumnList(DetailList columnList) {
      this.columnList = columnList;
      adjustColumns();
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
      this.target = target;
      updatePanel();
  }
  /** Getter for property stat.
   * @return Value of property stat.
   */
  public String getStat() {
      return stat;
  }
  /** Setter for property stat.
   * @param stat New value of property stat.
   */
  public void setStat(String stat) {
      this.stat = stat;
      updatePanel();
  }
  /** Getter for property base.
   * @return Value of property base.
   */
  public boolean isBase() {
      return base;
  }
  /** Setter for property base.
   * @param base New value of property base.
   */
  public void setBase(boolean base) {
      this.base = base;
      updatePanel();
  }
  
  public void setFont( Font f ) {
      super.setFont(f);
      if ( valueLabel != null ) {
          //valueLabel.setFont(f);
          valueField.setFont(f);
          //statLabel.setFont(f);
          //costLabel.setFont(f);
          //baseLabel.setFont(f);
          //cpLabel.setFont(f);
          cpField.setFont(f);
      }
  }
  
  public JTextField getValueField() {
      return valueField;
  }
  
  public void destroy() {
      removeAll();
      setNextFocusableComponent(null);
      
  }
  
  public static class focusNextAction extends AbstractAction {
      private TargetStatEditor2 te;
      public focusNextAction(TargetStatEditor2 te) {
          super();
          this.te = te;
      }
      
      public void actionPerformed(ActionEvent evt) {
          // System.out.println( "Tab pressed");
          if ( te.valueField.hasFocus() ) {
              Component c = te.getNextFocusableComponent();
              if ( c != null ) {
                  if ( c instanceof TargetStatEditor2 ) {
                      ((TargetStatEditor2)c).editValue();
                  }
                  else {
                      c.requestFocus();
                  }
              }
          }
          else if ( te.cpField.hasFocus() ) {
              Component c = te.getNextFocusableComponent();
              if ( c != null ) {
                  if ( c instanceof TargetStatEditor2 ) {
                      ((TargetStatEditor2)c).editCP();
                  }
                  else {
                      c.requestFocus();
                  }
              }
              else {
                  FocusManager.getCurrentManager().focusNextComponent(te.cpField);
              }
          }
      }
  }
  
}