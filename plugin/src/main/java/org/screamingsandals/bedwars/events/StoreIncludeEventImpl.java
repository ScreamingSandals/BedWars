package org.screamingsandals.bedwars.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.screamingsandals.bedwars.api.events.StoreIncludeEvent;
import org.screamingsandals.lib.event.CancellableAbstractEvent;
import org.screamingsandals.simpleinventories.builder.CategoryBuilder;

import java.nio.file.Path;

@EqualsAndHashCode(callSuper = true)
@Data
public class StoreIncludeEventImpl extends CancellableAbstractEvent implements StoreIncludeEvent {
    private final String name;
    private final Path path;
    private final boolean useParent;
    private final CategoryBuilder categoryBuilder;
}
