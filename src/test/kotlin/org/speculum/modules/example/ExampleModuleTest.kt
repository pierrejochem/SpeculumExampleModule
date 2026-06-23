package org.speculum.modules.example

import org.speculum.config.ModuleConfig
import kotlin.test.Test
import kotlin.test.assertEquals

class ExampleModuleTest {
    private fun moduleWithInterval(intervalMs: Long): ExampleModule =
        ExampleModule(ModuleConfig(module = "example", refreshIntervalMs = intervalMs))

    @Test
    fun `refreshIntervalMs is coerced up to the 1000ms floor`() {
        assertEquals(1000L, moduleWithInterval(500L).refreshIntervalMs)
    }

    @Test
    fun `refreshIntervalMs above the floor passes through unchanged`() {
        assertEquals(5000L, moduleWithInterval(5000L).refreshIntervalMs)
    }
}
