package org.jamesgames.easysprite.animation;

import org.jamesgames.jamesjavautils.graphics.Drawable;

/**
 * DrawableAnimationFrame contains information that can later be used to be transformed into an {@link AnimationFrame}.
 *
 * @author James Murphy
 */
public abstract class DrawableAnimationFrame implements Drawable {

    private final long lengthOfFrameInMilliseconds;
    private final int widthOfFrame;
    private final int heightOfFrame;

    protected DrawableAnimationFrame(long lengthOfFrameInMilliseconds, int widthOfFrame, int heightOfFrame) {
        this.lengthOfFrameInMilliseconds = lengthOfFrameInMilliseconds;
        this.widthOfFrame = widthOfFrame;
        this.heightOfFrame = heightOfFrame;
    }

    public long getLengthOfFrameInMilliseconds() {
        return lengthOfFrameInMilliseconds;
    }

    public int getWidthOfFrame() {
        return widthOfFrame;
    }

    public int getHeightOfFrame() {
        return heightOfFrame;
    }
}
