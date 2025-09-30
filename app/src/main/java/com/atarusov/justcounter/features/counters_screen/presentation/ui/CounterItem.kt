package com.atarusov.justcounter.features.counters_screen.presentation.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
    removeMode: Boolean,
    onPLusClick: () -> Unit,
    onMinusClick: () -> Unit,
    onEditClick: () -> Unit,
    onInputTitle: (titleInput: String) -> Unit,
    onInputTitleDone: () -> Unit,
    onInputValue: (valueInput: String) -> Unit,
    onInputValueDone: () -> Unit,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contentAlpha by animateFloatAsState(
        targetValue = if (removeMode) 0.5f else 1f,
        animationSpec = tween(durationMillis = 300)
    )

    Card(
        modifier = modifier
            .width(150.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = state.color,
            contentColor = state.color.getContrastContentColor()
        ),
        elevation = CardDefaults.cardElevation(6.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            ) {
                Spacer(
                    Modifier
                        .padding(start = 4.dp)
                        .size(24.dp)
                )
                BasicTextField(
                    value = state.titleField,
                    onValueChange = onInputTitle,
                    modifier = Modifier
                        .weight(1f)
                        .alpha(contentAlpha),
                    enabled = !removeMode,
                    textStyle = MaterialTheme.typography.titleMedium.copy(
                        color = state.color.getContrastContentColor(),
                        textAlign = TextAlign.Center,
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { onInputTitleDone() }
                    ),
                    singleLine = true,
                    cursorBrush = SolidColor(state.color.getContrastContentColor())
                )
                IconButton(
                    onClick = {
                        if (removeMode) onRemoveClick()
                        else onEditClick()
                    },
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .size(24.dp),
                ) {
                    Icon(
                        painter = painterResource(
                            if (removeMode) R.drawable.ic_cross
                            else R.drawable.ic_pencil
                        ),
                        contentDescription = stringResource(R.string.counter_screen_edit_btn_description),
                        modifier = Modifier.alpha(if (removeMode) 1f else 0.5f),
                        tint = state.color.getContrastContentColor()
                    )
                }
            }
            TextField(
                value = state.valueField,
                onValueChange = onInputValue,
                modifier = Modifier.alpha(contentAlpha),
                enabled = !removeMode,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    textAlign = TextAlign.Center
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onInputValueDone() }
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedTextColor = state.color.getContrastContentColor(),
                    unfocusedTextColor = state.color.getContrastContentColor(),
                    disabledTextColor = state.color.getContrastContentColor(),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    cursorColor = state.color.getContrastContentColor()
                )
            )
            Row(
                Modifier
                    .padding(bottom = 4.dp)
                    .alpha(contentAlpha)
            ) {
                IconButton(  //todo: обрезается ripple
                    onClick = { onMinusClick() },
                    modifier = Modifier
                        .size(24.dp)
                        .weight(1f),
                    enabled = !removeMode,
                    colors = IconButtonDefaults.iconButtonColors(
                        disabledContentColor = state.color.getContrastContentColor()
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_minus),
                        contentDescription = stringResource(R.string.counter_screen_plus_btn_description),
                    )
                }

                IconButton(
                    onClick = { onPLusClick() },
                    modifier = Modifier
                        .size(24.dp)
                        .weight(1f),
                    enabled = !removeMode,
                    colors = IconButtonDefaults.iconButtonColors(
                        disabledContentColor = state.color.getContrastContentColor()
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_plus),
                        contentDescription = stringResource(R.string.counter_screen_minus_btn_description),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CounterPreview() {
    JustCounterTheme {
        val counterItem = CounterItem(Counter(
            title = "Tvinkvinter",
            color = CounterCardColors.getRandom(),
            value = 128
        ))

        CounterItem(
            state = counterItem,
            removeMode = false,
            onPLusClick = {},
            onMinusClick = {},
            onEditClick = {},
            onInputTitle = {},
            onInputTitleDone = {},
            onInputValue = {},
            onInputValueDone = {},
            onRemoveClick = {},
            modifier = Modifier
                .width(200.dp)
                .padding(12.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CounterInRemoveModePreview() {
    JustCounterTheme {
        val counterItem = CounterItem(
            Counter(
                title = "Tvinkvinter",
                color = CounterCardColors.getRandom(),
                value = 128
            )
        )

        CounterItem(
            state = counterItem,
            removeMode = true,
            onPLusClick = {},
            onMinusClick = {},
            onEditClick = {},
            onInputTitle = {},
            onInputTitleDone = {},
            onInputValue = {},
            onInputValueDone = {},
            onRemoveClick = {},
            modifier = Modifier
                .width(200.dp)
                .padding(12.dp)
        )
    }
}
