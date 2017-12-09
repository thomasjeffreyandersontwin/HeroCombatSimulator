/*
 * EscapeNode.java
 *
 * Created on June 5, 2002, 3:27 PM
 */

package champions.attackTree;

import champions.Effect;
import champions.Target;
import champions.powers.effectGrabbed;
import java.util.Vector;
import javax.swing.UIManager;

/**
 *
 * @author  Trevor Walker
 */
public class EscapeSetupNode extends DefaultAttackTreeNode {
    
    private Object[] descriptionList = null;
/** Creates new EscapeNode */ 
    public EscapeSetupNode(String name) {
        this.name = name;
        icon = UIManager.getIcon("AttackTree.toHitIcon");
    }
    
    /**
     * Activates the node.
     *
     * The method is called when this node is being activated.  Based on
     * the information the node current has available, it can choose to
     * accept the activation or reject it.
     *
     * If the node rejects the activation, the model will call the appropriate
     * methods to advance out of the node.
     *
     * The manualOverride boolean indicates that the user click on this node
     * specifically.  Even if this node has all the information to continue processing
     * without user input, it should accept activation so the user can make changes.
     *
     * This method should be overriden by children.
     */
    public boolean activateNode(boolean manualOverride) {
        boolean acceptActivation = false;
        
        if ( descriptionList == null ) {
            descriptionList = buildGrabList();
        }
        
        if ( manualOverride || nodeRequiresInput() ) {
                effectGrabbed eg = (effectGrabbed)battleEvent.getValue("Escape.EFFECTGRABBED");
                Object[] options = buildGrabList();
            
                EscapeSetupPanel ep = EscapeSetupPanel.getDefaultPanel(battleEvent, descriptionList, getDescription(eg) );
                attackTreePanel.showInputPanel(this,ep);
                attackTreePanel.setInstructions("Select Grab to Escape from...");
            
                acceptActivation = true;
        }
      //  }
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        return acceptActivation;
    }
    
    /** 
     * Causes node to process an advance and perform any work that it needs to do.
     *
     * This is a method introduced by DefaultAttackTreeNode.  DefaultAttackTreeNode
     * delegates to this method if advanceNode node is called and this is the active
     * node.  This method should do all the work of advanceNode whenever it can, since
     * the next node processing and buildNextChild methods depend on the existing 
     * DefaultAttackTreeNode advanceNode method.
     *
     * Returns true if it is okay to leave the node, false if the node should
     * be reactivated to gather more information.
     */
    public boolean processAdvance() {
        boolean advance = true;
        
        effectGrabbed eg = (effectGrabbed)battleEvent.getValue("Escape.EFFECTGRABBED");
        // If Escape.GRABDESCRIPTION is non-null, change grabbed Effect accordingly.
        String description = battleEvent.getStringValue("Escape.GRABDESCRIPTION");
        if ( description != null ) {
            eg = getEffectFromDescription(description);
            battleEvent.add("Escape.EFFECTGRABBED", eg, true);
        }
        else {
            eg = (effectGrabbed)battleEvent.getValue("Escape.EFFECTGRABBED");
        }
        
        if ( eg == null && descriptionList.length == 1) {
            eg = getEffectFromDescription( (String) descriptionList[0]);
            battleEvent.add("Escape.EFFECTGRABBED", eg, true);
        }
        
        if ( eg == null ) {
            advance = false;
        }
        
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println( "Node " + name + " exited.");
        return advance;
    }

    
    /** Determines if this node needs to show an input panel.
     *
     */
    protected  boolean nodeRequiresInput() {
        effectGrabbed eg = (effectGrabbed)battleEvent.getValue("Escape.EFFECTGRABBED");
        return (descriptionList.length != 1 && eg == null);
    }
    
    private Object[] buildGrabList() {
        Vector v = new Vector();
        Target source = battleEvent.getSource();
        int count = source.getEffectCount();
        for(int index=0;index<count;index++) {
            Effect e = source.getEffect(index);
            if ( e instanceof effectGrabbed ) {
                v.add( getDescription((effectGrabbed)e) );
            }
        }
        return v.toArray();
    }
    
    private effectGrabbed getEffectFromDescription(String description) {
        if ( description != null ) {
            Target source = battleEvent.getSource();
            int count = source.getEffectCount();
            for(int index=0;index<count;index++) {
                Effect e = source.getEffect(index);
                if ( e instanceof effectGrabbed && description.equals(getDescription((effectGrabbed)e)) ) {
                    return (effectGrabbed)e;
                }
            }
        }
        
        return null;
    }
    
    private String getDescription(effectGrabbed effect) {
        if ( effect == null ) {
            return "None Selected";
        }
        return effect.getGrabber() + "'s grab with " + effect.getGrabbingEffect().getAbility().toString();
    }
}
