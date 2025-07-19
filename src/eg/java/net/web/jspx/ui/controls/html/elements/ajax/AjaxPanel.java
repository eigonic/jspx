package eg.java.net.web.jspx.ui.controls.html.elements.ajax;

import eg.java.net.web.jspx.engine.error.JspxException;
import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.WebControl;
import eg.java.net.web.jspx.ui.controls.attrbs.Attribute;
import eg.java.net.web.jspx.ui.controls.attrbs.JspxAttribute;
import eg.java.net.web.jspx.ui.controls.html.GenericWebControl;
import eg.java.net.web.jspx.ui.controls.html.elements.Form;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.IAjaxSubmitter;
import eg.java.net.web.jspx.ui.controls.html.w3.Head;
import eg.java.net.web.jspx.ui.controls.html.w3.Script;
import eg.java.net.web.jspx.ui.pages.Page;

/**
 * Ajax update panel
 *
 * @author amr.eladawy
 */
public class AjaxPanel extends GenericWebControl implements IAjaxSubmitter {
    /**
     *
     */
    private static final long serialVersionUID = -7382499735414987532L;
    @JspxAttribute
    protected static String submitOnceKey = "submitonce";
    @JspxAttribute
    protected static String refreshTimeKey = "refreshtime";
    @JspxAttribute
    protected static String onRefreshKey = "onrefresh";
    @JspxAttribute
    protected static String onRenderKey = "onrender";
    @JspxAttribute
    protected static String commandArgs = "commandargs";
    @JspxAttribute
    protected static String action = "action";
    @JspxAttribute
    protected static String GroupKey = "group";
    public AjaxPanel() {
        super(TagFactory.AjaxPanel);
    }
    public AjaxPanel(Page page) {
        super(TagFactory.AjaxPanel, page);
    }

    @Override
    public String getTageName() {
        return TagFactory.Span;
    }

    @Override
    protected void postRenderStartTag(RenderPrinter outputStream) throws Exception {
        // this is span, it should be rendered as a content <span></span> even there are no controls within.
        isContentControl = true;
    }

    @Override
    protected void loadInternalAttributes() {
        super.loadInternalAttributes();
        internalAttribtes.put(refreshTimeKey, 0);
        internalAttribtes.put(onRefreshKey, 0);
        internalAttribtes.put(submitOnceKey, 0);
        internalAttribtes.put(onRenderKey, 0);
        internalAttribtes.put(commandArgs, 0);
    }

    /**
     * overridden to provide html document for the Multipart form frame.
     */
    @Override
    public void renderChildren(RenderPrinter outputStream) throws Exception {
        boolean multipart = mySubmitter instanceof Form && ((Form) mySubmitter).getEnctype().equalsIgnoreCase(Form.MultiPArtForm);
        boolean renderRefreshtime = getRefreshTime() > 0;
        if (multipart) {
            // Create frame for the form submitting.
            GenericWebControl frame = new GenericWebControl(TagFactory.Frame, page);
            frame.setId(getId() + "_uploadFrame");
            frame.setStyle("width:0;height:0;border:0px none");
            frame.addAttribute(new Attribute("src", "about:blank"));
            frame.addAttribute(new Attribute("data-label", "ajaxPanel"));
            frame.render(outputStream);
        }
        if (multipart && page.isPostBack) {
            outputStream.write("<html>");
            new Head(page).render(outputStream);
            outputStream.write("<body>");

            outputStream.write("<span id=\"");
            outputStream.write(getId());
            outputStream.write("\" data-label=\"ajaxPanel\">");
        }
        if (!page.isPostBack && renderRefreshtime) {
            Script script = new Script(page);
            String code = "$jspx(document).ready(function() {setTimeout(\"" + getAutoRefreshScript() + "\"," +
                    getRefreshTime() + ");});";
            script.setScriptCode(code);
            script.render(outputStream);
        }
        outputStream.write("<span id=\"");
        outputStream.write(getId());
        outputStream.write("_content\">");
        super.renderChildren(outputStream);
        outputStream.write("</span>");
        // [Nov 5, 2013 2:43:26 PM] [Amr.ElAdawy] [render these scripts only if this is active ajax panel]
        if (multipart && page.isPostBack && page.getActiveAjaxSubmitter() != null
                && ((WebControl) page.getActiveAjaxSubmitter()).getId().equals(getId())) {
            outputStream.write("</span><script type=\"text/javascript\" language=\"javascript\">");
            if (renderRefreshtime) {
                outputStream.write("window.parent.");
                outputStream.write(getAutoRefreshScript());
            }
            if (page.isSendRedirect) {
                outputStream.write("window.parent.document.location='");
                outputStream.write(page.newLocation);
                outputStream.write("';");
            } else if (page.isSendDispatch) {
                outputStream.write("window.parent.document.body.innerHTML=document.getElementById('");
                outputStream.write(getId());
                outputStream.write("').innerHTML;");
            } else {
                // hiding the loading panel
                outputStream.write("var loading = window.parent.document.getElementById('");
                outputStream.write(getId());
                outputStream.write("AjaxLoading');\r\n if (loading!=null) loading.style.display='none';\r\n");
                // loading viewstate
                outputStream.write(" var vsElement=  window.parent.document.getElementById('_ViewState'); \r\n if(vsElement) vsElement.value='");
                outputStream.write(page.getViewStateString());
                outputStream.write("';");

                outputStream.write("jQuery( function($) {$(document).ready(function (){");
                outputStream.write("window.parent.document.getElementById('");
                outputStream.write(getId());
                outputStream.write("_content').innerHTML=document.getElementById('");
                outputStream.write(getId());
                outputStream.write("_content').innerHTML;");
                outputStream.write("});");
                outputStream.write("});");

                if (!StringUtility.isNullOrEmpty(getOnRender())) {
                    String[] parts = getOnRender().split(";");
                    for (int i = 0; i < parts.length; i++) {
                        outputStream.write("window.parent.");
                        outputStream.write(parts[i]);
                        outputStream.write(";");

                    }
                }
            }
            outputStream.write("window.parent.resetButtons();");
            outputStream.write("window.parent.JSPX_AJAX_ON_RENDER();");
            // outputStream.write("window.parent.initiateButtons();");

            outputStream.write("</script>");

            page.renderOnloadScripts(outputStream);

            outputStream.write("</body></html>");
        }

    }

    public int getRefreshTime() {
        return getAttributeIntValue(refreshTimeKey);
    }

    public void setRefreshTime(int refreshTime) {
        setAttributeIntValue(refreshTimeKey, refreshTime);
    }

    public String getOnRefresh() {
        return getAttributeValue(onRefreshKey);
    }

    public void setOnRefresh(String onRefresh) {
        setAttributeValue(onRefreshKey, onRefresh);
    }

    public String getOnRender() {
        return getAttributeValue(onRenderKey);
    }

    public void setOnRender(String onRender) {
        setAttributeValue(onRenderKey, onRender);
    }

    public String getAutoRefreshScript() {
        int refreshtime = getRefreshTime();
        if (refreshtime > 0) {
            if (getMySubmitter() == null)
                throw new JspxException("Ajax Panel [" + this + "] has no Form around it");

            StringBuilder builder = new StringBuilder();

            builder.append("refreshPanel(").append(refreshtime).append(",'").append(getOnRefresh()).append("','")
                    .append(((WebControl) getMySubmitter()).getId()).append("','").append(getId());
            if (!StringUtility.isNullOrEmpty(getCommandArgs()))
                builder.append("','").append(getCommandArgs());
            else
                builder.append("','");

            if (!StringUtility.isNullOrEmpty(getGroup()))
                builder.append("','").append(getGroup());
            builder.append("');");
            return builder.toString();
        }
        return "";
    }

    public String getGroup() {
        return getAttributeValue(GroupKey);
    }

    public void setGroup(String group) {
        setAttributeValue(GroupKey, group);
    }

    public String getCommandArgs() {
        return getAttributeValue(commandArgs);
    }

    public void setCommandArgs(String commandArgsValue) {
        setAttributeValue(commandArgs, commandArgsValue);
    }

    public String getAjaxId() {
        return getId();
    }

    /**
     * @return the action
     */
    public String getAction() {
        return getAttributeValue(action);
    }

    /**
     * @param action the action to set
     */
    public void setAction(String actionValue) {
        setAttributeValue(action, actionValue);
    }

    /**
     * overridden to change the src attribute by adding the absolute url.
     */
    @Override
    protected void renderAttributes(RenderPrinter outputStream) throws Exception {
        setAction(composeURL(getAction()));
        super.renderAttributes(outputStream);
    }
}
