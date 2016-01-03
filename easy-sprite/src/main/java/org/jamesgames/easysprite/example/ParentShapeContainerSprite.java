package org.jamesgames.easysprite.example;

import org.jamesgames.easysprite.input.GameInput;
import org.jamesgames.easysprite.physics.partitioning.SimpleSpacePartitioner;
import org.jamesgames.easysprite.sprite.Sprite;

import java.awt.*;

/**
 * ShapeSpriteBoard is a Sprite that acts as the parent container for all Sprites in this example. This sprite simply
 * displays a white background, and adds the initial {@link ShapeSprite}s to itself.
 *
 * @author James Murphy
 */
public class ParentShapeContainerSprite extends Sprite {

    public ParentShapeContainerSprite(int x, int y, GameInput left, GameInput right, GameInput up, GameInput down) {
        super(x, y);
        setSpacePartitioner(new SimpleSpacePartitioner());
        addChildShapeSprites();
        addChildPlayerSprite(left, right, up, down);
    }

    private void addChildPlayerSprite(GameInput left, GameInput right, GameInput up, GameInput down) {
        PlayerSprite player = new PlayerSprite(30, 30, left, right, up, down);
        player.setXCoordinateTopLeft(400);
        player.setYCoordinateTopLeft(360);
        addChildSprite(player);
    }

    private void addChildShapeSprites() {
        int margin = 50;
        int shapesToCreate = 4;
        int shapesPerRow = (int) Math.round(Math.ceil(Math.sqrt(shapesToCreate)));
        for (int numberOfShapeSpritesCreated = 0; numberOfShapeSpritesCreated < shapesToCreate;
             numberOfShapeSpritesCreated++) {
            ShapeSprite shapeSprite = new ShapeSprite();
            shapeSprite.setXCoordinateTopLeft((shapeSprite.getWidth() * (numberOfShapeSpritesCreated % shapesPerRow)) +
                    margin);
            shapeSprite.setYCoordinateTopLeft((shapeSprite.getHeight() * (numberOfShapeSpritesCreated / shapesPerRow)) +
                    margin);
            addChildSprite(shapeSprite);
        }
    }

    @Override
    protected synchronized void drawUnderChildren(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.fillRect(getXDrawingCoordinateTopLeft(), getYDrawingCoordinateTopLeft(), getWidth(), getHeight());
    }
}
