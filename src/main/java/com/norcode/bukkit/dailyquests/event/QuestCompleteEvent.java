package com.norcode.bukkit.dailyquests.event;

import com.norcode.bukkit.dailyquests.quest.Quest;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class QuestCompleteEvent extends PlayerEvent implements Cancellable {

	private boolean cancelled;
	private static final HandlerList handlers = new HandlerList();
	private Quest quest;

	public QuestCompleteEvent(Player who, Quest quest) {
		super(who);
		this.quest = quest;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean b) {
		cancelled = b;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public Quest getQuest() {
		return quest;
	}
}
