package client.view;

import javax.swing.*;
import java.awt.*;

/**
 * Created by sun on 4/28/16.
 *
 * Main frame of client.
 */
public class MainFrame extends JFrame {
    public MainFrame() {
        super("Snake");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ViewManager viewManager = new ViewManager(this);

        Container lay = getContentPane();
        lay.setLayout(new BoxLayout(lay, BoxLayout.Y_AXIS));
        lay.setBackground(Colors.get("BackgroundColor", Colors.BACKGROUND));
        lay.add(Box.createVerticalGlue());
        lay.add(viewManager);
        lay.add(Box.createVerticalGlue());
        addKeyListener(viewManager);
        addWindowListener(viewManager);

        setSize(1280, 840);
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((int) ((dimension.getWidth() - getWidth()) / 2), (int) ((dimension.getHeight() - getHeight()) / 2));
    }
}
