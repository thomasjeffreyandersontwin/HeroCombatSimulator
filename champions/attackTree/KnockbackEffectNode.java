/*
 * KnockbackEffectNode.java
 *
 * Created on November 8, 2001, 12:03 PM
 */

package champions.attackTree;

import champions.Ability;
import champions.ActivationInfo;
import champions.Target;
import champions.enums.KnockbackEffect;
import champions.interfaces.IndexIterator;
import javax.swing.UIManager;



/**
 *
 * @author  twalker
 * @version
 */
public class KnockbackEffectNode extends DefaultAttackTreeNode {
    
    public static KnockbackEffectNode Node;

	/** Holds value of property knockbackGroup. */
    private String knockbackGroup;
    
    /** Holds value of property targetReferenceNumber. */
    private int targetReferenceNumber;
    
    /** Holds value of property target. */
    private Target target;
    
    /** Holds value of property ability. */
    private Ability ability;
    
    
    
   // private KnockbackObstructionNode secondaryTargetNode;
    
    /** Creates new KnockbackEffectNode
     * @param name 
     */
    public KnockbackEffectNode(String name) {
        this.name = name;
        icon = UIManager.getIcon("AttackTree.knockbackEffectIcon");
        Node = this;
    }
    
    @Override
    public boolean activateNode(boolean manualOverride) {
        boolean acceptActivation = false;
        
        boolean requiresInput = nodeRequiresInput();

        prepareBattleEvent();
        if ( manualOverride || requiresInput ) {
            acceptActivation = true;
            if(attackTreePanel==null) {
            	attackTreePanel=AttackTreePanel.defaultAttackTreePanel;
            }          
            attackTreePanel.setInstructions( "Choose the effect of the Knockback...");
            attackTreePanel.showInputPanel(this, KnockbackEffectPanel.getDefaultPanel(battleEvent, getTarget(), getKnockbackGroup(),getTargetGroup()));
       }
        
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        return acceptActivation;
    }
    
    private boolean nodeRequiresInput() {
        boolean requiresInput = false;
        if(battleEvent!=null) {
	        int kbindex = battleEvent.getKnockbackIndex(getTarget(), getKnockbackGroup());
	        KnockbackEffect effect = battleEvent.getKnockbackEffect(kbindex);
	        int distance = battleEvent.getKnockbackDistance(kbindex);
	        
	        if ( effect == null && distance > 0) {
	            requiresInput = true;
	        }
        }
        return requiresInput && getAutoBypassTarget().getBooleanProfileOption( getAutoBypassOption());
    }
    
    @Override
    public boolean processAdvance() {
        boolean advance = true;
        //for breakfall
        int kbindex = battleEvent.getKnockbackIndex(getTarget(), getKnockbackGroup());
        int distance = battleEvent.getKnockbackDistance(kbindex);
        
        KnockbackEffect effect = battleEvent.getKnockbackEffect(kbindex);
        

        ActivationInfo ai = battleEvent.getActivationInfo();
        
        int tindex = ai.getTargetIndex(targetReferenceNumber, getTargetGroup() );
        Target target = ai.getTarget(tindex);
        Ability ability = battleEvent.getAbility();
        
        String reason;
        
        int amount = battleEvent.getKnockbackAmount(kbindex);
//        int zero = 0;
//      
//        if ( effect == null || effect.equals( KnockbackEffect.NOEFFECT) || effect.equals( KnockbackEffect.COLLISION)) {
//            battleEvent.removeSkillRollInfo("BreakfallRoll", getTargetGroup());
//        } 
        
        return advance;
    }
   /* private void checkDice(int kbindex, int dice) {
        Dice d = battleEvent.getKnockbackDamageRoll(kbindex);
        
        if ( d == null ) {
            d = new Dice(dice, true);
            battleEvent.setKnockbackDamageRoll(kbindex, d);
        }
        
        battleEvent.addBattleMessage( new champions.battleMessage.SimpleBattleMessage(target.getName() + " rolled " + d.toString() + " for knockback damage.", BattleEvent.MSG_DICE)); // .addBattleMessage( new champions.battleMessage.SimpleBattleMessage(target.getName() + " rolled " + d.toString() + " for knockback damage.", BattleEvent.MSG_DICE)); // .addMessage(target.getName() + " rolled " + d.toString() + " for knockback damage.", BattleEvent.MSG_DICE);
    } */
    
    @Override
    public AttackTreeNode buildNextChild(AttackTreeNode activeChild) {
         AttackTreeNode nextNode = null;
         
         int kbindex = battleEvent.getKnockbackIndex(getTarget(), getKnockbackGroup());
        KnockbackEffect effect = battleEvent.getKnockbackEffect(kbindex);
            
        if ( effect != null && effect.equals(KnockbackEffect.NOEFFECT) == false && effect.equals(KnockbackEffect.NOCOLLISION) == false) {
            // Compare the activeChild to the possible children, to determine what comes next.
            if ( activeChild != null && activeChild.getName() == null ) {
                // This is probably an error
                int index = children.indexOf(activeChild);
                if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Null Node name for child " + Integer.toString(index) + " of Parent " + this);
                nextNode = null;
            } else {
                boolean buildAnotherTarget = false;

                if ( activeChild != null ) {
                    //KnockbackObstructionNode stn = (KnockbackObstructionNode)activeChild;
                    KnockbackSecondaryTargetNode stn = (KnockbackSecondaryTargetNode)activeChild;
                    if ( stn.getTarget() != null ) buildAnotherTarget = true;
                } else {
                    // There are no targets, so go ahead and build the first...
                    buildAnotherTarget = true;
                }

                if ( buildAnotherTarget == true ) {
                    ActivationInfo ai = battleEvent.getActivationInfo();

                    int refNumber = ai.getNextTargetReferenceNumber( getTargetGroup() );
                    int tindex = ai.addTarget(null, getTargetGroup(), refNumber);

                    //KnockbackObstructionNode node = new KnockbackObstructionNode("Knockback Target");
                    KnockbackSecondaryTargetNode node = new KnockbackSecondaryTargetNode("Knockback Target");
                    node.setTargetReferenceNumber( refNumber );

                    nextNode = node;
                }
            }
        }
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
    
    public void buildChildren() {
        // Build the initial children that already exist in the target group (probably due to continuing power)...
        ActivationInfo ai = battleEvent.getActivationInfo();
        IndexIterator ii = ai.getTargetGroupIterator( getTargetGroup() );
        
        while ( ii.hasNext() ) {
            int index = ii.nextIndex();
            int refNumber = ai.getTargetReferenceNumber(index);
            
            //KnockbackObstructionNode node = new KnockbackObstructionNode("Knockback Target");
            KnockbackSecondaryTargetNode node = new KnockbackSecondaryTargetNode("Knockback Target");
            node.setTargetReferenceNumber( refNumber );
            
            addChild(node);
        }
    }
    
    @Override
    public void checkNodes() {
        int count = (children == null) ? 0 : children.size();
        int index;
        String previousNodeName = null;
        String nextNodeName = null;
        //KnockbackObstructionNode atn;
        KnockbackSecondaryTargetNode atn;
        
        int kbindex = battleEvent.getKnockbackIndex(getTarget(), getKnockbackGroup());
        KnockbackEffect effect = battleEvent.getKnockbackEffect(kbindex);
            
        if ( effect == null || effect.equals(KnockbackEffect.NOEFFECT)) {
            for(index=count-1;index>=0;index--) {
                //atn = (KnockbackObstructionNode)children.get(index);
                atn = (KnockbackSecondaryTargetNode)children.get(index);
                // There is an empty target that isn't the last, so remove it.
                removeChild(atn);
                index--;
            }
        }
        else {
            for(index=0;index<count;index++) {
                //atn = (KnockbackObstructionNode)children.get(index);
                atn = (KnockbackSecondaryTargetNode)children.get(index);
                if ( atn.getTarget() == null && index != count -1  ) {
                    // There is an empty target that isn't the last, so remove it.
                    removeChild(atn);
                    index--;
                    count--;
                }
            }
        }
    }
    
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
    
//    private void removeSecondaryTargets() {
//        if ( secondaryTargetNode != null ) {
//            if ( secondaryTargetNode.getTarget() != null ) {
//                secondaryTargetNode.setTarget(null);
//            }
//            removeChild( secondaryTargetNode );
//            secondaryTargetNode = null;
//        }
//    }
    
    @Override
    public String getAutoBypassOption() {
        return "SHOW_KNOCKBACK_EFFECT_PANEL";
    }
    
    @Override
    public Target getAutoBypassTarget() {
        return getTarget();
    }
    
    private void prepareBattleEvent() {
    	if(battleEvent!=null) {
	        int kbindex = battleEvent.getKnockbackIndex(getTarget(),getKnockbackGroup());
	        KnockbackEffect effect = battleEvent.getKnockbackEffect(kbindex);
	        
	        int distance = battleEvent.getKnockbackDistance(kbindex);
	        boolean knockdownPossible = battleEvent.isKnockedDownPossible(kbindex);
	        
	        if ( distance == 0 ) {
	            if ( knockdownPossible ) {
	                battleEvent.setKnockbackEffect(kbindex, KnockbackEffect.KNOCKDOWNONLY);
	            }
	            else {
	                battleEvent.setKnockbackEffect(kbindex, KnockbackEffect.NOEFFECT);
	            }
	        }
	        else if ( effect == null ) {
	            battleEvent.setKnockbackEffect(kbindex, KnockbackEffect.POSSIBLECOLLISION);
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
