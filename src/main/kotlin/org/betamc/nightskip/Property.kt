package org.betamc.nightskip

import kotlin.math.max
import kotlin.math.min

enum class Property(val key: String, val default: Any) {

    SLEEPING_PERCENTAGE_NIGHT("night.sleepingPercentage", 25),
    SLEEPING_TIME_NIGHT("night.sleepingTime", 5),
    CLEAR_RAIN("night.clearRain", true),
    SLEEPING_PERCENTAGE_STORM("storm.sleepingPercentage", 25),
    SLEEPING_TIME_STORM("storm.sleepingTime", 5),
    ANNOUNCEMENT_NIGHT("messages.nightAnnouncement", "&b{amount} players need to sleep to skip the night."),
    SKIPPED_NIGHT("messages.nightSkipped", "&bThe night has been skipped"),
    ANNOUNCEMENT_STORM("messages.stormAnnouncement", "&b{amount} players need to sleep to skip the storm."),
    SKIPPED_STORM("messages.stormSkipped", "&bThe storm has been skipped");

    override fun toString(): String =
        (NightSkip.config.getProperty(key) ?: default).toString()

    fun toUInt(): Int =
        max(toString().toIntOrDefault(default.toString().toInt()), 0)

    fun toUDouble(): Double =
        max(toString().toDoubleOrDefault(toUInt().toDouble()), 0.0)

    fun toPercentage(): Double = min(toUDouble(), 100.0)

    fun toBoolean(): Boolean =
        toString().toBooleanOrDefault(default.toString().toBooleanStrict())
}