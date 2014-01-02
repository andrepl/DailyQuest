package com.norcode.bukkit.dailyquests.type;

import com.norcode.bukkit.dailyquests.DailyQuests;
import com.norcode.bukkit.dailyquests.command.CommandError;
import com.norcode.bukkit.dailyquests.quest.HuntingQuest;
import com.norcode.bukkit.dailyquests.quest.Quest;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.HashMap;
import java.util.LinkedList;

public class Hunting extends QuestType {

    private DailyQuests plugin;

    private static HashMap<EntityType, Integer> enemies = new HashMap<EntityType, Integer>();

    static {
        enemies.put(EntityType.BAT , 2);
        enemies.put(EntityType.BLAZE, 1);
        enemies.put(EntityType.CHICKEN, 5);
        enemies.put(EntityType.COW,5);
        enemies.put(EntityType.CREEPER,5);
        enemies.put(EntityType.ENDERMAN,1);
        enemies.put(EntityType.GHAST,1);
        enemies.put(EntityType.HORSE,2);
        enemies.put(EntityType.IRON_GOLEM,2);
        enemies.put(EntityType.MAGMA_CUBE,1);
        enemies.put(EntityType.MUSHROOM_COW,1);
        enemies.put(EntityType.PIG,5);
        enemies.put(EntityType.PIG_ZOMBIE,3);
        enemies.put(EntityType.SHEEP,5);
        enemies.put(EntityType.SILVERFISH,1);
        enemies.put(EntityType.SQUID,2);
        enemies.put(EntityType.WITCH,1);
        enemies.put(EntityType.WITHER,1);
        enemies.put(EntityType.WOLF,1);
        enemies.put(EntityType.ZOMBIE,4);
        enemies.put(EntityType.SLIME,2);
    }

    public Hunting(DailyQuests plugin) {
        this.plugin = plugin;
        ConfigurationSerialization.registerClass(HuntingQuest.class);
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

	@Override
	public Quest fromUserInput(LinkedList<String> args) throws CommandError {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
    public HuntingQuest generateQuest(double difficulty) {
        //set the default to be 1 pig
        EntityType entityType =  EntityType.PIG;
        Integer qty = 1;

        if ((difficulty >= 0.0) && (difficulty < 0.2)) {
            enemies.remove(EntityType.CREEPER);
            enemies.remove(EntityType.SLIME);
            enemies.remove(EntityType.MUSHROOM_COW);
            enemies.remove(EntityType.IRON_GOLEM);
            enemies.remove(EntityType.WITCH);
            enemies.remove(EntityType.PIG_ZOMBIE);
            enemies.remove(EntityType.GHAST);
            enemies.remove(EntityType.MAGMA_CUBE);
            enemies.remove(EntityType.WITHER);
            enemies.remove(EntityType.ENDERMAN);
            enemies.remove(EntityType.BLAZE);
        } else if ((difficulty >= 0.2) && (difficulty < 0.4)) {
            enemies.remove(EntityType.MUSHROOM_COW);
            enemies.remove(EntityType.WITCH);
            enemies.remove(EntityType.PIG_ZOMBIE);
            enemies.remove(EntityType.GHAST);
            enemies.remove(EntityType.MAGMA_CUBE);
            enemies.remove(EntityType.WITHER);
            enemies.remove(EntityType.ENDERMAN);
            enemies.remove(EntityType.BLAZE);
        } else if ((difficulty >= 0.4) && (difficulty < 0.6)) {
            enemies.remove(EntityType.WITCH);
            enemies.remove(EntityType.GHAST);
            enemies.remove(EntityType.MAGMA_CUBE);
            enemies.remove(EntityType.WITHER);
            enemies.remove(EntityType.BLAZE);
        } else if ((difficulty >= 0.6) && (difficulty < 0.8)) {
            enemies.remove(EntityType.MAGMA_CUBE);
            enemies.remove(EntityType.WITHER);
        }

        Object[] keys = enemies.keySet().toArray();
        entityType = (EntityType) keys[random.nextInt(keys.length)];

        //scale difficulties to make more kills required
        if (difficulty > 0.25) {
            qty*=2;
        } else if (difficulty > 0.5) {
            qty*=3;
        } else if (difficulty > 0.75) {
            qty *=4;
        }
        qty = qty * enemies.get(entityType);
        return new HuntingQuest(System.currentTimeMillis(), entityType, qty, plugin.generateReward(difficulty));
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (enemies.keySet().contains(event.getEntityType())) {
            //verify it was a player kill, then check for quests and increment counters
            if (event.getEntity().getKiller() != null) {
                if (plugin.getPlayerQuest(event.getEntity().getKiller()) instanceof HuntingQuest) {
                    for (Quest quest: plugin.getPlayerQuests(event.getEntity().getKiller(), HuntingQuest.class)) {
                        if ((!quest.isFinished()) && (event.getEntityType() == ((HuntingQuest) quest).getEntityType())) {
                            quest.progress(event.getEntity().getKiller(), 1);
                            break;
                        }
                    }
                }
            }
        }
    }
}
