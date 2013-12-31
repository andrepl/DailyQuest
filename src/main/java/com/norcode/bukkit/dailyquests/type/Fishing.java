package com.norcode.bukkit.dailyquests.type;

import com.norcode.bukkit.dailyquests.DailyQuests;
import com.norcode.bukkit.dailyquests.quest.FishingQuest;
import com.norcode.bukkit.dailyquests.quest.Quest;
import com.norcode.bukkit.dailyquests.reward.ItemReward;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

public class Fishing extends QuestType {

	public static enum Catch {
		FISH("Fish", (byte) 32767),
		COD("Cod", (byte) 0),
		SALMON("Salmon", (byte) 1),
		CLOWNFISH("Clownfish", (byte) 2),
		PUFFERFISH("Pufferfish", (byte) 3);



		private final String name;
		private final byte data;

		private Catch(String name, byte data) {
			this.name = name;
			this.data = data;
		}

		public String getName() {
			return name;
		};

		public byte getData() {
			return data;
		}

		public boolean satisfiedBy(ItemStack caught) {
			return caught.getType().equals(Material.RAW_FISH) &&
					(caught.getData().getData() == this.data || this.data == 32767);
		}
	}

	public Fishing(DailyQuests plugin) {
		super();
		this.plugin = plugin;
		this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
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
		return new FishingQuest(System.currentTimeMillis(), c, qty, new ItemReward(new ItemStack(Material.FISHING_ROD)));
	}

	@EventHandler
	public void onFishCaught(PlayerFishEvent event) {
		if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
			Quest quest = plugin.getPlayerQuest(event.getPlayer());
			if (quest instanceof FishingQuest) {
				ItemStack caught = ((Item) event.getCaught()).getItemStack();
				if (!quest.isFinished() && ((FishingQuest) quest).getRequiredCatch().satisfiedBy(caught)) {
					quest.progress(event.getPlayer(), caught.getAmount());
				}
			}
		}
	}
}

