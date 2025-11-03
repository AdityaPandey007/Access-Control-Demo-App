package com.aditya.accesscontrolapp.manager

import com.aditya.accesscontrolapp.models.AppConfig
import com.aditya.accesscontrolapp.models.ModuleAccessState
import com.aditya.accesscontrolapp.models.ModuleData
import java.time.Duration
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class AccessManager(val config: AppConfig) {
    /**
     * Check if user is currently in cooling period
     */
    fun isInCoolingPeriod(): Boolean {
        val now = Instant.now()
        val coolingEnd = parseDateTime(config.user.coolingEndTime)
        return now.isBefore(coolingEnd)
    }

    /**
     * Get countdown string for cooling period
     * Returns null if not in cooling period
     * Returns formatted string like "Cooling ends in 02:31"
     */
    fun getCoolingCountdown(): String? {
        if (!isInCoolingPeriod()) return null

        val now = Instant.now()
        val coolingEnd = parseDateTime(config.user.coolingEndTime)
        val duration = Duration.between(now, coolingEnd)

        val minutes = duration.toMinutes()
        val seconds = duration.seconds % 60

        return String.format("Cooling ends in %02d:%02d", minutes, seconds)
    }

    /**
     * Check if a module is accessible based on:
     * 1. Cooling period status
     * 2. User permissions
     */
    fun checkModuleAccess(moduleId: String): ModuleAccessState {
        val module = config.modules.find { it.id == moduleId }
            ?: return ModuleAccessState(
                ModuleData(moduleId, "Unknown", false),
                false,
                "Module not found"
            )

        // Check cooling period first
        if (isInCoolingPeriod()) {
            return ModuleAccessState(
                module,
                false,
                "Access denied: cooling period active"
            )
        }

        // Check user permissions
        if (!config.user.accessibleModules.contains(moduleId)) {
            return ModuleAccessState(
                module,
                false,
                "Access denied: no permission"
            )
        }

        // Access granted
        return ModuleAccessState(module, true)
    }

    /**
     * Parse ISO 8601 datetime string to Instant
     */
    private fun parseDateTime(dateTimeString: String): Instant {
        return ZonedDateTime.parse(
            dateTimeString,
            DateTimeFormatter.ISO_DATE_TIME
        ).toInstant()
    }
}