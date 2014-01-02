package com.norcode.bukkit.dailyquests.quest;

import com.norcode.bukkit.dailyquests.reward.QuestReward;
import com.norcode.bukkit.dailyquests.type.Harvesting;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Map;

public class HarvestingQuest extends Quest implements ConfigurationSerializable {

	Material material;

	public HarvestingQuest(long received, Material material, int qty, QuestReward reward) {
		super(received, qty, reward);
		this.material = material;
	}

	public HarvestingQuest(Map<String, Object> data) {
		super(data);
		this.material = Material.valueOf((String) data.get("material"));
	}

	@Override
	public String getTitle() {
		return "Harvest " + getProgressMax() + " " + Harvesting.getCropName(material);
	}

	@Override
	public String[] getDescription() {
		return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> data = super.serialize();
		data.put("material", material.name());
		return data;
	}

	public Material getMaterial() {
		return material;
	}
}

