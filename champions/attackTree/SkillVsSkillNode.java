/*
 * SkillVsSkillNode.java
 *
 * Created on June 5, 2002, 3:27 PM
 */
package champions.attackTree;

import champions.Dice;
import champions.Target;
import champions.battleMessage.DiceRollMessage;
import champions.battleMessage.SimpleBattleMessage;
import javax.swing.UIManager;

/**
 *
 * @author  Trevor Walker
 */
public class SkillVsSkillNode extends DefaultAttackTreeNode {

    /** Holds value of property challenger. */
    private Target challenger;
    /** Holds value of property challengee. */
    private Target challengee;
    /** Holds value of property challengerDiceCount. */
    private int challengerDiceCount;
    /** Holds value of property challengeeDiceCount. */
    private int challengeeDiceCount;
    /** Holds value of property description. */
    private String description;
    /** Holds value of property description. */
    private String skillName;
    private boolean firstPass = true;
    
    /** Creates new SkillVsSkillNode */
    public SkillVsSkillNode(String name) {
        this.name = name;
        icon = UIManager.getIcon("AttackTree.toHitIcon");
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

        prepareBattleEvent();

        if (manualOverride || nodeRequiresInput()) {

            SkillVsSkillPanel ep = SkillVsSkillPanel.getDefaultPanel(battleEvent, description, skillName,
                    challenger, challengee, challengerDiceCount, challengeeDiceCount);
            attackTreePanel.showInputPanel(this, ep);
            attackTreePanel.setInstructions("Enter Rolls for " + skillName);

            acceptActivation = true;
        }
        //  }
        if (AttackTreeModel.DEBUG > 0) {
            System.out.println("Node " + name + " activated.");
        }
        return acceptActivation;
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
        int sindex = battleEvent.findSkillChallenge(description, skillName, challenger, challengee);

        Dice challengeeDice = battleEvent.getSkillChallengeChallengeeDice(sindex);
        if (challengeeDice == null) {
            challengeeDice = new Dice(challengeeDiceCount, true);
            battleEvent.setSkillChallengeChallengeeDice(sindex, challengeeDice);
            battleEvent.setSkillChallengeChallengeeAutoroll(sindex, true);
        }

        Dice challengerDice = battleEvent.getSkillChallengeChallengerDice(sindex);
        if (challengerDice == null) {
            challengerDice = new Dice(challengerDiceCount, true);
            battleEvent.setSkillChallengeChallengerDice(sindex, challengerDice);
            battleEvent.setSkillChallengeChallengerAutoroll(sindex, true);
        }

        battleEvent.addBattleMessage(new SimpleBattleMessage(challenger, challenger.getName() +
                " is attempting a " + name + " against " + challengee.getName()));
        battleEvent.addBattleMessage(new DiceRollMessage(challenger, challenger.getName() +
                " rolled " + challengerDice.getBody() + " Body and " + challengee.getName() +
                " rolled " + challengeeDice.getBody() + " Body."));

        if (challengerDice.getBody().intValue() > challengeeDice.getBody().intValue()) {
            // Challenger Won
            int margin = challengerDice.getBody().intValue() - challengeeDice.getBody().intValue();
            battleEvent.addBattleMessage(new DiceRollMessage(challenger, challenger.getName() +
                    " won the " + name + " challenge by " + Integer.toString(margin) + "."));
            battleEvent.setSkillChallengeWinner(sindex, challenger, challengerDice.getBody().intValue(),
                    challengeeDice.getBody().intValue());
        } else {
            // Challengee Won
            int margin = challengeeDice.getBody().intValue() - challengerDice.getBody().intValue();
            battleEvent.addBattleMessage(new DiceRollMessage(challenger, challengee.getName() +
                    " won the " + name + " challenge by " + Integer.toString(margin) + "."));
            battleEvent.setSkillChallengeWinner(sindex, challengee, challengeeDice.getBody().intValue(),
                    challengerDice.getBody().intValue());
        }

        firstPass = false;

        if (AttackTreeModel.DEBUG > 0) {
            System.out.println("Node " + name + " exited.");
        }

        return true;
    }

    /** Determines if this node needs to show an input panel.
     *
     */
    protected boolean nodeRequiresInput() {

        return firstPass && getAutoBypassTarget().getBooleanProfileOption(getAutoBypassOption());
    }

    public String getAutoBypassOption() {
        return "SHOW_ESCAPE_PANEL";
    }

    public Target getAutoBypassTarget() {
        return challenger;
    }

    private void prepareBattleEvent() {
        int sindex = battleEvent.addSkillChallenge(description, skillName, challenger, challengee);
        // Make sure the Dice Sizes haven't changed.
        Dice challengeeDice = battleEvent.getSkillChallengeChallengeeDice(sindex);
        if (challengeeDice != null && challengeeDice.getD6() != challengeeDiceCount) {
            // The dice size did change, so clear it...
            battleEvent.setSkillChallengeChallengeeDice(sindex, null);
        }

        Dice challengerDice = battleEvent.getSkillChallengeChallengerDice(sindex);
        if (challengerDice != null && challengerDice.getD6() != challengerDiceCount) {
            // The dice size did change, so clear it...
            battleEvent.setSkillChallengeChallengerDice(sindex, null);
        }
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

    /** Getter for property description.
     * @return Value of property description.
     */
    public String getDescription() {
        return this.description;
    }

    /** Setter for property description.
     * @param description New value of property description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /** Getter for property skillName.
     * @return Value of property skillName.
     */
    public String getSkillName() {
        return this.skillName;
    }

    /** Setter for property skillName.
     * @param skillName New value of property skillName.
     */
    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    /** Getter for property challengeeDiceCount.
     * @return Value of property challengeeDiceCount.
     */
    public int getChallengeeDiceCount() {
        return this.challengeeDiceCount;
    }

    /** Setter for property challengeeDiceCount.
     * @param challengeeDiceCount New value of property challengeeDiceCount.
     */
    public void setChallengeeDiceCount(int challengeeDiceCount) {
        this.challengeeDiceCount = challengeeDiceCount;
    }

    /** Getter for property challengerDiceCount.
     * @return Value of property challengerDiceCount.
     */
    public int getChallengerDiceCount() {
        return this.challengerDiceCount;
    }

    /** Setter for property challengerDiceCount.
     * @param challengerDiceCount New value of property challengerDiceCount.
     */
    public void setChallengerDiceCount(int challengerDiceCount) {
        this.challengerDiceCount = challengerDiceCount;
    }
}
