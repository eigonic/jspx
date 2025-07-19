package eg.java.net.web.jspx.engine.data;

import eg.java.net.web.jspx.engine.error.JspxException;
import eg.java.net.web.jspx.engine.util.PropertyAccessor;
import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.ui.controls.html.elements.dataitem.DataColumn;
import eg.java.net.web.jspx.ui.controls.html.elements.dataitem.DataLookup;
import eg.java.net.web.jspx.ui.controls.html.elements.dataitem.DataTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

public class ObjectToCSV {
    private static final Logger logger = LoggerFactory.getLogger(ObjectToCSV.class);

    private int printIndex = 0;
    private final SimpleDateFormat sdf = null;
    private final List<String> headers = new ArrayList<String>();
    private final List<List<String>> rows = new ArrayList<List<String>>();
    private final Hashtable<String, SimpleDateFormat> dfMap = new Hashtable<String, SimpleDateFormat>();
    private final Hashtable<Integer, List<String>> rowCache = new Hashtable<Integer, List<String>>();

    public void addHeader(List<String> fieldNames) {
        headers.addAll(fieldNames);
    }

    public <T> void addRows(List<T> list, List<DataColumn> fieldNames) {
        for (T object : list)
            addRow(object, fieldNames);
    }

    private SimpleDateFormat getDf(String colName) {
        return dfMap.get(colName);
    }

    private void initDfMap(List<DataColumn> dataColumns) {
        if (dfMap.isEmpty()) {
            for (DataColumn d : dataColumns) {
                dfMap.put(d.getFieldName(), new SimpleDateFormat(d.getDateFormat()));
            }
        }
    }

    private List<String> getRowFromCache(int index) {
        return rowCache.get(index);
    }

    public void initRowCache(int size) {
        if (rowCache.isEmpty()) {
            for (int j = 0; j < size; j++) {
                rowCache.put(j, new ArrayList<String>());
            }
        }
    }

    public void resetRowCache() {
        for (int j = 0; j < rowCache.size(); j++) {
            rowCache.get(j).clear();
        }
    }

    @SuppressWarnings("unchecked")
    public void addRow(Object o, List<DataColumn> dataColumns) {
        try {
            if (o != null) {
                initDfMap(dataColumns);
                List<String> row = new ArrayList<String>();
                //				List<String> row = getRowFromCache(rows.size());

                for (DataColumn dataColumn : dataColumns) {
                    if (o instanceof Hashtable<?, ?>) {
                        DataField fieldObject = ((Hashtable<String, DataField>) o).get(dataColumn.getFieldName().toLowerCase());
                        Object fieldvalue = "";
                        if (fieldObject != null)
                            fieldvalue = fieldObject.getValue();
                        if (dataColumn.getType().equalsIgnoreCase(DataColumn.Date)
                                && (fieldvalue instanceof Date || fieldvalue instanceof Timestamp || fieldvalue instanceof java.sql.Date)) {
                            row.add(getDf(dataColumn.getFieldName()).format((Date) fieldvalue));

                        } else if (dataColumn.getType().equalsIgnoreCase(DataColumn.Lookup)) {
                            DataLookup lookup = ((DataTable) dataColumn.getParent()).getLookups().get(dataColumn.getFK());
                            if (lookup == null)
                                throw new JspxException("Data Lookup [" + dataColumn.getFK() + "] is not found, please make sure of the name");
                            row.add(lookup.lookup(fieldvalue.toString()).toString());
                        } else {
                            row.add(fieldvalue.toString());
                        }
                    } else {

                        // Object resultObject = new String("");
                        Object property = PropertyAccessor.getProperty(o, dataColumn.getFieldName());

                        if (dataColumn.getType().equalsIgnoreCase(DataColumn.Date) && property != null) {
                            row.add(getDf(dataColumn.getFieldName()).format((Date) property));

                        } else if (property != null) {
                            row.add(property.toString());
                        } else {
                            row.add("");
                        }

                    }
                }
                rows.add(row);
            }
        } catch (RuntimeException e) {
            logger.error("addRow(o=" + o + ", dataColumns=" + dataColumns + ")", e);
        }
    }

    /**
     * write the current added rows to the outputstream, and flush the rows from memory, you can call again after adding new rows
     *
     * @param outputStream
     * @throws IOException
     */
    public void flushRows(OutputStream outputStream) throws IOException {
        System.out.println("Flushing rows to CSV >> count = " + printIndex);
        if (printIndex == 0)
            outputStream.write((StringUtility.join(headers, ",") + "\n").getBytes());
        for (List<String> row : rows) {
            printIndex++;
            outputStream.write((StringUtility.join(row, ",") + "\n").getBytes());
        }
        rows.clear();
    }

}
