# System Patterns: JSPX Web Framework

## Architecture Overview
JSPX follows a multi-layered architecture that separates concerns cleanly between presentation, control logic, and data management.

```
┌─────────────────────────────────────────────────┐
│                 Presentation Layer              │
├─────────────────────────────────────────────────┤
│  JSPX Pages (XML) + Master Pages + Resources   │
├─────────────────────────────────────────────────┤
│                 Control Layer                   │
├─────────────────────────────────────────────────┤
│    Page Controllers + UI Controls + Engine     │
├─────────────────────────────────────────────────┤
│                  Data Layer                     │
├─────────────────────────────────────────────────┤
│       DAO/MAO + Data Utilities + Beans         │
└─────────────────────────────────────────────────┘
```

## Core Design Patterns

### 1. Page Lifecycle Pattern
Every JSPX page follows a standardized lifecycle managed by the `RequestHandler`:

1. **Page Composition**: Parse XML and create page model
2. **Page Initialization**: Load query strings, set up context
3. **View State Loading**: Restore control state from postback
4. **Postback Data Loading**: Extract form data and bind to controls
5. **Validation**: Server-side validation of submitted data
6. **Event Handling**: Process user interactions and business logic
7. **Pre-Rendering**: Prepare data for display
8. **Rendering**: Generate HTML output
9. **Post-Rendering**: Cleanup and finalization

### 2. Master-Content Page Pattern
Similar to ASP.NET Web Forms, provides template inheritance:

```java
MasterPage (template)
    └── ContentPage (specific content)
        └── Content areas defined by master
```

- `MasterPage`: Defines common layout and structure
- `ContentPage`: Extends master with specific content
- Content areas marked with placeholders in master
- Shared resources and navigation handled at master level

### 3. Component-Based Architecture
All UI elements inherit from `WebControl` base class:

```java
WebControl (base)
├── GenericWebControl (HTML elements)
├── Form controls (inputs, selects, etc.)
├── Data controls (grids, repeaters)
├── Validation controls
└── AJAX controls (AjaxPanel)
```

### 4. Bean Management Pattern
Three scopes for managed beans using annotations:

- **@JspxBean(scope=REQUEST)**: Per-request lifecycle
- **@JspxBean(scope=SESSION)**: Per-session lifecycle  
- **@JspxBean(scope=CONVERSATION)**: Cross-request conversations

### 5. Event-Driven Pattern
Server-side events triggered by client interactions:

- **Postback Events**: Form submissions and control interactions
- **AJAX Events**: Partial page updates without full refresh
- **Internal Events**: Framework-managed control events
- **Custom Events**: User-defined business logic events

## Request Processing Flow

```
HTTP Request → RequestHandler
    ↓
Page Model Composition → Cached or Parse XML
    ↓
Page Instantiation → Clone cached page
    ↓
Master Page Setup → If ContentPage
    ↓
Context Initialization → JEXL, beans, annotations
    ↓
Postback Detection → GET vs POST + AJAX check
    ↓
View State Loading → Restore control state
    ↓
Data Binding → Form data to controls/beans
    ↓
Validation → Server-side rules
    ↓
Event Processing → User interaction handlers
    ↓
Rendering → HTML generation
    ↓
HTTP Response
```

## Data Binding Architecture

### Expression Language Integration
JSPX uses JEXL (Java Expression Language) for declarative binding:

```xml
<jspx:textbox value="${bean.property}" />
<jspx:label text="${bundle.message}" />
<jspx:repeater data="${bean.collection}" />
```

### Two-Way Data Binding
- **Control → Bean**: Form submission updates bean properties
- **Bean → Control**: Bean changes reflected in UI on render
- **Validation Integration**: Binding respects validation rules
- **Type Conversion**: Automatic conversion between strings and objects

## Security Patterns

### Role-Based Access Control
```java
@JspxBean(name="secureBean", allowedRoles="admin,manager")
```

- Page-level access control via annotations
- Control-level visibility based on roles
- Input sanitization for XSS prevention
- CSRF protection through view state tokens

### Session Management
- Conversation scoping for multi-step processes
- Automatic session cleanup for expired beans
- Secure view state handling
- Thread-safe session operations

## AJAX Integration Pattern

### Partial Page Updates
```xml
<jspx:ajaxpanel id="updatePanel">
    <!-- Content that can be updated via AJAX -->
</jspx:ajaxpanel>
```

- **AjaxPanel**: Container for updatable content
- **Postback Management**: AJAX vs full postback detection
- **View State Synchronization**: Maintain state during partial updates
- **Script Management**: Client-side script coordination

### Client-Server Communication
- **Headers**: Custom headers for AJAX metadata
- **JSON Responses**: Structured data for complex updates
- **Error Handling**: Graceful degradation for AJAX failures
- **Progress Indicators**: Built-in loading states

## Caching Strategy

### Page Model Caching
- **Parsed Models**: Cache compiled page structures
- **Locale-Specific**: Separate cache per language
- **Invalidation**: Remove from cache when files change
- **Memory Management**: LRU eviction for memory control

### Resource Management
- **Static Assets**: CSS, JavaScript, images served efficiently
- **Versioning**: Cache busting for updated resources
- **Compression**: Minification and gzip support
- **CDN Integration**: External resource hosting support

## Extension Points

### Custom Controls
```java
public class CustomControl extends WebControl {
    // Custom rendering and behavior
}
```

### Custom Validators
```java
public class CustomValidator extends Validator {
    // Custom validation logic
}
```

### Custom Data Sources
```java
public class CustomDAO extends DAO {
    // Custom data access patterns
}
```

This architecture provides a solid foundation for building maintainable, scalable web applications while maintaining developer productivity and code reusability.
