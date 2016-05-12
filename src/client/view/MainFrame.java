package client.view;

import javax.swing.*;
import java.awt.*;

/**
 * Created by sun on 4/28/16.
 *
 * Main frame of client.
 */
public class MainFrame extends JFrame {
    private ViewManager viewManager;

    public MainFrame() {
        super("Snake");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        viewManager = new ViewManager(this);

        Container lay = getContentPane();
        lay.setLayout(new BoxLayout(lay, BoxLayout.Y_AXIS));
        lay.setBackground(Colors.get("MainFrame.Background", Colors.FRAME_BACKGROUND));
        lay.add(Box.createVerticalGlue());
        lay.add(viewManager);
        lay.add(Box.createVerticalGlue());
        addKeyListener(viewManager);
        addWindowListener(viewManager);

        setSize(800, 600);
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((int) ((dimension.getWidth() - getWidth()) / 2), (int) ((dimension.getHeight() - getHeight()) / 2));
    }

    public void pushView(View view, Content content) {
        viewManager.pushView(view, content);
    }

    public View popView(Content content) {
        return viewManager.popView(content);
    }
}
