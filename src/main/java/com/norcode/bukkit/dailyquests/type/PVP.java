package com.norcode.bukkit.dailyquests.type;

import com.norcode.bukkit.dailyquests.DailyQuests;
import com.norcode.bukkit.dailyquests.command.CommandError;
import com.norcode.bukkit.dailyquests.quest.HuntingQuest;
import com.norcode.bukkit.dailyquests.quest.PVPQuest;
import com.norcode.bukkit.dailyquests.quest.Quest;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.HashMap;
import java.util.LinkedList;

public class PVP extends QuestType{

    private DailyQuests plugin;

    public PVP(DailyQuests plugin) {
        this.plugin = plugin;
        ConfigurationSerialization.registerClass(PVPQuest.class);
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public Quest fromUserInput(LinkedList<String> args) throws CommandError {
        return null;
    }

    @Override
    public Quest generateQuest(double difficulty) {
        Integer qty = 1;
        Material weapon = Material.WOOD_SWORD;

        //scale weapon up / down depending on difficulty of quest
        if ((difficulty >= 0.1) && (difficulty < 0.25)) {
            qty = 2;
            weapon = Material.STONE_SWORD;
        } else if ((difficulty >=0.25) && (difficulty < 0.35)) {
            qty = 3;
            weapon = Material.IRON_SWORD;
        } else if ((difficulty >=0.35) && (difficulty < 0.5)) {
            qty = 3;
            weapon = Material.IRON_SWORD;
        } else if ((difficulty >= 0.5) && (difficulty < 0.65)) {
            qty = 4;
            weapon = Material.DIAMOND_SWORD;
        } else if ((difficulty >=0.65) && (difficulty < 0.75)) {
            qty = 5;
            weapon = Material.IRON_SWORD;
        } else if ((difficulty >= 0.75) && (difficulty < 0.95)) {
            qty = 8;
            weapon = Material.STONE_SWORD;
        } else if ((difficulty >= 0.95)) {
            qty = 16;
            weapon = Material.WOOD_SWORD;
        }
        //add random chance for bow
        if ((random.nextBoolean()) && (random.nextDouble() < difficulty)) {
            weapon = Material.BOW;
        }

        return new PVPQuest(System.currentTimeMillis(), weapon, qty, plugin.generateReward(difficulty));
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        //verify it was a player kill, then check for quests and increment counters
        if ((event.getEntity().getKiller() != null) && (event.getEntityType() == EntityType.PLAYER)) {
            if (plugin.getPlayerQuest(event.getEntity().getKiller()) instanceof PVPQuest) {
                for (Quest quest: plugin.getPlayerQuests(event.getEntity().getKiller(), PVPQuest.class)) {
                    if (!quest.isFinished()) {
                        quest.progress(event.getEntity().getKiller(), 1);
                        break;
                    }
                }
            }
        }
    }
}
