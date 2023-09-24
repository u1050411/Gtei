package com.trueta.gtei

import com.trueta.gtei.FocoInfo.Companion.updateScreenWithCombination
import java.io.File

class FocoInfoProcessor {
    /**
     * Generates all possible combinations of n boolean variables.
     */
    private fun generateBooleanCombinations(n: Int): List<List<Boolean>> {
        if (n <= 0) return listOf(emptyList())
        val smaller = generateBooleanCombinations(n - 1)
        return smaller.flatMap { listOf(it + true, it + false) }
    }

    /**
     * Generates the best combinations for a given focus of infection.
     */

    internal fun getBestCombinations(focoInfo: FocoInfo): Triple<MutableMap<String, MutableList<Pair<List<Boolean>, List<String>>>>, List<String>, Map<String, List<String>>> {
        // Fetch boolean variables dynamically
        val name = focoInfo.nombre
        val screen = Screens().findAndCopyScreenByFocus(name) ?: throw IllegalArgumentException("Screen with focus $name not found")
        val booleanVariables = FocoInfo.booleanVariables(focoInfo.nombre)
        val booleanCombinations = generateBooleanCombinations(booleanVariables.size)
        val stringVariables = focoInfo.stringVariables
        val stringCombinations = stringVariables.values.flatten().map { listOf(it) }

        // Generate all possible combinations of boolean and string variables
        val allCombinations = booleanCombinations.flatMap { booleanCombination ->
            stringCombinations.map { stringCombination ->
                booleanCombination to stringCombination
            }
        }


        val treatmentMap = mutableMapOf<String, MutableList<Pair<List<Boolean>, List<String>>>>()

        allCombinations.map { combination ->
            val newScreen = updateScreenWithCombination(screen.copy(), combination, booleanVariables, stringVariables.keys.toList())

            // Nota: Aquí se usa newScreen en lugar de screen
            val logicaMedicaments = DrugsLogic(newScreen)

            val outputInt = focoInfo.treatmentFunction(newScreen)
            val outputString = Medication.getAllName(outputInt)
            val treatmentKey = outputInt.joinToString(",") + ";" + outputString

            // Guardar la combinación de variables que resulta en este treatmentKey
            treatmentMap.getOrPut(treatmentKey) { mutableListOf() }.add(combination)
        }

        return Triple(treatmentMap, booleanVariables, stringVariables )
    }


    /**
     * Writes the best combinations for all focuses into a CSV file.
     */

    fun generateAggregateCSV(
        focusName: String,
        bestCombinations: Triple<MutableMap<String, MutableList<Pair<List<Boolean>, List<String>>>>, List<String>, Map<String, List<String>>>
    ) {
        val aggregateFile = File("AllFocos.csv")

        aggregateFile.bufferedWriter().use { out ->
            // Escribir la cabecera del CSV
            val booleanVarHeader = bestCombinations.second.joinToString(",")
            val stringVarHeader = bestCombinations.third.keys.joinToString(",")
            out.write("focus,$booleanVarHeader,$stringVarHeader,outputInt,outputString\n")

            // Escribir cada enfoque y sus mejores combinaciones
            bestCombinations.first.forEach { (output, combinationsList) ->
                for (combination in combinationsList) {
                    val (outputInt, outputString) = output.split(";")
                    val booleanValues = combination.first.joinToString(",")
                    val stringValues = combination.second.joinToString(",")
                    out.write("$focusName,$booleanValues,$stringValues,$outputInt,$outputString\n")
                }
            }
        }
    }


    /**
     * Writes the given lines into a CSV file.
     */
    fun writeLinesToFile(lines: String) {
        val file = File(".\\app\\src\\test\\java\\com\\tpita", "Tpita.csv")
        file.writeText(lines)
    }

    /**
     * Generates a CSV file containing the best combinations for a given focus of infection.
     */
//    private fun generateOutputCSV(
//        focoInfo: FocoInfo,
//        bestCombinations: Map<String, MutableMap<String, Pair<List<Boolean>, List<String>>>>,
//        invertedTreatmentMap: Map<Pair<List<Boolean>, List<String>>, String>
//    ): String {
//        val file = File(".\\app\\src\\test\\java\\com\\tpita", focoInfo.archivoCSV)
//        if (file.exists()) {
//            file.delete()
//        }
//
//        val contentBuilder = StringBuilder()
//
//        // Write the CSV header
//        val allVariables = focoInfo.booleanVariables + focoInfo.stringVariables.keys
//        contentBuilder.append("focus,${allVariables.joinToString(",")},outputInt,outputString\n")
//
//        // Write each best combination
//        for ((mainBranch, combinationsMap) in bestCombinations) {
//            for ((output, combination) in combinationsMap) {
//                val (outputInt, outputString) = output.split(";")
//                contentBuilder.append("${focoInfo.nombre},${combination.first.joinToString(",")},${combination.second.joinToString(",")},${outputString},$outputInt\n")
//            }
//        }
//
//        file.writeText(contentBuilder.toString())
//
//        return contentBuilder.toString()
//    }
}
fun main() {

    // Crear una instancia de FocoInfoProcessor
    val focoInfoProcessor = FocoInfoProcessor()

    val focus = focus[0]

    // Supongamos que la función getBestCombinations es pública ahora, para acceder desde el main
    val bestCombinations = focoInfoProcessor.getBestCombinations(focus)
    focoInfoProcessor.generateAggregateCSV(focus.nombre, bestCombinations)
//    // Generar el archivo CSV agregado para toda la información del foco
//    focoInfoProcessor.generateAggregateCSV(mapOf(exampleFocoInfo.nombre to bestCombinations))
//
//    // Generar el archivo CSV de salida para el foco de ejemplo
//    val csvContent = focoInfoProcessor.generateOutputCSV(exampleFocoInfo, bestCombinations, invertedTreatmentMap)
    println("Contenido del CSV generado: $bestCombinations")
}
