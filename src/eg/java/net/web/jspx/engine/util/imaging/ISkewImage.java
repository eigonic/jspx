package eg.java.net.web.jspx.engine.util.imaging;

import java.awt.image.BufferedImage;

/**
 * <a href="http://skewpassim.sourceforge.net/">http://skewpassim.sourceforge.net/</a>
 * <br><b>Interface class for skewing security string</b>
 */
public interface ISkewImage {

    /**
     * The implementation method should draw the securityChars on the image
     * and skew it for security purpose. The return value is the finished image object
     *
     * @param securityChars
     * @return - BufferedImage finished skewed iamge
     */
    BufferedImage skewImage(String securityChars);
}
