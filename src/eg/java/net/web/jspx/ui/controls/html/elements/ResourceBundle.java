package eg.java.net.web.jspx.ui.controls.html.elements;

import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.engine.util.bean.ResourceBundleUtility;
import eg.java.net.web.jspx.ui.controls.html.elements.hidden.HiddenGenericWebControl;
import eg.java.net.web.jspx.ui.pages.Page;

import java.util.Properties;

public class ResourceBundle extends HiddenGenericWebControl {
    /**
     *
     */
    private static final long serialVersionUID = 8441380439818461486L;
    Properties bundle = null;
    private final String BaseName = "base";
    private final String charSetKey = "charset";

    public ResourceBundle() {
        super(TagFactory.ResourceBundle);
    }

    public ResourceBundle(Page page) {
        super(TagFactory.ResourceBundle, page);
    }

    public String getBaseName() {
        return getAttributeValue(BaseName);
    }

    public void setBaseName(String bundle) {
        setAttributeValue(BaseName, bundle);
    }

    public String getString(String key) {
        return getBundle().getProperty(key.trim());
    }

    public Properties getBundle() {
        if (bundle == null)
            reloadBundle();
        return bundle;
    }

    public void reloadBundle() {
        bundle = ResourceBundleUtility.getBundle(getBaseName(), page.getLocale().toString(),
                StringUtility.isNullOrEmpty(getCharSet()) ? page.getCharSet() : getCharSet(), page.getContext());
    }

    public String getCharSet() {
        return getAttributeValue(charSetKey);
    }

    public void setCharSet(String encoding) {
        setAttributeValue(charSetKey, encoding);
    }
}
