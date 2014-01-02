package com.norcode.bukkit.dailyquests.type;

import com.norcode.bukkit.dailyquests.DailyQuests;
import com.norcode.bukkit.dailyquests.command.CommandError;
import com.norcode.bukkit.dailyquests.quest.HarvestingQuest;
import com.norcode.bukkit.dailyquests.quest.Quest;
import org.apache.commons.lang.StringUtils;
import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.NetherWartsState;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.material.CocoaPlant;
import org.bukkit.material.Crops;
import org.bukkit.material.MaterialData;
import org.bukkit.material.NetherWarts;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.bukkit.Material.CROPS;

public class Harvesting extends QuestType {

	private DailyQuests plugin;

	public Harvesting(DailyQuests plugin) {
		this.plugin = plugin;
		ConfigurationSerialization.registerClass(HarvestingQuest.class);
		this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@Override
	public Quest fromUserInput(LinkedList<String> args) throws CommandError {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public Quest generateQuest(double difficulty) {
		List<HarvestGoal> possibilities = new ArrayList<HarvestGoal>();
		for (HarvestGoal hg : HarvestGoal.values()) {
			for (int i = 0; i < hg.weight; i++) {
				possibilities.add(hg);
			}
		}
		if (difficulty < 0.2) {
			while(possibilities.remove(HarvestGoal.Cocoa_Beans));
		}
		HarvestGoal type = possibilities.get(random.nextInt(possibilities.size()));
		int qty = type.minimumAmount * (1 + (int) (difficulty * 4));
		if (random.nextDouble() < difficulty) {
			qty += (random.nextInt((int) (difficulty * 4)) + 1) * type.minimumAmount;
		}
		return new HarvestingQuest(System.currentTimeMillis(), type.material, qty, plugin.generateReward(difficulty));
	}

	public static String getCropName(Material mat) {
		for (HarvestGoal goal: HarvestGoal.values()) {
			if (goal.material == mat) {
				return goal.name().replace("_", " ");
			}
		}
		return StringUtils.capitalize(mat.name().toLowerCase().replace("_", " "));
	}

	public static enum HarvestGoal {
		Wheat(CROPS, 8, 3),
		Carrots(Material.CARROT, 8, 5),
		Potatoes(Material.POTATO, 8, 5),
		Cocoa_Beans(Material.COCOA, 4, 2),
		Nether_Wart(Material.NETHER_STALK, 2, 2);


		private Material material;
		private int minimumAmount;
		private int weight;

		private HarvestGoal(Material material, int minimumAmount, int weight) {
			this.material = material;
			this.minimumAmount = minimumAmount;
			this.weight = weight;
		}
	}

	public boolean isFullyGrownCrop(Block b) {
		MaterialData md = b.getState().getData();
		if (md instanceof Crops) {
			return ((Crops) md).getState() == CropState.RIPE;
		} else if (md instanceof NetherWarts) {
			return ((NetherWarts) md).getState() == NetherWartsState.RIPE;
		} else if (md instanceof CocoaPlant) {
			return ((CocoaPlant) md).getSize() == CocoaPlant.CocoaPlantSize.LARGE;
		}
		return false;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerBreakCrop(BlockBreakEvent event) {
		if (isFullyGrownCrop(event.getBlock())) {
			Material type = event.getBlock().getType();
			for (HarvestingQuest q: plugin.getPlayerQuests(event.getPlayer(), HarvestingQuest.class)) {
				if (!q.isFinished() && q.getMaterial().equals(type)) {
					q.progress(event.getPlayer(), 1);
					break;
				}
			}
		}
	}
}
