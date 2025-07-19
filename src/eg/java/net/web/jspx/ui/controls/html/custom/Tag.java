package eg.java.net.web.jspx.ui.controls.html.custom;

import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.ui.controls.WebControl;
import eg.java.net.web.jspx.ui.controls.attrbs.JspxAttribute;
import eg.java.net.web.jspx.ui.controls.html.GenericWebControl;
import eg.java.net.web.jspx.ui.pages.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a declarative tag to specify backing code for a custom or user control.
 *
 * @author amr.eladawy
 */
public class Tag extends GenericWebControl {
    /**
     *
     */
    private static final long serialVersionUID = 1383130924840138523L;

    private static final Logger logger = LoggerFactory.getLogger(Tag.class);

    public static String CUSTOM_CONTROL = "custom";
    public static String USER_CONTROL = "user";
    @JspxAttribute
    public static String location = "location";
    @JspxAttribute
    protected String prefix = "prefix";
    @JspxAttribute
    protected String type = "type";

    public Tag() {
        super(TagFactory.Tag);
    }

    public Tag(Page page) {
        super(TagFactory.Tag, page);
    }

    /**
     * gets the Tag Prefix for this Tag Directive.
     *
     * @return
     */
    public String getPrefix() {
        return getAttributeValue(prefix);
    }

    public void setPrefix(String prefixValue) {
        setAttributeValue(prefix, prefixValue);
    }

    public String getType() {
        return getAttributeValue(type);
    }

    public void setType(String typeValue) {
        setAttributeValue(type, typeValue);
    }

    public String getLoction() {
        return getAttributeValue(location);
    }

    public void setLoction(String locationValue) {
        setAttributeValue(location, locationValue);
    }

    /**
     * based on the type of the control [Custom/User] a new instance is returned to represent this control.
     *
     * @return
     */
    public WebControl getWebControl() {
        if (getType().equalsIgnoreCase(CUSTOM_CONTROL))
            try {
                return (WebControl) Class.forName(getLoction()).newInstance();
            } catch (Exception e) {
                logger.debug("Cannot load Custom control for " + this);
                logger.error(e.getMessage(), e);
            }
        else
            try {
                UserControl userControl = new UserControl();
                userControl.setLocation(getLoction());
                return userControl;
            } catch (Exception e) {
                logger.debug("Cannot load User Control for " + this);
                logger.error(e.getMessage(), e);
            }
        return new GenericWebControl(getPrefix());
    }
}
