package eg.java.net.web.jspx.engine;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eg.java.net.web.jspx.engine.util.ResourceUtility;
import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.engine.util.imaging.ISkewImage;
import eg.java.net.web.jspx.engine.util.imaging.SkewImageProba;
import eg.java.net.web.jspx.ui.controls.html.elements.Captcha;

public class ResourceHandler extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	public static final String ResourcePrefix = "/jspxEmbededResources/";
	private static final long lastModified = System.currentTimeMillis();
	private static final String format = "EEE, dd MMM yyyy HH:mm:ss";

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		response.addHeader(RequestHandler.PoweredByHeader, "BAY JSPX " + RequestHandler.JSPXversion);
		if (isNotModified(request))
		{
			response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			return;
		}
		response.addHeader("Accept-Ranges", "bytes");
		response.addHeader("ETag", String.valueOf(lastModified));
		response.addHeader("Last-Modified", new SimpleDateFormat(format).format(new Date(lastModified)));
		String path = request.getRequestURI().toLowerCase();
		if (path.contains(ResourceUtility.PostBackScript.toLowerCase()))
		{
			response.setContentType(ResourceUtility.JavaScriptMime);
			ResourceUtility.writeBinaryResource(response.getOutputStream(), ResourceUtility.PostBackScript);
		}
		else if (path.contains(ResourceUtility.ValidationScript.toLowerCase()))
		{
			response.setContentType(ResourceUtility.JavaScriptMime);
			ResourceUtility.writeBinaryResource(response.getOutputStream(), ResourceUtility.ValidationScript);
		}
		else if (path.contains(ResourceUtility.AjaxScript.toLowerCase()))
		{
			response.setContentType(ResourceUtility.JavaScriptMime);
			ResourceUtility.writeBinaryResource(response.getOutputStream(), ResourceUtility.AjaxScript);
		}
		else if (path.contains(ResourceUtility.CalendarJS.toLowerCase()))
		{
			response.setContentType(ResourceUtility.JavaScriptMime);
			ResourceUtility.writeBinaryResource(response.getOutputStream(), ResourceUtility.CalendarJS);
		}
		else if (path.contains(ResourceUtility.CalendarCss.toLowerCase()))
		{
			response.setContentType(ResourceUtility.CssMime);
			ResourceUtility.writeBinaryResource(response.getOutputStream(), ResourceUtility.CalendarCss);
		}
		else if (path.contains(ResourceUtility.UtilScript.toLowerCase()))
		{
			response.setContentType(ResourceUtility.JavaScriptMime);
			ResourceUtility.writeBinaryResource(response.getOutputStream(), ResourceUtility.UtilScript);
		}
		else if (path.contains(ResourceUtility.JQueryScript.toLowerCase()))
		{
			response.setContentType(ResourceUtility.JavaScriptMime);
			ResourceUtility.writeBinaryResource(response.getOutputStream(), ResourceUtility.JQueryScript);
		}
		else if (path.contains(ResourceUtility.BootstrapScript.toLowerCase()))
		{
			response.setContentType(ResourceUtility.JavaScriptMime);
			ResourceUtility.writeBinaryResource(response.getOutputStream(), ResourceUtility.BootstrapScript);
		}
		else if (path.contains(ResourceUtility.TableHoverScript.toLowerCase()))
		{
			response.setContentType(ResourceUtility.JavaScriptMime);
			ResourceUtility.writeBinaryResource(response.getOutputStream(), ResourceUtility.TableHoverScript);
		}
		else if (path.contains(ResourceUtility.MaskedScript.toLowerCase()))
		{
			response.setContentType(ResourceUtility.JavaScriptMime);
			ResourceUtility.writeBinaryResource(response.getOutputStream(), ResourceUtility.MaskedScript);
		}
		else if (path.contains(ResourceUtility.LiveQueryScript.toLowerCase()))
		{
			response.setContentType(ResourceUtility.JavaScriptMime);
			ResourceUtility.writeBinaryResource(response.getOutputStream(), ResourceUtility.LiveQueryScript);
		}
		else if (path.contains(ResourceUtility.RatyScript.toLowerCase()))
		{
			response.setContentType(ResourceUtility.JavaScriptMime);
			ResourceUtility.writeBinaryResource(response.getOutputStream(), ResourceUtility.RatyScript);
		}
		else if (path.contains(ResourceUtility.GrowlScript.toLowerCase()))
		{
			response.setContentType(ResourceUtility.JavaScriptMime);
			ResourceUtility.writeBinaryResource(response.getOutputStream(), ResourceUtility.GrowlScript);
		}
		else if (path.contains(ResourceUtility.JQUIScript.toLowerCase()))
		{
			response.setContentType(ResourceUtility.JavaScriptMime);
			ResourceUtility.writeBinaryResource(response.getOutputStream(), ResourceUtility.JQUIScript);
		}
		else if (path.contains(ResourceUtility.JQUITimeScript.toLowerCase()))
		{
			response.setContentType(ResourceUtility.JavaScriptMime);
			ResourceUtility.writeBinaryResource(response.getOutputStream(), ResourceUtility.JQUITimeScript);
		}
		else if (path.contains(ResourceUtility.CalendarImg.toLowerCase()))
		{
			response.setContentType(ResourceUtility.GifMime);
			ResourceUtility.writeBinaryResource(response.getOutputStream(), ResourceUtility.CalendarImg);
		}
		else if (path.contains(ResourceUtility.AjaxLoaderImg.toLowerCase()))
		{
			response.setContentType(ResourceUtility.GifMime);
			ResourceUtility.writeBinaryResource(response.getOutputStream(), ResourceUtility.AjaxLoaderImg);
		}
		else if (path.contains(ResourceUtility.AjaxBG.toLowerCase()))
		{
			response.setContentType(ResourceUtility.JpgMime);
			ResourceUtility.writeBinaryResource(response.getOutputStream(), ResourceUtility.AjaxBG);
		}
		else if (path.contains(Captcha.CapatchaPrefix))
		{
			String imageName = path.substring(path.indexOf(Captcha.CapatchaPrefix) + Captcha.CapatchaPrefix.length() + 1, path.length() - 4);
			String passKeyStr = (String) request.getSession().getAttribute(imageName);
			if (!StringUtility.isNullOrEmpty(passKeyStr))
			{
				response.setContentType("image/jpeg");
				response.setHeader("Pragma", "no-cache");
				response.setHeader("Expires", "0");
				response.setHeader("Cache-Control", "no-cache");
				ISkewImage skewImage = new SkewImageProba();
				BufferedImage bufferedImage = skewImage.skewImage(passKeyStr);
				ImageIO.write(bufferedImage, "jpeg", response.getOutputStream());
			}
		}

		else if (path.contains(ResourceUtility.PlusB.toLowerCase()))
		{
			response.setContentType(ResourceUtility.PngMime);
			ResourceUtility.writeBinaryResource(response.getOutputStream(), ResourceUtility.PlusB);
		}
		else if (path.contains(ResourceUtility.MinuseB.toLowerCase()))
		{
			response.setContentType(ResourceUtility.PngMime);
			ResourceUtility.writeBinaryResource(response.getOutputStream(), ResourceUtility.MinuseB);
		}
		else if (path.contains(ResourceUtility.Excel.toLowerCase()))
		{
			response.setContentType(ResourceUtility.PngMime);
			ResourceUtility.writeBinaryResource(response.getOutputStream(), ResourceUtility.Excel);
		}
		else if (path.contains(ResourceUtility.Edit.toLowerCase()))
		{
			response.setContentType(ResourceUtility.PngMime);
			ResourceUtility.writeBinaryResource(response.getOutputStream(), ResourceUtility.Edit);
		}
		else if (path.contains(ResourceUtility.Delete.toLowerCase()))
		{
			response.setContentType(ResourceUtility.PngMime);
			ResourceUtility.writeBinaryResource(response.getOutputStream(), ResourceUtility.Delete);
		}
		else if (path.contains(ResourceUtility.SaveDB.toLowerCase()))
		{
			response.setContentType(ResourceUtility.PngMime);
			ResourceUtility.writeBinaryResource(response.getOutputStream(), ResourceUtility.SaveDB);
		}
		else if (path.contains(ResourceUtility.CancelDB.toLowerCase()))
		{
			response.setContentType(ResourceUtility.PngMime);
			ResourceUtility.writeBinaryResource(response.getOutputStream(), ResourceUtility.CancelDB);
		}
		else if (path.contains(ResourceUtility.AddDB.toLowerCase()))
		{
			response.setContentType(ResourceUtility.PngMime);
			ResourceUtility.writeBinaryResource(response.getOutputStream(), ResourceUtility.AddDB);
		}
		else if (path.contains(ResourceUtility.JspxCss.toLowerCase()))
		{
			response.setContentType(ResourceUtility.CssMime);
			ResourceUtility.writeBinaryResource(response.getOutputStream(), ResourceUtility.JspxCss);
		}
		else if (path.contains(ResourceUtility.JQUICss.toLowerCase()))
		{
			response.setContentType(ResourceUtility.CssMime);
			ResourceUtility.writeBinaryResource(response.getOutputStream(), ResourceUtility.JQUICss);
		}
		else if (path.contains(ResourceUtility.BootstrapCss.toLowerCase()))
		{
			response.setContentType(ResourceUtility.CssMime);
			ResourceUtility.writeBinaryResource(response.getOutputStream(), ResourceUtility.BootstrapCss);
		}
		else if (path.contains(ResourceUtility.BootstrapRTLCss.toLowerCase()))
		{
			response.setContentType(ResourceUtility.CssMime);
			ResourceUtility.writeBinaryResource(response.getOutputStream(), ResourceUtility.BootstrapRTLCss);
		}
		else if (path.contains(ResourceUtility.BootstrapResponsiveCss.toLowerCase()))
		{
			response.setContentType(ResourceUtility.CssMime);
			ResourceUtility.writeBinaryResource(response.getOutputStream(), ResourceUtility.BootstrapResponsiveCss);
		}
		else if (path.contains(ResourceUtility.BootstrapResponsiveRTLCss.toLowerCase()))
		{
			response.setContentType(ResourceUtility.CssMime);
			ResourceUtility.writeBinaryResource(response.getOutputStream(), ResourceUtility.BootstrapResponsiveRTLCss);
		}
		else if (path.contains(ResourceUtility.Error.toLowerCase()))
		{
			response.setContentType(ResourceUtility.PngMime);
			ResourceUtility.writeBinaryResource(response.getOutputStream(), ResourceUtility.Error);
		}
		else if (path.contains(ResourceUtility.Info.toLowerCase()))
		{
			response.setContentType(ResourceUtility.PngMime);
			ResourceUtility.writeBinaryResource(response.getOutputStream(), ResourceUtility.Info);
		}
		else if (path.contains(ResourceUtility.CancelOff.toLowerCase()))
		{
			response.setContentType(ResourceUtility.PngMime);
			ResourceUtility.writeBinaryResource(response.getOutputStream(), ResourceUtility.CancelOff);
		}
		else if (path.contains(ResourceUtility.CancelOn.toLowerCase()))
		{
			response.setContentType(ResourceUtility.PngMime);
			ResourceUtility.writeBinaryResource(response.getOutputStream(), ResourceUtility.CancelOn);
		}
		else if (path.contains(ResourceUtility.StarHalf.toLowerCase()))
		{
			response.setContentType(ResourceUtility.PngMime);
			ResourceUtility.writeBinaryResource(response.getOutputStream(), ResourceUtility.StarHalf);
		}
		else if (path.contains(ResourceUtility.StarOn.toLowerCase()))
		{
			response.setContentType(ResourceUtility.PngMime);
			ResourceUtility.writeBinaryResource(response.getOutputStream(), ResourceUtility.StarOn);
		}
		else if (path.contains(ResourceUtility.StarOff.toLowerCase()))
		{
			response.setContentType(ResourceUtility.PngMime);
			ResourceUtility.writeBinaryResource(response.getOutputStream(), ResourceUtility.StarOff);
		}
		else
		{
			response.setContentType(ResourceUtility.getMime(path));
			ResourceUtility.writeBinaryResource(response, path.substring(request.getContextPath().length() + ResourcePrefix.length()));
		}
	}

	/**
	 * checks if the request is for a resource that is not modified.
	 * @param request
	 * @return true if the resource is not modified
	 */
	private boolean isNotModified(HttpServletRequest request)
	{
		String lastMD = request.getHeader("If-Modified-Since");
		if (!StringUtility.isNullOrEmpty(lastMD))
		{
			Date lastMDVal;
			try
			{
				lastMDVal = new SimpleDateFormat(format).parse(lastMD);
			}
			catch (ParseException e)
			{
				return false;
			}
			if (lastMDVal.getTime() / 1000 >= lastModified / 1000)
				return true;
		}
		return false;
	}
}
