package org.betamc.nightskip

import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.config.Configuration
import java.io.File
import java.nio.file.Files

class NightSkip : JavaPlugin() {

    companion object {
        const val prefix = "[NightSkip]"
        lateinit var plugin: Plugin; private set
        lateinit var config: Configuration; private set
    }

    override fun onEnable() {
        plugin = this
        initConfig()

        server.pluginManager.registerEvents(PlayerListener(), this)
        scheduleRepeatingTask( {
            for (player in server.onlinePlayers) {
                if (player.isSleeping) (player as CraftPlayer).handle.sleepTicks = 0
            }
        }, 10)

        server.logger.info("$prefix ${description.name} ${description.version} has been enabled.")
    }

    override fun onDisable() {
        server.logger.info("$prefix ${description.name} ${description.version} has been disabled.")
    }

    private fun initConfig() {
        val file = File(dataFolder, "config.yml")
        if (!file.exists()) {
            try {
                val stream = this::class.java.getResourceAsStream("/config.yml")!!
                file.parentFile.mkdirs()
                Files.copy(stream, file.toPath())
            } catch (e: Exception) {
                server.logger.severe("$prefix Failed to create config.")
                server.logger.severe("$prefix If the issue persists, unzip the plugin JAR and copy config.yml to ${dataFolder.path}")
                throw e
            }
        }
        config = Configuration(file)
        config.load()
    }
}