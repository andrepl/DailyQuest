package com.norcode.bukkit.dailyquests.quest;

import com.norcode.bukkit.dailyquests.reward.QuestReward;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public abstract class Quest {
	long received;
	int progress = 0;
	int progressMax;
	QuestReward reward;

	public Quest(Map<String, Object> data) {
		this.received = received;

	}

	public Quest(long received, int progressMax, QuestReward reward) {
		this.received = received;
		this.progressMax = progressMax;
		this.reward = reward;
	}


	public abstract String getTitle();
	public abstract String[] getDescription();

	public boolean isFinished() {
		return progress == progressMax;
	}

	public int getProgress() {
		return progress;
	}

	public int getProgressMax() {
		return progressMax;
	}

	public void progress(Player player, int amt) {
		progress += amt;
		player.sendMessage(getTitle() + " [" + progress + "/" + progressMax + "]");
	}

	public Map<String, Object> serialize() {
		HashMap<String, Object> basicData = new HashMap<String, Object>();
		basicData.put("progress", progress);
		basicData.put("progressMax", progressMax);
		basicData.put("received", received);
		return basicData;
	}
}
