/**
 *
 */
package eg.java.net.web.jspx.ui.controls.html.elements.dataitem;

import eg.java.net.web.jspx.engine.el.XmlElementMap;
import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.ELUtility;
import eg.java.net.web.jspx.engine.util.PropertyAccessor;
import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.attrbs.JspxAttribute;
import eg.java.net.web.jspx.ui.controls.html.elements.Input;
import eg.java.net.web.jspx.ui.controls.html.elements.hidden.HiddenGenericWebControl;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.InternalValueHolder;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.ValueHolder;
import eg.java.net.web.jspx.ui.pages.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.util.Hashtable;

/**
 * @author aeladawy
 *
 */
public class XmlBean extends HiddenGenericWebControl implements InternalValueHolder, ValueHolder {
    private static final Logger logger = LoggerFactory.getLogger(XmlBean.class);
    private static final long serialVersionUID = 898265738143909816L;
    @JspxAttribute
    public static String value = "value";
    @JspxAttribute
    protected static String dateformat = "dateformat";
    @JspxAttribute
    protected static String autoBindKey = "autobind";
    @JspxAttribute
    private static final String bindToClass = "bindtoclass";
    protected Hashtable<String, XmlElementMap> bean;
    protected boolean bound = false;
    protected Document doc;
    private String valueBinding;

    public XmlBean(Page page) {
        super(TagFactory.XmlBean, page);
    }

    public XmlBean() {
        super(TagFactory.XmlBean);
    }

    /* (non-Javadoc)
     * @see eg.java.net.web.jspx.ui.controls.html.elements.markers.InternalValueHolder#setInternalInputValues(java.util.Hashtable)
     */
    public void setInternalInputValues(Hashtable<String, String> values) {
    }

    public String getValue() {
        return getAttributeValue(value);
    }

    public void setValue(String valueString) {
        setAttributeValue(value, valueString);
    }

    public String getValueBinding() {
        return Input.calculateValueBinding(valueBinding, getAttribute(value));
    }

    public void setValueBinding(String valueBinding) {
        this.valueBinding = valueBinding;
    }

    public void dataBind() {
        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(getValue().getBytes()));

            ELUtility.addObjectToJexlMap(getPage(), getId(), this);
        } catch (Exception e) {
        }

    }

    public String getDateformat() {
        return getAttributeValue(dateformat);
    }

    public void setDateformat(String dateformatVal) {
        setAttributeValue(dateformat, dateformatVal);
    }

    public boolean getAutoBind() {
        return getAttributeBooleanValue(autoBindKey);
    }

    public void setAutoBind(boolean auto) {
        setAttributeBooleanValue(autoBindKey, auto);
    }

    public String getBindToClass() {
        return getAttributeValue(bindToClass);
    }

    public void setBindToClass(String bindToClassVal) {
        setAttributeValue(bindToClass, bindToClassVal);
    }

    @Override
    public void render(RenderPrinter outputStream) throws Exception {
        if (getAutoBind())
            dataBind();
        super.render(outputStream);
    }

    public Object convertToObject(Hashtable<String, XmlElementMap> bean, String classname) throws InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        if (StringUtility.isNullOrEmpty(classname))
            return bean;
        try {
            Object dataClass = Class.forName(classname).newInstance();
            // do reflection.
            Method[] methods = dataClass.getClass().getDeclaredMethods();
            for (Method method : methods) {
                String name = method.getName();
                String key = "";
                if (name.startsWith("set")) {
                    key = name.substring(3);
                    XmlElementMap dataField = bean.get((key));
                    if (dataField == null)
                        continue;
                    Object value = dataField.getValue();
                    try {
                        PropertyAccessor.invokeSetter(dataClass, method, value, getDateformat());
                    } catch (Throwable e) {
                        logger.error("convertToObject(Hashtable<String,DataField>, String)", e);
                    }
                }
            }
            return dataClass;
        } catch (Exception e) {
            logger.error("convertToObject(Hashtable<String,DataField>, String)", e);
        }
        return bean;
    }

    /**
     * eval the xpath expression from XML document.
     * [12 May 2015 09:38:30]
     * @author aeladawy
     * @param expression
     * @return
     */
    public Object eval(String expression) {
        if (doc != null) {
            try {
                Node node = (Node) XPathFactory.newInstance().newXPath().evaluate(expression, doc, XPathConstants.NODE);
                if (node != null)
                    return node.getNodeValue();
            } catch (XPathExpressionException e) {
                logger.error("eval(String)", e);
            }
        }
        return null;
    }
}
