package org.screamingsandals.bedwars.api.events.game;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.screamingsandals.lib.gamecore.core.GameFrame;
import org.screamingsandals.lib.gamecore.core.cycle.GameCycle;

@EqualsAndHashCode(callSuper = false)
@Data
public class BedWarsGameCyclePrepareEvent extends Event implements Cancellable {
    private HandlerList handlerList = new HandlerList();
    private final GameFrame gameFrame;
    private GameCycle gameCycle; //todo - api!
    private boolean cancelled;

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
