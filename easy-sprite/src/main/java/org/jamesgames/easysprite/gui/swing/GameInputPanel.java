package org.jamesgames.easysprite.gui.swing;

import org.jamesgames.easysprite.input.GameInput;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;

/**
 * GameInputPanel is a {@link JPanel} that displays a list of {@link GameInput} objects, with an interface to change the
 * key bound to a specific GameInput.
 * <p>
 * Potential update is to remove the text of bound keys when a key is used on another bind (previous action to the key
 * currently loses the bind but it's not depicted within the GUI).
 *
 * @author James Murphy
 */
public class GameInputPanel extends JPanel {
    private final Map<GameInput, ChangeGameInputBindingPanel> gameInputBindingPanels = new HashMap<>();
    private final JPanel bindingPanels = new JPanel(new GridLayout(0, 1));
    private final JScrollPane scrollPane = new JScrollPane(bindingPanels);

    public GameInputPanel(List<GameInput> gameInputs) {
        addGameInputs(gameInputs);
        scrollPane.setBorder(new TitledBorder("Modify Key Bindings"));
        bindingPanels.setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        //add(scrollPane); // For now, don't use the scroll pane as the scrollbars were eating arrow key presses
        add(bindingPanels);
    }

    public void addGameInputs(List<GameInput> gameInputs) {
        int oldVerticalScrollBarPosition = scrollPane.getVerticalScrollBar().getValue();
        List<GameInput> gameInputsListCopy = new ArrayList<>(gameInputs);
        // Find whatever is not added yet to the gui
        gameInputsListCopy.removeAll(gameInputBindingPanels.keySet());
        // Store how GameInput matches to the corresponding gui key set
        gameInputsListCopy.forEach(input -> gameInputBindingPanels.put(input, new ChangeGameInputBindingPanel(input)));
        // Add any new items
        List<Component> displayedComponents = Arrays.asList(bindingPanels.getComponents());
        gameInputsListCopy.stream().
                map(gameInputBindingPanels::get).
                filter(item -> !displayedComponents.contains(item)).
                forEach(bindingPanels::add);

        validate();
        scrollPane.getVerticalScrollBar().setValue(oldVerticalScrollBarPosition);
        repaint();
    }

    public void removeGameInputs(List<GameInput> gameInputs) {
        Set<GameInput> gameInputsAsSet = new HashSet<>(gameInputs);
        gameInputBindingPanels.keySet().stream().filter(gameInputsAsSet::contains)
                .forEach(key -> bindingPanels.remove(gameInputBindingPanels.get(key)));
        gameInputs.forEach(gameInputBindingPanels::remove);
    }

    private static class ChangeGameInputBindingPanel extends JPanel {
        private final GameInput gameInput;
        private final JLabel name;
        private final JTextField binding;

        private ChangeGameInputBindingPanel(GameInput gameInput) {
            this.gameInput = gameInput;
            setOpaque(false);
            name = new JLabel();
            name.setText(gameInput.getActionName());
            name.setToolTipText(gameInput.getActionDescription());
            binding = new JTextField();
            binding.setOpaque(false);
            binding.setText(KeyEvent.getKeyText(gameInput.getKeyCode()));
            binding.addKeyListener(new KeyAdapter() {
                // Don't want the game to respond to any possible key presses/releases related to changing the keys
                // So for each action, the key event is consumed.
                @Override
                public void keyReleased(KeyEvent e) {
                    e.consume();
                    ChangeGameInputBindingPanel.this.gameInput.changeKey(e.getKeyCode());
                    binding.setText(KeyEvent.getKeyText(ChangeGameInputBindingPanel.this.gameInput.getKeyCode()));
                    name.requestFocus(); // Remove focus from the text field
                }

                @Override
                public void keyPressed(KeyEvent e) {
                    e.consume();
                }

                @Override
                public void keyTyped(KeyEvent e) {
                    e.consume();
                }

            });

            setLayout(new GridLayout(1, 2));
            add(name);
            add(binding);
        }
    }
}
