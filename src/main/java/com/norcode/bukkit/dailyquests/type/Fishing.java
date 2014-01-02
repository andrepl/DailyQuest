package com.norcode.bukkit.dailyquests.type;

import com.norcode.bukkit.dailyquests.DailyQuests;
import com.norcode.bukkit.dailyquests.command.CommandError;
import com.norcode.bukkit.dailyquests.quest.FishingQuest;
import com.norcode.bukkit.dailyquests.quest.Quest;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;

public class Fishing extends QuestType {

	public static enum Catch {
		FISH("Fish", new ItemStack(Material.RAW_FISH, 1, (byte) 32767)),
		COD("Cod", new ItemStack(Material.RAW_FISH, 1, (byte) 0)),
		SALMON("Salmon", new ItemStack(Material.RAW_FISH, 1, (byte) 1)),
		CLOWNFISH("Clownfish", new ItemStack(Material.RAW_FISH, 1, (byte) 2)),
		PUFFERFISH("Pufferfish", new ItemStack(Material.RAW_FISH, 1, (byte) 3));


		private final String name;
		private final ItemStack stack;

		private Catch(String name, ItemStack stack) {
			this.name = name;
			this.stack = stack;
		}

		public String getName() {
			return name;
		}

		;

		public byte getData() {
			return stack.getData().getData();
		}

		public Material getMaterial() {
			return stack.getType();
		}

		public boolean satisfiedBy(ItemStack caught) {
			Bukkit.getLogger().info("Checking if " + caught + " satisfies " + this);
			if (caught.getType() == stack.getType() && (
					caught.getData().getData() == this.getData() || this.getData() == -1)) {
				Bukkit.getLogger().info("matches, it's good.");
				return true;
			} else {
				Bukkit.getLogger().info("doesn't match: " + caught + " != " + stack);
				return false;
			}
		}
	}

	public Fishing(DailyQuests plugin) {
		super();
		this.plugin = plugin;
		ConfigurationSerialization.registerClass(FishingQuest.class);
		this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@Override
	public Quest fromUserInput(LinkedList<String> args) throws CommandError {
		return null;
	}

	@Override
	public FishingQuest generateQuest(double difficulty) {
		Catch c = Catch.FISH;
		int qtyBase = 5;
		if (random.nextDouble() < difficulty) {
			c = Catch.COD;
		}
		if (random.nextDouble() <= difficulty) {
			if (random.nextDouble() > difficulty) {
				c = Catch.SALMON;
				qtyBase = 5;
			} else if (random.nextDouble() > difficulty) {
				c = Catch.PUFFERFISH;
				qtyBase = 2;
			} else {
				c = Catch.CLOWNFISH;
				qtyBase = 1;
			}
		}
		int qty = qtyBase + random.nextInt(qtyBase + 1);
		if (difficulty > 0.33) {
			qty += qtyBase;
		}
		if (difficulty > 0.66) {
			qty += qtyBase * 2;
		}
		qty = (qty / qtyBase) * qtyBase;
		if (c == Catch.FISH || c == Catch.COD) {
			qty *= 2;
		}
		return new FishingQuest(System.currentTimeMillis(), c, qty, plugin.generateReward(difficulty));
	}

	@EventHandler
	public void onFishCaught(PlayerFishEvent event) {
		if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
			for (Quest quest : plugin.getPlayerQuests(event.getPlayer(), FishingQuest.class)) {
				ItemStack caught = ((Item) event.getCaught()).getItemStack();
				if (!quest.isFinished() && ((FishingQuest) quest).getRequiredCatch().satisfiedBy(caught)) {
					quest.progress(event.getPlayer(), caught.getAmount());
					break;
				}
			}
		}
	}
}

