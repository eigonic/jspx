/**
 * 
 */
package eg.java.net.web.jspx.engine.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility for the recourses.
 * 
 * @author amr.eladawy
 * 
 */
public class ResourceUtility
{
	public static String PostBackScript = "js/jspx-postback.js";
	public static String ValidationScript = "js/jspx-validation.js";
	public static String CalendarImg = "imgs/cal.gif";
	public static String AjaxLoaderImg = "imgs/ajax-loader.gif";
	public static String CalendarJS = "js/datetimepicker.js";
	public static String CalendarCss = "css/calendar.css";
	public static String GrowlCss = "css/jquery.jgrowl.css";
	public static String JQUICss = "css/jquery-ui-1.9.1.custom.min.css";
	public static String BootstrapCss = "css/bootstrap.css";
	public static String BootstrapRTLCss = "css/bootstrap-rtl.css";
	public static String BootstrapResponsiveCss = "css/bootstrap-responsive-ltr.css";
	public static String BootstrapResponsiveRTLCss = "css/bootstrap-responsive-rtl.css";

	public static String AjaxScript = "js/jspx-ajax.js";
	public static String UtilScript = "js/jspx-util.js";
	public static String JQueryScript = "js/jquery-1.8.3.min.js";
	public static String BootstrapScript = "js/bootstrap.min.js";
	public static String TableHoverScript = "js/jquery.tablehover.pack.js";
	public static String MaskedScript = "js/jquery.maskedinput-1.3.min.js";
	public static String LiveQueryScript = "js/jquery.livequery.min.js";
	public static String RatyScript = "js/jquery.raty.min.js";
	public static String GrowlScript = "js/jquery.jgrowl_minimized.js";
	public static String JQUIScript = "js/jquery-ui-1.9.1.custom.min.js";
	public static String JQUITimeScript = "js/jquery-ui-timepicker-addon.js";
	public static String AjaxBG = "imgs/ajaxBG.png";
	public static String Error = "imgs/error.png";
	public static String Info = "imgs/info.png";
	public static String PlusB = "imgs/ps.png";
	public static String MinuseB = "imgs/ms.png";

	public static String CancelOff = "imgs/rate/cancel-off.png";
	public static String CancelOn = "imgs/rate/cancel-on.png";
	public static String StarHalf = "imgs/rate/star-half.png";
	public static String StarOff = "imgs/rate/star-off.png";
	public static String StarOn = "imgs/rate/star-on.png";
	public static String Edit = "imgs/glyphicons_150_edit.png";
	public static String Delete = "imgs/glyphicons_207_remove_2.png";
	public static String SaveDB = "imgs/glyphicons_444_floppy_saved.png";
	public static String CancelDB = "imgs/glyphicons_445_floppy_remove.png";
	public static String AddDB = "imgs/glyphicons_141_database_plus.png";
	public static String Excel = "imgs/excel.png";

	// MIME
	public static String JavaScriptMime = "text/javascript";
	public static String GifMime = "image/gif";
	public static String JpgMime = "image/jpeg";
	public static String CssMime = "text/css";
	public static String PngMime = "image/png";

	private static final Logger logger = LoggerFactory.getLogger(ResourceUtility.class);
	public static final String JspxCss = "css/jspx.css";

	private static HashMap<String, String> resources = new HashMap<String, String>();

	private static HashMap<String, Resource> binaryResources = new HashMap<String, Resource>();

	private ResourceUtility()
	{
	}

	public static String getResource(String resouceName)
	{
		String resource = resources.get(resouceName);
		if (resource == null)
		{
			logger.info("getResponseFile(String) - The response file (" + resouceName + ") is not cached, getting it from the file system");

			StringBuffer response = new StringBuffer();
			try
			{
				BufferedReader br = new BufferedReader(new InputStreamReader(getLocalResourceStream(resouceName)));
				String temp = null;
				while ((temp = br.readLine()) != null)
					response.append(temp).append("\r\n");
				br.close();
			}
			catch (FileNotFoundException e)
			{
				logger.error("File".concat(resouceName).concat(" Was not found"));
				logger.error("getResponseFile(String)", e);
				return "";
			}
			catch (IOException e)
			{
				logger.error("IO Error in  ".concat(resouceName));
				logger.error("getResponseFile(String)", e);
				return "";
			}
			resource = response.toString();
			resources.put(resouceName, resource);
		}
		return resource;
	}

	/**
	 * reads the inputstream as one String
	 * 
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	public static String readResource(InputStream inputStream) throws IOException
	{
		StringBuffer response = new StringBuffer();
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		String temp = null;
		while ((temp = br.readLine()) != null)
			response.append(temp).append("\r\n");
		br.close();
		return response.toString();

	}

	protected static InputStream getLocalResourceStream(String resourceName)
	{
		return ResourceUtility.class.getResourceAsStream(new StringBuffer("/").append("resources/").append(resourceName).toString());
	}

	public static InputStream getResourceStream(String resourceName, ServletContext context)
	{
		InputStream is = ResourceUtility.class.getResourceAsStream(resourceName);
		if (is == null)
			is = context.getResourceAsStream(resourceName);
		return is;
	}

	public static void writeBinaryResource(HttpServletResponse response, String resourceName)
	{
		Resource resource = getBinaryResource(resourceName);
		try
		{
			response.setContentLength(resource.size);
			writeBinaryResource(response.getOutputStream(), resource);
		}
		catch (IOException e)
		{
			logger.error("writeBinaryResource(response=" + response + ", resourceName=" + resourceName + ")", e);
		}
	}

	public static void writeBinaryResource(OutputStream outputStream, String resourceName)
	{
		try
		{
			writeBinaryResource(outputStream, getBinaryResource(resourceName));
		}
		catch (IOException e)
		{
			logger.error("writeBinaryResource(outputStream=" + outputStream + ", resourceName=" + resourceName + ")", e);
		}
	}

	public static Resource getBinaryResource(String resourceName)
	{
		Resource resource = binaryResources.get(resourceName);
		if (resource != null)
		{
			return resource;
		}

		List<byte[]> data;
		int size = 0;
		int count;
		InputStream inputStream = getLocalResourceStream(resourceName);
		data = new ArrayList<byte[]>();
		int s = 10240;
		byte[] buffer = new byte[s];
		try
		{
			while ((count = inputStream.read(buffer, 0, s)) != -1)
			{
				if (count < s)
				{
					byte[] buffer2 = new byte[count];
					System.arraycopy(buffer, 0, buffer2, 0, count);
					data.add(buffer2);
				}
				else
					data.add(buffer);
				buffer = new byte[s];
				size += count;
			}
		}
		catch (IOException e)
		{
			logger.error("writeBinaryResource( resourceName=" + resourceName + ")", e);
		}
		finally
		{
			try
			{
				inputStream.close();
			}
			catch (Exception e)
			{
				logger.error("writeBinaryResource( resourceName=" + resourceName + ")", e);
			}
		}
		resource = new Resource(data, size);
		binaryResources.put(resourceName, resource);
		return resource;
	}

	public static void writeBinaryResource(OutputStream outputStream, Resource resource) throws IOException
	{
		BufferedOutputStream stream = new BufferedOutputStream(outputStream);
		for (byte[] bs : resource.data)
			stream.write(bs);
		stream.flush();
	}

	@SuppressWarnings("unused")
	private static byte[] convertToByteArray(List<Byte> list)
	{
		byte[] bytes = new byte[list.size()];
		for (int i = 0; i < bytes.length; i++)
			bytes[i] = list.get(i).byteValue();
		return bytes;
	}

	public static String getMime(String resourceURL)
	{
		if (resourceURL.endsWith("png"))
			return PngMime;
		else if (resourceURL.endsWith("gif"))
			return GifMime;
		else if (resourceURL.endsWith("jpeg") || resourceURL.endsWith("jpg"))
			return JpgMime;
		else if (resourceURL.endsWith("js"))
			return JavaScriptMime;
		else if (resourceURL.endsWith("css"))
			return CssMime;
		return "";
	}

	static class Resource
	{

		public Resource(List<byte[]> data, int size)
		{
			this.data = data;
			this.size = size;
		}

		List<byte[]> data = new ArrayList<byte[]>();
		int size = 0;
	}

}
