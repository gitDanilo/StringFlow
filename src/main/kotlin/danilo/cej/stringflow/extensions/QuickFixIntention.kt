package danilo.cej.stringflow.extensions

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import danilo.cej.stringflow.dialogs.CreateStringDialog

class QuickFixIntention : IntentionAction {
    override fun startInWriteAction(): Boolean = false

    override fun getFamilyName(): String = "Create new string resource"

    override fun getText(): String = "Create new string resource from selection"

    override fun isAvailable(project: Project, editor: Editor?, psiFile: PsiFile?): Boolean {
        return editor?.selectionModel?.hasSelection() ?: false
    }

    override fun invoke(project: Project, editor: Editor?, psiFile: PsiFile?) {
        if (editor == null) {
            thisLogger().warn("Editor not available")
            return
        }

        val selectedText = editor.selectionModel.selectedText
        if (selectedText.isNullOrEmpty()) {
            thisLogger().warn("No text selected")
            return
        }

        CreateStringDialog.show(project, selectedText)
    }
}