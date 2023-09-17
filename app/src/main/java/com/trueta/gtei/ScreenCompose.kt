package com.trueta.gtei

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun TryDisplay(screen: Screen) {

    val viewModel: ScreensViewModel = viewModel()

    val option = viewModel.goScreen(screen)

    when (option) {
        "Try" -> {
            ButtonDisplay(screen.listScreens)
        }
        "CheckBox" -> {
            CheckboxesDisplay(screen.listScreens)
        }

    }

}



@Composable
fun MessageDisplay(message: String) {
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
fun ButtonDisplay(listScreen: List<Screen>){
    LazyColumn {
        items(listScreen.size) { index ->
            FunctionButton(listScreen[index])
        }
    }
}

@Composable
fun FunctionButton( screen: Screen) {
    val viewModel: ScreensViewModel = viewModel()
    OutlinedButton(
        onClick = { viewModel.onScreenSelected(screen) },
        shape = RoundedCornerShape(0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 2.dp)
            .border(1.dp, MaterialTheme.colorScheme.primary)
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

@Composable
fun CheckboxesDisplay(screen: Screen) {
    // Initialize switches should be done outside, but for the purpose of this example, it's included here.
    val viewModel: ScreensViewModel = viewModel()
    val switches = viewModel.initializeSwitches()

    Column {
        LazyColumn {
            // Directly using screen?.listVar if not null
            items(screen?.listVar ?: listOf()) { variable ->
                // Only include items that exist in switches
                if (tryView.switches.containsKey(variable.name)) {
                    MultiSelectButton(variable, tryView)
                    Spacer(modifier = Modifier.height(5.dp))
                }
            }
        }
        OutlinedButton(
            onClick = {
                screen?.let {
                    tryView.onSubmit(it)
                    navController.navigate("Try")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Enviar", color = MaterialTheme.colorScheme.primary)
        }
    }
}


@Composable
fun MultiSelectButton(variable: Variable, tryView: TryView) {
    // Collect state from flows
    val isCheckedFlow = tryView.isCheckboxChecked(variable.name).collectAsState()
    val isAlergiaSeveraCheckedFlow =
        tryView.isCheckboxChecked(tryView.alergiaTrySevera.name).collectAsState()

    // Initialize label value
    val label: String
    // When the button is not alergiaSevera
    if (variable.name != tryView.alergiaTrySevera.name) {
        // Update label value based on conditions
        label = when {
            isAlergiaSeveraCheckedFlow.value && isCheckedFlow.value && variable.name == tryView.tryAlergiaPenicilina.name -> {
                "Al·lèrgia Penicil·lina Greu"
            }

            isAlergiaSeveraCheckedFlow.value && !isCheckedFlow.value && variable.name == tryView.tryAlergiaPenicilina.name -> {
                // If alergiaPenicilina is unchecked, revert the label
                tryView.changeCheckbox(variable.name)
                tryView.changeCheckbox(tryView.alergiaTrySevera.name)
                variable.name
            }

            isCheckedFlow.value && variable.name == tryView.tryAlergiaPenicilina.name -> {
                "Al·lèrgia Penicil·lina Lleu"
            }

            else -> {
                variable.name
            }
        }

        // Render CheckboxOptionRow
        CheckboxOptionRow(
            label = label,
            isCheckedFlow = isCheckedFlow.value,
            onCheckedChange = {
                tryView.toggleCheckboxState(variable.name)
            }
        )
    }

    // When the button is alergiaPenicilina and it's checked, and alergiaSevera is not checked
    if (variable.name == tryView.tryAlergiaPenicilina.name && isCheckedFlow.value && !isAlergiaSeveraCheckedFlow.value) {
        CheckboxOptionRow(
            label = tryView.alergiaTrySevera.name,
            isCheckedFlow = isAlergiaSeveraCheckedFlow.value,
            onCheckedChange = {
                tryView.toggleCheckboxState(tryView.alergiaTrySevera.name)
            }
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