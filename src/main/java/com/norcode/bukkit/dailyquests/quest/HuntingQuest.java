package com.norcode.bukkit.dailyquests.quest;

import com.norcode.bukkit.dailyquests.reward.QuestReward;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.EntityType;

import java.util.Map;

public class HuntingQuest extends Quest implements ConfigurationSerializable {

        private EntityType entityType;

    	public HuntingQuest(long receivedAt, EntityType entityType, int qty, QuestReward reward) {
       		super(receivedAt, qty, reward);
            this.entityType = entityType;
        }

    	public HuntingQuest(Map<String, Object> map) {
            super(map);
            this.entityType = EntityType.valueOf(map.get("ore").toString());
        }

        @Override
        public String getTitle() {
            return "Hunt " + progressMax + " " + this.entityType.name() + ".";
        }

        @Override
        public String[] getDescription() {
            return new String[0];
        }

        public EntityType getEntityType() {
            return entityType;
        }

}
