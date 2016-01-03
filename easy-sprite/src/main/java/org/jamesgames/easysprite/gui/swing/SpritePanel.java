package org.jamesgames.easysprite.gui.swing;

import org.jamesgames.easysprite.input.GameInput;
import org.jamesgames.easysprite.sprite.Sprite;
import org.jamesgames.easysprite.updater.UpdateListener;
import org.jamesgames.jamesjavautils.time.ActionsPerTimeFrameCounter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.text.DecimalFormat;
import java.util.List;

/**
 * SpritePanel is a JPanel that displays a {@link Sprite}.
 *
 * @author James Murphy
 */
public class SpritePanel extends JPanel implements UpdateListener {

    /**
     * The amount in nano seconds of how long the frames per second value will be calculated for every new value
     */
    private static final long timeToCalculateNewFpsValue = 1_000_000_000;

    /**
     * The amount of actions per frame render, which is one, one render will be viewed as one action
     */
    private static final int amountOfActionsPerFrameRender = 1;

    /**
     * This is the Sprite that is being rendered on the panel
     */
    private final Sprite spriteToDisplay;

    /**
     * Used to calculate the frames per second value
     */
    private final ActionsPerTimeFrameCounter framesPerSecondCounter;

    /**
     * Holds the most recent known updates per second value from {@link SpritePanel#newUpdatePerSecondCalculated(float)}
     */
    private float updatesPerSecond = 0;

    /**
     * If true, the panel displays the frames per second value
     *
     * @see #updatesPerSecond
     * @see #framesPerSecondCounter
     */
    private boolean isDisplayingTimeValues = false;

    /**
     * Preferred color to display time values in
     *
     * @see #isDisplayingTimeValues
     */
    private Color timeValueDisplayColor = Color.WHITE;

    /**
     * Formatted used to format output of the time values
     *
     * @see #isDisplayingTimeValues
     */
    private final DecimalFormat timeValueFormatter = new DecimalFormat();

    /**
     * Constructs a SpritePanel
     *
     * @param spriteToDisplay
     *         The sprite that will be displayed by this panel
     * @param resizeSpriteOnPanelResize
     *         If true, then spriteToDisplay's {@link Sprite#resize(int, int)} method will be called when this panel is
     *         resized (from {@link java.awt.event.ComponentAdapter#componentResized(java.awt.event.ComponentEvent)})
     *         with the panel's new width and height values
     */
    public SpritePanel(Sprite spriteToDisplay, boolean resizeSpriteOnPanelResize) {
        this.spriteToDisplay = spriteToDisplay;
        framesPerSecondCounter = new ActionsPerTimeFrameCounter(timeToCalculateNewFpsValue);
        timeValueFormatter.setMaximumFractionDigits(3);

        if (resizeSpriteOnPanelResize) {
            addComponentListener(new ComponentAdapter() {
                public void componentResized(ComponentEvent e) {
                    spriteToDisplay.resize(getWidth(), getHeight());
                }
            });
        }
    }

    public void bindGameInputs(List<GameInput> gameInputsToBind) {
        gameInputsToBind.forEach(input -> input.setComponentToBindInputTo(this));
    }

    public boolean isDrawingSpriteDebugGraphics() {
        return spriteToDisplay.isDrawingDebugGraphics();
    }

    public void setDrawingSpriteDebugGraphics(boolean drawingSpriteDebugGraphics) {
        spriteToDisplay.setDrawingDebugGraphicsIncludingChildSprites(drawingSpriteDebugGraphics);
    }

    public void toggleDrawingSpriteDebugGraphics() {
        spriteToDisplay.toggleDrawingDebugGraphicsIncludingChildSprites();
    }

    public boolean isDisplayingTimeValues() {
        return isDisplayingTimeValues;
    }

    public void setDisplayingTimeValues(boolean displayingTimeValues) {
        isDisplayingTimeValues = displayingTimeValues;
    }

    public void toggleSetDisplayingTimeValues() {
        isDisplayingTimeValues = !isDisplayingTimeValues;
    }

    public Color getTimeValueDisplayColor() {
        return timeValueDisplayColor;
    }

    public void setTimeValueDisplayColor(Color timeValueDisplayColor) {
        this.timeValueDisplayColor = timeValueDisplayColor;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        spriteToDisplay.drawAll((Graphics2D) g);

        if (isDisplayingTimeValues) {
            Graphics tempGraphics = g.create();
            tempGraphics.setColor(timeValueDisplayColor);
            int fontHeight = g.getFontMetrics(g.getFont()).getHeight();
            tempGraphics.drawString("FPS: " + timeValueFormatter.format(
                    framesPerSecondCounter.getActionCountPerTimeFrame()), 0, fontHeight);
            tempGraphics.drawString("UPS: " + timeValueFormatter.format(updatesPerSecond), 0, fontHeight * 2);
            tempGraphics.dispose();
        }

        framesPerSecondCounter.addActions(amountOfActionsPerFrameRender);
    }

    @Override
    public void newUpdatePerSecondCalculated(float updatesPerSecond) {
        this.updatesPerSecond = updatesPerSecond;
    }
}
