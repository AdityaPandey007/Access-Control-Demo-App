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


     // Loading configuration data from mock JSON as model class

    private fun loadConfig() {

        // Mock JSON data - modify times to test different scenarios
        val mockJson = """
        {
          "user": {
            "userType": "active",
            "coolingStartTime": "2025-11-03T15:35:00Z",
            "coolingEndTime": "2025-11-03T15:40:00Z",
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

    // Starting Countodown as per validation

    private fun startCoolingCountdown() {
        viewModelScope.launch {
            while (true) {
                updateModuleStates()
                delay(1000) // Update every second
            }
        }
    }

    // Updating access for module after cooling period
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

    // Handilnh toast messages for module card
    fun onModuleClicked(moduleState: ModuleAccessState): String {
        return if (moduleState.isAccessible) {
            "Navigating to ${moduleState.module.title}"
        } else {
            moduleState.denialReason ?: "Access denied"
        }
    }
}