package org.speculum.modules.example

import org.speculum.config.ModuleConfig
import org.speculum.config.SettingType
import org.speculum.config.undeclaredKeys
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

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

    @Test
    fun `settingsSchema declares every key the default config ships`() {
        val undeclared = factory.settingsSchema().undeclaredKeys(factory.defaultConfig().config)

        assertEquals(emptyList(), undeclared, "defaultConfig keys missing from settingsSchema")
    }

    @Test
    fun `settingsSchema describes the documented defaults and bounds`() {
        val schema = factory.settingsSchema()

        assertEquals(listOf("greeting", "tickStep"), schema.map { it.key })

        val greeting = schema.first { it.key == "greeting" }
        assertEquals(SettingType.STRING, greeting.type)
        assertEquals("Hello, Speculum!", greeting.default)

        val tickStep = schema.first { it.key == "tickStep" }
        assertEquals(SettingType.INT, tickStep.type)
        assertEquals("1", tickStep.default)
        assertEquals(1, tickStep.min)
        assertEquals(100, tickStep.max)
    }

    @Test
    fun `every schema entry is labelled and helpful`() {
        for (spec in factory.settingsSchema()) {
            assertTrue(spec.label.isNotBlank(), "${spec.key}: blank label")
            assertTrue(spec.help.isNotBlank(), "${spec.key}: blank help")
        }
    }
}
