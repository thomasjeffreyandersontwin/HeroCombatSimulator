/*
 * MoveByEffectNode.java
 *
 * Created on January 21, 2002, 10:58 AM
 */

package champions.attackTree;



import champions.ActivationInfo;
import champions.ChampionsUtilities;
import champions.Dice;
import champions.PADDiceValueEditor;
import champions.Preferences;
import champions.Target;
import champions.battleMessage.DiceRollMessage;
import champions.exception.BadDiceException;
import champions.exceptionWizard.ExceptionWizard;
import champions.interfaces.IndexIterator;
import javax.swing.UIManager;

/**
 *
 * @author  twalker
 * @version 
 */
public class MoveByEffectNode extends DefaultAttackTreeNode {

    public static DefaultAttackTreeNode Node;
	/** Holds value of property killingMoveBy. */
    private boolean killingMoveBy;
    
    /** Creates new EffectNode */
    public MoveByEffectNode(String name) {
        this.name = name;
        icon = UIManager.getIcon("AttackTree.diceIcon");
        // setVisible(false);
        Node =this;
    }
    
    public boolean activateNode(boolean manualOverride) {
        boolean acceptActivation = false;
        
        prepareBattleEvent();
        
        if ( manualOverride || nodeRequiresInput() ) {
            if ( killingMoveBy == true ) {
                String moveByTargetGroup = getTargetGroup() + ".MoveBy";
                EffectPanel ep = EffectPanel.getDefaultPanel(getBattleEvent(), moveByTargetGroup);
                attackTreePanel.showInputPanel(this,ep);
                attackTreePanel.setInstructions("Enter the Damage Roll for the MoveThough damage to " + battleEvent.getSource().getName() + "...");
            }
            else {
                String explination = buildExplination();
                InformationPanel ip = InformationPanel.getDefaultPanel( explination );
                attackTreePanel.showInputPanel(this,ip);
                attackTreePanel.setInstructions("Hit Okay to Continue...");
            }
            acceptActivation = true;
        }
                
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        return acceptActivation;
    }
    
    private boolean nodeRequiresInput() {
        // Run through the dice and make sure the all have values;
        boolean requiresInput = false;
        
        if ( killingMoveBy == true ) {
            // It is a killing move through, so you will need to make sure the dice are
            // realized!  Otherwise the node should never require input!
            Dice d;
            
            String moveByTargetGroup = getTargetGroup() + ".MoveBy";
            
            IndexIterator ii = getBattleEvent().getDiceIterator(moveByTargetGroup);
            int dindex;
            while ( ii.hasNext() ) {
                dindex = ii.nextIndex();
                d = getBattleEvent().getDiceRoll(dindex);
                if ( d == null || d.isRealized() == false ) {
                    requiresInput = true;
                    break;
                }
            }
        }
        
        // Before the override can be used, the Dice must be properly setup in processAdvance
        return requiresInput && getBattleEvent().getSource().getBooleanProfileOption("SHOW_EFFECT_ROLL_PANEL");
    }
    
    /**
     * Builds the next child based upon the last child which was constructed.
     *
     * If all the children necessary have been built, return null, otherwise
     * return the next node.
     *
     * BuildNextChild should add that child to the tree.
     */
    public AttackTreeNode buildNextChild(AttackTreeNode activeChild) {
        // This node build everything upfront, when it's target is first set.
        AttackTreeNode nextNode = null;
        // Compare the activeChild to the possible children, to determine what comes next.
        if ( activeChild != null && activeChild.getName() == null ) {
            // This is probably an error
            int index = children.indexOf(activeChild);
            System.out.println("Null Node name for child " + Integer.toString(index) + " of Parent " + this);
            nextNode = null;
        }
        else {
            String previousNodeName = (activeChild == null ) ? null : activeChild.getName();
            String nextNodeName = nextNodeName(previousNodeName);
            if ( nextNodeName != null ) {
                if ( nextNodeName.equals("Move-By Damage") ) {
                    // Build the very first node: Attack Param
                    MoveByDamageNode node = new MoveByDamageNode("Move-By Damage");
                    node.setTargetGroupSuffix("MoveBy");
                    nextNode = node;
                }
            }
        }
        return nextNode;
    }
    
    public String nextNodeName(String previousNodeName) {
        String nextNodeName = null;
        
        if ( previousNodeName == null ) {
            nextNodeName = "Move-By Damage";
        }
        
        return nextNodeName;
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
        checkDice();
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " exited.");
        return true;
    }
    
  /*  public void updateChildren() {
        // This method must make sure that all the Targets hit in the TargetGroup
        // have an associated TargetEffect node.
   
        boolean fireChange = false;
        boolean found;
        // Make sure the abilities in the source match the abilityNodes that are childern
        Vector newChildren = new Vector();
        int nindex, ncount;
        int total = 0;
        ncount = ( children != null ) ? children.size() : 0;
        TargetEffectNode ten;
        int dindex, refNumber;
        Target target;
        boolean hit;
   
        ActivationInfo ai = getBattleEvent().getActivationInfo();
   
        IndexIterator ii = ai.getTargetGroupIterator(getTargetGroup());
   
        while ( ii.hasNext() ) {
            dindex = ii.nextIndex();
            target = ai.getTarget(dindex);
            refNumber = ai.getTargetReferenceNumber(dindex);
            hit = ai.getTargetHit(dindex);
   
            if ( target != null && hit) {
                // Try to find it in the children array.
                found = false;
                for(nindex=0;nindex<ncount;nindex++) {
                    ten = (TargetEffectNode) children.get(nindex);
                    if (ten != null && ten.getTarget() == target && ten.getTargetReferenceNumber() == refNumber ) {
                        found = true;
                        // Move the ability node from the childern list to the newChildern list
                        newChildren.add(ten);
                        if ( nindex != total ) fireChange = true;
                        children.set(nindex, null);
                        break;
                    }
                }
   
                if ( found == false ) {
                    ten = new TargetEffectNode("Target Effect");
                    ten.setParent(this);
                    ten.setModel(getModel());
                    ten.setBattleEvent(getBattleEvent());
                    ten.setInlinePanel(getInlinePanel());
                    ten.setTarget(target);
                    ten.setTargetReferenceNumber(refNumber);
   
                    newChildren.add(ten);
                    fireChange = true;
                }
                total++;
            }
        }
   
        Vector oldChildren = children;
        children = newChildren;
   
        // Now that everything is done, anything level not-null in oldChildren should be destroyed
        // and references to it released.
        if ( oldChildren != null ) {
            for(nindex=0;nindex<oldChildren.size();nindex++) {
                if ( oldChildren.get(nindex) != null ) {
                    ((AttackTreeNode)oldChildren.get(nindex)).destroy();
                    oldChildren.set(nindex,null);
                    fireChange = true;
                }
            }
        }
   
        if ( fireChange && model != null ) model.nodeStructureChanged(this);
   
   
    }
   
    public void nodeChanged(AttackTreeNode changedNode) {
        if ( changedNode.getName().equals("ToHit") ) {
            updateChildren();
        }
    } */
    
    public String getAutoBypassOption() {
        return "SHOW_EFFECT_ROLL_PANEL";
    }
    
    public Target getAutoBypassTarget() {
        return getBattleEvent().getSource();
    }
   
    
    /** Getter for property killingMoveBy.
     * @return Value of property killingMoveBy.
     */
    public boolean isKillingMoveBy() {
        return killingMoveBy;
    }
    
    /** Setter for property killingMoveBy.
     * @param killingMoveBy New value of property killingMoveBy.
     */
    public void setKillingMoveBy(boolean killingMoveBy) {
        this.killingMoveBy = killingMoveBy;
    }
    
    private void prepareBattleEvent() {
        // Determine what kind of MoveBy damage the Source receives
        ActivationInfo ai = battleEvent.getActivationInfo();
        killingMoveBy = battleEvent.isKillingAttack();
        
        // Create the target Group appropriately
        String moveByTargetGroup = getTargetGroup() + ".MoveBy";
        int tgindex = ai.addTargetGroup( moveByTargetGroup );
        
        // Set the Primary Target
        ai.addTarget(battleEvent.getSource(), moveByTargetGroup, 0);
        
        // Grab the Appropriate Dice Information from BattleEvent
        int dindex = battleEvent.getDiceIndex("DamageDie", getTargetGroup());
        String attackDiceSize = battleEvent.getDiceSize(dindex);
        
        // Determine the Original Damage Classes
        double damageClass;
        
        if ( killingMoveBy == true ) {
            damageClass = ChampionsUtilities.StringToKillingDC(attackDiceSize);
        }
        else {
            damageClass = ChampionsUtilities.StringToNormalDC(attackDiceSize);
        }
        
        damageClass = Math.round(damageClass / 3);
        
        // Calculate the size of the new DamageDie
        String newDiceSize = ChampionsUtilities.DCToNormalString(damageClass);
        
        // Add a dice to the MoveBy targetgroup
        int newdindex = battleEvent.addDiceInfo( "DamageDie", moveByTargetGroup, "Move-Through Damage to " + battleEvent.getSource().getName(), newDiceSize);
        
        // If the Attack is killing, the die will need to be rolled.  However, if the attack is normal, take the dice from
        // the existing attack and use it appropriately.
        if ( killingMoveBy == false ) {
            Dice attackDice = battleEvent.getDiceRoll(dindex);
            try {
                Dice newDice = new Dice(newDiceSize, false);
                
                    newDice.setStun( attackDice.getStun().intValue() / 3 );
                    newDice.setBody( attackDice.getBody().intValue() / 3 );
                
                // Set the dice in the BattleEvent
                battleEvent.setDiceRoll( newdindex, newDice );
            }
            catch ( BadDiceException bde ) {
                ExceptionWizard.postException( bde );
            }
        }
    }
    
    private Dice getDamageDiceRoll() {
        String moveByTargetGroup = getTargetGroup() + ".MoveBy";
        int dindex = battleEvent.getDiceIndex("DamageDie", moveByTargetGroup);
        Dice d  = battleEvent.getDiceRoll(dindex);
        
        return d;
    }
    
    private String buildExplination() {
        String explination;
        String source = battleEvent.getSource().getName();
        
        explination = source + " performed a MoveBy, causing damage to theirselves.\n\n";
        
        explination = explination +  source + " will receive ONE THIRD of the damage done.\n\n";
        
        explination = explination + "The Attack Damage Die roll will be used to calculate the damage. " + 
        "The final die roll used to generate damage is " + getDamageDiceRoll().toString() + ".";
        
        return explination;
    }
    
    private void checkDice() {
        if ( battleEvent != null ) {
            String moveByTargetGroup = getTargetGroup() + ".MoveBy";
            IndexIterator ii = battleEvent.getDiceIterator(moveByTargetGroup);
            
            int vindex = 0;
            int dindex;
            
            Dice dice;
            boolean auto;
            
            PADDiceValueEditor editor;
            
            while ( ii.hasNext() ) {
                dindex = ii.nextIndex();
                
                dice = battleEvent.getDiceRoll(dindex);
                
                try {
                    if ( dice == null ) {
                        String size = battleEvent.getIndexedStringValue(dindex, "Die", "SIZE");
                        
                        dice = new Dice( size, true);
                        
                        battleEvent.setDiceRoll(dindex, dice);
                        battleEvent.setDiceAutoRoll(dindex, true);
                    }
                    
                    
                    if ( Preferences.getBooleanValue("DieRollMessages") ) {
                        String msg = battleEvent.getSource().getName() + " rolled " + dice.toString() + " for " + battleEvent.getDiceDescription(dindex)+ ".";
                        battleEvent.addBattleMessage( new DiceRollMessage(battleEvent.getSource(), msg));
                    }
                }
                catch ( BadDiceException bde) {
                    
                }
                
                vindex ++;
            }
        }
    }
}
