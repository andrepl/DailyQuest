package com.norcode.bukkit.dailyquests.command;

import com.norcode.bukkit.dailyquests.DailyQuests;
import com.norcode.bukkit.dailyquests.chat.Text;
import com.norcode.bukkit.dailyquests.quest.Quest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.LinkedList;

public class QuestCommand extends BaseCommand {
	public QuestCommand(DailyQuests plugin) {
		super(plugin, "quest", new String[] {"daily"}, "dailyquests.command.quest", new String[] {});
		plugin.getCommand("quest").setExecutor(this);
	}

	@Override
	protected void onExecute(CommandSender commandSender, String label, LinkedList<String> args) throws CommandError {
		plugin.getLogger().info("QuestCommand onExecute");
		if (!(commandSender instanceof Player)) {
			throw new CommandError("This command is only available to players.");
		}

		Player p = (Player) commandSender;

		Quest quest = plugin.getPlayerQuest(p);
		plugin.send(p, quest.getTitle());
		plugin.send(p, new Text("Progress: "  + (quest.isFinished() ? "Finished!" : quest.getProgress() + "/" + quest.getProgressMax())));
	}
}
