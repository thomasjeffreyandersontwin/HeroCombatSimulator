/*
 * ProcessEmbeddedEventNode.java
 *
 * Created on April 29, 2002, 8:28 PM
 */

package champions.attackTree;

import champions.BattleEngine;
import champions.BattleEvent;
import champions.exception.BattleEventException;


/**
 *
 * @author  Trevor Walker
 * @version 
 */
public class ProcessEmbeddedEventNode extends DefaultAttackTreeNode {

    /** Holds value of property embeddedEvent. */
    private BattleEvent embeddedEvent;
    
    /** Creates new ProcessActivateAbilityNode */
    public ProcessEmbeddedEventNode(String name, BattleEvent embeddedEvent) {
        this.name = name;
        setEmbeddedEvent(embeddedEvent);
        
      //  setVisible(false);
        buildChildren();
    }
    
    public boolean activateNode(boolean manualOverride) {
        boolean activateNode = manualOverride;
        
        String text = "This folder contains information relevant to a single activation or deactivation of " +
                "an ability.  Depending on the ability, this folder may be empty.";

        InformationPanel app = InformationPanel.getDefaultPanel( text );
        
        attackTreePanel.showInputPanel(this,app);
        attackTreePanel.setInstructions("Hit Okay to Continue...");
        
        return activateNode;
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
    public boolean processAdvance() throws BattleEventException {
        if ( embeddedEvent != null && embeddedEvent.isEmbedded() == false ) {
            battleEvent.embedBattleEvent(embeddedEvent);
        }
            
        
        return true;
    }
    
    public void checkNodes() {
        // Do nothing here...
    }
    
    private void buildChildren() {
        if ( embeddedEvent != null ) {
            AttackTreeNode embeddedNode = BattleEngine.getProcessAbilityRoot(embeddedEvent);
            embeddedNode.setVisible(false);
            addChild(embeddedNode, false);
        }
    }

    /** Getter for property embeddedEvent.
     * @return Value of property embeddedEvent.
     */
    public BattleEvent getEmbeddedEvent() {
        return embeddedEvent;
    }
    
    /** Setter for property embeddedEvent.
     * @param embeddedEvent New value of property embeddedEvent.
     */
    public void setEmbeddedEvent(BattleEvent embeddedEvent) {
        this.embeddedEvent = embeddedEvent;
    }
    
}
