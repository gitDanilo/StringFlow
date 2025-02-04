package danilo.cej.stringflow.settings

import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel

class AppSettingsComponent(initialState: AppState) {
    private val tfProcess = JBTextField(initialState.process, 15).apply {
        emptyText.text = "bash"
    }
    private val tfScriptFile = JBTextField(initialState.scriptFile).apply {
        emptyText.text = "/Users/john/StringFlowScripts/create_string.sh"
    }
    private val tfArguments = JBTextField(initialState.arguments).apply {
        emptyText.text = "--test1 --test2"
    }
    private val cbWorkingDir = JBCheckBox("Use project's working directory", initialState.useWorkingDir)
    private val cbShowOutput = JBCheckBox("Show command output", initialState.showOutput)

    val panel: JPanel = FormBuilder.createFormBuilder()
        .addLabeledComponent(JBLabel("Process name:"), tfProcess, 1, false)
        .addLabeledComponent(JBLabel("Script file:"), tfScriptFile, 1, false)
        .addLabeledComponent(JBLabel("Arguments:"), tfArguments, 1, false)
        .addComponent(cbWorkingDir)
        .addComponent(cbShowOutput)
        .addComponentFillVertically(JPanel(), 0)
        .panel

    val preferredFocusedComponent: JComponent
        get() = tfProcess

    private var process: String
        get() = tfProcess.text
        set(value) {
            tfProcess.text = value
        }

    private var command: String
        get() = tfScriptFile.text
        set(value) {
            tfScriptFile.text = value
        }

    private var arguments: String
        get() = tfArguments.text
        set(value) {
            tfArguments.text = value
        }

    private var useWorkingDir: Boolean
        get() = cbWorkingDir.isSelected
        set(value) {
            cbWorkingDir.isSelected = value
        }

    private var showOutput: Boolean
        get() = cbShowOutput.isSelected
        set(value) {
            cbShowOutput.isSelected = value
        }

    fun applyToState(state: AppState) {
        state.process = process
        state.scriptFile = command
        state.arguments = arguments
        state.useWorkingDir = useWorkingDir
        state.showOutput = showOutput
    }

    fun toState(): AppState {
        val state = AppState()
        state.process = process
        state.scriptFile = command
        state.arguments = arguments
        state.useWorkingDir = useWorkingDir
        state.showOutput = showOutput
        return state
    }
}