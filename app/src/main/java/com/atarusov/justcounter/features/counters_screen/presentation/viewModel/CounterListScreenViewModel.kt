package com.atarusov.justcounter.features.counters_screen.presentation.viewModel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atarusov.justcounter.features.counters_screen.domain.Counter
import com.atarusov.justcounter.features.counters_screen.domain.CounterListRepository
import com.atarusov.justcounter.features.counters_screen.presentation.ui.edit_counter_dialog.EditDialogState
import com.atarusov.justcounter.ui.theme.CounterCardColors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.atarusov.justcounter.features.counters_screen.presentation.viewModel.CounterListAction as Action

data class CounterListScreenState(
    val counterItems: List<CounterItem> = listOf(),
    val editDialog: EditDialogState? = null
)

data class CounterItem(
    val titleField: String,
    val valueField: String,
    val color: Color,
    val counterId: String
) {
    constructor(counter: Counter) : this(
        titleField = counter.title,
        valueField = counter.value.toString(),
        color = counter.color,
        counterId = counter.id
    )
}

@HiltViewModel
class CounterListScreenViewModel @Inject constructor(
    private val counterListRepository: CounterListRepository
) : ViewModel() {

    private val _screenEvents = MutableSharedFlow<OneTimeEvent>()
    val screenEvents: SharedFlow<OneTimeEvent> = _screenEvents.asSharedFlow()

    private val _screenState = MutableStateFlow(CounterListScreenState())
    val screenState: StateFlow<CounterListScreenState> = _screenState.asStateFlow()

    var currentCounterList: List<Counter> = emptyList()

    init {
        viewModelScope.launch {
            counterListRepository.counterListFlow.collect { newCounterList ->
                currentCounterList = newCounterList
                val newCounterItemList = newCounterList.map { CounterItem(it) }
                val newEditDialogState = getUpdatedEditDialogState(newCounterItemList)
                _screenState.update { oldScreenState ->
                    oldScreenState.copy(newCounterItemList, newEditDialogState)
                }
            }
        }

        viewModelScope.launch {
            if (counterListRepository.counterListFlow.first().isEmpty()) createNewCounter()
        }
    }

    fun onAction(action: Action) {
        when (action) {
            Action.CreateNewCounter -> createNewCounter()
            is Action.MinusClick -> onCounterMinusClick(action.counterId)
            is Action.PlusClick -> onCounterPlusClick(action.counterId)
            is Action.TitleInput -> onTitleInput(action.counterId, action.newTitle)
            is Action.TitleInputDone -> onTitleInputDone(action.counterId)
            is Action.ValueInput -> onValueInput(action.counterId, action.newValue)
            is Action.ValueInputDone -> onCounterValueInputDone(action.counterId)
            is Action.ChangeColor -> onCounterChangeColor(action.counterId, action.newColor)
            is Action.OpenCounterEditDialog -> openEditDialog(action.counterId)
            is Action.CloseCounterEditDialog -> closeEditDialog(action.cancelEdits)
        }
    }

    private fun createNewCounter() {
        viewModelScope.launch {
            val newCounter = Counter("test", 0, CounterCardColors.getRandom())
            counterListRepository.addCounter(newCounter)
        }
    }

    private fun onCounterMinusClick(counterId: String) {
        val counter = getCounterById(counterId)
        fillEmptyFields(counterId)
        updateCounter(counter.copy(value = (counter.value - 1).coerceAtLeast(-999999999)))
    }

    private fun onCounterPlusClick(counterId: String) {
        val counter = getCounterById(counterId)
        fillEmptyFields(counterId)
        updateCounter(counter.copy(value = (counter.value + 1).coerceAtMost(999999999)))
    }

    private fun onTitleInput(counterId: String, input: String) {
        val counterItem = getCounterItemById(counterId)
        if (input.length <= 12) {
            updateCounterItem(counterItem.copy(titleField = input))
            updateCounter(getCounterById(counterId).copy(title = input))
        }
    }

    private fun onTitleInputDone(counterId: String) {
        val counterItem = getCounterItemById(counterId)
        viewModelScope.launch {
            if (counterItem.titleField.isBlank()) {
                _screenEvents.emit(OneTimeEvent.ShowTitleInputError(counterId))
            } else {
                _screenEvents.emit(OneTimeEvent.ClearFocus)
                fillEmptyFields(counterId)
            }
        }

    }

    private fun onValueInput(counterId: String, input: String) {
        val counterItem = getCounterItemById(counterId)

        if (input.isEmpty() || input == "-") {
            updateCounterItem(counterItem.copy(valueField = input))
        } else {
            val valueInAcceptableRange =
                input.length <= 9 || (input.length <= 10 && input.first() == '-')
            val convertedValue = input.toIntOrNull()
            if (valueInAcceptableRange && convertedValue != null) {
                updateCounterItem(counterItem.copy(valueField = convertedValue.toString()))
                updateCounter(getCounterById(counterId).copy(value = convertedValue))
            }
        }
    }

    private fun onCounterValueInputDone(counterId: String) {
        viewModelScope.launch {
            _screenEvents.emit(OneTimeEvent.ClearFocus)
        }
        fillEmptyFields(counterId)
    }

    private fun onCounterChangeColor(counterId: String, newColor: Color) {
        val counter = getCounterById(counterId)
        updateCounter(counter.copy(color = newColor))
    }

    private fun openEditDialog(counterId: String) {
        viewModelScope.launch {
            val openForCounterItem = getCounterItemById(counterId)
            val openForCounter = getCounterById(counterId)
            _screenState.emit(
                _screenState.value.copy(
                    editDialog = EditDialogState(openForCounterItem, openForCounter)
                )
            )
        }
    }

    private fun closeEditDialog(cancelEdits: Boolean) {
        viewModelScope.launch {
            val editDialogState = _screenState.value.editDialog!!
            fillEmptyFields(editDialogState.itemState.counterId)
            if (cancelEdits) updateCounter(editDialogState.getInitialCounterState())
            _screenState.emit(_screenState.value.copy(editDialog = null))
        }
    }

    private fun fillEmptyFields(counterId: String) {
        val counterItem = getCounterItemById(counterId)
        if (counterItem.valueField.isEmpty() || counterItem.valueField == "-") {
            val counter = getCounterById(counterId)
            updateCounterItem(counterItem)
            updateCounter(counter.copy(value = 0))
        }
    }

    private fun updateCounter(newCounterState: Counter) {
        viewModelScope.launch {
            counterListRepository.updateCounter(newCounterState)
        }
    }

    private fun updateCounterItem(newCounterItem: CounterItem) {
        viewModelScope.launch {
            val newCounterItems = _screenState.value.counterItems.map { oldCounterItem ->
                if (oldCounterItem.counterId == newCounterItem.counterId) newCounterItem
                else oldCounterItem
            }

            _screenState.update {
                it.copy(
                    counterItems = newCounterItems,
                    editDialog = getUpdatedEditDialogState(newCounterItem)
                )
            }
        }
    }

    private fun getUpdatedEditDialogState(newCounterItemList: List<CounterItem>): EditDialogState? {
        val oldEditDialogCounterId = _screenState.value.editDialog?.itemState?.counterId
        val editDialogCounterItem =
            newCounterItemList.find { it.counterId == oldEditDialogCounterId }
        return editDialogCounterItem?.let { getUpdatedEditDialogState(it) }
    }

    private fun getUpdatedEditDialogState(newCounterItem: CounterItem): EditDialogState? {
        val oldEditDialogState = _screenState.value.editDialog
        return if (oldEditDialogState?.itemState?.counterId == newCounterItem.counterId) {
            oldEditDialogState.copy(itemState = newCounterItem)
        } else oldEditDialogState
    }


    fun getCounterItemById(id: String): CounterItem =
        _screenState.value.counterItems.find { it.counterId == id }
            ?: throw NoSuchElementException("Counter item with id = $id wasn't found")

    private fun getCounterById(counterId: String): Counter =
        currentCounterList.find { it.id == counterId }
            ?: throw NoSuchElementException("Counter with id = $counterId wasn't found")
}