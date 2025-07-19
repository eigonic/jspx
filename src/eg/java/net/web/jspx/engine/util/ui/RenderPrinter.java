package eg.java.net.web.jspx.engine.util.ui;

import java.io.PrintWriter;
import java.io.Writer;

public class RenderPrinter extends PrintWriter
{

	public RenderPrinter(Writer out)
	{
		super(out);
	}

	public void write(byte[] data)
	{
		write(new String(data));
	}

}
