package eg.java.net.web.jspx.ui.controls.html.elements.dataitem;

import eg.java.net.web.jspx.engine.data.DAO;
import eg.java.net.web.jspx.engine.data.DataException;
import eg.java.net.web.jspx.engine.data.DataField;
import eg.java.net.web.jspx.engine.error.JspxException;
import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.PropertyAccessor;
import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.engine.util.ui.Render;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.WebControl;
import eg.java.net.web.jspx.ui.controls.attrbs.JspxAttribute;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.InternalValueHolder;
import eg.java.net.web.jspx.ui.pages.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.*;

/**
 * @author amr.eladawy
 */
public class DataTable extends Table {

    private static final long serialVersionUID = 6627551379578774058L;
    public static String NAVIGATION_MODE_PAGING = "paging";
    public static String NAVIGATION_MODE_THROUGH = "through";
    @JspxAttribute
    protected static String dataSource = "datasource";
    @JspxAttribute
    protected static String navigationMode = "navigationmode";
    private static final Logger logger = LoggerFactory.getLogger(DataTable.class);
    @JspxAttribute
    private static final String tableName = "table";
    @JspxAttribute
    private static final String sqlKey = "sql";
    @JspxAttribute
    private static final String bindToClass = "bindtoclass";

    /**
     * @param tagName
     */
    public DataTable() {
        super(TagFactory.DataTable);
        loadInternalAttributes();
    }

    /**
     * @param tagName
     * @param page
     */
    public DataTable(Page page) {
        super(TagFactory.DataTable, page);
        loadInternalAttributes();
    }

    protected void loadInternalAttributes() {
        super.loadInternalAttributes();
        internalAttribtes.put(tableName.toLowerCase(), 0);
        internalAttribtes.put(sqlKey.toLowerCase(), 0);
        internalAttribtes.put(dataSource.toLowerCase(), 0);
        internalAttribtes.put(bindToClass.toLowerCase(), 0);
        internalAttribtes.put(navigationMode.toLowerCase(), 0);
    }

    /**
     * override this method to return the sql string.
     *
     * @return
     */
    public String getFinalSql() {
        String sql = getSql();
        for (DataParam param : parameters)
            sql = param.formatSql(sql, getcaseSensitive());
        return sql;
    }

    /**
     * invokes the binding operation.
     */
    public void dataBind() {
        try {
            bound = true;
            // [Sep 23, 2013 11:00:20 PM] [amr.eladawy] [reset the page index in case the event is external]
            if (!internalPostback)
                resetPageIndex();
            int index = getPageIndex();
            int size = getPageSize();
            String sql = "";

            try {
                // [17 Aug 2015 13:11:19] [aeladawy] [incase of missing parameter return]
                sql = getFinalSql();
            } catch (Exception e) {
                message = e.getMessage();
                return;
            }

            DataColumn sortedColumn = getSortingColumn();
            if (sortedColumn != null) {
                // [Jun 17, 2009 12:35:01 PM] [amr.eladawy] [Check of the sorting column has a filed name, if not log error for
                // that ]
                if (StringUtility.isNullOrEmpty(sortedColumn.getFieldName()))
                    logger.error("The DataColumn [" + sortedColumn + "] requires FieldName attribute, sorting is ignored.");
                else {
                    String sortKey = sortedColumn.getFieldName().trim();
                    // if (sortKey.contains(" "))
                    sortKey = "a.\"" + sortKey + "\"";
                    // [Nov 27, 2009 8:19:15 AM] [Amr.ElAdawy] [change to support DB2]
                    sql = "select * from (" + sql + ") a order by " + sortKey + " " +
                            sortedColumn.getSortDirection();
                }
            }

            if (rows == null)
                rows = new ArrayList<Hashtable<String, DataField>>();
            if (!dataRetrieved) {
                // [May 24, 2009 11:41:00 AM] [amr.eladawy] Arabi wanted to prop the next item,to know if there will be next page
                // or not.
                boolean getTotalCount = StringUtility.isNullOrEmpty(getNavigationMode())
                        || getNavigationMode().equalsIgnoreCase(NAVIGATION_MODE_PAGING);
                ArrayList<Integer> countList = new ArrayList<Integer>();
                rows = DAO.search(getDataSource(), sql, (index - 1) * size, size + 1, getTotalCount, countList);
                // [May 24, 2009 11:41:26 AM] [amr.eladawy] if the returned rows equal the required size +1 then there is a next
                // page.
                nextPage = rows.size() == size + 1;
                // [May 24, 2009 11:42:33 AM] [amr.eladawy] now remove the extra element from the list
                if (nextPage)
                    rows.remove(size);
                totalCount = countList.get(0);
                if (columns.isEmpty() && !rows.isEmpty()) {
                    // [Jan 17, 2013 8:29:51 PM] [Amr.ElAdawy] [auto generate columns]
                    logger.info("No columns are defined in the datatable. auto generating based on the returned resultset");
                    generateColumns();
                }
            }
            if (totalCount != Integer.MAX_VALUE) {
                totalPages = totalCount / size;
                if (totalCount % size > 0)
                    totalPages++;
            } else
                totalPages = Integer.MAX_VALUE;
        } catch (Exception e) {
            bound = false;
            if (e instanceof JspxException)
                throw (JspxException) e;
            setRendered(false);
            logger.error("dataBind()", e);
        }
    }

    /**
     * overridden to add the child control to the corresponding collection for example if dataparam --> params. datacolumn
     * colmuns.
     */
    public void addControl(WebControl control) {
        super.addControl(control);
        if (control instanceof DataParam)
            parameters.add((DataParam) control);
        else if (control instanceof DataPK)
            pks.add((DataPK) control);
        else if (control instanceof DataLookup)
            lookups.put(control.getId(), (DataLookup) control);
    }

    /**
     * @param outputStream
     * @throws Exception
     */
    protected void renderNewItemPanel(RenderPrinter outputStream) throws Exception {
        outputStream.write("<div class=\"modal fade\" id=\"" + getId() + NEW_ITEM_PANEL_ID
                + "\" tabindex=\"-1\" role=\"dialog\" aria-labelledby=\"myModalLabel\" aria-hidden=\"true\"><div class=\"modal-dialog\">"
                + "<div class=\"modal-content\"><div class=\"modal-header\">  "
                + "<button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-hidden=\"true\">&times;</button><h4 class=\"modal-title\">Add new Item</h4></div>"
                + "<div class=\"modal-body\"><div class=\"row\">");

        StringBuilder requiredID = new StringBuilder();
        // render the rows
        // render primary keys first.
        String keyname = null;
        String pkValue = null;
        String colLabel = "";
        boolean pkRendered = true;
        Hashtable<String, DataField> row = null;
        for (DataPK pk : pks) {
            pkRendered = pk.isRendered();
            keyname = pk.getName();
            String wcId = getId() + nameSplitter + nameSplitterInsert + keyname;

            outputStream.write("<div class=\"span4\"><div class=\"control-group info\">");
            Render.createLabel(keyname, wcId, page).render(outputStream);

            outputStream.write(" <div class=\"controls\">");
            // if the type of the PK is not auto generated.
            if (StringUtility.isNullOrEmpty(pk.getType())) {
                // [Jun 6, 2009 6:56:08 AM] [amr.eladawy] this PK is available for ser input.
                if (pkRendered) {
                    // [Jun 6, 2009 6:56:34 AM] [amr.eladawy] this PK to be rendered, so it will be rendered and loaded with
                    // the default value.
                    Render.createInput(wcId, pk.getDefaultValue(), null, page).render(outputStream);
                    outputStream.write("*");
                    requiredID.append("'").append(wcId).append("',");
                } else if (!StringUtility.isNullOrEmpty(pk.getDefaultValue()))
                    // [Jun 6, 2009 6:57:25 AM] [amr.eladawy] this is a hidden PK so, it will be rendered only if it has a
                    // default value.
                    Render.createHidden(wcId, pk.getDefaultValue(), page).rebindValueHolderControl();
            } else if (pkRendered) {
                // [Jun 6, 2009 6:58:11 AM] [amr.eladawy] this is an auto generated PK, so it should be displayed only if it
                // is visible
                if (!pk.getType().equalsIgnoreCase(DataPK.IDENTITY_TYPE))
                    Render.createHidden(wcId, "", page).render(outputStream);
                // [Jun 6, 2009 6:59:35 AM] [amr.eladawy] display the default value of the control only if it has one.
                if (StringUtility.isNullOrEmpty(pk.getDefaultValue()))
                    Render.createLabel("***", wcId, page).render(outputStream);
                else
                    Render.createLabel(pk.getDefaultValue(), wcId, page).render(outputStream);
            }
            outputStream.write("</div></div></div>");
        }
        // render rest of the rows.
        String colName = null;
        String colVal = "";
        for (DataColumn column : columns) {
            // [Mar 21, 2012 5:05:51 PM] [amr.eladawy] [skip rendering if this is a column command or an empty column name]
            if (column instanceof DataColumnCommand || StringUtility.isNullOrEmpty(column.getFieldName()))
                continue;
            colVal = column.getDefaultValue();
            colName = column.getFieldName();
            colLabel = column.getText();
            if (!isPK(colName)) {
                outputStream.write("<div class=\"span4\"><div class=\"control-group info\">");
                String wcId = getId() + nameSplitter + nameSplitterInsert + colName;
                Render.createLabel(colLabel, wcId, page).render(outputStream);

                if (!StringUtility.isNullOrEmpty(column.getHint()))
                    outputStream.write(" &nbsp; &nbsp;<small><cite title=\"" + column.getHint() + "\">" + column.getHint() + "</cite></small>");

                outputStream.write(" <div class=\"controls\">");

                // String id = new StringBuilder(getId()).append(nameSplitter).append(colName).toString();
                if (column.getReadOnly()) {
                    Render.createLabel(colVal, wcId, page).render(outputStream);
                    Render.createHidden(wcId, colVal, page).render(outputStream);
                } else {
                    if (column.getType().equalsIgnoreCase(DataColumn.Lookup))
                        Render.createSelect(wcId, colVal, lookups.get(column.getFK()), column.getCustomAttribute(), page).render(outputStream);
                        // renderSelect(outputStream, id, colVal, lookups.get(column.getFK()), column.getCustomAttribute());
                    else if (column.getType().equalsIgnoreCase(DataColumn.Date))
                        Render.createCalendar(wcId, colVal, column.getDateFormat(), page).render(outputStream);
                        // renderCalendar(outputStream, id, getSimpleDateFormat().format(row.get(colName.toLowerCase()).getValue()));
                    else
                        Render.createInput(wcId, colVal, column.getCustomAttribute(), page).render(outputStream);
                    // renderInput(outputStream, id, colVal, column.getCustomAttribute());
                }
                if (column.getRequired()) {
                    outputStream.write("*");
                    requiredID.append("'").append(wcId).append("',");
                }
                outputStream.write("</div></div></div>");
            }
        }

        if (requiredID.length() > 0) {
            requiredID.deleteCharAt(requiredID.length() - 1).append("]").insert(0, "[");
        }
        String saveAction = Render.composeAction(getId(), Table.SAVE_EVENT, "", null, true, null, false, false, getMySubmitter(), this);

        outputStream
                .write("</div></div><div class=\"modal-footer\"><button type=\"button\" class=\"btn btn-default\" data-dismiss=\"modal\">Close</button>  "
                        + "<button type=\"button\" class=\"btn btn-primary\" onclick=\"if (validInputData(" + requiredID + ")){"
                        + " $jspx('#" + getId() + NEW_ITEM_PANEL_ID + " ').modal('hide');" + saveAction
                        + ";}\">Save changes</button></div></div></div></div>");

    }

    /**
     * checks whether the given field name is a PK or not.
     *
     * @param name
     * @return
     */
    protected boolean isPK(String name) {
        for (int i = 0; i < pks.size(); i++)
            if (pks.get(i).getName().equalsIgnoreCase(name))
                return true;
        return false;
    }

    protected DataPK getPK(String name) {
        for (DataPK pk : pks)
            if (pk.getName().equalsIgnoreCase(name))
                return pk;
        return null;
    }

    /**
     * insert new item
     *
     * @param eventArgs
     */
    public void doAdd() {
        // DO Insert here
        try {
            String sql = createInsertStatement();
            // [Jun 23, 2011 3:52:44 PM] [amr.eladawy] [before save call the listener]
            if (!StringUtility.isNullOrEmpty(getOnInsert())) {
                try {
                    PropertyAccessor.invokePageMethod(page, page.getClass(), getOnInsert(), this, sql);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
            DAO.executeNoneQuery(getDataSource(), sql);
            setStatus(DoneState);
        } catch (Exception e) {
            logger.debug("Error while insert, msg = ", e);
            // [Aug 27, 2013 3:14:10 AM] [Amr.ElAdawy] [send error to the error listener]
            if (StringUtility.isNullOrEmpty(getOnError())) {
                message = e.getMessage();
                // [Jul 22, 2013 8:44:08 AM] [amr.eladawy] [if e is null pointer exception ]
                if (message == null)
                    message = e.toString();
            } else {
                try {
                    PropertyAccessor.invokePageMethod(page, page.getClass(), getOnError(), this, e.getMessage());
                } catch (InvocationTargetException e1) {
                    logger.error("doAdd()- ", e1);
                }
            }
        }
    }

    /**
     * save the updated item.
     *
     * @param eventArgs
     */
    public void update(String eventArgs) {
        setStatus(ViewState);
        if (!isActionAllowed(EditState))
            logger.error("User is trying to Edit Record while Editing is not allowed.");
        else {
            // DO update here.
            try {
                String updateStatement = createUpdateStatement(eventArgs);
                // [Jun 23, 2011 3:52:44 PM] [amr.eladawy] [before save call the listener]
                if (!StringUtility.isNullOrEmpty(getOnEdit())) {
                    try {

                        PropertyAccessor.invokePageMethod(page, page.getClass(), getOnEdit(), this, updateStatement);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
                DAO.executeNoneQuery(getDataSource(), updateStatement);
            } catch (Exception e) {
                logger.error("save(eventArgs=" + eventArgs + ")", e);
                // [Aug 27, 2013 3:14:10 AM] [Amr.ElAdawy] [send error to the error listener]
                if (StringUtility.isNullOrEmpty(getOnError())) {
                    message = e.getMessage();
                    // [Jul 22, 2013 8:44:08 AM] [amr.eladawy] [if e is null pointer exception ]
                    if (message == null)
                        message = e.toString();
                } else {
                    try {
                        PropertyAccessor.invokePageMethod(page, page.getClass(), getOnError(), this, e.getMessage());
                    } catch (InvocationTargetException e1) {
                        logger.error("doAdd()- ", e1);
                    }
                }
            }
        }
        dataBind();
    }

    public void delete(String eventArgs) {
        setStatus(ViewState);
        if (!isActionAllowed(DeleteState))
            logger.error("User is trying to Delete Record while deleting is not allowed.");
        else if (!pks.isEmpty()) {
            try {
                String deleteStatement = "delete from " + getTableName() + getWhereString(eventArgs);
                // [Jun 23, 2011 3:52:44 PM] [amr.eladawy] [before delete call the listener]
                if (!StringUtility.isNullOrEmpty(getOnDelete())) {
                    try {
                        PropertyAccessor.invokePageMethod(page, page.getClass(), getOnDelete(), this, deleteStatement);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
                // [Nov 27, 2009 8:19:15 AM] [Amr.ElAdawy] [change to support DB2]
                DAO.executeNoneQuery(getDataSource(), deleteStatement);
            } catch (Exception e) {
                logger.error("delete(eventArgs=" + eventArgs + ")", e);
                // [Aug 27, 2013 3:14:10 AM] [Amr.ElAdawy] [send error to the error listener]
                if (StringUtility.isNullOrEmpty(getOnError())) {
                    message = e.getMessage();
                    // [Jul 22, 2013 8:44:08 AM] [amr.eladawy] [if e is null pointer exception ]
                    if (message == null)
                        message = e.toString();
                } else {
                    try {
                        PropertyAccessor.invokePageMethod(page, page.getClass(), getOnError(), this, e.getMessage());
                    } catch (InvocationTargetException e1) {
                        logger.error("delete()- ", e1);
                    }
                }
            }
        }
        dataBind();
    }

    protected String createInsertStatement() throws DataException {
        int size = postedInternalValues.size();
        if (size > 0) {
            StringBuilder builder = new StringBuilder("INSERT INTO ").append(getTableName()).append("(");
            List<String> keys = getInternalFiledsNames(postedInternalValues);
            List<String> values = getInternalFiledsValues(postedInternalValues);
            int splitter = getId().length() + nameSplitter.length() + nameSplitterInsert.length();
            for (int i = 0; i < size; i++) {
                String key = keys.get(i).substring(splitter);
                // [Jul 22, 2013 8:48:50 AM] [amr.eladawy] [check if the key is a column or not]
                if (getColumn(key) != null) {
                    builder.append(key);
                    builder.append(",");
                } else {
                    DataPK pk = getPK(key);
                    if (pk != null) {
                        builder.append(key);
                        builder.append(",");
                    }
                }
            }
            builder.deleteCharAt(builder.length() - 1);
            builder.append(") VALUES (");
            for (int i = 0; i < size; i++) {
                String val = getSqlValue(keys.get(i).substring(splitter), values.get(i));
                if (val != null) {
                    builder.append(val);
                    builder.append(",");
                }
            }
            builder.deleteCharAt(builder.length() - 1);

            builder.append(")");
            return builder.toString();
        }
        return null;
    }

    /**
     * composes the update statement.
     *
     * @param eventArgs
     * @return
     * @throws DataException
     */
    protected String createUpdateStatement(String eventArgs) throws DataException {
        int size = postedInternalValues.size();
        if (size > 0) {

            String delimeter = InternalValueHolder.nameSplitter + InternalValueHolder.nameSplitterUpdate;
            int splitter = getId().length() + nameSplitter.length() + nameSplitterUpdate.length();
            StringBuilder builder = new StringBuilder("UPDATE ").append(getTableName()).append(" SET ");
            List<String> keys = getInternalFiledsNames(postedInternalValues);
            List<String> values = getInternalFiledsValues(postedInternalValues);
            for (int i = 0; i < size; i++) {
                String key = keys.get(i);
                if (key.indexOf(delimeter) > -1) {
                    String fieldName = key.substring(splitter);
                    // [Jul 8, 2014 10:24:03 AM] [Amr.ElAdawy] [check that this column is not read only]
                    if (getColumn(fieldName).getReadOnly())
                        continue;
                    String updatePart = getUpdatePart(fieldName, values.get(i));
                    if (updatePart != null) {
                        builder.append(updatePart);
                        builder.append(" , ");
                    }
                }
            }
            builder.delete(builder.length() - 2, builder.length() - 1);
            return builder.append(getWhereString(eventArgs)).toString();
        }
        return null;
    }

    /**
     * composes the update parte 'key=value' based on the type of the column.
     *
     * @param key
     * @param value
     * @return
     * @throws DataException
     */
    protected String getUpdatePart(String key, String value) throws DataException {
        String val = getSqlValue(key, value);
        if (val != null)
            return key + " = " + val;
        return null;
    }

    /**
     * gets the SQL value based on the data type Number or String or Date.
     *
     * @param key
     * @param value
     * @return
     */
    protected String getSqlValue(String key, String value) throws DataException {
        DataPK pk = getPK(key);
        if (pk != null && !StringUtility.isNullOrEmpty(pk.getType())) {
            if (pk.getType().equalsIgnoreCase(DataPK.SEQ_TYPE))
                return pk.getSequence() + ".NEXTVAL";
            else if (pk.getType().equalsIgnoreCase(DataPK.SQL_TYPE))
                return "(" + pk.getSql() + ")";
            else if (pk.getType().equalsIgnoreCase(DataPK.TRIGGER))
                return "1";
            return "";
        } else {
            DataColumn col = getColumn(key);
            if (col == null)
                return null;
            if (!StringUtility.isNullOrEmpty(value))
                value = value.replace("'", "''");
            else if (col.getRequired() || pk != null) {
                {
                    MessageFormat formatter = new MessageFormat("");
                    formatter.applyPattern(getRequiredtext());
                    throw new DataException(formatter.format(new String[]
                            {col.getText()}));
                }
            }
            StringBuilder builder = new StringBuilder(" ");
            String type = col.getType();
            if (type.equals(DataColumn.Date))
                builder.append("to_date('").append(value).append("','dd/MM/yyyy HH24:mi:ss')");
            else if (type.equals(DataColumn.Lookup)) {
                if (value.matches("\\d+"))
                    return value;
                else
                    return "'" + value + "'";
                //				builder.append("(").append(getLookups().get(col.getFK()).getReverseSql(value)).append(")");
            } else {
                if (type.equals(DataColumn.Varchar))
                    builder.append("'");
                builder.append(value);
                if (type.equals(DataColumn.Varchar))
                    builder.append("'");
            }
            return builder.toString();
        }
    }

    /**
     * gets a column by the field name declared on the HTML page.
     *
     * @param colName
     * @return
     */
    protected DataColumn getColumn(String colName) {
        DataColumn col = null;
        for (int i = 0; i < columns.size(); i++) {
            col = columns.get(i);
            if (col.getFieldName().equalsIgnoreCase(colName))
                return col;
        }
        return null;
    }

    /**
     * @param colName
     * @return
     */
    protected String getColumnDataType(String colName) {
        DataColumn col = getColumn(colName);
        if (col != null)
            return col.getType();
        return DataColumn.Varchar;
    }

    /**
     * @param colName
     * @return
     */
    protected boolean getColumnRequired(String colName) {
        DataColumn col = getColumn(colName);
        if (col != null)
            return col.getRequired();
        return false;
    }

    /**
     * composes the Where statement from PKS.
     *
     * @param eventArgs
     * @return
     */
    protected String getWhereString(String eventArgs) {
        StringBuffer buffer = new StringBuffer(" where 1=1 ");
        String[] args = eventArgs.split(pkSplitter);
        if (!pks.isEmpty()) {
            DataPK pk = null;
            for (int i = 0; i < pks.size(); i++) {
                pk = pks.get(i);
                // [Jun 6, 2009 7:01:56 AM] [amr.eladawy] if pk has a default value, then use it instead of posted data.
                String pkval = StringUtility.isNullOrEmpty(pk.getDefaultValue()) ? args[i] : pk.getDefaultValue();
                buffer.append(" and ").append(pk.getName()).append("=").append(pkval);
            }
        }
        return buffer.toString();
    }

    @SuppressWarnings("unchecked")
    public Hashtable<String, DataField> getRow(int rowIndex) {
        if (rows.isEmpty())
            dataBind();

        return (Hashtable<String, DataField>) rows.get(rowIndex);
    }

    public String getDataSource() {
        return getAttributeValue(dataSource);
    }

    public void setDataSource(String dataSourceVal) {
        setAttributeValue(dataSource, dataSourceVal);
    }

    public String getSql() {
        return getAttributeValue(sqlKey);
    }

    public void setSql(String sqlString) {
        setAttributeValue(sqlKey, sqlString);
    }

    public String getTableName() {
        return getAttributeValue(tableName);
    }

    public void setTableName(String att_value) {
        setAttributeValue(tableName, att_value);
    }

    public String getBindToClass() {
        return getAttributeValue(bindToClass);
    }

    public void setBindToClass(String bindToClass) {
        setAttributeValue(autoBindKey, bindToClass);
    }

    /**
     * get the selected row either as - an object filled with the coulmn values if the bindtoclass attribute is set - a hashtable
     * of the selected row otherwise
     *
     * @param index
     * @return Object
     */
    public Object getSelectedRow(String index) {
        int rowIndex = Integer.parseInt(index);
        Hashtable<String, DataField> row = getRow(rowIndex);
        if (!StringUtility.isNullOrEmpty(getBindToClass())) {
            try {
                return convertToObject(row);
            } catch (Throwable e) {
                logger.error("getSelectedRow(index=" + index + ")", e);
            }
        }
        return row;
    }

    /**
     * converts from MetaData to Object
     *
     * @param row
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    protected Object convertToObject(Hashtable<String, DataField> row) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return convertToObject(row, getBindToClass());
    }

    private Object convertToObject(Hashtable<String, DataField> row, String className) {
        try {
            Object dataClass = Class.forName(className).newInstance();
            // do reflection.
            Method[] methods = dataClass.getClass().getDeclaredMethods();
            for (Method method : methods) {
                String name = method.getName();
                String key = "";
                if (name.startsWith("set")) {
                    key = name.substring(3);
                    DataField dataField = row.get(key.toLowerCase());
                    if (dataField == null)
                        continue;
                    Object value = dataField.getValue();
                    try {
                        PropertyAccessor.invokeSetter(dataClass, method, value, getSimpleDateFormat());
                    } catch (Throwable e) {
                        logger.error("################ : key Not found: " + key + " " + e.getMessage());
                    }
                }
            }
            /*
             * // Adawy changed this to fixe the upper and lower casing. for (DataColumn column : columns) { if (column instanceof
             * DataColumnCommand) continue; String key = column.getFieldName(); DataField dataField = row.get(key.toLowerCase());
             * if (dataField == null) continue; Object value = dataField.getValue(); try { PropertyAccessor.setProperty(dataClass,
             * methods, key, value, getSimpleDateFormat()); } catch (Throwable e) { logger.error("################ : key: " + key
             * + " " + e.getMessage()); } }
             */
            return dataClass;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            logger.error("convertToObject(row=" + row + ")", e);
        }
        return null;
    }

    @Override
    protected Object getRowToRender(int index) {
        return rows.get(index);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Object> getSelectedRows() {
        if (this.rows.isEmpty())
            dataBind();
        List<Object> rowsReturned = new ArrayList<Object>();
        if (isSelectedAll()) {
            boolean selectAll = isAllSelected();
            try {
                List<Hashtable<String, DataField>> selectedRows = null;
                if (selectAll)
                    selectedRows = DAO.search(getDataSource(), getFinalSql(), 0, Integer.MAX_VALUE, false, null);
                else
                    selectedRows = (List<Hashtable<String, DataField>>) this.rows;

                if (StringUtility.isNullOrEmpty(getBindToClass()))
                    rowsReturned.addAll(selectedRows);
                else

                    for (Hashtable<String, DataField> row : selectedRows)
                        rowsReturned.add(convertToObject(row));
            } catch (Exception e) {
                logger.error("getSelectedRows()", e);
                return null;
            }
        } else {
            List<Integer> indeices = getSelectedIndices();
            for (Integer index : indeices)
                rowsReturned.add(getSelectedRow(String.valueOf(index)));
        }
        return rowsReturned;
    }

    public String getNavigationMode() {
        return getAttributeValue(navigationMode);
    }

    public void setNavigationMode(String mode) {
        setAttributeValue(navigationMode, mode);
    }

    @Override
    protected Object getRowToEdit() {
        return rows.get(selectedIndex);
    }

    @Override
    public String getEventArgs() {
        return composeEventArgs((Hashtable<String, DataField>) rows.get(selectedIndex));
    }

    private void generateColumns() {
        Hashtable<String, DataField> row = (Hashtable<String, DataField>) rows.get(0);
        TreeMap<Integer, DataField> orderedHeaders = new TreeMap<Integer, DataField>();
        Iterator<DataField> iterator = row.values().iterator();

        while (iterator.hasNext()) {
            DataField field = iterator.next();
            orderedHeaders.put(field.getIndex(), field);
        }
        for (DataField field : orderedHeaders.values()) {
            DataColumn column = getDataColumn(field);

            columns.add(column);
        }
    }

    private DataColumn getDataColumn(DataField field) {
        DataColumn column = new DataColumn();
        column.setFieldName(field.getOriginalName());
        column.setName(field.getName());
        column.setText(field.getName());
        column.setPage(page);
        column.setParent(this);
        column.setSortable(true);

        if (sortingColumn != null && sortingColumn.getFieldName().equals(column.getFieldName())) {
            // [Jan 18, 2013 12:53:22 AM] [Amr.ElAdawy] [set this column as sorting column]
            column.setSortDirection(sortingColumn.getSortDirection());
        }
        return column;
    }

    @Override
    protected List<?> createEmptyRows() {
        List<HashMap<String, DataField>> eRows = new ArrayList<HashMap<String, DataField>>();

        int size = getPageSize();
        for (int i = 0; i < size; i++)
            eRows.add(new HashMap<String, DataField>());

        return eRows;

    }

    public List<?> getDTList(String className) {
        if (!isBound())
            dataBind();
        List<Object> list = new ArrayList<Object>();

        for (int i = 0; i < getRowsCount(); i++) {
            try {
                list.add(convertToObject(getRow(i), className));
            } catch (Exception e) {
                logger.error("getDTList(String)", e);
            }
        }
        return list;
    }

}
