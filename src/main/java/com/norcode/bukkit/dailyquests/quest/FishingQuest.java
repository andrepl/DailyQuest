package com.norcode.bukkit.dailyquests.quest;

import com.norcode.bukkit.dailyquests.reward.QuestReward;
import com.norcode.bukkit.dailyquests.type.Fishing;
import com.norcode.bukkit.dailyquests.type.QuestType;

public class FishingQuest extends Quest {

	private final Fishing.Catch catchType;

	public FishingQuest(QuestType type, long received, Fishing.Catch catchType, int qty, QuestReward reward) {
		super(type, received, qty, reward);
		this.progress = 0;
		this.catchType = catchType;
	}

	@Override
	public String getTitle() {
		return "Catch " + progressMax + " " + catchType.getName();
	}

	@Override
	public String[] getDescription() {
		return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
	}

}
