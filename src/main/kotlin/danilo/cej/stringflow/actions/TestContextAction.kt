package danilo.cej.stringflow.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages

class TestContextAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val editor = event.getData(CommonDataKeys.EDITOR) ?: run {
            println("Editor not available")
            return
        }

        val selectedText = editor.selectionModel.selectedText
        if (selectedText.isNullOrEmpty()) {
            println("No text selected")
            return
        }

        Messages.showMessageDialog(
            "Text selected from editor: $selectedText",
            "My Context Action",
            Messages.getInformationIcon(),
        )
    }
}