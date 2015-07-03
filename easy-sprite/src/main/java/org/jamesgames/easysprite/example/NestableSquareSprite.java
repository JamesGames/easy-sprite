package org.jamesgames.easysprite.example;

import org.jamesgames.easysprite.physics.partitioning.SimpleSpacePartitioner;
import org.jamesgames.easysprite.physics.simple.SimpleCollisionDirection;
import org.jamesgames.easysprite.physics.simple.SimpleShapeCollisionDetection;
import org.jamesgames.easysprite.sprite.Sprite;

import java.awt.*;

/**
 * SimpleSquareSprite is another example {@link Sprite} that simply bounces off the edge of their parent sprite.
 * SimpleSquareSprite will nest additional SimpleSquareSprites within itself recursively until they reach a certain
 * size.
 *
 * @author James Murphy
 */
public class NestableSquareSprite extends Sprite {

    private final Color backgroundColor;

    public NestableSquareSprite(int width, int height, Color color, int nestLevel) {
        super(width, height);
        backgroundColor = color;

        float maxVelocity = .08f / nestLevel;
        setVelocitiesToRandomAmount(maxVelocity, .50f);

        setSpacePartitioner(new SimpleSpacePartitioner());

        // Add some SimpleSquareSprites internally to this ShapeSprite
        int squareWidth = width / 3;
        int squareHeight = height / 3;
        // Recursively keep adding SimpleSquares until the squares are too small
        if (squareWidth > 1 && squareHeight > 1) {
            // Cycle through Green->Pink->Cyan for the nested colors.
            Color squareColor = Color.GREEN;
            if (backgroundColor == Color.GREEN) {
                squareColor = Color.PINK;
            } else if (backgroundColor == Color.PINK) {
                squareColor = Color.CYAN;
            } else if (backgroundColor == Color.CYAN) {
                squareColor = Color.GREEN;
            }

            NestableSquareSprite spriteToAdd =
                    new NestableSquareSprite(squareWidth, squareHeight, squareColor, nestLevel * 2);
            spriteToAdd.setXCoordinateTopLeft(10);
            spriteToAdd.setYCoordinateTopLeft(10);
            addChildSprite(spriteToAdd);
            spriteToAdd = new NestableSquareSprite(width / 3, height / 3, squareColor, nestLevel * 2);
            spriteToAdd.setXCoordinateTopLeft(width - squareWidth - 10);
            spriteToAdd.setYCoordinateTopLeft(10);
            addChildSprite(spriteToAdd);
            spriteToAdd = new NestableSquareSprite(width / 3, height / 3, squareColor, nestLevel * 2);
            spriteToAdd.setXCoordinateTopLeft(10);
            spriteToAdd.setYCoordinateTopLeft(height - squareHeight - 10);
            addChildSprite(spriteToAdd);
            spriteToAdd = new NestableSquareSprite(width / 3, height / 3, squareColor, nestLevel * 2);
            spriteToAdd.setXCoordinateTopLeft(width - squareWidth - 10);
            spriteToAdd.setYCoordinateTopLeft(height - squareHeight - 10);
            addChildSprite(spriteToAdd);
        }
    }

    @Override
    protected synchronized void updateAfterChildren(long elapsedTimeInMilliseconds) {
        repositionAndReverseVelocitiesIfVeeringOffParent();
    }


    @Override
    protected synchronized void drawUnderChildren(Graphics2D g) {
        super.drawUnderChildren(g);
        g.setColor(backgroundColor);
        g.fillRect(getXDrawingCoordinateTopLeft(),
                getYDrawingCoordinateTopLeft(),
                getWidth() - 1, getHeight() - 1);
        g.setColor(Color.DARK_GRAY);
        g.drawRect(getXDrawingCoordinateTopLeft(),
                getYDrawingCoordinateTopLeft(),
                getWidth() - 1, getHeight() - 1);
    }

    @Override
    protected synchronized void handleCollision(Sprite collidingSprite, SimpleCollisionDirection direction) {
        SimpleShapeCollisionDetection.moveSpritesOffOfCollidingSprite(this, collidingSprite, direction);
        SimpleShapeCollisionDetection.changeVelocitiesIfNeededOnCollision(this, collidingSprite, direction);
    }
}
