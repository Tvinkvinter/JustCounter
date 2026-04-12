package com.atarusov.justcounter.features.category_drawer.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.atarusov.justcounter.R
import com.atarusov.justcounter.features.category_drawer.presentation.mvi.entities.Action
import com.atarusov.justcounter.features.category_drawer.presentation.mvi.entities.OneTimeEvent
import com.atarusov.justcounter.features.category_drawer.presentation.mvi.entities.State
import com.atarusov.justcounter.features.category_drawer.presentation.components.AddCategoryItem
import com.atarusov.justcounter.features.category_drawer.presentation.components.NoCategoryItem
import com.atarusov.justcounter.features.category_drawer.presentation.components.RegularCategoryItem
import com.atarusov.justcounter.shared_features.hints.presentation.DismissableHintCard
import com.atarusov.justcounter.ui.theme.Dimensions
import com.atarusov.justcounter.ui.theme.JustCounterTheme
import sh.calvin.reorderable.DragGestureDetector
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun Drawer(
    onCategorySelect: (categoryId: Int?, closeDrawer: Boolean) -> Unit,
    viewModel: CategoriesDrawerViewModel = hiltViewModel()
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()

    val categoriesLazyListState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.screenEvents.collect { event ->
            when (event) {
                is OneTimeEvent.SelectCategory -> onCategorySelect(event.categoryId, true)
                is OneTimeEvent.ScrollCategoryListDown -> {
                    if (state.categories.isNotEmpty())
                        categoriesLazyListState.animateScrollToItem(state.categories.lastIndex)
                }
            }
        }
    }

    DrawerContent(
        state = state,
        categoryLazyListState = categoriesLazyListState,
        onAction = viewModel::onAction
    )
}

@Composable
fun DrawerContent(
    state: State,
    categoryLazyListState: LazyListState,
    onAction: (Action) -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.width(Dimensions.Size.drawerSheetWidth),
        drawerContainerColor = MaterialTheme.colorScheme.background
    ) {
        Spacer(Modifier.height(Dimensions.Spacing.extraMedium))
        Text(
            text = stringResource(R.string.app_name),
            modifier = Modifier.padding(horizontal = Dimensions.Spacing.medium),
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(Modifier.height(Dimensions.Spacing.small))
        CategoryList(state, categoryLazyListState, onAction)
        if (!state.editDeleteHintDismissed || !state.moveHintDismissed) {
            Spacer(Modifier.height(Dimensions.Spacing.large))
            DrawerHints(state = state, onAction = onAction)
        }
    }
}

@Composable
fun CategoryList(
    state: State,
    lazyListState: LazyListState,
    onAction: (Action) -> Unit
) {
    val hapticFeedback = LocalHapticFeedback.current

    Column(
        modifier = Modifier.padding(start = Dimensions.Spacing.small)
    ) {
        NoCategoryItem(
            isSelected = state.selectedCategoryId == null,
            onClick = { onAction(Action.SelectCategory(null)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimensions.Size.extraMedium)
        )

        val reorderableLazyListState =
            rememberReorderableLazyListState(lazyListState) { first, second ->
                onAction(Action.SwapCategories(first.index, second.index))
            }

        LazyColumn(
            modifier = Modifier.heightIn(max = Dimensions.Size.extraMedium * 10),
            state = lazyListState
        ) {

            items(
                items = state.categories,
                key = { it.id }
            ) { category ->
                ReorderableItem(
                    state = reorderableLazyListState,
                    key = category.id
                ) { isDragging ->
                    RegularCategoryItem(
                        category = category,
                        isSelected = category.id == state.selectedCategoryId,
                        dragMode = isDragging,
                        onClick = { onAction(Action.SelectCategory(category.id)) },
                        onEdit = { onAction(Action.RenameCategory(category.id, it)) },
                        onDelete = {
                            val isSelected = category.id == state.selectedCategoryId
                            onAction(Action.RemoveCategory(category.id, isSelected))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(Dimensions.Size.extraMedium)
                            .draggableHandle(
                                onDragStarted = {
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                },
                                onDragStopped = {
                                    hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureEnd)
                                },
                                dragGestureDetector = DragGestureDetector.LongPress
                            )
                    )
                }
            }
        }
        AddCategoryItem(
            onInputDone = { text -> onAction(Action.AddCategory(text)) },
            modifier = Modifier.height(Dimensions.Size.extraMedium)

        )
    }

}

@Composable
fun DrawerHints(
    state: State,
    onAction: (Action) -> Unit,
) {
    Column(
        modifier = Modifier.padding(horizontal = Dimensions.Spacing.medium)
    ) {
        if (!state.editDeleteHintDismissed) {
            DismissableHintCard(
                text = stringResource(R.string.drawer_category_list_edit_delete_hint),
                onDismiss = { onAction(Action.DismissEditDeleteHint) }
            )
        }
        if (!state.editDeleteHintDismissed && !state.moveHintDismissed) {
            Spacer(Modifier.height(Dimensions.Spacing.small))
        }
        if (!state.moveHintDismissed) {
            DismissableHintCard(
                text = stringResource(R.string.drawer_category_list_move_hint),
                onDismiss = { onAction(Action.DismissMoveHint) }
            )
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun DrawerPreview() {
    JustCounterTheme {
        ModalNavigationDrawer(
            drawerContent = {
                ModalDrawerSheet {
                    DrawerContent(
                        state = State.getPreviewState(),
                        categoryLazyListState = LazyListState(),
                        onAction = {}
                    )
                }
            },
            drawerState = rememberDrawerState(DrawerValue.Open)
        ) { }
    }
}