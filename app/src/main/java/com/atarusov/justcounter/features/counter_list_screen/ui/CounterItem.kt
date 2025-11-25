package com.atarusov.justcounter.features.counter_list_screen.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.atarusov.justcounter.R
import com.atarusov.justcounter.domain.Counter
import com.atarusov.justcounter.features.counter_list_screen.mvi.entities.getPreviewCounter
import com.atarusov.justcounter.features.counter_list_screen.ui.callbacks.CounterItemCallbacks
import com.atarusov.justcounter.ui.theme.CounterColorProvider
import com.atarusov.justcounter.ui.theme.Dimensions
import com.atarusov.justcounter.ui.theme.JustCounterTheme
import com.atarusov.justcounter.ui.theme.getReadableContentColor
import sh.calvin.reorderable.DragGestureDetector
import sh.calvin.reorderable.ReorderableCollectionItemScope

@Composable
fun CounterItem(
    state: Counter,
    removeMode: Boolean,
    dragMode: Boolean,
    callbacks: CounterItemCallbacks,
    modifier: Modifier = Modifier,
    reorderableScope: ReorderableCollectionItemScope? = null
) {
    val hapticFeedback = LocalHapticFeedback.current

    val cardScale by animateFloatAsState(if (dragMode) 1.05f else 1f)
    val contentAlpha by animateFloatAsState(
        targetValue = if (removeMode) 0.5f else 1f,
        animationSpec = tween(durationMillis = 300)
    )

    val itemColor = CounterColorProvider.getColor(state.color)
    val contentColor = itemColor.getReadableContentColor()

    Column(modifier.width(150.dp)) {
        Card(
            modifier = Modifier
                .graphicsLayer {
                    scaleX = cardScale
                    scaleY = cardScale
                }.clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = callbacks.onCounterTap
                ).then(
                    reorderableScope?.run {
                        Modifier.draggableHandle(
                            onDragStarted = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            },
                            onDragStopped = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureEnd)
                            },
                            dragGestureDetector = DragGestureDetector.LongPress
                        )
                    } ?: Modifier
                ),
            shape = RoundedCornerShape(Dimensions.Radius.medium),
            colors = CardDefaults.cardColors(
                containerColor = itemColor,
                contentColor = contentColor
            ),
            elevation = CardDefaults.cardElevation(Dimensions.Elevation.card),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Dimensions.Spacing.extraSmall),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(
                        Modifier
                            .padding(start = Dimensions.Spacing.extraSmall)
                            .size(Dimensions.Size.small)
                    )

                    Text(
                        text = state.title,
                        modifier = Modifier
                            .weight(1f)
                            .graphicsLayer { alpha = contentAlpha },
                        color = contentColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = contentColor,
                            textAlign = TextAlign.Center,
                        )
                    )

                    Icon(
                        painter = painterResource(
                            if (removeMode) R.drawable.ic_cross
                            else R.drawable.ic_pencil
                        ),
                        contentDescription = stringResource(R.string.counter_screen_edit_btn_description),
                        modifier = Modifier
                            .padding(end = Dimensions.Spacing.extraSmall)
                            .size(Dimensions.Size.small)
                            .graphicsLayer { alpha = if (removeMode) 1f else 0.5f }
                            .clickable(
                                onClick = {
                                    if (removeMode) callbacks.onRemoveClick()
                                    else callbacks.onEditClick()
                                },
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(
                                    bounded = false,
                                    radius = Dimensions.Radius.medium
                                ),
                            ),
                        tint = contentColor
                    )
                }
                Text(
                    text = state.value.toString(),
                    modifier = Modifier
                        .graphicsLayer { alpha = contentAlpha }
                        .padding(vertical = Dimensions.Spacing.extraMedium),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        textAlign = TextAlign.Center
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
                Row(
                    Modifier
                        .padding(bottom = Dimensions.Spacing.extraSmall)
                        .graphicsLayer { alpha = contentAlpha },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(Modifier.width(Dimensions.Spacing.extraSmall))

                    BasicText(
                        text = if (state.steps[0] == 1) "−" else "−${state.steps[0]}",
                        modifier = Modifier
                            .weight(1f)
                            .clickable(
                                onClick = { callbacks.onMinusClick(state.steps[0]) },
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(bounded = false, radius = Dimensions.Radius.large),
                                enabled = !removeMode
                            ),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            textAlign = TextAlign.Center,
                        ),
                        overflow = TextOverflow.MiddleEllipsis,
                        maxLines = 1,
                        color = { contentColor },
                        autoSize = TextAutoSize.StepBased(
                            minFontSize = Dimensions.Typography.minFontSize,
                            maxFontSize = MaterialTheme.typography.bodyLarge.fontSize
                        )
                    )

                    Spacer(Modifier.width(Dimensions.Spacing.small))

                    BasicText(
                        text = if (state.steps[0] == 1) "+" else "+${state.steps[0]}",
                        modifier = Modifier
                            .weight(1f)
                            .clickable(
                                onClick = { callbacks.onPLusClick(state.steps[0]) },
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(bounded = false, radius = Dimensions.Radius.large),
                                enabled = !removeMode
                            ),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            textAlign = TextAlign.Center,
                        ),
                        overflow = TextOverflow.MiddleEllipsis,
                        maxLines = 1,
                        color = { contentColor },
                        autoSize = TextAutoSize.StepBased(
                            minFontSize = Dimensions.Typography.minFontSize,
                            maxFontSize = MaterialTheme.typography.bodyLarge.fontSize
                        )
                    )

                    Spacer(Modifier.width(Dimensions.Spacing.extraSmall))
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimensions.Spacing.extraSmall),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            for (index in state.steps.indices.reversed()) {
                if (index != 0) {
                    ExtraStepButton(
                        text = "-${state.steps[index]}",
                        containerColor = itemColor,
                        contentColor = contentColor,
                        contentAlpha = contentAlpha,
                        enabled = !removeMode,
                        onClick = { callbacks.onMinusClick(state.steps[index]) }
                    )
                }
            }
            for (index in state.steps.indices) {
                if (index != 0) {
                    ExtraStepButton(
                        text = "+${state.steps[index]}",
                        containerColor = itemColor,
                        contentColor = contentColor,
                        contentAlpha = contentAlpha,
                        enabled = !removeMode,
                        onClick = { callbacks.onPLusClick(state.steps[index]) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ExtraStepButton(
    text: String,
    containerColor: Color,
    contentColor: Color,
    contentAlpha: Float,
    enabled: Boolean,
    onClick: () -> Unit
) {
    BasicText(
        text = text,
        modifier = Modifier
            .size(Dimensions.Size.extraMedium)
            .background(color = containerColor, shape = CircleShape)
            .padding(horizontal = Dimensions.Spacing.xxSmall)
            .wrapContentSize()
            .graphicsLayer { alpha = contentAlpha }
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = false, radius = Dimensions.Radius.large),
                enabled = enabled
            ),
        style = MaterialTheme.typography.bodyMedium.copy(
            textAlign = TextAlign.Center,
        ),
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        color = { contentColor },
        autoSize = TextAutoSize.StepBased(
            minFontSize = Dimensions.Typography.minFontSize,
            maxFontSize = MaterialTheme.typography.bodyMedium.fontSize
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun CounterPreview() {
    JustCounterTheme {
        CounterItem(
            state = getPreviewCounter(),
            removeMode = false,
            dragMode = false,
            callbacks = CounterItemCallbacks.getEmptyCallbacks(),
            modifier = Modifier
                .width(200.dp)
                .padding(Dimensions.Spacing.medium)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CounterInRemoveModePreview() {
    JustCounterTheme {
        CounterItem(
            state = getPreviewCounter(),
            removeMode = true,
            dragMode = false,
            callbacks = CounterItemCallbacks.getEmptyCallbacks(),
            modifier = Modifier
                .width(200.dp)
                .padding(Dimensions.Spacing.medium)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CounterWithExtraStepsPreview() {
    JustCounterTheme {
        CounterItem(
            state = getPreviewCounter(withCustomSteps = true),
            removeMode = false,
            dragMode = false,
            callbacks = CounterItemCallbacks.getEmptyCallbacks(),
            modifier = Modifier
                .width(200.dp)
                .padding(Dimensions.Spacing.medium)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CounterWithExtraStepsInRemoveModePreview() {
    JustCounterTheme {
        CounterItem(
            state = getPreviewCounter(withCustomSteps = true),
            removeMode = true,
            dragMode = false,
            callbacks = CounterItemCallbacks.getEmptyCallbacks(),
            modifier = Modifier
                .width(200.dp)
                .padding(Dimensions.Spacing.medium)
        )
    }
}
