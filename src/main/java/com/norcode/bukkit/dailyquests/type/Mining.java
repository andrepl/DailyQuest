package com.norcode.bukkit.dailyquests.type;


import com.norcode.bukkit.dailyquests.DailyQuests;
import com.norcode.bukkit.dailyquests.quest.MiningQuest;
import com.norcode.bukkit.dailyquests.quest.Quest;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.HashMap;
import java.util.LinkedList;

public class Mining extends QuestType {

	private DailyQuests plugin;

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
		this.plugin = plugin;
		ConfigurationSerialization.registerClass(MiningQuest.class);
		this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@Override
	public Quest fromUserInput(LinkedList<String> args) {
		return null;
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
		return new MiningQuest(System.currentTimeMillis(), ore, qty, plugin.generateReward(difficulty));
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		plugin.getLogger().info("Player Placing Block." + event.getBlockPlaced().getType());
		if (ores.keySet().contains(event.getBlockPlaced().getType())) {
			plugin.getLogger().info("Setting block data to 1");
			event.getBlockPlaced().setData((byte)1);
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (ores.keySet().contains(event.getBlock().getType())) {
			if (event.getBlock().getData() == 0) {
				for (Quest quest: plugin.getPlayerQuests(event.getPlayer(), MiningQuest.class)) {
					if (!quest.isFinished() && event.getBlock().getType() == ((MiningQuest) quest).getOre()) {
						quest.progress(event.getPlayer(), 1);
						break;
					}
				}
			}
		}
	}


}
