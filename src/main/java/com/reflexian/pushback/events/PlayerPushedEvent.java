package com.reflexian.pushback.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

public class PlayerPushedEvent extends Event implements Cancellable {

    private final Player player;
    private final List<Player> PLAYERS_EFFECTED;

    private static final HandlerList HANDLERS = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public PlayerPushedEvent(Player player, List<Player> targets) {
        this.PLAYERS_EFFECTED = targets;
        this.player=player;
    }

    public Player getPlayer() {
        return player;
    }

    public List<Player> getEffectedPlayers() {
        return PLAYERS_EFFECTED;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }
    @Override
    public void setCancelled(boolean cancel) {

    }
}
