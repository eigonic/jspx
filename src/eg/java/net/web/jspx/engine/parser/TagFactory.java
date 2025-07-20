package eg.java.net.web.jspx.engine.parser;

import eg.java.net.web.jspx.ui.controls.WebControl;
import eg.java.net.web.jspx.ui.controls.html.GenericWebControl;
import eg.java.net.web.jspx.ui.controls.html.custom.Portlet;
import eg.java.net.web.jspx.ui.controls.html.custom.Tag;
import eg.java.net.web.jspx.ui.controls.html.elements.*;
import eg.java.net.web.jspx.ui.controls.html.elements.ajax.AjaxLoading;
import eg.java.net.web.jspx.ui.controls.html.elements.ajax.AjaxPanel;
import eg.java.net.web.jspx.ui.controls.html.elements.ajax.AutoComplete;
import eg.java.net.web.jspx.ui.controls.html.elements.dataitem.*;
import eg.java.net.web.jspx.ui.controls.html.elements.fieldSet.FieldSet;
import eg.java.net.web.jspx.ui.controls.html.elements.fieldSet.Legend;
import eg.java.net.web.jspx.ui.controls.html.elements.hidden.JspInclude;
import eg.java.net.web.jspx.ui.controls.html.elements.hidden.ResourceInclude;
import eg.java.net.web.jspx.ui.controls.html.elements.inputs.InputFactory;
import eg.java.net.web.jspx.ui.controls.html.elements.inputs.TextArea;
import eg.java.net.web.jspx.ui.controls.html.elements.model.DataModel;
import eg.java.net.web.jspx.ui.controls.html.elements.select.Option;
import eg.java.net.web.jspx.ui.controls.html.elements.select.OptionGroup;
import eg.java.net.web.jspx.ui.controls.html.elements.select.Select;
import eg.java.net.web.jspx.ui.controls.html.w3.*;
import eg.java.net.web.jspx.ui.controls.validators.ValidatorFactory;

import java.util.HashMap;

/**
 * @author amr.eladawy this factory is responsible for finding the suitable webcontrol for a give Tag
 */
public class TagFactory {

    public static String Page = "page";
    public static String Head = "head";
    public static String BreakLine = "br";
    public static String Body = "body";
    public static String Title = "title";
    public static String Form = "form";
    public static String HyperLink = "a";
    public static String LinkCommand = "linkcommand";
    public static String Panel = "div";
    public static String Image = "img";
    public static String Input = "input";
    public static String Select = "select";
    public static String PlaceHolder = "placeholder";
    public static String ContentHolder = "contentholder";
    public static String TextArea = "textarea";
    public static String DropDownList = "select";
    public static String Label = "label";
    public static String Table = "table";
    public static String TableRow = "tr";
    public static String TableColumn = "td";
    public static String TableHead = "th";
    public static String TableHeader = "thead";
    public static String TableFooter = "tfoot";
    public static String TableBody = "tbody";
    public static String HorizontalRuler = "hr";
    public static String Html = "html";
    public static String Script = "script";
    public static String Validator = "validator";
    public static String Span = "span";
    public static String Div = "div";
    public static String ListTable = "listtable";
    public static String DataTable = "datatable";
    public static String DataParam = "dataparam";
    public static String DataRow = "datarow";
    public static String ExportToExcel = "exporttoexcel";
    public static String DataColumn = "datacolumn";
    public static String DataColumnCommand = "datacolumncommand";
    public static String DataPK = "datapk";
    public static String SqlAutoComplete = "sqlautocomplete";
    public static String DataLookup = "datalookup";
    public static String JspInclude = "JspInclude";
    public static String Link = "link";
    public static String IFrame = "iframe";
    public static String Option = "option";
    public static String OptionGroup = "optiongroup";
    public static String Calendar = "calendar";
    public static String JCal = "jcal";
    public static String Rate = "Rate";
    public static String DataModel = "datamodel";
    public static String ResourceBundle = "resourcebundle";
    public static String Captcha = "captcha";
    public static String Capatcha = "capatcha";
    public static String Footer = "footer";
    public static String RadioButtonGroup = "radiogroup";
    public static String CheckBoxGroup = "checkboxgroup";
    public static String Repeater = "repeater";
    public static String AjaxPanel = "ajaxpanel";
    public static String AjaxLoading = "ajaxloading";
    public static String AutoComplete = "autocomplete";
    public static String Frame = "iframe";
    public static String FieldSet = "fieldset";
    public static String Legend = "legend";
    public static String ItemTemplate = "itemtemplate";
    public static String EditTemplate = "edittemplate";
    public static String Tag = "tag";
    public static String Control = "control";
    public static String Portlet = "portlet";
    public static String CollapsePanel = "collapsepanel";
    public static String ResourceInclude = "resourceinclude";
    public static String Meta = "meta";
    public static String DbBean = "dbbean";
    public static String EditField = "Editfield";
    public static String ActionTempalte = "actiontempalte";
    public static String SaveField = "savefield";
    public static String UpdateField = "updatefield";
    public static String CancelField = "cancelfield";
    public static String UnsortedList = "ul";
    public static String ListItem = "li";
    public static String Pre = "pre";
    public static String XmlBean = "xmlbean";
    private static final HashMap<String, Tag> externalControls = new HashMap<String, Tag>();

    /**
     * gets a web control with the given tag name.
     *
     * @param tagName
     * @return
     */
    public static WebControl getControl(String tagName) {

        if (tagName.equalsIgnoreCase(Head))
            return new Head();
        else if (tagName.equalsIgnoreCase(Body))
            return new Body();
        else if (tagName.equalsIgnoreCase(Script))
            return new Script();
        else if (tagName.equalsIgnoreCase(Panel))
            return new Panel();
        else if (tagName.equalsIgnoreCase(Input))
            return new InputFactory();
        else if (tagName.equalsIgnoreCase(Select))
            return new Select();
        else if (tagName.equalsIgnoreCase(HyperLink))
            return new HyperLink();
        else if (tagName.equalsIgnoreCase(LinkCommand))
            return new LinkCommand();
        else if (tagName.equalsIgnoreCase(Label))
            return new Label();
        else if (tagName.equalsIgnoreCase(Form))
            return new Form();
        else if (tagName.equalsIgnoreCase(PlaceHolder))
            return new PlaceHolder();
        else if (tagName.equalsIgnoreCase(ContentHolder))
            return new ContentHolder();
        else if (tagName.equalsIgnoreCase(Validator))
            return new ValidatorFactory();
        else if (tagName.equalsIgnoreCase(ListTable))
            return new ListTable();
        else if (tagName.equalsIgnoreCase(DataTable))
            return new DataTable();
        else if (tagName.equalsIgnoreCase(DataColumn))
            return new DataColumn();
        else if (tagName.equalsIgnoreCase(DataColumnCommand))
            return new DataColumnCommand();
        else if (tagName.equalsIgnoreCase(DataParam))
            return new DataParam();
        else if (tagName.equalsIgnoreCase(DataRow))
            return new DataRow();
        else if (tagName.equalsIgnoreCase(ExportToExcel))
            return new ExportToExcel();
        else if (tagName.equalsIgnoreCase(DataPK))
            return new DataPK();
        else if (tagName.equalsIgnoreCase(SqlAutoComplete))
            return new SqlAutoComplete();
        else if (tagName.equalsIgnoreCase(DataLookup))
            return new DataLookup();
        else if (tagName.equalsIgnoreCase(JspInclude))
            return new JspInclude();
        else if (tagName.equalsIgnoreCase(Link))
            return new Link();
        else if (tagName.equalsIgnoreCase(IFrame))
            return new IFrame();
        else if (tagName.equalsIgnoreCase(Image))
            return new Image();
        else if (tagName.equalsIgnoreCase(Option))
            return new Option();
        else if (tagName.equalsIgnoreCase(OptionGroup))
            return new OptionGroup();
        else if (tagName.equalsIgnoreCase(Calendar))
            return new Calendar();
        else if (tagName.equalsIgnoreCase(JCal))
            return new JCal();
        else if (tagName.equalsIgnoreCase(Rate))
            return new Rate();
        else if (tagName.equalsIgnoreCase(DataModel))
            return new DataModel();
        else if (tagName.equalsIgnoreCase(ResourceBundle))
            return new ResourceBundle();
        else if (tagName.equalsIgnoreCase(Captcha) || tagName.equalsIgnoreCase(Capatcha))
            return new Captcha();
        else if (tagName.equalsIgnoreCase(Footer))
            return new Footer();
        else if (tagName.equalsIgnoreCase(TextArea))
            return new TextArea();
        else if (tagName.equalsIgnoreCase(Repeater))
            return new Repeater();
        else if (tagName.equalsIgnoreCase(RadioButtonGroup))
            return new RadioButtonGroup();
        else if (tagName.equalsIgnoreCase(AjaxPanel))
            return new AjaxPanel();
        else if (tagName.equalsIgnoreCase(AjaxLoading))
            return new AjaxLoading();
        else if (tagName.equalsIgnoreCase(AutoComplete))
            return new AutoComplete();
        else if (tagName.equalsIgnoreCase(FieldSet))
            return new FieldSet();
        else if (tagName.equalsIgnoreCase(Legend))
            return new Legend();
        else if (tagName.equalsIgnoreCase(ItemTemplate))
            return new ItemTemplate();
        else if (tagName.equalsIgnoreCase(EditTemplate))
            return new EditTemplate();
        else if (tagName.equalsIgnoreCase(Tag))
            return new Tag();
        else if (tagName.equalsIgnoreCase(Portlet))
            return new Portlet();
        else if (tagName.equalsIgnoreCase(CollapsePanel))
            return new CollapsePanel();
        else if (tagName.equalsIgnoreCase(ResourceInclude))
            return new ResourceInclude();
        else if (tagName.equalsIgnoreCase(Title))
            return new Title();
        else if (tagName.equalsIgnoreCase(Meta))
            return new Meta();
        else if (tagName.equalsIgnoreCase(DbBean))
            return new DBBean();
        else if (tagName.equalsIgnoreCase(EditField))
            return new EditField();
        else if (tagName.equalsIgnoreCase(Pre))
            return new Pre();
        else if (tagName.equalsIgnoreCase(BreakLine))
            return new BreakLine();
        else if (tagName.equalsIgnoreCase(XmlBean))
            return new XmlBean();
        else {
            Tag tag = externalControls.get(tagName);
            if (tag != null)
                return tag.getWebControl();
        }
        return new GenericWebControl(tagName);

    }

    /**
     * registers external control in the tag factory.
     *
     * @param
     * @param tagName
     */
    public static void registerTagControl(Tag tag) {
        externalControls.putIfAbsent(tag.getPrefix(), tag);
    }
}
