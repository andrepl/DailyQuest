package com.norcode.bukkit.dailyquests.reward;

import net.minecraft.server.v1_7_R1.IChatBaseComponent;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

public interface QuestReward extends ConfigurationSerializable {
	public void give(Player p);

	public IChatBaseComponent getTitle();
}
