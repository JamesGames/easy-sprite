package org.jamesgames.easysprite.input;

import sun.plugin.dom.exception.InvalidStateException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * GameInput tracks keystroke input for a specific key, when it is down and up.
 *
 * @author James Murphy
 */
public class GameInput {

    private final String actionName;
    private final String actionDescription;
    private final AtomicInteger keyCode = new AtomicInteger();

    private final AtomicBoolean isKeyCurrentlyDown = new AtomicBoolean();
    private final AtomicBoolean wasKeyDownOnLastCheck = new AtomicBoolean();
    private final AtomicInteger keyPressesSinceLastCheck = new AtomicInteger();

    private JComponent componentToBindInputTo;
    private KeyStroke keyStrokePressed;
    private final Action actionOnKeyStrokePressed;
    private final String pressedActionName;
    private KeyStroke keyStrokeReleased;
    private final Action actionOnKeyStrokeReleased;
    private final String releasedActionName;

    /**
     * Construct a GameInput.
     *
     * @param componentToBindInputTo
     *         A component to bind the input to (with {@link JComponent#WHEN_IN_FOCUSED_WINDOW}).
     * @param keyCode
     *         A keycode for a key is the constant for a key found in java.awt.event.KeyEvent, for example, VK_P.
     * @param actionName
     *         A short name of the action, for example, "jump"
     * @param actionDescription
     *         The description of the action, used in case where a short action name can't fully describe what the key
     *         is used for.
     */
    public GameInput(JComponent componentToBindInputTo, int keyCode, String actionName, String actionDescription) {
        this.componentToBindInputTo = componentToBindInputTo;
        this.actionName = actionName;
        this.actionDescription = actionDescription;
        pressedActionName = actionName + "_Pressed";
        releasedActionName = actionName + "_Released";
        changeKey(keyCode);
        actionOnKeyStrokePressed = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isKeyCurrentlyDown.set(true);
                keyPressesSinceLastCheck.incrementAndGet();
            }
        };
        actionOnKeyStrokeReleased = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isKeyCurrentlyDown.set(false);
            }
        };
    }

    public GameInput(int keyCode, String actionName, String actionDescription) {
        this(new JComponent() {
        }, keyCode, actionName, actionDescription);
    }

    public synchronized void setComponentToBindInputTo(JComponent componentToBindInputTo) {
        removeActionInComponentActionMap();
        this.componentToBindInputTo = componentToBindInputTo;
        putKeystrokeInComponentInputMap();
        putActionInComponentActionMap();
    }

    /**
     * Changes the key that this GameInput listens to
     *
     * @param keyCode
     *         A keycode for a key is the constant for a key found in java.awt.event.KeyEvent, for example, VK_P.
     */
    public synchronized void changeKey(int keyCode) {
        isKeyCurrentlyDown.set(false);
        removeKeystrokeInComponentInputMap();
        this.keyCode.set(keyCode);
        keyStrokePressed = KeyStroke.getKeyStroke(keyCode, 0, false);
        keyStrokeReleased = KeyStroke.getKeyStroke(keyCode, 0, true);
        putKeystrokeInComponentInputMap();
    }

    private void removeKeystrokeInComponentInputMap() {
        Runnable removeKeyStroke = () -> {
            synchronized (this) {
                componentToBindInputTo.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).remove(keyStrokePressed);
                componentToBindInputTo.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).remove(keyStrokeReleased);
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            removeKeyStroke.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(removeKeyStroke);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (InvocationTargetException e) {
                throw new InvalidStateException("Could not remove keystroke in component input map");
            }
        }
    }

    private void removeActionInComponentActionMap() {
        Runnable removeAction = () -> {
            synchronized (this) {
                componentToBindInputTo.getActionMap().remove(pressedActionName);
                componentToBindInputTo.getActionMap().remove(releasedActionName);
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            removeAction.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(removeAction);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (InvocationTargetException e) {
                throw new InvalidStateException("Could not remove action in component action map");
            }
        }
    }

    private void putKeystrokeInComponentInputMap() {
        Runnable addKeystroke = () -> {
            synchronized (this) {
                componentToBindInputTo.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                        .put(keyStrokePressed, pressedActionName);
                componentToBindInputTo.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                        .put(keyStrokeReleased, releasedActionName);
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            addKeystroke.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(addKeystroke);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (InvocationTargetException e) {
                throw new InvalidStateException("Could not put keystroke in component input map");
            }
        }
    }

    private void putActionInComponentActionMap() {
        Runnable addAction = () -> {
            synchronized (this) {
                componentToBindInputTo.getActionMap().put(releasedActionName, actionOnKeyStrokeReleased);
                componentToBindInputTo.getActionMap().put(pressedActionName, actionOnKeyStrokePressed);
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            addAction.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(addAction);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (InvocationTargetException e) {
                throw new InvalidStateException("Could not put action in component action map");
            }
        }
    }


    public String getActionName() {
        return actionName;
    }

    public String getActionDescription() {
        return actionDescription;
    }

    public int getKeyCode() {
        return keyCode.get();
    }

    public boolean isKeyDownForFirstTimeSinceLastCheck() {
        boolean result = !wasKeyDownOnLastCheck.get() && isKeyCurrentlyDown.get();
        wasKeyDownOnLastCheck.set(isKeyCurrentlyDown.get());
        return result;
    }

    public int getKeyPressesSinceLastCheck() {
        int result = keyPressesSinceLastCheck.get();
        keyPressesSinceLastCheck.set(0);
        return result;
    }

    public boolean isKeyCurrentlyDown() {
        keyPressesSinceLastCheck.set(0);
        wasKeyDownOnLastCheck.set(isKeyCurrentlyDown.get());
        return isKeyCurrentlyDown.get();
    }


}
