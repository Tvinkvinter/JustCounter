package com.atarusov.justcounter.shared_features.analytics

internal data class AnalyticsEvent(
    val name: String,
    val parameters: Map<String, Any>,
)

enum class AnalyticsDestination(val value: String) {
    CounterList("counter_list"),
    CounterFullscreen("counter_fullscreen"),
    EditCounterDialog("edit_counter_dialog"),
    CategoryDrawer("category_drawer"),
}

enum class AnalyticsSurface(val value: String) {
    CounterList("counter_list"),
    CounterFullscreen("counter_fullscreen"),
}

enum class AnalyticsCategoryType(val value: String) {
    Uncategorized("uncategorized"),
    Custom("custom"),
}

enum class AnalyticsDirection(val value: String) {
    Plus("plus"),
    Minus("minus"),
}

internal object AnalyticsEvents {
    private const val APP_OPEN = "app_open"
    private const val SCREEN_VIEW = "screen_view"
    private const val SCREEN_CLOSED = "screen_closed"
    private const val COUNTER_CREATED = "counter_created"
    private const val COUNTER_VALUE_CHANGED = "counter_value_changed"
    private const val COUNTER_DELETED = "counter_deleted"
    private const val REMOVE_MODE_TOGGLED = "remove_mode_toggled"
    private const val COUNTER_EDIT_CLOSED = "counter_edit_closed"

    fun appOpen(
        isFirstOpen: Boolean,
        daysSinceFirstOpen: Long,
        countersCount: Long,
        categoriesCount: Long,
    ) = AnalyticsEvent(
        name = APP_OPEN,
        parameters = mapOf(
            "is_first_open" to isFirstOpen.asAnalyticsLong(),
            "days_since_first_open" to daysSinceFirstOpen,
            "counters_count" to countersCount,
            "categories_count" to categoriesCount,
        ),
    )

    fun screenView(
        destination: AnalyticsDestination,
        source: String,
        countersCount: Long,
        categoriesCount: Long,
    ) = AnalyticsEvent(
        name = SCREEN_VIEW,
        parameters = mapOf(
            "screen_name" to destination.value,
            "screen_class" to destination.value,
            "source" to source,
            "counters_count" to countersCount,
            "categories_count" to categoriesCount,
        ),
    )

    fun screenClosed(
        destination: AnalyticsDestination,
        durationSec: Long,
    ) = AnalyticsEvent(
        name = SCREEN_CLOSED,
        parameters = mapOf(
            "destination" to destination.value,
            "duration_sec" to durationSec,
        ),
    )

    fun counterCreated(
        isFirstForUser: Boolean,
        surface: AnalyticsSurface,
        categoryType: AnalyticsCategoryType,
        countersCountAfter: Long,
    ) = AnalyticsEvent(
        name = COUNTER_CREATED,
        parameters = mapOf(
            "is_first_for_user" to isFirstForUser.asAnalyticsLong(),
            "surface" to surface.value,
            "category_type" to categoryType.value,
            "counters_count_after" to countersCountAfter,
        ),
    )

    fun counterValueChanged(
        isFirstForUser: Boolean,
        surface: AnalyticsSurface,
        direction: AnalyticsDirection,
        step: Long,
        oldValue: Long,
        newValue: Long,
        hasCustomSteps: Boolean,
    ) = AnalyticsEvent(
        name = COUNTER_VALUE_CHANGED,
        parameters = mapOf(
            "is_first_for_user" to isFirstForUser.asAnalyticsLong(),
            "surface" to surface.value,
            "direction" to direction.value,
            "step" to step,
            "old_value" to oldValue,
            "new_value" to newValue,
            "has_custom_steps" to hasCustomSteps.asAnalyticsLong(),
        ),
    )

    fun counterDeleted(
        surface: AnalyticsSurface,
        valueNonzero: Boolean,
        hasCustomSteps: Boolean,
        countersCountAfter: Long,
    ) = AnalyticsEvent(
        name = COUNTER_DELETED,
        parameters = mapOf(
            "surface" to surface.value,
            "value_nonzero" to valueNonzero.asAnalyticsLong(),
            "has_custom_steps" to hasCustomSteps.asAnalyticsLong(),
            "counters_count_after" to countersCountAfter,
        ),
    )

    fun removeModeToggled(
        enabled: Boolean,
        surface: AnalyticsSurface,
    ) = AnalyticsEvent(
        name = REMOVE_MODE_TOGGLED,
        parameters = mapOf(
            "enabled" to enabled.asAnalyticsLong(),
            "surface" to surface.value,
        ),
    )

    fun counterEditClosed(
        saved: Boolean,
        changedTitle: Boolean,
        changedValue: Boolean,
        changedColor: Boolean,
        changedSteps: Boolean,
        durationSec: Long,
    ) = AnalyticsEvent(
        name = COUNTER_EDIT_CLOSED,
        parameters = mapOf(
            "saved" to saved.asAnalyticsLong(),
            "changed_title" to changedTitle.asAnalyticsLong(),
            "changed_value" to changedValue.asAnalyticsLong(),
            "changed_color" to changedColor.asAnalyticsLong(),
            "changed_steps" to changedSteps.asAnalyticsLong(),
            "duration_sec" to durationSec,
        ),
    )
}

private fun Boolean.asAnalyticsLong() = if (this) 1L else 0L
