package danilo.cej.stringflow.settings

import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import javax.swing.JComponent

class AppSettingsConfigurable : Configurable {

    private var appSettingsComponent: AppSettingsComponent? = null

    override fun isModified(): Boolean {
        val serviceState = service<AppSettings>().state
        val settingsState = appSettingsComponent?.toState() ?: return true
        val isEqual = serviceState.compare(settingsState)
        return !isEqual
    }

    override fun apply() {
        val state = service<AppSettings>().state
        appSettingsComponent?.applyToState(state)
    }

    override fun getDisplayName(): String = "StringFlow Settings"

    override fun createComponent(): JComponent? {
        val initialState = service<AppSettings>().state
        appSettingsComponent = AppSettingsComponent(initialState)
        return appSettingsComponent?.panel
    }

}