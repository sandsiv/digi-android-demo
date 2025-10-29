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
   # Authenticate with Google Cloud
   gcloud auth application-default login
   
   # Refresh token and set environment variable
   ./refresh-token.sh
   export GOOGLE_ACCESS_TOKEN=$(gcloud auth application-default print-access-token)
   ```

3. **Build and run**:
   ```bash
   # Option 1: Use the automated build script (recommended)
   ./build.sh
   
   # Option 2: Manual build
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
   # Run the refresh script to set up tokens
   ./refresh-token.sh
   
   # Set environment variable for current session (REQUIRED)
   export GOOGLE_ACCESS_TOKEN=$(gcloud auth application-default print-access-token)
   ```

3. **Build the project**:
   ```bash
   ./gradlew assembleDebug
   ```

### ⚠️ Important Notes for Developers

**The environment variable `GOOGLE_ACCESS_TOKEN` is REQUIRED for building.** The `settings.gradle` file uses `System.getenv("GOOGLE_ACCESS_TOKEN")` to authenticate with Google Artifact Registry.

**If you get `401 Unauthorized` errors:**
1. Run `gcloud auth application-default login`
2. Run `./refresh-token.sh`
3. Run `export GOOGLE_ACCESS_TOKEN=$(gcloud auth application-default print-access-token)`
4. Then build: `./gradlew assembleDebug`

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

