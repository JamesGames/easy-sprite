package org.jamesgames.easysprite.example;

import org.jamesgames.easysprite.animation.AnimationDescription;
import org.jamesgames.easysprite.animation.DrawableAnimationFrame;
import org.jamesgames.easysprite.physics.partitioning.SimpleSpacePartitioner;
import org.jamesgames.easysprite.physics.simple.SimpleCollisionDirection;
import org.jamesgames.easysprite.physics.simple.SimpleShapeCollisionDetection;
import org.jamesgames.easysprite.sprite.Sprite;
import org.jamesgames.easysprite.sprite.stateful.StatefulSprite;
import org.jamesgames.easysprite.sprite.stateful.StatesToAnimationMap;
import org.jamesgames.jamesjavautils.time.ObservableElapsedTimeTimer;

import java.awt.*;
import java.util.Random;

/**
 * An example {@link StatefulSprite}. This sprite has a few animations based on different shapes, and changes shapes
 * when it collides with other sprites (if some conditions within the state of the sprite allows for it).
 *
 * @author James Murphy
 */
public class ShapeSprite extends StatefulSprite<ShapeSpriteStates> {
    private static final Random randomGen = new Random();
    private static final int width = 330;
    private static final int height = 330;
    private static final long timeToWaitBeforeChangingShape = 1_000_000L * 3000L;

    private final ObservableElapsedTimeTimer timerToWaitBeforeChangingShape =
            new ObservableElapsedTimeTimer(timeToWaitBeforeChangingShape);

    /**
     * This default animation shouldn't appear according to the logic of our ShapeSprite (isCircle or isSquare state is
     * always active)
     */
    private static final AnimationDescription defaultAnimationDescription =
            new AnimationDescription.AnimationDescriptionBuilder().setDrawableFramesToUse(
                    new DrawableAnimationFrame(1000, width, height) {
                        @Override
                        public void draw(Graphics2D g) {
                            g.setColor(Color.RED);
                            g.drawRect(0, 0, width - 1, height - 1);
                            g.setColor(Color.BLACK);
                            g.drawString("default animation", 2, 10);
                        }
                    }).setLooping(false).createAnimation();
    private static final StatesToAnimationMap<ShapeSpriteStates> stateToAnimationMap =
            new StatesToAnimationMap<>(defaultAnimationDescription);

    private static final AnimationDescription circleAnimationDescription =
            new AnimationDescription.AnimationDescriptionBuilder().setDrawableFramesToUse(
                    new DrawableAnimationFrame(timeToWaitBeforeChangingShape / 1_000_000, width, height) {
                        @Override
                        public void draw(Graphics2D g) {
                            g.setRenderingHint(
                                    RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                            g.setColor(Color.CYAN);
                            g.fillOval(0, 0, width - 1, height - 1);
                            g.setColor(Color.DARK_GRAY);
                            g.drawOval(0, 0, width - 1, height - 1);
                            g.drawRect(0, 0, width - 1, height - 1);
                        }
                    },
                    new DrawableAnimationFrame(250, width, height) {
                        @Override
                        public void draw(Graphics2D g) {
                            g.setRenderingHint(
                                    RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                            g.setColor(Color.ORANGE);
                            g.fillOval(0, 0, width - 1, height - 1);
                            g.setColor(Color.DARK_GRAY);
                            g.drawOval(0, 0, width - 1, height - 1);
                            g.drawRect(0, 0, width - 1, height - 1);
                        }
                    },
                    new DrawableAnimationFrame(250, width, height) {
                        @Override
                        public void draw(Graphics2D g) {
                            g.setRenderingHint(
                                    RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                            g.setColor(Color.YELLOW);
                            g.fillOval(0, 0, width - 1, height - 1);
                            g.setColor(Color.DARK_GRAY);
                            g.drawOval(0, 0, width - 1, height - 1);
                            g.drawRect(0, 0, width - 1, height - 1);
                        }
                    }
            ).setLooping(true).setFrameIndexToLoopBackTo(1).createAnimation();

    private static final AnimationDescription squareAnimationDescription =
            new AnimationDescription.AnimationDescriptionBuilder().setDrawableFramesToUse(
                    new DrawableAnimationFrame(timeToWaitBeforeChangingShape / 1_000_000, width, height) {
                        @Override
                        public void draw(Graphics2D g) {
                            g.setRenderingHint(
                                    RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                            g.setColor(Color.CYAN);
                            g.fillRect(0, 0, width - 1, height - 1);
                            g.setColor(Color.DARK_GRAY);
                            g.drawRect(0, 0, width - 1, height - 1);
                        }
                    },
                    new DrawableAnimationFrame(250, width, height) {
                        @Override
                        public void draw(Graphics2D g) {
                            g.setRenderingHint(
                                    RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                            g.setColor(Color.ORANGE);
                            g.fillRect(0, 0, width - 1, height - 1);
                            g.setColor(Color.DARK_GRAY);
                            g.drawRect(0, 0, width - 1, height - 1);
                        }
                    },
                    new DrawableAnimationFrame(250, width, height) {
                        @Override
                        public void draw(Graphics2D g) {
                            g.setRenderingHint(
                                    RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                            g.setColor(Color.YELLOW);
                            g.fillRect(0, 0, width - 1, height - 1);
                            g.setColor(Color.DARK_GRAY);
                            g.drawRect(0, 0, width - 1, height - 1);
                        }
                    }
            ).setLooping(true).setFrameIndexToLoopBackTo(1).createAnimation();

    static {
        stateToAnimationMap.addActiveStateComboForAnimation(circleAnimationDescription, ShapeSpriteStates.isACircle);
        stateToAnimationMap.addActiveStateComboForAnimation(squareAnimationDescription, ShapeSpriteStates.isASquare);
    }

    public ShapeSprite() {
        super(stateToAnimationMap);
        setWidth(width);
        setHeight(height);

        float maxVelocity = .15f;
        setVelocitiesToRandomAmount(maxVelocity, .50f);
        if (randomGen.nextBoolean()) {
            setStateActive(ShapeSpriteStates.isACircle);
        } else {
            setStateActive((ShapeSpriteStates.isASquare));
        }

        timerToWaitBeforeChangingShape.addElapsedTimeTimerObserver(
                timer -> setStateInactive(ShapeSpriteStates.recentlyChangedShape));

        setSpacePartitioner(new SimpleSpacePartitioner());

        // Add some SimpleSquareSprites to this ShapeSprite
        int squareWidth = width / 3;
        int squareHeight = height / 3;
        Color squareColor = Color.GREEN;
        NestableSquareSprite spriteToAdd = new NestableSquareSprite(squareWidth, squareHeight, squareColor, 1);
        spriteToAdd.setXCoordinateTopLeft(10);
        spriteToAdd.setYCoordinateTopLeft(10);
        addChildSprite(spriteToAdd);
        spriteToAdd = new NestableSquareSprite(width / 3, height / 3, squareColor, 1);
        spriteToAdd.setXCoordinateTopLeft(width - squareWidth - 10);
        spriteToAdd.setYCoordinateTopLeft(10);
        addChildSprite(spriteToAdd);
        spriteToAdd = new NestableSquareSprite(width / 3, height / 3, squareColor, 1);
        spriteToAdd.setXCoordinateTopLeft(10);
        spriteToAdd.setYCoordinateTopLeft(height - squareHeight - 10);
        addChildSprite(spriteToAdd);
        spriteToAdd = new NestableSquareSprite(width / 3, height / 3, squareColor, 1);
        spriteToAdd.setXCoordinateTopLeft(width - squareWidth - 10);
        spriteToAdd.setYCoordinateTopLeft(height - squareHeight - 10);
        addChildSprite(spriteToAdd);

    }

    @Override
    protected synchronized void handleCollision(Sprite collidingSprite, SimpleCollisionDirection direction) {
        SimpleShapeCollisionDetection.moveSpritesOffOfCollidingSprite(this, collidingSprite, direction);
        SimpleShapeCollisionDetection.changeVelocitiesWithMomentumTransferIfNeededOnCollision(this, collidingSprite,
                direction);

        changeShapeOnCollisionIfPossible();
    }

    private void changeShapeOnCollisionIfPossible() {
        if (!isAnyActiveState(ShapeSpriteStates::bansShapeChangeOnCollision)) {
            changeShape();
            setStateActive(ShapeSpriteStates.recentlyChangedShape);
            timerToWaitBeforeChangingShape.resetElapsedTime();
        }
    }

    private void changeShape() {
        if (isStateActive(ShapeSpriteStates.isACircle)) {
            setStateInactive(ShapeSpriteStates.isACircle);
            setStateActive(ShapeSpriteStates.isASquare);
        } else if (isStateActive(ShapeSpriteStates.isASquare)) {
            setStateInactive(ShapeSpriteStates.isASquare);
            setStateActive(ShapeSpriteStates.isACircle);
        }
    }

    @Override
    protected synchronized void updateAfterChildren(long elapsedTimeInMilliseconds) {
        repositionAndReverseVelocitiesIfVeeringOffParent();
        timerToWaitBeforeChangingShape.addElapsedTimeInMilliseconds(elapsedTimeInMilliseconds);
    }
}



