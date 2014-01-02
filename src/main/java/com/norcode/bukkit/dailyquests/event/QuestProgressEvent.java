package com.norcode.bukkit.dailyquests.event;

import com.norcode.bukkit.dailyquests.quest.Quest;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class QuestProgressEvent extends PlayerEvent implements Cancellable {
	private boolean cancelled;
	private static final HandlerList handlers = new HandlerList();
	private Quest quest;
	private int amount;

	public QuestProgressEvent(Player who, Quest quest, int amount) {
		super(who);
		this.quest = quest;
		this.amount = amount;
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

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}
}
