package org.jamesgames.easysprite.physics;

/**
 * SimpleShapeCollisionDetection is a class that provides a static interface to detect a {@link
 * org.jamesgames.easysprite.physics.SimpleCollisionDirection} between two rectangles, and from what general direction
 * the collision came from.
 *
 * @author James Murphy
 */
public class SimpleShapeCollisionDetection {

    /**
     * Determines if there is a collision between two rectangles and if so from what general direction. Direction is
     * described in what part of rectangle Two that Rectangle One "hit", so if rectangle One is intersecting Rectangle
     * Two and in general rectangle One appears to be more above rectangle Two then it is to the side of rectangle two,
     * then top_collision would be returned. If the collision came equally on a corner of two sprites, then this method
     * favors side collisions and will return either left_collision or right_collision.
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
            boolean rectOneIsLeftOfRectTwo = xOne < xTwo;
            int numberOfUnitsSharedInXAxis = rectOneIsLeftOfRectTwo ? widthOne - xTwo : widthTwo - xOne;
            // Need to do this, in case you have say a rectangle intersecting the top of another, where this first
            // rectangle's far right index and far left index are both past to and the right and left of the second
            // rectangle's extremes
            numberOfUnitsSharedInXAxis = Math.min(Math.min(widthOne, widthTwo), numberOfUnitsSharedInXAxis);

            boolean rectOneIsAboveRectTwo = yOne < yTwo;
            int numberOfUnitsSharedInYAxis = rectOneIsAboveRectTwo ? heightOne - yTwo : heightTwo - yOne;
            numberOfUnitsSharedInYAxis = Math.min(Math.min(heightOne, heightTwo), numberOfUnitsSharedInYAxis);

            if (numberOfUnitsSharedInXAxis > numberOfUnitsSharedInYAxis) {
                // Intersection of two came from more of a y direction
                return rectOneIsAboveRectTwo ? SimpleCollisionDirection.top_collision :
                        SimpleCollisionDirection.bottom_collision;
            } else {
                // Intersection of two came from more of a x direction
                return rectOneIsLeftOfRectTwo ? SimpleCollisionDirection.left_collision :
                        SimpleCollisionDirection.right_collision;
            }
        }

        return SimpleCollisionDirection.no_collision;
    }
}
