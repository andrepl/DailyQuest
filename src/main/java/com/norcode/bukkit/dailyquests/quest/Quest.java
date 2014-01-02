package com.norcode.bukkit.dailyquests.quest;

import com.norcode.bukkit.dailyquests.event.QuestCompleteEvent;
import com.norcode.bukkit.dailyquests.event.QuestProgressEvent;
import com.norcode.bukkit.dailyquests.reward.QuestReward;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public abstract class Quest {

    String chatPrefix = ChatColor.DARK_RED + "«" + ChatColor.DARK_GREEN + "Daily Quests" + ChatColor.DARK_RED + "» " + ChatColor.RESET;
    long received;
	int progress = 0;
	int progressMax;
	boolean cancelled = false;
	QuestReward reward;

	public Quest(Map<String, Object> data) {
		this.received = (Long) data.get("received");
		this.progress = (Integer) data.get("progress");
		this.progressMax = (Integer) data.get("progressMax");
		this.reward = (QuestReward) data.get("reward");
	}

	public Quest(long received, int progressMax, QuestReward reward) {
		this.received = received;
		this.progressMax = progressMax;
		this.reward = reward;
	}


	public abstract String getTitle();
	public abstract String[] getDescription();

	public boolean isFinished() {
		return cancelled || progress == progressMax;
	}

	public String getProgressString() {
		return cancelled ? "Cancelled" : getProgress() == getProgressMax() ? "Completed" : getProgress() + "/" + getProgressMax();
	}
	public int getProgress() {
		return progress;
	}

	public int getProgressMax() {
		return progressMax;
	}

	public void progress(Player player, int amt) {
		QuestProgressEvent progEvent = new QuestProgressEvent(player, this, amt);
		Bukkit.getServer().getPluginManager().callEvent(progEvent);
		if (!progEvent.isCancelled()) {
			progress += progEvent.getAmount();
			if (isFinished()) {
				QuestCompleteEvent completeEvent = new QuestCompleteEvent(player, this);
				Bukkit.getServer().getPluginManager().callEvent(completeEvent);

			} else {
				player.sendMessage(chatPrefix + getTitle() + " [" + progress + "/" + progressMax + "]");
			}
		}
	}

	public Map<String, Object> serialize() {
		HashMap<String, Object> basicData = new HashMap<String, Object>();
		basicData.put("progress", progress);
		basicData.put("progressMax", progressMax);
		basicData.put("received", received);
		basicData.put("reward", reward);
		basicData.put("cancelled", cancelled);
		return basicData;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public QuestReward getReward() {
		return reward;
	}

}
