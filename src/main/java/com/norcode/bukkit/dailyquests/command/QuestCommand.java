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
		registerSubcommand(new NewCommand(plugin));
		registerSubcommand(new CancelCommand(plugin));
	}

	@Override
	protected void onExecute(CommandSender commandSender, String label, LinkedList<String> args) throws CommandError {
		if (!(commandSender instanceof Player)) {
			throw new CommandError("This command is only available to players.");
		}

		Player p = (Player) commandSender;

		Quest quest = plugin.getPlayerQuest(p);
		plugin.send(p, quest.getTitle());
		plugin.send(p, new Text("Reward: ").append(quest.getReward().getTitle()));
		plugin.send(p, new Text("Progress: "  + quest.getProgressString()));
	}

	public static class CancelCommand extends BaseCommand {
		public CancelCommand(DailyQuests plugin) {
			super(plugin, "cancel", new String[]{"abandon", "abort"}, "dailyquests.command.quest.cancel", new String[]{});
		}
		@Override
		protected void onExecute(CommandSender commandSender, String label, LinkedList<String> args) throws CommandError {
			if (!(commandSender instanceof Player)) {
				throw new CommandError("This command is only available to players.");
			}

			Player p = (Player) commandSender;

			Quest quest = plugin.getPlayerQuest(p);
			if (quest.isFinished()) {
				throw new CommandError("Your quest is already completed.");
			}

			quest.setCancelled(true);
			p.sendMessage("Your quest has been cancelled.");
		}
	}

	public static class NewCommand extends BaseCommand {

		public NewCommand(DailyQuests plugin) {
			super(plugin, "new", new String[]{"create", "generate"}, "dailyquests.command.quest.new", new String[]{});
		}

		@Override
		protected void onExecute(CommandSender commandSender, String label, LinkedList<String> args) throws CommandError {
			if (!(commandSender instanceof Player)) {
				throw new CommandError("This command is only available to players.");
			}

			Player p = (Player) commandSender;

			Quest quest = plugin.getPlayerQuest(p);
			if (!quest.isFinished()) {
				throw new CommandError("You already have a quest in progress. You must cancel your current quest to receive a new one.");
			}

			plugin.generateQuest(p);
			plugin.getServer().getPluginCommand("quest").execute(p, "quest", new String[]{});
		}
	}
}
