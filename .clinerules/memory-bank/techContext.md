# Technical Context: JSPX Web Framework

## Core Technologies

### Java Platform
- **Java Version**: Requires Java 1.5+ (uses generics, annotations)
- **Servlet API**: Version 2.5+ for web container integration
- **Build System**: Maven 2.x/3.x with standard directory structure
- **Alternative Build**: Ant build system also supported (build.xml present)

### Dependencies (Maven/Lib)
- **Apache Commons JEXL 2.1.1**: Expression language evaluation
- **Apache POI 3.12**: Excel file generation and manipulation
- **Apache Commons FileUpload 1.3.1**: Multipart form handling
- **Apache Commons IO 2.2**: I/O utilities
- **SLF4J 1.7.12**: Logging abstraction layer
- **Apache PDFBox 1.8.10**: PDF document processing
- **Servlet API**: Web container integration (provided scope)

### Client-Side Technologies
- **jQuery 1.8.3**: DOM manipulation and AJAX
- **jQuery UI 1.9.1**: UI widgets and interactions
- **Bootstrap**: Responsive CSS framework (multiple versions)
- **JavaScript Libraries**: 
  - Masked input, live query, hotkeys
  - Rating system (jquery.raty)
  - Growth notifications (jgrowl)
  - Date/time picker extensions

### XML Processing
- **Custom XML Parser**: kxml package for lightweight XML processing
- **JSPX Schema**: Custom XML schema for page definitions
- **Expression Language**: JEXL integration for dynamic content

## Development Environment

### Project Structure
```
jspx/
├── src/                          # Source code root
│   ├── eg/java/net/web/jspx/     # Framework core
│   ├── org/kxml/                 # XML parsing
│   └── resources/                # Static assets
├── lib/                          # JAR dependencies
├── Proposed Schema/              # JSPX XML schemas
├── pom.xml                       # Maven configuration
├── build.xml                     # Ant configuration
└── README.md                     # Project documentation
```

### Package Organization
- **Engine** (`eg.java.net.web.jspx.engine`): Core framework
  - Request handling and lifecycle management
  - Annotations, data access, error handling
  - Expression language and parsing utilities
- **UI** (`eg.java.net.web.jspx.ui`): User interface components
  - Web controls and page models
  - HTML elements, validators, attributes
- **Resources**: CSS, JavaScript, images for framework UI

### Build Configuration
- **Maven Group**: `org.eigonic`
- **Artifact**: `jspx`
- **Version**: `2.1`
- **Source Directory**: `src` (non-standard Maven structure)
- **Resource Inclusion**: All non-Java files from src directory

## Runtime Environment

### Web Container Requirements
- **Servlet Container**: Tomcat, Jetty, WebLogic, WebSphere
- **Minimum Servlet API**: 2.5
- **Session Management**: Required for bean scoping
- **Character Encoding**: UTF-8 default, configurable

### Framework Configuration
- **JEXL Integration**: Configurable via `RequestHandler.USE_JEXL`
- **Theme Support**: Twitter Bootstrap integration
- **SQL Debugging**: Configurable via `RequestHandler.SHOW_SQL`
- **Bean Expiration**: 20-minute default for conversation scope
- **JMX Monitoring**: Built-in administrative beans

### Security Considerations
- **Role-Based Access**: Annotation-driven security
- **XSS Protection**: Automatic input sanitization
- **CSRF Prevention**: View state token validation
- **Session Security**: Conversation scope isolation

## Development Patterns

### Annotation-Driven Development
```java
@JspxBean(name="userBean", scope=JspxBean.SESSION)
private UserData userData;

@JspxWebControl(name="userForm")
private Form loginForm;

@JspxClientMethod
public void handleUserLogin() { ... }
```

### Page Controller Pattern
- Extend `Page`, `ContentPage`, or `MasterPage`
- Override lifecycle methods (pageLoaded, pagePreRender, etc.)
- Handle events through reflection-based method calls
- Use annotations for dependency injection

### Resource Management
- **Static Resources**: Served via ResourceHandler servlet
- **Localization**: Resource bundle integration
- **Caching**: Page model caching for performance
- **Minification**: Client-side resource optimization

## Integration Points

### Database Integration
- **DAO Pattern**: Built-in data access objects
- **MAO Pattern**: Model access objects for complex mappings
- **Utility Classes**: SQL utilities, data field mapping
- **Export Capabilities**: CSV and Excel generation

### Monitoring and Management
- **JMX Beans**: Runtime administration and monitoring
- **Logging**: SLF4J integration for flexible logging backends
- **Performance Metrics**: Request timing and statistics
- **Error Handling**: Comprehensive exception hierarchy

### Extensibility
- **Custom Controls**: Extend WebControl base class
- **Custom Validators**: Implement validation interfaces
- **Custom Parsers**: Extend tag factory for new elements
- **Plugin Architecture**: Bean and resource registration

## Deployment Considerations

### Packaging
- **WAR Deployment**: Standard web application archive
- **Resource Placement**: Framework resources in classpath
- **Configuration**: web.xml servlet mapping required
- **Dependencies**: Include all JAR files in WEB-INF/lib

### Performance Optimization
- **Page Caching**: Parsed page models cached in memory
- **Resource Compression**: Minified CSS/JavaScript
- **AJAX Optimization**: Partial page updates
- **View State Management**: Efficient state serialization

### Compatibility
- **Java Versions**: 1.5+ (generics, annotations required)
- **Browser Support**: Modern browsers with JavaScript enabled
- **Container Compatibility**: Standard servlet containers
- **Framework Migration**: ASP.NET Web Forms concepts familiar
