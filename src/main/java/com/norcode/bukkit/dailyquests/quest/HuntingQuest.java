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
        this.entityType = EntityType.valueOf((String) map.get("enemy"));
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> basicData = super.serialize();
        basicData.put("enemy", this.entityType.name());
        return basicData;
    }

    @Override
    public String getTitle() {
        String response = "Hunt and kill " + progressMax + " " + this.entityType.name();
        //pluralize it for cleanliness
        if (progress > 1) response = response + "s.";
        else response = response + ".";

        return response;
    }

    @Override
    public String[] getDescription() {
        return new String[0];
    }

    public EntityType getEntityType() {
        return entityType;
    }

}
