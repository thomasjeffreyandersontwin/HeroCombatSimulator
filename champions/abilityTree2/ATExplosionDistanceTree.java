/*
 * ATSingleTargetTree.java
 *
 * Created on June 8, 2004, 10:34 PM
 */

package champions.abilityTree2;

import champions.ActivationInfo;

/**
 *
 * @author  1425
 */
public class ATExplosionDistanceTree extends ATTree {

    
    /**
     * Creates a new instance of ATSingleTargetTree
     */
    public ATExplosionDistanceTree() {
    }
    
    protected void setupModel() {
        ATNode root = new ATExplosionDistanceRoot(null, null, null);
        ATModel model = new ATExplosionDistanceModel(root, "Explosion DistanceFromCollision");
        setTreeTableModel(model);
        root.setTree(this);
    }
    
    public void setActivationInfo(ActivationInfo ai, String targetGroup) {
        ATExplosionDistanceModel m = (ATExplosionDistanceModel)getBaseTreeTableModel();
        ATExplosionDistanceRoot r = (ATExplosionDistanceRoot)m.getRoot();
        
        r.setActivationInfo(ai);
        r.setTargetGroup(targetGroup);
        
        r.buildNode();
    }

    
}
