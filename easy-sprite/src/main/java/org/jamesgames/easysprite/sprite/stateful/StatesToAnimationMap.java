package org.jamesgames.easysprite.sprite.stateful;

import org.jamesgames.easysprite.animation.AnimationDescription;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * StatesToAnimationMap maps a combination of active states from a {@link ActiveStateSet} to a specific {@link
 * AnimationDescription}. The order of multiple calls to the method addActiveStateComboForAnimation() determines the
 * order that this class will determine what Animation should be chosen for the set of active states (from
 * getAnimationDescription()). If no declaration of state combos are active, then the default animation description is
 * returned by getAnimationDescription().
 *
 * Potential update, design this class to be used in an immutable way.
 *
 * @author James Murphy
 */
public class StatesToAnimationMap<E extends Enum<E>> {
    private final AnimationDescription defaultAnimationDescription;
    private final List<StateCombosToAnimation> stateCombosToAnimation = new ArrayList<>();

    public StatesToAnimationMap(AnimationDescription defaultAnimationDescription) {
        this.defaultAnimationDescription = defaultAnimationDescription;
    }

    /**
     * Add a set of states that should be active for the passed Animation to be selected. The order of multiple calls to
     * this method determines the order that this class will determine what Animation should be chosen for the set of
     * active states.
     */
    public void addActiveStateComboForAnimation(AnimationDescription animationDescriptionForCombo,
            E activeStateNeededForAnimation,
            E... restOfPotentialCombo) {
        stateCombosToAnimation
                .add(new StateCombosToAnimation(animationDescriptionForCombo, activeStateNeededForAnimation,
                        restOfPotentialCombo));
    }

    public AnimationDescription getAnimationDescription(ActiveStateSet<E> activeStates) {
        return stateCombosToAnimation.stream().filter(combo -> combo.isStateComboActivated(activeStates)).findFirst()
                .map(StateCombosToAnimation::getMappedAnimationDescription).orElse(defaultAnimationDescription);
    }

    private class StateCombosToAnimation {
        private final List<E> activeStateCombosForAnimation = new ArrayList<>();
        private final AnimationDescription mappedAnimationDescription;

        public StateCombosToAnimation(AnimationDescription animationDescriptionForCombo,
                E activeStateNeededForAnimation,
                E... restOfPotentialCombo) {
            activeStateCombosForAnimation.add(activeStateNeededForAnimation);
            Collections.addAll(activeStateCombosForAnimation, restOfPotentialCombo);
            mappedAnimationDescription = animationDescriptionForCombo;
        }

        public boolean isStateComboActivated(ActiveStateSet<E> activeStates) {
            return activeStateCombosForAnimation.stream().allMatch(activeStates::isStateActive);
        }

        public AnimationDescription getMappedAnimationDescription() {
            return mappedAnimationDescription;
        }
    }
}
