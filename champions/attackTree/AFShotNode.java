/*
 * AFShotNode.java
 *
 * Created on December 16, 2001, 2:09 PM
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
import champions.battleMessage.AFShotMessageGroup;
import champions.battleMessage.BattleMessageGroup;
import champions.exception.BattleEventException;
import champions.interfaces.AbilityIterator;
import champions.powers.effectCombatMultiplier;

/**
 *
 * @author  twalker
 * @version
 */
public class AFShotNode extends DefaultAttackTreeNode implements BattleMessageGroupProvider {
    
    /** Holds value of property shot. */
    private int shot;
    
    /** Holds value of property firstShotTargetGroup. */
    private String firstShotTargetGroup;
    
    private AFShotMessageGroup afShotMessageGroup;
    
    /** Creates new AFShotNode */
    public AFShotNode(String name) {
        this.name = name;
        setVisible(true);
    }
    
    public boolean activateNode(boolean manualOverride) {
        boolean activateNode = false;
        // Whenever this node is activated, it need to gaurantee that the battleEvent is prepared correct.
        prepareBattleEvent();
        
        if ( nodeRequiresInput() || manualOverride == true ) {
            Target source = battleEvent.getSource();
            Ability ability = battleEvent.getAbility();
            String reason = "This folder contains all of the information concerning autofire shot " + shot + " of " +
                            source.getName() + "'s " + ability.getName() + "  power.";
            InformationPanel ip = InformationPanel.getDefaultPanel( reason );
            attackTreePanel.showInputPanel(this,ip);
            attackTreePanel.setInstructions("Hit Okay to Continue...");
            
            activateNode = true;
        }
        
        return activateNode;
    }
    
    
    public AttackTreeNode buildNextChild(AttackTreeNode activeChild) {
        
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
                if ( nextNodeName.equals("Single Attack")) {
                    SingleAttackNode node = new SingleAttackNode(nextNodeName);
                    node.setPrimaryTargetNumber(shot);
                    nextNode = node;
                }
                else if ( nextNodeName.equals("AreaEffect")) {
                    AreaEffectAttackNode node = new AreaEffectAttackNode(nextNodeName);
                    node.setPrimaryTargetNumber(shot);
                    nextNode = node;
                }
                else if ( nextNodeName.equals("AFShot Close Message Group")) {
                    CloseMessageGroupNode node = new CloseMessageGroupNode(nextNodeName, this);
                    nextNode = node;
                }
            }
            
        }
        return nextNode;
    }
    
    public String nextNodeName(String previousNodeName) {
        String nextNodeName = null;
        
        if ( previousNodeName == null ) {
            BattleEvent be = getBattleEvent();
            Ability ability = be.getAbility();
            ActivationInfo ai = be.getActivationInfo();
            
            if ( be.is("AE" ) ) {
                nextNodeName = "AreaEffect";
            }
            else {
                nextNodeName = "Single Attack";
            }
        }
        
        
        if ( nextNodeName == null && "AFShot Close Message Group".equals(previousNodeName) == false ) {
            nextNodeName = "AFShot Close Message Group";
        }
        
        return nextNodeName;
    }
    
    /** Getter for property shot.
     * @return Value of property shot.
     */
    public int getShot() {
        return shot;
    }
    
    /** Setter for property shot.
     * @param shot New value of property shot.
     */
    public void setShot(int shot) {
        this.shot = shot;
    }
    
    public void prepareBattleEvent() {
        // Create the group for this attack
        ActivationInfo ai = getBattleEvent().getActivationInfo();
        Target source = battleEvent.getSource();
        boolean accuratesprayfire = ai.getBooleanValue("Attack.ACCURATESPRAYFIRE");
        boolean concentratedsprayfire = ai.getBooleanValue("Attack.CONCENTRATEDSPRAYFIRE");
        boolean rapidautofire = ai.getBooleanValue("Attack.RAPIDAUTOFIRE");
        boolean skipoversprayfire = ai.getBooleanValue("Attack.SKIPOVERSPRAYFIRE");
        
        if ( ai.getBooleanValue("Attack.ISSPRAY") == false ) {
            // It is a normal autofire attack, so if this is not the first shot,
            // set the target appropriately and the HITOVERRIDE appropriately.
            if ( shot > 1 ) {
                String targetGroup = getTargetGroup();
                
                // Grab the firstShot's primary Target.
                int fsindex = ai.getTargetIndex(0, firstShotTargetGroup);
                
                if ( fsindex != -1 ) {
                    // Grab the information about the first shot Target
                    Target fstarget = (Target)ai.getIndexedValue(fsindex, "Target", "TARGET");
                    Integer tohit = ai.getIndexedIntegerValue(fsindex, "Target", "TOHIT" );
                    Integer hitroll = ai.getIndexedIntegerValue(fsindex, "Target", "HITROLL");
                    boolean blocking = ai.getIndexedBooleanValue(fsindex, "Target", "ISBLOCKING");
                    
                    int blockToHit = 0;
                    int blockRoll = 18;
                    
                    if ( blocking ) {
                        blockToHit = ai.getIndexedIntegerValue(fsindex, "Target", "BLOCKTOHIT");
                        Dice d = (Dice)ai.getIndexedValue(fsindex, "Target", "BLOCKDIE");
                        blockRoll = d.getStun();
                    }
                    
                    String calledShot = ai.getIndexedStringValue(fsindex, "Target", "HITLOCATIONCALLEDSHOT");
                    
                    // Grab the index of this shots first Target
                    int tindex = -1;
                    Target target = null;
                    // Grab the Primary Target for the TargetGroup
                    tindex = ai.getTargetIndex(0, targetGroup);
                    if ( tindex != -1 ) {
                        target = (Target)ai.getIndexedValue(fsindex, "Target", "TARGET");
                        if ( target != null && target != fstarget ) {
                            // There is an existing Target for this shot and it is not the first shot
                            // Target.  Remove the Target group targets and continue on.
                            ai.removeTargetGroup(targetGroup);
                        }
                    }
                    
                    // Create the Group for this attack, in case it doesn't exist yet or if we just removed it.
                    int tgindex = ai.addTargetGroup(targetGroup);
                    
                    // Add the first shot target with the reference number of 0 (primary target).
                    tindex = ai.addTarget(fstarget, targetGroup, 0);
                    
                    ai.addIndexed(tindex, "Target", "AFSHOT", shot, true, false);
                    ai.addIndexed(tindex, "Target", "AFFIRSTSHOTTARGETGROUP", firstShotTargetGroup, true, false);
                    ai.addIndexed(tindex, "Target", "AFFIRSTSHOTTARGETINDEX", fsindex, true, false);
                    
                    boolean hit;
                    boolean blocked = false;
                    int needed = tohit.intValue() - ( 2 * (shot - 1));
                    hit = ( hitroll.intValue() <= needed );
                    
                    int blockNeeded = 0;
                    if ( blocking ) {
                        blockNeeded = blockToHit - Math.max(0, ( 2 * (shot - 1)));
                        blocked = blockRoll <= blockNeeded;
                        ai.addIndexed(tindex, "Target", "BLOCKTOHIT", blockNeeded, true, false);
                    }
                    
                    String explination;
                    if ( blocked ) {
                        explination = fstarget.getName()+ " blocked/deflected automatically.\n\n"
                            + "This was shot " + Integer.toString(shot) + " of a normal autofire attack.  This shot was " 
                            + "blocked/deflected based upon the first shot's block roll of " + blockRoll + ". "
                            + "After an block/deflection penalty of " + Integer.toString(2 * (shot - 1)) + ", " + fstarget.getName()
                            + " needed a " + Integer.toString(blockNeeded) + " or less to block/deflect this shot.";
                        hit = false;
                        
                        ai.addIndexed(tindex, "Target", "BLOCKED", "TRUE", true, false);
                    }
                    else {
                        ai.addIndexed(tindex, "Target", "BLOCKED", "FALSE", true, false);
                        
                        if ( hit == true ) {
                            explination = fstarget.getName()+ " was hit automatically by " + source.getName() + ".\n\n"
                            + "This was shot " + Integer.toString(shot) + " of a normal autofire attack.  This shot hit based "
                            + "upon the first shot's ToHit roll of " + hitroll.toString() + ". "
                            + "After an autofire penalty of " + Integer.toString(2 * (shot - 1)) + ", " + source.getName()
                            + " needed a " + Integer.toString(needed) + " or less to hit with this shot.";
                        }
                        else {
                            explination = fstarget.getName() +" was missed automatically by " + source.getName() + ".\n\n"
                            + "This was shot " + Integer.toString(shot) + " of a normal autofire attack.  This shot missed based "
                            + "upon the first shot's ToHit roll of " + hitroll.toString() + ". "
                            + "After an autofire penalty of " + Integer.toString(2 * (shot - 1)) + ", " + source.getName()
                            + " needed a " + Integer.toString(needed) + " or less to hit with this shot.";
                        }
                    }
                    
                    String reason = "Autofire Shot " + Integer.toString(shot) + " ToHit based upon first shot roll.  Needed " + Integer.toString(needed) + " and rolled " + hitroll.toString() + ".";
                    
                    ai.setTargetHitOverride(tindex, hit, reason, explination);
                    
                    ai.setTargetFixed(tindex, true, "The Target of this shot is " + fstarget.getName()
                    + ".  It was set automatically since all shots of a normal autofire attack must be aimed at the "
                    + "same target.\n\n"
                    + "Select the First Shot to change targets of all the shots.");
                    
                    if ( calledShot != null ) {
                        ai.addIndexed(tindex, "Target", "HITLOCATIONCALLEDSHOT", calledShot, true);
                    }
                    
                    
                }
            }

            String targetGroup = getTargetGroup();
            // Create the Group for this attack, in case it doesn't exist yet or if we just removed it.
            int tgindex = ai.addTargetGroup(targetGroup);
            
            // Add the first shot target with the reference number of 0 (primary target).
            int tindex = ai.addTarget(null,targetGroup, 0);
            CVList cvl = ai.getCVList(tindex);
            cvl.removeSourceModifier("Accurate Sprayfire Penalty");
            cvl.removeSourceModifier("Concentrated Sprayfire Penalty");
            cvl.removeSourceModifier("Rapid Autofire Penalty");
            cvl.removeSourceModifier("Skipover Sprayfire Penalty");
            
        }
        else {
            String targetGroup = getTargetGroup();
            // Create the Group for this attack, in case it doesn't exist yet or if we just removed it.
            int tgindex = ai.addTargetGroup(targetGroup);
            
            // Add the first shot target with the reference number of 0 (primary target).
            int tindex = ai.addTarget(null,targetGroup, 0);
            
            Integer sprayWidth = ai.getIntegerValue("Attack.SPRAYWIDTH");
            
            if (accuratesprayfire == true && sprayWidth.intValue() > 1 ) {
                AbilityIterator aiter = source.getSkills();
                while (aiter.hasNext() ) {
                    Ability a = aiter.nextAbility();
                    if (a.getName().equals("Accurate Sprayfire") && a.isEnabled(source) ) {
                        CVList cvl = ai.getCVList(tindex);
                        cvl.addSourceCVModifier("Accurate Sprayfire Penalty", -1 );
                        cvl.removeSourceModifier("Autofire Penalty");
                    }
                    break;
                }
                
            }
            else {
                CVList cvl = ai.getCVList(tindex);
                cvl.addSourceCVModifier("Autofire Penalty", -1 * sprayWidth.intValue());
                cvl.removeSourceModifier("Accurate Sprayfire Penalty");
                //Effect aseffect = source.getEffect("Accurate Sprayfire watcher");
                //aseffect.add("Effect.USEDACCURATESPRAYFIRE", "FALSE", true);
            }
            if (concentratedsprayfire == true ) {
                CVList cvl = ai.getCVList(tindex);
                cvl.addSourceCVModifier("Concentrated Sprayfire Penalty", -1 );
            }
            else {
                CVList cvl = ai.getCVList(tindex);
                cvl.removeSourceModifier("Concentrated Sprayfire Penalty");
            }

            if (skipoversprayfire == true ) {
                CVList cvl = ai.getCVList(tindex);
                cvl.addSourceCVModifier("Skipover Sprayfire Penalty", -1 );
            }
            else {
                CVList cvl = ai.getCVList(tindex);
                cvl.removeSourceModifier("Skipover Sprayfire Penalty");
            }
            
            if (accuratesprayfire == true || concentratedsprayfire == true || rapidautofire == true || skipoversprayfire == true ) {
                try {
                    
                    Effect as = new effectCombatMultiplier("Autofire Skill Penalty",0.0,0.5);
                    BattleEngine.addEffect(battleEvent, as, source);
                }
                catch (BattleEventException bee) {
                    getModel().setError(bee);
                    
                }
            }
        }
        
        afShotMessageGroup = new AFShotMessageGroup(source,shot);
        battleEvent.openMessageGroup(afShotMessageGroup);
    }
    
    /** Getter for property firstShotTargetGroup.
     * @return Value of property firstShotTargetGroup.
     */
    public String getFirstShotTargetGroup() {
        return firstShotTargetGroup;
    }
    
    /** Setter for property firstShotTargetGroup.
     * @param firstShotTargetGroup New value of property firstShotTargetGroup.
     */
    public void setFirstShotTargetGroup(String firstShotTargetGroup) {
        this.firstShotTargetGroup = firstShotTargetGroup;
    }
    
    private boolean nodeRequiresInput() {
        // Run through the dice and make sure the all have values;
        boolean requiresInput = false;
        
        return requiresInput;
    }

    public BattleMessageGroup getBattleMessageGroup() {
        return afShotMessageGroup;
    }
}
