package org.jamesgames.easysprite.sprite;

import net.jcip.annotations.ThreadSafe;
import org.jamesgames.easysprite.physics.partitioning.NullSpacePartitioner;
import org.jamesgames.easysprite.physics.partitioning.SpacePartitioner;
import org.jamesgames.easysprite.physics.simple.SimpleCollisionDirection;
import org.jamesgames.easysprite.physics.simple.SimpleShapeCollisionDetection;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * A Sprite is an object that represents drawable graphics with a specified x coordinate, y coordinate, x velocity, and
 * y velocity in an arbitrary unit type. A Sprite can also consist of child sprites, whose x and y coordinates are based
 * on the top left position of their parent sprite (so if parent Sprite X had an x,y position of 10,15, then child
 * Sprite Y's x,y coordinates of 50,60 would translate to 60,75 on it's parent's coordinate plane, but 50.60 on it's own
 * plane). Sprite objects can be updated and drawn in a parent first manner or children first manner (a depth first pre
 * or post order traversal). This is useful if you need to update a parent sprite and child sprite during the same
 * update, where the parent sprite depends on the latest state of the child sprite. Sprite objects also can be set to
 * draw debug graphics, where additional graphics on top of it's own graphics will be drawn that might represent useful
 * information or visual cues to debug something. Sprite also is Iterable, where the Iterator iterates through all child
 * Sprites.
 *
 * @author James Murphy
 */
@ThreadSafe
public class Sprite implements Iterable<Sprite> {
    private static final Sprite endRootSprite = new EndRootSprite();
    private static final Random randomGen = new Random();

    private final List<Sprite> childSprites = new ArrayList<>();
    private Sprite parentSprite = endRootSprite;
    private float xCoordinateTopLeft = 0;
    private float yCoordinateTopLeft = 0;
    private float oldXCoordinateTopLeft = xCoordinateTopLeft;
    private float oldYCoordinateTopLeft = yCoordinateTopLeft;
    private float xVelocity = 0;
    private float yVelocity = 0;
    private float oldXVelocity = xVelocity;
    private float oldYVelocity = yVelocity;
    private int width = 0;
    private int height = 0;
    private boolean drawingDebugGraphics = false;
    private SpacePartitioner spacePartitioner = new NullSpacePartitioner();

    public Sprite() {
        this(0, 0);
    }

    public Sprite(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public Iterator<Sprite> iterator() {
        return childSprites.iterator();
    }

    public final synchronized float getOldXCoordinateTopLeft() {
        return oldXCoordinateTopLeft;
    }

    public final synchronized int getOldXDrawingCoordinateTopLeft() {
        return getParentOldXDrawingCoordinateTopLeftInternalImpl() + getRoundedOldXCoordinateTopLeft();
    }

    public final synchronized int getRoundedOldXCoordinateTopLeft() {
        return Math.round(oldXCoordinateTopLeft);
    }

    synchronized int getParentOldXDrawingCoordinateTopLeftInternalImpl() {
        return parentSprite.getOldXDrawingCoordinateTopLeft();
    }

    public final synchronized float getOldYCoordinateTopLeft() {
        return oldYCoordinateTopLeft;
    }

    public final synchronized int getOldYDrawingCoordinateTopLeft() {
        return getParentOldYDrawingCoordinateTopLeftInternalImpl() + getRoundedOldYCoordinateTopLeft();
    }

    public final synchronized int getRoundedOldYCoordinateTopLeft() {
        return Math.round(oldYCoordinateTopLeft);
    }

    synchronized int getParentOldYDrawingCoordinateTopLeftInternalImpl() {
        return parentSprite.getOldYDrawingCoordinateTopLeft();
    }

    /**
     * @return x coordinate location
     */
    public final synchronized float getXCoordinateTopLeft() {
        return xCoordinateTopLeft;
    }

    /**
     * Set x coordinate location
     */
    public final synchronized void setXCoordinateTopLeft(float xCoordinateTopLeft) {
        this.xCoordinateTopLeft = xCoordinateTopLeft;
    }

    /**
     * @return x coordinate units rounded to nearest integer for drawing purposes
     */
    public final synchronized int getXDrawingCoordinateTopLeft() {
        return getParentXDrawingCoordinateTopLeftInternalImpl() + getRoundedXCoordinateTopLeft();
    }

    /**
     * @return x coordinate location rounded
     */
    public final synchronized int getRoundedXCoordinateTopLeft() {
        return Math.round(xCoordinateTopLeft);
    }

    /**
     * Retrieves the parent Sprite's x drawing coordinate. Package protected, it's to be overridden in order to stop
     * recursion by having an overridden method return 0 instead of referencing another object.
     */
    synchronized int getParentXDrawingCoordinateTopLeftInternalImpl() {
        return parentSprite.getXDrawingCoordinateTopLeft();
    }

    /**
     * @return y coordinate location
     */
    public final synchronized float getYCoordinateTopLeft() {
        return yCoordinateTopLeft;
    }

    /**
     * Set y coordinate location
     */
    public final synchronized void setYCoordinateTopLeft(float yCoordinateTopLeft) {
        this.yCoordinateTopLeft = yCoordinateTopLeft;
    }

    /**
     * @return y coordinate units rounded to nearest integer for drawing purposes
     */
    public final synchronized int getYDrawingCoordinateTopLeft() {
        return getParentYDrawingCoordinateTopLeftInternalImpl() + getRoundedYCoordinateTopLeft();
    }

    /**
     * @return y coordinate location rounded
     */
    public synchronized int getRoundedYCoordinateTopLeft() {
        return Math.round(yCoordinateTopLeft);
    }

    /**
     * Retrieves the parent Sprite's y drawing coordinate. Package protected, it's designed to be overridden in order to
     * stop recursion by having an overridden method return 0 instead of referencing another object..
     */
    synchronized int getParentYDrawingCoordinateTopLeftInternalImpl() {
        return parentSprite.getYDrawingCoordinateTopLeft();
    }

    public final synchronized float getOldXVelocity() {
        return oldXVelocity;
    }

    /**
     * @return xVelocity in coordinate units per millisecond
     */
    public final synchronized float getXVelocity() {
        return xVelocity;
    }

    /**
     * Set xVelocity in coordinate units per millisecond
     */
    public final synchronized void setXVelocity(float xVelocity) {
        this.xVelocity = xVelocity;
    }

    public final synchronized float getOldYVelocity() {
        return oldYVelocity;
    }

    /**
     * @return yVelocity in coordinate units per millisecond
     */
    public final synchronized float getYVelocity() {
        return yVelocity;
    }

    /**
     * Set yVelocity in coordinate units per millisecond
     */
    public final synchronized void setYVelocity(float yVelocity) {
        this.yVelocity = yVelocity;
    }

    /**
     * @return width of the sprite
     */
    public final synchronized int getWidth() {
        return width;
    }

    /**
     * Set the width of the sprite
     */
    public final synchronized void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return height of the sprite
     */
    public final synchronized int getHeight() {
        return height;
    }

    /**
     * Set the height of the sprite
     */
    public final synchronized void setHeight(int height) {
        this.height = height;
    }

    /**
     * Sets the height and width of the sprite
     */
    public final synchronized void resize(int newWidth, int newHeight) {
        int oldWidth = width;
        int oldHeight = height;
        setWidth(newWidth);
        setHeight(newHeight);
        onResize(newWidth, newHeight, oldWidth, oldHeight);
    }

    /**
     * Called when {@link Sprite#resize(int, int)} is executed, and called with the same values as well as the old width
     * and height values. The idea behind this method is that a subclass can implement this method to do additional
     * actions when the sprite resized.
     */
    protected synchronized void onResize(int newWidth, int newHeight, int oldWidth, int oldHeight) {
        // Do nothing default implementation
    }

    /**
     * Adds a child sprite to this sprite
     */
    public final synchronized void addChildSprite(Sprite sprite) {
        if (childSprites.contains(sprite)) {
            throw new IllegalArgumentException("Child sprite already exists in this parent sprite");
        }
        childSprites.add(sprite);
        // Set draw debug flag that this parent sprite has active
        sprite.setDrawingDebugGraphicsIncludingChildSprites(drawingDebugGraphics);
        // Also add to the partitioner
        spacePartitioner.addSprite(sprite);
        sprite.setParentSprite(this);
    }

    private void setParentSprite(Sprite newParentSprite) {
        parentSprite = newParentSprite;
    }

    /**
     * Removes a child sprite from this sprite.
     */
    public final synchronized void removeChildSprite(Sprite sprite) {
        boolean spriteExistedInCollection = childSprites.remove(sprite);

        if (!spriteExistedInCollection) {
            throw new IllegalArgumentException("Child sprite did not already exist in this parent sprite");
        }

        // Also remove from the partitioner
        spacePartitioner.removeSprite(sprite);

        // Do this after we throw the exception, so we don't ruin state on a Sprite accidentally.
        sprite.setParentSprite(endRootSprite);
    }

    /**
     * Removes child sprites in the passed collection from this sprite, at least one Sprite passed should exist in this
     * Sprite. If no sprites are passed, the method does nothing.
     */
    public final synchronized void removeChildSprites(Collection<Sprite> sprites) {
        boolean atLeastOneOfTheSpritesExistedInCollection = childSprites.removeAll(sprites);

        if (!sprites.isEmpty() && !atLeastOneOfTheSpritesExistedInCollection) {
            throw new IllegalArgumentException("None of the child sprites exist in this parent sprite");
        }

        // Only change the parent sprite if we know that the sprite was removed
        // (otherwise we are corrupting sprites who may still have some true parent elsewhere)
        // We can determine that by seeing if the sprite's parent was this sprite.
        // (the maintaining of a valid and correct parent sprite is a class invariant here that we can assume
        // to always be up to date when needed)
        sprites.stream().filter(s -> s.parentSprite == this).forEach(s ->
                // Sprite no longer has parent, so set the parent to endRootSprite
                s.setParentSprite(endRootSprite));
    }


    /**
     * Updates the Sprite based on how much time has elapsed, and then updates all of it's child sprites. This method is
     * designed to be overridden, so that subclasses can updateBeforeChildren other states it may have based on how much
     * time has elapsed.
     *
     * @param elapsedTimeInMilliseconds
     *         Time elapsed since last updateBeforeChildren
     */
    public final synchronized void updateAll(long elapsedTimeInMilliseconds) {
        // Update the position first, so that the extensions to this updateBeforeChildren may work on the latest possible Sprite
        // position
        setXCoordinateTopLeft(getXCoordinateTopLeft() + (getXVelocity() * elapsedTimeInMilliseconds));
        setYCoordinateTopLeft(getYCoordinateTopLeft() + (getYVelocity() * elapsedTimeInMilliseconds));

        updateBeforeChildren(elapsedTimeInMilliseconds);

        // update all the child sprites too
        for (Sprite childSprite : childSprites) {
            childSprite.updateAll(elapsedTimeInMilliseconds);
            updateChildSpritePartitionerPosition(childSprite);
        }

        updateAfterChildren(elapsedTimeInMilliseconds);

        // Updating of old values must occur after they were updated in this update, and before handling collisions
        updateOldCoordinatePositions();
        updateOldVelocityPositions();

        handlePotentialChildSpriteCollisions();
    }

    /**
     * Updates the Sprite before the child Sprites are updated. The idea is that this is a method that is overridden by
     * Sprite subclasses, while the updateAll method is not.
     */
    protected synchronized void updateBeforeChildren(long elapsedTimeInMilliseconds) {
    }

    /**
     * Updates the Sprite after the child Sprites are updated. The idea is that this is a method that is overridden by
     * Sprite subclasses, while the updateAll method is not.
     */
    protected synchronized void updateAfterChildren(long elapsedTimeInMilliseconds) {
    }

    private void updateChildSpritePartitionerPosition(Sprite childSprite) {
        if (childSprite.positionChangedDuringUpdate()) {
            spacePartitioner.updatePosition(childSprite);
        }
    }

    private boolean positionChangedDuringUpdate() {
        return oldXCoordinateTopLeft != xCoordinateTopLeft ||
                oldYCoordinateTopLeft != yCoordinateTopLeft;
    }

    private void updateOldCoordinatePositions() {
        oldXCoordinateTopLeft = xCoordinateTopLeft;
        oldYCoordinateTopLeft = yCoordinateTopLeft;
    }

    private void updateOldVelocityPositions() {
        oldXVelocity = xVelocity;
        oldYVelocity = yVelocity;
    }

    private void handlePotentialChildSpriteCollisions() {
        for (Sprite childSprite : childSprites) {
            spacePartitioner.applyActionWithAllPotentialCollidingSprites(childSprite,
                    spriteCollidingWithChild -> {
                        if (childSprite != spriteCollidingWithChild)
                            childSprite.determineIfCollisionOccurredAndHandle(spriteCollidingWithChild);
                    });
        }
    }

    private void determineIfCollisionOccurredAndHandle(Sprite potentialCollidingSprite) {
        SimpleCollisionDirection collision = potentialCollision(potentialCollidingSprite);
        if (collision != SimpleCollisionDirection.no_collision) {
            handleCollision(potentialCollidingSprite, collision);
        }
    }

    /**
     * Determines if this Sprite believes it collided with the potential colliding sprite which assumes both Sprites are
     * square shaped. The method is designed to be possibly overridden by Sprite subclasses, so each unique Sprite
     * subclass can define if their Sprite collides with another Sprite. The result of this method is used to determine
     * if a call to {@link Sprite#handleCollision(Sprite, SimpleCollisionDirection)} is made or not.
     */
    protected synchronized SimpleCollisionDirection potentialCollision(Sprite potentialCollidingSprite) {
        return collidesWithPositionsAsOfLastUpdate(potentialCollidingSprite);
    }


    /**
     * Handles the collision between this Sprite and another. The idea is that this method can be overridden by Sprite
     * subclasses to be notified when a collision occurs, and also be overloaded to have separate methods for different
     * Sprite collisions.
     */
    protected synchronized void handleCollision(Sprite collidingSprite, SimpleCollisionDirection direction) {
    }


    /**
     * Draws the Sprite, and then draws all of it's child sprites. The idea is that this is the method that gets called
     * whenever the Sprite needs to be rendered to a graphics object.
     */
    public final synchronized void drawAll(Graphics2D g) {
        // draw the graphics that need to appear under the child sprite graphics
        drawUnderChildren(g);
        // draw the debug graphics too if needed
        if (drawingDebugGraphics) {
            debugDraw(g);
        }

        // Draw all the child sprites too
        // Using a for loop here because in the future I may rework the thread safety of the class and allow updating
        // and drawing to happen at the same time, or make it an option.
        for (int i = childSprites.size() - 1; i >= 0; i--) {
            childSprites.get(i).drawAll(g);
        }

        // now draw all the graphics that need to appear over the child sprite graphics
        drawOverChildren(g);
    }

    /**
     * Draws the graphics representing the Sprite before the child Sprites are drawn. The idea is that this is a method
     * that is overridden by Sprite subclasses, while the drawAll method is not, in order to recursively draw all child
     * sprites after drawing the parent sprites first.
     */
    protected synchronized void drawUnderChildren(Graphics2D g) {
    }

    /**
     * Draws the graphics representing the Sprite after the child Sprites are drawn. The idea is that this is a method
     * that is overridden by Sprite subclasses, while the drawAll method is not, in order to recursively draw all child
     * sprites after drawing the parent sprites first.
     */
    protected synchronized void drawOverChildren(Graphics2D g) {
    }

    /**
     * Draws any debug information or debug graphics that may help in debugging. Debug drawing is drawn before the
     * children graphics are drawn.
     */
    protected synchronized void debugDraw(Graphics2D g) {
    }

    /**
     * Sets whether or not the Sprite should draw debug graphics and information
     */
    public final synchronized void setDrawingDebugGraphics(boolean drawingDebugGraphics) {
        this.drawingDebugGraphics = drawingDebugGraphics;
    }

    /**
     * Sets whether or not the Sprite and all of it's child Sprites' should draw debug graphics and information
     */
    public final synchronized void setDrawingDebugGraphicsIncludingChildSprites(boolean drawingDebugGraphics) {
        setDrawingDebugGraphics(drawingDebugGraphics);
        for (Sprite s : this) {
            s.setDrawingDebugGraphicsIncludingChildSprites(drawingDebugGraphics);
        }
    }

    /**
     * Toggles whether or not the Sprite and all it's child Sprite' should draw debug graphics and information to the
     * opposite of current drawing debug graphics state of this Sprite. For example, if this Sprite is currently not
     * drawing debug graphics, and child sprite A is, but Child Sprite B is not, then calling this method will cause
     * this Sprite to then draw debug graphics, as well as having A to continue drawing debugs and B to start drawing
     * debug graphics.
     */
    public final synchronized void toggleDrawingDebugGraphicsIncludingChildSprites() {
        setDrawingDebugGraphicsIncludingChildSprites(!drawingDebugGraphics);
    }

    public final synchronized boolean isDrawingDebugGraphics() {
        return drawingDebugGraphics;
    }

    /**
     * Determines if this sprite collides with another Sprite based on positions of the two Sprite objects at the end of
     * the last update, assumes sprites are rectangle shaped. Designed to be overridden by subclasses to handle more
     * complex collisions
     */
    synchronized SimpleCollisionDirection collidesWithPositionsAsOfLastUpdate(Sprite otherShape) {
        return SimpleShapeCollisionDetection.detectCollisionOfTwoRectangles(
                // This rect
                getOldXDrawingCoordinateTopLeft(), getOldYDrawingCoordinateTopLeft(), getWidth(),
                getHeight(),
                // Other shape rect
                otherShape.getOldXDrawingCoordinateTopLeft(), otherShape.getOldYDrawingCoordinateTopLeft(),
                otherShape.getWidth(), otherShape.getHeight());
    }


    /**
     * @return The number of child sprites in this parent sprite
     */
    public final synchronized int getChildSpriteCount() {
        return childSprites.size();
    }


    /**
     * Determines if this sprite is visible within it's parent sprite's boundaries
     */
    public final synchronized boolean isVisibleOnParentSprite() {
        return collidesWithPositionsAsOfLastUpdate(parentSprite) != SimpleCollisionDirection.no_collision;
    }

    /**
     * Reverses velocities to stay on parent sprite, assumes the Sprite is moving in the direction to be off of the
     * parent Sprite if found to exceed the boundaries of the parent sprite (so it simply does a dumb reversal of x or y
     * velocities if found on left/right or top/bottom borders.
     */
    public final synchronized void repositionAndReverseVelocitiesIfVeeringOffParent() {
        if (oldXCoordinateTopLeft < 0) {
            setXVelocity(-getOldXVelocity());
            setXCoordinateTopLeft(0);
        } else if (oldXCoordinateTopLeft + width > parentSprite.getWidth()) {
            setXVelocity(-getOldXVelocity());
            setXCoordinateTopLeft(parentSprite.getWidth() - width);
        }

        if (oldYCoordinateTopLeft < 0) {
            setYVelocity(-getOldYVelocity());
            setYCoordinateTopLeft(0);
        } else if (oldYCoordinateTopLeft + height > parentSprite.getHeight()) {
            setYVelocity(-getOldYVelocity());
            setYCoordinateTopLeft(parentSprite.getHeight() - height);
        }
    }

    /**
     * Randomly sets the x and y velocity of the Sprite up to positive or negative maxVelocity, but not between pos/neg
     * maxVelocity that is some percent of maxVelocity near 0. So for inputs of 10.0f, and 0.50f, the velocity will
     * randomly be set uniformly between -10.0 and -5.0, and 5.0 and 10.0.
     *
     * @param maxVelocity
     *         Top speed allowed
     * @param percentFromZeroToExtremeToNotSetTo
     *         Percentage of the random bounds to ignore, so with a max speed of 1.0f, with percentage of 50 (0.50f),
     *         the speed will only be set to -/+1.0f to -/+0.5f, and never anywhere between -0.5f and 0.5f.
     */
    public final synchronized void setVelocitiesToRandomAmount(float maxVelocity,
            float percentFromZeroToExtremeToNotSetTo) {
        setXVelocity(maxVelocity - randomGen.nextFloat() * (maxVelocity * 2));
        if (getXVelocity() > -(maxVelocity * percentFromZeroToExtremeToNotSetTo) &&
                getXVelocity() < (maxVelocity * percentFromZeroToExtremeToNotSetTo)) {
            // Always ensure the random value is on either extreme of the random range
            // based on percentFromZeroToExtremeToNotSetTo
            setXVelocity(getXVelocity() * 2);
        }
        setYVelocity(maxVelocity - randomGen.nextFloat() * (maxVelocity * 2));
        if (getYVelocity() > -(maxVelocity * percentFromZeroToExtremeToNotSetTo) &&
                getYVelocity() < (maxVelocity * percentFromZeroToExtremeToNotSetTo)) {
            setYVelocity(getYVelocity() * 2);
        }
    }

    /**
     * @return The {@link SpacePartitioner} of this Sprite
     */
    public final synchronized SpacePartitioner getSpacePartitioner() {
        return spacePartitioner;
    }

    /**
     * Set a {@link SpacePartitioner} for the Sprite. A SpacePartitioner will allow child sprites to auto detect
     * possible collisions via the {@link Sprite#potentialCollision(Sprite)} method.
     */
    public final synchronized void setSpacePartitioner(SpacePartitioner spacePartitioner) {
        this.spacePartitioner = spacePartitioner;
    }

    private static final class EndRootSprite extends Sprite {

        @Override
        synchronized int getParentYDrawingCoordinateTopLeftInternalImpl() {
            return 0;
        }

        @Override
        synchronized int getParentXDrawingCoordinateTopLeftInternalImpl() {
            return 0;
        }

        @Override
        synchronized int getParentOldYDrawingCoordinateTopLeftInternalImpl() {
            return 0;
        }

        @Override
        synchronized int getParentOldXDrawingCoordinateTopLeftInternalImpl() {
            return 0;
        }
    }
}
