/*
 * MoveThroughEffectNode.java
 *
 * Created on January 20, 2002, 1:02 PM
 */

package champions.attackTree;

import champions.*;
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
public class MoveThroughEffectNode extends DefaultAttackTreeNode {
    
    /** Holds value of property targetsKnockedBack. */
    private boolean targetsKnockedBack;
    
    /** Holds value of property killingMoveThrough. */
    private boolean killingMoveThrough;

	public static MoveThroughEffectNode Node;
    
    /** Creates new EffectNode */
    public MoveThroughEffectNode(String name) {
        this.name = name;
        icon = UIManager.getIcon("AttackTree.diceIcon");
        // setVisible(false);
        Node = this;
    }
    
    public boolean activateNode(boolean manualOverride) {
        boolean acceptActivation = false;
        
        prepareBattleEvent();
        
        if ( manualOverride || nodeRequiresInput() ) {
            if ( killingMoveThrough == true ) {
                String moveThroughTargetGroup = getTargetGroup() + ".MoveThrough";
                EffectPanel ep = EffectPanel.getDefaultPanel(getBattleEvent(), moveThroughTargetGroup);
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
        
        if ( killingMoveThrough == true ) {
            // It is a killing move through, so you will need to make sure the dice are
            // realized!  Otherwise the node should never require input!
            Dice d;
            
            String moveThroughTargetGroup = getTargetGroup() + ".MoveThrough";
            
            IndexIterator ii = getBattleEvent().getDiceIterator(moveThroughTargetGroup);
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
                if ( nextNodeName.equals("Move Through Damage") ) {
                    // Build the very first node: Attack Param
                    ApplyMoveThroughDamageNode node = new ApplyMoveThroughDamageNode("Move Through Damage");
                    node.setTargetGroupSuffix("MoveThrough");
                    nextNode = node;
                }
            }
        }
        return nextNode;
    }
    
        public String nextNodeName(String previousNodeName) {
        String nextNodeName = null;
        
        
        if ( previousNodeName == null ) {
            nextNodeName = "Move Through Damage";
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
    
    /** Getter for property targetsKnockedBack.
     * @return Value of property targetsKnockedBack.
     */
    public boolean getTargetsKnockedBack() {
        return targetsKnockedBack;
    }
    
    /** Setter for property targetsKnockedBack.
     * @param targetsKnockedBack New value of property targetsKnockedBack.
     */
    public void setTargetsKnockedBack(boolean targetsKnockedBack) {
        this.targetsKnockedBack = targetsKnockedBack;
    }
    
    /** Getter for property killingMoveThrough.
     * @return Value of property killingMoveThrough.
     */
    public boolean isKillingMoveThrough() {
        return killingMoveThrough;
    }
    
    /** Setter for property killingMoveThrough.
     * @param killingMoveThrough New value of property killingMoveThrough.
     */
    public void setKillingMoveThrough(boolean killingMoveThrough) {
        this.killingMoveThrough = killingMoveThrough;
    }
    
    private void prepareBattleEvent() {
        // Determine what kind of MoveThrough damage the Source receives
        ActivationInfo ai = battleEvent.getActivationInfo();
        int tgindex = ai.getTargetGroupIndex(getTargetGroup());
        String kbGroup = ai.getKnockbackGroup(tgindex);
        
        targetsKnockedBack = BattleEngine.getAllTargetsKnockedBack(battleEvent, kbGroup);
        killingMoveThrough = battleEvent.isKillingAttack();
        
        // Create the target Group appropriately
        String moveThroughTargetGroup = getTargetGroup() + ".MoveThrough";
        tgindex = ai.addTargetGroup( moveThroughTargetGroup );
        
        // Set the Primary Target
        ai.addTarget(battleEvent.getSource(), moveThroughTargetGroup, 0);
        
        // Grab the Appropriate Dice Information from BattleEvent
        int dindex = battleEvent.getDiceIndex("DamageDie", getTargetGroup());
        String attackDiceSize = battleEvent.getDiceSize(dindex);
        
        // Determine the Original Damage Classes
        double damageClass;
        
        if ( killingMoveThrough == true ) {
            damageClass = ChampionsUtilities.StringToKillingDC(attackDiceSize);
        }
        else {
            damageClass = ChampionsUtilities.StringToNormalDC(attackDiceSize);
        }
        
        // Determine the Actual Damage Classes Caused by this Attack
        // 1/2 if knocked back, full otherwise
        if ( targetsKnockedBack == true ) {
            damageClass = Math.round(damageClass / 2);
        }
        
        // Calculate the size of the new DamageDie
        String newDiceSize = ChampionsUtilities.DCToNormalString(damageClass);
        
        // Add a dice to the MoveThrough targetgroup
        int newdindex = battleEvent.addDiceInfo( "DamageDie", moveThroughTargetGroup, "Move-Through Damage to " + battleEvent.getSource().getName(), newDiceSize);
        
        // If the Attack is killing, the die will need to be rolled.  However, if the attack is normal, take the dice from
        // the existing attack and use it appropriately.
        if ( killingMoveThrough == false ) {
            Dice attackDice = battleEvent.getDiceRoll(dindex);
            try {
                Dice newDice = new Dice(newDiceSize, false);
                
                if ( targetsKnockedBack ) {
                    newDice.setStun( attackDice.getStun().intValue() / 2 );
                    newDice.setBody( attackDice.getBody().intValue() / 2 );
                }
                else {
                    newDice.setStun( attackDice.getStun().intValue() );
                    newDice.setBody( attackDice.getBody().intValue() );
                }
                
                // Set the dice in the BattleEvent
                battleEvent.setDiceRoll( newdindex, newDice );
            }
            catch ( BadDiceException bde ) {
                ExceptionWizard.postException( bde );
            }
        }
    }
    
    private Dice getDamageDiceRoll() {
        String moveThroughTargetGroup = getTargetGroup() + ".MoveThrough";
        int dindex = battleEvent.getDiceIndex("DamageDie", moveThroughTargetGroup);
        Dice d  = battleEvent.getDiceRoll(dindex);
        
        return d;
    }
    
    private String buildExplination() {
        String explination;
        String source = battleEvent.getSource().getName();
        
        explination = source + " performed a MoveThrough, causing damage to theirselves.\n\n";
        
        if ( targetsKnockedBack ) {
            explination = explination +  "The target of MoveThrough was knocked back or knocked down, so " + source + 
            " will receive HALF of the damage done.\n\n";
        }
        else {
            explination = explination +  "The target of MoveThrough was NOT knocked back or knocked down, so " + source + 
            " will receive ALL of the damage done.\n\n";
        }
        
        explination = explination + "The Attack Damage Die roll will be used to generate the damage. " + 
        "The final die roll used to generate damage is " + getDamageDiceRoll().toString() + ".";
        
        return explination;
    }
    
    private void checkDice() {
        if ( battleEvent != null ) {
            String moveThroughTargetGroup = getTargetGroup() + ".MoveThrough";
            IndexIterator ii = battleEvent.getDiceIterator(moveThroughTargetGroup);
            
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
