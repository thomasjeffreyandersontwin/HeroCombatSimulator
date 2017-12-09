/*
 * PADTargetNode.java
 *
 * Created on February 27, 2002, 11:43 PM
 */

package champions.abilityTree2;

import champions.Battle;
import champions.BattleEvent;
import champions.CombatState;
import champions.Effect;
import champions.EffectDetail;
import champions.EffectPanelTreeTableEditor;
import champions.EffectPanelTreeTableRenderer;
import champions.GenericEffectDetail;
import champions.Roster;
import champions.Target;
import champions.enums.DefenseType;
import champions.exception.BattleEventException;
import java.util.Comparator;
import tjava.Filter;
import champions.powers.effectDead;
import champions.powers.effectDying;
import champions.powers.effectGeneric;
import champions.powers.effectKnockedDown;
import champions.powers.effectHipshot;
import champions.powers.effectStunned;
import champions.powers.effectUnconscious;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;
import treeTable.DefaultTreeTableModel;
import treeTable.TreeTable;
import treeTable.TreeTableCellEditor;
import treeTable.TreeTableCellRenderer;
import VirtualDesktop.*;


/**
 *
 * @author  twalker
 * @version
 *
 * The ATTargetNode is the root node for a list of all possible abilities
 * and disadvantages that can be used by the target.  This includes the
 * abilities owned by the target and the default abilities.
 *
 * The ATTargetNode2 should be used in situations where listing of the abilities
 * of a target, not including the default abilities, and any target specific
 * actions are needed.
 *
 * The ATTargetNode should be used when a full display of all target information
 * is needed.  This includes effect, stats, and possible abilities.
 */
public class ATTargetNode extends ATNode implements PropertyChangeListener, ActionListener {
    
    /** Holds value of property target. */
    private Target target;
    
    private boolean expanded = false;
    
    protected List<Effect> effects = null;
    
    private boolean highlightActiveTarget = false;
    private boolean includeAbilityNode = true;
    private boolean effectEnabled = false;
    
    private boolean editEnabled = true;
    
    private boolean needsUpdate = false;
    
    private ATTargetEffectNode effectNode = null;
    
    protected boolean flat;
    
    protected static ATTargetNode.EditCharacterAction editTarget;
    protected static ATTargetNode.DebugCharacterAction debugTarget;
    protected static ATTargetNode.SaveCharacterAction saveAction;
    protected static ATTargetNode.SaveAsCharacterAction saveAsAction;
    protected static ATTargetNode.AbortAction abortAction;
    protected static ATTargetNode.HipshotAction hipshotAction;
    protected static ATTargetNode.AddEffectAction addGenericEffect;
    protected static ATTargetNode.ShowSenseAction showSenses;
    protected static ATTargetNode.RemoveCharacterAction removeTarget;
    protected static MessageExporter.VirtualDesktopControllerAction spawnTarget;
    protected static MessageExporter.VirtualDesktopControllerAction targetTarget;
    protected static MessageExporter.VirtualDesktopControllerAction moveTargetToCamera;
    protected static MessageExporter.VirtualDesktopControllerAction manageAnimationsForTarget;
    //protected static ATTargetNode.VIrtualDesktopCOntrollerAction placeTargetAtLocation;
    protected static HealTargetAction healAction;
    
    protected static ATTargetCellRenderer targetCellRenderer = new ATTargetCellRenderer();
    protected static ATTargetCellRenderer targetCellEditor = new ATTargetCellRenderer();
    
//    private static RemoveTargetAction deleteAction = null;
//    private static DebugAction debugAction = null;
//    private static EditAction editAction = null;
    
    /** Creates new PADTargetNode */
//    public ATTargetNode(Target target) {
//        this(target, null, false);
//    }
    
    public ATTargetNode(Target target, boolean flat, ATNodeFactory nodeFactory, Filter<Object> nodeFilter, boolean pruned) {
        super(nodeFactory, nodeFilter, pruned);
        setup(target, flat);
    }
    
    public void setup(Target target, boolean flat) {
        this.flat = flat;
        
        setupActions();
        
        // Do this last since it will trigger a build...
        setTarget(target);
        
    }
    
    /** sets up actions which will be used in the invokeMenu method.
     *
     */
    private void setupActions() {
        if ( editTarget == null ) editTarget = new EditCharacterAction();
        if ( debugTarget == null ) debugTarget = new DebugCharacterAction();
        if ( saveAction == null ) saveAction = new SaveCharacterAction();
        if ( saveAsAction == null ) saveAsAction = new SaveAsCharacterAction();
        if ( abortAction == null ) abortAction = new AbortAction();
        if ( hipshotAction == null ) hipshotAction = new HipshotAction();
        if ( addGenericEffect == null ) addGenericEffect = new AddEffectAction();
        if ( showSenses == null ) showSenses = new ShowSenseAction();
        if ( healAction == null ) healAction = new HealTargetAction();
        if ( removeTarget == null ) removeTarget = new RemoveCharacterAction();
        if (spawnTarget == null ) spawnTarget =  MessageExporter.GetVIrtualDesktopAction("Spawn Character");
        if (targetTarget == null ) targetTarget = MessageExporter.GetVIrtualDesktopAction("Manuever with Camera");
        if (moveTargetToCamera == null ) moveTargetToCamera = MessageExporter.GetVIrtualDesktopAction("Move To Camera");
        if (manageAnimationsForTarget == null ) manageAnimationsForTarget = MessageExporter.GetVIrtualDesktopAction("Manage Animations");
    }
    
    public void buildNode() {
        if ( expanded ) {
            removeAndDestroyAllChildren();
            
            if ( target != null ) {
                ATTargetActionsNode node = nodeFactory.createTargetActionsNode(target, nodeFilter, pruned);
                if ( node != null ) {
                    if ( !isPruned() || node.getChildCount() > 0 ) {
                        add(node);
                    } else {
                        node.destroy();
                    }
                }
                
                if ( isEffectEnabled() ) {
                    effectNode = nodeFactory.createTargetEffectNode(effects, nodeFilter, pruned);
                    if( effectNode != null ) {
                        add(effectNode);
                    }
                }
                
                if ( isIncludeAbilityNode() ) {
                    ATAbilityListNode aln = nodeFactory.createAbilityListNode( target.getAbilityList(), flat, nodeFilter, pruned);
                    if ( aln != null ) {
                        if ( pruned == false || aln.getChildCount() != 0 ){
                            aln.setExpandedByDefault( isExpandedByDefault() );
                            add(aln);
                        } else {
                            aln.destroy();
                        }
                    }
                }
                
                ATAbilityListNode dan = nodeFactory.createDefaultAbilitiesNode(flat, nodeFilter, pruned);
                if ( dan != null ) {
                    if ( pruned == false || dan.getChildCount() != 0 ){
                        add(dan);
                    } else {
                        dan.destroy();
                    }
                }
                
                ATStatsNode sn = nodeFactory.createStatsNode(target, nodeFilter, pruned);
                if ( sn != null ) {
                    add(sn);
                }
                
//                if ( getModel() instanceof DefaultTreeTableModel ) {
//                    ((DefaultTreeTableModel)getModel()).nodeStructureChanged(this);
//                }
            }
            
        }
        
        updateEffects();
        
        if ( getModel() instanceof DefaultTreeTableModel ) {
            ((DefaultTreeTableModel)getModel()).nodeStructureChanged(this);
        }
        
        
    }
    
    public void updateTargetActions() {
        if ( target != null ) {
            if ( this.getChildCount() > 0 && getChildAt(0) instanceof ATTargetActionsNode ) {
                ATTargetActionsNode node = (ATTargetActionsNode)getChildAt(0);
                node.buildNode();
                if ( isPruned() && node.getChildCount() == 0 ) {
                    remove(0);
                    node.destroy();
                    if ( getModel() instanceof DefaultTreeTableModel ) {
                        ((DefaultTreeTableModel)getModel()).nodesWereRemoved(this, new int[] {0}, new Object[] {node});
                    }
                }
            } else {
                ATTargetActionsNode node = nodeFactory.createTargetActionsNode(target, nodeFilter, pruned);
                if ( node != null ) {
                    if ( !isPruned() || node.getChildCount() > 0 ) {
                        insert(node,0);
                        if ( getModel() instanceof DefaultTreeTableModel ) {
                            ((DefaultTreeTableModel)getModel()).nodesWereInserted(this, new int[] {0});
                        }
                    }
                }
            }
        }
    }
    
    public Object getValueAt(int column) {
        if ( target == null ) return null;
        
        ATColumn c = ATColumn.values()[column];
        
        switch(c) {
            case NAME_COLUMN:
                return target.getName();
            case DCV_COLUMN:
                return target.getCalculatedDCV();
            case ED_COLUMN:
                return target.getDefense(DefenseType.ED) + "/" + target.getDefense(DefenseType.rED);
            case EFFECTS_COLUMN:
                return effects;
            case OCV_COLUMN:
                return target.getCalculatedOCV();
            case PD_COLUMN:
                return target.getDefense(DefenseType.PD) + "/" + target.getDefense(DefenseType.rPD);
                
                
        }
        
        // If the switch didn't catch it, it might be a stat...
        if ( target.hasStat( c.getName().toUpperCase() ) ) {
            return target.getCurrentStat( c.getName().toUpperCase() );
        }
        
        return null;
    }
    
    public TreeTableCellRenderer getTreeTableCellRenderer(int columnIndex) {
        if ( columnIndex == ATColumn.NAME_COLUMN.ordinal() ) {
            return targetCellRenderer;
        } else if ( columnIndex == ATColumn.EFFECTS_COLUMN.ordinal() ) {
            return EffectPanelTreeTableRenderer.getDefaultRenderer();
        }
        
        return null;
    }
    
    public TreeTableCellEditor getTreeTableCellEditor(int columnIndex) {
        if ( columnIndex == ATColumn.NAME_COLUMN.ordinal() ) {
            return targetCellEditor;
        } else if ( columnIndex == ATColumn.EFFECTS_COLUMN.ordinal() ) {
            return EffectPanelTreeTableEditor.getDefaultEditor();
        }
        
        return null;
    }
    
    public boolean isCellEditable(int column) {
        if ( target == null ) return false;
        
        ATColumn c = ATColumn.values()[column];
        
        switch(c) {
            case NAME_COLUMN:
                return isEditEnabled();
            case EFFECTS_COLUMN:
                return true;
        }
        
        return false;
    }
    
    
    public void triggerUpdate() {
        
        if ( getModel() instanceof DefaultTreeTableModel && Battle.getCurrentBattle() != null && Battle.currentBattle.isProcessing() == false ) {
            ((DefaultTreeTableModel)getModel()).nodeChanged(this);
            needsUpdate = false;
        } else {
            needsUpdate = true;
        }
    }
    
    public void updateTree() {
        
        updateEffects();
        updateTargetActions();
        
        if ( needsUpdate ) triggerUpdate();
        
        super.updateTree();
    }
    
    
    public Color getBackgroundColor() {
        
        if ( target != null ) {
            if ( target.isDead() ) {
                return new Color(100, 100, 100);
            } else if ( target.isDying() ) {
                return new Color(255, 200, 200);
            } else if ( target.isUnconscious() ) {
                return new Color(150, 150, 150);
            } else if ( highlightActiveTarget && Battle.currentBattle != null && Battle.currentBattle.getActiveTarget() == getTarget() ) {
                
                if ( !target.isUnconscious() && (target.isStunned() || target.hasEffect( effectKnockedDown.class ))  ) {
                    return new Color(255,150,255);
                } else {
                    return new Color(150, 255, 150);
                }
            }
        }
        return null;
    }
    
    @Override
    public void triggerDefaultAction(MouseEvent mouseEvent) {
        if ( target != null ) {
            target.editTarget();
        }
    }
    
    public boolean isLeaf() {
        return false;
    }
    
    public void nodeWillExpand() {
        if ( expanded == false ) {
            expanded = true;
            rebuildNode();
        }
    }
    
    public void setExpanded(boolean expanded) {
        if ( this.expanded != expanded ) {
            this.expanded = expanded;
            rebuildNode();
        }
    }
    
//    public boolean isExpandedByDefault() {
//        return ( highlightActiveTarget && Battle.currentBattle != null && Battle.currentBattle.getActiveTarget() == getTarget() );
//    }
    
   /* public boolean startDrag(TreeTable tree, TreePath path, DragSourceListener listener, DragGestureEvent dge) {
        if ( target != null ) {
    
            Target o = (Target)target.clone();
    
            if ( o != null ) {
                Transferable t = new ObjectTransferable(o, Target.class);
    
                Point p = dge.getDragOrigin();
                Rectangle bounds = tree.getPathBounds(path);
                Point offset = new Point(p.x - bounds.x, p.y - bounds.y);
    
                BufferedImage i = ((DefaultTreeTable)tree).buildDragImage(path);
                DefaultTreeTable.startDrag(i, offset);
    
                dge.startDrag(null,i,offset, t, listener);
            }
        }
    } */
    
    public void handleDoubleClick(ATTree tree, TreePath targetPath) {
    	ATNode node = (ATNode) targetPath.getLastPathComponent();
    	Target t = node.getTarget();
    	Roster r=null;
    	try{
    		ATRosterNode rnode =(ATRosterNode) targetPath.getLastPathComponent();
    		r= rnode.getRoster();
    	}catch(Exception e){}
    	
    	VirtualDesktop.MessageExporter.exportEvent("Node Double CLick", t,r );
    }
    
    
    /** Allows the node to provide context menu feedback.
     *
     * invokeMenu is called whenever an event occurs which would cause a context menu to be displayed.
     * If the node has context menu items to display, it should add them to the JPopupMenu <code>popup</code>
     * and return true.
     * @param treeTable Indicates the TreeTable which is currently displaying the node.
     * @param path The TreePath which the cursor was over when the context menu
     * event occurred.
     * @param popup The JPopupMenu which is being built.  Always non-null and initialized.
     * @return True if menus where added, False if menus weren't.
     */
    public boolean invokeMenu(TreeTable treeTable,TreePath path, JPopupMenu popup) {
        boolean rv = false;
        if ( path.getLastPathComponent() == this ) {
            
            abortAction.setTarget(target);
            popup.add( abortAction );
            abortAction.setEnabled( target.isAbortable() );
            hipshotAction.setTarget(target);
            popup.add( hipshotAction );
            hipshotAction.setEnabled( target.isAbortable() );
            
            popup.addSeparator();
            
            
            addGenericEffect.setTarget(target);
            popup.add( addGenericEffect);
            popup.addSeparator();
            
            healAction.setTarget(target );
            popup.add(healAction);
            
            showSenses.setTarget(target);
            popup.add( showSenses );
            if ( Battle.debugLevel >= 1) {
                debugTarget.setTarget(target);
                popup.add( debugTarget);
                popup.addSeparator();
            }
            
            
            saveAction.setTarget(target);
            popup.add( saveAction);
            
            saveAsAction.setTarget(target);
            popup.add( saveAsAction);
            
            editTarget.setTarget(target);
            popup.add( editTarget );
            
            popup.addSeparator();
            ATRosterNode parent=null;
            
//            if(getParent().getClass() == ATRosterNode.class) {
            	parent= ((ATRosterNode)getParent());
            	Roster r= parent.getRoster();
                target.setRoster(r);
                spawnTarget.setTarget(target);
                popup.add( spawnTarget );
                
                targetTarget.setTarget(target);
                popup.add( targetTarget );
                
                moveTargetToCamera.setTarget(target);
                popup.add( moveTargetToCamera );
                
                manageAnimationsForTarget.setTarget(target);
                popup.add( manageAnimationsForTarget );
                
                if ( target.getRoster() != null ) {
                    removeTarget.setTarget(target);
                    removeTarget.setRoster(target.getRoster());
                    popup.add( removeTarget );
                }
            }
            
            
       // }
        return rv;
    }
    
    /** Setter for property target.
     * @param target New value of property target.
     *
     */
    public void setTarget(Target target) {
        if ( this.target != target ) {
            if ( this.target != null ) {
                this.target.removePropertyChangeListener(this);
            }
            
            this.target = target;
            
            rebuildNode();
            
            if ( this.target != null ) {
                this.target.addPropertyChangeListener(this);
            }
        }
        
    }
    
//    protected void updateName() {
//        if ( model instanceof DefaultTreeTableModel ) {
//            ((DefaultTreeTableModel)model).nodeChanged(this);
//        }
//    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        String s = evt.getPropertyName();
        if ( s != null ) {
            if (  s.equals("Effect.INDEXSIZE") ) {
                updateEffects();
            } else if (s.equals("Target.NAME") ) {
                triggerUpdate();
            } else if ( s.equals("END") || s.equals("BODY") || s.equals("STUN" ) || s.equals("PD") || s.equals("ED") ) {
                triggerUpdate();
            } else if ( s.equals("ActivationInfo.INDEXSIZE") == false && s.equals("SENSES") == false) {
                //triggerUpdate();
            }
        }
    }
    
    
    /** Returns the Target associated with node.
     *
     * This should return whatever the node represents.
     *
     * @return null if no Target is associated, such as in the case of a folder.
     */
    public Target getTarget() {
        return target;
    }
    
    public void destroy() {
        super.destroy();
        
        setTarget(null);
    }
    
    protected void updateEffects() {
        if ( target != null && isEffectEnabled() ) {
            // Skip out if the effect lists are the same size...
            // There are probably rare time in which this isn't true, but
            // it is rare enough not to worry about it...
            if ( effects != null && effects.size() == target.getEffectCount() ) return;
            
            List<Effect> e = new ArrayList<Effect>();
            
            List<Effect> targetEffects = target.getEffects();
            
            int count = targetEffects.size();
            for(int eindex = 0; eindex < count; eindex++) {
                Effect effect = targetEffects.get(eindex);
                if ( effect instanceof effectDead ) {
                    e.add(effect);
                    break;
                }
            }
            
            for(int eindex = 0; eindex < count; eindex++) {
                Effect effect = targetEffects.get(eindex);
                if ( effect instanceof effectDying ) {
                    e.add(effect);
                    break;
                }
            }
            
            for(int eindex = 0; eindex < count; eindex++) {
                Effect effect = targetEffects.get(eindex);
                if ( effect instanceof effectUnconscious ) {
                    e.add(effect);
                    break;
                }
            }
            
            
            
            for(int eindex = 0; eindex < count; eindex++) {
                Effect effect = targetEffects.get(eindex);
                if ( effect instanceof effectStunned ) {
                    e.add(effect);
                    break;
                }
            }
            
            for(int eindex = 0; eindex < count; eindex++) {
                Effect effect = targetEffects.get(eindex);
                if ( effect instanceof effectKnockedDown ) {
                    e.add(effect);
                    break;
                }
            }
            
            for(int eindex = 0; eindex < count; eindex++) {
                Effect effect = targetEffects.get(eindex);
                if ( effect.isCritical() &&
                        effect instanceof effectDead == false &&
                        effect instanceof effectUnconscious == false &&
                        effect instanceof effectKnockedDown == false &&
                        effect instanceof effectDying == false &&
                        effect instanceof effectStunned == false) {
                    e.add(effect);
                }
            }
            
            for(int eindex = 0; eindex < count; eindex++) {
                Effect effect = targetEffects.get(eindex);
                if ( effect.isCritical() == false) {
                    e.add(effect);
                }
            }
            
            setEffectList(e);
            
        } else {
            setEffectList(null);
        }
    }
    
    protected void setEffectList(List<Effect> e) {
        if ( effects != e && (effects == null || effects.equals(e) == false )) {
            effects = e;
            triggerUpdate();
            
            if ( effectNode != null ) {
                effectNode.setEffectList(effects);
                effectNode.triggerUpdate();
            }
        }
    }
    
    public boolean isHighlightActiveTarget() {
        return highlightActiveTarget;
    }
    
    public void setHighlightActiveTarget(boolean highlightActiveTarget) {
        if ( this.highlightActiveTarget != highlightActiveTarget ) {
            this.highlightActiveTarget = highlightActiveTarget;
            rebuildNode();
        }
    }
    
    public void actionPerformed(ActionEvent e) {
        if ( e.getSource() instanceof Effect ) {
            Effect effect = (Effect)e.getSource();
            EffectDetail ed = new EffectDetail(effect);
            ed.showEffectDetail(null);
        }
    }
    
    
    public boolean isIncludeAbilityNode() {
        return includeAbilityNode;
    }
    
    public void setIncludeAbilityNode(boolean includeAbilityNode) {
        if ( this.includeAbilityNode != includeAbilityNode ) {
            this.includeAbilityNode = includeAbilityNode;
            rebuildNode();
        }
    }
    
    public boolean isEffectEnabled() {
        return effectEnabled;
    }
    
    public void setEffectEnabled(boolean effectEnabled) {
        if ( this.effectEnabled != effectEnabled ) {
            this.effectEnabled = effectEnabled;
            
            rebuildNode();
        }
    }

    @Override
    public Comparator getSortComparator(int columnIndex, boolean ascending) {
        return null;
    }

    
    public boolean isEditEnabled() {
        return editEnabled;
    }
    
    public void setEditEnabled(boolean editEnabled) {
        this.editEnabled = editEnabled;
    }
    
    
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
    
    public static class EditCharacterAction extends AbstractAction {
        
        private Target target;
        
        public EditCharacterAction() {
            super("Edit Character...");
        }
        public void actionPerformed(ActionEvent e) {
            //  if ( target != null ) target.editTarget();
            if ( target != null ) target.editTarget();
        }
        
        public Target getTarget() {
            return target;
        }
        
        public void setTarget(Target target) {
            this.target = target;
        }
    }
    
    public static class HipshotAction extends AbstractAction {
        
        private Target target;
        BattleEvent battleEvent = null;
        
        public HipshotAction() {
            super("Hipshot");
        }
        
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
        
        
        
        
        
        
        
        public Target getTarget() {
            return target;
        }
        
        public void setTarget(Target target) {
            this.target = target;
        }
    }
    
    
    
    public static class DebugCharacterAction extends AbstractAction {
        
        private Target target;
        
        public DebugCharacterAction() {
            super("Debug Character...");
        }
        public void actionPerformed(ActionEvent e) {
            if ( target != null ) target.debugDetailList( "Debug" + target.getName() );
        }
        
        public Target getTarget() {
            return target;
        }
        
        public void setTarget(Target target) {
            this.target = target;
        }
    }
    
    
    public static class SaveCharacterAction extends AbstractAction {
        
        private Target target;
        
        public SaveCharacterAction() {
            super("Save Character");
        }
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
        
        public boolean isEnabled() {
            return target != null && target.getFile() != null;
        }
        
        public Target getTarget() {
            return target;
        }
        
        public void setTarget(Target target) {
            this.target = target;
        }
    }
    
    public static class SaveAsCharacterAction extends AbstractAction {
        
        private Target target;
        
        public SaveAsCharacterAction() {
            super("Save Character As...");
        }
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
        
        public Target getTarget() {
            return target;
        }
        
        public void setTarget(Target target) {
            this.target = target;
        }
    }
    
    public static class RemoveCharacterAction extends AbstractAction {
        
        private Target target;
        private Roster roster;
        
        public RemoveCharacterAction() {
            super("Remove Character");
        }
        public void actionPerformed(ActionEvent e) {
            if ( target != null && getRoster() != null ) {
                getRoster().remove(target);
            }
        }
        
        public boolean isEnabled() {
            return target != null ;
        }
        
        public Target getTarget() {
            return target;
        }
        
        public void setTarget(Target target) {
            this.target = target;
        }
        
        public Roster getRoster() {
            return roster;
        }
        
        public void setRoster(Roster roster) {
            this.roster = roster;
        }
    }
    
    public static class AbortAction extends AbstractAction {
        
        private Target target;
        
        public AbortAction() {
            super("Abort Next Action");
        }
        public void actionPerformed(ActionEvent e) {
            if ( target != null && target.isAbortable() ) {
                BattleEvent be = new BattleEvent(BattleEvent.ACTIVE_TARGET, target);
                be.addCombatStateEvent(target, target.getCombatState(), CombatState.STATE_ABORTING );
                target.setCombatState( CombatState.STATE_ABORTING );
                Battle.currentBattle.addEvent(be);
            }
        }
        
        public Target getTarget() {
            return target;
        }
        
        public void setTarget(Target target) {
            this.target = target;
        }
    }
    
    
    public static class AddEffectAction extends AbstractAction {
        
        private Target target;
        
        public AddEffectAction() {
            super("Add Generic Effect...");
        }
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
        
        public Target getTarget() {
            return target;
        }
        
        public void setTarget(Target target) {
            this.target = target;
        }
    }
    
    public static class ShowSenseAction extends AbstractAction {
        
        private Target target;
        
        public ShowSenseAction() {
            super("Show Senses...");
        }
        public void actionPerformed(ActionEvent e) {
            if ( target != null ) {
                target.displaySenseWindow();
            }
        }
        
        public Target getTarget() {
            return target;
        }
        
        public void setTarget(Target target) {
            this.target = target;
        }
    }
    
    
    
    
    
}
