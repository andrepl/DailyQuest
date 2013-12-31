package com.norcode.bukkit.dailyquests;

import com.norcode.bukkit.dailyquests.quest.FishingQuest;
import com.norcode.bukkit.dailyquests.quest.MiningQuest;
import com.norcode.bukkit.dailyquests.type.Fishing;
import com.norcode.bukkit.dailyquests.type.Mining;
import org.junit.Test;

public class TestQuestGeneration {

	@Test
	public void testFishingQuestGen() {
		DailyQuests dq = new DailyQuests();
		Fishing fishing = new Fishing(dq);
		Mining mining = new Mining(dq);
		for (double i=0; i<0.99; i+=0.01) {
			FishingQuest fq = fishing.generateQuest(i);
			MiningQuest mq = mining.generateQuest(i);
			//System.out.println(fq.getTitle());
			System.out.println(mq.getTitle());
		}
	}
}
