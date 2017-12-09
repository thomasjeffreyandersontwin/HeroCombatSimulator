/*
 * PreferencesPanel.java
 *
 * Created on December 21, 2000, 8:41 PM
 */

package champions;

import champions.event.PADValueEvent;
import champions.interfaces.PADValueListener;
import champions.parameters.BooleanParameter;
import champions.parameters.ComboParameter;
import champions.parameters.DiceParameter;
import champions.parameters.DoubleParameter;
import champions.parameters.FileParameter;
import champions.parameters.IntegerParameter;
import champions.parameters.PreferenceList;
import champions.parameters.Parameter;
import champions.parameters.ParameterList;
import champions.parameters.StringParameter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author  unknown
 * @version
 */
public class PreferencesPanel extends JPanel implements PADValueListener {
    
    /** Holds value of property preferenceList. */
    private PreferenceList preferenceList;
    /** Creates new form PreferencesPanel */
    public PreferencesPanel() {
        initComponents();
    }
    
    public void setupPreferences() {
        int index, count;
        int pindex,pcount;
        Object panel,pad, defaultValue, value;
        String title;
        JScrollPane scroll;
        PADAbstractEditor p;
        String padPanel, type, key, desc, name, help;
        boolean enabled;
        
        tabbedPane.removeAll();
        
        if ( preferenceList != null ) {
            
            //count = preferenceList.getIndexedSize( "Panel" );
            count = preferenceList.getPanelCount();
            //pcount = preferenceList.getIndexedSize( "parameter" );
            for ( index=0;index<count;index++) {
                
                //title = preferenceList.getIndexedStringValue( index, "Panel","NAME");
                title = preferenceList.getPanel(index);
                //    if ( (panel = preferenceList.getIndexedValue( index,"Panel","PANEL" )) == null ) {
                panel = new JPanel();
                ((JPanel)panel).setLayout( new PADLayout() );
                // JPanel group = new JPanel();
                scroll = new JScrollPane();
                scroll.setViewportView((JPanel)panel);
                //preferenceList.addIndexed( index,   "Panel","PANEL",panel) ;
                tabbedPane.addTab( title, scroll);
                //   }
                
                // Setup preferences
                Iterator<Parameter> it = preferenceList.getParameters();
                while ( it.hasNext() ) {
                    Parameter param = it.next();
                    name = param.getName();
                    
                    padPanel = preferenceList.getParameterPanel(name);
                    if ( padPanel == null || ! padPanel.equals(title) ) continue;
                   
                    
                    if ( param != null ) {
                        key = param.getKey();
                        desc = param.getDescription();
                        enabled = preferenceList.isParameterEnabled(name);
                        defaultValue = param.getDefaultValue();
                        value = preferenceList.getParameterValue(name);
                        
                        help = preferenceList.getHelpText(name);
                        
                        if ( value == null ) value = defaultValue;
                        //detailList.add ( key, value, true );
                        p = null;
                        
                        if ( param instanceof DiceParameter ) {
                            // Create a dice configuration panel
                            if ( value != null )  {
                                p = new PADDiceEditor(key,desc,(String)value,this);
                            } else {
                                p = new PADDiceEditor(key,desc,this);
                            }
                        } else if ( param instanceof IntegerParameter ) {
                            // Create a integer configuration panel
                            if ( value != null )  {
                                p = new PADIntegerEditor(key,desc,(Integer)value,this);
                            } else {
                                p = new PADIntegerEditor(key,desc,this);
                            }
                        } else if ( param instanceof DoubleParameter ) {
                            // Create a Double configuration panel
                            //Object increment = param.getOption("INCREMENT");
                            //if ( increment == null || increment.getClass() != Double.class ) return;
                            double increment = ((DoubleParameter)param).getIncrement();
                            if ( value != null )  {
                                p = new PADDoubleEditor(key,desc,(Double)value, (Double)increment,this);
                            } else {
                                p = new PADDoubleEditor(key,desc,(Double)increment,this);
                            }
                        } else if ( param instanceof StringParameter) {
                            // Create a string configuration panel
                            if ( value != null )  {
                                p = new PADStringEditor(key,desc,(String)value,this);
                            } else {
                                p = new PADStringEditor(key,desc,this);
                            }
                        } else if ( param instanceof FileParameter ) {
                            // Create a string configuration panel
                            boolean file = ((FileParameter)param).isFile();
                            
                            if ( value != null )  {
                                p = new PADFileEditor(key,desc,(String)value,file,this);
                            } else {
                                p = new PADFileEditor(key,desc,file,this);
                            }
                        } else if ( param instanceof BooleanParameter ) {
                            // Create a string configuration panel
                            if ( value != null )  {
                                p = new PADBooleanEditor(key,desc,(Boolean)value,this);
                            } else {
                                p = new PADBooleanEditor(key,desc,this);
                            }
                        } else if ( param instanceof ComboParameter ) {
                            // Create a JComboBox configuration panel
                            // Requires VALUES parameter
                            Object[] options = ((ComboParameter)param).getOptions();
                            if ( value != null )  {
                                p = new PADComboEditor(key,desc,value,options,this);
                            } else {
                                p = new PADComboEditor(key,desc,options,this);
                            }
                        } else  {
                            // Create a Unknown configuration panel
                           // p = new PADUnknownEditor(key,desc,type,this);
                            System.out.println("Error creating PADEditor for parameter " + name);
                            continue;
                        }
                        
                        preferenceList.setParameterEnabled(name, enabled);
                        p.setName(name);
                        p.setHelp(help, helpTextArea);
                        ((JPanel)panel).add( p );
                        p.addPADValueListener( this);
                        
                    }
                }
            }
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        tabbedPane = new javax.swing.JTabbedPane();
        helpTextArea = new javax.swing.JTextArea();
        controlGroup = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(tabbedPane, gridBagConstraints);

        helpTextArea.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        helpTextArea.setEditable(false);
        helpTextArea.setLineWrap(true);
        helpTextArea.setWrapStyleWord(true);
        helpTextArea.setDisabledTextColor(new java.awt.Color(102, 102, 153));
        helpTextArea.setEnabled(false);
        helpTextArea.setMinimumSize(new java.awt.Dimension(0, 64));
        helpTextArea.setPreferredSize(new java.awt.Dimension(0, 64));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(helpTextArea, gridBagConstraints);

        controlGroup.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        jButton1.setText("Reset Defaults");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        controlGroup.add(jButton1);

        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        controlGroup.add(closeButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(controlGroup, gridBagConstraints);

    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        Preferences.resetPreferences();
        setPreferenceList( Preferences.getPreferenceList() );
    }//GEN-LAST:event_jButton1ActionPerformed
    
    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        // Add your handling code here:
        getWindow().dispose();
    }//GEN-LAST:event_closeButtonActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton;
    private javax.swing.JPanel controlGroup;
    private javax.swing.JTextArea helpTextArea;
    private javax.swing.JButton jButton1;
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables
    
    /** Holds value of property window. */
    private PreferencesWindow window;
    
    /** Getter for property preferenceList.
     * @return Value of property preferenceList.
     */
    public ParameterList getPreferenceList() {
        return preferenceList;
    }
    /** Setter for property preferenceList.
     * @param preferenceList New value of property preferenceList.
     */
    public void setPreferenceList(PreferenceList preferenceList) {
        this.preferenceList = preferenceList;
        setupPreferences();
    }
    
    public void PADValueChanged(PADValueEvent evt) {
        //Preferences.getPreferenceList().add( evt.getKey(), evt.getValue(), true );
        
        String s;
        String parameterName = Preferences.getPreferenceList().findParameterKey((String)evt.getKey());
        if ( parameterName != null ) {
            if ((s = Preferences.getPreferenceList().getParameter(parameterName).getClassVariable()) != null) {
                int index = s.indexOf(".");
                int lastIndex = -1;
                while ( index != -1 ) {
                    lastIndex = index;
                    index = s.indexOf(".", index+1);
                }
                
                if ( lastIndex != -1 ) {
                    String c = s.substring(0, lastIndex );
                    String v = s.substring(lastIndex+1);
                    
                    try {
                        Class cl = Class.forName(c);
                        
                        if ( cl != null ) {
                            Field f = cl.getDeclaredField(v);
                            if ( Modifier.isStatic(f.getModifiers()) && Modifier.isPublic(f.getModifiers()) ) {
                                f.set(null, evt.getValue());
                            }
                        }
                    } catch ( Exception e ) {
                        
                    }
                }
            }
            
            Preferences.getPreferenceList().setParameterValue(parameterName, evt.getValue());
        }
    }
    
    public boolean PADValueChanging(PADValueEvent evt) {
        return true;
    }
    
    /** Getter for property window.
     * @return Value of property window.
     */
    public PreferencesWindow getWindow() {
        return this.window;
    }
    
    /** Setter for property window.
     * @param window New value of property window.
     */
    public void setWindow(PreferencesWindow window) {
        this.window = window;
    }
    
}