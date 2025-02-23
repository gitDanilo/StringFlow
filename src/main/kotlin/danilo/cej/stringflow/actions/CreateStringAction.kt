package danilo.cej.stringflow.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import danilo.cej.stringflow.dialogs.CreateStringDialog

class CreateStringAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        event.project?.let {
            CreateStringDialog.show(it)
        }
    }
}