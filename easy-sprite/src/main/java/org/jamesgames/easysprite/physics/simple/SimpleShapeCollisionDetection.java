package org.jamesgames.easysprite.physics.simple;

import org.jamesgames.easysprite.sprite.Sprite;

/**
 * SimpleShapeCollisionDetection is a class that provides a static interface to detect a {@link
 * SimpleCollisionDirection} between two rectangles, and from what general direction the collision came from. The class
 * also provides other useful but basic methods for collisions between two Sprites.
 *
 * @author James Murphy
 */
public class SimpleShapeCollisionDetection {

    /**
     * Determines if there is a collision between two rectangles and if so from what general direction. Direction is
     * described in what part of rectangle One that Rectangle Two "hit", so if rectangle One is intersecting Rectangle
     * Two and rectangle One appears to be more above rectangle Two then it is to the side of rectangle two, then
     * from_bottom would be returned. If the collision came equally on a corner of two sprites, then this method favors
     * side collisions and will return either from_left or from_right.
     *
     * @return If there is a collision and if so the direction of the collision
     */
    public static SimpleCollisionDirection detectCollisionOfTwoRectangles(int xOne, int yOne, int widthOne,
            int heightOne,
            int xTwo, int yTwo, int widthTwo, int heightTwo) {
        if (widthOne <= 0 || heightOne <= 0 || widthTwo <= 0 || heightTwo <= 0) {
            return SimpleCollisionDirection.no_collision;
        }

        // check if the two sprites' boundaries intersect
        if (xOne < xTwo + widthTwo &&
                xTwo < xOne + widthOne &&
                yOne < yTwo + heightTwo &&
                yTwo < yOne + heightOne) {

            int numberOfUnitsSharedInXAxis = calculateUnitsSharedAlongAxis(xOne, widthOne, xTwo, widthTwo);
            int numberOfUnitsSharedInYAxis = calculateUnitsSharedAlongAxis(yOne, heightOne, yTwo, heightTwo);

            if (numberOfUnitsSharedInXAxis > numberOfUnitsSharedInYAxis) {
                // Intersection of two came from more of a y direction
                return yOne < yTwo ? SimpleCollisionDirection.from_bottom :
                        SimpleCollisionDirection.from_top;
            } else {
                // Intersection of two came from more of a x direction
                return xOne < xTwo ? SimpleCollisionDirection.from_right :
                        SimpleCollisionDirection.from_left;
            }
        }

        return SimpleCollisionDirection.no_collision;
    }

    private static int calculateUnitsSharedAlongAxis(int axisCoordOne, int axisLengthOne, int axisCoordTwo,
            int axisLengthTwo) {
        boolean isARectangleFullyInTheOtherAlongXAxis =
                (axisCoordOne < axisCoordTwo && axisCoordOne + axisLengthOne > axisCoordTwo + axisLengthTwo) ||
                        (axisCoordTwo < axisCoordOne && axisCoordTwo + axisLengthTwo > axisCoordOne + axisLengthOne);
        if (isARectangleFullyInTheOtherAlongXAxis) {
            return Math.min(axisLengthOne, axisLengthTwo);
        } else {
            boolean rectOneIsLeftOfRectTwo = axisCoordOne < axisCoordTwo;
            return rectOneIsLeftOfRectTwo ? (axisCoordOne + axisLengthOne) - axisCoordTwo :
                    (axisCoordTwo + axisLengthTwo) - axisCoordOne;
        }
    }

    public static void moveSpritesOffOfCollidingSprite(Sprite sprite,
            Sprite potentialCollidingSprite, SimpleCollisionDirection collision) {
        switch (collision) {
            case no_collision:
                break;
            case from_right:
                sprite.setXCoordinateTopLeft(potentialCollidingSprite.getOldXCoordinateTopLeft() - sprite.getWidth());
                break;
            case from_left:
                sprite.setXCoordinateTopLeft(
                        potentialCollidingSprite.getOldXCoordinateTopLeft() + potentialCollidingSprite.getWidth());
                break;
            case from_bottom:
                sprite.setYCoordinateTopLeft(potentialCollidingSprite.getOldYCoordinateTopLeft() - sprite.getHeight());
                break;
            case from_top:
                sprite.setYCoordinateTopLeft(
                        potentialCollidingSprite.getOldYCoordinateTopLeft() + potentialCollidingSprite.getHeight());
                break;
        }
    }

    /**
     * Reverses velocities if needed on a collision of two sprites. If both sprites were travelling in the same
     * direction, only the faster of the two sprites has the velocity changed.
     *
     * @param sprite
     *         The sprite that had a collision, whom the direction corresponds to
     * @param collidingSprite
     *         The other sprite that the main sprite collided with
     * @param direction
     *         The direction in relation to the main sprite that the collision (other sprite) came from
     */
    public static void changeVelocitiesIfNeededOnCollision(Sprite sprite, Sprite collidingSprite,
            SimpleCollisionDirection direction) {
        if (chasedCollidingSpriteFromLeftOrRight(sprite, collidingSprite, direction) ||
                direction.isRightOrLeftDirection() && xVelocitiesBothDifferentDirections(sprite, collidingSprite)) {
            sprite.setXVelocity(-sprite.getXVelocity());
        } else if (chasedCollidingSpriteFromBelowOrAbove(sprite, collidingSprite, direction) ||
                direction.isTopOrBottomDirection() && yVelocitiesBothDifferentDirections(sprite, collidingSprite)) {
            sprite.setYVelocity(-sprite.getYVelocity());
        }
    }

    /**
     * Reverses velocities if needed on a collision of two sprites, tries to transfer velocities from a faster sprite to
     * a slower sprite if the faster sprite hit the slower sprite where both sprites were traveling in the same
     * direction.
     *
     * @param sprite
     *         The sprite that had a collision, whom the direction corresponds to
     * @param collidingSprite
     *         The other sprite that the main sprite collided with
     * @param direction
     *         The direction in relation to the main sprite that the collision (other sprite) came from
     */
    public static void changeVelocitiesWithMomentumTransferIfNeededOnCollision(Sprite sprite, Sprite collidingSprite,
            SimpleCollisionDirection direction) {
        // Sprite chased (x direction)
        if (chasedCollidingSpriteFromLeftOrRight(sprite, collidingSprite, direction)) {
            // Flip the velocity, but only go half as fast (other half transferred to the other sprite, for when it
            // calls this method
            sprite.setXVelocity(-(sprite.getOldXVelocity() / 2));
        }
        // Sprite was chased (x direction)
        else if (chasedCollidingSpriteFromLeftOrRight(collidingSprite, sprite, direction.oppositeDirection())) {
            // Add some of the velocity from the previous sprite
            sprite.setXVelocity(sprite.getOldXVelocity() + (collidingSprite.getOldXVelocity() / 2));
        }
        // Neither sprite chased (x direction)
        else if (direction.isRightOrLeftDirection() && xVelocitiesBothDifferentDirections(sprite, collidingSprite)) {
            sprite.setXVelocity(-sprite.getOldXVelocity());
        }
        // Sprite chased (y direction)
        else if (chasedCollidingSpriteFromBelowOrAbove(sprite, collidingSprite, direction)) {
            sprite.setYVelocity(-(sprite.getOldYVelocity() / 2));
        }
        // Sprite was chased (y direction)
        else if (chasedCollidingSpriteFromBelowOrAbove(collidingSprite, sprite, direction.oppositeDirection())) {
            sprite.setYVelocity(sprite.getOldYVelocity() + (collidingSprite.getOldYVelocity() / 2));
        }
        // Neither sprite chased (y direction)
        else if (direction.isTopOrBottomDirection() && yVelocitiesBothDifferentDirections(sprite, collidingSprite)) {
            sprite.setYVelocity(-sprite.getOldYVelocity());
        }
    }

    private static boolean chasedCollidingSpriteFromLeftOrRight(Sprite s, Sprite collidingS,
            SimpleCollisionDirection direction) {
        return direction.isRightOrLeftDirection() &&
                ((xVelocitiesBothPositive(s, collidingS) && direction == SimpleCollisionDirection.from_right) ||
                        (xVelocitiesBothNegative(s, collidingS) && direction == SimpleCollisionDirection.from_left));
    }

    private static boolean chasedCollidingSpriteFromBelowOrAbove(Sprite s, Sprite collidingS,
            SimpleCollisionDirection direction) {
        return direction.isTopOrBottomDirection() &&
                ((yVelocitiesBothPositive(s, collidingS) && direction == SimpleCollisionDirection.from_bottom) ||
                        (yVelocitiesBothNegative(s, collidingS) && direction == SimpleCollisionDirection.from_top));
    }

    private static boolean xVelocitiesBothPositive(Sprite a, Sprite b) {
        return a.getOldXVelocity() > 0 && b.getOldXVelocity() > 0;
    }

    private static boolean xVelocitiesBothNegative(Sprite a, Sprite b) {
        return a.getOldXVelocity() < 0 && b.getOldXVelocity() < 0;
    }

    private static boolean yVelocitiesBothPositive(Sprite a, Sprite b) {
        return a.getOldYVelocity() > 0 && b.getOldYVelocity() > 0;
    }

    private static boolean yVelocitiesBothNegative(Sprite a, Sprite b) {
        return a.getOldYVelocity() < 0 && b.getOldYVelocity() < 0;
    }

    private static boolean xVelocitiesBothDifferentDirections(Sprite a, Sprite b) {
        return (a.getOldXVelocity() >= 0 && b.getOldXVelocity() <= 0) ||
                (a.getOldXVelocity() <= 0 && b.getOldXVelocity() >= 0);
    }

    private static boolean yVelocitiesBothDifferentDirections(Sprite a, Sprite b) {
        return (a.getOldYVelocity() >= 0 && b.getOldYVelocity() <= 0) ||
                (a.getOldYVelocity() <= 0 && b.getOldYVelocity() >= 0);
    }
}
