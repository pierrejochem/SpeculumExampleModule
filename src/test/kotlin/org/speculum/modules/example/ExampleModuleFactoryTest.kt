package org.speculum.modules.example

import org.speculum.config.ModuleConfig
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ExampleModuleFactoryTest {
    private val factory = ExampleModuleFactory()

    @Test
    fun `name is example`() {
        assertEquals("example", factory.name)
    }

    @Test
    fun `create returns an ExampleModule`() {
        val module = factory.create(ModuleConfig(module = "example"))
        assertIs<ExampleModule>(module)
    }

    @Test
    fun `defaultConfig exposes the documented defaults`() {
        val config = factory.defaultConfig()

        assertEquals("example", config.module)
        assertEquals("top_center", config.position)
        assertEquals(3000L, config.refreshIntervalMs)
        assertEquals("Loaded from JAR!", config.config["greeting"])
        assertEquals("2", config.config["tickStep"])
    }
}
