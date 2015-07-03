package org.jamesgames.easysprite.sprite.stateful;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * ActiveStateSet is a class that represents what states of a {@link StatefulSprite} are active.
 *
 * @author James Murphy
 */
class ActiveStateSet<E extends Enum<E>> {
    private final Set<E> activeStates = new HashSet<>();
    private boolean anyStatesChangedSinceLastQuery = false;

    public void setStateActive(E state) {
        activeStates.add(state);
        anyStatesChangedSinceLastQuery = true;
    }

    public void setStateInactive(E state) {
        activeStates.remove(state);
        anyStatesChangedSinceLastQuery = true;
    }

    public boolean isStateActive(E state) {
        return activeStates.contains(state);
    }

    public boolean isAnyActiveState(Predicate<E> query) {
        return activeStates.stream().anyMatch(query);
    }

    /**
     * @return True if any state has been set active or inactive since the last time this method was called.
     */
    public boolean hasAnyStatesChangedSinceLastQuery() {
        try {
            return anyStatesChangedSinceLastQuery;
        } finally {
            anyStatesChangedSinceLastQuery = false;
        }
    }
}
