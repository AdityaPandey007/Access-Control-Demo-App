package com.aditya.accesscontrolapp.models

data class UserData(
    val userType: String,
    val coolingStartTime: String,
    val coolingEndTime: String,
    val accessibleModules: List<String>
)

data class ModuleData(
    val id: String,
    val title: String,
    val requiresConsent: Boolean
)

data class AppConfig(
    val user: UserData,
    val modules: List<ModuleData>
)

data class ModuleAccessState(
    val module: ModuleData,
    val isAccessible: Boolean,
    val denialReason: String? = null
)