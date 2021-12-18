package org.screamingsandals.bedwars.events;

import lombok.Data;
import org.screamingsandals.bedwars.api.events.PostPropertyScanEvent;
import org.screamingsandals.lib.event.SCancellableEvent;
import org.screamingsandals.simpleinventories.events.ItemRenderEvent;

@Data
public class PostPropertyScanEventImpl implements PostPropertyScanEvent, SCancellableEvent {
    private final ItemRenderEvent event; // for sba
    private boolean cancelled;
}
