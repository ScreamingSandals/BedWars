package org.screamingsandals.bedwars.events;

import lombok.Data;
import org.screamingsandals.bedwars.api.events.StoreIncludeEvent;
import org.screamingsandals.lib.event.SCancellableEvent;
import org.screamingsandals.simpleinventories.builder.CategoryBuilder;

import java.nio.file.Path;

@Data
public class StoreIncludeEventImpl implements StoreIncludeEvent, SCancellableEvent {
    private final String name;
    private final Path path;
    private final boolean useParent;
    private final CategoryBuilder categoryBuilder;
    private boolean cancelled;
}
