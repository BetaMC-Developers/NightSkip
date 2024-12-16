package org.betamc.nightskip

import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.Event.Type

class WorldWrapper(val worldName: String) {

    private val world: World
        get() = Bukkit.getWorld(worldName)

    private val players: Set<Player>
        get() = Bukkit.getOnlinePlayers().filter { p -> p.world == world }.toSet()

    private val sleepingPlayers: Set<Player>
        get() = players.filter { p -> p.isSleeping }.toSet()

    private val isNight: Boolean
        get() = world.time in 12540..23455

    private var announced = false
    private var taskRunning = false
    private var taskId = 0

    fun handlePlayerEvent(type: Type) {
        val required = getRequired(players.size)
        when (type) {
            Type.PLAYER_JOIN -> {
                if (announced && required != getRequired(players.size - 1)) {
                    announced = false
                    announce(required)
                }
            }
            Type.PLAYER_QUIT -> {
                if (announced && required != getRequired(players.size + 1)) {
                    announced = false
                    announce(required)
                }
            }
            Type.PLAYER_BED_ENTER -> announce(required)
            Type.PLAYER_BED_LEAVE -> if (!isNight) announced = false
            else -> return
        }
        updatePlayers()
    }

    private fun updatePlayers() {
        val sleeping = sleepingPlayers.size
        val required = getRequired(players.size)

        if (sleeping >= required && !taskRunning) {
            taskRunning = true
            taskId =
                Bukkit.getScheduler().scheduleSyncDelayedTask(NightSkip.plugin, {
                    world.time = 0
                    if (Property.CLEAR_RAIN.toBoolean() && world.hasStorm()) {
                        world.isThundering = false
                        world.weatherDuration = 1
                    }
                    taskRunning = false
                    success()
                }, Property.SLEEPING_TIME.toUInt() * 20L)
        } else if (sleeping < required && taskRunning) {
            Bukkit.getScheduler().cancelTask(taskId)
            taskRunning = false
        }
    }

    private fun announce(required: Int) {
        if (!isNight || announced) return
        for (player in players) {
            player.sendColoredMessage(Property.ANNOUNCE_MSG.toString()
                .replace("{amount}", "$required"))
        }
        announced = true
    }

    private fun success() {
        for (player in players) {
            player.sendColoredMessage(Property.SUCCESS_MSG.toString())
        }
    }
}