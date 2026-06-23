package org.speculum.modules.example

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.v2.runComposeUiTest
import org.speculum.config.ModuleConfig
import org.speculum.core.Notification
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class ExampleModuleContentTest {
    private fun module(config: Map<String, String> = emptyMap()): ExampleModule =
        ExampleModule(ModuleConfig(module = "example", config = config))

    @Test
    fun `renders the default greeting`() =
        runComposeUiTest {
            setContent { module().Content() }
            onNodeWithText("Hello, Speculum!").assertIsDisplayed()
        }

    @Test
    fun `renders a configured greeting`() =
        runComposeUiTest {
            setContent { module(mapOf("greeting" to "Hi there")).Content() }
            onNodeWithText("Hi there").assertIsDisplayed()
        }

    @Test
    fun `refresh increments the tick counter by tickStep`() =
        runComposeUiTest {
            val mod = module(mapOf("tickStep" to "2"))
            setContent { mod.Content() }
            onNodeWithText("Refreshes: 0").assertIsDisplayed()

            mod.refresh()
            waitForIdle()

            onNodeWithText("Refreshes: 2").assertIsDisplayed()
        }

    @Test
    fun `onNotification updates the last event line`() =
        runComposeUiTest {
            val mod = module()
            setContent { mod.Content() }
            onNodeWithText("Last notification: (none)").assertIsDisplayed()

            mod.onNotification(Notification("evt"))
            waitForIdle()

            onNodeWithText("Last notification: evt").assertIsDisplayed()
        }
}
