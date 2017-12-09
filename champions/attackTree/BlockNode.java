/*
 * BlockNode.java
 *
 * Created on May 2, 2002, 10:21 PM
 */

package champions.attackTree;

import champions.Ability;
import champions.ActivationInfo;
import champions.BattleEngine;
import champions.BattleEvent;
import champions.CVList;
import champions.Dice;
import champions.Effect;
import champions.Target;
import champions.exception.BattleEventException;
import champions.parameters.ParameterList;
import champions.powers.effectBlock;
import champions.powers.effectDeflection;
import javax.swing.UIManager;



/**
 *
 * @author  Trevor Walker
 * @version
 */
public class BlockNode extends DefaultAttackTreeNode {
    
    /** Holds value of property targetReferenceNumber. */
    private int targetReferenceNumber;
    
    /** Holds value of property target. */
    private Target target;
    
    /** Holds value of property blockEffect. */
    private Effect blockEffect;
    
    private ReflectionTargetNode deflectionTargetNode;
    
    /** Creates new BlockNode */
    public BlockNode(String name) {
        this.name = name;
        icon = UIManager.getIcon("AttackTree.blockIcon");
    }
    
    public boolean activateNode(boolean manualOverride) {
        boolean acceptActivation = false;
        
        if ( manualOverride || nodeRequiresInput() ) {
            ActivationInfo ai = getBattleEvent().getActivationInfo();
            
            int tindex = ai.getTargetIndex(targetReferenceNumber, getTargetGroup());
            
            if ( tindex != -1 ) {
                acceptActivation = true;
                Target source = battleEvent.getSource();
                
                int afShot = getAutofireShot(tindex);
                
                if ( afShot >= 2 ) {
                    String message;
                    int fsindex = ai.getIndexedIntegerValue(tindex, "Target", "AFFIRSTSHOTTARGETINDEX");
                    
                    
                    boolean fsBlocking = ai.getIndexedBooleanValue(fsindex, "Target", "ISBLOCKING");
                    ai.addIndexed(tindex, "Target", "ISBLOCKING", "TRUE", true, false);
                    
                    if ( fsBlocking == false) {
                        message = "This is shot " + afShot + " of an autofire attack.\n\n" +
                                "To block/deflect an autofire attack, select block/deflection node for the first " +
                                "shot of the attack.";
                        
                    }
                    else {
                        boolean blocked = ai.getIndexedBooleanValue(tindex, "Target", "BLOCKED"); // This is set by the AFShotNode
                        int needed = ai.getIndexedIntegerValue(tindex, "Target", "BLOCKTOHIT");
                        Dice d = (Dice)ai.getIndexedValue(fsindex, "Target", "BLOCKDIE");
                        int roll = d.getStun();
                        
                        if ( blocked ) {
                            message = "This is shot " + afShot + " of an autofire attack.\n\n" + 
                                    source.getName() + " previously rolled " + roll + " to block/deflect " +
                                    "this shot and needed \u2264 " + needed + ".\n\n" +
                                    "This shot was blocked/deflected successfully.";
                        }
                        else {
                            message = "This is shot " + afShot + " of an autofire attack.\n\n" + 
                                    source.getName() + " previously rolled " + roll + " to block/deflect " +
                                    "this shot but needed \u2264 " + needed + ".\n\n" +
                                    "This shot was not blocked/deflected successfully.";
                        }
                    }
                    InformationPanel ip = InformationPanel.getDefaultPanel( message );
                    attackTreePanel.showInputPanel(this,ip);
                    attackTreePanel.setInstructions("Hit Okay to Continue...");
                }
                else {
                    CVList cvl = (CVList)ai.getIndexedValue(tindex, "Target", "BLOCKCVLIST");
                    if ( cvl == null ) {
                        cvl = BattleEngine.buildBlockCVList(battleEvent, source, target, blockEffect, null);
                        ai.addIndexed(tindex, "Target", "BLOCKCVLIST", cvl, true);
                    }

                    attackTreePanel.setInstructions( "Enter Block/Deflection Information...");
                    attackTreePanel.showInputPanel(this, BlockPanel.getDefaultPanel(battleEvent, source, target, getTargetGroup(), targetReferenceNumber, cvl));
                }
            }
        }
        
        
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        return acceptActivation;
    }
    
    private boolean nodeRequiresInput() {
        
        return getAutoBypassTarget().getBooleanProfileOption( getAutoBypassOption());
    }
    
    public boolean processAdvance() throws BattleEventException {
        boolean advance = true;
        
        int tindex = getBattleEvent().getActivationInfo().getTargetIndex(targetReferenceNumber, getTargetGroup());
        
        boolean blocked = false;
        
        if ( getAutofireShot(tindex) < 2 ) {
            blocked = BattleEngine.determineBlockSuccess(battleEvent, targetReferenceNumber, getTargetGroup());
        }
        else {
            blocked = getBattleEvent().getActivationInfo().getIndexedBooleanValue(tindex, "Target", "BLOCKED");
        }

        
        if ( blockEffect instanceof effectBlock ) {
            BattleEngine.processBlock(battleEvent, targetReferenceNumber, getTargetGroup());
        }
        else {
            BattleEngine.processDeflection(battleEvent, targetReferenceNumber, getTargetGroup());
        }
        
        return advance;
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
                if ( nextNodeName.equals("Reflection Target") ) {
                    ReflectionTargetNode node = new ReflectionTargetNode("Reflection Target");
                    deflectionTargetNode = node;
                    nextNode = node;
                }
                else if ( nextNodeName.equals("Secondary Target") ) {
                    MissTargetNode node = new MissTargetNode("Secondary Target");
                    nextNode = node;
                }
            }
        }
        return nextNode;
    }
    
    public String nextNodeName(String previousNodeName) {
        String nextNodeName = null;
        
        if ( previousNodeName == null ) {
            ActivationInfo ai = battleEvent.getActivationInfo();
            int tindex = ai.getTargetIndex(targetReferenceNumber, getTargetGroup());
            boolean blocked = ai.getIndexedBooleanValue(tindex, "Target", "BLOCKED");

            if ( blocked && blockEffect instanceof effectDeflection ) {
                Ability deflection = ((effectDeflection)blockEffect).getAbility();
                ParameterList pl = deflection.getPowerParameterList();
                String reflect = pl.getParameterStringValue("Reflect");

                if ( "No Reflection".equals(reflect) == false ) {

                        nextNodeName = "Reflection Target";

                }
                else {
                    nextNodeName = "Secondary Target";
                }
            }
        }
        
        return nextNodeName;
    }

    public void adjustTarget(BattleEvent be, Target source, int referenceNumber, String targetGroup, int primaryTargetNumber) {
        ActivationInfo ai = battleEvent.getActivationInfo();
        int tindex = ai.getTargetIndex(targetReferenceNumber, getTargetGroup());
        boolean blocked = ai.getIndexedBooleanValue(tindex, "Target", "BLOCKED");

        if ( blocked && blockEffect instanceof effectDeflection && deflectionTargetNode != null && deflectionTargetNode.getTargetReferenceNumber() == referenceNumber ) {
            Ability deflection = ((effectDeflection)blockEffect).getAbility();
            ParameterList pl = deflection.getPowerParameterList();
            String reflect = pl.getParameterStringValue("Reflect");

            if ( reflect.equals("Reflect Attacks Back at Attacker") ) {
                int tindex2 = ai.getTargetIndex(referenceNumber, targetGroup);
                ai.setTarget(tindex2, be.getSource());
                ai.setTargetFixed(tindex2, true, "Missile Deflection configured to reflect attacks back at attacker only.");
            }
        }
    }

    public void adjustSource(BattleEvent be, int referneceNumber, String targetGroup) {
        ActivationInfo ai = battleEvent.getActivationInfo();
        int tindex = ai.getTargetIndex(targetReferenceNumber, getTargetGroup());
        int tindex2 = ai.getTargetIndex(referneceNumber, targetGroup);
        ai.setTargetSource(tindex2, ai.getTarget(tindex));
    }

    
    

    

    
    protected int getAutofireShot(int tindex ) {
        Integer i = battleEvent.getActivationInfo().getIndexedIntegerValue(tindex, "Target", "AFSHOT");
        return (i == null ? -1 : i.intValue() );
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
    
    /** Getter for property blockEffect.
     * @return Value of property blockEffect.
     */
    public Effect getBlockEffect() {
        return blockEffect;
    }
    
    /** Setter for property blockEffect.
     * @param blockEffect New value of property blockEffect.
     */
    public void setBlockEffect(Effect blockEffect) {
        this.blockEffect = blockEffect;
    }
    
    public String getAutoBypassOption() {
        return "SHOW_BLOCK_PANEL";
    }
    
    public Target getAutoBypassTarget() {
        return getTarget();
    }
}
