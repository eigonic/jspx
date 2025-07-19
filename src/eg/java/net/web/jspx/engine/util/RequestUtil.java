/**
 * 
 */
package eg.java.net.web.jspx.engine.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility for the HTTP request.
 * 
 * @author amr.eladawy
 * 
 */
public class RequestUtil
{
	private static final Logger logger = LoggerFactory.getLogger(RequestUtil.class);

	private RequestUtil()
	{
	}

	@SuppressWarnings("unchecked")
	public static HashMap<String, List<FileItem>> getParameters(HttpServletRequest request)
	{
		logger.debug("getParameters(request=" + request + ") - start");

		HashMap<String, List<FileItem>> parameters = new HashMap<String, List<FileItem>>();
		List<FileItem> allFileItems;
		try
		{
			allFileItems = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
			for (Iterator<FileItem> i = allFileItems.iterator(); i.hasNext();)
			{
				FileItem fileItem = i.next();
				// [24 Apr 2015 09:30:31] [aeladawy] [updated to get the array of parameters with the same name.]
				List<FileItem> fileItems= parameters.get(fileItem.getFieldName());
				if(fileItems== null)
					fileItems=new ArrayList<FileItem>();
				fileItems.add(fileItem);
				parameters.put(fileItem.getFieldName(), fileItems);
			}
		}
		catch (FileUploadException e)
		{
			logger.error("getParameters()", e);
		}
		return parameters;
	}
}
