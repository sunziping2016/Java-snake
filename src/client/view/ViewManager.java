package client.view;

import client.controller.ClientPropertiesLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by sun on 4/28/16.
 *
 * Manage different view with a stack.
 */
public class ViewManager extends JComponent implements KeyListener, WindowListener {
    private static final float ASPECT_RATIO = Float.parseFloat(ClientPropertiesLoader.get().getProperty("ViewManage.AspectRatio"));

    private JFrame frame;
    public ViewManager(JFrame frame) {
        super();
        this.frame = frame;
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F11"), "F11");
        getActionMap().put("F11", new AbstractAction() {
            private GraphicsDevice fullscreenDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            @Override
            public void actionPerformed(ActionEvent e) {
                ViewManager.this.frame.dispose();
                ViewManager.this.frame.setVisible(false);
                if (ViewManager.this.frame.isUndecorated()) {
                    fullscreenDevice.setFullScreenWindow(null);
                    ViewManager.this.frame.setUndecorated(false);
                } else {
                    ViewManager.this.frame.setUndecorated(true);
                    fullscreenDevice.setFullScreenWindow(ViewManager.this.frame);
                }
                ViewManager.this.frame.setVisible(true);
                ViewManager.this.frame.repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.clearRect(0, 0, getWidth(), getHeight());
    }

    public Dimension getPreferredSize() {
        Container parent = this.getParent();
        if (parent == null) return null;
        float parentRatio = (float) parent.getWidth() / parent.getHeight();
        if (parentRatio > ASPECT_RATIO) {
            float ourWidth = ASPECT_RATIO * parent.getHeight();
            return new Dimension((int) Math.round(ourWidth), parent.getHeight());
        }
        else {
            float ourHeight = parent.getWidth() / ASPECT_RATIO;
            return new Dimension(parent.getWidth(), (int) Math.round(ourHeight));
        }
    }
    public Dimension getMaximumSize() { return getPreferredSize(); }
    public Dimension getMinimumSize() { return getPreferredSize(); }

    public void close() {
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
    }

    @Override
    public void keyReleased(KeyEvent e) { }
    @Override
    public void keyTyped(KeyEvent e) { }
    @Override
    public void keyPressed(KeyEvent e) { }

    @Override
    public void windowActivated(WindowEvent e) { }
    @Override
    public void windowDeactivated(WindowEvent e) { }

    @Override
    public void windowClosed(WindowEvent e) { }
    @Override
    public void windowClosing(WindowEvent e) { }
    @Override
    public void windowDeiconified(WindowEvent e) { }
    @Override
    public void windowIconified(WindowEvent e) { }
    @Override
    public void windowOpened(WindowEvent e) { }
}
