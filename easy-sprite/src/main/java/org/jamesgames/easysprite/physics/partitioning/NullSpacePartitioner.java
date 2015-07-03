package org.jamesgames.easysprite.physics.partitioning;

import org.jamesgames.easysprite.sprite.Sprite;

import java.util.function.Consumer;

/**
 * NullSpacePartitioner implements {@link SpacePartitioner} but does nothing. Used in places as a null object pattern.
 *
 * @author James Murphy
 */
public class NullSpacePartitioner implements SpacePartitioner {

    @Override
    public void addSprite(Sprite s) {

    }

    @Override
    public void removeSprite(Sprite s) {

    }

    @Override
    public void applyActionWithAllPotentialCollidingSprites(Sprite spriteInPossibleCollision, Consumer<Sprite> action) {
    }

    @Override
    public void updatePosition(Sprite s) {

    }
}
