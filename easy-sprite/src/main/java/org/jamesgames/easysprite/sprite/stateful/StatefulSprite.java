package org.jamesgames.easysprite.sprite.stateful;

import org.jamesgames.easysprite.animation.Animation;
import org.jamesgames.easysprite.animation.AnimationDescription;
import org.jamesgames.easysprite.sprite.Sprite;

import java.awt.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * StatefulSprite is a {@link Sprite} whose states are based on an Enum where the enum values can be flagged as active
 * or inactive. The combination of active states will map to an animation which is used to help render the Sprite's
 * graphics.
 * <p>
 * This class could be improved to be designed in some way to allow implementations to be extended themselves, someway
 * to extend the active list of states (like, a GhostFlyableSprite extends FlyableSprite which extends StatefulSprite
 * through something like a decorator pattern. However, enums are not extensible. A quick change would be to change
 * ActiveStateSet to just contain {@link java.lang.Object}s, but that would introduce a lot of casting and instanceof
 * calls. Perhaps what is needed to keep minimal changes away from what already exists is to just use plain old int
 * based enums to represent different states.
 * <p>
 * Potential update, design more of StatefulSprite to be thread-safe. This right now, is pretty impossible because <E
 * extends Enum<E>> can be mutable, and you can't enforce a user of this class to always provide a thread-safe version
 * of <E extends Enum<E>>. StatefulSprite doesn't know what <E extends Enum<E>> will be, and that Enum could be
 * available globally and modified anywhere in code like any Enum. Perhaps I should have picked another way to represent
 * states.
 *
 * @author James Murphy
 */
public class StatefulSprite<E extends Enum<E>> extends Sprite {
    private final ActiveStateSet<E> activeStates = new ActiveStateSet<>();
    private final StatesToAnimationMap<E> activeStatesToAnimation;
    private final Animation currentAnimation;

    public StatefulSprite(Supplier<StatesToAnimationMap<E>> statesToAnimationMapSupplier) {
        this(statesToAnimationMapSupplier.get());
    }

    public StatefulSprite(StatesToAnimationMap<E> activeStatesToAnimation) {
        this.activeStatesToAnimation = activeStatesToAnimation;
        currentAnimation = new Animation(this.activeStatesToAnimation.getAnimationDescription(
                activeStates));
    }


    protected synchronized void setStateActive(E state) {
        activeStates.setStateActive(state);
    }

    protected synchronized void setStateInactive(E state) {
        activeStates.setStateInactive(state);
    }

    protected synchronized boolean isStateActive(E state) {
        return activeStates.isStateActive(state);
    }

    protected synchronized boolean isAnyActiveState(Predicate<E> query) {
        return activeStates.isAnyActiveState(query);
    }

    @Override
    protected synchronized void updateBeforeChildren(long elapsedTimeInMilliseconds) {
        super.updateBeforeChildren(elapsedTimeInMilliseconds);
        if (activeStates.hasAnyStatesChangedSinceLastQuery()) {
            AnimationDescription possibleNewAnimationDescription = activeStatesToAnimation.getAnimationDescription(
                    activeStates);
            if (possibleNewAnimationDescription != currentAnimation.getAnimationDescription()) {
                currentAnimation.changeAnimationDescription(possibleNewAnimationDescription);
            }
        }
        currentAnimation.update(elapsedTimeInMilliseconds);
    }

    @Override
    protected synchronized void drawUnderChildren(Graphics2D g) {
        super.drawUnderChildren(g);
        currentAnimation.drawAnimation(g, getXDrawingCoordinateTopLeft(), getYDrawingCoordinateTopLeft());
    }
}
