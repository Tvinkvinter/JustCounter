package com.atarusov.justcounter.features.category_drawer.presentation.ui

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.atarusov.justcounter.R
import com.atarusov.justcounter.features.category_drawer.presentation.CategoriesDrawerViewModel
import com.atarusov.justcounter.features.category_drawer.presentation.mvi.entities.Action
import com.atarusov.justcounter.features.category_drawer.presentation.mvi.entities.OneTimeEvent
import com.atarusov.justcounter.features.category_drawer.presentation.mvi.entities.State
import com.atarusov.justcounter.ui.theme.Dimensions
import com.atarusov.justcounter.ui.theme.JustCounterTheme

@Composable
fun Drawer(
    onCategorySelect: (categoryId: Int?) -> Unit,
    viewModel: CategoriesDrawerViewModel = hiltViewModel()
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()

    val categoriesLazyListState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.screenEvents.collect { event ->
            when (event) {
                is OneTimeEvent.SelectCategory -> onCategorySelect(event.categoryId)
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
        Column(
            modifier = Modifier.padding(start = Dimensions.Spacing.small)
        ) {
            NoCategoryItem (
                isSelected = state.selectedCategoryId == null,
                onClick = { onAction(Action.SelectCategory(null)) },
                modifier = Modifier.fillMaxWidth().height(Dimensions.Size.extraMedium)
            )
            LazyColumn(
                modifier = Modifier.heightIn(max = Dimensions.Size.extraMedium * 10),
                state = categoryLazyListState
            ) {
                items(
                    items = state.categories,
                    key = { it.id }
                ) { category ->
                    RegularCategoryItem(
                        category = category,
                        isSelected = category.id == state.selectedCategoryId,
                        onClick = { onAction(Action.SelectCategory(category.id)) },
                        onEdit = { onAction(Action.RenameCategory(category.id, it)) },
                        onDelete = {
                            val isSelected = category.id == state.selectedCategoryId
                            onAction(Action.RemoveCategory(category.id, isSelected))
                        },
                        modifier = Modifier.fillMaxWidth().height(Dimensions.Size.extraMedium)
                    )
                }
            }
            AddCategoryItem(
                onInputDone = { text -> onAction(Action.AddCategory(text)) },
                modifier = Modifier.height(Dimensions.Size.extraMedium)

            )
        }
        Spacer(Modifier.height(Dimensions.Spacing.large))
        DrawerHints()
    }
}

@Composable
fun DrawerHints() {
    Column {
        Text(
            text = stringResource(R.string.drawer_category_list_edit_delete_hint),
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelSmall,
        )
        Spacer(Modifier.height(Dimensions.Spacing.small))
        Text(
            text = stringResource(R.string.drawer_category_list_move_hint),
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelSmall,
        )
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