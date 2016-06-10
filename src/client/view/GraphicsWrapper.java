package client.view;

import client.controller.ClientPropertiesLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

/**
 * Created by Sun on 5/12/2016.
 *
 * Graphics wrapper.
 */
public class GraphicsWrapper {
    public static final float WIDTH  = Float.parseFloat(ClientPropertiesLoader.get().getProperty("GraphicsWrapper.Width"));
    public static final float HEIGHT = Float.parseFloat(ClientPropertiesLoader.get().getProperty("GraphicsWrapper.Height"));
    private static final int PRECISION_FACTOR = Integer.parseInt(ClientPropertiesLoader.get().getProperty("GraphicsWrapper.PrecisionFactor"));
    private static final String FONT_NAME = ClientPropertiesLoader.get().getProperty("GraphicsWrapper.FontName");

    private Graphics2D g2;
    private LinkedList<Graphics2D> transformStack = new LinkedList<>();
    private int width, height;

    public GraphicsWrapper(Graphics g, int width, int height) {
        this.g2 = (Graphics2D) g.create();
        this.width = width;
        this.height = height;

        //enable antialiasing
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        //configure transforms for coordinate space
        float scale = PRECISION_FACTOR;
        float xScale = width / (WIDTH * scale);
        float yScale = height / (HEIGHT * scale);
        AffineTransform t = g2.getTransform();
        g2.setTransform(new AffineTransform(xScale, 0.0, 0.0, yScale, t.getTranslateX(), t.getTranslateY()));
    }

    private void addTransform() {
        transformStack.push(g2);
        g2 = (Graphics2D) g2.create();
    }

    public void restoreTransform() {
        restoreTransform(1);
    }

    public void restoreTransform(int n) {
        for (int i = 0; i < n && transformStack.size() > 0; i++) {
            g2.dispose();
            g2 = transformStack.pop();
        }
    }

    public void restoreTransformAll() {
        while (transformStack.size() > 0) {
            g2.dispose();
            g2 = transformStack.pop();
        }
    }

    public void setOrigin(float x, float y) {
        addTransform();
        g2.translate(PRECISION_FACTOR * x, PRECISION_FACTOR * y);
    }

    public void drawString(String s, float fontSize, Color c, float x, float y, boolean centered) {
        String[] lines = s.split("\n");
        if (lines.length > 1) {
            for (String line : lines) {
                drawString(line, fontSize, c, x, y, centered);
                y += fontSize * 1.2f;
            }
            return;
        }
        Font f = new Font(FONT_NAME, 0, Math.round(fontSize * PRECISION_FACTOR));
        g2.setFont(f);
        g2.setColor(c);
        float w = centered ? g2.getFontMetrics(f).stringWidth(s) : 0;
        g2.drawString(s, x * PRECISION_FACTOR - w / 2.0f, y*PRECISION_FACTOR);
    }

    public void drawStringCentered(String s, float fontSize, Color c, float x, float y) {
        drawString(s, fontSize, c, x, y, true);
    }
    public void drawString(String s, float fontSize, Color c, float x, float y) {
        drawString(s, fontSize, c, x, y, false);
    }

    public void fillAll(Color c) {
        fillRect(0, 0, WIDTH, HEIGHT, c);
    }

    public void fillRect(float x, float y, float w, float h, Color c) {
        g2.setColor(c);
        g2.fillRect(
                Math.round(x * PRECISION_FACTOR),
                Math.round(y * PRECISION_FACTOR),
                Math.round(w * PRECISION_FACTOR),
                Math.round(h * PRECISION_FACTOR)
        );
    }

    public void fillCircle(float x, float y, float radius, Color c) {
        x -= radius;
        y -= radius;
        g2.setColor(c);
        g2.fillOval(
                Math.round(x * PRECISION_FACTOR),
                Math.round(y * PRECISION_FACTOR),
                Math.round(2 * radius * PRECISION_FACTOR),
                Math.round(2 * radius * PRECISION_FACTOR)
        );
    }

    public void maskRectangle(float leftupx, float leftupy, float rightbuttomx, float rightbuttomy) {
        float scale = PRECISION_FACTOR;
        g2.setClip((int)Math.round(leftupx * scale), (int)Math.round(leftupy * scale), (int)Math.round(rightbuttomx * scale), (int)Math.round(rightbuttomy * scale));
    }

    public void fillArc(float x, float y, float radius, float startAngle, float arcAngle, Paint c) {
        x -= radius;
        y -= radius;
        g2.setPaint(c);
        g2.fillArc(
                Math.round(x * PRECISION_FACTOR),
                Math.round(y * PRECISION_FACTOR),
                Math.round(2 * radius * PRECISION_FACTOR),
                Math.round(2 * radius * PRECISION_FACTOR),
                Math.round(startAngle),
                Math.round(arcAngle)
        );
    }

    public void drawImage(String imgName, float x, float y) {
        float scaleX = ((float) width) / (WIDTH * PRECISION_FACTOR);
        float scaleY = ((float) height) / (HEIGHT * PRECISION_FACTOR);
        Image inst = ImageManager.getImage(imgName);
        g2.drawImage(inst, Math.round(x * PRECISION_FACTOR), Math.round(y * PRECISION_FACTOR),
                Math.round(inst.getWidth(null) * ImageManager.getDefaultScale(imgName)),
                Math.round(inst.getHeight(null) * ImageManager.getDefaultScale(imgName)),
                null);
    }
}
