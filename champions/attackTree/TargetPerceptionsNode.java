/*
 * PerceptionsNode.java
 *
 * Created on November 20, 2001, 4:19 PM
 */

package champions.attackTree;

import champions.ActivationInfo;
import champions.Dice;
import champions.Sense;
import champions.BattleEvent;
import champions.Target;
import champions.battleMessage.PerceptionMessage;
import java.util.List;
import javax.swing.UIManager;

/**
 *
 * @author  twalker
 * @version
 */
public class TargetPerceptionsNode extends DefaultAttackTreeNode {
	public static TargetPerceptionsNode Node;
    
    /** Store Target Selected for this node */
    protected Target target;
    
    /** Holds value of property targetReferenceNumber. */
    private int targetReferenceNumber;
    
    /** Creates new PerceptionsNode */
    public TargetPerceptionsNode(String name) {
        this.name = name;
        icon = UIManager.getIcon("AttackTree.perceptionsIcon");
        Node=this;
    }
    
    public boolean activateNode(boolean manualOverride) {
        boolean acceptActivation = false;
        
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        

        
        
        return acceptActivation;
    }
    
    
    public AttackTreeNode buildNextChild(AttackTreeNode activeChild) {
        // This node build everything upfront, when it's target is first set.
        return null;
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
        // Find the sense that was used...
        ActivationInfo ai = battleEvent.getActivationInfo();
        int tindex = ai.getTargetSenseIndex( getTarget() );
        if ( tindex != -1 ) {
            Sense s = ai.getTargetsSense(tindex);
            
            if ( s == null ) {
                s = target.getBestSense( target );
                ai.setTargetsSense(tindex,s);
            }
            if ( s == null) {
                // There is no sense that can sense the target!!!
                if (target.suffersDCVPenaltyDueToSenses() == true){
                    battleEvent.addBattleMessage( new PerceptionMessage( target, target.getName() + " can't perceive " + ai.getSource().getName() + "!  Defense penalties will apply."));//, BattleEvent.MSG_MISS)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( target.getName() + " can't perceive " + ai.getSource().getName() + "!  Defense penalties will apply.", BattleEvent.MSG_MISS)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( target.getName() + " can't perceive " + ai.getSource().getName() + "!  Defense penalties will apply.", BattleEvent.MSG_MISS);
                    ai.setTargetCanSenseSource(tindex, false);
                }
            } else {
                boolean requiresRoll = ai.getTargetRequiresSenseRoll(tindex);
                int needed = target.getPerceptionRoll(s, ai.getSource());
                int base = target.getPerceptionRoll();
                
                if ( s.isTargettingSense() == false || requiresRoll || needed < base  ) {
                    Dice d = ai.getTargetSenseRoll(tindex);
                    
                    if ( d == null || d.isRealized() == false ) {
                        d = new Dice(3);
                        ai.setTargetSenseRoll(tindex, d);
                    }
                    
                    if ( d.getStun().intValue() <= needed ) {
                        // The roll suceeded...
                        battleEvent.addBattleMessage( new PerceptionMessage( target,  target.getName() + " is using " + s.getSenseName() + " to perceive " + ai.getSource().getName() +"."));//, BattleEvent.MSG_HIT));
                        battleEvent.addBattleMessage( new PerceptionMessage( target,  target.getName() + " perception roll with " + s.getSenseName() + " succeeded.  Needed to roll " + needed + ". Rolled " + d.getStun() +"."));//, BattleEvent.MSG_HIT));
                        
                        ai.setTargetCanSenseSource(tindex, true);
                    } else {
                        ai.setTargetCanSenseSource(tindex, false);
                        battleEvent.addBattleMessage( new PerceptionMessage( target,  target.getName() + " is using " + s.getSenseName() + " to perceive " + ai.getSource().getName() +"."));//, BattleEvent.MSG_MISS));
                        battleEvent.addBattleMessage( new PerceptionMessage( target,  target.getName() + " perception roll with " + s.getSenseName() + " failed!  Needed to roll " + needed + ". Rolled " + d.getStun() +"."));//, BattleEvent.MSG_MISS));
                        
                    }
                } else {
                    if ( s.getSenseName().equals("Normal Sight") == false ) {
                        battleEvent.addBattleMessage( new PerceptionMessage( target,  target.getName() + " is using " + s.getSenseName() + " to perceive " + ai.getSource().getName() + ".  No PER Roll is necessary."));//, BattleEvent.MSG_HIT));
                    }
                    ai.setTargetCanSenseSource(tindex, true);
                }
            }
        }
        return true;
    }
    
    public void setTarget(Target target) {
        if ( this.target != target ) {
            ActivationInfo ai = getBattleEvent().getActivationInfo();
            if ( this.target != null ) {
                // This is actually an error, since these nodes should be created for one specific target
            }
            
            this.target = target;
            
            if ( this.target != null ) {
                buildChildren();
            }
        }
    }
    
    public Target getTarget() {
        return this.target;
    }
    
    public void destroy() {
        setTarget(null);
        
        super.destroy();
    }
    
    public void buildChildren() {
        // Build a ToHit node and add it
        //   ToHitNode thn = new ToHitNode("ToHit");
        //   addChild(thn);
        //   thn.setTarget(getTarget()); // Do this afterwards, since the BE would be set before the addChild.
        
    }
    
    public String getAutoBypassOption() {
        return "SHOW_PERCEPTION_PANEL";
    }
    
    public Target getAutoBypassTarget() {
        return getTarget();
    }

    public int getTargetReferenceNumber() {
        return targetReferenceNumber;
    }

    public void setTargetReferenceNumber(int targetReferenceNumber) {
        this.targetReferenceNumber = targetReferenceNumber;
    }
    
}
