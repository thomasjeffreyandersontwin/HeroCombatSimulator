/*
 * EscapeTriggerNode.java
 *
 * Created on June 5, 2002, 3:24 PM
 */
package champions.attackTree;

import champions.Target;
import champions.exception.BattleEventException;
import champions.powers.maneuverEscape;
import champions.powers.effectGrabbed;
import champions.interfaces.AbilityIterator;
import champions.battleMessage.GenericSummaryMessage;
import champions.BattleEngine;
import champions.Ability;
import champions.BattleEvent;

/**
 *
 * @author Trevor Walker
 * @author Patrick Noffke
 */
public class EscapeTriggerNode extends DefaultAttackTreeNode {

    /** Holds value of property challenger. */
    private Target challenger;
    /** Holds value of property challengee. */
    private Target challengee;
    /** Holds value of property challengerStrength. */
    private int challengerStrength;
    /** Holds value of property challengeeStrength. */
    private int challengeeStrength;

    private boolean followUpWithFullStrContest = false;
    /** Holds value of property grabEffect. */
    private effectGrabbed grabEffect;
    /** Holds ability we're trying to escape from. */
    private Ability challengeeAbility;

    /** Creates new EscapeTriggerNode */
    public EscapeTriggerNode(String name) {
        this.name = name;

        setVisible(false);
    }

    public boolean activateNode(boolean manualOverride) {
        boolean activateNode = false;

        prepareBattleEvent();

        return activateNode;
    }

    private void prepareBattleEvent() {
        calculateChallengeeStrength();
        calculateChallengerStrength();
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
        return true;
    }

    public AttackTreeNode buildNextChild(AttackTreeNode activeChild) {

        AttackTreeNode nextNode = null;
        // Compare the activeChild to the possible children, to determine what comes next.
        if (activeChild != null && activeChild.getName() == null) {
            // This is probably an error
            int index = children.indexOf(activeChild);
            System.out.println("Null Node name for child " + Integer.toString(index) + " of Parent " + this);
            nextNode = null;
        } else {
            String previousNodeName = (activeChild == null ) ? null : activeChild.getName();
            String nextNodeName = nextNodeName(previousNodeName);
            if ( nextNodeName != null ) {
                if ( nextNodeName.equals("Casual Strength vs. Strength") ) {
                    SkillVsSkillNode node = new SkillVsSkillNode("Casual Strength vs. Strength");
                    node.setSkillName("Strength");
                    node.setDescription("Casual Strength");
                    node.setChallengee(challengee);
                    node.setChallenger(challenger);
                    node.setChallengeeDiceCount((int) Math.round((double) challengeeStrength / 5.0));
                    node.setChallengerDiceCount((int) Math.round((double) challengerStrength / 5.0 / 2.0));
                    nextNode = node;
                }
                else if ( nextNodeName.equals("Strength vs. Strength") ) {
                    SkillVsSkillNode node = new SkillVsSkillNode("Strength vs. Strength");
                    node.setSkillName("Strength");
                    node.setDescription("Strength");
                    node.setChallengee(challengee);
                    node.setChallenger(challenger);
                    node.setChallengeeDiceCount((int) Math.round((double) challengeeStrength / 5.0));
                    node.setChallengerDiceCount((int) Math.round((double) challengerStrength / 5.0));
                    nextNode = node;
                }
            }
        }
        return nextNode;
    }

    public String nextNodeName(String previousNodeName) {
        String nextNodeName = null;

        if ( previousNodeName == null ) {
            nextNodeName = "Casual Strength vs. Strength";
        } else if ( previousNodeName.equals("Casual Strength vs. Strength") && followUpWithFullStrContest) {
            // Must see if the challenger failed the casual str contest.
            int sindex = battleEvent.findSkillChallenge("Casual Strength", "Strength", challenger, challengee);
            if (sindex != -1 && battleEvent.getSkillChallengeWinner(sindex) == challengee) {
                nextNodeName = "Strength vs. Strength";
            }
        }

        return nextNodeName;
    }

    private void calculateChallengerStrength() {
        // Find the escape with the highest strength.
        AbilityIterator it = challenger.getAbilities();
        // Need this because getName is not static.
        maneuverEscape dummyEscape = new maneuverEscape();
        Ability escapeAbility = null;
        challengerStrength = 0;
        while (it.hasNext())
        {
            Ability ability = it.next();
            if (ability.getPower().getName().equals(dummyEscape.getName()))
            {
                if (ability.isEnabled(challenger, false))
                {
                    int abilityStrength = calculateStrengthFromAbility(ability, challenger, challengee);
                    if (abilityStrength > challengerStrength)
                    {
                        escapeAbility = ability;
                        challengerStrength = abilityStrength;
                    }
                }
            }
        }
        if (escapeAbility == null)
        {
            // Use the challenger's base STR.
            if (challenger.hasStat("STR"))
            {
                challengerStrength = challenger.getCurrentStat("STR");
            }
        }
        else {
            getBattleEvent().addBattleMessage( new GenericSummaryMessage(challenger, " has a maximum escape STR of " +
                    Integer.toString(challengerStrength) + " from " + escapeAbility.getName()));
        }
    }

    private void calculateChallengeeStrength() {
        // Calculate the strength using the ability that we're trying to escape from.
        challengeeStrength = calculateStrengthFromAbility(challengeeAbility, challengee, challenger);
        getBattleEvent().addBattleMessage( new GenericSummaryMessage(challengee, " has a STR of " +
                Integer.toString(challengeeStrength) + " from " + challengeeAbility.getName()));
    }

    private int calculateStrengthFromAbility(Ability ability, Target source, Target target)
    {
        int sourceStrength = 0;
        if (source != null && target != null) {
            if (source.hasStat("STR")) {
                BattleEvent be = new BattleEvent(ability, false);
                be.setSource(source);
                be.setTarget(target);
                // Target group can be anything.
                be.getActivationInfo().addTargetGroup("DUMMY");
                BattleEngine.setupAttackParameters(be);
                try {
                    BattleEngine.calculateDamage(be, source, "DUMMY");
                } catch (BattleEventException exception) {
                }
                double challengeeDC = be.getDC();
                sourceStrength = (int) Math.round(challengeeDC * 5.0);
            }
        }
        return sourceStrength;
    }

    /** Getter for property grabEffect.
     * @return Value of property grabEffect.
     */
    public effectGrabbed getGrabEffect() {
        return this.grabEffect;
    }

    /** Setter for property grabEffect.
     * @param grabEffect New value of property grabEffect.
     */
    public void setGrabEffect(effectGrabbed grabEffect) {
        this.grabEffect = grabEffect;
    }

    public void setFollowUpWithFullStrContest(boolean followUp) {
        this.followUpWithFullStrContest = followUp;
    }

    public boolean getFollowUpWithFullStrContest() {
        return followUpWithFullStrContest;
    }

    /** Getter for property challenger.
     * @return Value of property challenger.
     */
    public Target getChallenger() {
        return this.challenger;
    }

    /** Setter for property challenger.
     * @param challenger New value of property challenger.
     */
    public void setChallenger(Target challenger) {
        this.challenger = challenger;
    }

    /** Getter for property challengee.
     * @return Value of property challengee.
     */
    public Target getChallengee() {
        return this.challengee;
    }

    /** Setter for property challengee.
     * @param challengee New value of property challengee.
     */
    public void setChallengee(Target challengee) {
        this.challengee = challengee;
    }

    /** Getter for property challengeeAbility.
     * @return Value of property challengeeAbility.
     */
    public Ability getChallengeeAbility() {
        return this.challengeeAbility;
    }

    /** Setter for property challengeeAbility.
     * @param challengeeAbility New value of property challengeeAbility.
     */
    public void setChallengeeAbility(Ability ability) {
        this.challengeeAbility = ability;
    }
}
