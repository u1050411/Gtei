package com.trueta.gtei

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ScreensViewModel : ViewModel() {
    // Initialize your Variables and Screens here
    val screensGtei = Screens()

    private val _selectedScreen = MutableStateFlow<Screen?>(screensGtei.respiratori1)
    val selectedScreen = _selectedScreen.asStateFlow()

    private val _message = MutableStateFlow(screensGtei.respiratori1.message)
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
                if (screen.listVar.isEmpty()) "slider" else "checkbox"
            }
            else -> "try"
        }
    }

    private var switches: Map<String, MutableStateFlow<Boolean>> = mutableMapOf()

    val switchesPublic: Map<String, StateFlow<Boolean>>
        get() = switches.mapValues { it.value.asStateFlow() }

    fun isCheckboxChecked(nameVariable: String): StateFlow<Boolean> ?=
        switches[nameVariable]?.asStateFlow()

    fun toggleCheckboxState(variableName: String) {
        switches[variableName]?.value = !(switches[variableName]?.value ?: false)
    }

    fun initializeSwitches(screen: Screen?) {
        val select = selectedScreen.value
        val tryAlergiaPenicilina = Variables().alergiaPenicilina
        val alergiaTrySevera = Variables().alergiaSevera

        select?.listVar?.firstOrNull { it.name == tryAlergiaPenicilina.name }?.let {
            switches[alergiaTrySevera.name]
        }

        select?.listVar?.forEach { variable ->
            if (variable is VarBool || variable.name == tryAlergiaPenicilina.name) {
                switches[variable.name]
            }
        }
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
