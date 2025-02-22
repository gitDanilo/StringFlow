package danilo.cej.stringflow.settings

import com.intellij.openapi.components.*

@Service
@State(name = "danilo.cej.stringflow.settings.AppSettings", storages = [Storage("StringFlowSettings.xml")])
class AppSettings : SimplePersistentStateComponent<AppState>(AppState())

class AppState : BaseState() {
    enum class Directory(val value: Int, val desc: String) {
        PROJECT(0, "Project"),
        SCRIPT(1, "Script"),
        CUSTOM(2, "Custom");

        companion object {
            fun fromString(desc: String?, fallback: Directory = PROJECT) =
                entries.firstOrNull { it.desc == desc } ?: fallback
        }
    }

    var processPath by string()
    var scriptPath by string()
    var arguments by string()
    var workingDirType: Int by property(0)
    var workingDir by string()

    fun compare(other: AppState): Boolean {
        return (processPath == other.processPath &&
                scriptPath == other.scriptPath &&
                arguments == other.arguments &&
                workingDirType == other.workingDirType &&
                workingDir == other.workingDir)
    }
}