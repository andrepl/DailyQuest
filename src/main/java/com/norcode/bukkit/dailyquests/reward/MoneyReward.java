package com.norcode.bukkit.dailyquests.reward;

import com.norcode.bukkit.dailyquests.DailyQuests;
import com.norcode.bukkit.dailyquests.chat.Text;
import net.milkbowl.vault.economy.Economy;
import net.minecraft.server.v1_7_R1.IChatBaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MoneyReward implements QuestReward {
	private double amount = 0.0;
	private static Economy economy;
	private static double MIN_AMOUNT = 25;
	private static double MAX_AMOUNT = 500;

	@Override
	public void give(Player p) {
		getEconomy().depositPlayer(p.getName(), amount);
	}

	public MoneyReward(double amount) {
		this.amount = amount;
	}

	public static void initialize(DailyQuests plugin) {
		MIN_AMOUNT = plugin.getConfig().getDouble("rewards.money.min-amount", 25.0);
		MAX_AMOUNT = plugin.getConfig().getDouble("rewards.money.max-amount", 500.0);
	}

	public static MoneyReward generate(Random random, double difficulty) {
		return new MoneyReward(((MAX_AMOUNT - MIN_AMOUNT) * difficulty) + MIN_AMOUNT);
	}

	public MoneyReward(Map<String, Object> map) {
		this.amount = (Double) map.get("amount");
	}


	private static Economy getEconomy() {
	    if (economy == null) {
			RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
			if (economyProvider != null) {
				economy = economyProvider.getProvider();
			}
		}
		return economy;
	}

	@Override
	public IChatBaseComponent getTitle() {
		return new Text(getEconomy().format(amount));
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("amount", amount);
		return data;
	}
}
