package org.jamesgames.easysprite.physics.partitioning;

import org.jamesgames.easysprite.sprite.Sprite;

import java.awt.*;
import java.util.function.Consumer;

/**
 * SpacePartitioner is an interface that defines the behavior of any type of object that will partition {@link
 * Sprite}s's to subgroups of some overall space, such as a quadtree or a bsp tree. The purpose of such a class is to
 * limit how long it takes to lookup nearby sprites. Useful for determining possible collisions between {@link Sprite}s,
 * so you only do a handful amount of collision tests per Sprite instead of as many checks per Sprite as there are
 * Sprites.
 *
 * @author James Murphy
 */
public interface SpacePartitioner {

    void addSprite(Sprite s);

    void removeSprite(Sprite s);

    void applyActionWithAllPotentialCollidingSprites(Sprite spriteInPossibleCollision, Consumer<Sprite> action);

    void updatePosition(Sprite s);

    /**
     * Useful for visual debugging, default method draws nothing.
     */
    default void draw(Graphics2D g) {

    }
}
