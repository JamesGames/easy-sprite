package org.jamesgames.easysprite.updater;

/**
 * UpdateListener is an interface that defines a method that should be called whenever the calculated updates per second
 * in a {@link SpriteUpdater} object is calculated.
 *
 * @author James Murphy
 */
public interface UpdateListener {
    void newUpdatePerSecondCalculated(float updatesPerSecond);
}
