# Active Context: JSPX Web Framework

## Current Work Focus
- **Enhanced Documentation**: Updating comprehensive documentation based on official JSPX website information
- **Official Features Integration**: Incorporating verified features and capabilities from jspx.sourceforge.net
- **Version 2.1 Updates**: Latest release information and new feature documentation

## Recent Analysis & Discoveries from Official Website

### Official Project Information
- **Official Website**: http://jspx.sourceforge.net/
- **Current Version**: JSPX 2.1 (latest stable release)
- **License**: WTFPL (Do What The F*ck You Want To Public License) - completely free and open source
- **Project Status**: Active, maintained, with YouTube support channel
- **Tagline**: "Web tier is no more a concern"

### Key Official Features Confirmed
- **Business Case Driven Framework**: Designed around common business needs
- **HTML-Based**: Uses standard HTML tags with custom extensions
- **Web Forms Architecture**: Built around HTML form-based interactions
- **Ajax Support**: Native AJAX with AjaxPanel wrapping
- **Validators**: Both client and server-side validation
- **DataTable/ListTable**: Rich data presentation controls
- **JAAS Ready**: Java Authentication and Authorization Service integration
- **JSP Integration**: Can import and use existing JSP files

### Framework Philosophy (Official Mission & Vision)
1. **Develop Once, Run Everywhere**: True portability across application servers
2. **Web tier is no more a concern**: Abstracts web development complexity
3. **Full management on HTML**: Complete control over HTML generation
4. **No Magic**: Simple, transparent, understandable architecture
5. **Developer Friendly**: Clear error messages, intuitive development

### JSPX 2.1 New Features (Official)
- **Enhanced DataColumn**: Long text wrapping, hint attributes, decimal formatting
- **Improved Ajax**: Override default submitters, cross-panel refresh
- **XmlBean Introduction**: New bean type for XML data handling
- **ListTable Sorting**: Column sorting capabilities added
- **Enhanced Security**: Role-based DataParam exclusion
- **Performance**: 5-minute SQL timeout, improved Excel export
- **Developer Experience**: Runtime validators, loading indicators

### Technical Configuration (Official)
- **Required JARs**: 
  - jspx-2.1.jar (core framework)
  - poi-3.9-20121203.jar (Excel support)
  - commons-fileupload-1.2.1.jar (file uploads)
  - commons-jexl-2.1.1.jar (expression language)
  - commons-io-1.3.2.jar (I/O utilities)
  - slf4j-api-1.7.2.jar (logging)
  - jcl-over-slf4j-1.7.5.jar (optional logging bridge)

- **Web.xml Configuration**:
  - RequestHandler servlet for *.jspx pages
  - ResourceHandler servlet for /jspxEmbededResources/*

### Official Examples Structure
- **Page Definition**: XML with controller attribute
- **Controller Class**: Extends Page, uses @JspxWebControl annotations
- **Lifecycle Methods**: pageLoaded() for initialization
- **Integration**: Works within existing Java web applications

### Security Model (Official JAAS Integration)
- **AllowedRoles**: Whitelist attribute for access control
- **DeniedRoles**: Blacklist attribute for access restriction
- **Simulated Action Protection**: Server-side role verification
- **Table Security**: Automatic permission checking for CRUD operations
- **Role-based Rendering**: Controls visibility based on user roles

### Modern Web Technologies Integration
- **jQuery 1.8.3**: DOM manipulation and AJAX
- **Twitter Bootstrap**: Responsive UI framework
- **jQuery UI**: Enhanced widgets and interactions
- **jQuery Plugins**: Rating, notifications, masked input, etc.

## Active Development Context

### Current State Assessment
- **Framework Status**: Mature, actively maintained, version 2.1 stable
- **Community**: Sourceforge project with forum support and YouTube channel
- **Documentation**: Official website with live demos and examples
- **Portability**: Tested on JBoss and OC4J, designed for universal compatibility

### Key Differentiators
- **Zero Configuration**: No XML configuration files required
- **Business-Oriented**: Built around common business application patterns
- **Migration Friendly**: Easy integration with existing projects
- **Export Capabilities**: Built-in Excel export functionality
- **Captcha Support**: Built-in CAPTCHA validation controls

## Next Steps & Considerations

### Documentation Updates Required
1. **Update README**: Include official website information and correct URLs
2. **Feature Expansion**: Add newly discovered features from website
3. **Configuration Guide**: Update with official servlet configuration
4. **Migration Path**: Document integration with existing applications

### Official Resources Integration
- **Live Demo**: http://jspx-demo.appspot.com/
- **Documentation**: Official PDF guides and tutorials
- **Support**: YouTube channel and Sourceforge forums
- **Download**: Official jar files and demo projects

### Enhanced Understanding
- **Framework Positioning**: Alternative to JSF with simpler deployment
- **Target Audience**: Java web developers, especially those migrating from ASP.NET
- **Use Cases**: Business applications, enterprise web development
- **Competitive Advantage**: Portability, simplicity, no magic approach

## Important Notes & Corrections

### URL Corrections
- **Correct Website**: http://jspx.sourceforge.net/ (not https://jspx.js.net)
- **Project Hosting**: SourceForge, not GitHub
- **Version**: 2.1 is latest, not just framework version identifier

### License Clarification
- **WTFPL**: Extremely permissive license, more permissive than MIT/Apache
- **Commercial Use**: Fully allowed without restrictions
- **Redistribution**: Unrestricted modification and redistribution

### Technical Clarifications
- **Servlet Requirements**: Standard servlet containers, no special requirements
- **Java Compatibility**: Java 1.5+ with annotations and generics support
- **Database**: Works with any JDBC-compatible database
- **Application Server**: Universal compatibility (JBoss, OC4J, Tomcat, etc.)

This active context reflects the enhanced understanding based on official JSPX website documentation and represents accurate, verified information about the framework's capabilities and positioning.
