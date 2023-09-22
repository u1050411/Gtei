package com.trueta.gtei

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.annotation.StringRes
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.max
import kotlin.reflect.full.memberProperties

class ScreensViewModel : ViewModel() {
    // Initialize your Variables and Screens here
    var screensGtei = Screens().copy()

    private val _selectedScreen = MutableStateFlow<Screen?>(screensGtei.start.copy())
    internal val selectedScreen = _selectedScreen.asStateFlow()

    private val _nextScreen = MutableStateFlow<String>("Try")
    internal val nextScreen = _nextScreen.asStateFlow()

    private val _message = MutableStateFlow(screensGtei.start.message)
    val message = _message.asStateFlow()


    // Immutable Map switches
    private var switches: Map<String, MutableStateFlow<Boolean>> = mapOf()

    // Expose an immutable map to the outside
    val switchesPublic: Map<String, StateFlow<Boolean>>
        get() = switches.mapValues { it.value.asStateFlow() }

    // This flow stores a pair of a list of integers and a Medication object.
    // It's initialized as null
    private var pairMedicationTry: Pair<List<Int>, Medication?> = Pair(emptyList(), null)
    internal val medication: Medication? get() = pairMedicationTry?.second
    private var _resultPair: List<Pair<String, String>> = emptyList()
    val resultPair: List<Pair<String, String>> get() = _resultPair

    /**
     * Update the selected screen, next screen, and message.
     *
     * @param screen The Screen object to be used for updating.
     */
    fun updateSelectedScreen(screen: Screen) {
        _selectedScreen.value = screen
        _nextScreen.value = determineNextScreen(screen)
        _message.value = retrieveMessage(screen)
    }

    /**
     * Add imageResId to the listScreens of the given screen if it's not already present.
     *
     * @param screen The Screen object to be used for updating.
     */
    fun onScreenSelected(screen: Screen) {
        screen?.listScreens?.forEach { it ->
            if (it.imageResId == 0) {
                it.imageResId = screen.imageResId
            }
        }
      updateSelectedScreen(screen)
    }

    /**
     * Retrieve the appropriate message based on the size of resultPair and the given screen.
     *
     * @param screen The Screen object that may contain a default message.
     * @return The message to be displayed.
     */
    private fun retrieveMessage(screen: Screen?): String {
        return when (resultPair.size) {
            1 -> {
                // If there is only one item in resultPair, use plural form 'Medicaments'
                "El Medicament es : "
            }
            in 2..Int.MAX_VALUE -> {
                // If there is more than one item in resultPair, use singular form 'Medicament'
                "Els "+resultPair.size+" Medicaments son : "
            }
            else -> {
                // If none of the above conditions are met, use the message from the screen object
                // or an empty string if the screen object is null
                screen?.message ?: ""
            }
        }
    }


    /**
     * Determines the next screen to navigate to.
     * @param screen The current screen containing various lists.
     * @return The name of the next screen.
     */
    fun determineNextScreen(screen: Screen): String {
        // Check for conditions
        val isListScreensEmpty = screen.listScreens.isEmpty()
        val isListVarEmpty = screen.listVar.isEmpty()
        val hasListIntInPair = pairMedicationTry?.first?.isNotEmpty() == true
        val needSlider = pairMedicationTry?.second?.run { fg && weight && sex } ?: false

        return when {
            (resultPair.isNotEmpty()) -> "Resultat"
            (hasListIntInPair && needSlider) -> "Slider"
            isListScreensEmpty && isListVarEmpty -> "OnSubmit"
            isCheckBoxScreen(screen) -> "CheckBox"
            else -> "Try"
        }
    }

    /**
     * Determines if a given screen should be treated as a "Checkbox Screen."
     *
     * A "Checkbox Screen" is defined as a screen that meets one of the following criteria:
     * 1. It has no sub-screens and contains at least one variable of type VarBool.
     * 2. It has no sub-screens, contains variables of types other than VarBool, and includes a variable named "alergiaPenicilina".
     *
     * @param screen The screen object to be checked.
     * @return Boolean value indicating if the screen is a "Checkbox Screen."
     */
    fun isCheckBoxScreen(screen: Screen): Boolean {
        // Check if the screen has no sub-screens and at least one variable
        if (screen.listScreens.isEmpty() && screen.listVar.isNotEmpty()) {
            // Check if there's any variable of type VarString with the name "alergiaPenicilina"
            val haveApendicitis = screen.listVar.any { it.name == Variables().alergiaPenicilina.name }

            // If all variables are of a type different from VarBool and there's a variable named "alergiaPenicilina"
            if (screen.listVar.all { it !is VarBool } && haveApendicitis) {
                return true
            }

            // If there's at least one variable of type VarBool
            if (screen.listVar.any { it is VarBool }) {
                return true
            }
        }

        return false
    }


    // Check if a checkbox is checked
    fun isCheckboxChecked(nameVariable: String): StateFlow<Boolean>? =
        switches[nameVariable]?.asStateFlow()

    // Toggle the state of a checkbox
    fun toggleCheckboxState(variableName: String) {
        // Clone existing map, modify clone, then replace original map
        val newSwitches = switches.toMutableMap()
        newSwitches[variableName]?.value = !(newSwitches[variableName]?.value ?: false)
        switches = newSwitches
    }

    fun Variables.getAllVarBools(): List<VarBool> {
        return this::class.memberProperties.filter { it.returnType.classifier == VarBool::class }
            .mapNotNull { it as? VarBool }
    }

    /**
     * Initialize the switches map based on the given screen and its associated variables.
     *
     * Special handling is done for the variable 'alergiaTrySevera' based on the presence
     * of 'tryAlergiaPenicilina' in the list of variables for the screen.
     *
     * @param screen The Screen object containing the list of variables to be used.
     */
// Initialize the variables only once to avoid creating multiple instances
    private var tryAlergiaPenicilina = Variables().alergiaPenicilina.copy()
    private var alergiaTrySevera = Variables().alergiaSevera.copy()

    fun initializeSwitches(screen: Screen?) {
        // Mutable map to store the new switches
        val newSwitches = mutableMapOf<String, MutableStateFlow<Boolean>>()

        // Special handling for alergiaTrySevera
        if (screen?.listVar?.any { it.name == tryAlergiaPenicilina.name } == true) {
            newSwitches[alergiaTrySevera.name] = MutableStateFlow(false)
        }

        // Initialize switches for other variables
        screen?.listVar?.forEach { variable ->
            if (variable is VarBool || variable.name == tryAlergiaPenicilina.name) {
                newSwitches[variable.name] = MutableStateFlow(false)
            }
        }

        // Replace the existing switches with the new ones
        switches = newSwitches
    }


    // Checkbox

    /**
    logic for drugs depends buttons and checkboxes
     * @param currentScreen The current screen containing various lists.
     * @return The name of the next screen.
     * */
    fun onSubmit(currentScreen: Screen) {
        val controllerLogic = ControllerLogic()
        currentScreen.listVar.filter { variable ->
            switches[variable.name]?.value == true && variable.name != Variables().alergiaSevera.name
        }.toMutableList()
        updateVarStringValues(currentScreen)
        pairMedicationTry = controllerLogic.processTryScreen(currentScreen)
        val needSlider = pairMedicationTry?.second?.run { fg && weight && sex } ?: false
        if (needSlider) {
            updateSelectedScreen(currentScreen)
        } else {
            onSubmitSlice(currentScreen)
               }
    }


    /**
     * Helper function to check if a checkbox for a given variable name is checked.
     *
     * @param variableName The name of the variable whose checkbox status we want to know.
     * @return Boolean indicating whether the checkbox is checked or not.
     */
    private fun isCheckboxCheckedFor(variableName: String): Boolean {
        return isCheckboxChecked(variableName)?.value ?: false
    }

    /**
     * Updates the 'valorString' field of the variable with name 'alergiaPenicilina' in the given list.
     *
     * @param listVar The list of variables to update.
     */
    private fun updateAlergiaPenicilina(listVar: MutableList<Variable>) {
        // Get the name of the 'alergiaPenicilina' variable
        val tryAlergiaPenicilina = Variables().alergiaPenicilina.name

        // Check if the checkboxes for 'alergiaPenicilina' and 'alergiaSevera' are checked
        val isCheckedPenicilina = isCheckboxCheckedFor(tryAlergiaPenicilina)
        val isCheckedSevera = isCheckboxCheckedFor(Variables().alergiaSevera.name)

        // Find the variable in the list and update its 'valorString'
        listVar.find { it.name == tryAlergiaPenicilina }?.let { variable ->
            (variable as? VarString)?.valorString = when {
                isCheckedPenicilina && isCheckedSevera -> "Severa"
                isCheckedPenicilina -> "Sí"
                else -> "No"
            }
        }
    }

    /**
     * Updates the 'valorString' field of variables in the given Screen object.
     *
     * @param screenList The Screen object containing a list of variables to update.
     */
    private fun updateVarStringValues(screenList: Screen) {
        // Update the value of AlergiaPenicilina
        updateAlergiaPenicilina(screenList.listVar)

        // Update other VarString values if needed
        updateVarStringValue(screenList.listVar)
    }


    /**
     * Updates the value of a VarString variable in the listVar list.
     * @param listVar The list of variables to update.
     * @param varName The name of the variable to update.
     * @param value The new value of the variable.
     */

    private fun updateVarStringValue(listVar: MutableList<Variable>) {
        listVar.forEach() { variable ->
            // if variable is VarBool, valor = true
            (variable as? VarBool)?.valor = true
        }
    }

    // Slider

    private var fgVar = mutableStateOf(0.0)
    private var weightVar = mutableStateOf(0.0)
    private var heightVar = mutableStateOf(0.0)
    var sexVar = mutableStateOf(Gender.Men)

    fun initializeRangeSlice(sliders: Medication, isMen: Boolean, haveFg: Boolean): List<RangeSlice> {
        val tempList = mutableListOf<RangeSlice>()  // Lista mutable temporal
        if (sliders.fg) {
            var initialFg = if (isMen) 90f else 95f
            initialFg = if (haveFg) 20f else initialFg
            tempList.add(createRangeSlice(R.string.fg, 0..150, initialFg, R.string.ml_per_min) { newValue -> updateFg(newValue) })
        }
        if (sliders.weight) {
            val initialWeight = if (isMen) 85f else 65f
            tempList.add(createRangeSlice(R.string.weight, 0..300, initialWeight, R.string.kg) { newValue -> updateWeight(newValue) })
        }
        if (sliders.weight) {  // Corregí la condición aquí
            val initialHeight = if (isMen) 175f else 165f
            tempList.add(createRangeSlice(R.string.height, 0..240, initialHeight, R.string.cm) { newValue -> updateHeight(newValue) })
        }
        return tempList.toList()  // Convertir la lista mutable a inmutable
    }


    private fun createRangeSlice(@StringRes name: Int, range: IntRange, initialValue: Float, @StringRes unit: Int, callback: (Float) -> Unit): RangeSlice {
        return RangeSlice(name, range, initialValue, 0, unit, callback)
    }


    private fun updateFg(newValue: Float) {
        fgVar.value = newValue.toDouble()
    }

    private fun updateWeight(newValue: Float) {
        weightVar.value = newValue.toDouble()
    }

    private fun updateHeight(newValue: Float) {
        heightVar.value = newValue.toDouble()
    }

    fun updateGender(gender: Gender) {
        sexVar.value = gender
    }

    /**
     * Updates the value of variable in the listVar list.
     * @param listVar The list of variables to update.
     */

    fun onSubmitSlice(currentLogic: Screen) {
        // Create a ControllerLogic instance to handle logic processing
        val controllerLogic = ControllerLogic()

        // Safely get the fg, weight, and height values, providing default values if needed
        val fg = if (fgVar.value.toFloat() == 0.0f) 35.0 else fgVar.value.toDouble()
        val weight = if (weightVar.value.toFloat() == 0.0f) 85.0 else weightVar.value.toDouble()
        val height = if (heightVar.value.toFloat() == 0.0f) 175.0 else heightVar.value.toDouble()

        // Determine the gender based on the value of sexVar
        val sex = (sexVar.value == Gender.Men)

        // Prepare the SliderData object with the retrieved values
        val sliderData = SliderData(fg, weight, height, sex)

        // Safely retrieve the medication list, or process it through the current screen
        val listMedication = pairMedicationTry.first.takeIf { it.isNotEmpty() } ?: controllerLogic.processTryScreen(currentLogic).first


        // Process the slice screen and update the result pair
        _resultPair = controllerLogic.processSliceScreen(currentLogic, listMedication, sliderData) as List<Pair<String, String>>

        // Update the selected screen based on the current logic
        updateSelectedScreen(currentLogic)
    }


    // Method to determine the text size for medicine and dose
    /**
     * Get the screen size of the device in inches.
     *
     * @param context Application context.
     * @return Screen size in inches.
     */
    fun getScreenSizeInInches(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val widthInches = displayMetrics.widthPixels / displayMetrics.xdpi
        val heightInches = displayMetrics.heightPixels / displayMetrics.ydpi

        return Math.sqrt((widthInches * widthInches + heightInches * heightInches).toDouble()).toInt()
    }

    /**
     * Adapt text sizes based on device size.
     *
     * @param context Application context.
     * @param resultsPair List of text pairs.
     * @return A pair containing the adapted text sizes for the first and second elements.
     */
    fun sizeText(context: Context, resultsPair: List<Pair<String, String>>): Pair<Int, Int> {
        val screenSize = getScreenSizeInInches(context)

        // Finding maximum lengths for first and second elements in pairs
        var maxFirst = 0
        var maxSecond = 0
        for (pair in resultsPair) {
            max(maxFirst, pair.first.length).also { maxFirst = it }
            maxSecond = max(maxSecond, pair.second.length)
        }

        // Determine sizes based on maximum lengths
        val max = determineTextSize(maxFirst, screenSize)
        val min = determineTextSize(maxSecond, screenSize)

        // Adjust max and min if needed
        return if (max > min) {
            Pair(if (min == 20) 30 else 35, min)
        } else {
            Pair(max, if (max == 20) 30 else 35)
        }
    }

    /**
     * Determine text size based on text length and screen size.
     *
     * @param length Length of the text.
     * @param screenSize Size of the device screen in inches.
     * @return Adapted text size.
     */
    fun determineTextSize(length: Int, screenSize: Int): Int {
        val baseSize = when {
            length <= 10 -> 35
            length in 11..20 -> 30
            else -> 20
        }

        // Adapt the base size according to the screen size
        return when {
            screenSize <= 5 -> (baseSize * 0.8).toInt()
            screenSize in 6..7 -> baseSize
            else -> (baseSize * 1.2).toInt()
        }
    }

    /**
     * Calculate the height of an item in the list based on the text size and character count.
     *
     * @param sizeTextDrugsLogic Text size for the first element in the pair.
     * @param sizeTextDoseLogic Text size for the second element in the pair.
     * @param entry The pair of strings to be used for calculating the height.
     * @return The height of the item.
* */
    fun calculateItemHeight(sizeTextDrugsLogic: Int, sizeTextDoseLogic: Int, entry: Pair<String, String>): Float {
        val baseHeight = 10f  // Base height in pixels for empty text

        // Factors to convert text size to pixel height (these are hypothetical factors for demonstration)
        val textToPixelFactorDrugs = 1.2f
        val textToPixelFactorDose = 1.0f

        // Calculate the height based on the text size and character count for each string
        val firstTextHeight = entry.first.length * sizeTextDrugsLogic * textToPixelFactorDrugs
        val secondTextHeight = entry.second.length * sizeTextDoseLogic * textToPixelFactorDose

        // Calculate the total height
        val totalHeight = baseHeight + firstTextHeight + secondTextHeight

        return totalHeight
    }


    /**
     * Resets the state of the ViewModel.
     */
    fun resetState() {
        screensGtei = Screens().copy()
        pairMedicationTry = Pair(emptyList(), null)
        switches = mapOf()
        _resultPair = emptyList()
        updateSelectedScreen(screensGtei.start.copy())
        tryAlergiaPenicilina = Variables().alergiaPenicilina.copy()
        alergiaTrySevera = Variables().alergiaSevera.copy()
        fgVar = mutableStateOf(0.0)
        weightVar = mutableStateOf(0.0)
        heightVar = mutableStateOf(0.0)
        sexVar = mutableStateOf(Gender.Men)
    }
}

