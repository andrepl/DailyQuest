package com.norcode.bukkit.dailyquests;

import com.norcode.bukkit.dailyquests.command.QuestCommand;
import com.norcode.bukkit.dailyquests.quest.Quest;
import com.norcode.bukkit.dailyquests.reward.QuestReward;
import com.norcode.bukkit.dailyquests.reward.RewardManager;
import com.norcode.bukkit.dailyquests.type.Fishing;
import com.norcode.bukkit.dailyquests.type.Hunting;
import com.norcode.bukkit.dailyquests.type.Mining;
import com.norcode.bukkit.dailyquests.type.QuestType;
import com.norcode.bukkit.playerid.PlayerID;
import net.minecraft.server.v1_7_R1.ChatBaseComponent;
import net.minecraft.server.v1_7_R1.IChatBaseComponent;
import net.minecraft.server.v1_7_R1.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class DailyQuests extends JavaPlugin implements Listener {

	private Random rand = new Random();
	private HashMap<String, QuestType> questTypes = new HashMap<String, QuestType>();
	private QuestCommand questCommand;
	private RewardManager rewardManager;

	@Override
	public void onEnable() {
		registerQuestType("Fishing", new Fishing(this));
		registerQuestType("Mining", new Mining(this));
        registerQuestType("Hunting", new Hunting(this));
		questCommand = new QuestCommand(this);
		rewardManager = new RewardManager(this);
	}

	public boolean registerQuestType(String name, QuestType questType) {
		for (QuestType t: questTypes.values()) {
			if (t.getClass().equals(questType.getClass())) {
				return false;
			}
		}
		questTypes.put(name, questType);
		return true;
	}

	public Quest generateQuest(Player player) {
		List<String> keys = new ArrayList<String>(questTypes.keySet());
		String key = keys.get(rand.nextInt(keys.size()));
		QuestType t = questTypes.get(key);
		Quest quest = t.generateQuest(getQuestsCompleted(player) / 200.0);
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


	public static void send(CommandSender player, Object ... lines) {
		for (Object line: lines) {
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
		PacketPlayOutChat packet =	new PacketPlayOutChat(chat, true);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}

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
}
