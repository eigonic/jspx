package eg.java.net.web.jspx.ui.controls.html.elements.model;

import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.PropertyAccessor;
import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.WebControl;
import eg.java.net.web.jspx.ui.controls.html.elements.Calendar;
import eg.java.net.web.jspx.ui.controls.html.elements.Label;
import eg.java.net.web.jspx.ui.controls.html.elements.hidden.HiddenGenericWebControl;
import eg.java.net.web.jspx.ui.controls.html.elements.inputs.FileUpload;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.IAjaxSubmitter;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.ISubmitter;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.ItemDataBound;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.ValueHolder;
import eg.java.net.web.jspx.ui.pages.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataModel extends HiddenGenericWebControl implements ItemDataBound {

    /**
     *
     */
    private static final long serialVersionUID = -5191252907114070234L;
    private final Logger logger = LoggerFactory.getLogger(Date.class);
    private final String dateformatKey = "dateformat";
    private final String ModelClass = "modelClass";
    private final String ModelObject = "modelObject";

    public DataModel() {
        super(TagFactory.DataModel);
    }

    public DataModel(Page page) {
        super(TagFactory.DataModel, page);
    }

    public void render(RenderPrinter outputStream) throws Exception {
        // Do reverseBinding;
        reverseBinding();
        for (int i = 0; i < controls.size(); i++)
            controls.get(i).render(outputStream);
    }

    public WebControl clone(WebControl parent, Page page, ISubmitter submitter, IAjaxSubmitter ajaxSubmitter) {
        DataModel dataModel = (DataModel) super.clone(parent, page, submitter, ajaxSubmitter);
        dataModel.setDataModelChild(true);

        return dataModel;
    }

    public void dataBind() {
        if (!StringUtility.isNullOrEmpty(getModelCalss())) {
            try {
                Object object = getObject();
                PropertyAccessor.setProperty(page, getModelObject(), object);
                // do reflection.
                Method[] methods = object.getClass().getDeclaredMethods();
                for (Method method : methods) {
                    String name = method.getName();
                    if (name.startsWith("set")) {
                        try {
                            String id = PropertyAccessor.getPropertName(name);
                            WebControl control = findControl(id, true);
                            if (control != null) {
                                if (!(control instanceof ValueHolder))
                                    continue;
                                if (control instanceof FileUpload)
                                    PropertyAccessor.invokeSetter(object, method, ((FileUpload) control).getFileItem());
                                else {
                                    Object value = ((ValueHolder) control).getValue();
                                    if (control instanceof Label) {
                                    } else if (control instanceof Calendar) {
                                        try {
                                            PropertyAccessor.invokeSetter(object, method, value, ((Calendar) control).getDateFormat());
                                            // PropertyAccessor.setProperty(object, methods, id, value, ((Calendar) control).getDateFormat());
                                        } catch (Exception e) {
                                        }
                                    } else {
                                        try {
                                            PropertyAccessor.invokeSetter(object, method, value);
                                            // PropertyAccessor.setProperty(object, methods, id, value);
                                        } catch (Exception e) {
                                        }
                                    }
                                    // BeanUtils.setProperty(object,id, value);
                                }
                            }
                        } catch (Exception e) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    protected Object getObject() {
        // 1- see the model name attribute, if empty create new.
        // 2- else get the object out of the page.
        // 3- if the object is null create it.
        String modelClass = getModelCalss();
        Object object = null;
        try {
            if (!StringUtility.isNullOrEmpty(getModelObject())) {
                try {
                    object = PropertyAccessor.getProperty(page, getModelObject());
                } catch (Exception e) {
                }
            }
            if (object == null)
                object = Class.forName(modelClass).newInstance();
        } catch (Exception e) {
            logger.error("getObject()", e);
        }
        return object;
    }

    /**
     * loads the child controls with the Model Object.
     */
    public void reverseBinding() {

        Object object = getObject();
        if (object != null) {
            setControlValueRecursive(object, this);
        }

    }

    private void setControlValueRecursive(Object object, WebControl startControl) {
        for (WebControl control : startControl.getControls()) {
            if (control instanceof ValueHolder || control instanceof Label) {
                setControlValue(object, control);
            }
            setControlValueRecursive(object, control);
        }
    }

    private void setControlValue(Object object, WebControl control) {
        Object value = PropertyAccessor.getProperty(object, control.getId());

        if (value == null)
            return;

        // getting the value as string according to the type
        String valueStr = "";
        if (value instanceof Date || value instanceof Timestamp || value instanceof java.sql.Date)
            valueStr = new SimpleDateFormat(getDateformatDefault()).format((Date) value);
        else
            valueStr = String.valueOf(value);

        // setting the control value
        if (control instanceof Label) {
            ((Label) control).setText(valueStr);
        } else {
            ((ValueHolder) control).setValue(valueStr);
        }
    }

    private String formatValue(Object value) {
        if (value instanceof Date) {
            Date date = (Date) value;
            String df = getDateformat();
            if (StringUtility.isNullOrEmpty(df))
                df = "dd-MM-yyyy HH:mm:ss";
            return new SimpleDateFormat(df).format(date);
        }
        if (value instanceof java.sql.Date) {
            java.sql.Date date = (java.sql.Date) value;
            String df = getDateformat();
            if (StringUtility.isNullOrEmpty(df))
                df = "dd-MM-yyyy HH:mm:ss";
            return new SimpleDateFormat(df).format(date);
        }
        return String.valueOf(value);
    }

    public Object getChildValue(String id) {
        Object object = getObject();
        Object value = null;
        if (object != null) {
            value = PropertyAccessor.getProperty(object, id);
        }
        return value;
    }

    public String getDateformat() {
        return getAttributeValue(dateformatKey);
    }

    public void setDateformat(String dateformat) {
        setAttributeValue(dateformatKey, dateformat);
    }

    public String getDateformatDefault() {
        String df = getDateformat();
        return StringUtility.isNullOrEmpty(df) ? "dd-MM-yyyy HH:mm:ss" : df;
    }

    public String getModelCalss() {
        return getAttributeValue(ModelClass);
    }

    public void setModelCalss(String modelName) {
        setAttributeValue(ModelClass, modelName);
    }

    public String getModelObject() {
        return getAttributeValue(ModelObject);
    }

    public void setModelObject(String modelName) {
        setAttributeValue(ModelObject, modelName);
    }

}
