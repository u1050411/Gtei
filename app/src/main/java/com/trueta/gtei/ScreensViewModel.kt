package com.trueta.gtei

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.reflect.full.memberProperties

class ScreensViewModel : ViewModel() {
    // Initialize your Variables and Screens here
    val screensGtei = Screens()

    private val _selectedScreen = MutableStateFlow<Screen?>(screensGtei.start)
    val selectedScreen = _selectedScreen.asStateFlow()

    private val _message = MutableStateFlow(screensGtei.start.message)
    val message = _message.asStateFlow()

    // Immutable Map
    private var switches: Map<String, MutableStateFlow<Boolean>> = mapOf()

    // Expose an immutable map to the outside
    val switchesPublic: Map<String, StateFlow<Boolean>>
        get() = switches.mapValues { it.value.asStateFlow() }

    // This flow stores a pair of a list of integers and a Medication object.
    // It's initialized as null, but consider using a default value.
    private var pairMedicationTry: MutableStateFlow<Pair<List<Int>?, Medication?>?> = MutableStateFlow(null)
    val medication: Medication? get() = pairMedicationTry.value?.second

    fun onScreenSelected(screen: Screen) {
        screen?.listScreens?.forEach { it ->
            if (it.imageResId == 0) {
                it.imageResId = screen.imageResId
            }
        }
        _selectedScreen.value = screen
        _selectedScreen.value?.let {
            _message.value = retrieveMessage(it, "Try")
        }
    }
    private fun retrieveMessage(screen: Screen?, opcioMessage: String): String {
        return when (opcioMessage) {
            "Resultat1" -> "Els Medicaments son : "
            "Resultat2" -> "El Medicament es : "
            else -> screen?.message ?: ""
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
        val hasListIntInPair = pairMedicationTry.value?.first?.isNotEmpty() == true
        val hasMedicationInPair = pairMedicationTry.value?.second != null

        return when {
            isListScreensEmpty -> if (isListVarEmpty) "Slider" else "CheckBox"
            hasListIntInPair -> "Slider"
            hasMedicationInPair -> "Resultat"
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

    // Initialize switches based on screen selection and available variables
    fun initializeSwitches(screen: Screen?) {
        val select = selectedScreen.value ?: return
        val tryAlergiaPenicilina = Variables().alergiaPenicilina
        val alergiaTrySevera = Variables().alergiaSevera

        val newSwitches = mutableMapOf<String, MutableStateFlow<Boolean>>()

        // Special handling for alergiaTrySevera
        select.listVar.firstOrNull { it.name == tryAlergiaPenicilina.name }?.let {
            newSwitches[alergiaTrySevera.name] = MutableStateFlow(false)
        }

        // Initialize switches for other variables
        for (variable in select.listVar) {
            if (variable is VarBool || variable.name == tryAlergiaPenicilina.name) {
                newSwitches[variable.name] = MutableStateFlow(false)
            }
        }

        // Replace the existing switches with the new ones
        switches = newSwitches
    }



  // Checkbox




    // Safe unwrapping in onSubmit function
    fun onSubmit(currentScreen: Screen) {
        val controllerLogic = ControllerLogic()
        val currentLogic = currentScreen.copy()
        val updatedListVar = currentLogic.listVar.filter { variable ->
            switches[variable.name]?.value == true && variable.name != Variables().alergiaSevera.name
        }.toMutableList()
        updateVarStringValues(updatedListVar)
        pairMedicationTry.value = controllerLogic.processTryScreen(currentLogic)
        currentScreen.listVar = updatedListVar
    }

    // --- Private Helper Methods ---

    private fun updateVarStringValues(listVar: MutableList<Variable>) {
        val tryAlergiaPenicilina = Variables().alergiaPenicilina.name
        val isCheckedPenicilina = (isCheckboxChecked(tryAlergiaPenicilina)?.value) ?: false
        val isCheckedSevera = (isCheckboxChecked((Variables().alergiaSevera.name))?.value) ?: false

        val value = when {
            isCheckedPenicilina && isCheckedSevera -> "Severa"
            isCheckedPenicilina -> "Sí"
            else -> "No"
        }

        updateVarStringValue(listVar, tryAlergiaPenicilina, value)
    }

    private fun updateVarStringValue(listVar: MutableList<Variable>, varName: String, value: String) {
        listVar.find { it.name == varName }?.let { variable ->
            (variable as? VarString)?.valorString = value
        }
    }



}
