package org.screamingsandals.bedwars.events;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.screamingsandals.bedwars.api.events.PrePropertyScanEvent;
import org.screamingsandals.lib.event.CancellableAbstractEvent;
import org.screamingsandals.simpleinventories.events.ItemRenderEvent;

@EqualsAndHashCode(callSuper = true)
@Data
public class PrePropertyScanEventImpl extends CancellableAbstractEvent implements PrePropertyScanEvent {
    private final ItemRenderEvent event; // for sba
}
