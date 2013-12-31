package com.norcode.bukkit.dailyquests;

import net.minecraft.server.v1_7_R1.IChatBaseComponent;
import net.minecraft.server.v1_7_R1.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class DailyQuests extends JavaPlugin {


	public static void send(Player player, Object ... lines) {
		for (Object line: lines) {
			if (line instanceof String) {
				player.sendMessage((String) line);
			} else if (line instanceof IChatBaseComponent) {
				send(player, (IChatBaseComponent) line);
			} else {
				Bukkit.getLogger().info("Cannot send unknown type: " + line);
			}
		}
	}

	public static void send(Player player, IChatBaseComponent chat) {
		PacketPlayOutChat packet =	new PacketPlayOutChat(chat, true);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}

}
