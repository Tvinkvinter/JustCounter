package com.atarusov.justcounter.features.category_drawer.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.atarusov.justcounter.R
import com.atarusov.justcounter.common.Category
import com.atarusov.justcounter.ui.theme.Dimensions
import com.atarusov.justcounter.ui.theme.JustCounterTheme
import com.atarusov.justcounter.ui.theme.lightOrange
import kotlinx.coroutines.launch

private enum class RegularCategoryItemMode {
    ReadOnly, ContextMenu, Editing
}

@Composable
fun RegularCategoryItem(
    category: Category,
    isSelected: Boolean,
    dragMode: Boolean,
    onClick: () -> Unit,
    onEdit: (newName: String) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    var mode by remember { mutableStateOf(RegularCategoryItemMode.ReadOnly) }
    var menuSize by remember { mutableFloatStateOf(0f) }
    val offset = remember { Animatable(initialValue = menuSize) }

    val itemScale by animateFloatAsState(if (dragMode) 1.05f else 1f)
    val itemBackground by animateColorAsState(if (dragMode) MaterialTheme.colorScheme.surfaceContainerHigh
                                              else MaterialTheme.colorScheme.background)
    val focusManager = LocalFocusManager.current
    val textFieldFocusRequester = remember { FocusRequester() }
    val textFieldState = rememberTextFieldState(initialText = category.name)

    val isReadOnly = mode == RegularCategoryItemMode.ReadOnly
    val isEditing = mode == RegularCategoryItemMode.Editing
    val isContextMenu = mode == RegularCategoryItemMode.ContextMenu

    LaunchedEffect(mode) {
        when (mode) {
            RegularCategoryItemMode.ReadOnly -> focusManager.clearFocus()
            RegularCategoryItemMode.ContextMenu -> null
            RegularCategoryItemMode.Editing -> textFieldFocusRequester.requestFocus()
        }
    }

    fun commitInput() {
        val input = textFieldState.text.toString()
        if (input.isNotBlank()) onEdit(input)
        mode = RegularCategoryItemMode.ReadOnly
    }

    fun Modifier.swipeCategoryItemModifier() = this.pointerInput(Unit) {
        detectHorizontalDragGestures(
            onDragStart = { mode = RegularCategoryItemMode.ContextMenu },
            onDragEnd = {
                scope.launch {
                    if (offset.value > menuSize / 3) {
                        offset.animateTo(menuSize)
                        mode = RegularCategoryItemMode.ReadOnly
                    } else {
                        offset.animateTo(0f)
                        mode = RegularCategoryItemMode.ContextMenu
                    }
                }
            },
            onHorizontalDrag = { change, dragAmount ->
                change.consume()
                scope.launch {
                    val newOffset = (offset.value + dragAmount).coerceIn(0f, menuSize)
                    offset.snapTo(newOffset)
                }
            },
        )
    }

    Row(modifier = modifier
        .drawBehind { drawRect(itemBackground) }
        .swipeCategoryItemModifier()
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .height(Dimensions.Size.large)
                .graphicsLayer {
                    scaleY = itemScale
                    scaleX = itemScale
                }
                .clickable(
                    enabled = !dragMode,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onClick
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(Dimensions.Spacing.medium))
            Icon(
                painter = painterResource(R.drawable.ic_category_label),
                contentDescription = stringResource(
                    if (isSelected) R.string.drawer_category_item_selected_description
                    else R.string.drawer_category_item_unselected_description
                ),
                modifier = Modifier.size(Dimensions.Size.small),
                tint = if (isSelected) lightOrange else MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.width(Dimensions.Spacing.medium))
            if (!isEditing) {
                Text(
                    text = textFieldState.text.toString(),
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.titleSmall,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            } else {
                CategoryTextField(
                    textFieldState = textFieldState,
                    onInputDone = { commitInput() },
                    modifier = Modifier.weight(1f),
                    focusRequester = textFieldFocusRequester,
                )
            }
        }
        Spacer(Modifier.width(Dimensions.Spacing.small))
        CategoryItemEditMenu(
            isVisible = isEditing,
            onInputDone = { commitInput() },
            onInputCancelled = {
                mode = RegularCategoryItemMode.ReadOnly
                textFieldState.setTextAndPlaceCursorAtEnd(category.name)
            },
        )
        CategoryItemContextMenu(
            isVisible = isContextMenu,
            offset = offset,
            onDeleteClick = onDelete,
            onEditModeClick = { mode = RegularCategoryItemMode.Editing },
            modifier = Modifier.onSizeChanged { menuSize = it.width.toFloat() }
        )

    }
}

@Composable
fun NoCategoryItem(
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(Dimensions.Spacing.medium))
        Icon(
            painter = painterResource(R.drawable.ic_null_category_label),
            contentDescription = stringResource(
                if (isSelected) R.string.drawer_category_item_selected_description
                else R.string.drawer_category_item_unselected_description
            ),
            modifier = Modifier.size(Dimensions.Size.small),
            tint = if (isSelected) lightOrange else MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.width(Dimensions.Spacing.medium))
        Text(
            text = stringResource(R.string.drawer_uncategorized_counters_item_text),
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.titleSmall,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }
}

@Composable
fun AddCategoryItem(
    onInputDone: (input: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    var isEditMenuVisible by remember { mutableStateOf(false) }
    val textFieldState = rememberTextFieldState()

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(Dimensions.Spacing.medium))
        Icon(
            painter = painterResource(R.drawable.ic_plus),
            contentDescription = stringResource(R.string.drawer_category_item_add_category_text),
            modifier = Modifier.size(Dimensions.Size.small),
            tint = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.width(Dimensions.Spacing.medium))

        fun handleInput() {
            val input = textFieldState.text.toString()
            if (input.isNotBlank()) onInputDone(input)
            textFieldState.setTextAndPlaceCursorAtEnd("")
            focusManager.clearFocus()
        }

        CategoryTextField(
            textFieldState = textFieldState,
            onInputDone = { handleInput() },
            modifier = Modifier.weight(1f),
            hint = stringResource(R.string.drawer_category_item_add_category_text),
            onFocusChanged = { isEditMenuVisible = it }
        )
        Spacer(Modifier.width(Dimensions.Spacing.small))
        CategoryItemEditMenu(
            isVisible = isEditMenuVisible,
            onInputDone = { handleInput() },
            onInputCancelled = {
                textFieldState.setTextAndPlaceCursorAtEnd("")
                focusManager.clearFocus()
            },
        )
    }
}

@Composable
fun CategoryTextField(
    textFieldState: TextFieldState,
    onInputDone: () -> Unit,
    modifier: Modifier,
    focusRequester: FocusRequester? = null,
    onFocusChanged: (isFocused: Boolean) -> Unit = {},
    hint: String? = null
) {
    var textFieldFocused by remember { mutableStateOf(false) }
    val hintAlpha by animateFloatAsState(
        targetValue = if (textFieldFocused) 0.3f else 1f,
        animationSpec = tween(durationMillis = 300)
    )

    BasicTextField(
        state = textFieldState,
        modifier = modifier
            .then(
                focusRequester?.let { Modifier.focusRequester(it) } ?: Modifier
            )
            .onFocusChanged {
                textFieldFocused = it.isFocused
                onFocusChanged(it.isFocused)
            },
        textStyle = MaterialTheme.typography.titleSmall.copy(
            color = MaterialTheme.colorScheme.onSurface
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        onKeyboardAction = { onInputDone() },
        lineLimits = TextFieldLineLimits.SingleLine,
        decorator = { innerTextField ->
            if (textFieldState.text.isEmpty() && hint != null) {
                Text(
                    text = hint,
                    modifier = Modifier.graphicsLayer { alpha = hintAlpha },
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            innerTextField()
        }
    )
}

@Preview(showBackground = true)
@Composable
fun CategoryItemPreview() {
    JustCounterTheme {
        Column(
            modifier = Modifier.width(300.dp)
        ) {
            NoCategoryItem(true, {})
            RegularCategoryItem(Category.getPreviewCategory(), false, false, {}, {}, {})
            RegularCategoryItem(Category.getPreviewCategory(), false, false, {}, {}, {})
            RegularCategoryItem(Category.getPreviewCategory(), false, false, {}, {}, {})
            AddCategoryItem({})
        }
    }
}