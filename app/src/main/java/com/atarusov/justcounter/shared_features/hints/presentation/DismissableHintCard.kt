package com.atarusov.justcounter.shared_features.hints.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.atarusov.justcounter.R
import com.atarusov.justcounter.ui.theme.Dimensions
import com.atarusov.justcounter.ui.theme.JustCounterTheme
import com.atarusov.justcounter.ui.theme.grey
import com.atarusov.justcounter.ui.theme.lightOrange

@Composable
fun DismissableHintCard(
    text: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor = MaterialTheme.colorScheme.outlineVariant
    val density = LocalDensity.current
    val strokeWidthPx = with(density) { Dimensions.Border.thin.toPx() }
    val cornerRadiusPx = with(density) { Dimensions.Radius.medium.toPx() }
    val dashLengthPx = with(density) { 4.dp.toPx() }
    val dashGapPx = with(density) { 4.dp.toPx() }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimensions.Radius.medium))
            .drawBehind {
                drawRoundRect(
                    color = borderColor,
                    cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx),
                    style = Stroke(
                        width = strokeWidthPx,
                        pathEffect = PathEffect.dashPathEffect(
                            floatArrayOf(dashLengthPx, dashGapPx),
                            0f
                        )
                    )
                )
            }
            .padding(
                start = Dimensions.Spacing.extraMedium,
                end = Dimensions.Spacing.small,
                top = Dimensions.Spacing.medium,
                bottom = Dimensions.Spacing.extraSmall,
            ),
    ) {
        Text(
            text = text,
            modifier = Modifier.fillMaxWidth(),
            color = grey,
            style = MaterialTheme.typography.bodySmall,
        )
        TextButton(
            onClick = onDismiss,
            modifier = Modifier.align(Alignment.End),
            contentPadding = PaddingValues(
                horizontal = Dimensions.Spacing.medium,
                vertical = Dimensions.Spacing.extraSmall,
            ),
            colors = ButtonDefaults.textButtonColors(
                contentColor = lightOrange
            ),
        ) {
            Text(
                text = stringResource(R.string.hint_confirm_button),
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DismissableHintCardPreview() {
    JustCounterTheme {
        Column(
            modifier = Modifier.padding(Dimensions.Spacing.medium),
            verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.small),
        ) {
            DismissableHintCard(
                text = "Swipe a category left to edit or delete it",
                onDismiss = {}
            )
            DismissableHintCard(
                text = "Press and hold a category to move it",
                onDismiss = {}
            )
        }
    }
}
