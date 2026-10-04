package org.speculum.modules.example

import org.speculum.config.ModuleConfig
import org.speculum.config.SettingSpec
import org.speculum.config.SettingType
import org.speculum.core.MirrorModule
import org.speculum.core.ModuleFactory

/**
 * SPI entry point discovered by the host app via ServiceLoader (declared in
 * META-INF/services/org.speculum.core.ModuleFactory). Provides the module
 * name, a constructor, a default placement so it appears automatically, and
 * the schema the admin console renders its settings from.
 */
class ExampleModuleFactory : ModuleFactory {
    override val name: String = "example"

    override fun create(config: ModuleConfig): MirrorModule = ExampleModule(config)

    override fun defaultConfig(): ModuleConfig =
        ModuleConfig(
            module = "example",
            position = "top_center",
            refreshIntervalMs = 3000,
            config = mapOf("greeting" to "Loaded from JAR!", "tickStep" to "2"),
        )

    /**
     * Declares the `config` keys above so the Speculum admin console shows
     * labelled, typed controls instead of raw key/value text rows. Each
     * `default` is the fallback [ExampleModule] itself reads, which is what the
     * console shows as a hint while the key is unset — so it is deliberately
     * not the same as the starting value [defaultConfig] ships.
     */
    override fun settingsSchema(): List<SettingSpec> =
        listOf(
            SettingSpec(
                key = "greeting",
                label = "Greeting",
                default = "Hello, Speculum!",
                help = "Headline text shown at the top of the module.",
            ),
            SettingSpec(
                key = "tickStep",
                label = "Tick step",
                type = SettingType.INT,
                default = "1",
                min = 1,
                max = 100,
                help = "How much the refresh counter advances on each refresh.",
            ),
        )
}
