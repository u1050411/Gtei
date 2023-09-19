package com.trueta.gtei

class ControllerLogic {

    /**
     * Processes the provided screen and determines the required medications and their needs.
     *
     * @param screen The screen containing the required data.
     * @return A Pair containing the list of medications and their needs.
     */
    fun processTryScreen(screen: Screen): Pair<List<Int>, Medication> {
        val drugsList = getMedicationForTreatment(screen)
        return Pair(drugsList, calculateDrugNeeds(drugsList))
    }

    /**
     * Retrieves the list of medications for a given infection treatment based on the screen details.
     *
     * @param screen The screen containing the infection details.
     * @return List of medications for the treatment.
     */
    private fun getMedicationForTreatment(screen: Screen): List<Int> =
        DrugsLogic(screen).tractamentInfeccio() ?: emptyList()

    /**
     * Calculates the medication needs based on the given list of medications.
     * It determines the overall medication needs by folding over the list and aggregating individual drug requirements.
     *
     * @param drugInt List of medications.
     * @return Medication object representing the cumulative needs.
     */
    private fun calculateDrugNeeds(drugInt: List<Int>): Medication {
        val drugMedication = Medication.getAll(drugInt)
        return drugMedication.fold(Medication(Index = 0)) { acc, drug ->
            Medication(
                Index = 0,
                fg = acc.fg || drug?.fg ?: false,
                sex = acc.sex || drug?.sex ?: false,
                weight = acc.weight || drug?.weight ?: false,
                height = acc.height || drug?.height ?: false,
            )
        }
    }

    /**
     * For each drug in the list, determines the infection treatment dose based on the screen and data bundle.
     *
     * @param screen The screen containing the infection details.
     * @param listDrugs List of medications.
     * @param sliceData Additional data for determining the dose.
     * @return List of Pairs with the drug name and its dose for the infection treatment.
     */
    fun processSliceScreen(
        screen: Screen?,
        listDrugs: List<Int>?,
        sliceData: SliderData
    ): List<Pair<String, String?>> {
        // Si listDrugs o screen es nulo, retorna una lista vacía.
        if (listDrugs == null || screen == null) {
            return emptyList()
        }

        return listDrugs.map { medication ->
            // Si el medicamento no es nulo, procede con la lógica de dosificación.
            if (medication != null) {
                val doseLogic =  DoseLogic(screen,medication, sliceData)
                val doseInfo = doseLogic?.doseInfectionTreatment()
                // Si doseInfo es nulo, incluye el nombre del medicamento en el mensaje de error.
                doseInfo ?: Pair(Medication.get(medication)!!.name, "No hay datos de dosis para este medicamento")
            } else {
                // En caso de medicamento nulo, incluye un mensaje de error.
                Pair("Desconocido", "No hay datos de dosis para este medicamento")
            }
        }
    }
}





