package com.trueta.gtei

class DoseLogic(
    originalData: Screen,
    medication: Int,
    sliderData: SliderData
) {
    internal fun Map<String, Any>.getStringValue(name: String): String =
        (this[name] as? VarString)?.valorString ?: ""

    private val data = sliderData
    private val drug = medication

    private var focus = originalData.focus
    private val variables = Variables()
    private val dades = originalData.listVar.filterIsInstance<Variable>().associateBy { it.name }


    private val fg = data.fg
    private val height = data.height
    private val isFemale = data.sex == false
    private val cellType = dades.getVariableString(variables.tipusCelulitis.name)
    private val suspectedPneumonia = dades.getVariableValue(variables.sospitaPneumonia.name)
    private val septicShock = dades.getVariableValue(variables.xocSeptic.name)
    private val abm = data.weight // Actual Body Mass
    internal val bmi = (abm / (height * height)) > 30 // Body Mass Index
    internal val ibw = // Ideal Body Weight
        if (isFemale) (45.5 + 0.89 * (height - 152.4)) else (49.9 + 0.89 * (height - 152.4))
    internal val ajbw = (ibw + 0.4 * (abm + ibw)) // Adjusted Body Weight

    private val withoutData = if (medication > 80) "" else "No hi ha guies específiques disponibles"

    internal fun calculateWeight(tipus: String, dosi: Int): Int =
        when (tipus) {
            "ABW" -> dosi * abm.toInt()
            "AjBW" -> dosi * ajbw.toInt()
            else -> 0
        }

    private val medicationDoseMapByIndex: Map<Int, () -> String> = mapOf(
        1 to ::doxiciclinaDosage,
        2 to ::ampicillinDosage,
        3 to ::cloxacillinDosage,
        4 to ::piperacillinTazobactamDosage,
        5 to ::amoxicillinClavulanateDosage,
        6 to ::cefazolinDosage,
        7 to ::cefalexinDosage,
        8 to ::cefuroximaAxetilDosage, // Assuming the dosage function name, please correct if wrong
        9 to ::ceftriaxonaDosage,
        10 to ::cefepimeDosage,
        11 to ::aztreonamDosage,
        12 to ::meropenemDosage,
        13 to ::azithromycinDosage,
        14 to ::clindamycinDosage,
        15 to ::gentamicinDosage,
        16 to ::amikacinaDosage,
        17 to ::ciprofloxacinDosage,
        18 to ::levofloxacinDosage,
        19 to ::vancomycinDosage,
        20 to ::metronidazolDosage,
        21 to ::fosfomycinDosage,
        22 to ::fluconazoleDosage,
        23 to ::daptomycinDosage,

        25 to ::linezolidDosage,
        26 to ::caspofunginDosage,
        27 to ::aciclovirDosage,
        28 to ::rifampicinDosage,

    )



    fun doseInfectionTreatment(): Pair<String, String?> {
        val medical = Medication.get(drug) as Medication
        val dose = medicationDoseMapByIndex[drug]?.invoke() ?: notDosage()
        return Pair(medical.name, dose)
    }

    internal fun dosage(weightType: String, maxDose: Int, mgPerKg: Int, frequency: String): String {
        return "${minOf(calculateWeight(weightType, mgPerKg), maxDose)} mg cada $frequency iv"
    }

    internal fun aciclovirDosage(): String {
        val weightType = if (bmi) "AjBW" else "ABW"
        return when {
            fg in 51.0..200.0 && (bmi || septicShock) -> dosage(weightType, 1000, 10, "8h")
            fg in 25.0..50.0 -> dosage(weightType, 1000, 10, "12h")
            fg in 10.0..25.0 -> dosage(weightType, 1000, 10, "24h")
            fg in 0.0..9.0 -> dosage(weightType, 1000, 5, "24h")
            else -> withoutData
        }
    }

    private fun amikacinaDosage(): String {
        val sepsis = focus.contains("SEPSIS", ignoreCase = true)
        val baseDose = if (fg in 60.0..200.0) 15 + (if (sepsis) 5 else 0)
        else if (fg in 30.0..60.0 || fg in 15.0..29.0) 15 + (if (sepsis) 5 else 0)
        else if (fg in 0.0..14.0) 10
        else 0

        val frequency = when (fg) {
            in 60.0..200.0 -> "24h"
            in 30.0..59.0 -> "36h"
            in 15.0..29.0, in 0.0..14.0 -> "48h"
            else -> withoutData
        }

        val monitoring =
            if (fg in 0.0..200.0 && baseDose > 0) " i sol·licitar monitoratge concentracions plasmàtiques" else ""

        return if (baseDose > 0 && frequency.isNotEmpty()) dosage(
            "AjBW",
            1500,
            baseDose,
            frequency
        ) + monitoring
        else withoutData
    }


    internal fun amoxicillinClavulanateDosage(): String = when {
        cellType == "CEL.LULITIS MODERADA/GREU" -> "1/0.2 g cada 8h iv"
        suspectedPneumonia && fg in 15.0..30.0 -> "1/0.2 g seguit de 500/100 mg cada 12-24h iv"
        suspectedPneumonia && fg > 30.0 -> "2/0.2 g cada 8h iv"
        suspectedPneumonia && fg < 15.0 -> "500/1000 mg cada 24h iv"
        cellType == "CEL.LULITIS LLEU" && !septicShock -> "500/100 mg cada 8h vo"
        cellType == "CEL.LULITIS LLEU" && septicShock -> "875/125 mg cada 8h vo"
        else -> withoutData
    }

    private fun ampicillinDosage(): String {
        return when {
            fg in 60.0..200.0 -> "2 g cada 4h iv"
            fg in 30.0..59.0 -> "2 g cada 8h iv"
            fg in 15.0..29.0 -> "2 g cada 12h iv"
            fg in 0.0..14.0 -> "2 g cada 24h iv"
            else -> withoutData
        }
    }

    private fun azithromycinDosage(): String = when {
        fg >= 15.0 -> "500 mg cada 24h iv"
        fg < 15.0 -> "500 mg cada 24h iv - Utilitzar amb precaucio"
        else -> withoutData
    }

    internal fun aztreonamDosage(): String {
        val neurologic = focus.contains("NEUROLOGIC", ignoreCase = true)

        return when {
            fg in 30.0..200.0 && (septicShock || bmi || neurologic) -> "2 g cada 6h iv"
            fg in 30.0..200.0 -> "2 g cada 8h iv"
            fg in 15.0..29.0 -> "dosi inicial 1-2 g \nseguit de 0,5-1 g cada 6-12h iv"
            fg in 0.0..14.0 -> "dosi inicial 1-2 g \nseguit de 500 mg cada 6-12h iv"
            else -> withoutData
        }
    }

    internal fun caspofunginDosage(): String = when {
        abm in 80.0..200.0 -> "70 mg cada 24h iv"
        else -> "70 mg seguit de 50 mg a partir del dia 2 cada 24h iv"
    }

    internal fun cefalexinDosage(): String {
        return when {
            fg in 30.0..200.0 && !bmi -> dosage("ABW", 200, 500, "8h")
            fg in 15.0..29.0 && !bmi -> dosage("ABW", 200, 500, "8h")
            fg in 0.0..14.0 && !bmi -> dosage("ABW", 200, 500, "12h")
            fg in 30.0..200.0 && bmi -> dosage("AjBW", 200, 500, "6h")
            else -> withoutData
        }
    }

    internal fun cefazolinDosage(): String {
        return when {
            fg in 0.0..14.0 -> "1-2 g cada 24h iv"
            else -> "2 g ${
                when {
                    fg in 15.0..29.0 -> "12h iv"
                    fg in 60.0..200.0 && bmi -> "6h iv"
                    else -> "8h iv"
                }
            }"
        }
    }


    internal fun cefepimeDosage(): String {
        val weightType = if (bmi) "AjBW" else "ABW"
        val perfusio = if (bmi) " iv en perfusió extesa (Admin en 3h)" else " iv"

        return when (fg) {
            in 60.0..200.0 -> dosage(weightType, 200, 2000, "8h") + perfusio
            in 30.0..59.0 -> dosage(weightType, 200, 2000, "12h") + perfusio
            in 15.0..29.0 -> dosage(weightType, 200, 2000, "24h") + perfusio
            in 0.0..14.0 -> dosage(weightType, 200, 1000, "24h") + perfusio
            else -> withoutData
        }
    }

    internal fun ceftriaxonaDosage(): String {
        val septicArthritis = focus.contains("ARTRITIS SEPTICA")
        val endocarditis = focus.contains("ENDOCARDITIS")
        return when {
            (septicShock || septicArthritis || endocarditis) && bmi -> "2 g cada 12h iv"
            septicShock || septicArthritis || endocarditis -> "2 g cada 24h iv"
            else -> "1 g dosi única iv"
        }
    }

    internal fun cefuroximaAxetilDosage(): String {
        return when {
            fg > 30 -> "500 mg cada 12h iv"
            else -> "500 mg cada 24h iv"
        }
    }

    fun ciprofloxacinDosage(): String {
        return "2 mg cada 8h iv"
    }

    internal fun clindamycinDosage(): String {
        val necrotizingFasciitis = focus.contains("PELL FASCITIS NECROTITZANT")
        val dosis = when {
            septicShock || necrotizingFasciitis || suspectedPneumonia -> 600
            else -> 450 //cellType == "CEL.LULITIS MODERADA/GREU"
        }

        val frequency = when {
            bmi -> "6h"
            else -> "8h"
        }

        val unit = when {
            septicShock || necrotizingFasciitis || suspectedPneumonia -> "iv"
            else -> "vo" // cellType == "CEL.LULITIS MODERADA/GREU"
        }

        return "$dosis cada $frequency $unit"
    }


    internal fun cloxacillinDosage(): String {
        val septicArthritis = focus.contains("ARTRITIS SEPTICA")
        val skinSoftTissues = focus.contains("PELL I PARTS TOVES")
        val endocarditis = focus.contains("ENDOCARDITIS")
        return when {
            skinSoftTissues -> "2 g cada 6h iv"
            septicArthritis || endocarditis -> "2 g cada 4h iv"
            else -> withoutData
        }
    }

    internal fun daptomycinDosage(

    ): String {
        val septicArthritis = focus.contains("ARTRITIS SEPTICA")
        val endocarditis = focus.contains("ENDOCARDITIS")
        val catheterInfection = focus.contains("INFECCIO CATETER")
        val weightType = if (bmi) "AjBW" else "ABW"
        val maxDose = 1000
        var mgPerKg= 0
        val frequency = if (fg >= 30) "24h" else "48h"

        when {
            fg in 30.0..200.0 -> mgPerKg = 10
            fg in 15.0..29.0 -> mgPerKg = 8
            fg in 0.0..14.0 -> mgPerKg = 6
            else -> return withoutData
        }

        return when {
            septicArthritis || endocarditis || catheterInfection -> dosage(
                weightType,
                maxDose,
                mgPerKg,
                frequency
            )

            else -> withoutData
        }
    }

    internal fun doxiciclinaDosage(): String {
        return "100 mg cada 12h vo"
    }


    fun fluconazoleDosage(): String {
        val firstDayDose = "800 mg dia 1 seguit de"
        val subsequentDose: String
        val frequency = "24h"
        val method = "iv"

        if (fg > 50) {
            subsequentDose = if (abm > 50) {
                "${calculateWeight("ABW", 6)} mg/kg"
            } else {
                "400 mg"
            }
        } else {
            subsequentDose = if (abm < 50) {
                "${calculateWeight("ABW", 3)} mg/kg"
            } else {
                "200 mg"
            }
        }

        return "$firstDayDose, $subsequentDose $frequency $method"
    }


    internal fun fosfomycinDosage(): String {
        val UTIsPregname =  dades.getVariableString(variables.tipusInfeccionsUrinariesGestants.name) =="INFECCIONS URINÀRIES EN GESTANTS"
        val cistitisComplicada = focus.contains("CISTITIS COMPLICADA", ignoreCase = true)

        return when {
            cistitisComplicada -> "3 g cada 48h 2  vo"
            UTIsPregname -> "3 g dosi única vo"
            fg in 0.0..9.0 -> "No recomanada. Consultar protocol tractament infecció urinària."
            else -> withoutData
        }
    }


    internal fun gentamicinDosage(): String {
        val septicArthritis = focus.contains("ARTRITIS SEPTICA")
        val endocarditis = focus.contains("ENDOCARDITIS")
        val mgPerKg = if (septicArthritis || endocarditis) 5 else 3
        val maxDose = 450
        val frequency = when {
            fg in 60.0..200.0 -> "24h"
            fg in 30.0..59.0 -> "36h"
            fg in 15.0..29.0 || fg in 0.0..14.0 -> "48h"
            else -> withoutData
        }

        val additionalComment =
            if (fg < 60) "i sol·licitar monitoratge concentracions plasmàtiques" else ""
        return "${dosage("AjBW", maxDose, mgPerKg, frequency)} $additionalComment"
    }

    internal fun levofloxacinDosage(): String {
        val respiratory = focus.contains("RESPIRATORI", ignoreCase = true)
        val orquiditis = focus.contains("ORQUIDITIS", ignoreCase = true)

        val dose = when {
            fg in 30.0..200.0 && (respiratory || orquiditis) -> "500 mg cada "
            fg in 0.0..29.0 && (respiratory || orquiditis) -> "dosi inicial 500 mg seguit de 250 mg cada"
            else -> withoutData
        }

        val frequency = when {
            fg in 60.0..200.0 && (respiratory || orquiditis) -> "12h"
            fg in 15.0..59.0 && (respiratory || orquiditis) -> "24h"
            fg in 0.0..14.0 && (respiratory || orquiditis) -> "48h"
            else -> withoutData
        }

        val method = when {
            orquiditis -> "vo"
            else -> "iv"
        }

        return "$dose $frequency $method"
    }


    fun linezolidDosage(): String {
        return "600 mg cada 12h iv"
    }

    internal fun meropenemDosage(): String {
        val colangitis = focus.contains("COLANGITIS", ignoreCase = true)
        val colecistitis = focus.contains("COLECISTITIS", ignoreCase = true)
        val neurologic = focus.contains("NEUROLOGIC", ignoreCase = true)

//        return if (colangitis || colecistitis || neurologic) {
        return when {
                fg in 60.0..200.0 && septicShock -> "2 g cada 8h iv" + if (bmi) " en perfusió extesa (Admin en 3h)" else ""
                fg in 30.0..59.0 && septicShock -> "2 g cada 12h iv" + if (bmi) " en perfusió extesa (Admin en 3h)" else ""
                fg in 15.0..29.0 && septicShock -> "1 g cada 12h iv" + if (bmi) " en perfusió extesa (Admin en 3h)" else ""
                fg in 0.0..14.0 && septicShock -> "1 g cada 24h iv" + if (bmi) " en perfusió extesa (Admin en 3h)" else ""
                fg in 60.0..200.0 -> "1 g cada 8h iv"
                fg in 30.0..59.0 -> "1 g cada 12h iv"
                fg in 15.0..29.0 -> "500 mg cada 12h iv"
                fg in 0.0..14.0 -> "500 mg cada 24h iv"
                else -> withoutData
            }
        }

    fun metronidazolDosage(): String {
        return "500 mg cada 8h iv"
    }

    internal fun piperacillinTazobactamDosage(): String {
        return when {
            fg > 40 -> "4 g cada 6h iv"
            fg in 20.0..39.0 -> "4 g cada 8h iv"
            else -> "2 g cada 8h iv"
        }
    }


    fun rifampicinDosage(): String {
        return "300 mg cada 8h vo"
    }

    internal fun vancomycinDosage(): String {
        val neurologic = focus.contains("NEUROLOGIC", ignoreCase = true)
        val maxDose = 2000
        var mgPerKg = 15
        val weightType = "ABW"
        val frequency = when {
            fg in 0.0..14.0 -> "48-72h"
            fg in 15.0..29.0 -> "24h"
            fg in 30.0..200.0  -> if (septicShock) "8-12h" else "12h"
            else -> return withoutData
        }
        val calculWeight = minOf(calculateWeight(weightType, mgPerKg), maxDose)
        val initialDose = "Dosis inicial ${calculWeight} mg/kg\n"

        mgPerKg = when {
            fg in 0.0..14.0 -> 10
            fg in 15.0..200.0 -> 15
            else -> return withoutData
        }
        val dose = "Dosis manteniment ${dosage(weightType, maxDose, mgPerKg, frequency)}"
        val onlyDose = "${dosage(weightType, maxDose, mgPerKg, frequency)}"

        val comment = when {
            fg in 0.0..14.0 || neurologic -> "i sol·licitar monitoratge concentracions plasmàtiques"
            fg in 30.0..200.0 && bmi -> "i sol·licitar monitoratge concentracions plasmàtiques"
            else -> ""
        }

        return when {
            fg in 0.0..14.0 -> "$initialDose $dose $comment\n"
            fg in 30.0..200.0 && bmi -> "$initialDose $dose $comment\n"
            else -> "$onlyDose $comment"
        }

    }

    internal fun notDosage(): String = withoutData //Sense Dosis

}



