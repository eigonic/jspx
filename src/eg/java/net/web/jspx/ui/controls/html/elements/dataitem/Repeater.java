package eg.java.net.web.jspx.ui.controls.html.elements.dataitem;

import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.ELUtility;
import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.engine.util.ui.Render;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.WebControl;
import eg.java.net.web.jspx.ui.controls.attrbs.Attribute;
import eg.java.net.web.jspx.ui.controls.attrbs.JspxAttribute;
import eg.java.net.web.jspx.ui.controls.html.elements.hidden.HiddenGenericWebControl;
import eg.java.net.web.jspx.ui.pages.Page;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class Repeater extends HiddenGenericWebControl {

    /**
     *
     */
    private static final long serialVersionUID = -5282832537487303409L;
    @JspxAttribute
    protected static String listKey = "list";
    protected List<Attribute> runtimeAttributes = new ArrayList<Attribute>();
    @JspxAttribute
    protected String changeId = "changeid";
    @JspxAttribute
    protected String rowCount = "rowCount";
    @JspxAttribute
    protected String colCount = "colCount";
    @JspxAttribute
    protected String onRenderCell = "onRenderCell";
    @JspxAttribute
    protected String headerClass = "headerClass";
    @JspxAttribute
    protected String onRenderTable = "onRenderTable";
    @JspxAttribute
    protected String onRenderHeader = "onRenderHeader";
    @JspxAttribute
    protected String onRenderRow = "onRenderRow";
    @JspxAttribute
    protected String onRenderColumn = "onRenderColumn";
    @JspxAttribute
    protected String objectName = "var";
    @JspxAttribute
    protected String itemList = "itemlist";
    List<Object> list = new ArrayList<Object>();

    public Repeater() {
        super(TagFactory.Repeater);
    }

    public Repeater(String tagName) {
        super(tagName);
    }

    public Repeater(String tagName, Page page) {
        super(tagName, page);
    }

    @Override
    public void render(RenderPrinter outputStream) throws Exception {

        if (!StringUtility.isNullOrEmpty(getRowCount())) {
            outputStream.write(Render.startTag);
            outputStream.write(TagFactory.Table);
            renderAttributes(outputStream);
            outputStream.write(Render.endTag);
            outputStream.write(Render.newLine);
            for (int row = 0; row < Integer.parseInt(getRowCount()); row++) {
                if (row == 0) {
                    outputStream.write(Render.startTag);
                    outputStream.write(TagFactory.TableHeader);
                    fireEvent(getOnRenderHeader(), outputStream, 0, 0);
                    outputStream.write(Render.endTag);
                    outputStream.write(Render.newLine);

                    outputStream.write(Render.startTag);
                    outputStream.write(TagFactory.TableRow);
                    outputStream.write(Render.endTag);
                    outputStream.write(Render.newLine);
                } else {
                    if (row == 1) {
                        outputStream.write(Render.startTag);
                        outputStream.write(TagFactory.TableBody);
                        outputStream.write(Render.endTag);
                        outputStream.write(Render.newLine);
                    }
                    outputStream.write(Render.startTag);
                    outputStream.write(TagFactory.TableRow);
                    fireEvent(getOnRenderRow(), outputStream, row, 0);
                    outputStream.write(Render.endTag);
                    outputStream.write(Render.newLine);
                }
                int colSpan = 1;
                for (int col = 0; col < Integer.parseInt(getColCount()); col++) {
                    if (--colSpan == 0) {
                        if (row == 0) {
                            //for first row, render the first column as <th>
                            outputStream.write(Render.startTag);
                            outputStream.write(TagFactory.TableHead);
                            fireEvent(getOnRenderColumn(), outputStream, row, col);
                            colSpan = getColSpanFromAttributes();
                            outputStream.write(Render.endTag);
                            outputStream.write(Render.newLine);
                        } else {
                            outputStream.write(Render.startTag);
                            outputStream.write(TagFactory.TableColumn);
                            fireEvent(getOnRenderColumn(), outputStream, row, col);
                            colSpan = getColSpanFromAttributes();
                            outputStream.write(Render.endTag);
                            outputStream.write(Render.newLine);
                        }
                        fireEvent(getOnRenderCell(), outputStream, row, col);
                        if (row == 0) {
                            outputStream.write(Render.newLine);
                            outputStream.write(Render.startEndTag);
                            outputStream.write(TagFactory.TableHead);
                            outputStream.write(Render.endTag);
                        } else {
                            outputStream.write(Render.newLine);
                            outputStream.write(Render.startEndTag);
                            outputStream.write(TagFactory.TableColumn);
                            outputStream.write(Render.endTag);
                        }
                    }
                }

                outputStream.write(Render.newLine);
                Render.renderRowEnd(outputStream);

                if (row == 0) {
                    outputStream.write(Render.newLine);
                    outputStream.write(Render.startEndTag);
                    outputStream.write(TagFactory.TableHeader);
                    outputStream.write(Render.endTag);
                    outputStream.write(Render.newLine);
                } else {
                    outputStream.write(Render.newLine);
                    outputStream.write(Render.startEndTag);
                    outputStream.write(TagFactory.TableColumn);
                    outputStream.write(Render.endTag);
                }
                if (row == Integer.parseInt(getRowCount()) - 1) {
                    outputStream.write(Render.newLine);
                    outputStream.write(Render.startEndTag);
                    outputStream.write(TagFactory.TableBody);
                    outputStream.write(Render.endTag);
                    outputStream.write(Render.newLine);
                }
            }
            outputStream.write(Render.newLine);
            outputStream.write(Render.startEndTag);
            outputStream.write(TagFactory.Table);
            outputStream.write(Render.endTag);
        } else {
            String oName = getObjectName();
            bind();
            int i = 0;
            for (Object object : list) {
                changeIds(i);
                if (!StringUtility.isNullOrEmpty(oName)) {
                    page.request.setAttribute(Page.RepeaterNamePrefix + oName, object);
                    // [Jul 16, 2013 1:08:45 PM] [Amr.ElAdawy] [adding the repeater object to the jexl context]
                    ELUtility.addObjectToJexlMap(page, oName, object);
                }
                renderChildren(outputStream);
                i++;
            }
        }
    }

    protected void fireEvent(String methodName, RenderPrinter outputStream, int row, int col)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        try {

            runtimeAttributes = new ArrayList<Attribute>();
            this.page.getClass().getDeclaredMethod(methodName, WebControl.class, RenderPrinter.class, Integer.class, Integer.class).invoke(page, this,
                    outputStream, row, col);
            renderRuntimeAttributes(outputStream);
        } catch (Exception e) {

        }
    }

    private void renderRuntimeAttributes(RenderPrinter outputStream) {
        outputStream.write(" ");
        for (Attribute att : runtimeAttributes) {
            String key = att.getKey();
            if (!StringUtility.isNullOrEmpty(key))
                att.render(outputStream, page);
        }
    }

    private int getColSpanFromAttributes() {
        int colSpan = 1;
        for (Attribute att : runtimeAttributes) {
            if (att.getKey().equalsIgnoreCase("colspan")) {
                int i = Integer.parseInt(att.getValue(page).trim());
                colSpan = i < 1 ? colSpan : i;
                break;
            }
        }
        return colSpan;
    }

    /**
     * Overridden to clone the controls before rendering.
     */
    public void renderChildren(RenderPrinter outputStream) throws Exception {
        // [Aug 13, 2014 3:35:53 PM] [Amr.ElAdawy] [clone the controls before rendering to prevent overriding the attributes]
        for (WebControl control : controls)
            if (control != null)
                control.clone(this, page, getMySubmitter(), getMyAjaxSubmitter()).render(outputStream);
    }

    private void changeIds(int counter) {
        String changId = getAttributeValue(changeId);
        boolean change = StringUtility.isNullOrEmpty(changId) || changId.equalsIgnoreCase("true");
        if (change)
            setControlId(this, counter);
    }

    private void setControlId(WebControl startControl, int counter) {
        for (WebControl control : startControl.getControls()) {
            if (!StringUtility.isNullOrEmpty(control.getId())) {
                String changId = control.getAttributeValue(changeId);
                boolean change = StringUtility.isNullOrEmpty(changId) || changId.equalsIgnoreCase("true");
                if (change) {
                    String id = control.getId();
                    if (counter == 0) {
                        control.setAttributeValue(WebControl.originalId, id);
                    } else {
                        id = control.getAttributeValue(WebControl.originalId);
                    }
                    control.setId(id + counter);
                }
            }
            setControlId(control, counter);
        }
    }

    @SuppressWarnings("unchecked")
    public void bind() {
        String itemListName = null;
        if (attributes.get(itemList) != null)
            itemListName = attributes.get(itemList).getValue();
        if (!StringUtility.isNullOrEmpty(itemListName)) {
            if (StringUtility.isEL(itemListName))
                list = (List<Object>) ELUtility.evaluateEL(itemListName, page);
            else
                list = (List<Object>) ELUtility.evaluateEL("${this." + itemListName + "}", page);
        } else
            list = (List<Object>) getAttributeValueObject(listKey);
        if (list == null)
            list = new ArrayList<Object>();

    }

    public String getChangeId() {
        return getAttributeValue(changeId);
    }

    public void setChangeId(String ChangeId) {
        setAttributeValue(changeId, ChangeId);
    }

    public String getRowCount() {
        return getAttributeValue(rowCount);
    }

    public void setRowCount(String RowCount) {
        setAttributeValue(rowCount, RowCount);
    }

    public String getColCount() {
        return getAttributeValue(colCount);
    }

    public void setColCount(String ColCount) {
        setAttributeValue(colCount, ColCount);
    }

    public String getOnRenderCell() {
        return getAttributeValue(onRenderCell);
    }

    public void setOnRenderCell(String OnRenderCell) {
        setAttributeValue(onRenderCell, OnRenderCell);
    }

    public String getHeaderClass() {
        return getAttributeValue(headerClass);
    }

    public void setHeaderClass(String HeaderClass) {
        setAttributeValue(headerClass, HeaderClass);
    }

    public String getOnRenderTable() {
        return getAttributeValue(onRenderTable);
    }

    public void setOnRenderTable(String OnRenderTable) {
        setAttributeValue(onRenderTable, OnRenderTable);
    }

    public String getOnRenderHeader() {
        return getAttributeValue(onRenderHeader);
    }

    public void setOnRenderHeader(String OnRenderHeader) {
        setAttributeValue(onRenderHeader, OnRenderHeader);
    }

    public String getOnRenderRow() {
        return getAttributeValue(onRenderRow);
    }

    public void setOnRenderRow(String OnRenderRow) {
        setAttributeValue(onRenderRow, OnRenderRow);
    }

    public String getOnRenderColumn() {
        return getAttributeValue(onRenderColumn);
    }

    public void setOnRenderColumn(String OnRenderColumn) {
        setAttributeValue(onRenderColumn, OnRenderColumn);
    }

    public String getObjectName() {
        return getAttributeValue(objectName);
    }

    public void setObjectName(String ObjectName) {
        setAttributeValue(objectName, ObjectName);
    }

    public String getItemList() {
        return getAttributeValue(itemList);
    }

    public void setItemList(String itemListName) {
        setAttributeValue(itemList, itemListName);
    }

    public String getList() {
        return getAttributeValue(listKey);
    }

    public void setList(String listVal) {
        setAttributeValue(listKey, listVal);
    }

    public List<Attribute> getRuntimeAttributes() {
        return runtimeAttributes;
    }

    public void setRuntimeAttributes(List<Attribute> runtimeAttributes) {
        this.runtimeAttributes = runtimeAttributes;
    }
}
