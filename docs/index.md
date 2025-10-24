# Digi Android SDK Demo App

## Overview

This demo application showcases the integration of the Digi Android SDK, providing a comprehensive example of how to implement survey functionality in Android applications. The app demonstrates key SDK features including survey configuration, size customization, and result handling.

## 🎯 Purpose

This demo serves as both a **testing tool** for SDK functionality and a **reference implementation** for developers integrating the Digi SDK into their applications.

## 📱 Features

- **Survey Configuration**: Dynamic API URL, Survey ID, and Language settings
- **Size Customization**: Visual icon-based selectors for survey positioning
- **Real-time Testing**: Live survey launching with immediate feedback
- **Debug Logging**: Comprehensive logging for troubleshooting
- **Modern UI**: Material Design with intuitive icon-based controls

## 📚 Documentation Structure

### Core Integration Guides
- **[SDK Integration Guide](sdk-integration.md)** - Step-by-step SDK setup and basic integration
- **[Configuration Guide](configuration.md)** - How to configure surveys and handle parameters
- **[UI Customization Guide](ui-customization.md)** - Survey positioning, margins, and visual customization

### Advanced Topics
- **[Architecture Overview](architecture.md)** - App structure and key components
- **[Code Examples](code-examples.md)** - Practical code snippets and patterns
- **[Troubleshooting Guide](troubleshooting.md)** - Common issues and solutions

### Reference
- **[API Reference](api-reference.md)** - Complete SDK API documentation
- **[Best Practices](best-practices.md)** - Recommended implementation patterns

## 🚀 Quick Start

1. **Clone the repository**
2. **Configure Google Artifact Registry** (see [SDK Integration Guide](sdk-integration.md))
3. **Build and run** the demo app
4. **Test different configurations** using the UI controls
5. **Review the code** to understand integration patterns

## 🔧 Key Integration Points

### Essential Files for SDK Integration
- `app/src/main/java/com/sandsiv/surveytest/MainActivity.kt` - Main integration logic
- `app/src/main/java/com/sandsiv/surveytest/SurveyTestApplication.kt` - Application initialization
- `app/src/main/res/layout/activity_main.xml` - UI layout with size selectors
- `app/build.gradle` - SDK dependency configuration

### Critical Code Sections
- **SDK Initialization** - `DigiModule.init()` in MainActivity
- **Survey Launching** - `DigiModule.show()` with margin calculations
- **Size Configuration** - Icon-based selectors with margin logic
- **Result Handling** - Callback implementation for survey completion

## 📋 Requirements

- **Android SDK**: API level 21+ (Android 5.0+)
- **Kotlin**: 1.9.10+
- **Gradle**: 7.3.0+
- **Google Artifact Registry Access**: For SDK dependency

## 🎨 UI Components

### Survey Size Selectors
- **Full Screen**: Complete screen coverage
- **Middle 1/3**: Centered survey with equal margins
- **Bottom 1/3**: Bottom-positioned survey

### Configuration Fields
- **API URL**: Dynamic script endpoint configuration
- **Survey ID**: Numeric survey identifier
- **Language**: Survey language code (e.g., "en", "es")

## 🔍 Debug Features

- **Console Logging**: WebView JavaScript console capture
- **Dimension Logging**: Screen size and margin calculations
- **Network Logging**: HTTP request/response tracking
- **State Logging**: SDK initialization and survey lifecycle

## 📖 Next Steps

1. **Read the [SDK Integration Guide](sdk-integration.md)** for basic setup
2. **Explore [Code Examples](code-examples.md)** for implementation patterns
3. **Review [Best Practices](best-practices.md)** for production recommendations
4. **Check [Troubleshooting Guide](troubleshooting.md)** if you encounter issues

## 🤝 Support

For additional support or questions about SDK integration:
- Review the troubleshooting guide
- Check the API reference for detailed method documentation
- Examine the code examples for implementation patterns

---

*This documentation is designed to help developers successfully integrate the Digi Android SDK into their applications. Each guide focuses on specific aspects of integration, from basic setup to advanced customization.*
