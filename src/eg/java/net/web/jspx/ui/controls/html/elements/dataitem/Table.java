package eg.java.net.web.jspx.ui.controls.html.elements.dataitem;

import eg.java.net.web.jspx.engine.ResourceHandler;
import eg.java.net.web.jspx.engine.data.DAO;
import eg.java.net.web.jspx.engine.data.DataField;
import eg.java.net.web.jspx.engine.data.ObjectToCSV;
import eg.java.net.web.jspx.engine.data.ObjectToExcel;
import eg.java.net.web.jspx.engine.message.JspxMessage;
import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.ELUtility;
import eg.java.net.web.jspx.engine.util.PageCarriage;
import eg.java.net.web.jspx.engine.util.ResourceUtility;
import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.engine.util.ui.Render;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.WebControl;
import eg.java.net.web.jspx.ui.controls.attrbs.Attribute;
import eg.java.net.web.jspx.ui.controls.attrbs.JspxAttribute;
import eg.java.net.web.jspx.ui.controls.html.elements.Label;
import eg.java.net.web.jspx.ui.controls.html.elements.hidden.HiddenGenericWebControl;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.IAjaxSubmitter;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.ISubmitter;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.InternalEventsListener;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.InternalValueHolder;
import eg.java.net.web.jspx.ui.controls.html.w3.Script;
import eg.java.net.web.jspx.ui.pages.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * parent for the DataTable and ListTable.
 *
 * @author amr.eladawy
 */
public abstract class Table extends HiddenGenericWebControl implements InternalValueHolder, InternalEventsListener, IAjaxSubmitter {
    public static final String NEW_ITEM_PANEL_ID = "_DetailedPanel";
    public static final String DELETE_EVENT = "delete";
    protected static final int DeafultPageSize = 20;
    private static final long serialVersionUID = 1846429421963113772L;
    private static final Logger logger = LoggerFactory.getLogger(Table.class);
    public static String SAVE_EVENT = "add";
    public static String UPDATE_EVENT = "update";
    public static String CANCEL_EVENT = "cancelItem";
    public static String EditState = "1";
    @JspxAttribute
    protected static String saveLabel = "savecommandtext";
    @JspxAttribute
    protected static String updateLabel = "updatecommandtext";
    @JspxAttribute
    protected static String cancelLabel = "cancelcommandtext";
    @JspxAttribute
    protected static String requiredtext = "requiredtext";
    protected static String ViewState = "0";
    protected static String NewState = "2";
    protected static String DeleteState = "3";
    protected static String DoneState = "4";
    protected static String pkSplitter = "_";
    @JspxAttribute
    protected static String pageSize = "pagesize";
    @JspxAttribute
    protected static String pageIndexKey = "pageindex";
    @JspxAttribute
    protected static String selectedRowStyle = "selectedrowstyle";
    @JspxAttribute
    protected static String showRowIndex = "showrowindex";
    @JspxAttribute
    protected static String startRowIndex = "startrowindex";
    @JspxAttribute
    protected static String hoverClass = "hoverclass";
    @JspxAttribute
    protected static String onError = "onError";
    @JspxAttribute
    protected static String onDelete = "ondelete";
    @JspxAttribute
    protected static String onEdit = "onedit";
    @JspxAttribute
    protected static String onInsert = "oninsert";
    @JspxAttribute
    protected static String noResultMessage = "noresults";
    @JspxAttribute
    protected static String noResultsClass = "noresultsclass";
    @JspxAttribute
    protected static String headerClass = "headerclass";
    @JspxAttribute
    protected static String headerStyle = "headerstyle";
    @JspxAttribute
    protected static String tdClass = "tdclass";
    @JspxAttribute
    protected static String rowStyle = "rowstyle";
    @JspxAttribute
    protected static String rowClass = "rowclass";
    @JspxAttribute
    protected static String dateformatKey = "dateformat";
    @JspxAttribute
    protected static String selectedRowClass = "selectedrowclass";
    @JspxAttribute
    protected static String panelStatus = "status";
    @JspxAttribute
    protected static String showNew = "new";
    @JspxAttribute
    protected static String autoBindKey = "autobind";
    @JspxAttribute
    protected static String var = "var";
    @JspxAttribute
    protected static String onRenderKey = "onrender";
    @JspxAttribute
    protected static String sortingInfo = "sortingInfo";
    protected boolean dataRetrieved = false;
    protected Footer footer = new Footer();
    protected EditTemplate editTemplate;
    protected Integer totalCount;
    protected List<DataParam> parameters = new ArrayList<DataParam>();
    protected List<DataColumn> columns = new ArrayList<DataColumn>();
    protected List<ExportToExcel> exportToExcelControls = new ArrayList<ExportToExcel>();
    protected List<DataPK> pks = new ArrayList<DataPK>();
    protected Hashtable<String, DataLookup> lookups = new Hashtable<String, DataLookup>();
    protected boolean bound = false;
    protected boolean canDelete = false;
    protected boolean canEdit = false;
    /**
     * the datarow decalred in the table
     */
    protected DataRow dataRow;
    protected List<?> rows = new ArrayList<Object>();
    protected int totalPages;
    protected int selectedIndex = 0;
    protected Hashtable<String, String> postedInternalValues;
    protected String message = "";
    /**
     * when protected static String the name of column to be used for sorting.
     */
    protected DataColumn sortingColumn;
    protected boolean organized = false;
    protected Object row = null;
    protected TableEventListener eventListener = null;
    protected boolean nextPage;
    protected boolean internalPostback;
    @JspxAttribute
    protected String caseSensitiveKey = "casesensitive";

    public Table(String tagName) {
        super(tagName);
    }

    public Table(String tagName, Page page) {
        super(tagName, page);
    }

    /**
     * gets the label of the save button that appears in the Editpanel
     *
     * @return
     */
    public String getSaveLabel() {
        String s = getAttributeValue(saveLabel);
        return StringUtility.isNullOrEmpty(s) ? "Save" : s;
    }

    /**
     * sets the label of the save button that appears in the Editpanel
     *
     * @return
     */
    public void setSaveLabel(String saveLabel1) {
        setAttributeValue(saveLabel, saveLabel1);
    }

    /**
     * gets the label of the update button that appears in the Editpanel
     *
     * @return
     */
    public String getUpdateLabel() {
        String s = getAttributeValue(updateLabel);
        return StringUtility.isNullOrEmpty(s) ? "Update" : s;
    }

    /**
     * sets the label of the update button that appears in the Editpanel
     *
     * @return
     */
    public void setUpdateLabel(String updateLabel1) {
        setAttributeValue(updateLabel, updateLabel1);
    }

    /**
     * gets the label of the cancel button that appears in the Editpanel
     *
     * @return
     */
    public String getCancelLabel() {
        String s = getAttributeValue(cancelLabel);
        return StringUtility.isNullOrEmpty(s) ? "Cancel" : s;
    }

    /**
     * sets the label of the cancel button that appears in the Editpanel
     *
     * @return
     */
    public void setCancelLabel(String cancelLabel1) {
        setAttributeValue(cancelLabel, cancelLabel1);
    }

    public String getRequiredtext() {
        String s = getAttributeValue(requiredtext);
        return StringUtility.isNullOrEmpty(s) ? "The field {0} is required" : s;
    }

    public void setRequiredtext(String requiredtextValue) {
        setAttributeValue(requiredtext, requiredtextValue);
    }

    public String getOnError() {
        return getAttributeValue(onError);
    }

    public void setOnError(String onErrorVal) {
        setAttributeValue(onError, onErrorVal);
    }

    public Hashtable<String, DataLookup> getLookups() {
        return lookups;
    }

    @Override
    protected void loadInternalAttributes() {
        super.loadInternalAttributes();
        internalAttribtes.put(selectedRowClass.toLowerCase(), 0);
        internalAttribtes.put(selectedRowStyle.toLowerCase(), 0);
        internalAttribtes.put(autoBindKey.toLowerCase(), 0);
        internalAttribtes.put(showNew.toLowerCase(), 0);
        internalAttribtes.put(panelStatus.toLowerCase(), 0);
        internalAttribtes.put(rowClass.toLowerCase(), 0);
        internalAttribtes.put(rowStyle.toLowerCase(), 0);
        internalAttribtes.put(headerStyle.toLowerCase(), 0);
        internalAttribtes.put(headerClass.toLowerCase(), 0);
        internalAttribtes.put(noResultMessage.toLowerCase(), 0);
        internalAttribtes.put(pageSize.toLowerCase(), 0);
        internalAttribtes.put(pageIndexKey.toLowerCase(), 0);
        internalAttribtes.put(tdClass.toLowerCase(), 0);
        internalAttribtes.put(dateformatKey.toLowerCase(), 0);
        internalAttribtes.put(autoBindKey.toLowerCase(), 0);
        internalAttribtes.put(rendered.toLowerCase(), 0);
        internalAttribtes.put(showRowIndex.toLowerCase(), 0);
        internalAttribtes.put(startRowIndex.toLowerCase(), 0);

        internalAttribtes.put(saveLabel.toLowerCase(), 0);
        internalAttribtes.put(updateLabel.toLowerCase(), 0);
        internalAttribtes.put(cancelLabel.toLowerCase(), 0);
        internalAttribtes.put(onInsert.toLowerCase(), 0);
        internalAttribtes.put(onEdit.toLowerCase(), 0);
        internalAttribtes.put(onDelete.toLowerCase(), 0);
        internalAttribtes.put(var.toLowerCase(), 0);

    }

    public String getOnDelete() {
        return getAttributeValue(onDelete);
    }

    public void setOnDelete(String onClientDelete) {
        setAttributeValue(onDelete, onClientDelete);
    }

    public String getOnEdit() {
        return getAttributeValue(onEdit);
    }

    public void setOnEdit(String onClientDelete) {
        setAttributeValue(onEdit, onClientDelete);
    }

    public String getOnInsert() {
        return getAttributeValue(onInsert);
    }

    public void setOnInsert(String onClientClick) {
        setAttributeValue(onInsert, onClientClick);
    }

    /**
     * returns all the rows from this table
     *
     * @return the rows
     */
    public List<?> getRows() {
        return rows;
    }

    /**
     * overridden to add the child control to the corresponding collection for example if dataparam --> params. datacolumn
     * colmuns.
     */
    @Override
    public void addControl(WebControl control) {
        super.addControl(control);
        if (control instanceof DataColumn)
            columns.add((DataColumn) control);
        else if (control instanceof ExportToExcel)
            exportToExcelControls.add((ExportToExcel) control);
    }

    /*
     * overridden to clone members that are not attributes.
     */
    @Override
    public WebControl clone(WebControl parent, Page page, ISubmitter submitter, IAjaxSubmitter ajaxSubmitter) {
        Table table = (Table) super.clone(parent, page, submitter, ajaxSubmitter);
        for (WebControl control : table.controls) {
            if (control instanceof DataParam)
                table.parameters.add((DataParam) control);
            else if (control instanceof DataColumn)
                table.columns.add((DataColumn) control);
            else if (control instanceof DataPK)
                table.pks.add((DataPK) control);
            else if (control instanceof Footer)
                table.footer = (Footer) control;
            else if (control instanceof DataLookup)
                table.lookups.put(control.getId(), (DataLookup) control);
            else if (control instanceof ExportToExcel)
                table.exportToExcelControls.add((ExportToExcel) control);
            else if (control instanceof EditTemplate)
                table.editTemplate = (EditTemplate) control;
            else if (control instanceof DataRow)
                table.dataRow = (DataRow) control;
        }
        return table;
    }

    /**
     * invokes the binding operation.
     */
    public abstract void dataBind();

    /**
     * @param outputStream
     * @throws Exception
     */
    protected void renderTableFooter(RenderPrinter outputStream) throws Exception {
        int colCount = columns.size();

        if (getShowRowIndex())
            colCount++;
        // Render.renderIndent(outputStream, indentLevel);

        outputStream.write(Render.startTag);
        outputStream.write(TagFactory.TableFooter);
        outputStream.write(Render.endTag);
        outputStream.write(Render.startTag);
        outputStream.write(TagFactory.TableRow);
        String style = null;
        String cssClass = null;
        if (StringUtility.isNullOrEmpty(footer.getCssClass())) {
            style = getAttribute(headerStyle).getValue();
            cssClass = getAttribute(headerClass).getValue();
        } else {
            style = footer.getStyleString();
            cssClass = footer.getCssClass();
        }

        outputStream.write(" style=\"");
        outputStream.write(style);
        outputStream.write("\"");

        outputStream.write(" class=\"");
        outputStream.write(cssClass);
        outputStream.write("\"");
        outputStream.write(Render.endTag);

        outputStream.write(Render.startTag);
        outputStream.write(TagFactory.TableColumn);

        outputStream.write(" style=\"");
        outputStream.write(style);
        outputStream.write("; text-align:center;");
        outputStream.write("\"");

        outputStream.write(" class=\"");
        outputStream.write(cssClass);
        outputStream.write("\"");
        outputStream.write(" colspan=\"");
        outputStream.write(String.valueOf(colCount));
        outputStream.write("\"");
        outputStream.write(Render.endTag);

        footer.render(outputStream);

        Render.renderTagEnd(outputStream, TagFactory.TableColumn);
        Render.renderTagEnd(outputStream, TagFactory.TableRow);
        Render.renderTagEnd(outputStream, TagFactory.TableFooter);

        // indentLevel--;
    }

    /**
     * @param outputStream
     * @throws Exception
     */
    protected void renderTableHeader(RenderPrinter outputStream) throws Exception {
        // Render.renderIndent(outputStream, indentLevel++);

        // Render.renderIndent(outputStream, indentLevel);

        // Header*
        // [Mar 15, 2012 5:12:50 PM] [amr.eladawy] [add Thead]
        outputStream.write(Render.startTag);
        outputStream.write(TagFactory.TableHeader);
        outputStream.write(" class=\" alert-info\"");
        outputStream.write(Render.endTag);
        outputStream.write(Render.newLine);
        // [Mar 15, 2012 5:13:43 PM] [amr.eladawy] [render tr]
        outputStream.write(Render.startTag);
        outputStream.write(TagFactory.TableRow);
        getAttribute(headerStyle).renderCustomKey("style", outputStream, page);
        Attribute headClass = getAttribute(headerClass);
        headClass.renderCustomKey("class", outputStream, page);
        outputStream.write(Render.endTag);

        outputStream.write(Render.newLine);
        if (getShowRowIndex()) {
            Render.renderColumnStart(outputStream, headClass, TagFactory.TableHead, page);
            outputStream.write("#");
            Render.renderTagEnd(outputStream, TagFactory.TableHead);
        }
        for (DataColumn column : columns) {
            if (!column.isRendered() || !column.isAccessible())
                continue;
            // Render.renderIndent(outputStream, ++indentLevel);
            column.renderHeader(outputStream, headClass);
            // indentLevel--;
        }

        // Render.renderIndent(outputStream, indentLevel);
        Render.renderRowEnd(outputStream);
        outputStream.write(Render.startEndTag);
        outputStream.write(TagFactory.TableHeader);
        outputStream.write(Render.endTag);
    }

    @Override
    public void render(RenderPrinter outputStream) throws Exception {
        if (!isRendered() || !isAccessible())
            return;

        // render DIV before the actual table.
        outputStream.write(Render.startTag);
        outputStream.write(TagFactory.Div);
        // renderAttributes(outputStream);
        outputStream.write(" id=\"");
        outputStream.write(getAjaxId());
        outputStream.write("\"");
        outputStream.write(Render.endTag);
        outputStream.write(Render.newLine);
        // ////
        renderChildren(outputStream);
        // ///////
        outputStream.write(Render.startEndTag);
        outputStream.write(TagFactory.Div);
        outputStream.write(Render.endTag);
        outputStream.write(Render.newLine);

        if (getStatus().equals(EditState) && editTemplate != null) {
            editTemplate.setRow(getRowToEdit());
            editTemplate.render(outputStream);
            editTemplate.setVar(getVar());
        }

    }

    private void renderHoverScript(RenderPrinter outputStream) throws Exception {
        Script script = new Script(page);
        String code = getHoverScript();
        script.setScriptCode(code);
        script.render(outputStream);
    }

    protected void renderDetailedPanelScript(RenderPrinter outputStream, String action) throws Exception {
        Script script = new Script(page);
        /***
         * ,close: function(event, ui) {" + Render.composeAction(getId(), Table.CANCEL_EVENT, "", null, true, "", false, true,
         * mySubmitter, this).replace("javascript:", "") + "
         */

        String code = "jQuery( function($){$(\"#" + getId() + NEW_ITEM_PANEL_ID + "\").dialog({autoOpen: false,width: 460,modal: true" + action
                + "})});";
        script.setScriptCode(code);
        script.render(outputStream);
    }

    protected void renderDetailedPanelCloseScript(RenderPrinter outputStream) throws Exception {
        Script script = new Script(page);
        String code = "loadJQueryIfNot('" + composeURL(ResourceHandler.ResourcePrefix + ResourceUtility.JQueryScript) + "');jQuery( function($){$(\"#"
                + getId() + NEW_ITEM_PANEL_ID + "\").dialog('close');});";
        script.setScriptCode(code);
        script.render(outputStream);
    }

    private String getHoverScript() {
        String hoverClass = getHoverClass();
        if (StringUtility.isNullOrEmpty(hoverClass))
            hoverClass = "jspxTableHover";
        String code = "jQuery( function($) {$('#" + getId() + "').tableHover({rowClass:'" + hoverClass +
                "',colClass:''});});";
        return code;
    }

    protected abstract void renderNewItemPanel(RenderPrinter outputStream) throws Exception;

    protected void addErrorMessage(RenderPrinter outputStream) {
        // [May 31, 2012 3:31:34 PM] [amr.eladawy] [adding the message as jspx message]
        if (!StringUtility.isNullOrEmpty(message))
            page.addMessage(new JspxMessage(message, "Error", JspxMessage.ERROR, JspxMessage.FOR_EVER));
    }

    public abstract Object getSelectedRow(String index);

    /**
     * gets list of the selected rows.
     *
     * @return
     */
    public abstract List<Object> getSelectedRows();

    /**
     * gets the currently row under editing
     *
     * @return
     */
    protected abstract Object getRowToEdit();

    /**
     * Indicates whether the Select All is checked
     *
     * @return
     */
    public boolean isSelectedAll() {
        // [Nov 18, 2009 12:16:44 PM] [Amr.ElAdawy] [to indicate the user has selected all, then it has to be the
        // checkbox in the header checked and the type of selection is set to select not check]
        String value = postedInternalValues.get(getId() + nameSplitter + DataColumnCommand.SelectAll);
        boolean isSelectAllChecked = (value != null);
        boolean isSelectTypeCheck = false;
        for (DataColumn column : columns)
            if (column instanceof DataColumnCommand && column.getType().equalsIgnoreCase(DataColumnCommand.Check)) {
                isSelectTypeCheck = "check".equalsIgnoreCase(((DataColumnCommand) column).getSelectType());
                break;
            }
        return isSelectAllChecked && !isSelectTypeCheck;
    }

    /**
     * gets the list of selected indices.
     */
    public List<Integer> getSelectedIndices() {
        List<Integer> indices = new ArrayList<Integer>();
        String index = "";
        String value = "";
        for (String key : postedInternalValues.keySet()) {
            index = key.substring(key.indexOf(nameSplitter) + nameSplitter.length());
            if (index.startsWith(DataColumnCommand.Check)) {
                index = index.substring(DataColumnCommand.Check.length());
                value = postedInternalValues.get(key);
                if (value != null)
                    indices.add(Integer.parseInt(index));
            }
        }
        Collections.sort(indices);
        return indices;
    }

    protected List<DataColumn> getGroupByColumn() {
        List<DataColumn> groupByCols = new ArrayList<DataColumn>();
        for (DataColumn col : columns) {
            if (col.getGroupBy())
                groupByCols.add(col);
        }
        return groupByCols;
    }

    protected Hashtable<DataColumn, Double> getTotalColumn() {
        Hashtable<DataColumn, Double> groupByCols = new Hashtable<DataColumn, Double>();
        for (DataColumn col : columns) {
            if (col.getTotal())
                groupByCols.put(col, 0d);
        }
        return groupByCols;
    }

    public boolean isOrganized() {
        return organized;
    }

    public void setOrganized(boolean organized) {
        this.organized = organized;
    }

    /**
     * @param outputStream
     * @return
     * @throws Exception
     */
    protected void renderTableRows(RenderPrinter outputStream) throws Exception {

        outputStream.write(Render.startTag);
        outputStream.write(TagFactory.TableBody);
        outputStream.write(Render.endTag);
        outputStream.write(Render.newLine);

        List<DataColumn> groupByCols = getGroupByColumn();
        if (groupByCols.size() > 0 && !isOrganized()) {
            rows = reOrganizeRows(groupByCols);
            setOrganized(true);
        }

        for (int i = 0; i < rows.size(); i++) {
            renderOneRow(outputStream, i);
        }
        // indentLevel--;
        outputStream.write(Render.newLine);

        outputStream.write(Render.startEndTag);
        outputStream.write(TagFactory.TableBody);
        outputStream.write(Render.endTag);
    }

    private void renderOneRow(RenderPrinter outputStream, int i) throws Exception {
        boolean odd = i % 2 != 0;
        // row = rows.get(i);if (getShowRowIndex())
        row = getRowToRender(i);

        // [Jul 16, 2013 1:08:45 PM] [Amr.ElAdawy] [adding the repeater object to the jexl context]
        ELUtility.addObjectToJexlMap(page, getVar(), row);
        // [Aug 5, 2013 11:07:06 AM] [Amr.ElAdawy] [check if the datarow to be rendered or not]
        if (dataRow == null || (dataRow.isRendered() && dataRow.isAccessible())) {
            outputStream.write(Render.newLine);
            // Render.renderIndent(outputStream, indentLevel);
            renderRowStart(outputStream, i);

            renderOneRowColumns(outputStream, i, odd);
            outputStream.write(Render.newLine);
            // Render.renderIndent(outputStream, indentLevel);
            Render.renderRowEnd(outputStream);
        }
    }

    private void renderOneRowColumns(RenderPrinter outputStream, int i, boolean odd) throws Exception {
        if (getShowRowIndex()) {
            renderColumnStart(outputStream, odd, null);
            outputStream.write(String.valueOf(i + getStartRowIndex() + ((getPageIndex() - 1) * getPageSize())));
            Render.renderTagEnd(outputStream, TagFactory.TableColumn);
        }
        for (DataColumn column : columns) {
            if (!column.isRendered() || !column.isAccessible())
                continue;
            outputStream.write(Render.newLine);
            // Render.renderIndent(outputStream, ++indentLevel);
            renderColumnStart(outputStream, odd, column);
            if (row instanceof Hashtable<?, ?>)
                column.setEventArgs(composeEventArgs((Hashtable<String, DataField>) row));
            column.setRowIndex(i);
            column.setRow(getRowToRender(i));
            column.render(outputStream);
            Render.renderTagEnd(outputStream, TagFactory.TableColumn);
            // indentLevel--;
        }
    }

    protected List<Object> reOrganizeRows(List<DataColumn> groupByCols) {
        List<Object> organizedRows = new ArrayList<Object>();
        Hashtable<DataColumn, Double> totals = getTotalColumn();

        HashSet<String> headers = new HashSet<String>();
        for (int i = 0; i < rows.size(); i++) {
            StringBuilder groupByExpr = new StringBuilder();
            for (DataColumn col : groupByCols) {
                col.setRow(getRowToRender(i));
                groupByExpr.append(col.getColumnValue().toString()).append(",");
            }
            if (headers.add(groupByExpr.toString())) {
                if (i != 0 && totals.size() > 0) {
                    Hashtable<String, DataField> totalRow = new Hashtable<String, DataField>();
                    Set<DataColumn> cols = totals.keySet();
                    for (DataColumn col : cols) {
                        totalRow.put(col.getFieldName().toLowerCase(), new DataField(col.getFieldName().toLowerCase(), "Total: " + totals.get(col)));
                    }
                    organizedRows.add(totalRow);
                    for (DataColumn col : cols) {
                        totals.put(col, 0d);
                    }
                }
                if (totals.size() > 0) {
                    Set<DataColumn> cols = totals.keySet();
                    for (DataColumn col : cols) {
                        col.setRow(getRowToRender(i));
                        totals.put(col, totals.get(col) + Double.parseDouble(col.getColumnValue() == null ? "0" : col.getColumnValue().toString()));
                    }
                }
                GroupByRow<String, DataField> row = new GroupByRow<String, DataField>();
                // String[] values = groupByExpr.split(",");
                for (int j = 0; j < groupByCols.size(); j++) {
                    DataColumn col = groupByCols.get(j);
                    col.setRow(getRowToRender(i));
                    row.put(col.getFieldName().toLowerCase(), new DataField(col.getFieldName().toLowerCase(), col.getColumnValue()));
                }
                organizedRows.add(row);
                organizedRows.add(getRowToRender(i));
            } else {
                if (totals.size() > 0) {
                    Set<DataColumn> cols = totals.keySet();
                    for (DataColumn col : cols) {
                        col.setRow(getRowToRender(i));
                        totals.put(col, totals.get(col) + Double.parseDouble(col.getColumnValue() == null ? "0" : col.getColumnValue().toString()));
                    }
                }
                organizedRows.add(getRowToRender(i));
            }

        }
        Hashtable<String, DataField> totalRow = new Hashtable<String, DataField>();
        Set<DataColumn> cols = totals.keySet();
        for (DataColumn col : cols) {
            totalRow.put(col.getFieldName().toLowerCase(), new DataField(col.getFieldName().toLowerCase(), "Total: " + totals.get(col)));
        }
        organizedRows.add(totalRow);

        return organizedRows;
    }

    public void registerEventListener(TableEventListener listener) {
        eventListener = listener;
    }

    protected abstract Object getRowToRender(int index);

    /**
     * composes String of the primary keys ids
     *
     * @param row
     * @return
     */
    protected String composeEventArgs(Hashtable<String, DataField> row) {
        if (pks.size() > 0) {
            String keyname = pks.get(0).getName().toLowerCase();
            DataField df = row.get(keyname);
            if (df != null) {
                StringBuffer buffer = new StringBuffer(df.getValue().toString());
                for (int i = 1; i < pks.size(); i++) {
                    buffer.append(pkSplitter);
                    keyname = pks.get(i).getName().toLowerCase();
                    buffer.append(row.get(keyname).getValue().toString());
                }
                return buffer.toString();
            }
        }
        return "";
    }

    /**
     * every child should provide a the event Args to be used
     *
     * @return
     */
    public abstract String getEventArgs();

    /**
     * renders the row start , and based on the index renders the style if this is the selected row or not.
     *
     * @param outputStream
     * @param rowindex
     * @throws Exception
     */
    protected void renderRowStart(RenderPrinter outputStream, int rowindex) throws Exception {
        if (rowindex == selectedIndex && getStatus().equals(EditState)) {
            outputStream.write(Render.startTag);
            outputStream.write(TagFactory.TableRow);
            getAttribute(selectedRowStyle).renderCustomKey("style", outputStream, page);
            getAttribute(selectedRowClass).renderCustomKey("class", outputStream, page);
            outputStream.write(Render.endTag);
        } else
            renderRowStart(outputStream, rowindex % 2 != 0);
    }

    protected void renderRowStart(RenderPrinter outputStream, boolean odd) throws Exception {
        outputStream.write(Render.startTag);
        outputStream.write(TagFactory.TableRow);
        if (dataRow != null) {
            dataRow.renderAttributes(outputStream);
            dataRow.renderStyle(outputStream);
        } else if (odd) {
            getAttribute(rowStyle).renderCustomKey("style", outputStream, page);
            getAttribute(rowClass).renderCustomKey("class", outputStream, page);
        }
        outputStream.write(Render.endTag);
    }

    protected void renderColumnStart(RenderPrinter outputStream, boolean odd, DataColumn column) throws Exception {
        outputStream.write(Render.startTag);
        outputStream.write(TagFactory.TableColumn);
        // [Aug 5, 2013 12:31:58 PM] [Amr.ElAdawy] [if the DataRow is null, then highlight even rows]
        if (dataRow == null) {
            if (odd)
                getAttribute(rowClass).renderCustomKey("class", outputStream, page);
        } else {
            getAttribute(tdClass).renderCustomKey("class", outputStream, page);
            if (column != null)
                column.renderStyle(outputStream);
        }
        outputStream.write(Render.endTag);
    }

    public void setInternalInputValues(Hashtable<String, String> values) {
        postedInternalValues = values;
    }

    protected List<String> getInternalFiledsNames(Hashtable<String, String> dataCollection) {
        return new ArrayList<String>(dataCollection.keySet());
    }

    protected List<String> getInternalFiledsValues(Hashtable<String, String> dataCollection) {
        List<String> values = new ArrayList<String>();
        for (String key : dataCollection.keySet())
            values.add(dataCollection.get(key));
        return values;
    }

    public int getPageSize() {
        String val = getAttributeValue(pageSize);
        if (StringUtility.isNullOrEmpty(val))
            return DeafultPageSize;
        return Integer.parseInt(val);
    }

    public void setPageSize(int size) {
        setAttributeValue(pageSize, String.valueOf(size));
    }

    public int getPageIndex() {
        String val = getAttributeValue(pageIndexKey);
        if (StringUtility.isNullOrEmpty(val))
            return 1;
        return Integer.parseInt(val);
    }

    public void setPageIndex(int index) {
        setAttributeValue(pageIndexKey, String.valueOf(index));
    }

    public int getTotalPages() {
        return totalPages;
    }

    public String getNoResults() {
        return getAttributeValue(noResultMessage);
    }

    public void setNoResults(String noResults) {
        setAttributeValue(noResultMessage, noResults);
    }

    public String getNoResultsClass() {
        return getAttributeValue(noResultsClass);
    }

    public void setNoResultsClass(String noResultsclass) {
        setAttributeValue(noResultsClass, noResultsclass);
    }

    public String getHeaderClass() {
        return getAttributeValue(headerClass);
    }

    public void setHeaderClass(String css) {
        setAttributeValue(headerClass, css);
    }

    public String getHeaderStyle() {
        return getAttributeValue(headerStyle);
    }

    public void setHeaderStyle(String style) {
        setAttributeValue(headerStyle, style);
    }

    public String getTdClass() {
        return getAttributeValue(tdClass);
    }

    public void setTdClass(String tdclass) {
        setAttributeValue(tdClass, tdclass);
    }

    public String getRowStyle() {
        return getAttributeValue(rowStyle);
    }

    public void setRowStyle(String style) {
        setAttributeValue(rowStyle, style);
    }

    public String getRowCalss() {
        return getAttributeValue(rowClass);
    }

    public void setRowCalss(String css) {
        setAttributeValue(rowClass, css);
    }

    public String getDateFormat() {
        return getAttributeValue(dateformatKey);
    }

    public void setDateFormat(String dateformat) {
        setAttributeValue(dateformatKey, dateformat);
    }

    public SimpleDateFormat getSimpleDateFormat() {
        if (StringUtility.isNullOrEmpty(getDateFormat()))
            return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        else
            return new SimpleDateFormat(getDateFormat());
    }

    public String getSelectedRowStyle() {
        return getAttributeValue(selectedRowStyle);
    }

    public void setSelectedRowStyle(String style) {
        setAttributeValue(selectedRowStyle, style);
    }

    public String getSelectedRowClass() {
        return getAttributeValue(selectedRowClass);
    }

    public void setSelectedRowClass(String style) {
        setAttributeValue(selectedRowClass, style);
    }

    /**
     * gets the current status of the Editpanel (Edit, New)
     *
     * @return
     */
    public String getStatus() {
        return getAttributeValue(panelStatus);
    }

    public void setStatus(String statusString) {
        setAttributeValue(panelStatus, statusString);
    }

    public String getNew() {
        return getAttributeValue(showNew);
    }

    public void setNew(String attString) {
        setAttributeValue(showNew, attString);
    }

    public boolean getAutoBind() {
        return getAttributeBooleanValue(autoBindKey);
    }

    public void setAutoBind(boolean auto) {
        setAttributeBooleanValue(autoBindKey, auto);
    }

    public int getTotalCount() {
        return (totalCount == null || totalCount == Integer.MAX_VALUE) ? -1 : totalCount;
    }

    protected abstract void delete(String eventArgs);

    protected abstract void update(String eventArgs);

    /**
     * insert new item
     *
     * @param eventArgs
     */
    public void add(String eventArgs) {
        setStatus(ViewState);
        if (!isActionAllowed(NewState))
            logger.error("User is trying to Insert Record while inserting is not allowed.");
        else
            // DO Insert here
            doAdd();
        dataBind();
    }

    protected abstract void doAdd();

    public void navigate(String eventArgs) {
        setPageIndex(Integer.parseInt(eventArgs));
        setStatus(ViewState);
        if (!StringUtility.isNullOrEmpty(getSortingInfo()))
            findSortingColumn(getSortingInfo());
        dataBind();
    }

    /**
     * @param eventArgs
     * @throws Exception
     */
    public void exportToExcel(String eventArgs) throws Exception {
        if (DoneState.equals(getStatus())) {
            logger.info("Repeated Export to excel, return");
            return;
        }
        ExportToExcel exportToExcelControl = exportToExcelControls.get(Integer.parseInt(eventArgs));
        if (exportToExcelControl.getFileType().equalsIgnoreCase(ExportToExcel.CSV)) {
            exportToCSV(eventArgs);
            setStatus(DoneState);
            return;
        }
        setStatus(ViewState);
        if (!StringUtility.isNullOrEmpty(getSortingInfo()))
            findSortingColumn(getSortingInfo());
        ObjectToExcel toExcel = new ObjectToExcel();
        toExcel.init("Sheet Name");
        List<String> headers = new ArrayList<String>();
        List<DataColumn> fields = extractHeadersAndFields(headers, exportToExcelControl);
        toExcel.addHeader(headers);

        List<?> rowsToBeExported = new ArrayList<Object>();
        if (ExportToExcel.EXPORT_TO_EXCEL_DISPLAYED.equalsIgnoreCase(exportToExcelControl.getRowsToExport())) {
            // [Feb 19, 2015 2:11:54 PM] [aeladawy] [This is a stuipd refactor I made in DXB airport to call databind only if the
            // reows to be exported are the displayed rows.]
            dataBind();
            rowsToBeExported = rows;
        } else if (ExportToExcel.EXPORT_TO_EXCEL_ALL.equalsIgnoreCase(exportToExcelControl.getRowsToExport())) {
            // TODO: Refactor to remove the coupling between the parent Table
            // and Children
            // Suggested move the some code to the ListTable and Parent Table
            // and then
            // make export to Excel in table generic in order to be able to
            // accept any data structure
            if (this instanceof ListTable) {
                // [Feb 23, 2015 11:55:40 AM] [aeladawy] [If this is a list table, then call DataBind one more time]
                dataBind();
                rowsToBeExported = ((ListTable) this).getList();
            } else if (this instanceof DataTable)
                rowsToBeExported = DAO.search(((DataTable) this).getDataSource(), ((DataTable) this).getFinalSql(), 0,
                        exportToExcelControl.getMaxRows(), false, null);

        } else if (ExportToExcel.EXPORT_TO_EXCEL_SELECTED.equalsIgnoreCase(exportToExcelControl.getRowsToExport())) {
            // [Jul 26, 2009 9:14:45 AM] [amr.eladawy] [if there is no selected rows, then export all rows]
            rowsToBeExported = getSelectedRows();
            if (rowsToBeExported == null || rowsToBeExported.size() == 0) {
                if (this instanceof ListTable)
                    rowsToBeExported = ((ListTable) this).getList();
                else if (this instanceof DataTable)
                    rowsToBeExported = DAO.search(((DataTable) this).getDataSource(), ((DataTable) this).getFinalSql(), 0,
                            exportToExcelControl.getMaxRows(), false, null);
            }

        }
        writeExcelRows(toExcel, rowsToBeExported, fields);
        // Write the output to a file
        try {
            page.response.setHeader("Content-disposition", "attachment; filename=" + exportToExcelControl.getFileName());
            page.response.setContentType("application/vnd.ms-excel");
            OutputStream out = page.response.getOutputStream();
            toExcel.persist(out);
            out.close();
            page.skip();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        setStatus(DoneState);
    }

    /**
     * writes the given rows to the Excel document.
     *
     * @param toExcel
     * @param rows
     */
    protected void writeExcelRows(ObjectToExcel toExcel, List<?> rows, List<DataColumn> fields) {
        toExcel.addRows(rows, fields);
    }

    /**
     * hide edit panel.
     *
     * @param eventArgs
     */
    public void cancelItem(String eventArgs) {
        setStatus(ViewState);
        dataBind();
    }

    /**
     * show edit panel for certain item.
     *
     * @param eventArgs
     */
    public void edit(String eventArgs) {
        setStatus(EditState);
        dataBind();
        // if (this instanceof DataTable)
        // {
        // int i = eventArgs.indexOf(pkSplitter);
        // if (i > 0)
        // selectedIndex = Integer.parseInt(eventArgs.substring(i - 1));
        // }
        // else
        String rowIndex = "0";

        try {
            rowIndex = eventArgs.split(InternalValueHolder.nameSplitter)[1];
        } catch (Exception e) {

        }
        selectedIndex = Integer.parseInt(rowIndex);
    }

    /**
     * remove sorting.
     *
     * @param eventArgs
     */
    public void desort(String eventArgs) {

        // [Dec 4, 2013 4:55:51 PM] [Amr.ElAdawy] [save the sorting info]
        setSortingInfo(null);
        dataBind();
    }

    /**
     * sorts the table rows based on the column name passed.
     *
     * @param eventArgs
     */
    public void sort(String eventArgs) {
        findSortingColumn(eventArgs);
        resetPageIndex();
        dataBind();
    }

    /**
     * finds the column that will sort the table.
     *
     * @param eventArgs
     * @Author Amr.ElAdawy Dec 4, 2013 5:26:15 PM
     */
    protected void findSortingColumn(String eventArgs) {
        int index = eventArgs.indexOf(InternalValueHolder.nameSplitter);

        if (index > 0) {
            // [Dec 4, 2013 4:55:51 PM] [Amr.ElAdawy] [save the sorting info]
            setSortingInfo(eventArgs);
            // [Nov 9, 2013 11:34:16 AM] [Amr.ElAdawy] [set the page index to 1]
            String contText = eventArgs.substring(0, index);
            String dir = eventArgs.substring(index + InternalValueHolder.nameSplitter.length());
            // [Jan 17, 2013 10:57:58 PM] [Amr.ElAdawy] [support auto generated columns]
            if (columns.size() == 0) {
                DataColumn column = new DataColumn();
                column.setFieldName(contText);
                column.setSortDirection(dir.trim());
                sortingColumn = column;
            } else
                for (DataColumn column : columns)
                    if (column.getFieldName().trim().equalsIgnoreCase(contText.trim())) {
                        sortingColumn = column;
                        column.setSortDirection(dir.trim());
                    } else
                        column.setSortDirection("");
        }
    }

    public void changePageSize(String eventArgs) {
        int size = getPageSize();
        try {
            size = Integer.parseInt(eventArgs);
        } catch (Exception e) {

        }
        setPageSize(size);
        viewstate.put(pageSize, String.valueOf(size));
        // [Dec 3, 2013 8:44:43 PM] [Amr.ElAdawy] [reset the page index because this is a change in the page size]

        if (!StringUtility.isNullOrEmpty(getSortingInfo()))
            findSortingColumn(getSortingInfo());
        resetPageIndex();
        dataBind();
    }

    /**
     * tells whether the Check Data Column Command has CheckType=All or not
     */
    protected boolean isAllSelected() {
        for (DataColumn column : columns) {
            if (column instanceof DataColumnCommand && column.getType().equalsIgnoreCase(DataColumnCommand.Check)) {
                return ((DataColumnCommand) column).getCheckType().equalsIgnoreCase("all");

            }
        }
        return false;
    }

    public List<DataParam> getParameters() {
        return parameters;
    }

    public void setParameters(List<DataParam> parameters) {
        this.parameters = parameters;
    }

    public boolean getShowRowIndex() {
        return getAttributeBooleanValue(showRowIndex);
    }

    public void setShowRowIndex(boolean show) {
        setAttributeBooleanValue(showRowIndex, show);
    }

    public int getStartRowIndex() {
        return getAttributeIntValue(startRowIndex);
    }

    public void setStartRowIndex(int startIndexVal) {
        setAttributeIntValue(startRowIndex, startIndexVal);
    }

    public TableEventListener getEventListener() {
        return eventListener;
    }

    public void setEventListener(TableEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public Object getRow() {
        return row;
    }

    public void setRow(Object row) {
        this.row = row;
    }

    public int getRowsCount() {
        return rows.size();
    }

    /**
     * saves the table rows in session.
     */
    public void persistRows() {
        page.session.setAttribute(page.getPageURL() + getId() + "_rows", rows);
        page.session.setAttribute(page.getPageURL() + getId() + "_rows_count", totalCount);
    }

    /**
     * gets the saved table rows from the session. session is cleared after this call.
     */
    public void retrieveRows() {
        Object object = page.session.getAttribute(page.getPageURL() + getId() + "_rows");
        if (object != null) {
            dataRetrieved = true;
            rows = (List<?>) object;
        }
        object = page.session.getAttribute(page.getPageURL() + getId() + "_rows_count");
        if (object != null)
            totalCount = (Integer) object;
        page.session.removeAttribute(page.getPageURL() + getId() + "_rows");
        page.session.removeAttribute(page.getPageURL() + getId() + "_rows_count");
    }

    public String getVar() {
        return getAttributeValue(var);
    }

    public void setVar(String varValue) {
        setAttributeValue(var, varValue);
    }

    public String getHoverClass() {
        return getAttributeValue(hoverClass);
    }

    public void setHoverClass(String hoverValue) {
        setAttributeValue(hoverClass, hoverValue);
    }

    /**
     * gets the current column that is sorting.
     *
     * @return
     */
    protected DataColumn getSortingColumn() {
        // [Jan 17, 2013 11:06:15 PM] [Amr.ElAdawy] [return the already defined sorted column]

        return sortingColumn;
    }

    /**
     * security check to make sure that New/Edit commands are not served unless the developer had decalred New Command / Edit
     * DataColumnCommand.
     *
     * @param action
     * @return
     */
    protected boolean isActionAllowed(String action) {
        if (action.equalsIgnoreCase(EditState)) {
            for (DataColumn column : columns)
                if (column instanceof DataColumnCommand && column.getType().equalsIgnoreCase(DataColumnCommand.Edit))
                    return true;
            return false;
        }
        if (action.equalsIgnoreCase(DeleteState)) {
            for (DataColumn column : columns)
                if (column instanceof DataColumnCommand && column.getType().equalsIgnoreCase(DataColumnCommand.Delete))
                    return true;
            return false;
        }
        if (action.equalsIgnoreCase(NewState))
            return !getNew().equalsIgnoreCase(FALSE);
        return false;
    }

    /**
     * tells whether this table has a next page or not.
     *
     * @return
     */
    public boolean hasNext() {
        return nextPage;
    }

    /**
     * [Jun 10, 2009 6:35:47 PM] [amr.eladawy] <br/>
     * clears the current items in this Table in order to allow rebinding. used when tables are nested within another table, so
     * that the items are fetched every time the table is rendered.
     *
     * @author amr.eladawy
     */
    public void resetTable() {
        bound = false;
        rows = null;
    }

    @Override
    public void renderChildren(RenderPrinter outputStream) throws Exception {
        // [Apr 2, 2013 10:07:11 AM] [Amr.ElAdawy] [all this section is moved from the render() method to be executed with ajax
        // calls]
        // //////////////////
        String oldInternalValueHolderId = (String) page.carriage.get(PageCarriage.CURRENT_INTERNAL_VALUE_HOLDER_ID);
        page.carriage.set(PageCarriage.CURRENT_INTERNAL_VALUE_HOLDER_ID, this.getId());

        InternalValueHolder oldInternalValueHolder = (InternalValueHolder) page.carriage.get(PageCarriage.CURRENT_INTERNAL_VALUE_HOLDER);
        page.carriage.set(PageCarriage.CURRENT_INTERNAL_VALUE_HOLDER, this);
        // [Jun 10, 2009 6:32:42 PM] [amr.eladawy] [check if the parent has a id suffix, which means that this ]
        // [Jun 10, 2009 6:33:50 PM] [amr.eladawy] [control will be rendered in item repeater like another table.]
        // [Jun 10, 2009 6:34:04 PM] [amr.eladawy] [if so, rest the rows.]
        if (!StringUtility.isNullOrEmpty(parent.getIdSuffix()))
            resetTable();
        if (this instanceof ListTable || (getAutoBind() && !bound))
            dataBind();
        // //////////////////

        footer.setParent(this);
        footer.setPage(page);
        // if empty results, render message and New command
        if (rows == null || rows.size() == 0) {
            if (StringUtility.isNullOrEmpty(getNoResults())) {
                rows = createEmptyRows();
                renderTableParts(outputStream);
                rows = null;
            } else {
                Label label = new Label();
                label.setId(getId());
                label.setCssClass(getNoResultsClass());
                label.setText(getNoResults());
                label.render(outputStream);
                footer.renderNewCommand(outputStream, this);
                // renderNewCommand(outputStream);
            }
        } else {
            renderTableParts(outputStream);
        }
        if (!getNew().equalsIgnoreCase(FALSE))
            renderNewItemPanel(outputStream);

        addErrorMessage(outputStream);

        // [Apr 2, 2013 10:07:11 AM] [Amr.ElAdawy] [all this section is moved from the render() method to be executed with ajax
        // calls]

        // //////////////////
        page.carriage.set(PageCarriage.CURRENT_INTERNAL_VALUE_HOLDER_ID, oldInternalValueHolderId);
        page.carriage.set(PageCarriage.CURRENT_INTERNAL_VALUE_HOLDER, oldInternalValueHolder);
        // //////////////////
    }

    /**
     * render the table main parts like Header , body and footer
     *
     * @param outputStream
     * @throws Exception
     */
    protected void renderTableParts(RenderPrinter outputStream) throws Exception {
        // [Jul 5, 2012 1:50:36 PM] [Amr.ElAdawy] [add twitter class]
        setCssClass(getCssClass() + " table  table-bordered table-hover");
        // render table.
        outputStream.write(Render.startTag);
        outputStream.write(TagFactory.Table);
        renderAttributes(outputStream);
        outputStream.write(Render.endTag);
        outputStream.write(Render.newLine);
        // ////
        // render header.
        renderTableHeader(outputStream);

        // render rows
        renderTableRows(outputStream);

        // footer
        renderTableFooter(outputStream);

        // close table
        outputStream.write(Render.startEndTag);
        outputStream.write(TagFactory.Table);
        outputStream.write(Render.endTag);
    }

    public String getOnRender() {
        return getAttributeValue(onRenderKey);
    }

    public void setOnRender(String onRendered) {
        setAttributeValue(onRenderKey, onRendered);
    }

    public String getAjaxId() {
        return getId() + "_AjaxDev";
    }

    /**
     * creates an empty rows to be used during rendering the table given that there is no search results.
     *
     * @return
     */
    protected abstract List<?> createEmptyRows();

    public DataRow getDataRow() {
        return dataRow;
    }

    public void setDataRow(DataRow dataRow) {
        this.dataRow = dataRow;
    }

    public void setInteralPostback(boolean isInternalPostback) {
        this.internalPostback = isInternalPostback;
    }

    /**
     * returns true if the databind() was fired.
     *
     * @return the bound
     */
    public boolean isBound() {
        return bound;
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

    /**
     * resets the page index to 1
     *
     * @Author Amr.ElAdawy Nov 9, 2013 11:35:13 AM
     */
    public void resetPageIndex() {
        setPageIndex(1);
        // [Nov 8, 2013 2:28:27 PM] [Amr.ElAdawy] [if this is page rendering, save the value in the view state to retrive
        // it back in the next trip]
        viewstate.put(pageIndexKey, "1");
    }

    public String getSortingInfo() {
        return getAttributeValue(sortingInfo);
    }

    public void setSortingInfo(String sortingInfoVal) {
        setAttributeValue(sortingInfo, sortingInfoVal);
    }

    public void exportToCSV(String eventArgs) throws Exception {
        setStatus(ViewState);
        if (!StringUtility.isNullOrEmpty(getSortingInfo()))
            findSortingColumn(getSortingInfo());

        ObjectToCSV toCsv = new ObjectToCSV();
        List<String> headers = new ArrayList<String>();
        ExportToExcel exportToExcelControl = exportToExcelControls.get(Integer.parseInt(eventArgs));
        List<DataColumn> fields = extractHeadersAndFields(headers, exportToExcelControl);
        toCsv.addHeader(headers);
        try {
            page.response.setHeader("Content-disposition", "attachment; filename=" + exportToExcelControl.getFileName());
            page.response.setContentType("application/vnd.ms-excel");
            OutputStream out = page.response.getOutputStream();

            if (ExportToExcel.EXPORT_TO_EXCEL_DISPLAYED.equalsIgnoreCase(exportToExcelControl.getRowsToExport())) {
                // [18 Nov 2015 09:37:18] [aeladawy] [this path and export selected rows required to do databind() again, however other path does the databind() in later stage]
                dataBind();
                writeRowsToFile(toCsv, rows, fields, out);
            } else if (ExportToExcel.EXPORT_TO_EXCEL_ALL.equalsIgnoreCase(exportToExcelControl.getRowsToExport())) {
                writeAllRowsToCsv(toCsv, fields, exportToExcelControl, out);
            } else if (ExportToExcel.EXPORT_TO_EXCEL_SELECTED.equalsIgnoreCase(exportToExcelControl.getRowsToExport())) {
                List<Integer> list = getSelectedIndices();
                if (list == null || list.size() == 0) {
                    writeAllRowsToCsv(toCsv, fields, exportToExcelControl, out);
                } else {
                    writeRowsToFile(toCsv, getSelectedRows(), fields, out);
                }
            }
            out.close();
            page.skip();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected void writeAllRowsToCsv(ObjectToCSV toCsv, List<DataColumn> fields, ExportToExcel exportToExcelControl, OutputStream out)
            throws Exception {
        if (this instanceof ListTable) {
            writeRowsToFile(toCsv, ((ListTable) this).getList(), fields, out);
        } else if (this instanceof DataTable) {
            writeAllDbRowsToFile(toCsv, exportToExcelControl, fields, out);
        }
    }

    protected void writeAllDbRowsToFile(ObjectToCSV toCsv, ExportToExcel exportToExcelControl, List<DataColumn> fields, OutputStream out)
            throws Exception {
        DAO.writeSearchResultToStream(((DataTable) this).getDataSource(), ((DataTable) this).getFinalSql(), 0, exportToExcelControl.getMaxRows(),
                toCsv, fields, out);
    }

    protected void writeRowsToFile(ObjectToCSV toCsv, List<?> rowsToBeExported, List<DataColumn> fields, OutputStream out) throws Exception {
        toCsv.addRows(rowsToBeExported, fields);
        toCsv.flushRows(out);
    }

    protected List<DataColumn> extractHeadersAndFields(List<String> headers, ExportToExcel exportToExcelControl) {
        List<DataColumn> fields = new ArrayList<DataColumn>();
        List<DataColumn> columnList = (exportToExcelControl.getColumns().size() > 0) ? exportToExcelControl.getColumns() : columns;
        for (DataColumn column : columnList) {
            // [Jul 24, 2013 1:38:06 PM] [amr.eladawy] [check the flag export on each column]
            if (column instanceof DataColumnCommand || !column.getExport())
                continue;
            headers.add(column.getText());
            fields.add(column);
        }
        return fields;
    }

    public boolean getcaseSensitive() {
        return getAttributeBooleanValue(caseSensitiveKey);
    }

    public void setcaseSenstive(boolean caseSenstive) {
        setAttributeBooleanValue(caseSensitiveKey, caseSenstive);
    }

}
