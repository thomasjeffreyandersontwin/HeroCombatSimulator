/*
 * KnockbackEffectNode.java
 *
 * Created on November 8, 2001, 12:03 PM
 */

package champions.attackTree;


import champions.Ability;
import champions.ActivationInfo;
import champions.Dice;
import champions.PADDiceValueEditor;
import champions.Target;
import champions.enums.KnockbackEffect;
import champions.exception.BadDiceException;
import champions.interfaces.IndexIterator;
import javax.swing.UIManager;

/**
 *
 * @author  twalker
 * @version
 */
public class KnockbackDamageRollNode extends DefaultAttackTreeNode {
    
    /** Holds value of property knockbackGroup. */
    private String knockbackGroup;
    
    /** Holds value of property targetReferenceNumber. */
    private int targetReferenceNumber;
    
    /** Holds value of property target. */
    private Target target;
    
    /** Holds value of property ability. */
    private Ability ability;
    
    
    private KnockbackObstructionNode secondaryTargetNode;
    
    /** Creates new KnockbackEffectNode
     * @param name
     */
    public KnockbackDamageRollNode(String name) {
        this.name = name;
        icon = UIManager.getIcon("AttackTree.diceIcon");
    }
    private void setupNode() {
        ActivationInfo ai = battleEvent.getActivationInfo();
        int tindex = ai.getTargetIndex(targetReferenceNumber, getTargetGroup() );
    }
    
    @Override
    public boolean activateNode(boolean manualOverride) {
        boolean acceptActivation = false;
        setupNode();
        prepareBattleEvent();

        if ( manualOverride || nodeRequiresInput() ) {
            EffectPanel ep = EffectPanel.getDefaultPanel(getBattleEvent(), getTargetGroup());
            
            Ability ability = battleEvent.getAbility();
            Target source = battleEvent.getSource();
            
            attackTreePanel.showInputPanel(this,ep);
            attackTreePanel.setInstructions( "Enter the Knockback Damage Roll...");
            
            acceptActivation = true;
        }
        
        
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        return acceptActivation;
    }
    
    private boolean nodeRequiresInput() {
        boolean requiresInput = false;
        
        int kbindex = battleEvent.getKnockbackIndex(getTarget(), getKnockbackGroup());
        KnockbackEffect effect = battleEvent.getKnockbackEffect(kbindex);
       // int distance = battleEvent.getKnockbackDistance(kbindex);
        
        Dice d;
        if ( effect == null || effect.equals(KnockbackEffect.NOEFFECT) == false ) {
            IndexIterator ii = getBattleEvent().getDiceIterator(getTargetGroup());
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
        
        Dice roll = battleEvent.getDiceRoll("KBDamage", getTargetGroup() );
        
        return requiresInput && roll == null && getAutoBypassTarget().getBooleanProfileOption( getAutoBypassOption());
    }
    
    @Override
    public boolean processAdvance() {
        checkDice();
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " exited.");
        return true;
    }
    
    private void checkDice() {
        if ( battleEvent != null ) {
            IndexIterator ii = battleEvent.getDiceIterator(getTargetGroup());
            
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
                    else {
                        // Make sure the correctly sized dice were rolled!
                        String size = battleEvent.getIndexedStringValue(dindex, "Die", "SIZE");
                        boolean autoroll = battleEvent.getDiceAutoRoll(dindex);
                        
                        if ( autoroll && dice.checkSize( size ) == false) {
                            dice = new Dice( size, true);
                            
                            battleEvent.setDiceRoll(dindex, dice);
                        }
                        
                    }
                    
                    
                }
                catch ( BadDiceException bde) {
                    
                }
                
                vindex ++;
            }
        }
    }
    
    /**
     * Builds the next child based upon the last child which was constructed.
     *
     * If all the children necessary have been built, return null, otherwise
     * return the next node.
     *
     * BuildNextChild should add that child to the tree.
     * @param activeChild
     */
    @Override
    public AttackTreeNode buildNextChild(AttackTreeNode activeChild) {
        
        AttackTreeNode nextNode = null;
        
        // AttackParameter has no children...
        
        return nextNode;
    }
    
    
   /* public AttackTreeNode buildNextChild(AttackTreeNode activeChild) {
        
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
                if ( nextNodeName.equals("Knockback Target") ) {
                    ActivationInfo ai = battleEvent.getActivationInfo();
                    int refNumber = ai.getNextTargetReferenceNumber( getTargetGroup() );
                    
                    KnockbackObstructionNode node = new KnockbackObstructionNode("Knockback Target");
                    node.setTargetReferenceNumber( refNumber );
                    nextNode = node;
                    
                    secondaryTargetNode = node;
                }
            }
        }
        return nextNode;
    } */
    
    /** Getter for property knockbackGroup.
     * @return Value of property knockbackGroup.
     */
    public String getKnockbackGroup() {
        return knockbackGroup;
    }
    
    /** Setter for property knockbackGroup.
     * @param knockbackGroup New value of property knockbackGroup.
     */
    public void setKnockbackGroup(String knockbackGroup) {
        this.knockbackGroup = knockbackGroup;
    }
    
    /** Getter for property target.
     * @return Value of property target.
     */
    public Target getTarget() {
        return target;
    }
    
    /** Setter for property target.
     * @param target New value of property target.
     */
    public void setTarget(Target target) {
        this.target = target;
    }
    
    
    @Override
    public String getAutoBypassOption() {
        return "SHOW_KNOCKBACK_EFFECT_PANEL";
    }
    
    @Override
    public Target getAutoBypassTarget() {
        return getTarget();
    }
    
    private void prepareBattleEvent() {
        int kbindex = battleEvent.getKnockbackIndex(getTarget(), getKnockbackGroup());
        int distance = battleEvent.getKnockbackDistance(kbindex);
        int amount = battleEvent.getKnockbackAmount(kbindex);
        
        KnockbackEffect effect = battleEvent.getKnockbackEffect(kbindex);
        
        
        if ( effect == null ) {
            // This shouldn't happen!
        }
        else if ( effect.equals(KnockbackEffect.NOEFFECT) ){
            // This is shouldn't happen either
        }
        else {
            Target sourceTarget = null;
            ActivationInfo ai = battleEvent.getActivationInfo();
            
            sourceTarget = ai.getKnockbackSourceTarget(getTargetGroup());
            boolean collisionOccurred = false;
        
            collisionOccurred = (effect.equals(KnockbackEffect.COLLISION) || ai.getKnockbackCollisionWithSecondaryTargetOccurred(getTargetGroup()));
            
            if ( collisionOccurred ) {
                battleEvent.addDiceInfo("KBDamage", getTargetGroup(), "Knockback damage roll", amount + "d6");
            }
            else {
                
                battleEvent.addDiceInfo("KBDamage", getTargetGroup(), "Knockback damage roll", (amount/2) + "d6");
            }
        }
    }
    
    /** Getter for property ability.
     * @return Value of property ability.
     */
    public Ability getAbility() {
        return this.ability;
    }
    
    /** Setter for property ability.
     * @param ability New value of property ability.
     */
    public void setAbility(Ability ability) {
        this.ability = ability;
        
        
    }
}
