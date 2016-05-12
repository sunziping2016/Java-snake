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

    public GraphicsWrapper(Graphics g, int width, int height) {
        this.g2 = (Graphics2D) g.create();

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

    /*public void maskCircle(float x, float y, float radius) {
        addTransform();
        float scale = PRECISION_FACTOR;
        x -= radius;
        y -= radius;
        Area a = new Area(g2.getClip());
        a.subtract(new Area(new Ellipse2D.Float(x * scale, y * scale, scale * radius * 2, scale * radius * 2)));
        g2.setClip(a);
    }*/
    /*public void drawImage(String imgName, float x, float y) {
        drawImage(imgName, x, y, true);
    }*/
    /*//if useInstanceCache is false, do the rotate transform here, not at the instance cache level
    //this is desirable if the image will be rotated often (i.e. on a body) to avoid running out of memory by filling up the instance cache
    public void drawImage(String imgName, float x, float y, boolean useInstanceCache) {

        float scale = PRECISION_FACTOR;
        float scaleX = ((float)canvas.getWidth()) / (16.0f * scale);
        float scaleY = ((float)canvas.getHeight()) / (10.0f * scale);
        float rotation = transformStack.peek().cumRotation;

        float cumTransX = (float)(g2.getTransform().getTranslateX() - originalGraphics.getTransform().getTranslateX());
        float cumTransY = (float)(g2.getTransform().getTranslateY() - originalGraphics.getTransform().getTranslateY());
        float realX = (x / 16f) * canvas.getWidth() + cumTransX;
        float realY = (y / 10f) * canvas.getHeight() + cumTransY;

        if (!useInstanceCache) {

            BufferedImage image = ImageManager.getImage(imgName);
            float defaultScale = ImageManager.getDefaultScale(imgName);

            AffineTransform tform = new AffineTransform();
            tform.concatenate(AffineTransform.getTranslateInstance(realX, realY));
            tform.concatenate(AffineTransform.getTranslateInstance(-image.getWidth()*scaleX*0.5f, -image.getHeight()*scaleY*0.5f));
            tform.concatenate(AffineTransform.getScaleInstance(scaleX*defaultScale, scaleY*defaultScale));
            if (rotation != 0)
                tform.concatenate(AffineTransform.getRotateInstance(rotation / 180f * Math.PI, image.getWidth()*0.5f, image.getHeight()*0.5f));

            originalGraphics.drawImage(image, tform, null);

        } else {

            Image inst = ImageManager.getImageInstance(imgName, scaleX, scaleY, 0);
            AffineTransform tf = new AffineTransform();
            tf.concatenate(AffineTransform.getTranslateInstance(realX - inst.getWidth(null)/2f/ImageManager.REAL_DENSITY, realY - inst.getHeight(null)/2f/ImageManager.REAL_DENSITY));
            if (ImageManager.REAL_DENSITY != 1.0f)
                tf.concatenate(AffineTransform.getScaleInstance(1/ImageManager.REAL_DENSITY, 1/ImageManager.REAL_DENSITY));
            originalGraphics.drawImage(inst, tf, null);
        }
    }*/
    /*
    public void fillBall(float x, float y, float radius, Color c) {
        final float RATE = 0.8f;
        float scale = PRECISION_FACTOR;
        float[] dist = {0.0f, 1.0f};
        float sqrtr = (float) (radius / Math.sqrt(2));
        Color[] colors = {Colors.mixtue(Color.BLACK, c, RATE), Colors.mixtue(Color.WHITE, c, RATE)};
        g2.setPaint(new LinearGradientPaint((x - sqrtr) * scale, (y + sqrtr) * scale,
                (x + sqrtr) * scale, (y - sqrtr) * scale, dist, colors));
        x -= radius;
        y -= radius;
        g2.fillOval(
                (int)Math.round(x * scale),
                (int)Math.round(y * scale),
                (int)Math.round(2 * radius * scale),
                (int)Math.round(2 * radius * scale)
        );
    }

    public void fillBoundary(float leftupx, float leftupy, float rightbuttomx, float rightbuttomy, float width, Color c) {
        float scale = PRECISION_FACTOR;
        final float RATE = 0.6f;
        int[] xPoints, yPoints;
        float[] dist = {0.0f, 1.0f};
        Color[] colors = {Colors.mixtue(Color.BLACK, c, RATE), Colors.mixtue(Color.WHITE, c, RATE)};
        xPoints = new int[] {
                (int)Math.round((leftupx - width) * scale),
                (int)Math.round((leftupx - width) * scale),
                (int)Math.round(leftupx * scale),
                (int)Math.round(leftupx * scale),
        };
        yPoints = new int[] {
                (int)Math.round((leftupy - width) * scale),
                (int)Math.round((rightbuttomy + width) * scale),
                (int)Math.round(rightbuttomy * scale),
                (int)Math.round(leftupy * scale),
        };
        g2.setPaint(new LinearGradientPaint((leftupx - width) * scale, 0.0f, leftupx * scale, 0.0f, dist, colors));
        g2.fillPolygon(xPoints, yPoints, 4);
        xPoints = new int[] {
                (int)Math.round(leftupx * scale),
                (int)Math.round((leftupx - width) * scale),
                (int)Math.round((rightbuttomx + width) * scale),
                (int)Math.round(rightbuttomx * scale),
        };
        yPoints = new int[] {
                (int)Math.round(rightbuttomy * scale),
                (int)Math.round((rightbuttomy + width) * scale),
                (int)Math.round((rightbuttomy + width) * scale),
                (int)Math.round(rightbuttomy * scale),
        };
        g2.setPaint(new LinearGradientPaint(0.0f, (rightbuttomy + width) * scale, 0.0f, rightbuttomy * scale, dist, colors));
        g2.fillPolygon(xPoints, yPoints, 4);
        xPoints = new int[] {
                (int)Math.round(rightbuttomx * scale),
                (int)Math.round(rightbuttomx * scale),
                (int)Math.round((rightbuttomx + width) * scale),
                (int)Math.round((rightbuttomx + width) * scale),
        };
        yPoints = new int[] {
                (int)Math.round(leftupy * scale),
                (int)Math.round(rightbuttomy * scale),
                (int)Math.round((rightbuttomy + width) * scale),
                (int)Math.round((leftupy - width) * scale),
        };
        g2.setPaint(new LinearGradientPaint((rightbuttomx + width) * scale, 0.0f, rightbuttomx * scale, 0.0f, dist, colors));
        g2.fillPolygon(xPoints, yPoints, 4);
        xPoints = new int[] {
                (int)Math.round((leftupx - width) * scale),
                (int)Math.round(leftupx * scale),
                (int)Math.round(rightbuttomx * scale),
                (int)Math.round((rightbuttomx + width) * scale),
        };
        yPoints = new int[] {
                (int)Math.round((leftupy - width) * scale),
                (int)Math.round(leftupy * scale),
                (int)Math.round(leftupy * scale),
                (int)Math.round((leftupy - width) * scale),
        };
        g2.setPaint(new LinearGradientPaint(0.0f, (leftupy - width) * scale, 0.0f, leftupy * scale, dist, colors));
        g2.fillPolygon(xPoints, yPoints, 4);
    } */
}
