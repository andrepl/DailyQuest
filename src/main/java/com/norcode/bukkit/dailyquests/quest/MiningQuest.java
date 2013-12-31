package com.norcode.bukkit.dailyquests.quest;

import com.norcode.bukkit.dailyquests.reward.QuestReward;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Map;

public class MiningQuest extends Quest implements ConfigurationSerializable {
	private Material ore;


	public MiningQuest(long receivedAt, Material ore, int qty, QuestReward reward) {
		super(receivedAt, qty, reward);
		this.ore = ore;
	}

	public MiningQuest(Map<String, Object> map) {
		super(map);
		this.ore = Material.valueOf(map.get("ore").toString());
	}

	@Override
	public String getTitle() {
		return "Mine " + progressMax + " " + ore.name().replace("_", " ").toLowerCase();
	}

	@Override
	public String[] getDescription() {
		return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> basicData = super.serialize();
		basicData.put("ore", this.ore.name());
		return basicData;
	}

	public Material getOre() {
		return ore;
	}
}
