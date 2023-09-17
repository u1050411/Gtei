package com.trueta.gtei

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
fun ButtonDisplay(listScreen: List<Screen> , onScreenSelected: (Screen) -> Unit) {
    LazyColumn {
        items(listScreen.size) { index ->
            FunctionButton(listScreen[index], onScreenSelected)
        }
    }
}

@Composable
fun FunctionButton( screen: Screen, onScreenSelected: (Screen) -> Unit) {
    OutlinedButton(
        onClick = { onScreenSelected(screen) },
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