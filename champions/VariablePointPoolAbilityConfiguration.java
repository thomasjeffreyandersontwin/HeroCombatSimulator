/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package champions;

import champions.attackTree.AttackTreeNode;
import champions.attackTree.ConfigureFrameworkCommitNode;
import champions.battleMessage.FrameworkSummaryMessage;
import champions.exception.BattleEventException;
import champions.interfaces.Framework;
import champions.interfaces.Undoable;
import java.awt.Color;
import java.awt.Event;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.swing.event.EventListenerList;
import treeTable.ColorProvider;

/** This class represents the current and proposed configuration of a VPP.
 * 
 * Each ability in the framework has a STATUS and an ACTION.  The configuratedAbilities
 * map stores the status and the action.  If there is no entry for the ability in 
 * the map, they are generally status DISABLED with action NO_ACTION.  However, 
 * the getAbilityStatus and getAbilityAction methods should also be used to access
 * the map, since parent abilities must be considered when calculating status and 
 * actions.
 * 
 * The legal status/action combinations follow.  If a combination isn't listed,
 * it isn't legal and shouldn't be used.
 * 
 * status ENABLED:  Ability is currently enable.
 *      action DISABLE:  The ability will be disabled immediately when config is applied.
 *      action NO_ACTION:  No changes will occur.
 * 
 * status ATTEMPT_ENABLED:  Ability is attempting to enable.  Once the appropriate
 *                          amount of time passes and the skill roll is made, this
 *                          will switch to either ENABLED or DISABLED.
 *      action NO_ACTION:  Keep attempting to enable the ability.
 *      action CANCEL_ENABLE:  Abort the enable of this ability.  This occurs immediately.
 * 
 * status PARENT_ATTEMPT_ENABLED: The parent of this ability is being enabled.  
 *      action NO_ACTION:  No change is occuring to this ability. 
 *     
 * status PARENT_ENABLED: The parent of this ability is enabled (and therefore this one is too).
 *      action NO_ACTION:  No change is occuring to this ability.
 * 
 * status DISABLED:  This ability (and its parent) is current disabled.
 *      action NO_ACTION:  No change is occuring.
 *      action ATTEMPT_ENABLE:  The character is about to enable this ability.  Once the
 *                              actions are committed, the status will change to ENABLE,
 *                              ATTEMPT_ENABLE, or DISABLED, depending on the skill roll
 *                              and time required to reconfigure framework.
 *      action FORCE_ENABLE:  The GM is forcing the ability active.  It will change to
 *                            ENABLED after the actions are committed.
 * 
 * @author twalker
 */
public class VariablePointPoolAbilityConfiguration implements Serializable {

    static final long serialVersionUID = 7240085569124704509L;
    protected Framework framework;
    protected Map<AbilityKey, ConfigurationEntry> configuredAbilities = new HashMap<AbilityKey, ConfigurationEntry>();
    transient protected EventListenerList listenerList = null;

    public VariablePointPoolAbilityConfiguration(Framework framework) {
        this.framework = framework;
    }

    public boolean isAbilityEnabled(Ability ability) {
        VPPConfigurationStatus status = getAbilityStatus(ability);
        return status == VPPConfigurationStatus.ENABLED || status == VPPConfigurationStatus.PARENT_ENABLED;
    }

    public void addConfigurationListener(FrameworkConfigurationListener listener) {
        if (listenerList == null) {
            listenerList = new EventListenerList();
        }
        listenerList.add(FrameworkConfigurationListener.class, listener);
    }

    public void removeConfigurationListener(FrameworkConfigurationListener listener) {
        if (listenerList != null) {
            listenerList.remove(FrameworkConfigurationListener.class, listener);
        }
    }

    public void fireConfigurationEvent() {

        //msg ( "Segment Advanced to " + time.toString(), BattleMessageEvent.MSG_SEGMENT);
        Event e = null;
        if (listenerList != null) {
            FrameworkConfigurationListener[] listeners = listenerList.getListeners(FrameworkConfigurationListener.class);
            for (int i = 0; i < listeners.length; i++) {
                FrameworkConfigurationListener eventListener = listeners[i];

                // Lazily create the event:
                if (e == null) {
                    e = new Event(this, 0, 0);
                }
                eventListener.configurationChanged(e);
            }
        }
    }

    public VPPConfigurationStatus getAbilityStatus(Ability ability) {
        ConfigurationEntry ce = configuredAbilities.get(new AbilityKey(ability));

        if (ce != null) {
            return ce.status;
        } else {
            Ability parent = ability.getParentAbility();
            if (parent != null) {
                VPPConfigurationStatus status = getAbilityStatus(parent);

                if (status == VPPConfigurationStatus.ENABLED || status == VPPConfigurationStatus.PARENT_ENABLED) {
                    return VPPConfigurationStatus.PARENT_ENABLED;
                } else if (status == VPPConfigurationStatus.ATTEMPT_ENABLED || status == VPPConfigurationStatus.PARENT_ATTEMPT_ENABLED) {
                    return VPPConfigurationStatus.PARENT_ATTEMPT_ENABLED;
                }
            }
        }
        return VPPConfigurationStatus.DISABLED;

    }

    public Undoable setAbilityStatus(Ability ability, VPPConfigurationStatus status) {

        Undoable u = null;

        if (status == VPPConfigurationStatus.PARENT_ATTEMPT_ENABLED || status == VPPConfigurationStatus.PARENT_ENABLED) {
            throw new IllegalArgumentException();
        }
        VPPConfigurationStatus oldStatus = getAbilityStatus(ability);

        if (oldStatus != status) {

            if (((status == VPPConfigurationStatus.ENABLED || status == VPPConfigurationStatus.ATTEMPT_ENABLED) && oldStatus == VPPConfigurationStatus.PARENT_ENABLED) == false) {
                AbilityKey key = new AbilityKey(ability);
                ConfigurationEntry ce = configuredAbilities.get(key);

                if (ce == null) {
                    ce = new ConfigurationEntry(status);
                    configuredAbilities.put(key, ce);

                    u = new VPPAddConfigurationEntryUndoable(ability, ce);
                } else {
                    u = new VPPSetConfigurationEntryStatusUndoable(ce, ce.status, status);

                    ce.status = status;
                }

                Undoable u2 = removeAbilityChildren(ability, status);

                if (u2 != null) {
                    u = new CompoundUndoable(u, u2);
                }
            }

            fireConfigurationEvent();
        }

        return u;
    }

    public VPPConfigurationAction getAbilityAction(Ability ability) {
        ConfigurationEntry ce = configuredAbilities.get(new AbilityKey(ability));

        if (ce != null) {
            return ce.action;
        }

        return VPPConfigurationAction.NO_ACTION;

    }

    public void setAbilityAction(Ability ability, VPPConfigurationAction action) {
        ConfigurationEntry ce = configuredAbilities.get(new AbilityKey(ability));

        if (ce != null || action != VPPConfigurationAction.NO_ACTION) {
            if (ce == null) {
                ce = new ConfigurationEntry(VPPConfigurationStatus.DISABLED);
                configuredAbilities.put(new AbilityKey(ability), ce);
            }

            ce.action = action;

            fireConfigurationEvent();
        }

    }

    protected Undoable removeAbilityChildren(Ability ability, VPPConfigurationStatus status) {
        // Need to intelligently remove children, so that only the parent ability
        // remains in the list.

        // If status == DISABLED, we don't need to do anything.
        // If status == ENABLED, we need to remove children with ATTEMPT_ENABLED && ENABLED
        // If status == ATTEMPT_ENABLED, we don't need to do anything since we don't
        //                 really know if this is a separate attempt or the same attempt
        //                 as above.  We will trust the gui to be smart about this.

        Undoable u = null;

        if (status == VPPConfigurationStatus.ENABLED) {
            Set<Entry<AbilityKey, ConfigurationEntry>> mapEntries = configuredAbilities.entrySet();


            for (Iterator<Entry<AbilityKey, ConfigurationEntry>> it = mapEntries.iterator(); it.hasNext();) {
                Entry<AbilityKey, ConfigurationEntry> e = it.next();

                if (e.getValue().status == VPPConfigurationStatus.ENABLED && isAbilityAncestor(e.getKey().ability, ability)) {
                    u = new VPPRemoveConfigurationEntryUndoable(e.getKey().ability, e.getValue());
                    it.remove();
                } else if (e.getValue().status == VPPConfigurationStatus.ATTEMPT_ENABLED) {
                    u = new VPPRemoveConfigurationEntryUndoable(e.getKey().ability, e.getValue());
                    Undoable u2 = removeEnableAttempt(e.getKey().ability, e.getValue().enableEvent);

                    if (u2 != null) {
                        u = new CompoundUndoable(u, u2);
                    }

                    it.remove();
                }
            }
        }

        if (u != null) {
            fireConfigurationEvent();
        }

        return u;
    }

    protected boolean isAbilityAncestor(Ability possibleDescendent, Ability possibleAncestor) {
        if (possibleDescendent == null) {
            return false;
        }
        return possibleDescendent.getParentAbility() == possibleAncestor || isAbilityAncestor(possibleDescendent.getParentAbility(), possibleAncestor);
    }

    public AttackTreeNode getCommitAttackTreeNode(String name) {
        return new ConfigureFrameworkCommitNode(name, configuredAbilities);
    }

    /** Applies the outstanding actions.
     *
     * This should be called too apply to outstanding action.  This is called after
     * the skill roll is successfully made.
     * 
     */
    public void applyActions(BattleEvent be) {

        boolean changeOccurred = false;

        for (Iterator<Entry<AbilityKey, ConfigurationEntry>> it = configuredAbilities.entrySet().iterator(); it.hasNext();) {
            Entry<AbilityKey, ConfigurationEntry> next = it.next();

            AbilityKey key = next.getKey();
            ConfigurationEntry entry = next.getValue();

            VPPConfigurationAction action = entry.action;
            if (action == VPPConfigurationAction.FORCE_ENABLE || action == VPPConfigurationAction.ATTEMPT_ENABLE) {
                if (entry.status != VPPConfigurationStatus.ENABLED) {
                    if (be != null) {
                        be.addBattleMessage(new FrameworkSummaryMessage(framework, be.getSource(), key.ability, true));
                        be.addUndoableEvent(new VPPSetConfigurationEntryStatusUndoable(entry, VPPConfigurationStatus.ENABLED, entry.status));
                    }
                    entry.status = VPPConfigurationStatus.ENABLED;
                    changeOccurred = true;
                }
            } else if (action == VPPConfigurationAction.DISABLE) {
                if (entry.status != VPPConfigurationStatus.DISABLED) {
                    if (be != null) {
                        be.addBattleMessage(new FrameworkSummaryMessage(framework, be.getSource(), key.ability, false));
                        be.addUndoableEvent(new VPPSetConfigurationEntryStatusUndoable(entry, VPPConfigurationStatus.DISABLED, entry.status));
                    }
                    entry.status = VPPConfigurationStatus.DISABLED;
                    changeOccurred = true;
                }
            }

            if (entry.action != VPPConfigurationAction.NO_ACTION) {
                if (be != null) {
                    be.addUndoableEvent(new VPPSetConfigurationEntryActionUndoable(entry, VPPConfigurationAction.NO_ACTION, entry.action));
                }
                entry.action = VPPConfigurationAction.NO_ACTION;
                changeOccurred = true;
            }
        }

        if (changeOccurred) {
            fireConfigurationEvent();
        }
    }

    /** Applies the configuration changes for the ability assuming the configuration succeeded.
     *
     * @param ability
     * @param be
     * @return true if the ability should be disabled.
     */
    public boolean applyActionsForAbility(Ability ability, BattleEvent be) {
        boolean disableAbility = false;
        boolean changeOccurred = false;

        ConfigurationEntry entry = configuredAbilities.get(new AbilityKey(ability));

        VPPConfigurationAction action = entry.action;
        if (action == VPPConfigurationAction.FORCE_ENABLE || action == VPPConfigurationAction.ATTEMPT_ENABLE) {
            if (entry.status != VPPConfigurationStatus.ENABLED) {
                if (be != null) {
                    be.addBattleMessage(new FrameworkSummaryMessage(framework, be.getSource(), ability, true));
                    be.addUndoableEvent(new VPPSetConfigurationEntryStatusUndoable(entry, VPPConfigurationStatus.ENABLED, entry.status));
                }
                entry.status = VPPConfigurationStatus.ENABLED;
                changeOccurred = true;
            }
        } else if (action == VPPConfigurationAction.DISABLE) {
            if (entry.status != VPPConfigurationStatus.DISABLED) {
                if (be != null) {
                    be.addBattleMessage(new FrameworkSummaryMessage(framework, be.getSource(), ability, false));
                    be.addUndoableEvent(new VPPSetConfigurationEntryStatusUndoable(entry, VPPConfigurationStatus.DISABLED, entry.status));
                }
                entry.status = VPPConfigurationStatus.DISABLED;
                changeOccurred = true;
                disableAbility = true;
            }
        }

        if (entry.action != VPPConfigurationAction.NO_ACTION) {
            if (be != null) {
                be.addUndoableEvent(new VPPSetConfigurationEntryActionUndoable(entry, VPPConfigurationAction.NO_ACTION, entry.action));
            }
            entry.action = VPPConfigurationAction.NO_ACTION;
            changeOccurred = true;
        }

        if (changeOccurred) {
            fireConfigurationEvent();
        }

        return disableAbility;
    }

    /** Fail the outstanding actions.
     *
     * This should be called when the configuration change failed due to a missed
     * skill roll.  ATTEMPT_ENABLED and DISABLE actions will both set the status
     * to DISABLED.  FORCE_ENABLE will still set the status to ENABLED.
     *
     */
    public void failActions(BattleEvent be) {

        boolean changeOccurred = false;

        for (Iterator<Entry<AbilityKey, ConfigurationEntry>> it = configuredAbilities.entrySet().iterator(); it.hasNext();) {
            Entry<AbilityKey, ConfigurationEntry> next = it.next();

            AbilityKey key = next.getKey();
            ConfigurationEntry entry = next.getValue();
            Ability ability = key.ability;

            VPPConfigurationAction action = entry.action;
            if (action == VPPConfigurationAction.FORCE_ENABLE) {
                if (entry.status != VPPConfigurationStatus.ENABLED) {
                    if (be != null) {
                        be.addBattleMessage(new FrameworkSummaryMessage(framework, be.getSource(), key.ability, true));
                        be.addUndoableEvent(new VPPSetConfigurationEntryStatusUndoable(entry, VPPConfigurationStatus.ENABLED, entry.status));
                    }
                    entry.status = VPPConfigurationStatus.ENABLED;
                    changeOccurred = true;
                }
            } else if (action == VPPConfigurationAction.DISABLE || action == VPPConfigurationAction.ATTEMPT_ENABLE) {
                if (entry.status != VPPConfigurationStatus.DISABLED) {
                    if (be != null) {
                        be.addBattleMessage(new FrameworkSummaryMessage(framework, be.getSource(), key.ability, false));
                        be.addUndoableEvent(new VPPSetConfigurationEntryStatusUndoable(entry, VPPConfigurationStatus.DISABLED, entry.status));

                        Iterator<ActivationInfo> it2 = ability.getActivations();
                        while (it2.hasNext()) {
                            ActivationInfo ai = it2.next();
                            BattleEvent newBe = new BattleEvent(BattleEvent.DEACTIVATE, ai);

                        }
                    }


                    entry.status = VPPConfigurationStatus.DISABLED;
                    changeOccurred = true;
                }
            }

            if (entry.action != VPPConfigurationAction.NO_ACTION) {
                if (be != null) {
                    be.addUndoableEvent(new VPPSetConfigurationEntryActionUndoable(entry, VPPConfigurationAction.NO_ACTION, entry.action));
                }
                entry.action = VPPConfigurationAction.NO_ACTION;
                changeOccurred = true;
            }
        }

        if (changeOccurred) {
            fireConfigurationEvent();
        }
    }

    /** Applies the configuration changes for the ability assuming the configuration failed.
     *
     * @param ability
     * @param be
     * @return true if the ability should be disabled.
     */
    public boolean failActionsForAbility(Ability ability, BattleEvent be) {
        boolean disableAbility = false;
        boolean changeOccurred = false;

        ConfigurationEntry entry = configuredAbilities.get(new AbilityKey(ability));

        VPPConfigurationAction action = entry.action;
        if (action == VPPConfigurationAction.FORCE_ENABLE) {
            if (entry.status != VPPConfigurationStatus.ENABLED) {
                if (be != null) {
                    be.addBattleMessage(new FrameworkSummaryMessage(framework, be.getSource(), ability, true));
                    be.addUndoableEvent(new VPPSetConfigurationEntryStatusUndoable(entry, VPPConfigurationStatus.ENABLED, entry.status));
                }
                entry.status = VPPConfigurationStatus.ENABLED;
                changeOccurred = true;
            }
        } else if (action == VPPConfigurationAction.DISABLE || action == VPPConfigurationAction.ATTEMPT_ENABLE) {
            if (entry.status != VPPConfigurationStatus.DISABLED) {
                if (be != null) {
                    be.addBattleMessage(new FrameworkSummaryMessage(framework, be.getSource(), ability, false));
                    be.addUndoableEvent(new VPPSetConfigurationEntryStatusUndoable(entry, VPPConfigurationStatus.DISABLED, entry.status));
                }
                entry.status = VPPConfigurationStatus.DISABLED;
                changeOccurred = true;
                disableAbility = true;
            }
        }

        if (entry.action != VPPConfigurationAction.NO_ACTION) {
            if (be != null) {
                be.addUndoableEvent(new VPPSetConfigurationEntryActionUndoable(entry, VPPConfigurationAction.NO_ACTION, entry.action));
            }
            entry.action = VPPConfigurationAction.NO_ACTION;
            changeOccurred = true;
        }

        if (changeOccurred) {
            fireConfigurationEvent();
        }

        return disableAbility;
    }

    /** Cancels outstanding actions.
     *
     * This will cancel all outstanding configuration changes, reseting the configuration
     * back to the original settings.
     * 
     */
    public void cancelActions() {
        for (Iterator<Entry<AbilityKey, ConfigurationEntry>> it = configuredAbilities.entrySet().iterator(); it.hasNext();) {
            Entry<AbilityKey, ConfigurationEntry> entry = it.next();

            entry.getValue().action = VPPConfigurationAction.NO_ACTION;
        }

        fireConfigurationEvent();
    }

    public void removeAbilityInstanceGroup(AbilityInstanceGroup instanceGroup) {

        for (Iterator<Entry<AbilityKey, ConfigurationEntry>> it = configuredAbilities.entrySet().iterator(); it.hasNext();) {
            Entry<AbilityKey, ConfigurationEntry> entry = it.next();
            if (entry.getKey().ability.getInstanceGroup() == instanceGroup) {
                it.remove();
                fireConfigurationEvent();
                break;
            }
        }
    }

    /** Returns the number of active points currently configured.
     * 
     */
    public int getCurrentRealPointCost() {
        int cp = 0;

        for (Iterator<Entry<AbilityKey, ConfigurationEntry>> it = configuredAbilities.entrySet().iterator(); it.hasNext();) {
            Entry<AbilityKey, ConfigurationEntry> entry = it.next();

            if (entry.getValue().status == VPPConfigurationStatus.ENABLED) {
                cp += entry.getKey().ability.getCPCost();
            }
        }

        return cp;
    }

    /** Returns the number of active points being enabled.
     * 
     * This only included the active points of newly enabled abilities.  This is
     * the cost that the skill roll will be based upon.
     * 
     * @return
     */
    public int getProposedActivePointEnableCost() {

        int ap = 0;
        for (Iterator<Entry<AbilityKey, ConfigurationEntry>> it = configuredAbilities.entrySet().iterator(); it.hasNext();) {
            Entry<AbilityKey, ConfigurationEntry> entry = it.next();

            if (entry.getValue().action == VPPConfigurationAction.ATTEMPT_ENABLE) {
                //VPPConfigurationStatus status = getAbilityStatus(ability);

                ap += entry.getKey().ability.getAPCost();
            }
        }

        return ap;
    }

    public int getProposedSkillRollAdjustment() {
        return (int) Math.round(-1 * getProposedActivePointEnableCost() / 10);
    }

    /** Returns the number of active points in the proposed configuration.
     * 
     * This will include currently active abilities plus the cost of newly
     * activated abilities minus deactivated abilities.
     */
    public int getProposedRealPointCost() {
        int cp = 0;
        for (Iterator<Entry<AbilityKey, ConfigurationEntry>> it = configuredAbilities.entrySet().iterator(); it.hasNext();) {
            Entry<AbilityKey, ConfigurationEntry> entry = it.next();

            int abilityCP = entry.getKey().ability.getCPCost();

            VPPConfigurationStatus status = entry.getValue().status;
            VPPConfigurationAction action = entry.getValue().action;

            switch (status) {
                case ENABLED:
                    if (action == VPPConfigurationAction.NO_ACTION) {
                        cp += abilityCP;
                    }
                    break;
                case ATTEMPT_ENABLED:
                    if (action == VPPConfigurationAction.NO_ACTION) {
                        cp += abilityCP;
                    }
                    break;
                case PARENT_ATTEMPT_ENABLED:
                    break;
                case PARENT_ENABLED:
                    break;
                case DISABLED:
                    if (entry.getValue().action == VPPConfigurationAction.ATTEMPT_ENABLE || entry.getValue().action == VPPConfigurationAction.FORCE_ENABLE) {

                        cp += entry.getKey().ability.getCPCost();
                    }
                    break;
            }

        }

        return cp;
    }

    /** Removes the ability from the battleEvent corresponding the enabling the ability.
     * 
     * If all enable-attempt abilities are removed from the battleEvent, the battleEvent will
     * be dequeued.
     * 
     * @param ability
     * @param battleEvent
     */
    protected Undoable removeEnableAttempt(Ability ability, BattleEvent battleEvent) {

        fireConfigurationEvent();

        return null;
    }

    public enum VPPConfigurationStatus implements ColorProvider, Serializable {

        DISABLED("Disabled", Color.RED),
        ENABLED("Enabled"),
        ATTEMPT_ENABLED("Attempting to enable", Color.BLUE),
        PARENT_ENABLED("Parent Enabled"),
        PARENT_ATTEMPT_ENABLED("Parent attempting to enable", Color.BLUE);
        protected String text;
        protected Color color;

        VPPConfigurationStatus(String text) {
            this.text = text;
        }

        private VPPConfigurationStatus(String text, Color color) {
            this.text = text;
            this.color = color;
        }

        public String toString() {
            return this.text;
        }

        public Color getForegroundColor() {
            return color;
        }

        public Color getBackgroundColor() {
            return null;
        }
    }

    public static class ConfigurationEntry implements Serializable {

        public VPPConfigurationStatus status;
        public VPPConfigurationAction action;
        public Chronometer enableTime;
        public BattleEvent enableEvent;

        public ConfigurationEntry(VPPConfigurationStatus status) {
            this.status = status;
        }

        public ConfigurationEntry(VPPConfigurationStatus status, Chronometer enableTime) {
            this.status = status;
            this.enableTime = enableTime;
        }
    }

    public class VPPRemoveConfigurationEntryUndoable implements Undoable {

        protected Ability ability;
        protected ConfigurationEntry configurationEntry;

        public VPPRemoveConfigurationEntryUndoable(Ability ability, ConfigurationEntry configurationEntry) {
            this.ability = ability;
            this.configurationEntry = configurationEntry;
        }

        public void undo() throws BattleEventException {
            configuredAbilities.put(new AbilityKey(ability), configurationEntry);
        }

        public void redo() throws BattleEventException {
            configuredAbilities.remove(new AbilityKey(ability));
        }

        public String toString() {
            return "VPPRemoveConfigurationEntryUndoable(" + ability + ", " + configurationEntry + ")";
        }
    }

    public class VPPAddConfigurationEntryUndoable implements Undoable {

        protected Ability ability;
        protected ConfigurationEntry configurationEntry;

        public VPPAddConfigurationEntryUndoable(Ability ability, ConfigurationEntry configurationEntry) {
            this.ability = ability;
            this.configurationEntry = configurationEntry;
        }

        public void redo() throws BattleEventException {
            configuredAbilities.put(new AbilityKey(ability), configurationEntry);
        }

        public void undo() throws BattleEventException {
            configuredAbilities.remove(new AbilityKey(ability));
        }

        public String toString() {
            return "VPPAddConfigurationEntryUndoable(" + ability + ", " + configurationEntry + ")";
        }
    }

    public class VPPSetConfigurationEntryStatusUndoable implements Undoable {

        protected ConfigurationEntry configurationEntry;
        protected VPPConfigurationStatus newStatus;
        protected VPPConfigurationStatus oldStatus;

        public VPPSetConfigurationEntryStatusUndoable(ConfigurationEntry configurationEntry, VPPConfigurationStatus newStatus, VPPConfigurationStatus oldStatus) {
            this.configurationEntry = configurationEntry;
            this.newStatus = newStatus;
            this.oldStatus = oldStatus;
        }

        public void redo() throws BattleEventException {
            configurationEntry.status = newStatus;
        }

        public void undo() throws BattleEventException {
            configurationEntry.status = oldStatus;
        }

        public String toString() {
            return "VPPSetConfigurationEntryStatusUndoable(" + configurationEntry + "," + oldStatus + " ->" + newStatus + ")";
        }
    }

    public class VPPSetConfigurationEntryActionUndoable implements Undoable {

        protected ConfigurationEntry configurationEntry;
        protected VPPConfigurationAction newAction;
        protected VPPConfigurationAction oldAction;

        public VPPSetConfigurationEntryActionUndoable(ConfigurationEntry configurationEntry, VPPConfigurationAction newAction, VPPConfigurationAction oldAction) {
            this.configurationEntry = configurationEntry;
            this.newAction = newAction;
            this.oldAction = oldAction;
        }

        public void redo() throws BattleEventException {
            configurationEntry.action = newAction;
        }

        public void undo() throws BattleEventException {
            configurationEntry.action = oldAction;
        }

        public String toString() {
            return "VPPSetConfigurationEntryActionUndoable(" + configurationEntry + "," + oldAction + " ->" + newAction + ")";
        }
    }

    public static enum VPPConfigurationAction implements Serializable {

        NO_ACTION("No Change"),
        ATTEMPT_ENABLE("Attempt to enable"),
        DISABLE("Disable"),
        FORCE_ENABLE("Force Enable"),
        CANCEL_ENABLE("Cancel Enable Attempt");
        protected String text;

        VPPConfigurationAction(String text) {
            this.text = text;
        }

        public String toString() {
            return this.text;
        }
        static final VPPConfigurationAction[] actions1 = {NO_ACTION, ATTEMPT_ENABLE};
        static final VPPConfigurationAction[] actions2 = {NO_ACTION, ATTEMPT_ENABLE, FORCE_ENABLE};
        static final VPPConfigurationAction[] actions3 = {NO_ACTION, DISABLE};
        static final VPPConfigurationAction[] actions4 = {NO_ACTION, CANCEL_ENABLE};

        public static VPPConfigurationAction[] getAvailableActions(VPPConfigurationStatus status) {
            if (status == VPPConfigurationStatus.PARENT_ENABLED) {
                return actions1;
            } else if (status == VPPConfigurationStatus.DISABLED) {
                return actions2;
            } else if (status == VPPConfigurationStatus.ENABLED) {
                return actions3;
            } else if (status == VPPConfigurationStatus.PARENT_ATTEMPT_ENABLED) {
                return null;
            } else if (status == VPPConfigurationStatus.ATTEMPT_ENABLED) {
                return actions4;
            } else {
                return null;
            }
        }
    }
}
