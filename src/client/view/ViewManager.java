package client.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.EmptyStackException;
import java.util.Stack;

/**
 * Created by sun on 4/28/16.
 *
 * Manage different view with a stack.
 */
public class ViewManager extends JComponent implements KeyListener, WindowListener {
    public static final float ASPECT_RATIO = GraphicsWrapper.WIDTH / GraphicsWrapper.HEIGHT;

    private Stack<View> views = new Stack<>();

    private JFrame frame;
    public ViewManager(JFrame frame) {
        super();
        this.frame = frame;
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F11"), "F11");
        getActionMap().put("F11", new AbstractAction() {
            private GraphicsDevice fullscreenDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                frame.setVisible(false);
                if (frame.isUndecorated()) {
                    fullscreenDevice.setFullScreenWindow(null);
                    frame.setAlwaysOnTop(false);
                    frame.setUndecorated(false);
                } else {
                    frame.setUndecorated(true);
                    frame.setAlwaysOnTop(true);
                    fullscreenDevice.setFullScreenWindow(frame);
                }
                frame.setVisible(true);
                frame.repaint();
            }
        });
    }

    public View getActiveView() {
        try {
            return views.peek();
        } catch (EmptyStackException error) {
            return null;
        }
    }

    public synchronized void pushView(View view, Content content) {
        content.putString("method", "push");
        content.putObject("parent", getActiveView());
        View activeView = getActiveView();
        if (activeView != null)
            activeView.onStop();
        view.setViewManager(this);
        view.onStart(content);
        views.push(view);
        repaint();
    }

    public synchronized View popView(Content content) {
        content.putString("method", "pop");
        View popped;
        try {
            popped = views.pop();
        } catch (EmptyStackException error) {
            return null;
        }
        if (popped != null) {
            popped.onStop();
            popped.setViewManager(null);
        }
        View activeView = getActiveView();
        if (activeView != null)
            activeView.onStart(content);
        repaint();
        return popped;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        View activeView = getActiveView();
        if (activeView != null)
            activeView.onPaint(g);
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

    public JFrame getFrame() {
        return frame;
    }

    public void close() {
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
    }

    @Override
    public void keyReleased(KeyEvent e) { }
    @Override
    public void keyTyped(KeyEvent e) { }
    @Override
    public void keyPressed(KeyEvent e) {
        View activeView = getActiveView();
        if (activeView != null)
            activeView.onKey(e.getKeyCode());
    }

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
