package danilo.cej.stringflow.windows

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBTabbedPane
import com.intellij.ui.components.JBTextField
import com.intellij.ui.content.ContentFactory
import com.intellij.util.ui.UIUtil
import danilo.cej.stringflow.services.ProjectService
import java.awt.Component
import java.awt.FlowLayout
import javax.swing.BoxLayout
import javax.swing.JComponent
import javax.swing.JTextField


class ToolWindowFactory : ToolWindowFactory {

    val TOOL_WINDOW_ID = "StringFlowToolWindow"

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val stringFlowWindow = StringFlowToolWindow(toolWindow)
        val content = ContentFactory.getInstance().createContent(stringFlowWindow.getContent(), null, false)
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true

    class StringFlowToolWindow(toolWindow: ToolWindow) {

        private val service = toolWindow.project.service<ProjectService>()

        class MainPanel : JBPanel<MainPanel>() {
            init {
                layout = BoxLayout(this, BoxLayout.Y_AXIS)

                add(JBLabel("String 1"))

                val keyPanel = JBPanel<JBPanel<*>>().apply {
                    layout = FlowLayout(FlowLayout.LEFT)
                    add(JBLabel("String key: "))
                    add(JBTextField("key", 20))
                    maximumSize = preferredSize
                }
                keyPanel.alignmentX = Component.LEFT_ALIGNMENT
                add(keyPanel)


                val stringPanel = JBPanel<JBPanel<*>>().apply {
                    layout = FlowLayout(FlowLayout.LEFT)
                    add(JBLabel("English text: "))
                    add(JTextField("text", 20))
                    maximumSize = preferredSize
                }
                stringPanel.alignmentX = Component.LEFT_ALIGNMENT
                add(stringPanel)
            }
        }

        class SettingsPanel : JBPanel<MainPanel>() {
            init {
                add(JBLabel("Settings panel label", UIUtil.ComponentStyle.LARGE))
            }
        }

        fun getContent(): JComponent {
            val tabbedPane = JBTabbedPane()

            val mainPanel = MainPanel()
            tabbedPane.addTab("Strings", mainPanel)

            val settingsPanel = SettingsPanel()
            tabbedPane.addTab("Settings", settingsPanel)

            return tabbedPane
        }
    }
}
