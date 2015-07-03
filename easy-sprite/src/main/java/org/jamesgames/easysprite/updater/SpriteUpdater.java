package org.jamesgames.easysprite.updater;

import net.jcip.annotations.ThreadSafe;
import org.jamesgames.easysprite.sprite.Sprite;
import org.jamesgames.jamesjavautils.time.ActionsPerTimeFrameCounter;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * SpriteUpdater updates a {@link Sprite} at a specific rate on a separate thread, supplying how much time has been
 * elapsed since the last update from the SpriteUpdater. SpriteUpdater can also execute {@link UpdateAction}s during
 * each update to update any other pieces of data that the user wants updated per action (a good example UpdateAction
 * would be a to repaint the graphics of an application). SpriteUpdater updates in fixed-rate execution, where each
 * execution is scheduled relative to the scheduled execution time of the initial execution.  If an execution is delayed
 * for any reason (such as garbage collection or other background activity), two or more executions will occur in rapid
 * succession to "catch up."  In the long run, the frequency of execution will be exactly the reciprocal of the
 * specified period (assuming the system clock underlying Object.wait(long) is accurate). (comments partly taken from
 * java.util.Timer spec).
 *
 * @author James Murphy
 */
@ThreadSafe
public final class SpriteUpdater {

    /**
     * The amount in nano seconds of how long the updates per second value will be calculated for every new value
     */
    private static final long timeToCalculateNewUpsValue = 1_000_000_000;

    /**
     * The amount of actions per game update, which is one, one update of the base sprite will be viewed as one action,
     * the number of additional UpdateActions executed is not kept track of
     */
    private static final int amountOfActionsPerGameUpdate = 1;

    private static final int numberOfNanosecondsInMillisecond = 1_000_000;

    private static final String spriteUpdaterThreadName = "Sprite Updater Thread";


    /**
     * The sprite to be updated at the specified interval
     */
    private final Sprite spriteToUpdate;

    /**
     * Used to calculate the updates per second value
     */
    private final ActionsPerTimeFrameCounter updatesPerSecondCounter =
            new ActionsPerTimeFrameCounter(timeToCalculateNewUpsValue);

    /**
     * Timer used to execute the TimerTask used to update
     */
    private final Timer updateTimer = new Timer(spriteUpdaterThreadName);

    /**
     * References the most recently created TimerTask to update the Sprite and execute other additional update actions
     */
    private TimerTask updateTask = new TimerTask() {
        @Override
        public void run() {
            // do nothing default initialization
        }
    };

    /**
     * The system nano time from the last update, used to calculate how much time elapsed from one update to the next
     */
    private long systemNanoTimeFromLastUpdate = System.nanoTime();

    /**
     * Set of additional actions to do during each update. Example usage could be to tell some other piece of software
     * to make another render, or to log some data
     */
    private final Set<UpdateAction> additionalActionsPerUpdate = new HashSet<>();

    /**
     * Set of update listeners listening for things such as new calculated updates per second values
     */
    private final Set<UpdateListener> updateListeners = new HashSet<>();

    /**
     * Last known computed updates per second value, used to know if the value changed, that way if it did all listeners
     * can be notified of the value change
     */
    private float lastUpdatesPerSecondValue = 0;

    /**
     * Creates a new SpriteUpdater which updates every time the amount of milliseconds supplied elapses. Sprite will
     * begin updating immediately.
     *
     * @param spriteToUpdate
     *         sprite to be updated at the specified interval
     * @param updateSpeedInMilliseconds
     *         Amount of time to elapse for new update to occur
     */
    public SpriteUpdater(Sprite spriteToUpdate, int updateSpeedInMilliseconds) {
        if (updateSpeedInMilliseconds <= 0) {
            throw new IllegalArgumentException("Update speed in milliseconds must be greater than 0");
        }
        this.spriteToUpdate = spriteToUpdate;
        scheduleSpriteUpdate(updateSpeedInMilliseconds);
    }

    /**
     * Stops the updating of the Sprite. To update again, call {@link SpriteUpdater#scheduleSpriteUpdate(int)}. This
     * method is safe to call even if this SpriteUpdater was not already updating.
     */
    public synchronized void stopUpdating() {
        // safe to call if not scheduled
        updateTask.cancel();
    }

    /**
     * Set how often the Sprite should update.
     *
     * @param updateSpeedInMilliseconds
     *         How often a update should occur
     */
    public synchronized void scheduleSpriteUpdate(int updateSpeedInMilliseconds) {
        // Stops the current updating task, a new one will replace it
        stopUpdating();
        // Create the update task which will simply call updateData
        updateTask = new TimerTask() {
            @Override
            public void run() {
                updateData();
            }
        };
        updateTimer.scheduleAtFixedRate(updateTask, 0,
                updateSpeedInMilliseconds);
    }

    public synchronized void addAdditionalActionPerUpdate(UpdateAction action) {
        boolean actionNotYetAdded = additionalActionsPerUpdate.add(action);
        if (!actionNotYetAdded) {
            throw new IllegalArgumentException("Action is already added to this SpriteUpdater");
        }
    }

    public synchronized void removeAdditionalActionPerUpdate(UpdateAction action) {
        boolean actionExisted = additionalActionsPerUpdate.remove(action);

        if (!actionExisted) {
            throw new IllegalArgumentException("Action has not been added to this SpriteUpdater");
        }
    }

    public synchronized void addUpdateListener(UpdateListener listener) {
        boolean listenerNotYetAdded = updateListeners.add(listener);
        if (!listenerNotYetAdded) {
            throw new IllegalArgumentException("Listener is already listening to this SpriteUpdater");
        }
    }

    public synchronized void removeUpdateListener(UpdateListener listener) {
        boolean listenerExisted = updateListeners.remove(listener);

        if (!listenerExisted) {
            throw new IllegalArgumentException("Listener is not listening to this SpriteUpdater");
        }
    }

    private void updateData() {
        long elapsedTimeInNanoseconds = System.nanoTime() - systemNanoTimeFromLastUpdate;

        systemNanoTimeFromLastUpdate += elapsedTimeInNanoseconds;

        long elapsedTimeInMilliseconds = elapsedTimeInNanoseconds / numberOfNanosecondsInMillisecond;


        // Update the sprite and all of it's child sprites
        spriteToUpdate.updateAll(elapsedTimeInMilliseconds);

        // Update all the additional UpdateActions
        for (UpdateAction action : additionalActionsPerUpdate) {
            action.updateAction(elapsedTimeInMilliseconds);
        }

        updateUpdatesPerSecondCounter();
    }

    /**
     * Update updates per second counter and notify listeners if needed
     */
    private void updateUpdatesPerSecondCounter() {
        updatesPerSecondCounter.addActions(amountOfActionsPerGameUpdate);
        float currentUpdatesPerSecond = updatesPerSecondCounter.getActionCountPerTimeFrame();
        if (lastUpdatesPerSecondValue != currentUpdatesPerSecond) {
            // Updates per second value changed, notify all listeners
            for (UpdateListener listener : updateListeners) {
                listener.newUpdatePerSecondCalculated(currentUpdatesPerSecond);
            }
        }
        lastUpdatesPerSecondValue = currentUpdatesPerSecond;
    }

}
