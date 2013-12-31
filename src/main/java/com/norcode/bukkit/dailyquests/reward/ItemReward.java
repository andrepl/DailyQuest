package com.norcode.bukkit.dailyquests.reward;

import com.norcode.bukkit.dailyquests.chat.Text;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ItemReward implements QuestReward {

	List<ItemStack> stacks;

	public ItemReward(ItemStack... stacks) {
		this.stacks = Arrays.asList(stacks);
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
			t.append(new Text(s.getAmount() + "x "));
			t.appendItem(s);
			if (stackIt.hasNext()) {
				t.append(new Text(", ").setColor(ChatColor.DARK_GRAY));
			}
		}
		return t;
	}
}
