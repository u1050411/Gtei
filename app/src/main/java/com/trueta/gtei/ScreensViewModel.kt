package com.trueta.gtei

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.reflect.full.memberProperties

class ScreensViewModel : ViewModel() {
    // Initialize your Variables and Screens here
    val screensGtei = Screens().copy()

    private val _selectedScreen = MutableStateFlow<Screen?>(screensGtei.start.copy())
    internal val selectedScreen = _selectedScreen.asStateFlow()

    private val _nextScreen = MutableStateFlow<String>("Try")
    internal val nextScreen = _nextScreen.asStateFlow()
    fun updateSelectedScreen(screen: Screen) {
        _selectedScreen.value = screen
        _nextScreen.value = determineNextScreen(screen)
        _message.value = retrieveMessage(screen)
    }

    private val _message = MutableStateFlow(screensGtei.start.message)
    val message = _message.asStateFlow()


    // Immutable Map
    private var switches: Map<String, MutableStateFlow<Boolean>> = mapOf()

    // Expose an immutable map to the outside
    val switchesPublic: Map<String, StateFlow<Boolean>>
        get() = switches.mapValues { it.value.asStateFlow() }

    // This flow stores a pair of a list of integers and a Medication object.
    // It's initialized as null, but consider using a default value.
    private var pairMedicationTry: Pair<List<Int>, Medication?> = Pair(emptyList(), null)
    internal val medication: Medication? get() = pairMedicationTry?.second
    private var _resultPair: List<Pair<String, String>> = emptyList()
    val resultPair: List<Pair<String, String>> get() = _resultPair

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
                "Els Medicaments son : "
            }
            in 2..Int.MAX_VALUE -> {
                // If there is more than one item in resultPair, use singular form 'Medicament'
                "El Medicament es : "
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
        val hasListIntInPair = pairMedicationTry?.first?.isNotEmpty() == true
        val needSlider = pairMedicationTry?.second?.run { fg && weight && sex } ?: false

        return when {
            (resultPair.isNotEmpty()) -> "Resultat"
            (hasListIntInPair && needSlider) -> "Slider"
            isListScreensEmpty -> "CheckBox"
            else -> "Try"
        }
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
        val updatedListVar = currentScreen.listVar.filter { variable ->
            switches[variable.name]?.value == true && variable.name != Variables().alergiaSevera.name
        }.toMutableList()
        updateVarStringValues(updatedListVar)
        currentScreen.listVar = updatedListVar
        pairMedicationTry = controllerLogic.processTryScreen(currentScreen)
        val needSlider = pairMedicationTry?.second?.run { fg && weight && sex } ?: false
        if (needSlider) {
            currentScreen.listVar = updatedListVar
            updateSelectedScreen(currentScreen)
        } else {
            onSubmitSlice(currentScreen)
               }
    }

    /**
     * Updates the value of variable in the listVar list.
     * @param listVar The list of variables to update.
     */

    private fun updateVarStringValues(listVar: MutableList<Variable>) {
        val tryAlergiaPenicilina = Variables().alergiaPenicilina.name
        val isCheckedPenicilina = (isCheckboxChecked(tryAlergiaPenicilina)?.value) ?: false
        val isCheckedSevera = (isCheckboxChecked((Variables().alergiaSevera.name))?.value) ?: false

        listVar.find { it.name == tryAlergiaPenicilina }?.let { variable ->
            (variable as? VarString)?.valorString = when {
                isCheckedPenicilina && isCheckedSevera -> "Severa"
                isCheckedPenicilina -> "Sí"
                else -> "No"
            }
        }
        updateVarStringValue(listVar)
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
        val listMedication = pairMedicationTry.first
        val controllerLogic = ControllerLogic()
        val fg =  (if (fgVar.value ==0.0) 35 else fgVar.value).toDouble()
        val weight = (if (weightVar.value ==0.0) 85 else weightVar.value).toDouble()
        val height = (if (heightVar.value ==0.0) 175 else heightVar.value).toDouble()
        val sex = (sexVar.value == Gender.Men)

        val sliderData = SliderData(fg, weight, height, sex)
        _resultPair = controllerLogic.processSliceScreen(currentLogic, listMedication, sliderData) as List<Pair<String, String>>
        updateSelectedScreen(currentLogic)
    }


    // Result

    // Method to determine the text size based on the text length
    private fun determineTextSize(textLength: Int): Int {
        return when {
            textLength < 10 -> 40
            textLength < 20 -> 30
            else -> 20
        }
    }

    // Method to determine the text size for medicine and dose
    fun sizeText(resultsPair: List<Pair<String, String>>): Pair<Int, Int> {
        // Finding maximum lengths for first and second elements in pairs
        val maxFirst = resultsPair.map { it.first.length }.maxOrNull() ?: 0
        val maxSecond = resultsPair.map { it.second.length }.maxOrNull() ?: 0

        // Determine sizes based on maximum lengths
        val max = determineTextSize(maxFirst)
        val min = determineTextSize(maxSecond)

        // Adjust max and min if needed
        return if (max > min) {
            Pair(if (min == 20) 30 else 40, min)
        } else {
            Pair(max, if (max == 20) 30 else 40)
        }
    }

    /**
     * Resets the state of the ViewModel.
     */
    fun resetState() {
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

