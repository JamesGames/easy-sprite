package org.jamesgames.easysprite.example;

import org.jamesgames.easysprite.gui.swing.SpritePanel;
import org.jamesgames.easysprite.sprite.Sprite;
import org.jamesgames.easysprite.updater.SpriteUpdater;
import org.jamesgames.jamesjavautils.gui.swing.JFrameSizedAfterInsets;

import javax.swing.*;
import java.awt.*;

/**
 * Small example program showing off features of EasySprite. This example application shows off some of the graphical
 * features one can easily create using EasySprite. In this example, there's a set of nested sprites, some that have
 * Animations {@link ParentShapeContainerSprite}, some that are nested recursively until a certain size with static
 * graphics {@link NestableSquareSprite}, and one parent most Sprite that acts as the container of all other Sprites
 * (the simple white background, which is the {@link ParentShapeContainerSprite}.
 * <p>
 * Code example shows how these three custom Sprite classes work together. Within the {@link ShapeSprite}, one can find
 * some static inner classes which define some of the animation frames and other classes required to build a state based
 * animation Sprite. Also within {@link ShapeSprite}, which is the most complex Sprite shown in this example, you can
 * find examples of using timers that help update the internal set of active and inactive states, as well as how
 * collisions are handled and how some collisions affect the internal set of states too. This internal set of states is
 * actually what determines the graphics of the {@link ShapeSprite}, where there's a lookup table of what Animation to
 * display based on the set of active states.
 *
 * @author James Murphy
 */
public class MainShapeExample {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            int width = 900;
            int height = 900;
            JFrame frame = new JFrameSizedAfterInsets(width, height, true);
            frame.setTitle("Example graphical application using Easy Sprite library " +
                    "(github.com/jamesgames/easy-sprite)");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            Sprite board = new ParentShapeContainerSprite(width, height);
            SpritePanel boardPanel = new SpritePanel(board, true);
            boardPanel.setDisplayingTimeValues(true);
            boardPanel.setDrawingSpriteDebugGraphics(true);
            boardPanel.setTimeValueDisplayColor(Color.BLACK);

            SpriteUpdater boardUpdater = new SpriteUpdater(board, 16);
            boardUpdater.addAdditionalActionPerUpdate(elapsedTimeInMilliseconds -> boardPanel.repaint());
            boardUpdater.addUpdateListener(boardPanel);

            frame.add(boardPanel);
            frame.setVisible(true);
        });
    }
}
