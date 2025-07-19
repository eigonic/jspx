package eg.java.net.web.jspx.engine.util;

/**
 * SQL Utility for preventing SQL injection. <br />
 * based on the OWASP ESAPI
 *
 * @author amr.eladawy
 * @author Jeff Williams (jeff.williams .at. aspectsecurity.com) <a href="http://www.aspectsecurity.com">Aspect Security</a>
 * @see org.owasp.esapi.Encoder
 * @since June 1, 2007
 */
public class SqlUtility {
    private static final String[] hex = new String[256];
    private final static char IMMUNE_SQL = ' ';

    static {
        for (char c = 0; c < 0xFF; c++) {
            if (c >= 0x30 && c <= 0x5A || c >= 0x61 && c <= 0x7A || c == 0x2E || c == 0x2F || c == 0x2D || c == 0x28 || c == 0x29 || c == 0x2C
                    || c == 0x3D || c == 0x5f || c == 0x25)
                hex[c] = null;
            else
                hex[c] = Integer.toHexString(c);
        }
    }

    private SqlUtility() {
    }

    public static String encodeForSQL(String input) {
        if (!StringUtility.isNullOrEmpty(input))
            return encode(input);
        return input;
    }

    /**
     * Encode a String so that it can be safely used in a specific context.
     *
     * @param immune
     * @param input  the String to encode
     * @return the encoded String
     */
    private static String encode(String input) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            sb.append(encodeCharacter(Character.valueOf(c)));
        }
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Encode a single character with a backslash
     *
     * @param immune
     */
    private static String encodeCharacter(Character c) {
        if (c.charValue() == IMMUNE_SQL || c >= hex.length)
            return c.toString();

        // check for alphanumeric characters
        return (hex[c] == null ? c.toString() : ("\\" + c));
    }
}
