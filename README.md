# Digi Android SDK Demo App

A comprehensive demo application showcasing the integration of the Digi Android SDK with complete documentation and examples.

## 🚀 Quick Start

### Prerequisites

- Android Studio (latest version)
- Android SDK API level 21+
- Google Cloud access to Artifact Registry
- Git

### Setup

1. **Clone the repository**:
   ```bash
   git clone https://github.com/sandsiv/digi-android-demo.git
   cd digi-android-demo
   ```

2. **Configure authentication** (see [Authentication Setup](#authentication-setup)):
   ```bash
   # Copy template and add your token
   cp gradle.properties.template gradle.properties
   # Edit gradle.properties with your Google Cloud access token
   ```

3. **Build and run**:
   ```bash
   ./gradlew assembleDebug
   ```

## 🔐 Authentication Setup

This project requires access to Google Artifact Registry. See [setup-auth.md](setup-auth.md) for detailed instructions.

### Quick Setup

1. **Get your access token**:
   ```bash
   gcloud auth application-default print-access-token
   ```

2. **Configure authentication**:
   ```bash
   # Option 1: Use gradle.properties (recommended)
   cp gradle.properties.template gradle.properties
   # Edit gradle.properties and add your token
   
   # Option 2: Use environment variable
   export GOOGLE_ACCESS_TOKEN="your_token_here"
   
   # Option 3: Use the refresh script
   ./refresh-token.sh
   ```

3. **Build the project**:
   ```bash
   ./gradlew build
   ```

## 📱 Features

- **Survey Configuration**: Dynamic API URL, Survey ID, and Language settings
- **Size Customization**: Visual icon-based selectors for survey positioning
- **Real-time Testing**: Live survey launching with immediate feedback
- **Debug Logging**: Comprehensive logging for troubleshooting
- **Modern UI**: Material Design with intuitive icon-based controls

## 📚 Documentation

Comprehensive documentation is available in the `docs/` folder:

- **[SDK Integration Guide](docs/sdk-integration.md)** - Step-by-step SDK setup
- **[Configuration Guide](docs/configuration.md)** - Survey configuration options
- **[UI Customization Guide](docs/ui-customization.md)** - Visual customization
- **[Code Examples](docs/code-examples.md)** - Practical implementation patterns
- **[Best Practices](docs/best-practices.md)** - Production recommendations
- **[Troubleshooting Guide](docs/troubleshooting.md)** - Common issues and solutions
- **[API Reference](docs/api-reference.md)** - Complete SDK documentation
- **[Architecture Overview](docs/architecture.md)** - App structure and components

## 🛠️ Development

### Project Structure

```
digi-android-demo/
├── app/                    # Main application module
│   ├── src/main/
│   │   ├── java/          # Kotlin source code
│   │   ├── res/           # Android resources
│   │   └── AndroidManifest.xml
│   └── build.gradle       # App-level build configuration
├── docs/                   # Comprehensive documentation
├── build.gradle           # Project-level build configuration
├── settings.gradle        # Repository configuration
├── gradle.properties.template  # Authentication template
├── refresh-token.sh       # Token refresh script
└── setup-auth.md         # Authentication setup guide
```

### Key Files

- **`app/src/main/java/com/sandsiv/surveytest/MainActivity.kt`** - Main integration logic
- **`app/src/main/res/layout/activity_main.xml`** - UI layout with size selectors
- **`settings.gradle`** - Repository configuration with secure token handling
- **`docs/`** - Complete documentation suite

### Build Configuration

The project uses:
- **SDK Version**: 1.0.21 from Google Artifact Registry
- **Android SDK**: API level 21+ (Android 5.0+)
- **Kotlin**: 1.9.10
- **Gradle**: 7.3.0

## 🔧 Troubleshooting

### Common Issues

1. **"Could not resolve com.sandsiv:digi"**:
   - Check your Google Cloud authentication
   - Verify access token is valid
   - Run `./refresh-token.sh` to get a fresh token

2. **"401 Unauthorized"**:
   - Re-authenticate with `gcloud auth application-default login`
   - Refresh your access token

3. **Build failures**:
   - Check Kotlin version compatibility
   - Verify all dependencies are resolved
   - Clean and rebuild: `./gradlew clean build`

### Getting Help

- Check the [Troubleshooting Guide](docs/troubleshooting.md)
- Review the [Authentication Setup](setup-auth.md)
- Examine the [Code Examples](docs/code-examples.md)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Make your changes
4. Test thoroughly
5. Commit your changes: `git commit -m "Add your feature"`
6. Push to the branch: `git push origin feature/your-feature`
7. Submit a pull request

## 📄 License

This project is part of the Digi SDK ecosystem. See the main SDK repository for licensing information.

## 🔗 Related Projects

- [Digi Android SDK](https://github.com/sandsiv/digi-android) - Main SDK repository
- [Digi Documentation](https://docs.sandsiv.com) - Complete SDK documentation

---

*This demo app serves as both a testing tool and a reference implementation for integrating the Digi Android SDK into your applications.*
