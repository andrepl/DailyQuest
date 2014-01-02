package com.norcode.bukkit.dailyquests.quest;

import com.norcode.bukkit.dailyquests.reward.QuestReward;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.EntityType;

import java.util.EnumSet;
import java.util.Map;

public class HuntingQuest extends Quest implements ConfigurationSerializable {

	private static final EnumSet<EntityType> ALREADY_PLURAL = EnumSet.of(EntityType.SHEEP, EntityType.SILVERFISH, EntityType.SQUID);
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
        String response = "Hunt and kill " + progressMax + " ";
        //pluralize it for cleanliness
        String typeName = StringUtils.capitalize(this.entityType.name().toLowerCase().replace("_", " "));
		if (entityType == EntityType.PIG_ZOMBIE) {
			typeName = "Zombie Pigman";
		} else if (entityType == EntityType.MUSHROOM_COW) {
			typeName = "Mooshroom";
		}
		if (progress > 1) {
			if (typeName.endsWith("man")) {
				typeName = typeName.substring(0, typeName.length()-3) + "men";
			} else if (!ALREADY_PLURAL.contains(entityType)) {
				if (entityType == EntityType.WOLF) {
					typeName = "Wolves";
				} else if (entityType == EntityType.WITCH) {
					typeName = typeName + "es";
				} else {
					typeName += "s";
				}
			}
		}


        return response + typeName;
    }

    @Override
    public String[] getDescription() {
        return new String[0];
    }

    public EntityType getEntityType() {
        return entityType;
    }

}
