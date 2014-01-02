package com.norcode.bukkit.dailyquests.command;

import com.norcode.bukkit.dailyquests.DailyQuests;
import com.norcode.bukkit.dailyquests.chat.Text;
import com.norcode.bukkit.dailyquests.quest.Quest;
import com.norcode.bukkit.dailyquests.type.QuestType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.Map;

public class QuestCommand extends BaseCommand {
	public QuestCommand(DailyQuests plugin) {
		super(plugin, "quest", new String[] {"daily"}, "dailyquests.command.quest", new String[] {});
		plugin.getCommand("quest").setExecutor(this);
		registerSubcommand(new NewCommand(plugin));
		registerSubcommand(new CancelCommand(plugin));
		registerSubcommand(new GiveCommand(plugin));

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

	public static class GiveCommand extends BaseCommand {
		public GiveCommand(DailyQuests plugin) {
			super(plugin, "give", new String[] {"transfer"}, "dailyquests.command.quest.give", new String[]{});
		}

		@Override
		protected void onExecute(CommandSender sender, String label, LinkedList<String> args)  throws CommandError {
			if (args.size() == 0) {
				throw new CommandError("Expecting player name.");
			}
			if (!(sender instanceof Player)) {
				throw new CommandError("This command is only available to players.");
			}

			Quest origQuest = plugin.getPlayerQuest((Player) sender);
			Quest quest = null;
			Map<String, Object> data = origQuest.serialize();
			try {
				quest = origQuest.getClass().getConstructor(Map.class).newInstance(data);
			} catch (InstantiationException e) {
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			} catch (IllegalAccessException e) {
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			} catch (InvocationTargetException e) {
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			} catch (NoSuchMethodException e) {
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			}

			if (quest != null) {
				Player p = plugin.getServer().getPlayer(args.peek());
				if (p != null) {
					plugin.setPlayerQuest(p, quest);

					sender.sendMessage("Your quest has been given to " + p.getName());
				}
			} else {
				throw new CommandError("Something went wrong.");
			}
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
			if (args.size() == 0) {
				plugin.generateQuest(p);
				plugin.getServer().getPluginCommand("quest").execute(p, "quest", new String[]{});
			} else {
				String ts = args.pop().toLowerCase();
				QuestType type = plugin.getQuestType(ts);
				if (type == null) {
					throw new CommandError("Unknown Quest Type: " + ts);
				}

				double difficulty = plugin.getQuestsCompleted(p) / 100.0;
				if (!args.isEmpty()) {
					try {
						difficulty = Double.parseDouble(args.peek());
					} catch (IllegalArgumentException ex) {
						Quest q = type.fromUserInput(args);
						plugin.setPlayerQuest(p, q);
						plugin.getServer().getPluginCommand("quest").execute(p, "quest", new String[]{});
						return;
					}
				}
				Quest q = type.generateQuest(difficulty);
				plugin.setPlayerQuest(p, q);
				plugin.getServer().getPluginCommand("quest").execute(p, "quest", new String[]{});
			}
		}
	}
}
