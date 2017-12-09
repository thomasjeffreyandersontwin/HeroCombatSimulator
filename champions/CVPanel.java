/*
 * cvPanel.java
 *
 * Created on November 13, 2000, 7:28 PM
 */

package champions;

import java.beans.*;

import champions.interfaces.*;

/**
 *
 * @author  unknown
 * @version
 */
public class CVPanel extends javax.swing.JPanel
implements PropertyChangeListener, ChampionsConstants{
    
    /** Holds value of property cvList. */
    public CVList cvList;
    /** Holds value of property sourceCV. */
    private int sourceCV;
    /** Holds value of property targetCV. */
    private int targetCV;
    /** Creates new form cvPanel */
    CVBasePanel cvSourceBase,cvTargetBase;
    public CVRangePanel cvRangePanel;
    
    public CVPanel() {
        initComponents ();
        
        sourceDetails.setLayout( new PADLayout() );
        targetDetails.setLayout( new PADLayout() );
        
    }
    
    public void adjustDetails() {
        //int fail = 0;
        //if ( fail == 0 ) return;
        int count, index;
        String type,desc,special;
        Object value;
        boolean active;
        CVModPanel modPanel;
        CVMultiplePanel multiplePanel;
        CVSetPanel setPanel;
        
        if ( cvList == null ) {
            // Error
        }
        else {
            sourceName.setText( cvList.getSourceName() );
            targetName.setText( cvList.getTargetName() );
            
            sourceDetails.removeAll();
            
            String sourceIndex = cvList.getSourceIndex();
            
            cvSourceBase = new CVBasePanel(sourceIndex,cvList.getIntegerValue(sourceIndex + ".BASECV" ));
            cvSourceBase.addPropertyChangeListener(this);
            sourceDetails.add(cvSourceBase);
            
            count = cvList.getIndexedSize( sourceIndex );
            
            // Find the modifiers...
            for(index=0;index<count;index++) {
                if ( (type = cvList.getIndexedStringValue(index,sourceIndex,"TYPE")) != null && type.equals("MOD") ) {
                    value = cvList.getIndexedValue( index,sourceIndex,"VALUE" );
                    desc = cvList.getIndexedStringValue( index,sourceIndex,"DESCRIPTION");
                    active = cvList.getIndexedBooleanValue( index, sourceIndex, "ACTIVE" );
                    special = cvList.getIndexedStringValue(index, sourceIndex, "SPECIAL" );
                    
                    if ( special != null && special.equals("RANGE") ) {
                        cvRangePanel = new CVRangePanel(sourceIndex, index, desc, (Integer)value, active);
                        cvRangePanel.addPropertyChangeListener(this);
                        sourceDetails.add(cvRangePanel);
                    }
                    else if ( special != null && special.equals("CONCEALMENT") ) {
                         CVConcealmentPanel cvConcealmentPanel = new CVConcealmentPanel(sourceIndex, index, desc, (Integer)value, active);
                         cvConcealmentPanel.addPropertyChangeListener(this);
                         sourceDetails.add(cvConcealmentPanel);
                     }

                    else if ( special != null && special.equals("THROW") ) {
                        CVThrowPanel cvThrowPanel = new CVThrowPanel(sourceIndex, index, desc, (Integer)value, active);
                        cvThrowPanel.addPropertyChangeListener(this);
                        sourceDetails.add(cvThrowPanel);
                    }
                    else {
                        modPanel = new CVModPanel(sourceIndex, index, desc, (Integer)value, active);
                        modPanel.addPropertyChangeListener(this);
                        sourceDetails.add(modPanel);
                    }
                    
                    
                }
            }
            
            // Find the multipliers...
            for(index=0;index<count;index++) {
                if ( (type = cvList.getIndexedStringValue(index,sourceIndex,"TYPE")) != null && type.equals("MULTIPLE") ) {
                    value = cvList.getIndexedValue( index,sourceIndex,"VALUE" );
                    desc = cvList.getIndexedStringValue( index,sourceIndex,"DESCRIPTION");
                    active = cvList.getIndexedBooleanValue( index, sourceIndex, "ACTIVE" );
                    
                    multiplePanel = new CVMultiplePanel(sourceIndex, index, desc, (Double)value, active);
                    multiplePanel.addPropertyChangeListener(this);
                    sourceDetails.add(multiplePanel);
                }
            }
            
            // Find the sets...
            for(index=0;index<count;index++) {
                if ( (type = cvList.getIndexedStringValue(index,sourceIndex,"TYPE")) != null && type.equals("SET") ) {
                    value = cvList.getIndexedValue( index,sourceIndex,"VALUE" );
                    desc = cvList.getIndexedStringValue( index,sourceIndex,"DESCRIPTION");
                    active = cvList.getIndexedBooleanValue( index, sourceIndex, "ACTIVE" );
                    
                    setPanel = new CVSetPanel(sourceIndex, index, desc, (Integer)value, active);
                    setPanel.addPropertyChangeListener(this);
                    sourceDetails.add(setPanel);
                }
            }
            
            String targetIndex = cvList.getTargetIndex();
            
            // Setup Target Panel
            targetDetails.removeAll();
            cvTargetBase = new CVBasePanel(targetIndex,cvList.getIntegerValue(targetIndex + ".BASECV" ));
            targetDetails.add(cvTargetBase);
            cvTargetBase.addPropertyChangeListener(this);
            
            count = cvList.getIndexedSize( targetIndex );
            
            // Find the modifiers...
            for(index=0;index<count;index++) {
                if ( (type = cvList.getIndexedStringValue(index,targetIndex,"TYPE")) != null && type.equals("MOD") ) {
                    value = cvList.getIndexedValue( index,targetIndex,"VALUE" );
                    desc = cvList.getIndexedStringValue( index,targetIndex,"DESCRIPTION");
                    active = cvList.getIndexedBooleanValue( index, targetIndex, "ACTIVE" );
                    
                    modPanel = new CVModPanel(targetIndex, index, desc, (Integer)value, active);
                    modPanel.addPropertyChangeListener(this);
                    targetDetails.add(modPanel);
                }
            }
            
            // Find the multipliers...
            for(index=0;index<count;index++) {
                if ( (type = cvList.getIndexedStringValue(index,targetIndex,"TYPE")) != null && type.equals("MULTIPLE") ) {
                    value = cvList.getIndexedValue( index,targetIndex,"VALUE" );
                    desc = cvList.getIndexedStringValue( index,targetIndex,"DESCRIPTION");
                    active = cvList.getIndexedBooleanValue( index, targetIndex, "ACTIVE" );
                    
                    multiplePanel = new CVMultiplePanel(targetIndex, index, desc, (Double)value, active);
                    multiplePanel.addPropertyChangeListener(this);
                    targetDetails.add(multiplePanel);
                }
            }
            
            // Find the sets...
            for(index=0;index<count;index++) {
                if ( (type = cvList.getIndexedStringValue(index,targetIndex,"TYPE")) != null && type.equals("SET") ) {
                    value = cvList.getIndexedValue( index,targetIndex,"VALUE" );
                    desc = cvList.getIndexedStringValue( index,targetIndex,"DESCRIPTION");
                    active = cvList.getIndexedBooleanValue( index, targetIndex, "ACTIVE" );
                    
                    setPanel = new CVSetPanel(targetIndex, index, desc, (Integer)value, active);
                    setPanel.addPropertyChangeListener(this);
                    targetDetails.add(setPanel);
                }
            }
            
            adjustFinalValues();
        }
        
    }
    
    public void adjustFinalValues() {
        sourceCV = cvList.getSourceCV();
        finalSourceValue.setText( "= " + Integer.toString(sourceCV) );
        
        
        targetCV = cvList.getTargetCV();
        finalTargetValue.setText( "= " + Integer.toString(targetCV) );
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        sourcePanel = new javax.swing.JPanel();
        sourceLabel = new javax.swing.JLabel();
        sourceName = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        sourceDetails = new javax.swing.JPanel();
        finalSourceGroup = new javax.swing.JPanel();
        finalSourceLabel = new javax.swing.JLabel();
        finalSourceValue = new javax.swing.JLabel();
        targetPanel = new javax.swing.JPanel();
        targetLabel = new javax.swing.JLabel();
        targetName = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        targetDetails = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        finalTargetLabel = new javax.swing.JLabel();
        finalTargetValue = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        sourcePanel.setLayout(new java.awt.GridBagLayout());

        sourcePanel.setBorder(new javax.swing.border.TitledBorder("Attacker"));
        sourceLabel.setText("Attacker: ");
        sourcePanel.add(sourceLabel, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        sourcePanel.add(sourceName, gridBagConstraints);

        jScrollPane1.setPreferredSize(new java.awt.Dimension(120, 180));
        jScrollPane1.setMinimumSize(new java.awt.Dimension(100, 140));
        jScrollPane1.setViewportView(sourceDetails);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        sourcePanel.add(jScrollPane1, gridBagConstraints);

        finalSourceGroup.setLayout(new java.awt.GridBagLayout());

        finalSourceLabel.setText("Final OCV/ECV");
        finalSourceGroup.add(finalSourceLabel, new java.awt.GridBagConstraints());

        finalSourceValue.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        finalSourceValue.setFont(new java.awt.Font("Arial", 0, 11));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        finalSourceGroup.add(finalSourceValue, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 15);
        sourcePanel.add(finalSourceGroup, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        add(sourcePanel, gridBagConstraints);

        targetPanel.setLayout(new java.awt.GridBagLayout());

        targetPanel.setBorder(new javax.swing.border.TitledBorder("Target"));
        targetLabel.setText("Target: ");
        targetPanel.add(targetLabel, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        targetPanel.add(targetName, gridBagConstraints);

        jScrollPane2.setPreferredSize(new java.awt.Dimension(120, 180));
        jScrollPane2.setMinimumSize(new java.awt.Dimension(100, 140));
        jScrollPane2.setViewportView(targetDetails);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        targetPanel.add(jScrollPane2, gridBagConstraints);

        jPanel3.setLayout(new java.awt.GridBagLayout());

        finalTargetLabel.setText("Final DCV/ECV");
        jPanel3.add(finalTargetLabel, new java.awt.GridBagConstraints());

        finalTargetValue.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        finalTargetValue.setFont(new java.awt.Font("Arial", 0, 11));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(finalTargetValue, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 15);
        targetPanel.add(jPanel3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        add(targetPanel, gridBagConstraints);

    }//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel finalSourceGroup;
    private javax.swing.JLabel finalSourceLabel;
    private javax.swing.JLabel finalSourceValue;
    private javax.swing.JLabel finalTargetLabel;
    private javax.swing.JLabel finalTargetValue;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    public javax.swing.JPanel sourceDetails;
    private javax.swing.JLabel sourceLabel;
    private javax.swing.JLabel sourceName;
    private javax.swing.JPanel sourcePanel;
    private javax.swing.JPanel targetDetails;
    private javax.swing.JLabel targetLabel;
    private javax.swing.JLabel targetName;
    private javax.swing.JPanel targetPanel;
    // End of variables declaration//GEN-END:variables

    /** Holds value of property type. */
    private int type;    
    
    /** Getter for property cvList.
     * @return Value of property cvList.
     */
    public CVList getCVList() {
        return cvList;
    }
    /** Setter for property cvList.
     * @param cvList New value of property cvList.
     */
    public void setCVList(CVList cvList) {
        this.cvList = cvList;
        setType( cvList.getType() );
        adjustDetails();
    }
    /** Getter for property sourceCV.
     * @return Value of property sourceCV.
     */
    public int getSourceCV() {
        return sourceCV;
    }
    /** Setter for property sourceCV.
     * @param sourceCV New value of property sourceCV.
     */
    public void setSourceCV(int sourceCV) {
        this.sourceCV = sourceCV;
    }
    /** Getter for property targetCV.
     * @return Value of property targetCV.
     */
    public int getTargetCV() {
        return targetCV;
    }
    /** Setter for property targetCV.
     * @param targetCV New value of property targetCV.
     */
    public void setTargetCV(int targetCV) {
        this.targetCV = targetCV;
    }
    /** This method gets called when a bound property is changed.
     * @param evt A PropertyChangeEvent object describing the event source
     *  	and the property that has changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if ( cvList != null ) {
            String key = evt.getPropertyName();
            cvList.add( key, evt.getNewValue(), true);
            // Mark them as manual also
            int index = key.indexOf(".");
            String keyName = key.substring(0, index);
            cvList.add( keyName + "MANUAL", "TRUE", true);
            
            //System.out.println( evt.toString() );
            adjustFinalValues();
        }
    }
    
    /** Getter for property type.
     * @return Value of property type.
     */
    public int getType() {
        return type;
    }
    
    /** Setter for property type.
     * @param type New value of property type.
     */
    public void setType(int type) {
        this.type = type;
        
        switch (type) {
            case CVLIST_OCVDCV:
                sourceLabel.setText( "Attacker: " );
                finalSourceLabel.setText( "Final OCV:" );
                
                targetLabel.setText( "Target: " );
                finalTargetLabel.setText( "Final DCV:" );
                break;
            case CVLIST_ECV:
                sourceLabel.setText( "Attacker: " );
                finalSourceLabel.setText( "Final ECV:" );
                
                targetLabel.setText( "Target: " );
                finalTargetLabel.setText( "Final ECV:" );
                break;
            case CVLIST_BLOCK:
                sourceLabel.setText( "Blocker: " );
                finalSourceLabel.setText( "Blocking CV:" );
                
                targetLabel.setText( "Attacker: " );
                finalTargetLabel.setText( "Blocker CV:" );
                break;
        }
    }
}