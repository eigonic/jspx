package eg.java.net.web.jspx.ui.controls.html.elements.select;

import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.ELUtility;
import eg.java.net.web.jspx.engine.util.PropertyAccessor;
import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.WebControl;
import eg.java.net.web.jspx.ui.controls.attrbs.Attribute;
import eg.java.net.web.jspx.ui.controls.attrbs.JspxAttribute;
import eg.java.net.web.jspx.ui.controls.html.GenericWebControl;
import eg.java.net.web.jspx.ui.controls.html.elements.Input;
import eg.java.net.web.jspx.ui.controls.html.elements.dataitem.DataLookup;
import eg.java.net.web.jspx.ui.controls.html.elements.dataitem.DataParam;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.ValueHolder;
import eg.java.net.web.jspx.ui.pages.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;

public class Select extends GenericWebControl implements ValueHolder {

    private static final long serialVersionUID = -4090802001544158940L;

    private static final Logger logger = LoggerFactory.getLogger(Select.class);
    @JspxAttribute
    protected static String bindValueToObject = "bindValueToObject";
    @JspxAttribute
    protected static String onChangeKey = "onchange";
    @JspxAttribute
    protected static String onServerChangeKey = "onserverchange";
    @JspxAttribute
    protected static String list = "list";
    private static final String value = "value";
    @JspxAttribute
    protected String renderOptions = "renderOptions";
    private boolean valueFound = false;
    @JspxAttribute
    private final String size = "size";
    @JspxAttribute
    private final String multiple = "multiple";
    private String valueBinding;

    public Select() {
        super(TagFactory.Select);
    }

    public Select(Page page) {
        super(TagFactory.Select, page);
    }

    protected void loadInternalAttributes() {
        super.loadInternalAttributes();
        internalAttribtes.put(onServerChangeKey, 0);
        internalAttribtes.put(onChangeKey, 0);
        internalAttribtes.put(value, 0);
        internalAttribtes.put(list, 0);
        internalAttribtes.put(bindValueToObject, 0);
    }

    @Override
    public void renderChildren(RenderPrinter outputStream) throws Exception {

        Collection<Object> optionList = null;
        if (!StringUtility.isNullOrEmpty(getListEl()))
            optionList = getListObject();
        for (WebControl control : controls) {
            if (control instanceof DataLookup)
                renderDataLookup((DataLookup) control, outputStream);
            else if (control instanceof Option) {
                if (optionList != null)
                    renderListOption((Option) control, outputStream, optionList);
                else
                    renderOption((Option) control, outputStream);
            } else
                control.render(outputStream);
        }
        // when setting the value of the control from Java , developer can set it to a value
        // that is not in the options items.
        // so , just logging this issue and do nothing.
        if (!StringUtility.isNullOrEmpty(getValue()) && !valueFound) {
            logger.warn("Selected value [" + getValue() + "] for the [" + getId() + "] select-control is not in the option list...");
        }
    }

    private void renderListOption(Option option, RenderPrinter outputStream, Collection<Object> optionList) throws Exception {
        if (!StringUtility.isNullOrEmpty(option.getValueProperty()) && !StringUtility.isNullOrEmpty(option.getTextProperty())) {
            if (!isRenderOptions())
                return;
            for (Object object : optionList) {
                Option o = new Option();
                o.setValue(String.valueOf(PropertyAccessor.getProperty(object, option.getValueProperty())));
                o.setText(String.valueOf(PropertyAccessor.getProperty(object, option.getTextProperty())));
                if (isSelectedOption(o.getValue())) {
                    o.setSelected(true);
                    valueFound = true;
                }
                o.render(outputStream);
            }
        } else {
            renderOption(option, outputStream);
        }

    }

    /*
     * private void setSelectedValues(Object obj) { value = ""; if(obj instanceof String[]) { String[] values = (String[])obj; for
     * (String selectedVal : values) { value +=selectedVal+","; } if(value.length() > 0) { value = value.substring(0,
     * value.length()-1); } }else if(obj instanceof List) { java.util.List l = (java.util.List)obj; for (Object selectedVal : l) {
     *
     * } } }
     */
    private boolean isSelectedOption(String optionValue) {
        if (StringUtility.isNullOrEmpty(getValue()))
            return false;
        String[] values = getValue().trim().split(",");
        for (String value : values) {
            if (value.equals(optionValue))
                return true;
        }
        return false;
    }

    private void renderOption(Option option, RenderPrinter outputStream) throws Exception {
        option.setSelected(isSelectedOption(option.getValue()));
        option.render(outputStream);
        valueFound = true;
    }

    private void renderDataLookup(DataLookup dataLookup, RenderPrinter outputStream) throws Exception {
        for (WebControl control : controls)
            if (control instanceof DataParam)
                dataLookup.setSql(((DataParam) control).formatSql(dataLookup.getSql(), dataLookup.getcaseSenstive()));

        Hashtable<String, String> options = dataLookup.dataBind();
        java.util.List<String> keyList = dataLookup.getKeys();
        Option option;
        String value;
        for (String key : keyList) {
            value = options.get(key);
            option = new Option();
            option.setValue(key);
            option.setText(value);
            if (isSelectedOption(key)) {
                option.setSelected(true);
                valueFound = true;
            }
            option.render(outputStream);
        }
    }

    public int getSize() {
        return Integer.parseInt(getAttributeValue(size));
    }

    public void setSize(int sizeValue) {
        setAttributeValue(size, String.valueOf(sizeValue));
    }

    public boolean getMultiple() {
        return getAttributeBooleanValue(multiple);
    }

    public void setMultiple(boolean multipleVlaue) {
        setAttributeBooleanValue(multiple, multipleVlaue);
    }

    public void addOption(String key, String value) {
        Option option = new Option();
        option.setValue(value);
        option.setText(key);
        controls.add(option);
        // [Jul 23, 2012 2:19:02 PM] [Amr.ElAdawy] [isContentControl is aproperty of the web control itself not a function of
        // of the children]
        // isContentControl = true;
    }

    public void removeOption(String value) {
        ArrayList<WebControl> forRemoval = new ArrayList<WebControl>();
        for (WebControl controll : controls)
            if (controll instanceof Option)
                if (((Option) controll).getValue().equals(value)) {
                    forRemoval.add(controll);
                }
        controls.removeAll(forRemoval);
    }

    public void addOption(Option option) {
        controls.add(option);
        // isContentControl = true;
    }

    public void removeOption(Option option) {
        removeOption(option.getValue());
    }

    public String getValue() {
        return getAttributeValue(value);
    }

    public void setValue(String valueString) {
        setAttributeValue(value, valueString);

    }

    /**
     * overridden to remove the value attribute out of the attributes collection.
     */
    @Override
    protected void renderAttributes(RenderPrinter outputStream) throws Exception {
        super.renderAttributes(outputStream);
        StringBuilder onchange = new StringBuilder();
        if (!StringUtility.isNullOrEmpty(getOnChange()))
            onchange.append(getOnChange()).append(";");
        if (!StringUtility.isNullOrEmpty(getOnServerChange()))
            onchange.append("postBack(this.id,'").append(getOnServerChange()).append("','")
                    .append(mySubmitter == null ? "null" : ((WebControl) mySubmitter).getId()).append("','")
                    .append(getMyAjaxSubmitter() == null ? "null" : ((WebControl) getMyAjaxSubmitter()).getId()).append("')");

        String s = onchange.toString();
        if (!StringUtility.isNullOrEmpty(s))
            new Attribute(onChangeKey, s).render(outputStream, page);

    }

    public String getOnChange() {
        return getAttributeValue(onChangeKey);
    }

    public void setOnChange(String onChange) {
        setAttributeValue(onChangeKey, onChange);
    }

    public String getOnServerChange() {
        return getAttributeValue(onServerChangeKey);
    }

    public void setOnServerChange(String onServerChange) {
        setAttributeValue(onServerChangeKey, onServerChange);
    }

    @SuppressWarnings("unchecked")
    public Collection<Object> getListObject() {
        try {
            Collection c = (Collection<Object>) ELUtility.evaluateEL(getListEl(), page);
            if (c == null) {
                // get the list from the controller
                c = (Collection) ELUtility.evaluateEL("${this." + getList() + "}", page);
            }
            return c;
        } catch (Exception e) {
            logger.debug("Error while getting list for the select item>>> listname: " + list + ", error: " + e);
            return null;
        }

    }

    private String getListEl() {
        return getAttribute(list).getValue();
    }

    public String getList() {
        return getAttributeValue(list);
    }

    public void setList(String list) {
        setAttributeValue(Select.list, list);
    }

    public String getBindValueToObject() {
        Attribute a = getAttribute(bindValueToObject);
        return a != null ? a.getValue() : "";
    }

    public void setBindValueToObject(String bindValueToObject) {
        setAttributeValue(Select.bindValueToObject, bindValueToObject);
    }

    public boolean bindValueToObject(Object bean, String value) {
        if (!StringUtility.isNullOrEmpty(value) && !StringUtility.isNullOrEmpty(getBindValueToObject())) {
            Collection<Object> optionList = getListObject();
            if (optionList != null) {
                // get binding attribute
                String valueProperty = null;
                for (WebControl control : controls) {
                    if (control instanceof Option) {
                        if (!StringUtility.isNullOrEmpty(((Option) control).getValueProperty())) {
                            valueProperty = ((Option) control).getValueProperty();
                            break;
                        }
                    }
                }
                if (!StringUtility.isNullOrEmpty(valueProperty)) {
                    for (Object object : optionList) {
                        Object val = PropertyAccessor.getProperty(object, valueProperty);
                        if (val != null && String.valueOf(val).equals(value)) {
                            try {
                                String propertyName = getBindValueToObject();
                                propertyName = propertyName.substring(2, propertyName.length() - 1);
                                propertyName = propertyName.substring(propertyName.indexOf('.') + 1);
                                PropertyAccessor.setProperty(bean, propertyName, object);
                            } catch (Exception e) {
                                return false;
                            }
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public String getValueBinding() {
        return Input.calculateValueBinding(valueBinding, getAttribute(value));
    }

    public void setValueBinding(String valueBinding) {
        this.valueBinding = valueBinding;
    }

    public boolean isRenderOptions() {
        return !FALSE.equalsIgnoreCase(getAttribute(renderOptions.toLowerCase()).getValue(page));
    }

    public void setRenderOptions(boolean renderOptions) {
        setAttributeBooleanValue(this.renderOptions, renderOptions);
    }

}
