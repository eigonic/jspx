package eg.java.net.web.jspx.engine.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class GeneralUtil {

    /**
     * Utility for making deep copies (vs. clone()'s shallow copies) of
     * objects. Objects are first serialized and then deserialized. Error
     * checking is fairly minimal in this implementation. If an object is
     * encountered that cannot be serialized (or that references an object
     * that cannot be serialized) an error is printed to System.err and
     * null is returned. Depending on your specific application, it might
     * make more sense to have copy(...) re-throw the exception.
     * <p>
     * A later version of this class includes some minor optimizations.
     * Returns a copy of the object, or null if the object cannot
     * be serialized.
     */
    public static Object copy(Object orig) {
        Object obj = null;
        try {
            // Write the object out to a byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(orig);
            out.flush();
            out.close();

            // Make an input stream from the byte array and read
            // a copy of the object back in.
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
            obj = in.readObject();
        } catch (Throwable e) {
            throw new RuntimeException("Can't copy the obj [" + orig + "], error: " + e,e);
        }
        return obj;
    }

}
