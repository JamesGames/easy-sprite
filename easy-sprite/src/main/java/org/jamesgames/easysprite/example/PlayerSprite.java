package org.jamesgames.easysprite.example;

import org.jamesgames.easysprite.input.GameInput;
import org.jamesgames.easysprite.sprite.Sprite;

import java.awt.*;

/**
 * PlayerSprite is a {@link Sprite} that can be controlled by the user of the application. It acts as an obstacle to
 * other Sprites, causing them to bounce off of the PlayerSprite.
 *
 * @author James Murphy
 */
public class PlayerSprite extends Sprite {
    private static final float velocityUnitsPerMillisecond = .15f;

    private final GameInput left;
    private final GameInput right;
    private final GameInput up;
    private final GameInput down;


    public PlayerSprite(int width, int height, GameInput left, GameInput right,
            GameInput up, GameInput down) {
        super(width, height);
        this.left = left;
        this.right = right;
        this.up = up;
        this.down = down;
    }


    @Override
    protected synchronized void drawUnderChildren(Graphics2D g) {
        super.drawUnderChildren(g);
        g.setColor(Color.ORANGE);
        g.fillRect(getXDrawingCoordinateTopLeft(),
                getYDrawingCoordinateTopLeft(),
                getWidth() - 1, getHeight() - 1);
        g.setColor(Color.DARK_GRAY);
        g.drawRect(getXDrawingCoordinateTopLeft(),
                getYDrawingCoordinateTopLeft(),
                getWidth() - 1, getHeight() - 1);
        g.drawLine(getXDrawingCoordinateTopLeft(),
                getYDrawingCoordinateTopLeft() + getHeight() - 1,
                getXDrawingCoordinateTopLeft() + getWidth() - 1,
                getYDrawingCoordinateTopLeft());
        g.drawLine(getXDrawingCoordinateTopLeft(),
                getYDrawingCoordinateTopLeft(),
                getXDrawingCoordinateTopLeft() + getWidth() - 1,
                getYDrawingCoordinateTopLeft() + getHeight() - 1);
    }

    @Override
    protected synchronized void updateAfterChildren(long elapsedTimeInMilliseconds) {
        setXVelocity(0);
        setYVelocity(0);
        if (left.isKeyCurrentlyDown()) {
            setXVelocity(getXVelocity() + -velocityUnitsPerMillisecond);
        }
        if (right.isKeyCurrentlyDown()) {
            setXVelocity(getXVelocity() + velocityUnitsPerMillisecond);
        }
        if (up.isKeyCurrentlyDown()) {
            setYVelocity(getYVelocity() + -velocityUnitsPerMillisecond);
        }
        if (down.isKeyCurrentlyDown()) {
            setYVelocity(getYVelocity() + velocityUnitsPerMillisecond);
        }
    }
}
