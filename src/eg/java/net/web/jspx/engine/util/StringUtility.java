/**
 *
 */
package eg.java.net.web.jspx.engine.util;

import eg.java.net.web.jspx.engine.el.ComplexEL;
import eg.java.net.web.jspx.engine.el.ResourceBundleEL;
import eg.java.net.web.jspx.engine.util.bean.JspxBeanUtility;
import eg.java.net.web.jspx.ui.controls.attrbs.Attribute;
import eg.java.net.web.jspx.ui.pages.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Utility Class for the Strings.
 *
 * @author amr.eladawy
 *
 */
public class StringUtility {
    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(StringUtility.class);
    public static String ELregex = "\\$\\{[^\\$]*\\}";

    private StringUtility() {
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * joins a String [] into one String with given joiner.
     *
     * @param strs
     * @return
     */
    public static String join(String[] strs, String joiner) {
        if (strs == null || strs.length == 0)
            return "";
        StringBuilder buffer = new StringBuilder(strs[0]);
        for (int i = 1; i < strs.length; i++)
            buffer.append(joiner).append(strs[i]);
        return buffer.toString();
    }

    public static String joinWithQuotes(String[] strs, String joiner) {
        if (strs == null || strs.length == 0)
            return "";
        StringBuilder buffer = new StringBuilder("'");
        buffer.append(strs[0]).append("'");
        for (int i = 1; i < strs.length; i++)
            buffer.append(joiner).append("'").append(strs[i]).append("'");
        return buffer.toString();
    }

    /**
     * joins a List<String> into one String with given joiner.
     *
     * @param strs
     * @return
     */
    public static String join(List<String> strs, String joiner) {
        if (strs == null || strs.isEmpty())
            return "";
        StringBuilder buffer = new StringBuilder(strs.get(0));
        for (int i = 1; i < strs.size(); i++)
            buffer.append(joiner).append(strs.get(i));
        return buffer.toString();
    }

    public static String join(HashMap<String, String> hashMap, String outerJoiner, String innerJoiner) {
        if (hashMap == null || hashMap.isEmpty())
            return "";
        Iterator<String> i = hashMap.keySet().iterator();
        String key = i.next();
        StringBuilder buffer = new StringBuilder(hashString(key));
        buffer.append(innerJoiner);
        buffer.append(hashString(hashMap.get(key)));
        while (i.hasNext()) {
            buffer.append(outerJoiner);
            key = i.next();
            buffer.append(hashString(key));
            buffer.append(innerJoiner);
            buffer.append(hashString(hashMap.get(key)));
        }
        return buffer.toString();

    }

    public static String hashString(String key) {
        if (isNullOrEmpty(key))
            return "";
        StringBuffer hashed = new StringBuffer();
        for (int i = 0; i < key.length(); i++) {
            int a = key.charAt(i);
            if (i != 0)
                hashed.append("$");
            hashed.append(a);
        }
        return hashed.toString();
    }

    public static String unHashString(String key) {
        if (isNullOrEmpty(key))
            return "";
        StringBuffer unhashed = new StringBuffer();
        StringTokenizer st = new StringTokenizer(key, "$");
        while (st.hasMoreTokens()) {
            int a = Integer.parseInt(st.nextToken());
            unhashed.append((char) a);
        }
        return unhashed.toString();
    }

    public static String join(HashMap<String, HashMap<String, String>> hashMap, String mostOuterJoiner, String innerJoiner, String outerJoiner,
                              String mostInnerJoiner) {
        if (hashMap == null || hashMap.isEmpty())
            return "";
        Iterator<String> i = hashMap.keySet().iterator();
        String key = i.next();
        StringBuilder buffer = new StringBuilder(hashString(key));
        buffer.append(innerJoiner);
        buffer.append(join(hashMap.get(key), outerJoiner, mostInnerJoiner));
        while (i.hasNext()) {
            buffer.append(mostOuterJoiner);
            key = i.next();
            buffer.append(hashString(key));
            buffer.append(innerJoiner);
            buffer.append(join(hashMap.get(key), outerJoiner, mostInnerJoiner));
        }
        return buffer.toString();

    }

    public static HashMap<String, HashMap<String, String>> split(String input, String mostOuterJoiner, String innerJoiner, String outerJoiner,
                                                                 String mostInnerJoiner) {
        HashMap<String, HashMap<String, String>> viewState = new HashMap<String, HashMap<String, String>>();
        try {
            String[] items = input.split(mostOuterJoiner);
            String key;
            HashMap<String, String> value;
            String[] itemParts = null;
            for (int i = 0; i < items.length; i++) {
                itemParts = items[i].split(innerJoiner);
                key = itemParts[0];
                try {
                    value = split(itemParts[1], mostInnerJoiner, outerJoiner);
                } catch (Exception e) {
                    value = new HashMap<String, String>();
                }
                viewState.put(unHashString(key), value);
            }
        } catch (RuntimeException e) {
            //			System.out.println("split excption: " + e);
            throw e;
        }
        return viewState;
    }

    public static HashMap<String, String> split(String input, String innerJoiner, String outerJoiner) {
        String[] items = input.split(outerJoiner);
        String[] itemParts = null;
        HashMap<String, String> map = new HashMap<String, String>();
        for (int i = 0; i < items.length; i++) {
            itemParts = items[i].split(innerJoiner);
            map.put(unHashString(itemParts[0]), unHashString(itemParts[1]));
        }
        return map;
    }

    public static String convertClassNameToJspxName(String className) {
        return "/".concat(className.replace('.', '/')).concat(".jspx");
    }

    public static String getJspxName(String className) {
        return "/".concat(className.substring(className.lastIndexOf('.') + 1)).concat(".jspx");
    }

    /**
     * joins the keys of a hashmap in to one String.
     *
     * @param map
     * @param joiner
     * @return
     */
    public static String joinKeys(HashMap<String, Object> map, String joiner) {
        StringBuilder buffer = new StringBuilder();
        Iterator<String> i = map.keySet().iterator();
        if (i.hasNext())
            buffer.append(i.next());
        while (i.hasNext()) {
            buffer.append(joiner);
            buffer.append(i.next());
        }
        return buffer.toString();
    }

    /**
     * joins the values of a hashmap in to one String.
     *
     * @param map
     * @param joiner
     * @return
     */
    public static String joinValues(HashMap<String, Object> map, String joiner) {
        StringBuilder buffer = new StringBuilder();
        Iterator<String> i = map.keySet().iterator();
        if (i.hasNext())
            buffer.append(map.get(i.hasNext()));
        while (i.hasNext()) {
            buffer.append(joiner);
            buffer.append(map.get(i.next()));
        }
        return buffer.toString();
    }

    public static String joinStyle(Hashtable<String, Attribute> style) {
        StringBuilder builder = new StringBuilder();
        for (Attribute att : style.values())
            builder.append(att.getKey()).append(":").append(att.getValue()).append("; ");
        return builder.toString();
    }

    /**
     * checks whether the passed valus is an EL or not
     * @param value
     * @return
     */
    public static boolean isEL(String value) {
        if (value != null) {
            String el = value.trim();
            return !StringUtility.isNullOrEmpty(el) && el.charAt(0) == '$' && el.charAt(1) == '{' && el.charAt(el.length() - 1) == '}';
        }
        return false;
    }

    public static boolean containsEL(String value) {
        return value != null && value.contains("${");
    }

    /**
     * Creates ComplexEl from plan String
     * @param value
     * @return
     */
    protected static ComplexEL createComplexEL(String value) {
        ComplexEL complexEL = new ComplexEL();
        // the String after replacing the EL with place holders
        StringBuilder holder = new StringBuilder();
        StringTokenizer stringTokenizer = new StringTokenizer(value, ELregex, true);
        int index = 1;
        String token;
        while (stringTokenizer.hasMoreElements()) {
            token = (String) stringTokenizer.nextElement();
            if (token.equals("$")) {
                // read the '{'
                String nextToken = (String) stringTokenizer.nextElement();
                if ("{".equals(nextToken)) {
                    holder.append("%").append(index++).append("$s");
                    // set the EL
                    complexEL.getValues().add("${" + stringTokenizer.nextElement().toString().trim() + "}");
                    // read the '}'
                    try {
                        // [17 May 2015 08:46:59] [aeladawy] [don't fail if closing bracket is not found.]
                        stringTokenizer.nextElement();
                    } catch (Exception e) {
                        logger.error("Missing closing curly braket } at the EL");
                    }
                } else
                    holder.append(token).append(nextToken);
            } else
                holder.append(token);
        }

        complexEL.setString(holder.toString());
        return complexEL;
    }

    /**
     * Central place for evaluating the Complex EL.
     * @param value
     * @param page
     * @param dateformat
     * @return
     */
    public static String evaluateComplexVlaue(String value, Page page, String dateformat) {
        String newValue = null;
        if (StringUtility.containsEL(value) && page != null && page.request != null && page.session != null) {
            ComplexEL complexEL = StringUtility.createComplexEL(value);
            for (int i = 0; i < complexEL.getValues().size(); i++) {
                newValue = null;
                String el = complexEL.getValues().get(i);
                Object object = ELUtility.evaluateEL(el, page);

                // now use the calculated value
                if (object == null) {
                    if (ResourceBundleEL.inBundle(el, page))
                        complexEL.setString(complexEL.getString().replace("%" + (i + 1) + "$s", el));
                    else
                        complexEL.setString(complexEL.getString().replace("%" + (i + 1) + "$s", ""));
                } else {
                    // [Feb 23, 2010 2:29:53 PM] [Amr.ElAdawy] [if the returned object is a date object then apply the date format]
                    String valString = null;
                    if (object instanceof Date || object instanceof Timestamp || object instanceof java.sql.Date) {

                        try {
                            if (StringUtility.isNullOrEmpty(dateformat))
                                dateformat = page.getJspxBeanByName(JspxBeanUtility.getBeanName(el)).getJspxBean().dateformat();
                        } catch (Exception e) {
                        }
                        try {
                            valString = new SimpleDateFormat(dateformat).format((Date) object);
                        } catch (Exception e) {
                            // [Oct 2, 2011 1:54:44 PM] [amr.eladawy] [in case there was an exception, then display the date as is]
                            if (object != null)
                                valString = object.toString();
                        }
                    } else
                        valString = object.toString();
                    complexEL.setString(complexEL.getString().replace("%" + (i + 1) + "$s", valString));
                }
            }
            newValue = complexEL.getString();
        }
        return newValue;
    }

    /**
     * extracts the file name of the a given String,
     * @param fileName
     * @return
     */
    public static String getFileName(String fileName) {
        if (!isNullOrEmpty(fileName)) {
            int index = fileName.lastIndexOf("\\");
            if (index > -1 && index < fileName.length())
                fileName = fileName.substring(index + 1);
        }
        return fileName;
    }

    /**
     *
     * sanitize Data
     * @author Mohamed.IHassan
     * @param str
     * @return
     */
    public static String sanitizedData(String str) {
        return str != null ? ((str.replace("<", " &lt; ")).replace(">", " &gt; ")) : str;
    }
}
