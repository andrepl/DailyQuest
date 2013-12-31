package com.norcode.bukkit.dailyquests.quest;

import com.norcode.bukkit.dailyquests.reward.QuestReward;
import com.norcode.bukkit.dailyquests.type.QuestType;
import org.bukkit.Material;

public class MiningQuest extends Quest {
	private Material ore;
	public MiningQuest(QuestType type, long received, int progressMax, QuestReward reward) {
		super(type, received, progressMax, reward);
	}

	public MiningQuest(QuestType type, long receivedAt, Material ore, int qty, QuestReward reward) {
		super(type, receivedAt, qty, reward);
		this.ore = ore;
	}

	@Override
	public String getTitle() {
		return "Mine " + progressMax + " " + ore.name().replace("_", " ").toLowerCase();
	}

	@Override
	public String[] getDescription() {
		return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
	}
}
