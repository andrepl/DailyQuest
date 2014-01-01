package com.norcode.bukkit.dailyquests.reward;

import com.norcode.bukkit.dailyquests.DailyQuests;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.Random;

public class RewardManager {

	private DailyQuests plugin;
	private Random random = new Random();

	public RewardManager(DailyQuests plugin) {
		this.plugin = plugin;
		ConfigurationSerialization.registerClass(ItemReward.class);
	}

	public QuestReward generateReward(double difficulty) {
		return ItemReward.generate(random, difficulty);
	}
}
