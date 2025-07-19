/**
 *
 */
package eg.java.net.web.jspx.ui.controls.html;

import eg.java.net.web.jspx.engine.parser.PageModelComposer;
import eg.java.net.web.jspx.engine.util.PageCarriage;
import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.engine.util.bean.JspxBeanUtility;
import eg.java.net.web.jspx.engine.util.bean.RepeaterUtility;
import eg.java.net.web.jspx.engine.util.ui.Render;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.WebControl;
import eg.java.net.web.jspx.ui.controls.attrbs.Attribute;
import eg.java.net.web.jspx.ui.controls.attrbs.JspxAttribute;
import eg.java.net.web.jspx.ui.controls.html.elements.Input;
import eg.java.net.web.jspx.ui.controls.html.elements.ResourceBundle;
import eg.java.net.web.jspx.ui.controls.html.elements.dataitem.ItemTemplate;
import eg.java.net.web.jspx.ui.controls.html.elements.inputs.InputFactory;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.*;
import eg.java.net.web.jspx.ui.controls.html.w3.Head;
import eg.java.net.web.jspx.ui.pages.ContentPage;
import eg.java.net.web.jspx.ui.pages.Page;
import org.kxml.Xml;
import org.kxml.io.ParseException;
import org.kxml.parser.ParseEvent;
import org.kxml.parser.XmlParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * Generic Web Control , first implementation of the WebControl.
 *
 * @author amr.eladawy
 *
 */
public class GenericWebControl extends WebControl {
    /**
     *
     */
    private static final long serialVersionUID = 4035681640702178960L;

    private static final Logger logger = LoggerFactory.getLogger(GenericWebControl.class);

    @JspxAttribute
    protected static String styleKey = "style";

    @JspxAttribute
    protected static String classKey = "class";

    protected static String content = "content";
    protected boolean noSystemCss;

    public GenericWebControl(String tagName) {
        super(tagName);
    }

    public GenericWebControl(String tagName, Page page) {
        super(tagName, page);
    }

    protected void loadInternalAttributes() {
        super.loadInternalAttributes();
        // style is added as internal control as it is rendered in a different way.
        internalAttribtes.put(styleKey, 0);
        internalAttribtes.put(content, 0);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.net.web.jspx.ui.controls.WebControl#render(java.io.OutputStream)
     */
    @Override
    public void render(RenderPrinter outputStream) throws Exception {
        if (!isRendered() || !isAccessible())
            return;
        rebindValueHolderControl();
        outputStream.write(Render.startTag);
        outputStream.write(getTageName());

        renderAttributes(outputStream);
        renderStyle(outputStream);

        if (isContentControl) {
            outputStream.write(Render.endTag);
            postRenderStartTag(outputStream);
            // Render.renderNewLine(outputStream);
            renderChildren(outputStream);

            preRenderEndTag(outputStream);
            outputStream.write(Render.startEndTag);
            outputStream.write(getTageName());
            outputStream.write(Render.endTag);
        } else
            outputStream.write(Render.closeTag);
        // outputStream.write(Render.newLine);
    }

    /**
     * gets the Tagname to be rendered. Some controls changes this value while rendering like LinkCommand.
     *
     * @return
     */
    public String getTageName() {
        return tagName;
    }

    /**
     * @author Yahya.Hamdy
     * @param outputStream
     * @throws Exception
     */
    public void renderChildren(RenderPrinter outputStream) throws Exception {
        for (WebControl control : controls)
            if (control != null)
                control.render(outputStream);
    }

    /**
     * renders the style of this control.
     */
    protected void renderStyle(RenderPrinter outputStream) throws Exception {
        // [Jul 14, 2011 12:38:55 PM] [Amr.ElAdawy] [fixing the visibility condition]
        if (!visible)
            addStyle(new Attribute("display", "none"));
        if (style.isEmpty())
            return;
        outputStream.write(Render.whiteSpace);
        outputStream.write(styleKey);
        outputStream.write(Render.equal);
        outputStream.write(Render.doubleQuotes);
        for (String styleKey : style.keySet()) {
            outputStream.write(styleKey);
            outputStream.write(Render.colon);
            outputStream.write(style.get(styleKey).getValue(page));
            outputStream.write(Render.semiColon);
            outputStream.write(Render.whiteSpace);
        }
        outputStream.write(Render.doubleQuotes);
    }

    protected void renderAttributes(RenderPrinter outputStream) throws Exception {
        if (parent != null && !StringUtility.isNullOrEmpty(parent.getIdSuffix())) {
            setId(getId() + parent.getIdSuffix());
            setIdSuffix(parent.getIdSuffix());
        }
        for (Attribute attribute : attributes.values()) {
            String key = attribute.getKey();
            if (!StringUtility.isNullOrEmpty(key))
                if (!isInternalAttribute(key))
                    attribute.render(outputStream, page);
        }
    }

    @Override
    public WebControl deSerialize(XmlParser parser, Page page, WebControl parent) throws Exception {
        ParseEvent peek = parser.peek();
        String tagPlace = parser.getLineNumber() + ":" + parser.getColumnNumber();
        parseStartTag(parser);
        // [Aug 27, 2009 8:55:42 AM] [amr.eladawy] [if this control is an internal value holder,
        // then save its ID in the page CurrentInternalValueHolder]
        this.page = page;
        String oldInternalValueHodler = (String) page.carriage.get(PageCarriage.CURRENT_INTERNAL_VALUE_HOLDER_ID);
        if (this instanceof InternalValueHolder)
            page.carriage.set(PageCarriage.CURRENT_INTERNAL_VALUE_HOLDER_ID, this.getId());
        if (isContentControl) {
            // parsing children
            while (!parser.peek(Xml.END_TAG, null, tagName)) {
                peek = parser.peek();
                tagPlace = parser.getLineNumber() + ":" + parser.getColumnNumber();
                if (peek.getType() == Xml.START_TAG)
                    addControl(PageModelComposer.prepareWebControl(peek, page, tagPlace).deSerialize(parser, page, this));
                else if (peek.getType() == Xml.COMMENT) {
                    page.addControl(new StringLiteral("<!--" + peek.getText() + "-->"));
                    peek = parser.read();
                } else if (peek.getType() == Xml.TEXT) {
                    addControl(new StringLiteral(parser.readText()));
                } else if (peek.getType() == Xml.WHITESPACE) {
                    addControl(new WhiteSpace(peek.getText()));
                    peek = parser.read();
                } // [May 30, 2011 8:05:18 AM] [amr.eladawy] [DocType should not be found here, just incase]
                else if (peek.getType() == Xml.DOCTYPE) {
                    addControl(new StringLiteral(
                            Render.startTag + "!" + peek.getText() + Render.endTag));
                    peek = parser.read();
                } else if (peek.getType() == Xml.END_DOCUMENT || (peek.getType() == Xml.END_TAG && !peek.getName().equalsIgnoreCase(tagName)))
                    throw new ParseException(
                            "The tag [" + tagName + "] palced at " + tagPlace + " has no matching closing tag ["
                                    + (peek.getType() == Xml.END_DOCUMENT ? "End Document"
                                    : ((peek.getName() == null ? "self closing tag /> " : peek.getName())))
                                    + "]",
                            null, parser.getLineNumber(), parser.getColumnNumber());

            }
            // [Jul 23, 2012 2:19:02 PM] [Amr.ElAdawy] [isContentControl is a property of the web control itself not a function of
            // of the children]
            // isContentControl = controls.size() != 0;
        }
        parseEndTage(parser);
        setPage(page);
        setParent(parent);
        // [Aug 27, 2009 9:00:27 AM] [amr.eladawy] [Set it back to the previous ID]
        if (this instanceof InternalValueHolder)
            page.carriage.set(PageCarriage.CURRENT_INTERNAL_VALUE_HOLDER_ID, oldInternalValueHodler);

        return this;
    }

    protected void parseEndTage(XmlParser parser) throws IOException {
        PageModelComposer.ensureTag(parser, Xml.END_TAG, tagName);
    }

    /**
     * Parses the start tag and its attributes.
     *
     * @param parser
     * @throws IOException
     */
    protected void parseStartTag(XmlParser parser) throws IOException {
        List<Attribute> attributes = PageModelComposer.ensureTag(parser, Xml.START_TAG, tagName);
        Attribute attribute = null;
        if (attributes != null)
            for (int i = 0; i < attributes.size(); i++) {
                attribute = attributes.get(i);
                if (attribute.getKey().equalsIgnoreCase("style"))
                    parseStyle(attribute);
                else if (attribute.getKey().equalsIgnoreCase("width"))
                    addStyle(new Attribute("width", attribute.getValue()));
                else if (attribute.getKey().equalsIgnoreCase("height"))
                    addStyle(new Attribute("height", attribute.getValue()));
                else if (attribute.getKey().equalsIgnoreCase("id") || attribute.getKey().equalsIgnoreCase("name")) {
                    // [Feb 21, 2011 10:01:18 PM] [Amr.ElAdawy] [if the Id is set to auto generated value don't change it]
                    if (!StringUtility.isNullOrEmpty(attribute.getValue()))
                        setId(attribute.getValue());
                } else
                    addAttribute(attribute);
            }
    }

    /**
     * parses the style attribute and composes styles attributes collection.
     *
     * @param attribute
     */
    protected void parseStyle(Attribute attribute) {
        String val = attribute.getValue().replace("\r", " ").replace("\n", " ").trim();
        String[] styleItems;
        String[] styleParts = val.split(";");
        for (int i = 0; i < styleParts.length; i++) {
            if (!styleParts[i].trim().isEmpty()) {
                styleItems = styleParts[i].trim().split(":");
                if (styleItems.length == 2)
                    addStyle(new Attribute(styleItems[0].toLowerCase().trim(), styleItems[1].trim()));
            }
        }
    }

    public WebControl clone(WebControl parent, Page page, ISubmitter submitter, IAjaxSubmitter ajaxSubmitter) {
        // See if this control is declared
        // if so ,
        // 1- get it from page.
        // 2- load it with this instance.
        // 3- add it to the declared collection
        GenericWebControl newGenericWebControl = null;
        try {
            if (isDeclared)
                try {
                    newGenericWebControl = (GenericWebControl) page.getControl(getId());
                    // [Jul 10, 2009 9:19:19 PM] [amr.eladawy] [if the control is null, then this means that the control has
                    // getter but initial null value]
                    if (newGenericWebControl == null) {
                        newGenericWebControl = newInstance();
                        // PropertyAccessor.setProperty(page, getId(), newGenericWebControl);
                        page.setControl(getId(), newGenericWebControl);
                    }
                    page.addDeclaredControl(newGenericWebControl);
                } catch (Exception e) {
                    // the control is not found in the page, creating new one.
                    newGenericWebControl = newInstance();
                }
            else
                newGenericWebControl = newInstance();
        } catch (Exception e) {
            logger.warn("clone() - exception ignored", e);
        }

        newGenericWebControl.setPage(page);
        newGenericWebControl.setMySubmitter(submitter);
        newGenericWebControl.setMyAjaxSubmitter(ajaxSubmitter);
        newGenericWebControl.setParent(parent);

        this.mergeBasicProperties(newGenericWebControl);
        // clone attributes
        newGenericWebControl.setAttributes(cloneAttributes());
        // clone style
        newGenericWebControl.setStyle(cloneStyle());
        // [Nov 10, 2014 11:04:31 AM] [amr.eladawy] [if this is Item Template, raise a flag to prevent value holder from being
        // added to page value holders]
        if (newGenericWebControl instanceof ItemTemplate) {
            Stack<String> itemTempalteStack = (Stack<String>) page.carriage.get(PageCarriage.ITEM_TEMPLATE_PARENT);
            if (itemTempalteStack == null) {
                itemTempalteStack = new Stack<String>();
                page.carriage.set(PageCarriage.ITEM_TEMPLATE_PARENT, itemTempalteStack);
            }
            itemTempalteStack.push(getId());
        }
        // clone controls
        cloneChildrenControls(newGenericWebControl);
        // [Nov 10, 2014 11:05:17 AM] [amr.eladawy] [remove the flag]
        if (newGenericWebControl instanceof ItemTemplate) {
            Stack<String> itemTempalteStack = (Stack<String>) page.carriage.get(PageCarriage.ITEM_TEMPLATE_PARENT);
            if (itemTempalteStack != null) {
                try {
                    itemTempalteStack.pop();

                } catch (EmptyStackException e) {
                    page.carriage.remove(PageCarriage.ITEM_TEMPLATE_PARENT);
                }
            }
        }
        Stack<String> itemTempalteStack = (Stack<String>) page.carriage.get(PageCarriage.ITEM_TEMPLATE_PARENT);
        boolean isItemTemplateParent = false;
        try {
            isItemTemplateParent = itemTempalteStack != null && !StringUtility.isNullOrEmpty(itemTempalteStack.peek());
        } catch (Exception e) {
        }
        // [Nov 10, 2014 11:32:55 AM] [amr.eladawy] [if the parent is Item Template, then dont add to value holder collection.]
        if (newGenericWebControl instanceof ValueHolder && !isItemTemplateParent) {
            ValueHolder c = (ValueHolder) newGenericWebControl;
            c.setValueBinding(c.getValueBinding());
            page.addValueHolder(newGenericWebControl.getId(), newGenericWebControl);
        }
        if (!newGenericWebControl.isDeclared && newGenericWebControl instanceof InternalEventsListener)
            page.addInternalEventsControl(newGenericWebControl.getId(), newGenericWebControl);
        if (!newGenericWebControl.isDeclared && newGenericWebControl instanceof InternalValueHolder)
            page.addInternalValueHolder(newGenericWebControl.getId(), (InternalValueHolder) newGenericWebControl);
        if (newGenericWebControl instanceof ItemDataBound)
            page.addItemDataBoundControl(newGenericWebControl.getId(), newGenericWebControl);

        if (newGenericWebControl instanceof ResourceBundle)
            page.addResourceBundle(newGenericWebControl.getId(), (ResourceBundle) newGenericWebControl);

        if (newGenericWebControl instanceof IValidate)
            page.addValidator((IValidate) newGenericWebControl);

        if (newGenericWebControl instanceof Head) {
            if (page instanceof ContentPage)
                ((ContentPage) page).getMasterPage().setHead((Head) newGenericWebControl);
            else
                page.setHead((Head) newGenericWebControl);
        }
        if (newGenericWebControl instanceof IAjaxSubmitter)
            page.addAjaxSubmitter(newGenericWebControl.getId(), (IAjaxSubmitter) newGenericWebControl);

        newGenericWebControl.loadInternalAttributes();
        // [6 Jul 2015 09:17:22] [aeladawy] [adding control to undeclared controls collection]
        if (!newGenericWebControl.isDeclared)
            page.addUndeclaredControl(newGenericWebControl);

        return newGenericWebControl;
    }

    /**
     * iterates throw the children controls and clone them.
     *
     * @param newGenericWebControl
     */
    protected void cloneChildrenControls(GenericWebControl newGenericWebControl) {
        ISubmitter submitter = newGenericWebControl.getMySubmitter();
        if (this instanceof ISubmitter)
            submitter = (ISubmitter) newGenericWebControl;
        IAjaxSubmitter ajaxSubmitter = newGenericWebControl.getMyAjaxSubmitter();
        if (this instanceof IAjaxSubmitter)
            ajaxSubmitter = (IAjaxSubmitter) newGenericWebControl;
        for (int i = 0; i < controls.size(); i++)
            newGenericWebControl.getControls()
                    .add(controls.get(i).clone(newGenericWebControl, newGenericWebControl.getPage(), submitter, ajaxSubmitter));
    }

    private GenericWebControl newInstance() {
        try {
            // [Jul 10, 2009 9:17:16 PM] [amr.eladawy] [if this is an input factory, then return the concrete calss]
            if (this instanceof InputFactory)
                return InputFactory.getConcreteInput(((Input) this).getType());
            return getClass().newInstance();
        } catch (Exception e) {
            // this means that this is a Generic Web control , which has
            // no nullary constructor.
            // calling the one with the Tagname
            return new GenericWebControl(tagName);
        }

    }

    protected Hashtable<String, Attribute> cloneAttributes() {
        return cloneAttributesMap(attributes);
    }

    protected Hashtable<String, Attribute> cloneStyle() {
        return cloneAttributesMap(style);
    }

    protected Hashtable<String, Attribute> cloneAttributesMap(Hashtable<String, Attribute> attributesMap) {
        Hashtable<String, Attribute> newAttributes = new Hashtable<String, Attribute>();
        String key = null;
        for (Iterator<String> i = attributesMap.keySet().iterator(); i.hasNext(); ) {
            key = i.next();
            newAttributes.put(key, attributesMap.get(key).clone());
        }
        return newAttributes;
    }

    /**
     * implement it if you want to do anything after rendering the start tag.
     *
     * @param outputStream
     * @throws Exception
     */
    protected void postRenderStartTag(RenderPrinter outputStream) throws Exception {
    }

    /**
     * implement it if you want to do any thing before rendering end Tag.
     *
     * @param outputStream
     * @throws Exception
     */
    protected void preRenderEndTag(RenderPrinter outputStream) throws Exception {
    }

    @Override
    public void saveViewState(HashMap<String, HashMap<String, String>> viewState) {
        if (!this.viewstate.isEmpty())
            viewState.put(getId(), this.viewstate);
    }

    public void setStyle(String style) {
        parseStyle(new Attribute(styleKey, style));
    }

    public String getStyleString() {
        return StringUtility.joinStyle(getStyle());
    }

    public void rebindValueHolderControl() {
        rebindValueHolderControl(null);
    }

    public void rebindValueHolderControl(String dateformat) {
        if (this instanceof ValueHolder) {
            ValueHolder v = (ValueHolder) this;
            String bind = v.getValueBinding();
            if (!StringUtility.isNullOrEmpty(bind)) {
                if (StringUtility.isEL(bind) && page != null && page.request != null && page.session != null) {
                    String newValue = JspxBeanUtility.evaluate(bind, page, dateformat);
                    if (newValue == null)
                        newValue = RepeaterUtility.evaluate(bind, page, dateformat);
                    if (newValue != null)
                        v.setValue(newValue);
                }
            }
        }
    }

    /**
     * Some controls have attributes that ponit at external or internal resources. <br />
     * HyperLink, Image, Script, ImageButton and Link <br />
     * this method formats this path to add the context path in case it is internal resource.
     *
     * @param path
     * @return
     */
    protected String composeURL(String path) {
        if (!StringUtility.isNullOrEmpty(path) && !path.startsWith("http://") && !path.startsWith("https://") && !path.startsWith("mailto:")
                && !path.startsWith(page.request.getContextPath())) {
            if (!path.startsWith("/"))
                path = "/" + path;
            path = page.request.getContextPath() + path;
        }
        return path;
    }

    public void setNoSystemCss(boolean noSystemCss) {
        this.noSystemCss = noSystemCss;
    }

}
