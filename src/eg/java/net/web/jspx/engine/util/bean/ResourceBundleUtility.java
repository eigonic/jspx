package eg.java.net.web.jspx.engine.util.bean;

import eg.java.net.web.jspx.engine.util.ResourceUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Properties;

public class ResourceBundleUtility {

    private static final Logger logger = LoggerFactory.getLogger(ResourceBundleUtility.class);
    private static final Object chacheLock = new Object();
    protected static Hashtable<String, Properties> chacedBundles = new Hashtable<String, Properties>();

    private ResourceBundleUtility() {
    }

    public static void chacheBundle(String name, Properties bundle) {
        synchronized (chacheLock) {
            chacedBundles.put(name, bundle);
        }
    }

    public static Properties getCachedBundle(String name) {
        return chacedBundles.get(name);
    }

    public static Properties getBundle(String name, String locale, String charset, ServletContext context) {
        if (isDefaultLocale(locale))
            locale = "";
        else
            locale = "_" + locale;
        String bundleName = buildBundleName(name, locale);
        Properties bundle = getCachedBundle(bundleName);
        if (bundle == null) {
            InputStream stream = ResourceUtility.getResourceStream(bundleName, context);
            if (stream == null) {
                // get the default resource
                bundleName = buildBundleName(name, "");
                bundle = getCachedBundle(bundleName);
                if (bundle == null) {
                    stream = ResourceUtility.getResourceStream(bundleName, context);
                    // [Sep 11, 2009 12:31:53 AM] [amr.eladawy] [if the stream is null, log error and return]
                    if (stream == null) {
                        logger.error("Bundle [" + bundleName + "] is not found");
                        return new Properties();
                    }
                }
            }
            if (bundleName.endsWith(".xml")) {
                try {
                    bundle = new Properties();
                    bundle.loadFromXML(stream);
                } catch (Exception e) {
                    logger.error("getBundle()- ", e);
                    bundle = null;
                }
            } else
                bundle = loadProperties(stream, charset);
            if (bundle != null)
                chacheBundle(bundleName, bundle);
        }
        return bundle;
    }

    private static String buildBundleName(String name, String locale) {
        String sufix = ".properties";
        if (name.toLowerCase().endsWith(".xml")) {
            sufix = ".xml";
            name = name.substring(0, name.length() - 4);
        }
        if (name.charAt(0) != '/')
            name = "/" + name;
        String bundleName = name + locale;
        bundleName = bundleName.replace('.', '/').concat(sufix);
        return bundleName;
    }

    public static boolean isDefaultLocale(String locale) {
        return locale.equalsIgnoreCase("en") || locale.equalsIgnoreCase("en_us") || locale.equalsIgnoreCase("us");
    }

    public static Properties loadProperties(InputStream inputStream, String charset) {
        Properties properties = new Properties();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, charset));
            String temp = null;
            int index = -1;
            while ((temp = reader.readLine()) != null) {
                index = temp.indexOf('=');
                if (index > -1)
                    properties.put(temp.substring(0, index), temp.substring(index + 1));
            }
        } catch (Exception e) {
            logger.error("loadProperties(inputStream=" + inputStream + ", charset=" + charset + ")", e);
        }

        return properties;
    }
}
