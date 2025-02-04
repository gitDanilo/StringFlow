package danilo.cej.stringflow.dialogs

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBTextField
import danilo.cej.stringflow.services.ProjectService
import java.awt.Component
import java.awt.FlowLayout
import javax.swing.BoxLayout
import javax.swing.JComponent
import javax.swing.SwingUtilities

class CreateStringDialog(keyName: String?) : DialogWrapper(true) {
    private val keyInput = JBTextField(keyName, 30).apply {
        emptyText.text = "example_welcome_message"
    }
    private val textInput = JBTextField(30).apply {
        emptyText.text = "Welcome to example screen"
    }

    val key: String
        get() = keyInput.text

    val text: String
        get() = textInput.text

    init {
        title = "Create String Resource"
        init()
    }

    override fun createCenterPanel(): JComponent {
        val panel = JBPanel<JBPanel<*>>().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)

            val keyPanel = JBPanel<JBPanel<*>>().apply {
                layout = FlowLayout(FlowLayout.LEFT)
                add(JBLabel("String key:"))
                add(keyInput)
                maximumSize = preferredSize
            }
            keyPanel.alignmentX = Component.RIGHT_ALIGNMENT
            add(keyPanel)

            val stringPanel = JBPanel<JBPanel<*>>().apply {
                layout = FlowLayout(FlowLayout.LEFT)
                add(JBLabel("Text:"))
                add(textInput)
                maximumSize = preferredSize
            }
            stringPanel.alignmentX = Component.RIGHT_ALIGNMENT
            add(stringPanel)
        }
        SwingUtilities.invokeLater {
            if (keyInput.text.isNullOrEmpty()) {
                keyInput.requestFocus()
            } else {
                textInput.requestFocus()
            }
        }
        return panel
    }

    companion object {
        fun show(project: Project, keyName: String?) {
            val dialog = CreateStringDialog(keyName)
            val ok = dialog.showAndGet()
            if (ok) {
                val service = project.service<ProjectService>()
                service.createString(dialog.key, dialog.text)
            }
        }
    }
}