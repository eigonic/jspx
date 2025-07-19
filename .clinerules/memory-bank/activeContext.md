# Active Context: JSPX Web Framework

## Current Work Focus
- **Memory Bank Initialization**: Creating comprehensive documentation for the JSPX web framework project
- **Project Analysis**: Understanding the architecture and structure of this Java-based component framework
- **Documentation Foundation**: Establishing the baseline knowledge required for future development work

## Recent Analysis & Discoveries

### Framework Structure Analysis
- **Core Engine**: Located in `eg.java.net.web.jspx.engine` package with `RequestHandler` as the main servlet
- **UI Components**: Comprehensive control library in `eg.java.net.web.jspx.ui` package
- **Page Model**: Three main page types - `Page` (base), `ContentPage`, `MasterPage` 
- **Resource Management**: Static assets organized in `src/resources/` with CSS, JS, images
- **Build System**: Dual build support (Maven + Ant) with version 2.1

### Key Technical Insights
- **Page Lifecycle**: 11-stage lifecycle from composition to rendering (PageCompose through PageRendered)
- **Bean Management**: Three scopes (REQUEST, SESSION, CONVERSATION) with 20-minute default expiration
- **AJAX Integration**: Built-in support through AjaxPanel controls with partial page updates
- **Security Model**: Role-based access control via annotations and input sanitization
- **Expression Language**: JEXL integration for data binding and dynamic content

### Important Patterns Discovered
- **Annotation-Driven**: Heavy use of `@JspxBean`, `@JspxWebControl`, `@JspxClientMethod`
- **Reflection-Based**: Property access and method invocation through `PropertyAccessor`
- **Caching Strategy**: Page model caching with locale support and memory management
- **Event System**: Server-side event handling similar to ASP.NET Web Forms postback model

## Active Development Context

### Current State Assessment
- **Framework Status**: Mature codebase with comprehensive feature set
- **Architecture**: Well-structured multi-layered design with clear separation of concerns
- **Code Quality**: Professional enterprise-level implementation with proper error handling
- **Documentation**: Minimal external documentation, relying primarily on code comments

### Key Components Status
✅ **Engine Package**: Complete request handling infrastructure
✅ **UI Controls**: Comprehensive web control library 
✅ **Page Models**: Full page lifecycle and inheritance support
✅ **Data Layer**: DAO/MAO pattern implementation
✅ **Client Resources**: jQuery/Bootstrap integration
✅ **XML Processing**: Custom lightweight kxml parser
✅ **Build Configuration**: Both Maven and Ant support

## Next Steps & Considerations

### Immediate Priorities
1. **Complete Memory Bank**: Finish initialization with progress.md
2. **Architecture Deep Dive**: Understand control rendering and view state management
3. **Extension Points**: Document how to create custom controls and validators
4. **Performance Characteristics**: Analyze caching and optimization strategies

### Development Considerations
- **Java Version**: Minimum Java 1.5+ requirement (uses generics and annotations)
- **Container Support**: Standard servlet containers (Tomcat, Jetty, etc.)
- **Client Dependencies**: jQuery 1.8.3, Bootstrap for modern UI components
- **Threading Model**: Thread-safe session operations with conversation scoping

### Integration Points
- **Database**: DAO pattern with SQL utilities and export capabilities
- **Security**: Role-based access with CSRF protection and XSS prevention
- **Monitoring**: JMX beans for runtime administration
- **Internationalization**: Resource bundle support with locale-specific caching

## Important Notes & Learnings

### Framework Philosophy
- **Component-Oriented**: Similar to ASP.NET Web Forms but Java-native
- **Declarative UI**: XML-based page definitions with code-behind controllers
- **State Management**: Automatic view state handling and postback processing
- **Developer Experience**: Annotation-driven development with minimal configuration

### Technical Constraints
- **Memory Management**: Page model caching requires memory monitoring
- **Session Dependencies**: Bean scoping requires session support
- **JavaScript Requirements**: Client-side functionality depends on jQuery
- **Servlet API**: Minimum version 2.5 required for proper operation

This active context represents the current understanding of the JSPX framework and will be updated as development work progresses and new insights are discovered.
