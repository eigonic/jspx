/**
 *
 */
package eg.java.net.web.jspx.engine.util.ui;

import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.ui.controls.WebControl;
import eg.java.net.web.jspx.ui.controls.attrbs.Attribute;
import eg.java.net.web.jspx.ui.controls.html.GenericWebControl;
import eg.java.net.web.jspx.ui.controls.html.StringLiteral;
import eg.java.net.web.jspx.ui.controls.html.elements.Calendar;
import eg.java.net.web.jspx.ui.controls.html.elements.HyperLink;
import eg.java.net.web.jspx.ui.controls.html.elements.Image;
import eg.java.net.web.jspx.ui.controls.html.elements.Label;
import eg.java.net.web.jspx.ui.controls.html.elements.ajax.AjaxPanel;
import eg.java.net.web.jspx.ui.controls.html.elements.dataitem.DataLookup;
import eg.java.net.web.jspx.ui.controls.html.elements.inputs.Hidden;
import eg.java.net.web.jspx.ui.controls.html.elements.inputs.TextBox;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.IAjaxSubmitter;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.ISubmitter;
import eg.java.net.web.jspx.ui.controls.html.elements.select.DropDown;
import eg.java.net.web.jspx.ui.pages.Page;

import java.util.List;
import java.util.Map;

/**
 * Utility class for Common HTML rendering.
 *
 * @author amr.eladawy
 *
 */
public abstract class Render {

    public static final String mostOuterJoiner = "#";

    public static final String innerJoiner = "_";

    public static final String outerJoiner = "-";

    public static final String mostInnerJoiner = "!";

    public static final String indent = "\t";

    public static final String newLine = "\r\n";

    public static final String whiteSpace = " ";

    public static final String NonBreakSpace = "&nbsp;";

    public static final String startTag = "<";

    public static final String startEndTag = "</";

    public static final String closeTag = "/>";

    public static final String endTag = ">";

    public static final String equal = "=";

    public static final String doubleQuotes = "\"";

    public static final String colon = ":";

    public static final String semiColon = ";";

    public static final String longSpace = "&nbsp;&nbsp;&nbsp;";

    public static final String OnMouseOver = "onmouseover";
    public static final String OnMouseOut = "onmouseout";
    public static final String OnLoad = "onmouseload";
    public static final String CssClass = "class";

    public static void renderNewLine(RenderPrinter outputStream) throws Exception {
        outputStream.write(newLine);
    }

    public static void renderWhiteSpaceLine(RenderPrinter outputStream) throws Exception {
        outputStream.write(whiteSpace);
    }

    public static void renderStartTag(RenderPrinter outputStream) throws Exception {
        outputStream.write(startTag);
    }

    public static void renderEndTag(RenderPrinter outputStream) throws Exception {
        outputStream.write(endTag);
    }

    public static void renderCloseTag(RenderPrinter outputStream) throws Exception {
        outputStream.write(closeTag);
    }

    public static void renderInput(RenderPrinter outputStream, String name, String value, String javaScript, Page page) throws Exception {
        TextBox box = createInput(name, value, javaScript, page);
        box.render(outputStream);
    }

    public static TextBox createInput(String name, String value, String javaScript, Page page) {
        TextBox box = new TextBox();
        box.setId(name);
        box.setValue(value);
        box.setPage(page);
        if (!StringUtility.isNullOrEmpty(javaScript)) {
            int idx = javaScript.indexOf("=");
            String attName = javaScript.substring(0, idx);
            String attValue = javaScript.substring(idx + 1);
            Attribute att = new Attribute(attName, attValue, box);
            box.getAttributes().put(attName, att);
        }
        return box;
    }

    public static void renderHiddenInput(RenderPrinter outputStream, String name, String value, Page page) throws Exception {
        Hidden box = createHidden(name, value, page);
        box.render(outputStream);
    }

    public static Hidden createHidden(String name, String value, Page page) {
        Hidden box = new Hidden();
        box.setId(name);
        box.setPage(page);
        box.setValue(value);
        return box;
    }

    public static void renderSelect(RenderPrinter outputStream, String name, String value, DataLookup dataFK, String javaScript, Page page)
            throws Exception {
        DropDown dropDown = createSelect(name, value, dataFK, javaScript, page);
        dropDown.render(outputStream);
    }

    public static DropDown createSelect(String name, String value, DataLookup dataFK, String javaScript, Page page) {
        DropDown dropDown = new DropDown();
        dropDown.setName(name);
        dropDown.setPage(page);
        Map<String, String> options = dataFK.dataBind();
        List<String> keys = dataFK.getKeys();
        for (String key : keys)
            dropDown.addOption(options.get(key), key);
        dropDown.setValue(value);
        if (!StringUtility.isNullOrEmpty(javaScript)) {
            int idx = javaScript.indexOf("=");
            String attName = javaScript.substring(0, idx);
            String attValue = javaScript.substring(idx + 1);
            Attribute att = new Attribute(attName, attValue, dropDown);
            dropDown.getAttributes().put(attName, att);
        }
        return dropDown;
    }

    public static void renderCalendar(RenderPrinter outputStream, String name, String dateValue, String dateFormat, Page page) throws Exception {
        Calendar calendar = createCalendar(name, dateValue, dateFormat, page);
        calendar.render(outputStream);
    }

    public static Calendar createCalendar(String name, String dateValue, String dateFormat, Page page) {
        Calendar calendar = new Calendar();
        calendar.setId(name);
        if (StringUtility.isNullOrEmpty(dateFormat))
            dateFormat = "dd/MM/yyyy HH:mm:ss";
        calendar.setDateFormat(dateFormat);
        calendar.setValue(dateValue);
        calendar.setPage(page);
        return calendar;
    }

    public static void renderRowEnd(RenderPrinter outputStream) throws Exception {
        outputStream.write(Render.startEndTag);
        outputStream.write(TagFactory.TableRow);
        outputStream.write(Render.endTag);
    }

    public static void renderColumnStart(RenderPrinter outputStream, Attribute style, Page page) throws Exception {
        renderColumnStart(outputStream, style, TagFactory.TableColumn, page);
    }

    public static void renderColumnStart(RenderPrinter outputStream, Attribute style, String tagName, Page page) throws Exception {
        outputStream.write(Render.startTag);
        outputStream.write(tagName);
        // render sytle for the td
        if (style != null && !StringUtility.isNullOrEmpty(style.getValue())) {
            style.renderCustomKey("class", outputStream, page);
        }
        outputStream.write(Render.endTag);
    }

    public static void renderTagEnd(RenderPrinter outputStream, String tagName) throws Exception {
        outputStream.write(Render.startEndTag);
        outputStream.write(tagName);
        outputStream.write(Render.endTag);
    }

    public static Label createLabel(String text, String forString, Page page) {
        Label label = new Label();
        label.setPage(page);
        label.setCssClass(CSS.CSS_CONTROL_LABEL);
        label.setText(text);
        label.setFor(forString);
        return label;
    }

    /**
     * renders commands like delete and navigate.
     *
     * @param outputStream
     * @param enabled
     *            TODO
     * @param internal
     *            TODO
     * @param target
     *            TODO
     * @param onClick
     *            TODO
     * @param postNormal
     *            overrides the ajax posting to be normal
     * @throws Exception
     */
    public static void renderCommand(RenderPrinter outputStream, String eventName, String eventArgs, String commandTitle, String confirmation,
                                     boolean enabled, boolean internal, String target, String onClick, boolean postNormal, Page page, String id, WebControl containerControl)
            throws Exception {
        renderCommand(outputStream, eventName, eventArgs, confirmation, enabled, internal, target, onClick, postNormal, new WebControl[]
                {new StringLiteral(commandTitle)}, null, page, id, containerControl);
    }

    public static void renderImgCommand(RenderPrinter outputStream, String eventName, String eventArgs, String imageSrc, String imageTip,
                                        String confirmation, boolean enabled, boolean internal, String target, String onClick, boolean postNormal, Page page, String id,
                                        WebControl containerControl) throws Exception {
        Image img = createImage(imageSrc, imageTip, page);
        renderCommand(outputStream, eventName, eventArgs, confirmation, enabled, internal, target, onClick, postNormal, new WebControl[]
                {img}, "jspxLinkImage " + (!enabled ? " disabled " : ""), page, id, containerControl);
    }

    /**
     * rendered a hyperlink with an image
     *
     * @param outputStream
     * @param script
     * @param imageSrc
     * @param imageTip
     * @param enabled
     * @param target
     * @param page
     * @throws Exception
     */
    public static void renderImgLink(RenderPrinter outputStream, String script, String imageSrc, String imageTip, boolean enabled, String target,
                                     Page page) throws Exception {
        createImgLink(script, imageSrc, imageTip, enabled, target, page).render(outputStream);
    }

    /**
     * create a hyperlink with an image
     *
     * @param script
     * @param imageSrc
     * @param imageTip
     * @param enabled
     * @param target
     * @param page
     * @throws Exception
     */
    public static HyperLink createImgLink(String script, String imageSrc, String imageTip, boolean enabled, String target, Page page) throws Exception {
        Image img = createImage(imageSrc, imageTip, page);

        return createLink(script, enabled, target, new WebControl[]
                {img}, "jspxLinkImage btn", page);
    }

    /**
     * creates Image
     * @param imageSrc
     * @param imageTip
     * @param page
     * @return
     */
    public static Image createImage(String imageSrc, String imageTip, Page page) {
        Image img = new Image(page);
        img.setSrc(imageSrc);
        img.setAlt(imageTip);
        img.setTitle(imageTip);
        return img;
    }

    /**
     * renders the comand in form of bootstrap link
     *
     * @param outputStream
     * @param eventName
     * @param eventArgs
     * @param imageTip
     * @param confirmation
     * @param enabled
     * @param internal
     * @param target
     * @param onClick
     * @param postNormal
     * @param iconClass
     * @param isButton
     * @param page
     * @param id
     * @param containerControl
     * @throws Exception
     */
    public static void renderReversedBootCommand(RenderPrinter outputStream, String eventName, String eventArgs, String imageTip, String confirmation,
                                                 boolean enabled, boolean internal, String target, String onClick, boolean postNormal, String iconClass, boolean isButton, Page page,
                                                 String id, WebControl containerControl) throws Exception {
        createReversedBootCommand(eventName, eventArgs, imageTip, confirmation, enabled, internal, target, onClick, postNormal, iconClass, isButton,
                page, id, containerControl).render(outputStream);

    }

    /**
     * renders the comand in form of bootstrap link
     *
     * @param outputStream
     * @param eventName
     * @param eventArgs
     * @param imageTip
     * @param confirmation
     * @param enabled
     * @param internal
     * @param target
     * @param onClick
     * @param postNormal
     * @param iconClass
     * @param isButton
     * @param page
     * @param id
     * @param containerControl
     * @throws Exception
     */
    public static void renderBootCommand(RenderPrinter outputStream, String eventName, String eventArgs, String imageTip, String confirmation,
                                         boolean enabled, boolean internal, String target, String onClick, boolean postNormal, String iconClass, boolean isButton, Page page,
                                         String id, WebControl containerControl) throws Exception {
        createBootCommand(eventName, eventArgs, imageTip, confirmation, enabled, internal, target, onClick, postNormal, iconClass, isButton, page, id,
                containerControl).render(outputStream);

    }

    /**
     * creates the command in form of bootstrap link and reverse the image inside the button.
     *
     * @param eventName
     * @param eventArgs
     * @param imageTip
     * @param confirmation
     * @param enabled
     * @param internal
     * @param target
     * @param onClick
     * @param postNormal
     * @param iconClass
     * @param isButton
     * @param page
     * @param id
     * @param containerControl
     * @return
     * @throws Exception
     */
    public static HyperLink createReversedBootCommand(String eventName, String eventArgs, String imageTip, String confirmation, boolean enabled,
                                                      boolean internal, String target, String onClick, boolean postNormal, String iconClass, boolean isButton, Page page, String id,
                                                      WebControl containerControl) throws Exception {
        GenericWebControl gwc = new GenericWebControl("i", page);

        gwc.setCssClass(iconClass);

        return createLinkCommand(eventName, eventArgs, confirmation, enabled, internal, target, onClick, postNormal, new WebControl[]
                        {new StringLiteral(imageTip), gwc}, "jspxLinkImage" + (isButton ? " btn " : "") + (!enabled ? " disabled " : ""), page, id,
                containerControl);

    }

    /**
     * creates the command in form of bootstrap link
     *
     * @param eventName
     * @param eventArgs
     * @param imageTip
     * @param confirmation
     * @param enabled
     * @param internal
     * @param target
     * @param onClick
     * @param postNormal
     * @param iconClass
     * @param isButton
     * @param page
     * @param id
     * @param containerControl
     * @return
     * @throws Exception
     */
    public static HyperLink createBootCommand(String eventName, String eventArgs, String imageTip, String confirmation, boolean enabled,
                                              boolean internal, String target, String onClick, boolean postNormal, String iconClass, boolean isButton, Page page, String id,
                                              WebControl containerControl) throws Exception {
        GenericWebControl gwc = new GenericWebControl("i", page);

        gwc.setCssClass(iconClass);

        return createLinkCommand(eventName, eventArgs, confirmation, enabled, internal, target, onClick, postNormal, new WebControl[]
                        {gwc, new StringLiteral(imageTip)}, "jspxLinkImage" + (isButton ? " btn " : "") + (!enabled ? " disabled " : ""), page, id,
                containerControl);

    }

    /**
     * Creates a link command for controls that are generated inside a table.
     *
     * @param outputStream
     * @param eventName
     *            eventName the name of the event to be fired (update, save, cancel)
     * @param eventArgs
     *            any extra args to be used.
     * @param confirmation
     *            if there is a confirmation message to be displayed
     * @param enabled
     * @param internal
     *            if this is an internal event
     * @param target
     *            the target of the anchor
     * @param onClick
     *            the client side code
     * @param postNormal
     *            true to override the ajax and force normal post back.
     * @param children
     *            array of controls to be rendered inside this anchor.
     * @param cssClass
     *            the CSS class of this anchor
     * @param page
     * @param id
     *            the id of this anchor
     * @param mySubmitter
     *            the normal submitter of the container control (i.e. the form)
     * @param myAjaxSubmitter
     *            The ajax submitter of this container control (i.e. ajaxpanel)
     * @param containerControl
     *            the continer control itselt (i.e. Table)
     * @throws Exception
     */
    public static void renderCommand(RenderPrinter outputStream, String eventName, String eventArgs, String confirmation, boolean enabled,
                                     boolean internal, String target, String onClick, boolean postNormal, WebControl[] children, String cssClass, Page page, String id,
                                     WebControl containerControl) throws Exception {

        HyperLink hyperLink = createLinkCommand(eventName, eventArgs, confirmation, enabled, internal, target, onClick, postNormal, children,
                cssClass, page, id, containerControl);
        hyperLink.getStyle().put("text-decoration", new Attribute("test-decoration", "none"));
        hyperLink.render(outputStream);
    }

    /**
     * Creates a link command for controls that are generated inside a table.
     *
     * @param eventName
     *            eventName the name of the event to be fired (update, save, cancel)
     * @param eventArgs
     *            any extra args to be used.
     * @param confirmation
     *            if there is a confirmation message to be displayed
     * @param enabled
     * @param internal
     *            if this is an internal event
     * @param target
     *            the target of the anchor
     * @param onClick
     *            the client side code
     * @param postNormal
     *            true to override the ajax and force normal post back.
     * @param children
     *            array of controls to be rendered inside this anchor.
     * @param cssClass
     *            the CSS class of this anchor
     * @param page
     * @param id
     *            the id of this anchor
     * @param mySubmitter
     *            the normal submitter of the container control (i.e. the form)
     * @param myAjaxSubmitter
     *            The ajax submitter of this container control (i.e. ajaxpanel)
     * @param containerControl
     *            the container control itself (i.e. Table)
     * @throws Exception
     */
    public static HyperLink createLinkCommand(String eventName, String eventArgs, String confirmation, boolean enabled, boolean internal,
                                              String target, String onClick, boolean postNormal, WebControl[] children, String cssClass, Page page, String id,
                                              WebControl containerControl) {
        HyperLink hyperLink = new HyperLink();
        hyperLink.setPage(page);
        if (!StringUtility.isNullOrEmpty(target))
            hyperLink.setTarget(target);

        if (enabled)
            hyperLink.setNavigationURL(composeAction(id, eventName, eventArgs, confirmation, internal, onClick, postNormal, true,
                    containerControl.getMySubmitter(), internal ? (IAjaxSubmitter) containerControl : containerControl.getMyAjaxSubmitter()));
        else
            hyperLink.setNavigationURL("#");
        if (children != null)
            for (WebControl child : children)
                hyperLink.getControls().add(child);

        if (!StringUtility.isNullOrEmpty(cssClass))
            hyperLink.setCssClass(cssClass);
        hyperLink.getStyle().put("text-decoration", new Attribute("text-decoration", "none"));
        hyperLink.addAttribute(new Attribute("data-loading-img", "true"));
        return hyperLink;
    }

    /**
     * Creates a link does not post back for controls that are generated inside a table.
     *
     * @param script
     *            the java script to rendered in the hyperlink
     * @param enabled
     * @param target
     *            the target of the anchor
     * @param children
     *            array of controls to be rendered inside this anchor.
     * @param cssClass
     *            the CSS class of this anchor
     * @param page
     * @throws Exception
     */
    public static HyperLink createLink(String script, boolean enabled, String target, WebControl[] children, String cssClass, Page page) {
        HyperLink hyperLink = new HyperLink();
        hyperLink.setPage(page);
        if (!StringUtility.isNullOrEmpty(target))
            hyperLink.setTarget(target);

        hyperLink.setNavigationURL("#");
        hyperLink.setAttributeValue("onclick", script.replace("\"", "'"));
        if (children != null)
            for (WebControl child : children)
                hyperLink.getControls().add(child);

        if (!StringUtility.isNullOrEmpty(cssClass))
            hyperLink.setCssClass(cssClass);
        return hyperLink;
    }

    /**
     * composes action on an action item.
     *
     * @param id
     *            the Id of the table containing the control
     * @param eventName
     *            the name of the event to be fired (update, save, cancel)
     * @param eventArgs
     *            any extra args to be used.
     * @param confirmation
     *            if there is a confirmation message to be displayed
     * @param internal
     *            if this is an internal event
     * @param onClick
     *            the client side code
     * @param postNormal
     *            true to override the ajax and force normal post back.
     * @param isLinkCommand
     *            true if it is a linkcommand that is using this action.
     * @param mySubmitter
     *            the submiter of control
     * @param myAjaxSubmitter
     *            the ajax submiter around the control.
     * @return
     */
    public static String composeAction(String id, String eventName, String eventArgs, String confirmation, boolean internal, String onClick,
                                       boolean postNormal, boolean isLinkCommand, ISubmitter mySubmitter, IAjaxSubmitter myAjaxSubmitter) {
        StringBuilder builder = new StringBuilder();
        if (isLinkCommand)
            builder.append("javascript:");
        if (!StringUtility.isNullOrEmpty(confirmation))
            builder.append("if (confirm ('").append(confirmation).append("')){");

        if (!StringUtility.isNullOrEmpty(onClick))
            builder.append(onClick).append(";");
        builder.append("post").append(internal ? "Internal" : "External").append("('").append(id).append("','").append(eventName).append("','")
                .append(eventArgs).append("','").append((mySubmitter == null ? "null" : ((WebControl) mySubmitter).getId())).append("','")
                .append((myAjaxSubmitter == null || postNormal ? "null" : ((WebControl) myAjaxSubmitter).getId())).append("')");

        if (!StringUtility.isNullOrEmpty(confirmation))
            builder.append("}");
        return builder.toString();
    }

    public static void renderCloseTag(RenderPrinter outputStream, String tagName) throws Exception {
        renderTagEnd(outputStream, tagName);
    }

    public static void renderStartTag(RenderPrinter outputStream, String tagName, String cssClass, String style) {
        outputStream.write(Render.startTag);
        outputStream.write(tagName);
        outputStream.write(Render.whiteSpace);
        if (!StringUtility.isNullOrEmpty(cssClass)) {
            outputStream.write("class");
            outputStream.write(Render.equal);
            outputStream.write(Render.doubleQuotes);
            outputStream.write(cssClass);
            outputStream.write(Render.doubleQuotes);
        }
        if (!StringUtility.isNullOrEmpty(style)) {
            outputStream.write("style");
            outputStream.write(Render.equal);
            outputStream.write(Render.doubleQuotes);
            outputStream.write(style);
            outputStream.write(Render.doubleQuotes);
        }
        outputStream.write(Render.endTag);
    }

    public static AjaxPanel createHiddenAjaxPane(Page page) {
        AjaxPanel p = new AjaxPanel(page);
        p.setId(Page.JSPX_HIDDEN_AJAX_PANEL);
        p.addStyle("display", "none");
        return p;
    }

}
