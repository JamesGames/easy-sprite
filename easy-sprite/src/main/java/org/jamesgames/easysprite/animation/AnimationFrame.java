package org.jamesgames.easysprite.animation;

import net.jcip.annotations.Immutable;
import org.jamesgames.jamesjavautils.graphics.image.ImageCreator;
import org.jamesgames.jamesjavautils.graphics.image.ImageDescription;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * AnimationFrame represents one frame of an {@link AnimationDescription}. An AnimationFrame consists of an
 * BufferedImage and a time of how long the frame should last for.
 *
 * @author James Murphy
 */
@Immutable
public class AnimationFrame {
    private final BufferedImage frameImage;
    private final long lengthOfFrameInMilliseconds;

    public AnimationFrame(BufferedImage frameImage, long lengthOfFrameInMilliseconds) {
        this.frameImage = frameImage;
        this.lengthOfFrameInMilliseconds = lengthOfFrameInMilliseconds;
    }

    public AnimationFrame(DrawableAnimationFrame drawableFrame) {
        this(new ImageCreator().createImage(
                new ImageDescription(drawableFrame, drawableFrame.getWidthOfFrame(), drawableFrame.getHeightOfFrame(),
                        Transparency.TRANSLUCENT)), drawableFrame.getLengthOfFrameInMilliseconds());
    }

    public void drawFrame(Graphics2D g, int xCoordinate, int yCoordinate) {
        g.drawImage(frameImage, xCoordinate, yCoordinate, null);
    }

    public long getLengthOfFrameInMilliseconds() {
        return lengthOfFrameInMilliseconds;
    }

}
