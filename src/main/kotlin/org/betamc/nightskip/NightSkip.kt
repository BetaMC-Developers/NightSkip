package org.betamc.nightskip

import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.config.Configuration
import java.io.File

class NightSkip : JavaPlugin() {

    companion object {
        const val prefix = "[NightSkip]"
        lateinit var plugin: Plugin; private set
        lateinit var config: Configuration; private set
    }

    override fun onEnable() {
        plugin = this
        config = Configuration(File(dataFolder, "config.yml"))
        reloadConfig()

        server.pluginManager.registerEvents(PlayerListener(), this)
        server.scheduler.scheduleSyncRepeatingTask(this, {
            for (player in server.onlinePlayers) {
                if (player.isSleeping) (player as CraftPlayer).handle.sleepTicks = 0
            }
        }, 0, 10)

        server.logger.info("$prefix ${description.name} ${description.version} has been enabled.")
    }

    override fun onDisable() {
        server.logger.info("$prefix ${description.name} ${description.version} has been disabled.")
    }

    private fun reloadConfig() {
        config.load()
        for (property in Property.entries) {
            if (config.getProperty(property.key) == null) {
                config.setProperty(property.key, property.value)
            }
            property.value = config.getProperty(property.key)
        }
        config.save()
    }
}