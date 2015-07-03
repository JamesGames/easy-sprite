package org.jamesgames.easysprite.physics.partitioning;

import org.jamesgames.easysprite.sprite.Sprite;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * SimpleSpacePartitioner is a {@link SpacePartitioner} that simply has one partitioned space for all Sprites, so the
 * performance is quite bad, except for small sets of Sprites.
 *
 * @author James Murphy
 */
public class SimpleSpacePartitioner implements SpacePartitioner {

    private final Set<Sprite> sprites = new HashSet<>();

    @Override
    public void addSprite(Sprite s) {
        sprites.add(s);
    }

    @Override
    public void removeSprite(Sprite s) {
        sprites.remove(s);
    }

    @Override
    public void applyActionWithAllPotentialCollidingSprites(Sprite spriteInPossibleCollision, Consumer<Sprite> action) {
        sprites.forEach(action);
    }

    @Override
    public void updatePosition(Sprite s) {
    }
}
