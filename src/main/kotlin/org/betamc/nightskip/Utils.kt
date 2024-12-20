@file:JvmName("Utils")

package org.betamc.nightskip

import org.bukkit.Bukkit
import org.bukkit.entity.Player

fun scheduleTask(task: Runnable) = scheduleTask(task, 0)

fun scheduleTask(task: Runnable, delay: Long) =
    Bukkit.getScheduler().scheduleSyncDelayedTask(NightSkip.plugin, task, delay)

fun scheduleRepeatingTask(task: Runnable, interval: Long) =
    Bukkit.getScheduler().scheduleSyncRepeatingTask(NightSkip.plugin, task, 0, interval)

fun Player.sendColoredMessage(message: String) =
    sendMessage(message.replace("&([0-9a-f])".toRegex(), "§$1"))

fun String?.toIntOrDefault(default: Int) = this?.toIntOrNull() ?: default

fun String?.toDoubleOrDefault(default: Double) = this?.toDoubleOrNull() ?: default

fun String?.toBooleanOrDefault(default: Boolean) = this?.toBooleanStrictOrNull() ?: default