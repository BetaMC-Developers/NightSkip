package org.betamc.nightskip

import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.event.Event.Type
import kotlin.math.ceil

class WorldWrapper(val worldName: String) {

    private val world: World
        get() = Bukkit.getWorld(worldName)

    val isNight: Boolean
        get() = world.time in 12540..23455

    val isStorming: Boolean
        get() = world.hasStorm() && world.isThundering

    private val players: Set<Player>
        get() = Bukkit.getOnlinePlayers().filter { p -> p.world == world }.toSet()

    private val sleepingPlayers: Set<Player>
        get() = players.filter { p -> p.isSleeping }.toSet()

    private val sleepingPercentage
        get() = (if (isNight) Property.SLEEPING_PERCENTAGE_NIGHT
                else Property.SLEEPING_PERCENTAGE_STORM).toPercentage()

    private val sleepingTime
        get() = (if (isNight) Property.SLEEPING_TIME_NIGHT
                else Property.SLEEPING_TIME_STORM).toUInt()

    private val announcementMsg
        get() = (if (isNight) Property.ANNOUNCEMENT_NIGHT
                else Property.ANNOUNCEMENT_STORM).toString()

    private val skippedMsg
        get() = (if (isNight) Property.SKIPPED_NIGHT
                else Property.SKIPPED_STORM).toString()

    private var announced = Announced.NONE
    private var taskRunning = false
    private var taskId = 0

    private enum class Announced {
        NIGHT, STORM, NONE
    }

    init {
        scheduleRepeatingTask({
            if (announced != Announced.NONE) {
                if (!isNight && !isStorming) {
                    announced = Announced.NONE
                    if (taskRunning) {
                        Bukkit.getScheduler().cancelTask(taskId)
                        taskRunning = false
                    }
                    wakePlayersUp()
                } else if (isNight && announced == Announced.STORM ||
                    !isNight && isStorming && announced == Announced.NIGHT) {
                    announce()
                    updatePlayers()
                }
            }
        }, 1)
    }

    fun handlePlayerEvent(type: Type) {
        if (!isNight && !isStorming) return
        val required = getRequired(players.size)
        when (type) {
            Type.PLAYER_JOIN -> {
                if (announced != Announced.NONE &&
                    required != getRequired(players.size - 1))
                    announce()
            }
            Type.PLAYER_QUIT -> {
                if (announced != Announced.NONE &&
                    required != getRequired(players.size + 1))
                    announce()
            }
            Type.PLAYER_BED_ENTER -> if (announced == Announced.NONE) announce()
            else -> {}
        }
        updatePlayers()
    }

    private fun updatePlayers() {
        val sleeping = sleepingPlayers.size
        val required = getRequired(players.size)

        if (sleeping >= required && !taskRunning) {
            taskRunning = true
            taskId =
                scheduleTask({
                    success()
                    if (isNight) {
                        world.time = 0
                        if (Property.CLEAR_RAIN.toBoolean() && world.hasStorm()) {
                            clearWeather()
                        }
                    }
                    else clearWeather()
                    wakePlayersUp()
                    announced = Announced.NONE
                    taskRunning = false
                }, sleepingTime * 20L)
        } else if (sleeping < required && taskRunning) {
            Bukkit.getScheduler().cancelTask(taskId)
            taskRunning = false
        }
    }

    private fun announce() {
        for (player in players) {
            player.sendColoredMessage(announcementMsg.replace(
                "{amount}", "${getRequired(players.size)}"))
        }
        announced = if (isNight) Announced.NIGHT else Announced.STORM
    }

    private fun success() {
        for (player in players) {
            player.sendColoredMessage(skippedMsg)
        }
    }

    private fun getRequired(amount: Int): Int {
        val required = ceil(amount * (sleepingPercentage / 100)).toInt()
        return if (required == 0) 1 else required
    }

    private fun clearWeather() {
        if (!world.hasStorm()) return
        world.isThundering = false
        world.weatherDuration = 1
    }

    private fun wakePlayersUp() {
        for (player in sleepingPlayers) {
            (player as CraftPlayer).handle.a(false, false, true)
        }
    }
}