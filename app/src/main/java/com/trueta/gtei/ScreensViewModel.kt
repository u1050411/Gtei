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
                if (screen.listVar.isEmpty()) "slider" else "checkbox"
            }
            else -> "try"
        }
    }
    fun initializeSwitches(): Map<String, StateFlow<Boolean>> {
        val select = selectedScreen.value
        val switches = mutableMapOf<String, MutableStateFlow<Boolean>>()
        val tryAlergiaPenicilina = Variables().alergiaPenicilina
        val alergiaTrySevera = Variables().alergiaSevera

        select?.listVar?.firstOrNull { it.name == tryAlergiaPenicilina.name }?.let {
            switches.getOrPut(alergiaTrySevera.name) { MutableStateFlow(false) }
        }

        select?.listVar?.forEach { variable ->
            if (variable is VarBool || variable.name == tryAlergiaPenicilina.name) {
                switches.getOrPut(variable.name) { MutableStateFlow(false) }
            }
        }

        return switches
    }



    private fun updateVarStringValue(listVar: MutableList<Variable>, varName: String, value: String) {
        listVar.find { it.name == varName }?.let { variable ->
            (variable as? VarString)?.valorString = value
        }
    }
}
