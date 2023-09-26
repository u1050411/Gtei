package com.trueta.gtei

import androidx.annotation.StringRes
import java.io.Serializable
import kotlin.reflect.full.memberProperties


interface Variable : Serializable {
    var name: String
    val popUp: String
}

data class VarBool(
    override var name: String,
    var valor: Boolean = false,
    override val popUp: String = "",
    val options: Pair<String, String> = Pair("Sí", "No")
) : Variable

data class VarString(
    override var name: String,
    var valorString: String = "No",
    override val popUp: String = ""
) : Variable

// Data class to hold SliderData
data class SliderData(
    val fg: Double = 125.0,
    val weight: Double = 85.0,
    val height: Double = 175.0,
    val sex: Boolean = true
) : Serializable


enum class Gender {
    Men, Women
}

data class RangeSlice(
    @StringRes val name: Int, // Name of the slider
    val range: IntRange, // Allowed value range for the slider
    val initialValue: Float, // Initial value of the slider
    val stepSize: Int, // Step size between values (can be 0 for continuous)
    @StringRes val unit: Int, // Unit of the value (e.g., kg, cm, etc.)
    val valueCallback: (Float) -> Unit // Callback function when the slider value changes
)


data class VariablesGtei(
    var focus: VarString = VarString("Focus infecció"),
    var xocSeptic: VarBool = VarBool("Xoc Sèptic"),
    var blee: VarBool = VarBool("BLEE"),
    var isCvc: VarBool = VarBool("CVC"),
    var isFocusAbdominal: VarBool = VarBool("Focus Abdominal"),
    var isKill: VarBool = VarBool("Sospita Pell"),
    var tractamentAntifungic: VarBool = VarBool("Tractament Antifúngic"),
    var frmr: VarBool = VarBool("FRMR"),
    var sospitaIts: VarBool = VarBool("SOSPITA ITS < 35 anys"),
    var multiResistent: VarBool = VarBool("Multi Resistent"),
    var sarm: VarBool = VarBool("SARM"),
    var enterococ: VarBool = VarBool("Enterococ"),
    var sospitaPneumonia: VarBool = VarBool("Sospita Pneumònia Aspirativa"),
    var sospitaLegionella: VarBool = VarBool("Sospita Legionella"),
    var mossegada: VarBool = VarBool("Mossegada"),
    var fracasTractament: VarBool = VarBool("Fracas Tractament"),
    var marsa: VarBool = VarBool("MARSA"),
    var mrsa: VarBool = VarBool("MRSA/Enterococ"),
    var sospitaPell: VarBool = VarBool("Sospita Pell i parts toves"),
    var tipusComorbilitat: VarBool = VarBool("Te Comorbilitat"),
    var condicionsAdicionals: VarBool = VarBool("Condicions Addicionals"),
    var fracasRenal: VarBool = VarBool("Fracàs Renal"),
    var infeccioPiemefritisAguda: VarBool = VarBool("Infeccio Piemefritis Profunda"),
    var infeccioTransmissioSexual: VarBool = VarBool("Orquitis de transmissió sexual"),
    var insuficienciaRenal: VarBool = VarBool("Insuficiència Renal"),
    var cvc: VarString = VarString("CVC"),
    var factorRisc: VarBool = VarBool("Factor de Risc"),
    var tipusCelulitis: VarString = VarString("Tipus Celulitis"),
    var focusAbdominal: VarString = VarString("Focus Abdominal"),
    var infeccioCateter: VarString = VarString("Infecció Catèter"),
    var mes60anys: VarString = VarString(">60 ANYS/IMMUNOSUPRESSIÓ, UDVP/FRMR"),
    var tipusColestitis: VarString = VarString("tipusColestitis"),
    var tipusColangitis: VarString = VarString("tipusColangitis"),
    var tipusBacteriuriaAsiptomatica: VarString = VarString("BACTERIÚRIA ASIMPTOMÀTICA"),
    var tipusInfeccionsUrinariesGestants: VarString = VarString("INFECCIONS URINÀRIES EN GESTANTS"),
    var tipusMeningitis: VarString = VarString("TIPUS MENINGITIS"),
    var tipusCateter: VarString = VarString("TIPUS CATETER"),
    var alergiaPenicilina: VarString = VarString("Al·lèrgia Penicil·lina"),
    var alergiaSevera: VarBool = VarBool("És Al·lèrgia Severa ?"),
    var fg: VarBool = VarBool("Filtrat glomerular <30 ml/min"),
    var postCpre: VarBool = VarBool("post-CPRE"),
    var celulitis: VarBool = VarBool("Celulitis"),
) : Serializable{
    companion object {
        fun getAllVarBools(instance: VariablesGtei): List<VarBool> {
            return instance::class.memberProperties
                .filter { it.returnType.classifier == VarBool::class }
                .mapNotNull { it.get(instance as Nothing) as? VarBool }
        }
        fun getAllVarVariables( ): List<VariablesGtei> {
            val listBol = this.getAllVarBools(VariablesGtei())
            return listBol.map { it as? VariablesGtei }.filterNotNull()
        }
    }
}



data class Screen(
    var name: String = "",
    val focus: String = "",
    var listVar: MutableList<Variable> = mutableListOf(),
    val listScreens: List<Screen> = emptyList(),
    var imageResId: Int = 0,
    val message: String = ""
) : Serializable


fun Map<String, Any>.getVariableValue(nom: String): Boolean =
    (this[nom] as? VarBool)?.valor ?: false
fun Map<String, Any>.getVariableString(nom: String, default: String = ""): String =
    (this[nom] as? VarString)?.valorString ?: default

data class Screens(val variablesGtei: VariablesGtei = VariablesGtei()) {

    internal val respiratori1: Screen = Screen(
        name = "RESPIRATORI NOSOCOMIAL",
        focus = "RESPIRATORI NOSOCOMIAL",
        listVar = mutableListOf(
            variablesGtei.multiResistent,
            variablesGtei.xocSeptic,
            variablesGtei.blee,
            variablesGtei.alergiaPenicilina,
            variablesGtei.sarm,
            variablesGtei.alergiaSevera,
            variablesGtei.fg
        ),
        message = "Nosocomial"
    )

    internal val respiratori2: Screen = Screen(
        name = "RESPIRATORI COMUNITAT",
        focus = "RESPIRATORI COMUNITAT",
        listVar = mutableListOf(
            variablesGtei.sospitaPneumonia,
            variablesGtei.sospitaLegionella,
            variablesGtei.alergiaPenicilina
        ),
        message = "Comunitat"
    )

    internal val respiratori: Screen = Screen(
        name = "RESPIRATORI",
        focus = "RESPIRATORI",
        listScreens = listOf(respiratori1, respiratori2),
        imageResId = R.drawable.image_respiratori,
        message = "Respiratori"
    )

    internal val abdominal1: Screen = Screen(
        name = "ABDOMINAL ALT RISC",
        focus = "ABDOMINAL ALT RISC",
        listVar = mutableListOf(
            variablesGtei.alergiaPenicilina,
            variablesGtei.alergiaSevera,
            variablesGtei.xocSeptic,
            variablesGtei.blee,
            variablesGtei.tractamentAntifungic,
            variablesGtei.fg,
            variablesGtei.marsa
        ),
        message = "Abdominal Alt Risc"
    )
    internal val abdominal2: Screen = Screen(
        name = "ABDOMINAL BAIX RISC",
        focus = "ABDOMINAL BAIX RISC",
        listVar = mutableListOf(
            variablesGtei.alergiaSevera,
            variablesGtei.sarm,
            variablesGtei.blee,
            variablesGtei.alergiaPenicilina
        ),
        message = "Abdominal Baix Risc"
    )
    internal val abdominal3c1c1: Screen = Screen(
        focus = "ABDOMINAL COLECISTITIS",
        name = "GRAU I",
        listVar = mutableListOf(variablesGtei.alergiaPenicilina, variablesGtei.tipusColestitis.copy().apply {
            valorString = "GRAU I"
        }),
        message = "Colestitis Grau I"
    )
    internal val abdominal3c1c2: Screen = Screen(
        focus = "ABDOMINAL COLECISTITIS",
        name = "GRAU II",
        listVar = mutableListOf(variablesGtei.alergiaPenicilina, variablesGtei.tipusColestitis.copy().apply {
            valorString = "GRAU II"
        }),
        message ="Colestitis Grau II"
    )
    internal val abdominal3c1c3: Screen = Screen(
        focus = "ABDOMINAL COLECISTITIS",
        name = "GRAU III",
        listVar = mutableListOf(variablesGtei.alergiaPenicilina, variablesGtei.tipusColestitis.copy().apply {
            valorString = "GRAU III"
        }),
        message = "Colestitis Grau III"
    )

    internal val abdominal3c1c4: Screen = Screen(
        focus = "ABDOMINAL COLECISTITIS",
        name = "XOC SÈPTIC",
        listVar = mutableListOf( variablesGtei.alergiaPenicilina, variablesGtei.tipusColestitis.copy().apply {
            valorString = "GRAU IV"
        }),
        message = "XOC SÈPTIC"
    )
    internal val abdominal3c1: Screen = Screen(
        focus = "ABDOMINAL COLECISTITIS",
        name = "COLECISTITIS",
        listScreens = listOf(abdominal3c1c1, abdominal3c1c2, abdominal3c1c3, abdominal3c1c4),
        message = "Colestitis"
    )
    internal val abdominal3c2l1: Screen = Screen(
        name = "TIPUS I",
        focus = "ABDOMINAL COLANGITIS",
        listVar = mutableListOf(variablesGtei.alergiaPenicilina, variablesGtei.tipusColangitis.copy().apply {
            valorString = "TIPUS I"
        }),
        message = "Colangitis Tipus I"
    )

    internal val abdominal3c2l2: Screen = Screen(
        name = "TIPUS II",
        focus = "ABDOMINAL COLANGITIS",
        listVar = mutableListOf(variablesGtei.alergiaPenicilina, variablesGtei.fg, variablesGtei.postCpre, variablesGtei.tipusColangitis.copy().apply {
            valorString = "TIPUS II"
        }),
        message = "Colangitis Tipus II"
    )

    internal val abdominal3c2l3: Screen = Screen(
        name = "TIPUS III",
        focus = "ABDOMINAL COLANGITIS",
        listVar = mutableListOf(variablesGtei.alergiaPenicilina, variablesGtei.fg, variablesGtei.postCpre, variablesGtei.tipusColangitis.copy().apply {
            valorString = "TIPUS III"
        }),
        message = "Colangitis Tipus III"
    )
    internal val abdominal3c2l4: Screen = Screen(
        name = "XOC SÈPTIC",
        focus = "ABDOMINAL COLANGITIS",
        listVar = mutableListOf(variablesGtei.alergiaPenicilina, variablesGtei.fg, variablesGtei.tipusColangitis.copy().apply {
            valorString = "XOC SÈPTIC"
        }),
        message = "Colangitis Tipus IV"
    )
    internal val abdominal3c2: Screen = Screen(
        focus = "ABDOMINAL COLANGITIS",
        name = "COLANGITIS",
        listScreens = listOf(abdominal3c2l1, abdominal3c2l2, abdominal3c2l3, abdominal3c2l4),
        message = "Colangitis"
    )
    internal val abdominal3c3p1: Screen = Screen(
        name = "PIELEMEFRITIS LLEU",
        focus = "ABDOMINAL PIELEMEFRITIS LLEU",
        listVar = mutableListOf(variablesGtei.celulitis),
        message = "Pielemefritis Lleu"
    )
    internal val abdominal3c3p2: Screen = Screen(
        name = "PIELEMEFRITIS MODERADA/GREU",
        focus = "ABDOMINAL PIELEMEFRITIS MODERADA/GREU",
        listVar = mutableListOf(
            variablesGtei.alergiaPenicilina,
            variablesGtei.alergiaSevera,
            variablesGtei.xocSeptic,
            variablesGtei.blee,
            variablesGtei.tractamentAntifungic,
            variablesGtei.fg,
            variablesGtei.infeccioPiemefritisAguda
        ),
        message = "Pielemefritis Moderada/Greu"
    )
    internal val abdominal3c3: Screen = Screen(
        name = "PIELENEFRITIS",
        focus = "ABDOMINAL PIELENEFRITIS",
        listScreens = listOf(abdominal3c3p1, abdominal3c3p2),
        message = "Pielemefritis",

        )
    internal val abdominal3: Screen = Screen(
        name = "ALTRES",
        focus = "ABDOMINAL ALTRES",
        listScreens = listOf(
            abdominal3c1,
            abdominal3c2,
            abdominal3c3
        ),
        message = "Altres",
    )

    internal val abdominal: Screen = Screen(
        name = "ABDOMINAL",
        focus = "ABDOMINAL",
        listScreens = listOf(
            abdominal1, abdominal2, abdominal3
        ),
        imageResId = R.drawable.image_abdominal,
        message = "Abdominal"
    )

    internal val urologic1b1: Screen = Screen(
        name = "GESTANT",
        focus = "UROLOGIA BACTERIÚRIA ASIMPTOMÀTICA",
        listVar = mutableListOf(variablesGtei.tipusBacteriuriaAsiptomatica.copy().apply {
            valorString = "GESTANT"
        }),
        message = "Gestant"
    )
    internal val urologic1b2: Screen = Screen(
        name = "CIRURGIA PROSTÀTICA",
        focus = "UROLOGIA BACTERIÚRIA ASIMPTOMÀTICA",
        listVar = mutableListOf(variablesGtei.tipusBacteriuriaAsiptomatica.copy().apply {

            valorString = "CIRURGIA PROSTÀTICA"
        }),
        message = "Cirurgia Prostàtica"
    )
    internal val urologic1b3: Screen = Screen(
        name = "MANIPULACIÓ UROLÒGICA \nAMB RISC DE SAGNAT",
        focus = "UROLOGIA BACTERIÚRIA ASIMPTOMÀTICA",
        listVar = mutableListOf(variablesGtei.tipusBacteriuriaAsiptomatica.copy().apply {

            valorString = "MANIPULACIÓ UROLÒGICA AMB RISC DE SAGNAT"
        }),
        message = "Manipulació Urològica"
    )

    internal val urologic1b4: Screen = Screen(
        name = "ALTRES",
        focus = "UROLOGIA BACTERIÚRIA ASIMPTOMÀTICA",
        listVar = mutableListOf(variablesGtei.tipusBacteriuriaAsiptomatica.copy().apply {
            valorString = "ALTRES"
        }),
        message = "Altres"
    )
    internal val urologic1: Screen = Screen(
        name = "UROLOGIA BACTERIÚRIA ASIMPTOMÀTICA",
        focus = "UROLOGIA BACTERIÚRIA ASIMPTOMÀTICA",
        listScreens = listOf(
            urologic1b1,
            urologic1b2,
            urologic1b3,
            urologic1b4
        ),
        message = "Bacteriúria Asimptomàtica"
    )

    internal val urologic2: Screen = Screen(
        name = "CISTITIS COMPLICADA",
        focus = "UROLOGIA CISTITIS COMPLICADA",
        message = "Cistitis Complicada"
    )
    internal val urologic3: Screen = Screen(
        name = "PIELONEFRITIS COMPLICADA/SÈPSIA",
        focus = "UROLOGIA PIELONEFRITIS COMPLICADA/SÈPSIA",
        listVar = mutableListOf(variablesGtei.alergiaPenicilina, variablesGtei.frmr),
        message = "Pielemefritis Complicada/Sèpsia"
    )
    internal val urologic4: Screen = Screen(
        name = "PIELONEFRITIS AGUDA NO COMPLICADA",
        focus = "UROLOGIA PIELONEFRITIS AGUDA NO COMPLICADA",
        listVar = mutableListOf(variablesGtei.alergiaPenicilina),
        message = "Pielemefritis Aguda no Complicada"
    )

    internal val urologic5: Screen = Screen(
        name = "PROSTATITIS",
        focus = "UROLOGIA PROSTATITIS",
        listVar = mutableListOf(variablesGtei.alergiaPenicilina, variablesGtei.frmr),
        message = "Prostatitis"
    )

    internal val urologic6: Screen = Screen(
        name = "ORQUITIS I EPIDIMITIS",
        focus = "UROLOGIA ORQUITIS I EPIDIMITIS",
        listVar = mutableListOf(
            variablesGtei.infeccioTransmissioSexual,
            variablesGtei.alergiaPenicilina,
            variablesGtei.frmr
        ),
        message = "Orquitis i Epidimitis"
    )

    internal val urologic7: Screen = Screen(
        name = "XOC SÈPTIC",
        focus = "UROLOGIA XOC SÈPTIC",
        listVar = mutableListOf(variablesGtei.alergiaPenicilina, variablesGtei.sarm),
        message = "Xoc Sèptic"
    )

    internal val urologic8g1: Screen = Screen(
        name = "BACTERIÚRIA ASIMPTOMÀTICA",
        focus = "UROLOGIA INFECCIONS URINÀRIES EN GESTANTS",
        listVar = mutableListOf(variablesGtei.tipusInfeccionsUrinariesGestants.copy().apply { valorString ="BACTERIÚRIA ASIMPTOMÀTICA GESTANT" }),
        message = "Bacteriúria Asimptomàtica"
    )

    internal val urologic8g2: Screen = Screen(
        name = "CISTITIS AGUDA",
        focus = "UROLOGIA INFECCIONS URINÀRIES EN GESTANTS",
        listVar = mutableListOf(variablesGtei.tipusInfeccionsUrinariesGestants.copy().apply { valorString ="CISTITIS AGUDA GESTANT" }),
        message = "Cistitis Aguda"
    )
    internal val urologic8g3: Screen = Screen(
        name = "PIELONEFRITIS",
        focus = "UROLOGIA INFECCIONS URINÀRIES EN GESTANTS",
        listVar = mutableListOf(
            variablesGtei.alergiaPenicilina,
            variablesGtei.tipusInfeccionsUrinariesGestants.copy().apply { valorString ="PIELONEFRITIS" }),
        message = "Pielonefritis"
    )
    internal val urologic8: Screen = Screen(
        name = "INFECCIONS URINÀRIES",
        focus = "UROLOGIA INFECCIONS URINÀRIES EN GESTANTS",
        listScreens = listOf(urologic8g1, urologic8g2, urologic8g3),
        message = "Infeccions Urinàries"

    )

    internal val urologic: Screen = Screen(
        name = "UROLOGIC",
        focus = "UROLOGIA",
        listScreens = listOf(
            urologic1,
            urologic2,
            urologic3,
            urologic4,
            urologic5,
            urologic6,
            urologic7,
            urologic8
        ),
        imageResId = R.drawable.image_urologic,
        message = "Urologic",

        )

    internal val pellIPartsToves1c1: Screen = Screen(
        name = "CEL.LULITIS LLEU",
        focus = "PELL CEL.LULITIS LLEU",
        listVar = mutableListOf(
            variablesGtei.tipusCelulitis.copy().apply {
                valorString = "CEL.LULITIS LLEU"
            },
            variablesGtei.mossegada, variablesGtei.alergiaPenicilina,
        ),
        message = "Cel.lulitis Lleu"
    )
    internal val pellIPartsToves1c2: Screen = Screen(
        name = "CEL.LULITIS MODERADA/GREU",
        focus = "PELL CEL.LULITIS LLEU",
        listVar = mutableListOf(
            variablesGtei.tipusCelulitis.copy().apply {
                valorString = "CEL.LULITIS MODERADA/GREU"
            },
            variablesGtei.fracasTractament, variablesGtei.alergiaPenicilina,
            variablesGtei.frmr, variablesGtei.xocSeptic
        ),
        message = "Cel.lulitis Moderada/Greu"
    )

    internal val pellIPartsToves1: Screen = Screen(
        name = "CEL.LULITIS LLEU",
        focus = "PELL CEL.LULITIS LLEU",
        listScreens = listOf(pellIPartsToves1c1, pellIPartsToves1c2),
        message = "Cel.lulitis Lleu"
    )

    internal val pellIPartsToves2: Screen = Screen(
        name = "FASCITIS NECROTITZANT",
        focus = "PELL FASCITIS NECROTITZANT",
        listVar = mutableListOf(
            variablesGtei.xocSeptic,
            variablesGtei.alergiaPenicilina,
            variablesGtei.marsa,
            variablesGtei.alergiaSevera,
            variablesGtei.tipusCelulitis,
        ),
        message = "Fascitis Necrotitzant"
    )

    internal val pellIPartsToves3: Screen = Screen(
        name = "INFECCIÓ DE PEU DIABÈTIC",
        focus = "PELL INFECCIÓ DE PEU DIABÈTIC",
        message = "Infecció de Peu Diabètic"
    )

    internal val pellIPartsToves: Screen = Screen(
        name = "PELL I PARTS TOVES",
        focus = "PELL I PARTS TOVES",
        listScreens = listOf(pellIPartsToves1, pellIPartsToves2, pellIPartsToves3),
        imageResId = R.drawable.image_pell_i_parts_toves,
        message = "Pell i Parts Toves"
    )

    internal val neurologic1b1: Screen = Screen(
        name = "NOSOCOMIAL (I POSTQUIRÚRGICA)",
        focus = "NEUROLOGIC MENINGITIS BACTERIANA",
        listVar = mutableListOf(
            variablesGtei.alergiaPenicilina,
            variablesGtei.tipusMeningitis.copy().apply { valorString ="NEUROLOGIA NOSOCOMIAL (I POSTQUIRÚRGICA)" }
        ),
        message = "Nosocomial (i Postquirúrgica)"
    )

    internal val neurologic1b2: Screen = Screen(
        name = "QUALSEVOL EDAT, SENSE COMORBIDITAT SIGNIFICATIVA",
        focus = "NEUROLOGIC MENINGITIS BACTERIANA",
        listVar = mutableListOf(
            variablesGtei.alergiaPenicilina,
            variablesGtei.tipusMeningitis.copy().apply { valorString ="QUALSEVOL EDAT, SENSE COMORBIDITAT SIGNIFICATIVA" },
            variablesGtei.tipusComorbilitat
        ),
        message = "Qualsevol Edat, Sense Comorbiditat Significativa"
    )

    internal val neurologic1b3: Screen = Screen(
        name = "AMB IMMUNOSUPRESSIÓ",
        focus = "NEUROLOGIC MENINGITIS BACTERIANA",
        listVar = mutableListOf(
            variablesGtei.alergiaPenicilina,
            variablesGtei.tipusMeningitis.copy().apply { valorString ="AMB IMMUNOSUPRESSIÓ" },
        ),
        message = "Amb Immunosupressió"
    )

    internal val neurologic1: Screen = Screen(
        name = "SENSE COMORBIDITAT",
        focus = "NEUROLOGIC MENINGITIS BACTERIANA",
        listScreens = listOf(neurologic1b1, neurologic1b2, neurologic1b3),
        message = "Sense Comorbiditat"
    )

    internal val neurologic2: Screen = Screen(
        name = "MENINGITIS VÍRICA",
        focus = "NEUROLOGIC MENINGITIS VÍRICA",
        listVar = mutableListOf(),
        message = "Menigitis Vírica"
    )

    internal val neurologic3: Screen = Screen(
        name = "ENCEFALITIS I SOSPITA CAUSA HERPÈTICA",
        focus = "NEUROLOGIC ENCEFALITIS I SOSPITA CAUSA HERPÈTICA",
        listVar = mutableListOf(),
        message = "Encefalitis i Sospita Causa Herpètica"
    )

    internal val neurologic: Screen = Screen(
        name = "NEUROLOGIC",
        focus = "NEUROLOGIC",
        listScreens = listOf(neurologic1, neurologic2, neurologic3),
        imageResId = R.drawable.image_neurologic,
        message = "Neurologic"
    )

    internal val artritisSeptica1: Screen = Screen(
        name = "ARTRITIS SEPTICA < 60 ANYS \nSENSE COMORBIDITATS",
        focus = "ARTRITIS SEPTICA < 60 ANYS SENSE COMORBIDITATS",
        listVar = mutableListOf(variablesGtei.alergiaPenicilina),
        message = "Artritis Septica < 60 Anys"
    )
    internal val artritisSeptica2: Screen = Screen(
        name = "ARTRITIS SEPTICA  > 60 ANYS \nO COMORBIDITATS",
        focus = "ARTRITIS SEPTICA  > 60 ANYS O COMORBIDITATS",
        listVar = mutableListOf(variablesGtei.alergiaPenicilina),
        message = "Artritis Septica > 60 Anys"
    )

    internal val artritisSeptica: Screen = Screen(
        name = "ARTRITIS SEPTICA",
        focus = "ARTRITIS SEPTICA",
        listScreens = listOf(artritisSeptica1, artritisSeptica2),
        imageResId = R.drawable.image_artritis_septica,
        message = "Artritis Septica",
    )

    internal val endocarditis1: Screen = Screen(
        name = "ENDOCARDITIS VÀLVULA NADIUA/PROTÈSICA TARDANA\n (> 1 ANY)",
        focus = "ENDOCARDITIS VÀLVULA NADIUA/PROTÈSICA TARDANA (> 1 ANY)",
        listVar = mutableListOf(variablesGtei.alergiaPenicilina, variablesGtei.fg),
        message = "Endocarditis Vàlvula Tardana (> 1 any)",
    )
    internal val endocarditis2: Screen = Screen(
        name = "ENDOCARDITIS VÀLVULA NADIUA/PROTÈSICA PRECOÇ \n(< 1 ANY)",
        focus = "ENDOCARDITIS VÀLVULA NADIUA/PROTÈSICA PRECOÇ (< 1 ANY)",
        listVar = mutableListOf(variablesGtei.alergiaPenicilina, variablesGtei.fg),
        message = "Endocarditis Vàlvula Precoç (< 1 any)",
    )

    internal val endocarditis: Screen = Screen(
        name = "ENDOCARDITIS",
        focus = "ENDOCARDITIS",
        listScreens = listOf(endocarditis1, endocarditis2),
        imageResId = R.drawable.image_endocarditis,
        message = "Endocarditis",
    )


    internal val febreNeutropenica: Screen = Screen(
        name = "FEBRE NEUTROPÈNICA",
        focus = "FEBRE NEUTROPENICA",
        listVar = mutableListOf(variablesGtei.alergiaPenicilina, variablesGtei.fg,
            variablesGtei.frmr, variablesGtei.xocSeptic, variablesGtei.blee, variablesGtei.isCvc,
            variablesGtei.isFocusAbdominal, variablesGtei.isKill),
        imageResId = R.drawable.image_febre_neutropenica,
        message = "Febre Neutropènica",
    )



    internal val infeccioDeCateter: Screen = Screen(
        name = "INFECCIÓ DE CATÈTER",
        focus = "INFECCIOCATETER",
        listVar = mutableListOf(variablesGtei.factorRisc, variablesGtei.fg, variablesGtei.frmr, variablesGtei.xocSeptic, variablesGtei.alergiaPenicilina),
        imageResId = R.drawable.image_infeccio_de_cateter,
        message = "Infecció de Catèter",
    )


    internal val sepsisOd: Screen = Screen(
        name = "SEPSIS ORIGÈN DESCONEGUT",
        focus = "SEPSISOD",
        listVar = mutableListOf(variablesGtei.alergiaPenicilina, variablesGtei.xocSeptic),
        imageResId = R.drawable.image_sepsis_od,
        message = "Sepsis Origen Desconegut",
    )

    internal val start: Screen = Screen(
        name = "INICI",
        focus = "INICI",
        listScreens = listOf(
            abdominal, artritisSeptica, endocarditis, febreNeutropenica, infeccioDeCateter, neurologic, pellIPartsToves, respiratori, sepsisOd, urologic,
        ),
        imageResId = R.mipmap.logo_geti,
        message = "Selecciona el Focus de la Infecció",
    )
    /**
     * Finds and returns a copy of the first Screen object that matches the given focus.
     * If there are multiple Screen objects with the same focus, the function merges
     * all the boolean variables from those screens into the first one found.
     *
     * @param focus The focus value to look for among the Screen objects.
     * @return A copy of the first matching Screen object, with boolean variables merged
     *         if there are multiple matches, or null if no match is found.
     */
    internal fun findAndCopyScreenByFocus(focus: String): Screen? {
        // List of all available Screen objects
        val allScreens = listOf(
            respiratori1, respiratori2,
            abdominal1, abdominal2, abdominal3c1c1, abdominal3c1c2, abdominal3c1c3, abdominal3c1c4, abdominal3c1, abdominal3c2l1, abdominal3c2l2,
            abdominal3c2l3, abdominal3c2l4, abdominal3c2, abdominal3c3p1, abdominal3c3p2, abdominal3c3, abdominal3,
            urologic1b1, urologic1b2, urologic1b3, urologic1b4, urologic1, urologic2, urologic3, urologic4, urologic5, urologic6, urologic7, urologic8g1,
            urologic8g2, urologic8g3, urologic8, urologic, pellIPartsToves1, pellIPartsToves1c1, pellIPartsToves1c2, pellIPartsToves2, pellIPartsToves3,
             neurologic1b1, neurologic1b2, neurologic1b3, neurologic1, neurologic2, neurologic3, artritisSeptica1, artritisSeptica2,
            artritisSeptica, endocarditis1, endocarditis2, endocarditis, febreNeutropenica, infeccioDeCateter, sepsisOd

        )

        // Find all the Screen objects that have the given "focus"
        val foundScreens = allScreens.filter { it.focus == focus }

        // If no Screen is found, return null
        if (foundScreens.isEmpty()) return null

        // Take the first Screen found
        val firstScreen = foundScreens.first()

        // Initialize a mutable set to hold all the unique boolean variables from the found screens
        val mergedBooleanVariables = mutableSetOf<Variable>()  // Assuming Variable is the superclass of VarBool

        // Filter and add all the boolean variables from each found Screen
        for (screen in foundScreens) {
            val variablesBool = screen.listVar.filterIsInstance<VarBool>()  // Assuming listVar is a list of Variable
            mergedBooleanVariables.addAll(variablesBool)
        }

        // Create a copy of the first Screen but with the merged boolean variables
        return firstScreen.copy(listVar = mergedBooleanVariables.toList().toMutableList())
    }
}

data class Medication(
    val Index: Int = 0,
    val name: String = "",
    var fg: Boolean = false,
    val sex: Boolean = false,
    val weight: Boolean = false,
    val height: Boolean = false,
    val maxDose: Int = 0
)  {
    companion object {
        internal fun get(code: Int): Medication? {
            return medicationDict[code]
        }

        internal fun getAll(codes: List<Int>): List<Medication?> {
            return codes.map { medicationDict[it] }
        }

        internal fun getAllName(codes: List<Int>): List<String> {
            return codes.map {
                medicationDict[it]?.name ?: ""
            }
        }

        internal val medicationDict = mapOf(
            1 to Medication(1, "doxicilina", false, false, false, false, 0),
            2 to Medication(2, "ampicilina", true, false, false, false, 0),
            3 to Medication(3, "cloxacilina", false, false, false, false, 0),
            4 to Medication(4, "piperacil.lina-Tazobactam", true, false, false, false, 0),
            5 to Medication(5, "amoxicilina-clavulanic", true, false, false, false, 0),
            6 to Medication(6, "cefazolina", true, false, false, false, 0),
            7 to Medication(7, "cefalexina", true, false, false, false, 0),
            8 to Medication(8, "cefuroxima", true, false, false, false, 0),
            9 to Medication(9, "ceftriaxona", false, false, false, false, 0),
            10 to Medication(10, "cefepime", true, false, false, false, 0),
            11 to Medication(11, "aztreonam", true, false, false, false, 0),
            12 to Medication(12, "meropenem", true, false, false, false, 0),
            13 to Medication(13, "azitromicina", true, false, false, false, 0),
            14 to Medication(14, "clindamicina", false, false, false, false, 0),
            15 to Medication(15, "gentamicina", true, true, true, false, 450),
            16 to Medication(16, "amikacina", true, true, true, false, 1500),
            17 to Medication(17, "ciprofloxacina", true, false, false, false, 0),
            18 to Medication(18, "levofloxacino", true, false, false, false, 0),
            19 to Medication(19, "vancomicina", true, true, true, false, 2000),
            20 to Medication(20, "metronidazol", false, false, false, false, 0),
            21 to Medication(21, "fosfomicina", true, false, false, false, 0),
            22 to Medication(22, "fluconazol", true, true, true, true, 0),
            23 to Medication(23, "daptomicina", true, true, true, false, 1000),
            24 to Medication(24, "cefuroxima o ceftriaxona", true, false, false, false, 0),
            25 to Medication(25, "linezolid", false, false, false, false, 0),
            26 to Medication(26, "caspofungina", false, false, false, false, 0),
            27 to Medication(27, "aciclovir", true, true, true, false, 1000),
            28 to Medication(28, "rifampicina", false, false, false, false, 0),
            90 to Medication(90, "Consultar Protocol Pneumonies", false, false, false, false, 0),
            91 to Medication(91, "No tractar", false, false, false, false, 0),
            92 to Medication(92, "Contactar Servei Infeccions", false, false, false, false, 0),
            93 to Medication(93, "Drenatge i neteja de la ferida.", false, false, false, false, 0),
            94 to Medication(94,"Dreneu i cultiveu el pus obtingut de forma estèril. \n\nSi es tracta d'una infecció més profunda, tracteu-la com a infecció intraabdominal nosocomial.",              false,
                false,
                false,
                false,
                0
            ),
            95 to Medication(95, "Tractar segons antibiograma", false, false, false, false, 0),
            96 to Medication(96, "Mirar protocol peu diabètic", false, false, false, false, 0),
            97 to Medication(
                97,
                "Tractament simptomàtic, \nun cop descartat encefalitis herpètica",
                false,
                false,
                false,
                false,
                0
            ),
            999 to Medication(999, "Contactar amb Servei Informatic", false, false, false, false, 0)
        )

    }
}
