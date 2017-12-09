/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tjava;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/** Provides a timer which will execute an action when triggered.
 *
 * The DeferredActionTimer will gaurantee that the actions are not
 * triggered more often than a minimum delay period.  Additionally,
 * depending on the configuration of the timer, when a trigger event
 * occurs the next execution can either be as soon as possible or
 * after the minimum delay period.
 *
 * @author twalker
 */
public class DeferredActionTimer {

    private boolean executeInSwingEventThread;

    private long minimumMillisecondsBetweenActions;

    private String name;

    private Runnable action;

    private boolean alwaysDeferredOnTrigger;

    protected final Object scheduleLock = new Object();

    protected final Object runLock = new Object();

    private TimerTask task = null;

    protected boolean needsScheduling = false;

    protected boolean running = false;

    private boolean deamon = true;

    private long lastExecutionTime = 0;

    private static Map<TimerKey, DeferredActionTimer> timers = Collections.synchronizedMap(new WeakHashMap<TimerKey, DeferredActionTimer>());

    /** Constructor for a DeferredActionTimer.
     *
     * DeferredActionTimer can also track the created timers for you.  See createDeferredActionTimer,
     * getDeferredActionTime, and removeDeferredActionTimer.
     * <P>
     * I protected the contructor since you probably want to use a combination of createDeferredActionTimer
     * and getDeferredActionTime to track the timers and make sure there are no duplicates.  If you really
     * need the constructors direct, you will have to create a subclass.
     *
     * @param name Name of the timer.
     * @param action Action to execute when triggered.
     * @param minimumMillisecondsBetweenActions Minimum amount of time between triggering the action.
     * @param alwaysDeferredOnTrigger If true, the timer will always wait minimumMillisecondsBetweenActions
     * before triggering the action, even if more than minimumMillisecondsBetweenActions has passed
     * since the last trigger.
     */
    protected DeferredActionTimer(String name, Runnable action, long minimumMillisecondsBetweenActions, boolean alwaysDeferredOnTrigger) {
        this(name, action, minimumMillisecondsBetweenActions, alwaysDeferredOnTrigger, false);
    }

    /** Constructor for a DeferredActionTimer.
     *
     * DeferredActionTimer can also track the created timers for you.  See createDeferredActionTimer,
     * getDeferredActionTime, and removeDeferredActionTimer.
     * <P>
     * I protected the contructor since you probably want to use a combination of createDeferredActionTimer
     * and getDeferredActionTime to track the timers and make sure there are no duplicates.  If you really
     * need the constructors direct, you will have to create a subclass.
     *
     * @param name Name of the timer.
     * @param action Action to execute when triggered.
     * @param minimumMillisecondsBetweenActions Minimum amount of time between triggering the action.
     * @param alwaysDeferredOnTrigger If true, the timer will always wait minimumMillisecondsBetweenActions
     * before triggering the action, even if more than minimumMillisecondsBetweenActions has passed
     * since the last trigger.
     * @param executeInSwingEventThread If true, the action will be scheduled in the Swing event thread
     * via a call to SwingUtilities.InvokeAndWait.
     */
    protected DeferredActionTimer(String name, Runnable action, long minimumMillisecondsBetweenActions, boolean alwaysDeferredOnTrigger, boolean executeInSwingEventThread) {
        this.minimumMillisecondsBetweenActions = minimumMillisecondsBetweenActions;
        this.action = action;
        this.alwaysDeferredOnTrigger = alwaysDeferredOnTrigger;
        this.executeInSwingEventThread = executeInSwingEventThread;
    }

    
    /** Returns a DeferredActionTimer.
     *
     * If a DeferredActionTimer with the same name and owner were
     * created previously, this will return the previously created
     * timer (which may have different settings).  Otherwise,
     * this will create a new timer with the specified parameters.
     *
     * @param name Name of the timer.
     * @param owner Owner of the timer, used as an index for the saved timers.
     * @param action Action to execute when triggered.
     * @param minimumMillisecondsBetweenActions Minimum amount of time between triggering the action.
     * @param alwaysDeferredOnTrigger If true, the timer will always wait minimumMillisecondsBetweenActions
     * before triggering the action, even if more than minimumMillisecondsBetweenActions has passed
     * since the last trigger.
     * @param executeInSwingEventThread If true, the action will be scheduled in the Swing event thread
     * via a call to SwingUtilities.InvokeAndWait.
     * @return A DeferredActionTimer with name and owner, created if necessary.
     */
    public static synchronized DeferredActionTimer createDeferredActionTimer(String name, Object owner, Runnable action, long minimumMillisecondsBetweenActions, boolean alwaysDeferredOnTrigger, boolean executeInSwingEventThread) {
        TimerKey key = new TimerKey(name, owner);

        DeferredActionTimer timer = timers.get(key);

        if ( timer == null ) {
            timer = new DeferredActionTimer(name, action, minimumMillisecondsBetweenActions, alwaysDeferredOnTrigger, executeInSwingEventThread);
            timers.put(key, timer);
        }

        return timer;
    }

    /** Returns a DeferredActionTimer.
     *
     * If a DeferredActionTimer with the same name and owner were
     * created previously, this will return the previously created
     * timer (which may have different settings).  Otherwise,
     * this will create a new timer with the specified parameters.
     *
     * @param name Name of the timer.
     * @param owner Owner of the timer, used as an index for the saved timers.
     * @param action Action to execute when triggered.
     * @param minimumMillisecondsBetweenActions Minimum amount of time between triggering the action.
     * @param alwaysDeferredOnTrigger If true, the timer will always wait minimumMillisecondsBetweenActions
     * before triggering the action, even if more than minimumMillisecondsBetweenActions has passed
     * since the last trigger.
     * @return A DeferredActionTimer with name and owner, created if necessary.
     */
    public static synchronized DeferredActionTimer createDeferredActionTimer(String name, Object owner, Runnable action, long minimumMillisecondsBetweenActions, boolean alwaysDeferredOnTrigger) {
        return createDeferredActionTimer(name, owner, action, minimumMillisecondsBetweenActions, alwaysDeferredOnTrigger, false);
    }

    /** Returns a previously created DeferredActionTimer with name and owner.
     *
     * @param name Name of the timer.
     * @param owner Owner of the timer, used as an index for the saved timers.
     * @return A DeferredActionTimer with name and owner, null if one with the correct name and owner isn't found.
     */
    public static synchronized DeferredActionTimer getDeferredActionTime(String name, Object owner) {
        TimerKey key = new TimerKey(name, owner);

        return timers.get(key);
    }

    /** Removes a previously created DeferredActionTimer from the list of recorded timers.
     *
     * @param name Name of the timer.
     * @param owner Owner of the timer.
     */
    public static synchronized void removeDeferredActionTimer(String name, Object owner) {
        TimerKey key = new TimerKey(name, owner);

        DeferredActionTimer timer = timers.get(key);

        if ( timer != null ) {
            timer.cancelScheduling();
            timers.remove(key);
        }
    }

    

    /** Triggers the execution of the action.
     *
     * If the action hasn't been performed within minimumMillisecondsBetweenActions
     * milliseconds, the action will be executed immediately.  Otherwise, it
     * will be scheduled to run at the next time slot.
     *
     * This method is gauranteed not to block.  The action will always be run
     * separately.
     */
    public void triggerAction() {
        schedule(true);
    }

    /** Executes the action immediate, in the current thread, regardless of the time since the last execution.
     *
     * The action will be run immediately.  However, if the action is currently executing,
     * this method will block until the current action is finished.
     *
     * @throws RuntimeException If a runtime exception occurs during the execution
     * of the action, that runtime exception will be rethrown.  Other exception
     * will be caught (but they shouldn't be possible!?!)
     */
    public void triggerActionNow() throws RuntimeException {
        cancelScheduling();

        // We could get an errant schedule right here!!!
        // This could theoretically, in the worst case, run
        // the task 3 times back to back.
        try {
            run();
        } catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            if (t instanceof RuntimeException) {
                RuntimeException runtimeException = (RuntimeException) t;
                throw runtimeException;
            }
            else {
                Logger.getLogger(DeferredActionTimer.class.getCanonicalName()).log(Level.SEVERE, "Non-runtime exception occurred during deferred execution of action '" + getName() + "':", t);
            }
        }
    }

    /** Cancels the scheduling of the task.
     * 
     */
    private void cancelScheduling() {
        synchronized (scheduleLock) {
            if (getTask() != null) {
                getTask().cancel();
                setTask(null);
            }
            needsScheduling = false;
        }
    }

    /** Schedules the execution of the action if necessary.
     *
     */
    private void schedule(boolean forceNeedsScheduling) {
        synchronized (scheduleLock) {
            if (forceNeedsScheduling && getTask() == null) {
                needsScheduling = true;
            }

            if (running == false) {
                if (needsScheduling == true && getTask() == null) {
                    setTask(new TimerTask() {

                        @Override
                        public void run() {
                            try {
                                DeferredActionTimer.this.run();
                            } catch (Throwable t) {
                                Logger.getLogger(DeferredActionTimer.class.getCanonicalName()).log(Level.SEVERE, "Exception occurred during deferred execution of action '" + getName() + "':", t);
                            }
                        }
                    });

                    long delay;

                    if (isAlwaysDeferredOnTrigger()) {
                        delay = getMinimumMillisecondsBetweenActions();
                    }
                    else {
                        delay = Math.max(0, (getLastExecutionTime() + getMinimumMillisecondsBetweenActions()) - System.currentTimeMillis());
                    }

                    getTimer().schedule(getTask(), delay);

                    needsScheduling = false;
                }
            }
        }
    }

    /** Executes the action.
     *
     */
    private void run() throws InvocationTargetException {
        synchronized (runLock) {
            running = true;

            setTask(null);

            try {
                if (isExecuteInSwingEventThread()) {
                    SwingUtilities.invokeAndWait(getAction());
                }
                else {
                    getAction().run();
                }
            } catch (InterruptedException e) {
                Logger.getLogger(DeferredActionTimer.class.getCanonicalName()).log(Level.SEVERE, "Exception occurred during deferred execution of action '" + getName() + "':", e);
            } finally {
                // Clean up no matter what happens...
                lastExecutionTime = System.currentTimeMillis();

                running = false;

                schedule(false);
            }
        }
    }

    private Timer getTimer() {

        return SharedTimer.getSharedTimer(deamon);
    }

    /**
     * @return the executeInSwingEventThread
     */
    public boolean isExecuteInSwingEventThread() {
        return executeInSwingEventThread;
    }

    /**
     * @param executeInSwingEventThread the executeInSwingEventThread to set
     */
    public void setExecuteInSwingEventThread(boolean executeInSwingEventThread) {
        this.executeInSwingEventThread = executeInSwingEventThread;
    }

    /**
     * @return the minimumMillisecondsBetweenActions
     */
    public long getMinimumMillisecondsBetweenActions() {
        return minimumMillisecondsBetweenActions;
    }

    /**
     * @param minimumMillisecondsBetweenActions the minimumMillisecondsBetweenActions to set
     */
    public void setMinimumMillisecondsBetweenActions(long minimumMillisecondsBetweenActions) {
        this.minimumMillisecondsBetweenActions = minimumMillisecondsBetweenActions;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the action
     */
    public Runnable getAction() {
        return action;
    }

    /**
     * @param action the action to set
     */
    public void setAction(Runnable action) {
        this.action = action;
    }

    /**
     * @return the task
     */
    public TimerTask getTask() {
        return task;
    }

    /**
     * @param task the task to set
     */
    public void setTask(TimerTask task) {
        this.task = task;
    }

    /**
     * @return the lastExecutionTime
     */
    public long getLastExecutionTime() {
        return lastExecutionTime;
    }

    /**
     * @return the alwaysDeferredOnTrigger
     */
    public boolean isAlwaysDeferredOnTrigger() {
        return alwaysDeferredOnTrigger;
    }

    /** Sets wheather the execution of the action should always be delayed at least minimumMillisecondsBetweenActions after a trigger event.
     *
     * <p>If true, then a trigger event will result in a mandatory wait of minimumMillisecondsBetweenActions
     * milliseconds before the action is executed.  This is useful in situations where you expect a bunch of
     * changes to occur together but you are not sure how many.  For example, if you are saving a file
     * every time it changes, you may want to always wait a bit of time to make sure that a group of
     * changes doesn't result in a write for each change.
     *
     * <p>If false, then a trigger event can result in an immediate execution of the action
     * (assuming minimumMillisecondsBetweenActions time has passed since the last execution).
     *
     *
     * <p>This doesn't affect the time of execution if an execution is already scheduled.
     *
     * <p>triggerActionNow is also not affected by this parameter.
     *
     * @param alwaysDeferredOnTrigger the alwaysDeferredOnTrigger to set
     */
    public void setAlwaysDeferredOnTrigger(boolean alwaysDeferredOnTrigger) {
        this.alwaysDeferredOnTrigger = alwaysDeferredOnTrigger;
    }

    /**
     * @return the deamon
     */
    public boolean isDeamon() {
        return deamon;
    }

    /** Sets whether the task will keep the application alive if it is currently scheduled.
     *
     * <p>When True, the application will not wait for the task to finish before exiting.
     *
     * <p>When False, the application will not quit until the last scheduled execution of
     * the task has completed.
     *
     * @param deamon the deamon to set
     */
    public void setDeamon(boolean deamon) {
        this.deamon = deamon;
    }

    protected static class TimerKey {


        private String name;

        private Object owner;

        public TimerKey(String name, Object owner) {
            this.name = name;
            this.owner = owner;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final TimerKey other = (TimerKey) obj;
            if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
                return false;
            }
            if (this.owner != other.owner ) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 43 * hash + (this.name != null ? this.name.hashCode() : 0);
            hash = 43 * hash + (this.owner != null ? this.owner.hashCode() : 0);
            return hash;
        }

        
    }
}
