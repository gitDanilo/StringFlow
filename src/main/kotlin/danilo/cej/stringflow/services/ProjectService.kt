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
import com.intellij.util.io.awaitExit
import danilo.cej.stringflow.StringFlowBundle
import danilo.cej.stringflow.settings.AppSettings
import kotlinx.coroutines.*
import java.io.File

typealias Callback = () -> Unit

@Service(Service.Level.PROJECT)
class ProjectService(private val project: Project) {

    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    init {
        thisLogger().info(StringFlowBundle.message("projectService", project.name))
    }

//    private val client = HttpClient(CIO) {
//        engine {
//            requestTimeout = 5000
//        }
//    }

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

    suspend fun executeCommand(command: List<String>, workingDir: String? = null): Pair<Boolean, String> =
        withContext(Dispatchers.IO) {
            try {
                val process = ProcessBuilder(command)
                    .directory(File(workingDir ?: System.getProperty("user.home")))
                    .redirectErrorStream(true)
                    .start()
                val output = process.inputStream.bufferedReader().use { it.readText() }
                val exitCode = process.awaitExit()
                Pair(exitCode == 0, output)
            } catch (e: Exception) {
                Pair(false, "Error executing command: $e")
            }
        }

    fun createString(key: String, text: String) {
        val settings = service<AppSettings>().state
        val projectPath = if (settings.useWorkingDir) project.basePath else null
        val processName = settings.process ?: ""
        val scriptFile = settings.scriptFile ?: ""
        val arguments = settings.arguments?.split(" ") ?: listOf()
        val showOutput = settings.showOutput

        if (processName.isEmpty()) {
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
            val result = executeCommand(listOf(processName, scriptFile) + arguments + listOf(key, text), projectPath)
            if (result.first) {
                showNotification(
                    "String created",
                    if (showOutput) result.second else "\"$key\"",
                    NotificationType.INFORMATION
                )
            } else {
                showNotification(
                    "Error creating string",
                    if (showOutput) result.second else "\"$key\".",
                    NotificationType.ERROR
                )
            }
            thisLogger().info("Command output: ${result.second}")
        }
    }

//    suspend fun testRequest(): String? = withContext(Dispatchers.IO) {
//        thisLogger().info("testRequest")
//        val response = client.get("https://pastebin.com/raw/5WVJxKKF")
//        val result = if (response.status.value / 100 == 2) response.bodyAsText() else null
//        return@withContext result
//    }
}
