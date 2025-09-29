package com.atarusov.justcounter.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.atarusov.justcounter.R
import com.atarusov.justcounter.common.getContrastContentColor
import com.atarusov.justcounter.features.counters_screen.domain.Counter
import com.atarusov.justcounter.features.counters_screen.presentation.viewModel.CounterItem
import com.atarusov.justcounter.ui.theme.CounterCardColors
import com.atarusov.justcounter.ui.theme.JustCounterTheme

@Composable
fun CounterItem(
    state: CounterItem,
    onPLusClick: (counterId: String) -> Unit,
    onMinusClick: (counterId: String) -> Unit,
    onEditClick: (counterId: String) -> Unit,
    onInputTitle: (titleInput: String) -> Unit,
    onInputTitleDone: (counterId: String) -> Unit,
    onInputValue: (valueInput: String) -> Unit,
    onInputValueDone: (counterId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.width(150.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = state.color,
            contentColor = state.color.getContrastContentColor()
        ),
        elevation = CardDefaults.cardElevation(6.dp),
    ) {
        val customTextSelectionColors = TextSelectionColors(
            handleColor = Color.Transparent,
            backgroundColor = Color.Transparent
        ) // TODO: refactor

        CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                Modifier.fillMaxWidth().padding(top = 4.dp)
            ) {
                Spacer(
                    modifier = Modifier.size(24.dp)
                )
                BasicTextField(
                    value = state.titleField,
                    onValueChange = onInputTitle,
                    modifier = Modifier.weight(1f),
                    textStyle = MaterialTheme.typography.titleMedium.copy(
                        color = state.color.getContrastContentColor(),
                        textAlign = TextAlign.Center,
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { onInputTitleDone(state.counterId) }
                    ),
                    singleLine = true,
                    cursorBrush = SolidColor(state.color.getContrastContentColor())
                )
                IconButton(
                    {}, modifier = Modifier
                        .size(24.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_pencil_48),
                        contentDescription = stringResource(R.string.counter_screen_edit_btn_description),
                        modifier = Modifier
                            .alpha(0.5f)
                            .clickable(
                                onClick = { onEditClick(state.counterId) }
                            ),
                    )
                }
            }
            TextField(
                value = state.valueField,
                onValueChange = onInputValue,
                modifier = Modifier,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    textAlign = TextAlign.Center
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onInputValueDone(state.counterId) }
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedTextColor = state.color.getContrastContentColor(),
                    unfocusedTextColor = state.color.getContrastContentColor(),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = state.color.getContrastContentColor()
                )
            )
            Row(Modifier.padding(bottom = 4.dp)) {
                IconButton(  //todo: обрезается ripple
                    onClick = { onMinusClick(state.counterId) },
                    Modifier
                        .size(24.dp)
                        .weight(1f)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_minus_48),
                        contentDescription = stringResource(R.string.counter_screen_plus_btn_description),
                    )
                }

                IconButton(
                    onClick = { onPLusClick(state.counterId) },
                    modifier = Modifier
                        .size(24.dp)
                        .weight(1f)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_plus_48),
                        contentDescription = stringResource(R.string.counter_screen_minus_btn_description),
                    )
                }
            }
        }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CounterPreview() {
    JustCounterTheme {
        val counter = Counter(
            title = "Tvinkvinter",
            color = CounterCardColors.getRandom(),
            value = 128
        )

        CounterItem(
            state = CounterItem(counter),
            onPLusClick = {},
            onMinusClick = {},
            onEditClick = {},
            onInputTitle = {},
            onInputTitleDone = {},
            onInputValue = {},
            onInputValueDone = {},
            modifier = Modifier
                .width(200.dp)
                .padding(12.dp)
        )
    }
}
