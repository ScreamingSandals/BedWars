package org.screamingsandals.bedwars.api.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.screamingsandals.bedwars.api.game.Game;

/**
 * @author Bedwars Team
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BedwarsPlayerDeathMessageSendEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Player victim;
    private final Game game;
    @NotNull
    private String message;
    private boolean cancelled = false;

    public BedwarsPlayerDeathMessageSendEvent(Player victim, Game game, @NotNull String message) {
        this.victim = victim;
        this.game = game;
        this.message = message;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    public static HandlerList getHandlerList() {
        return BedwarsPlayerDeathMessageSendEvent.handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
