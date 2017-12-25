/*
 * ToHitNode.java
 *
 * Created on November 9, 2001, 4:40 PM
 */

package champions.attackTree;

import champions.ActivationInfo;
import champions.BattleEngine;
import champions.CVList;
import champions.Dice;
import champions.Sense;
import champions.Target;
import champions.exception.BattleEventException;
import javax.swing.UIManager;



/**
 *
 * @author  twalker
 * @version
 */
public class ToHitNode extends DefaultAttackTreeNode {
    
    /** Holds value of property target. */
    private Target target;
    
    /** Holds value of property cvList. */
    private CVList cvList;
    
    /** Holds value of property hitStatus. */
    private String hitStatus;
    
    /** Holds value of property targetReferenceNumber. */
    private int targetReferenceNumber;
    
    private MissTargetNode secondaryTargetNode;
    
    private int mode;
    
    public static ToHitNode Node;
    /** Creates new ToHitNode */
    public ToHitNode(String name) {
        this.name = name;
        icon = UIManager.getIcon("AttackTree.toHitIcon");
        Node = this;
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
        if(attackTreePanel==null) {
        	attackTreePanel = AttackTreePanel.defaultAttackTreePanel;
        	}
        // Make sure you have the correct target...
        ActivationInfo ai = getBattleEvent().getActivationInfo();
        int tindex = ai.getTargetIndex(targetReferenceNumber, getTargetGroup() );
        if ( tindex != -1 ) {
            // Grab the first target found
            setTarget( ai.getTarget(tindex) );
            prepareBattleEvent();
            setCVList( ai.getCVList(tindex) );
        }
        
        if ( manualOverride || nodeRequiresInput() ) {
            String hitmode = ai.getIndexedStringValue(tindex, "Target", "HITMODE");
            if ( hitmode != null && hitmode.equals(OVERRIDE) && ( ai.getIndexedBooleanValue(tindex, "Target", "BLOCKED") == false || ai.getIndexedIntegerValue(tindex, "Target", "AFSHOT") != null ) ) {
                String explination = ai.getTargetOverrideExplination(tindex);
                InformationPanel ip = InformationPanel.getDefaultPanel( explination );
                attackTreePanel.showInputPanel(this,ip);
                attackTreePanel.setInstructions("Hit Okay to Continue...");
            } else {
                ToHitPanel app = ToHitPanel.getToHitPanel( battleEvent, target, getTargetGroup(), targetReferenceNumber);
                
                attackTreePanel.showInputPanel(this,app);
                attackTreePanel.setInstructions("Configure CV Values to hit " + getTarget().getName() + ".");
            }
            
            acceptActivation = true;
        }
        //  }
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        return acceptActivation;
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
        
        AttackTreeNode nextNode = null;
        // Compare the activeChild to the possible children, to determine what comes next.
        if ( activeChild != null && activeChild.getName() == null ) {
            // This is probably an error
            int index = children.indexOf(activeChild);
            System.out.println("Null Node name for child " + Integer.toString(index) + " of Parent " + this);
            nextNode = null;
        } else {
            String previousNodeName = (activeChild == null ) ? null : activeChild.getName();
            String nextNodeName = nextNodeName(previousNodeName);
            if ( nextNodeName != null ) {
                if ( nextNodeName.equals("Secondary Target") ) {
                    // Build the very first node: Attack Param
                    MissTargetNode node = new MissTargetNode("Secondary Target");
                    secondaryTargetNode = node;
                    //node.setTargetReferenceNumber(targetReferenceNumber);
                    //node.setTargetGroupSuffix("");
                    nextNode = node;
                }
            }
        }
        return nextNode;
    }
    
    public String nextNodeName(String previousNodeName) {
        String nextNodeName = null;
        
        switch(mode) {
            case KNOCKBACK_TARGET:
                nextNodeName = null;
                break;
            
            default:
                if ( previousNodeName == null ) {
                    if ( hitStatus != null && ( hitStatus.equals("Missed") ) )  {
                        nextNodeName = "Secondary Target";
                    }
                }
                break;
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
        // Make sure if it is a roll, that there is a dice out there...
        ActivationInfo ai = battleEvent.getActivationInfo();
        int tindex = ai.getTargetIndex(targetReferenceNumber, getTargetGroup());
        
         tindex = ai.getSourceSenseIndex( ai.getSource() );
        CVList list = ai.getCVList(tindex);
         tindex = ai.getTargetIndex(targetReferenceNumber, getTargetGroup());
        
        String hitmode = ai.getTargetHitMode(tindex);
        if ( hitmode == null ) {
            hitmode = USEDICE;
            ai.addIndexed(tindex, "Target", "HITMODE", hitmode, true);
        }
        
        if (ai.getAbility().isSkill() || ai.getAbility().isDisadvantage()) {
            hitmode = FORCEHIT;
            ai.addIndexed(tindex, "Target", "HITMODE", hitmode, true);
        }
        
        if ( hitmode.equals(USEDICE) || hitmode.equals(OVERRIDE) )  {
            Dice d = ai.getIndexedDiceValue(tindex, "Target", "TOHITDIE");
            if ( d == null ) {
                d = new Dice(3,true);
                ai.addIndexed(tindex, "Target", "TOHITDIE", d, true);
                ai.addIndexed(tindex, "Target", "ROLLMODE", AUTO_ROLL, true);
            }
        }
        
        // Now that there is at least a roll out there, determine if there was a hit.
        
        try {
            
            boolean blocked = ai.getIndexedBooleanValue(tindex, "Target", "BLOCKED");
            
            boolean hit = BattleEngine.determineHit(getBattleEvent(), targetReferenceNumber, getTargetGroup());
            if ( blocked ) {
                if ( getBattleEvent().isMeleeAttack() ) {
                    setHitStatus("Blocked");
                }
                else {
                    setHitStatus("Deflected");
                }
            }
            else {
                setHitStatus( hit ? "Hit" : "Missed" );
            }
        } catch (BattleEventException bee) {
            
        }
        
        
        
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println( "Node " + name + " exited.");
        return true;
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
    
    /** Getter for property cvList.
     * @return Value of property cvList.
     */
    public CVList getCVList() {
        return cvList;
    }
    
    /** Setter for property cvList.
     * @param cvList New value of property cvList.
     */
    public void setCVList(CVList cvList) {
        this.cvList = cvList;
    }
    
    /** Determines if this node needs to show an input panel.
     *
     */
    protected  boolean nodeRequiresInput() {
        boolean requiresInput = false;
        
        if ( getBattleEvent() != null && getTarget() != null && getTargetGroup() != null ) {
            ActivationInfo ai = getBattleEvent().getActivationInfo();
            int tindex = ai.getTargetIndex(targetReferenceNumber, getTargetGroup());
            
            String hitmode = ai.getIndexedStringValue(tindex, "Target", "HITMODE");
            if ( hitmode.equals(USEDICE) ) {
                Dice d = (Dice)ai.getIndexedValue(tindex, "Target", "TOHITDIE");
                if ( d == null ) {
                    requiresInput = true;
                } else {
                    requiresInput = false;
                }
            } else {
                // FORCEHIT, FORCEMISS, OVERRIDE options
                requiresInput = false;
            }
        }
        
        return requiresInput && battleEvent.getSource().getBooleanProfileOption("SHOW_TOHIT_PANEL");
    }
    
    /** Getter for property hitStatus.
     * @return Value of property hitStatus.
     */
    public String getHitStatus() {
        return hitStatus;
    }
    
    /** Setter for property hitStatus.
     * @param hitStatus New value of property hitStatus.
     */
    public void setHitStatus(String hitStatus) {
        if ( hitStatus == null || hitStatus.equals(this.hitStatus) == false) {
            this.hitStatus = hitStatus;
            if ( hitStatus.equals("Missed") == false  && secondaryTargetNode != null) {
                secondaryTargetNode.clearSecondaryTarget();
            }
            // getRealParent().nodeChanged(this);
            
            // Alert the model that the node has changed (the description) so it
            // can update the tree.
            if ( getModel() != null ) getModel().nodeChanged(this);
        }
    }
    
    public String toString() {
        if ( hitStatus == null ) {
            return "ToHit";
        } else {
            return "ToHit - " + hitStatus;
        }
    }
    
    /** Getter for property targetReferenceNumber.
     * @return Value of property targetReferenceNumber.
     */
    public int getTargetReferenceNumber() {
        return targetReferenceNumber;
    }
    
    /** Setter for property targetReferenceNumber.
     * @param targetReferenceNumber New value of property targetReferenceNumber.
     */
    public void setTargetReferenceNumber(int targetReferenceNumber) {
        this.targetReferenceNumber = targetReferenceNumber;
    }
    
    public String getAutoBypassOption() {
        return "SHOW_TOHIT_PANEL";
    }
    
    public Target getAutoBypassTarget() {
        return battleEvent.getSource();
    }
    
    private void prepareBattleEvent() {
        if ( target != null ) {
            ActivationInfo ai = battleEvent.getActivationInfo();
            // Do all the target initialization here
            int tindex = ai.getTargetIndex(targetReferenceNumber,getTargetGroup());
            
            CVList cvList = ai.getCVList(tindex); // This will return a blank CVList in most cases
            if ( cvList.isInitialized() == false ) {
                Target source = ai.getTargetSource(tindex);
                if ( source == null ) source = battleEvent.getSource();
                
                if ( ai.isTargetSecondary(tindex) ) {
                    BattleEngine.buildCVListForSecondary(getBattleEvent(), source, target, cvList);
                }
                else if ( ai.isTargetKnockbackSecondary( tindex ) ) {
                    BattleEngine.buildCVListForKBSecondary(getBattleEvent(), source, target, cvList);
                }
                else {
                    BattleEngine.buildCVList(getBattleEvent(), source, target, cvList);
                    adjustCVList(getBattleEvent(), source, target, cvList);
                } 
            }
            
            // Adjust the cvList according to the perception rolls...
            cvList.removeSourceModifier("Perception");
            
            //jeff ai.getsource instead if target
            int sindex = ai.getSourceSenseIndex( ai.getSource() );
           
            Sense s = ai.getSourcesSense(sindex);
            if ( ai.getSourceCanSenseTarget(sindex) == false ) {
                // Can't perceive target with sense!!!
                if ( ai.getAbility().isRangedAttack() ) {
                    // Ranged OCV = 0
                    cvList.addSourceCVMultiplier("Perception", 0);
                } else {
                    // Hand-to-Hand OCV = 1/2
                    cvList.addSourceCVMultiplier("Perception", 0.5);
                }
            } else {
                // Can perceive target with sense!!!
                if ( s.isTargettingSense() == false) {
                    // Non-Target Sense that works = 1/2 OCV
                    cvList.addSourceCVMultiplier("Perception", 0.5);
                }
            }
            
            cvList.removeTargetModifier("Perception");
            
            sindex = ai.getTargetSenseIndex( target );
            s = ai.getTargetsSense(sindex);
            if (getTarget().suffersDCVPenaltyDueToSenses() == true) {
                if ( ai.getTargetCanSenseSource(sindex) == false) {
                    // Can't perceive target with sense!!!
                    // Ranged DCV = 1/2
                    // Hand-to-Hand DCV = 1/2
                    cvList.addTargetCVMultiplier("Perception", 0.5);
                } else {
                    // Can perceive target with sense!!!
                    if ( s.isTargettingSense() == false) {
                        if ( ai.getAbility().isRangedAttack() ) {
                            // Ranged DCV = 1
                            // cvList.addTargetCVMultiplier("Perception", 0);
                        } else {
                            // Hand-to-Hand DCV = -1
                            cvList.addTargetCVModifier("Perception", -1);
                        }
                    }
                }
            }
        }
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
    
}
