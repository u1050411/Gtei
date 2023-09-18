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
    fun goScreen(screen: Screen): String {
        return when {
            screen.listScreens.isEmpty() -> {
                if (screen.listVar.isEmpty()) "Slider" else "CheckBox"
            }
            else -> "Try"
        }
    }
    // Immutable Map
    private var switches: Map<String, MutableStateFlow<Boolean>> = mapOf()

    // Expose an immutable map to the outside
    val switchesPublic: Map<String, StateFlow<Boolean>>
        get() = switches.mapValues { it.value.asStateFlow() }

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
//        val updatedListVar = currentScreen.listVar.filter { variable ->
//            _switches[variable.name]?.value == true && variable.name != alergiaTrySevera.name
//        }.toMutableList()
//
//        updateVarStringValues(updatedListVar)
//        repositoryTpita?.updateScreen(currentScreen)
//        pairMedicationTry.value = controllerLogic.processTryScreen(currentScreen)
//        pairMedicationTry.value?.let {
//            repositoryTpita?.updateTryResult(it)
//        }
//        if (pairMedicationTry.value?.second != null) {
//            repositoryTpita?.updateDesti("Slider")
//        } else {
//            repositoryTpita?.updateDesti("Result")
//        }
    }

    // --- Private Helper Methods ---

    private fun updateVarStringValues(listVar: MutableList<Variable>) {
//        val isCheckedPenicilina = isCheckboxChecked(tryAlergiaPenicilina.name).value
//        val isCheckedSevera = isCheckboxChecked(alergiaTrySevera.name).value
//
//        val value = when {
//            isCheckedPenicilina && isCheckedSevera -> "Severa"
//            isCheckedPenicilina -> "Sí"
//            else -> "No"
//        }
//
//        updateVarStringValue(listVar, tryAlergiaPenicilina.name, value)
    }

    private fun updateVarStringValue(listVar: MutableList<Variable>, varName: String, value: String) {
        listVar.find { it.name == varName }?.let { variable ->
            (variable as? VarString)?.valorString = value
        }
    }



}
