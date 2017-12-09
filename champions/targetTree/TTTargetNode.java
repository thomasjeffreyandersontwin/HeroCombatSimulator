/*
 * PADTargetNode.java
 *
 * Created on February 27, 2002, 11:43 PM
 */

package champions.targetTree;

import champions.Battle;
import champions.Effect;
import champions.EffectPanelTreeTableEditor;
import champions.EffectPanelTreeTableRenderer;
import champions.Target;
import champions.TargetList;
import champions.enums.DefenseType;
import champions.powers.effectDead;
import champions.powers.effectDying;
import champions.powers.effectKnockedDown;
import champions.powers.effectStunned;
import champions.powers.effectUnconscious;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;
import treeTable.DefaultTreeTableModel;
import treeTable.TreeTable;
import treeTable.TreeTableCellEditor;
import treeTable.TreeTableCellRenderer;
import treeTable.TreeTableModel;


/**
 *
 * @author  twalker
 * @version
 *
 * PADTargetNode's hold references to Abilities stored in an Target list.
 * It should not be used for powers/templates which are cached by the PADRoster
 * shared instance mechanism.
 */
public class TTTargetNode extends TTNode implements PropertyChangeListener {
    
    private TargetList targetList;
    
    /** Holds value of property target. */
    private Target target;
    
    private List<Effect> effects;
    
    /** Indicates the delete option should be shown.
     */
    private boolean deleteEnabled = false;
    
    private static RemoveTargetAction deleteAction = null;
    private static DebugAction debugAction = null;
    private static EditAction editAction = null;
    private static SaveAction saveAction = null;
    
    /** Indicates that this is a preset target and should be instantiated to create a real target.
     *
     */
    private boolean preset = false;
    
    /** Creates new PADTargetNode */
    public TTTargetNode(TreeTableModel model, Target target, TargetList targetList) {
        setModel(model);
        setTarget(target);
        setTargetList(targetList);
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
    
    /**
     * Returns the value to be displayed for node <code>node</code>,
     * at column number <code>column</code>.
     */
    public Object getValueAt(int column) {
        Object result = null;
        
        if ( target != null ) {
            switch(column) {
                case TTModel.COLUMN_NAME:
                    if ( model instanceof TTSelectTargetModel && ((TTSelectTargetModel)model).getSource() == target )
                        result = "" + target.getName() + " (Self)";
                    else
                        result = target.getName();
                    break;
                case TTModel.COLUMN_BODY:
                    if ( target.hasStat("BODY") ) result = target.getCurrentStat("BODY");
                    break;
                case TTModel.COLUMN_STUN:
                    if ( target.hasStat("STUN") ) result = target.getCurrentStat("STUN");
                    break;
                case TTModel.COLUMN_PD:
                {
                    int pd = target.getDefense(DefenseType.PD);
                    int rpd = target.getDefense(DefenseType.rPD);
                    result = "" + pd + "/" + rpd;
                }
                break;
                case TTModel.COLUMN_ED:
                {
                    int pd = target.getDefense(DefenseType.ED);
                    int rpd = target.getDefense(DefenseType.rED);
                    result = "" + pd + "/" + rpd;
                }
                break;
                case TTModel.COLUMN_END:
                    if ( target.hasStat("END") ) result = target.getCurrentStat("END");
                    break;
                case TTModel.COLUMN_EFFECTS:
                    //result = getEffectList();
                    result = effects;
                    break;
                case TTModel.COLUMN_OCV:
                    result = Integer.toString( target.getCalculatedOCV() );
                    break;
                case TTModel.COLUMN_DCV:
                    result = Integer.toString( target.getCalculatedDCV() );
                    break;
                case TTModel.COLUMN_ECV:
                    result = Integer.toString( target.getCalculatedECV() );
                    break;
            }
        }
        return result;
    }
    
//    protected String getEffectList() {
//        StringBuffer sb = new StringBuffer();
//        boolean first = true;
//        sb.append("<html>");
//        
//        int count = target.getEffectCount();
//        for(int eindex = 0; eindex < count; eindex++) {
//            Effect effect = target.getEffect(eindex);
//            if ( effect.isCritical()) {
//                Color color = effect.getEffectColor();
//                
//                if ( ! first ) {
//                    sb.append(", ");
//                }
//                first = false;
//                
//                if ( color != null ) {
//                    sb.append("<Font Color=#");
//                    sb.append(toHex(color.getRed()));
//                    sb.append(toHex(color.getGreen()));
//                    sb.append(toHex(color.getBlue()));
//                    sb.append(">");
//                }
//                
//                sb.append(effect.getName());
//                
//                if ( color != null ) {
//                    sb.append("</Font>");
//                }
//                
//            }
//        }
//        
//        for(int eindex = 0; eindex < count; eindex++) {
//            Effect effect = target.getEffect(eindex);
//            if ( effect.isCritical() == false) {
//                Color color = effect.getEffectColor();
//                
//                if ( ! first ) {
//                    sb.append(", ");
//                }
//                first = false;
//                
//                if ( color != null ) {
//                    sb.append("<Font Color=#");
//                    sb.append(toHex(color.getRed()));
//                    sb.append(toHex(color.getGreen()));
//                    sb.append(toHex(color.getBlue()));
//                    sb.append(">");
//                }
//                
//                sb.append(effect.getName());
//                
//                if ( color != null ) {
//                    sb.append("</Font>");
//                }
//                
//            }
//        }
//        
//        sb.append("</html>");
//        
//        return sb.toString();
//    }
//    
//    protected String toHex(int i) {
//        
//        if ( i < 16 ) {
//            return "0" + Integer.toString(i, 16);
//        } else {
//            return Integer.toString(i, 16);
//        }
//    }
    
        protected void updateEffects() {
        if ( target != null  ) {
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
            
        }
        else {
            setEffectList(null);
        }
    }
    
    protected void setEffectList(List<Effect> e) {
        if ( effects != e && (effects == null || effects.equals(e) == false )) {
            effects = e;
            triggerUpdate();
        }
    }
    
    protected void triggerUpdate() {
        if ( getModel() instanceof DefaultTreeTableModel ) {
            ((DefaultTreeTableModel)getModel()).nodeChanged(this);
        }
    }
    
        public TreeTableCellRenderer getTreeTableCellRenderer(int columnIndex) {
        if ( columnIndex == TTModel.COLUMN_EFFECTS) {
            return EffectPanelTreeTableRenderer.getDefaultRenderer();
        }
        
        return null;
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
            
            if ( target != null ) {
                if ( editAction == null ) editAction = new EditAction();
                
                editAction.setTarget(target);
                popup.add(editAction);
                rv = true;
                
                if ( saveAction == null ) saveAction = new SaveAction();
                
                saveAction.setTarget(target);
                popup.add(saveAction);
                rv = true;
            }
            
            if ( deleteEnabled ) {
                if ( deleteAction == null ) deleteAction = new RemoveTargetAction();
                
                if ( target != null && getTargetList() != null ) {
                    deleteAction.setTarget(target);
                    deleteAction.setTargetList(targetList);
                    popup.add(deleteAction);
                    rv = true;
                }
            }
            
            if ( Battle.debugLevel >= 0 ) {
                if ( rv ) popup.addSeparator();
                
                if ( debugAction == null ) debugAction = new DebugAction();
                debugAction.setTarget(target);
                popup.add(debugAction);
                rv = true;
            }
        }
        return rv;
    }
    
    /** Setter for property target.
     * @param target New value of property target.
     *
     */
    public void setTarget(Target target) {
        if ( this.target != target ) {
            if ( this.target != null ) {
                this.target.removePropertyChangeListener("Target.NAME", this);
            }
            
            this.target = target;
            
            updateName();
            updateEffects();
            
            if ( this.target != null ) {
                this.target.addPropertyChangeListener("Target.NAME", this);
            }
        }
        
    }
    
    private void updateName() {
        if ( target != null ) {
            setUserObject(target.getName());
            if ( model instanceof DefaultTreeTableModel ) {
                ((DefaultTreeTableModel)model).nodeChanged(this);
            }
        }
        //Icon icon = p.getIcon();
        // setIcon(icon);
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        updateName();
    }
    
    /** Getter for property deleteEnabled.
     * @return Value of property deleteEnabled.
     *
     */
    public boolean isDeleteEnabled() {
        return deleteEnabled;
    }
    
    /** Setter for property deleteEnabled.
     * @param deleteEnabled New value of property deleteEnabled.
     *
     */
    public void setDeleteEnabled(boolean deleteEnabled) {
        this.deleteEnabled = deleteEnabled;
    }
    
    /** Getter for property targetList.
     * @return Value of property targetList.
     *
     */
    public TargetList getTargetList() {
        return targetList;
    }
    
    /** Setter for property targetList.
     * @param targetList New value of property targetList.
     *
     */
    public void setTargetList(TargetList targetList) {
        this.targetList = targetList;
    }
    
        public boolean isPreset() {
        return preset;
    }

    public void setPreset(boolean preset) {
        this.preset = preset;
    }

    public void destroy() {
        super.destroy();
        
        setTarget(null);
        setTargetList(null);
    }
    
    private static class EditAction extends AbstractAction {
        private Target target;
        public EditAction() {
            super("Edit Target...");
        }
        
        public void actionPerformed(ActionEvent e) {
            if (target != null ) {
                target.editTarget();
            }
        }
        
        public void setTarget(Target target) {
            this.target = target;
        }
    }
    
    private static class DebugAction extends AbstractAction {
        private Target target;
        public DebugAction() {
            super("Debug Target...");
        }
        
        public void actionPerformed(ActionEvent e) {
            if (target != null ) {
                target.debugDetailList( "Target Debugger" );
            }
        }
        
        public void setTarget(Target target) {
            this.target = target;
        }
    }
    
    public static class RemoveTargetAction extends AbstractAction {
        private Target target;
        private TargetList targetList;
        public RemoveTargetAction() {
            super("Delete Target");
        }
        
        public void actionPerformed(ActionEvent e ) {
            
            if ( target != null && targetList != null ) {
                targetList.removeTarget(target);
            }
        }
        
        public void setTarget(Target target) {
            this.target = target;
        }
        
        /** Getter for property targetList.
         * @return Value of property targetList.
         *
         */
        public TargetList getTargetList() {
            return targetList;
        }
        
        /** Setter for property targetList.
         * @param targetList New value of property targetList.
         *
         */
        public void setTargetList(TargetList targetList) {
            this.targetList = targetList;
        }
        
    }
    
    public static class SaveAction extends AbstractAction {
        private Target target;
        public SaveAction() {
            super("Save As...");
        }
        
        public void actionPerformed(ActionEvent e ) {
            
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
        
        public void setTarget(Target target) {
            this.target = target;
        }
        
    }


    

}
