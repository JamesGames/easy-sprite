package org.jamesgames.easysprite.example;

/**
 * Example state enum for {@link ShapeSprite}.
 *
 * @author James Murphy
 */
public enum ShapeSpriteStates {
    recentlyChangedShape {
        @Override
        public boolean bansShapeChangeOnCollision() {
            return true;
        }
    }, isACircle {
        @Override
        public boolean bansShapeChangeOnCollision() {
            return false;
        }
    }, isASquare {
        @Override
        public boolean bansShapeChangeOnCollision() {
            return false;
        }
    };

    public abstract boolean bansShapeChangeOnCollision();
}
