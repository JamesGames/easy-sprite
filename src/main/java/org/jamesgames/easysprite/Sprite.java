package org.jamesgames.easysprite;

import net.jcip.annotations.ThreadSafe;
import org.jamesgames.easysprite.physics.SimpleCollisionDirection;
import org.jamesgames.easysprite.physics.SimpleShapeCollisionDetection;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
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
    private final static Sprite endRootSprite = new EndRootSprite(0, 0);

    private final List<Sprite> childSprites = new ArrayList<>();
    private Sprite parentSprite = endRootSprite;
    private float xCoordinateTopLeft = 0;
    private float yCoordinateTopLeft = 0;
    private float xVelocity = 0;
    private float yVelocity = 0;
    private int width = 0;
    private int height = 0;
    private boolean drawingDebugGraphics = false;

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

    /**
     * @return x coordinate location
     */
    public synchronized final float getXCoordinateTopLeft() {
        return xCoordinateTopLeft;
    }


    /**
     * @return x coordinate units rounded to nearest integer for drawing purposes
     */
    public synchronized final int getXDrawingCoordinateTopLeft() {
        return getParentXDrawingCoordinateTopLeftInternalImpl() + getRoundedXCoordinateTopLeft();
    }

    /**
     * @return x coordinate location rounded
     */
    public synchronized final int getRoundedXCoordinateTopLeft() {
        return Math.round(xCoordinateTopLeft);
    }

    /**
     * Retrieves the parent Sprite's x drawing coordinate. Package protected, it's to be overridden in order to stop
     * recursion by having an overridden method return 0 instead of referencing another object.
     */
    int getParentXDrawingCoordinateTopLeftInternalImpl() {
        return parentSprite.getXDrawingCoordinateTopLeft();
    }

    /**
     * Set x coordinate location
     */
    public synchronized final void setXCoordinateTopLeft(float xCoordinateTopLeft) {
        this.xCoordinateTopLeft = xCoordinateTopLeft;
    }


    /**
     * @return y coordinate location
     */
    public synchronized final float getYCoordinateTopLeft() {
        return yCoordinateTopLeft;
    }

    /**
     * @return y coordinate units rounded to nearest integer for drawing purposes
     */
    public synchronized final int getYDrawingCoordinateTopLeft() {
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
    int getParentYDrawingCoordinateTopLeftInternalImpl() {
        return parentSprite.getYDrawingCoordinateTopLeft();
    }

    /**
     * Set y coordinate location
     */
    public synchronized final void setYCoordinateTopLeft(float yCoordinateTopLeft) {
        this.yCoordinateTopLeft = yCoordinateTopLeft;
    }

    /**
     * @return xVelocity in coordinate units per millisecond
     */
    public synchronized final float getXVelocity() {
        return xVelocity;
    }

    /**
     * Set xVelocity in coordinate units per millisecond
     */
    public synchronized final void setXVelocity(float xVelocity) {
        this.xVelocity = xVelocity;
    }

    /**
     * @return yVelocity in coordinate units per millisecond
     */
    public synchronized final float getYVelocity() {
        return yVelocity;
    }

    /**
     * Set yVelocity in coordinate units per millisecond
     */
    public synchronized final void setYVelocity(float yVelocity) {
        this.yVelocity = yVelocity;
    }

    /**
     * @return width of the sprite
     */
    public synchronized final int getWidth() {
        return width;
    }

    /**
     * Set the width of the sprite
     */
    public synchronized final void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return height of the sprite
     */
    public synchronized final int getHeight() {
        return height;
    }

    /**
     * Set the height of the sprite
     */
    public synchronized final void setHeight(int height) {
        this.height = height;
    }

    /**
     * Sets the height and width of the sprite
     */
    public synchronized final void resize(int newWidth, int newHeight) {
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
    public synchronized final void addChildSprite(Sprite sprite) {
        if (childSprites.contains(sprite)) {
            throw new IllegalArgumentException("Child sprite already exists in this parent sprite");
        }
        childSprites.add(sprite);
        sprite.setParentSprite(this);
    }

    private void setParentSprite(Sprite newParentSprite) {
        this.parentSprite = newParentSprite;
    }

    /**
     * Removes a child sprite from this sprite.
     */
    public synchronized final void removeChildSprite(Sprite sprite) {
        boolean spriteExistedInCollection = childSprites.remove(sprite);

        if (!spriteExistedInCollection) {
            throw new IllegalArgumentException("Child sprite did not already exist in this parent sprite");
        }

        // Do this after we throw the exception, so we don't ruin state on a Sprite accidentally.
        sprite.setParentSprite(endRootSprite);
    }

    /**
     * Removes child sprites in the passed collection from this sprite, at least one Sprite passed should exist in this
     * Sprite. If no sprites are passed, the method does nothing.
     */
    public synchronized final void removeChildSprites(Collection<Sprite> sprites) {
        boolean atLeastOneOfTheSpritesExistedInCollection = childSprites.removeAll(sprites);

        if (sprites.size() > 0 && !atLeastOneOfTheSpritesExistedInCollection) {
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
    public synchronized final void updateAll(long elapsedTimeInMilliseconds) {
        // Update the position first, so that the extensions to this updateBeforeChildren may work on the latest possible Sprite
        // position
        setXCoordinateTopLeft(getXCoordinateTopLeft() + (getXVelocity() * elapsedTimeInMilliseconds));
        setYCoordinateTopLeft(getYCoordinateTopLeft() + (getYVelocity() * elapsedTimeInMilliseconds));

        updateBeforeChildren(elapsedTimeInMilliseconds);

        // updateBeforeChildren all the child sprites too
        for (Sprite childSprite : childSprites) {
            childSprite.updateAll(elapsedTimeInMilliseconds);
        }

        updateAfterChildren(elapsedTimeInMilliseconds);
    }

    /**
     * Updates the Sprite before the child Sprites are updated. The idea is that this is a method that is overridden by
     * Sprite subclasses, while the updateAll method is not, in order to recursively update all child sprites after
     * updating the parent sprites first.
     */
    protected synchronized void updateBeforeChildren(long elapsedTimeInMilliseconds) {
    }

    /**
     * Updates the Sprite after the child Sprites are updated. The idea is that this is a method that is overridden by
     * Sprite subclasses, while the updateAll method is not, in order to recursively update all child sprites after
     * updating the parent sprites first.
     */
    protected synchronized void updateAfterChildren(long elapsedTimeInMilliseconds) {
    }


    /**
     * Draws the Sprite, and then draws all of it's child sprites. The idea is that this is the method that gets called
     * whenever the Sprite needs to be rendered to a graphics object.
     */
    public synchronized final void drawAll(Graphics2D g) {
        // draw the graphics that need to appear under the child sprite graphics
        this.drawUnderChildren(g);
        // draw the debug graphics too if needed
        if (drawingDebugGraphics) {
            this.debugDraw(g);
        }

        // Draw all the child sprites too
        // Using a for loop here because in the future I may rework the thread safety of the class and allow updating
        // and drawing to happen at the same time, or make it an option.
        for (int i = childSprites.size() - 1; i >= 0; i--) {
            childSprites.get(i).drawAll(g);
        }

        // now draw all the graphics that need to appear over the child sprite graphics
        this.drawOverChildren(g);

        //setIsInNeedOfRepaint(false);
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
    public synchronized final void setDrawingDebugGraphics(boolean drawingDebugGraphics) {
        this.drawingDebugGraphics = drawingDebugGraphics;
        setDrawingDebugGraphicsToCachedSprites(drawingDebugGraphics);
    }

    /**
     * Method can be overridden by subclasses for subclasses to toggle the drawing debug graphic state of any Sprites
     * that are not child Sprites but are still potential child Sprites which that may be swapping in and out often. For
     * example, a Sprite may have cached some other Sprites that will make up the Sprite in two different states, but
     * only a subset of those Sprites are currently child Sprites,
     */
    protected synchronized void setDrawingDebugGraphicsToCachedSprites(boolean drawingDebugGraphics) {
    }

    /**
     * Sets whether or not the Sprite and all of it's child Sprites' should draw debug graphics and information
     */
    public synchronized final void setDrawingDebugGraphicsIncludingChildSprites(boolean drawingDebugGraphics) {
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
    public synchronized final void toggleDrawingDebugGraphicsIncludingChildSprites() {
        setDrawingDebugGraphicsIncludingChildSprites(!drawingDebugGraphics);
    }

    /**
     * Determines if this sprite collides with another, assumes sprites are rectangle shaped. Designed to be overridden
     * by subclasses to handle more complex collisions
     */
    public synchronized final SimpleCollisionDirection collides(Sprite otherShape) {
        return SimpleShapeCollisionDetection.detectCollisionOfTwoRectangles(
                // This rect
                this.getXDrawingCoordinateTopLeft(), this.getYDrawingCoordinateTopLeft(), this.getWidth(),
                this.getHeight(),
                // Other shape rect
                otherShape.getXDrawingCoordinateTopLeft(), otherShape.getYDrawingCoordinateTopLeft(),
                otherShape.getWidth(), otherShape.getHeight());
    }

    /**
     * @return The number of child sprites in this parent sprite
     */
    public synchronized final int getChildSpriteCount() {
        if (this.childSprites.size() == 1 && this.childSprites.get(0) instanceof EndRootSprite) {
            return 0;
        } else {
            return this.childSprites.size();
        }
    }


    /**
     * Determines if this sprite is visible within it's parent sprite's boundaries
     */
    public final boolean isVisibleOnParentSprite() {
        return collides(parentSprite) != SimpleCollisionDirection.no_collision;
    }

    private static class EndRootSprite extends Sprite {

        public EndRootSprite(int width, int height) {
            super(width, height);
            // Width and height here should be zero, because this isn't suppose to be a real sprite.
            // Having a width and height of zero will help in not accidentally computing collisions or any other related
            // operations using width and height.
            assert (width == 0);
            assert (height == 0);
        }

        @Override
        int getParentYDrawingCoordinateTopLeftInternalImpl() {
            return 0;
        }

        @Override
        int getParentXDrawingCoordinateTopLeftInternalImpl() {
            return 0;
        }
    }
}
