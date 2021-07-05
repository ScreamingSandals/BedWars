package org.screamingsandals.bedwars.api.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.api.game.Game;

/**
 * @author Bedwars Team
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BedwarsBedDestroyedMessageSendEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Player destroyer;
    private final Player victim;
    private final Game game;
    private final Team destroyedTeam;

    @NotNull
    private String message;
    private boolean cancelled = false;

    public BedwarsBedDestroyedMessageSendEvent(Player destroyer, Player victim, Game game, Team destroyedTeam, @NotNull String message) {
        this.destroyer = destroyer;
        this.victim = victim;
        this.game = game;
        this.destroyedTeam = destroyedTeam;
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
        return BedwarsBedDestroyedMessageSendEvent.handlers;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
