package org.screamingsandals.bedwars.api.events;

import org.screamingsandals.bedwars.api.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Bedwars Team
 */
public class BedwarsPreSpawnEffectEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private Game game;
    private Player player;
    private String effectsGroupName;
    private boolean cancelled = false;

    /**
     * @param game
     * @param player
     * @param effectsGroupName
     */
    public BedwarsPreSpawnEffectEvent(Game game, Player player, String effectsGroupName) {
        this.game = game;
        this.player = player;
        this.effectsGroupName = effectsGroupName;
    }

    public static HandlerList getHandlerList() {
        return BedwarsPreSpawnEffectEvent.handlers;
    }

    /**
     * @return game
     */
    public Game getGame() {
        return this.game;
    }

    /**
     * @return player
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * @return group effects
     */
    public String getEffectsGroupName() {
        return this.effectsGroupName;
    }

    @Override
    public HandlerList getHandlers() {
        return BedwarsPreSpawnEffectEvent.handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

}
