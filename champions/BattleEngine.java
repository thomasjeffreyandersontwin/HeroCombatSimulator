/*
 * BattleEngine.java
 *
 * Created on September 17, 2000, 11:04 AM
 */
package champions;    

import champions.attackTree.AttackProcessNode;
import champions.attackTree.AttackTreeModel;
import champions.attackTree.AttackTreeNode;
import champions.attackTree.CombinedActivateRootNode;
import champions.attackTree.CombinedDeactivateRootNode;
import champions.attackTree.ConfigureBattleRootNode;
import champions.attackTree.GenericAbilityRootNode;
import champions.attackTree.GenericAttackTreeNode;
import champions.attackTree.LinkedActivateRootNode;
import champions.attackTree.ProcessActivateRootNode;
import champions.attackTree.ProcessDeactivateRootNode;
import champions.attackTree.SweepActivateRootNode;
import champions.battleMessage.ActivateAbilityMessage;
import champions.battleMessage.ActivateAbilityMessageGroup;
import champions.battleMessage.BattleMessageGroup;
import champions.battleMessage.DefaultAttackMessage;
import champions.battleMessage.DefenseMessage;
import champions.battleMessage.HitLocationMessage;
import champions.battleMessage.SimpleBattleMessage;
import champions.battleMessage.SummaryMessageGroup;
import champions.enums.DefenseType;
import champions.exception.BattleEventException;
import champions.exceptionWizard.ExceptionWizard;
import champions.interfaces.AbilityIterator;
import champions.interfaces.Advantage;
import champions.interfaces.ChampionsConstants;
import champions.interfaces.CombatLevelProvider;
import champions.interfaces.ENDSource;
import champions.interfaces.IndexIterator;
import champions.interfaces.Limitation;
import champions.interfaces.Sequencer;
import champions.interfaces.Undoable;
import champions.parameters.ParameterList;
import champions.powers.SizeModifierEffect;
import champions.powers.advantageAutofire;
import champions.powers.advantageReducedEndurance;
import champions.powers.effectBlock;
import champions.powers.effectCombatLevel;
import champions.powers.effectCombatModifier;
import champions.powers.effectDeflection;
import champions.powers.effectEND;
import champions.powers.effectInterruptible;
import champions.powers.effectNoHitLocations;
import champions.powers.effectStacked;
import champions.powers.powerCharacteristic;
import champions.powers.powerPass;
import champions.undoableEvent.ActivationInfoLastTimeProcessUndoable;
import champions.undoableEvent.EffectUndoable;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import VirtualDesktop.Character.Charasteristic;

/** Processes BattleEvent placed into the BattleEvent queue.
 *
 * Each BattleEvent goes through a number of states, as it is processed.  The state is actually
 * stored with the ActivationInfo and is persistent between battleEvent.  However, the ActivationInfo
 * state determines what is down with the event at every step.
 *
 * The States are as Follows:
 *
 * <PRE>
 *                       NEW
 *                        |
 *                     PREDELAY
 *                      |   \
 *                      |   ABILITY_DELAYED
 *                      |    |
 *               WAIT_FOR_ABILITY_TRIGGER
 *                         |
 *                  ABILITY_TRIGGERED------------\
 *                   \     |    |                 \
 *                 PREACTIVATE_ABILITY             \
 *                   /     |     \                  \
 *                   |     |  MANEUVER_DELAYED       \
 *                   |     |      |                   |
 *                   |    MANEUVER_TRIGGERED----------|
 *                   |            |                   |
 *                   |    PREACTIVATE_MANEUVER        |
 *                   |     |                          |
 *                   |     |                          |
 *                  ACTIVATED                 ACTIVATION_FAILED
 *                      |                           /
 *                  CONTINUING         /----------/
 *                      |            /
 *                DEACTIVATING     /
 *                       \       /
 *                      DEACTIVATED
 *
 * </PRE>
 *
 * Generally, for each state there is a method called proccessState<STATE> which will do everyting
 * necessary to process a BattleEvent in that particular State.
 *
 * The exception to this rule is the method processDeactivating.  ProcessDeactivating should do everything
 * necessary to shutdown a BattleEvent, no matter what state it is in.  However, it does not charge end.
 * When END is necessary the method processFinishedActivating should be used.  It will properly charge END
 * and shut down the power, as necessary.
 *
 * The DEACTIVATING state is used in reenterant Continuous Abilities which should be deactivated
 * prior to END being charged.
 *
 * The method processFinishing is also of note.  After a battleEvent is processed, processFinishing should
 * be called to charge end and do various other cleanup functions.  Generally, processFinishing takes care
 * of calling processDeactiving, if it is necessary.
 *
 * @author  unknown
 * @version
 */
public class BattleEngine extends Thread
        implements ChampionsConstants {

    /** Holds value of property battle. */
    private Battle battle;
    /** Holds value of property battleEventList. */
    private BattleEventList battleEventList;
    private boolean terminate = false;
    private boolean processing;
    /** Holds value of property frame. */
    private JFrame frame;
    private static InlineView inlineView;
    private static ExceptionHandler exceptionHandler = new ExceptionHandler();
    public static final int STATUS_CANCEL = 0;
    public static final int STATUS_SUCCESS = 1;
    private static final Object[][] HLKillingStunMultipliers = {
        {"HEAD", new Double(5)},
        {"HANDS", new Double(1)},
        {"ARMS", new Double(2)},
        {"SHOULDERS", new Double(3)},
        {"CHEST", new Double(3)},
        {"STOMACH", new Double(4)},
        {"VITALS", new Double(4)},
        {"THIGHS", new Double(2)},
        {"LEGS", new Double(2)},
        {"FEET", new Double(1)}
    };
    private static final Object[][] HLNormalStunMultipliers = {
        {"HEAD", new Double(2)},
        {"HANDS", new Double(0.5)},
        {"ARMS", new Double(0.5)},
        {"SHOULDERS", new Double(1)},
        {"CHEST", new Double(1)},
        {"STOMACH", new Double(1.5)},
        {"VITALS", new Double(1.5)},
        {"THIGHS", new Double(1)},
        {"LEGS", new Double(0.5)},
        {"FEET", new Double(1)}
    };
    private static final Object[][] HLBodyMultipliers = {
        {"HEAD", new Double(2)},
        {"HANDS", new Double(0.5)},
        {"ARMS", new Double(0.5)},
        {"SHOULDERS", new Double(1)},
        {"CHEST", new Double(1)},
        {"STOMACH", new Double(1)},
        {"VITALS", new Double(2)},
        {"THIGHS", new Double(1)},
        {"LEGS", new Double(0.5)},
        {"FEET", new Double(0.5)}
    };
    private long startTime = 0;
    private static final int DEBUG = 0;

    /** Creates new BattleEngine */
    public BattleEngine(String name, Battle b) {
        super(name);

        battle = b;

        battleEventList = new BattleEventList();

        //hitDialog = new HitDialog2(frame);
        //selectTargetDialog = new SelectTargetDialog3(frame,battle);
        // kbDialog = new KnockbackDialog(frame);
        //  diceDialog = new DiceDialog(frame);

        inlineView = InlineView.getDefaultInlineView();

        this.start();
    }

    /**
     *
     */
    public synchronized void run() {
        BattleEvent be;
        while (terminate == false) {
            // Wait to start processing
            while (processing == false && terminate == false) {
                try {

                		wait();
                	
                } catch (InterruptedException e) {
                }
            }

            if (terminate == false) {
                while (battleEventList.hasEvents()) {
                    if (DEBUG >= 2) {
                        startTime = System.currentTimeMillis();
                    }
                    try {
                        be = battleEventList.getEvent();
                        if (be.getType() != BattleEvent.ADVANCE_TIME) {
                            battle.addCompletedEvent(be);
                        }
                        try {
                            if (DEBUG >= 1) {
                                System.out.println("Processing Event: " + be.toString());
                            }
                            if (processEvent(be)) {
                                battle.addEvent(new BattleEvent(BattleEvent.ADVANCE_TIME, false));
                            }
                        } catch (BattleEventException bee) {
                            battle.removeCompletedEvent(be);
                            be.setError(bee.toString());
                            if (DEBUG >= 1) {
                                System.out.println(bee.toString());
                            }
                            //c cleanAbility( be );
                            be.rollbackBattleEvent();
                            cleanBattleEventAfterFailure(be);

                            if (bee.isDisplayError()) {
                                displayBattleError(bee);
                            }
                        }
                    } catch (Throwable thrown) {
                        exceptionHandler.handle(thrown);
                    }
                    if (DEBUG >= 2) {
                        System.out.println("Process Event Time: " + Long.toString(System.currentTimeMillis() - startTime));
                    }
                }

                processing = false;
                notifyAll();
                battle.triggerProcessingNotify(processing);
            }
        }

    }

    public synchronized void waitForProcessingToFinish() {
        while (processing) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
    }

    public synchronized void terminateEngine() {
        terminate = true;

        notifyAll();
    }

    /**
     * @param be
     * @throws BattleEventException
     * @return
     */
    public boolean processEvent(BattleEvent be) throws BattleEventException {
        Chronometer time = (Chronometer) battle.getTime().clone();
        be.setTimeProcessed(time);
       
        if (be.getType() == BattleEvent.ACTIVATE) {
            processAbility(be);
        } else if (be.getType() == BattleEvent.DEACTIVATE) {
            processAbility(be);
        } else if (be.getType() == BattleEvent.CONTINUE) {
            processAbility(be);
        } else if (be.getType() == BattleEvent.CHARGE_END) {
            processAbility(be);
        } else if (be.getType() == BattleEvent.DELAYED_ACTIVATE) {
            be.addBattleMessage(new champions.battleMessage.LegacyBattleMessage(be.getSource().getName() + "'s " + be.getAbility().getInstanceName() + " finished activating.", BattleEvent.MSG_ABILITY)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( be.getSource().getName() + "'s " + be.getAbility().getInstanceName() + " finished activating.", BattleEvent.MSG_ABILITY)); // .addMessage( be.getSource().getName() + "'s " + be.getAbility().getInstanceName() + " finished activating.", BattleEvent.MSG_ABILITY);
            if (be.getEffect() != null) {
                be.getEffect().removeEffect(be, be.getSource());
            }
            processAttackTreeNode(new ProcessActivateRootNode("Delayed Activate Root", be));
        } else if (be.getType() == BattleEvent.TRIGGER) {
            Target t = be.getSource();
            Ability a = be.getAbility();
            be.addBattleMessage(new champions.battleMessage.LegacyBattleMessage(t.getName() + " triggers " + a.getInstanceName() + " (Time Delayed, Triggered, or Delayed Effect Power)", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( t.getName() + " triggers " + a.getInstanceName() + " (Time Delayed, Triggered, or Delayed Effect Power)", BattleEvent.MSG_NOTICE)); // .addMessage( t.getName() + " triggers " + a.getInstanceName() + " (Time Delayed, Triggered, or Delayed Effect Power)", BattleEvent.MSG_NOTICE);
            processAttackTreeNode(new ProcessActivateRootNode("Triggered Root", be));
        } else if (be.getType() == BattleEvent.MANEUVER) {
            processAttackTreeNode(new ProcessActivateRootNode("Maneuver Root", be));
        } else if (be.getType() == BattleEvent.ADVANCE_TIME) {
            advanceSegment(be.isForcedAdvance(), be.getTimeParameter());
            return false;
        } else if (be.getType() == BattleEvent.ADD_EFFECT) {
            Effect e = be.getEffect();
            Target t = be.getTarget();

            if (e != null && t != null) {
                addEffect(be, e, t);
            }
        } else if (be.getType() == BattleEvent.REMOVE_EFFECT) {
            Effect e = be.getEffect();
            Target t = be.getTarget();

            if (e != null && t != null) {
                e.removeEffect(be, t);
            }

            Battle.fireChangeEvent(BattleChangeType.EFFECT_REMOVED, t);
        } else if (be.getType() == BattleEvent.ACTIVE_TARGET) {
            Target t = be.getTarget();
            if (battle.getActiveTarget() != t) {
                be.addActiveTargetEvent(battle.getActiveTarget(), t);
                be.addBattleMessage(new champions.battleMessage.LegacyBattleMessage(t.getName() + " is now Active.", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( t.getName() + " is now Active.", BattleEvent.MSG_NOTICE)); // .addMessage( t.getName() + " is now Active.", BattleEvent.MSG_NOTICE);
                battle.setSelectedTarget(t);
                doPrephase(be);
                // battle.addCompletedEvent(be);

                return false; // Don't advance segment at all
            } else {
                throw new BattleEventException(t.getName() + " was already active target.");
            }
        } else if (be.getType() == BattleEvent.HOLD_TARGET) {
            processHold(be);
        } else if (be.getType() == BattleEvent.PASS_TARGET) {
            Target t = be.getTarget();
            if (t.getCombatState() == CombatState.STATE_ACTIVE || t.getCombatState() == CombatState.STATE_HELD || t.getCombatState() == CombatState.STATE_HALFHELD) {
                be.addCombatStateEvent(t, t.getCombatState(), CombatState.STATE_FIN);
                t.setCombatState(CombatState.STATE_FIN);
            }
            be.addBattleMessage(new champions.battleMessage.LegacyBattleMessage(t.getName() + " passes.", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( t.getName() + " passes.", BattleEvent.MSG_NOTICE)); // .addMessage( t.getName() + " passes.", BattleEvent.MSG_NOTICE);
        } else if (be.getType() == BattleEvent.ACTION) {
            be.getAction().actionPerformed(new ActionEvent(be, BattleEvent.ACTION, "Action"));
        } else if (be.getType() == BattleEvent.LINKED_ACTIVATE) {
            processLinkedActivate(be);
        } else if (be.getType() == BattleEvent.GENERIC_ABILITY_ACTIVATE) {
            processAttackTreeNode(new GenericAbilityRootNode("Generic Ability Root", be));
        } else if (be.getType() == BattleEvent.REMOVE_DELAYED_EVENT) {
            BattleEvent eventToRemove = be.getBattleEventToRemove();
            if (eventToRemove != null) {
                be.addUndoableEvent(Battle.getCurrentBattle().removeDelayedEvent(eventToRemove));
                if (eventToRemove.getEffect() != null) {
                    eventToRemove.getEffect().removeEffect(be, eventToRemove.getEffect().getTarget());
                }
            }

        } else if (be.getType() == BattleEvent.ADD_ABILITY) {
            // Don't do anything
        } else if (be.getType() == BattleEvent.CONFIGURE_BATTLE) {
            processAttackTreeNode(new ConfigureBattleRootNode("ConfigureBattleRootNode", be));
        } else if (be.getType() == BattleEvent.REPROCESS_EVENT) {
            ReprocessBattleEvent rbe = (ReprocessBattleEvent) be;

            BattleEvent obe = rbe.getOriginalEvent();

            battle.undoCompleteEvent(obe);
            battle.addCompletedEvent(obe);

            obe.setReprocessingEvent(true);
            obe.resetBattleEvent();

            processEvent(obe);
        } else if (be instanceof RunnableBattleEvent) {
            ((RunnableBattleEvent) be).processEvent();
        } else {
            if (battle.DEBUG >= 2) {
                throw new BattleEventException("Unknown battle event type: \n" + be.toLongString());
            }
        }

        /*    else if ( be.getType() == BattleEvent.KNOCKBACK ) {
        processKnockback(be);
        }*/
        return true; // Advance the segment normally
    }

    /**
     * @param be
     * @throws BattleEventException
     */
    public void processAbility(BattleEvent be) throws BattleEventException {
    	
        
        if (be.getType() == BattleEvent.ACTIVATE) {
        	Ability ability = be.getAbility();
            if (ability.isLinked() && ability.getLinkedAbilityCount() > 0) {
                // We need to determine the source right here...
                // This should always be right for an event that is currently
                // being processed.
                Target source = ability.getSource();
                LinkedBattleEvent lbe = new LinkedBattleEvent(source);
                lbe.addLinkedAbility(be, true);

                // Unhook the be from the completed pile and add the
                // lbe.
                battle.removeCompletedEvent(be);
                battle.addCompletedEvent(lbe);

                processAttackTreeNode(getProcessLinkedActivateRoot(lbe));
            } else {
                
				processAttackTreeNode(getProcessAbilityRoot(be));		
            }
        } else if (be.getType() == BattleEvent.CONTINUE || be.getType() == BattleEvent.CHARGE_END) {
            // processActivating(be);
            processAttackTreeNode(getProcessAbilityRoot(be));
        } else if (be.getType() == BattleEvent.DEACTIVATE) {
            processAttackTreeNode(getProcessAbilityRoot(be));
        } else {
            throw (new BattleEventException("Malformed BattleEvent: Unknown Action Type"));
        }
    }

    public void processLinkedActivate(BattleEvent be) throws BattleEventException {
        if (be instanceof SweepBattleEvent) {
            processAttackTreeNode(getProcessSweepActivateRoot(be));
        } else if (be instanceof CombinedAbilityBattleEvent) {
            processAttackTreeNode(getProcessCombinedActivateRoot(be));
        } else {
            processAttackTreeNode(getProcessLinkedActivateRoot(be));
        }
    }

    /**
     * @param be
     * @throws BattleEventException
     */
    public static void processEND(BattleEvent be) throws BattleEventException {
        chargeEND(be);
    }

    /**
     * @param be
     * @param source
     * @throws BattleEventException
     */
    public void processAttack(BattleEvent be, Target source)
            throws BattleEventException {
        //   Ability ability = be.getAbility();
        //   ActivationInfo ai = be.getActivationInfo();
        //   Target target;

        AttackProcessNode rn = new AttackProcessNode("Root");
        rn.setBattleEvent(be);
        AttackTreeModel atm = new AttackTreeModel(rn);
        // atm.setBattleEvent(be);

        atm.processAttackTree();

    }

    static public boolean getAllTargetsKnockedBack(BattleEvent be, String knockbackGroup) {
        boolean targetWasKnockedBack = true;

        // In the case of a MoveThrough, there should only ever be one knockback target.  However,
        // just for fun we will check if there are multiple ones.
        IndexIterator ii = be.getKnockbackTargets(knockbackGroup);
        while (ii.hasNext()) {
            int tindex = ii.nextIndex();
            if (be.getKnockbackAmount(tindex) <= 0) {
                // We found a target that did not get knocked back!
                targetWasKnockedBack = false;
                break;
            }
        }

        return targetWasKnockedBack;
    }

    /**
     * The following functions are run every time an attack occurs, in the following order:
     * getDefenses
     * defending character's -> predefense
     * attacking character's -> preattack
     * ability.power -> prepower
     * advantages/limitation -> prepower
     * applyDefenses
     * defending character's -> postdefense
     * attacking character's -> postattack
     * ability.power -> postpower
     * advantages/limitation -> postpower
     *
     * Additional, effects have priorities from 0 to 5:
     * 0 can look at subeffects prior to any changes by other effects
     * 1
     * 2 normal priority.  Make changes
     * 3 high priority changes.  After normal effects change things
     * 4 can change the subeffects any way they want...final say on changes
     * 5 view final results
     *
     * @param be
     * @param ability
     * @param effect
     * @param source
     * @param target
     * @throws BattleEventException
     */
    //public static void processEffect(BattleEvent be, Ability ability, Effect effect, Target source, Target target, DefenseList defenses, String targetGroup, String hitLocation, boolean finalTarget)
    public static void processEffect(BattleEvent be, Effect effect, Target source, int targetReferenceNumber, String targetGroup, String hitLocationForDamage, boolean finalTarget)
            throws BattleEventException {
        int count, i, priority;
        Effect c;

        ActivationInfo ai = be.getActivationInfo();

        Ability ability = be.getAbility();
        Ability maneuver = be.getManeuver();

        int tindex = ai.getTargetIndex(targetReferenceNumber, targetGroup);
        Target target = ai.getTarget(tindex);
        DefenseList defenses = ai.getDefenseList(tindex);

        if (finalTarget) {
            if ((Boolean) Preferences.getPreferenceList().getParameterValue("HitLocationAffectDamage") == true && !target.hasEffect(effectNoHitLocations.effectName)) {
                processHitLocationStun(be, effect, hitLocationForDamage);
            } else {
                processKillingStunMultiplier(be, effect, targetGroup);
            }
        }

        for (priority = 0; priority < 6; priority++) {
            // Process Hit Location Modifiers


            ProcessAllHitMofifiers(be, effect, targetReferenceNumber, targetGroup, hitLocationForDamage, finalTarget,
					priority, target);

            if (ability != null) {
                ability.getPower().prepower(be, ability, effect, target, targetReferenceNumber, targetGroup, hitLocationForDamage, finalTarget, priority);

                ProcessAllSourcePreAttacks(be, effect, source, targetReferenceNumber, targetGroup, hitLocationForDamage,
						finalTarget, priority, ability, target);

                ProcessAllPowerAdvantages(be, effect, source, targetReferenceNumber, targetGroup, hitLocationForDamage,
						finalTarget, priority, ability, target);

                // Process All Power Limitations
                //count = ability.getIndexedSize( "Limitation");
                count = ability.getLimitationCount();
                for (i = 0; i < count; i++) {
                    Limitation lim = ability.getLimitation(i);
                    if (lim.getPriority() == priority) {
                        //Limitation l = (Limitation) ability.getIndexedValue(i,"Limitation" , "LIMITATION");

                        lim.prepower(be, i, ability, effect, target, targetReferenceNumber, targetGroup, hitLocationForDamage, finalTarget);
                    }
                }
            }
            // Process Maneuver Stuff
            if (maneuver != null) {
                maneuver.getPower().prepower(be, maneuver, effect, target, targetReferenceNumber, targetGroup, hitLocationForDamage, finalTarget, priority);

                // Process All Power Advantages
                count = maneuver.getAdvantageCount();
                for (i = 0; i < count; i++) {        
                    Advantage a = maneuver.getAdvantage(i);
                    if (a.getPriority() == priority) {
                        a.prepower(be, i, maneuver, effect, target, targetReferenceNumber, targetGroup, hitLocationForDamage, finalTarget);
                    }
                }

                // Process All Power Limitations
                //count = maneuver.getIndexedSize( "Limitation");
                count = maneuver.getLimitationCount();
                for (i = 0; i < count; i++) {
                    Limitation lim = maneuver.getLimitation(i);
                    if (lim.getPriority() == priority) {
                        lim.prepower(be, i, maneuver, effect, target, targetReferenceNumber, targetGroup, hitLocationForDamage, finalTarget);
                    }
                }

            }
        }

        // Modify the Effect to represent actual damage through defenses.
        applyDefenses(be, effect, target, targetReferenceNumber, targetGroup, defenses);

        for (priority = 0; priority < 6; priority++) {
            processHitLocationBody(be, effect, hitLocationForDamage, priority);

            // Process All Target Effect Postdefenses
            count = target.getEffectCount();
            for (i = 0; i < count; i++) {
                c = (Effect) target.getEffect(i);
                if (c.getEffectPriority() == priority) {
                    c.postdefense(be, effect, target, targetReferenceNumber, targetGroup, hitLocationForDamage, finalTarget);
                }
            }

            // Process All Source preattack effects
            count = source.getEffectCount();
            for (i = 0; i < count; i++) {
                c = (Effect) source.getEffect(i);
                if (c.getEffectPriority() == priority) {
                    c.postattack(be, effect, target, targetReferenceNumber, targetGroup, hitLocationForDamage, finalTarget);
                }
            }

            if (ability != null) {
                ability.getPower().postpower(be, ability, effect, target, targetReferenceNumber, targetGroup, hitLocationForDamage, finalTarget, priority);

                // Process All Power Advantages
                count = ability.getAdvantageCount();
                for (i = 0; i < count; i++) {
                    Advantage a = ability.getAdvantage(i);
                    if (a.getPriority() == priority) {
                        a.postpower(be, i, ability, effect, target, targetReferenceNumber, targetGroup, hitLocationForDamage, finalTarget);
                    }
                }

                // Process All Power Limitations
                //count = ability.getIndexedSize( "Limitation");
                count = ability.getLimitationCount();
                for (i = 0; i < count; i++) {
                    Limitation lim = ability.getLimitation(i);
                    if (lim.getPriority() == priority) {
                        lim.postpower(be, i, ability, effect, target, targetReferenceNumber, targetGroup, hitLocationForDamage, finalTarget);
                    }
                }
            }

            if (maneuver != null) {
                maneuver.getPower().postpower(be, maneuver, effect, target, targetReferenceNumber, targetGroup, hitLocationForDamage, finalTarget, priority);

                // Process All Power Advantages
                count = maneuver.getAdvantageCount();
                for (i = 0; i < count; i++) {
                    Advantage a = maneuver.getAdvantage(i);
                    if (a.getPriority() == priority) {
                        a.postpower(be, i, maneuver, effect, target, targetReferenceNumber, targetGroup, hitLocationForDamage, finalTarget);
                    }
                }

                // Process All Power Limitations
                //count = maneuver.getIndexedSize( "Limitation");
                count = maneuver.getLimitationCount();
                for (i = 0; i < count; i++) {
                    Limitation lim = maneuver.getLimitation(i);
                    if (lim.getPriority() == priority) {
                        lim.postpower(be, i, maneuver, effect, target, targetReferenceNumber, targetGroup, hitLocationForDamage, finalTarget);
                    }
                }
            }

            processKilling(be, effect, priority);
        }
    }

	private static void ProcessAllHitMofifiers(BattleEvent be, Effect effect, int targetReferenceNumber,
			String targetGroup, String hitLocationForDamage, boolean finalTarget, int priority, Target target)
					throws BattleEventException {
		int count;
		int i;
		Effect c;
		// Process All Target Effect Predefenses
		//count = target.getIndexedSize( "Effect" );
		count = target.getEffectCount();
		for (i = 0; i < count; i++) {
		    c = target.getEffect(i);
		    if (c.getEffectPriority() == priority) {
		        c.predefense(be, effect, target, targetReferenceNumber, targetGroup, hitLocationForDamage, finalTarget);
		    }
		}
	}

	private static void ProcessAllPowerAdvantages(BattleEvent be, Effect effect, Target source,
			int targetReferenceNumber, String targetGroup, String hitLocationForDamage, boolean finalTarget,
			int priority, Ability ability, Target target) throws BattleEventException {
		int count;
		int i;
		count = ability.getAdvantageCount();
		for (i = 0; i < count; i++) {
		    Advantage a = ability.getAdvantage(i);
		    if (a.getPriority() == priority) {
		    /*	virtual if(ability.getName().equals("MobAttack")){
		    		ability.removeAdvantage(a);
		     		String advantage=a.getName();
		     		if (advantage.equals("Autofire")){
		     			advantageAutofire autofire=  (advantageAutofire)a;
		     			Integer maxshots = (Integer)autofire.parameterList.getParameterValue("MaxShots");
		     			maxshots=source.getRoster().getCombatants().size()+3;
		     			autofire.parameterList.setParameterValue("MaxShots", maxshots);
		     			ability.addAdvantageInfo(autofire, "Autofire", autofire.parameterList);
		     		}
		    	}
		  */  	
		        a.prepower(be, i, ability, effect, target, targetReferenceNumber, targetGroup, hitLocationForDamage, finalTarget);
		    }
		}
	}

	private static void ProcessAllSourcePreAttacks(BattleEvent be, Effect effect, Target source,
			int targetReferenceNumber, String targetGroup, String hitLocationForDamage, boolean finalTarget,
			int priority, Ability ability, Target target) throws BattleEventException {
		int count;
		int i;
		Effect c;
		count = source.getEffectCount();
		for (i = 0; i < count; i++) {
		    c = source.getEffect(i);
		    if (c.getEffectPriority() == priority) {
		    	c.preattack(be, effect, target, targetReferenceNumber, targetGroup, hitLocationForDamage, finalTarget);
		       /*virtual if(c.getName().equals("CSL: MobAttackBonus watcher")){
		            if(source.getRoster().MobMode=true){
		            	String attack=ability.getName();
		            	if(ability.getName().equals("MobAttack")){
		                	int mobNumber= source.getRoster().getSize();
		                	int ocvMod=mobNumber/3+1;
		                	effectCombatLevel mobAttackMod= (effectCombatLevel) c;
		                	int currentLevel= mobAttackMod.getLevels();
		                	mobAttackMod.setConfiguredModifiers(ocvMod, 0, 0);
		                	mobAttackMod.preattack(be, effect, target, targetReferenceNumber, targetGroup, hitLocationForDamage, finalTarget);
		            	}
		            }*/
		    }
		}
	}

    /**
     * @param be
     * @param effect
     * @param priority
     * @throws BattleEventException
     */
    public static void processKilling(BattleEvent be, Effect effect, int priority)
            throws BattleEventException {
        if (priority == 2) {
            int index, count;
            count = effect.getIndexedSize("Subeffect");
            for (index = 0; index < count; index++) {
                String defspecial = effect.getIndexedStringValue(index, "Subeffect", "DEFSPECIAL");
                if (defspecial != null && defspecial.equals("KILLING")) {
                    int bindex, sindex;
                    bindex = effect.findIndexed("Subeffect", "VERSUS", "BODY");
                    sindex = effect.findIndexed("Subeffect", "VERSUS", "STUN");
                    if (bindex != -1 && sindex != -1) {
                        Double bvalue = effect.getIndexedDoubleValue(bindex, "Subeffect", "VALUE");
                        Double svalue = effect.getIndexedDoubleValue(sindex, "Subeffect", "VALUE");

                        if (bvalue != null && svalue != null && bvalue.intValue() > svalue.intValue()) {
                            effect.addIndexed(sindex, "Subeffect", "VALUE", new Double(bvalue.intValue()), true);
                        }
                    }
                }
            }
        }
    }

    static private void processKillingStunMultiplier(BattleEvent be, Effect effect, String targetGroup)
            throws BattleEventException {
        Ability ability = be.getAbility();

        int sindex;
        sindex = effect.findIndexed("Subeffect", "VERSUS", "STUN");
        if (sindex != -1) {
            String defspecial = effect.getIndexedStringValue(sindex, "Subeffect", "DEFSPECIAL");
            if (defspecial != null && defspecial.equals("KILLING")) {
                int bindex = effect.findIndexed("Subeffect", "VERSUS", "BODY");
                if (bindex != -1) {
                    String effectType = effect.getSubeffectEffectType(bindex);
                    if (effectType.equals("DAMAGE")) {
                        double bvalue = effect.getSubeffectValue(bindex);

                        double multiple = 0;

                        if ((Boolean) Preferences.getPreferenceList().getParameterValue("HitLocationAffectDamage") == false) {
                            Dice d = be.getDiceRoll("StunDie", targetGroup);

                            multiple = d.getStun().intValue() - 1;

                            // The Ability.STUNMULTIPLIER is a positive or negative amound which needs to be applied to
                            // the stun multiplier to make it right.
                            Integer i = ability.getIntegerValue("Ability.STUNMULTIPLIER");
                            if (i != null) {
                                multiple += i.intValue();
                            }

                            if (multiple <= 0) {
                                multiple = 1;
                            }

                            if (DEBUG > 0) {
                                System.out.println("Killing Stun multiple = " + Double.toString(multiple));
                            }
                            effect.addIndexed(sindex, "Subeffect", "VALUE", new Double(Math.floor(bvalue * multiple)), true);
                            
                            Target t = be.getActivationInfo().getTarget(0);
                            t.setLastStunTaken( effect.getTotalStunDamage());
                            t.setLastBodyTaken( effect.getTotalBodyDamage());
                            be.addBattleMessage(new champions.battleMessage.LegacyBattleMessage("Stun Multiplier for killing attack was " + Double.toString(multiple) + ".", MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( "Stun Multiplier for killing attack was " + Double.toString(multiple) + ".", MSG_NOTICE)); // .addMessage( "Stun Multiplier for killing attack was " + Double.toString(multiple) + ".", MSG_NOTICE);

                        }
                    }
                }
            }
        }
    }

    static private void processHitLocationStun(BattleEvent be, Effect effect, String hitLocation)
            throws BattleEventException {
        Ability ability = be.getAbility();

        if (hitLocation != null && hitLocation.equals("NONE") == false) {
            int sindex;
            sindex = effect.findIndexed("Subeffect", "VERSUS", "STUN");
            if (sindex != -1) {
                String effectType = effect.getSubeffectEffectType(sindex);
                if (effectType.equals("DAMAGE")) {

                    String defspecial = effect.getIndexedStringValue(sindex, "Subeffect", "DEFSPECIAL");
                    if (defspecial != null && defspecial.equals("KILLING")) {
                        int bindex = effect.findIndexed("Subeffect", "VERSUS", "BODY");
                        if (bindex != -1) {
                            Double bvalue = effect.getIndexedDoubleValue(bindex, "Subeffect", "VALUE");
                            double multiple = lookupKillingStunMultiplier(hitLocation);

                            Integer i = ability.getIntegerValue("Ability.STUNMULTIPLIER");
                            if (i != null) {
                                multiple += i.intValue();
                            }

                            if (multiple <= 0) {
                                multiple = 1;
                            }

                            if (DEBUG > 0) {
                                System.out.println("Killing Stun multiple = " + Double.toString(multiple));
                            }
                            effect.addIndexed(sindex, "Subeffect", "VALUE", new Double(Math.floor(bvalue.intValue() * multiple)), true);
                            be.addBattleMessage(new champions.battleMessage.LegacyBattleMessage("Stun Multiplier for killing attack was " + Double.toString(multiple) + " due to hit location.", MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( "Stun Multiplier for killing attack was " + Double.toString(multiple) + " due to hit location.", MSG_NOTICE)); // .addMessage( "Stun Multiplier for killing attack was " + Double.toString(multiple) + " due to hit location.", MSG_NOTICE);
                        }
                    } else {
                        // Non-killing attack...
                        Double svalue = effect.getIndexedDoubleValue(sindex, "Subeffect", "VALUE");
                        double multiple = lookupNormalStunMultiplier(hitLocation);

                        if (DEBUG > 0) {
                            System.out.println("Normal Stun multiple = " + Double.toString(multiple));
                        }
                        effect.addIndexed(sindex, "Subeffect", "VALUE", new Double(Math.floor(svalue.intValue() * multiple)), true);
                        if (multiple != 1) {
                            be.addBattleMessage(new champions.battleMessage.LegacyBattleMessage("Stun damage adjusted by " + Double.toString(multiple) + " due to hit location.", MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( "Stun damage adjusted by " + Double.toString(multiple) + " due to hit location.", MSG_NOTICE)); // .addMessage( "Stun damage adjusted by " + Double.toString(multiple) + " due to hit location.", MSG_NOTICE);
                        }
                    }
                }
            }
        } else {
            // There was no hit location, so make one up for now
            int sindex;
            sindex = effect.findIndexed("Subeffect", "VERSUS", "STUN");
            if (sindex != -1) {
                String effectType = effect.getSubeffectEffectType(sindex);
                if (effectType.equals("DAMAGE")) {
                    String defspecial = effect.getIndexedStringValue(sindex, "Subeffect", "DEFSPECIAL");
                    if (defspecial != null && defspecial.equals("KILLING")) {
                        int bindex = effect.findIndexed("Subeffect", "VERSUS", "BODY");
                        if (bindex != -1) {
                            Double bvalue = effect.getIndexedDoubleValue(bindex, "Subeffect", "VALUE");
                            double multiple = 2; // This is just an average assumption

                            Integer i = ability.getIntegerValue("Ability.STUNMULTIPLIER");
                            if (i != null) {
                                multiple += i.intValue();
                            }

                            if (multiple <= 0) {
                                multiple = 1;
                            }

                            if (DEBUG > 0) {
                                System.out.println("Killing Stun multiple = " + Double.toString(multiple));
                            }
                            effect.addIndexed(sindex, "Subeffect", "VALUE", new Double(Math.floor(bvalue.intValue() * multiple)), true);
                            if (multiple != 1) {
                                be.addBattleMessage(new champions.battleMessage.LegacyBattleMessage("Stun Multiplier for killing attack was " + Double.toString(multiple) + ".", MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( "Stun Multiplier for killing attack was " + Double.toString(multiple) + ".", MSG_NOTICE)); // .addMessage( "Stun Multiplier for killing attack was " + Double.toString(multiple) + ".", MSG_NOTICE);
                            }
                        }
                    }
                }
            }
        }
    }

    static private void processHitLocationBody(BattleEvent be, Effect effect, String hitLocation, int priority)
            throws BattleEventException {
        if (priority == 0 && hitLocation != null && hitLocation.equals("NONE") == false) {
            int bindex;
            bindex = effect.findIndexed("Subeffect", "VERSUS", "BODY");
            
            if (bindex != -1) {
                String effectType = effect.getSubeffectEffectType(bindex);
                if (effectType.equals("DAMAGE")) {
                    // Non-killing attack...
                    Double bvalue = effect.getIndexedDoubleValue(bindex, "Subeffect", "VALUE");
                    if (bvalue.doubleValue() > 0) {
                        double multiple = lookupBodyMultiplier(hitLocation);
                        // System.out.println("Normal Stun multiple = " + Double.toString(multiple));
                        effect.addIndexed(bindex, "Subeffect", "VALUE", new Double(Math.floor(bvalue.intValue() * multiple)), true);
                        if (multiple != 1) {
                            be.addBattleMessage(new champions.battleMessage.LegacyBattleMessage("Body damage (after defenses) adjusted by " + Double.toString(multiple) + " due to hit location.", MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( "Body damage (after defenses) adjusted by " + Double.toString(multiple) + " due to hit location.", MSG_NOTICE)); // .addMessage( "Body damage (after defenses) adjusted by " + Double.toString(multiple) + " due to hit location.", MSG_NOTICE);
                        }
                    }
                }
            }
        }
    }

    static private double lookupKillingStunMultiplier(String hitLocation) {
        if (hitLocation != null) {
            for (int index = 0; index < HLKillingStunMultipliers.length; index++) {
                if (hitLocation.equals(HLKillingStunMultipliers[index][0])) {
                    Double d = (Double) HLKillingStunMultipliers[index][1];
                    return d.doubleValue();
                }
            }
        }
        return 1;
    }

    static private double lookupNormalStunMultiplier(String hitLocation) {
        if (hitLocation != null) {
            for (int index = 0; index < HLNormalStunMultipliers.length; index++) {
                if (hitLocation.equals(HLNormalStunMultipliers[index][0])) {
                    Double d = (Double) HLNormalStunMultipliers[index][1];
                    return d.doubleValue();
                }
            }
        }
        return 1;
    }

    static private double lookupBodyMultiplier(String hitLocation) {
        if (hitLocation != null) {
            for (int index = 0; index < HLBodyMultipliers.length; index++) {
                if (hitLocation.equals(HLBodyMultipliers[index][0])) {
                    Double d = (Double) HLBodyMultipliers[index][1];
                    return d.doubleValue();
                }
            }
        }
        return 1;
    }

    private void cleanBattleEventAfterFailure(BattleEvent be) {
        Ability ability = be.getAbility();

        ActivationInfo ai = be.getActivationInfo();
        if (ai != null) {
            ai.setState(AI_STATE_ACTIVATION_FAILED);
        }

        if (ability != null && ability.wasCreatedAutomatically() && ability.isInstanceActivated() == false && ability.isInstanceAdjusted() == false) {
            ability.setInstanceGroup(null);
        }

    }

    /**
     * @param dl
     * @param target  */
    public static void buildDefenseList(DefenseList dl, Target target) {
        target.buildDefenseList(dl, DefenseType.PD);
        target.buildDefenseList(dl, DefenseType.rPD);
        target.buildDefenseList(dl, DefenseType.ED);
        target.buildDefenseList(dl, DefenseType.rED);
        target.buildDefenseList(dl, DefenseType.MD);
        target.buildDefenseList(dl, DefenseType.POWERDEFENSE);
    }

    /**
     * @param be
     * @param effect
     * @param target
     * @throws BattleEventException
     */
    public static void applyDefenses(BattleEvent be, Effect effect, Target target, int targetReferenceNumber, String targetGroup, DefenseList defenses) throws BattleEventException {
        int count = effect.getIndexedSize("Subeffect");
        int i;
        String deftype, defspecial;
        double defenseMultiplier, defenseModifier;
        double value;
        ActivationInfo ai = be.getActivationInfo();
        int tindex = ai.getTargetIndex(targetReferenceNumber, targetGroup);
        int bodyAbsorbed = 0;
        int stunAbsorbed = 0;
        int mentalAbsorbed = 0;

        for (i = 0; i < count; i++) {
            deftype = effect.getSubeffectDefenseType(i);
            
            defspecial = effect.getSubeffectDefenseSpecial(i);

            Object versus;

            versus = effect.getIndexedValue(i, "Subeffect", "VERSUS");

            defenseMultiplier = 1;
            defenseModifier = 0;

            if (defspecial.equals("NND")) {
                boolean hasNNDDefense = false;
                //ActivationInfo ai = be.getActivationInfo();
                //int tindex = ai.getTargetIndex(targetReferenceNumber, targetGroup);
//                if ( ai.isTargetHasNNDDefenseSet(tindex) == false ) {
//                    hasNNDDefense = checkNND(i,effect,target);
//                    ai.setTargetHasNNDDefense(tindex, hasNNDDefense);
//                }
//                else {
//
//                }
                hasNNDDefense = ai.getTargetHasNNDDefense(tindex);

                if (hasNNDDefense) {
                    effect.addIndexed(i, "Subeffect", "VALUE", new Double(0), true);
                    be.addBattleMessage(new champions.battleMessage.LegacyBattleMessage(target.getName() + " was not affected by attack.", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( target.getName() + " was not affected by attack.", BattleEvent.MSG_NOTICE)); // .addMessage( target.getName() + " was not affected by attack.", BattleEvent.MSG_NOTICE);
                } else {
                    // Target was affected, but he will still get damage reduction multipliers
                    if (defenses != null) {
                        defenseMultiplier = 1;
                        if (effect.hasSubeffectDefenseType(i, "PD")) {
                            defenseMultiplier *= defenses.getTotalDefenseMultiplier(DefenseType.rPD);
                        }
                        if (effect.hasSubeffectDefenseType(i, "ED")) {
                            defenseMultiplier *= defenses.getTotalDefenseMultiplier(DefenseType.rED);
                        }
                        if (effect.hasSubeffectDefenseType(i, "MD")) {
                            defenseMultiplier *= defenses.getTotalDefenseMultiplier(DefenseType.MD);
                        }
                    }


                    value = effect.getSubeffectValue(i);
                    double unadjusted = value;

                    if (value > 0) {
                        value = Math.round(value * defenseMultiplier);
                    }

                    double defensesApplied = unadjusted - value;

                    effect.setSubeffectValue(i, value);
                    effect.setSubeffectDefensesApplied(i, defensesApplied);

                    if (versus.equals("STUN")) {
                        stunAbsorbed += defensesApplied;
                    } else if (versus.equals("BODY")) {
                        bodyAbsorbed += defensesApplied;
                    } else if (versus.equals("EFFECT")) {
                        mentalAbsorbed += defensesApplied;
                    }
                }
            } else if (defspecial.equals("AVLD")) {
                if (checkAVLD(i, effect, target) == false) {
                    effect.setSubeffectValue(i, 0);
                } else {
                    // Target was affected, but he will still get damage reduction multipliers
                    if (defenses != null) {
                        defenseMultiplier = 1;
                        if (effect.hasSubeffectDefenseType(i, "PD")) {
                            defenseMultiplier *= defenses.getTotalDefenseMultiplier(DefenseType.rPD);
                        }
                        if (effect.hasSubeffectDefenseType(i, "ED")) {
                            defenseMultiplier *= defenses.getTotalDefenseMultiplier(DefenseType.rED);
                        }
                        if (effect.hasSubeffectDefenseType(i, "MD")) {
                            defenseMultiplier *= defenses.getTotalDefenseMultiplier(DefenseType.MD);
                        }
                    }


                    value = effect.getSubeffectValue(i);
                    double unadjusted = value;

                    if (value > 0) {
                        value = Math.round(value * defenseMultiplier);
                    }

                    double defensesApplied = unadjusted - value;

                    effect.setSubeffectValue(i, value);
                    effect.setSubeffectDefensesApplied(i, defensesApplied);

                    if (versus.equals("STUN")) {
                        stunAbsorbed += defensesApplied;
                    } else if (versus.equals("BODY")) {
                        bodyAbsorbed += defensesApplied;
                    } else if (versus.equals("EFFECT")) {
                        mentalAbsorbed += defensesApplied;
                    }
                }
            } else if (defspecial.equals("KILLING")) {
                if (versus.equals("BODY")) {
                    if (defenses != null) {
                        defenseModifier = 0;
                        if (effect.hasSubeffectDefenseType(i, "PD")) {
                            defenseModifier += defenses.getTotalDefenseModifier(DefenseType.rPD);
                        }
                        if (effect.hasSubeffectDefenseType(i, "ED")) {
                            defenseModifier += defenses.getTotalDefenseModifier(DefenseType.rED);
                        }
                        if (effect.hasSubeffectDefenseType(i, "MD")) {
                            defenseModifier += defenses.getTotalDefenseModifier(DefenseType.MD);
                        }
                    }
                } else { // Stun case
                    if (defenses != null) {
                        defenseModifier = 0;
                        if (effect.hasSubeffectDefenseType(i, "PD")) {
                            defenseModifier += defenses.getTotalDefenseModifier(DefenseType.rPD);
                        }
                        if (effect.hasSubeffectDefenseType(i, "ED")) {
                            defenseModifier += defenses.getTotalDefenseModifier(DefenseType.rED);
                        }
                        if (effect.hasSubeffectDefenseType(i, "MD")) {
                            defenseModifier += defenses.getTotalDefenseModifier(DefenseType.MD);
                        }
                    }

                    if (defenseModifier != 0) {
                        // If there is resistent defense, you get all normal defenses against stun,
                        // otherwise you get 0.
                        defenseModifier = 0;
                        if (effect.hasSubeffectDefenseType(i, "PD")) {
                            defenseModifier += defenses.getTotalDefenseModifier(DefenseType.PD);
                        }
                        if (effect.hasSubeffectDefenseType(i, "ED")) {
                            defenseModifier += defenses.getTotalDefenseModifier(DefenseType.ED);
                        }
                        if (effect.hasSubeffectDefenseType(i, "MD")) {
                            defenseModifier += defenses.getTotalDefenseModifier(DefenseType.MD);
                        }
                    }
                }

                if (defenses != null) {
                    defenseMultiplier = 1;
                    if (effect.hasSubeffectDefenseType(i, "PD")) {
                        defenseMultiplier *= defenses.getTotalDefenseMultiplier(DefenseType.rPD);
                    }
                    if (effect.hasSubeffectDefenseType(i, "ED")) {
                        defenseMultiplier *= defenses.getTotalDefenseMultiplier(DefenseType.rED);
                    }
                    if (effect.hasSubeffectDefenseType(i, "MD")) {
                        defenseMultiplier *= defenses.getTotalDefenseMultiplier(DefenseType.MD);
                    }
                }

                value = effect.getSubeffectValue(i);

                double unadjusted = value;

                value = value - defenseModifier;
                if (value < 0) {
                    value = 0;
                }

                if (value > 0) {
                    value = Math.round(value * defenseMultiplier);
                }

                double defensesApplied = unadjusted - value;

                effect.setSubeffectValue(i, value);
                effect.setSubeffectDefensesApplied(i, defensesApplied);

                if (versus.equals("STUN")) {
                    stunAbsorbed += defensesApplied;
                } else if (versus.equals("BODY")) {
                    bodyAbsorbed += defensesApplied;
                } else if (versus.equals("EFFECT")) {
                    mentalAbsorbed += defensesApplied;
                }
            } else if (defspecial.equals("NORMAL")) {
                if (defenses != null) {
                    defenseMultiplier = 1;
                    defenseModifier = 0;
                    if (effect.hasSubeffectDefenseType(i, "PD")) {
                        defenseMultiplier *= defenses.getTotalDefenseMultiplier(DefenseType.PD);
                        defenseModifier += defenses.getTotalDefenseModifier(DefenseType.PD);
                    }
                    if (effect.hasSubeffectDefenseType(i, "ED")) {
                        defenseMultiplier *= defenses.getTotalDefenseMultiplier(DefenseType.ED);
                        defenseModifier += defenses.getTotalDefenseModifier(DefenseType.ED);
                    }
                    if (effect.hasSubeffectDefenseType(i, "MD")) {
                        defenseMultiplier *= defenses.getTotalDefenseMultiplier(DefenseType.MD);
                        defenseModifier += defenses.getTotalDefenseModifier(DefenseType.MD);
                    }
                }

                value = effect.getSubeffectValue(i);

                double unadjusted = value;

                value = value - defenseModifier;
                if (value < 0) {
                    value = 0;
                }

                if (value > 0) {
                    value = Math.round(value * defenseMultiplier);
                }

                double defensesApplied = unadjusted - value;

                effect.setSubeffectValue(i, value);
                effect.setSubeffectDefensesApplied(i, defensesApplied);

                if (versus.equals("STUN")) {
                    stunAbsorbed += defensesApplied;
                } else if (versus.equals("BODY")) {
                    bodyAbsorbed += defensesApplied;
                } else if (versus.equals("EFFECT")) {
                    mentalAbsorbed += defensesApplied;
                }
            } else if (defspecial.equals("POWERDEFENSE")) {
                String otherDefenseDescription = "";
                if (defenses != null) {
                    defenseModifier = defenses.getTotalDefenseModifier(DefenseType.POWERDEFENSE);
                    defenseMultiplier = defenses.getTotalDefenseMultiplier(DefenseType.POWERDEFENSE);

                    if (effect.hasSubeffectDefenseType(i, "PD")) {
                        // Additionally add PD or ED defenses...
                        otherDefenseDescription += " and PD";
                        defenseModifier += defenses.getTotalDefenseModifier(DefenseType.PD);
                        defenseMultiplier *= defenses.getTotalDefenseMultiplier(DefenseType.PD);
                    }
                    if (effect.hasSubeffectDefenseType(i, "MD")) {
                        // Additionally add PD or ED defenses...
                        otherDefenseDescription += " and MD";
                        defenseModifier += defenses.getTotalDefenseModifier(DefenseType.MD);
                        defenseMultiplier *= defenses.getTotalDefenseMultiplier(DefenseType.MD);
                    }
                    if (effect.hasSubeffectDefenseType(i, "ED")) {
                        // Additionally add PD or ED defenses...
                        otherDefenseDescription += " and ED";
                        defenseModifier += defenses.getTotalDefenseModifier(DefenseType.ED);
                        defenseMultiplier *= defenses.getTotalDefenseMultiplier(DefenseType.ED);
                    }
                }

                value = effect.getSubeffectValue(i);

                double unadjusted = value;

                value = value - defenseModifier;
                if (value < 0) {
                    value = 0;
                }

                if (value > 0) {
                    value = Math.round(value * defenseMultiplier);
                }

                double defensesApplied = unadjusted - value;

                effect.setSubeffectValue(i, value);
                effect.setSubeffectDefensesApplied(i, defensesApplied);

                if ((Boolean) Preferences.getPreferenceList().getParameterValue("DamageAbsorbedMessage") &&
                        (defensesApplied > 0)) {
                    be.addBattleMessage(new champions.battleMessage.LegacyBattleMessage(target.getName() + "'s Power Defenses" + otherDefenseDescription + " absorbed " + Integer.toString((int) defensesApplied) + " Character Point from Attack.", MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( target.getName() + "'s Power Defenses" + otherDefenseDescription + " absorbed " + Integer.toString( (int)defensesApplied ) + " Character Point from Attack.", MSG_NOTICE)); // .addMessage( target.getName() + "'s Power Defenses" + otherDefenseDescription + " absorbed " + Integer.toString( (int)defensesApplied ) + " Character Point from Attack.", MSG_NOTICE);
                }
            } else if (defspecial.equals("NONE") == false) {
                throw new BattleEventException("Unknown DEFSPECIAL: " + defspecial);
            }
            
            be.addDamageEffect(effect,tindex);
        }

        if ((Boolean) Preferences.getPreferenceList().getParameterValue("DamageAbsorbedMessage")) {


            if (bodyAbsorbed > 0 || stunAbsorbed > 0) {
                boolean hasStun = target.hasStat("STUN");
                boolean hasBody = target.hasStat("BODY");

                if (hasStun && hasBody) {
                    String msg = target.getName() + "'s defenses absorbed " + Integer.toString(stunAbsorbed) + " Stun and " + Integer.toString(bodyAbsorbed) + " Body.";
                    //be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( target.getName() + "'s defenses absorbed " + Integer.toString( stunAbsorbed ) + " Stun and " + Integer.toString(bodyAbsorbed) + " Body.", MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( target.getName() + "'s defenses absorbed " + Integer.toString( stunAbsorbed ) + " Stun and " + Integer.toString(bodyAbsorbed) + " Body.", MSG_NOTICE)); // .addMessage( target.getName() + "'s defenses absorbed " + Integer.toString( stunAbsorbed ) + " Stun and " + Integer.toString(bodyAbsorbed) + " Body.", MSG_NOTICE);
                    be.addBattleMessage(new DefenseMessage(target, msg));
                } else if (hasStun && stunAbsorbed > 0) {
                    String msg = target.getName() + "'s defenses absorbed " + Integer.toString(stunAbsorbed) + " Stun.";
                    //be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( target.getName() + "'s defenses absorbed " + Integer.toString( stunAbsorbed ) + " Stun.", MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( target.getName() + "'s defenses absorbed " + Integer.toString( stunAbsorbed ) + " Stun.", MSG_NOTICE)); // .addMessage( target.getName() + "'s defenses absorbed " + Integer.toString( stunAbsorbed ) + " Stun.", MSG_NOTICE);
                    be.addBattleMessage(new DefenseMessage(target, msg));
                } else if (hasBody && bodyAbsorbed > 0) {
                    String msg = target.getName() + "'s defenses absorbed " + Integer.toString(bodyAbsorbed) + " Body.";
                    //be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( target.getName() + "'s defenses absorbed " + Integer.toString(bodyAbsorbed) + " Body.", MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( target.getName() + "'s defenses absorbed " + Integer.toString(bodyAbsorbed) + " Body.", MSG_NOTICE)); // .addMessage( target.getName() + "'s defenses absorbed " + Integer.toString(bodyAbsorbed) + " Body.", MSG_NOTICE);
                    be.addBattleMessage(new DefenseMessage(target, msg));
                }
            }

            if (mentalAbsorbed > 0) {
                String msg = target.getName() + "'s defenses absorbed " + Integer.toString(mentalAbsorbed) + " Mental Effect Points.";
                //be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( target.getName() + "'s defenses absorbed " + Integer.toString( mentalAbsorbed ) + " Mental Effect Points.", MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( target.getName() + "'s defenses absorbed " + Integer.toString( mentalAbsorbed ) + " Mental Effect Points.", MSG_NOTICE)); // .addMessage( target.getName() + "'s defenses absorbed " + Integer.toString( mentalAbsorbed ) + " Mental Effect Points.", MSG_NOTICE);
                be.addBattleMessage(new DefenseMessage(target, msg));
            }
        }
    }

    /**
     * @param index
     * @param effect
     * @param target
     * @throws BattleEventException
     * @return
     */

    /**
     * @param index
     * @param effect
     * @param target
     * @return
     */
    public static boolean checkAVLD(int index, Effect effect, Target target) {
        // Needs to pop up dialog box to ask if target has NND defense
        // False indicates NND doesn't work against target
        return false;
    }

    /**
     * @param be
     * @param source
     * @throws BattleEventException
     */
    public static void calculateDamage(BattleEvent be, Target source, String targetGroup) throws BattleEventException {
        Ability ability = be.getAbility();
        ActivationInfo ai = be.getActivationInfo();

        Power p;
        int count, i;
        Ability maneuver;

        // The DCCALCULATED has to be associated with the TargetGroup otherwise the dice won't
        // be recalculated.
        int tgindex = ai.getTargetGroupIndex(targetGroup);
        if (tgindex == -1 || ai.getIndexedBooleanValue(tgindex, "TargetGroup", "DCCALCULATED") == true) {
            return;
        } else {
            ai.addIndexed(tgindex, "TargetGroup", "DCCALCULATED", "TRUE", true);
        }

        addGenericDice(be, targetGroup);

        // Merge Dice into BE
        if ((maneuver = be.getManeuver()) != null) {
            copyDice(maneuver, be, targetGroup);
        }
        copyDice(ability, be, targetGroup);


        // Clear DC modifiers!
        be.add("Maneuver.DC", new Integer(0), true);
        be.add("Base.DC", new Integer(0), true);
        be.add("Combat.DC", new Integer(0), true);
        be.add("Martial.DC", new Integer(0), true);

        // Run adjustDice for everything.
        if ((p = ability.getPower()) != null) {
            p.adjustDice(be, targetGroup);
        }

        if ((maneuver = be.getManeuver()) != null) {
            Power m = maneuver.getPower();
            m.adjustDice(be, targetGroup);
        }

        // Run through the source Effects to let them adjust the dice
        // First build list, then run through list
        count = source.getEffectCount();
        Effect[] sourceEffects = new Effect[count];
        for (i = 0; i < count; i++) {
            sourceEffects[i] = (Effect) source.getEffect(i);
        }

        for (i = 0; i < count; i++) {
            if (source.hasEffect(sourceEffects[i])) {
                sourceEffects[i].adjustDice(be, targetGroup);
            }
        }

        ChampionsUtilities.calculateDCs(be, targetGroup);
    }

    static public void resetDamageCalculation(BattleEvent be, String targetGroup) {
        // The DCCALCULATED has to be associated with the TargetGroup otherwise the dice won't
        // be recalculated.
        ActivationInfo ai = be.getActivationInfo();

        int tgindex = ai.getTargetGroupIndex(targetGroup);
        if (tgindex != -1 && ai.getIndexedBooleanValue(tgindex, "TargetGroup", "DCCALCULATED") == true) {
            ai.addIndexed(tgindex, "TargetGroup", "DCCALCULATED", "FALSE", true);
        }
    }

    /**
     * @param from
     * @param to
     */
    public void copyDice(DetailList from, DetailList to) {
        int fromindex, toindex, count;
        String name, description, size;
        Object dice;
        count = from.getIndexedSize("Die");
        for (fromindex = 0; fromindex < count; fromindex++) {
            name = from.getIndexedStringValue(fromindex, "Die", "NAME");
            if ((toindex = to.findIndexed("Die", "NAME", name)) == -1) {
                // Only add the dice if it isn't already there,
                size = from.getIndexedStringValue(fromindex, "Die", "SIZE");
                description = from.getIndexedStringValue(fromindex, "Die", "DESCRIPTION");
                dice = from.getIndexedDiceValue(fromindex, "Die", "DIEROLL");

                toindex = to.createIndexed("Die", "NAME", name);

                if (size != null) {
                    to.addIndexed(toindex, "Die", "SIZE", size, true);
                }
                if (description != null) {
                    to.addIndexed(toindex, "Die", "DESCRIPTION", description, true);
                }
                if (dice != null) {
                    to.addIndexed(toindex, "Die", "DIEROLL", dice, true);
                }
            }
        }
    }

    /** Adds the DamageDie and StunDie(killing only) appropriately, based upon the ability/maneuver used.
     *
     * @param be
     * @param targetGroup
     */
    public static void addGenericDice(BattleEvent be, String targetGroup) {
        Power p, m;
        Ability ability, maneuver;

        boolean addGeneric = false;
        boolean killing = false;
        boolean addStun = false;

        ability = be.getAbility();
        if (ability != null && ability.getGenerateDefaultEffects() == true) {
            addGeneric = true;

            if (ability.isKillingAttack()) {
                addStun = true;
                killing = true;
            }
        }

        maneuver = be.getManeuver();
        if (maneuver != null && maneuver.getGenerateDefaultEffects() == true) {
            m = maneuver.getPower();
            addGeneric = true;

            if (ability.isKillingAttack()) {
                addStun = true;
                killing = true;
            }
        }

        if (addStun && (Boolean) Preferences.getPreferenceList().getParameterValue("HitLocationAffectDamage") == true) {
            addStun = false;
        }

        if (addGeneric) {
            be.addDiceInfo("DamageDie", targetGroup, "Damage", "1d6");
        }

        if (addStun == true) {
            // be.addDiceInfo( "StunDie", targetGroup, "Stun Multiplier", "1d6");
            be.addDiceInfo("StunDie", targetGroup, "Stun Multiplier", "1d6", STUN_ONLY, "Stun Multiplier", null);
        }
    }

    /**
     * @param from
     * @param to
     */
    public static void copyDice(DetailList from, BattleEvent to, String targetGroup) {
        int fromindex, toindex, count;
        String name, description, size, type, stunLabel, bodyLabel;
        Dice dice;
        String existingGroup;
        boolean found;
        count = from.getIndexedSize("Die");
        for (fromindex = 0; fromindex < count; fromindex++) {
            name = from.getIndexedStringValue(fromindex, "Die", "NAME");
            found = false;

            toindex = to.findIndexed("Die", "NAME", name);
            while (toindex != -1) {
                existingGroup = to.getIndexedStringValue(toindex, "Die", "TARGETGROUP");
                if (targetGroup.equals(existingGroup)) {
                    // There is a duplicate, so don't copy that die
                    found = true;
                    break;
                }
                toindex = to.findIndexed(toindex + 1, "Die", "NAME", name);
            }

            if (!found) {
                // Only add the dice if it isn't already there
                size = from.getIndexedStringValue(fromindex, "Die", "SIZE");
                description = from.getIndexedStringValue(fromindex, "Die", "DESCRIPTION");
                dice = from.getIndexedDiceValue(fromindex, "Die", "DIEROLL");
                type = from.getIndexedStringValue(fromindex, "Die", "TYPE");
                stunLabel = from.getIndexedStringValue(fromindex, "Die", "STUNLABEL");
                bodyLabel = from.getIndexedStringValue(fromindex, "Die", "BODYLABEL");

                toindex = to.addDiceInfo(name, targetGroup, description, size);
                if (dice != null) {
                    to.setDiceRoll(name, targetGroup, dice);
                }
                if (type != null) {
                    to.addIndexed(toindex, "Die", "TYPE", type, true);
                }
                if (stunLabel != null) {
                    to.addIndexed(toindex, "Die", "STUNLABEL", stunLabel, true);
                }
                if (bodyLabel != null) {
                    to.addIndexed(toindex, "Die", "BODYLABEL", bodyLabel, true);
                }
            }
        }
    }

    /**
     * @param battleEvent  */
    static public void setupAttackParameters(BattleEvent battleEvent) {
        Ability ability = battleEvent.getAbility();

        ActivationInfo ai = battleEvent.getActivationInfo();
        
       // boolean continuing = ai.isContinuing();

        if (battleEvent.is("AUTOFIRE")) {
            Integer maxshots = ability.getIntegerValue("Ability.MAXSHOTS");
            if (BattleEngine.checkEND(battleEvent) < maxshots.intValue()) {
                int endShots = BattleEngine.checkEND(battleEvent);
                if (endShots == 0) {
                    endShots = 1;
                }

                maxshots = new Integer(endShots);
            }
            ai.add("Attack.SHOTS", maxshots, true);

            ai.add("Attack.ISSPRAY", "FALSE", true);
            ai.add("Attack.SPRAYWIDTH", new Integer(1), true);
        } else if (battleEvent.is("MOVEBY")) {
            ai.add("Attack.ISMOVEBY", "TRUE", true);
            ai.add("Attack.MOVEBYTARGETS", new Integer(1), true);
        } else if (battleEvent.can("SPREAD")) {
            ai.add("Attack.ISSPREAD", "FALSE", true);
            ai.add("Attack.ISSPREADM", "FALSE", true);
            ai.add("Attack.SPREADWIDTH", new Integer(1), true);
        }
        if(ai!=null) {
        	ai.add("Attack.BURNSTUN", "TRUE", true);
        }
        if(battleEvent.getSource()!=null) 
        {
        	battleEvent.add("Normal.STR", new Integer(battleEvent.getSource().getCurrentStat("STR")), true);
        }
        battleEvent.add("Pushed.STR", new Integer(0), true);
        
        
        if (ability!=null && ability.isSTRMiniumSet()) {
        	battleEvent.add("Minimum.STR", ability.getSTRMinimum(), true);
        }
    }

    /**
     * @param be
     */
    public void clearDice(BattleEvent be) {
    }

    /**
     * @param be
     * @throws BattleEventException
     */
    public void processHold(BattleEvent be) throws BattleEventException {
        Target t = be.getTarget();
        if (t.getCombatState() == CombatState.STATE_ACTIVE) {
            be.addCombatStateEvent(t, CombatState.STATE_ACTIVE, CombatState.STATE_HELD);
            t.setCombatState(CombatState.STATE_HELD);
        } else if (t.getCombatState() == CombatState.STATE_HALFFIN) {
            be.addCombatStateEvent(t, CombatState.STATE_HALFFIN, CombatState.STATE_HALFHELD);
            t.setCombatState(CombatState.STATE_HALFHELD);
        } else {
            throw new BattleEventException("Not in Holdable Combat State");
        }
        be.addBattleMessage(new champions.battleMessage.LegacyBattleMessage(t.getName() + " holds", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( t.getName() + " holds", BattleEvent.MSG_NOTICE)); // .addMessage( t.getName() + " holds", BattleEvent.MSG_NOTICE);
        battle.triggerChangeEvent();
    }

    /**
     * Generates the Effects for an attack based upon the Ability/Maneuver of the BE and applies them to a target.
     *
     */
    public static void processEffectsForTarget(BattleEvent be, String targetGroup, int targetReferenceNumber) throws BattleEventException {
        DetailList effects;
        boolean trigger;
        effects = new DetailList();

        ActivationInfo ai = be.getActivationInfo();
        Ability ability = be.getAbility();
        Ability maneuver = be.getManeuver();
        Target source = be.getSource();
        int tindex = ai.getTargetIndex(targetReferenceNumber, targetGroup);
        Target target = ai.getTarget(tindex);
        ObstructionList ol = ai.getObstructionList(tindex);
        //virtualInsert
        /*if(source.getRoster().MobMode==true){
        	source.getRoster().MobTarget=target;
        }*/

        String knockbackGroup = ai.getKnockbackGroup(targetGroup);

        int index, count;
        int index2;
        Effect effect;
        String ttype;

        SummaryMessageGroup emg = new SummaryMessageGroup(target);
        be.openMessageGroup(emg);

        // Check the Hit location and pop out a message if appropriate.
        String hitLocation = ai.getIndexedStringValue(tindex, "Target", "HITLOCATION");
        if (hitLocation != null) {
            generateHitLocationMessage(be, target, hitLocation);
        }

        if ((Boolean) Preferences.getPreferenceList().getParameterValue("HitLocationAffectDamage") == false) {
            hitLocation = null;
        }

        Power p = ability.getPower();
        p.triggerPower(be, ability, effects, target, targetReferenceNumber, targetGroup);
        trigger = p.isGenerateDefaultEffects(ability);

        if (trigger && be.hasManeuver()) {
            //Ability maneuver = be.getManeuver();
            Power m = maneuver.getPower();
            m.triggerPower(be, maneuver, effects, target, targetReferenceNumber, targetGroup);
            trigger = m.isGenerateDefaultEffects(maneuver);
        }

        if (trigger) {
            if (be.isKillingAttack()) {
                createKillingEffects(be, effects, targetGroup);
            } else if (be.isNormalAttack()) {
                createNormalEffects(be, effects, targetGroup, targetReferenceNumber);
            }
        }

        count = effects.getIndexedSize("Effect");
        for (index = 0; index < count; index++) {
            effect = (Effect) effects.getIndexedValue(index, "Effect", "EFFECT");
            ttype = effects.getIndexedStringValue(index, "Effect", "TTYPE");
            if (ttype == null || ttype.equals("TARGET")) {
                //DefenseList defenses = (DefenseList)ai.getIndexedValue(tindex, "Target", "DEFENSELIST");
                if (ol != null && processObstructions(be, ol, effect, knockbackGroup) == false) {
                    continue;
                }
                //jeff does KB
                // Do Knockback addition
                Double body;
                if (be.doesKnockback()) {
                    int totalBodyDamage = effect.getTotalBodyDamage();
                    if(totalBodyDamage==0) 
                    {
                    	int dindex = be.getDiceIndex( "DamageDie", targetGroup );
                    
                    	if( dindex !=-1)
						{
                    		Dice dice = be.getDiceRoll(dindex);
                    		totalBodyDamage = dice.getBody();
						}
                    }
                    addKnockback(be, target, knockbackGroup, totalBodyDamage);
                }

                processEffect(be, effect, source, targetReferenceNumber, targetGroup, hitLocation, true);
                addEffect(be, effect, target);
                String x="A";

                adjustKnockback(be, effect, target, knockbackGroup);
            } else {
                //System.out.println("Warning: Source Effect applied to Attack Source.  Currently Not Implemented.");

                // Add a temporary Target group for the source effect...
                int tgindex = ai.getTargetGroupIndex("SOURCE");
                if (tgindex == -1) {
                    tgindex = ai.addTargetGroup("SOURCE");
                    ai.setTargetGroupIsTemporary(tgindex, true);
                }

                int t = ai.addTarget(source, "SOURCE", 0);
                ai.setTargetHitOverride(t, true, null, null);

                processEffect(be, effect, source, 0, "SOURCE", null, true);
                addEffect(be, effect, source);
            }
        }

        be.closeMessageGroup(emg);

        if (emg.getChildCount() == 0) {
            // Since nothing happened, remove this group...
            BattleMessageGroup currentGroup = be.getCurrentMessageGroup();
            if (currentGroup != null) {
                currentGroup.removeMessage(emg);
            }
        }
    }

    /**
     * Processes and applies the indicated Effect to a target.
     *
     *
     */
    public static void applyEffectToTarget(BattleEvent be, Target target, Effect effect, String knockbackGroup, boolean finalTarget) throws BattleEventException {
        DetailList effects;
        boolean trigger;
        effects = new DetailList();

        ActivationInfo ai = be.getActivationInfo();
        int tgindex = ai.addTargetGroup("applyEffectToTarget");
        int tindex = ai.addTarget(target, "applyEffectToTarget");


        if (tindex != -1) {
            int targetReferenceNumber = ai.getTargetReferenceNumber(tindex);

            DefenseList defenses = ai.getDefenseList(tindex);
            if (defenses == null) {
                defenses = new DefenseList();
                buildDefenseList(defenses, target);
                ai.setDefenseList(tindex, defenses);
            }

            String hitLocation = null;

            if ((Boolean) Preferences.getPreferenceList().getParameterValue("HitLocationAffectDamage")) {
                hitLocation = ai.getTargetHitLocation(tindex);
            }

            int index;
            Double body;
            // Do Knockback addition

            if (knockbackGroup != null && effect.getBooleanValue("Effect.DOESKB")) {
                int bodyDamage = effect.getTotalBodyDamage();
                if (bodyDamage > 0) {
                    addKnockback(be, target, knockbackGroup, bodyDamage);
                }
            }


            processEffect(be, effect, target, targetReferenceNumber, "applyEffectToTarget", hitLocation, finalTarget);
            addEffect(be, effect, target);

            if (knockbackGroup != null) {
                adjustKnockback(be, effect, target, knockbackGroup);
            }
        }
    }

    /**
     * Processes and applies the indicated Effect to a target.
     *
     *
     */
    public static void applyEffectToTarget(BattleEvent be, int targetReferenceNumber, String targetGroup, Effect effect, boolean finalTarget) throws BattleEventException {
        DetailList effects;
        boolean trigger;
        effects = new DetailList();

        ActivationInfo ai = be.getActivationInfo();
        int tindex = ai.getTargetIndex(targetReferenceNumber, targetGroup);

        if (tindex != -1) {
            Target target = ai.getTarget(tindex);
            String knockbackGroup = ai.getKnockbackGroup(targetGroup);

            DefenseList defenses = ai.getDefenseList(tindex);
            if (defenses == null) {
                defenses = new DefenseList();
                buildDefenseList(defenses, target);
                ai.setDefenseList(tindex, defenses);
            }

            String hitLocation = null;

            if ((Boolean) Preferences.getPreferenceList().getParameterValue("HitLocationAffectDamage")) {
                hitLocation = ai.getTargetHitLocation(tindex);
            }

            int index;
            Double body;
            // Do Knockback addition

            if (knockbackGroup != null && effect.getBooleanValue("Effect.DOESKB")) {
                int bodyDamage = effect.getTotalBodyDamage();
                if (bodyDamage > 0) {
                    addKnockback(be, target, knockbackGroup, bodyDamage);
                }
            }


            processEffect(be, effect, target, targetReferenceNumber, targetGroup, hitLocation, finalTarget);
            addEffect(be, effect, target);

            if (knockbackGroup != null) {
                adjustKnockback(be, effect, target, knockbackGroup);
            }
        }
    }

    static public void generateHitLocationMessage(BattleEvent be, Target target, String hitLocation) {
        String msg = target.getName() + " was hit in the " + hitLocation + ".";
        //be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( target.getName() + " was hit in the " + hitLocation + ".", MSG_NOTICE )); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( target.getName() + " was hit in the " + hitLocation + ".", MSG_NOTICE )); // .addMessage( target.getName() + " was hit in the " + hitLocation + ".", MSG_NOTICE );
        be.addBattleMessage(new HitLocationMessage(target, msg));
    }

    /**
     * @return
     */
    public BattleEventList getBattleEventList() {
        return battleEventList;
    }

    /**
     *
     */
    public synchronized void startProcessing() {
//        if (processing == false) {
            processing = true;
            battle.triggerProcessingNotify(processing);
            startTime = System.currentTimeMillis();
            notifyAll();
   //     }
    }

    /**
     * @param be
     * @param source
     */
    public static void adjustSourceCombatStatus(BattleEvent be, Target source) {
        Ability ability = be.getAbility();

        String time = "";

//        if ( be.contains("Ability.TEMPTIME") ) {
//            time = (String)be.getValue("Ability.TEMPTIME");
//        } else if ( ability.getActivationTime() != null  ) {
//            time = ability.getActivationTime();
//        }
        time = be.getActivationTime();

        if (source.isPostTurn()) {
            be.addPostTurnEvent(source, false);
            source.setPostTurn(false);

        } else if (time.equals("ATTACK") || time.equals("FULLMOVE")) {
            if (source.getCombatState() == CombatState.STATE_ABORTING) {
                be.addCombatStateEvent(source, source.getCombatState(), CombatState.STATE_ABORTED);
                source.setCombatState(CombatState.STATE_ABORTED);

            } else {
                be.addCombatStateEvent(source, source.getCombatState(), CombatState.STATE_FIN);
                source.setCombatState(CombatState.STATE_FIN);

            }
        } else if (time.equals("HOLD")) {
            if (source.getCombatState() == CombatState.STATE_ACTIVE) {
                be.addCombatStateEvent(source, source.getCombatState(), CombatState.STATE_HELD);
                source.setCombatState(CombatState.STATE_HELD);

            } else if (source.getCombatState() == CombatState.STATE_HALFFIN) {
                be.addCombatStateEvent(source, source.getCombatState(), CombatState.STATE_HALFHELD);
                source.setCombatState(CombatState.STATE_HALFHELD);

            }
        } else if (time.equals("HALFMOVE")) {
            if (source.getCombatState() == CombatState.STATE_ABORTING) {
                be.addCombatStateEvent(source, source.getCombatState(), CombatState.STATE_ABORTED);
                source.setCombatState(CombatState.STATE_ABORTED);

            } else if (source.getCombatState() == CombatState.STATE_ACTIVE || source.getCombatState() == CombatState.STATE_HELD) {
                be.addCombatStateEvent(source, source.getCombatState(), CombatState.STATE_HALFFIN);
                source.setCombatState(CombatState.STATE_HALFFIN);

            } else if (source.getCombatState() == CombatState.STATE_HALFFIN || source.getCombatState() == CombatState.STATE_HALFHELD) {
                be.addCombatStateEvent(source, source.getCombatState(), CombatState.STATE_FIN);
                source.setCombatState(CombatState.STATE_FIN);
            }
        } else if (time.equals("INSTANT")) {
            if (source.getCombatState() == CombatState.STATE_ABORTING) {
                be.addCombatStateEvent(source, source.getCombatState(), CombatState.STATE_ABORTED);
                source.setCombatState(CombatState.STATE_ABORTED);

            }
        }
    }

    /**
     * @return
     */
    public boolean isProcessing() {
        return processing;
    }

    /**
     * @param be
     * @param source
     * @param index
     * @throws BattleEventException
     */
    public void removeEffect(BattleEvent be, Target source, int index)
            throws BattleEventException {
        Effect effect;
        int count;

        // Call the appropriate Remove
        count = source.getEffectCount();
        if (index < count) {
            effect = source.getEffect(index);
            effect.removeEffect(be, source);
        }
    }

    /**
     * @param be
     * @param effect
     * @param target
     * @throws BattleEventException
     */
    public static void addEffect(BattleEvent be, Effect effect, Target target) throws BattleEventException {
        // add the effect to the target
        effect.addEffect(be, target);
    }

    /**
     * @param be
     * @param source
     * @param target
     * @param cvl
     * @return  */
    static public CVList buildCVListForSecondary(BattleEvent be, Target source, Target target, CVList cvl) {
        Ability ability = be.getAbility();

        int index, count;
        String cvtype;
        Effect effect;

        if (cvl == null) {
            cvl = new CVList();
        }

        cvl.setSourceName(source.getName());
        cvl.setTargetName(target.getName());

        cvl.addSourceCVSet("Secondary Target", -3);
        cvl.addTargetCVModifier("Generic", 0);

        Power p = ability.getPower();
        Ability maneuver = null;
        Power m = null;


        count = be.getIndexedSize("DCVModifier");
        for (index = 0; index < count; index++) {
            String desc = be.getIndexedStringValue(index, "DCVModifier", "DESCRIPTION");
            Integer value = be.getIndexedIntegerValue(index, "DCVModifier", "VALUE");
            if (desc != null && value != null) {
                cvl.addTargetCVModifier(desc, value.intValue());
            }
        }

        if ((cvtype = ability.getStringValue("Ability.CVTYPE")) != null && cvtype.equals("EGO")) {
            // It is a ECV attack
            cvl.setType(CVLIST_ECV);

            cvl.setTargetCVBase(target.getBaseECV());


            // Run through Target effects...
            count = target.getEffectCount();
            for (index = 0; index < count; index++) {
                effect = target.getEffect(index);
                effect.addECVDefenseModifiers(cvl, ability);
            }
        } else {
            cvl.setType(CVLIST_OCVDCV);
            // It is a normal attack
            cvl.setTargetCVBase(target.getBaseDCV());


            // Run through Target effects...
            count = target.getEffectCount();
            for (index = 0; index < count; index++) {
                effect = target.getEffect(index);
                effect.addDCVDefenseModifiers(cvl, ability);
            }
        }

        cvl.setInitialized(true);

        return cvl;
    }

    /**
     * @param be
     * @param source
     * @param target
     * @param cvl
     * @return  */
    static public CVList buildCVListForKBSecondary(BattleEvent be, Target source, Target target, CVList cvl) {
        Ability ability = be.getAbility();

        int index, count;
        String cvtype;
        Effect effect;

        if (cvl == null) {
            cvl = new CVList();
        }

        cvl.setSourceName(source.getName());
        cvl.setTargetName(target.getName());

        cvl.setSourceCVBase(source.getBaseOCV());
        cvl.addSourceCVModifier("Generic", 0);



        Power p = ability.getPower();
        Ability maneuver = null;
        Power m = null;


//        count = be.getIndexedSize( "DCVModifier" );
//        for(index=0;index<count;index++) {
//            String desc = be.getIndexedStringValue(index, "DCVModifier", "DESCRIPTION" );
//            Integer value = be.getIndexedIntegerValue(index, "DCVModifier", "VALUE" );
//            if (desc != null && value != null ) {
//                cvl.addTargetCVModifier( desc, value.intValue() );
//            }
//        }


        cvl.setType(CVLIST_OCVDCV);
        // It is a normal attack
        cvl.setTargetCVBase(0);

        // Run through Target effects...
        count = target.getEffectCount();
        for (index = 0; index < count; index++) {
            effect = target.getEffect(index);
            if (effect instanceof SizeModifierEffect) {
                effect.addDCVDefenseModifiers(cvl, ability);
            }
        }

        cvl.setInitialized(true);

        return cvl;
    }

    /**
     * @param be
     * @param source
     * @param target
     * @param cvl
     * @return  */
    static public CVList buildCVList(BattleEvent be, Target source, Target target, CVList cvl) {
        Ability ability = be.getAbility();

        int index, count;
        String cvtype;
        Effect effect;

        if (cvl == null) {
            cvl = new CVList();
        }

        cvl.setSourceName(source.getName());
        cvl.setTargetName(target.getName());

        cvl.addSourceCVModifier("Generic", 0, "TRUE");
        cvl.addSourceCVModifier("Off Hand", -3, "FALSE");
        cvl.addSourceCVModifier("Unfamiliar Weapon", -3, "FALSE");
        cvl.addSourceCVModifier("Surprise Move +(1-3)", 2, "FALSE");
        cvl.addSourceCVModifier("Encumbrance -(1-5)", 1, "FALSE");

        cvl.addTargetCVModifier("Generic", 0);
        cvl.addTargetCVMultiplier("From Behind", 0.5, "FALSE");
        cvl.addTargetCVMultiplier("Surprised", 0.5, "FALSE");

        //We only turn off Concealment in the case where Hit Locations
        //are actually used for damage so that concealment can continue to be an option
        //for those GM's that like to use Hit Location reporting for color but not for
        //damage.  If they do do it for damage then they really should process concealment
        //in the Hit Location panel.
        if (!target.getBooleanValue("HitLocation.AFFECTSDAMAGE"))  {
            cvl.addConcealmentCVModifier("Concealment", 0);
        }
        Power p = ability.getPower();
        Ability maneuver = null;
        Power m = null;

        if (be.hasManeuver()) {
            maneuver = be.getManeuver();
            m = maneuver.getPower();
        }


        if (ability.hasRangeModifier()) {
            cvl.addRangeCVModifier("Ranged Attack", 0);
        }

        if (ability.isThrow()) {
            cvl.addThrowCVModifier("Throw Penalty", 0);
        }

        // Add OCV/DCV modifiers from battleEvent
        count = be.getIndexedSize("OCVModifier");
        for (index = 0; index < count; index++) {
            String desc = be.getIndexedStringValue(index, "OCVModifier", "DESCRIPTION");
            Integer value = be.getIndexedIntegerValue(index, "OCVModifier", "VALUE");
            if (desc != null && value != null) {
                cvl.addSourceCVModifier(desc, value.intValue());
            }
        }

        count = be.getIndexedSize("DCVModifier");
        for (index = 0; index < count; index++) {
            String desc = be.getIndexedStringValue(index, "DCVModifier", "DESCRIPTION");
            Integer value = be.getIndexedIntegerValue(index, "DCVModifier", "VALUE");
            if (desc != null && value != null) {
                cvl.addTargetCVModifier(desc, value.intValue());
            }
        }

        if ((cvtype = ability.getStringValue("Ability.CVTYPE")) != null && cvtype.equals("EGO")) {
            // It is a ECV attack
            cvl.setType(CVLIST_ECV);

            cvl.setSourceCVBase(source.getBaseECV());
            cvl.setTargetCVBase(target.getBaseECV());

            p.addECVAttackModifiers(be, cvl, ability);
            if (m != null) {
                m.addECVAttackModifiers(be, cvl, maneuver);
            }
            // Run through Source effects...
            count = source.getEffectCount();
            for (index = 0; index < count; index++) {
                effect = source.getEffect(index);
                effect.addECVAttackModifiers(cvl, ability);
            }

            // Run through Target effects...
            count = target.getEffectCount();
            for (index = 0; index < count; index++) {
                effect = target.getEffect(index);
                effect.addECVDefenseModifiers(cvl, ability);
            }
        } else {
            cvl.setType(CVLIST_OCVDCV);
            // It is a normal attack
            cvl.setSourceCVBase(source.getBaseOCV());
            cvl.setTargetCVBase(target.getBaseDCV());

            p.addOCVAttackModifiers(be, cvl, ability);
            if (m != null) {
                m.addOCVAttackModifiers(be, cvl, maneuver);
            }
            // Run through Source effects...
            count = source.getEffectCount();
            for (index = 0; index < count; index++) {
                effect = source.getEffect(index);
                effect.addOCVAttackModifiers(cvl, ability);
            }

            // Run through Target effects...
            count = target.getEffectCount();
            for (index = 0; index < count; index++) {
                effect = target.getEffect(index);
                effect.addDCVDefenseModifiers(cvl, ability);
            }
        }

        cvl.setInitialized(true);

        return cvl;
    }

    /** Builds a CVList to be used with the Block maneuver.
     *
     * The returned CVList will contain the Blockers OCV as the CVList source and will
     * contain the Attackers OCV as the CVList target.
     *
     * @param be
     * @param source
     * @param target
     * @param cvl
     * @return  */
    static public CVList buildBlockCVList(BattleEvent be, Target attacker, Target blocker, Effect blockEffect, CVList cvl) {
        Ability ability = be.getAbility();

        int index, count;
        String cvtype;
        Effect effect;

        if (cvl == null) {
            cvl = new CVList();
        }

        cvl.setType(CVLIST_BLOCK);

        cvl.setSourceName(attacker.getName());
        cvl.setTargetName(blocker.getName());

        cvl.addSourceCVModifier("Generic", 0);
        cvl.addTargetCVModifier("Generic", 0);

        Power p = ability.getPower();
        Ability maneuver = null;
        Power m = null;

        if (be.hasManeuver()) {
            maneuver = be.getManeuver();
            m = maneuver.getPower();
        }

        // Add OCVmodifiers from battleEvent to the Attacker.  The blocker does not get his DCV
        count = be.getIndexedSize("OCVModifier");
        for (index = 0; index < count; index++) {
            String desc = be.getIndexedStringValue(index, "OCVModifier", "DESCRIPTION");
            Integer value = be.getIndexedIntegerValue(index, "OCVModifier", "VALUE");
            if (desc != null && value != null) {
                cvl.addSourceCVModifier(desc, value.intValue());
            }
        }


        if ((cvtype = ability.getStringValue("Ability.CVTYPE")) != null && cvtype.equals("EGO")) {
            // It is a ECV attack
            cvl.setSourceCVBase(attacker.getBaseECV());

            p.addECVAttackModifiers(be, cvl, ability);
            if (m != null) {
                m.addECVAttackModifiers(be, cvl, maneuver);
            }
            // Run through Source effects...
            count = attacker.getEffectCount();
            for (index = 0; index < count; index++) {
                effect = attacker.getEffect(index);
                effect.addECVAttackModifiers(cvl, ability);
            }
        } else {
            // It is a normal attack
            cvl.setSourceCVBase(attacker.getBaseOCV());

            p.addOCVAttackModifiers(be, cvl, ability);
            if (m != null) {
                m.addOCVAttackModifiers(be, cvl, maneuver);
            }
            // Run through Source effects...
            count = attacker.getEffectCount();
            for (index = 0; index < count; index++) {
                effect = attacker.getEffect(index);
                effect.addOCVAttackModifiers(cvl, ability);
            }
        }

        // Now Swap the Attacker and Blocker, then run through OCV values for the blocker
        cvl.swapSourceAndTarget();

        if ((cvtype = ability.getStringValue("Ability.CVTYPE")) != null && cvtype.equals("EGO")) {
            // It is a ECV attack
            cvl.setSourceCVBase(blocker.getBaseECV());

            // Run through Source effects...
            count = blocker.getEffectCount();
            for (index = 0; index < count; index++) {
                effect = (Effect) blocker.getEffect(index);
                effect.addECVAttackModifiers(cvl, ability);
            }
        } else {
            // It is a normal attack
            cvl.setSourceCVBase(blocker.getBaseOCV());

            // Run through Source effects...
            count = blocker.getEffectCount();
            for (index = 0; index < count; index++) {
                effect = blocker.getEffect(index);
                effect.addOCVAttackModifiers(cvl, ability);
            }
        }

        if (blockEffect instanceof effectBlock) {
            effectBlock eb = (effectBlock) blockEffect;
            if (eb != null) {
                // Add Multiple Block penalty
                int blockCount = eb.getBlockCount();
                cvl.addSourceCVModifier("Multiple Block Penalty", blockCount * -2);

            }
        } else if (blockEffect instanceof effectDeflection) {
            effectDeflection eb = (effectDeflection) blockEffect;
            if (eb != null) {
                // Add Multiple Block penalty
                int blockCount = eb.getDeflectionCount();
                cvl.addSourceCVModifier("Multiple Deflection Penalty", blockCount * -2);

            }
        }

        cvl.setInitialized(true);

        return cvl;
    }

    /** Indicates that the Parameters for a battleEvent have changed.
     *
     * This method is called when the parameters and settings for a particular battle event have
     * changed due to a user input.  Any cached calculations, based upon past parameters, should
     * be recalculated.
     *
     * In general, this will reset things like CVLists which require reinitialization due to
     * possible changes in OCV/DCV penalties due to different advantages/limitations.
     */
    public static void battleParametersChanged(BattleEvent be) {
        ActivationInfo ai = be.getActivationInfo();

        // Reset the Initialization setting on all current CVLists
        int tindex = ai.getIndexedSize("Target") - 1;
        for (; tindex >= 0; tindex--) {
            CVList cvl = ai.getCVList(tindex);
            if (cvl != null) {
                cvl.setInitialized(false);
            }
        }
    }

    /** Determines if a particular target was hit by an attack.
     *
     * Determine hit examines the battleEvent/activationInfo for this attack and determine if a particular
     * target was hit by the attack.
     *
     * There are several different variables which are taken into account to determine if a target was hit:
     *
     * Target[].HITMODE - The HITMODE specifies how a hit should be treated.  There are several different
     * allowed setting for the HITMODE.
     *
     * OVERRIDE mode: Override mode specifies that no matter what the CV values specify, the Target[].HIT variable
     * is already correctly set and should be used definitely.  The Target[].OVERRIDEREASON and
     * Target[].OVERRIDEEXPLINATION, as well as the Target[].HIT vp's should be populated previously.
     * The Target[].TOHIT will be filled in based on the CVList, but may not reflect that actual reason for hitting.
     * The Target[].HITROLL can but filled in elsewhere, but it if it not, it will be filled with 3 for a hit
     * 18 for a miss.
     *
     * FORCEHIT mode: ForceHit mode specifies that the user specified that the Attack should succeed.  The Target[].TOHIT
     * will be filled in based upon the CVList values, Target[].HITROLL will be set to 3, and Target[].HIT will be
     * set true.
     *
     * FORCEMISS mode: ForceMiss mode specifies that the user specified that the Attack should fail.  The Target[].TOHIT
     * will be filled in based upon the CVList values, Target[].HITROLL will be set to 18, and Target[].HIT will be
     * set false.
     *
     * USEDICE mode: UseDice indicates that this is a normal attack.  If USEDICE is indicated, the Target[].TOHITDICE
     * vp should be properly set to the dice roll for the attack.  If the dice is null, the attack will automatically
     * miss.  Target[].TOHIT, Target[].HITROLL, and Target[].HIT will be properly populated based upon the dice, CVList,
     * and hit values.
     *
     * All modes of use will generate a battleEvent message which indicates if the attack hit or missed, and why that
     * occurred.
     *
     * @param be BattleEvent of Attack.
     * @param targetReferenceNumber Reference Number of Target within specified TargetGroup.
     * @param targetGroup TargetGroup twhere Target information is located.
     * @throws BattleEventException
     * @return TRUE if attack hit, FASLE if attack missed.
     */
    static public boolean determineHit(BattleEvent be, int targetReferenceNumber, String targetGroup)
            throws BattleEventException {
        // Determine the CV values
        Ability ability = be.getAbility();
        ActivationInfo ai = be.getActivationInfo();

        int tindex = ai.getTargetIndex(targetReferenceNumber, targetGroup);
        if (tindex == -1) {
            throw new BattleEventException("Error Determining Hit: Target not found.");
        }

        Target source = ai.getTargetSource(tindex);
        if (source == null) {
            source = be.getSource();
        }
        Target target = ai.getTarget(tindex);

        CVList cvl = (CVList) ai.getIndexedValue(tindex, "Target", "CVLIST");
        if (cvl == null) {
            throw new BattleEventException("Error Determining Hit: CVList is null.");
        } else if (cvl.isInitialized() == false) {
            System.out.println("Warning: CVList was not initialized prior to BattleEngine.determineHit() method.");
            buildCVList(be, source, target, cvl);
        }

        boolean hitTarget = false;

        int ocv = cvl.getSourceCV();
        int dcv = cvl.getTargetCV();
        int toHit = 11 - dcv + ocv;

        ai.addIndexed(tindex, "Target", "TOHIT", new Integer(toHit), true);

        String hitmode = ai.getIndexedStringValue(tindex, "Target", "HITMODE");

        if (hitmode.equals(OVERRIDE)) {
            hitTarget = ai.getIndexedBooleanValue(tindex, "Target", "HIT");
            String reason = ai.getIndexedStringValue(tindex, "Target", "OVERRIDEREASON");
            if (reason != null) {
                if (hitTarget) {
                    String msg = source.getName() + " hit " + target.getName() + ". " + reason;
                    //be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( msg,BattleEvent.MSG_HIT)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( msg,BattleEvent.MSG_HIT)); // .addMessage( msg,BattleEvent.MSG_HIT);

                    be.addBattleMessage(new DefaultAttackMessage(source, target, hitTarget, msg));

                    // Fill in the HITROLL if it isn't set elsewhere.
                    ai.addIndexed(tindex, "Target", "HITROLL", new Integer(3), false);
                } else {
                    String msg = source.getName() + " missed " + target.getName() + ". " + reason;
                    //be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( msg , BattleEvent.MSG_MISS)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( msg , BattleEvent.MSG_MISS)); // .addMessage( msg , BattleEvent.MSG_MISS);

                    be.addBattleMessage(new DefaultAttackMessage(source, target, hitTarget, msg));

                    // Fill in the HITROLL if it isn't set elsewhere.
                    ai.addIndexed(tindex, "Target", "HITROLL", new Integer(18), false);
                }
            }
        } else if (hitmode.equals(FORCEHIT)) {
            // Force Hit button was used
            String msg = source.getName() + " hit " + target.getName() + ".  Automatic Hit.";
            //be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage(msg ,BattleEvent.MSG_HIT)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(msg ,BattleEvent.MSG_HIT)); // .addMessage(msg ,BattleEvent.MSG_HIT);

            be.addBattleMessage(new DefaultAttackMessage(source, target, hitTarget, msg));

            ai.addIndexed(tindex, "Target", "HITROLL", new Integer(3), true);
            hitTarget = true;

        } else if (hitmode.equals(FORCEMISS)) {
            // Force Miss button was used
            String msg = source.getName() + " missed " + target.getName() + ".  Automatic Miss.";
            //be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( msg, BattleEvent.MSG_MISS)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( msg, BattleEvent.MSG_MISS)); // .addMessage( msg, BattleEvent.MSG_MISS);

            be.addBattleMessage(new DefaultAttackMessage(source, target, hitTarget, msg));

            ai.addIndexed(tindex, "Target", "HITROLL", new Integer(18), true);
            hitTarget = false;
        } else {
            // This is a USEDICE hitmode attack
            Dice d = (Dice) ai.getIndexedValue(tindex, "Target", "TOHITDIE");

            if (d == null) {
                hitTarget = false;
                ai.addIndexed(tindex, "Target", "HITROLL", new Integer(Integer.MAX_VALUE), true);
            } else {
                int roll = d.getStun().intValue();
                ai.addIndexed(tindex, "Target", "HITROLL", new Integer(roll), true);
                if (roll <= toHit) {
                    String msg = source.getName() + " hit " + target.getName() + ". " + source.getName() + " needed roll \u2264 " + Integer.toString(toHit) + ". Rolled " + Integer.toString(roll) + ".";
                    //be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( msg , BattleEvent.MSG_HIT)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( msg , BattleEvent.MSG_HIT)); // .addMessage( msg , BattleEvent.MSG_HIT);
                    hitTarget = true;
                    be.addBattleMessage(new DefaultAttackMessage(source, target, hitTarget, msg));
                } else {
                    String msg = source.getName() + " missed " + target.getName() + ". " + source.getName() + " needed roll \u2264 " + Integer.toString(toHit) + ". Rolled " + Integer.toString(roll) + ".";
                    //be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( msg , BattleEvent.MSG_MISS)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( msg , BattleEvent.MSG_MISS)); // .addMessage( msg , BattleEvent.MSG_MISS);
                    hitTarget = false;
                    be.addBattleMessage(new DefaultAttackMessage(source, target, hitTarget, msg));
                }
            }
        }

        ai.addIndexed(tindex, "Target", "HIT", hitTarget ? "TRUE" : "FALSE", true);

        return hitTarget;
    }

    /** Determines if a Blocking Target was able to block the Attack.
     *
     * Determine block success examines the battleEvent/activationInfo for this attack.
     *
     * There are several different variables which are taken into account to determine if a target was hit:
     *
     * Target#.BLOCKCVLIST: CVList containing Blocker and Attacker CV values.  Blocker information should be
     * in the Source location and Attacker CV should be in the Target location of the CVList.
     *
     * Target#.BLOCKDIE: Contains the roll made by the blocker when attempting to block the attack.
     *
     * All modes of use will generate a battleEvent message which indicates if the attack hit or missed, and why that
     * occurred.
     *
     * @param be BattleEvent of Attack.
     * @param targetReferenceNumber Reference Number of Target within specified TargetGroup.
     * @param targetGroup TargetGroup twhere Target information is located.
     * @throws BattleEventException
     * @return TRUE if attack hit, FASLE if attack missed.
     */
    static public boolean determineBlockSuccess(BattleEvent be, int targetReferenceNumber, String targetGroup)
            throws BattleEventException {
        // Determine the CV values
        Ability ability = be.getAbility();
        ActivationInfo ai = be.getActivationInfo();

        Target attacker = be.getSource();

        int tindex = ai.getTargetIndex(targetReferenceNumber, targetGroup);

        if (tindex == -1) {
            throw new BattleEventException("Error Determining Block Success: Blocker not found.");
        }

        Target blocker = ai.getTarget(tindex);

        boolean isBlocking = ai.getIndexedBooleanValue(tindex, "Target", "ISBLOCKING");

        boolean blockedAttack = false;

        if (isBlocking) {
            CVList cvl = (CVList) ai.getIndexedValue(tindex, "Target", "BLOCKCVLIST");
            if (cvl == null) {
                throw new BattleEventException("Error Determining Block Success: CVList is null.");
            } else if (cvl.isInitialized() == false) {
                throw new BattleEventException("Error Determining Block Success: CVList is null.");
            }


            int ocv = cvl.getSourceCV();
            int dcv = cvl.getTargetCV();
            int toHit = 11 - dcv + ocv;

            ai.addIndexed(tindex, "Target", "BLOCKTOHIT", toHit, true, false);

            Dice d = (Dice) ai.getIndexedValue(tindex, "Target", "BLOCKDIE");

            if (d == null) {
                blockedAttack = false;
            } else {
                int roll = d.getStun().intValue();
                if (roll <= toHit) {
                    be.addBattleMessage(new champions.battleMessage.LegacyBattleMessage(blocker.getName() + " blocked " + attacker.getName() + ". " + blocker.getName() + " needed to roll <= " + Integer.toString(toHit) + ". Rolled " + Integer.toString(roll) + ".", BattleEvent.MSG_HIT)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( blocker.getName() + " blocked " + attacker.getName() +". " + blocker.getName() + " needed to roll <= " + Integer.toString(toHit) +". Rolled " + Integer.toString(roll) +".", BattleEvent.MSG_HIT)); // .addMessage( blocker.getName() + " blocked " + attacker.getName() +". " + blocker.getName() + " needed to roll \u2264 " + Integer.toString(toHit) +". Rolled " + Integer.toString(roll) +".", BattleEvent.MSG_HIT);
                    blockedAttack = true;
                } else {
                    be.addBattleMessage(new champions.battleMessage.LegacyBattleMessage(blocker.getName() + " failed to block " + attacker.getName() + ". " + blocker.getName() + " needed to roll <= " + Integer.toString(toHit) + ". Rolled " + Integer.toString(roll) + ".", BattleEvent.MSG_MISS)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( blocker.getName() + " failed to block " + attacker.getName()+ ". " + blocker.getName() + " needed to roll <= " + Integer.toString(toHit) +". Rolled " + Integer.toString(roll) +".", BattleEvent.MSG_MISS)); // .addMessage( blocker.getName() + " failed to block " + attacker.getName()+ ". " + blocker.getName() + " needed to roll \u2264 " + Integer.toString(toHit) +". Rolled " + Integer.toString(roll) +".", BattleEvent.MSG_MISS);
                    blockedAttack = false;
                }
            }
        }

        ai.addIndexed(tindex, "Target", "BLOCKED", blockedAttack ? "TRUE" : "FALSE", true);

        return blockedAttack;
    }

    static public void processBlock(BattleEvent be, int targetReferenceNumber, String targetGroup)
            throws BattleEventException {
        // Determine the CV values
        Ability ability = be.getAbility();
        ActivationInfo ai = be.getActivationInfo();

        int tindex = ai.getTargetIndex(targetReferenceNumber, targetGroup);

        if (tindex == -1) {
            throw new BattleEventException("Error Processing Block: Blocker not found.");
        }

        Target blocker = ai.getTarget(tindex);

        boolean isBlocking = ai.getIndexedBooleanValue(tindex, "Target", "ISBLOCKING");

        if (isBlocking) {
            boolean blockedAttack = ai.getIndexedBooleanValue(tindex, "Target", "BLOCKED");

            effectBlock eb = effectBlock.getBlockEffect(blocker);

            if (eb == null) {
                System.out.println("Warning: Couldn't Find Block Effect for Block.");
                return;
            }

            be.addUndoableEvent(eb.setBlockCount(eb.getBlockCount() + 1));

            if (blockedAttack == false) {
                be.addUndoableEvent(eb.setBlockFailed(true));
            }

            if (blockedAttack) {
                String reason = blocker.getName() + " has successfully blocked this attack.  Blocked attacks always miss.";
                ai.setTargetHitOverride(tindex, false, reason, reason);

                // Add Block Entry to battle so Blocker will go before Attack in next concurrent segment (if any)
                Chronometer time = blocker.getNextActiveTime();
                be.addUndoableEvent(Battle.getCurrentBattle().addBlockSequence(time, blocker, be.getSource()));

            } else {
                ai.clearTargetHitOverride(tindex);
            }
        }
    }

    static public void processDeflection(BattleEvent be, int targetReferenceNumber, String targetGroup)
            throws BattleEventException {
        // Determine the CV values
        Ability ability = be.getAbility();
        ActivationInfo ai = be.getActivationInfo();

        int tindex = ai.getTargetIndex(targetReferenceNumber, targetGroup);

        if (tindex == -1) {
            throw new BattleEventException("Error Processing Deflection: Deflector not found.");
        }

        Target blocker = ai.getTarget(tindex);

        boolean isBlocking = ai.getIndexedBooleanValue(tindex, "Target", "ISBLOCKING");

        if (isBlocking) {
            boolean blockedAttack = ai.getIndexedBooleanValue(tindex, "Target", "BLOCKED");

            effectDeflection eb = effectDeflection.getDeflectionEffect(blocker);

            if (eb == null) {
                System.out.println("Warning: Couldn't Find Deflection Effect for Deflection.");
                return;
            }

            be.addUndoableEvent(eb.setDeflectionCount(eb.getDeflectionCount() + 1));

            if (blockedAttack == false) {
                be.addUndoableEvent(eb.setDeflectionFailed(true));
            }

            if (blockedAttack) {
                String reason = blocker.getName() + " has successfully deflected this attack.  Deflected attacks always miss original target.";
                ai.setTargetHitOverride(tindex, false, reason, reason);
            } else {
                ai.clearTargetHitOverride(tindex);
            }
        }
    }

    public boolean advanceSegment(boolean forceAdvance) throws BattleEventException {
        return advanceSegment(forceAdvance, null);
    }

    /**
     * @param forceAdvance
     * @throws BattleEventException
     * @return
     */
    public boolean advanceSegment(boolean forceAdvance, Chronometer stopTime) throws BattleEventException {
        //msg("Advancing Segment",BattleMessageEvent.MSG_SEGMENT);
        Target newTarget;
        Sequencer seq = battle.getSequencer();
        Chronometer time = battle.getTime();
        BattleEvent be;
        BattleSequencePair bsp; 
        Object o;

        if (battle == null || battle.getCombatants().size() == 0) {
            return false;
        }

        Target activeTarget = battle.getActiveTarget();
        Chronometer startTime = (Chronometer) battle.getTime().clone();
        int count = 0;
        // First, see if there is anybody that is actually scheduled to go still this segment...
        BattleSequence bs = seq.getBattleSequence(1, false);
        if (bs.size() > 0) {
            // Advance ActiveTarget but not segment
            // Select the next target in line
            bsp = bs.get(0);
            o = bsp.getTarget();        	
            be = new BattleEvent(BattleEvent.ADVANCE_SEGMENT_MARKER, false);
            be.setTimeProcessed((Chronometer) time.clone());
            if (o instanceof Target) {
                newTarget = (Target) o;
              /*  Roster mobRoster= newTarget.getRoster();
                /*virtuAL
                */
                /*if(mobRoster.MobMode== true){
                	newTarget= mobRoster.MobLeader;
                	if(newTarget==null){
                		newTarget= mobRoster.getCombatants().get(1);
                		mobRoster.MobLeader=newTarget;
                	}
                }*/
                if (newTarget != activeTarget) {
                    try {
                        be.addActiveTargetEvent(activeTarget, newTarget);
                        battle.setSelectedTarget(newTarget);
                        be.addBattleMessage(new champions.battleMessage.LegacyBattleMessage(newTarget.getName() + " is now Active.", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( newTarget.getName() + " is now Active", BattleEvent.MSG_NOTICE)); // .addMessage( newTarget.getName() + " is now Active", BattleEvent.MSG_NOTICE);
                        battle.addCompletedEvent(be);
                        doPrephase(be);
                    } catch (BattleEventException bee) {
                        be.setError(bee.toString());
                        be.rollbackBattleEvent();
                        if (bee.isDisplayError()) {
                            displayBattleError(bee);
                        }
                        return false;
                    }
                }
                return false; // Didn't advance segment
            } else if (o instanceof BattleEvent) {
                BattleEvent delayEvent = (BattleEvent) o;
                processDelayedEvent(be, delayEvent);

                // Since this was a delayed event, make sure you push an advance segment event on
                // the BattleEventQueue to make sure the clock is advanced after this.
                if (getBattleEventList().peekAtFirstEvent() == null) {
                    battle.addEvent(new BattleEvent(BattleEvent.ADVANCE_TIME, false));
                }

                return false;
            }
        }

        // Next, if there is nobody scheduled this segment, if forceadvance == false and this isn't the TurnEnd
        // then look at the on-hold eligible list and just take the top member...
        BattleSequence e = seq.getBattleEligible();
        if (forceAdvance == false && time.isTurnEnd() == false && e.size() > 0) {
            bsp = e.get(0);
            o = bsp.getTarget();
            if (o instanceof Target) {
                newTarget = (Target) o;
                if (newTarget != activeTarget) {
                    be = new BattleEvent(BattleEvent.ADVANCE_SEGMENT_MARKER, false);
                    be.setTimeProcessed((Chronometer) time.clone());
                    try {
                        be.addActiveTargetEvent(activeTarget, newTarget);
                        battle.setSelectedTarget(newTarget);
                        be.addBattleMessage(new champions.battleMessage.LegacyBattleMessage(newTarget.getName() + " is now Active", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( newTarget.getName() + " is now Active", BattleEvent.MSG_NOTICE)); // .addMessage( newTarget.getName() + " is now Active", BattleEvent.MSG_NOTICE);
                        battle.addCompletedEvent(be);
                        doPrephase(be); // Should we do a prephase here?!?
                    } catch (BattleEventException bee) {
                        be.setError(bee.toString());
                        be.rollbackBattleEvent();
                        if (bee.isDisplayError()) {
                            displayBattleError(bee);
                        }
                        return false;
                    }
                }
                return false;  // Turn wasn't advanced cause there was still eligibles this turn
            }
        }
        // There is no one scheduled and eligibles were either skipped or this is turn end...so try to advance to
        // the next active character...
        // If it is turn end and the single advance preference is set, then make sure you advance at most to the next segment.
        if (time.isTurnEnd() && e.size() > 0 && (Boolean) Preferences.getPreferenceList().getParameterValue("SingleAdvanceWhenEligible")) {
            stopTime = (Chronometer) time.clone();
            stopTime.setTime(stopTime.getTime() + 1);
        }

        // If the stop time is still null, allow only 12 segments to pass so no infinite loops occur.
        if (stopTime == null) {
            stopTime = (Chronometer) time.clone();
            stopTime.setTime(stopTime.getTime() + 12);
        }

        // Start Advancing the Segments forward and adjusting combat states accordingly.
        be = new BattleEvent(BattleEvent.ADVANCE_SEGMENT_MARKER, false);
        be.setTimeProcessed((Chronometer) time.clone());
        Set combatants = battle.getCombatants();
        Iterator i;
        while (seq.getBattleSequence(1, false).size() == 0 && time.getTime() < stopTime.getTime()) {
            count++;
            // Never advance more then a turn to avoid infinite loop
            // Advance the segment until there are actual participants
            cleanRandoms(time);
            time.incrementSegment();

            // Let every target know that the segment advanced...
            processSegmentAdvance(be);
            be.addBattleMessage(new champions.battleMessage.LegacyBattleMessage("Segment was advanced to " + time.toString() + ".", BattleEvent.MSG_SEGMENT)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( "Segment was advanced to " + time.toString() + ".", BattleEvent.MSG_SEGMENT )); // .addMessage( "Segment was advanced to " + time.toString() + ".", BattleEvent.MSG_SEGMENT );

            // First a segment advance message out to all the listeners
            Battle.fireSegmentAdvanced();

            if (time.isTurnEnd()) {


                String purgeTimeString = (String) Preferences.getPreferenceList().getParameterValue("AutopurgeTime");


                if (purgeTimeString != null && purgeTimeString.equals("Never") == false) {
                    Chronometer purgeTime = (Chronometer) time.clone();
                    if (purgeTimeString.equals("1 Turn")) {
                        purgeTime.add(-12);
                    } else if (purgeTimeString.equals("2 Turns")) {
                        purgeTime.add(-24);
                    } else if (purgeTimeString.equals("3 Turns")) {
                        purgeTime.add(-36);
                    } else if (purgeTimeString.equals("4 Turns")) {
                        purgeTime.add(-48);
                    } else if (purgeTimeString.equals("1 Minute")) {
                        purgeTime.add(-60);
                    } else if (purgeTimeString.equals("5 Minutes")) {
                        purgeTime.add(-300);
                    } else {
                        // Catch cases where the time string wasn't recognized
                        System.out.println("Warning: Autopurge.TIME preference contains unrecognized time string.");
                        purgeTime = null;
                    }

                    if (purgeTime != null && purgeTime.isLegal()) {
                        Battle.getCurrentBattle().purgeCompletedEvents(purgeTime);
                    }

                }

                //loops through targets
                i = combatants.iterator();
                while (i.hasNext()) {
                    Target c = (Target) i.next();
                    //c.setCombatState( c.getCombatState() | Target.STATE_POSTTURN);
                    //if ( c.isCombatActive() ) {
                    // Run through the effect postturn
                    doPostturn(be, c);

                    be.addPostTurnEvent(c, true);
                    c.setPostTurn(true);
                //}
                }
            } else {
                // This is a normal Segment.  Make everybody active that should be
                i = combatants.iterator();
                while (i.hasNext()) {
                    Target c = (Target) i.next();
                    if (c.isCombatActive() && time.isActivePhase(c.getEffectiveSpeed(time))) {
                        // Set done = false so prephase event occur next time charater is active
                        be.addPrephaseDoneEvent(c, false);
                        c.setPrephaseDone(false);

                        if (c.getCombatState() == CombatState.STATE_FIN || c.getCombatState() == CombatState.STATE_HALFFIN) {
                            // Set the state to Active
                            be.addCombatStateEvent(c, c.getCombatState(), CombatState.STATE_ACTIVE);
                            c.setCombatState(CombatState.STATE_ACTIVE);

                        } else if (c.getCombatState() == CombatState.STATE_HELD || c.getCombatState() == CombatState.STATE_HALFHELD) {
                            // Maintain Held status
                            be.addBattleMessage(new champions.battleMessage.LegacyBattleMessage(c.getName() + " lost held action.", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(c.getName() + " lost held action.", BattleEvent.MSG_NOTICE)); // .addMessage(c.getName() + " lost held action.", BattleEvent.MSG_NOTICE);
                            be.addCombatStateEvent(c, c.getCombatState(), CombatState.STATE_ACTIVE);
                            c.setCombatState(CombatState.STATE_ACTIVE);

                        } else if (c.getCombatState() == CombatState.STATE_ABORTED) {
                            // Steal this action phase for prior Aborted action
                            be.addCombatStateEvent(c, c.getCombatState(), CombatState.STATE_FIN);
                            c.setCombatState(CombatState.STATE_FIN);
                        }
                    }
                }
            }
        }

        // Segment was advanced
        be.addTimeEvent(startTime, time);
        // removed to put above so that every segment advance is announced
        //be.addBattleMessage( "Segment was advanced to " + time.toString() + ".", BattleEvent.MSG_SEGMENT );
        battle.setTime(time);
        // Select the next target in line
        BattleSequence v = seq.getBattleSequence(1, false);

        if (v.size() >= 1) {
            bsp = v.get(0);
            o = bsp.getTarget();
            if (o instanceof Target) {

                newTarget = (Target) o;

                try {
                    be.addActiveTargetEvent(activeTarget, newTarget);
                    battle.setSelectedTarget(newTarget);
                    be.addBattleMessage(new champions.battleMessage.LegacyBattleMessage(newTarget.getName() + " is now Active.", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( newTarget.getName() + " is now Active.", BattleEvent.MSG_NOTICE)); // .addMessage( newTarget.getName() + " is now Active.", BattleEvent.MSG_NOTICE);
                    battle.addCompletedEvent(be);
                    doPrephase(be);

                    return true;
                } catch (BattleEventException bee) {
                    be.setError(bee.toString());
                    be.rollbackBattleEvent();
                    displayBattleError(bee);
                    return false;
                }
            } else if (o instanceof BattleEvent) {
                be.addActiveTargetEvent(activeTarget, null);
                battle.setSelectedTarget(null);

                BattleEvent delayEvent = (BattleEvent) o;

                processDelayedEvent(be, delayEvent);

                if (getBattleEventList().peekAtFirstEvent() == null) {
                    battle.addEvent(new BattleEvent(BattleEvent.ADVANCE_TIME, false));
                }
//                be.addUndoableEvent( battle.removeDelayedEvent( delayEvent) );
//                be.addUndoableEvent( battle.addEvent(delayEvent) );
//                //  be.addBattleMessage( delayEvent.getSource().getName() + "'s " + delayEvent.getAbility().getName() + " is active.", BattleEvent.MSG_NOTICE);
//                battle.addCompletedEvent(be);
                return true;
            }
        }

        // The stop time was hit and there still wasn't an scheduled target.
        // then look at the on-hold eligible list again and take the top member (unless it is turn end)
        e = seq.getBattleEligible();
        if (time.isTurnEnd() == false && e.size() > 0) {
            bsp = e.get(0);
            o = bsp.getTarget();
            if (o instanceof Target) {
                newTarget = (Target) o;
                if (newTarget != activeTarget) {
                    be = new BattleEvent(BattleEvent.ADVANCE_SEGMENT_MARKER, false);
                    be.setTimeProcessed((Chronometer) time.clone());
                    try {
                        be.addActiveTargetEvent(activeTarget, newTarget);
                        battle.setSelectedTarget(newTarget);
                        be.addBattleMessage(new champions.battleMessage.LegacyBattleMessage(newTarget.getName() + " is now Active", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( newTarget.getName() + " is now Active", BattleEvent.MSG_NOTICE)); // .addMessage( newTarget.getName() + " is now Active", BattleEvent.MSG_NOTICE);
                        battle.addCompletedEvent(be);
                        doPrephase(be);
                    } catch (BattleEventException bee) {
                        be.setError(bee.toString());
                        be.rollbackBattleEvent();
                        if (bee.isDisplayError()) {
                            displayBattleError(bee);
                        }
                        return false;
                    }
                }
                return true;  // Turn wasn't advanced cause there was still eligibles this turn
            }
        }

        // There is just no-one who can go.  Set the active target to null...
        be.addActiveTargetEvent(activeTarget, null);
        battle.setSelectedTarget(null);
        battle.addCompletedEvent(be);
        return true;
    }

    protected void processDelayedEvent(BattleEvent advanceTimeBe, BattleEvent delayEvent) throws BattleEventException {
        advanceTimeBe.addUndoableEvent(battle.removeDelayedEvent(delayEvent));
        BattleEvent newBe = delayEvent.clone();  // Uncertain why we clone...perhaps because there is no undo for battleEvent contents...

        if (delayEvent.getType() != BattleEvent.ACTION && delayEvent.getType() != BattleEvent.CHARGE_END) {
            advanceTimeBe.addBattleMessage(new champions.battleMessage.LegacyBattleMessage(delayEvent.getSource().getName() + "'s " + delayEvent.getAbility().getInstanceName() + " is now finishing.", BattleEvent.MSG_NOTICE));
        }
        battle.addCompletedEvent(advanceTimeBe);

        advanceTimeBe.embedBattleEvent(newBe);
        processEvent(newBe);
    }

    /**
     * @param time
     */
    public void cleanRandoms(Chronometer time) {
        //String randomKey = "Random" + Long.toString(time.getTime() );

        long timeKey = time.getTime();

        Iterator i = battle.getCombatants().iterator();
        while (i.hasNext()) {
            Target t = (Target) i.next();
            //t.remove(randomKey);
            t.removeRandomSequenceNumber(timeKey);
        }
    }

    /**
     * @param be
     * @throws BattleEventException
     */
    public void doPrephase(BattleEvent be) throws BattleEventException {
        if (battle == null) {
            return;
        }

        Target activeTarget = battle.getActiveTarget();
        if (activeTarget != null) {
            if (activeTarget.isPrephaseDone() == false) {
                if (activeTarget.isPostTurn() == false) {
                    Effect effect;
                    Ability ability;
                    int count;
                    int i;

                    // Build list of Effect currently in effect
                    count = activeTarget.getEffectCount();
                    List<Effect> removeList = new ArrayList<Effect>();
                    List<Effect> prephaseEffectList = new ArrayList<Effect>();
                    for (i = 0; i < count; i++) {
                        effect = activeTarget.getEffect(i);
                        // Collect ability seperate copy of the coditions
                        prephaseEffectList.add(effect);
                    }

                    // Run prephase for each effect and determine if effect needs to be removed
                    for (i = 0; i < prephaseEffectList.size(); i++) {
                        effect = prephaseEffectList.get(i);
                        if (activeTarget.hasEffect(effect)) {
                            if (effect.prephase(be, activeTarget)) {
                                removeList.add(effect);
                            }
                        }
                    }

                    // Actually remove the effects
                    for (i = 0; i < removeList.size(); i++) {
                        effect = removeList.get(i);
                        if (activeTarget.hasEffect(effect)) {
                            // Effect is still there
                            effect.removeEffect(be, activeTarget);
                        }
                    }

                    // Charge Endurance.  END Effect should throw error if you don't have
                    // Enough END to run all the currently active powers
                    // This loop will also trigger all currently active powers, so Constant damage
                    // powers will actually be retriggered.

                    Set justActivatedAbilities = new HashSet();

                    Chronometer time = (Chronometer) Battle.getCurrentBattle().getTime();

                    ActivationInfo ai;
                    count = activeTarget.getIndexedSize("ActivationInfo");
                    ActivationInfo[] ais = new ActivationInfo[count];
                    for (i = 0; i < count; i++) {
                        ais[i] = (ActivationInfo) activeTarget.getIndexedValue(i, "ActivationInfo", "ACTIVATIONINFO");
                    }

                    for (i = 0; i < count; i++) {
                        ai = ais[i];
                        if (ai == null) {
                            NullPointerException npe = new NullPointerException("Null Activation Info detected during doPrephase for Target: " + activeTarget);
                            npe.printStackTrace();
                            ExceptionWizard.postException(npe);
                            continue;
                        }

                        boolean shouldDeactivate = false;

                        if (ai.isActivated() && (ai.getLastProcessedTime() == null || ai.getLastProcessedTime().equals(time) == false)) {
                            //if ( activeTarget.checkEND( ai.getAbility().getENDCost(), false) > 0  ) {
                            boolean burnStun = ai.getBooleanValue("Attack.BURNSTUN");
                            if (checkEND(ai.getAbility(), activeTarget, burnStun, true) > 0) {
                                try {
                                    // Trigger each ability that is currently Activatved.  This
                                    // will cause end to be charged and targets to be rehit.
                                    BattleEvent newBe = new BattleEvent(BattleEvent.CONTINUE, ai);

                                    // Don't charge END for abilities that are marked accordingly.
                                    if (ai.getAbility().getChargeContinuingEND() == false) {
                                        // Set the end as paid so it won't be charged.
                                        newBe.setENDPaid(true);
                                    }

                                    be.embedBattleEvent(newBe);
                                    processEvent(newBe);

                                    justActivatedAbilities.add(newBe.getAbility());
                                } catch (BattleEventException bee) {
                                    if (bee.isDisplayError() == true) {
                                        ExceptionWizard.postException(bee);
                                    }
                                    shouldDeactivate = true;
                                }
                            } else {
                                shouldDeactivate = true;
                            }
                        }

                        if (shouldDeactivate == true) {
                            // Don't have enough END, so shut it down...
                            BattleEvent newBe = new BattleEvent(BattleEvent.DEACTIVATE, ai);
                            //processDeactivate(newBe);
                            //be.embedBattleEvent(newBe);
                            be.embedBattleEvent(newBe);
                            processEvent(newBe);

                            justActivatedAbilities.add(ai.getAbility());
                        }
                    }

                    // Activate the Normally On powers
                    // We would normally want to activate normally on powers after END was charged
                    // for currently active powers.  However, if we do that, there is a race condition
                    // in which already activated powers, with activation limitaiton, fail activation
                    // and then are immediately reactivated do to normally on.

                    // To Avoid this, we record the abilities that we just activated/deactivated in ability Set
                    // and check that the ability is not in the Set before activating it...
                    AbilityIterator iterator = activeTarget.getAbilities();
                    while (iterator.hasNext()) {
                        ability = iterator.nextAbility();
                        ability = ability.getInstanceGroup().getCurrentInstance();
                        if (ability.isNormallyOn() == true && ability.isActivated(activeTarget) == false && ability.isEnabled(activeTarget) && activeTarget.checkEND(ability.getENDCost(true), false) > 0 && setContainsAbility(justActivatedAbilities, ability) == false) {
                            BattleEvent newBe = ability.getActivateAbilityBattleEvent(ability, null, activeTarget);

                            be.embedBattleEvent(newBe);
                            processEvent(newBe);
                        }
                    }

                    activeTarget.setPrephaseDone(true);
                    be.addPrephaseDoneEvent(activeTarget, true);

                    // Perform the AutoStunRecovery and AutoUnconscious
                    if (activeTarget.isUnconscious() && activeTarget.autoUnconscious()) {
                        Ability recovery = Battle.getRecoveryAbility();
                        Ability pass = Battle.getPassAbility();
                        Ability unstun = Battle.getStunRecoveryAbility();
                        if (recovery != null && pass != null && unstun != null) {
                            if (activeTarget.isStunned() && unstun.isEnabled(activeTarget)) {
                                // activateAndEmbed( unstun, be );
                                battle.addEvent(new BattleEvent(unstun));
                            } else if (recovery.isEnabled(activeTarget)) {
                                // activateAndEmbed( recovery, be);
                                battle.addEvent(new BattleEvent(recovery));
                            } else if (pass.isEnabled(activeTarget)) {
                                // activateAndEmbed(pass,be);
                                battle.addEvent(new BattleEvent(pass));
                            }
                        }
                    } else if (activeTarget.isStunned() && activeTarget.autoStunRecovery()) {
                        Ability unstun = Battle.getStunRecoveryAbility();
                        if (unstun != null) {
                            if (unstun.isEnabled(activeTarget)) {
                                //activateAndEmbed( unstun, be );
                                battle.addEvent(new BattleEvent(unstun));
                            }
                        }
                    }
                }
            } else if (activeTarget.isPostTurn() == true) {
                // Check for AutoPost12
                if (activeTarget.autoPost12() == true) {
                    Ability recovery = Battle.getRecoveryAbility();
                    Ability pass = Battle.getPassAbility();
                    if (recovery != null && pass != null) {
                        if (recovery.isEnabled(activeTarget)) {
                            //activateAndEmbed( recovery, be);
                            battle.addEvent(new BattleEvent(recovery));
                        } else {
                            //activateAndEmbed( pass, be,false);
                            battle.addEvent(new BattleEvent(pass));
                        }
                    }
                }
            }
        }
    }

    /** Checks a Set for the presence of an ability */
    private boolean setContainsAbility(Set set, Ability ability) {
        Iterator i = set.iterator();
        while (i.hasNext()) {
            if (ability.equals(i.next())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Does the presegment processing for an individual target.
     *
     * This is executed for every target at beginning of ability new segment, just
     * after the current Time has been updated.
     *
     * This method runs the Target.processSegmentAdvance method for the target
     * and then runs the presegment method for all effect current applied to
     * the target, removing them if presegment returns true.
     *
     *
     * @param be BattleEvent preforming advanceSegment.  This will always be non-null.
     * @param target Target to process advance for.  Guaranteed non-null.
     * @throws BattleEventException BattleEventException that occurred, if any.
     */
    public void processSegmentAdvance(BattleEvent be, Target target) throws BattleEventException {
        Chronometer time = battle.getTime();
        target.processSegmentAdvance(be, time);

        //be.addBattleMessage( activeTarget.getName() + " executed Prephase actions", BattleEvent.MSG_NOTICE);
        Effect effect;
        Ability ability;
        int count, index;

        List<Effect> removeList = new ArrayList<Effect>();
        List<Effect> effectList = new ArrayList<Effect>();

        // Build list of Effect currently in effect
        count = target.getEffectCount();
        ;
        for (index = 0; index < count; index++) {
            effect = target.getEffect(index);
            // Collect ability seperate copy of the coditions
            effectList.add(effect);
        }

        // Run prephase for each effect and determine if effect needs to be removed
        for (index = 0; index < effectList.size(); index++) {
            effect = effectList.get(index);
            if (target.hasEffect(effect)) {
                if (effect.presegment(be, target)) {
                    removeList.add(effect);
                }
            }
        }

        // Actually remove the effects
        for (index = 0; index < removeList.size(); index++) {
            effect = (Effect) removeList.get(index);
            // If ActiveTarget has the effect:
            // Execute Prephase
            // Remove Effect if prephase returns true
            if (target.hasEffect(effect)) {
                // Effect is still there
                effect.removeEffect(be, target);
            }
        }
    }

    /**
     * @param be
     * @param target
     * @throws BattleEventException
     */
    public void doPostturn(BattleEvent be, Target target) throws BattleEventException {
        if (battle == null) {
            return;
        }

        if (target != null) {
            //be.addBattleMessage( activeTarget.getName() + " executed Prephase actions", BattleEvent.MSG_NOTICE);
            Effect effect;
            Ability ability;
            int count, index;

            // Build list of Effect currently in effect
            count = target.getEffectCount();
            List<Effect> removeList = new ArrayList<Effect>();
            List<Effect> effectList = new ArrayList<Effect>();
            for (index = 0; index < count; index++) {
                effect = target.getEffect(index);
                // Collect ability seperate copy of the coditions
                effectList.add(effect);
            }

            // Run prephase for each effect and determine if effect needs to be removed
            for (index = 0; index < effectList.size(); index++) {
                effect = effectList.get(index);
                if (target.hasEffect(effect)) {
                    if (effect.postturn(be, target)) {
                        removeList.add(effect);
                    }
                }
            }

            // Actually remove the effects
            for (index = 0; index < removeList.size(); index++) {
                effect = removeList.get(index);
                // If ActiveTarget has the effect:
                // Execute Prephase
                // Remove Effect if prephase returns true
                if (target.hasEffect(effect)) {
                    // Effect is still there
                    effect.removeEffect(be, target);
                }
            }
        }
    }

    /* Executed Just after the segment is advance, prior to the Battle broadcasting the advancement.
     * At this point, the active target hasn't yet been updated, but the time Chronometer will represent
     * the current time.
     */
    /**
     * @param be
     * @throws BattleEventException  */
    public void processSegmentAdvance(BattleEvent be) throws BattleEventException {
        Set v = battle.getCombatants();
        Iterator<Target> i = v.iterator();
        while (i.hasNext()) {
            Target t = i.next();
            processSegmentAdvance(be, t);
        }
    }

    /**
     * @param bee
     */
    public void displayBattleError(final BattleEventException bee) {

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                JOptionPane.showMessageDialog(null, bee.getMessage(), "Battle Event Message", JOptionPane.OK_OPTION);
            }
        });
    }

    /**
     * @param be
     * @param target
     * @param body
     */
    public static void addKnockback(BattleEvent be, Target target, String knockbackGroup, int body) {
        KnockbackUndoable ku = be.addKnockbackBody(target, knockbackGroup, body);
        //KnockbackUndoable ku = new KnockbackUndoable(be, index, target, knockbackGroup, body);
        be.addUndoableEvent(ku);
    }

    /**
     * Adds effect based information to the Knockback Entry.
     *
     * Effect based information includes Knockback resistence and mass difference of the target
     * that might affect the knockback calculation.
     *
     * Knockback resistence decreases both the distance caused and the damage caused by the knockback.
     *
     * Mass resistence decreases/increases the distance, but does not affect the damage caused by the knockback.
     *
     * Only the very last adjustKnockback call will be used to determine resistence/mass for the target.  In
     * general, this is not a problem since resistence/mass modifiers should be the same for all effects applied
     * to a target.
     *
     * @param be The BattleEvent in which to store the information.
     * @param effect The Effect from which to pull resistence/mass information.
     * @param target The Target to apply the information to.
     * @param knockbackGroup The knockback group currently being processed.
     */
    public static void adjustKnockback(BattleEvent be, Effect effect, Target target, String knockbackGroup) {
        Integer resistence;
        int kbindex;

        kbindex = be.getKnockbackIndex(target, knockbackGroup);
        if (kbindex != -1) {
            if ((resistence = effect.getIntegerValue("Knockback.RESISTANCE")) != null) {
                be.addIndexed(kbindex, "Knockback", "RESISTANCE", resistence);
            }

            if ((resistence = effect.getIntegerValue("Knockback.MASS")) != null) {
                be.addIndexed(kbindex, "Knockback", "MASS", resistence);
            }
        }
    }

    /** Builds a KnockbackModifierList for the provided target/battleEvent.
     *
     * This method will populate a KnockbackModifiersList for the provided target given the
     * battleEvent.  If a null KnockbackModifiersList is provided, it will create a new
     * knockbackModifiersList.
     *
     */
    public static KnockbackModifiersList buildKnockbackModifiersList(BattleEvent battleEvent, Target target, String knockbackGroup, KnockbackModifiersList kml) {
        // If KML is null, create a new list and populate it properly.
        if (kml == null) {
            kml = new KnockbackModifiersList();
        }

        kml.setKnockbackRollBase(2);

        int eindex;
        Effect effect;

        //
        // This needs to copy modifiers from the ability/maneuver
        if (battleEvent.isKillingAttack()) {
            // Add the +1d6 for killing attack.
            kml.addKnockbackRollModifier("Killing Attack", +1);
        }

        Ability ability = battleEvent.getAbility();
        if (ability != null) {

            int index = ability.getIndexedSize("KnockbackRoll");
            for (; index >= 0; index--) {
                if (ability.getIndexedBooleanValue(index, "KnockbackRoll", "ACTIVE")) {
                    kml.addKnockbackRollModifier(
                            ability.getIndexedStringValue(index, "KnockbackRoll", "DESCRIPTION"),
                            ability.getIndexedIntegerValue(index, "KnockbackRoll", "VALUE").intValue());
                }
            }
        }

        ability = battleEvent.getManeuver();
        if (ability != null) {

            int index = ability.getIndexedSize("KnockbackRoll");
            for (; index >= 0; index--) {
                if (ability.getIndexedBooleanValue(index, "KnockbackRoll", "ACTIVE")) {
                    kml.addKnockbackRollModifier(
                            ability.getIndexedStringValue(index, "KnockbackRoll", "DESCRIPTION"),
                            ability.getIndexedIntegerValue(index, "KnockbackRoll", "VALUE").intValue());
                }
            }
        }

        // Add the Target Based Modifiers
        int count = target.getEffectCount();
        for (eindex = 0; eindex < count; eindex++) {
            effect = target.getEffect(eindex);
            effect.addKnockbackModifiers(battleEvent, kml, knockbackGroup);
        }

        // Add a Generic modifier with 0 for value.
        kml.addKnockbackRollModifier("Generic", 0);

        return kml;

    }

    /**
     * Calculates the Knockback DistanceFromCollision and Damage for a particular target/knockbackGroup.
     * @param be
     * @throws BattleEventException
     */
    public static void calculateKnockback(BattleEvent be, Target target, String knockbackGroup) {
        int kbindex = be.getKnockbackIndex(target, knockbackGroup);

        Dice d = be.getKnockbackAmountRoll(kbindex);
        int totalBody = be.getTotalKnockback(kbindex);

        int targetResistance = target.getKnockbackResistance();

        Integer i;
        i = be.getIndexedIntegerValue(kbindex, "Knockback", "RESISTANCE");
        int effectResistance = (i == null) ? 0 : i.intValue();

        i = be.getIndexedIntegerValue(kbindex, "Knockback", "MASS");
        int mass = (i == null) ? 0 : i.intValue();

        String doubleKB = null;
        String doubleKBAbilityValue = null;
        String doubleKBManeuverValue = null;
        if (be.getAbility().getStringValue("Ability.DOUBLEKB") != null) {
            doubleKBAbilityValue = new String(be.getAbility().getStringValue("Ability.DOUBLEKB"));

        }

        if (be.getManeuver() != null && be.getManeuver().getStringValue("Ability.DOUBLEKB") != null) {
            doubleKBManeuverValue = new String(be.getAbility().getStringValue("Ability.DOUBLEKB"));
        }
        if (doubleKBAbilityValue != null && doubleKBAbilityValue.equals("2X KB")) {
            totalBody *= 2;
            be.addBattleMessage(new champions.battleMessage.LegacyBattleMessage(target.getName() + " will be subjected to 2x knockback from attack.", BattleEvent.MSG_ABILITY)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( target.getName() + " will be subjected to 2x knockback from attack.", BattleEvent.MSG_ABILITY)); // .addMessage( target.getName() + " will be subjected to 2x knockback from attack.", BattleEvent.MSG_ABILITY);
        } else if (doubleKBManeuverValue != null && doubleKBManeuverValue.equals("2X KB")) {
            totalBody *= 2;
            be.addBattleMessage(new champions.battleMessage.LegacyBattleMessage(target.getName() + " will be subjected to 2x knockback from attack.", BattleEvent.MSG_ABILITY)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( target.getName() + " will be subjected to 2x knockback from attack.", BattleEvent.MSG_ABILITY)); // .addMessage( target.getName() + " will be subjected to 2x knockback from attack.", BattleEvent.MSG_ABILITY);
        } else if (doubleKBAbilityValue != null && doubleKBAbilityValue.equals("1.5X KB")) {
            totalBody *= 1.5;
            be.addBattleMessage(new champions.battleMessage.LegacyBattleMessage(target.getName() + " will be subjected to 1.5x knockback from attack.", BattleEvent.MSG_ABILITY)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( target.getName() + " will be subjected to 1.5x knockback from attack.", BattleEvent.MSG_ABILITY)); // .addMessage( target.getName() + " will be subjected to 1.5x knockback from attack.", BattleEvent.MSG_ABILITY);
        } else if (doubleKBManeuverValue != null && doubleKBManeuverValue.equals("1.5X KB")) {
            totalBody *= 1.5;
            be.addBattleMessage(new champions.battleMessage.LegacyBattleMessage(target.getName() + " will be subjected to 1.5x knockback from attack.", BattleEvent.MSG_ABILITY)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( target.getName() + " will be subjected to 1.5x knockback from attack.", BattleEvent.MSG_ABILITY)); // .addMessage( target.getName() + " will be subjected to 1.5x knockback from attack.", BattleEvent.MSG_ABILITY);
        }

        int realAmount = totalBody - d.getStun() - effectResistance - targetResistance;

        int amount = Math.max(0, realAmount);

        int distance = Math.max(0, amount - mass);

        be.addIndexed(kbindex, "Knockback", "AMOUNT", new Integer(amount), true);
        be.addIndexed(kbindex, "Knockback", "DISTANCE", new Integer(distance), true);
        be.addIndexed(kbindex, "Knockback", "KNOCKDOWN", realAmount >= 0 ? "TRUE" : "FALSE", true);

    }

    /**
     * @param be
     * @param obstructions
     * @param effect
     * @throws BattleEventException
     * @return True if the Damage penetrated the obstruction, false if not.
     */
    static public boolean processObstructions(BattleEvent be, ObstructionList obstructions, Effect effect, String knockbackGroup)
            throws BattleEventException {

        boolean pentratedObstructions = true;

        int oindex;
        Target obstruction;

        if (obstructions != null) {
            IndexIterator ii = obstructions.getObstructionIterator();
            while (ii.hasNext() && effect.damageSubeffectsRemain()) {
                oindex = ii.nextIndex();
                obstruction = obstructions.getObstruction(oindex);

                if (!obstruction.isDead()) { // Make sure it isn't destroyed
                    int initialBody = effect.getTotalBodyDamage();

                    // Muck up the effect to limit the amount of damage applied...

                    double initialLimit = 0;
                    for (int index = 0; index < effect.getSubeffectCount(); index++) {
                        if (effect.getSubeffectVersusObject(index).equals("BODY")) {
                            initialLimit = effect.getSubeffectLimit(index);

                            // If Body is > 0, limit is 0.  If Body <= 0, limit
                            // is -1 * Max Body.
                            int current = obstruction.getCurrentStat("BODY");
                            int max = obstruction.getAdjustedStat("BODY");

                            if (current > 0) {
                                effect.setSubeffectLimit(index, 0);
                            } else {
                                effect.setSubeffectLimit(index, -1 * max);
                            }
                        }
                    }

                    applyEffectToTarget(be, obstruction, effect, knockbackGroup, false);

                    // Put the limits back
                    for (int index = 0; index < effect.getSubeffectCount(); index++) {
                        if (effect.getSubeffectVersusObject(index).equals("BODY")) {
                            effect.setSubeffectLimit(index, initialLimit);
                        }
                    }

                    int currentBody = effect.getTotalBodyDamage();
                    // This is a bit of a hack to allow for stun only attack to pass through
                    // obstructions.  Although it is not always the case, there are attacks that
                    // are unaffected by obstacles (EGO) but do no body.  Normally, 0 body
                    // attack don't penetrate, but we will allow them if the body wasn't affected
                    // by the obstacle.
                    if (initialBody != currentBody && currentBody <= 0) {
                        be.addBattleMessage(new champions.battleMessage.LegacyBattleMessage("The Obstructions between " + be.getSource().getName() + " and the target absorbed all attack body.", BattleEvent.MSG_COMBAT)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage("The Obstructions between " + be.getSource().getName() + " and the target absorbed all attack body.", BattleEvent.MSG_COMBAT)); // .addMessage("The Obstructions between " + be.getSource().getName() + " and the target absorbed all attack body.", BattleEvent.MSG_COMBAT);

                        pentratedObstructions = false;
                        break;
                    }
                }
            }
        }

        return pentratedObstructions;
    }

    /**
     * @param be  */
    public void buildSummaryAndStats(BattleEvent be) {
        ActivationInfo ai = be.getActivationInfo();
        Target source = be.getSource();
        Ability ability = ai.getAbility();

        int tindex, tcount;
        Target target;
        int body, stun;
        String criticalEffects[] = new String[10];

        // Variable to Store critical effects.
        int ceCount;

        int eindex, ecount;
        Effect effect;
        String eventType;
        int sindex, scount;
        String versusType, versus, effectType;
        Double amount;
        String effectName, summary;
        boolean dead;
        int targetStatIndex, totalStatIndex;
        int totalHits, totalMisses, totalBody, totalStun;
        int targetHits, targetMisses, targetBody, targetStun;
        Integer i;

        // Grab the index for the battle Stats.
        if ((totalStatIndex = source.findIndexed("BattleStat", "TARGETNAME", "BATTLETOTALS")) == -1) {
            totalStatIndex = source.createIndexed("BattleStat", "TARGETNAME", "BATTLETOTALS");
        }

        // Grab the total Hits for source.
        i = source.getIndexedIntegerValue(totalStatIndex, "BattleStat", "HITS");
        totalHits = (i == null) ? 0 : i.intValue();

        // Grab the total Misses for source.
        i = source.getIndexedIntegerValue(totalStatIndex, "BattleStat", "MISSES");
        totalMisses = (i == null) ? 0 : i.intValue();

        // Grab the total Body for source.
        i = source.getIndexedIntegerValue(totalStatIndex, "BattleStat", "BODY");
        totalBody = (i == null) ? 0 : i.intValue();

        // Grab the total Stun for source.
        i = source.getIndexedIntegerValue(totalStatIndex, "BattleStat", "STUN");
        totalStun = (i == null) ? 0 : i.intValue();

        tcount = ai.getIndexedSize("Target");
        for (tindex = 0; tindex < tcount; tindex++) {
            ceCount = 0;
            body = 0;
            stun = 0;
            dead = false;
            target = (Target) ai.getIndexedValue(tindex, "Target", "TARGET");

            if ((targetStatIndex = source.findIndexed("BattleStat", "TARGETNAME", target.getName())) == -1) {
                targetStatIndex = source.createIndexed("BattleStat", "TARGETNAME", target.getName());
            }

            // Grab the target Hits for source.
            i = source.getIndexedIntegerValue(targetStatIndex, "BattleStat", "HITS");
            targetHits = (i == null) ? 0 : i.intValue();

            // Grab the target Misses for source.
            i = source.getIndexedIntegerValue(targetStatIndex, "BattleStat", "MISSES");
            targetMisses = (i == null) ? 0 : i.intValue();

            // Grab the target Body for source.
            i = source.getIndexedIntegerValue(targetStatIndex, "BattleStat", "BODY");
            targetBody = (i == null) ? 0 : i.intValue();

            // Grab the target Stun for source.
            i = source.getIndexedIntegerValue(targetStatIndex, "BattleStat", "STUN");
            targetStun = (i == null) ? 0 : i.intValue();

            if (ai.getIndexedBooleanValue(tindex, "Target", "HIT") == false) {
                //System.out.println( source.getName() + "'s " + ability.getInstanceName() + " missed " + target.getName() );

                // Add a miss stat
                totalMisses += 1;
                targetMisses += 1;
            } else {
                // Scored a hit (actually, this could be multiple hits due to autofire)
                totalHits += 1;
                targetHits += 1;

                ecount = be.getUndoableEventCount();
                for (eindex = 0; eindex < ecount; eindex++) {
                    Undoable undoable = be.getUndoableEvent(eindex);
                    if (undoable instanceof EffectUndoable) {
                        EffectUndoable eu = (EffectUndoable) undoable;
                        effect = eu.getEffect();

                        if (effect == null) {
                            continue;
                        }

                        effectName = effect.getName();
                        if (effectName.equals("Unconscious")) {
                            criticalEffects[ceCount++] = "Unconscious";
                        } else if (effectName.equals(target.getDeadEffectName())) {
                            dead = true;
                        } else if (effectName.equals("Dying")) {
                            criticalEffects[ceCount++] = "Dying";
                        } else if (effectName.equals("Bleeding")) {
                            criticalEffects[ceCount++] = "Bleeding";
                        } else if (effectName.equals("Knocked Down")) {
                            criticalEffects[ceCount++] = "Knocked Down";
                        } else if (effectName.equals("Stunned")) {
                            criticalEffects[ceCount++] = "Stunned";
                        } else {
                            scount = effect.getIndexedSize("Subeffect");
                            for (sindex = 0; sindex < scount; sindex++) {
                                effectType = effect.getIndexedStringValue(sindex, "Subeffect", "EFFECTTYPE");
                                versusType = effect.getIndexedStringValue(sindex, "Subeffect", "VERSUSTYPE");
                                if (effectType != null && effectType.equals("DAMAGE") && versusType != null && versusType.equals("STAT")) {
                                    versus = effect.getIndexedStringValue(sindex, "Subeffect", "VERSUS");
                                    if (versus != null && versus.equals("BODY")) {
                                        amount = effect.getIndexedDoubleValue(sindex, "Subeffect", "ADJUSTEDAMOUNT");
                                        if (amount != null) {
                                            body += amount.intValue();
                                        }
                                    } else if (versus != null && versus.equals("STUN")) {
                                        amount = effect.getIndexedDoubleValue(sindex, "Subeffect", "ADJUSTEDAMOUNT");
                                        if (amount != null) {
                                            stun += amount.intValue();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                totalBody += body;
                targetBody += body;
                totalStun += stun;
                targetStun += stun;

                summary = source.getName() + "'s " + ability.getInstanceName() + " hit " + target.getName() + " for " + Integer.toString(stun) + " stun and " + Integer.toString(body) + " body damage. ";
                if (dead == true) {
                    summary = summary + target.getName() + " is DEAD.";
                } else if (ceCount > 0) {
                    summary = summary + target.getName() + " is ";
                    for (int j = 0; j < ceCount; j++) {
                        summary = summary + criticalEffects[j];
                        if (j + 1 < ceCount) {
                            summary = summary + ", ";
                        }
                    }
                    summary = summary + ".";
                }
                System.out.println(summary);
            }

            // Record the target stat...
            source.addIndexed(targetStatIndex, "BattleStat", "HITS", new Integer(targetHits), true);
            source.addIndexed(targetStatIndex, "BattleStat", "MISSES", new Integer(targetMisses), true);
            source.addIndexed(targetStatIndex, "BattleStat", "BODY", new Integer(targetBody), true);
            source.addIndexed(targetStatIndex, "BattleStat", "STUN", new Integer(targetStun), true);
        }
        // Record the total stat...
        source.addIndexed(totalStatIndex, "BattleStat", "HITS", new Integer(totalHits), true);
        source.addIndexed(totalStatIndex, "BattleStat", "MISSES", new Integer(totalMisses), true);
        source.addIndexed(totalStatIndex, "BattleStat", "BODY", new Integer(totalBody), true);
        source.addIndexed(totalStatIndex, "BattleStat", "STUN", new Integer(totalStun), true);
    }

    /**
     * @param be
     * @param effectList
     * @param targetGroup
     * @return  */
    public static DetailList createKillingEffects(BattleEvent be, DetailList effectList, String targetGroup) {
        Ability ability = be.getAbility();

        Dice bodyDice, stunDice;

        String defense = ability.getDefense();

        bodyDice = be.getDiceRoll("DamageDie", targetGroup);
        //  stunDice = be.getDiceRoll("StunDie", targetGroup);

        if (bodyDice != null) {

            Effect effect = new Effect("Killing Damage", "INSTANT");
            // Add Effects
            if (be.doesBody()) {
                effect.addDamageSubeffect("HKABody", "BODY", bodyDice.getStun().doubleValue(), defense, "KILLING");
            }
            if (be.doesStun()) {
                effect.addDamageSubeffect("HKAStun", "STUN", bodyDice.getStun().doubleValue(), defense, "KILLING");
            }

            if (be.doesECVBody()) {
                effect.addDamageSubeffect("HKAEBody", "EBODY", bodyDice.getStun().doubleValue(), defense, "KILLING");
            }
            /*    if ( be.doesKnockback() ) {
            // Find knockbackGroup

            be.addKBModifier( "Killing Attack", knockbackGroup, 1 );
            } */

            effectList.createIndexed("Effect", "EFFECT", effect);
        }

        return effectList;
    }

    /**
     * @param be
     * @param effectList
     * @return
     */
//    public static DetailList createNormalEffects(BattleEvent be, DetailList effectList) {
//        Ability ability = be.getAbility();
//
//        Dice dice;
//
//        String defense = ability.getDefense();
//
//        int dindex;
//        if ( (dindex = be.findIndexed("Die","NAME", "DamageDie")) != -1 ) {
//            dice = be.getIndexedDiceValue(dindex,"Die","DIEROLL" );
//            Effect effect = new Effect( "Damage", "INSTANT" );
//
//            String special;
//
//            if ( be.isNND() ) {
//                special =  "NND";
//                String attackType = be.getNNDAttackType();
//                String nnddefense = be.getNNDDefense();
//                effect.add( "NND.ATTACKTYPE", attackType ,true);
//                effect.add( "NND.DEFENSE", nnddefense,true );
//            } else {
//                special = "NORMAL";
//            }
//
//            double stun = dice.getStun().doubleValue();
//            double body = dice.getBody().doubleValue();
//
//            // Add Effects
//            if ( be.doesStun() ) {
//                //effect.addSubeffectInfo( "HAStun", "DAMAGE", defense, "NORMAL", "STAT", "STUN",dice.getStun() );
//                effect.addDamageSubeffect( "HAStun", "STUN", stun, defense, special );
//
//            }
//
//            if ( be.doesBody() ) {
//                // effect.addSubeffectInfo( "HABody", "DAMAGE", defense, "NORMAL", "STAT", "BODY",dice.getBody() );
//                effect.addDamageSubeffect( "HABody", "BODY", body, defense, special );
//            }
//
//            if ( be.doesECVBody() ) {
//                effect.addDamageSubeffect( "HAEBody", "EBODY", body, defense, special );
//            }
//
//            effectList.createIndexed(  "Effect","EFFECT",effect) ;
//        }
//        return effectList;
//    }
    /**
     * @param be
     * @param effectList
     * @return
     */
    public static DetailList createNormalEffects(BattleEvent be, DetailList effectList, String targetGroup, int targetReferenceNumber) {
        Ability ability = be.getAbility();

        Dice dice;

        String defense = ability.getDefense();

        dice = be.getDiceRoll("DamageDie", targetGroup);

        if (dice != null) {

            Effect effect = new Effect("Damage", "INSTANT");

            String special;

            if (be.isNND()) {
                special = "NND";
                String attackType = be.getNNDAttackType();
                String nnddefense = be.getNNDDefense();
                effect.add("NND.ATTACKTYPE", attackType, true);
                effect.add("NND.DEFENSE", nnddefense, true);
            } else {
                special = "NORMAL";
            }

            double stun = dice.getStun().doubleValue();
            double body = dice.getBody().doubleValue();



            // Add Effects
            if (be.doesStun()) {
                effect.addDamageSubeffect("HAStun", "STUN", stun, defense, special);
            }
            if (be.doesBody()) {
                effect.addDamageSubeffect("HABody", "BODY", body, defense, special);
            }
            if (be.doesECVBody()) {
                effect.addDamageSubeffect("HAEBody", "EBODY", body, defense, special);
            }

            effectList.createIndexed("Effect", "EFFECT", effect);
        }
        return effectList;
    }

//    /**
//     * @param effectList
//     * @param dc
//     * @param defense
//     */
//    public void createNormalEffects(DetailList effectList, Double dc, String defense) {
//        createNormalEffects(effectList, dc.doubleValue(), defense);
//    }
//    /**
//     * @param effectList
//     * @param dc
//     * @param defense
//     */
//    public void createNormalEffects(DetailList effectList, double dc, String defense) {
//        Dice dice;
//
//        dice = ChampionsUtilities.DCToNormalDice( dc );
//
//        createNormalEffects(effectList, dice, defense);
//
//    }
//    /**
//     * @param effectList
//     * @param dice
//     * @param defense
//     */
//    public void createNormalEffects(DetailList effectList, Dice dice, String defense) {
//        Effect effect = new Effect( "Damage", "INSTANT" );
//
//        // Add Effects
//        //effect.addSubeffectInfo( "HAStun", "DAMAGE", defense, "NORMAL", "STAT", "STUN",dice.getStun() );
//        effect.addDamageSubeffect( "HAStun", "STUN", dice.getStun().doubleValue(), defense, "NORMAL" );
//        // effect.addSubeffectInfo( "HABody", "DAMAGE", defense, "NORMAL", "STAT", "BODY",dice.getBody() );
//        effect.addDamageSubeffect( "HABody", "BODY", dice.getBody().doubleValue(), defense, "NORMAL" );
//
//        effectList.createIndexed(  "Effect","EFFECT",effect) ;
//    }
    /**
     * @param effectList
     * @param dice
     * @param defense
     */
    static public Effect createNormalEffects(Dice dice, String defense) {
        Effect effect = new Effect("Damage", "INSTANT");

        // Add Effects
        //effect.addSubeffectInfo( "HAStun", "DAMAGE", defense, "NORMAL", "STAT", "STUN",dice.getStun() );
        effect.addDamageSubeffect("HAStun", "STUN", dice.getStun().doubleValue(), defense, "NORMAL");
        // effect.addSubeffectInfo( "HABody", "DAMAGE", defense, "NORMAL", "STAT", "BODY",dice.getBody() );
        effect.addDamageSubeffect("HABody", "BODY", dice.getBody().doubleValue(), defense, "NORMAL");

        return effect;
    }

    static public boolean isENDSourceActive(Ability ability, Target source) {
        ENDSource endSource;
        String endName;

        // Now ActivePoints is the final amount of end to charge.  Look up the END source(s), verify them, and checkEND on them.
        endName = ability.getPrimaryENDSource();
        if ((endSource = source.getENDSource(endName)) == null) {
            return false;
        }

        endName = ability.getSecondaryENDSource();
        if (endName != null) {
            if ((endSource = source.getENDSource(endName)) == null) {
                return false;
            }
        }

        return true;
    }

    /**
     * @param ability
     * @param source
     * @param burnStun
     * @return  */
    static public int checkEND(Ability ability, Target source, boolean burnStun, boolean continuing) {
        // BattleEvent be = new BattleEvent(ability);
        // be.setSource(source);
        int shots;
        int maxshots = Integer.MAX_VALUE;
        ENDSource endSource;
        String endName;

        int endCost = ability.getENDCost(continuing);

        // Now ActivePoints is the final amount of end to charge.  Look up the END source(s), verify them, and checkEND on them.
        endName = ability.getPrimaryENDSource();
        if ((endSource = source.getENDSource(endName)) == null) {
            return 0;
        }
        shots = endSource.checkEND(endCost, burnStun);
        if (shots == 0) {
            return 0;
        }
        if (shots < maxshots) {
            maxshots = shots;
        }

        endName = ability.getSecondaryENDSource();
        if (endName != null) {
            if ((endSource = source.getENDSource(endName)) == null) {
                return 0;
            }
            shots = endSource.checkEND(endCost, burnStun);
            if (shots == 0) {
                return 0;
            }
            if (shots < maxshots) {
                maxshots = shots;
            }
        }

        return maxshots;
    }

    static public boolean canBurnStun(Ability ability, Target source) {

        ENDSource endSource;
        String endName;

        // Now ActivePoints is the final amount of end to charge.  Look up the END source(s), verify them, and checkEND on them.
        endName = ability.getPrimaryENDSource();
        if ((endSource = source.getENDSource(endName)) == null) {
            return false;
        }
        if (endSource.canBurnStun() == false) {
            return false;
        }

        endName = ability.getSecondaryENDSource();
        if (endName != null) {
            if ((endSource = source.getENDSource(endName)) == null) {
                return false;
            }
            if (endSource.canBurnStun() == false) {
                return false;
            }
        }

        return true;
    }

    /**
     * @param be
     * @return  */
    static public int checkEND(BattleEvent be) {
        Ability ability = be.getAbility();
        Ability maneuver = be.getManeuver();
        ActivationInfo ai = be.getActivationInfo();
        boolean continuing = ai.isContinuing();
        Power power;
        int endCost;
        Double advCost;
        Double endMultiplier;
        String primary, secondary;
        ENDSource endSource;
        Object o;
        Target source;
        int maxshots = Integer.MAX_VALUE;
        int shots;
        int index;
        boolean burnStun;

        source = be.getSource();

        burnStun = ai.getBooleanValue("Attack.BURNSTUN");

        if (source == null) {
            return 0;
        }

        if (ability != null) {
            // First Grab the END from the power
            if ((index = be.findIndexed("TempEND", "ABILITY", ability)) != -1) {
                endCost = be.getIndexedIntegerValue(index, "TempEND", "AMOUNT").intValue();
            } else {
                endCost = ability.getENDCost(continuing);
            }

            // Now ActivePoints is the final amount of end to charge.  Look up the END source(s), verify them, and checkEND on them.
            primary = ability.getPrimaryENDSource();
            if ((endSource = source.getENDSource(primary)) == null) {
                return 0;
            }
            shots = endSource.checkEND(be, endCost, burnStun);
            if (shots == 0) {
                return 0;
            }
            if (shots < maxshots) {
                maxshots = shots;
            }

            secondary = ability.getSecondaryENDSource();
            if (secondary != null) {
                if ((endSource = source.getENDSource(secondary)) == null) {
                    return 0;
                }
                shots = endSource.checkEND(be, endCost, burnStun);
                if (shots == 0) {
                    return 0;
                }
                if (shots < maxshots) {
                    maxshots = shots;
                }
            }
        }

        if (maneuver != null) {
            if ((index = be.findIndexed("TempEND", "ABILITY", maneuver)) != -1) {
                endCost = be.getIndexedIntegerValue(index, "TempEND", "AMOUNT").intValue();
            } else {
                endCost = ability.getENDCost(continuing);
            }

            // Now ActivePoints is the final amount of end to charge.  Look up the END source(s), verify them, and checkEND on them.
            primary = ability.getPrimaryENDSource();
            if (primary != null) {
                if ((endSource = source.getENDSource(primary)) == null) {
                    return 0;
                }
                shots = endSource.checkEND(be, endCost, burnStun);
                if (shots == 0) {
                    return 0;
                }
                if (shots < maxshots) {
                    maxshots = shots;
                }
            }
            secondary = ability.getSecondaryENDSource();
            if (secondary != null) {
                if ((endSource = source.getENDSource(secondary)) == null) {
                    return 0;
                }
                shots = endSource.checkEND(be, endCost, burnStun);
                if (shots == 0) {
                    return 0;
                }
                if (shots < maxshots) {
                    maxshots = shots;
                }
            }
        }
        return maxshots;
    }

    /**
     * @param be
     * @throws BattleEventException  */
    public static void chargeEND(BattleEvent be) throws BattleEventException {
        int endCost, count, index;
        Ability ability = be.getAbility();
        ActivationInfo ai = be.getActivationInfo();
        boolean continuing = ai.isContinuing();
        String sourceName;
        ENDSource endSource;
        boolean burnStun;

        if (be.isENDPaid() == false && ability != null) {
            Target source = be.getSource();
            burnStun = ai.getBooleanValue("Attack.BURNSTUN");

            if (source == null) {
                throw new BattleEventException("Null Source specified in BattleEvent while applying END");
            }

            if (ability.getBooleanValue("Ability.ISAUTOFIRE")) {
                count = ai.getIntegerValue("Attack.SHOTS").intValue();
            } else {
                count = 1;   
            }

            // Process END for Ability

            // First Grab the END from the power
            if ((index = be.findIndexed("TempEND", "ABILITY", ability)) != -1) {
                endCost = be.getIndexedIntegerValue(index, "TempEND", "AMOUNT").intValue();
            } else {
                endCost = ability.getENDCost(continuing);
            }

            // Now ActivePoints is the final amount of end to charge.  Look up the END source(s), verify them, and checkEND on them.
            sourceName = ability.getPrimaryENDSource();
            if ((endSource = source.getENDSource(sourceName)) == null) {
                throw new BattleEventException("ENDSource (" + sourceName + ") is not activated." + source);
            }
            endSource.chargeEND(be, endCost, count, burnStun, ability.getInstanceName());
            sourceName = ability.getSecondaryENDSource();
            if (sourceName != null) {
                if ((endSource = source.getENDSource(sourceName)) == null) {
                    throw new BattleEventException("ENDSource (" + sourceName + ") is not activated." + source);
                }
                endSource.chargeEND(be, endCost, count, burnStun, ability.getInstanceName());
            }

            Ability maneuver = be.getManeuver();
            if (maneuver != null) {
                if ((index = be.findIndexed("TempEND", "ABILITY", maneuver)) != -1) {
                    endCost = be.getIndexedIntegerValue(index, "TempEND", "AMOUNT").intValue();
                } else {
                    endCost = maneuver.getENDCost(continuing);
                }

                // Now ActivePoints is the final amount of end to charge.  Look up the END source(s), verify them, and checkEND on them.
                sourceName = ability.getPrimaryENDSource();
                if ((endSource = source.getENDSource(sourceName)) == null) {
                    throw new BattleEventException("ENDSource (" + sourceName + " does not belond to target " + source + ".");
                }
                endSource.chargeEND(be, endCost, count, burnStun, maneuver.getInstanceName());

                sourceName = ability.getSecondaryENDSource();
                if (sourceName != null) {
                    if ((endSource = source.getENDSource(sourceName)) == null) {
                        throw new BattleEventException("ENDSource (" + sourceName + " does not belond to target " + source + ".");
                    }
                    endSource.chargeEND(be, endCost, count, burnStun, maneuver.getInstanceName());
                }
            }

            Integer str = be.getIntegerValue("Normal.STR_USED");
            Integer pushed = be.getIntegerValue("Pushed.STR_USED");
            //jeff desparate hack to endurance reduction working with added str powers
            if (str >0) {
                for(AbilityIterator i = source.getAbilities();i.hasNext();)
                {
                			
                	Ability a = i.next();
                			
                	if(a.getPower() instanceof powerCharacteristic )
                	{
                		ParameterList pl =  ((powerCharacteristic)a.getPower()).getParameterList(a, 0);
                    	String stat = pl.getParameterStringValue("Stat");    	
                		if(stat.equals("STR") && a.isActivated(source))
                		
                		{
                			if(a.hasAdvantage(new advantageReducedEndurance().getName()))
                			{
                				endCost+=a.getENDCost();
                			}
                		}
                	}
                	
                }
            	CharacteristicPrimary STR = source.getCharacteristic("STR");
                endCost+= ChampionsUtilities.strToEnd(pushed == null ? 0 : pushed);
                
                
             
                
               /* int radi = ability.getAdvantageIndex("reducedEndurance");
                if(radi!=-1)
                {	
                	advantageReducedEndurance are = (advantageReducedEndurance)ability.getAdvantage(radi);
                	double multiplier = are.getMultiplier();
                	endCost =  (int) (endCost * multiplier);
                	
                }
                */
                if (endCost > 0) {
                    sourceName = ability.getStrengthENDSource();
                    if ((endSource = source.getENDSource(sourceName)) == null) {
                        throw new BattleEventException("ENDSource (" + sourceName + " does not belond to target " + source);
                    }
                    endSource.chargeEND(be, endCost, count, burnStun, "Strength");
                }
            }

            if (pushed != null) {
                // end cost is normal cost of strength = 1 end per 5 str (counted above)
                // and 1 end per 1 pushed str.
                endCost = pushed;

                if (endCost > 0) {
                    sourceName = ability.getStrengthENDSource();
                    if ((endSource = source.getENDSource(sourceName)) == null) {
                        throw new BattleEventException("ENDSource (" + sourceName + " does not belond to target " + source);
                    }
                    endSource.chargeEND(be, endCost, count, burnStun, "Pushed Strength");
                }
            }

            // Apply any extra end contained in the battleEvent
            DetailList endList;
            if ((endList = be.getEndList()) != null) {
                count = endList.getIndexedSize("End");
                for (index = 0; index < count; index++) {
                    if (endList.getIndexedBooleanValue(index, "End", "PAID") == false) {
                        String reason = endList.getIndexedStringValue(index, "End", "REASON");
                        endCost = endList.getIndexedIntegerValue(index, "End", "END").intValue();
                        if (endCost > 0) {
                            endSource = source.getENDSource("Character");
                            endSource.chargeEND(be, endCost, 1, burnStun, reason);
                            System.out.println("Warning: Extra END for " + reason + "CHARGED to Character");
                        }
                        endList.addIndexed(index, "End", "PAID", "TRUE", true);
                    }
                }
            }

            ai.setTimeOfLastENDPaymet(Battle.currentBattle.getTime().clone());
        }
    }

    /**
     * @param ability
     * @param be
     * @throws BattleEventException  */
    public void activateAndEmbed(Ability ability, BattleEvent be) throws BattleEventException {
        activateAndEmbed(ability, be, true);
    }

    /**
     * @param ability
     * @param be
     * @param advanceTime
     * @throws BattleEventException  */
    public void activateAndEmbed(Ability ability, BattleEvent be, boolean advanceTime) throws BattleEventException {
        BattleEvent newBe = new BattleEvent(ability);
        be.embedBattleEvent(newBe);
        processEvent(newBe);


        if (advanceTime) {
            battle.addEvent(new BattleEvent(BattleEvent.ADVANCE_TIME, false));
        }
    }

    public static AttackTreeNode getProcessAbilityRoot(BattleEvent be) {
        if (be.getType() == BattleEvent.ACTIVATE || be.getType() == BattleEvent.CONTINUE || be.getType() == BattleEvent.CHARGE_END) {
            return new ProcessActivateRootNode("Activate Root", be);
        } else if (be.getType() == BattleEvent.LINKED_ACTIVATE) {
            if (be instanceof SweepBattleEvent) {
                return getProcessSweepActivateRoot(be);
            } else if (be instanceof CombinedAbilityBattleEvent) {
                return getProcessCombinedActivateRoot(be);
            } else {
                return getProcessLinkedActivateRoot(be);
            }
        } else if (be.getType() == BattleEvent.DEACTIVATE) {
            if (be.getAbility() instanceof CombinedAbility) {
                return new CombinedDeactivateRootNode("Combined Deactivate Root", be);
            } else {
                return new ProcessDeactivateRootNode("Deactivate Root", be);
            }
        // return new ProcessDeactivateRootNode("Deactivate Root", be);
        }
        return null;
    }

    public static AttackTreeNode getProcessLinkedActivateRoot(BattleEvent be) {
        // I don't know why I made a method
        return new LinkedActivateRootNode("LinkedActivateRoot", be);
    }

    public static AttackTreeNode getProcessSweepActivateRoot(BattleEvent be) {
        // I don't know why I made a method
        return new SweepActivateRootNode("SweepActivateRoot", be);
    }

    public static AttackTreeNode getProcessCombinedActivateRoot(BattleEvent be) {
        // I don't know why I made a method
        return new CombinedActivateRootNode("CombinedActivateRoot", be);
    }

    public static AttackTreeNode getProcessCombinedDeactivateRoot(BattleEvent be) {
        return new CombinedDeactivateRootNode("CombinedDeactivateRoot", be);
    }

    /** Processes The Attack Tree Node via the Attack Tree Mechanism */
    public static void processAttackTreeNode(AttackTreeNode node) throws BattleEventException {
        AttackTreeModel atm = new AttackTreeModel(node);
        atm.processAttackTree();
    }

    /** Preform the processActivating Step of BattleEvent Process.
     *
     * This method is designed to be called from an AttackTree node.
     *
     * This method should be used in conjunction with the buildPreDelayNodes() method.  The
     * buildPredelayNodes method should be called and the resulting node should be placed in
     * the attackTree just as a child of whatever node calls processStateNew.  For safest
     * use, the buildPredelayNodes should actually be called after processStateNew has
     * been called.
     *
     * ActivationInfo State:
     * On Entry: AI_STATE_NEW
     * On Exit: AI_STATE_PREDELAY
     */
    public static void processStateNew(BattleEvent be) throws BattleEventException {
        Ability ability = be.getAbility();



        if (be.getTimeProcessed() == null) {
            // Make sure we fill in the processed time, in case it slipped by
            Chronometer time = (Chronometer) Battle.getCurrentBattle().getTime().clone();
            be.setTimeProcessed(time);
        }

        if (be.getActivationInfo() == null) {
            throw new BattleEventException("Activation Info is null");
        }

        ActivationInfo ai = be.getActivationInfo();

        // If Ability requires new instance is set Create new Ability Instance.
        // This will prevent subsequent adjustment powers from affecting this power.
        if (ability.getBooleanValue("Ability.REQUIRESNEWINSTANCE")) {
            Ability newAbility;
            if (ability.isModifiableInstance()) {
                newAbility = ability.createChildInstance();
            } else {
                newAbility = ability.getModifiableInstance();
            }
            newAbility.setCreatedAutomatically(true);
            ai.setAbility(newAbility);
            ability = newAbility;
        } else if (ability.isModifiableInstance() == false) {
            Ability newAbility = ability.getModifiableInstance();
            newAbility.setCreatedAutomatically(true);
            ai.setAbility(newAbility);
            ability = newAbility;
        }

        ai.addAbilityLink(ability);
        be.addActivationInfoEvent(be.getAbility(), ai, true);

        Ability maneuver = be.getManeuver();
        if (maneuver != null) {

            // If Maneuver requires new instance is set Create new Ability Instance.
            // This will prevent subsequent adjustment powers from affecting this power.
            if (maneuver.getBooleanValue("Ability.REQUIRESNEWINSTANCE")) {
                Ability newManeuver = maneuver.getInstanceGroup().createNewInstanceFromCurrent();
                ai.setManeuver(newManeuver);
                maneuver = newManeuver;
            }

            ai.addAbilityLink(maneuver);
            be.addActivationInfoEvent(maneuver, ai, true);
        }

        // Determine the Source.  Should already be set, unless Ability is AUTOSOURCE
        // Only Instantanous abilitys should be AUTOSOURCE
        Target source;
        if (be.getSource() == null) {
            if (ability.isAutoSource()) {
                source = Battle.getCurrentBattle().getActiveTarget();
            } else if (ability.getSource() == null) {
                throw (new BattleEventException("Malformed BattleEvent: Ability source is null."));
            } else {
                source = ability.getSource();
            }

            be.setSource(source);
        } else {
            source = be.getSource();
        }

        // Check the ActivationInfo instead of the ability...
        if (!ai.getState().equals(AI_STATE_NEW)) {
            //jeff change throw new BattleEventException("ActivationInfo for " + ability.getInstanceName() + " not in NEW state.");
        }

        // Set ability to Activated
        if (ability.isActivated(source)) {
            throw new BattleEventException("Ability " + ability.getInstanceName() + " already activated.", false);
        }

        be.openMessageGroup(new ActivateAbilityMessageGroup(source, ability, maneuver));

        if (source != null) {
            //be.addBattleMessage( new champions.battleMessage.LegacyBattleMessage( source.getName() + " activates " + ability.getInstanceName() + ".", BattleEvent.MSG_ABILITY)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( source.getName() + " activates " + ability.getInstanceName() + ".", BattleEvent.MSG_ABILITY)); // .addMessage( source.getName() + " activates " + ability.getInstanceName() + ".", BattleEvent.MSG_ABILITY);
            be.addBattleMessage(new ActivateAbilityMessage(source, ability));
        }

        ai.setStartTime((Chronometer) Battle.getCurrentBattle().getTime().clone());


        be.addAIStateEvent(ai, ai.getState(), AI_STATE_PREDELAY);
        ai.setState(AI_STATE_PREDELAY);
    }

    /** Builds the required predelay nodes for the power and maneuver.
     *
     * See ProcessStateNew method for usage information.
     *
     */
    public static AttackTreeNode buildPredelayNodes(BattleEvent be) {
        // Execute predelay for power and maneuver, giving them a chance to doing things prior to
        // the delay of both...
        AttackTreeNode powerNode = null;
        AttackTreeNode maneuverNode = null;

        powerNode = be.getAbility().getPower().predelay(be);

        if (be.getManeuver() != null) {
            maneuverNode = be.getManeuver().getPower().predelay(be);
        }

        if (powerNode != null && maneuverNode != null) {
            GenericAttackTreeNode node = new GenericAttackTreeNode("Predelay Node");
            node.addChild(powerNode);
            node.addChild(maneuverNode);
            return node;
        } else if (powerNode != null) {
            return powerNode;
        } else if (maneuverNode != null) {
            return maneuverNode;
        }
        return null;
    }

    /** Builds the required predelay nodes for the power and maneuver.
     *
     * See ProcessStateWaitForAbilityTrigger method for usage information.
     *
     * This method can be used for either the ability or the maneuver of a
     * BattleEvent.  When used for the maneuver, specify the maneuver for the ability
     * argument.
     *
     */
    public static AttackTreeNode buildPreactivationNodes(BattleEvent be, Ability ability) {
        // Execute predelay for power and maneuver, giving them a chance to doing things prior to
        // the delay of both...
        AttackTreeNode powerNode = null;
        AttackTreeNode maneuverNode = null;

        powerNode = be.getAbility().getPower().preactivate(be);

        if (be.getManeuver() != null) {
            maneuverNode = be.getManeuver().getPower().preactivate(be);
        }

        if (powerNode != null && maneuverNode != null) {
            GenericAttackTreeNode node = new GenericAttackTreeNode("Predelay Node");
            node.addChild(powerNode);
            node.addChild(maneuverNode);
            return node;
        }

        GenericAttackTreeNode genericNode = null;
        AttackTreeNode preactivateNode;

        // Run Preactivate Advantages, Disadvantages
        // If anyone returns false, the ability failed to activate.
        boolean activateSuccessful = true;
        Power p = ability.getPower();
        preactivateNode = p.preactivate(be);
        if (preactivateNode != null) {
            if (genericNode == null) {
                genericNode = new GenericAttackTreeNode("Preactivation Group");
            }
            genericNode.addChild(preactivateNode);
        }

        int count, i;
        // Process All Power Advantages
        count = ability.getAdvantageCount();
        for (i = 0; i < count; i++) {
            Advantage a = (Advantage) ability.getAdvantage(i);
            preactivateNode = a.preactivate(be, i, ability);
            if (preactivateNode != null) {
                if (genericNode == null) {
                    genericNode = new GenericAttackTreeNode("Preactivation Group");
                }
                genericNode.addChild(preactivateNode);
            }
        }

        // Process All Power Limitations
        count = ability.getLimitationCount();
        for (i = 0; i < count; i++) {
            Limitation l = (Limitation) ability.getLimitation(i);
            preactivateNode = l.preactivate(be, i, ability);
            if (preactivateNode != null) {
                if (genericNode == null) {
                    genericNode = new GenericAttackTreeNode("Preactivation Group");
                }
                genericNode.addChild(preactivateNode);
            }
        }

        // Process All Source Effects
        Target source = ability.getSource();
        if (source != null) {
            count = source.getEffectCount();
            for (i = 0; i < count; i++) {
                Effect e = (Effect) source.getEffect(i);
                preactivateNode = e.preactivate(be, ability);
                if (preactivateNode != null) {
                    if (genericNode == null) {
                        genericNode = new GenericAttackTreeNode("Preactivation Group");
                    }
                    genericNode.addChild(preactivateNode);
                }
            }
        }

        return genericNode;
    }

    /** Process the delaying of an ability.
     *
     * This method will do everything necessary to put an ability into a delayed state.
     * This method can be used for either the ability or the maneuver.
     *
     * ActivationInfo State:
     * On Entry: AI_STATE_PREDELAY (For Ability)
     *
     * On Exit: AI_STATE_ABILITY_DELAYED (If Ability is Delayed Activation)
     * On Exit: AI_STATE_WAIT_FOR_ABILITY_TRIGGER (If Ability was not a Delayed Ability)
     */
    public static boolean processStatePredelay(BattleEvent be) throws BattleEventException {
        boolean delayed = false;

        Target source = be.getSource();
        ActivationInfo ai = be.getActivationInfo();
        Ability ability = be.getAbility();

        // Check for Delayed
        if (ai.isContinuing() == false && be.isAbilityDelayed()) {
            if (ability.isAttack()) {
                source.add("Target.DELAYEDATTACK", "TRUE", true);
                be.addAttackDelayEvent(source, true);
            }

            Chronometer time;
            time = ability.getDelayTime(Battle.getCurrentBattle().getTime(), source);
            int dex = ability.getDelayDex(source);

            BattleEvent newEvent = new BattleEvent(BattleEvent.DELAYED_ACTIVATE, ai, time, dex);
            if (dex == SEQUENCE_BEFORE_TARGET) {
                newEvent.setDelayTarget(source);
            }

            newEvent.setActivationInfo(be.getActivationInfo());
            be.getActivationInfo().setDelayedActivationEvent(newEvent);

            be.addUndoableEvent(Battle.getCurrentBattle().addDelayedEvent(newEvent));

            Effect delayEffect = ability.getDelayEffect(newEvent);

            if (delayEffect != null) {
                newEvent.setEffect(delayEffect);
                delayEffect.addEffect(be, source);
            }

            String dexString;
            if (dex == SEQUENCE_END_OF_SEGMENT) {
                dexString = ", End of Segment";
            } else if (dex == SEQUENCE_BEFORE_TARGET) {
                dexString = ", just prior to " + source.getName() + "'s action";
            } else {
                dexString = ", Dex " + Integer.toString(dex);
            }
            be.addBattleMessage(new champions.battleMessage.LegacyBattleMessage(ability.getInstanceName() + " requires extra time to Activate and will finish activating at " + time.toString() + dexString + ".", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(ability.getInstanceName() + " requires extra time to Activate and will finish activating at " + time.toString() + dexString + ".", BattleEvent.MSG_NOTICE)); // .addMessage(ability.getInstanceName() + " requires extra time to Activate and will finish activating at " + time.toString() + dexString + ".", BattleEvent.MSG_NOTICE);

            if (ability.isDelayExclusive()) {
                be.addCombatStateEvent(source, source.getCombatState(), CombatState.STATE_DELAYED);
                source.setCombatState(CombatState.STATE_DELAYED);
            }

            be.addAIStateEvent(ai, ai.getState(), AI_STATE_ABILITY_DELAYED);
            ai.setState(AI_STATE_ABILITY_DELAYED);

            be.addUndoableEvent(be.setFinishedProcessingEvent(true));

            delayed = true;
        } else {
            // Ability was not Delayed
            be.addAIStateEvent(ai, ai.getState(), AI_STATE_WAIT_FOR_ABILITY_TRIGGER);
            ai.setState(AI_STATE_WAIT_FOR_ABILITY_TRIGGER);
        }

        return delayed;
    }

    /** Finish Processing the Delay on an Ability.
     *
     * This method will do everything necessary to put an ability into a delayed state.
     * This method can be used for either the ability or the maneuver.
     *
     * ActivationInfo State:
     * On Entry: AI_STATE_ABILITY_DELAYED
     *
     * On Exit: AI_STATE_WAIT_FOR_ABILITY_TRIGGER
     */
    public static void processStateAbilityDelayed(BattleEvent be) throws BattleEventException {
        Ability ability = be.getAbility();
        ActivationInfo ai = be.getActivationInfo();
        Target source = be.getSource();

        //if ( ability.isDelayExclusive() ) {
        Chronometer time = (Chronometer) Battle.getCurrentBattle().getTime().clone();
        if (be.getDex() == SEQUENCE_BEFORE_TARGET && Chronometer.isActiveSegment(be.getSource().getEffectiveSpeed(time), time.getSegment())) {
            // This was a delay exclusive event and it was a sequence before target, so the target should probably be set
            // to active after this event completes.
            be.addCombatStateEvent(source, source.getCombatState(), CombatState.STATE_ACTIVE);
            source.setCombatState(CombatState.STATE_ACTIVE);
            be.setActivationTime("INSTANT");

        } else {
            be.addCombatStateEvent(source, source.getCombatState(), CombatState.STATE_FIN);
            source.setCombatState(CombatState.STATE_FIN);
        }

        //be.setActivationTime("INSTANT", true);
        //}

        be.addUndoableEvent(new ActivationInfoLastTimeProcessUndoable(ai, ai.getLastProcessedTime(), time));
        ai.setLastProcessedTime(time);

        be.addAIStateEvent(ai, ai.getState(), AI_STATE_WAIT_FOR_ABILITY_TRIGGER);
        ai.setState(AI_STATE_WAIT_FOR_ABILITY_TRIGGER);
    }

    /** Process BattleEvent in AI_STATE_WAIT_FOR_ABILITY_TRIGGER.
     *
     * This method will do everything just prior to the ability being triggered.
     *
     * ActivationInfo State:
     * On Entry: AI_STATE_WAIT_FOR_ABILITY_TRIGGER
     *
     * On Exit: AI_STATE_ABILITY_TRIGGERED
     */
    public static void processStateWaitForAbilityTrigger(BattleEvent be) throws BattleEventException {
        Ability ability = be.getAbility();
        ActivationInfo ai = be.getActivationInfo();
        Target source = be.getSource();

        if (ability.isTriggered()) {
            // Check to see if the Stackable limit has been reached.
            effectStacked stackedEffect;

            if (source.hasEffect("Stacked Abilities")) {
                stackedEffect = (effectStacked) source.getEffect("Stacked Abilities");

                int max = source.getMaximumStackedAbilities();
                if (max != -1 && stackedEffect.getStackedCount() >= max) {
                    throw new BattleEventException("Source already has maximum stacked abilities stacked");
                }
            } else {
                stackedEffect = new effectStacked();
                stackedEffect.addEffect(be, source);
            }

            // Setup Delayed Event and Queue Event
            BattleEvent newEvent = new BattleEvent(BattleEvent.TRIGGER, ai, ability.getTriggerTime(), ability.getTriggerDex());
            be.addUndoableEvent(Battle.getCurrentBattle().addDelayedEvent(newEvent));

            // The following line of code is not undoable.
            stackedEffect.addStackedEvent(newEvent);


            be.addBattleMessage(new champions.battleMessage.LegacyBattleMessage("This Ability has the advantage Delayed Effect, Triggered, or Time Delay.", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( "This Ability has the advantage Delayed Effect, Triggered, or Time Delay.", BattleEvent.MSG_NOTICE)); // .addMessage( "This Ability has the advantage Delayed Effect, Triggered, or Time Delay.", BattleEvent.MSG_NOTICE);
            be.addBattleMessage(new champions.battleMessage.LegacyBattleMessage("Click in the Delayed Event Window to Activate.", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( "Click in the Delayed Event Window to Activate.", BattleEvent.MSG_NOTICE)); // .addMessage( "Click in the Delayed Event Window to Activate.", BattleEvent.MSG_NOTICE);
            // ******************************************
            // Should add Undo Event to BE right here!!!!
            // ******************************************

            // Add a Delayed Effect
            Effect delayEffect = ability.getTriggerEffect();
            if (delayEffect != null) {
                delayEffect.addEffect(be, source);
                be.addEffectEvent(delayEffect, source, true);
            }

            // Determine the End Use.
            // If power has advantage Uncontrolled, then charge all initial END, create new END source, and
            // set END paid.
            if (ability.getBooleanValue("Ability.ISUNCONTROLLED") == false) {
                // If not uncontrolled, then just charge first phase END, and set END paid on both this event and future
                // event.
                // processEND(be);
                newEvent.setENDPaid(true);
            }

            be.addAIStateEvent(ai, ai.getState(), AI_STATE_ABILITY_TRIGGERED);
            ai.setState(AI_STATE_ABILITY_TRIGGERED);
            be.addUndoableEvent(be.setFinishedProcessingEvent(true));
        } else {
            // Ability is not a triggered ability...
            be.addAIStateEvent(ai, ai.getState(), AI_STATE_ABILITY_TRIGGERED);
            ai.setState(AI_STATE_ABILITY_TRIGGERED);
        }
    }

    /** Process BattleEvent Event Stage.
     *
     * This method will process everything just after an event has been triggered.
     *
     * Immediately following this method in the AttackTree flow, the preactivation
     * nodes should be assembled using the buildPreactivationNodes method.
     *
     * ActivationInfo State:
     * On Entry: AI_STATE_ABILITY_TRIGGERED
     *
     * On Exit: AI_STATE_PREACTIVATE_ABILITY
     */
    public static void processStateAbilityTriggered(BattleEvent be) throws BattleEventException {
        Ability ability = be.getAbility();
        ActivationInfo ai = be.getActivationInfo();
        Target source = be.getSource();
        int count, i, index;

        // Remove this battle event from the Delayed Event Queue, if it is in there.
        if (Battle.currentBattle.getDelayedEvents().contains(be)) {
            be.addUndoableEvent(Battle.currentBattle.removeDelayedEvent(be));

            // the following lines of code will not undo properly.  This should be fixed.
            if (source.hasEffect("Stacked Abilities")) {
                effectStacked stackedEffect = (effectStacked) source.getEffect("Stacked Abilities");
                stackedEffect.removeStackedEvent(be);
            }
        }

        be.addAIStateEvent(ai, ai.getState(), "PREACTIVATE_ABILITY");
        ai.setState("PREACTIVATE_ABILITY");
    }

    /** Process BattleEvent Event Stage.
     *
     * This method will process everything just after an event has been triggered.
     *
     * ActivationInfo State:
     * On Entry: AI_STATE_PREACTIVATE_ABILITY
     *
     * On Exit: AI_STATE_MANEUVER_DELAYED (If Maneuver Has A Delay On It)
     * On Exit: AI_STATE_MANEUVER_TRIGGERED (If BE has Maneuver and no delay)
     * On Exit: AI_STATE_MANEUVER_ACTIVATED (If BE has no Maneuver)
     */
    public static void processStatePreactivateAbility(BattleEvent be) throws BattleEventException {
        Ability ability = be.getAbility();
        ActivationInfo ai = be.getActivationInfo();
        Target source = be.getSource();
        int count, i;

        if (ai.isActivated() == false) {
            be.addAIStateEvent(ai, ai.getState(), "ACTIVATED");
            ai.setState("ACTIVATED");
            //ability.add("Ability.ACTIVATED", "TRUE",  true);
            //be.addActivateEvent( ability, true );

            // Run through the source Effects to let them know that Ability is activating
            // First build list, then run through list (just in case it changes).
            count = source.getEffectCount();
            Effect[] sourceEffects = new Effect[count];
            for (i = 0; i < count; i++) {
                sourceEffects[i] = (Effect) source.getEffect(i);
            }

            for (i = 0; i < count; i++) {
                if (source.hasEffect(sourceEffects[i])) {
                    sourceEffects[i].abilityIsActivating(be, ability);
                }
            }
            
            CombatLevelList combatLevelList = be.getCombatLevelList(source);
            if ( combatLevelList != null ) {
                for (CombatLevelListEntry combatLevelEntry : combatLevelList) {
                    combatLevelEntry.getProvider().activateCombatLevels(be,combatLevelEntry.isEnabled());
                }
            }
        }

        // Add combatModifiers based on OCVBONUS, DCVBONUS keys
        int ocvbonus = ability.getOCVModifier();
        int dcvbonus = ability.getDCVModifier();

        if (ocvbonus != 0 || dcvbonus != 0) {
            Effect combatBonus = new effectCombatModifier(ability.getInstanceName() + " Modifiers", ocvbonus, dcvbonus,0,true);
            addEffect(be, combatBonus, source);
        }

        // Check Whether there is a maneuver...if there is, check for delay
        Ability maneuver = be.getManeuver();
        if (maneuver != null) {
            if (maneuver != null && ai.isContinuing() == false && be.isManeuverDelayed()) {
                if (maneuver.isAttack()) {
                    source.add("Target.DELAYEDATTACK", "TRUE", true);
                    be.addAttackDelayEvent(source, true);
                }

                Chronometer time;
                time = maneuver.getDelayTime(Battle.getCurrentBattle().getTime(), source);
                int dex = maneuver.getDelayDex(source);

                BattleEvent newEvent = new BattleEvent(BattleEvent.DELAYED_ACTIVATE, ai, time, dex);
                if (dex == SEQUENCE_BEFORE_TARGET) {
                    newEvent.setDelayTarget(source);
                }

                newEvent.setActivationInfo(be.getActivationInfo());

                be.addUndoableEvent(Battle.getCurrentBattle().addDelayedEvent(newEvent));

                Effect delayEffect = maneuver.getDelayEffect(newEvent);

                if (delayEffect != null) {
                    newEvent.setEffect(delayEffect);
                    delayEffect.addEffect(be, source);
                }

                String dexString;
                if (dex == SEQUENCE_END_OF_SEGMENT) {
                    dexString = ", End of Segment";
                } else if (dex == SEQUENCE_BEFORE_TARGET) {
                    dexString = ", just prior to " + source.getName() + "'s action";
                } else {
                    dexString = ", Dex " + Integer.toString(dex);
                }
                be.addBattleMessage(new champions.battleMessage.LegacyBattleMessage(ability.getInstanceName() + " requires extra time to Activate and will finish activating at " + time.toString() + dexString + ".", BattleEvent.MSG_NOTICE)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage(ability.getInstanceName() + " requires extra time to Activate and will finish activating at " + time.toString() + dexString + ".", BattleEvent.MSG_NOTICE)); // .addMessage(ability.getInstanceName() + " requires extra time to Activate and will finish activating at " + time.toString() + dexString + ".", BattleEvent.MSG_NOTICE);

                if (maneuver.isDelayExclusive()) {
                    be.addCombatStateEvent(source, source.getCombatState(), CombatState.STATE_DELAYED);
                    source.setCombatState(CombatState.STATE_DELAYED);
                }

                be.addAIStateEvent(ai, ai.getState(), AI_STATE_MANEUVER_DELAYED);
                ai.setState(AI_STATE_MANEUVER_DELAYED);

                be.addUndoableEvent(be.setFinishedProcessingEvent(true));
            } else {
                // There is a Maneuver, but it is not delayed!
                be.addAIStateEvent(ai, ai.getState(), AI_STATE_MANEUVER_TRIGGERED);
                ai.setState(AI_STATE_MANEUVER_TRIGGERED);
            }
        } else {
            // There is no Maneuver
            be.addAIStateEvent(ai, ai.getState(), AI_STATE_ACTIVATED);
            ai.setState(AI_STATE_ACTIVATED);
        }
    }

    /** Finish Processing the Delay on an Ability.
     *
     * This method will do everything necessary to put an ability into a delayed state.
     * This method can be used for either the ability or the maneuver.
     *
     * ActivationInfo State:
     * On Entry: AI_STATE_MANEUVER_DELAYED
     *
     * On Exit: AI_STATE_MANEUVER_TRIGGER
     */
    public static void processStateManeuverDelayed(BattleEvent be) throws BattleEventException {
        Ability maneuver = be.getAbility();
        ActivationInfo ai = be.getActivationInfo();
        Target source = be.getSource();

        if (maneuver.isDelayExclusive()) {
            Chronometer time = Battle.currentBattle.getTime();
            if (be.getDex() == SEQUENCE_BEFORE_TARGET && Chronometer.isActiveSegment(be.getSource().getEffectiveSpeed(time), time.getSegment())) {
                // This was a delay exclusive event and it was a sequence before target, so the target should probably be set
                // to active after this event completes.
                be.addCombatStateEvent(source, source.getCombatState(), CombatState.STATE_ACTIVE);
                source.setCombatState(CombatState.STATE_ACTIVE);
                be.setActivationTime("INSTANT");
            } else {
                be.addCombatStateEvent(source, source.getCombatState(), CombatState.STATE_FIN);
                source.setCombatState(CombatState.STATE_FIN);
            }

            be.setActivationTime("INSTANT", true);
        }

        be.addAIStateEvent(ai, ai.getState(), AI_STATE_MANEUVER_TRIGGERED);
        ai.setState(AI_STATE_MANEUVER_TRIGGERED);
    }

    /** Finish Processing Activation of Maneuver
     *
     * There is nothing really done here.  However, immediately following this in the flow, the
     * preactivation nodes, build using the buildPreactivationNode method, should be attached
     * and executed.
     *
     * ActivationInfo State:
     * On Entry: AI_STATE_MANEUVER_TRIGGERED
     *
     * On Exit: AI_STATE_PREACTIVATE_MANEUVER
     */
    public static void processStateManeuverTriggered(BattleEvent be) throws BattleEventException {
        ActivationInfo ai = be.getActivationInfo();
        be.addAIStateEvent(ai, ai.getState(), AI_STATE_PREACTIVATE_MANEUVER);
        ai.setState(AI_STATE_PREACTIVATE_MANEUVER);
    }

    /** Finish Processing Activation of Maneuver
     *
     *
     * ActivationInfo State:
     * On Entry: AI_STATE_PREACTIVATE_MANEUVER
     *
     * On Exit: AI_STATE_ACTIVATED
     */
    public static void processStatePreactivateManeuver(BattleEvent be) throws BattleEventException {
        ActivationInfo ai = be.getActivationInfo();
        Target source = be.getSource();
        Ability maneuver = be.getManeuver();
        int count, i;

        // Run through the source Effects to let them know that Maneuver is activating
        // First build list, then run through list (just in case it changes).
        count = source.getEffectCount();
        Effect[] sourceEffects = new Effect[count];
        for (i = 0; i < count; i++) {
            sourceEffects[i] = (Effect) source.getEffect(i);
        }

        for (i = 0; i < count; i++) {
            if (source.hasEffect(sourceEffects[i])) {
                sourceEffects[i].abilityIsActivating(be, maneuver);
            }
        }

        // Add combatModifiers based on OCVBONUS, DCVBONUS keys for Maneuver
        int ocvbonus = maneuver.getOCVModifier();
        int dcvbonus = maneuver.getDCVModifier();

        if (ocvbonus != 0 || dcvbonus != 0) {
            Effect combatBonus = new effectCombatModifier(maneuver.getInstanceName() + " Modifiers", ocvbonus, dcvbonus,0,true);
            addEffect(be, combatBonus, source);
        }

        be.addAIStateEvent(ai, ai.getState(), AI_STATE_ACTIVATED);
        ai.setState(AI_STATE_ACTIVATED);
    }

    /** Process BattleEvent that are about should be deactivated.
     *
     * ActivationInfo State:
     * On Entry: AI_STATE_ACTIVATED
     * On Entry: AI_STATE_ACTIVATION_FAILED
     * On Entry: AI_STATE_DEACTIVATING
     * On Entry: AI_STATE_CONTINUING
     * On Entry: AI_STATE_ABILITY_DELAYED
     *
     * On Exit: AI_STATE_DEACTIVATED
     */
    public static void processDeactivating(BattleEvent be)
            throws BattleEventException {
        Ability ability = be.getAbility();
        Ability maneuver = be.getManeuver();
        ActivationInfo ai = be.getActivationInfo();
        Target source = be.getSource();

        if (ai.getState().equals(AI_STATE_ABILITY_DELAYED)) {
            processStateDelayedDeactivating(be);
        } else {
            if ((be.getType() == BattleEvent.DEACTIVATE || be.getType() == BattleEvent.CONTINUE) && ability != null && source != null) {
                // Only print the deactivate message if this is only for a deactivation or if this is a deactivation stemming
                // from a continue.
                be.addBattleMessage(new champions.battleMessage.LegacyBattleMessage(source.getName() + " deactivates " + ability.getInstanceName() + ".", BattleEvent.MSG_ABILITY)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage( source.getName() + " deactivates " + ability.getInstanceName() +".", BattleEvent.MSG_ABILITY)); // .addMessage( source.getName() + " deactivates " + ability.getInstanceName() +".", BattleEvent.MSG_ABILITY);
            }
        }

        int count, index;

        // Set ability to deactivated.
        // This must be set, otherwise the removeEffect will cause a ghost
        // Deactivate Event.



        be.addUndoableEvent(be.setFinishedProcessingEvent(true));
        //ability.add("Ability.ACTIVATED", "FALSE",  true);
        //be.addActivateEvent( ability, false );

        // Let the ability know it is being shutdown
        Power p = ability.getPower();
        p.shutdownPower(be, source);

        count = ability.getAdvantageCount();
        for (index = 0; index < count; index++) {
            Advantage a = (Advantage) ability.getAdvantage(index);
            a.shutdownAbility(be, index, ability, source);
        }

        count = ability.getLimitationCount();
        for (index = 0; index < count; index++) {
            Limitation a = (Limitation) ability.getLimitation(index);
            a.shutdownAbility(be, index, ability, source);
        }


        // Clean the ActivationInfo up
        Object o;
        while (ai.getIndexedSize("Ability") > 0) {
            o = ai.getIndexedValue(0, "Ability", "ABILITY");
            if (o != null) {
                ai.removeAbilityLink((Ability) o);
                be.addActivationInfoEvent(ability, ai, false);
            }
        }

        // Clean up targets
        be.addActivationInfoEvent(ai.getSource(), ai, false);
        ai.removeSource();

        // Remove effect generated by the activation
        count = ai.getIndexedSize("Effect");
        if (count > 0) {
            Effect[] effects = new Effect[count];
            Target target;
            // Create list of effects to remove
            for (index = 0; index < count; index++) {
                effects[index] = (Effect) ai.getIndexedValue(index, "Effect", "EFFECT");
            }

            // Remove effects one by one.  Check first to make sure other removes didn't remove
            // effect first
            for (index = 0; index < count; index++) {
                int i = ai.findIndexed("Effect", "EFFECT", effects[index]);
                if (i != -1) {
                    target = (Target) ai.getIndexedValue(i, "Effect", "TARGET");

                    effects[index].removeEffect(be, target);
                }
            }
        }

        // Clean up the Ability Instance if Possible...
        if (ability.wasCreatedAutomatically() && !ability.isInstanceActivated() && !ability.isInstanceAdjusted()) {
            be.addUndoableEvent(ability.removeInstanceFromInstanceGroup());
        }

        // Do this last, since it is the last time that the ActivationInfo will ever be used.
        // This can be used to trigger the destruction of the activationInfo during
        // battleEvent destruction.
        be.addAIStateEvent(ai, ai.getState(), AI_STATE_DEACTIVATED);
        ai.setState(AI_STATE_DEACTIVATED);
    }

    /** Perform specific task for deactivating a current delay activation event that hasn't finished activating.
     *
     * processDelayedDeactivate will clean up after an activation which is currently in the DELAY state.
     * It will remove necessary events from the delayed event queue and remove any effectInterruptibles that
     * happen to be hanging around.
     *
     * This method should never be called directly.  It is called by the processDeactivate method when that
     * method deactivates a DELAY state battle event.
     */
    private static void processStateDelayedDeactivating(BattleEvent be) throws BattleEventException {
        Ability ability = be.getAbility();
        Ability maneuver = be.getManeuver();
        ActivationInfo ai = be.getActivationInfo();
        Target source = be.getSource();

        // The Ability was in the Delay state of activating.  Check for effectInterruptibles that belong to the ability
        // or the maneuver.

        // This might not be the best thing to do anymore.  In cases where multiple
        // instances of an ability where being activated, this will break.  This is
        // a pretty rare case however (independent advantage?!?).
        Effect effect;
        int index = source.getEffectCount() - 1;
        for (; index >= 0; index--) {
            effect = (Effect) source.getEffect(index);
            if (effect instanceof effectInterruptible) {
                if (((effectInterruptible) effect).getInterruptibleAbility() == ability) {
                    effect.removeEffect(be, source);
                    continue;
                }

                if (((effectInterruptible) effect).getInterruptibleAbility() == maneuver) {
                    effect.removeEffect(be, source);
                    continue;
                }
            }
        }

        // Remove the DELAYED_ACTIVATION event from the delayEventQueue...actually, you can remove
        // all delayed events with this activation info.  There really should only be the DELAYED_ACTIVATION
        // event, but something else might sneak in there...however, since removing extra events would
        // screw up the iterator and make things more complicated, we will only deal with the simple case.
//        Set s = Battle.getCurrentBattle().getDelayedEvents();
//        Iterator i = s.iterator();
//        BattleEvent delayedEvent;
//        while (i.hasNext() ) {
//            delayedEvent = (BattleEvent)i.next();
//            if ( delayedEvent.getActivationInfo() == ai ) {
//                be.addUndoableEvent( Battle.getCurrentBattle().removeDelayedEvent(delayedEvent) );
//                break;
//            }
//        }

        BattleEvent delayedEvent = ai.getDelayedActivationEvent();
        if (delayedEvent != null) {
            be.addUndoableEvent(Battle.getCurrentBattle().removeDelayedEvent(delayedEvent));
        }

        be.addBattleMessage(new champions.battleMessage.LegacyBattleMessage("Activation of " + ability.getInstanceName() + " was cancelled prior to completing activation.", BattleEvent.MSG_ABILITY)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage("Activation of " + ability.getInstanceName() + " was cancelled prior to completing activation.", BattleEvent.MSG_ABILITY)); // .addMessage("Activation of " + ability.getInstanceName() + " was cancelled prior to completing activation.", BattleEvent.MSG_ABILITY);

    // END May need to be charged here...
    }

    /** Failed Activation of Ability.
     *
     * processFinished changes END (if it hasn't already been paid), shuts down the ability,
     * and adjusts the combat state of source.
     *
     * @param be
     * @throws BattleEventException
     */
    public static void processFailedActivating(BattleEvent be) throws BattleEventException {
        Ability ability = be.getAbility();
        ActivationInfo ai = be.getActivationInfo();
        Target source = be.getSource();

        int count, index;

        if (be.isENDPaid() == false) {
            processEND(be);
        }

        // This must be done at the beginning of a continuous activation, not here, because it
        // is not undoable...
        //  ai.cleanActivationInfo(be);

        processDeactivating(be);

        // Apply any extra end contained in the battleEvent
        DetailList endList;
        if ((endList = be.getEndList()) != null) {
            count = endList.getIndexedSize("End");
            for (index = 0; index < count; index++) {
                String reason = endList.getIndexedStringValue(index, "End", "REASON");
                Integer end = endList.getIndexedIntegerValue(index, "End", "END");
                Effect endEffect = new effectEND(reason, end.intValue());
                addEffect(be, endEffect, source);
            }
        }

        adjustSourceCombatStatus(be, source);

        be.addUndoableEvent(be.setFinishedProcessingEvent(true));
    }

    /** Finish Activating Ability.
     *
     * processFinished changes END (if it hasn't already been paid), shuts down the ability
     * if it is instant, sets the ActivationInfo state to continuing if constant/persistent,
     * and adjusts the combat state of source.
     *
     * @param be
     * @throws BattleEventException
     */
    public static void processFinishedActivating(BattleEvent be) throws BattleEventException {
        Ability ability = be.getAbility();
        ActivationInfo ai = be.getActivationInfo();
        Target source = be.getSource();

        int count, index;

        if (be.isENDPaid() == false) {
            processEND(be);
        }

        // This can't be done here because it is not undoable...instead,
        // it is done at the beginning of a continuing activation.
        //ai.cleanActivationInfo(be);

        // Check if Power is Instant.  If so, deactivate it immediately
        String ptype = ability.getPType(); //getStringValue("Ability.PTYPE" );
        if (ptype != null && ptype.equals("INSTANT")) {
            processDeactivating(be);
        } else if (ai.isContinuing()) {
            // This Activation occurred in a different segment and the power
            // was simply continuing.  Since the ability was already targeted
            // and is now basically passive, the time necessary to use the ability
            // is INSTANT
            //
            // This isn't true, except in the case of independent!
            //
            // Okay, now I think this is true.  I will leave it with INSTANT time for continuing power
            // for now.
            be.setActivationTime("INSTANT", true);

            // Check to see if there are Targets which are being affected.
            // This doesn't really work, since it is pretty much guaranteed that something will
            // be hit in the case of an AE attack (the center of the hex will be hit).  This
            // function will somehow need to know which TargetGroups should be considered
            // pertinent to keeping a power activated.
            //
            // However, it is possible that a character would want to maintain a power which hit
            // only the center hex and nothing else, so leave as is for now.
            //String atype = ai.getAbility().getStringValue("Ability.ATYPE");
            String atype = ai.getAbility().getAType();
            if (atype.equals("SELF") == false && ai.getHasHitTargets() == false) {
                // Nothing is being affected, so shut it down
                processDeactivating(be);
            }
        } else if (ability.getBooleanValue("Ability.ISINDEPENDENT")) {
            // The power is independent of the source.
            // Usually this means the power has the triggered or time delayed advantage,
            // and doesn't require the source character to be active.
            be.setActivationTime("INSTANT", true);
        } else {
            // The Ability must be a CONTINUOUS/PERSISTENT power
            // Check to see if there are Targets which are being affected.
            //String atype = ai.getAbility().getStringValue("Ability.ATYPE");
            String atype = ai.getAbility().getAType();
            if (atype.equals("SELF") || ai.getHasHitTargets()) {
                // There are still targets being affects, so keep on running.
                be.addAIStateEvent(ai, ai.getState(), "CONTINUING");
                ai.setState("CONTINUING");
            } else {
                // Nothing is being affected, so shut it down
                processDeactivating(be);
            }
        }

        // Apply any extra end contained in the battleEvent
        DetailList endList;
        if ((endList = be.getEndList()) != null) {
            count = endList.getIndexedSize("End");
            for (index = 0; index < count; index++) {
                String reason = endList.getIndexedStringValue(index, "End", "REASON");
                Integer end = endList.getIndexedIntegerValue(index, "End", "END");
                Effect endEffect = new effectEND(reason, end.intValue());
                addEffect(be, endEffect, source);
            }
        }

        adjustSourceCombatStatus(be, source);

        be.addUndoableEvent(be.setFinishedProcessingEvent(true));
    }

    /** Process the Start of a CONTINUING activation
     *
     * Immediately following this in the flow, the preactivation nodes, build using the
     * buildPreactivationNode method, should be attached and executed, for both the
     * ability and the maneuver.
     *
     * ActivationInfo State:
     * On Entry: AI_STATE_CONTINUING
     *
     * On Exit: AI_STATE_CONTINUING or AI_STATE_ACTIVATION_FAILED
     */
    public static void processStateContinuing(BattleEvent be) throws BattleEventException {
        ActivationInfo ai = be.getActivationInfo();

        Chronometer time = (Chronometer) Battle.getCurrentBattle().getTime().clone();
        be.addUndoableEvent(new ActivationInfoLastTimeProcessUndoable(ai, ai.getLastProcessedTime(), time));

        if (be.getType() != BattleEvent.CHARGE_END) {
            ai.setLastProcessedTime(time);
        }


        ai.cleanActivationInfo(be);

        if (be.isENDPaid() == false && checkEND(be) <= 0) {
            // During the continuing phase, we found we don't have enough end...doh
            be.setENDPaid(true);
            be.addBattleMessage(new champions.battleMessage.LegacyBattleMessage("Not enough END is available to continue using " + be.getAbility().getName() + ".  It will be shut down.", BattleEvent.MSG_END)); // .addBattleMessage( new champions.battleMessage.LegacyBattleMessage("Not enough END is available to continue using " + be.getAbility().getName() + ".  It will be shut down.", BattleEvent.MSG_END)); // .addMessage("Not enough END is available to continue using " + be.getAbility().getName() + ".  It will be shut down.", BattleEvent.MSG_END);
            be.addAIStateEvent(ai, ai.getState(), AI_STATE_ACTIVATION_FAILED);
            ai.setState(AI_STATE_ACTIVATION_FAILED);
        }

    }

    /** Processes the END and TIME for movement.
     *
     * processMovement performs any processing necessary to charge END for a movement power.
     * It also changes the TEMPTIME battleEvent vp to represent the correct amount of time
     * for performing the Movement.
     *
     * The "Movement.DISTANCE" vp should be set as an integer in the ActivationInfo of the
     * BattleEvent.  If it is set to MOVEMENT_FULL_MOVE or MOVEMENT_HALF_MOVE, those distances
     * will be calculated automatically.
     *
     * @param be
     * @throws BattleEventException
     */
    public static void processMovement(BattleEvent be) throws BattleEventException {
        ActivationInfo ai = be.getActivationInfo();
        Ability ability = be.getAbility();
        Target source = be.getSource();

        Integer d = ai.getDistanceMoved();

        int distance;

        if (d == null) {
            distance = MOVEMENT_FULL_MOVE;
        } else {
            distance = d.intValue();
        }

        int maximumDistance = ability.getRange();
        int halfDistance = (int) Math.ceil((double) maximumDistance / 2.0);
        Power power = ability.getPower();
        String reason = power.getMovementType(ability);

        if (source.hasFullPhase() == false) {
            maximumDistance = halfDistance;
        }

        if (distance > maximumDistance) {
            throw new BattleEventException(source.getName() + " can only move " + Integer.toString(maximumDistance) + " with " + reason);
        }

        if (distance == MOVEMENT_FULL_MOVE) {
            distance = maximumDistance;
        } else if (distance == MOVEMENT_HALF_MOVE) {
            distance = halfDistance;
        }

        double endAmount = ChampionsUtilities.roundValue((double) distance / 5.0, false);

        double multiplier = ability.getENDMultiplier();

        endAmount = ChampionsUtilities.roundValue(endAmount * multiplier, false);

        if (endAmount == 0 && multiplier != 0) {
            endAmount = 1;
        }

        int index = be.createIndexed("TempEND", "ABILITY", ability);
        be.addIndexed(index, "TempEND", "AMOUNT", new Integer((int) endAmount), true);


        if (distance <= halfDistance) {
            be.setActivationTime("HALFMOVE", true);
        } else {
            be.setActivationTime("FULLMOVE", true);
        }

        SimpleBattleMessage sbm = new SimpleBattleMessage(source, source.getName() + " moved " + distance + "\" using " + ability.getName() + ".");
        be.addBattleMessage(sbm);

    }

    
}
