package org.jamesgames.easysprite.animation;

import org.jamesgames.jamesjavautils.time.ElapsedTimeTimerObserver;
import org.jamesgames.jamesjavautils.time.ObservableElapsedTimeTimer;

import java.awt.*;

/**
 * Animation is a class that represents a set of graphics that when rendered sequentially form a some type of moving
 * graphics that could be used for a Sprite or anything else. Animation can be queried to find out what frame should be
 * displayed, and can be updated with how much time has elapsed which will change the active frame to be displayed when
 * enough time has passed to represent the next frame. An Animation is based off a {@link AnimationDescription}, which
 * is useful because it allows multiple Animation instances to use the same AnimationDescription instance in order to
 * shared resources (such as the images of each frame which could use a lot of memory).
 *
 * @author James Murphy
 */

public class Animation {

    private final ObservableElapsedTimeTimer frameTimer;
    private AnimationDescription animationDescription;
    private int currentFrameIndex;


    public Animation(AnimationDescription animationDescription) {
        this.animationDescription = animationDescription;
        currentFrameIndex = 0;
        frameTimer = new ObservableElapsedTimeTimer(
                this.animationDescription.getFrameAtIndex(currentFrameIndex).getLengthOfFrameInMilliseconds() *
                        1_000_000);

        ElapsedTimeTimerObserver changeFrameObserver = timer -> {
            if (currentFrameIndex == this.animationDescription.getNumberOfFrames() - 1) {
                if (this.animationDescription.isLooping()) {
                    // Last frame, loop back
                    changeFrameOnFramePassed(this.animationDescription.getFrameIndexToLoopBackTo());
                }
            } else {
                changeFrameOnFramePassed(++currentFrameIndex);
            }
        };
        frameTimer.addElapsedTimeTimerObserver(changeFrameObserver);
    }

    public void changeAnimationDescription(AnimationDescription animationDescription) {
        this.animationDescription = animationDescription;
        changeFrameOnNewAnimationDescription();
    }

    private void changeFrameOnNewAnimationDescription() {
        changeFrame(0);
        frameTimer.resetElapsedTime();
    }

    private void changeFrameOnFramePassed(int newFrameIndex) {
        changeFrame(newFrameIndex);
    }

    private void changeFrame(int newFrameIndex) {
        currentFrameIndex = newFrameIndex;
        frameTimer.setTargetTimeInMilliseconds(
                animationDescription.getFrameAtIndex(currentFrameIndex).getLengthOfFrameInMilliseconds());
    }

    public void update(long elapsedTimeInMilliseconds) {
        frameTimer.addElapsedTimeInMilliseconds(elapsedTimeInMilliseconds);
    }

    public void drawAnimation(Graphics2D g, int xCoordinate, int yCoordinate) {
        animationDescription.getFrameAtIndex(currentFrameIndex).drawFrame(g, xCoordinate, yCoordinate);
    }

    public AnimationDescription getAnimationDescription() {
        return animationDescription;
    }
}
