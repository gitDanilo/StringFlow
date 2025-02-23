package danilo.cej.stringflow.settings

import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import java.awt.Dimension
import javax.swing.BoxLayout
import javax.swing.JComponent
import javax.swing.JPanel

class AppSettingsComponent(initialState: AppState) {
    private val tfProcessPath = JBTextField(initialState.processPath).apply {
        emptyText.text = "/bin/bash"
    }

    private val tfScriptPath = JBTextField(initialState.scriptPath).apply {
        emptyText.text = "/Users/user/StringFlowScripts/create_string.sh"
    }

    private val tfArguments = JBTextField(initialState.arguments).apply {
        emptyText.text = "--verbose --overwrite"
    }

    private val cbWorkingDir = ComboBox(AppState.Directory.entries.map { it.desc }.toTypedArray()).apply {
        selectedIndex = initialState.workingDirType
        maximumSize = Dimension(120, Integer.MAX_VALUE)
        addActionListener { action ->
            if (action.actionCommand == "comboBoxChanged") {
                val dirType = AppState.Directory.fromIndex(selectedIndex)
                when (dirType) {
                    AppState.Directory.PROJECT,
                    AppState.Directory.SCRIPT -> {
                        btfWorkingDir.isVisible = false
                    }

                    AppState.Directory.CUSTOM -> {
                        btfWorkingDir.isVisible = true
                    }
                }
            }
        }
    }

    private val tfWorkingDir = JBTextField(initialState.workingDir).apply {
        emptyText.text = "/Users/user/StringFlowScripts"
    }

    private val btfWorkingDir = TextFieldWithBrowseButton(tfWorkingDir).apply {
        isVisible = initialState.workingDirType == AppState.Directory.CUSTOM.value
        addActionListener {
            openFileChooser("Select the working directory", "Choose a folder.", files = false) { file ->
                file?.let { tfWorkingDir.text = it.path }
            }
        }
    }

    val panel: JPanel
        get() {
            val btfProcessPath = TextFieldWithBrowseButton(tfProcessPath).apply {
                addActionListener {
                    openFileChooser("Select the process executable", "Choose an executable file.") { file ->
                        file?.let { tfProcessPath.text = it.path }
                    }
                }
            }
            val btfScriptPath = TextFieldWithBrowseButton(tfScriptPath).apply {
                addActionListener {
                    openFileChooser("Select the script file", "Choose an script file.") { file ->
                        file?.let { tfScriptPath.text = it.path }
                    }
                }
            }
            val panel = JPanel().apply {
                layout = BoxLayout(this, BoxLayout.X_AXIS)
                add(cbWorkingDir)
                add(btfWorkingDir)
            }
            return FormBuilder.createFormBuilder()
                .addLabeledComponent(JBLabel("Process path:"), btfProcessPath, 1, false)
                .addLabeledComponent(JBLabel("Script path:"), btfScriptPath, 1, false)
                .addLabeledComponent(JBLabel("Arguments:"), tfArguments, 1, false)
                .addLabeledComponent(JBLabel("Working directory:"), panel, 1, false)
                .addComponentFillVertically(JPanel(), 0)
                .panel
        }

    private fun openFileChooser(
        title: String,
        description: String,
        files: Boolean = true,
        folders: Boolean = true,
        onFileChosen: (VirtualFile?) -> Unit
    ) {
        val fileChooserDescriptor = FileChooserDescriptor(
            files,
            folders,
            false,
            false,
            false,
            false,
        ).withTitle(title).withDescription(description)
        val file = FileChooser.chooseFile(fileChooserDescriptor, null, null)
        onFileChosen.invoke(file)
    }

    val preferredFocusedComponent: JComponent
        get() = tfProcessPath

    private val process: String
        get() = tfProcessPath.text

    private val command: String
        get() = tfScriptPath.text

    private val arguments: String
        get() = tfArguments.text

    private val workingDirType: Int
        get() {
            val index = cbWorkingDir.selectedIndex
            val dirType = AppState.Directory.fromIndex(index)
            return dirType.value
        }

    private val workingDir: String
        get() = tfWorkingDir.text

    fun applyToState(state: AppState) {
        state.processPath = process
        state.scriptPath = command
        state.arguments = arguments
        state.workingDirType = workingDirType
        state.workingDir = workingDir
    }

    fun toState(): AppState {
        val state = AppState()
        state.processPath = process
        state.scriptPath = command
        state.arguments = arguments
        state.workingDirType = workingDirType
        state.workingDir = workingDir
        return state
    }
}