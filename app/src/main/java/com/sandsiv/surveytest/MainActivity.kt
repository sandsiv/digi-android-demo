package com.sandsiv.surveytest

import android.animation.ObjectAnimator
import android.content.SharedPreferences
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.sandsiv.digi.DigiModule
import com.sandsiv.digi.Margins
import java.util.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    
    private lateinit var apiUrlInput: TextInputEditText
    private lateinit var surveyIdInput: TextInputEditText
    private lateinit var languageInput: TextInputEditText
    private lateinit var launchButton: MaterialButton
    private lateinit var sizeRadioGroup: android.widget.RadioGroup
    private lateinit var sharedPreferences: SharedPreferences
    
    // Advanced settings views
    private lateinit var advancedHeader: LinearLayout
    private lateinit var advancedContent: LinearLayout
    private lateinit var advancedArrow: ImageView
    private lateinit var customerIdInput: TextInputEditText
    private lateinit var randomCustomerIdButton: MaterialButton
    private lateinit var metadataContainer: LinearLayout
    private lateinit var addMetadataButton: MaterialButton
    
    // Advanced settings state
    private var isAdvancedExpanded = false
    private val metadataRows = mutableListOf<View>()
    
    companion object {
        private const val PREFS_NAME = "survey_settings"
        private const val KEY_API_URL = "api_url"
        private const val KEY_SURVEY_ID = "survey_id"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_SIZE_SELECTION = "size_selection"
        
        // Advanced settings keys
        private const val KEY_CUSTOMER_ID = "customer_id"
        private const val KEY_METADATA_COUNT = "metadata_count"
        private const val KEY_METADATA_NAME = "metadata_name_"
        private const val KEY_METADATA_VALUE = "metadata_value_"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        
        initializeViews()
        loadSavedSettings()
        loadAdvancedSettings()
        setupClickListeners()
    }
    
    private fun initializeViews() {
        apiUrlInput = findViewById(R.id.api_url_input)
        surveyIdInput = findViewById(R.id.survey_id_input)
        languageInput = findViewById(R.id.language_input)
        launchButton = findViewById(R.id.launch_survey_button)
        sizeRadioGroup = findViewById(R.id.size_radio_group)
        
        // Advanced settings views
        advancedHeader = findViewById(R.id.advanced_header)
        advancedContent = findViewById(R.id.advanced_content)
        advancedArrow = findViewById(R.id.advanced_arrow)
        customerIdInput = findViewById(R.id.customer_id_input)
        randomCustomerIdButton = findViewById(R.id.random_customer_id_button)
        metadataContainer = findViewById(R.id.metadata_container)
        addMetadataButton = findViewById(R.id.add_metadata_button)
    }
    
    private fun setupClickListeners() {
        launchButton.setOnClickListener { 
            launchSurvey() 
        }
        
        // Advanced settings click listeners
        advancedHeader.setOnClickListener {
            toggleAdvancedSettings()
        }
        
        randomCustomerIdButton.setOnClickListener {
            generateRandomCustomerId()
        }
        
        addMetadataButton.setOnClickListener {
            addMetadataRow()
        }
    }
    
    private fun loadSavedSettings() {
        // Load saved API URL
        val savedApiUrl = sharedPreferences.getString(KEY_API_URL, "https://genie-survey.sandsiv.com/digi_runner.js")
        apiUrlInput.setText(savedApiUrl)
        
        // Load saved Survey ID
        val savedSurveyId = sharedPreferences.getString(KEY_SURVEY_ID, "162")
        surveyIdInput.setText(savedSurveyId)
        
        // Load saved Language
        val savedLanguage = sharedPreferences.getString(KEY_LANGUAGE, "en")
        languageInput.setText(savedLanguage)
        
        // Load saved size selection
        val savedSizeSelection = sharedPreferences.getInt(KEY_SIZE_SELECTION, R.id.size_full_screen)
        sizeRadioGroup.check(savedSizeSelection)
        
        android.util.Log.d("SurveyTest", "Loaded saved settings: API=$savedApiUrl, Survey=$savedSurveyId, Lang=$savedLanguage, Size=$savedSizeSelection")
    }
    
    private fun saveSettings() {
        val editor = sharedPreferences.edit()
        
        // Save current values
        editor.putString(KEY_API_URL, apiUrlInput.text.toString().trim())
        editor.putString(KEY_SURVEY_ID, surveyIdInput.text.toString().trim())
        editor.putString(KEY_LANGUAGE, languageInput.text.toString().trim())
        editor.putInt(KEY_SIZE_SELECTION, sizeRadioGroup.checkedRadioButtonId)
        
        editor.apply()
        
        android.util.Log.d("SurveyTest", "Settings saved successfully")
    }

    private fun launchSurvey() {
        // Validate inputs
        val apiUrl = apiUrlInput.text.toString().trim()
        val surveyIdText = surveyIdInput.text.toString().trim()
        val language = languageInput.text.toString().trim()
        
        if (apiUrl.isEmpty()) {
            showError("Please enter API URL")
            return
        }
        
        if (surveyIdText.isEmpty()) {
            showError("Please enter Survey ID")
            return
        }
        
        val surveyId = try {
            surveyIdText.toInt()
        } catch (e: NumberFormatException) {
            showError("Survey ID must be a number")
            return
        }
        
        if (language.isEmpty()) {
            showError("Please enter Language")
            return
        }
        
        // Save settings before launching survey
        saveSettings()
        saveAdvancedSettings()
        
        // Update status
        updateStatus("Initializing survey...")
        
        // Calculate margins based on size selection
        val margins = calculateMargins()
        android.util.Log.d("SurveyTest", "Calculated margins: $margins")
        
        // Get advanced settings
        val advancedParams = getAdvancedSettings()
        android.util.Log.d("SurveyTest", "Advanced params: $advancedParams")
        
        // Initialize DigiModule with user-provided URL
        DigiModule.init(
            url = apiUrl,
            context = this,
        )
        
        // Add 1-second delay between init and show to let survey fully activate
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            updateStatus("Launching survey...")
            DigiModule.show(
                surveyId = surveyId,
                language = language,
                params = advancedParams,
                context = this,
                margins = margins,
                cornerRadius = 16,
                onResult = { result -> 
                    android.util.Log.d("SurveyTest", "Survey result: $result")
                    updateStatus("Survey completed: $result")
                    showToast("Survey completed: $result")
                }
            )
        }, 1000)
    }
    
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        updateStatus("Error: $message")
    }
    
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
    
    private fun updateStatus(message: String) {
        findViewById<android.widget.TextView>(R.id.status_text).text = message
    }
    
    private fun calculateMargins(): Margins {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenHeightPx = displayMetrics.heightPixels
        val screenWidthPx = displayMetrics.widthPixels
        val density = displayMetrics.density
        
        // Convert screen height from pixels to DP
        val screenHeightDp = screenHeightPx / density
        
        android.util.Log.d("SurveyTest", "Screen dimensions: ${screenWidthPx}x${screenHeightPx}px (${screenHeightDp}dp)")
        android.util.Log.d("SurveyTest", "Screen density: $density")
        
        val selectedRadioButtonId = sizeRadioGroup.checkedRadioButtonId
        
        android.util.Log.d("SurveyTest", "Selected radio button ID: $selectedRadioButtonId")
        
        val margins = when (selectedRadioButtonId) {
            R.id.size_full_screen -> {
                // No margins - full screen
                android.util.Log.d("SurveyTest", "Using full screen margins")
                Margins(0, 0, 0, 0)  // top, bottom, start, end
            }
            R.id.size_middle_third -> {
                // Center the survey in the middle third of the screen
                // Equal top and bottom margins to center the survey
                val marginDp = (screenHeightDp * 0.25).toInt()
                android.util.Log.d("SurveyTest", "Middle 50% margins: top=${marginDp}dp, bottom=${marginDp}dp")
                Margins(marginDp, marginDp, 0, 0)  // top, bottom, start, end
            }
            R.id.size_bottom_third -> {
                // Position survey in bottom third
                // Large top margin to push survey to bottom, no bottom margin
                val topMarginDp = (screenHeightDp * 0.5).toInt()
                android.util.Log.d("SurveyTest", "Bottom 50% margins: top=${topMarginDp}dp")
                Margins(topMarginDp, 0, 0, 0)  // top, bottom, start, end
            }
            else -> {
                // Default to full screen
                android.util.Log.d("SurveyTest", "Default to full screen margins")
                Margins(0, 0, 0, 0)  // top, bottom, start, end
            }
        }
        
        android.util.Log.d("SurveyTest", "Final margins: $margins")
        return margins
    }
    
    // ===== ADVANCED SETTINGS FUNCTIONALITY =====
    
    private fun toggleAdvancedSettings() {
        isAdvancedExpanded = !isAdvancedExpanded
        
        if (isAdvancedExpanded) {
            advancedContent.visibility = View.VISIBLE
            ObjectAnimator.ofFloat(advancedArrow, "rotation", 0f, 180f).setDuration(200).start()
        } else {
            advancedContent.visibility = View.GONE
            ObjectAnimator.ofFloat(advancedArrow, "rotation", 180f, 0f).setDuration(200).start()
        }
    }
    
    private fun generateRandomCustomerId() {
        val randomString = generateRandomString(12)
        val customerId = "sdk_demo_$randomString"
        customerIdInput.setText(customerId)
        android.util.Log.d("SurveyTest", "Generated random customer ID: $customerId")
    }
    
    private fun generateRandomString(length: Int): String {
        val chars = "abcdefghijklmnopqrstuvwxyz0123456789"
        return (1..length)
            .map { chars[Random.nextInt(chars.length)] }
            .joinToString("")
    }
    
    private fun addMetadataRow() {
        if (metadataRows.size >= 5) {
            showError("Maximum 5 metadata pairs allowed")
            return
        }
        
        val metadataRow = createMetadataRow()
        metadataContainer.addView(metadataRow)
        metadataRows.add(metadataRow)
        
        android.util.Log.d("SurveyTest", "Added metadata row. Total: ${metadataRows.size}")
    }
    
    private fun createMetadataRow(): View {
        val rowLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setPadding(0, 8, 0, 8)
        }
        
        // Name field
        val nameLayout = TextInputLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            hint = "Name"
            setPadding(0, 0, 2, 0)
        }
        
        val nameInput = TextInputEditText(this).apply {
            id = View.generateViewId()
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            inputType = android.text.InputType.TYPE_CLASS_TEXT
            hint = "Name"
        }
        
        nameLayout.addView(nameInput)
        
        // Disable floating label behavior AFTER adding the EditText
        nameLayout.isHintEnabled = false
        nameLayout.boxStrokeWidth = 0
        nameLayout.boxStrokeWidthFocused = 0
        
        // Value field
        val valueLayout = TextInputLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            hint = "Value"
            setPadding(0, 0, 2, 0)
        }
        
        val valueInput = TextInputEditText(this).apply {
            id = View.generateViewId()
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            inputType = android.text.InputType.TYPE_CLASS_TEXT
            hint = "Value"
        }
        
        valueLayout.addView(valueInput)
        
        // Disable floating label behavior AFTER adding the EditText
        valueLayout.isHintEnabled = false
        valueLayout.boxStrokeWidth = 0
        valueLayout.boxStrokeWidthFocused = 0
        
        // Remove button - MaterialButton with proper sizing and positioning
        val removeButton = MaterialButton(this).apply {
            layoutParams = LinearLayout.LayoutParams(64, 64).apply {
                gravity = android.view.Gravity.CENTER_VERTICAL
                topMargin = 8  // Move down to center with input fields
            }
            backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#F44336"))
            cornerRadius = 32  // Half of width/height for perfect circle
            setIconResource(android.R.drawable.ic_delete)
            iconSize = 28
            iconTint = android.content.res.ColorStateList.valueOf(android.graphics.Color.WHITE)
            setOnClickListener {
                removeMetadataRow(rowLayout)
            }
        }
        
        rowLayout.addView(nameLayout)
        rowLayout.addView(valueLayout)
        rowLayout.addView(removeButton)
        
        return rowLayout
    }
    
    private fun removeMetadataRow(rowView: View) {
        metadataContainer.removeView(rowView)
        metadataRows.remove(rowView)
        android.util.Log.d("SurveyTest", "Removed metadata row. Total: ${metadataRows.size}")
    }
    
    private fun getAdvancedSettings(): HashMap<String, Any> {
        val params = HashMap<String, Any>()
        
        // Add customer ID if provided
        val customerId = customerIdInput.text.toString().trim()
        if (customerId.isNotEmpty()) {
            params["customerId"] = customerId
        }
        
        // Add metadata
        for (rowView in metadataRows) {
            val nameLayout = (rowView as LinearLayout).getChildAt(0) as TextInputLayout
            val valueLayout = rowView.getChildAt(1) as TextInputLayout
            
            val nameInput = (nameLayout.getChildAt(0) as android.widget.FrameLayout).getChildAt(0) as TextInputEditText
            val valueInput = (valueLayout.getChildAt(0) as android.widget.FrameLayout).getChildAt(0) as TextInputEditText
            
            val name = nameInput.text.toString().trim()
            val value = valueInput.text.toString().trim()
            
            if (name.isNotEmpty() && value.isNotEmpty()) {
                // Validate name (no spaces, no special characters)
                if (name.matches(Regex("[a-zA-Z0-9_]+"))) {
                    params[name] = value
                } else {
                    showError("Metadata name '$name' contains invalid characters. Use only letters, numbers, and underscores.")
                    return HashMap()
                }
            }
        }
        
        return params
    }
    
    private fun saveAdvancedSettings() {
        val editor = sharedPreferences.edit()
        
        // Save customer ID
        val customerId = customerIdInput.text.toString().trim()
        editor.putString(KEY_CUSTOMER_ID, customerId)
        
        // Save metadata
        editor.putInt(KEY_METADATA_COUNT, metadataRows.size)
        
        for (i in metadataRows.indices) {
            val rowView = metadataRows[i]
            val nameLayout = (rowView as LinearLayout).getChildAt(0) as TextInputLayout
            val valueLayout = rowView.getChildAt(1) as TextInputLayout
            
            val name = (nameLayout.getChildAt(0) as android.widget.FrameLayout).getChildAt(0) as TextInputEditText
            val value = (valueLayout.getChildAt(0) as android.widget.FrameLayout).getChildAt(0) as TextInputEditText
            
            val nameText = name.text.toString().trim()
            val valueText = value.text.toString().trim()
            
            editor.putString("${KEY_METADATA_NAME}$i", nameText)
            editor.putString("${KEY_METADATA_VALUE}$i", valueText)
        }
        
        editor.apply()
        android.util.Log.d("SurveyTest", "Advanced settings saved")
    }
    
    private fun loadAdvancedSettings() {
        // Load customer ID
        val savedCustomerId = sharedPreferences.getString(KEY_CUSTOMER_ID, "")
        customerIdInput.setText(savedCustomerId)
        
        // Load metadata
        val metadataCount = sharedPreferences.getInt(KEY_METADATA_COUNT, 0)
        
        if (metadataCount > 0) {
            // Load saved metadata rows
            for (i in 0 until metadataCount) {
                val name = sharedPreferences.getString("${KEY_METADATA_NAME}$i", "")
                val value = sharedPreferences.getString("${KEY_METADATA_VALUE}$i", "")
                
                if (name?.isNotEmpty() == true) {
                    addMetadataRow()
                    val rowView = metadataRows.last()
                    val nameLayout = (rowView as LinearLayout).getChildAt(0) as TextInputLayout
                    val valueLayout = rowView.getChildAt(1) as TextInputLayout
                    
                    val nameInput = (nameLayout.getChildAt(0) as android.widget.FrameLayout).getChildAt(0) as TextInputEditText
                    val valueInput = (valueLayout.getChildAt(0) as android.widget.FrameLayout).getChildAt(0) as TextInputEditText
                    
                    nameInput.setText(name)
                    valueInput.setText(value)
                }
            }
        } else {
            // Start with one empty metadata row by default
            addMetadataRow()
        }
        
        // Show advanced indicator if there are settings
        if (savedCustomerId?.isNotEmpty() == true || metadataCount > 0) {
            showAdvancedIndicator()
        }
        
        android.util.Log.d("SurveyTest", "Advanced settings loaded: customerId=$savedCustomerId, metadataCount=$metadataCount")
    }
    
    private fun showAdvancedIndicator() {
        // You can add visual indicator here if needed
        android.util.Log.d("SurveyTest", "Advanced settings indicator shown")
    }
}
