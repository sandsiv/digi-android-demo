# UI Customization Guide

## Overview

This guide explains how to customize the visual appearance and behavior of surveys in your Android application using the Digi SDK.

## 🎨 Visual Customization

### Survey Positioning

The SDK provides flexible positioning options through the margin system:

```kotlin
// Full screen - no margins
val fullScreenMargins = Margins(0, 0, 0, 0)

// Centered with equal margins
val centeredMargins = Margins(100, 100, 0, 0)

// Bottom positioned
val bottomMargins = Margins(400, 0, 0, 0)

// Custom positioning
val customMargins = Margins(
    top = 50,      // 50dp from top
    bottom = 100,  // 100dp from bottom
    start = 20,    // 20dp from start
    end = 20       // 20dp from end
)
```

### Corner Radius Customization

```kotlin
DigiModule.show(
    // ... other parameters
    cornerRadius = 16,  // 16dp corner radius
    onResult = { result -> ... }
)
```

**Corner radius options:**
- `0` - Sharp corners (rectangular)
- `8` - Subtle rounding
- `16` - Standard Material Design
- `24` - Pronounced rounding
- `32` - Very rounded

## 📱 Size Selector Implementation

### Icon-Based Selectors

The demo app uses visual icon selectors for survey size configuration:

```xml
<!-- activity_main.xml -->
<RadioGroup
    android:id="@+id/size_radio_group"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal"
    android:gravity="center">

    <RadioButton
        android:id="@+id/size_full_screen"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:button="@null"
        android:drawableTop="@drawable/selector_survey_full_screen"
        android:gravity="center"
        android:padding="16dp"
        android:background="?attr/selectableItemBackgroundBorderless"
        android:checked="true" />

    <!-- Additional size options... -->
</RadioGroup>
```

### Custom Icon Implementation

Create custom icons for different survey sizes:

```xml
<!-- ic_survey_full_screen.xml -->
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="48dp"
    android:height="48dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="#4285F4"
        android:strokeColor="#000000"
        android:strokeWidth="1"
        android:pathData="M3,3h18v18H3z" />
</vector>
```

### Selection State Handling

```xml
<!-- selector_survey_full_screen.xml -->
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:state_checked="true" 
          android:drawable="@drawable/ic_survey_full_screen_selected" />
    <item android:drawable="@drawable/ic_survey_full_screen" />
</selector>
```

## 🎯 Dynamic Size Calculation

### Screen-Aware Positioning

```kotlin
private fun calculateMargins(): Margins {
    val displayMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(displayMetrics)
    val screenHeightDp = displayMetrics.heightPixels / displayMetrics.density
    val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
    
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

### Responsive Design

```kotlin
private fun getResponsiveMargins(): Margins {
    val displayMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(displayMetrics)
    val screenHeightDp = displayMetrics.heightPixels / displayMetrics.density
    
    return when {
        screenHeightDp > 800 -> {
            // Large screens - more generous margins
            val marginDp = (screenHeightDp * 0.25).toInt()
            Margins(marginDp, marginDp, 0, 0)
        }
        screenHeightDp > 600 -> {
            // Medium screens - standard margins
            val marginDp = (screenHeightDp * 0.33).toInt()
            Margins(marginDp, marginDp, 0, 0)
        }
        else -> {
            // Small screens - minimal margins
            val marginDp = (screenHeightDp * 0.1).toInt()
            Margins(marginDp, marginDp, 0, 0)
        }
    }
}
```

## 🎨 Material Design Integration

### Theme Integration

```xml
<!-- styles.xml -->
<style name="SurveyTheme" parent="Theme.Material3.DayNight">
    <item name="colorPrimary">@color/survey_primary</item>
    <item name="colorOnPrimary">@color/survey_on_primary</item>
    <item name="colorSurface">@color/survey_surface</item>
    <item name="colorOnSurface">@color/survey_on_surface</item>
</style>
```

### Color Customization

```xml
<!-- colors.xml -->
<resources>
    <color name="survey_primary">#4285F4</color>
    <color name="survey_primary_selected">#FF6200EE</color>
    <color name="survey_surface">#FFFFFF</color>
    <color name="survey_on_surface">#000000</color>
</resources>
```

## 🔧 Advanced UI Customization

### Custom Survey Container

```kotlin
// Custom survey container with additional styling
class CustomSurveyContainer : FrameLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    
    init {
        // Apply custom styling
        background = ContextCompat.getDrawable(context, R.drawable.custom_survey_background)
        elevation = 8f
    }
}
```

### Animation Integration

```kotlin
// Animate survey appearance
private fun showSurveyWithAnimation() {
    DigiModule.show(
        // ... parameters
        onResult = { result ->
            when (result) {
                is Result.Success -> {
                    // Animate success
                    animateSuccess()
                }
                is Result.Error -> {
                    // Animate error
                    animateError()
                }
            }
        }
    )
}

private fun animateSuccess() {
    // Custom success animation
    val successView = findViewById<View>(R.id.success_indicator)
    successView.animate()
        .alpha(1f)
        .scaleX(1.2f)
        .scaleY(1.2f)
        .setDuration(300)
        .start()
}
```

## 📱 Layout Customization

### Configuration UI Layout

```xml
<!-- activity_main.xml - Key layout structure -->
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="16dp">

    <androidx.constraintlayout.widget.ConstraintLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content">

        <!-- Header -->
        <TextView
            android:id="@+id/header_title"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:text="Digi SDK Demo"
            android:textSize="24sp"
            android:textStyle="bold"
            android:gravity="center"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintEnd_toEndOf="parent" />

        <!-- Configuration Card -->
        <com.google.android.material.card.MaterialCardView
            android:id="@+id/config_card"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            app:cardCornerRadius="8dp"
            app:cardElevation="4dp"
            app:layout_constraintTop_toBottomOf="@id/header_title"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintEnd_toEndOf="parent">

            <!-- Configuration content -->
        </com.google.android.material.card.MaterialCardView>

    </androidx.constraintlayout.widget.ConstraintLayout>
</ScrollView>
```

### Input Field Styling

```xml
<!-- TextInputLayout with custom styling -->
<com.google.android.material.textfield.TextInputLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginBottom="16dp"
    android:hint="API URL"
    app:startIconDrawable="@android:drawable/ic_menu_edit"
    style="@style/Widget.Material3.TextInputLayout.OutlinedBox">

    <com.google.android.material.textfield.TextInputEditText
        android:id="@+id/api_url_input"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:inputType="textUri"
        android:text="https://genie-survey.sandsiv.com/digi_runner.js" />

</com.google.android.material.textfield.TextInputLayout>
```

## 🎯 Size Selector Implementation

### Icon Creation

Create icons for different survey sizes:

```xml
<!-- ic_survey_middle_third.xml -->
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="48dp"
    android:height="48dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <!-- Outline of the screen -->
    <path
        android:fillColor="#FFFFFF"
        android:strokeColor="#000000"
        android:strokeWidth="1"
        android:pathData="M3,3h18v18H3z" />
    <!-- Middle third filled -->
    <path
        android:fillColor="#4285F4"
        android:pathData="M3,8h18v8H3z" />
</vector>
```

### Selection State Icons

```xml
<!-- ic_survey_middle_third_selected.xml -->
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="48dp"
    android:height="48dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <!-- Outline of the screen - selected state -->
    <path
        android:fillColor="#FFFFFF"
        android:strokeColor="#000000"
        android:strokeWidth="2"
        android:pathData="M3,3h18v18H3z" />
    <!-- Middle third filled - selected state -->
    <path
        android:fillColor="#FF6200EE"
        android:pathData="M3,8h18v8H3z" />
</vector>
```

### Selector Implementation

```xml
<!-- selector_survey_middle_third.xml -->
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:state_checked="true" 
          android:drawable="@drawable/ic_survey_middle_third_selected" />
    <item android:drawable="@drawable/ic_survey_middle_third" />
</selector>
```

## 🔧 Interactive Elements

### Button Styling

```xml
<!-- Launch button with custom styling -->
<com.google.android.material.button.MaterialButton
    android:id="@+id/launch_survey_button"
    android:layout_width="0dp"
    android:layout_height="wrap_content"
    android:text="🚀 SHOW SURVEY"
    android:textSize="16sp"
    android:textStyle="bold"
    app:cornerRadius="8dp"
    app:backgroundTint="@color/survey_primary"
    app:layout_constraintTop_toBottomOf="@id/config_card"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintEnd_toEndOf="parent" />
```

### Status Display

```xml
<!-- Status text with custom styling -->
<TextView
    android:id="@+id/status_text"
    android:layout_width="0dp"
    android:layout_height="wrap_content"
    android:text="Ready to launch survey"
    android:textAlignment="center"
    android:textColor="@android:color/darker_gray"
    android:textSize="14sp"
    app:layout_constraintTop_toBottomOf="@id/launch_survey_button"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintEnd_toEndOf="parent" />
```

## 📱 Responsive Design

### Screen Size Adaptation

```kotlin
private fun adaptToScreenSize() {
    val displayMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(displayMetrics)
    val screenHeightDp = displayMetrics.heightPixels / displayMetrics.density
    val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
    
    when {
        screenHeightDp > 800 -> {
            // Large screens - adjust layout
            adjustLayoutForLargeScreen()
        }
        screenHeightDp > 600 -> {
            // Medium screens - standard layout
            adjustLayoutForMediumScreen()
        }
        else -> {
            // Small screens - compact layout
            adjustLayoutForSmallScreen()
        }
    }
}
```

### Orientation Handling

```kotlin
override fun onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig)
    
    // Recalculate margins for new orientation
    val newMargins = calculateMargins()
    
    // Update UI if needed
    updateUIForOrientation(newConfig.orientation)
}
```

## 🎨 Custom Themes

### Dark Mode Support

```xml
<!-- styles.xml -->
<style name="SurveyTheme" parent="Theme.Material3.DayNight">
    <item name="colorPrimary">@color/survey_primary</item>
    <item name="colorOnPrimary">@color/survey_on_primary</item>
</style>

<!-- colors.xml -->
<resources>
    <color name="survey_primary">#4285F4</color>
    <color name="survey_on_primary">#FFFFFF</color>
</resources>

<!-- colors.xml (night) -->
<resources>
    <color name="survey_primary">#5C9AFF</color>
    <color name="survey_on_primary">#000000</color>
</resources>
```

### Custom Color Schemes

```xml
<!-- Custom color scheme -->
<resources>
    <!-- Primary colors -->
    <color name="survey_primary">#4285F4</color>
    <color name="survey_primary_selected">#FF6200EE</color>
    
    <!-- Surface colors -->
    <color name="survey_surface">#FFFFFF</color>
    <color name="survey_on_surface">#000000</color>
    
    <!-- Status colors -->
    <color name="survey_success">#4CAF50</color>
    <color name="survey_error">#F44336</color>
    <color name="survey_warning">#FF9800</color>
</resources>
```

## 🔧 Accessibility

### Accessibility Labels

```xml
<!-- Add accessibility labels -->
<RadioButton
    android:id="@+id/size_full_screen"
    android:contentDescription="Full screen survey size"
    android:accessibilityTraits="button"
    ... />

<RadioButton
    android:id="@+id/size_middle_third"
    android:contentDescription="Middle third survey size"
    android:accessibilityTraits="button"
    ... />
```

### Screen Reader Support

```kotlin
// Add screen reader support
private fun setupAccessibility() {
    val fullScreenButton = findViewById<RadioButton>(R.id.size_full_screen)
    fullScreenButton.contentDescription = "Full screen survey size"
    
    val middleThirdButton = findViewById<RadioButton>(R.id.size_middle_third)
    middleThirdButton.contentDescription = "Middle third survey size"
    
    val bottomThirdButton = findViewById<RadioButton>(R.id.size_bottom_third)
    bottomThirdButton.contentDescription = "Bottom third survey size"
}
```

## 📋 UI Customization Checklist

### Visual Elements
- [ ] Survey positioning configured
- [ ] Corner radius set appropriately
- [ ] Icons created for size selectors
- [ ] Selection states implemented
- [ ] Colors and themes applied

### Layout
- [ ] Responsive design implemented
- [ ] Orientation handling added
- [ ] Screen size adaptation configured
- [ ] Accessibility labels added

### Interaction
- [ ] Touch feedback implemented
- [ ] Selection states working
- [ ] Animations added (optional)
- [ ] Error states handled

## 🚨 Common UI Issues

### Issue: Icons not showing
**Solution**: Check drawable resources and selector states

### Issue: Selection not visible
**Solution**: Verify selector drawable configuration

### Issue: Wrong positioning
**Solution**: Check margin calculations and screen density

### Issue: Layout issues on different screens
**Solution**: Implement responsive design patterns

## 📖 Next Steps

- Review [Code Examples](code-examples.md) for implementation patterns
- Check [Best Practices](best-practices.md) for production recommendations
- Explore [Troubleshooting Guide](troubleshooting.md) for common issues
- Read [API Reference](api-reference.md) for detailed method documentation

---

*This UI customization guide covers all aspects of visual customization. For specific implementation details, refer to the code examples and integration guide.*

