package org.jamesgames.easysprite.animation;

import net.jcip.annotations.Immutable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * AnimationDescription is a class that represents a description of a set of graphics that could be rendered
 * sequentially. An AnimationDescription is used to create an {@link Animation} instance which will query the
 * AnimationDescription for the stored frame. The idea is that you would create one AnimationDescription per type of
 * Animation to share frame data that does not change, but create many of those Animation instances. For example, you
 * may have three frames that represent a Sprite jumping and ten Sprites that are jumping. One would store the frame
 * data in only one instance of an AnimationDescription, and create one Animation instance per Sprite instance, and then
 * all ten of those Animation/Sprite pairs are then allowed to be at different time segments of the jumping animation
 * depending on when they started to jump as that data of how far along an animation is is stored in the Animation
 * object.
 *
 * @author James Murphy
 */
@Immutable
public final class AnimationDescription {
    private final List<AnimationFrame> frames = new ArrayList<>();

    private final int frameIndexToLoopBackTo;
    private final boolean looping;


    private AnimationDescription(List<AnimationFrame> framesToUse, int frameIndexToLoopBackTo, boolean looping) {
        Objects.requireNonNull(framesToUse, "framesToUse list cannot be null");
        if (framesToUse.isEmpty()) {
            throw new IllegalArgumentException("framesToUse list cannot be empty");
        }
        frames.addAll(framesToUse);
        this.frameIndexToLoopBackTo = frameIndexToLoopBackTo;
        this.looping = looping;
    }

    public int getFrameIndexToLoopBackTo() {
        return frameIndexToLoopBackTo;
    }

    public boolean isLooping() {
        return looping;
    }

    public int getNumberOfFrames() {
        return frames.size();
    }

    public AnimationFrame getFrameAtIndex(int index) {
        return frames.get(index);
    }


    /**
     * AnimationDescriptionBuilder builds {@link AnimationDescription}s.
     */
    public static class AnimationDescriptionBuilder {
        private List<AnimationFrame> framesToUse;
        private int frameIndexToLoopBackTo = 0;
        private boolean looping = true;

        public AnimationDescriptionBuilder setAnimationFramesToUse(List<AnimationFrame> framesToUse) {
            this.framesToUse = framesToUse;
            return this;
        }

        public AnimationDescriptionBuilder setDrawableFramesToUse(List<DrawableAnimationFrame> drawableFramesToUse) {
            framesToUse = new ArrayList<>();
            drawableFramesToUse.forEach(drawableFrame -> framesToUse.add(new AnimationFrame(drawableFrame)));
            return this;
        }

        public AnimationDescriptionBuilder setDrawableFramesToUse(DrawableAnimationFrame firstFrame,
                DrawableAnimationFrame... restOfFrames) {
            return setDrawableFramesToUse(((Supplier<List<DrawableAnimationFrame>>) () -> {
                List<DrawableAnimationFrame> framesToUse = new ArrayList<>();
                framesToUse.add(firstFrame);
                framesToUse.addAll(Arrays.asList(restOfFrames));
                return framesToUse;
            }).get());
        }

        public AnimationDescriptionBuilder setFrameIndexToLoopBackTo(int frameIndexToLoopBackTo) {
            this.frameIndexToLoopBackTo = frameIndexToLoopBackTo;
            return this;
        }

        public AnimationDescriptionBuilder setLooping(boolean looping) {
            this.looping = looping;
            return this;
        }

        public AnimationDescription createAnimation() {
            return new AnimationDescription(framesToUse, frameIndexToLoopBackTo, looping);
        }
    }

}
