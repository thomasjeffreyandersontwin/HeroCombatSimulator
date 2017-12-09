/*
 * KnockbackDamageApplyNode.java
 *
 * Created on November 8, 2001, 12:08 PM
 */
package champions.attackTree;

import champions.ActivationInfo;
import champions.BattleEngine;
import champions.Dice;
import champions.Effect;
import champions.Preferences;
import champions.SkillRollInfo;
import champions.Target;
import champions.battleMessage.DiceRollMessage;
import champions.battleMessage.KnockbackSummaryMessage;
import champions.enums.KnockbackEffect;

import champions.exception.BattleEventException;
import champions.interfaces.IndexIterator;
import champions.powers.effectKnockback;
import champions.powers.effectKnockedDown;
import javax.swing.UIManager;

/**
 *
 * @author  twalker
 * @version
 */
public class KnockbackDamageApplyNode extends DefaultAttackTreeNode {

    /** Holds value of property knockbackGroup. */
    private String knockbackGroup;
    /** Holds value of property target. */
    private Target target;
    private int startMessageCount;
    private int endMessageCount;
    private boolean firstPass = true;
    private boolean buildSecondaryNode = false;

    /**
     * Creates new KnockbackDamageApplyNode
     * @param name
     */
    public KnockbackDamageApplyNode(String name) {
        this.name = name;
        // icon = UIManager.getIcon("AttackTree.diceIcon");
        setVisible(true);
        icon = UIManager.getIcon("AttackTree.summaryIcon");
    }

    @Override
    public boolean activateNode(boolean manualOverride) throws BattleEventException {
        boolean activateNode = false;

        if (AttackTreeModel.DEBUG > 0) {
            System.out.println("Node " + name + " activated.");
        }

        startMessageCount = battleEvent.getMessageCount();

        generateKnockbackDamage();

        endMessageCount = battleEvent.getMessageCount();

        if (nodeRequiresInput() || manualOverride == true) {
            MessagePanel mp = MessagePanel.getDefaultPanel(battleEvent, target, startMessageCount, endMessageCount);
            attackTreePanel.showInputPanel(this, mp);
            attackTreePanel.setInstructions("Hit Okay to Continue...");

            activateNode = true;
        }

        return activateNode;
    }

    private boolean nodeRequiresInput() {
        boolean requiresInput = false;

        if (startMessageCount < endMessageCount) {
            requiresInput = true;
        }

        return requiresInput && firstPass && getAutoBypassTarget().getBooleanProfileOption(getAutoBypassOption());
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
        // Compare the activeChild to the possible children, to determine what comes next.
        if (activeChild != null && activeChild.getName() == null) {
            // This is probably an error
            int index = children.indexOf(activeChild);
            System.out.println("Null Node name for child " + Integer.toString(index) + " of Parent " + this);
            nextNode = null;
        } else {
            String previousNodeName = (activeChild == null) ? null : activeChild.getName();
            String nextNodeName = nextNodeName(previousNodeName);
            if (nextNodeName != null) {
                if (nextNodeName.equals("Secondary Knockback")) {
                    ActivationInfo ai = battleEvent.getActivationInfo();
                    String knockbackGroup = ai.getKnockbackGroup(getTargetGroup());
                    KnockbackNode node = new KnockbackNode("Secondary Knockback");
                    node.setTargetGroupSuffix("KB");
                    node.setKnockbackGroup(knockbackGroup);
                    nextNode = node;
                }
            }
        }

        return nextNode;
    }

    @Override
    public String nextNodeName(String previousNodeName) {
        String nextNodeName = null;

        if (previousNodeName == null) {
            ActivationInfo ai = battleEvent.getActivationInfo();

            // We might have to make this smarter...if the target was hit we could create this
            // node, but what we really want to know is if the target suffered any damage...
            if (buildSecondaryNode == true) {
                nextNodeName = "Secondary Knockback";
            }
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
    @Override
    public boolean processAdvance() {
        firstPass = false;

        return true;
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

    private void generateKnockbackDamage() throws BattleEventException {
        //
        ActivationInfo ai = battleEvent.getActivationInfo();

        int kbindex = battleEvent.getKnockbackIndex(target, knockbackGroup);

        KnockbackEffect kbeffect = battleEvent.getKnockbackEffect(kbindex);




        //Dice dice = battleEvent.getKnockbackDamageRoll(kbindex);
        int distance = battleEvent.getKnockbackDistance(kbindex);
        boolean knockdown = battleEvent.isKnockedDownPossible(kbindex);

        boolean breakfallPossible = false;
        boolean breakfallSuccessful = false;

        Target sourceTarget = null;
        int tindex;
        boolean hit;

        buildSecondaryNode = false; // Reset the secondary node

        sourceTarget = ai.getKnockbackSourceTarget(getTargetGroup());



        if (distance == 0 && knockdown == false) {
            battleEvent.addBattleMessage(new KnockbackSummaryMessage(sourceTarget, "was not knocked back or knocked down"));
        } else if (kbeffect == null || kbeffect == KnockbackEffect.NOEFFECT) {
            battleEvent.addBattleMessage(new KnockbackSummaryMessage(sourceTarget, "was knocked back " + Integer.toString(distance) + "\" with no effect"));
        } else {
            SkillRollInfo sri = battleEvent.getSkillRollInfo("BreakfallRoll", getTargetGroup());
            if (sri != null) {
                breakfallPossible = true;
                if (sri.isSuccessful()) {
                    battleEvent.addBattleMessage(new DiceRollMessage(sri.getSource(), sri.getSource().getName() + " " + sri.getShortDescription() + " skill roll succeeded.  Needed " + sri.getFinalRollNeeded() + ", rolled " + sri.getDiceRollInfo().getDice().getStun() + "."));
                } else {
                    battleEvent.addBattleMessage(new DiceRollMessage(sri.getSource(), sri.getSource().getName() + " " + sri.getShortDescription() + " skill roll failed.  Needed " + sri.getFinalRollNeeded() + ", rolled " + sri.getDiceRollInfo().getDice().getStun() + "."));
                }
            }

            // Dice can be null in cases where the breakfall roll succeeds.
            Dice dice = battleEvent.getDiceRoll(battleEvent.getDiceIndex("KBDamage", getTargetGroup()));

            if (dice != null && Preferences.getBooleanValue("DieRollMessages")) {
                String msg = sourceTarget + " rolled " + dice.toString() + " for knockback damage.";
                battleEvent.addBattleMessage(new DiceRollMessage(sourceTarget, msg));
            }

            boolean collisionOccurred = false;


            if (kbeffect.equals(KnockbackEffect.COLLISION) || ai.getKnockbackCollisionWithSecondaryTargetOccurred(getTargetGroup())) {
                collisionOccurred = true;
            }

            breakfallSuccessful = KnockbackTargetNode.isBreakfallSuccessful(battleEvent, getTargetGroup());

            if (distance > 0) {
                if (collisionOccurred) {
                    battleEvent.addBattleMessage(new KnockbackSummaryMessage(sourceTarget, "was knocked back " + Integer.toString(distance) + "\" with collision"));
                } else if (breakfallPossible == false) {
                    battleEvent.addBattleMessage(new KnockbackSummaryMessage(sourceTarget, "was knocked back " + Integer.toString(distance) + "\" with no collision"));
                } else {
                    if (breakfallSuccessful) {
                        battleEvent.addBattleMessage(new KnockbackSummaryMessage(sourceTarget, "was knocked back " + Integer.toString(distance) + "\" with no collision and breakfell successfully"));
                    } else {
                        battleEvent.addBattleMessage(new KnockbackSummaryMessage(sourceTarget, "was knocked back " + Integer.toString(distance) + "\" with no collision but failed to breakfall"));
                    }
                }
            } else {
                // distance == 0 was at worse we were knocked down...
                if (breakfallPossible == false) {
                    battleEvent.addBattleMessage(new KnockbackSummaryMessage(sourceTarget, "was not knocked back but was knocked down"));
                } else {
                    if (breakfallSuccessful) {
                        battleEvent.addBattleMessage(new KnockbackSummaryMessage(sourceTarget, "was not knocked back and breakfell successfully to avoid knock down"));
                    } else {
                        battleEvent.addBattleMessage(new KnockbackSummaryMessage(sourceTarget, "was not knocked back but failed breakfall"));
                    }
                }
            }

            IndexIterator ii = ai.getIteratorForIndex("Target", "TARGETGROUP", getTargetGroup());

            while (ii.hasNext()) {
                tindex = ii.nextIndex();
                Target aTarget = (Target) ai.getIndexedValue(tindex, "Target", "TARGET");

                if (dice != null && aTarget == sourceTarget && (collisionOccurred || breakfallSuccessful == false)) {
                    // Always hit the sourceTarget since he will always take the damage
                    // Remember, the damage die was adjusted previously, so this should be
                    // okay...
                    hit = true;
                } else {
                    hit = ai.getIndexedBooleanValue(tindex, "Target", "HIT");
                }

                if (hit) {
                    if (aTarget != sourceTarget) {
                        //battleEvent.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(sourceTarget.getName() + " collided with " + target.getName() + " while being knocked back.", MSG_COMBAT)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(sourceTarget.getName() + " collided with " + target.getName() + " while being knocked back.", MSG_COMBAT)); // .addMessage(sourceTarget.getName() + " collided with " + target.getName() + " while being knocked back.", MSG_COMBAT);

                        battleEvent.addBattleMessage(new KnockbackSummaryMessage(sourceTarget, "collided with " + aTarget.getName()));
                    }

                    Effect effect = new effectKnockback(distance);
                    // Add Effects
                    effect.addDamageSubeffect("KBStun", "STUN", dice.getStun(), "PD", "NORMAL");
                    effect.addDamageSubeffect("KBBody", "BODY", dice.getBody(), "PD", "NORMAL");

                    effect.add("Effect.DOESKB", (aTarget == sourceTarget) ? "FALSE" : "TRUE");

                    if (aTarget != sourceTarget) {
                        buildSecondaryNode = true;
                    }

                    try {
                        int targetReferenceNumber = ai.getTargetReferenceNumber(tindex);
                        BattleEngine.applyEffectToTarget(battleEvent, targetReferenceNumber, getTargetGroup(), effect, true);


                    } catch (BattleEventException bee) {
                        getModel().setError(bee);
                    }
                }


                if (aTarget == sourceTarget && breakfallSuccessful == false) {
                    Effect kd = new effectKnockedDown();
                    BattleEngine.addEffect(battleEvent, kd, aTarget);
                }
            }
        }
    }

    @Override
    public String getAutoBypassOption() {
        return "SHOW_KNOCKBACK_APPLY_EFFECTS_PANEL";
    }

    @Override
    public Target getAutoBypassTarget() {
        return getTarget();
    }
}
