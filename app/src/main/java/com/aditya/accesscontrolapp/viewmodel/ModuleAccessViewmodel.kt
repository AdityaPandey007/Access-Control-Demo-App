package com.aditya.accesscontrolapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aditya.accesscontrolapp.manager.AccessManager
import com.aditya.accesscontrolapp.models.AppConfig
import com.aditya.accesscontrolapp.models.ModuleAccessState
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UiState(
    val modules: List<ModuleAccessState> = emptyList(),
    val coolingMessage: String? = null,
    val isLoading: Boolean = true
)

class ModuleAccessViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private lateinit var accessManager: AccessManager

    init {
        loadConfig()
        startCoolingCountdown()
    }

    /**
     * Load configuration from mock JSON
     * In production, this could load from assets or API
     */
    private fun loadConfig() {
        // Mock JSON data - modify times to test different scenarios
        val mockJson = """
        {
          "user": {
            "userType": "active",
            "coolingStartTime": "2025-10-30T10:00:00Z",
            "coolingEndTime": "2025-10-30T10:05:00Z",
            "accessibleModules": ["payments", "account_info"]
          },
          "modules": [
            {
              "id": "payments",
              "title": "Payments",
              "requiresConsent": true
            },
            {
              "id": "account_info",
              "title": "Account Info",
              "requiresConsent": false
            },
            {
              "id": "consent_center",
              "title": "Consent Center",
              "requiresConsent": true
            }
          ]
        }
        """.trimIndent()

        val config = Gson().fromJson(mockJson, AppConfig::class.java)
        accessManager = AccessManager(config)

        updateModuleStates()
    }

    /**
     * Start countdown timer that updates every second
     */
    private fun startCoolingCountdown() {
        viewModelScope.launch {
            while (true) {
                updateModuleStates()
                delay(1000) // Update every second
            }
        }
    }

    /**
     * Update module access states and cooling message
     */
    private fun updateModuleStates() {
        val config = accessManager.config
        val moduleStates = config.modules.map { module ->
            accessManager.checkModuleAccess(module.id)
        }

        _uiState.value = UiState(
            modules = moduleStates,
            coolingMessage = accessManager.getCoolingCountdown(),
            isLoading = false
        )
    }

    /**
     * Handle module click and return appropriate message
     */
    fun onModuleClicked(moduleState: ModuleAccessState): String {
        return if (moduleState.isAccessible) {
            "Navigating to ${moduleState.module.title}"
        } else {
            moduleState.denialReason ?: "Access denied"
        }
    }
}