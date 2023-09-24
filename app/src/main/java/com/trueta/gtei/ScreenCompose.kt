package com.trueta.gtei

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.trueta.gtei.ui.theme.GteiTheme
import kotlin.math.round

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.launch

/**
 * ScreensGtei Composable: This function observes showScreenTry and viewModel.
 * Depending on the value of showScreenTry, it displays different Composables.
 *
 * @param showScreenTry The MutableState that controls the visibility of ScreensGtei.
 * @param viewModel The ViewModel that contains the StateFlow for selectedScreen and nextScreen.
 */
@Composable
fun ScreensGtei(showScreenTry: MutableState<Boolean>, viewModel: ScreensViewModel) {
    if (showScreenTry.value) {
        ScreenTry(viewModel)
    } else {
        ScreenStart()
    }
}

@Preview
@Composable
fun ScreenStart() {
    val padding = 32.dp
    DisposableEffect(Unit) {
        // Código para ejecutar cuando el composable entra en la composición
        Log.d("ScreenStart", "Recomponiendo debido a cambio en selectedScreen")

        onDispose {
            Log.d("ScreenStart", "Composable ha sido destruido")
        }
    }
    GteiTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary),
            verticalArrangement = Arrangement.Bottom
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                // Primera columna (Textos)
                Column(
                    modifier = Modifier
                        .fillMaxHeight(0.8f)
                        .padding(top = padding),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top
                ) {
                    // Título y subtitulo
                    Text(
                        text = "GUIA DE TRACTAMENT EMPÍRIC DE LES INFECCIONS",
                        style = TextStyle(
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        ),
                        modifier = Modifier.padding(start = padding)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "\nHOSPITAL UNIVERSITARI DE GIRONA DOCTOR JOSEP TRUETA. EQUIP PROA",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Light,
                            color = Color.White
                        ),
                        modifier = Modifier.padding(start = padding)
                    )
                }

                // Segunda columna (Imagen en el fondo)

                Column(
                    modifier = Modifier
                        .fillMaxHeight(0.2f)
                        .align(Alignment.BottomCenter) // Esto coloca la columna en la parte inferior
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxHeight(0.5f)
                            .align(Alignment.Start) // Esto coloca la columna en la parte inferior
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_trueta_generalitat),
                            contentDescription = "Logo Generalitat",
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(start = padding)
                        )
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxHeight(0.5f)
                            .align(Alignment.End) // Esto coloca la columna en la parte inferior
                    ) {}
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewScreeTry() {
    val viewModel: ScreensViewModel = viewModel()
    ScreenTry(viewModel = viewModel)
}

@Composable
fun ScreenTry(viewModel: ScreensViewModel) {

    DisposableEffect(Unit) {
        // Código para ejecutar cuando el composable entra en la composición
        Log.d("ScreenTry", "Recomponiendo debido a cambio en selectedScreen")

        onDispose {
            Log.d("ScreenTry", "Composable ha sido destruido")
        }
    }
    GteiTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary),
            verticalArrangement = Arrangement.Bottom
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(5.dp)
                    .weight(.095f),
                contentAlignment = Alignment.BottomCenter,
            ) {
                MessageDisplay(viewModel)
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(.9f)
                    .background(Color.White)
                    .padding(
                        start = 5.dp,
                        end = 5.dp,
                        top = 5.dp,
                        bottom = 5.dp
                    ), // padding right and left, top and bottom
                contentAlignment = Alignment.TopCenter
            ) {
                TryDisplay(viewModel)  // Pass viewModel as an argument
            }
        }
    }
}

/**
 * TryDisplay Composable: This function observes selectedScreen and nextScreen from the ViewModel.
 * Depending on the value of nextScreen, it displays different Composables.
 *
 * @param viewModel The ViewModel that contains the StateFlow for selectedScreen and nextScreen.
 */
@Composable
fun TryDisplay(viewModel: ScreensViewModel) {
    // Observing StateFlow from ViewModel
    val selectedScreen by viewModel.selectedScreen.collectAsState()
    val nextScreen by viewModel.nextScreen.collectAsState()

    BackHandler {
        viewModel.onBackPress()
    }

    // Display Composables based on the value of nextScreen
    when (nextScreen) {
        "Try" -> {
            // Display buttons based on the list of screens
            selectedScreen?.let { ButtonDisplay(it.listScreens, viewModel = viewModel) }
        }

        "CheckBox" -> {
            // Display checkboxes, but only if selectedScreen is not null
            selectedScreen?.let { CheckboxesDisplay(screen = it, viewModel = viewModel) }
        }

        "Slider" -> {
            // Display sliders, but only if selectedScreen is not null
            selectedScreen?.let { SliderDisplay(screen = it, viewModel = viewModel) }
        }

        "Resultat" -> {
            // Display results, but only if selectedScreen is not null
            selectedScreen?.let { ScreenResult(screen = it, viewModel = viewModel) }
        }
        "OnSubmit" -> {
            // Display results, but only if selectedScreen is not null
            selectedScreen?.let { viewModel.onSubmit(it) }
        }
    }
}

@Preview
@Composable
fun PreviewMessage() {
    val viewModel: ScreensViewModel = viewModel()
    MessageDisplay(viewModel = viewModel)
}

@Composable
fun MessageDisplay(viewModel: ScreensViewModel) {
    val message by viewModel.message.collectAsState()

    Text(
        text = message,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.padding(bottom = 20.dp),
    )
    Divider(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .height(1.dp),

        )
    Divider(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 2.dp)
            .height(1.dp),
    )
}

@Composable
fun ButtonDisplay(listScreen: List<Screen>, viewModel: ScreensViewModel) {
    LazyColumn {
        items(listScreen.size) { index ->
            FunctionButton(listScreen[index], viewModel = viewModel)
        }
    }
}

@Composable
fun FunctionButton(screen: Screen, viewModel: ScreensViewModel) {
    OutlinedButton(
        onClick = { viewModel.onScreenSelected(screen) },
        shape = RoundedCornerShape(0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 2.dp)
            .border(1.dp, MaterialTheme.colorScheme.secondary)
    ) {
        FunctionButtonContent(screen)
    }
}

@Composable
fun FunctionButtonContent(screen: Screen?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        screen?.imageResId?.let { resId ->
            Image(
                painter = painterResource(id = resId),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(shape = RectangleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
        }
        Text(
            text = screen?.name.orEmpty(),
            color = MaterialTheme.colorScheme.primary,
            fontSize = 19.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview
@Composable
fun PreviewCheckboxes() {
    val viewModel: ScreensViewModel = viewModel()
    val screen = viewModel.screensGtei.respiratori1
    CheckboxesDisplay(screen = screen, viewModel = viewModel)
}

@Composable
fun CheckboxesDisplay(screen: Screen, viewModel: ScreensViewModel) {
    viewModel.initializeSwitches(screen)
    val switches = viewModel.switchesPublic
    val listVariables = screen.listVar


    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {

            items(listVariables.size) { index ->  // Use the size of listVariables
                val variable =
                    listVariables[index] as Variable// Get the item from the list by index
                val nameVariable = variable.name
                if (switches != null) {
                    if (switches.containsKey(nameVariable)) {
                        MultiSelectButton(
                            nameVariable = nameVariable,
                            viewModel = viewModel
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                    }
                }
            }
        }

        OutlinedButton(
            onClick = { viewModel.onSubmit(screen) },  // Simplified with non-null screen
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Enviar", color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun MultiSelectButton(
    nameVariable: String,
    viewModel: ScreensViewModel,
    ) {
    val alergiaSeveraName = VariablesGtei().alergiaSevera.name
    val alergiaPenicilinaName = VariablesGtei().alergiaPenicilina.name

    val AlergiaSeveraCheckedFlow =
        (viewModel.isCheckboxChecked(alergiaSeveraName)?.collectAsState())
    val CheckedFlow = (viewModel.isCheckboxChecked(nameVariable)?.collectAsState())

    val isAlergiaSeveraCheckedFlow = AlergiaSeveraCheckedFlow?.value == true
    val isCheckedFlow = CheckedFlow?.value == true

    var label = when {
        isAlergiaSeveraCheckedFlow && isCheckedFlow && nameVariable == alergiaPenicilinaName -> {
            "Al·lèrgia Penicil·lina Greu"
        }
        isAlergiaSeveraCheckedFlow && nameVariable == alergiaPenicilinaName -> {
            viewModel.toggleCheckboxState(alergiaSeveraName)
            viewModel.toggleCheckboxState(nameVariable)
            "Al·lèrgia Penicil·lina Lleu"
        }
        else -> {
            nameVariable
        }
    }

    if (nameVariable != alergiaSeveraName) {

        CheckboxOptionRow(
            label = label,
            isCheckedFlow = isCheckedFlow,
            onCheckedChange = { viewModel.toggleCheckboxState(nameVariable) }
        )
    }

    // Additional logic for alergiaSevera
    if (nameVariable == alergiaPenicilinaName && isCheckedFlow && !isAlergiaSeveraCheckedFlow) {
        CheckboxOptionRow(
            label = alergiaSeveraName,
            isCheckedFlow = isAlergiaSeveraCheckedFlow,
            onCheckedChange = { viewModel.toggleCheckboxState(alergiaSeveraName) }
        )
    }
}

@Composable
fun CheckboxOptionRow(
    label: String,
    isCheckedFlow: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    val colors = MaterialTheme.colorScheme
    val backgroundColor = if (isCheckedFlow) colors.primary else colors.secondary
    val valAlpha = if (isCheckedFlow) 1f else 0f

    OutlinedButton(
        onClick = { onCheckedChange(!isCheckedFlow) },
        shape = RoundedCornerShape(0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(bottom = 2.dp)
            .border(15.dp, backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 4.dp
                ),  // Adjust padding to align with other Row
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start  // Same as other Row
        ) {

            Text(
                text = label,
                fontSize = 19.sp,  // Same as other Row
                textAlign = TextAlign.Start,  // Same as other Row
                color = MaterialTheme.colorScheme.background,  // Same color as other Row
                modifier = Modifier.weight(1f)  // Take up remaining space, same as other Row
            )
            Spacer(modifier = Modifier.width(16.dp))  // Same width as other Row
            Checkbox(
                checked = isCheckedFlow,
                onCheckedChange = onCheckedChange,
                enabled = enabled,
                modifier = Modifier.alpha(valAlpha)  // Hide the Checkbox
            )


        }

    }
}

@Preview
@Composable
fun SliderCheckboxes() {
    val viewModel: ScreensViewModel = viewModel()
    val screen = viewModel.screensGtei.respiratori1
    SliderDisplay(screen = screen, viewModel = viewModel)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SliderDisplay(screen: Screen, viewModel: ScreensViewModel) {
    // Initialize switches and get current values
    viewModel.initializeSwitches(screen)
    val currentSlider = viewModel.medication ?: return
    val currentGender = viewModel.sexVar.value
    val isMen = (currentGender == Gender.Men)
    val slidersDataRange = viewModel.initializeRangeSlice(
        currentSlider,
        isMen,
        screen.listVar.contains(VariablesGtei().fg)
    )

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (isMen) {
                stickyHeader { GenderSelector(viewModel) }
            }
            items(slidersDataRange.size) { index ->
                val data = slidersDataRange[index]
                SliderNumeric(data = data, viewModel = viewModel)
                drawLines(count = 2, colorLine = MaterialTheme.colorScheme.primary)
            }
        }
        OutlinedButton(
            onClick = { viewModel.onSubmitSlice(screen) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Enviar", color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun GenderSelector(viewModel: ScreensViewModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
    ) {
        Text(
            text = "Selecciona el Gènere del Pacient",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(
            modifier = Modifier
                .height(16.dp)
                .fillMaxWidth()
        )
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            RadioButtonOption("Home", Gender.Men, viewModel)
            Spacer(modifier = Modifier.width(10.dp))
            RadioButtonOption("Dona", Gender.Women, viewModel)
        }
    }
    drawLines(count = 2, colorLine = MaterialTheme.colorScheme.primary)
}

@Composable
fun RadioButtonOption(label: String, gender: Gender, viewModel: ScreensViewModel) {
    val takeGender = (gender == viewModel.sexVar.value)
    val isSelected = takeGender

    Row(
        Modifier.selectable(
            selected = (takeGender),
            onClick = { viewModel.updateGender(gender) }
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = (takeGender),
            onClick = { viewModel.updateGender(gender) },
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary,
                unselectedColor = MaterialTheme.colorScheme.secondary
            )
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Change the color of the text based on the selection
        val textColor = if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.secondary
        }
        Text(text = label, color = textColor, fontWeight = FontWeight.Bold)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SliderNumeric(data: RangeSlice, viewModel: ScreensViewModel) {

    val context = LocalContext.current
    var sliderValue by remember { mutableStateOf(data.initialValue) }
    var valorInitial by remember { mutableStateOf(0f) }
    val sex = viewModel.sexVar.value
    var showDialog by remember { mutableStateOf(false) }
    var dialogValue by remember { mutableStateOf(TextFieldValue("")) }
    var showErrorDialog by remember { mutableStateOf(false) }
    val name = context.getString(data.name)


    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(showDialog) {
        if (showDialog) {
            dialogValue = TextFieldValue("")
            focusRequester.requestFocus()
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = name,
                        fontSize = 25.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            },
            text = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    TextField(
                        value = dialogValue,
                        onValueChange = { dialogValue = it },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        ),
                        textStyle = TextStyle(
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.secondary
                        ),
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .width(130.dp)
                    )
                }
            },

            confirmButton = {
                TextButton(onClick = {
                    val newSliderValue = dialogValue.text.toFloatOrNull()
                    if (newSliderValue != null && newSliderValue in data.range.first.toFloat()..data.range.last.toFloat()) {
                        sliderValue = newSliderValue
                        data.valueCallback(sliderValue)
                        showDialog = false
                    } else {
                        sliderValue = valorInitial
                        data.valueCallback(sliderValue)
                        showErrorDialog = true
                    }
                }) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Error") },
            text = { Text("El valor introduit esta fora del rang (${data.range.first} - ${data.range.last}).") },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) {
                    Text("Aceptar")
                }
            }
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = name,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { showDialog = true }
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "${sliderValue.toInt()} ${context.getString(data.unit)}",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.clickable { showDialog = true }
        )

        Spacer(Modifier.height(16.dp))

        Slider(
            value = sliderValue,
            onValueChange = { newValue ->
                val roundedValue = round(newValue)
                sliderValue = roundedValue
                data.valueCallback(roundedValue)
            },
            valueRange = data.range.first.toFloat()..data.range.last.toFloat()
        )
    }
}



@Preview
@Composable
fun SliderResult() {
    val viewModel: ScreensViewModel = ScreensViewModel()
    val screen = viewModel.screensGtei.respiratori1
    ScreenResult(screen = screen, viewModel = viewModel)
}
@Composable
        /**
         * ScreenResult composable shows the list of medications and provides an option to reset.
         *
         * @param screen Screen data (not used in this example).
         * @param viewModel ViewModel that provides the list of medications and text sizes.
         */
fun ScreenResult(screen: Screen, viewModel: ScreensViewModel) {
    val context = LocalContext.current // Get the current context
    val medicamentList = viewModel.resultPair // Fetch the list of medications from the ViewModel
    val sizeText = viewModel.sizeText(context, medicamentList) // Determine text sizes

    val lazyListState = rememberLazyListState() // Remember the scroll state of the LazyColumn
    val coroutineScope = rememberCoroutineScope() // Coroutine scope for launching coroutines
    val listSize = medicamentList.size // Size of the list
    var reachedEnd by remember { mutableStateOf(false) } // State variable to track if the end of the list is reached

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Show a message prompting the user to scroll for more data if not at the end of the list
        if (!reachedEnd) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .background(MaterialTheme.colorScheme.secondary),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Desplaça Avall per més Dades !", color = MaterialTheme.colorScheme.background)
            }
        }
        drawLines(count = 2, colorLine = MaterialTheme.colorScheme.primary)
        // LazyColumn to display the list of medications
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(listSize) { index ->
                val data = medicamentList[index]
                MedicamentItem(data = data, sizeText.first, sizeText.second, viewModel = viewModel)
                drawLines(count = 2, colorLine = MaterialTheme.colorScheme.primary)
            }
        }

        // Observe changes in the LazyColumn scroll position and update 'reachedEnd' accordingly
        DisposableEffect(lazyListState) {
            val flow = snapshotFlow { lazyListState.firstVisibleItemIndex }

            val job = coroutineScope.launch {
                flow.collect { firstVisibleItemIndex ->
                    val visibleItems = lazyListState.layoutInfo.visibleItemsInfo.size
                    reachedEnd = (firstVisibleItemIndex + visibleItems) > (listSize - 1)
                }
            }

            // Cancel the coroutine job when the composable is disposed
            onDispose { job.cancel() }
        }

        // Button to reset the ViewModel state
        OutlinedButton(
            onClick = { viewModel.resetState() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Tornar a Inici", color = MaterialTheme.colorScheme.primary)
        }
    }
}



@Composable
fun MedicamentItem(
    data: Pair<String, String>,
    sizeTextDrugsLogic: Int,
    sizeTextDoseLogic: Int,
    sizeText : Int = if(sizeTextDrugsLogic < sizeTextDoseLogic) sizeTextDrugsLogic else sizeTextDoseLogic,
    viewModel: ScreensViewModel
) {
//    // Calculate itemHeightPixels for this specific item based on its text content
//    val itemHeightPixels = viewModel.calculateItemHeight(sizeTextDrugsLogic, sizeTextDoseLogic, data) // Implement this function

    Column(
        modifier = Modifier
//            .requiredHeight(itemHeightPixels.dp) // Set the dynamic height here
            .fillMaxWidth()
            .padding(start = 5.dp, end = 5.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (data.first.isNotEmpty()) {
            Text(
                text = data.first.uppercase(),
                fontSize = sizeText.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center,
                lineHeight = (sizeText * 1.5).sp // Increase line height by 1.5 times
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (data.second.isNotEmpty()) {
            Text(
                text = data.second,
                fontSize = sizeText.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                lineHeight = (sizeText * 1.5).sp // Increase line height by 1.5 times
            )
        }
    }
}

/**
 * Draw lines
 * @param count Number of lines to draw
 * @param colorLine Color of the line
 */
@Composable
fun drawLines(count: Int, colorLine: Color = MaterialTheme.colorScheme.primary, background: Color = MaterialTheme.colorScheme.background, size: Int = 20) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 2.dp)
            .height(size.dp)
            .background(background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        repeat(count) {
            Divider(
                color = colorLine,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp)
                    .height(1.dp)
            )
        }
    }
}