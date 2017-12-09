/*
 * SkillRollNode2.java
 *
 *  This node resolves skill rolls using the new SkillRollInfo 
 *  information contained in the battleEvent.  
 *
 *  Prior to instantiating this node, the SkillRollInfo should be added
 *  to the battleEvent.
 *
 *  This node (and its associated panel) will resolve the skill roll.  After
 *  this node has been evaluated, the SkillRollInfo's isSuccessful() can be
 *  used to determine the success or failure of the skill roll.
 *
 * Created on November 9, 2001, 4:40 PM
 */

package champions.attackTree;


import champions.SkillRollInfo;
import champions.Target;
import champions.battleMessage.DiceRollMessage;

import champions.exception.BattleEventException;
import javax.swing.UIManager;

/**
 *
 * @author  twalker
 * @version
 */
public class SkillRollNode2 extends DefaultAttackTreeNode {

    /** Holds value of property diceName. */
    private String skillRollName;
    private boolean printOutcome = true;

    
    /** Creates new ToHitNode */
    public SkillRollNode2(String name) {
        this.name = name;
        icon = UIManager.getIcon("AttackTree.diceIcon");
    }
    
    /** Creates new ToHitNode */
    public SkillRollNode2(String name, String skillRollName) {
        this.name = name;
        icon = UIManager.getIcon("AttackTree.diceIcon");
        setSkillRollName(skillRollName);
    }
    
    private void setupNode() {
        
    }
    
//   /* private void updateSkillRoll() {
//        Ability ability = battleEvent.getAbility();
//        ActivationInfo ai = battleEvent.getActivationInfo();
//        Target source = battleEvent.getSource();
//        Integer usedsl = new Integer(0);
//        Ability effectAbility;
//        Integer level = new Integer(0);
//        Integer allocatesl = new Integer(0);
//        Integer skillbonus = new Integer(0);
//        int count, i;
//        
//        int sbtot = 0;
//        //Effect effectsl = source.getEffect("Skill Levels");
//        
//        if ( ai.isActivated() == false ) {
//            //          battleEvent.addAIStateEvent(ai, ai.getState(), "ACTIVATED" );
//            //            ai.setState("ACTIVATED");
//            //            //ability.add("Ability.ACTIVATED", "TRUE",  true);
//            //            //be.addActivateEvent( ability, true );
//            //
//            // Run through the source Effects to let them know that Ability is activating
//            // First build list, then run through list (just in case it changes).
//            count = source.getIndexedSize("Effect");
//            Effect[] sourceEffects = new Effect[count];
//            for (i=0;i<count;i++) {
//                sourceEffects[i] = (Effect)source.getIndexedValue( i,"Effect","EFFECT" );
//            }
//            
//            for (i=0;i<count;i++) {
//                if ( source.findIndexed( "Effect","EFFECT",sourceEffects[i]) != -1 ) {
//                    sourceEffects[i].skillIsActivating(battleEvent,ability);
//                    skillbonus = sourceEffects[i].getIntegerValue("Effect.SKILLLEVEL");
//                    usedsl = (Integer)sourceEffects[i].getValue("Effect.USEDSL");
//                    effectAbility = (Ability)sourceEffects[i].getValue("Effect.ABILITY");
//                    if (effectAbility != null) {
//                        level = effectAbility.getIntegerValue("Power.SKILLLEVEL");
//                        allocatesl = effectAbility.getIntegerValue("CombatLevel.ALLOCATESL");
//                    }
//                    if (level == null) level = new Integer(0);
//                    if (usedsl == null) usedsl = new Integer(0);
//                    if (skillbonus != null) {
//                        if (level.intValue() >= (usedsl.intValue() + skillbonus.intValue())) {
//                            sbtot = sbtot + skillbonus.intValue();
//                            usedsl = new Integer(usedsl.intValue() + skillbonus.intValue());
//                        }
//                        else {
//                            sbtot = sbtot + (level.intValue() - usedsl.intValue());
//                            usedsl = new Integer(level.intValue());
//                        }
//                    }
//                }
//            }
//        }
//        
//        //below is a quick hack to force the effectroll for dependence to take the place
//        //of the skillroll.  WHen skillrolls are revamped this should be taken into
//        //account in the design
//        int skillroll = skill.getSkillRoll(target);
//        if (skillroll < 0) {
//            skillroll = getEffectRoll();
//        }
//        if (skillroll < 3) {
//            skillroll = 3;
//        }
//        
//        neededLabel.setText( Integer.toString( sbtot + skillroll + skillModifier.getValue().intValue()) );
//    } */
    
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
        boolean fixed = false;
        String reason = null;
        
        setupNode();
        
        SkillRollInfo sri = battleEvent.getSkillRollInfo(skillRollName, getTargetGroup());
        
        if ( manualOverride || nodeRequiresInput() ) {

            SkillRollPanel2 app = SkillRollPanel2.getDefaultPanel( battleEvent, sri);
            
            attackTreePanel.showInputPanel(this,app);
            attackTreePanel.setInstructions("Enter Skill Roll for " + sri.getShortDescription() + "...");
            
            acceptActivation = true;
        }
        
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println("Node " + name + " activated.");
        return acceptActivation;
    }
    
    
    
    public String nextNodeName(String previousNodeName) {
        String nextNodeName = null;
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
    public boolean processAdvance() throws BattleEventException{
        if ( AttackTreeModel.DEBUG > 0 ) System.out.println( "Node " + name + " exited.");
        
        SkillRollInfo sri = battleEvent.getSkillRollInfo(skillRollName, getTargetGroup());
        
        String msg = sri.getSource().getName() + " rolled " + sri.getDiceRollInfo().getDice() + " for " + sri.getShortDescription() + " skill roll.";
        //battleEvent.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( sri.getSource().getName() + " rolled " + sri.getDiceRollInfo().getDice() + " for " + sri.getShortDescription() + " skill roll.", BattleEvent.MSG_DICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( sri.getSource().getName() + " rolled " + sri.getDiceRollInfo().getDice() + " for " + sri.getShortDescription() + " skill roll.", BattleEvent.MSG_DICE)); // .addMessage( sri.getSource().getName() + " rolled " + sri.getDiceRollInfo().getDice() + " for " + sri.getShortDescription() + " skill roll.", BattleEvent.MSG_DICE);
        battleEvent.addBattleMessage( new DiceRollMessage(sri.getSource(), msg));
        
        if ( printOutcome ) {
            if ( sri.isSuccessful() ) {
                msg = sri.getSource().getName() + " " + sri.getShortDescription() +  " skill roll succeeded.  Needed " + sri.getFinalRollNeeded() + ", rolled " + sri.getDiceRollInfo().getDice().getStun() + ".";
                battleEvent.addBattleMessage( new DiceRollMessage(sri.getSource(), msg));
            }
            else {
                msg = sri.getSource().getName() + " " + sri.getShortDescription() +  " skill roll failed.  Needed " + sri.getFinalRollNeeded() + ", rolled " + sri.getDiceRollInfo().getDice().getStun() + ".";
                battleEvent.addBattleMessage( new DiceRollMessage(sri.getSource(), msg));
            }
        }
        
        return true;
    }
    
    /** Determines if this node needs to show an input panel.
     *
     */
    protected  boolean nodeRequiresInput() {
        boolean requiresInput = false;
        
        return getAutoBypassTarget().getBooleanProfileOption(getAutoBypassOption());
    }
    
    public String getAutoBypassOption() {
        return "SHOW_SKILL_PANEL";
    }
    
    public Target getAutoBypassTarget() {
        return battleEvent.getSource();
    }
    
    private void prepareBattleEvent() {
    }
    

    public String getSkillRollName() {
        return skillRollName;
    }

    public void setSkillRollName(String skillRollName) {
        this.skillRollName = skillRollName;
    }

    public boolean isPrintOutcome() {
        return printOutcome;
    }

    public void setPrintOutcome(boolean printOutcome) {
        this.printOutcome = printOutcome;
    }
}
