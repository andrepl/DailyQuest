package com.norcode.bukkit.dailyquests.type;

import com.norcode.bukkit.dailyquests.DailyQuests;
import com.norcode.bukkit.dailyquests.event.QuestCompleteEvent;
import com.norcode.bukkit.dailyquests.quest.CompoundQuest;
import com.norcode.bukkit.dailyquests.quest.Quest;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.EventHandler;

public class Compound extends QuestType {

	public Compound(DailyQuests plugin) {
		super();
		this.plugin = plugin;
		ConfigurationSerialization.registerClass(CompoundQuest.class);
		this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	@Override
	public Quest generateQuest(double difficulty) {
		int num = 1 + (int)(difficulty * (1+random.nextDouble()) * 3);
		if (num == 1) {
			num++;
		}
		Quest[] quests = new Quest[num];
		for (int i=0;i<num;i++) {
			quests[i] = plugin.generateQuest(difficulty/2);
			if (quests[i] instanceof CompoundQuest) {
				Bukkit.getLogger().info("Compound generated a compound, retrying.");
				// re-generate it if it's another compound quest.
				i--;
			}
		}
		Bukkit.getLogger().info("Generating compound quest w/ " + quests.length + "quests");
		return new CompoundQuest(System.currentTimeMillis(), num, plugin.generateReward(difficulty*num), quests);
	}

	@EventHandler(ignoreCancelled = true)
	public void onQuestCompletedEvent(QuestCompleteEvent event) {
		plugin.getLogger().info("QuestCompleteEvent: " + event.getQuest());
		Quest quest = plugin.getPlayerQuest(event.getPlayer());
		if (quest instanceof CompoundQuest) {
			for (Quest q: ((CompoundQuest) quest).getQuests()) {
				plugin.getLogger().info(q.getTitle() + ": " + q.equals(event.getQuest()));
			}

			if (((CompoundQuest) quest).getQuests().contains(event.getQuest())) {
				plugin.getLogger().info("Part of a compound quest was completed. cancelling completion event");
				event.setCancelled(true);
				quest.progress(event.getPlayer(), 1);
			}

		}
	}
}
