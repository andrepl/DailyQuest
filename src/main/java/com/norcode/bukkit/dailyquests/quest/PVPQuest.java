package com.norcode.bukkit.dailyquests.quest;

import com.norcode.bukkit.dailyquests.reward.QuestReward;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Map;

public class PVPQuest extends Quest implements ConfigurationSerializable {

    Material weaponType;

    public PVPQuest(long received, Material weapon, int qty, QuestReward reward) {
        super(received, qty, reward);
        this.weaponType = weapon;
    }

    @Override
    public String getTitle() {
        String response = "Kill " + progressMax;
        if (getProgressMax() > 1) {
            return response + " people.";
        } else {
            return response + " person.";
        }
    }

    @Override
    public String[] getDescription() {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> basicData = super.serialize();
        basicData.put("weapon", this.weaponType.name());
        return basicData;
    }

    public Material getWeaponType() {
        return this.weaponType;
    }

}
