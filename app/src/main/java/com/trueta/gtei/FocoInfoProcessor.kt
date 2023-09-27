package com.trueta.gtei


import java.io.File
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileOutputStream
import java.util.regex.Pattern

class FocoInfoProcessor {
    // Generates all possible combinations of n boolean variables.
    private fun generateBooleanCombinations(n: Int): List<List<Boolean>> {
        if (n <= 0) return listOf(emptyList())
        return generateBooleanCombinations(n - 1).flatMap { listOf(it + true, it + false) }
    }

    private // Función para generar el producto cartesiano de listas de listas
    fun cartesianProduct(lists: List<List<String>>): List<List<String>> {
        // Caso base: si la lista está vacía, retornar una lista con una lista vacía
        if (lists.isEmpty()) return listOf(emptyList())

        // Primer elemento de las listas
        val firstList = lists.first()

        // Resto de las listas
        val remainingLists = lists.drop(1)

        // Calcular el producto cartesiano del resto de las listas de forma recursiva
        val subCartesian = cartesianProduct(remainingLists)

        // Generar el producto cartesiano completo
        val result = mutableListOf<List<String>>()
        for (element in firstList) {
            for (subList in subCartesian) {
                val newList = mutableListOf(element)
                newList.addAll(subList)
                result.add(newList)
            }
        }

        return result
    }

    // Generates the best combinations for a given focus of infection.
    internal fun getBestCombinations(focoInfo: FocoInfo): Triple<MutableMap<String, MutableList<Pair<List<Boolean>, List<String>>>>, List<String>, Map<String, List<String>>> {
        // Fetch boolean variables dynamically
        val name = focoInfo.nombre
        val screen = Screens().findAndCopyScreenByFocus(name)
            ?: throw IllegalArgumentException("Screen with focus $name not found")
        val booleanVariables = FocoInfo.booleanVariables(focoInfo.nombre)
        val booleanCombinations = generateBooleanCombinations(booleanVariables.size)
        val stringVariables = focoInfo.stringVariables
        val stringCombinations = cartesianProduct(stringVariables.values.toList())

        // Generate all possible combinations of boolean and string variables
        val allCombinations = booleanCombinations.flatMap { booleanCombination ->
            stringCombinations.map { stringCombination ->
                booleanCombination to stringCombination
            }
        }

        val treatmentMap = mutableMapOf<String, MutableList<Pair<List<Boolean>, List<String>>>>()

        // Simplified map operation
        allCombinations.forEach { combination ->
            val newScreen = FocoInfo.updateScreenWithCombination(
                screen.copy(),
                combination,
                booleanVariables,
                stringVariables.keys.toList()
            )
            val sortedOutPutInt = focoInfo.treatmentFunction(newScreen).toMutableList()
            sortedOutPutInt.sort()
            val outputInt: List<Int> = sortedOutPutInt.toList()
            val outputString = Medication.getAllName(outputInt)
            val treatmentKey = "$outputInt;$outputString"
            treatmentMap.getOrPut(treatmentKey) { mutableListOf() }.add(combination)
        }

        return Triple(treatmentMap, booleanVariables, stringVariables)
    }

    // Writes the best combinations for all focuses into a CSV file.
    fun generateAggregateCSV(
        focusName: String,
        bestCombinations: Triple<MutableMap<String, MutableList<Pair<List<Boolean>, List<String>>>>, List<String>, Map<String, List<String>>>
    ) {
        val aggregateFile = File("AllFocos.csv")
        aggregateFile.bufferedWriter().use { out ->
            val booleanVarHeader = bestCombinations.second.joinToString(",")
            val stringVarHeader = bestCombinations.third.keys.joinToString(",")
            out.write("focus,$booleanVarHeader,$stringVarHeader,outputInt,outputString\n")

            bestCombinations.first.forEach { (output, combinationsList) ->
                combinationsList.forEach { combination ->
                    val (outputInt, outputString) = output.split(";")
                    val booleanValues = combination.first.joinToString(",")
                    val stringValues = combination.second.joinToString(",")
                    out.write("$focusName,$booleanValues,$stringValues,$outputInt,$outputString\n")
                }
            }
        }
    }

    // Function to generate aggregate CSV for all focuses
    fun generateAllFocosToCSV(focos: List<FocoInfo>) {
        focos.forEach { foco ->
            val bestCombinations = getBestCombinations(foco)
            generateAggregateCSV(foco.nombre, bestCombinations)
        }
    }


    fun cleanSheetName(sheetName: String): String {
        // List of determinant words to remove
        val removeWords = listOf(
            "i",
            "el",
            "la",
            "los",
            "las",
            "un",
            "una",
            "unos",
            "unas",
            "y",
            "o",
            "u",
            "de",
            "en",
            "con",
            "a",
            "<",
            ">",
            "sin",
            "any",
            "anys"
        )

        // Remove special characters and convert to uppercase
        val cleanName =
            Pattern.compile("[^a-zA-Z0-9 ]").matcher(sheetName).replaceAll("").toUpperCase()

        // Remove determinant words
        val cleanNameWithoutDeterminants =
            cleanName.split(" ").filter { it.toLowerCase() !in removeWords }.joinToString("")

        // Limit the length to 31 characters
        val finalCleanName = cleanNameWithoutDeterminants.take(31)

        return finalCleanName
    }


    /**
     * Sanitizes the sheet name to remove invalid characters and limit the length to 31 characters.
     *
     * @param focos List of FocoInfo objects.
     */

    fun sanitizeSheetName(name: String): String {
        // List of determinant words to remove
        val removeWords = listOf(
            "i",
            "el",
            "la",
            "els",
            "les",
            "un",
            "una",
            "uns",
            "unes",
            "i",
            "o",
            "ni",
            "de",
            "en",
            "amb",
            "a",
            "<",
            ">",
            "sense",
            "qualsevol",
            "qualssevol"
        )

        // Remove special characters and convert to uppercase
        val cleanName = Pattern.compile("[^a-zA-Z0-9 ]").matcher(name).replaceAll("").toUpperCase()

        // Remove determinant words
        val cleanNameWithoutDeterminants =
            cleanName.split(" ").filter { it.toLowerCase() !in removeWords }.joinToString("")

        // List of invalid characters to remove for Excel sheet names
        val invalidChars = listOf('\\', '/', '?', '*', '[', ']')

        // Remove invalid characters specific to Excel and limit the length to 31 characters
        val finalCleanName = cleanNameWithoutDeterminants.filter { it !in invalidChars }.take(31)

        return finalCleanName
    }

    // Map to keep track of the frequency of each sanitized sheet name
    private val sheetNameCount = mutableMapOf<String, Int>()

    // Function to generate an Excel file with each focus in a different sheet

    fun generateAllFocosToExcel(focos: List<FocoInfo>) {
        // Create a new Excel workbook
        val workbook = XSSFWorkbook()
        val existingSheetNames = mutableSetOf<String>()
        // Loop through each FocoInfo object to create a new sheet in the workbook
        focos.forEach { foco ->
            val bestCombinations = getBestCombinations(foco)

            // Sanitize the sheet name
            var sanitizedSheetName = sanitizeSheetName(foco.nombre)
            var counter = 1

            // Check for existing sheet names and add a counter if necessary
            while (existingSheetNames.contains(sanitizedSheetName) || sanitizedSheetName.length > 31) {
                sanitizedSheetName = sanitizedSheetName.take(30 - counter.toString().length) + counter.toString()
                counter++
            }

            existingSheetNames.add(sanitizedSheetName)

            println("Creating sheet: $sanitizedSheetName")  // Debugging line to see what the sheet name will be
            val sheet = workbook.createSheet(sanitizedSheetName)  // Create new sheet with sanitized name

            // Generate headers dynamically
            val headerRow = sheet.createRow(0)
            val booleanVarHeader = bestCombinations.second.joinToString(",")
            val stringVarHeader = bestCombinations.third.keys.joinToString(",")
            val headers =
                "focus,$booleanVarHeader,$stringVarHeader,outputInt,outputString".split(",")

            headers.forEachIndexed { index, header ->
                val cell = headerRow.createCell(index)
                cell.setCellValue(header)
            }

            // Add data rows
            var rowNum = 1
            bestCombinations.first.forEach { (output, combinationsList) ->
                combinationsList.forEach { combination ->
                    val row = sheet.createRow(rowNum++)

                    val focusName = foco.nombre
                    val (outputInt, outputString) = output.split(";")
                    val booleanValues = combination.first // No longer joined into a single string
                    val stringValues = combination.second // No longer joined into a single string

                    // Combine all the data into a single list, maintaining the order
                    val data = listOf(focusName) + booleanValues + stringValues + listOf(
                        outputInt,
                        outputString
                    )

                    data.forEachIndexed { index, value ->
                        val cell = row.createCell(index)
                        cell.setCellValue(value.toString())
                    }
                }
            }
        }

        // Write the workbook to a file
        val fileOut = FileOutputStream("AllFocos.xlsx")
        workbook.write(fileOut)
        fileOut.close()

        // Close the workbook
        workbook.close()
    }
}


fun main() {
    // Create an instance of FocoInfoProcessor
    val focoInfoProcessor = FocoInfoProcessor()
    // Assuming 'focus' is a list of FocoInfo objects
    focoInfoProcessor.generateAllFocosToExcel(focus)
    //   val foco2 =    createFocoInfo("UROLOGIA PIELONEFRITIS AGUDA NO COMPLICADA", mapOf("Al·lèrgia Penicil·lina" to listOf("Sí", "Severa", "No")), "UROLOGIA PIELONEFRITIS AGUDA NO COMPLICADA.csv", "TpitaUnitTestUROLOGIA PIELONEFRITIS AGUDA NO COMPLICADA.kt", commonTreatmentFunction, commonIdentifyMainBranch)
    //      val tiple = focoInfoProcessor.getBestCombinations(foco2)
    //     focoInfoProcessor.generateAggregateCSV(foco2.nombre, tiple)
}
