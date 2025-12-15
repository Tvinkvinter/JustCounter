package com.atarusov.justcounter.features.category_drawer.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import com.atarusov.justcounter.R
import com.atarusov.justcounter.ui.theme.Dimensions
import com.atarusov.justcounter.ui.theme.dangerRed
import com.atarusov.justcounter.ui.theme.green
import com.atarusov.justcounter.ui.theme.lightBlue
import kotlin.math.roundToInt


@Composable
fun CategoryItemContextMenu(
    isVisible: Boolean,
    offset: Animatable<Float, AnimationVector1D>,
    onDeleteClick: () -> Unit,
    onEditModeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideIn(animationSpec = tween(300)) { IntOffset(it.width, 0) },
        exit = slideOut(animationSpec = tween(300)) { IntOffset(it.width, 0) }
    ) {
        Row(
            modifier = modifier.fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                onClick = onDeleteClick,
                color = dangerRed,
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f / 1f)
                    .offset { IntOffset(offset.value.roundToInt() * 2, 0) }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_trash_can),
                    contentDescription = stringResource(R.string.drawer_category_item_menu_delete_category_description),
                    modifier = Modifier.padding(Dimensions.Spacing.small),
                    tint = MaterialTheme.colorScheme.onTertiary
                )
            }
            Surface(
                onClick = onEditModeClick,
                color = lightBlue,
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f / 1f)
                    .offset { IntOffset(offset.value.roundToInt(), 0) }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_pencil_with_text),
                        contentDescription = stringResource(R.string.drawer_category_item_menu_edit_category_description),
                        modifier = Modifier.padding(Dimensions.Spacing.small),
                        tint = MaterialTheme.colorScheme.onTertiary
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryItemEditMenu(
    isVisible: Boolean,
    onInputDone: () -> Unit,
    onInputCancelled: () -> Unit,
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = scaleIn(),
        exit = scaleOut()
    ) {
        Row(
            modifier = Modifier.fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onInputCancelled,
                modifier = Modifier.size(Dimensions.Size.medium)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_cross),
                    contentDescription = stringResource(R.string.drawer_category_item_menu_cancel_input_description),
                    modifier = Modifier.size(Dimensions.Size.small),
                    tint = dangerRed
                )
            }
            Spacer(Modifier.width(Dimensions.Spacing.small))
            IconButton(
                onClick = onInputDone,
                modifier = Modifier.size(Dimensions.Size.medium)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = stringResource(R.string.drawer_category_item_menu_confirm_input_description),
                    modifier = Modifier.size(Dimensions.Size.small),
                    tint = green
                )
            }
        }
    }
}