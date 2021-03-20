package org.screamingsandals.bedwars.api.events;

import lombok.RequiredArgsConstructor;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.screamingsandals.simpleinventories.builder.CategoryBuilder;

import java.io.File;

/**
 * @author Bedwars Team
 *
 */
@RequiredArgsConstructor
public class BedwarsStoreIncludeEvent extends Event implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();
    private final CategoryBuilder builder;
    private final String name;
    private final File file;
    private final boolean useParent;
    private boolean cancelled;

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return
     */
    public boolean isUseParent() {
        return useParent;
    }

    /**
     *
     * @return
     */
    public File getFile() {
        return file;
    }

    /**
     *
     * @return
     */
    public CategoryBuilder getBuilder() {
        return builder;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return BedwarsStoreIncludeEvent.handlerList;
    }

    public static HandlerList getHandlerList() {
        return BedwarsStoreIncludeEvent.handlerList;
    }
}
