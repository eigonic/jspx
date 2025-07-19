package eg.java.net.web.jspx.ui.controls.html.elements.dataitem;

import eg.java.net.web.jspx.engine.ResourceHandler;
import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.ResourceUtility;
import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.engine.util.ui.Render;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.attrbs.Attribute;
import eg.java.net.web.jspx.ui.controls.attrbs.JspxAttribute;
import eg.java.net.web.jspx.ui.controls.html.GenericWebControl;
import eg.java.net.web.jspx.ui.controls.html.elements.HyperLink;
import eg.java.net.web.jspx.ui.controls.html.elements.Label;
import eg.java.net.web.jspx.ui.controls.html.elements.inputs.TextBox;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.IAjaxSubmitter;
import eg.java.net.web.jspx.ui.pages.Page;

import java.text.DecimalFormat;
import java.text.MessageFormat;

public class Footer extends GenericWebControl {

    private static final long serialVersionUID = -7574435699943472884L;
    public static String NavigateEvent = "navigate";
    @JspxAttribute
    protected static String hidePageSize = "hidePageSize";
    @JspxAttribute
    private final String footerMessage = "message";
    @JspxAttribute
    private final String newText = "newcommandtext";
    @JspxAttribute
    private final String prevText = "prevcommandtext";
    @JspxAttribute
    private final String nextText = "nextcommandtext";
    @JspxAttribute
    private final String firstText = "firstcommandtext";
    @JspxAttribute
    private final String lastText = "lastcommandtext";
    @JspxAttribute
    private final String exportToExcelText = "exportToExcelText";
    @JspxAttribute
    private final String hidePaging = "hidePaging";

    public Footer() {
        super(TagFactory.Footer);
    }

    public Footer(Page page) {
        super(TagFactory.Footer, page);
    }

    @JspxAttribute
    public String getMessage() {
        return getAttributeValue(footerMessage);
    }

    public void setMessage(String message) {
        setAttributeValue(footerMessage, message);
    }

    @JspxAttribute
    public String getNewCommandText() {
        return getAttributeValue(newText);
    }

    public void setNewCommandText(String text) {
        setAttributeValue(newText, text);
    }

    public String getPrevCommandText() {
        return getAttributeValue(prevText);
    }

    public void setPrevCommandText(String text) {
        setAttributeValue(prevText, text);
    }

    public String getNextCommandText() {
        return getAttributeValue(nextText);
    }

    public void setNextCommandText(String text) {
        setAttributeValue(nextText, text);
    }

    public String getFirstCommandText() {
        return getAttributeValue(firstText);
    }

    public void setFirstCommandText(String text) {
        setAttributeValue(firstText, text);
    }

    public String getLastCommandText() {
        return getAttributeValue(lastText);
    }

    public void setLastCommandText(String text) {
        setAttributeValue(lastText, text);
    }

    public String getExportToExcelText() {
        return getAttributeValue(exportToExcelText);
    }

    public void setExportToExcelText(String text) {
        setAttributeValue(exportToExcelText, text);
    }

    public boolean getHidePaging() {
        return getAttributeBooleanValue(hidePaging);
    }

    public void setHidePaging(boolean hide) {
        setAttributeBooleanValue(hidePaging, hide);
    }

    public boolean getHidePageSize() {
        return getAttributeBooleanValue(hidePageSize);
    }

    public void setHidePageSize(boolean hidePageSizeVal) {
        setAttributeBooleanValue(hidePageSize, hidePageSizeVal);
    }

    public void render(RenderPrinter outputStream) throws Exception {
        if (!isRendered() || !isAccessible())
            return;
        if (!(parent instanceof Table)) {
            super.render(outputStream);
            return;
        }
        Table parentTable = (Table) parent;
        boolean navigationThrough = parentTable.getTotalPages() == Integer.MAX_VALUE;
        int pageIndexInt = parentTable.getPageIndex();

        renderPageIndex(outputStream, parentTable, navigationThrough);

        IAjaxSubmitter old = parentTable.getMyAjaxSubmitter();
        parentTable.setMyAjaxSubmitter(parentTable);
        // [Dec 4, 2010 4:16:20 PM] [Amr.ElAdawy] [here to render the flickr pagination]
        renderPagination(outputStream, pageIndexInt, parentTable.getTotalPages(), parentTable.hasNext(), parentTable, navigationThrough);
        renderCommandBar(outputStream, parentTable);
        parentTable.setMyAjaxSubmitter(old);
    }

    protected void renderPageIndex(RenderPrinter outputStream, Table parentTable, boolean navigationThrough) throws Exception {
        String message = getMessage();
        if (StringUtility.isNullOrEmpty(message))
            message = "{0} record" + (parentTable.getTotalCount() > 1 ? "s" : "") + " , Page({1}/{2})";
        MessageFormat formatter = new MessageFormat("");
        formatter.applyPattern(message);
        String totalPagesString = navigationThrough ? "...." : new DecimalFormat("###,###,###").format(parentTable.getTotalPages());
        String totalCountString = navigationThrough ? "...." : new DecimalFormat("###,###,###").format(parentTable.getTotalCount());
        String[] params = new String[]
                {totalCountString, String.valueOf(parentTable.getPageIndex()), totalPagesString};
        String output = formatter.format(params);

        Label m = new Label(page);

        m.setText(output);
        m.getStyle().put("display", new Attribute("display", "inline"));
        m.render(outputStream);

        outputStream.write(Render.longSpace);
        // [Jul 18, 2013 2:23:13 PM] [amr.eladawy] [render the pagesize input]

        /*
         * <div class="input-append"> <input class="span2" id="appendedInput" type="text"> <span class="add-on"><span
         * class="caret" style="vertical-align: middle;"></span></span> </div>
         */
        // Render.renderStartTag(outputStream, TagFactory.Div, "input-append", null);
        if (!getHidePageSize()) {
            TextBox pagesize = new TextBox(page);
            pagesize.setId(parentTable.getId() + "_Footer_PageSize");
            pagesize.setValue(String.valueOf(parentTable.getPageSize()));
            pagesize.setNoSystemCss(true);
            pagesize.setInline(true);
            pagesize.setCssClass("input-mini search-query");
            pagesize.setMask("9?999");

            String action = Render.composeAction(parentTable.getId(), "changePageSize", "SRC_VAL", null, true, null, false, false,
                    parentTable.getMySubmitter(), parentTable);
            action = action.replace("'SRC_VAL'", "this.value");
            pagesize.setAttributeValue("onchange", action);
            pagesize.render(outputStream);
        }
    }

    /**
     * renders the new Command
     *
     * @param outputStream
     * @throws Exception
     */
    public void renderNewCommand(RenderPrinter outputStream, Table parentTable) throws Exception {
        if (!parentTable.getNew().equalsIgnoreCase(FALSE)) {

            HyperLink newLink = Render.createImgLink("", ResourceHandler.ResourcePrefix + ResourceUtility.AddDB,
                    StringUtility.isNullOrEmpty(getNewCommandText()) ? "New" : getNewCommandText(), true, null, page);
            newLink.getAttributes().put("data-target", new Attribute("data-target", "#" + parentTable.getId() + Table.NEW_ITEM_PANEL_ID));
            newLink.getAttributes().put("data-", new Attribute("data-toggle", "modal"));
            newLink.render(outputStream);
        }
    }

    protected void renderExportToExcelCommand(RenderPrinter outputStream, Table parentTable) throws Exception {
        int i = 0;
        for (ExportToExcel exportToExcelControl : parentTable.exportToExcelControls) {
            if (exportToExcelControl.isRendered() && exportToExcelControl.isAccessible()) {
                outputStream.write(Render.longSpace);

                Render.renderImgCommand(outputStream, "exportToExcel", String.valueOf(i++), ResourceHandler.ResourcePrefix + ResourceUtility.Excel,
                        StringUtility.isNullOrEmpty(exportToExcelControl.getcommand()) ? "Export To Excel" : exportToExcelControl.getcommand(), null,
                        true, true, null, null, true, page, parentTable.getId(), parentTable);
            }
        }
    }

    protected void renderPagination(RenderPrinter outputStream, int currentPage, long totalPages, boolean hasNext, Table parentTable,
                                    boolean navigationThrough) throws Exception {
        outputStream.write("<br />");
        /**
         * 1. if the current page is less 7 then the start 1 end (the current page) or 7(the last index) 2. if the current page is
         * 7 or more, then the start will be n-3 end will be as above
         *
         *
         */
        if (currentPage > 1 || hasNext) {
            int startPage = 1;
            int endPage = calculatePaginationEnd(currentPage, totalPages, hasNext, navigationThrough);
            if (currentPage > 5) {
                startPage = currentPage - 3;
                if ((endPage - startPage) < 6)
                    startPage = Math.max(1, endPage - 6);
            }

            Render.renderStartTag(outputStream, TagFactory.Span, "pagination", null);

            // [Jul 5, 2012 2:41:12 PM] [Amr.ElAdawy] [<u>]
            Render.renderStartTag(outputStream, TagFactory.UnsortedList, null, null);

            boolean enabled = (currentPage > 1);

            Render.renderStartTag(outputStream, TagFactory.ListItem, null, null);
            Render.renderBootCommand(outputStream, NavigateEvent, "1",
                    StringUtility.isNullOrEmpty(getFirstCommandText()) ? "First" : getFirstCommandText(), null, enabled, true, null, null, false,
                    "icon-fast-backward", true, page, parent.getId(), parent);
            Render.renderCloseTag(outputStream, TagFactory.ListItem);
            Render.renderStartTag(outputStream, TagFactory.ListItem, null, null);
            Render.renderBootCommand(outputStream, NavigateEvent, String.valueOf(currentPage - 1),
                    StringUtility.isNullOrEmpty(getPrevCommandText()) ? "Prev" : getPrevCommandText(), null, enabled, true, null, null, false,
                    "icon-backward", true, page, parent.getId(), parent);
            Render.renderCloseTag(outputStream, TagFactory.ListItem);

            for (int i = startPage; i <= endPage; i++) {
                outputStream.write(Render.startTag);
                outputStream.write(TagFactory.ListItem);
                if (i == currentPage) {
                    outputStream.write(Render.whiteSpace);
                    outputStream.write(Render.CssClass);
                    outputStream.write(Render.equal);
                    outputStream.write(Render.doubleQuotes);
                    outputStream.write("active");
                    outputStream.write(Render.doubleQuotes);

                }

                outputStream.write(Render.endTag);

                if (i == currentPage) {
                    HyperLink a = new HyperLink(page);
                    a.setNavigationURL("#");
                    a.setValue(String.valueOf(i));
                    a.render(outputStream);
                } else
                    Render.renderCommand(outputStream, NavigateEvent, String.valueOf(i), String.valueOf(i), null, true, true, null, null, false, page,
                            parentTable.getId(), parentTable);

                Render.renderCloseTag(outputStream, TagFactory.ListItem);
            }

            enabled = (currentPage < parentTable.getTotalPages() && parentTable.hasNext());
            Render.renderStartTag(outputStream, TagFactory.ListItem, null, null);
            Render.renderReversedBootCommand(outputStream, NavigateEvent, String.valueOf(currentPage + 1),
                    StringUtility.isNullOrEmpty(getNextCommandText()) ? "Next" : getNextCommandText(), null, enabled, true, null, null, false,
                    "icon-forward", true, page, parent.getId(), parent);
            Render.renderCloseTag(outputStream, TagFactory.ListItem);
            Render.renderStartTag(outputStream, TagFactory.ListItem, null, null);
            if (!navigationThrough) {
                Render.renderReversedBootCommand(outputStream, NavigateEvent, String.valueOf(parentTable.getTotalPages()),
                        StringUtility.isNullOrEmpty(getLastCommandText()) ? "Last" : getLastCommandText(), null, enabled, true, null, null, false,
                        "icon-fast-forward", true, page, parent.getId(), parent);
            }
            Render.renderCloseTag(outputStream, TagFactory.ListItem);
            Render.renderCloseTag(outputStream, TagFactory.UnsortedList);

            Render.renderCloseTag(outputStream, TagFactory.Span);
        }

    }

    protected void renderCommandBar(RenderPrinter outputStream, Table parentTable) throws Exception {
        Render.renderStartTag(outputStream, TagFactory.Div, null, null);

        renderNewCommand(outputStream, parentTable);
        renderExportToExcelCommand(outputStream, parentTable);
        Render.renderCloseTag(outputStream, TagFactory.Div);
    }

    protected int calculatePaginationEnd(int currentPage, Long totalPages, boolean hasNext, boolean navigationThrough) {
        if (!hasNext || navigationThrough)
            return currentPage;
        else {
            if (currentPage < 6)
                return Math.min(totalPages.intValue(), 7);
            else
                return Math.min(totalPages.intValue(), currentPage + 3);
        }
    }
}
