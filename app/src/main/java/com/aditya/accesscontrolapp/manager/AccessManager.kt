package com.aditya.accesscontrolapp.manager

import com.aditya.accesscontrolapp.models.AppConfig
import com.aditya.accesscontrolapp.models.ModuleAccessState
import com.aditya.accesscontrolapp.models.ModuleData
import java.time.Duration
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class AccessManager(val config: AppConfig) {

    //Check if user is currently in cooling period

    fun isInCoolingPeriod(): Boolean {
        val now = Instant.now()
        val coolingEnd = parseDateTime(config.user.coolingEndTime)
        return now.isBefore(coolingEnd)
    }


     // showing countdown timer for cooling period
     // valdiation check for cooling period
     // Showing text "Cooling ends in 02:31"

    fun getCoolingCountdown(): String? {
        if (!isInCoolingPeriod()) return null

        val now = Instant.now()
        val coolingEnd = parseDateTime(config.user.coolingEndTime)
        val duration = Duration.between(now, coolingEnd)

        val minutes = duration.toMinutes() %60
        val seconds = duration.seconds % 60

        return String.format("Cooling ends in %02dm:%02ds", minutes, seconds)
    }

    /* Validation for access
     1. Cooling period status
     2. User permissions */

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

    // parsing date time string into instant.

    private fun parseDateTime(dateTimeString: String): Instant {
        return ZonedDateTime.parse(
            dateTimeString,
            DateTimeFormatter.ISO_DATE_TIME
        ).toInstant()
    }
}