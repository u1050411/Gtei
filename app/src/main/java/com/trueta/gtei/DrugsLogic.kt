package com.trueta.gtei

class DrugsLogic(dadesOriginals: Screen) {

    private var focus = dadesOriginals.focus
    private val variablesGtei = VariablesGtei()
    private val dades = dadesOriginals.listVar.filterIsInstance<Variable>().associateBy { it.name }
    val factorRisc = dades.getVariableValue(variablesGtei.factorRisc.name)
    private val tipusCelulitis = dades.getVariableString(variablesGtei.tipusCelulitis.name)
    private val tipusColestitis = dades.getVariableString(variablesGtei.tipusColestitis.name)
    private val tipusColangitis = dades.getVariableString(variablesGtei.tipusColangitis.name)
    private val tipusBacteriuriaAsiptomatica =
        dades.getVariableString(variablesGtei.tipusBacteriuriaAsiptomatica.name)
    private val tipusInfeccionsUrinariesGestants =
        dades.getVariableString(variablesGtei.tipusInfeccionsUrinariesGestants.name)
    private val tipusComorbilitat = dades.getVariableValue(variablesGtei.tipusComorbilitat.name)
    private val tipusMeningitis = dades.getVariableString(variablesGtei.tipusMeningitis.name)
    private val cvc = dades.getVariableString(variablesGtei.cvc.name)
    private val mrsa = dades.getVariableValue(variablesGtei.mrsa.name)
    private val alergiaPenicilina =
        !dades.getVariableString(variablesGtei.alergiaPenicilina.name).contains("No")
    private val alergiaPenicilinaSevera =
        dades.getVariableString(variablesGtei.alergiaPenicilina.name).contains("Severa")
    private val sospitaPneumonia = dades.getVariableValue(variablesGtei.sospitaPneumonia.name)
    private val xocSeptic = dades.getVariableValue(variablesGtei.xocSeptic.name)
    private val alergiaPenicilinaString = dades.getVariableString(variablesGtei.alergiaPenicilina.name)
    private val blee = dades.getVariableValue(variablesGtei.blee.name)
    private var sarm = dades.getVariableValue(variablesGtei.sarm.name)
    private val sospitaLegionella = dades.getVariableValue(variablesGtei.sospitaLegionella.name)
    private val infeccioTransmissioSexual =
        dades.getVariableValue(variablesGtei.infeccioTransmissioSexual.name)
    private val mossegada = dades.getVariableValue(variablesGtei.mossegada.name)
    private val fracasTractament = dades.getVariableValue(variablesGtei.fracasTractament.name)
    private val multiResistent = dades.getVariableValue(variablesGtei.multiResistent.name)
    private val frmr = dades.getVariableValue(variablesGtei.frmr.name)
    private val marsa = dades.getVariableValue(variablesGtei.marsa.name)
    private val focusAbdominal = dades.getVariableValue(variablesGtei.focusAbdominal.name)
    private val infeccioPiemefritisAguda =
        dades.getVariableValue(variablesGtei.infeccioPiemefritisAguda.name)
    private val fg = dades.getVariableValue(variablesGtei.fg.name)
    private val postCPRE = dades.getVariableValue(variablesGtei.postCpre.name)


    fun tractamentInfeccio(): List<Int> {
        return when {
            focus.contains("SEPSISOD") -> tractamentSepsiaOdc()
            focus.contains("RESPIRATORI") -> tractamentRespiratori()
            focus.contains("ABDOMINAL") -> tractamentAbdominal()
            focus.contains("ENDOCARDITIS") -> tractamentEndocarditis()
            focus.contains("NEUROLOGIC") -> tractamentNeurologic()
            focus.contains("UROLOGIA") -> tractamentUrologic()
            focus.contains("PELL") -> tractamentPell()
            focus.contains("ARTRITIS SEPTICA") -> tractamentArtritisSeptica()
            focus.contains("FEBRE NEUTROPENICA") -> tractamentFebreNeutropenica()
            focus.contains("CATETER") -> tractamentInfeccioCateter()
            else -> listOf(999) // Contactar Servei Informàtic
        }
    }

    private fun tractamentRespiratori(): List<Int> {
        val baseList = if (fg) 25 else 19
        return when (focus) {
            "RESPIRATORI NOSOCOMIAL" -> when {
                alergiaPenicilinaString == "Severa" -> listOf(92)
                xocSeptic -> when {
                    blee -> listOf(12, 16) + baseList
                    alergiaPenicilinaString == "Sí" && multiResistent && sarm -> listOf(
                        12,
                        18,
                        21
                    )

                    alergiaPenicilinaString == "Sí" && multiResistent -> listOf(12, 17, 20)
                    alergiaPenicilinaString == "Sí" && sarm -> listOf(12, 17, 20)
                    else -> listOf(4, 16, 25)
                }

                sarm -> when {
                    multiResistent && blee -> listOf(12, 19)
                    multiResistent -> listOf(4, 16) + baseList
                    else -> listOf(4, 19)
                }

                blee -> listOf(12)
                multiResistent && alergiaPenicilinaString == "Sí" -> listOf(12)
                multiResistent -> listOf(4, 16)
                alergiaPenicilinaString == "Sí" -> listOf(18)
                else -> listOf(4)
            }

            "RESPIRATORI COMUNITAT" -> {
                // Handle community-based respiratory conditions
                when {
                    sospitaLegionella -> listOf(90) // Consultar Protocol Pneumonies
                    alergiaPenicilina -> listOf(18) + if (sospitaPneumonia) listOf(14) else emptyList() // levofloxacino, clindamicina
                    sospitaPneumonia -> listOf(5) // amoxicilina-clavulanic
                    else -> listOf(9, 13) // ceftriaxona, azitromicina
                }
            }

            else -> listOf(5) // Contactar Servei Informàtic
        }
    }

    private fun tractamentAbdominal(): List<Int> {
        val baseList = if (sarm) listOf(19) else emptyList()
        return when (focus) {
            "ABDOMINAL BAIX RISC" -> when (alergiaPenicilinaString) {
                "Severa" -> when {
                    blee -> listOf(92)
                    sarm -> listOf(11, 19, 20)
                    else -> listOf(17, 19, 20)
                }

                "Sí" -> when {
                    blee -> listOf(12, 19)
                    else -> listOf(17, 20) + baseList
                }

                else -> when {
                    blee -> listOf(12) + baseList
                    else -> listOf(9, 20) + baseList
                }
            }

            "ABDOMINAL ALT RISC" -> {
                val baseListAbdominal1 =
                    (if (mrsa || xocSeptic) (if (fg) listOf(23) else listOf(19)) else emptyList()) //  daptomicina or vancomicina,
                when (alergiaPenicilinaString) {
                    "Severa" -> when {
                        xocSeptic || blee -> listOf(92)
                        else -> when {
                            factorRisc -> listOf(
                                11,
                                20
                            ) + (if (xocSeptic) listOf(26) else listOf(22)) + baseListAbdominal1 // aztreonam, metronidazol, fluconazol,  daptomicina or vancomicina,
                            else -> listOf(17, 19, 20) // ceftriaxona, amikacina, vancomicina
                        }
                    }

                    "Sí" -> when {
                        (xocSeptic || blee) -> listOf(12) + baseListAbdominal1 + (if (factorRisc) listOf(
                            26
                        ) else listOf(22)) // meropenem, daptomicina or vancomicina, caspofungina or fluconazol
                        else -> listOf(
                            11,
                            20
                        ) + baseListAbdominal1 + (if (factorRisc) listOf(26) else listOf(22)) // aztreonam, metronidazol,  daptomicina or vancomicina, caspofungina or fluconazol
                    }

                    else -> when {
                        (xocSeptic || blee) -> listOf(12) + baseListAbdominal1 + (if (factorRisc) listOf(
                            26
                        ) else listOf(22)) // meropenem, daptomicina or vancomicina, caspofungina or fluconazol
                        else -> listOf(
                            11,
                            20
                        ) + baseListAbdominal1 + (if (factorRisc) listOf(26) else listOf(22)) // aztreonam, metronidazol,  daptomicina or vancomicina, caspofungina or fluconazol
                    }
                }
            }

            else -> when {
                focus.contains("COLECISTITIS") -> when (tipusColestitis) {
                    "GRAU I" -> listOf(if (alergiaPenicilina) 11 else 9)
                    "GRAU II" -> listOf(if (alergiaPenicilina) 11 else 9, 20)
                    "GRAU III" -> when (alergiaPenicilinaString) {
                        "Severa" -> listOf(17, 20)
                        "Sí" -> listOf(12)
                        else -> listOf(10, 20)
                    }

                    else -> {

                            when (alergiaPenicilinaString) {
                                "Severa" -> listOf(16, 17, 19, 20)
                                else -> listOf(12, 19)
                            }

                    }
                }

                focus.contains("COLANGITIS") -> when (tipusColangitis) {
                    "TIPUS I" -> when (alergiaPenicilinaString) {
                        "Severa" -> listOf(16, 17) //
                        "Sí" -> listOf(12)
                        else -> listOf(4)
                    }

                    "TIPUS II" -> when (alergiaPenicilinaString) {
                        "Severa" -> listOf(16, 17) + (if (postCPRE) if (fg) listOf(25) else listOf(
                            19
                        ) else emptyList()) // meropenem, amikacina, vancomicina
                        "Sí" -> listOf(12, 19)
                        else -> listOf(4, 19)
                    }

                    "TIPUS III" -> listOf(16) + (if (alergiaPenicilinaString == "Severa") listOf(17) else listOf(
                        12
                    )) + (if (postCPRE) if (fg) listOf(25) else listOf(19) else emptyList())

                    else -> {
                        val baseList2 =
                            if (fg) listOf(25) else listOf(19) // linezolid or vancomicina

                            when (alergiaPenicilinaString) {
                                "Severa" -> listOf(
                                    11,
                                    16
                                ) + baseList2 // aztreonam, amikacina, linezolid or vancomicina
                                else -> listOf(
                                    11,
                                    12
                                ) + baseList2 // aztreonam, meropenem, linezolid or vancomicina

                        }
                    }
                }


                focus.contains("PIELEMEFRITIS LLEU") ->
                    if ((tipusCelulitis != "No")&&(tipusCelulitis != ""))
                        listOf(3) // Choose cloxacilina if tipusCelulitis is not "No"
                    else
                        listOf(92) // Otherwise, contact infectious disease service

                focus.contains("PIELEMEFRITIS MODERADA/GREU") ->
                    if (infeccioPiemefritisAguda)
                        listOf(93) // Drain and clean the wound for acute pyelonephritis
                    else {
                        focus = "ABDOMINAL ALT RISC" // Re-assign focus to high-risk abdominal
                        tractamentAbdominal() // Treat as nosocomial intraabdominal infection
                    }

                // ... you can add other focus-based conditions here
                else -> listOf()  // Return an empty list or throw an exception if focus is not expected
            }

        }
    }


    private fun tractamentUrologic(): List<Int> {
        val baseList = if (frmr) listOf(16) else emptyList() // amikacina
        return when (focus) {
            "UROLOGIA BACTERIÚRIA ASIMPTOMÀTICA" -> when (tipusBacteriuriaAsiptomatica) {
                "GESTANT", "CIRURGIA PROSTÀTICA", "MANIPULACIÓ UROLÒGICA AMB RISC DE SAGNAT" -> listOf(
                    95
                ) // "Tractar segons antibiograma"
                else -> listOf(96) // "No tractar"
            }

            "UROLOGIA CISTITIS COMPLICADA" -> listOf(21) // fosfomicina trometamol

            "UROLOGIA PIELONEFRITIS COMPLICADA/SÈPSIA" -> when {
                alergiaPenicilina -> listOf(11) + baseList // aztreonam i amikacina
                else -> listOf(9) + baseList // ceftriaxona
            }

            "UROLOGIA PIELONEFRITIS AGUDA NO COMPLICADA" -> when {
                alergiaPenicilina -> listOf(17) // ciprofloxacino
                else -> listOf(8) // cefuroxima
            }

            "UROLOGIA PROSTATITIS" -> when {
                alergiaPenicilina -> listOf(17) // ciprofloxacino
                else -> listOf(9) + baseList // ceftriaxona i amikacina
            }

            "UROLOGIA ORQUITIS I EPIDIMITIS" -> when {
                alergiaPenicilina -> listOf(18) // levofloxacino
                else -> {
                    if (infeccioTransmissioSexual) listOf(1, 9) // doxiciclina, ceftriaxona
                    else listOf(24) // cefuroxima o ceftriaxona
                }
            }

            "UROLOGIA XOC SÈPTIC" -> when {
                alergiaPenicilinaString == "Severa" -> listOf(11) + (if (sarm) listOf(19) else emptyList()) // aztreonam i vancomicina
                else -> listOf(
                    12,
                    16
                ) + (if (sarm) listOf(19) else emptyList()) // meropenem i amikacina i vancomicina
            }

            "UROLOGIA INFECCIONS URINÀRIES EN GESTANTS" -> when (tipusInfeccionsUrinariesGestants) {
                "BACTERIÚRIA ASIMPTOMÀTICA GESTANT" -> listOf(95) // Tracteu segons antibiograma
                "CISTITIS AGUDA GESTANT" -> listOf(21) // fosfomicina trometamol
                "PIELONEFRITIS" -> if (alergiaPenicilina) listOf(11) else listOf(9) // aztreonam or ceftriaxona
                else -> listOf(999) // Contactar Servei Informàtic
            }

            else -> listOf(999) // Contactar Servei Informàtic
        }
    }

    private fun tractamentPell(): List<Int> {
        return when (focus) {
            "PELL CEL.LULITIS LLEU" -> when (tipusCelulitis) {
                "CEL.LULITIS LLEU" -> when {
                    mossegada -> listOf(5) // amoxicilina-clavulanic
                    alergiaPenicilina -> listOf(14) // clindamicina
                    else -> listOf(7) // cefalexina
                }

                "CEL.LULITIS MODERADA/GREU" -> when {
                    alergiaPenicilina || marsa -> listOf(14) // clindamicina
                    fracasTractament -> listOf(5) // amoxicilina-clavulanic
                    xocSeptic && frmr -> listOf(12) + if (fg) listOf(25) else listOf(19) // meropenem, linezolid or vancomicina
                    xocSeptic -> listOf(4, 19) // pipertazo, vancomicina
                    else -> listOf(3) // cloxacilina
                }

                else -> listOf(999) // Contactar Servei Informàtic
            }

            "PELL FASCITIS NECROTITZANT" -> when {
                xocSeptic || (alergiaPenicilina && marsa) -> listOf(
                    12,
                    14,
                    19
                ) // meropenem, clindamicina, vancomicina
                alergiaPenicilinaSevera -> listOf(92) // Contactar Servei Infeccions
                alergiaPenicilina -> listOf(12, 14) // meropenem, clindamicina
                marsa -> listOf(4, 14, 19) // pipertazo, clindamicina, vancomicina
                else -> listOf(4, 14) // pipertazo, clindamicina
            }

            "PELL INFECCIÓ DE PEU DIABÈTIC" -> listOf(97) // "Mirar protocol peu diabètic"
            else -> listOf(999) // Contactar Servei Informàtic
        }
    }


    private fun tractamentNeurologic(): List<Int> {
        return when (focus) {
            "NEUROLOGIC MENINGITIS BACTERIANA" -> when (tipusMeningitis) {
                "NEUROLOGIA NOSOCOMIAL (I POSTQUIRÚRGICA)" -> when {
                    alergiaPenicilinaSevera -> listOf(11, 19) // aztreonam, vancomicina
                    else -> listOf(12, 19) // meropenem, vancomicina
                }

                "QUALSEVOL EDAT, SENSE COMORBIDITAT SIGNIFICATIVA" -> when {
                    alergiaPenicilinaSevera -> listOf(92) // Contactar Servei Infeccions
                    alergiaPenicilina -> listOf(12) // meropenem
                    tipusComorbilitat -> listOf(9, 2) // ceftriaxona, ampicilina
                    else -> listOf(9) // ceftriaxona
                }

                "AMB IMMUNOSUPRESSIÓ" -> when {
                    alergiaPenicilinaSevera -> listOf(92) // Contactar Servei Infeccions
                    alergiaPenicilina -> listOf(12) // meropenem
                    else -> listOf(12, 2) // meropenem, ampicilina
                }

                else -> listOf(999) // Contactar Servei Informàtic
            }

            "NEUROLOGIC ENCEFALITIS I SOSPITA CAUSA HERPÈTICA" -> listOf(27) // Aciclovir
            "NEUROLOGIC MENINGITIS VÍRICA" -> listOf(97) // “Tractament simptomàtic, un cop descartat encefalitis herpètica”
            else -> listOf(999) // Contactar Servei Informàtic
        }
    }

    private fun tractamentArtritisSeptica(): List<Int> {
        return when (focus) {
            "ARTRITIS SEPTICA < 60 ANYS SENSE COMORBIDITATS" -> when (alergiaPenicilinaString) {
                "Severa" -> listOf(19, 17) // vancomicina, ciprofloxacino
                "Sí" -> listOf(6, 15) // cefazolina, gentamicina
                else -> listOf(9, 3) // ceftriaxona, cloxacil·lina
            }

            "ARTRITIS SEPTICA  > 60 ANYS O COMORBIDITATS" -> when {
                alergiaPenicilinaString == "Severa" -> listOf(
                    11,
                    15,
                    23
                ) // aztreonam, gentamicina, daptomicina
                fg -> listOf(23) // daptomicina
                else -> listOf(19) // vancomicina
            }

            else -> listOf(999) // Contactar Servei Informàtic
        }
    }


    private fun tractamentEndocarditis(): List<Int> {
        return when (focus) {
            "ENDOCARDITIS VÀLVULA NADIUA/PROTÈSICA TARDANA (> 1 ANY)" -> {
                return when (alergiaPenicilinaString) {
                    "Severa", "Sí" -> listOf(15) + (if (fg) 23 else 19) // gentamicina, daptomicina or vancomicina
                    "No" -> listOf(
                        1,
                        2
                    ) + (if (fg) 9 else 15) // ampicilina , cloxacilina cefriaxona or gentamicina
                    else -> listOf(999) // Contactar Servei Informàtic
                }
            }

            "ENDOCARDITIS VÀLVULA NADIUA/PROTÈSICA PRECOÇ (< 1 ANY)" -> listOf(28) + (if (fg) listOf(
                9,
                23
            ) else listOf(15, 19)) // rifampicina, daptomicina or gentamicina, vancomicina
            else -> listOf(999) // Contactar Servei Informàtic
        }
    }

    private fun tractamentFebreNeutropenica(): List<Int> {
        val isCvc = cvc in listOf("SOSPITA PELL I PART TOVES")
        return when (alergiaPenicilinaString) {
            "Severa" -> listOf(
                11,
                19,
                20
            ) + (if (frmr || xocSeptic) listOf(16) else emptyList()) // aztreonam, vancomicina, metronidazol, amikacina
            "Si" -> listOf(
                12,
                16
            ) + (if (isCvc || xocSeptic) listOf(19) else emptyList())// meropenem, amikacina i  vancomicina
            "No" -> when {
                (isCvc || xocSeptic) && focusAbdominal -> listOf(
                    10,
                    16,
                    19,
                    20
                ) // cefepime, amikacina, vancomicina, metronidazol
                (isCvc || xocSeptic) -> listOf(10, 16, 19) // cefepime, amikacina, vancomicina
                else -> listOf(10, 16) // cefepime, amikacina
            }

            else -> listOf(999) // Contactar Servei Informàtic
        }
    }

    private fun tractamentInfeccioCateter(): List<Int> {
        val baseList = if (fg) 23 else 19
        return if (factorRisc) listOf(baseList)
        else when (alergiaPenicilinaString) {
            "Severa" -> listOf(11) + (if (frmr) listOf(16) else emptyList()) + baseList
            "Sí" -> listOf(12) + baseList
            "No"-> (if (frmr) listOf(12) else listOf(10)) + baseList
            else -> listOf(999) // Contactar Servei Informàtic
        }
    }

    private fun tractamentSepsiaOdc(): List<Int> {
        val baseList =
            if (xocSeptic) listOf(16, 19) else listOf(16)  // amikacina, vancomicina or vancomicina
        return when (alergiaPenicilinaString) {
            "Severa" -> listOf(
                11,
                20
            ) + baseList  // aztreonam, vancomicina, metronidazol // amikacina, vancomicina or vancomicina
            "Sí", "No" -> listOf(12) + baseList  // meropenem  // amikacina, vancomicina or vancomicina
            else -> listOf(999)  // Contactar Servei Informàtic
        }
    }
}


