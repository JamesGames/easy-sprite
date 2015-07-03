package org.jamesgames.easysprite.physics.simple;

/**
 * SimpleCollisionDirection represents five collision states of left, right, top, bottom, and no collision.
 *
 * @author James Murphy
 */
public enum SimpleCollisionDirection {

    no_collision(false, false) {
        @Override
        public SimpleCollisionDirection oppositeDirection() {
            return no_collision;
        }
    }, from_left(false, true) {
        @Override
        public SimpleCollisionDirection oppositeDirection() {
            return from_right;
        }
    }, from_right(false, true) {
        @Override
        public SimpleCollisionDirection oppositeDirection() {
            return from_left;
        }
    }, from_top(true, false) {
        @Override
        public SimpleCollisionDirection oppositeDirection() {
            return from_bottom;
        }
    }, from_bottom(true, false) {
        @Override
        public SimpleCollisionDirection oppositeDirection() {
            return from_top;
        }
    };

    private final boolean isTopOrBottomDirection;
    private final boolean isRightOrLeftDirection;

    SimpleCollisionDirection(boolean isTopOrBottomDirection, boolean isRightOrLeftDirection) {
        this.isTopOrBottomDirection = isTopOrBottomDirection;
        this.isRightOrLeftDirection = isRightOrLeftDirection;
    }

    /**
     * Exists due to how one cannot have an forward reference of an enum not declared yet in another enum constructor
     * call declared before it
     *
     * @return The opposite direction
     */
    public abstract SimpleCollisionDirection oppositeDirection();

    public final boolean isTopOrBottomDirection() {
        return isTopOrBottomDirection;
    }

    public final boolean isRightOrLeftDirection() {
        return isRightOrLeftDirection;
    }
}
