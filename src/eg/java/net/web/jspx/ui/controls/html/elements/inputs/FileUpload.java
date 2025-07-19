/**
 * 
 */
package eg.java.net.web.jspx.ui.controls.html.elements.inputs;

import org.apache.commons.fileupload.FileItem;

import eg.java.net.web.jspx.ui.controls.html.elements.Input;

/**
 * @author amr.eladawy
 * 
 */
public class FileUpload extends Input
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3828599920593199938L;

	public FileUpload()
	{
		super();
		setType(Input.File);
	}

	protected String fileName;

	protected String fileFullName;

	protected String fileType;

	protected byte[] fileData;

	protected FileItem fileItem;

	public FileItem getFileItem()
	{
		return fileItem;
	}

	public void setFileItem(FileItem fileItem)
	{
		this.fileItem = fileItem;
	}

	public String getFileFullName()
	{
		return fileFullName;
	}

	public void setFileFullName(String fileFullName)
	{
		this.fileFullName = fileFullName;
	}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public String getFileType()
	{
		return fileType;
	}

	public void setFileType(String fileType)
	{
		this.fileType = fileType;
	}

	public byte[] getFileData()
	{
		return fileData;
	}

	public void setFileData(byte[] fileData)
	{
		this.fileData = fileData;
	}

}
