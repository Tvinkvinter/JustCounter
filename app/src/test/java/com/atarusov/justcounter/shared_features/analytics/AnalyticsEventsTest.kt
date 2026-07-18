package com.atarusov.justcounter.shared_features.analytics

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class AnalyticsEventsTest {

    @Test
    fun appOpenUsesStandardNameAndOmitsAutomaticallyCollectedVersion() {
        val event = AnalyticsEvents.appOpen(
            isFirstOpen = true,
            daysSinceFirstOpen = 3,
            countersCount = 4,
            categoriesCount = 2,
        )

        assertEquals("app_open", event.name)
        assertEquals(1L, event.parameters["is_first_open"])
        assertEquals(3L, event.parameters["days_since_first_open"])
        assertFalse(event.parameters.containsKey("app_version"))
    }

    @Test
    fun screenViewUsesFirebaseScreenParameters() {
        val event = AnalyticsEvents.screenView(
            destination = AnalyticsDestination.EditCounterDialog,
            source = AnalyticsDestination.CounterList.value,
            countersCount = 5,
            categoriesCount = 2,
        )

        assertEquals("screen_view", event.name)
        assertEquals("edit_counter_dialog", event.parameters["screen_name"])
        assertEquals("edit_counter_dialog", event.parameters["screen_class"])
        assertEquals("counter_list", event.parameters["source"])
    }

    @Test
    fun counterDeletedMatchesReducedContract() {
        val event = AnalyticsEvents.counterDeleted(
            surface = AnalyticsSurface.CounterFullscreen,
            valueNonzero = true,
            hasCustomSteps = false,
            countersCountAfter = 1,
        )

        assertEquals("counter_deleted", event.name)
        assertEquals(1L, event.parameters["value_nonzero"])
        assertEquals(0L, event.parameters["has_custom_steps"])
        assertFalse(event.parameters.containsKey("counter_age_days"))
        assertFalse(event.parameters.containsKey("has_custom_title"))
    }

    @Test
    fun counterCreatedContainsFirstEventAndCategoryMetadata() {
        val event = AnalyticsEvents.counterCreated(
            isFirstForUser = true,
            surface = AnalyticsSurface.CounterList,
            categoryType = AnalyticsCategoryType.Custom,
            countersCountAfter = 3,
        )

        assertEquals("counter_created", event.name)
        assertEquals(1L, event.parameters["is_first_for_user"])
        assertEquals("counter_list", event.parameters["surface"])
        assertEquals("custom", event.parameters["category_type"])
        assertEquals(3L, event.parameters["counters_count_after"])
    }

    @Test
    fun counterValueChangedContainsAtomicChangeMetadata() {
        val event = AnalyticsEvents.counterValueChanged(
            isFirstForUser = false,
            surface = AnalyticsSurface.CounterFullscreen,
            direction = AnalyticsDirection.Minus,
            step = 5,
            oldValue = 12,
            newValue = 7,
            hasCustomSteps = true,
        )

        assertEquals("counter_value_changed", event.name)
        assertEquals("counter_fullscreen", event.parameters["surface"])
        assertEquals("minus", event.parameters["direction"])
        assertEquals(5L, event.parameters["step"])
        assertEquals(12L, event.parameters["old_value"])
        assertEquals(7L, event.parameters["new_value"])
        assertEquals(1L, event.parameters["has_custom_steps"])
    }

    @Test
    fun removeModeToggledUsesNumericBoolean() {
        val event = AnalyticsEvents.removeModeToggled(
            enabled = true,
            surface = AnalyticsSurface.CounterList,
        )

        assertEquals("remove_mode_toggled", event.name)
        assertEquals(1L, event.parameters["enabled"])
        assertEquals("counter_list", event.parameters["surface"])
    }

    @Test
    fun screenClosedOmitsCloseReason() {
        val event = AnalyticsEvents.screenClosed(
            destination = AnalyticsDestination.CategoryDrawer,
            durationSec = 12,
        )

        assertEquals("screen_closed", event.name)
        assertEquals("category_drawer", event.parameters["destination"])
        assertEquals(12L, event.parameters["duration_sec"])
        assertFalse(event.parameters.containsKey("closed_by"))
    }

    @Test
    fun counterEditClosedUsesNumericBooleans() {
        val event = AnalyticsEvents.counterEditClosed(
            saved = true,
            changedTitle = false,
            changedValue = true,
            changedColor = false,
            changedSteps = true,
            durationSec = 7,
        )

        assertEquals("counter_edit_closed", event.name)
        assertEquals(1L, event.parameters["saved"])
        assertEquals(0L, event.parameters["changed_title"])
        assertEquals(1L, event.parameters["changed_value"])
        assertEquals(7L, event.parameters["duration_sec"])
    }
}
