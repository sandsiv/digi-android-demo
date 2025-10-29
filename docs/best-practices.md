# Best Practices

## Overview

This guide provides best practices for integrating the Digi Android SDK into production applications, covering performance, security, user experience, and maintainability.

## 🚀 Performance Best Practices

### Memory Management

**1. WebView Lifecycle Management**
```kotlin
override fun onDestroy() {
    super.onDestroy()
    // Clean up WebView resources
    webView?.destroy()
    webView = null
}

override fun onPause() {
    super.onPause()
    webView?.onPause()
}

override fun onResume() {
    super.onResume()
    webView?.onResume()
}
```

**2. Efficient Resource Usage**
```kotlin
// Use lazy initialization for heavy objects
private val surveyManager by lazy { SurveyManager(this) }

// Cache frequently used values
private val screenMetrics by lazy {
    val displayMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(displayMetrics)
    displayMetrics
}
```

### Network Optimization

**1. Connection Timeout Configuration**
```kotlin
val client = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .build()
```

**2. Caching Strategy**
```kotlin
// Cache survey configurations
private val surveyConfigCache = mutableMapOf<String, SurveyConfig>()

fun getCachedConfig(key: String): SurveyConfig? {
    return surveyConfigCache[key]
}

fun cacheConfig(key: String, config: SurveyConfig) {
    surveyConfigCache[key] = config
}
```

## 🔒 Security Best Practices

### Network Security

**1. HTTPS Only in Production**
```kotlin
// Use HTTPS in production, HTTP only for development
val scriptUrl = when (BuildConfig.BUILD_TYPE) {
    "release" -> "https://secure.yourcompany.com/digi_runner.js"
    "debug" -> "http://dev.yourcompany.com/digi_runner.js"
    else -> "https://staging.yourcompany.com/digi_runner.js"
}
```

**2. Certificate Pinning**
```kotlin
val certificatePinner = CertificatePinner.Builder()
    .add("yourcompany.com", "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=")
    .build()

val client = OkHttpClient.Builder()
    .certificatePinner(certificatePinner)
    .build()
```

### Data Protection

**1. Sensitive Data Handling**
```kotlin
// Don't log sensitive information
private fun logSurveyLaunch(surveyId: Int, language: String) {
    Log.d("Survey", "Launching survey: ID=$surveyId, Language=$language")
    // Don't log: API URLs, user data, survey responses
}
```

**2. Input Validation**
```kotlin
private fun validateSurveyInput(apiUrl: String, surveyId: String, language: String): Boolean {
    return apiUrl.isNotEmpty() && 
           apiUrl.startsWith("https://") && 
           surveyId.matches(Regex("\\d+")) && 
           language.matches(Regex("[a-z]{2}"))
}
```

## 🎨 User Experience Best Practices

### Error Handling

**1. User-Friendly Error Messages**
```kotlin
private fun handleSurveyError(result: Result.Error) {
    val userMessage = when (result.status) {
        401 -> "Please log in again to continue"
        404 -> "This survey is not available right now"
        500 -> "We're experiencing technical difficulties. Please try again later."
        else -> "Something went wrong. Please try again."
    }
    
    showError(userMessage)
    Log.e("Survey", "Technical error: ${result.message}")
}
```

**2. Loading States**
```kotlin
private fun showLoadingState() {
    launchButton.isEnabled = false
    launchButton.text = "Loading..."
    updateStatus("Initializing survey...")
}

private fun hideLoadingState() {
    launchButton.isEnabled = true
    launchButton.text = "🚀 SHOW SURVEY"
    updateStatus("Ready to launch survey")
}
```

### Accessibility

**1. Screen Reader Support**
```xml
<!-- Add accessibility labels -->
<RadioButton
    android:id="@+id/size_full_screen"
    android:contentDescription="Full screen survey size"
    android:accessibilityTraits="button" />
```

**2. Keyboard Navigation**
```kotlin
// Ensure proper focus management
private fun setupAccessibility() {
    apiUrlInput.nextFocusDownId = R.id.survey_id_input
    surveyIdInput.nextFocusDownId = R.id.language_input
    languageInput.nextFocusDownId = R.id.launch_survey_button
}
```

## 🔧 Code Organization Best Practices

### Separation of Concerns

**1. Configuration Management**
```kotlin
// Separate configuration class
class SurveyConfiguration {
    var apiUrl: String = ""
    var surveyId: Int = 0
    var language: String = ""
    var size: SurveySize = SurveySize.FULL_SCREEN
    var cornerRadius: Int = 16
    
    fun isValid(): Boolean {
        return apiUrl.isNotEmpty() && 
               surveyId > 0 && 
               language.isNotEmpty()
    }
}
```

**2. Business Logic Separation**
```kotlin
// Separate business logic from UI
class SurveyManager(private val context: Context) {
    fun launchSurvey(config: SurveyConfiguration, onResult: (Result) -> Unit) {
        // Business logic here
    }
    
    fun calculateMargins(screenHeightDp: Float, size: SurveySize): Margins {
        // Margin calculation logic here
    }
}
```

### Error Handling Patterns

**1. Result Wrapper Pattern**
```kotlin
sealed class SurveyResult {
    data class Success(val data: Any) : SurveyResult()
    data class Error(val message: String, val code: Int) : SurveyResult()
    object Loading : SurveyResult()
}
```

**2. Exception Handling**
```kotlin
private fun safeLaunchSurvey() {
    try {
        launchSurvey()
    } catch (e: Exception) {
        Log.e("Survey", "Unexpected error", e)
        showError("An unexpected error occurred. Please try again.")
    }
}
```

## 📱 Platform Best Practices

### Android-Specific Considerations

**1. Lifecycle Management**
```kotlin
class MainActivity : AppCompatActivity() {
    private var surveyManager: SurveyManager? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        surveyManager = SurveyManager(this)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        surveyManager = null
    }
}
```

**2. Configuration Changes**
```kotlin
override fun onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig)
    
    // Recalculate margins for new orientation
    val newMargins = calculateMargins()
    // Update survey if currently showing
}
```

### Screen Size Adaptation

**1. Responsive Design**
```kotlin
private fun getResponsiveMargins(): Margins {
    val screenHeightDp = screenMetrics.heightPixels / screenMetrics.density
    
    return when {
        screenHeightDp > 800 -> getLargeScreenMargins(screenHeightDp)
        screenHeightDp > 600 -> getMediumScreenMargins(screenHeightDp)
        else -> getSmallScreenMargins(screenHeightDp)
    }
}
```

**2. Orientation Handling**
```kotlin
private fun handleOrientationChange(orientation: Int) {
    when (orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            // Adjust for landscape
            adjustLayoutForLandscape()
        }
        Configuration.ORIENTATION_PORTRAIT -> {
            // Adjust for portrait
            adjustLayoutForPortrait()
        }
    }
}
```

## 🔍 Debugging Best Practices

### Logging Strategy

**1. Structured Logging**
```kotlin
private fun logSurveyEvent(event: String, data: Map<String, Any>) {
    Log.d("Survey", "Event: $event, Data: $data")
}

// Usage
logSurveyEvent("survey_launched", mapOf(
    "survey_id" to surveyId,
    "language" to language,
    "size" to selectedSize
))
```

**2. Debug vs Production Logging**
```kotle
private fun debugLog(message: String) {
    if (BuildConfig.DEBUG) {
        Log.d("Survey", message)
    }
}
```

### Testing Strategy

**1. Unit Testing**
```kotlin
@Test
fun testMarginCalculation() {
    val screenHeightDp = 800f
    val margins = calculateMargins(screenHeightDp, SurveySize.MIDDLE_THIRD)
    
    assertEquals(264, margins.top) // 800 * 0.33
    assertEquals(264, margins.bottom)
}
```

**2. Integration Testing**
```kotlin
@Test
fun testSurveyLaunch() {
    val config = SurveyConfiguration(
        apiUrl = "https://test.com/script.js",
        surveyId = 162,
        language = "en"
    )
    
    val result = surveyManager.launchSurvey(config) { result ->
        assertTrue(result is SurveyResult.Success)
    }
}
```

## 🚀 Production Deployment Best Practices

### Build Configuration

**1. Environment-Specific Configuration**
```kotlin
// BuildConfig fields
buildConfigField "String", "API_BASE_URL", "\"https://api.yourcompany.com\""
buildConfigField "boolean", "ENABLE_DEBUG_LOGGING", "false"
buildConfigField "String", "VERSION_NAME", "\"${versionName}\""
```

**2. Proguard Configuration**
```proguard
# Keep SDK classes
-keep class com.sandsiv.digi.** { *; }

# Keep result classes
-keep class com.sandsiv.digi.Result { *; }
-keep class com.sandsiv.digi.Margins { *; }
```

### Monitoring and Analytics

**1. Performance Monitoring**
```kotlin
private fun trackSurveyPerformance(startTime: Long, endTime: Long) {
    val duration = endTime - startTime
    analytics.track("survey_performance", mapOf(
        "duration_ms" to duration,
        "survey_id" to surveyId
    ))
}
```

**2. Error Tracking**
```kotlin
private fun trackSurveyError(error: Result.Error) {
    analytics.track("survey_error", mapOf(
        "error_code" to error.status,
        "error_message" to error.message,
        "survey_id" to surveyId
    ))
}
```

## 🔧 Maintenance Best Practices

### Code Documentation

**1. Method Documentation**
```kotlin
/**
 * Calculates survey margins based on screen size and selected survey size.
 * 
 * @param screenHeightDp Screen height in density-independent pixels
 * @param selectedSize The selected survey size (full screen, middle third, bottom third)
 * @return Margins object with calculated top, bottom, start, and end margins
 */
private fun calculateMargins(screenHeightDp: Float, selectedSize: SurveySize): Margins {
    // Implementation
}
```

**2. Configuration Documentation**
```kotlin
/**
 * Survey configuration class containing all parameters needed to launch a survey.
 * 
 * @property apiUrl The script URL for the survey
 * @property surveyId Numeric identifier for the survey
 * @property language ISO 639-1 language code
 * @property size Visual size of the survey on screen
 * @property cornerRadius Corner radius in DP for the survey container
 */
data class SurveyConfiguration(
    val apiUrl: String,
    val surveyId: Int,
    val language: String,
    val size: SurveySize,
    val cornerRadius: Int = 16
)
```

### Version Management

**1. SDK Version Management**
```gradle
// Use specific versions, not ranges
implementation "com.sandsiv:digi:1.0.5"

// Document version changes
// v1.0.5 - Current stable version
```

**2. Dependency Updates**
```kotlin
// Regular dependency updates
dependencies {
    implementation "com.sandsiv:digi:1.0.5"
    implementation "com.squareup.okhttp3:okhttp:4.12.0"
    implementation "androidx.core:core-ktx:1.8.0"
}
```

## 📋 Production Checklist

### Pre-Deployment
- [ ] All sensitive data removed from logs
- [ ] HTTPS URLs configured for production
- [ ] Error handling tested for all scenarios
- [ ] Performance optimized for target devices
- [ ] Accessibility features implemented
- [ ] Security measures in place

### Post-Deployment
- [ ] Monitoring and analytics configured
- [ ] Error tracking active
- [ ] Performance metrics collected
- [ ] User feedback mechanisms in place
- [ ] Update mechanisms ready

### Ongoing Maintenance
- [ ] Regular dependency updates
- [ ] Security patches applied
- [ ] Performance monitoring active
- [ ] User feedback reviewed
- [ ] Documentation updated

## 🚨 Common Pitfalls to Avoid

### Performance Issues
- **Don't**: Initialize SDK multiple times
- **Don't**: Keep WebView references after Activity destruction
- **Don't**: Use blocking operations on main thread

### Security Issues
- **Don't**: Log sensitive information
- **Don't**: Use HTTP in production
- **Don't**: Store credentials in code

### User Experience Issues
- **Don't**: Show technical error messages to users
- **Don't**: Block UI during network operations
- **Don't**: Ignore accessibility requirements

### Code Quality Issues
- **Don't**: Mix UI and business logic
- **Don't**: Use hard-coded values
- **Don't**: Ignore error handling

## 📖 Next Steps

- Review [Troubleshooting Guide](troubleshooting.md) for common issues
- Check [Code Examples](code-examples.md) for implementation patterns
- Read [API Reference](api-reference.md) for detailed method documentation
- Explore [Architecture Overview](architecture.md) for app structure

---

*These best practices ensure your SDK integration is production-ready, maintainable, and provides an excellent user experience. Follow them to avoid common pitfalls and ensure long-term success.*

