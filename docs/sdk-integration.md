# SDK Integration Guide

## Overview

This guide provides step-by-step instructions for integrating the Digi Android SDK into your Android application. The demo app serves as a complete reference implementation.

## 📋 Prerequisites

- Android Studio (latest version recommended)
- Android SDK API level 21+ (Android 5.0+)
- Google Artifact Registry access credentials
- Basic knowledge of Android development and Kotlin

## 🔧 Step 1: Project Setup

### 1.1 Add Repository Configuration

Add the Google Artifact Registry repository to your `settings.gradle`:

```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url "https://europe-maven.pkg.dev/sandsiv-infrastructure/digi-module"
            credentials {
                username = "oauth2accesstoken"
                password = "YOUR_ACCESS_TOKEN_HERE"
            }
        }
    }
}
```

**⚠️ Important**: Replace `YOUR_ACCESS_TOKEN_HERE` with your actual Google Cloud access token.

### 1.2 Add SDK Dependency

Add the SDK dependency to your app's `build.gradle`:

```gradle
dependencies {
    // Digi SDK
    implementation "com.sandsiv:digi:1.0.5"
    
    // Required OkHttp dependencies
    implementation "com.squareup.okhttp3:okhttp:4.12.0"
    implementation "com.squareup.okhttp3:logging-interceptor:4.12.0"
}
```

### 1.3 Configure Kotlin Version

Ensure your project uses compatible Kotlin version:

```gradle
// In your project-level build.gradle
plugins {
    id 'org.jetbrains.kotlin.android' version '1.9.10' apply false
}
```

## 🚀 Step 2: Basic Integration

### 2.1 Initialize the SDK

Initialize the SDK in your Application class:

```kotlin
// SurveyTestApplication.kt
class SurveyTestApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // SDK will be initialized when user launches a survey
    }
}
```

**Alternative**: Initialize globally in Application.onCreate():

```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DigiModule.init(
            url = "https://your-domain.com/digi_runner.js",
            context = this
        )
    }
}
```

### 2.2 Launch a Survey

Launch a survey from your Activity:

```kotlin
// MainActivity.kt - Key integration example
private fun launchSurvey() {
    val apiUrl = "https://genie-survey.sandsiv.com/digi_runner.js"
    val surveyId = 162
    val language = "en"
    
    // Initialize SDK with your script URL
    DigiModule.init(
        url = apiUrl,
        context = this
    )
    
    // Prepare advanced parameters
    val advancedParams = HashMap<String, Any>()
    advancedParams["customerId"] = "user_12345"
    advancedParams["customField"] = "customValue"
    
    // Launch survey with configuration
    DigiModule.show(
        surveyId = surveyId,
        language = language,
        params = advancedParams, // Pass custom parameters
        context = this,
        margins = Margins(0, 0, 0, 0), // Full screen
        cornerRadius = 16,
        onResult = { result ->
            // Handle survey result
            when (result) {
                is Result.Success -> {
                    Log.d("Survey", "Survey completed successfully")
                    // Handle success
                }
                is Result.Error -> {
                    Log.e("Survey", "Survey failed: ${result.message}")
                    // Handle error
                }
            }
        }
    )
}
```

## 🎨 Step 3: UI Customization

### 3.1 Survey Positioning

Configure survey positioning using margins:

```kotlin
// Full screen (no margins)
val fullScreenMargins = Margins(0, 0, 0, 0)

// Middle third of screen
val middleMargins = Margins(
    top = screenHeightDp * 0.33,    // 33% from top
    bottom = screenHeightDp * 0.33, // 33% from bottom
    start = 0,
    end = 0
)

// Bottom third of screen
val bottomMargins = Margins(
    top = screenHeightDp * 0.67,   // 67% from top
    bottom = 0,
    start = 0,
    end = 0
)
```

### 3.2 Dynamic Margin Calculation

```kotlin
private fun calculateMargins(): Margins {
    val displayMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(displayMetrics)
    val screenHeightDp = displayMetrics.heightPixels / displayMetrics.density
    
    return when (selectedSize) {
        R.id.size_full_screen -> Margins(0, 0, 0, 0)
        R.id.size_middle_third -> {
            val marginDp = (screenHeightDp * 0.33).toInt()
            Margins(marginDp, marginDp, 0, 0)
        }
        R.id.size_bottom_third -> {
            val topMarginDp = (screenHeightDp * 0.67).toInt()
            Margins(topMarginDp, 0, 0, 0)
        }
        else -> Margins(0, 0, 0, 0)
    }
}
```

## 🔍 Step 4: Debug and Logging

### 4.1 Enable WebView Debugging

```kotlin
// In your WebView configuration
if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
    WebView.setWebContentsDebuggingEnabled(true)
}
```

## 🔧 Step 5: Advanced Settings

### 4.1 Customer ID and Metadata

The SDK supports passing custom parameters to surveys:

```kotlin
// Prepare advanced parameters
val advancedParams = HashMap<String, Any>()

// Customer ID (required for user identification)
advancedParams["customerId"] = "user_12345"

// Custom metadata (key-value pairs)
advancedParams["userType"] = "premium"
advancedParams["region"] = "US"
advancedParams["version"] = "2.1.0"

// Launch survey with parameters
DigiModule.show(
    surveyId = surveyId,
    language = language,
    params = advancedParams, // Pass custom parameters
    context = this,
    margins = margins,
    cornerRadius = 16,
    onResult = { result -> /* Handle result */ }
)
```

### 4.2 Parameter Validation

```kotlin
// Validate metadata names (alphanumeric and underscore only)
if (name.matches(Regex("[a-zA-Z0-9_]+"))) {
    advancedParams[name] = value
} else {
    Log.w("Survey", "Invalid metadata name: $name")
}
```

### 4.3 Settings Persistence

```kotlin
// Save settings using SharedPreferences
private fun saveSettings() {
    val editor = sharedPreferences.edit()
    editor.putString("apiUrl", apiUrl)
    editor.putString("surveyId", surveyId)
    editor.putString("language", language)
    editor.putString("customerId", customerId)
    // Save metadata...
    editor.apply()
}

// Load settings on app startup
private fun loadSettings() {
    val savedApiUrl = sharedPreferences.getString("apiUrl", "")
    val savedSurveyId = sharedPreferences.getString("surveyId", "")
    // Load other settings...
}
```

### 4.4 Console Message Handling

```kotlin
webView.webChromeClient = object : WebChromeClient() {
    override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
        Log.d("WebView", "Console: ${consoleMessage.message()}")
        return true
    }
}
```

## 📱 Step 6: Android Manifest Configuration

### 5.1 Required Permissions

```xml
<!-- AndroidManifest.xml -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.CHANGE_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

### 5.2 Network Security Configuration

```xml
<!-- Allow HTTP traffic for development -->
<application
    android:usesCleartextTraffic="true"
    ... >
```

## 🎯 Step 7: Result Handling

### 6.1 Survey Result Types

```kotlin
DigiModule.show(
    // ... other parameters
    onResult = { result ->
        when (result) {
            is Result.Success -> {
                // Survey completed successfully
                val responseData = result.data
                // Process survey results
            }
            is Result.Error -> {
                // Survey failed or was cancelled
                val errorCode = result.status
                val errorMessage = result.message
                // Handle error appropriately
            }
        }
    }
)
```

### 6.2 Error Handling Best Practices

```kotlin
onResult = { result ->
    when (result) {
        is Result.Success -> {
            // Show success message
            Toast.makeText(this, "Survey completed!", Toast.LENGTH_SHORT).show()
            // Update UI state
            updateUIAfterSurvey()
        }
        is Result.Error -> {
            // Log error for debugging
            Log.e("Survey", "Error ${result.status}: ${result.message}")
            
            // Show user-friendly message
            when (result.status) {
                401 -> showError("Authentication failed")
                404 -> showError("Survey not found")
                else -> showError("Survey failed: ${result.message}")
            }
        }
    }
}
```

## 🔧 Step 8: Advanced Configuration

### 7.1 Custom Parameters

```kotlin
val customParams = hashMapOf(
    "customerId" to "12345",
    "sessionId" to "abc123",
    "metadata" to "custom_data"
)

DigiModule.show(
    surveyId = 162,
    language = "en",
    params = customParams,
    context = this,
    onResult = { result -> /* handle result */ }
)
```

### 7.2 Corner Radius Customization

```kotlin
DigiModule.show(
    // ... other parameters
    cornerRadius = 24, // Custom corner radius in DP
    onResult = { result -> /* handle result */ }
)
```

## ✅ Step 9: Testing Your Integration

### 8.1 Test Different Configurations

1. **Test different survey sizes** using the demo app's icon selectors
2. **Verify network connectivity** and script loading
3. **Test error scenarios** (invalid survey ID, network issues)
4. **Validate result handling** for both success and error cases

### 8.2 Debug Checklist

- [ ] SDK initializes without errors
- [ ] Survey loads and displays correctly
- [ ] Size positioning works as expected
- [ ] Results are handled properly
- [ ] Error scenarios are handled gracefully
- [ ] Network permissions are configured
- [ ] Console logging is working

## 🚨 Common Issues

### Issue: "Could not resolve com.sandsiv:digi"
**Solution**: Check your Google Artifact Registry credentials and access token

### Issue: "Survey not loading"
**Solution**: Verify network permissions and script URL accessibility

### Issue: "Margins not working"
**Solution**: Ensure margins are calculated in DP, not pixels, and use correct parameter order

## 📖 Next Steps

- Review the [Configuration Guide](configuration.md) for advanced setup
- Explore [UI Customization Guide](ui-customization.md) for visual customization
- Check [Code Examples](code-examples.md) for implementation patterns
- Read [Best Practices](best-practices.md) for production recommendations

---

*This integration guide provides the foundation for implementing the Digi Android SDK. For advanced features and customization options, refer to the other documentation files.*

