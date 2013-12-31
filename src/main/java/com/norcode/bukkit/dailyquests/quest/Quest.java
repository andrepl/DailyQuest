package com.norcode.bukkit.dailyquests.quest;

import com.norcode.bukkit.dailyquests.reward.QuestReward;
import com.norcode.bukkit.dailyquests.type.QuestType;

public abstract class Quest {
	QuestType type;
	long received;
	int progress = 0;
	int progressMax;
	QuestReward reward;

	public Quest(QuestType type, long received, int progressMax, QuestReward reward) {
		this.type = type;
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

	public void progress(int amt) {
		progress += amt;
	}
}
