package org.betamc.nightskip

import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerBedEnterEvent
import org.bukkit.event.player.PlayerBedLeaveEvent
import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class PlayerListener : Listener {

    private val wrappers: Set<WorldWrapper> =
        Bukkit.getWorlds().map { world -> WorldWrapper(world.name) }.toSet()

    @EventHandler(priority = Event.Priority.Monitor)
    fun onPlayerJoin(event: PlayerJoinEvent) = onPlayerEvent(event)

    @EventHandler(priority = Event.Priority.Monitor)
    fun onPlayerQuit(event: PlayerQuitEvent) = onPlayerEvent(event)

    @EventHandler(priority = Event.Priority.Monitor)
    fun onPlayerEnterBed(event: PlayerBedEnterEvent) = onPlayerEvent(event)

    @EventHandler(priority = Event.Priority.Monitor)
    fun onPlayerLeaveBed(event: PlayerBedLeaveEvent) = onPlayerEvent(event)

    private fun onPlayerEvent(event: PlayerEvent) {
        val wrapper = getWrapper(event.player.world)
        scheduleTask { wrapper.handlePlayerEvent(event.type) }
    }

    private fun scheduleTask(runnable: Runnable) =
        Bukkit.getScheduler().scheduleSyncDelayedTask(NightSkip.plugin, runnable)

    private fun getWrapper(world: World) =
        wrappers.first { wrapper -> wrapper.worldName == world.name }
}