package eg.java.net.web.jspx.ui.controls.html.elements;

import eg.java.net.web.jspx.engine.parser.TagFactory;
import eg.java.net.web.jspx.engine.util.StringUtility;
import eg.java.net.web.jspx.engine.util.ui.RenderPrinter;
import eg.java.net.web.jspx.ui.controls.WebControl;
import eg.java.net.web.jspx.ui.controls.attrbs.JspxAttribute;
import eg.java.net.web.jspx.ui.controls.html.GenericWebControl;
import eg.java.net.web.jspx.ui.controls.html.elements.inputs.TextBox;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.IAjaxSubmitter;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.ISubmitter;
import eg.java.net.web.jspx.ui.controls.html.elements.markers.ValueHolder;
import eg.java.net.web.jspx.ui.pages.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Calendar extends GenericWebControl implements ValueHolder {
    public static final String Navigate_Serial = "serial";
    public static final String Navigate_Random = "random";
    /**
     *
     */
    private static final long serialVersionUID = 5906557145135460397L;
    private static final Logger logger = LoggerFactory.getLogger(Calendar.class);
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final String readonly = "readonly";
    private final String showweek = "showweek";
    @JspxAttribute
    private final String dateFormat = "dateFormat";
    @JspxAttribute
    private final String title = "title";
    private String valueBinding;
    @JspxAttribute
    private final String navigate = "navigate";

    public Calendar() {
        super(TagFactory.Calendar);
        isContentControl = false;
    }

    public Calendar(Page page) {
        super(TagFactory.Calendar, page);
        isContentControl = false;
    }

    public boolean getReadonly() {
        return !StringUtility.isNullOrEmpty(getAttributeValue(readonly));
    }

    public void setReadonly(boolean Readonly) {
        setAttributeValue(readonly, Readonly ? "readonly" : "");
    }

    public boolean getShowWeek() {
        return getAttributeBooleanValue(showweek);
    }

    public void setShowWeek(boolean showWeekString) {
        setAttributeBooleanValue(showweek, showWeekString);
    }

    public String getValue() {
        return getAttributeValue(Input.value, getDateFormat());
    }

    public void setValue(String valueString) {
        setAttributeValue(Input.value, valueString);
    }

    public String getDateFormat() {
        // [Feb 21, 2011 9:13:35 PM] [Amr.ElAdawy] [in case the date format is null, then use the default.]
        String format = getAttributeValue(dateFormat);
        if (StringUtility.isNullOrEmpty(format))
            return "dd/MM/yyyy";
        return format;

    }

    public void setDateFormat(String format) {
        format = format.trim();
        if (!StringUtility.isNullOrEmpty(format))
            simpleDateFormat = new SimpleDateFormat(format);
        setAttributeValue(dateFormat, format);
    }

    public String getTitle() {
        return getAttributeValue(title);
    }

    public void setTitle(String title) {
        setAttributeValue(this.title, title);
    }

    /**
     * gets the internal value as Date, is the internal value is invalid, the current date is returned.
     *
     * @return
     */
    public Date getDate() {
        try {
            return simpleDateFormat.parse(getValue());
        } catch (ParseException e) {
            logger.warn("the date [" + getValue() + "] is not parsable");
        }
        return new Date();
    }

    public void setDate(Date date) {
        setValue(simpleDateFormat.format(date));
    }

    /**
     * overridden to clone members that are not attributes.
     */
    public WebControl clone(WebControl parent, Page page, ISubmitter submitter, IAjaxSubmitter ajaxSubmitter) {
        Calendar calendar = (Calendar) super.clone(parent, page, submitter, ajaxSubmitter);
        // I set the date format here again although it was already set in the
        // clone method in order to update the
        // simpleDateforamt in the cloned version with the declared date format.
        calendar.setDateFormat(getDateFormat());
        return calendar;
    }

    public void render(RenderPrinter outputStream) throws Exception {
        if (!isRendered() || !isAccessible())
            return;
        TextBox box = new TextBox();
        box.setReadonly(getReadonly());
        box.setSize(25);
        // [Jun 9, 2009 8:09:36 PM] [amr.eladawy] setting the parent of the control in order to allow iterative IDs to be
        // rendered.
        box.setParent(parent);
        box.setId(getId());
        box.setValue(getValue());
        box.setNoSystemCss(true);
        box.render(outputStream);
        String script = getScript();
        page.addOnloadScript(script);
    }

    private String getScript() {
        String mode = "datepicker";
        if (getDateFormat().toLowerCase().contains("hh"))
            mode = "datetimepicker";

        String document = "";
        if (page != null && page.isAjaxPostBack && page.isMultiPartForm)
            document = ",window.parent.document";

        String script = "$('#" + getId() + "'" + document + ")." + mode +
                "({changeYear: true,showOtherMonths: true,showSecond: true,timeFormat: 'hh:mm:ss',showWeek: " + getShowWeek() +
                ",selectOtherMonths: true,dateFormat:'dd/mm/yy'});";
        return script;
    }

    protected String getScriptDateFormat() {
        String format = getDateFormat();
        format = format.replace("/", "");
        int timePartIndex = format.indexOf(" ");
        if (timePartIndex > 0)
            format = format.substring(0, timePartIndex);
        return format;
    }

    public String getValueBinding() {
        return Input.calculateValueBinding(valueBinding, getAttribute(Input.value));
    }

    public void setValueBinding(String valueBinding) {
        this.valueBinding = valueBinding;
    }

    public String getNavigate() {
        return getAttributeValue(navigate);
    }

    public void setNavigate(String navigateValue) {
        setAttributeValue(navigate, navigateValue);
    }

}
