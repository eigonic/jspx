package eg.java.net.web.jspx.engine.util.imaging;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * <a href="http://skewpassim.sourceforge.net/">http://skewpassim.sourceforge.net/</a>
 * <br>
 * <b>This is a sample implementation of the ISkewImage class
 * in order to skew the secured chars passed encoded as a parameter</b>
 */
public class SkewImageProba implements ISkewImage {

    private static final int MAX_LETTER_COUNT = 8;
    private static final int LETTER_WIDTH = 22;
    private static final int IMAGE_HEIGHT = 45;
    private static final double SKEW = 0.6;
    private static final int DRAW_LINES = 3;
    private static final int DRAW_BOXES = 300;
    private static final int MAX_X = LETTER_WIDTH * MAX_LETTER_COUNT;
    private static final int MAX_Y = IMAGE_HEIGHT;
    private static final boolean PAINT_BG = false;

    private static final Color[] RANDOM_BG_COLORS =
            {Color.RED, Color.DARK_GRAY, Color.GREEN, Color.MAGENTA, Color.ORANGE, Color.PINK, Color.YELLOW};

    private static final Color[] RANDOM_FG_COLORS =
            {Color.BLACK, Color.RED, Color.ORANGE, Color.BLUE, Color.GREEN};

    public BufferedImage skewImage(String securityChars) {
        BufferedImage outImage = new BufferedImage(MAX_X, MAX_Y, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = outImage.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, MAX_X, MAX_Y);
        for (int i = 0; i < DRAW_BOXES; i++) {
            paindBoxes(g2d);
        }

        g2d.setColor(Color.BLACK);
        g2d.drawRect(0, 0, (MAX_X) - 1, MAX_Y - 1);

        AffineTransform affineTransform = new AffineTransform();
        int length = Math.min(securityChars.length(), MAX_LETTER_COUNT);
        for (int i = 0; i < length; i++) {
            double angle = 0;
            if (Math.random() * 2 > 1) {
                angle = Math.random() * SKEW;
            } else {
                angle = Math.random() * -SKEW;
            }
            affineTransform.rotate(angle, (LETTER_WIDTH * i) + ((double) LETTER_WIDTH / 2), (double) MAX_Y / 2);
            g2d.setTransform(affineTransform);
            setRandomFont(g2d);
            setRandomFGColor(g2d);
            g2d.drawString(securityChars.substring(i, i + 1), (i * LETTER_WIDTH) + 3, 28 + (int) (Math.random() * 6));

            affineTransform.rotate(-angle, (LETTER_WIDTH * i) + ((double) LETTER_WIDTH / 2), (double) MAX_Y / 2);
        }

        if (PAINT_BG) {
            g2d.setXORMode(Color.RED);
            g2d.setStroke(new BasicStroke(10));
            g2d.drawLine(0, 0, MAX_X, MAX_Y);
            g2d.setXORMode(Color.YELLOW);
            g2d.drawLine(0, MAX_Y, MAX_X, 0);

            g2d.setXORMode(RANDOM_BG_COLORS[(int) (Math.random() * RANDOM_BG_COLORS.length)]);
            g2d.setStroke(new BasicStroke(10));
            g2d.drawLine(0, MAX_Y / 2, MAX_X, MAX_Y / 2);
            g2d.setXORMode(RANDOM_BG_COLORS[(int) (Math.random() * RANDOM_BG_COLORS.length)]);
            g2d.drawLine(0, (MAX_Y / 2) - 10, MAX_X, (MAX_Y / 2) - 10);
            g2d.setXORMode(RANDOM_BG_COLORS[(int) (Math.random() * RANDOM_BG_COLORS.length)]);
            g2d.drawLine(0, (MAX_Y / 2) + 10, MAX_X, (MAX_Y / 2) + 10);
        }

        for (int i = 0; i < DRAW_LINES * length; i++) {
            g2d.setColor(Color.GRAY);
            g2d.setStroke(new BasicStroke(1));
            affineTransform.rotate(0);
            g2d.drawLine(getRandomX(), getRandomY(), getRandomX(), getRandomY());
        }
        return outImage;
    }

    private void paindBoxes(Graphics2D g2d) {
        int colorId = (int) (Math.random() * RANDOM_BG_COLORS.length);
        //g2d.setColor(RANDOM_BG_COLORS[colorId]);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(getRandomX(), getRandomY(), getRandomX(), getRandomY());
    }

    private int getRandomX() {
        return (int) (Math.random() * MAX_X);
    }

    private int getRandomY() {
        return (int) (Math.random() * MAX_Y);
    }

    private void setRandomFont(Graphics2D g2d) {
        g2d.setFont(new Font(null, Font.ITALIC, new Double((Math.random() * 10 + 30)).intValue()));
    }

    private void setRandomFGColor(Graphics2D g2d) {
        int colorId = (int) (Math.random() * RANDOM_FG_COLORS.length);
        g2d.setColor(RANDOM_FG_COLORS[colorId]);
    }

    private void setRandomBGColor(Graphics2D g2d) {
        int colorId = (int) (Math.random() * RANDOM_BG_COLORS.length);
        g2d.setColor(RANDOM_BG_COLORS[colorId]);
    }
}
