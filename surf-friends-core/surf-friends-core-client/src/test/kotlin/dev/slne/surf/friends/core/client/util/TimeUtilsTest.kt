package dev.slne.surf.friends.core.client.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.time.ZoneOffset

class TimeUtilsTest {

    @Test
    fun `formats a moment in day-first notation`() {
        val moment = OffsetDateTime.of(2024, 3, 7, 9, 5, 42, 0, ZoneOffset.UTC)

        assertEquals("07.03.2024 09:05", moment.format())
    }

    @Test
    fun `formats a moment as it reads in its own offset`() {
        val moment = OffsetDateTime.of(2024, 12, 31, 23, 30, 0, 0, ZoneOffset.ofHours(2))

        assertEquals("31.12.2024 23:30", moment.format())
    }
}
