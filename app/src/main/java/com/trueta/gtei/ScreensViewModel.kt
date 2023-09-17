package com.trueta.gtei

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
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
}
