package com.norcode.bukkit.dailyquests.type;

import com.norcode.bukkit.dailyquests.DailyQuests;
import com.norcode.bukkit.dailyquests.command.CommandError;
import com.norcode.bukkit.dailyquests.event.QuestCompleteEvent;
import com.norcode.bukkit.dailyquests.quest.CompoundQuest;
import com.norcode.bukkit.dailyquests.quest.Quest;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.EventHandler;

import java.util.LinkedList;

public class Compound extends QuestType {

	public Compound(DailyQuests plugin) {
		super();
		this.plugin = plugin;
		ConfigurationSerialization.registerClass(CompoundQuest.class);
		this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@Override
	public Quest fromUserInput(LinkedList<String> args) throws CommandError, CommandError {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public Quest generateQuest(double difficulty) {
		int num = 1 + (int) (difficulty * (1 + random.nextDouble()) * 3);
		if (num == 1) {
			num++;
		}
		Quest[] quests = new Quest[num];
		for (int i = 0; i < num; i++) {
			quests[i] = plugin.generateQuest(difficulty / 2);
			if (quests[i] instanceof CompoundQuest) {
				// re-generate it if it's another compound quest.
				i--;
			}
		}
		return new CompoundQuest(System.currentTimeMillis(), num, plugin.generateReward(difficulty * num), quests);
	}

	@EventHandler(ignoreCancelled = true)
	public void onQuestCompletedEvent(QuestCompleteEvent event) {
		Quest quest = plugin.getPlayerQuest(event.getPlayer());
		if (quest instanceof CompoundQuest) {
			if (((CompoundQuest) quest).getQuests().contains(event.getQuest())) {
				event.setCancelled(true);
				quest.progress(event.getPlayer(), 1);
			}

		}
	}
}
