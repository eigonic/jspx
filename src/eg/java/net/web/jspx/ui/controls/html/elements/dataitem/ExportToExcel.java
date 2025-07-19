/**
 * 
 */
package eg.java.net.web.jspx.ui.controls.html.elements.dataitem;

import java.util.ArrayList;
import java.util.List;

import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.ui.controls.WebControl;
import eg.java.net.web.jspx.ui.controls.attrbs.JspxAttribute;
import eg.java.net.web.jspx.ui.controls.html.elements.hidden.HiddenGenericWebControl;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.IAjaxSubmitter;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.ISubmitter;
import eg.java.net.web.jspx.ui.pages.Page;

/**
 * @author ahmed.abdelaal
 * 
 */
public class ExportToExcel extends HiddenGenericWebControl
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6184911435441697305L;
	public static final String EXCEL = "excel";
	public static final String CSV = "csv";
	private List<DataColumn> columnsList = new ArrayList<DataColumn>();
	public static String EXPORT_TO_EXCEL_ALL = "all";
	public static String EXPORT_TO_EXCEL_DISPLAYED = "displayed";
	public static String EXPORT_TO_EXCEL_SELECTED = "selected";

	public ExportToExcel()
	{
		super(TagFactory.ExportToExcel);
	}

	/**
	 * @param tagName
	 * @param page
	 */
	public ExportToExcel(Page page)
	{
		super(TagFactory.ExportToExcel, page);
	}

	protected static String filename = "filename";

	public String getFileName()
	{
		return getAttributeValue(filename);
	}

	public void setFileName(String name)
	{
		setAttributeValue(filename, name);
	}

	protected static String command = "command";

	public String getcommand()
	{
		return getAttributeValue(command);
	}

	public void setCommand(String commandName)
	{
		setAttributeValue(command, commandName);
	}

	@JspxAttribute
	public static String maxRows = "maxrows";

	public int getMaxRows()
	{
		String rows = getAttributeValue(maxRows);
		return StringUtility.isNullOrEmpty(rows) ? Integer.MAX_VALUE : Integer.parseInt(rows);
	}

	public void setMaxRows(int maxRowsValue)
	{
		setAttributeValue(maxRows, "" + maxRowsValue);
	}

	public static String rowsToExport = "rowstoexport";

	public String getRowsToExport()
	{

		String export = getAttributeValue(rowsToExport);

		return StringUtility.isNullOrEmpty(export) ? EXPORT_TO_EXCEL_DISPLAYED : export;
	}

	public void setRowsToExport(String rowsToExportText)
	{
		setAttributeValue(rowsToExport, rowsToExportText);
	}

	public void addControl(WebControl control)
	{
		super.addControl(control);
		if (control instanceof DataColumn)
			getColumns().add((DataColumn) control);
	}

	public void setColumns(List<DataColumn> columns)
	{
		this.columnsList = columns;
	}

	public List<DataColumn> getColumns()
	{
		return columnsList;
	}

	/**
	 * overridden to clone members that are not attributes.
	 */
	public WebControl clone(WebControl parent, Page page, ISubmitter submitter, IAjaxSubmitter ajaxSubmitter)
	{
		ExportToExcel exportToExcelControl = (ExportToExcel) super.clone(parent, page, submitter, ajaxSubmitter);
		for (WebControl control : exportToExcelControl.controls)
		{
			if (control instanceof DataColumn)
				exportToExcelControl.columnsList.add((DataColumn) control);
		}
		return exportToExcelControl;
	}

	@JspxAttribute
	protected String target = "target";

	public String getTarget()
	{
		return getAttributeValue(target);
	}

	public void setTarget(String targetValue)
	{
		setAttributeValue(target, targetValue);
	}

	@JspxAttribute
	public static String fileType = "fileType";

	public String getFileType()
	{
		String f = getAttributeValue(fileType);
		return StringUtility.isNullOrEmpty(f) ? EXCEL : f;
	}

	public void setFileType(String FileType)
	{
		setAttributeValue(fileType, FileType);
	}

}
