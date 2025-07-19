package eg.java.net.web.jspx.engine.data;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eg.java.net.web.jspx.engine.error.JspxException;
import eg.java.net.web.jspx.engine.util.PropertyAccessor;
import eg.java.net.web.jspx.ui.controls.html.elements.dataitem.DataColumn;
import eg.java.net.web.jspx.ui.controls.html.elements.dataitem.DataColumnCommand;
import eg.java.net.web.jspx.ui.controls.html.elements.dataitem.DataLookup;
import eg.java.net.web.jspx.ui.controls.html.elements.dataitem.DataTable;

public class ObjectToExcel
{
	private static final Logger logger = LoggerFactory.getLogger(ObjectToExcel.class);

	private HSSFWorkbook wb;
	private HSSFSheet sheet;
	private int index;
	private int sheetIndex = 1;
	private String sheetname = "sheet name";
	private SimpleDateFormat sdf = null;
	/**
	 * the max Rows can be on one sheet
	 */
	private static final int maxInt = 65527;

	public void init(String sheetname)
	{
		wb = new HSSFWorkbook();
		this.sheetname = sheetname;
		createNewSheet(sheetname);
	}

	private void createNewSheet(String sheetname)
	{
		index = 0;
		sheet = wb.createSheet(sheetname + " " + sheetIndex++);
	}

	public void addHeader(List<String> fieldNames)
	{
		HSSFRow row = sheet.createRow(index++);
		short i = 0;
		for (String fieldname : fieldNames)
			row.createCell(i++).setCellValue(new HSSFRichTextString(fieldname));
	}

	public <T> void addRows(List<T> list, List<DataColumn> fieldNames)
	{
		// [Feb 19, 2015 2:15:22 PM] [aeladawy] [check for empty headers]
		if (fieldNames == null || fieldNames.size() == 0)
		{
			HSSFRow row = sheet.createRow(0);
			if (list.size() > 0)
			{
				T o = list.get(0);
				logger.info("Empty headers, this is an auto generated columns in the Table");
				fieldNames = new ArrayList<DataColumn>(Collections.nCopies(((Hashtable<String, DataField>) o).values().size(), new DataColumn()));
				if (o instanceof Hashtable<?, ?>)
				{
					int x = 0;
					for (Iterator<DataField> i = ((Hashtable<String, DataField>) o).values().iterator(); i.hasNext();)
					{
						DataField field = (DataField) i.next();
						DataColumn column = new DataColumnCommand();
						column.setFieldName(field.getName());
						x = field.getIndex() - 1;
						fieldNames.set(x, column);
						row.createCell(x).setCellValue(new HSSFRichTextString(field.getName()));
					}
				}
				else
				{

				}
			}
		}
		for (T object : list)
			addRow(object, fieldNames);

	}

	@SuppressWarnings("unchecked")
	public void addRow(Object o, List<DataColumn> dataColumns)
	{
		if (index >= maxInt)
			createNewSheet(sheetname);
		try
		{
			if (o != null)
			{
				HSSFRow row = sheet.createRow(index++);
				short i = 0;

				for (DataColumn dataColumn : dataColumns)
				{
					// // [Oct 30, 2013 5:13:58 PM] [amr.eladawy] [skip datacolumn commands]
					// if (dataColumn instanceof DataColumnCommand)
					// continue;
					if (o instanceof Hashtable<?, ?>)
					{
						DataField fieldObject = ((Hashtable<String, DataField>) o).get(dataColumn.getFieldName().toLowerCase());
						Object fieldvalue = new String("");
						if (fieldObject != null)
							fieldvalue = fieldObject.getValue();
						if (dataColumn.getType().equalsIgnoreCase(DataColumn.Date)
								&& (fieldvalue instanceof Date || fieldvalue instanceof Timestamp || fieldvalue instanceof java.sql.Date))
						{
							sdf = dataColumn.getSimpleDateFormat();
							row.createCell(i++).setCellValue(new HSSFRichTextString(sdf.format((Date) fieldvalue)));

						}
						// [Jun 3, 2010 9:09:32 AM] [amr.eladawy] [this is make the Export to Excel to understand Data Lookup]
						else if (dataColumn.getType().equalsIgnoreCase(DataColumn.Lookup))
						{
							DataLookup lookup = ((DataTable) dataColumn.getParent()).getLookups().get(dataColumn.getFK());
							if (lookup == null)
								throw new JspxException("Data Lookup [" + dataColumn.getFK() + "] is not found, please make sure of the name");
							row.createCell(i++).setCellValue(lookup.lookup(fieldvalue.toString()).toString());
						}
						else
						{
							row.createCell(i++).setCellValue(new HSSFRichTextString(fieldvalue.toString()));
						}
					}
					else
					{

						Object resultObject = new String("");
						Object property = PropertyAccessor.getProperty(o, dataColumn.getFieldName());

						if (property != null)
							resultObject = property.toString();

						if (dataColumn.getType().equalsIgnoreCase(DataColumn.Date)
								&& (resultObject instanceof Date || resultObject instanceof Timestamp || resultObject instanceof java.sql.Date))
						{
							sdf = new SimpleDateFormat(dataColumn.getDateFormat());
							row.createCell(i++).setCellValue(new HSSFRichTextString(sdf.format((Date) resultObject)));

						}
						else
						{
							row.createCell(i++).setCellValue(new HSSFRichTextString(resultObject.toString()));
						}

					}
				}
			}
		}
		catch (RuntimeException e)
		{
			logger.error("addRow(o=" + o + ", dataColumns=" + dataColumns + ")", e);
		}
	}

	public void persist(OutputStream outputStream) throws IOException
	{
		wb.write(outputStream);
	}
}
