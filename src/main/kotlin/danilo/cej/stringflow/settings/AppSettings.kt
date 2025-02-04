package danilo.cej.stringflow.settings

import com.intellij.openapi.components.*

@Service
@State(name = "danilo.cej.stringflow.settings.AppSettings", storages = [Storage("StringFlowSettings.xml")])
class AppSettings : SimplePersistentStateComponent<AppState>(AppState())

class AppState : BaseState() {
    var process by string()
    var scriptFile by string()
    var arguments by string()
    var useWorkingDir: Boolean by property(false)
    var showOutput: Boolean by property(false)

    fun compare(other: AppState): Boolean {
        return (process == other.process &&
                scriptFile == other.scriptFile &&
                arguments == other.arguments &&
                useWorkingDir == other.useWorkingDir &&
                showOutput == other.showOutput)
    }
}