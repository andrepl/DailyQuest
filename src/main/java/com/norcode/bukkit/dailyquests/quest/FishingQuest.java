package com.norcode.bukkit.dailyquests.quest;

import com.norcode.bukkit.dailyquests.reward.QuestReward;
import com.norcode.bukkit.dailyquests.type.Fishing;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Map;

public class FishingQuest extends Quest implements ConfigurationSerializable {

	private final Fishing.Catch catchType;

	public FishingQuest(long received, Fishing.Catch catchType, int qty, QuestReward reward) {
		super(received, qty, reward);
		this.progress = 0;
		this.catchType = catchType;
	}

	public FishingQuest(Map<String, Object> map) {
		super(map);
		this.catchType = Fishing.Catch.valueOf(map.get("catch").toString());
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> basicData = super.serialize();
		basicData.put("catch", this.catchType.name());
		return basicData;
	}

	@Override
	public String getTitle() {
		return "Catch " + progressMax + " " + catchType.getName();
	}

	@Override
	public String[] getDescription() {
		return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
	}

	public Fishing.Catch getRequiredCatch() {
		return catchType;
	}
}
