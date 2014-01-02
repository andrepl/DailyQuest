package com.norcode.bukkit.dailyquests.reward;

import com.norcode.bukkit.dailyquests.DailyQuests;
import net.milkbowl.vault.Vault;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RewardManager {

	private DailyQuests plugin;
	private Random random = new Random();
	private HashMap<Class<? extends QuestReward>, Integer> rewardTypes;

	public RewardManager(DailyQuests plugin) {
		this.plugin = plugin;
		ConfigurationSerialization.registerClass(ItemReward.class);
		ConfigurationSerialization.registerClass(MoneyReward.class);
		Vault vault = (Vault) this.plugin.getServer().getPluginManager().getPlugin("Vault");
		rewardTypes = new HashMap<Class<? extends QuestReward>, Integer>();
		ItemReward.initialize(plugin);
		MoneyReward.initialize(plugin);
		rewardTypes.put(ItemReward.class, 3);
		if (vault.isEnabled()) {
			rewardTypes.put(MoneyReward.class, 1);
		}
	}

	public QuestReward generateReward(double difficulty) {
		List<Class<? extends QuestReward>> choices = new ArrayList<Class<? extends QuestReward>>();
		for (Map.Entry<Class<? extends QuestReward>, Integer> e: rewardTypes.entrySet()) {
			for (int i=0;i<e.getValue();i++) {
				choices.add(e.getKey());
			}
		}
		Class<? extends QuestReward> rewardClass = choices.get(random.nextInt(choices.size()));
		try {
			return (QuestReward) rewardClass.getDeclaredMethod("generate", Random.class, Double.TYPE).invoke(null, random, difficulty);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		} catch (InvocationTargetException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		} catch (IllegalAccessException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		return ItemReward.generate(random, difficulty);
	}
}
