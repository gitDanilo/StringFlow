package danilo.cej.stringflow.services

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project
import com.intellij.platform.ide.progress.withBackgroundProgress
import com.intellij.util.io.awaitExit
import danilo.cej.stringflow.StringFlowBundle
import danilo.cej.stringflow.settings.AppSettings
import danilo.cej.stringflow.settings.AppState
import kotlinx.coroutines.*
import java.io.File

typealias Callback = () -> Unit

@Service(Service.Level.PROJECT)
class ProjectService(private val project: Project) {

    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    init {
        thisLogger().info(StringFlowBundle.message("projectService", project.name))
    }

    private suspend fun executeCommand(command: List<String>, workingDir: String): Pair<Boolean, String> =
        withContext(Dispatchers.IO) {
            val process = ProcessBuilder(command)
                .directory(File(workingDir))
                .redirectErrorStream(true)
                .start()

            try {
                val output = async { process.inputStream.bufferedReader().use { it.readText() } }
                val exitCode = process.awaitExit()
                Pair(exitCode == 0, output.await())
            } catch (e: CancellationException) {
                process.destroyForcibly()
                throw e
            } catch (e: Exception) {
                Pair(false, "Error executing command: $e")
            }
        }

    private val workingDir: String
        get() {
            val settings = service<AppSettings>().state
            val dirType = AppState.Directory.fromValue(settings.workingDirType)
            return when (dirType) {
                AppState.Directory.PROJECT -> project.basePath ?: ""
                AppState.Directory.SCRIPT -> settings.scriptPath?.let { File(it).parent } ?: ""
                AppState.Directory.CUSTOM -> settings.workingDir ?: ""
            }
        }

    fun showNotification(
        title: String,
        content: String,
        type: NotificationType,
        action: Pair<String, Callback>? = null
    ) {
        val notificationGroup = NotificationGroupManager.getInstance()
            .getNotificationGroup("StringFlow Notification Group")

        val notification = notificationGroup.createNotification(title, content, type)
        action?.let {
            notification.addAction(object : AnAction(it.first) {
                override fun actionPerformed(e: AnActionEvent) {
                    it.second.invoke()
                }
            })
        }

        notification.notify(project)
    }

    fun createString(key: String, text: String) {
        val settings = service<AppSettings>().state
        val processPath = settings.processPath ?: ""
        val scriptPath = settings.scriptPath ?: ""
        val arguments = settings.arguments?.split(" ") ?: listOf()

        if (processPath.isEmpty()) {
            showNotification(
                "StringFlow",
                "Process not configured.",
                NotificationType.ERROR,
                Pair("Open Settings") {
                    ShowSettingsUtil.getInstance().showSettingsDialog(project, "StringFlow Settings")
                }
            )
            return
        }

        scope.launch {
            withBackgroundProgress(project, "Creating string", true) {
                val command = listOf(processPath) + listOf(scriptPath) + arguments + listOf(key, text)
                val result = executeCommand(command, workingDir)
                if (result.first) {
                    showNotification(
                        "String created",
                        "\"$key\"",
                        NotificationType.INFORMATION
                    )
                } else {
                    showNotification(
                        "Error creating string",
                        "\"$key\".",
                        NotificationType.ERROR
                    )
                }
                thisLogger().info("Command output: ${result.second}")
            }
        }
    }
}
