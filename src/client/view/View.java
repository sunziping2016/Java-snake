package client.view;

import java.awt.*;

/**
 * Created by sun on 4/28/16.
 *
 * Base classes for different views of the game.
 */
abstract public class View {
    protected static final float WIDTH = GraphicsWrapper.WIDTH;
    protected static final float HEIGHT = GraphicsWrapper.HEIGHT;

    private ViewManager viewManager = null;
    private String name;

    public View(String name) {
        this.name = name;
    }

    abstract public void onStart(Content content);
    abstract public void onPaint(Graphics g);
    abstract public void onStop();
    abstract public void onKey(int keyCode);

    //public boolean isActive() {
        //return getViewManager().isActiveView(this);
    //}


    public int getWidth() {
        return viewManager.getWidth();
    }

    public int getHeight() {
        return viewManager.getHeight();
    }

    public String getName() {
        return name;
    }

    public ViewManager getViewManager() {
        return viewManager;
    }
    public void setViewManager(ViewManager viewManager) {
        this.viewManager = viewManager;
    }

    public void close() {
        viewManager.close();
    }

    public void repaint() {
        viewManager.repaint();
    }
}
