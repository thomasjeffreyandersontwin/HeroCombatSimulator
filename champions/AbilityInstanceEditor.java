/*
 * AbilityInstanceEditor.java
 *
 * Created on June 7, 2004, 7:43 PM
 */

package champions;

import champions.event.AbilityAddedEvent;
import champions.event.AbilityRemovedEvent;
import champions.event.ActivationStateChangeEvent;
import champions.event.InstanceChangedEvent;
import champions.interfaces.AbilityInstanceGroupListener;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import tjava.Destroyable;



/**
 *
 * @author  1425
 */
public class AbilityInstanceEditor extends JPanel
implements Destroyable, AbilityInstanceGroupListener, ItemListener {
    
    protected Ability baseAbility;
    
    protected Ability editedAbility;
    
    /** Creates new form AbilityInstanceEditor */
    public AbilityInstanceEditor() {
        initComponents();
        
        instanceCombo.addItemListener(this);
        //abilityEditor.setUseRealCost(false);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        instanceCombo = new javax.swing.JComboBox();
        instanceLabel = new javax.swing.JLabel();
        splitPane = new javax.swing.JSplitPane();
        padScrollPane = new javax.swing.JScrollPane();
        padTreeTable = new champions.abilityTree.PADTree.PADTreeTable();
        abilityEditor = new champions.AbilityEditor();

        instanceLabel.setText("Select Template");

        splitPane.setDividerSize(3);
        splitPane.setResizeWeight(0.8);

        padScrollPane.setViewportView(padTreeTable);

        splitPane.setRightComponent(padScrollPane);
        splitPane.setLeftComponent(abilityEditor);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(instanceLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(instanceCombo, 0, 418, Short.MAX_VALUE))
                    .addComponent(splitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 528, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(instanceLabel)
                    .addComponent(instanceCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(splitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
 
    
    protected void buildModel() {

        InstanceComboModel model = new InstanceComboModel(baseAbility);
        instanceCombo.setModel( model );
        
        //informationGroup.setVisible( model.getSize() > 1 );
    }
    
    public void abilityAdded(AbilityAddedEvent evt) {
        
        buildModel();
    }
    
    public void abilityRemove(AbilityRemovedEvent evt) {
        if ( baseAbility == evt.getAbility() ) {
            setBaseAbility( ((AbilityInstanceGroup)evt.getSource()).getBaseInstance() );
        }
        else {
            buildModel();
        }
    }
    
    public void activationStateChanged(ActivationStateChangeEvent evt) {
        buildModel();
    }
    
    public void instanceChanged(InstanceChangedEvent evt) {
        buildModel();
    }
    
    public void destroy() {
        padTreeTable.destroy();
        abilityEditor.destroy();
        
        baseAbility = null;
        editedAbility = null;
    }
    
    /**
     * Getter for property baseAbility.
     * @return Value of property baseAbility.
     */
    public champions.Ability getBaseAbility() {
        return baseAbility;
    }
    
    /**
     * Setter for property baseAbility.
     * @param baseAbility New value of property baseAbility.
     */
    public void setBaseAbility(champions.Ability baseAbility) {
        if ( this.baseAbility != baseAbility ) {
            
           this.baseAbility = baseAbility; 
           
           buildModel();
           
           setEditedAbility(baseAbility);
        }
    }
    
    /**
     * Getter for property editedAbility.
     * @return Value of property editedAbility.
     */
    public champions.Ability getEditedAbility() {
        return editedAbility;
    }
    
    /**
     * Setter for property editedAbility.
     * @param editedAbility New value of property editedAbility.
     */
    public void setEditedAbility(champions.Ability editedAbility) {
        if ( this.editedAbility != editedAbility ) {
            abilityEditor.setAbility(editedAbility);
            this.editedAbility = editedAbility;
            
            InstanceEntry ie = (InstanceEntry)instanceCombo.getSelectedItem();
            if ( ie == null || ie.ability != editedAbility ) {
                ie= ((InstanceComboModel)instanceCombo.getModel()).findEntry(editedAbility);
                if ( ie != null ) {
                    instanceCombo.setSelectedItem(ie);
                }
            }
        }
    }

    
    public void itemStateChanged(java.awt.event.ItemEvent e) {
        InstanceEntry ie = (InstanceEntry)instanceCombo.getSelectedItem();
        if ( ie != null ) {
            setEditedAbility(ie.ability);
        }
    }    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private champions.AbilityEditor abilityEditor;
    private javax.swing.JComboBox instanceCombo;
    private javax.swing.JLabel instanceLabel;
    private javax.swing.JScrollPane padScrollPane;
    private champions.abilityTree.PADTree.PADTreeTable padTreeTable;
    private javax.swing.JSplitPane splitPane;
    // End of variables declaration//GEN-END:variables
    
    static public class InstanceComboModel extends DefaultComboBoxModel 
     {
        
        protected Ability ability;
        
        protected ArrayList entries;
        
        public InstanceComboModel(Ability ability) {
            this.ability = ability;
            
            buildModel();
        }
        
        protected void buildModel() {
            entries = new ArrayList();
            
            AbilityInstanceGroup aig = ability.getInstanceGroup();
            
            Iterator<Ability> it = aig.getInstances();
            while ( it.hasNext() ) {
                Ability a = it.next();
                if ( a.isBaseInstance() ) {
                    String name = a.getName() + " " + a.getSpecialInstanceDescription();
                    entries.add( new AbilityInstanceEditor.InstanceEntry(name, a));
                }
                else if ( a == aig.getAdjustedInstance() && a != aig.getFrameworkInstance() ) {
                    String name = a.getName() + " " + a.getSpecialInstanceDescription();
                    entries.add( new AbilityInstanceEditor.InstanceEntry(name, a));
                }
                else if (  a.isModifiableInstance() ) {
                    String name = a.getName();
                    if ( a.getInstanceDescription() != null ) {
                        name += "(" + a.getInstanceDescription() + ")";
                    }
                    entries.add( new AbilityInstanceEditor.InstanceEntry(name, a));
                }
            }
        }
        
        public int getSize() {
            return entries.size();
        }
        
        public Object getElementAt(int index) {
            return entries.get(index);
        }
        
        public InstanceEntry findEntry(Ability ability) {
            Iterator it = entries.iterator();
            while ( it.hasNext() ) {
                InstanceEntry ie= (InstanceEntry)it.next();
                if ( ie.ability == ability ) {
                    return ie;
                }
            }
            return null;
        }
    }
    
    static public class InstanceEntry {
        protected String title;
        protected Ability ability;
        public InstanceEntry(String title, Ability ability) {
            this.ability = ability;
            this.title = title;
        }
        
        public boolean equals(Object that) {
            return that != null && that instanceof InstanceEntry && ((InstanceEntry)that).ability == ability;
        }
        
        public String toString() {
            return title;
        }
    }
}
