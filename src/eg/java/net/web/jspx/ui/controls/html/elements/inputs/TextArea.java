/**
 *
 */
package eg.java.net.web.jspx.ui.controls.html.elements.inputs;

import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.engine.util.ui.Render;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.WebControl;
import eg.java.net.web.jspx.ui.controls.html.GenericWebControl;
import eg.java.net.web.jspx.ui.controls.html.StringLiteral;
import eg.java.net.web.jspx.ui.controls.html.elements.Input;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.IAjaxSubmitter;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.ISubmitter;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.ValueHolder;
import eg.java.net.web.jspx.ui.pages.Page;

import java.util.ArrayList;

/**
 * @author basma.moukhtar
 *
 */
public class TextArea extends GenericWebControl implements ValueHolder {
    /**
     *
     */
    private static final long serialVersionUID = 1145133221602803039L;
    private final String readonlyKey = "readonly";
    private final String rowsKey = "rows";
    private final String colsKey = "cols";
    private String valueBinding;

    public TextArea() {
        super(TagFactory.TextArea);

    }

    public TextArea(Page page) {
        super(TagFactory.TextArea, page);

    }

    @Override
    public void render(RenderPrinter outputStream) throws Exception {

        if (!isRendered() || !isAccessible())
            return;
        rebindValueHolderControl();
        outputStream.write(Render.startTag);
        outputStream.write(getTageName());

        renderAttributes(outputStream);
        outputStream.write(Render.whiteSpace);
        renderStyle(outputStream);
        outputStream.write(Render.whiteSpace);
        outputStream.write(Render.endTag);
        postRenderStartTag(outputStream);
        String text = getText();
        if (!StringUtility.isNullOrEmpty(text))
            outputStream.write(getText());
        preRenderEndTag(outputStream);
        outputStream.write(Render.startEndTag);
        outputStream.write(getTageName());
        outputStream.write(Render.endTag);
        outputStream.write(Render.newLine);
    }

    public int getRows() {
        return getAttributeIntValue(rowsKey);
    }

    public void setRows(int rows) {
        setAttributeIntValue(rowsKey, rows);
    }

    public int getCols() {
        return getAttributeIntValue(colsKey);
    }

    public void setCols(int cols) {
        setAttributeIntValue(colsKey, cols);
    }

    public void setReadonly(boolean readonly) {
        if (readonly)
            setAttributeValue(readonlyKey, readonlyKey);
        else
            attributes.remove(readonlyKey);
    }

    public boolean isChecked() {
        return attributes.contains(readonlyKey);
    }

    public String getValue() {
        return getText();
    }

    public void setValue(String valueString) {
        if (page != null)
            setText(valueString);
    }

    private String getText() {
        return getContentControl().getText();
    }

    private void setText(String text) {
        getContentControl().setText(text);
        if (page != null && page.PageStatus != Page.PageViewState && page.PageStatus != Page.PageCompose)
            viewstate.put(content, text);
    }

    protected StringLiteral getContentControl() {
        if (controls.size() > 0) {
            // [Jun 23, 2011 3:31:31 PM] [amr.eladawy] [now the the text area may contain white spaces controls, neglect them]
            for (WebControl control : controls)
                if (control instanceof StringLiteral)
                    return ((StringLiteral) control);
        }
        StringLiteral literal = new StringLiteral("");
        literal.setPage(page);
        addControl(literal);
        return literal;
    }

    public String getValueBinding() {
        return Input.calculateValueBinding(valueBinding, getAttribute(Input.value));
    }

    public void setValueBinding(String valueBinding) {
        this.valueBinding = valueBinding;
    }

    /* (non-Javadoc)
     * @see eg.java.net.web.jspx.ui.controls.html.GenericWebControl#clone(eg.java.net.web.jspx.ui.controls.WebControl, eg.java.net.web.jspx.ui.pages.Page, eg.java.net.web.jspx.ui.controls.html.elements.markers.ISubmitter, eg.java.net.web.jspx.ui.controls.html.elements.markers.IAjaxSubmitter)
     */
    @Override
    public WebControl clone(WebControl parent, Page page, ISubmitter submitter, IAjaxSubmitter ajaxSubmitter) {
        TextArea newControl = (TextArea) super.clone(parent, page, submitter, ajaxSubmitter);
        // [Jun 16, 2012 2:22:32 PM] [amr.eladawy] [merge all the String literal controls inside one control]
        newControl.setControls(new ArrayList<WebControl>());
        if (StringUtility.isNullOrEmpty(getValue())) {
            StringBuilder sb = new StringBuilder();
            for (WebControl control : newControl.getControls()) {
                if (control instanceof StringLiteral)
                    sb.append(((StringLiteral) control).getText());
            }
            newControl.getControls().add(new StringLiteral(sb.toString()));
        } else
            newControl.setValue(getValue());
        return newControl;
    }
}
