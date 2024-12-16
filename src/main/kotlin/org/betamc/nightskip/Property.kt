package org.betamc.nightskip

import kotlin.math.max
import kotlin.math.min

enum class Property(val key: String, var value: Any) {

    SLEEPING_PERCENTAGE("sleepingPercentage.value", 25),
    SLEEPING_PERCENTAGE_INFO("sleepingPercentage.info", "What percentage of players need to sleep for the night to be skipped?"),
    SLEEPING_TIME("sleepingTime.value", 5),
    SLEEPING_TIME_INFO("sleepingTime.info", "How long do the required percentage of players have to sleep before the night is skipped?"),
    CLEAR_RAIN("clearRain.value", true),
    CLEAR_RAIN_INFO("clearRain.info", "Should the rain be cleared when the night is skipped?"),
    ANNOUNCE_MSG("announceMsg.value", "&b{amount} players need to sleep to skip the night."),
    ANNOUNCE_MSG_INFO("announceMsg.info", "Announcement message"),
    SUCCESS_MSG("successMsg.value", "&bThe night has been skipped"),
    SUCCESS_MSG_INFO("successMsg.info", "Success message");

    override fun toString(): String = value.toString()

    fun toUInt(): Int = max(toString().toInt(), 0)

    fun toUDouble(): Double = max(toString().toDouble(), 0.0)

    fun toPercentage(): Double = min(toUDouble(), 100.0)

    fun toBoolean(): Boolean = toString().toBoolean()
}