package com.trueta.gtei

// Holds the information about the screen and its variables
val varsGtei = VariablesGtei().copy()

// Data class to hold information about a focus of infection
data class FocoInfo(
    val nombre: String,
    val stringVariables: Map<String, List<String>>,
    val archivoCSV: String,
    val archivoTest: String,
    val treatmentFunction: (Screen) -> List<Int>,
    val identifyMainBranch: (List<Boolean>, List<String>) -> String,
    val assignVariables: (List<Boolean>, List<String>) -> Screen
) {
    companion object {
        /**
         * Function to find boolean variables from a given screen name.
         *
         * @param name The name of the screen to look for.
         * @return A list of boolean variable names.
         */
        fun booleanVariables(name: String): List<String> {
            // Initialize an empty list to hold the boolean variable names
            val booleanVariables = mutableListOf<String>()

            // Find and copy the screen based on the name
            val screen = Screens().findAndCopyScreenByFocus(name)

            // Loop through each variable in the screen's listVar
            screen?.listVar?.forEach { variable ->
                // Check if the variable is of type VarBool and its name is not "És Al·lèrgia Severa ?"
                if (variable is VarBool && variable.name != "És Al·lèrgia Severa ?") {
                    // Add the variable's name to the list
                    booleanVariables.add(variable.name)
                }
            }

            // Return the list of boolean variable names
            return booleanVariables
        }

        // Handle variable assignment or modification in a list of variables
        fun handleVariable(
            listVar: MutableList<Variable>,
            name: String,
            booleanValue: Boolean? = null,
            stringValue: String? = null
        ) {
            val existingVar = listVar.find { it.name == name }
            when (existingVar) {
                is VarBool -> existingVar.valor = booleanValue ?: false
                is VarString -> existingVar.valorString = stringValue ?: ""
                else -> {
                    val newVar = booleanValue?.let { VarBool(name, it) } ?: VarString(name, stringValue ?: "")
                    listVar.add(newVar)
                }
            }
        }

        fun updateScreenWithCombination(
            screen: Screen,
            combination: Pair<List<Boolean>, List<String>>,
            booleanVarNames: List<String>,
            stringVarNames: List<String>
        ): Screen {
            // Actualizar variables booleanas
            booleanVarNames.forEachIndexed { index, name ->
                val value = combination.first.getOrNull(index) ?: false
                handleVariable(screen.listVar, name, booleanValue = value)
            }

            // Actualizar variables de cadena
            stringVarNames.forEachIndexed { index, name ->
                val value = combination.second.getOrNull(index) ?: ""
                handleVariable(screen.listVar, name, stringValue = value)
            }

            return screen
        }

        // Generate a function to assign variables dynamically in a Screen
        fun generateAssignVariables(
            booleanVarNames: List<String>,
            stringVarNames: Map<String, List<String>>,
            nombre: String
        ): (List<Boolean>, List<String>) -> Screen {
            return { booleanVals, stringVals ->
                val screen = Screens().findAndCopyScreenByFocus(nombre) ?: throw IllegalArgumentException("Screen with focus $nombre not found")

                with(screen.listVar) {
                    stringVarNames.keys.forEachIndexed { index, name ->
                        handleVariable(this, name, stringValue = stringVals.getOrNull(index))
                    }
                    booleanVarNames.forEachIndexed { index, name ->
                        handleVariable(this, name, booleanValue = booleanVals.getOrNull(index))
                    }
                }
                screen
            }
        }
    }
}

// Factory function to create a FocoInfo object
fun createFocoInfo(
    nombre: String,
    stringVariables: Map<String, List<String>>,
    archivoCSV: String,
    archivoTest: String,
    treatmentFunction: (Screen) -> List<Int>,
    identifyMainBranch: (List<Boolean>, List<String>) -> String
): FocoInfo {
    val booleanVariablesFunction = FocoInfo.booleanVariables(nombre)
    val generatedAssignVariables = FocoInfo.generateAssignVariables(booleanVariablesFunction, stringVariables, nombre)
    return FocoInfo(nombre, stringVariables, archivoCSV, archivoTest, treatmentFunction, identifyMainBranch, generatedAssignVariables)
}

// Define a common treatment function
val commonTreatmentFunction: (Screen) -> List<Int> = { screen -> DrugsLogic(screen).tractamentInfeccio() }

// Define a common identifyMainBranch function
val commonIdentifyMainBranch: (List<Boolean>, List<String>) -> String = { _, _ -> "else" }

// Create FocoInfo objects
val focus = listOf(
    createFocoInfo("RESPIRATORI NOSOCOMIAL", mapOf("alergiaPenicilina" to listOf("Sí", "Severa", "No")), "respiratoriNosocomial.csv", "TpitaUnitTestRespiratoriNosocomial.kt", commonTreatmentFunction, commonIdentifyMainBranch),
    createFocoInfo("RESPIRATORI COMUNITAT", emptyMap(), "respiratoriComunitat.csv", "TpitaUnitTestRespiratoriComunitat.kt", commonTreatmentFunction, commonIdentifyMainBranch)
)
