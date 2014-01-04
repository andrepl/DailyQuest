package com.norcode.bukkit.dailyquests;

import com.norcode.bukkit.dailyquests.chat.Text;
import com.norcode.bukkit.dailyquests.command.QuestCommand;
import com.norcode.bukkit.dailyquests.event.QuestCompleteEvent;
import com.norcode.bukkit.dailyquests.event.QuestProgressEvent;
import com.norcode.bukkit.dailyquests.quest.CompoundQuest;
import com.norcode.bukkit.dailyquests.quest.Quest;
import com.norcode.bukkit.dailyquests.reward.QuestReward;
import com.norcode.bukkit.dailyquests.reward.RewardManager;
import com.norcode.bukkit.dailyquests.type.Compound;
import com.norcode.bukkit.dailyquests.type.Fishing;
import com.norcode.bukkit.dailyquests.type.Harvesting;
import com.norcode.bukkit.dailyquests.type.Hunting;
import com.norcode.bukkit.dailyquests.type.Mining;
import com.norcode.bukkit.dailyquests.type.PVP;
import com.norcode.bukkit.dailyquests.type.QuestType;
import com.norcode.bukkit.playerid.PlayerID;
import net.minecraft.server.v1_7_R1.ChatBaseComponent;
import net.minecraft.server.v1_7_R1.IChatBaseComponent;
import net.minecraft.server.v1_7_R1.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class DailyQuests extends JavaPlugin implements Listener {

	private String chatPrefix = ChatColor.DARK_RED + "«" + ChatColor.DARK_GREEN + "Daily Quests" + ChatColor.DARK_RED + "» " + ChatColor.RESET;
	private Random rand = new Random();
	private HashMap<String, QuestType> questTypes = new HashMap<String, QuestType>();
	private QuestCommand questCommand;
	private RewardManager rewardManager;

	@Override
	public void onEnable() {
		saveDefaultConfig();
		getServer().getPluginManager().registerEvents(this, this);
		registerQuestType("Fishing", new Fishing(this));
		registerQuestType("Mining", new Mining(this));
		registerQuestType("Hunting", new Hunting(this));
		registerQuestType("Harvesting", new Harvesting(this));
		registerQuestType("PVP", new PVP(this));
		registerQuestType("Compound", new Compound(this));
		questCommand = new QuestCommand(this);
		rewardManager = new RewardManager(this);
	}

	public boolean registerQuestType(String name, QuestType questType) {
		for (QuestType t : questTypes.values()) {
			if (t.getClass().equals(questType.getClass())) {
				return false;
			}
		}
		questTypes.put(name, questType);
		return true;
	}

	public Quest generateQuest(double difficulty) {
		List<String> keys = new ArrayList<String>(questTypes.keySet());
		String key = keys.get(rand.nextInt(keys.size()));
		keys.remove("Compound");
		if (difficulty > 0.2 && rand.nextDouble() < difficulty) {
			keys.add("Compound");
		}
		QuestType t = questTypes.get(key);
		Quest quest = t.generateQuest(difficulty);
		return quest;
	}

	public Quest generateQuest(Player player) {
		Quest quest = generateQuest(getQuestsCompleted(player) / 100.0);
		player.setMetadata(MetaKeys.ACTIVE_QUEST,
				new FixedMetadataValue(this, quest));
		ConfigurationSection cfg = PlayerID.getPlayerData(getName(), player);
		cfg.set(MetaKeys.ACTIVE_QUEST, quest);
		PlayerID.savePlayerData(getName(), player, cfg);
		return quest;
	}

	public void setQuestsCompleted(Player player, int numCompleted) {
		ConfigurationSection cfg = PlayerID.getPlayerData(getName(), player);
		cfg.set(MetaKeys.QUESTS_COMPLETED, numCompleted);
		player.setMetadata(MetaKeys.QUESTS_COMPLETED, new FixedMetadataValue(this, numCompleted));
		PlayerID.savePlayerData(getName(), player, cfg);
	}

	public int getQuestsCompleted(Player player) {
		if (!player.hasMetadata(MetaKeys.QUESTS_COMPLETED)) {
			ConfigurationSection cfg = PlayerID.getPlayerData(getName(), player);
			int val = cfg.getInt(MetaKeys.QUESTS_COMPLETED);
			player.setMetadata(MetaKeys.QUESTS_COMPLETED, new FixedMetadataValue(this, val));
		}
		return player.getMetadata(MetaKeys.QUESTS_COMPLETED).get(0).asInt();
	}


	public static void send(CommandSender player, Object... lines) {
		for (Object line : lines) {
			if (line instanceof String) {
				player.sendMessage((String) line);
			} else if (line instanceof IChatBaseComponent) {
				send(player, (IChatBaseComponent) line);
			} else {
				Bukkit.getLogger().info("Cannot send unknown type: " + line);
			}
		}
	}

	public static void send(CommandSender player, ChatBaseComponent chat) {
		PacketPlayOutChat packet = new PacketPlayOutChat(chat, true);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}

	/**
	 * get the players current quest, or generate it if they have none.
	 * this does not include completed quests.
	 *
	 * @param player
	 * @return the players current Quest, or a new one if they had none.
	 */
	public Quest getPlayerQuest(Player player) {
		if (!player.hasMetadata(MetaKeys.ACTIVE_QUEST)) {
			ConfigurationSection cfg = PlayerID.getPlayerData(getName(), player);
			if (!cfg.contains(MetaKeys.ACTIVE_QUEST)) {
				return generateQuest(player);
			}
			Quest quest = (Quest) cfg.get(MetaKeys.ACTIVE_QUEST);
			player.setMetadata(MetaKeys.ACTIVE_QUEST, new FixedMetadataValue(this, quest));
		}
		return (Quest) player.getMetadata(MetaKeys.ACTIVE_QUEST).get(0).value();
	}

	public QuestReward generateReward(double difficulty) {
		return rewardManager.generateReward(difficulty);
	}

	/**
	 * returns all quests of the given type that a player currently has assigned.  this is usually 0 or 1 quests..
	 * but in the case of a compound quest may be more.
	 *
	 * @param player
	 * @param questType
	 * @return
	 */
	public <T extends Quest> List<T> getPlayerQuests(Player player, Class<T> questType) {
		Quest quest = getPlayerQuest(player);
		List<T> results = new ArrayList<T>();
		if (quest instanceof CompoundQuest) {
			for (Quest q : ((CompoundQuest) quest).getQuests()) {
				if (q.getClass().equals(questType)) {
					results.add((T) q);
				}
			}
		} else if (quest.getClass().equals(questType)) {
			results.add((T) quest);
		}
		return results;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onQuestProgress(QuestProgressEvent event) {
		send(event.getPlayer(), new Text(getChatPrefix()).append(ChatColor.GOLD + event.getQuest().getTitle() + ChatColor.RESET).append(" [" +
				(event.getQuest().getProgress() + event.getAmount()) + "/" + event.getQuest().getProgressMax() + "]"));
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onQuestComplete(QuestCompleteEvent event) {
		setQuestsCompleted(event.getPlayer(), getQuestsCompleted(event.getPlayer()) + 1);
		event.getQuest().getReward().give(event.getPlayer());
		send(event.getPlayer(), new Text(getChatPrefix()).append(ChatColor.GOLD + event.getQuest().getTitle() + ChatColor.RESET + " Complete!"));
	}

	@EventHandler()
	public void onPlayerJoin(PlayerJoinEvent event) {
		this.getServer().getPluginCommand("quest").execute(event.getPlayer(), "quest", new String[]{});
	}

	public QuestType getQuestType(String typeLower) {
		for (String k : questTypes.keySet()) {
			if (k.equalsIgnoreCase(typeLower)) {
				return questTypes.get(k);
			}
		}
		return null;
	}

	public void setPlayerQuest(Player player, Quest quest) {
		ConfigurationSection cfg = PlayerID.getPlayerData(getName(), player);
		cfg.set(MetaKeys.ACTIVE_QUEST, quest);
		PlayerID.savePlayerData(getName(), player, cfg);
		player.setMetadata(MetaKeys.ACTIVE_QUEST, new FixedMetadataValue(this, quest));
	}

	public String getChatPrefix() {
		return chatPrefix;
	}
}
