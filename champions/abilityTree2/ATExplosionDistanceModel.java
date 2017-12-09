/*
 * ATConfigureBattleModel.java
 *
 * Created on November 21, 2007, 7:46 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package champions.abilityTree2;

import champions.ActivationInfo;
import champions.Target;
import champions.event.PADValueEvent;
import champions.interfaces.PADValueListener;
import champions.parameterEditor.IntegerParameterEditor;
import treeTable.TreeTableCellEditor;
import treeTable.TreeTableCellRenderer;

/**
 *
 * @author twalker
 */
public class ATExplosionDistanceModel extends ATModel {
    
    protected  static final int[] visibleColumns = {
        ATColumn.NAME_COLUMN.ordinal(),
       // ATColumn.EXPLOSION_SLIDER.ordinal(),
        ATColumn.EXPLOSION_DISTANCE.ordinal(),
    };
    
    /** Creates a new instance of ATConfigureBattleModel */
    public ATExplosionDistanceModel(ATNode root, String title) {
        super(root, title);
    }

    public int[] getVisibleColumns() {
        return visibleColumns;
    }

    public boolean isColumnAllowed(int modelIndex) {
        // Allow only the columns in visibleColumns...
        for(int i : visibleColumns) {
            if ( modelIndex == i ) return true;
        }
        
        return false;
    }


    public boolean isColumnRequired(int modelIndex) {
        // Require the name column to be shown...
        return modelIndex == ATColumn.NAME_COLUMN.ordinal();
    }
    
    protected static IntegerParameterEditor integerEditor;
    protected static IntegerParameterEditor integerRenderer = new IntegerParameterEditor(null, null);
    protected static DistanceListener distanceListener = new DistanceListener();

    @Override
    public TreeTableCellRenderer getCellRenderer(Object node, int column) {
        if ( node instanceof ATTargetNode && column == ATColumn.EXPLOSION_DISTANCE.ordinal() ) {
            integerRenderer.setCurrentValue(  getValueAt(node, column) );
            return integerRenderer;
        }
        
        return super.getCellRenderer(node, column);
    }
    
    @Override
    public TreeTableCellEditor getCellEditor(Object node, int column) {
        if ( node instanceof ATTargetNode && column == ATColumn.EXPLOSION_DISTANCE.ordinal() ) {
            
            ATExplosionDistanceRoot r = (ATExplosionDistanceRoot)getRoot();
            
            if ( integerEditor == null ) {
                integerEditor = new IntegerParameterEditor(null, null);
                integerEditor.addPADValueListener( distanceListener );
            }
            
            distanceListener.activationInfo = r.getActivationInfo();
            distanceListener.targetGroup = r.getTargetGroup();
            distanceListener.target = ((ATTargetNode)node).getTarget();
            
            integerEditor.setCurrentValue(  getValueAt(node, column) );
            return integerEditor;
        }
        
        return super.getCellEditor(node, column);
    }
    
    

    @Override
    public Object getValueAt(Object node, int column) {
        
        if ( node instanceof ATTargetNode && (column == ATColumn.EXPLOSION_DISTANCE.ordinal() || column == ATColumn.EXPLOSION_SLIDER.ordinal())) {
            Target t = ((ATTargetNode)node).getTarget();
            
            ActivationInfo ai = getActivationInfo();
            int tindex = ai.getTargetIndex( t, getTargetGroup());
            
            if ( tindex != -1 ) {
                return ai.getTargetDistanceFromExplosion(tindex);
            }
            
            return 0;
        }
        
        
        return super.getValueAt(node, column);
        
    }

    @Override
    public void setValueAt(Object node, int column, Object aValue) {
        if ( node instanceof ATTargetNode && (column == ATColumn.EXPLOSION_DISTANCE.ordinal() || column == ATColumn.EXPLOSION_SLIDER.ordinal())) {
            Target t = ((ATTargetNode)node).getTarget();
            
            ActivationInfo ai = getActivationInfo();
            int tindex = ai.getTargetIndex( t, getTargetGroup());
            
            if ( tindex != -1 ) {
                if ( aValue instanceof String) {
                    
                    try {
                        int newValue = Integer.parseInt((String) aValue);
                        ai.setTargetDistanceFromExplosion(tindex, newValue);
                    } catch (NumberFormatException numberFormatException) {
                    }
                }
                else if ( aValue instanceof Integer ) {
                    ai.setTargetDistanceFromExplosion(tindex, (Integer)aValue);
                }
            }
            
        }
        else {
            super.setValueAt(node, column,aValue);
        }
    }
    
    

    
    @Override
    public boolean isCellEditable(Object node, int column) {
        return column == ATColumn.EXPLOSION_DISTANCE.ordinal() || column == ATColumn.EXPLOSION_SLIDER.ordinal();
    }

    public ActivationInfo getActivationInfo() {
        if ( getRoot() instanceof ATExplosionDistanceRoot ) {
            return ((ATExplosionDistanceRoot)getRoot()).getActivationInfo();
        }
        return null;
    }
    
    public String getTargetGroup() {
        if ( getRoot() instanceof ATExplosionDistanceRoot ) {
            return ((ATExplosionDistanceRoot)getRoot()).getTargetGroup();
        }
        return null;
    }
    
    public static class  DistanceListener implements PADValueListener {
        
        protected ActivationInfo activationInfo;
        protected String targetGroup;
        protected Target target;

        public DistanceListener() {

        }

        public void PADValueChanged(PADValueEvent evt) {
            Object newValue = evt.getValue();
            
            if ( newValue instanceof Integer ) {
                int i = (Integer)newValue;
                
                if ( activationInfo != null && target != null && targetGroup != null ) {
                    int tindex = activationInfo.getTargetIndex(target, targetGroup);
                    activationInfo.setTargetDistanceFromExplosion(tindex, i);
                }
            }
        }

        public boolean PADValueChanging(PADValueEvent evt) {
            Object newValue = evt.getValue();
            
            if ( newValue instanceof Integer ) {
                return ( ((Integer)newValue) >= 0 );
            }
            
            return true;
        }
        
    }


}
