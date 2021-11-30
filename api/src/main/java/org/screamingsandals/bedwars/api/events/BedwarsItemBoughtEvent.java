package org.screamingsandals.bedwars.api.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.screamingsandals.bedwars.api.game.Game;

@EqualsAndHashCode(callSuper = true)
@Data
public class BedwarsItemBoughtEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    private final Game game;
    private final Player customer;
    private final ItemStack item;
    private final int price;

    @Override
    public @NotNull HandlerList getHandlers() {
        return BedwarsItemBoughtEvent.handlers;
    }
}
