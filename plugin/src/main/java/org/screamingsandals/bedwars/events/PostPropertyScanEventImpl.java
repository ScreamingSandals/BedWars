package org.screamingsandals.bedwars.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.screamingsandals.bedwars.api.events.PostPropertyScanEvent;
import org.screamingsandals.lib.event.CancellableAbstractEvent;
import org.screamingsandals.simpleinventories.events.ItemRenderEvent;

@EqualsAndHashCode(callSuper = true)
@Data
public class PostPropertyScanEventImpl extends CancellableAbstractEvent implements PostPropertyScanEvent {
    private final ItemRenderEvent event; // for sba
}
