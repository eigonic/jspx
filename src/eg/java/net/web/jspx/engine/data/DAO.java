/**
 *
 */
package eg.java.net.web.jspx.engine.data;

import eg.java.net.web.jspx.engine.RequestHandler;
import eg.java.net.web.jspx.engine.error.JspxException;
import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.ui.controls.html.elements.dataitem.DataColumn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.io.OutputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * @author amr.eladawy
 *
 */
public class DAO {
    private static final Logger logger = LoggerFactory.getLogger(DAO.class);
    private static final int writeToFileCount = 1000;
    private static final Hashtable<Integer, DataField> cache = new Hashtable<Integer, DataField>();

    public static List<Hashtable<String, DataField>> search(String datasource, String sql, int start, int size, boolean getTotalCount,
                                                            List<Integer> totalCount) throws Exception {
        if (StringUtility.isNullOrEmpty(datasource))
            throw new JspxException("Data Source cannot be empty");
        List<Hashtable<String, DataField>> rows = new ArrayList<Hashtable<String, DataField>>();
        Hashtable<String, DataField> cols = null;
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            DataSource ds = ((DataSource) new InitialContext().lookup(datasource));
            if (ds == null)
                throw new JspxException("Cannot lookup Data Source [" + datasource + "]");
            connection = ds.getConnection();
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            // [17 Aug 2015 12:45:35] [aeladawy] [set the timeout to 5 minutes]
            try {
                statement.setQueryTimeout(5 * 60);
            } catch (Exception e) {
            }
            if (RequestHandler.SHOW_SQL)
                logger.info("search() - initial sql=    " + sql);
            sql = sanitizeSql(sql);

            if (RequestHandler.SHOW_SQL)
                logger.info("search() -executing -->  sql=     " + sql);
            resultSet = statement.executeQuery(sql);
            int counter = 0;
            String fieldName;
            String fieldOriginalName;
            String fieldType;
            if (resultSet.absolute(start + 1)) {
                int colCount = resultSet.getMetaData().getColumnCount();
                do {
                    cols = new Hashtable<String, DataField>();
                    for (int i = 1; i <= colCount; i++) {
                        fieldOriginalName = resultSet.getMetaData().getColumnLabel(i).trim();
                        fieldName = fieldOriginalName.toLowerCase();
                        fieldType = resultSet.getMetaData().getColumnTypeName(i).trim();
                        if (fieldType.equals("TIMESTAMP"))
                            cols.put(fieldName, new DataField(fieldName, resultSet.getTimestamp(i), fieldType, i, fieldOriginalName));
                        else
                            cols.put(fieldName, new DataField(fieldName, resultSet.getObject(i), fieldType, i, fieldOriginalName));
                    }
                    rows.add(cols);
                }
                while (resultSet.next() && (++counter < size));
            }
            // [Jul 17, 2011 7:07:21 PM] [amr.eladawy] [getting the count of the rows along with the results.]
            if (getTotalCount) {
                totalCount.add(totalCount(connection, sql));
            } else if (totalCount != null)
                totalCount.add(Integer.MAX_VALUE);
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException e) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
            }
        }
        return rows;
    }

    /**
     * [18 Nov 2015 09:33:33]
     * @author aeladawy
     * @param sql
     * @return
     */
    private static String sanitizeSql(String sql) {
        // [Aug 19, 2012 6:54:14 PM] [Amr.ElAdawy] [remove 1=1]
        sql = sql.replace("AND 1\\s*=\\s*1", "");
        sql = sql.replaceAll("AND[ ]*1\\s*=\\s*1", "");
        sql = sql.replace("and 1\\s*=\\s*1", "");
        sql = sql.replaceAll("and[ ]*1\\s*=\\s*1", "");
        sql = sql.trim();
        return sql;
    }

    private static DataField getFieldFromCache(int index) {
        return cache.get(index);
    }

    private static void initFieldsCache(int colCount) {
        System.out.println("init field cache...");
        cache.clear();
        for (int i = 0; i < writeToFileCount * colCount; i++) {
            cache.put(i, new DataField(null, i));
        }
    }

    private static DataField getColumn(String name, Object value, String type, int index, String originalName, int loopIndex) {
        DataField f = new DataField(name, value);// getFieldFromCache(loopIndex);
        f.setType(type);
        f.setIndex(index);
        f.setOriginalName(originalName);
        return f;
    }

    public static void writeSearchResultToStream(String datasource, String sql, int start, int size, ObjectToCSV csv,
                                                 List<DataColumn> exportedColumns, OutputStream out) throws Exception {
        if (StringUtility.isNullOrEmpty(datasource))
            throw new JspxException("Data Source cannot be empty");

        List<String> exportColNames = new ArrayList<String>();
        for (DataColumn dataColumn : exportedColumns) {
            exportColNames.add(dataColumn.getFieldName().toLowerCase());
        }
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            DataSource ds = ((DataSource) new InitialContext().lookup(datasource));
            if (ds == null)
                throw new JspxException("Cannot lookup Data Source [" + datasource + "]");
            connection = ds.getConnection();
            statement = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            if (RequestHandler.SHOW_SQL)
                logger.info("search() - sql=" + sql);
            sql = sanitizeSql(sql);
            //			sql = "SELECT * FROM (" + sql + ") WHERE ROWNUM<" + (size + 3);

            if (RequestHandler.SHOW_SQL)
                logger.info("search() -executed  sql=" + sql);
            resultSet = statement.executeQuery();
            resultSet.setFetchSize(writeToFileCount + 1);
            int counter = 1;
            String fieldName;
            String fieldOriginalName;
            String fieldType;
            Hashtable<String, DataField> row = new Hashtable<String, DataField>();
            DataField col = null;
            int loopIndex = 0;
            if (resultSet.next()) {
                int colCount = resultSet.getMetaData().getColumnCount();
                do {
                    row.clear();
                    for (int i = 1; i <= colCount; i++) {
                        fieldOriginalName = resultSet.getMetaData().getColumnLabel(i).trim();
                        fieldName = fieldOriginalName.toLowerCase();
                        if (exportColNames.contains(fieldName)) {

                            fieldType = resultSet.getMetaData().getColumnTypeName(i).trim();
                            if (fieldType.equals("TIMESTAMP"))
                                col = getColumn(fieldName, resultSet.getTimestamp(i), fieldType, i, fieldOriginalName, loopIndex);
                            else
                                col = getColumn(fieldName, resultSet.getObject(i), fieldType, i, fieldOriginalName, loopIndex);

                            row.put(fieldName, col);
                            loopIndex++;
                        }
                    }
                    csv.addRow(row, exportedColumns);
                    if (counter % writeToFileCount == 0) {
                        csv.flushRows(out);
                        // System.out.println("CC: writing to CSV file, written count = " + counter + " out of " + size);
                        loopIndex = 0;
                        //csv.resetRowCache();
                    }
                }
                while (resultSet.next() && (++counter < size));
                csv.flushRows(out);
                System.out.println("Flushing rows to CSV >> completed all records " + counter);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException e) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
            }
        }
    }

    public static int totalCount(Connection connection, String sql) throws Exception {
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("select count(1) from (" + sql + ") totalResult");
            if (resultSet.next())
                return resultSet.getInt(1);
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException e) {
            }
        }
        return 0;
    }

    public static int totalCount(String datasource, String sql) throws Exception {
        Connection connection = null;
        try {
            connection = ((DataSource) new InitialContext().lookup(datasource)).getConnection();
            return totalCount(connection, sql);
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
            }
        }
    }

    public static int executeNoneQuery(String datasource, String sql) throws Exception {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = ((DataSource) new InitialContext().lookup(datasource)).getConnection();
            statement = connection.createStatement();
            logger.debug("executeNoneQuery() - sql=" + sql);
            return statement.executeUpdate(sql);
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException e) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
            }
        }
    }

    /**
     * loads the lookup control from the DB. returns hashmap of the key-value pairs. and also loads the keys List with keys data.
     *
     * @param datasource
     * @param sql
     * @param keys
     * @return
     */
    public static Hashtable<String, String> loadLookup(String datasource, String sql, List<String> keys) {
        Hashtable<String, String> hashtable = new Hashtable<String, String>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = ((DataSource) new InitialContext().lookup(datasource)).getConnection();
            statement = connection.createStatement();
            if (RequestHandler.SHOW_SQL)
                logger.info("loadLookup() - sql=" + sql);
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                hashtable.put(resultSet.getString(1), resultSet.getString(2));
                keys.add(resultSet.getString(1));
            }
        } catch (Exception e) {
            logger.error("Failed to load lookup", e);
        } finally {
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException e) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
            }
        }
        return hashtable;
    }

    /**
     * looks up for a certain field based on the given sql
     *
     * @param datasource
     *            the data source
     * @param sql
     *            the statement to be executed
     * @param defaultObject
     *            the default value to be returned
     * @param keyField
     *            the name of the field to be looked up
     * @return
     */
    public static Object lookup(String datasource, String sql, Object defaultObject, String keyField) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = ((DataSource) new InitialContext().lookup(datasource)).getConnection();
            statement = connection.createStatement();
            if (RequestHandler.SHOW_SQL)
                logger.info("lookup() - sql=" + sql);
            resultSet = statement.executeQuery(sql);
            if (resultSet.next())
                return resultSet.getObject(keyField);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException e) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
            }
        }
        return defaultObject;
    }

    public static long getSeqNextVal(String datasource, String seqName) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = ((DataSource) new InitialContext().lookup(datasource)).getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT ".concat(seqName).concat(".NEXTVAL FROM DUAL"));
            if (resultSet.next())
                return resultSet.getLong(1);
        } catch (Exception e) {
            logger.error("Failed to get sequence value", e);
        } finally {
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException e) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
            }
        }
        return 0;
    }

    /**
     * gets list of objects out of sql statement.
     *
     * @param datasource
     * @param sql
     * @return
     */
    public static List<Object> getAutoCompleteList(String datasource, String sql) {
        List<Object> res = new ArrayList<Object>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = ((DataSource) new InitialContext().lookup(datasource)).getConnection();
            statement = connection.createStatement();
            logger.debug("getting auto complete sql :" + sql);
            resultSet = statement.executeQuery(sql);
            while (resultSet.next())
                res.add(resultSet.getObject(1));
        } catch (Exception e) {
            logger.error("Failed to execute Auto Complete SQL", e);
        } finally {
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException e) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
            }
        }
        return res;
    }
}
