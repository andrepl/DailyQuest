package com.norcode.bukkit.dailyquests.quest;

import com.norcode.bukkit.dailyquests.reward.QuestReward;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CompoundQuest extends Quest implements ConfigurationSerializable {

	List<Quest> quests = new ArrayList<Quest>();

	public CompoundQuest(Map<String, Object> data) {
		super(data);
		quests = (List<Quest>) data.get("quests");
	}

	public CompoundQuest(long received, int progressMax, QuestReward reward, Quest... questList) {
		super(received, progressMax, reward);
		for (Quest q: questList) {
			quests.add(q);
		}
	}

	@Override
	public String getTitle() {
		List<String> titles = new ArrayList<String>();
		for (Quest q: quests) {
			titles.add(q.getTitle());
		}
		return StringUtils.join(titles, ", ");
	}

	@Override
	public String[] getDescription() {
		return new String[0];
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> data = super.serialize();
		data.put("quests", quests);
		return data;
	}

	public List<Quest> getQuests() {
		return quests;
	}
}
