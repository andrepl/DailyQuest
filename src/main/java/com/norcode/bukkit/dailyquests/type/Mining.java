package com.norcode.bukkit.dailyquests.type;


import com.norcode.bukkit.dailyquests.DailyQuests;
import com.norcode.bukkit.dailyquests.quest.MiningQuest;
import com.norcode.bukkit.dailyquests.reward.ItemReward;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class Mining extends QuestType {

	private static HashMap<Material, Integer> ores = new HashMap<Material, Integer>();
	static {
		ores.put(Material.DIAMOND_ORE, 2);
		ores.put(Material.EMERALD_ORE, 1);
		ores.put(Material.GOLD_ORE, 6);
		ores.put(Material.IRON_ORE, 12);
		ores.put(Material.LAPIS_ORE, 8);
		ores.put(Material.REDSTONE_ORE, 10);
		ores.put(Material.COAL_ORE, 15);
	}

	public Mining(DailyQuests plugin) {
		super(plugin);
	}

	@Override
	public MiningQuest generateQuest(double difficulty) {
		Material ore = Material.COAL_ORE;
		int baseMult = 1;
		if (difficulty < 0.33) {
			if (random.nextBoolean()) {
				ore = Material.IRON_ORE;
			}
			if (random.nextDouble() < difficulty) {
				ore = Material.GOLD_ORE;
			}
		} else if (difficulty < 0.66) {
			baseMult = 2;
			if (random.nextBoolean() || random.nextBoolean()) {
				ore = Material.IRON_ORE;
			}
			if (random.nextDouble() * 1.5 < difficulty) {
				ore = Material.GOLD_ORE;
			} else if (random.nextDouble() < difficulty) {
			    ore = Material.LAPIS_ORE;
				if (random.nextBoolean()) {
					ore = Material.REDSTONE_ORE;
				}
			} else {
				ore = Material.EMERALD_ORE;
				if (random.nextBoolean()) {
					ore = Material.DIAMOND_ORE;
				}
			}
		} else {
			baseMult = 3;
			ore = Material.GOLD_ORE;
			if (random.nextDouble() < difficulty) {
				ore = Material.REDSTONE_ORE;
			}
			if (random.nextBoolean()) {
				ore = Material.LAPIS_ORE;
			} else {
				if  (random.nextBoolean()) {
					ore = Material.EMERALD_ORE;
				}
				if (random.nextBoolean()) {
					ore = Material.DIAMOND_ORE;
				}
			}
		}
		baseMult += random.nextInt(baseMult + 1);
		if (random.nextDouble() < difficulty && (ore == Material.DIAMOND_ORE || ore == Material.EMERALD_ORE || ore == Material.GOLD_ORE)) {
			baseMult += baseMult;
		}
		int qty = ores.get(ore) * baseMult;
		return new MiningQuest(this, System.currentTimeMillis(), ore, qty, new ItemReward(new ItemStack(Material.DIAMOND_PICKAXE)));
	}
}
