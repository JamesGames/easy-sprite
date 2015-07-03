package org.jamesgames.easysprite.updater;

/**
 * UpdateAction is an interface that defines one method that can be used to describe something that should be done
 * during an update done by {@link SpriteUpdater}
 *
 * @author James Murphy
 */
public interface UpdateAction {
    void updateAction(long elapsedTimeInMilliseconds);
}
