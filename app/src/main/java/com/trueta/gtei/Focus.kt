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
    createFocoInfo("RESPIRATORI NOSOCOMIAL", mapOf("Al·lèrgia Penicil·lina" to listOf("Sí", "Severa", "No")), "RESPIRATORI NOSOCOMIAL.csv", "TpitaUnitTestRESPIRATORI NOSOCOMIAL.kt", commonTreatmentFunction, commonIdentifyMainBranch),
    createFocoInfo("RESPIRATORI COMUNITAT", mapOf("Al·lèrgia Penicil·lina" to listOf("Sí", "Severa", "No")), "RESPIRATORI COMUNITAT.csv", "TpitaUnitTestRESPIRATORI COMUNITAT.kt", commonTreatmentFunction, commonIdentifyMainBranch),
    createFocoInfo("ABDOMINAL ALT RISC", mapOf("Al·lèrgia Penicil·lina" to listOf("Sí", "Severa", "No")), "ABDOMINAL ALT RISC.csv", "TpitaUnitTestABDOMINAL ALT RISC.kt", commonTreatmentFunction, commonIdentifyMainBranch),
    createFocoInfo("ABDOMINAL BAIX RISC", mapOf("Al·lèrgia Penicil·lina" to listOf("Sí", "Severa", "No")), "ABDOMINAL BAIX RISC.csv", "TpitaUnitTestABDOMINAL BAIX RISC.kt", commonTreatmentFunction, commonIdentifyMainBranch),
    createFocoInfo("ABDOMINAL COLECISTITIS", mapOf("Al·lèrgia Penicil·lina" to listOf("Sí", "Severa", "No"), "tipusColestitis" to listOf("GRAU I", "GRAU II", "GRAU III", "XOC SÈPTIC")), "ABDOMINAL COLECISTITIS.csv", "TpitaUnitTestABDOMINAL COLECISTITIS.kt", commonTreatmentFunction, commonIdentifyMainBranch),
    createFocoInfo("ABDOMINAL COLANGITIS", mapOf("Al·lèrgia Penicil·lina" to listOf("Sí", "Severa", "No"), "tipusColangitis" to listOf("TIPUS I", "TIPUS II", "TIPUS III", "XOC SÈPTIC")), "ABDOMINAL COLANGITIS.csv", "TpitaUnitTestABDOMINAL COLANGITIS.kt", commonTreatmentFunction, commonIdentifyMainBranch),
    createFocoInfo("ABDOMINAL PIELEMEFRITIS LLEU", emptyMap(), "ABDOMINAL PIELEMEFRITIS LLEU.csv", "TpitaUnitTestABDOMINAL PIELEMEFRITIS LLEU.kt", commonTreatmentFunction, commonIdentifyMainBranch),
    createFocoInfo("ABDOMINAL PIELEMEFRITIS MODERADA/GREU", mapOf("Al·lèrgia Penicil·lina" to listOf("Sí", "Severa", "No")), "ABDOMINAL PIELEMEFRITIS MODERADA/GREU.csv", "TpitaUnitTestABDOMINAL PIELEMEFRITIS MODERADA/GREU.kt", commonTreatmentFunction, commonIdentifyMainBranch),
    createFocoInfo("UROLOGIA BACTERIÚRIA ASIMPTOMÀTICA", mapOf("BACTERIÚRIA ASIMPTOMÀTICA" to listOf("GESTANT", "CIRURGIA PROSTÀTICA", "Manipulació Urològica", "ALTRES")), "UROLOGIA BACTERIÚRIA ASIMPTOMÀTICA.csv", "TpitaUnitTestUROLOGIA BACTERIÚRIA ASIMPTOMÀTICA.kt", commonTreatmentFunction, commonIdentifyMainBranch),
    createFocoInfo("UROLOGIA CISTITIS COMPLICADA", emptyMap(), "UROLOGIA PIELONEFRITIS COMPLICADA/SÈPSIA.csv", "TpitaUnitTestUROLOGIA PIELONEFRITIS COMPLICADA/SÈPSIA.kt", commonTreatmentFunction, commonIdentifyMainBranch),
    createFocoInfo("UROLOGIA PIELONEFRITIS COMPLICADA/SÈPSIA", mapOf("Al·lèrgia Penicil·lina" to listOf("Sí", "Severa", "No")), "UROLOGIA PIELONEFRITIS COMPLICADA/SÈPSIA.csv", "TpitaUnitTestUROLOGIA PIELONEFRITIS COMPLICADA/SÈPSIA.kt", commonTreatmentFunction, commonIdentifyMainBranch),
    createFocoInfo("UROLOGIA PIELONEFRITIS AGUDA NO COMPLICADA", mapOf("Al·lèrgia Penicil·lina" to listOf("Sí", "Severa", "No")), "UROLOGIA PIELONEFRITIS AGUDA NO COMPLICADA.csv", "TpitaUnitTestUROLOGIA PIELONEFRITIS AGUDA NO COMPLICADA.kt", commonTreatmentFunction, commonIdentifyMainBranch),
    createFocoInfo("UROLOGIA PROSTATITIS", mapOf("Al·lèrgia Penicil·lina" to listOf("Sí", "Severa", "No")), "UROLOGIA PROSTATITIS.csv", "TpitaUnitTestUROLOGIA PROSTATITIS.kt", commonTreatmentFunction, commonIdentifyMainBranch),
    createFocoInfo("UROLOGIA ORQUITIS I EPIDIMITIS", mapOf("Al·lèrgia Penicil·lina" to listOf("Sí", "Severa", "No")), "UROLOGIA ORQUITIS I EPIDIMITIS.csv", "TpitaUnitTestUROLOGIA ORQUITIS I EPIDIMITIS.kt", commonTreatmentFunction, commonIdentifyMainBranch),
    createFocoInfo("UROLOGIA XOC SÈPTIC", mapOf("Al·lèrgia Penicil·lina" to listOf("Sí", "Severa", "No")), "UROLOGIA XOC SÈPTIC.csv", "TpitaUnitTestUROLOGIA XOC SÈPTIC.kt", commonTreatmentFunction, commonIdentifyMainBranch),
    createFocoInfo("UROLOGIA INFECCIONS URINÀRIES EN GESTANTS", mapOf("Al·lèrgia Penicil·lina" to listOf("Sí", "Severa", "No"), "INFECCIONS URINÀRIES EN GESTANTS" to listOf("BACTERIÚRIA ASIMPTOMÀTICA GESTANT","CISTITIS AGUDA GESTANT","PIELONEFRITIS")), "UROLOGIA INFECCIONS URINÀRIES EN GESTANTS.csv", "TpitaUnitTestUROLOGIA INFECCIONS URINÀRIES EN GESTANTS.kt", commonTreatmentFunction, commonIdentifyMainBranch),
    createFocoInfo("PELL CEL.LULITIS LLEU", mapOf("Al·lèrgia Penicil·lina" to listOf("Sí", "Severa", "No"), "Tipus Celulitis" to listOf("CEL.LULITIS LLEU", "CEL.LULITIS MODERADA/GREU")), "PELL CEL.LULITIS LLEU.csv", "TpitaUnitTestPELL CEL.LULITIS LLEU.kt", commonTreatmentFunction, commonIdentifyMainBranch),
    createFocoInfo("PELL FASCITIS NECROTITZANT", mapOf("Al·lèrgia Penicil·lina" to listOf("Sí", "Severa", "No")), "PELL FASCITIS NECROTITZANT.csv", "TpitaUnitTestPELL FASCITIS NECROTITZANT.kt", commonTreatmentFunction, commonIdentifyMainBranch),
    createFocoInfo("PELL INFECCIÓ DE PEU DIABÈTIC", emptyMap(), "PELL INFECCIÓ DE PEU DIABÈTIC.csv", "TpitaUnitTestPELL INFECCIÓ DE PEU DIABÈTIC.kt", commonTreatmentFunction, commonIdentifyMainBranch),
    createFocoInfo("NEUROLOGIC MENINGITIS BACTERIANA", mapOf("Al·lèrgia Penicil·lina" to listOf("Sí", "Severa", "No"), "TIPUS MENINGITIS" to listOf("QUALSEVOL EDAT, SENSE COMORBIDITAT SIGNIFICATIVA", "NEUROLOGIA NOSOCOMIAL (I POSTQUIRÚRGICA)", "AMB IMMUNOSUPRESSIÓ")), "NEUROLOGIC MENINGITIS BACTERIANA.csv", "TpitaUnitTestNEUROLOGIC MENINGITIS BACTERIANA.kt", commonTreatmentFunction, commonIdentifyMainBranch),
    createFocoInfo("NEUROLOGIC MENINGITIS VÍRICA", emptyMap(), "NEUROLOGIC MENINGITIS VÍRICA.csv", "TpitaUnitTestNEUROLOGIC MENINGITIS VÍRICA.kt", commonTreatmentFunction, commonIdentifyMainBranch),
    createFocoInfo("NEUROLOGIC ENCEFALITIS I SOSPITA CAUSA HERPÈTICA", emptyMap(), "NEUROLOGIC ENCEFALITIS I SOSPITA CAUSA HERPÈTICA.csv", "TpitaUnitTestNEUROLOGIC ENCEFALITIS I SOSPITA CAUSA HERPÈTICA.kt", commonTreatmentFunction, commonIdentifyMainBranch),
    createFocoInfo("ARTRITIS SEPTICA < 60 ANYS SENSE COMORBIDITATS", mapOf("Al·lèrgia Penicil·lina" to listOf("Sí", "Severa", "No")), "ARTRITIS SEPTICA < 60 ANYS SENSE COMORBIDITATS.csv", "TpitaUnitTestARTRITIS SEPTICA < 60 ANYS SENSE COMORBIDITATS.kt", commonTreatmentFunction, commonIdentifyMainBranch),
    createFocoInfo("ARTRITIS SEPTICA  > 60 ANYS O COMORBIDITATS", mapOf("Al·lèrgia Penicil·lina" to listOf("Sí", "Severa", "No")), "ARTRITIS SEPTICA > 60 ANYS O COMORBIDITATS.csv", "TpitaUnitTestARTRITIS SEPTICA > 60 ANYS O COMORBIDITATS.kt", commonTreatmentFunction, commonIdentifyMainBranch),
    createFocoInfo("ENDOCARDITIS VÀLVULA NADIUA/PROTÈSICA TARDANA (> 1 ANY)", mapOf("Al·lèrgia Penicil·lina" to listOf("Sí", "Severa", "No")), "ENDOCARDITIS VÀLVULA NADIUA/PROTÈSICA TARDANA (> 1 ANY).csv", "TpitaUnitTestENDOCARDITIS VÀLVULA NADIUA/PROTÈSICA TARDANA (> 1 ANY).kt", commonTreatmentFunction, commonIdentifyMainBranch),
    createFocoInfo("ENDOCARDITIS VÀLVULA NADIUA/PROTÈSICA PRECOÇ (< 1 ANY)", mapOf("Al·lèrgia Penicil·lina" to listOf("Sí", "Severa", "No")), "ENDOCARDITIS VÀLVULA NADIUA/PROTÈSICA PRECOÇ (< 1 ANY).csv", "TpitaUnitTestENDOCARDITIS VÀLVULA NADIUA/PROTÈSICA PRECOÇ (< 1 ANY).kt", commonTreatmentFunction, commonIdentifyMainBranch),
    createFocoInfo("FEBRE NEUTROPENICA", mapOf("Al·lèrgia Penicil·lina" to listOf("Sí", "Severa", "No")), "FEBRE NEUTROPENICA.csv", "TpitaUnitTestFEBRE NEUTROPENICA.kt", commonTreatmentFunction, commonIdentifyMainBranch),
    createFocoInfo("INFECCIOCATETER", mapOf("Al·lèrgia Penicil·lina" to listOf("Sí", "Severa", "No")), "INFECCIOCATETER.csv", "TpitaUnitTestINFECCIOCATETER.kt", commonTreatmentFunction, commonIdentifyMainBranch)
)

