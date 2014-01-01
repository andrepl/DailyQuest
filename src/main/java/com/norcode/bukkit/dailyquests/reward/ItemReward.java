package com.norcode.bukkit.dailyquests.reward;

import com.norcode.bukkit.dailyquests.chat.Text;
import net.minecraft.server.v1_7_R1.EnchantmentManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ItemReward implements QuestReward {

	List<ItemStack> stacks;

	public ItemReward(ItemStack... stacks) {
		this.stacks = Arrays.asList(stacks);
	}

	public static ItemReward generate(Random random, double difficulty) {

		Material mat = Material.DIAMOND;

		if (difficulty <= 0.2 && random.nextDouble() < 0.85) {

			// leather gear, sometimes iron.
			switch (random.nextInt(4)) {
				case 0: mat = Material.LEATHER_BOOTS; break;
				case 1: mat = Material.LEATHER_LEGGINGS; break;
				case 2: mat = Material.LEATHER_CHESTPLATE; break;
				case 3: mat = Material.LEATHER_HELMET; break;
			}

		} else if (difficulty <= 0.5 && random.nextDouble() < 0.85) {
			// chain or iron
			switch (random.nextInt(12)) {
				case 0: mat = Material.IRON_SWORD; break;
				case 1: mat = Material.IRON_AXE; break;
				case 2: mat = Material.IRON_PICKAXE; break;
				case 3: mat = Material.BOW; break;
				case 4: mat = Material.IRON_BOOTS; break;
				case 5: mat = Material.IRON_LEGGINGS; break;
				case 6: mat = Material.IRON_CHESTPLATE; break;
				case 7: mat = Material.IRON_HELMET; break;
				case 8: mat = Material.CHAINMAIL_BOOTS; break;
				case 9: mat = Material.CHAINMAIL_LEGGINGS; break;
				case 10: mat = Material.CHAINMAIL_CHESTPLATE; break;
				case 11: mat = Material.CHAINMAIL_HELMET; break;
			}
		} else {
			// diamond
			switch (random.nextInt(8)) {
				case 0: mat = Material.DIAMOND_AXE; break;
				case 1: mat = Material.DIAMOND_SWORD; break;
				case 2: mat = Material.DIAMOND_PICKAXE; break;
				case 3: mat = Material.DIAMOND_BOOTS; break;
				case 4: mat = Material.DIAMOND_HELMET; break;
				case 5: mat = Material.DIAMOND_LEGGINGS; break;
				case 6: mat = Material.DIAMOND_CHESTPLATE; break;
				case 7: mat = Material.DIAMOND_SWORD; break; // an extra sword, because diamond swords rule.
			}
		}
		if (random.nextDouble() < 0.15) {
			// enchanted book once in a while.
			mat = Material.ENCHANTED_BOOK;
		}
		ItemStack stack = new ItemStack(mat);
		net.minecraft.server.v1_7_R1.ItemStack nms = CraftItemStack.asNMSCopy(stack);
		nms = EnchantmentManager.a(random, nms, (int)Math.min(30, difficulty * 30));
		stack = CraftItemStack.asCraftMirror(nms);
		ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
		if (difficulty > 0.8) {
			int extra = 1+random.nextInt((int) ((difficulty-0.8) * 10));
			for (int i=0;i<extra;i++) {
				ItemReward rwd = ItemReward.generate(random, 0.8);
				stacks.addAll(rwd.stacks);
			}
			return new ItemReward(stacks.toArray(new ItemStack[0]));
		}
		return new ItemReward(stack);
	}

	public ItemReward(Map<String, Object> map) {
		Bukkit.getLogger().info("Deserializing: " + map);
		List<String> keys = new ArrayList<String>(map.keySet());
		this.stacks = new ArrayList<ItemStack>();
		Collections.sort(keys);
		for (String key: keys) {
			if (key.startsWith("item")) {
				Object value = map.get(key);
				Bukkit.getLogger().info("Got " + key + "->" + value + "(" + value.getClass() + ")");
				this.stacks.add((ItemStack) value);
			}
		}
	}

	@Override
	public void give(Player p) {
		ItemStack[] rewardStacks = new ItemStack[stacks.size()];
		for (int i=0;i<stacks.size();i++) {
			rewardStacks[i] = stacks.get(i).clone();
		}
		HashMap<Integer, ItemStack> noFit = p.getInventory().addItem(rewardStacks);
		// whatever doesn't fit in their inv,  drop it at their feet.
		for (ItemStack s: noFit.values()) {
			p.getWorld().dropItem(p.getLocation(), s);
		}
	}

	@Override
	public Text getTitle() {
		Text t = new Text("");
		Iterator<ItemStack> stackIt = stacks.iterator();
		while (stackIt.hasNext()) {
			ItemStack s = stackIt.next();
			if (s.getAmount() != 1) {
				t.append(new Text(s.getAmount() + "x "));
			}
			t.appendItem(s);
			if (stackIt.hasNext()) {
				t.append(new Text(", ").setColor(ChatColor.DARK_GRAY));
			}
		}

		return t;
	}

	@Override
	public Map<String, Object> serialize() {
		HashMap map = new HashMap<String, Object>();
		int ctr=0;
		for (ItemStack s: stacks) {
			map.put("item"+(ctr++), s);
		}
		return map;
	}
}
