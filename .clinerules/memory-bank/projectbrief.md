# Project Brief: JSPX Web Framework

## Overview
JSPX is a Java-based web framework that provides a component-driven approach to building web applications. It combines the power of Java with XML-based page definitions and offers features like master pages, AJAX support, data binding, validation, and more.

## Core Purpose
- **Component-Based Web Development**: Provides reusable web controls and components
- **Declarative UI**: Uses XML-based syntax for defining web pages
- **Master/Content Page Architecture**: Similar to ASP.NET Web Forms model
- **AJAX Integration**: Built-in AJAX support for dynamic web applications
- **Data Binding**: Automatic binding between UI controls and data objects
- **Validation Framework**: Client and server-side validation system

## Key Requirements
1. **Framework Core**: Build and maintain the JSPX engine for processing requests
2. **UI Controls**: Comprehensive library of web controls (forms, data grids, etc.)
3. **Page Model**: Support for master pages, content pages, and portlet pages
4. **Data Integration**: Object-to-database mapping and data utilities
5. **Resource Management**: Handle CSS, JavaScript, images, and other resources
6. **Security**: Role-based access control and input sanitization
7. **Internationalization**: Multi-language support via resource bundles

## Project Structure
- **Engine Package** (`eg.java.net.web.jspx.engine`): Core framework logic
- **UI Package** (`eg.java.net.web.jspx.ui`): Web controls and page models
- **Resources** (`src/resources`): Static assets (CSS, JS, images)
- **Schema** (`Proposed Schema`): XML schema definitions for JSPX

## Success Criteria
- Functional web framework capable of running Java web applications
- Clean separation between presentation and business logic
- Extensible architecture for custom controls and components
- Good performance with caching and optimization features
- Comprehensive documentation and examples

## Technology Stack
- **Java**: Core language (Servlet API 2.5+)
- **Maven**: Build and dependency management
- **XML/JEXL**: Page definitions and expression language
- **jQuery/Bootstrap**: Client-side frameworks
- **SLF4J**: Logging framework
- **Apache Commons**: Various utilities (file upload, expressions, etc.)
