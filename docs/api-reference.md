# API Reference

## Overview

This document provides comprehensive API reference for the Digi Android SDK, including all public methods, classes, and configuration options.

## 📚 Core Classes

### DigiModule

The main SDK class providing survey functionality.

#### Methods

##### `init(url: String, context: Context)`

Initializes the SDK with the script URL and Android context.

**Parameters**:
- `url: String` - The script URL for the survey (e.g., "https://genie-survey.sandsiv.com/digi_runner.js")
- `context: Context` - Android context (typically Activity context)

**Example**:
```kotlin
DigiModule.init(
    url = "https://genie-survey.sandsiv.com/digi_runner.js",
    context = this
)
```

**Notes**:
- Must be called before launching any surveys
- Can be called multiple times with different URLs
- Use Activity context, not Application context

##### `show(surveyId: Int, language: String, context: Context, onResult: (Result) -> Unit)`

Launches a survey with basic configuration.

**Parameters**:
- `surveyId: Int` - Numeric identifier for the survey
- `language: String` - ISO 639-1 language code (e.g., "en", "es", "fr")
- `context: Context` - Android context (typically Activity context)
- `onResult: (Result) -> Unit` - Callback for survey results

**Example**:
```kotlin
DigiModule.show(
    surveyId = 162,
    language = "en",
    context = this,
    onResult = { result ->
        when (result) {
            is Result.Success -> { /* Handle success */ }
            is Result.Error -> { /* Handle error */ }
        }
    }
)
```

##### `show(surveyId: Int, language: String, context: Context, params: Map<String, Any>?, margins: Margins?, cornerRadius: Int?, onResult: (Result) -> Unit)`

Launches a survey with advanced configuration.

**Parameters**:
- `surveyId: Int` - Numeric identifier for the survey
- `language: String` - ISO 639-1 language code
- `context: Context` - Android context
- `params: Map<String, Any>?` - Additional parameters (optional)
- `margins: Margins?` - Survey positioning margins (optional)
- `cornerRadius: Int?` - Corner radius in DP (optional)
- `onResult: (Result) -> Unit` - Callback for survey results

**Example**:
```kotlin
DigiModule.show(
    surveyId = 162,
    language = "en",
    context = this,
    params = mapOf(
        "customerId" to "12345",
        "sessionId" to "abc123"
    ),
    margins = Margins(100, 100, 0, 0),
    cornerRadius = 16,
    onResult = { result -> /* Handle result */ }
)
```

### Margins

Data class for survey positioning.

#### Constructor

```kotlin
data class Margins(
    val top: Int = 0,      // Top margin in DP
    val bottom: Int = 0,   // Bottom margin in DP
    val start: Int = 0,    // Start margin in DP (left in LTR)
    val end: Int = 0       // End margin in DP (right in LTR)
)
```

#### Examples

**Full Screen**:
```kotlin
val fullScreenMargins = Margins(0, 0, 0, 0)
```

**Centered (Middle Third)**:
```kotlin
val screenHeightDp = screenHeightPx / density
val marginDp = (screenHeightDp * 0.33).toInt()
val centeredMargins = Margins(marginDp, marginDp, 0, 0)
```

**Bottom Positioned**:
```kotlin
val screenHeightDp = screenHeightPx / density
val topMarginDp = (screenHeightDp * 0.67).toInt()
val bottomMargins = Margins(topMarginDp, 0, 0, 0)
```

**Custom Positioning**:
```kotlin
val customMargins = Margins(
    top = 100,      // 100dp from top
    bottom = 50,    // 50dp from bottom
    start = 20,     // 20dp from start
    end = 20        // 20dp from end
)
```

### Result

Sealed class representing survey results.

#### Success Result

```kotlin
data class Success(
    val data: Any,           // Survey response data
    val timestamp: Long      // Completion timestamp
) : Result
```

**Usage**:
```kotlin
when (result) {
    is Result.Success -> {
        val surveyData = result.data
        val completionTime = result.timestamp
        // Handle successful completion
    }
}
```

#### Error Result

```kotlin
data class Error(
    val status: Int,         // HTTP status code
    val message: String      // Error message
) : Result
```

**Usage**:
```kotlin
when (result) {
    is Result.Error -> {
        when (result.status) {
            401 -> { /* Authentication error */ }
            404 -> { /* Survey not found */ }
            500 -> { /* Server error */ }
            else -> { /* Generic error */ }
        }
    }
}
```

## 🔧 Configuration Options

### Survey Parameters

#### Basic Parameters

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `surveyId` | `Int` | Yes | Numeric survey identifier |
| `language` | `String` | Yes | ISO 639-1 language code |
| `context` | `Context` | Yes | Android context |

#### Optional Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `params` | `Map<String, Any>?` | `null` | Additional survey parameters |
| `margins` | `Margins?` | `null` | Survey positioning margins |
| `cornerRadius` | `Int?` | `null` | Corner radius in DP |

### Common Parameter Values

#### Language Codes

| Code | Language |
|------|----------|
| `"en"` | English |
| `"es"` | Spanish |
| `"fr"` | French |
| `"de"` | German |
| `"it"` | Italian |
| `"pt"` | Portuguese |
| `"ru"` | Russian |
| `"zh"` | Chinese |
| `"ja"` | Japanese |
| `"ko"` | Korean |

#### Corner Radius Values

| Value | Description |
|-------|-------------|
| `0` | Sharp corners (rectangular) |
| `8` | Subtle rounding |
| `16` | Standard Material Design |
| `24` | Pronounced rounding |
| `32` | Very rounded |

## 📱 Integration Patterns

### Basic Integration

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Initialize SDK
        DigiModule.init(
            url = "https://genie-survey.sandsiv.com/digi_runner.js",
            context = this
        )
        
        // Launch survey
        launchSurvey()
    }
    
    private fun launchSurvey() {
        DigiModule.show(
            surveyId = 162,
            language = "en",
            context = this,
            onResult = { result ->
                when (result) {
                    is Result.Success -> {
                        // Handle success
                    }
                    is Result.Error -> {
                        // Handle error
                    }
                }
            }
        )
    }
}
```

### Advanced Integration

```kotlin
class SurveyManager(private val context: Context) {
    fun launchSurvey(config: SurveyConfiguration, onResult: (Result) -> Unit) {
        // Initialize SDK
        DigiModule.init(
            url = config.apiUrl,
            context = context
        )
        
        // Calculate margins
        val margins = calculateMargins(config.size)
        
        // Launch survey
        DigiModule.show(
            surveyId = config.surveyId,
            language = config.language,
            context = context,
            params = config.customParams,
            margins = margins,
            cornerRadius = config.cornerRadius,
            onResult = onResult
        )
    }
    
    private fun calculateMargins(size: SurveySize): Margins {
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenHeightDp = displayMetrics.heightPixels / displayMetrics.density
        
        return when (size) {
            SurveySize.FULL_SCREEN -> Margins(0, 0, 0, 0)
            SurveySize.MIDDLE_THIRD -> {
                val marginDp = (screenHeightDp * 0.33).toInt()
                Margins(marginDp, marginDp, 0, 0)
            }
            SurveySize.BOTTOM_THIRD -> {
                val topMarginDp = (screenHeightDp * 0.67).toInt()
                Margins(topMarginDp, 0, 0, 0)
            }
        }
    }
}
```

## 🔍 Error Handling

### Common Error Codes

| Code | Description | Solution |
|------|-------------|----------|
| `401` | Unauthorized | Check authentication |
| `404` | Survey not found | Verify survey ID |
| `500` | Server error | Retry later |
| `-1` | Network error | Check connectivity |
| `-2` | SDK not initialized | Call `init()` first |

### Error Handling Patterns

```kotlin
private fun handleSurveyResult(result: Result) {
    when (result) {
        is Result.Success -> {
            // Extract survey data
            val surveyData = result.data
            val completionTime = result.timestamp
            
            // Update analytics
            analytics.trackSurveyCompleted(surveyId, completionTime)
            
            // Show success message
            showSuccessMessage("Survey completed successfully!")
        }
        is Result.Error -> {
            // Handle different error types
            when (result.status) {
                401 -> {
                    showError("Please log in again")
                    navigateToLogin()
                }
                404 -> {
                    showError("Survey not available")
                }
                500 -> {
                    showError("Server error. Please try again later.")
                }
                else -> {
                    showError("Survey failed: ${result.message}")
                }
            }
            
            // Log error for debugging
            Log.e("Survey", "Error ${result.status}: ${result.message}")
        }
    }
}
```

## 🎨 UI Customization

### Survey Positioning

#### Full Screen
```kotlin
val fullScreenMargins = Margins(0, 0, 0, 0)
```

#### Centered
```kotlin
val screenHeightDp = screenHeightPx / density
val marginDp = (screenHeightDp * 0.33).toInt()
val centeredMargins = Margins(marginDp, marginDp, 0, 0)
```

#### Bottom Positioned
```kotlin
val screenHeightDp = screenHeightPx / density
val topMarginDp = (screenHeightDp * 0.67).toInt()
val bottomMargins = Margins(topMarginDp, 0, 0, 0)
```

### Visual Customization

#### Corner Radius
```kotlin
DigiModule.show(
    // ... other parameters
    cornerRadius = 16,  // 16dp corner radius
    onResult = { result -> ... }
)
```

#### Custom Parameters
```kotlin
val customParams = mapOf(
    "customerId" to "12345",
    "sessionId" to "abc123",
    "userType" to "premium",
    "metadata" to "custom_data"
)

DigiModule.show(
    // ... other parameters
    params = customParams,
    onResult = { result -> ... }
)
```

## 🔧 Debugging

### Enable Debug Logging

```kotlin
// Enable WebView debugging
if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
    WebView.setWebContentsDebuggingEnabled(true)
}

// Console message handling
webView.webChromeClient = object : WebChromeClient() {
    override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
        Log.d("WebView", "Console: ${consoleMessage.message()}")
        return true
    }
}
```

### Network Logging

```kotlin
val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

val client = OkHttpClient.Builder()
    .addInterceptor(loggingInterceptor)
    .build()
```

## 📋 Best Practices

### Initialization
- Always call `init()` before launching surveys
- Use Activity context, not Application context
- Initialize once per survey session

### Error Handling
- Always provide user-friendly error messages
- Log technical details for debugging
- Handle all possible error scenarios

### Performance
- Calculate margins in DP, not pixels
- Cache frequently used values
- Clean up resources in `onDestroy()`

### Security
- Use HTTPS URLs in production
- Validate all input parameters
- Don't log sensitive information

## 🚨 Common Issues

### Build Issues
- **"Could not resolve com.sandsiv:digi"**: Check repository configuration and access token
- **"Incompatible classes"**: Update Kotlin version to 1.9.10
- **"Plugin not found"**: Simplify build.gradle configuration

### Runtime Issues
- **"Survey not loading"**: Check network permissions and script URL
- **"Wrong positioning"**: Verify margin calculations and parameter order
- **"Crashes on launch"**: Ensure SDK initialization and valid parameters

### UI Issues
- **"Icons not showing"**: Check drawable resources and selector configuration
- **"Selection not working"**: Verify RadioButton state handling
- **"Layout issues"**: Check layout constraints and screen size adaptation

## 📖 Additional Resources

### Documentation
- [SDK Integration Guide](sdk-integration.md)
- [Configuration Guide](configuration.md)
- [UI Customization Guide](ui-customization.md)
- [Code Examples](code-examples.md)
- [Best Practices](best-practices.md)
- [Troubleshooting Guide](troubleshooting.md)

### Support
- Check logs for specific error messages
- Verify network connectivity
- Test with different devices/emulators
- Review SDK documentation

---

*This API reference provides complete documentation for the Digi Android SDK. Use it as a reference for implementing survey functionality in your application.*

