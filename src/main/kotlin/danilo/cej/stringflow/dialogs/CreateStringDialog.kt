package danilo.cej.stringflow.dialogs

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import danilo.cej.stringflow.services.ProjectService
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.SwingUtilities

class CreateStringDialog(keyName: String?) : DialogWrapper(true) {
    private val tfStringKey = JBTextField(keyName, 40).apply {
        emptyText.text = "example_welcome_message"
    }

    private val tfStringContent = JBTextField(40).apply {
        emptyText.text = "Welcome to example screen"
    }

    private val tfStringDescription = JBTextField(40).apply {
        emptyText.text = "Welcome message"
    }

    private val cbNoTranslation = JBCheckBox("Do not translate")

    val key: String
        get() = tfStringKey.text

    val text: String
        get() = tfStringContent.text

    init {
        title = "Create String Resource"
        init()
    }

    override fun createCenterPanel(): JComponent {
        val gbc = GridBagConstraints().apply {
            gridx = 0
            gridy = 0
            weightx = 1.0
            fill = GridBagConstraints.HORIZONTAL
        }
        val form = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("Key:"), JPanel().apply {
                layout = GridBagLayout()
                add(tfStringKey, gbc)
            })
            .addLabeledComponent(JBLabel("Content:"), JPanel().apply {
                layout = GridBagLayout()
                add(tfStringContent, gbc)
            })
            .addLabeledComponent(JBLabel("Description:"), JPanel().apply {
                layout = GridBagLayout()
                add(tfStringDescription, gbc)
            })
            .addComponent(cbNoTranslation)
            .addComponentFillVertically(JPanel(), 0)
        val centerPanel = form.panel.apply {
            minimumSize = preferredSize
        }
        SwingUtilities.invokeLater {
            if (tfStringKey.text.isNullOrEmpty()) {
                tfStringKey.requestFocus()
            } else {
                tfStringContent.requestFocus()
            }
        }
        return centerPanel
    }

    companion object {
        fun show(project: Project, keyName: String? = null) {
            val dialog = CreateStringDialog(keyName)
            val ok = dialog.showAndGet()
            if (ok) {
                val service = project.service<ProjectService>()
                service.createString(dialog.key, dialog.text)
            }
        }
    }
}