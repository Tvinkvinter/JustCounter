package com.atarusov.justcounter.features.counter_full_screen.presentation.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.atarusov.justcounter.R
import com.atarusov.justcounter.common.Counter
import com.atarusov.justcounter.common.Counter.Companion.getPreviewCounter
import com.atarusov.justcounter.features.counter_full_screen.presentation.ui.callbacks.CounterFullScreenCallbacks
import com.atarusov.justcounter.ui.theme.CounterColorProvider
import com.atarusov.justcounter.ui.theme.Dimensions
import com.atarusov.justcounter.ui.theme.JustCounterTheme
import com.atarusov.justcounter.ui.theme.getReadableContentColor

@Composable
fun CounterFullScreenCard(
    state: Counter,
    removeMode: Boolean,
    callbacks: CounterFullScreenCallbacks,
    modifier: Modifier = Modifier
) {
    val contentAlpha by animateFloatAsState(
        targetValue = if (removeMode) 0.5f else 1f,
        animationSpec = tween(durationMillis = 300)
    )

    val itemColor = CounterColorProvider.getColor(state.color)
    val contentColor = itemColor.getReadableContentColor()

    Column(
        modifier = modifier.padding(
            horizontal = Dimensions.Spacing.large,
            vertical = Dimensions.Spacing.huge
        )
    ) {
        Card(
            shape = RoundedCornerShape(Dimensions.Radius.extraLarge),
            colors = CardDefaults.cardColors(
                containerColor = itemColor,
                contentColor = contentColor
            ),
            elevation = CardDefaults.cardElevation(Dimensions.Elevation.card),
            modifier = Modifier.weight(1f)
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Dimensions.Spacing.extraMedium)
                        .padding(horizontal = Dimensions.Spacing.extraMedium),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_shrink_arrow),
                        contentDescription = stringResource(R.string.counter_card_shrink_btn_description),
                        modifier = Modifier
                            .size(Dimensions.Size.medium)
                            .graphicsLayer { alpha = 0.5f }
                            .clickable(
                                onClick = { if (!removeMode) callbacks.onShrinkClick() },
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(
                                    bounded = false,
                                    radius = Dimensions.Radius.large
                                ),
                            ),
                        tint = contentColor
                    )

                    Text(
                        text = state.title,
                        modifier = Modifier
                            .weight(1f)
                            .graphicsLayer { alpha = contentAlpha },
                        color = contentColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.displaySmall.copy(
                            color = contentColor,
                            textAlign = TextAlign.Center
                        )
                    )

                    Icon(
                        painter = painterResource(
                            if (removeMode) R.drawable.ic_cross
                            else R.drawable.ic_pencil
                        ),
                        contentDescription = stringResource(R.string.counter_full_screen_back_btn_description),
                        modifier = Modifier
                            .size(Dimensions.Size.medium)
                            .graphicsLayer { alpha = if (removeMode) 1f else 0.5f }
                            .clickable(
                                onClick = {
                                    if (removeMode) callbacks.onRemoveClick()
                                    else callbacks.onEditClick()
                                },
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(
                                    bounded = false,
                                    radius = Dimensions.Radius.large
                                ),
                            ),
                        tint = contentColor
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(
                            horizontal = Dimensions.Spacing.huge,
                            vertical = Dimensions.Spacing.medium
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.value.toString(),
                        modifier = Modifier
                            .graphicsLayer { alpha = contentAlpha },
                        style = MaterialTheme.typography.displayLarge,
                        autoSize = TextAutoSize.StepBased(),
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = Dimensions.Spacing.small),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (index in state.steps.indices.reversed()) {
                        if (index != 0) {
                            ExtraStepButton(
                                text = "−${state.steps[index]}",
                                contentColor = contentColor,
                                contentAlpha = contentAlpha,
                                enabled = !removeMode,
                                onClick = { callbacks.onMinusClick(state.steps[index]) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    for (index in state.steps.indices) {
                        if (index != 0) {
                            ExtraStepButton(
                                text = "+${state.steps[index]}",
                                contentColor = contentColor,
                                contentAlpha = contentAlpha,
                                enabled = !removeMode,
                                onClick = { callbacks.onPLusClick(state.steps[index]) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
                Row(
                    Modifier
                        .height(Dimensions.Size.huge)
                        .padding(bottom = Dimensions.Spacing.extraSmall)
                        .graphicsLayer { alpha = contentAlpha },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(Modifier.width(Dimensions.Spacing.medium))

                    BasicText(
                        text = if (state.steps[0] == 1) "−" else "−${state.steps[0]}",
                        modifier = Modifier
                            .weight(1f)
                            .clickable(
                                onClick = { callbacks.onMinusClick(state.steps[0]) },
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(
                                    bounded = false,
                                    radius = Dimensions.Radius.huge
                                ),
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
                            maxFontSize = MaterialTheme.typography.displayLarge.fontSize
                        )
                    )

                    Spacer(Modifier.width(Dimensions.Spacing.extraMedium))

                    BasicText(
                        text = if (state.steps[0] == 1) "+" else "+${state.steps[0]}",
                        modifier = Modifier
                            .weight(1f)
                            .clickable(
                                onClick = { callbacks.onPLusClick(state.steps[0]) },
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(
                                    bounded = false,
                                    radius = Dimensions.Radius.huge
                                ),
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
                            maxFontSize = MaterialTheme.typography.displayLarge.fontSize
                        )
                    )

                    Spacer(Modifier.width(Dimensions.Spacing.medium))
                }
            }
        }
    }
}

@Composable
private fun ExtraStepButton(
    text: String,
    contentColor: Color,
    contentAlpha: Float,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BasicText(
        text = text,
        modifier = modifier
            .padding(horizontal = Dimensions.Spacing.small)
            .wrapContentSize()
            .graphicsLayer { alpha = contentAlpha }
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = false, radius = Dimensions.Radius.huge),
                enabled = enabled
            ),
        style = MaterialTheme.typography.displayMedium.copy(
            textAlign = TextAlign.Center,
        ),
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        color = { contentColor },
        autoSize = TextAutoSize.StepBased(
            minFontSize = Dimensions.Typography.minFontSize,
            maxFontSize = MaterialTheme.typography.displayMedium.fontSize
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun CounterFullScreenCardPreview() {
    JustCounterTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Some AppBar") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Gray
                    )
                )
            }
        ) { paddingValues ->
            CounterFullScreenCard(
                state = getPreviewCounter(withCustomSteps = false),
                removeMode = false,
                callbacks = CounterFullScreenCallbacks.getEmptyCallbacks(),
                modifier = Modifier.padding(paddingValues)
            )
        }

    }
}