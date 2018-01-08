/*
 * SelectTargetPanel.java
 *
 * Created on November 9, 2001, 1:53 PM
 */

package champions.attackTree;

import champions.Ability;
import champions.BattleEvent;
import champions.Target;
import champions.event.TargetSelectedEvent;
import champions.interfaces.ChampionsConstants;
import tjava.Filter;
import champions.interfaces.TargetListener;
import champions.targetTree.TTModel;
import champions.targetTree.TTNode;
import champions.targetTree.TTSelectTargetModel;
import champions.targetTree.TTTargetNode;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.event.EventListenerList;
import javax.swing.tree.TreePath;
import treeTable.DefaultTreeTableNode;

/**
 *
 * @author  twalker
 */
public class SelectTargetPanel extends JPanel implements MouseListener, AttackTreeInputPanel, ChampionsConstants {
    /** Stores a cached AttackParametersPanel which can be reused. */
    static public SelectTargetPanel ad = null;
    
    /** Stores the BattleEvent */
    protected BattleEvent battleEvent;
    
    /** Stores the current TargetGroup */
    protected String targetGroup;
    
    /** Stores the EventListenerList */
    private EventListenerList listenerList;
    
    private AttackTreePanel atip = null;
    
    private Target source;
    
    private boolean presetTarget;
    
    private Filter<Target> targetFilter;
    
    private String additionalNotes = null;
    
    //   public static final int NORMAL_TARGET = 1;
    //  public static final int KNOCKBACK_TARGET = 2;
    // public static final int AE_TARGET = 3;
    
    /** Creates new form SelectTargetPanel */
    public SelectTargetPanel() {
        initComponents();
        
        //  targetTree.getSelectionModel().addTreeSelectionListener(this);
    }
    
    protected void reinitialize() {
        //Target source = getBattleEvent().getSource();
        
        TTSelectTargetModel m = new TTSelectTargetModel(source, targetFilter);
        
        targetTree.setTreeTableModel( m );
        
        expandAll(new TreePath(m.getRoot()));
        
        // Clear any old listeners by creating a new listener list.
        listenerList = new EventListenerList();
        
        setPresetTarget(false);
        
        setAdditionalNotes(null);
    }
    
    static public SelectTargetPanel getSelectTargetPanel(BattleEvent be, String targetGroup, int targetType, Filter<Target> targetFilter) {
        if ( ad == null ) ad = new SelectTargetPanel();
        ad.setBattleEvent(be);
        ad.setTargetGroup(targetGroup);
        ad.setTargetType(targetType);
        ad.setTargetSelected(false);
        ad.setSource(be.getSource());
        ad.setTargetFilter(targetFilter);
        
        ad.reinitialize();
        
        return ad;
    }
    
    static public SelectTargetPanel getSelectTargetPanel(BattleEvent be, String targetGroup, int targetType, Target source, Filter<Target> targetFilter) {
        if ( ad == null ) ad = new SelectTargetPanel();
        ad.setBattleEvent(be);
        ad.setTargetGroup(targetGroup);
        ad.setTargetType(targetType);
        ad.setTargetSelected(false);
        ad.setSource(source);
        ad.setTargetFilter(targetFilter);
        
        ad.reinitialize();
        
        return ad;
    }
    
    static public SelectTargetPanel getSelectTargetPanel(BattleEvent be, String targetGroup, int targetType, boolean targetSelected, Filter<Target> targetFilter) {
        if ( ad == null ) ad = new SelectTargetPanel();
        ad.setBattleEvent(be);
        ad.setTargetGroup(targetGroup);
        ad.setTargetSelected(targetSelected);
        ad.setTargetType(targetType);
        ad.setSource(be.getSource());
        
        ad.reinitialize();
        
        return ad;
    }
    
    static public SelectTargetPanel getSelectTargetPanel(BattleEvent be, String targetGroup, int targetType, boolean targetSelected, Target source, Filter<Target> targetFilter) {
        if ( ad == null ) ad = new SelectTargetPanel();
        ad.setBattleEvent(be);
        ad.setTargetGroup(targetGroup);
        ad.setTargetSelected(targetSelected);
        ad.setTargetType(targetType);
        ad.setSource(source);
        ad.setTargetFilter(targetFilter);
        
        ad.reinitialize();
        
        return ad;
    }
    
    public void fireTargetingEvent(Target target, boolean preset) {
        if ( listenerList != null ) {
            TargetSelectedEvent e = new TargetSelectedEvent(this, target,preset);
            // Guaranteed to return a non-null array
            Object[] listeners = listenerList.getListenerList();
            // Process the listeners last to first, notifying
            // those that are interested in this event
            for (int i = listeners.length-2; i>=0; i-=2) {
                if (listeners[i]==TargetListener.class) {
                    // System.out.println( "Firing chance event to: " + listeners[i+1].toString());
                    ((TargetListener)listeners[i+1]).targetSelected(e);
                     TargetListener[] list = this.getListeners(TargetListener.class);
                }
            }
        }
    }
    
    /**
     *  Adds a <code>Battle</code> listener.
     *
     *  @param l  the <code>ChangeListener</code> to add
     */
    public void addTargetListener(TargetListener l) {
        listenerList.add(TargetListener.class,l);
    }
    
    /**
     * Removes a <code>Battle</code> listener.
     *
     * @param l  the <code>BattleListener</code> to remove
     */
    public void removeTargetListener(TargetListener l) {
        listenerList.remove(TargetListener.class,l);
    }
    
    /** Getter for property battleEvent.
     * @return Value of property battleEvent.
     */
    public BattleEvent getBattleEvent() {
        return battleEvent;
    }
    /** Setter for property battleEvent.
     * @param battleEvent New value of property battleEvent.
     */
    public void setBattleEvent(BattleEvent battleEvent) {
        this.battleEvent = battleEvent;
    }
    
    /** Getter for property battleEvent.
     * @return Value of property battleEvent.
     */
    public String getTargetGroup() {
        return targetGroup;
    }
    /** Setter for property battleEvent.
     * @param battleEvent New value of property battleEvent.
     */
    public void setTargetGroup(String targetGroup) {
        this.targetGroup = targetGroup;
    }
    
    public void showPanel(AttackTreePanel atip) {
        this.atip = atip;
        //targetTree.getSelectionModel().addTreeSelectionListener(this);
        
        targetTree.addMouseListener(this);
        targetTree.requestFocusInWindow();
        
        updatePanel();
    }
    
    public JPanel getPanel() {
        return this;
    }
    
    public void hidePanel() {
        //targetTree.getSelectionModel().removeTreeSelectionListener(this);
        targetTree.removeMouseListener(this);
        
        setBattleEvent(null);
        setSource(null);
        setTargetFilter(null);
        
        targetTree.setTreeTableModel( new TTModel( new TTNode(), ""));
        
        listenerList = null;
    }
    
    private void updatePanel() {
        if ( additionalNotes == null ) {
            notesLabel.setVisible(false);
        } else {
            notesLabel.setVisible(true);
            notesLabel.setText(additionalNotes);
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        targetTreeScroll = new javax.swing.JScrollPane();
        targetTree = new champions.targetTree.TargetTree();
        buttonGroup = new javax.swing.JPanel();
        noTargetButton = new javax.swing.JButton();
        noMoreTargetsButton = new javax.swing.JButton();
        notesLabel = new javax.swing.JLabel();

        targetTreeScroll.setViewportView(targetTree);

        noTargetButton.setText("No Target");
        noTargetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noTargetButtonActionPerformed(evt);
            }
        });
        buttonGroup.add(noTargetButton);

        noMoreTargetsButton.setText("No More Targets");
        noMoreTargetsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noMoreTargetsButtonActionPerformed(evt);
            }
        });
        buttonGroup.add(noMoreTargetsButton);

        notesLabel.setFont(UIManager.getFont("CombatSimulator.defaultFont"));
        notesLabel.setText("<html>This is some text. This is some text. This is some text. This is some text. This is some text. This is some text. This is some text. This is some text. This is some text. This is some text. This is some text. This is some text. This is some text. </html>");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(targetTreeScroll, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 490, Short.MAX_VALUE)
                    .addComponent(buttonGroup, javax.swing.GroupLayout.DEFAULT_SIZE, 490, Short.MAX_VALUE)
                    .addComponent(notesLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 490, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(notesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(targetTreeScroll, javax.swing.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    private void noMoreTargetsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noMoreTargetsButtonActionPerformed
        // Add your handling code here:
        fireTargetingEvent(null,false);
        atip.advanceNode();
    }//GEN-LAST:event_noMoreTargetsButtonActionPerformed
    
    private void noTargetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noTargetButtonActionPerformed
        // Add your handling code here:
        fireTargetingEvent(null,false);
        atip.advanceNode();
    }//GEN-LAST:event_noTargetButtonActionPerformed
    
    /**
     * Called whenever the value of the selection changes.
     * @param e the event that characterizes the change.
     */
//    public void valueChanged(javax.swing.event.TreeSelectionEvent e) {
//        TreePath tp = e.getPath();
//        if ( tp != null ) {
//            Object last = tp.getLastPathComponent();
//            if ( last instanceof TTTargetNode ) {
//                Target t = ((TTTargetNode)last).getTarget();
//                boolean preset = ((TTTargetNode)last).isPreset();
//
//                setPresetTarget(preset);
//
//                Target source = battleEvent.getSource();
//
//                if ( source != null &&  t != source && preset == false) source.addRecentTarget(t);
//
//                fireTargetingEvent( t , preset);
//                atip.advanceNode();
//            }
//        }
//    }
    
    public void selectTarget(Target target) {
    	SelectTarget(target, false);
        
    }
    
    /** Getter for property targetType.
     * @return Value of property targetType.
     */
    public int getTargetType() {
        return targetType;
    }
    
    /** Setter for property targetType.
     * @param targetType New value of property targetType.
     */
    public void setTargetType(int targetType) {
        this.targetType = targetType;
        
        Ability ability = battleEvent.getAbility();
        
        switch ( targetType ) {
            case NORMAL_TARGET:
            case AE_CENTER:
                //targetSelfButton.setVisible(ability.can("TARGETSELF"));
                noMoreTargetsButton.setVisible(false);
                noTargetButton.setVisible(false);
                
                break;
            case SKILL_TARGET:
                //targetSelfButton.setVisible(ability.can("TARGETSELF"));
                noMoreTargetsButton.setVisible(false);
                noTargetButton.setVisible(false);
                
                break;
            case AE_TARGET:
            case AE_SELECTIVE_TARGET:
            case AE_NONSELECTIVE_TARGET:
                //targetSelfButton.setVisible(ability.can("TARGETSELF"));
                noMoreTargetsButton.setVisible(true);
                if ( targetSelected == true ) {
                    noMoreTargetsButton.setText( "Remove Target" );
                } else {
                    noMoreTargetsButton.setText( "No More Targets" );
                }
                noTargetButton.setVisible(false);
                break;
            case KNOCKBACK_TARGET:
                //targetSelfButton.setVisible(false);
                noMoreTargetsButton.setVisible(false);
                noTargetButton.setVisible(true);
                break;
            case SECONDARY_TARGET:
                //targetSelfButton.setVisible(ability.can("TARGETSELF"));
                
                if ( targetSelected == true ) {
                    noMoreTargetsButton.setText( "Remove Target" );
                    noMoreTargetsButton.setVisible(true);
                    noTargetButton.setVisible(false);
                } else {
                    noMoreTargetsButton.setVisible(false);
                    noTargetButton.setVisible(true);
                }
                
                break;
        }
    }
    
    /** Getter for property targetSelected.
     * @return Value of property targetSelected.
     */
    public boolean isTargetSelected() {
        return targetSelected;
    }
    
    /** Setter for property targetSelected.
     * @param targetSelected New value of property targetSelected.
     */
    public void setTargetSelected(boolean targetSelected) {
        this.targetSelected = targetSelected;
    }
    
    public void expandAll(TreePath path) {
        DefaultTreeTableNode node = (DefaultTreeTableNode)path.getLastPathComponent();
        
        if ( node instanceof TTNode == false || ((TTNode)node).isExpandedByDefault() ) {
            
            targetTree.expandPath(path);
            
            int count = node.getChildCount( );
            for(int index = 0; index < count; index++) {
                TreePath newPath = path.pathByAddingChild( node.getChildAt(index));
                expandAll(newPath);
            }
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonGroup;
    private javax.swing.JButton noMoreTargetsButton;
    private javax.swing.JButton noTargetButton;
    private javax.swing.JLabel notesLabel;
    private champions.targetTree.TargetTree targetTree;
    private javax.swing.JScrollPane targetTreeScroll;
    // End of variables declaration//GEN-END:variables
    
    /** Holds value of property targetType. */
    private int targetType;
    
    /** Holds value of property targetReferenceNumber. */
    private int targetReferenceNumber;
    
    /** Holds value of property targetSelected. */
    private boolean targetSelected;
    
    /*public class AllTargetListModel extends DefaultListModel
    implements ListModel {
        protected Object[] targets;
     
        public AllTargetListModel(Target source) {
            if ( Battle.currentBattle != null ) {
                HashSet hs = Battle.currentBattle.getCombatants();
                hs.remove(source);
                targets = hs.toArray();
            }
        }
     
        public int getSize() {
            if ( targets != null ) {
                return targets.length;
            }
            return 0;
        }
     
        public Object getElementAt(int index) {
            if ( targets != null ) {
                return targets[index];
            }
            return null;
        }
    }*/
    
   /* public class RecentTargetsListModel extends DefaultListModel
    implements ListModel {
        protected Object[] targets;
    
        public RecentTargetsListModel(Target source) {
            if ( Battle.currentBattle != null ) {
                targets = source.getRecentTargetsArray();
            }
        }
    
        public int getSize() {
            if ( targets != null ) {
                return targets.length;
            }
            return 0;
        }
    
        public Object getElementAt(int index) {
            if ( targets != null ) {
                return targets[index];
            }
            return null;
        }
    }*/
    
   /* public class SpecialTargetListModel extends DefaultListModel
    implements ListModel {
        protected Object[] targets;
    
        public SpecialTargetListModel() {
            if ( Battle.currentBattle != null ) {
                Set hs = Battle.currentBattle.getSpecialTargets();
                targets = hs.toArray();
            }
        }
    
        public int getSize() {
            if ( targets != null ) {
                return targets.length;
            }
            return 0;
        }
    
        public Object getElementAt(int index) {
            if ( targets != null ) {
                return targets[index];
            }
            return null;
        }
    }*/
    
    public Target getSource() {
        return source;
    }
    
    public void setSource(Target source) {
        this.source = source;
    }
    
    public boolean isPresetTarget() {
        return presetTarget;
    }
    
    public void setPresetTarget(boolean presetTarget) {
        this.presetTarget = presetTarget;
    }
    
    public String getAdditionalNotes() {
        return additionalNotes;
    }
    
    public void setAdditionalNotes(String additionalNotes) {
        this.additionalNotes = additionalNotes;
        updatePanel();
    }
    
    public Filter<Target> getTargetFilter() {
        return targetFilter;
    }
    
    public void setTargetFilter(Filter<Target> targetFilter) {
        this.targetFilter = targetFilter;
    }
    
    public void mouseClicked(MouseEvent e) {
        
    }
    
    public void mousePressed(MouseEvent e) {
        if ( e.getSource() == targetTree && e.getClickCount() == 1 ) {
            
            TreePath tp = targetTree.getPathForLocation(e.getPoint().x, e.getPoint().y);
            if ( tp != null ) {
                Object last = tp.getLastPathComponent();
                if ( last instanceof TTTargetNode ) {
                    Target t = ((TTTargetNode)last).getTarget();
                    boolean preset = ((TTTargetNode)last).isPreset();
                    
                    setPresetTarget(preset);
                    
                    Target source = battleEvent.getSource();
                    
                    if ( source != null &&  t != source && preset == false) source.addRecentTarget(t);
                    //here here
                    
                    SelectTarget(t, preset);
                    
                }
            }
        }
    }
    
    public void SelectTarget(Target t, boolean preset) {
    	if(SingleTargetNode.Node==null) {
    		new SingleTargetNode("");
    	}
    	if(SingleTargetNode.Node!=null && t!= SingleTargetNode.Node.getTarget()) {
    		
    		fireTargetingEvent( t , preset);
    		//jeff
    		atip.advanceNode();
    	}
    }
    
    public void mouseReleased(MouseEvent e) {
    }
    
    public void mouseEntered(MouseEvent e) {
    }
    
    public void mouseExited(MouseEvent e) {
    }
    
    
}
