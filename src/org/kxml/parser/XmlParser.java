/*
 * kXML The contents of this file are subject to the Enhydra Public License Version 1.1 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the License on the Enhydra web site (
 * http://www.enhydra.org/ ). Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the License for the specific terms governing rights and
 * limitations under the License. The Initial Developer of kXML is Stefan Haustein. Copyright (C) 2000, 2001 Stefan
 * Haustein, D-46045 Oberhausen (Rhld.), Germany. All Rights Reserved. Contributor(s): Paul Palaszewski, Wilhelm
 * Fitzpatrick, Eric Foster-Johnson, Michael Angel, Jan Andrle
 */

package org.kxml.parser;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Vector;

import org.kxml.Xml;
import org.kxml.io.ParseException;

import eg.java.net.web.jspx.ui.controls.attrbs.Attribute;

/**
 * A simple, pull based "Common XML" parser. Attention: This class has been renamed from DefaultParser for consitency with the
 * org.kxml.io package.
 */

public class XmlParser extends AbstractXmlParser
{

	static final String UNEXPECTED_EOF = "Unexpected EOF";

	char[] buf;

	boolean eof;

	int bufPos;

	int bufCount;

	Reader reader;

	boolean relaxed = true;

	int line = 1;

	int column = 1;

	Vector qNames = new Vector();

	boolean immediateClose = false;

	StartTag current;

	/** The next event. May be null at startup. */

	protected ParseEvent next;

	int peekChar() throws IOException
	{
		if (eof)
			return -1;
		// System.out.println("NOT EOF");
		if (bufPos >= bufCount)
		{
			// System.out.println("bufPos >= bufCount");
			if (buf.length == 1)
			{
				// System.out.println("buf.length == 1");
				int c = reader.read();
				if (c == -1)
				{
					eof = true;
					return -1;
				}

				bufCount = 1;
				buf[0] = (char) c;
			}
			else
			{
				bufCount = reader.read(buf, 0, buf.length);

				if (bufCount == -1)
				{
					eof = true;
					return -1;
				}
			}

			bufPos = 0;
		}

		return buf[bufPos];
	}

	int readChar() throws IOException
	{
		int p = peekChar();
		bufPos++;
		column++;
		if (p == 10)
		{
			line++;
			column = 1;
		}
		return p;
	}

	void skipWhitespace() throws IOException
	{
		while (!eof && peekChar() <= ' ')
			readChar();
	}

	public String readName() throws IOException
	{

		int c = readChar();
		// adawy add '@'
		if (c != '@' && c != '%' && c < 128 && c != '_' && c != ':' && (c < 'a' || c > 'z') && (c < 'A' || c > 'Z'))
			throw new DefaultParserException("name expected!", null);

		StringBuffer buf = new StringBuffer();
		buf.append((char) c);

		while (!eof)
		{
			c = peekChar();

			if (c != '@' && c != '%' && c < 128 && c != '_' && c != '-' && c != ':' && c != '.' && (c < '0' || c > '9') && (c < 'a' || c > 'z')
					&& (c < 'A' || c > 'Z'))

				break;

			buf.append((char) readChar());
		}

		return buf.toString();
	}

	/**
	 * Reads chars to the given buffer until the given stopChar is reached. The stopChar itself is not consumed.
	 */

	public StringBuffer readTo(char stopChar, StringBuffer buf) throws IOException
	{

		while (!eof && peekChar() != stopChar)
			buf.append((char) readChar());

		return buf;
	}

	/**
	 * HOME MADE METHOD, Reads chars to the given charArray until the given stopChar is reached. The stopChar itself is not
	 * consumed.
	 */

	public int readToCharArray(char[] buffer, char stopChar) throws IOException
	{
		int index = 0;

		while (index < buffer.length && !eof && peekChar() != stopChar)
		{
			buffer[index] = (char) readChar();
			index++;
		}

		return index;
	}

	/** creates a new Parser based on the give reader */

	class DefaultParserException extends ParseException
	{
		private static final long serialVersionUID = 1497971487667546497L;

		DefaultParserException(String msg, Exception chained)
		{
			super(msg, chained, XmlParser.this.line, XmlParser.this.column);
		}
	}

	public XmlParser(Reader reader) throws IOException
	{
        this(reader, 1024 * 1024);
	}

	public XmlParser(Reader reader, int bufSize) throws IOException
	{

		this.reader = reader; // new LookAheadReader (reader);
		buf = new char[bufSize];
	}

	public String resolveCharacterEntity(String name) throws IOException
	{

		throw new DefaultParserException("Undefined: &" + name + ";", null);
	}

	/* precondition: &lt;!- consumed */

	ParseEvent parseComment() throws IOException
	{

		StringBuffer buf = new StringBuffer();

		if (readChar() != '-')
			throw new DefaultParserException("'-' expected", null);

		int cnt;
		int lst;

		while (true)
		{
			readTo('-', buf);

			if (readChar() == -1)
				throw new DefaultParserException(UNEXPECTED_EOF, null);

			cnt = 0;

			do
			{
				lst = readChar();
				cnt++; // adds one more, but first is not cnted
			}
			while (lst == '-');

			if (lst == '>' && cnt >= 2)
				break;

			while (cnt-- > 0)
				buf.append('-');

			buf.append((char) lst);
		}

		while (cnt-- > 2)
			buf.append('-');

		return new ParseEvent(Xml.COMMENT, buf.toString());
	}

	/* precondition: &lt! consumed */

	ParseEvent parseDoctype() throws IOException
	{

		StringBuffer buf = new StringBuffer();
		int nesting = 1;

		while (true)
		{
			int i = readChar();
			switch (i)
			{
			case -1:
				throw new DefaultParserException(UNEXPECTED_EOF, null);
			case '<':
				nesting++;
				break;
			case '>':
				if ((--nesting) == 0)
					return new ParseEvent(Xml.DOCTYPE, buf.toString());
				break;
			}
			buf.append((char) i);
		}
	}

	ParseEvent parseCData() throws IOException
	{

		StringBuffer buf = readTo('[', new StringBuffer());

		if (!buf.toString().equals("CDATA"))
			throw new DefaultParserException("Invalid CDATA start!", null);

		buf.setLength(0);

		readChar(); // skip '['

		int c0 = readChar();
		int c1 = readChar();

		while (true)
		{
			int c2 = readChar();

			if (c2 == -1)
				throw new DefaultParserException(UNEXPECTED_EOF, null);

			if (c0 == ']' && c1 == ']' && c2 == '>')
				break;

			buf.append((char) c0);
			c0 = c1;
			c1 = c2;
		}

		return new ParseEvent(Xml.TEXT, buf.toString());
	}

	/* precondition: &lt;/ consumed */

	ParseEvent parseEndTag() throws IOException
	{

		skipWhitespace();
		String name = readName();
		skipWhitespace();

		if (readChar() != '>')
			throw new DefaultParserException("'>' expected", null);

		int last = qNames.size();
		while (true)
		{
			if (last == 0)
			{
				// [Jan 15, 2011 4:35:51 PM] [Amr.ElAdawy] [this means that the closing tag found is not a closing for any of the
				// above opened tags ]
				if (relaxed)
					throw new DefaultParserException("The end tag </" + name + "> has no starting tag", null);
				// return new ParseEvent(Xml.END_DOCUMENT, null);
				throw new DefaultParserException("tagstack empty parsing </" + name + ">", null);
			}
			String qName = (String) qNames.elementAt(--last);
			qNames.removeElementAt(last);

			// [Jan 15, 2011 4:31:45 PM] [Amr.ElAdawy] [matching the tag name is case in sensitive]
			if (qName.equalsIgnoreCase(name))
				break;
            if (!relaxed)
				throw new DefaultParserException("StartTag <" + qName + "> does not match end tag </" + name + ">", null);
			current = current.parent;
		}

		Tag result = new Tag(Xml.END_TAG, current, current.namespace, current.name);

		current = current.parent;

		return result;
	}

	/** precondition: <? consumed */

	ParseEvent parsePI() throws IOException
	{
		StringBuffer buf = new StringBuffer();
		readTo('?', buf);
		readChar(); // ?

		while (peekChar() != '>')
		{
			buf.append('?');

			int r = readChar();
			if (r == -1)
				throw new DefaultParserException(UNEXPECTED_EOF, null);

			buf.append((char) r);
			readTo('?', buf);
			readChar();
		}

		readChar(); // consume >

		return new ParseEvent(Xml.PROCESSING_INSTRUCTION, buf.toString());
	}

	StartTag parseStartTag() throws IOException
	{

        String qname = readName();

		// System.out.println ("name: ("+name+")");

		List<Attribute> attributes = null;
		immediateClose = false;

		while (true)
		{
			skipWhitespace();

			int c = peekChar();

			if (c == '/')
			{
				immediateClose = true;
				readChar();
				skipWhitespace();
				if (readChar() != '>')
					throw new DefaultParserException("illegal element termination", null);
				break;
			}

			if (c == '>')
			{
				readChar();
				break;
			}

			if (c == -1)
				throw new DefaultParserException(UNEXPECTED_EOF, null);

			String attrName = readName();

			if (attrName.isEmpty())
				throw new DefaultParserException("illegal char / attr", null);

			skipWhitespace();

			if (readChar() != '=')
				throw new DefaultParserException("Attribute name [" + attrName + "] must be followed by [=] ", null);

			skipWhitespace();
			int delimiter = readChar();

			if (delimiter != '\'' && delimiter != '"')
			{
				if (!relaxed)
					throw new DefaultParserException("<" + qname + ">: invalid delimiter: " + (char) delimiter, null);

				delimiter = ' ';
			}

			StringBuffer buf = new StringBuffer();
			// [Mar 29, 2012 4:10:27 PM] [amr.eladawy] [call the new method to get the attribute value]
			// [Mar 29, 2012 3:54:09 PM] [amr.eladawy] [changed to support having & in attributes]
			// readText(buf, (char) delimiter);
			readAttributeTextValue(buf, (char) delimiter);
			if (attributes == null)
				attributes = new Vector();
			attributes.add(new Attribute(null, attrName, buf.toString()));

			if (delimiter != ' ')
				readChar(); // skip endquote
		}

		try
		{
			current = new StartTag(current, Xml.NO_NAMESPACE, qname, attributes, immediateClose, processNamespaces);
		}
		catch (Exception e)
		{
			throw new DefaultParserException(e.toString(), e);
		}

		// System.out.println ("tag: ("+next+")");

		if (!immediateClose)
			qNames.addElement(qname);

		return current;
	}

	/**
	 * 
	 * @author amr.eladawy
	 * @param buf
	 * @param delimiter
	 * @return
	 * @throws IOException
	 */
	public int readAttributeTextValue(StringBuffer buf, char delimiter) throws IOException
	{
		String code = readTo(delimiter, new StringBuffer()).toString();
		// [Nov 5, 2013 4:44:43 AM] [Amr.ElAdawy] [fix issue detected by find bug]
		code = code.replace("&lt;", "<").replace("&gt;", ">").replace("&apos;", "'").replace("&quot;", "\"").replace("&;", "").replace("&amp;", "&");
		buf.append(code);
		if (code.trim().isEmpty())
			return Xml.WHITESPACE;
		return Xml.TEXT;
	}

	/**
	 * it wasn't public B4
	 * 
	 * @param buf
	 * @param delimiter
	 * @return
	 * @throws IOException
	 */
	public int readText(StringBuffer buf, char delimiter) throws IOException
	{

		int type = Xml.WHITESPACE;
		int nextChar;

		while (true)
		{
			nextChar = peekChar();

			if (nextChar == -1 || nextChar == delimiter || (delimiter == ' ' && (nextChar == '>' || nextChar < ' ')))
				break;

			readChar();
			// System.out.println("a new char is read");
			// [Apr 1, 2012 11:39:39 AM] [amr.eladawy] [I think that in case of HMTL, the text should be parsed as is and the
			// browser will
			// [Apr 1, 2012 11:40:00 AM] [amr.eladawy] [do the conversion. Special chars should not considered as HTML tags]

			type = Xml.TEXT;
			buf.append((char) nextChar);
			if (false)
			{
				if (nextChar == '&')
				{
					String code = readTo(';', new StringBuffer()).toString();
					readChar();

					if (code.charAt(0) == '#')
					{
						nextChar = (code.charAt(1) == 'x' ? Integer.parseInt(code.substring(2), 16) : Integer.parseInt(code.substring(1)));

						if (nextChar > ' ')
							type = Xml.TEXT;

						buf.append((char) nextChar);
					}
					else
					{
						if (code.equals("lt"))
							buf.append('<');
						else if (code.equals("gt"))
							buf.append('>');
						else if (code.equals("apos"))
							buf.append('\'');
						else if (code.equals("quot"))
							buf.append('"');
						else if (code.equals("amp"))
							buf.append('&');
						else if (code.indexOf("nbsp") >= 0)
							buf.append("&nbsp;");
						else if (code.indexOf("copy") >= 0)
							buf.append("&copy;");
						// [Jun 21, 2009 12:45:39 PM] [amr.eladawy] [support for &&]
						else if (code.equals("&"))
							buf.append("&&");
						// [Jun 21, 2009 12:56:16 PM] [amr.eladawy] [if it is not recognized, then consider it as text]
						else
							// buf.append(resolveCharacterEntity(code));
							buf.append(code);

						type = Xml.TEXT;
					}
				}
				else
				{
					if (nextChar > ' ')
						type = Xml.TEXT;
					buf.append((char) nextChar);
				}
			}
		}

		return type;
	}

	/**
	 * it wasn't public B4
	 * 
	 * @param buf
	 * @param delimiter
	 * @return
	 * @throws IOException
	 */
	public int readText(StringBuffer buf, String endString) throws IOException
	{

		int type = Xml.TEXT;
		int nextChar;
		int i = 0;
		String uperCase = endString.toUpperCase();
		String lowerCase = endString.toLowerCase();

		while (true)
		{
			nextChar = peekChar();

			if (nextChar == -1)
				break;

			boolean match = false;
			if (nextChar == lowerCase.charAt(i) || nextChar == uperCase.charAt(i))
			{
				// i++;
				match = true;
				int ch = nextChar;
				int j = 0;
				StringBuffer subBuf = new StringBuffer();
				for (j = 0; j < lowerCase.length(); j++)
				{
					if (ch != lowerCase.charAt(j) && ch == uperCase.charAt(j))
					{
						match = false;
						break;
					}
					subBuf.append((char) ch);
					readChar();
					ch = peekChar();
				}
				if (!match)
				{
					for (int k = 0; k < j; k++)
					{
						readChar();
						buf.append((char) nextChar);
					}
				}

			}
            if (match)
				return type;

			readChar();
			buf.append((char) nextChar);
			if (i == endString.length())
				break;
		}
        return type;
	}

	/**
	 * this method is of my own addition <br>
	 * It reads text to a given char[] until the buffer is filled or until the delimeter is reached, or end of file
	 * 
	 * @author Amr.Alhossary
	 * @param buf
	 *            the char[] to fill
	 * @param delimiter
	 *            the character tillwhich it reads
	 * @return the actual number of chars read
	 * @throws IOException
	 */
	public int readTextIntoCharArray(char[] buffer, char delimiter) throws IOException
	{

		int nextChar;
		int index = 0;

		while (index < buffer.length)
		{

			nextChar = peekChar();
			if (nextChar == -1 /* that means if EOF */
					|| nextChar == delimiter || (delimiter == ' ' && (nextChar == '>' || nextChar < ' ')))
				break;

			readChar();

			if (nextChar == '&')
			{
				String code = readTo(';', new StringBuffer()).toString();
				readChar();

				if (code.charAt(0) == '#')
				{
					nextChar = (code.charAt(1) == 'x' ? Integer.parseInt(code.substring(2), 16) : Integer.parseInt(code.substring(1)));

					buffer[index] = (char) nextChar;
				}
				else
				{
					if (code.equals("lt"))
						buffer[index] = '<';
					else if (code.equals("gt"))
						buffer[index] = '>';
					else if (code.equals("apos"))
						buffer[index] = '\'';
					else if (code.equals("quot"))
						buffer[index] = '"';
					else if (code.equals("amp"))
						buffer[index] = '&';
					else
						resolveCharacterEntity(code);
				}
			}
			else
			{
				buffer[index] = (char) nextChar;
			}
			index++;
		}

		return index;
	}

	/** precondition: &lt; consumed */

	ParseEvent parseSpecial() throws IOException
	{

		switch (peekChar())
		{
		case -1:
			throw new DefaultParserException(UNEXPECTED_EOF, null);

		case '!':
			readChar();
			switch (peekChar())
			{
			case '-':
				readChar();
				return parseComment();

			case '[':
				readChar();
				return parseCData();

			default:
				return parseDoctype();
			}

		case '?':
			readChar();
			return parsePI();

		case '/':
			readChar();
			return parseEndTag();

		case '%':
			readChar();
			return parseScriptlet();
		default:
			return parseStartTag();
		}
	}

	/**
	 * @author amr.eladawy parses the <% %>
	 * @throws IOException
	 */
	private ParseEvent parseScriptlet() throws IOException
	{
		StringBuffer buf = new StringBuffer();
		readTo('%', buf);
		readChar(); // %
		readChar(); // >
		return new ParseEvent(Xml.SCRIPTLET, buf.toString());
	}

	public ParseEvent read() throws IOException
	{
		// System.out.println(next);
		if (next == null)
			peek();

		ParseEvent result = next;
		next = null;
		return result;
	}

	public ParseEvent peek() throws IOException
	{

		if (next == null)
		{

			if (immediateClose)
			{
				next = new Tag(Xml.END_TAG, current, current.namespace, current.name);
				current = current.getParent();
				immediateClose = false;
			}
			else
			{
				switch (peekChar())
				{

				case '<':
					readChar();
					next = parseSpecial();
					break;

				case -1:
					if (current != null && !relaxed)
						throw new DefaultParserException("End tag missing for: " + current, null);
					next = new ParseEvent(Xml.END_DOCUMENT, null);
					break;

				default:
				{
					StringBuffer buf = new StringBuffer();
					int type = readText(buf, '<');
					next = new ParseEvent(type, buf.toString());
				}
				}
			}
		}
		return next;
	}

	/**
	 * default is false. Setting relaxed true allows CHTML parsing
	 */

	public void setRelaxed(boolean relaxed)
	{
		this.relaxed = relaxed;
	}

	public int getLineNumber()
	{
		return line;
	}

	public int getColumnNumber()
	{
		return column;
	}

}
