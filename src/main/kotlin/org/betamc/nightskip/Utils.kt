@file:JvmName("Utils")

package org.betamc.nightskip

import org.bukkit.entity.Player
import kotlin.math.ceil

fun Player.sendColoredMessage(message: String) =
    sendMessage(message.replace("&([0-9a-f])".toRegex(), "§$1"))

fun getRequired(amount: Int): Int =
    ceil(amount * (Property.SLEEPING_PERCENTAGE.toPercentage() / 100)).toInt()